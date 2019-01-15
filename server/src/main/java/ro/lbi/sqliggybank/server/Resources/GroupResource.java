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
import ro.lbi.sqliggybank.server.Core.GroupEntry;
import ro.lbi.sqliggybank.server.Core.Invite;
import ro.lbi.sqliggybank.server.Core.User;
import ro.lbi.sqliggybank.server.Database.GroupDAO;
import ro.lbi.sqliggybank.server.Database.GroupListDAO;
import ro.lbi.sqliggybank.server.Database.UserDAO;
import ro.lbi.sqliggybank.server.Responses.GenericResponse;
import ro.lbi.sqliggybank.server.Responses.InternalErrorResponse;
import ro.lbi.sqliggybank.server.Responses.JWTResponse;
import ro.lbi.sqliggybank.server.Responses.JoinedGroupResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

@Path("/api/groups/")
@Produces(MediaType.APPLICATION_JSON)
public class GroupResource {

	private final GroupDAO groupDAO;
	private final GroupListDAO groupListDAO;
	private final UserDAO userDAO;
	private final byte[] JWTSecret;
	private final Algorithm authAlgorithm;
	private final JWTVerifier authVerifier;
	private final ArrayList<Invite> invites = new ArrayList<>();

	public GroupResource(GroupDAO groupDAO, GroupListDAO groupListDAO, UserDAO userDAO, byte[] JWTSecret) {
		this.groupDAO = groupDAO;
		this.groupListDAO = groupListDAO;
		this.userDAO = userDAO;
		this.JWTSecret = JWTSecret;
		this.authAlgorithm = Algorithm.HMAC256(this.JWTSecret);
		this.authVerifier = JWT.require(this.authAlgorithm)
				.withIssuer("SQLiggyBank")
				.build();
	}

	@GET
	@UnitOfWork
	@Path("{groupName}")
	public Response getGroup(@HeaderParam("Authorization") String authorization, @PathParam("groupName") String groupName) {
		if (authorization != null) { // are they a user? Let's see if they pass the test.
			return findGroup(groupName, authorization);
		} else {
			return Response
					.status(Response.Status.FORBIDDEN)
					.entity(new GenericResponse(Response.Status.FORBIDDEN.getStatusCode(), "You must be logged in to view group information!"))
					.build();
		}
	}

	private Response findGroup(String groupName, String authorization) {
		authorization = authorization.substring(authorization.indexOf(" ") + 1); // remove "Bearer" from Authorization header
		try {
			DecodedJWT jwt = authVerifier.verify(authorization); // verify token
			Group group = groupDAO.findByName(groupName).orElseThrow(() -> new NotFoundException("No such group."));
			if (groupListDAO.isUserPartOfGroup(jwt.getClaim("username").asString(), groupName)) { // user part of group, give group information
				return Response.ok(group).build();
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
		}
	}

	@GET
	@UnitOfWork
	@Path("{groupName}/invites")
	public Response getInvites(@HeaderParam("Authorization") String authorization, @PathParam("groupName") String groupName) {
		if (authorization != null) { // get the invites
			return inviteList(authorization, groupName);
		} else { // if not owner, they can kiss the links goodbye
			return Response
					.status(Response.Status.FORBIDDEN)
					.entity(new GenericResponse(Response.Status.FORBIDDEN.getStatusCode(), "You must be logged in to view group information!"))
					.build();
		}
	}

	private Response inviteList(String authorization, String groupName) {
		authorization = authorization.substring(authorization.indexOf(" ") + 1); // remove "Bearer" from Authorization header
		try {
			DecodedJWT jwt = authVerifier.verify(authorization); // verify token
			Group group = groupDAO.findByName(groupName).orElseThrow(() -> new NotFoundException("No such group."));
			if (groupDAO.isUserOwnerOfGroup(jwt.getClaim("username").asString(), groupName)) { // user owner of group, give group invites
				ArrayList<Invite> groupInvites = new ArrayList<>();
				for (Invite invite :
						invites) {
					if (invite.getGroup().equals(groupName)) {
						groupInvites.add(invite);
					}
				}
				return Response.ok(groupInvites).build();
			} else { // not owner, eject client
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
		}
	}

	@POST
	@UnitOfWork
	@Path("{groupName}/invites/new")
	public Response createInvite(@HeaderParam("Authorization") String authorization, @PathParam("groupName") String groupName) {
		if (authorization != null) { // get the invites
			return generateInviteLink(authorization, groupName);
		} else { // if not owner, they can kiss the links goodbye
			return Response
					.status(Response.Status.FORBIDDEN)
					.entity(new GenericResponse(Response.Status.FORBIDDEN.getStatusCode(), "You must be the group owner to generate invite links for this group!"))
					.build();
		}
	}

	private Response generateInviteLink(String authorization, String groupName) {
		authorization = authorization.substring(authorization.indexOf(" ") + 1); // remove "Bearer" from Authorization header
		try {
			DecodedJWT jwt = authVerifier.verify(authorization); // verify token
			Group group = groupDAO.findByName(groupName).orElseThrow(() -> new NotFoundException("No such group."));
			if (groupDAO.isUserOwnerOfGroup(jwt.getClaim("username").asString(), groupName)) { // user owner of group, give group invites
				Invite invite = new Invite(groupName, new Date());
				invites.add(invite);
				return Response
						.ok(invite)
						.build();
			} else { // not owner, eject client
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
		}
	}

	@POST
	@UnitOfWork
	@Path("{groupName}/invite/{inviteID}")
	public Response groupInvite(@HeaderParam("Authorization") String authorization, @PathParam("groupName") String groupName, @PathParam("inviteID") String inviteUUID) {
		if (authorization != null) { // get the invites
			return addUserToGroup(authorization, groupName);
		} else { // if not owner, they can kiss the links goodbye
			return Response
					.status(Response.Status.FORBIDDEN)
					.entity(new GenericResponse(Response.Status.FORBIDDEN.getStatusCode(), "You must be logged in to accept invites!"))
					.build();
		}
	}

	@POST
	@UnitOfWork
	@Path("/new")
	public Response addGroup(@HeaderParam("Authorization") String authorization, String body) {
		if (authorization != null) { // let's create a group
			return createGroup(authorization, body);
		} else { // if not logged in, eject client
			return Response
					.status(Response.Status.FORBIDDEN)
					.entity(new GenericResponse(Response.Status.FORBIDDEN.getStatusCode(), "You must be the group owner to generate invite links for this group!"))
					.build();
		}
	}

	private Response createGroup(String authorization, String groupBody) {
		authorization = authorization.substring(authorization.indexOf(" ") + 1); // remove "Bearer" from Authorization header
		try {
			DecodedJWT jwt = authVerifier.verify(authorization); // verify token
			User owner = userDAO.findByUsername(jwt.getClaim("username").asString()).orElseThrow(() -> new NotFoundException("User not found!"));
			Group tempGroup = new ObjectMapper().readValue(groupBody, Group.class); // create new Group object
			tempGroup.setOwner(owner);
			groupDAO.create(tempGroup);
			return addUserToGroup(authorization, tempGroup.getName());
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
					.entity(new InternalErrorResponse(Response.Status.BAD_REQUEST.getStatusCode(), "Cannot parse submitted group body!", e.getMessage()))
					.build();
		} catch (IOException e) {
			return Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(new InternalErrorResponse(e.getMessage()))
					.build();
		}
	}

	private Response addUserToGroup(String authorization, String groupName) {
		authorization = authorization.substring(authorization.indexOf(" ") + 1); // remove "Bearer" from Authorization header
		try {
			DecodedJWT jwt = authVerifier.verify(authorization); // verify token
			Group group = groupDAO.findByName(groupName).orElseThrow(() -> new NotFoundException("No such group."));
			if (!groupListDAO.isUserPartOfGroup(jwt.getClaim("username").asString(), groupName)) { // user not part of group, put them in
				groupListDAO.addUserToGroup(userDAO.findByUsername(jwt.getClaim("username").asString()).orElseThrow(() -> new NotFoundException("No user found!")), group);
				return Response
						.ok()
						.entity(new JoinedGroupResponse(group.getUuid()))
						.build();
			} else { // part of group, eject client
				return Response
						.status(Response.Status.FORBIDDEN)
						.entity(new GenericResponse(Response.Status.FORBIDDEN.getStatusCode(), "You are already part of this group!"))
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

	@PATCH
	@UnitOfWork
	@Path("{groupName}")
	public Response editGroupInfo(@HeaderParam("Authorization") String authorization, @PathParam("groupName") String groupName, String body) {
		if (authorization != null) { // get the invites
			return editGroup(authorization, groupName, body);
		} else { // if not owner, they can kiss the links goodbye
			return Response
					.status(Response.Status.FORBIDDEN)
					.entity(new GenericResponse(Response.Status.FORBIDDEN.getStatusCode(), "You must be the group owner to edit group information!"))
					.build();
		}
	}

	private Response editGroup(String authorization, String groupName, String newGroup) {
		authorization = authorization.substring(authorization.indexOf(" ") + 1); // remove "Bearer" from Authorization header
		try {
			Group group = groupDAO.findByName(groupName).orElseThrow(() -> new NotFoundException("No such group."));
			DecodedJWT jwt = authVerifier.verify(authorization); // verify token
			if (groupDAO.isUserOwnerOfGroup(jwt.getClaim("username").asString(), groupName)) { // are they ok?
				Group tempGroup = new ObjectMapper().readValue(newGroup, Group.class); // create new Group object
				// Apparently, Hibernate doesn't like you modifying the objects it remembers in memory, even if they
				// are functionally the same. In conclusion, crap code like this shows up, where I have to replace
				// everything in the original one.
				//
				// I'm just happy I don't have to make my own actual SQL queries :)
				group.setName(tempGroup.getName());
				group.setDescription(tempGroup.getDescription());
				group.setOwner(tempGroup.getOwner());
				// update group
				groupDAO.update(group);
				return Response
						.ok(new GenericResponse(Response.Status.OK.getStatusCode(), "Update complete!"))
						.build();
			} else { // wrong user, eject client
				return Response
						.status(Response.Status.FORBIDDEN)
						.entity(new GenericResponse(Response.Status.FORBIDDEN.getStatusCode(), "You must be the group owner to edit this info!"))
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
		} catch (JsonParseException | JsonMappingException e) { // they screwed up the JSON for the group, eject them
			e.printStackTrace();
			return Response
					.status(Response.Status.BAD_REQUEST)
					.entity(new GenericResponse(Response.Status.BAD_REQUEST.getStatusCode(), "Invalid new group information! Try again!"))
					.build();
		} catch (IOException e) { // internal server error, apologize
			e.printStackTrace();
			return Response
					.serverError()
					.entity(new InternalErrorResponse("Could not access database!"))
					.build();
		}
	}

	@DELETE
	@UnitOfWork
	@Path("{groupName}")
	public Response deleteGroup(@HeaderParam("Authorization") String authorization, @PathParam("groupName") String groupName) {
		if (authorization != null) { // get the invites
			return removeGroup(authorization, groupName);
		} else { // if not owner, they can kiss the links goodbye
			return Response
					.status(Response.Status.FORBIDDEN)
					.entity(new GenericResponse(Response.Status.FORBIDDEN.getStatusCode(), "You must be the group owner to delete this group!"))
					.build();
		}
	}

	private Response removeGroup(String authorization, String groupName) {
		authorization = authorization.substring(authorization.indexOf(" ") + 1); // remove "Bearer" from Authorization header
		try {
			Group group = groupDAO.findByName(groupName).orElseThrow(() -> new NotFoundException("No such username."));
			DecodedJWT jwt = authVerifier.verify(authorization); // verify token
			if (groupDAO.isUserOwnerOfGroup(jwt.getClaim("username").asString(), groupName)) { // if user is correct...
				groupDAO.delete(group); // delete that group of bad boiz
				return Response // return OK
						.ok(new GenericResponse(Response.Status.OK.getStatusCode(), "Deleted! Get the people back together again! :("))
						.build();

			} else { // nice try hacker, eject client
				return Response
						.status(Response.Status.FORBIDDEN)
						.entity(new GenericResponse(Response.Status.FORBIDDEN.getStatusCode(), "You are not the owner! Man, not cool!"))
						.build();
			}
		} catch (TokenExpiredException e) {
			return Response
					.status(Response.Status.UNAUTHORIZED)
					.entity(new GenericResponse(Response.Status.UNAUTHORIZED.getStatusCode(), "Token expired! Log in again!"))
					.build();
		} catch (JWTVerificationException e) { // token's screwed, eject client
			return Response
					.status(Response.Status.UNAUTHORIZED)
					.entity(new GenericResponse(Response.Status.UNAUTHORIZED.getStatusCode(), "Invalid authentication scheme!"))
					.build();
		}
	}
}
