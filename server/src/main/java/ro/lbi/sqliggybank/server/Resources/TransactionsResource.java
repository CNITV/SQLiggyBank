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
import ro.lbi.sqliggybank.server.Responses.NotFoundResponse;

import javax.persistence.NoResultException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.UUID;

/**
 * TransactionsResource covers the transactions endpoint for the SQLiggyBank API.
 *
 * It is in charge of getting and deleting transaction information. It is
 * important to note that transactions are immutable.
 *
 * Details for the implementation of these methods can be found in the
 * SQLiggyBank API Documentation.
 *
 * @author StormFireFox1
 * @since 2019-03-28
 * @see <a href="https://documenter.getpostman.com/view/3806934/RWgwRFa8?version=latest#2ed666f7-3a03-47e3-9652-67b8e5f18c4c">SQLiggyBank API Documentation</a>
 */
@Path("/api/transactions/")
@Produces(MediaType.APPLICATION_JSON)
public class TransactionsResource {
	/**
	 * groupDAO is the DAO for the "groups" table in the database.
	 *
	 * This is modified by the constructor.
	 */
	private final GroupDAO groupDAO;
	/**
	 * groupListDAO is the DAO for the "group_lists" table in the database.
	 *
	 * This is modified by the constructor.
	 */
	private final GroupListDAO groupListDAO;
	/**
	 * userDAO is the DAO for the "users" table in the database.
	 *
	 * This is modified by the constructor.
	 */
	private final UserDAO userDAO;
	/**
	 * depositDAO is the DAO for the "deposits" table in the database.
	 *
	 * This is modified by the constructor.
	 */
	private final DepositDAO depositDAO;
	/**
	 * withdrawalDAO is the DAO for the "withdrawals" table in the datbase.
	 *
	 * This is modified by the constructor.
	 */
	private final WithdrawalDAO withdrawalDAO;
	/**
	 * piggyBankDAO is the DAO for the "banks" table in the database.
	 *
	 * This is modified by the constructor.
	 */
	private final PiggyBankDAO piggyBankDAO;
	/**
	 * authVerifier is the verifier for the HMAC256 algorithm used to sign
	 * JWT's.
	 */
	private final JWTVerifier authVerifier;

	/**
	 * The constructor for TransactionResource.
	 *
	 * The parameters should be passed solely by the ServerApplication
	 * class.
	 *
	 * @param groupDAO      The DAO to the "groups" table in the database.
	 * @param groupListDAO  The DAO to the "group_lists" table in the
	 *                      database.
	 * @param userDAO       The DAO to the "users" table in the database.
	 * @param depositDAO    The DAO to the "deposits" table in the database.
	 * @param withdrawalDAO The DAO to the "withdrawals" table in the
	 * 			database.
	 * @param piggyBankDAO  The DAO to the "banks" table in the database. 
	 * @param JWTSecret     The secret to be used for signing JWT's using
	 * 			the HMAC256 algorithm.
	 */
	public TransactionsResource(GroupDAO groupDAO, GroupListDAO groupListDAO, UserDAO userDAO, DepositDAO depositDAO, WithdrawalDAO withdrawalDAO, PiggyBankDAO piggyBankDAO, byte[] JWTSecret) {
		this.groupDAO = groupDAO;
		this.groupListDAO = groupListDAO;
		this.userDAO = userDAO;
		this.depositDAO = depositDAO;
		this.withdrawalDAO = withdrawalDAO;
		this.piggyBankDAO = piggyBankDAO;
		Algorithm authAlgorithm = Algorithm.HMAC256(JWTSecret);
		this.authVerifier = JWT.require(authAlgorithm)
				.withIssuer("SQLiggyBank")
				.build();
	}

	/**
	 * The endpoint for extracting transaction information.
	 *
	 * This endpoint, like almost all other endpoints in GroupResource,
	 * requires authentication.
	 *
	 * @param authorization The "Authorization" header of the HTTP request.
	 * @param groupName     The "groupName" parameter of the request. Passed
	 *			in the URL.
	 * @param bankName	The "bankName" parameter of the request. Passed
	 * 			in the URL.
	 * @param transactionID The UUID of the transaction. Passed in the URL.
	 *
	 * @return A response according to the SQLiggyBank API Documentation. In
	 *         general, it returns a JSON object representing a Transaction.
	 * @see <a href="https://documenter.getpostman.com/view/3806934/RWgwRFa8?version=latest#0f829f51-50d1-4e7c-8a3f-f77b9e0c772b">API Documentation</a>
	 */
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

	/**
	 * The endpoint for creating a transaction.
	 *
	 * This endpoint, like almost all other endpoints in GroupResource,
	 * requires authentication.
	 *
	 * @param authorization The "Authorization" header of the HTTP request.
	 * @param groupName     The "groupName" parameter of the request. Passed
	 *			in the URL.
	 * @param bankName	The "bankName" parameter of the request. Passed
	 * 			in the URL.
	 * @param body		The body of the request. Usually a JSON
	 * 			representation of a transaction.
	 *
	 * @return A response according to the SQLiggyBank API Documentation. In
	 *         general, it returns a JSON object relaying success.
	 * @see <a href="https://documenter.getpostman.com/view/3806934/RWgwRFa8?version=latest#0f829f51-50d1-4e7c-8a3f-f77b9e0c772b">API Documentation</a>
	 */
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

	/**
	 * Finds a transaction, querying by UUID, group and bank. 
	 *
	 * This handles the logic behind the endpoint. It checks whether the
	 * logged in user is a member of the group and returns the transaction,
	 * whether it be a deposit or withdrawal. 
	 *
	 * @param groupName	The name of the group to query transactions for.
	 * @param bankName 	The name of the bank to query transactions for.
	 * @param transactionID The UUID of the transaction.
	 * @param authorization The "Authorization" header in the HTTP request.
	 *
	 * @return A response, depending on the query and errors. In general,
	 *         200 (OK) status code is returned if a transaction is found, and
	 *         a 404 (Not Found) status code is returned if any element of 
	 *         the query cannot be found in the database. If a client is
	 *         not a member of the group, they are restricted access using
	 *         a 403 (Forbidden) status code.
	 */
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
		} catch (NoResultException e) {
			return Response
					.status(Response.Status.NOT_FOUND)
					.entity(new NotFoundResponse("The transaction with UUID " + transactionID + " cannot be found!"))
					.build();
		}
	}

	/**
	 * Creates a transaction. 
	 *
	 * This handles the logic behind the endpoint. It checks whether the
	 * logged in user is the owner of the group and creates the transaction,
	 * whether it be a deposit or withdrawal. 
	 *
	 * @param groupName	The name of the group to query transactions for.
	 * @param bankName 	The name of the bank to query transactions for.
	 * @param authorization The "Authorization" header in the HTTP request.
	 * @param body		The body of the request. Usually a JSON
	 * 			representation of the transaction to create.
	 *
	 * @return A response, depending on the query and errors. In general,
	 *         200 (OK) status code is returned if a transaction is created,
	 *         and a 404 (Not Found) status code is returned if any element
	 *         of the query cannot be found in the database. If a client is
	 *         not the owner of the group, they are restricted access using
	 *         a 403 (Forbidden) status code.
	 */
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
				tempDeposit.setUuid(UUID.randomUUID());
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
				withdrawal.setUuid(UUID.randomUUID());
				withdrawal.setBank(tempBank);
				withdrawalDAO.create(withdrawal);
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
			} catch (NotFoundException e1) {
				return EndpointExceptionHandler.parseBankNotFound(groupName, bankName, e1);
			}

		} catch (IOException e) {
			return Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(new InternalErrorResponse(e.getMessage()))
					.build();
		} catch (NotFoundException e) {
			return EndpointExceptionHandler.parseBankNotFound(groupName, bankName, e);
		}
	}
}
