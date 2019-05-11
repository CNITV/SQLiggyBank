package ro.lbi.sqliggybank.client.backend.database;

import okhttp3.*;
import ro.lbi.sqliggybank.client.backend.account.Account;
import ro.lbi.sqliggybank.client.backend.exceptions.BadRequestException;
import ro.lbi.sqliggybank.client.backend.exceptions.ForbiddenException;
import ro.lbi.sqliggybank.client.backend.exceptions.NotFoundException;
import ro.lbi.sqliggybank.client.backend.exceptions.UnauthorizedException;
import ro.lbi.sqliggybank.client.backend.user.User;

import java.io.IOException;

/**
 * This class handles all the requests from the client and calls the API from the server.
 *
 * <p>
 * The connection between the client and the server is made through this class.
 *
 * @author Alexandru GHERGHESCU (alexghergh)
 * @since 2018-12-15
 */
public class DatabaseHandler {

	private String serverUrl = "http://localhost:8010";

	/**
	 * This method is used to log in a user.
	 *
	 * <p>
	 * It calls the server on this endpoint:
	 * POST /api/users/login
	 *
	 * @param account the account data introduced by the user on the client side.
	 * @return the user credentials gotten from the server.
	 * @throws IOException throws this exception if something went wrong with the http call.
	 * @throws ForbiddenException throws this exception if the user's login username/password combination was wrong.
	 * @throws NotFoundException throws this exception if the username wasn't found in the database.
	 */
	public String loginUser(Account account) throws IOException, ForbiddenException, NotFoundException {
		OkHttpClient httpClient = new OkHttpClient();

		MediaType JSON = MediaType.parse("application/json; charset=utf-8");
		RequestBody body = RequestBody.create(JSON, "{\n" +
				"\t\"username\":\"" + account.getUsername() + "\",\n" +
				"\t\"password\":\"" + account.getPassword() + "\"\n" +
				"}");

		Request request = new Request.Builder()
				.url(serverUrl + "/api/users/login")
				.post(body)
				.build();

		Response response = httpClient.newCall(request).execute();

		if (response.code() == 404) {
			throw new NotFoundException("Incorrect username or password!");
		}
		if (response.code() == 403) {
			throw new ForbiddenException("Invalid username and password combination!");
		}

		String result;
		if (response.body() != null) {
			result = response.body().string();
		} else {
			throw new NullPointerException("Response body came out null! Try again!");
		}
		response.close();

		return result;
	}

	/**
	 * This method is used to get all the user information from the server.
	 *
	 * <p>
	 * It calls the server on this endpoint:
	 * GET /api/users/{username}
	 *
	 * @param account the account data introduced by the user on the client side.
	 * @param JWT the JWT needed for the authorization schema (if the JWT is wrong, only public fields are given).
	 * @return a JSON containing the user information.
	 * @throws IOException throws this exception if something went wrong with the http call.
	 * @throws UnauthorizedException throws this exception if the user has an invalid authorization header.
	 * @throws NotFoundException throws this exception if the user resource wasn't found in the database.
	 */
	public String getUser(Account account, String JWT) throws IOException, UnauthorizedException, NotFoundException {
		OkHttpClient httpClient = new OkHttpClient();

		Request request = new Request.Builder()
				.url(serverUrl + "/api/users/" + account.getUsername())
				.get()
				.addHeader("Authorization", "Bearer " + JWT)
				.build();

		Response response = httpClient.newCall(request).execute();

		if (response.code() == 404) {
			throw new NotFoundException("That username doesn't exist!");
		}

		if (response.code() == 401) {
			throw new UnauthorizedException("Invalid authorization header or token!");
		}

		String result;
		if (response.body() != null) {
			result = response.body().string();
		} else {
			throw new NullPointerException("Response body came out null! Try again!");
		}
		response.close();

		return result;
	}

	/**
	 * This method is used to delete a user from the server.
	 *
	 * <p>
	 * It calls the server on this endpoint:
	 * DEL /api/users/{username}
	 *
	 * @param user the user to be deleted.
	 * @return returns an OK response if everything went right.
	 * @throws IOException throws this exception if something went wrong with the http call.
	 * @throws UnauthorizedException throws this exception if the user has an invalid authorization header.
	 */
	@SuppressWarnings("Duplicates")
	public String deleteUser(User user) throws IOException, UnauthorizedException {
		OkHttpClient httpClient = new OkHttpClient();

		Request request = new Request.Builder()
				.url(serverUrl + "/api/users/" + user.getUsername())
				.delete()
				.addHeader("Authorization", "Bearer " + user.getJWT())
				.build();

		Response response = httpClient.newCall(request).execute();

		//TODO modify this to actual code
		/*if (response.code() == 500) {

		}*/

		if (response.code() == 401) {
			throw new UnauthorizedException("Invalid authorization header!");
		}

		String result;
		if (response.body() != null) {
			result = response.body().string();
		} else {
			throw new NullPointerException("Response body came out null! Try again!");
		}
		response.close();

		return result;
	}

	/**
	 * This method is used to register a user to the server.
	 *
	 * <p>
	 * It calls the server on this endpoint:
	 * POST api/users/new
	 *
	 * @param username the username of the user.
	 * @param password the password of the user.
	 * @param first_name the first name of the user.
	 * @param last_name the last name of the user.
	 * @param email the email of the user.
	 * @return returns a JWT if everything went right.
	 * @throws IOException throws this exception if something went wrong with the http call.
	 * @throws ForbiddenException throws this exception if the username already exists in the database.
	 */
	@SuppressWarnings("Duplicates")
	public String registerUser(String username, String password, String first_name, String last_name, String email)
			throws IOException, ForbiddenException {
		OkHttpClient httpClient = new OkHttpClient();

		MediaType JSON = MediaType.parse("application/json; charset=utf-8");
		RequestBody body = RequestBody.create(JSON, "{\n" +
				"\t\"username\":\"" + username + "\",\n" +
				"\t\"password\":\"" + password + "\",\n" +
				"\t\"first_name\":" + (first_name.equals("") ? "null" : "\"" + first_name + "\"") + ",\n" +
				"\t\"last_name\":" + (last_name.equals("") ? "null" : "\"" + last_name + "\"") + ",\n" +
				"\t\"email\":" + (email.equals("") ? "null" : "\"" + email + "\"") + "\n" +
				"}");

		Request request = new Request.Builder()
				.url(serverUrl + "/api/users/new")
				.post(body)
				.build();

		Response response = httpClient.newCall(request).execute();

		if (response.code() == 403) {
			/*
			Username/email already exists in the database.
			 */
			throw new ForbiddenException("Username or email already exists in the database!");
		}

		String result;
		if (response.body() != null) {
			result = response.body().string();
		} else {
			throw new NullPointerException("Response body came out null! Try again!");
		}
		response.close();

		return result;
	}

	/**
	 * This method is used to edit information about a user.
	 *
	 * <p>
	 * It calls the method on this endpoint:
	 * PATCH /api/users/{username}
	 *
	 * @param username the changed username of the user.
	 * @param password the changed password of the user.
	 * @param first_name the changed first name of the user.
	 * @param last_name the changed last name of the user.
	 * @param email the changed email of the user.
	 * @param JWT the JWT needed for the authorization schema (a logged in user can only change his account).
	 * @return returns OK on success.
	 * @throws IOException throws this exception if something went wrong with the http call.
	 * @throws IllegalStateException throws this exception if the username doesn't exist in the database.
	 * @throws UnauthorizedException throws this exception if the authorization schema is wrong.
	 * @throws BadRequestException throws this exception if the request wasn't understood by the server.
	 */
	@SuppressWarnings("Duplicates")
	public String editUser(String username, String password, String first_name, String last_name, String email, String JWT)
			throws IOException, IllegalStateException, UnauthorizedException, BadRequestException {
		OkHttpClient httpClient = new OkHttpClient();

		MediaType JSON = MediaType.parse("application/json; charset=utf-8");
		RequestBody body = RequestBody.create(JSON, "{\n" +
				"\t\"username\":\"" + username + "\",\n" +
				"\t\"password\":\"" + password + "\",\n" +
				"\t\"first_name\":" + (first_name.equals("") ? "null" : "\"" + first_name + "\"") + ",\n" +
				"\t\"last_name\":" + (last_name.equals("") ? "null" : "\"" + last_name + "\"") + ",\n" +
				"\t\"email\":" + (email.equals("") ? "null" : "\"" + email + "\"") + "\n" +
				"}");

		Request request = new Request.Builder()
				.url(serverUrl + "/api/users/" + username)
				.patch(body)
				.addHeader("Authorization", "Bearer " + JWT)
				.build();

		Response response = httpClient.newCall(request).execute();

		//TODO modify this to actual code
		if (response.code() == 500) {
			throw new IllegalStateException("Username doesn't exist in the database!");
		}

		if (response.code() == 401) {
			throw new UnauthorizedException("Wrong authorization schema!");
		}

		if (response.code() == 400) {
			throw new BadRequestException("Bad request!");
		}

		String result;
		if (response.body() != null) {
			result = response.body().string();
		} else {
			throw new NullPointerException("Response body came out null! Try again!");
		}
		response.close();

		return result;
	}
}
