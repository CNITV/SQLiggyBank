package ro.lbi.sqliggybank.server.Resources;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.PATCH;
import ro.lbi.sqliggybank.server.Core.Group;
import ro.lbi.sqliggybank.server.Core.PiggyBank;
import ro.lbi.sqliggybank.server.Database.GroupDAO;
import ro.lbi.sqliggybank.server.Database.GroupListDAO;
import ro.lbi.sqliggybank.server.Database.PiggyBankDAO;
import ro.lbi.sqliggybank.server.Responses.GenericResponse;
import ro.lbi.sqliggybank.server.Responses.InternalErrorResponse;
import ro.lbi.sqliggybank.server.Responses.NotFoundResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Path("/api/banks/")
@Produces(MediaType.APPLICATION_JSON)
public class BanksResource {

	private final GroupDAO groupDAO;
	private final GroupListDAO groupListDAO;
	private final PiggyBankDAO piggyBankDAO;
	private final JWTVerifier authVerifier;

	public BanksResource(GroupDAO groupDAO, GroupListDAO groupListDAO, PiggyBankDAO piggyBankDAO, byte[] JWTSecret) {
		this.groupDAO = groupDAO;
		this.groupListDAO = groupListDAO;
		this.piggyBankDAO = piggyBankDAO;
		Algorithm authAlgorithm = Algorithm.HMAC256(JWTSecret);
		this.authVerifier = JWT.require(authAlgorithm)
				.withIssuer("SQLiggyBank")
				.build();
	}

	@GET
	@UnitOfWork
	@Path("{groupName}/{bankName}")
	public Response getPiggyBankInfo(@HeaderParam("Authorization") String authorization, @PathParam("groupName") String groupName, @PathParam("bankName") String bankName) {
		if (authorization != null) { // are they a user? Let's see if they pass the test.
			return findPiggyBank(groupName, bankName, authorization);
		} else {
			return Response
					.status(Response.Status.FORBIDDEN)
					.entity(new GenericResponse(Response.Status.FORBIDDEN.getStatusCode(), "You must be logged in to view piggy bank information!"))
					.build();
		}
	}

	@GET
	@UnitOfWork
	@Path("{groupName}/list")
	public Response listPiggyBanks(@HeaderParam("Authorization") String authorization, @PathParam("groupName") String groupName) {
		if (authorization != null) { // are they a user? Let's see if they pass the test.
			return getPiggyBankList(groupName, authorization);
		} else {
			return Response
					.status(Response.Status.FORBIDDEN)
					.entity(new GenericResponse(Response.Status.FORBIDDEN.getStatusCode(), "You must be logged in to list piggy banks!"))
					.build();
		}
	}

	@POST
	@UnitOfWork
	@Path("{groupName}/new")
	public Response newPiggyBank(@HeaderParam("Authorization") String authorization, @PathParam("groupName") String groupName, String body) {
		if (authorization != null) { // are they a user? Let's see if they pass the test.
			return createPiggyBank(groupName, authorization, body);
		} else {
			return Response
					.status(Response.Status.FORBIDDEN)
					.entity(new GenericResponse(Response.Status.FORBIDDEN.getStatusCode(), "You must be logged in to create a piggy bank!"))
					.build();
		}
	}

	@PATCH
	@UnitOfWork
	@Path("{groupName}/{bankName}")
	public Response editPiggyBankInfo(@HeaderParam("Authorization") String authorization, @PathParam("groupName") String groupName, @PathParam("bankName") String bankName, String body) {
		if (authorization != null) { // are they a user? Let's see if they pass the test.
			return editPiggyBank(groupName, bankName, authorization, body);
		} else {
			return Response
					.status(Response.Status.FORBIDDEN)
					.entity(new GenericResponse(Response.Status.FORBIDDEN.getStatusCode(), "You must be logged in to edit piggy bank information!"))
					.build();
		}
	}

	@DELETE
	@UnitOfWork
	@Path("{groupName}/{bankName}")
	public Response deletePiggyBank(@HeaderParam("Authorization") String authorization, @PathParam("groupName") String groupName, @PathParam("bankName") String bankName) {
		if (authorization != null) { // are they a user? Let's see if they pass the test.
			return removePiggyBank(groupName, bankName, authorization);
		} else {
			return Response
					.status(Response.Status.FORBIDDEN)
					.entity(new GenericResponse(Response.Status.FORBIDDEN.getStatusCode(), "You must be logged in to delete a piggy bank!"))
					.build();
		}
	}

	private Response getPiggyBankList(String groupName, String authorization) {
		authorization = authorization.substring(authorization.indexOf(" ") + 1); // remove "Bearer" from Authorization header
		try {
			DecodedJWT jwt = authVerifier.verify(authorization); // verify token
			if (groupListDAO.isUserPartOfGroup(jwt.getClaim("username").asString(), groupName)) { // user part of group, give bank information
				Group group = groupDAO.findByName(groupName).orElseThrow(() -> new NotFoundException("Group not found!"));
				List<PiggyBank> list = piggyBankDAO.findByGroup(group);
				return Response.ok(list).build();
			} else { // not part of group, eject client
				return Response
						.status(Response.Status.FORBIDDEN)
						.entity(new GenericResponse(Response.Status.FORBIDDEN.getStatusCode(), "You are not part of this group!"))
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
		} catch (NotFoundException e) {
				return Response
						.status(Response.Status.NOT_FOUND)
						.entity(new NotFoundResponse("The group \"" + groupName + "\" could not be found!"))
						.build();
		}
	}

	private Response findPiggyBank(String groupName, String bankName, String authorization) {
		authorization = authorization.substring(authorization.indexOf(" ") + 1); // remove "Bearer" from Authorization header
		try {
			DecodedJWT jwt = authVerifier.verify(authorization); // verify token
			if (groupListDAO.isUserPartOfGroup(jwt.getClaim("username").asString(), groupName)) { // user part of group, give bank information
				Group group = groupDAO.findByName(groupName).orElseThrow(() -> new NotFoundException("Group not found!"));
				PiggyBank bank = piggyBankDAO.findByNameAndGroup(group, bankName).orElseThrow(() -> new NotFoundException("Bank not found!"));
				return Response.ok(bank).build();
			} else { // not part of group, eject client
				return Response
						.status(Response.Status.FORBIDDEN)
						.entity(new GenericResponse(Response.Status.FORBIDDEN.getStatusCode(), "You are not part of the group this piggy bank is in!"))
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
		} catch (NotFoundException e) {
			return handleBankNotFoundException(groupName, bankName, e);
		}
	}

	private Response createPiggyBank(String groupName, String authorization, String body) {
		authorization = authorization.substring(authorization.indexOf(" ") + 1); // remove "Bearer" from Authorization header
		try {
			DecodedJWT jwt = authVerifier.verify(authorization); // verify token
			if (groupDAO.isUserOwnerOfGroup(jwt.getClaim("username").asString(), groupName)) { // user owner of group, allow creation
				PiggyBank tempBank = new ObjectMapper().readValue(body, PiggyBank.class);
				Group group = groupDAO.findByName(groupName).orElseThrow(() -> new NotFoundException("Group not found!"));
				if (tempBank.getName() == null) {
					return Response
							.status(Response.Status.BAD_REQUEST)
							.entity(new GenericResponse(Response.Status.BAD_REQUEST.getStatusCode(), "Your submitted body is missing the \"name\" field, try again!"))
							.build();
				}
				PiggyBank possibleBank = piggyBankDAO.findByNameAndGroup(group, tempBank.getName()).orElse(null);
				if (possibleBank != null) {
					return Response
							.status(Response.Status.FORBIDDEN)
							.entity(new GenericResponse(Response.Status.BAD_REQUEST.getStatusCode(), "A piggy bank with this name already exists! Please try another name!"))
							.build();
				}
				tempBank.setGroup(group);
				tempBank.setUuid(UUID.randomUUID());
				piggyBankDAO.create(tempBank);
				return Response.ok(new GenericResponse(Response.Status.OK.getStatusCode(), "Created piggy bank!")).build();
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
			return Response
					.status(Response.Status.BAD_REQUEST)
					.entity(new InternalErrorResponse(Response.Status.BAD_REQUEST.getStatusCode(), "Cannot parse submitted piggy bank body!", e.getMessage()))
					.build();
		} catch (IOException e) {
			return Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(new InternalErrorResponse(e.getMessage()))
					.build();
		} catch (NotFoundException e) {
				return Response
						.status(Response.Status.NOT_FOUND)
						.entity(new NotFoundResponse("The group \"" + groupName + "\" could not be found!"))
						.build();
		}
	}

	private Response editPiggyBank(String groupName, String bankName, String authorization, String body) {
		authorization = authorization.substring(authorization.indexOf(" ") + 1); // remove "Bearer" from Authorization header
		try {
			DecodedJWT jwt = authVerifier.verify(authorization); // verify token
			if (groupDAO.isUserOwnerOfGroup(jwt.getClaim("username").asString(), groupName)) { // user owner of group, allow creation
				PiggyBank tempBank = new ObjectMapper().readValue(body, PiggyBank.class);
				Group group = groupDAO.findByName(groupName).orElseThrow(() -> new NotFoundException("Group not found!"));
				PiggyBank piggyBank = piggyBankDAO.findByNameAndGroup(group, bankName).orElseThrow(() -> new NotFoundException("Piggy bank not found!"));
				// Apparently, Hibernate doesn't like you modifying the objects it remembers in memory, even if they
				// are functionally the same. In conclusion, crap code like this shows up, where I have to replace
				// everything in the original one.
				//
				// I'm just happy I don't have to make my own actual SQL queries :)
				if (tempBank.getName() == null) {
					return Response
							.status(Response.Status.BAD_REQUEST)
							.entity(new GenericResponse(Response.Status.BAD_REQUEST.getStatusCode(), "Your submitted body is missing the \"name\" field, try again!"))
							.build();
				}
				piggyBank.setName(tempBank.getName());
				piggyBank.setDescription(tempBank.getDescription());
				piggyBankDAO.update(piggyBank);
				return Response.ok(new GenericResponse(Response.Status.OK.getStatusCode(), "Update complete!")).build();
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
			return Response
					.status(Response.Status.BAD_REQUEST)
					.entity(new InternalErrorResponse(Response.Status.BAD_REQUEST.getStatusCode(), "Cannot parse submitted piggy bank body!", e.getMessage()))
					.build();
		} catch (IOException e) {
			return Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(new InternalErrorResponse(e.getMessage()))
					.build();
		} catch (NotFoundException e) {
			return handleBankNotFoundException(groupName, bankName, e);
		}
	}

	private Response removePiggyBank(String groupName, String bankName, String authorization) {
		authorization = authorization.substring(authorization.indexOf(" ") + 1); // remove "Bearer" from Authorization header
		try {
			DecodedJWT jwt = authVerifier.verify(authorization); // verify token
			if (groupDAO.isUserOwnerOfGroup(jwt.getClaim("username").asString(), groupName)) { // user owner of group, allow creation
				Group group = groupDAO.findByName(groupName).orElseThrow(() -> new NotFoundException("Group not found!"));
				PiggyBank piggyBank = piggyBankDAO.findByNameAndGroup(group, bankName).orElseThrow(() -> new NotFoundException("Piggy Bank not found!"));
				piggyBankDAO.delete(piggyBank);
				return Response.ok(new GenericResponse(Response.Status.OK.getStatusCode(), "Deleted piggy bank!")).build();
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
		} catch (NotFoundException e) {
			return handleBankNotFoundException(groupName, bankName, e);
		}
	}

	private Response handleBankNotFoundException(String groupName, String bankName, NotFoundException e) {
		if (e.getMessage().split(" ")[0].equals("Group")) {
			return Response
					.status(Response.Status.NOT_FOUND)
					.entity(new NotFoundResponse("The group \"" + groupName + "\" could not be found!"))
					.build();
		} else {
			return Response
					.status(Response.Status.NOT_FOUND)
					.entity(new NotFoundResponse("The piggy bank \"" + bankName + "\" could not be found!"))
					.build();
		}
	}

}
