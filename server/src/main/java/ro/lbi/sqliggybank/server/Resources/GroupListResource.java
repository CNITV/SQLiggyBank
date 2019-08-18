package ro.lbi.sqliggybank.server.Resources;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.dropwizard.hibernate.UnitOfWork;
import ro.lbi.sqliggybank.server.Core.Group;
import ro.lbi.sqliggybank.server.Core.User;
import ro.lbi.sqliggybank.server.Core.GroupEntry;
import ro.lbi.sqliggybank.server.Database.GroupDAO;
import ro.lbi.sqliggybank.server.Database.UserDAO;
import ro.lbi.sqliggybank.server.Database.GroupListDAO;
import ro.lbi.sqliggybank.server.Responses.GenericResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * GroupListResource covers the group lists endpoint for the SQLiggyBank API.
 *
 * It is in charge of getting and editing group entry information.
 *
 * Details for the implementation of these methods can be found in the
 * SQLiggyBank API Documentation.
 *
 * @author StormFireFox1
 * @since 2019-03-28
 * @see <a href="https://documenter.getpostman.com/view/3806934/RWgwRFa8?version=latest#f1784a6d-6b2f-4eed-a58a-c8d127274925">SQLiggyBank API Documentation</a>
 */
@Path("/api/lists/")
@Produces(MediaType.APPLICATION_JSON)
public class GroupListResource {
	
	/**
	 * groupDAO is the DAO for the "groups" table in the database.
	 *
	 * This is modified by the constructor.
	 */
	private final GroupDAO groupDAO;
	/**
	 * userDAO is the DAO for the "users" table in the database.
	 *
	 * This is modified by the constructor.
	 */
	private final UserDAO userDAO;	
	/**
	 * groupListDAO is the DAO for the "group_lists" table in the database.
	 *
	 * This is modified by the constructor.
	 */
	private final GroupListDAO groupListDAO;
	/**
	 * authVerifier is the verifier for the HMAC256 algorithm used to sign
	 * JWT's.
	 */
	private final JWTVerifier authVerifier;

	/**
	 * The constructor for GroupListResource.
	 *
	 * The parameters should be passed solely by the ServerApplication
	 * class.
	 *
	 * @param groupDAO     The DAO to the "groups" table in the database.
	 * @param userDAO      The DAO to the "users" table in the database.
	 * @param groupListDAO The DAO to the "group_lists" table in the
	 *                     database.
	 * @param JWTSecret    The secret to be used for signing JWT's using the
	 *		       HMAC256 algorithm.
	 */
	public GroupListResource(GroupDAO groupDAO, UserDAO userDAO, GroupListDAO groupListDAO, byte[] JWTSecret) {
		this.groupDAO = groupDAO;
		this.userDAO = userDAO;
		this.groupListDAO = groupListDAO;
		Algorithm authAlgorithm = Algorithm.HMAC256(JWTSecret);
		this.authVerifier = JWT.require(authAlgorithm)
				.withIssuer("SQLiggyBank")
				.build();
	}

	/**
	 * The endpoint for extracting members of a specific group.
	 *
	 * This endpoint, like almost all other endpoints in GroupListResource,
	 * requires authentication.
	 *
	 * @param authorization The "Authorization" header of the HTTP request.
	 * @param groupName     The "groupName" parameter of the request. Passed
	 *			in the URL.
	 *
	 * @return A response according to the SQLiggyBank API Documentation. In
	 *         general, it returns a JSON object representing an array of
	 *         Users.
	 * @see <a href="https://documenter.getpostman.com/view/3806934/RWgwRFa8?version=latest#d3b5ade1-b421-43c1-a04e-d41a06d0b02d">API Documentation</a>
	 */
	@GET
	@UnitOfWork
	@Path("members/{groupName}")
	public Response getMembersOfGroup(@HeaderParam("Authorization") String authorization, @PathParam("groupName") String groupName) {
		if (authorization != null) { // continue with logic
			return findMembersOfGroup(groupName, authorization);
		} else { // nice try, though
			return Response
					.status(Response.Status.UNAUTHORIZED)
					.entity(new GenericResponse(Response.Status.UNAUTHORIZED.getStatusCode(), "You must be authenticated to view user lists!"))
					.build();
		}	
	}

	/**
	 * The endpoint for extraction the groups of a specific user.
	 *
	 * This endpoint, like almost all other endpoints in GroupListResource,
	 * requires authentication.
	 *
	 * @param authorization The "Authorization" header of the HTTP request.
	 * @param userName	The "userName" parameter of the request. Passed
	 * 			in the URL.
	 *
	 * @return A response according to the SQLiggyBank API Documentation. In
	 *         general, it returns a JSON object representing an array of
	 *         Groups.
	 * @see <a href="https://documenter.getpostman.com/view/3806934/RWgwRFa8?version=latest#cf0a90fd-e9e5-45a7-b272-d7e3d20018ec">API Documentation</a>
	 */
	@GET
	@UnitOfWork
	@Path("groups/{userName}")
	public Response getGroupsOfUser(@HeaderParam("Authorization") String authorization, @PathParam("userName") String userName) {
		if (authorization != null) { // continue with logic
			return listGroupsForUser(userName, authorization);
		} else { // nice try, though
			return Response
					.status(Response.Status.UNAUTHORIZED)
					.entity(new GenericResponse(Response.Status.UNAUTHORIZED.getStatusCode(), "You must be authenticated to view user lists!"))
					.build();
		}	
	}

	/**
	 * Finds the members of a group, querying by the name of the group.
	 *
	 * This handles the logic behind the endpoint. It checks whether the
	 * user is a member of the group that they are querying for, and then
	 * returns the members of the group.
	 *
	 * @param groupName     The name of the group to find the members for.
	 * @param authorization The "Authorization" header in the HTTP request.
	 *
	 * @return A response, depending on the query and errors. In general,
	 *         200 (OK) status code is returned if a group is found, and
	 *         thus has members in it, a 404 (Not Found) status code is
	 *         returned if a group cannot be found in the database. If an
	 *         authenticated user is not a member of the goal's group,
	 *         they are restricted access with a 403 (Forbidden) status code.
	 */
	private Response findMembersOfGroup(String groupName, String authorization) {
		authorization = authorization.substring(authorization.indexOf(" ") + 1); // remove "Bearer" from Authorization header
		try {
			DecodedJWT jwt = authVerifier.verify(authorization); // verify token
			User user = userDAO.findByUsername(jwt.getClaim("username").asString()).orElseThrow(() -> new NotFoundException("User not found!"));
			Group group = groupDAO.findByName(groupName).orElseThrow(() -> new NotFoundException("Group not found!"));
			if (groupListDAO.isUserPartOfGroup(user.getUsername(), group.getName())) { // is user part of group? If yes...
				List<User> list = groupListDAO.membersOfGroup(group);
				return Response.ok(list).build();
			} else { // not member, eject client
				return Response
						.status(Response.Status.FORBIDDEN)
						.entity(new GenericResponse(Response.Status.FORBIDDEN.getStatusCode(), "You are not a member of this group!"))
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

	/**
	 * Finds the groups that a user is a member of.
	 *
	 * This handles the logic behind the endpoint. It checks whether the
	 * client is logged in as the user they are querying for, and then
	 * returns the groups that they are a member of.
	 *
	 * @param groupName     The name of the group to find the members for.
	 * @param authorization The "Authorization" header in the HTTP request.
	 *
	 * @return A response, depending on the query and errors. In general,
	 *         200 (OK) status code is returned if the user is found, and
	 *         thus has groups that they are in, a 404 (Not Found) status
	 *         code is returned if the user cannot be found in the database.
	 *         If an authenticated user is not the user they are querying
	 *         for, they are restricted access with a 403 (Forbidden)
	 *         status code.
	 */
	private Response listGroupsForUser(String userName, String authorization) {
		authorization = authorization.substring(authorization.indexOf(" ") + 1); // remove "Bearer" from Authorization header
		try {
			DecodedJWT jwt = authVerifier.verify(authorization); // verify token
			User user = userDAO.findByUsername(userName).orElseThrow(() -> new NotFoundException("User not found!"));
			if (!jwt.getClaim("username").asString().equals(user.getUsername())) {
				return Response
						.status(Response.Status.FORBIDDEN)
						.entity(new GenericResponse(Response.Status.FORBIDDEN.getStatusCode(), "You are not allowed to view the groups of another user!"))
						.build();
			}
			if (jwt.getClaim("username").asString().equals(user.getUsername()) && jwt.getClaim("password").asString().equals(user.getPassword())) { // is user logged in? If yes...
				List<Group> list = groupListDAO.groupsOfUser(user);		
				return Response.ok(list).build();
			} else { // invalid credentials, eject client
				return Response
						.status(Response.Status.FORBIDDEN)
						.entity(new GenericResponse(Response.Status.FORBIDDEN.getStatusCode(), "Incorrect username or password! Log in again!"))
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
	
}
