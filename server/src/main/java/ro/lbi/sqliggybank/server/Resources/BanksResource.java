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

/**
 * BanksResource covers the banks endpoint for the SQLiggyBank API. It is in charge of getting piggy bank information
 * and editing piggy bank information.
 * <p>
 * Details for the implementation of these methods can be found in the SQLiggyBank API Documentation.
 *
 * @author StormFireFox1
 * @see <a href="https://documenter.getpostman.com/view/3806934/RWgwRFa8?version=latest#90e27b58-617b-4249-96bf-d3ef8f2f61db">SQLiggyBank API Documentation</a>
 * @since 2019-01-29
 */
@Path("/api/banks/")
@Produces(MediaType.APPLICATION_JSON)
public class BanksResource {

	/**
	 * groupDAO is the DAO for the "groups" table in the database. This is modified by the constructor.
	 *
	 * @see ro.lbi.sqliggybank.server.Database.GroupDAO
	 */
	private final GroupDAO groupDAO;

	/**
	 * groupListDAO is the DAO for the "group_lists" table in the database. This is modified by the constructor.
	 *
	 * @see ro.lbi.sqliggybank.server.Database.GroupListDAO
	 */
	private final GroupListDAO groupListDAO;

	/**
	 * piggyBankDAO is the DAO for the "banks" table in the database. This is modified by the constructor.
	 *
	 * @see ro.lbi.sqliggybank.server.Database.PiggyBankDAO
	 */
	private final PiggyBankDAO piggyBankDAO;

	/**
	 * authVerifier is the verifier for the HMAC256 algorithm used to sign JWT's.
	 */
	private final JWTVerifier authVerifier;

	/**
	 * The constructor for BanksResource. The parameters should be passed solely by the ServerApplication class.
	 *
	 * @param groupDAO     The DAO to the "groups" table in the database.
	 * @param groupListDAO The DAO to the "group_lists" table in the database.
	 * @param piggyBankDAO The DAO for the "banks" table in the database.
	 * @param JWTSecret    The secret to be used for signing JWT's using the HMAC256 algorithm.
	 */
	public BanksResource(GroupDAO groupDAO, GroupListDAO groupListDAO, PiggyBankDAO piggyBankDAO, byte[] JWTSecret) {
		this.groupDAO = groupDAO;
		this.groupListDAO = groupListDAO;
		this.piggyBankDAO = piggyBankDAO;
		Algorithm authAlgorithm = Algorithm.HMAC256(JWTSecret);
		this.authVerifier = JWT.require(authAlgorithm)
				.withIssuer("SQLiggyBank")
				.build();
	}

	/**
	 * The endpoint for extracting piggy bank information. This endpoint requires authentication.
	 *
	 * @param authorization The "Authorization" header in the HTTP request.
	 * @param groupName     The "groupName" parameter of the request. Passed in the URL.
	 * @param bankName      The "bankName" parameter of the request. Passed in the URL.
	 * @return A response according to the SQLiggyBank API Documentation. In general, it returns a JSON representation of the PiggyBank class.
	 * @see ro.lbi.sqliggybank.server.Core.PiggyBank
	 * @see <a href="https://documenter.getpostman.com/view/3806934/RWgwRFa8?version=latest#3463fa61-f4ac-4e0c-b288-a8b6fa7a715b">API Documentation</a>
	 */
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

	/**
	 * The endpoint for listing all the piggy banks that are part of a group. This endpoint requires authentication.
	 *
	 * @param authorization The "Authorization" header in the HTTP request.
	 * @param groupName     The "groupName" parameter of the request. Passed in the URL.
	 * @return A response according to the SQLiggyBank API Documentation. In general, it returns a JSON array of PiggyBank objects.
	 * @see ro.lbi.sqliggybank.server.Core.PiggyBank
	 * @see <a href="https://documenter.getpostman.com/view/3806934/RWgwRFa8?version=latest#594dea63-8961-45ba-b556-9c8f0f0a595e">API Documentation</a>
	 */
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

	/**
	 * The endpoint for creating a new piggy bank. This endpoint requires authentication. The user must also be the group
	 * owner in order to be capable of creating a piggy bank.
	 *
	 * @param authorization The "Authorization" header in the HTTP request.
	 * @param groupName     The "groupName" parameter of the request. Passed in the URL.
	 * @return A response according to the SQLiggyBank API Documentation. In general, it returns a message relaying success.
	 * @see ro.lbi.sqliggybank.server.Core.PiggyBank
	 * @see <a href="https://documenter.getpostman.com/view/3806934/RWgwRFa8?version=latest#f3f2c366-c7b5-4a97-8fe1-3ecb78b6507e">API Documentation</a>
	 */
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

	/**
	 * The endpoint for editing piggy bank information. This endpoint requires authentication. The user must also be the
	 * group owner in order to be capable of editing piggy bank information.
	 *
	 * @param authorization The "Authorization" header in the HTTP request.
	 * @param groupName     The "groupName" parameter of the request. Passed in the URL.
	 * @param bankName      The "bankName" parameter of the request. Passed in the URL.
	 * @return A response according to the SQLiggyBank API Documentation. In general, it returns a message relaying success.
	 * @see ro.lbi.sqliggybank.server.Core.PiggyBank
	 * @see <a href="https://documenter.getpostman.com/view/3806934/RWgwRFa8?version=latest#b3d8f1df-a365-44dc-b890-8dff627a4a6f">API Documentation</a>
	 */
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

	/**
	 * The endpoint for deleting a piggy bank. This endpoint requires authentication. The user must also be the
	 * group owner in order to be capable of deleting a piggy bank.
	 *
	 * @param authorization The "Authorization" header in the HTTP request.
	 * @param groupName     The "groupName" parameter of the request. Passed in the URL.
	 * @param bankName      The "bankName" parameter of the request. Passed in the URL.
	 * @return A response according to the SQLiggyBank API Documentation. In general, it returns a message relaying success.
	 * @see ro.lbi.sqliggybank.server.Core.PiggyBank
	 * @see <a href="https://documenter.getpostman.com/view/3806934/RWgwRFa8?version=latest#1d4f4412-1d0b-444a-a7cf-6f532159aa3f">API Documentation</a>
	 */
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

	/**
	 * Gets the list of piggy banks belonging to a specific group. This handles the logic behind the endpoint. It checks
	 * whether the user is a member of the group and returns the JSON array of piggy banks that are part of a group.
	 *
	 * @param groupName     The name of the group to check piggy banks in.
	 * @param authorization The "Authorization" header in the HTTP request.
	 * @return A response, depending on the query and errors. In general, 200 (OK) status code is returned if every
	 * parameter is met in the request, and 404 (Not Found) status code is returned if the group (or user)
	 * cannot be found in the database. If an authenticated user is not part of the group, a 403 (Forbidden) status code
	 * is returned.
	 */
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

	/**
	 * Finds a piggy bank belonging to a specific group. This handles the logic behind the endpoint. It checks
	 * whether the user is a member of the group and returns the JSON representation of a PiggyBank object.
	 *
	 * @param groupName     The name of the group to check piggy banks in.
	 * @param bankName      The name of the piggy bank to find.
	 * @param authorization The "Authorization" header in the HTTP request.
	 * @return A response, depending on the query and errors. In general, 200 (OK) status code is returned if a piggy
	 * bank is found, and 404 (Not Found) status code is returned if a piggy bank cannot be found in the database. If an
	 * authenticated user is not a member of the piggy bank's group, he is restricted access using a 403 (Forbidden)
	 * status code.
	 */
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

	/**
	 * Creates a piggy bank belonging to a specific group. This handles the logic behind the endpoint. It checks
	 * whether the user is the owner of the group, reads the body passed to it and creates a new PiggyBank object.
	 *
	 * @param groupName     The name of the group to put the piggy bank in.
	 * @param body          The JSON representation of the PiggyBank object to create.
	 * @param authorization The "Authorization" header in the HTTP request.
	 * @return A response, depending on the query and errors. In general, 200 (OK) status code is returned if a piggy
	 * bank is created, and 404 (Not Found) status code is returned if a piggy bank cannot be found in the database. If an
	 * authenticated user is not the owner of the new piggy bank's group, they are restricted access using a 403 (Forbidden)
	 * status code.
	 */
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
							.status(Response.Status.BAD_REQUEST)
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

	/**
	 * Modifies an existing piggy bank belonging to a specific group. This handles the logic behind the endpoint.
	 * It checks whether the user is the owner of the group, reads the body passed to it and edits the existing
	 * PiggyBank object in the database.
	 *
	 * @param groupName     The name of the group the piggy bank is in.
	 * @param bankName      The name of the piggy bank to edit.
	 * @param body          The JSON representation of the PiggyBank object to modify, in essence inserting all the different information.
	 * @param authorization The "Authorization" header in the HTTP request.
	 * @return A response, depending on the query and errors. In general, 200 (OK) status code is returned if a piggy
	 * bank is successfully edited, and 404 (Not Found) status code is returned if a piggy bank cannot be found in the
	 * database. If an authenticated user is not the owner of the new piggy bank's group, they are restricted access
	 * using a 403 (Forbidden) status code.
	 */
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
				PiggyBank possibleBank = piggyBankDAO.findByNameAndGroup(group, tempBank.getName()).orElse(null);
				if (possibleBank != null && !possibleBank.getUuid().equals(piggyBank.getUuid())) {
					return Response
							.status(Response.Status.FORBIDDEN)
							.entity(new GenericResponse(Response.Status.BAD_REQUEST.getStatusCode(), "A piggy bank with this name already exists! Please try another name!"))
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

	/**
	 * Deletes an existing piggy bank belonging to a specific group. This handles the logic behind the endpoint.
	 * It checks whether the user is the owner of the group, and deletes the existing PiggyBank object from the database.
	 *
	 * @param groupName     The name of the group to put the piggy bank in.
	 * @param bankName      The name of the bank to delete.
	 * @param authorization The "Authorization" header in the HTTP request.
	 * @return A response, depending on the query and errors. In general, 200 (OK) status code is returned if a piggy
	 * bank is successfully deleted, and 404 (Not Found) status code is returned if a piggy bank cannot be found in the
	 * database. If an authenticated user is not the owner of the new piggy bank's group, they are restricted access
	 * using a 403 (Forbidden) status code.
	 */
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

	/**
	 * Receives a NotFoundException and checks which part of the queries threw this exception. Since it is impossible
	 * to figure this out at application level, the message must be checked.
	 *
	 * @param groupName The name of the group to handle the exception with.
	 * @param bankName  The name of the bank to handle the exception with.
	 * @param e         The NotFoundException to handle.
	 * @return A Response with a 404 (Not Found) status code, depending on the exception thrown.
	 */
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
