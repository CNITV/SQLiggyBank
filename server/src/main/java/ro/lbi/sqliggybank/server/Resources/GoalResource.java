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
import ro.lbi.sqliggybank.server.Core.Goal;
import ro.lbi.sqliggybank.server.Core.Group;
import ro.lbi.sqliggybank.server.Core.PiggyBank;
import ro.lbi.sqliggybank.server.Database.*;
import ro.lbi.sqliggybank.server.Responses.GenericResponse;
import ro.lbi.sqliggybank.server.Responses.InternalErrorResponse;

import javax.persistence.NoResultException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.UUID;
import java.util.List;

/**
 * GroupResource covers the groups endpoint for the SQLiggyBank API.
 *
 * It is in charge of getting and editing group information.
 *
 * Details for the implementation of these methods can be found in the
 * SQLiggyBank API Documentation.
 *
 * @author StormFireFox1
 * @since 2019-03-28
 * @see <a href="https://documenter.getpostman.com/view/3806934/RWgwRFa8?version=latest#bcbb639a-f0bd-4628-94d6-feee91fe036c">SQLiggyBank API Documentation</a>
 */
@Path("/api/goals/")
@Produces(MediaType.APPLICATION_JSON)
public class GoalResource {
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
	 * piggyBankDAO is the DAO for the "banks" table in the database.
	 *
	 * This is modified by the constructor.
	 */
	private final PiggyBankDAO piggyBankDAO;
	/**
	 * goalDAO is the DAO for the "goals" table in the database.
	 *
	 * This is modified by the constructor.
	 */
	private final GoalDAO goalDAO;
	/**
	 * authVerifier is the verifier for the HMAC256 algorithm used to sign
	 * JWT's.
	 */
	private final JWTVerifier authVerifier;

	/**
	 * The constructor for GoalResource.
	 *
	 * The parameters should be passed solely by the ServerApplication
	 * class.
	 *
	 * @param groupDAO     The DAO to the "groups" table in the database.
	 * @param groupListDAO The DAO to the "group_lists" table in the
	 *                     database.
	 * @param piggyBankDAO The DAO to the "banks" table in the database.
	 * @param goalDAO      The DAO to the "goals" table in the database.
	 * @param JWTSecret    The secret to be used for signing JWT's using the
	 *		       HMAC256 algorithm.
	 */
	public GoalResource(GroupDAO groupDAO, GroupListDAO groupListDAO, PiggyBankDAO piggyBankDAO, GoalDAO goalDAO, byte[] JWTSecret) {
		this.groupDAO = groupDAO;
		this.groupListDAO = groupListDAO;
		this.piggyBankDAO = piggyBankDAO;
		this.goalDAO = goalDAO;
		Algorithm authAlgorithm = Algorithm.HMAC256(JWTSecret);
		this.authVerifier = JWT.require(authAlgorithm)
				.withIssuer("SQLiggyBank")
				.build();
	}

	/**
	 * The endpoint for extracting goal information.
	 *
	 * This endpoint, like almost all other endpoints in GoalResource,
	 * requires authentication.
	 *
	 * @param authorization The "Authorization" header of the HTTP request.
	 * @param groupName     The "groupName" parameter of the request. Passed
	 *			in the URL.
	 * @param bankName      The "bankName" parameter of the request. Passed
	 * 			in the URL.
	 * @param goalName      The "goalName" parameter of the request. Passed
	 * 			in the URL.
	 *
	 * @return A response according to the SQLiggyBank API Documentation. In
	 *         general, it returns a JSON object representing a Goal.
	 * @see <a href="https://documenter.getpostman.com/view/3806934/RWgwRFa8?version=latest#930f1688-6842-42c0-be67-a75863e74d96">API Documentation</a>
	 */
	@GET
	@UnitOfWork
	@Path("{groupName}/{bankName}/{goalName}")
	public Response getGoalInfo(@HeaderParam("Authorization") String authorization, @PathParam("groupName") String groupName, @PathParam("bankName") String bankName, @PathParam("goalName") String goalName) {
		if (authorization != null) { // are they a user? Let's see if they pass the test.
			return findGoal(groupName, bankName, goalName, authorization);
		} else {
			return Response
					.status(Response.Status.FORBIDDEN)
					.entity(new GenericResponse(Response.Status.FORBIDDEN.getStatusCode(), "You must be logged in to view goal information!"))
					.build();
		}
	}

	/**
	 * The endpoint for creating a new goal.
	 *
	 * This endpoint, like almost all other endpoints in GoalResource,
	 * requires authentication. It also requires that the client must be the
	 * group owner in order to be capable of cerating a goal.
	 *
	 * @param authorization The "Authorization" header of the HTTP request.
	 * @param groupName     The "groupName" parameter of the request. Passed
	 *			in the URL.
	 * @param bankName      The "bankName" parameter of the request. Passed
	 * 			in the URL.
	 * @param body 		The body of the request, containing a JSON
	 * 			representation of the Goal object to be created.
	 *
	 * @return A response according to the SQLiggyBank API Documentation. In
	 *         general, it returns a message relaying success.
	 * @see <a href="https://documenter.getpostman.com/view/3806934/RWgwRFa8?version=latest#51c89d68-ea21-49b6-87b8-38ae3b7f14e6">API Documentation</a>
	 */
	@POST
	@UnitOfWork
	@Path("{groupName}/{bankName}/new")
	public Response newGoal(@HeaderParam("Authorization") String authorization, @PathParam("groupName") String groupName, @PathParam("bankName") String bankName, String body) {
		if (authorization != null) { // are they a user? Let's see if they pass the test.
			return createGoal(groupName, bankName, authorization, body);
		} else {
			return Response
					.status(Response.Status.FORBIDDEN)
					.entity(new GenericResponse(Response.Status.FORBIDDEN.getStatusCode(), "You must be logged in to create a goal!"))
					.build();
		}
	}

	/**
	 * The endpoint for extracting a list of all goals in a specific piggy
	 * bank.
	 *
	 * This endpoint, like almost all other endpoints in GoalResource,
	 * requires authentication.
	 *
	 * @param authorization The "Authorization" header of the HTTP request.
	 * @param groupName     The "groupName" parameter of the request. Passed
	 *			in the URL.
	 * @param bankName      The "bankName" parameter of the request. Passed
	 * 			in the URL.
	 *
	 * @return A response according to the SQLiggyBank API Documentation. In
	 *         general, it returns a JSON object representing an array of
	 *         Goals.
	 * @see <a href="https://documenter.getpostman.com/view/3806934/RWgwRFa8?version=latest#262d815d-8781-4325-8d42-20f20ba5a66b">API Documentation</a>
	 */
	@GET
	@UnitOfWork
	@Path("{groupName}/{bankName}/list")
	public Response listGoalsInBank(@HeaderParam("Authorization") String authorization, @PathParam("groupName") String groupName, @PathParam("bankName") String bankName) {
		if (authorization != null) { // are they a user? Let's see if they pass the test.
			return listGoals(groupName, bankName, authorization);
		} else {
			return Response
					.status(Response.Status.FORBIDDEN)
					.entity(new GenericResponse(Response.Status.FORBIDDEN.getStatusCode(), "You must be logged in view the goals in this bank!"))
					.build();
		}
	}

	/**
	 * The endpoint for editing goal information.
	 *
	 * This endpoint, like almost all other endpoints in GoalResource,
	 * requires authentication. It also requires that the client must be the
	 * group owner in order to be capable of cerating a goal.
	 *
	 * @param authorization The "Authorization" header of the HTTP request.
	 * @param groupName     The "groupName" parameter of the request. Passed
	 *			in the URL.
	 * @param bankName      The "bankName" parameter of the request. Passed
	 * 			in the URL.
	 * @param goalName      The "goalName" parameter of the request. Passed
	 * 			in the URL.
	 * @param body          The body of the request, containing a JSON
	 *                      representation of the modifications to be made
	 *                      to the existing Goal.
	 *
	 * @return A response according to the SQLiggyBank API Documentation. In
	 *         general, it returns a message relaying success.
	 * @see <a href="https://documenter.getpostman.com/view/3806934/RWgwRFa8?version=latest#48093f5a-5e98-4a8d-895b-40fa8977d6c6">API Documentation</a>
	 */
	@PATCH
	@UnitOfWork
	@Path("{groupName}/{bankName}/{goalName}")
	public Response editGoalInfo(@HeaderParam("Authorization") String authorization, @PathParam("groupName") String groupName, @PathParam("bankName") String bankName, @PathParam("goalName") String goalName, String body) {
		if (authorization != null) { // are they a user? Let's see if they pass the test.
			return editGoal(groupName, bankName, goalName, authorization, body);
		} else {
			return Response
					.status(Response.Status.FORBIDDEN)
					.entity(new GenericResponse(Response.Status.FORBIDDEN.getStatusCode(), "You must be logged in to edit goal information!"))
					.build();
		}
	}

	/**
	 * The endpoint for deleting a goal.
	 *
	 * This endpoint, like almost all other endpoints in GoalResource,
	 * requires authentication. It also requires that the client must be the
	 * group owner in order to be capable of cerating a goal.
	 *
	 * @param authorization The "Authorization" header of the HTTP request.
	 * @param groupName     The "groupName" parameter of the request. Passed
	 *			in the URL.
	 * @param bankName      The "bankName" parameter of the request. Passed
	 * 			in the URL.
	 * @param goalName      The "goalName" parameter of the request. Passed
	 * 			in the URL.
	 *
	 * @return A response according to the SQLiggyBank API Documentation. In
	 *         general, it returns a message relaying success.
	 * @see <a href="https://documenter.getpostman.com/view/3806934/RWgwRFa8?version=latest#930f1688-6842-42c0-be67-a75863e74d96">API Documentation</a>
	 */
	@DELETE
	@UnitOfWork
	@Path("{groupName}/{bankName}/{goalName}")
	public Response deleteGoal(@HeaderParam("Authorization") String authorization, @PathParam("groupName") String groupName, @PathParam("bankName") String bankName, @PathParam("goalName") String goalName) {
		if (authorization != null) { // are they a user? Let's see if they pass the test.
			return removeGoal(groupName, bankName, goalName, authorization);
		} else {
			return Response
					.status(Response.Status.FORBIDDEN)
					.entity(new GenericResponse(Response.Status.FORBIDDEN.getStatusCode(), "You must be logged in to delete a goal!"))
					.build();
		}
	}

	/**
	 * Finds a goal belonging to a specific piggy bank.
	 *
	 * This handles the logic behind the endpoint. It checks whether the
	 * user is a member of the group that the piggybank is in and returns
	 * the JSON representation of a Goal object.
	 *
	 * @param groupName     The name of the group to check piggy banks in.
	 * @param bankName      The name of the bank to check goals in.
	 * @param goalName      The name of the goal to find.
	 * @param authorization The "Authorization" header in the HTTP request.
	 *
	 * @return A response, depending on the query and errors. In general,
	 *         200 (OK) status code is returned if a goal is found, and
	 *         404 (Not Found) status code is returned if a goal cannot
	 *         be found in the database. If an authenticated user is not a
	 *         member of the goal's group, they are restricted access using
	 *         a 403 (Forbidden) status code.
	 */
	private Response findGoal(String groupName, String bankName, String goalName, String authorization) {
		authorization = authorization.substring(authorization.indexOf(" ") + 1); // remove "Bearer" from Authorization header
		try {
			DecodedJWT jwt = authVerifier.verify(authorization); // verify token
			if (groupListDAO.isUserPartOfGroup(jwt.getClaim("username").asString(), groupName)) { // user part of group, give bank information
				Goal goal = queryGoal(bankName, groupName, goalName);
				return Response.ok(goal).build();
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
			return EndpointExceptionHandler.parseGoalNotFound(groupName, bankName, goalName, e);
		}
	}

	/**
	 * Queries for a goal in a specific database.
	 *
	 * This is mostly a helper method to reduce the clutter in Goal-finding
	 * methods.
	 *
	 * @param bankName  The name of the bank to query in.
	 * @param groupName The name of the group to query in.
	 * @param goalName  The name of the goal to query for.
	 *
	 * @return A Goal object that matches the provided parameters.
	 *
	 * @throws NoResultException If a goal, group, or bank cannot be found.
	 */
	private Goal queryGoal(String bankName, String groupName, String goalName) throws NoResultException {
		Group group = groupDAO.findByName(groupName).orElseThrow(() -> new NotFoundException("Group not found!"));
		PiggyBank bank = piggyBankDAO.findByNameAndGroup(group, bankName).orElseThrow(() -> new NotFoundException("Piggy bank not found!"));
		return goalDAO.findByNameAndBank(bank, goalName).orElseThrow(() -> new NotFoundException("Goal not found!"));
	}
	
	/**
	 * Finds all the goals belonging to a specific piggy bank.
	 *
	 * This handles the logic behind the endpoint. It checks whether the
	 * user is a member of the group that the piggybank is in and returns
	 * the JSON representation of an array of Goal objects.
	 *
	 * @param groupName     The name of the group to check piggy banks in.
	 * @param bankName      The name of the bank to check goals in.
	 * @param authorization The "Authorization" header in the HTTP request.
	 *
	 * @return A response, depending on the query and errors. In general,
	 *         200 (OK) status code is returned if a goal is found, and
	 *         404 (Not Found) status code is returned if a goal cannot
	 *         be found in the database. If an authenticated user is not a
	 *         member of the goal's group, they are restricted access using
	 *         a 403 (Forbidden) status code.
	 */
	private Response listGoals(String groupName, String bankName, String authorization) {
		authorization = authorization.substring(authorization.indexOf(" ") + 1); // remove "Bearer" from Authorization header
		try {
			DecodedJWT jwt = authVerifier.verify(authorization); // verify token
			if (groupListDAO.isUserPartOfGroup(jwt.getClaim("username").asString(), groupName)) { // user part of group, give bank information
				Group group = groupDAO.findByName(groupName).orElseThrow(() -> new NotFoundException("Group not found!"));
				PiggyBank bank = piggyBankDAO.findByNameAndGroup(group, bankName).orElseThrow(() -> new NotFoundException("Piggy bank not found!"));
				List<Goal> list = goalDAO.findByBank(bank);
				return Response.ok(list).build();
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
			return EndpointExceptionHandler.parseBankNotFound(groupName, bankName, e);
		}	
	}

	/**
	 * Creates a goal from the provided body and parameters.
	 *
	 * This handles the logic behind the endpoint. It checks whether the
	 * user is an owner of the group that the piggybank is in and returns
	 * a message relaying success if a Goal object has been successfully
	 * created.
	 *
	 * @param groupName     The name of the group to check piggy banks in.
	 * @param bankName      The name of the bank to check goals in.
	 * @param authorization The "Authorization" header in the HTTP request.
	 * @param body          The JSON representation of a Goal object to
	 *			create.
	 *
	 * @return A response, depending on the query and errors. In general,
	 *         200 (OK) status code is returned if a goal is created, and
	 *         404 (Not Found) status code is returned if a group or bank
	 *         cannot be found in the database. If an authenticated user is
	 *         not the owner of the goal's group, they are restricted access
	 *         using a 403 (Forbidden) status code.
	 */
	private Response createGoal(String groupName, String bankName, String authorization, String body) {
		authorization = authorization.substring(authorization.indexOf(" ") + 1); // remove "Bearer" from Authorization header
		try {
			DecodedJWT jwt = authVerifier.verify(authorization); // verify token
			if (groupDAO.isUserOwnerOfGroup(jwt.getClaim("username").asString(), groupName)) { // user owner of group, allow creation
				Goal goal = new ObjectMapper().readValue(body, Goal.class);
				Group group = groupDAO.findByName(groupName).orElseThrow(() -> new NotFoundException("Group not found!"));
				PiggyBank bank = piggyBankDAO.findByNameAndGroup(group, bankName).orElseThrow(() -> new NotFoundException("Piggy bank not found!"));
				Goal possibleGoal = goalDAO.findByNameAndBank(bank, goal.getName()).orElse(null);
				if (possibleGoal != null) {
					return Response
						        .status(Response.Status.FORBIDDEN)
							.entity(new GenericResponse(Response.Status.FORBIDDEN.getStatusCode(), "Goal already exists, choose another name!"))
							.build();
				}
				if (goal.getName() == null) {
					return Response
							.status(Response.Status.BAD_REQUEST)
							.entity(new GenericResponse(Response.Status.BAD_REQUEST.getStatusCode(), "Your submitted body is missing the \"name\" field, try again!"))
							.build();
				} else if (goal.getTarget_amount() == null) {
					return Response
							.status(Response.Status.BAD_REQUEST)
							.entity(new GenericResponse(Response.Status.BAD_REQUEST.getStatusCode(), "Your submitted body is missing the \"target_amount\" field, try again!"))
							.build();
				} else if (goal.getDeadline() == null) {
					return Response
							.status(Response.Status.BAD_REQUEST)
							.entity(new GenericResponse(Response.Status.BAD_REQUEST.getStatusCode(), "Your submitted body is missing the \"deadline\" field, try again!"))
							.build();
				}
				goal.setBank(bank);
				goal.setUuid(UUID.randomUUID());
				goalDAO.create(goal);
				return Response.ok(new GenericResponse(Response.Status.OK.getStatusCode(), "Created goal!")).build();
			} else { // not owner of group, eject client
				return Response
						.status(Response.Status.FORBIDDEN)
						.entity(new GenericResponse(Response.Status.FORBIDDEN.getStatusCode(), "You are not the owner of this group!"))
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
					.entity(new InternalErrorResponse(Response.Status.BAD_REQUEST.getStatusCode(), "Cannot parse submitted goal body!", e.getMessage()))
					.build();
		} catch (IOException e) {
			return Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(new InternalErrorResponse(e.getMessage()))
					.build();
		} catch (NotFoundException e) {
			return EndpointExceptionHandler.parseBankNotFound(groupName, bankName, e);
		}
	}

	/**
	 * Edits a goal from the provided body and parameters.
	 *
	 * This handles the logic behind the endpoint. It checks whether the
	 * user is an owner of the group that the piggybank is in and returns
	 * a message relaying success if a Goal object has been successfully
	 * edited.
	 *
	 * @param groupName     The name of the group to check piggy banks in.
	 * @param bankName      The name of the bank to check goals in.
	 * @param authorization The "Authorization" header in the HTTP request.
	 * @param body          The JSON representation of a Goal object with
	 * 			the new modified information.
	 *
	 * @return A response, depending on the query and errors. In general,
	 *         200 (OK) status code is returned if a goal is edited, and
	 *         404 (Not Found) status code is returned if a group, bank or
	 *         goal cannot be found in the database. If an authenticated
	 *         user is not the owner of the goal's group, they are
	 *         restricted acces using a 403 (Forbidden) status code.
	 */
	private Response editGoal(String groupName, String bankName, String goalName, String authorization, String body) {
		authorization = authorization.substring(authorization.indexOf(" ") + 1); // remove "Bearer" from Authorization header
		try {
			DecodedJWT jwt = authVerifier.verify(authorization); // verify token
			if (groupDAO.isUserOwnerOfGroup(jwt.getClaim("username").asString(), groupName)) { // user owner of group, allow creation
				Goal tempGoal = new ObjectMapper().readValue(body, Goal.class);
				Goal goal = queryGoal(bankName, groupName, goalName);
				// Apparently, Hibernate doesn't like you modifying the objects it remembers in memory, even if they
				// are functionally the same. In conclusion, crap code like this shows up, where I have to replace
				// everything in the original one.
				//
				// I'm just happy I don't have to make my own actual SQL queries :)
				if (tempGoal.getName() != null) {
					goal.setName(tempGoal.getName());
				}
				if (tempGoal.getDescription() != null) {
					goal.setDescription(tempGoal.getDescription());
				}
				if (tempGoal.getTarget_amount() != null) {
					goal.setTarget_amount(tempGoal.getTarget_amount());
				}
				if (tempGoal.getDeadline() != null) {
					goal.setDeadline(tempGoal.getDeadline());
				}
				goalDAO.update(goal);
				return Response.ok(new GenericResponse(Response.Status.OK.getStatusCode(), "Update complete!")).build();
			} else { // not owner of group, eject client
				return Response
						.status(Response.Status.FORBIDDEN)
						.entity(new GenericResponse(Response.Status.FORBIDDEN.getStatusCode(), "You are not the owner of this group!"))
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
					.entity(new InternalErrorResponse(Response.Status.BAD_REQUEST.getStatusCode(), "Cannot parse submitted goal body!", e.getMessage()))
					.build();
		} catch (IOException e) {
			return Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(new InternalErrorResponse(e.getMessage()))
					.build();
		} catch (NotFoundException e) {
			return EndpointExceptionHandler.parseGoalNotFound(groupName, bankName, goalName, e);
		}
	}

	/**
	 * Deletes a goal from the database.
	 *
	 * This handles the logic behind the endpoint. It checks whether the
	 * user is an owner of the group that the piggybank is in and returns
	 * a message relaying success if a Goal object has been successfully
	 * deleted.
	 *
	 * @param groupName     The name of the group to check piggy banks in.
	 * @param bankName      The name of the bank to check goals in.
	 * @param authorization The "Authorization" header in the HTTP request.
	 *
	 * @return A response, depending on the query and errors. In general,
	 *         200 (OK) status code is returned if a goal is deleted, and
	 *         404 (Not Found) status code is returned if a group, bank or
	 *         goal cannot be found in the database. If an authenticated
	 *         user is not the owner of the goal's group, they are
	 *         restricted access using a 403 (Forbidden) status code.
	 */
	private Response removeGoal(String groupName, String bankName, String goalName, String authorization) {
		authorization = authorization.substring(authorization.indexOf(" ") + 1); // remove "Bearer" from Authorization header
		try {
			DecodedJWT jwt = authVerifier.verify(authorization); // verify token
			if (groupDAO.isUserOwnerOfGroup(jwt.getClaim("username").asString(), groupName)) { // user owner of group, allow creation
				Goal goal = queryGoal(bankName, groupName, goalName);
				goalDAO.delete(goal);
				return Response.ok(new GenericResponse(Response.Status.OK.getStatusCode(), "Deleted goal!")).build();
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
			return EndpointExceptionHandler.parseGoalNotFound(groupName, bankName, goalName, e);
		}
	}
}
