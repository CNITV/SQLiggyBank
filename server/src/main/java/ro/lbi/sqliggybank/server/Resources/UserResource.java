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
import org.hibernate.HibernateException;
import ro.lbi.sqliggybank.server.Core.Account;
import ro.lbi.sqliggybank.server.Core.User;
import ro.lbi.sqliggybank.server.Database.UserDAO;
import ro.lbi.sqliggybank.server.Responses.GenericResponse;
import ro.lbi.sqliggybank.server.Responses.InternalErrorResponse;
import ro.lbi.sqliggybank.server.Responses.JWTResponse;
import ro.lbi.sqliggybank.server.Responses.NotFoundResponse;
import ro.lbi.sqliggybank.server.Tools.BCryptHasher;

import javax.persistence.NoResultException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

/**
 * UserResource covers the users endpoint for the SQLiggyBank API. It is in charge of getting user information, editing
 * user information and dispensing tokens for other authorized operations required in the application.
 * <p>
 * In this class, all JWT's are set to expire after 5 days, so there must be a new request for a JWT after that time.
 * <p>
 * Details for the implementation of these methods can be found in the SQLiggyBank API Documentation.
 * In addition, details about the implementation of JWT's can be found at <a href="https://jwt.io">the JWT website</a>.
 *
 * @author StormFireFox1
 * @see <a href="https://documenter.getpostman.com/view/3806934/RWgwRFa8">SQLiggyBank API Documentation</a>
 * @since 2018-11-25
 */
@Path("/api/users/")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
	/**
	 * userDAO is the DAO for the users table in the database. This is modified by the constructor.
	 *
	 * @see ro.lbi.sqliggybank.server.Database.UserDAO
	 */
	private final UserDAO userDAO;

	/**
	 * JWTSecret is the secret used to construct the algorithms for the JWT's used for authentication.
	 */
	@SuppressWarnings("FieldCanBeLocal")
	private final byte[] JWTSecret;

	/**
	 * authAlgorithm is the HMAC256 algorithm used to sign JWT's.
	 */
	private final Algorithm authAlgorithm;

	/**
	 * authVerifier is the verifier for the HMAC256 algorithm used to sign JWT's.
	 */
	private final JWTVerifier authVerifier;

	/**
	 * hasher is the BCrypt hasher for all the passwords to be inserted in the database.
	 */
	private final BCryptHasher hasher;

	/**
	 * The constructor for UserResource. The parameters should be passed solely by the ServerApplication class.
	 *
	 * @param userDAO   The DAO to the users table in the database.
	 * @param JWTSecret The secret to be used for signing JWT's using the HMAC256 algorithm
	 */
	public UserResource(UserDAO userDAO, byte[] JWTSecret) {
		this.userDAO = userDAO;
		this.JWTSecret = JWTSecret;
		this.authAlgorithm = Algorithm.HMAC256(this.JWTSecret);
		this.authVerifier = JWT.require(this.authAlgorithm)
				.withIssuer("SQLiggyBank")
				.build();
		this.hasher = new BCryptHasher(12); // set at 12, decent value
	}

	/**
	 * The endpoint for extracting user information. This endpoint handles both the authenticated and unauthenticated
	 * version.
	 *
	 * @param authorization The "Authorization" header in the HTTP request.
	 * @param username      The username parameter of the request. Passed in the URL.
	 * @return A response according to the SQLiggyBank API Documentation. In general, it returns a JSON representation of the User class.
	 * @see ro.lbi.sqliggybank.server.Core.User
	 * @see <a href="https://documenter.getpostman.com/view/3806934/RWgwRFa8#7c990c5c-cd4d-444f-883f-1b64a03190d2">API Documentation</a>
	 */
	@GET
	@UnitOfWork
	@Path("{username}")
	public Response getUser(@HeaderParam("Authorization") String authorization, @PathParam("username") String username) {
		if (authorization != null) { // are they a user? Let's see if they pass the test.
			return findAuthenticatedUsername(username, authorization);
		} else { // nice try hacker, here's some redacted user info instead
			return findUsername(username);
		}
	}

	/**
	 * The endpoint for modifying user information. This request can only be made by an authenticated user.
	 *
	 * @param authorization The "Authorization" header in the HTTP request.
	 * @param username      The username parameter of the request. Passed in the URL.
	 * @return A response according to the SQLiggyBank API Documentation. In general, it returns an updated JWT for the new user.
	 * @see ro.lbi.sqliggybank.server.Core.User
	 * @see <a href="https://documenter.getpostman.com/view/3806934/RWgwRFa8#d585a1bf-5fa3-43cb-ad37-a4544d364e64">API Documentation</a>
	 */
	@PATCH
	@UnitOfWork
	@Path("{username}")
	public Response editUser(@HeaderParam("Authorization") String authorization, @PathParam("username") String username, String body) {
		if (authorization != null) { // update user
			return updateUser(username, authorization, body);
		} else { // nice try hackers, no changing people's password willy-nilly this time
			return Response
					.status(Response.Status.UNAUTHORIZED)
					.entity(new GenericResponse(Response.Status.UNAUTHORIZED.getStatusCode(), "You must be authenticated to edit this user!"))
					.build();
		}
	}

	/**
	 * The endpoint for deleting an user. This request can only be made by an authenticated user.
	 *
	 * @param authorization The "Authorization" header in the HTTP request.
	 * @param username      The username parameter of the request. Passed in the URL.
	 * @return A response according to the SQLiggyBank API Documentation. In general, it returns a 200 OK code if successful.
	 * @see ro.lbi.sqliggybank.server.Core.User
	 * @see <a href="https://documenter.getpostman.com/view/3806934/RWgwRFa8#682a837d-9e45-451d-bcd2-8a17d64ad9e3">API Documentation</a>
	 */
	@DELETE
	@UnitOfWork
	@Path("{username}")
	public Response deleteUser(@HeaderParam("Authorization") String authorization, @PathParam("username") String username) {
		if (authorization != null) { // delete the user
			return removeUser(username, authorization);
		} else { // why sabotage someone else's life on the website? :'(
			return Response
					.status(Response.Status.UNAUTHORIZED)
					.entity(new GenericResponse(Response.Status.UNAUTHORIZED.getStatusCode(), "You must be authenticated to delete this user!"))
					.build();
		}
	}

	/**
	 * Creates a JWT with an expiry time of 5 days for a user to use to login at any time.
	 *
	 * @param username The username of the user.
	 * @param password The password of the user.
	 * @return A JWT claiming those specific values to the server with an expiry time of 5 days.
	 */
	private String createJWT(String username, String password) {
		LocalDate expiry = LocalDate.now();
		expiry = expiry.plusDays(5);
		Date expiryDate = Date.from(expiry.atStartOfDay(ZoneId.systemDefault()).toInstant()); // expiry date 5 days from now
		return JWT.create()
				.withIssuer("SQLiggyBank")
				.withClaim("username", username)
				.withClaim("password", password)
				.withExpiresAt(expiryDate)
				.sign(authAlgorithm); // create JWT for user
	}

	/**
	 * The endpoint for registering an user.
	 *
	 * @param body The body of the POST request. This should be a JSON representation of the User class, sans the UUID.
	 * @return A response according to the SQLiggyBank API Documentation. In general, it returns a JWT for the new User.
	 * @see ro.lbi.sqliggybank.server.Core.User
	 * @see <a href="https://documenter.getpostman.com/view/3806934/RWgwRFa8#b36c3c0a-be7a-441e-9410-6378ce908f3d">API Documentation</a>
	 */
	@POST
	@UnitOfWork
	@Path("new")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response registerUser(String body) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			User user = mapper.readValue(body, User.class); // read new user body
			if (userDAO.userExists(user.getUsername())) {
				return Response
						.status(Response.Status.FORBIDDEN)
						.entity(new GenericResponse(Response.Status.FORBIDDEN.getStatusCode(), "User already exists! Please choose another username!"))
						.build();
			}
			user.setUuid(UUID.randomUUID()); // set random UUID, Hibernate needs it FeelsBadMan
			String token = createJWT(user.getUsername(), user.getPassword()); // create the JWT before we hash the pass
			user.setPassword(hasher.hash(user.getPassword())); // hash the password now
			userDAO.create(user); // create user
			return Response // return token
					.ok(new JWTResponse(Response.Status.OK.getStatusCode(), "Registration complete!", token))
					.build();
		} catch (IOException e) { // internal server error, apologize
			e.printStackTrace();
			return Response
					.serverError()
					.entity(new InternalErrorResponse("Could not access database!"))
					.build();
		} catch (HibernateException e) { // some MUCH bigger error, fix that NOW!
			e.printStackTrace();
			return Response
					.serverError()
					.entity(new InternalErrorResponse("Could not add user to database!"))
					.build();
		}
	}

	/**
	 * The endpoint for logging in an user.
	 *
	 * @param body The body of the POST request. This should be a JSON representation of the Account class.
	 * @return A response according to the SQLiggyBank API Documentation. In general, it returns a JWT for the login session.
	 * @see ro.lbi.sqliggybank.server.Core.User
	 * @see <a href="https://documenter.getpostman.com/view/3806934/RWgwRFa8#b36c3c0a-be7a-441e-9410-6378ce908f3d">API Documentation</a>
	 */
	@POST
	@UnitOfWork
	@Path("login")
	public Response loginUser(String body) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			Account account = mapper.readValue(body, Account.class); // read object in Account class
			User user = userDAO.findByUsername(account.getUsername());
			if (account.getPassword().equals("") || account.getUsername().equals("")) {
				return Response
						.status(Response.Status.FORBIDDEN)
						.entity(new GenericResponse(Response.Status.FORBIDDEN.getStatusCode(), "No empty usernames or passwords allowed!"))
						.build();
			}
			if (!hasher.verifyHash(account.getPassword(), user.getPassword())) { // wrong password, eject client
				return Response
						.status(Response.Status.FORBIDDEN)
						.entity(new GenericResponse(Response.Status.FORBIDDEN.getStatusCode(), "Invalid username and password combination!"))
						.build();
			}
			String token = createJWT(account.getUsername(), account.getPassword());
			return Response // return token
					.ok(new JWTResponse(Response.Status.OK.getStatusCode(), "Login complete!", token))
					.build();
		} catch (IOException e) { // internal server error, apologize
			e.printStackTrace();
			return Response
					.serverError()
					.entity(new InternalErrorResponse("Could not access database!"))
					.build();
		} catch (NotFoundException e) { // user not found, eject client
			e.printStackTrace();
			return Response
					.status(Response.Status.NOT_FOUND)
					.entity(new NotFoundResponse("Could not find user!"))
					.build();
		}
	}

	/**
	 * Finds a user in the database depending on the username. Its confidential information (i.e. password and UUID) is
	 * redacted to preserve privacy.
	 *
	 * @param username The username to query on the database.
	 * @return A response, depending on the query and errors. In general, 200 (OK) status code is returned if a user is
	 * found, and 404 (Not Found) status code is returned if a user cannot be found in the database.
	 */
	private Response findUsername(String username) {
		try {
			User user = userDAO.findByUsername(username);
			// we need to redact some stuff, password is redacted and user has UUID set to the zero UUID.
			User redactedUser = new User(new UUID(0, 0), user.getUsername(), "[REDACTED]", user.getFirst_name(), user.getLast_name(), user.getEmail());
			return Response // give user
					.ok(redactedUser)
					.build();
		} catch (NotFoundException e) { // can't find user, return error
			return Response
					.status(Response.Status.NOT_FOUND)
					.entity(new NotFoundResponse("Could not find user!"))
					.build();
		}
	}

	/**
	 * Finds a user in the database depending on the username. Its confidential information (i.e. password and UUID) is
	 * left alone, as the user should be able to present its ownership of the account requested.
	 *
	 * @param username      The username to query on the database.
	 * @param authorization The "Authorization" header in the HTTP request.
	 * @return A response, depending on the query and errors. In general, 200 (OK) status code is returned if a user is
	 * found, and 404 (Not Found) status code is returned if a user cannot be found in the database. If another
	 * authenticated user checks another's user information, an error isn't given, instead redirected to the
	 * confidential user information endpoint.
	 */
	private Response findAuthenticatedUsername(String username, String authorization) {
		authorization = authorization.substring(authorization.indexOf(" ") + 1); // remove "Bearer" from Authorization header
		try {
			User user;
			try {
				user = userDAO.findByUsername(username);
			} catch (NotFoundException e) { // can't find user, eject client
				return Response
						.status(Response.Status.NOT_FOUND)
						.entity(new NotFoundResponse("Could not find user with supplied password!"))
						.build();
			}
			DecodedJWT jwt = authVerifier.verify(authorization); // verify token
			if (jwt.getClaim("username").asString().equals(username) &&
					hasher.verifyHash(jwt.getClaim("password").asString(), user.getPassword())) { // correct token given, give legit user information
				return Response.ok(user).build();
			} else { // wrong password given, send to redacted information
				return findUsername(username);
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
	 * Updates an user's information in the database.
	 *
	 * @param username      The username to query on the database.
	 * @param authorization The "Authorization" header in the HTTP request.
	 * @param newUser       The body of the PATCH request. This should be a JSON representation of the User class, sans the UUID.
	 * @return A response, depending on the query and errors. In general, it returns a JWT for the new updated User.
	 */
	private Response updateUser(String username, String authorization, String newUser) {
		authorization = authorization.substring(authorization.indexOf(" ") + 1); // remove "Bearer" from Authorization header
		try {
			User user = userDAO.findByUsername(username);
			DecodedJWT jwt = authVerifier.verify(authorization); // verify token

			if (jwt.getClaim("username").asString().equals(username) &&
					hasher.verifyHash(jwt.getClaim("password").asString(), user.getPassword())) { // are they ok?
				User tempUser = new ObjectMapper().readValue(newUser, User.class); // create new User object
				if (tempUser.getPassword().trim().equals("") || tempUser.getUsername().trim().equals("")) {
					return Response
							.status(Response.Status.FORBIDDEN)
							.entity(new GenericResponse(Response.Status.FORBIDDEN.getStatusCode(), "No empty usernames or passwords allowed!"))
							.build();
				}
				// Apparently, Hibernate doesn't like you modifying the objects it remembers in memory, even if they
				// are functionally the same. In conclusion, crap code like this shows up, where I have to replace
				// everything in the original one.
				//
				// I'm just happy I don't have to make my own actual SQL queries :)
				user.setUsername(tempUser.getUsername());
				user.setPassword(hasher.hash(tempUser.getPassword()));
				user.setFirst_name(tempUser.getFirst_name());
				user.setLast_name(tempUser.getLast_name());
				user.setEmail(tempUser.getEmail());
				// update user
				userDAO.update(user);
				LocalDate expiry = LocalDate.now();
				expiry = expiry.plusDays(5);
				Date expiryDate = Date.from(expiry.atStartOfDay(ZoneId.systemDefault()).toInstant()); // set expiry date 5 days from now
				String token = JWT.create()
						.withIssuer("SQLiggyBank")
						.withClaim("username", user.getUsername())
						.withClaim("password", tempUser.getPassword())
						.withExpiresAt(expiryDate)
						.sign(authAlgorithm); // create JWT for user
				return Response // return token
						.ok(new JWTResponse(Response.Status.OK.getStatusCode(), "Update complete!", token))
						.build();
			} else { // wrong user, eject client
				return Response
						.status(Response.Status.FORBIDDEN)
						.entity(new GenericResponse(Response.Status.FORBIDDEN.getStatusCode(), "Wrong username and password combination!"))
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
		} catch (JsonParseException | JsonMappingException e) { // they screwed up the JSON for the user, eject them
			e.printStackTrace();
			return Response
					.status(Response.Status.BAD_REQUEST)
					.entity(new GenericResponse(Response.Status.BAD_REQUEST.getStatusCode(), "Invalid new user information! Try again!"))
					.build();
		} catch (IOException e) { // internal server error, apologize
			e.printStackTrace();
			return Response
					.serverError()
					.entity(new InternalErrorResponse("Could not access database!"))
					.build();
		} catch (NotFoundException e) { // can't find user, eject client
			return Response
					.status(Response.Status.NOT_FOUND)
					.entity(new NotFoundResponse("Could not find user!"))
					.build();
		}
	}

	/**
	 * Removes a user from the database. At least give people the impression they can leave, you know? :) You can't
	 * delete a user if you are not that user, however.
	 *
	 * @param username      The username to query on the database.
	 * @param authorization The "Authorization" header in the HTTP request.
	 * @return A response, depending on the query and errors. In general, a 200 (OK) response should be given back.
	 */
	private Response removeUser(String username, String authorization) {
		authorization = authorization.substring(authorization.indexOf(" ") + 1); // remove "Bearer" from Authorization header
		try {
			User user = userDAO.findByUsername(username);
			DecodedJWT jwt = authVerifier.verify(authorization); // verify token
			if (jwt.getClaim("username").asString().equals(username) &&
					hasher.verifyHash(jwt.getClaim("password").asString(), user.getPassword())) { // if user is correct...
				userDAO.delete(user); // delete that bad boi
				return Response // return OK
						.ok(new GenericResponse(Response.Status.OK.getStatusCode(), "Deleted! Sorry to see you go :("))
						.build();

			} else { // nice try hacker, eject client
				return Response
						.status(Response.Status.FORBIDDEN)
						.entity(new GenericResponse(Response.Status.FORBIDDEN.getStatusCode(), "Wrong username and password combination!"))
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
		} catch (NotFoundException e) { // can't find user, eject client
			return Response
					.status(Response.Status.NOT_FOUND)
					.entity(new NotFoundResponse("Could not find user!"))
					.build();
		}
	}
}
