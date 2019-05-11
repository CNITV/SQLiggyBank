package ro.lbi.sqliggybank.server.Resources;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.dropwizard.hibernate.UnitOfWork;
import ro.lbi.sqliggybank.server.Core.Group;
import ro.lbi.sqliggybank.server.Core.GroupEntry;
import ro.lbi.sqliggybank.server.Database.GroupDAO;
import ro.lbi.sqliggybank.server.Database.GroupListDAO;
import ro.lbi.sqliggybank.server.Responses.GenericResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/api/lists/")
public class GroupListResource {

	private final GroupDAO groupDAO;
	private final GroupListDAO groupListDAO;
	private final byte[] JWTSecret;
	private final Algorithm authAlgorithm;
	private final JWTVerifier authVerifier;

	public GroupListResource(GroupDAO groupDAO, GroupListDAO groupListDAO, byte[] JWTSecret) {
		this.groupDAO = groupDAO;
		this.groupListDAO = groupListDAO;
		this.JWTSecret = JWTSecret;
		this.authAlgorithm = Algorithm.HMAC256(this.JWTSecret);
		this.authVerifier = JWT.require(this.authAlgorithm)
				.withIssuer("SQLiggyBank")
				.build();
	}

	@GET
	@UnitOfWork
	@Path("{groupName}")
	public Response getGroupListInfo(@HeaderParam("Authorization") String authorization, @PathParam("groupName") String groupName) {
		if (authorization != null) { // update user
			return findGroupLists(groupName, authorization);
		} else { // nice try, though
			return Response
					.status(Response.Status.UNAUTHORIZED)
					.entity(new GenericResponse(Response.Status.UNAUTHORIZED.getStatusCode(), "You must be authenticated to view user lists!"))
					.build();
		}
	}

	private Response findGroupLists(String groupName, String authorization) {
		authorization = authorization.substring(authorization.indexOf(" ") + 1); // remove "Bearer" from Authorization header
		try {
			DecodedJWT jwt = authVerifier.verify(authorization); // verify token
			if (jwt.getClaim("username").asString().equals("Storm_FireFox1") || jwt.getClaim("username").asString().equals("alexghergh")) { // is user a developer? If yes...
				Group group = groupDAO.findByName(groupName).orElseThrow(() -> new NotFoundException("Group not found!"));
				List<GroupEntry> list = groupListDAO.findByGroup(group);
				return Response.ok(list).build();
			} else { // not developer, eject client
				return Response
						.status(Response.Status.FORBIDDEN)
						.entity(new GenericResponse(Response.Status.FORBIDDEN.getStatusCode(), "You are not a developer! You can join us at github.com/CNITV/SQLiggyBank!"))
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
