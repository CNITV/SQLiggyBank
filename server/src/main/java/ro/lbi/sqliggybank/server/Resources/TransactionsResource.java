package ro.lbi.sqliggybank.server.Resources;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.hibernate.UnitOfWork;
import ro.lbi.sqliggybank.server.Core.Deposit;
import ro.lbi.sqliggybank.server.Core.PiggyBank;
import ro.lbi.sqliggybank.server.Core.User;
import ro.lbi.sqliggybank.server.Core.Withdrawal;
import ro.lbi.sqliggybank.server.Database.*;
import ro.lbi.sqliggybank.server.Responses.GenericResponse;
import ro.lbi.sqliggybank.server.Responses.InternalErrorResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.UUID;

@Path("/api/transactions/")
@Produces(MediaType.APPLICATION_JSON)
public class TransactionsResource {
	private final GroupDAO groupDAO;
	private final GroupListDAO groupListDAO;
	private final UserDAO userDAO;
	private final DepositDAO depositDAO;
	private final WithdrawalDAO withdrawalDAO;
	private final PiggyBankDAO piggyBankDAO;
	private final byte[] JWTSecret;
	private final Algorithm authAlgorithm;
	private final JWTVerifier authVerifier;

	public TransactionsResource(GroupDAO groupDAO, GroupListDAO groupListDAO, UserDAO userDAO, DepositDAO depositDAO, WithdrawalDAO withdrawalDAO, PiggyBankDAO piggyBankDAO, byte[] JWTSecret) {
		this.groupDAO = groupDAO;
		this.groupListDAO = groupListDAO;
		this.userDAO = userDAO;
		this.depositDAO = depositDAO;
		this.withdrawalDAO = withdrawalDAO;
		this.piggyBankDAO = piggyBankDAO;
		this.JWTSecret = JWTSecret;
		this.authAlgorithm = Algorithm.HMAC256(this.JWTSecret);
		this.authVerifier = JWT.require(this.authAlgorithm)
				.withIssuer("SQLiggyBank")
				.build();
	}

	@GET
	@UnitOfWork
	@Path("{groupName}/{bankName}/{transactionID}")
	public Response getTransaction(@HeaderParam("Authorization") String authorization, @PathParam("groupName") String groupName, @PathParam("bankName") String bankName, @PathParam("transactionID") String transactionID) {
		if (authorization != null) { // are they a user? Let's see if they pass the test.
			return findTransaction(groupName, bankName, transactionID, authorization);
		} else {
			return Response
					.status(Response.Status.FORBIDDEN)
					.entity(new GenericResponse(Response.Status.FORBIDDEN.getStatusCode(), "You must be logged in to view transactions!"))
					.build();
		}
	}

	@POST
	@UnitOfWork
	@Path("{groupName}/{bankName}/new")
	public Response postTransaction(@HeaderParam("Authorization") String authorization, @PathParam("groupName") String groupName, @PathParam("bankName") String bankName, String body) {
		if (authorization != null) { // are they a user? Let's see if they pass the test.
			return createTransaction(groupName, bankName, authorization, body);
		} else {
			return Response
					.status(Response.Status.FORBIDDEN)
					.entity(new GenericResponse(Response.Status.FORBIDDEN.getStatusCode(), "You must be logged in to create a transaction!"))
					.build();
		}
	}

	private Response findTransaction(String groupName, String bankName, String transactionID, String authorization) {
		authorization = authorization.substring(authorization.indexOf(" ") + 1); // remove "Bearer" from Authorization header
		try {
			DecodedJWT jwt = authVerifier.verify(authorization); // verify token
			if (groupListDAO.isUserPartOfGroup(jwt.getClaim("username").asString(), groupName)) { // user part of group, give transaction information
				if (depositDAO.isDeposit(UUID.fromString(transactionID))) {
					Deposit transaction = depositDAO.findByID(UUID.fromString(transactionID)).orElseThrow(() -> new NotFoundException("Transaction not found!"));
					return Response.ok(transaction).build();
				} else {
					Withdrawal transaction = withdrawalDAO.findByID(UUID.fromString(transactionID)).orElseThrow(() -> new NotFoundException("Transaction not found!"));
					return Response.ok(transaction).build();
				}
			} else { // not part of group, eject client
				return Response
						.status(Response.Status.FORBIDDEN)
						.entity(new GenericResponse(Response.Status.FORBIDDEN.getStatusCode(), "You are not part of the group this transaction is made in!"))
						.build();
			}
		} catch (TokenExpiredException e) {
			return Response
					.status(Response.Status.UNAUTHORIZED)
					.entity(new GenericResponse(Response.Status.UNAUTHORIZED.getStatusCode(), "Token expired! Log in again!"))
					.build();
		} catch (JWTVerificationException e) { // invalid token, eject client
			return Response
					.status(Response.Status.UNAUTHORIZED)
					.entity(new GenericResponse(Response.Status.UNAUTHORIZED.getStatusCode(), "Invalid authentication scheme!"))
					.build();
		}
	}

	private Response createTransaction(String groupName, String bankName, String authorization, String body) {
		authorization = authorization.substring(authorization.indexOf(" ") + 1); // remove "Bearer" from Authorization header
		try {
			DecodedJWT jwt = authVerifier.verify(authorization); // verify token
			if (groupDAO.isUserOwnerOfGroup(jwt.getClaim("username").asString(), groupName)) { // user owner of group, allow creation
				ObjectMapper mapper = new ObjectMapper();
				JsonNode bodyJSON = mapper.readTree(body);
				String username = bodyJSON.get("username").asText();
				PiggyBank tempBank = piggyBankDAO.findByName(bankName).orElseThrow(() -> new NotFoundException("Piggy bank not found!"));
				User payee = userDAO.findByUsername(username).orElseThrow(() -> new NotFoundException("User not found!"));
				Deposit tempDeposit = mapper.readValue(body, Deposit.class);
				tempDeposit.setBank(tempBank);
				tempDeposit.setPayee(payee);
				depositDAO.create(tempDeposit);
				return Response.ok(new GenericResponse(Response.Status.OK.getStatusCode(), "Created transaction!")).build();
			} else { // not owner of group, eject client
				return Response
						.status(Response.Status.FORBIDDEN)
						.entity(new GenericResponse(Response.Status.FORBIDDEN.getStatusCode(), "You are not the owner of this group"))
						.build();
			}
		} catch (TokenExpiredException e) {
			return Response
					.status(Response.Status.UNAUTHORIZED)
					.entity(new GenericResponse(Response.Status.UNAUTHORIZED.getStatusCode(), "Token expired! Log in again!"))
					.build();
		} catch (JWTVerificationException e) { // invalid token, eject client
			return Response
					.status(Response.Status.UNAUTHORIZED)
					.entity(new GenericResponse(Response.Status.UNAUTHORIZED.getStatusCode(), "Invalid authentication scheme!"))
					.build();
		} catch (JsonParseException | JsonMappingException e) {
			// perhaps it is a withdrawal, let's find out!
			try {
				PiggyBank tempBank = piggyBankDAO.findByName(bankName).orElseThrow(() -> new NotFoundException("Piggy bank not found!"));
				Withdrawal withdrawal = new ObjectMapper().readValue(body, Withdrawal.class);
				withdrawal.setBank(tempBank);
				return Response.ok(new GenericResponse(Response.Status.OK.getStatusCode(), "Created transaction!")).build();
			} catch (JsonParseException | JsonMappingException e1) {
				return Response
						.status(Response.Status.BAD_REQUEST)
						.entity(new InternalErrorResponse(Response.Status.BAD_REQUEST.getStatusCode(), "Cannot parse submitted transaction body!", e1.getMessage()))
						.build();
			} catch (IOException e1) {
				return Response
						.status(Response.Status.INTERNAL_SERVER_ERROR)
						.entity(new InternalErrorResponse(e1.getMessage()))
						.build();
			}

		} catch (IOException e) {
			return Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(new InternalErrorResponse(e.getMessage()))
					.build();
		}
	}
}
