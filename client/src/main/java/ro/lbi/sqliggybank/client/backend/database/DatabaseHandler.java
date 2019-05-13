package ro.lbi.sqliggybank.client.backend.database;

import okhttp3.*;
import ro.lbi.sqliggybank.client.backend.Account;
import ro.lbi.sqliggybank.client.backend.exceptions.BadRequestException;
import ro.lbi.sqliggybank.client.backend.exceptions.ForbiddenException;
import ro.lbi.sqliggybank.client.backend.exceptions.NotFoundException;
import ro.lbi.sqliggybank.client.backend.exceptions.UnauthorizedException;
import ro.lbi.sqliggybank.client.backend.User;

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
	 * @param username the username to look for in the database.
	 * @param JWT the JWT needed for the authorization schema (if the JWT doesn't match the username, only public fields are given).
	 * @return a JSON containing the user information.
	 * @throws IOException throws this exception if something went wrong with the http call.
	 * @throws UnauthorizedException throws this exception if the user has an invalid authorization header.
	 * @throws NotFoundException throws this exception if the user resource wasn't found in the database.
	 */
	public String getUser(String username, String JWT) throws IOException, UnauthorizedException, NotFoundException {
		OkHttpClient httpClient = new OkHttpClient();

		Request request = new Request.Builder()
				.url(serverUrl + "/api/users/" + username)
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
	 * @param oldUsername the old username (needed for the API request).
	 * @param username the changed username of the user.
	 * @param password the changed password of the user.
	 * @param first_name the changed first name of the user.
	 * @param last_name the changed last name of the user.
	 * @param email the changed email of the user.
	 * @param JWT the JWT needed for the authorization schema (a logged in user can only change his account).
	 * @return returns a JSON containing the new JWT token on success.
	 * @throws IOException throws this exception if something went wrong with the http call.
	 * @throws IllegalStateException throws this exception if the username doesn't exist in the database.
	 * @throws ForbiddenException throws this exception if the requested username or password is empty.
	 * @throws UnauthorizedException throws this exception if the authorization schema is wrong.
	 * @throws BadRequestException throws this exception if the request wasn't understood by the server.
	 */
	@SuppressWarnings("Duplicates")
	public String editUser(String oldUsername, String username, String password, String first_name, String last_name, String email, String JWT)
			throws IOException, IllegalStateException, ForbiddenException, UnauthorizedException, BadRequestException {
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
				.url(serverUrl + "/api/users/" + oldUsername)
				.patch(body)
				.addHeader("Authorization", "Bearer " + JWT)
				.build();

		Response response = httpClient.newCall(request).execute();

		//TODO modify this to actual code
		if (response.code() == 500) {
			throw new IllegalStateException("Username already exists in the database!");
		}

		if (response.code() == 403) {
			throw new ForbiddenException("You can't have an empty username or password!");
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

	/**
	 * This method is used to get information about a group.
	 *
	 * <p>
	 * The group information is, however, only available to users who are currently in the group.
	 *
	 * @param groupName the group name whose information the user wants.
	 * @param JWT the JWT needed for authorization to view the group.
	 * @return a JSON containing group information.
	 * @throws IOException throws this exception if there was a connection error.
	 * @throws ForbiddenException throws this exception if the user is not authorized to view the group (isn't part of it).
	 */
	public String getGroup(String groupName, String JWT) throws IOException, ForbiddenException {
		OkHttpClient httpClient = new OkHttpClient();

		Request request = new Request.Builder()
				.url(serverUrl + "/api/groups/" + groupName)
				.get()
				.addHeader("Authorization", "Bearer " + JWT)
				.build();

		Response response = httpClient.newCall(request).execute();

		if (response.code() == 403) {
			throw new ForbiddenException("You are not authorized to view this group!");
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
	 * This method is used to generate a group invite link for a group. Only the owner of the group can generate a link.
	 *
	 * @param groupName generate a group invite for this group.
	 * @param JWT the JWT needed for the authorization to generate a link.
	 * @return a JSON containing a new invite link.
	 * @throws IOException throws this exception if there was a connection error.
	 * @throws ForbiddenException throws this exception if the user isn't authorized to perform this action.
	 */
	public String generateGroupInvite(String groupName, String JWT) throws IOException, ForbiddenException {
		OkHttpClient httpClient = new OkHttpClient();

		Request request = new Request.Builder()
				.url(serverUrl + "/api/groups/" + groupName + "/invites/new")
				.get()
				.addHeader("Authorization", "Bearer " + JWT)
				.build();

		Response response = httpClient.newCall(request).execute();

		if (response.code() == 403) {
			throw new ForbiddenException("You are not the owner of the group.");
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
	 * This method is used to get the invite links of a group. They can only be seen by the owner of the group.
	 *
	 * @param groupName the group name whose links' information the user wants.
	 * @param JWT the JWT needed for authorization to view the group links.
	 * @return a JSON containing all the invite links.
	 * @throws IOException throws this exception if there was a connection error.
	 * @throws ForbiddenException throws this exception if the user is not authorized to view the group invites (isn't part of it).
	 */
	public String getGroupInviteList(String groupName, String JWT) throws IOException, ForbiddenException {
		OkHttpClient httpClient = new OkHttpClient();

		Request request = new Request.Builder()
				.url(serverUrl + "/api/groups/" + groupName + "/invites")
				.get()
				.addHeader("Authorization", "Bearer " + JWT)
				.build();

		Response response = httpClient.newCall(request).execute();

		if (response.code() == 403) {
			throw new ForbiddenException("You are not authorized to view the group links!");
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
	 * This method adds a user to a specified group through an invite link.
	 *
	 * @param groupName the group where the user wants to join.
	 * @param inviteID the invite ID of the group provided by the owner of the group.
	 * @param JWT the JWT of the user who wants to join the group.
	 * @return OK on success.
	 * @throws IOException throws this exception if there was a connection error.
	 * @throws ForbiddenException throws this exception if the user is already part of the group.
	 */
	public String joinGroup(String groupName, String inviteID, String JWT) throws IOException, ForbiddenException {
		OkHttpClient httpClient = new OkHttpClient();

		Request request = new Request.Builder()
				.url(serverUrl + "/api/groups/" + groupName + "/invite/" + inviteID)
				.get()
				.addHeader("Authorization", "Bearer " + JWT)
				.build();

		Response response = httpClient.newCall(request).execute();

		if (response.code() == 403) {
			throw new ForbiddenException("You are already part of the group!");
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
	 * This method is used to create a new group.
	 *
	 * @param groupName the new group name.
	 * @param description the new group description.
	 * @param JWT the JWT of the user who wants to create a group.
	 * @return OK on success.
	 * @throws IOException throws this exception if there was a connection error.
	 */
	public String createGroup(String groupName, String description, String JWT) throws IOException  {
		OkHttpClient httpClient = new OkHttpClient();

		MediaType JSON = MediaType.parse("application/json; charset=utf-8");
		RequestBody body = RequestBody.create(JSON, "{\n" +
				"\t\"name\":\"" + groupName + "\",\n" +
				"\t\"description\":\"" + (description.equals("") ? "null" : "\"" + description + "\"") + "\"\n" +
				"}");

		Request request = new Request.Builder()
				.url(serverUrl + "/api/groups/new")
				.post(body)
				.addHeader("Authorization", "Bearer " + JWT)
				.build();

		Response response = httpClient.newCall(request).execute();

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
