package ro.lbi.sqliggybank.server.Resources;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.PATCH;
import org.hibernate.HibernateException;
import ro.lbi.sqliggybank.server.Core.Account;
import ro.lbi.sqliggybank.server.Core.User;
import ro.lbi.sqliggybank.server.Database.UserDAO;
import ro.lbi.sqliggybank.server.Responses.GenericResponse;
import ro.lbi.sqliggybank.server.Responses.InternalErrorResponse;
import ro.lbi.sqliggybank.server.Responses.JWTResponse;
import ro.lbi.sqliggybank.server.Responses.NotFoundResponse;

import javax.persistence.NoResultException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Path("/api/users/")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
	private final UserDAO userDAO;
	@SuppressWarnings("FieldCanBeLocal")
	private final byte[] JWTSecret;
	private final Algorithm authAlgorithm;
	private final JWTVerifier authVerifier;

	public UserResource(UserDAO userDAO, byte[] JWTSecret) {
		this.userDAO = userDAO;
		this.JWTSecret = JWTSecret;
		this.authAlgorithm = Algorithm.HMAC256(this.JWTSecret);
		this.authVerifier = JWT.require(this.authAlgorithm)
				.withIssuer("SQLiggyBank")
				.build();
	}

	@GET
	@UnitOfWork
	@Path("{username}")
	public Response getUser(@HeaderParam("Authorization") String authorization, @PathParam("username") String username) {
		if (authorization != null) {
			return findAuthenticatedUsername(username, authorization);
		} else {
			return findUsername(username);
		}
	}

	@PATCH
	@UnitOfWork
	@Path("{username}")
	public Response editUser(@HeaderParam("Authorization") String authorization, @PathParam("username") String username, String body) {
		if (authorization != null) {
			return updateUser(username, authorization, body);
		} else {
			return Response
					.status(Response.Status.UNAUTHORIZED)
					.entity(new GenericResponse(Response.Status.UNAUTHORIZED.toString(), "You must be authenticated to edit this user!"))
					.build();
		}
	}

	@DELETE
	@UnitOfWork
	@Path("{username}")
	public Response deleteUser(@HeaderParam("Authorization") String authorization, @PathParam("username") String username) {
		if (authorization != null) {
			return removeUser(username, authorization);
		} else {
			return Response
					.status(Response.Status.UNAUTHORIZED)
					.entity(new GenericResponse(Response.Status.UNAUTHORIZED.toString(), "You must be authenticated to edit this user!"))
					.build();
		}
	}

	@POST
	@UnitOfWork
	@Path("new")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response registerUser(String body) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			User user = mapper.readValue(body, User.class);
			user.setUuid(UUID.randomUUID());
			userDAO.create(user);
			LocalDate expiry = LocalDate.now();
			expiry = expiry.plusDays(5);
			Date expiryDate = Date.from(expiry.atStartOfDay(ZoneId.systemDefault()).toInstant());
			String token = JWT.create()
					.withIssuer("SQLiggyBank")
					.withClaim("username", user.getUsername())
					.withClaim("password", user.getPassword())
					.withExpiresAt(expiryDate)
					.sign(authAlgorithm);
			return Response
					.ok(new JWTResponse(Response.Status.OK.toString(), "Registration complete!", token))
					.build();
		} catch (IOException e) {
			e.printStackTrace();
			return Response
					.serverError()
					.entity(new InternalErrorResponse("Could not access database!"))
					.build();
		} catch (HibernateException e) {
			e.printStackTrace();
			return Response
					.serverError()
					.entity(new InternalErrorResponse("Could not add user to database!"))
					.build();
		}
	}

	@POST
	@UnitOfWork
	@Path("login")
	public Response loginUser(String body) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			Account account = mapper.readValue(body, Account.class);
			User user = userDAO.findByUsername(account.getUsername()).orElseThrow(() -> new NotFoundException("No such username."));
			if (!user.getPassword().equals(account.getPassword())) {
				return Response.status(Response.Status.FORBIDDEN).entity("Invalid username and password combination!").build();
			}
			LocalDate expiry = LocalDate.now();
			expiry = expiry.plusDays(5);
			Date expiryDate = Date.from(expiry.atStartOfDay(ZoneId.systemDefault()).toInstant());
			String token = JWT.create()
					.withIssuer("SQLiggyBank")
					.withClaim("username", account.getUsername())
					.withClaim("password", account.getPassword())
					.withExpiresAt(expiryDate)
					.sign(authAlgorithm);
			return Response
					.ok(new JWTResponse(Response.Status.OK.toString(), "Login complete!", token))
					.build();
		} catch (IOException e) {
			e.printStackTrace();
			return Response
					.serverError()
					.entity(new InternalErrorResponse("Could not access database!"))
					.build();
		} catch (NoResultException e) {
			e.printStackTrace();
			return Response
					.status(Response.Status.NOT_FOUND)
					.entity(new NotFoundResponse("Could not find user!"))
					.build();
		}
	}

	private Response findUsername(String username) {
		try {
			User user = userDAO.findByUsername(username).orElseThrow(() -> new NotFoundException("No such username."));
			User redactedUser = new User(new UUID(0, 0), user.getUsername(), "[REDACTED]", user.getFirst_name(), user.getLast_name(), user.getEmail());
			return Response
					.ok(redactedUser)
					.build();
		} catch (NoResultException e) {
			return Response
					.status(Response.Status.NOT_FOUND)
					.entity(new NotFoundResponse("Could not find user!"))
					.build();
		}
	}

	private Response findAuthenticatedUsername(String username, String authorization) {
			authorization = authorization.substring(authorization.indexOf(" ") + 1); // remove "Bearer" from Authorization header
			try {
				User user = userDAO.findByUsername(username).orElseThrow(() -> new NotFoundException("No such username."));
				DecodedJWT jwt = authVerifier.verify(authorization);
				if (jwt.getClaim("username").asString().equals(username) &&
					jwt.getClaim("password").asString().equals(user.getPassword())) {
					return Response.ok(user).build();
				} else {
					return Response
							.status(Response.Status.FORBIDDEN)
							.entity(new GenericResponse(Response.Status.FORBIDDEN.toString(), "Wrong username and password combination!"))
							.build();
				}
			} catch (JWTVerificationException e) {
				return Response
						.status(Response.Status.UNAUTHORIZED)
						.entity(new GenericResponse(Response.Status.UNAUTHORIZED.toString(), "Invalid authentication scheme!"))
						.build();
			}
	}

	private Response updateUser(String username, String authorization, String newUser) {
		authorization = authorization.substring(authorization.indexOf(" ") + 1); // remove "Bearer" from Authorization header
		try {
			User user = userDAO.findByUsername(username).orElseThrow(() -> new NotFoundException("No such username."));
			DecodedJWT jwt = authVerifier.verify(authorization);
			if (jwt.getClaim("username").asString().equals(username) &&
					jwt.getClaim("password").asString().equals(user.getPassword())) {
				User tempUser = new ObjectMapper().readValue(newUser, User.class);
				user.setUsername(tempUser.getUsername());
				user.setPassword(tempUser.getPassword());
				user.setFirst_name(tempUser.getFirst_name());
				user.setLast_name(tempUser.getLast_name());
				user.setEmail(tempUser.getEmail());
				userDAO.update(user);
				LocalDate expiry = LocalDate.now();
				expiry = expiry.plusDays(5);
				Date expiryDate = Date.from(expiry.atStartOfDay(ZoneId.systemDefault()).toInstant());
				String token = JWT.create()
						.withIssuer("SQLiggyBank")
						.withClaim("username", user.getUsername())
						.withClaim("password", user.getPassword())
						.withExpiresAt(expiryDate)
						.sign(authAlgorithm);
				return Response
						.ok(new JWTResponse(Response.Status.OK.toString(), "Update complete!", token))
						.build();
			} else {
				return Response
						.status(Response.Status.FORBIDDEN)
						.entity(new GenericResponse(Response.Status.FORBIDDEN.toString(), "Wrong username and password combination!"))
						.build();
			}
		} catch (JWTVerificationException e) {
			return Response
					.status(Response.Status.UNAUTHORIZED)
					.entity(new GenericResponse(Response.Status.UNAUTHORIZED.toString(), "Invalid authentication scheme!"))
					.build();
		} catch (JsonParseException | JsonMappingException e) {
			e.printStackTrace();
			return Response
					.status(Response.Status.BAD_REQUEST)
					.entity(new GenericResponse(Response.Status.BAD_REQUEST.toString(), "Invalid new user information! Try again!"))
					.build();
		} catch (IOException e) {
			e.printStackTrace();
			return Response
					.serverError()
					.entity(new InternalErrorResponse("Could not access database!"))
					.build();
		}
	}

	private Response removeUser(String username, String authorization) {
		authorization = authorization.substring(authorization.indexOf(" ") + 1); // remove "Bearer" from Authorization header
		try {
			User user = userDAO.findByUsername(username).orElseThrow(() -> new NotFoundException("No such username."));
			DecodedJWT jwt = authVerifier.verify(authorization);
			if (jwt.getClaim("username").asString().equals(username) &&
					jwt.getClaim("password").asString().equals(user.getPassword())) {
				userDAO.delete(user);
				return Response
						.ok(new GenericResponse(Response.Status.OK.toString(), "Deleted! Sorry to see you go :("))
						.build();

			} else {
				return Response
						.status(Response.Status.FORBIDDEN)
						.entity(new GenericResponse(Response.Status.FORBIDDEN.toString(), "Wrong username and password combination!"))
						.build();
			}
		} catch (JWTVerificationException e) {
			return Response
					.status(Response.Status.UNAUTHORIZED)
					.entity(new GenericResponse(Response.Status.UNAUTHORIZED.toString(), "Invalid authentication scheme!"))
					.build();
		}
	}
}
