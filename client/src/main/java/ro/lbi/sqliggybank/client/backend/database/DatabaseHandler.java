package ro.lbi.sqliggybank.client.backend.database;

import okhttp3.*;
import ro.lbi.sqliggybank.client.backend.exceptions.BadRequestException;
import ro.lbi.sqliggybank.client.backend.exceptions.ForbiddenException;
import ro.lbi.sqliggybank.client.backend.exceptions.NotFoundException;

import java.io.IOException;

/**
 * This class handles all the requests from the client and calls the API from the server.
 *
 * <p>
 * The connection between the client and the server is made through this class.
 *
 * @author Alexandru GHERGHESCU (alexghergh)
 * @see <a href="https://documenter.getpostman.com/view/7475341/S1TZyFyC?version=latest" target="_top">Postman online documentation for the API</a>.
 * @since 2018-12-15
 */
public class DatabaseHandler {

	/**
	 * The URL of the server where the API rests.
	 */
	private String serverUrl = "http://localhost:8089";

	/**
	 * The HTTP client to handle server connections.
	 */
	private OkHttpClient httpClient;

	public DatabaseHandler() {
		httpClient = new OkHttpClient();
	}

	/*------------------------- Beginning of Users -------------------------*/

	/**
	 * Login a user.
	 * <p>
	 * Endpoint: POST /api/users/login
	 *
	 * @param username the username of the user
	 * @param password the password of the user
	 * @return a JSON schema containing the credentials of the user
	 * @throws IOException        if there was a connection error
	 * @throws NotFoundException  if the username couldn't be found in the database
	 * @throws ForbiddenException if the password didn't match the password in the database
	 */
	public String loginUser(String username, String password)
			throws IOException, NotFoundException, ForbiddenException {
		MediaType JSON = MediaType.parse("application/json; charset=utf-8");
		RequestBody body = RequestBody.create(JSON, "{\n" +
				"\t\"username\":\"" + username + "\",\n" +
				"\t\"password\":\"" + password + "\"\n" +
				"}");

		Request request = new Request.Builder()
				.url(serverUrl + "/api/users/login")
				.post(body)
				.build();

		Response response = httpClient.newCall(request).execute();

		if (response.code() == 404) {
			throw new NotFoundException("Not found", "No such username!");
		} else if (response.code() == 403) {
			throw new ForbiddenException("Failed to login", "Invalid username and password combination!");
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
	 * Creates a user.
	 * <p>
	 * Endpoint: POST /api/users/new
	 *
	 * @param username   the username of the new user
	 * @param password   the password of the new user
	 * @param first_name the first name of the new user. Can be null.
	 * @param last_name  the last name of the new user. Can be null.
	 * @param email      the email of the new user. Can be null.
	 * @return a JSON schema containing the credentials of the user
	 * @throws IOException        if there was a connection error
	 * @throws ForbiddenException if the username or the email of the new user already appear in the database
	 */
	public String newUser(String username, String password, String first_name, String last_name, String email)
			throws IOException, ForbiddenException {
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
			throw new ForbiddenException("Error", "Username or email already exists! Please choose another username/email!");
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
	 * Gets a user profile.
	 * <p>
	 * Endpoint: GET /api/users/{username}
	 *
	 * @param username the username of the user to be searched for
	 * @param JWT      the authentication token of the logged in user
	 * @return a JSON schema containing the user profile
	 * @throws IOException       if there was a connection error
	 * @throws NotFoundException if there is no such username in the database
	 */
	public String userProfile(String username, String JWT)
			throws IOException, NotFoundException {
		Request request = new Request.Builder()
				.url(serverUrl + "/api/users/" + username)
				.get()
				.addHeader("Authorization", "Bearer " + JWT)
				.build();

		Response response = httpClient.newCall(request).execute();

		if (response.code() == 404) {
			throw new NotFoundException("Not found", "No such username!");
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
	 * Edits information about a user.
	 * <p>
	 * Endpoint: PATCH /api/users/{username}
	 *
	 * @param username       the username of the user
	 * @param JWT            the authentication token of the user
	 * @param new_username   the new username of the user
	 * @param new_password   the new password of the user
	 * @param new_first_name the new first name of the user. Can contain a string with the value of "null". CANNOT be null.
	 * @param new_last_name  the new last name of the user. Can contain a string with the value of "null". CANNOT be null.
	 * @param new_email      the new email of the user. Can contain a string with the value of "null". CANNOT be null.
	 * @return a JSON schema containing the new credentials of the user
	 * @throws IOException         if there was a connection error
	 * @throws ForbiddenException  if the username is already taken
	 * @throws BadRequestException if the username or password are empty
	 */
	public String editUser(String username, String JWT, String new_username, String new_password,
	                       String new_first_name, String new_last_name, String new_email)
			throws IOException, ForbiddenException, BadRequestException {
		MediaType JSON = MediaType.parse("application/json; charset=utf-8");
		RequestBody body = RequestBody.create(JSON, "{\n" +
				"\t\"username\":\"" + new_username + "\",\n" +
				"\t\"password\":\"" + new_password + "\",\n" +
				"\t\"first_name\":" + (new_first_name.equals("") ? "null" : "\"" + new_first_name + "\"") + ",\n" +
				"\t\"last_name\":" + (new_last_name.equals("") ? "null" : "\"" + new_last_name + "\"") + ",\n" +
				"\t\"email\":" + (new_email.equals("") ? "null" : "\"" + new_email + "\"") + "\n" +
				"}");

		Request request = new Request.Builder()
				.url(serverUrl + "/api/users/" + username)
				.patch(body)
				.addHeader("Authorization", "Bearer " + JWT)
				.build();

		Response response = httpClient.newCall(request).execute();

		if (response.code() == 403) {
			throw new ForbiddenException("Username taken", "User already exists! Please choose another username!");
		}

		if (response.code() == 400) {
			throw new BadRequestException("Error", "You cannot have an empty username or password!");
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
	 * Deletes a user.
	 * <p>
	 * Endpoint DELETE /api/users/{username}
	 *
	 * @param username the username of the user
	 * @param JWT      the authentication token of the user
	 * @throws IOException if there was a connection error
	 */
	public void deleteUser(String username, String JWT)
			throws IOException {
		Request request = new Request.Builder()
				.url(serverUrl + "/api/users/" + username)
				.delete()
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
	}

	/*------------------------- Ending of Users -------------------------*/

	/*------------------------- Beginning of Groups -------------------------*/

	/**
	 * Creates a new group.
	 * <p>
	 * Endpoint: POST /api/groups/new
	 *
	 * @param name        the name of the group
	 * @param description the description of the group
	 * @param JWT         the authentication token of the user
	 * @throws IOException        if there was a connection error
	 * @throws ForbiddenException if the group name already exists
	 */
	public void newGroup(String name, String description, String JWT)
			throws IOException, ForbiddenException {
		MediaType JSON = MediaType.parse("application/json; charset=utf-8");
		RequestBody body = RequestBody.create(JSON, "{" +
				"\t\"name\":\"" + name + "\",\n" +
				"\t\"description\":" + (description.equals("") ? "null" : "\"" + description + "\"") + "\n" +
				"}");

		Request request = new Request.Builder()
				.url(serverUrl + "/api/groups/new")
				.post(body)
				.addHeader("Authorization", "Bearer " + JWT)
				.build();

		Response response = httpClient.newCall(request).execute();

		if (response.code() == 403) {
			throw new ForbiddenException("Name taken", "A group already exists with that name, please choose another one!");
		}

		String result;
		if (response.body() != null) {
			result = response.body().string();
		} else {
			throw new NullPointerException("Response body came out null! Try again!");
		}
		response.close();
	}

	/**
	 * Gets info about a group. User must be in the group to be able to see information about it.
	 * <p>
	 * Endpoint: GET /api/groups/{groupName}
	 *
	 * @param name the name of the group
	 * @param JWT  the authentication token of the user
	 * @return a JSON schema containing info about the group
	 * @throws IOException if there was a connection error
	 */
	public String groupInfo(String name, String JWT)
			throws IOException {
		Request request = new Request.Builder()
				.url(serverUrl + "/api/groups/" + name)
				.get()
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

	/**
	 * Gets the groups of a user.
	 * <p>
	 * Endpoint: GET /api/lists/groups/{username}
	 *
	 * @param username the username of the user
	 * @param JWT      the authentication token of the user
	 * @return a JSON schema containing all the groups of the user
	 * @throws IOException if there was a connection error
	 */
	public String getGroupsOfUser(String username, String JWT)
			throws IOException {
		Request request = new Request.Builder()
				.url(serverUrl + "/api/lists/groups/" + username)
				.get()
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

	/**
	 * Gets all the members of a group.
	 * <p>
	 * Endpoint: GET /api/lists/members/{groupName}
	 *
	 * @param name the name of the group
	 * @param JWT  the authentication of the user (the user must be logged in and be part of the group)
	 * @return a JSON schema containing all the members of the group
	 * @throws IOException if there was a connection error
	 */
	public String getMembersOfGroup(String name, String JWT)
			throws IOException {
		Request request = new Request.Builder()
				.url(serverUrl + "/api/lists/members/" + name)
				.get()
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

	/**
	 * Generates a group invite (only the owner of the group has such privileges).
	 * <p>
	 * Endpoint: GET /api/groups/{groupName}/invites/new
	 *
	 * @param name the name of the group
	 * @param JWT  the authentication token of the owner
	 * @return a JSON schema containing the ID of the new invite
	 * @throws IOException        if there was a connection error
	 * @throws ForbiddenException if the user is not the owner of the group
	 */
	public String generateGroupInvite(String name, String JWT)
			throws IOException, ForbiddenException {
		Request request = new Request.Builder()
				.url(serverUrl + "/api/groups/" + name + "/invites/new")
				.get()
				.addHeader("Authorization", "Bearer " + JWT)
				.build();

		Response response = httpClient.newCall(request).execute();

		if (response.code() == 403) {
			throw new ForbiddenException("Unauthorized", "You are not the owner of this group!");
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
	 * Gets the invites for a group (only the owner of the group has such privileges).
	 * <p>
	 * Endpoint: GET /api/groups/{groupName}/invites
	 *
	 * @param name the name of the group
	 * @param JWT  the authentication token for the owner
	 * @return a JSON schema containing all the invites for a group
	 * @throws IOException        if there was a connection error
	 * @throws ForbiddenException if the user is not the owner of the group
	 */
	public String getInvitesOfGroup(String name, String JWT)
			throws IOException, ForbiddenException {
		Request request = new Request.Builder()
				.url(serverUrl + "/api/groups/" + name + "/invites")
				.get()
				.addHeader("Authorization", "Bearer " + JWT)
				.build();

		Response response = httpClient.newCall(request).execute();

		if (response.code() == 403) {
			throw new ForbiddenException("Unauthorized", "You are not the owner of this group!");
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
	 * Joins a group.
	 * <p>
	 * Endpoint: GET /api/groups/{groupName}/invite/{inviteID}
	 *
	 * @param name     the name of the group
	 * @param JWT      the authentication token of the user that wants to join the group
	 * @param inviteID the invite ID provided by the owner of the group used by the user to join the group
	 * @throws IOException        if there was a connection error
	 * @throws ForbiddenException if the user is already part of the group
	 */
	public void joinGroup(String name, String JWT, String inviteID)
			throws IOException, ForbiddenException {
		Request request = new Request.Builder()
				.url(serverUrl + "/api/groups/" + name + "/invite/" + inviteID)
				.get()
				.addHeader("Authorization", "Bearer " + JWT)
				.build();

		Response response = httpClient.newCall(request).execute();

		if (response.code() == 403) {
			throw new ForbiddenException("Error", "You are already part of this group!");
		}

		String result;
		if (response.body() != null) {
			result = response.body().string();
		} else {
			throw new NullPointerException("Response body came out null! Try again!");
		}
		response.close();
	}

	/**
	 * Edits information about a group (only the owner of the group has such privileges).
	 * <p>
	 * Endpoint: PATCH /api/groups/{groupName}
	 *
	 * @param name            the name of the group
	 * @param JWT             the authentication token of the user
	 * @param new_name        a new name for the group
	 * @param new_description a new description for the group
	 * @throws IOException         if there was a connection error
	 * @throws ForbiddenException  if the user is not the owner of the group
	 * @throws BadRequestException if the group name already exists
	 */
	public void editGroup(String name, String JWT, String new_name, String new_description)
			throws IOException, ForbiddenException, BadRequestException {
		MediaType JSON = MediaType.parse("application/json; charset=utf-8");
		RequestBody body = RequestBody.create(JSON, "{\n" +
				"\t\"name\":\"" + new_name + "\",\n" +
				"\t\"description\":" + (new_description.equals("") ? "null" : "\"" + new_description + "\"") + "\n" +
				"}");

		Request request = new Request.Builder()
				.url(serverUrl + "/api/groups/" + name)
				.patch(body)
				.addHeader("Authorization", "Bearer " + JWT)
				.build();

		Response response = httpClient.newCall(request).execute();

		if (response.code() == 403) {
			throw new ForbiddenException("Unauthorized", "You are not the owner of the group!");
		}

		if (response.code() == 400) {
			throw new BadRequestException("Name taken", "A group with this name already exists!");
		}

		String result;
		if (response.body() != null) {
			result = response.body().string();
		} else {
			throw new NullPointerException("Response body came out null! Try again!");
		}
		response.close();
	}

	/**
	 * Deletes a group (only the owner of the group has such privileges).
	 * <p>
	 * Endpoint: DELETE /api/users/{groupName}
	 *
	 * @param name the name of the group
	 * @param JWT  the authentication token of the user
	 * @throws IOException        if there was a connection error
	 * @throws ForbiddenException if the user is not authorized to delete the group (is not the owner of the group)
	 */
	public void deleteGroup(String name, String JWT)
			throws IOException, ForbiddenException {
		Request request = new Request.Builder()
				.url(serverUrl + "/api/groups/" + name)
				.delete()
				.addHeader("Authorization", "Bearer " + JWT)
				.build();

		Response response = httpClient.newCall(request).execute();

		if (response.code() == 403) {
			throw new ForbiddenException("Unauthorized", "You are not the owner of the group, so you cannot delete it!");
		}

		String result;
		if (response.body() != null) {
			result = response.body().string();
		} else {
			throw new NullPointerException("Response body came out null! Try again!");
		}
		response.close();
	}

	/*------------------------- Ending of groups -------------------------*/

	/*------------------------- Beginning of banks -------------------------*/

	/**
	 * Creates a new bank (only the owner of the group has such privileges).
	 * <p>
	 * Endpoint: POST /api/banks/{groupName}/new
	 *
	 * @param groupName   the name of the group
	 * @param JWT         the authentication token of the user
	 * @param name        the name of the bank
	 * @param description the description of the bank
	 * @throws IOException        if there was a connection error
	 * @throws ForbiddenException if the user is not the owner of the group
	 */
	public void newBank(String groupName, String JWT, String name, String description)
			throws IOException, ForbiddenException {
		MediaType JSON = MediaType.parse("application/json; charset=utf-8");
		RequestBody requestBody = RequestBody.create(JSON, "{" +
				"\t\"name\":\"" + name + "\",\n" +
				"\t\"description\":" + (description.equals("") ? "null" : "\"" + description + "\"") + "\n" +
				"}");

		Request request = new Request.Builder()
				.url(serverUrl + "/api/banks/" + groupName + "/new")
				.post(requestBody)
				.addHeader("Authorization", "Bearer " + JWT)
				.build();

		Response response = httpClient.newCall(request).execute();

		if (response.code() == 403) {
			throw new ForbiddenException("Unauthorized", "You are not the owner of this group!");
		}

		String result;
		if (response.body() != null) {
			result = response.body().string();
		} else {
			throw new NullPointerException("Response body came out null! Try again!");
		}
		response.close();
	}

	/**
	 * Gets bank information.
	 * <p>
	 * Endpoint: GET /api/banks/{groupName}/{bankName}
	 *
	 * @param groupName the name of the group
	 * @param JWT       the authentication token of the user
	 * @param name      the name of the bank
	 * @return a JSON schema containing information about the bank
	 * @throws IOException if there was a connection error.
	 */
	public String bankInfo(String groupName, String JWT, String name)
			throws IOException {
		Request request = new Request.Builder()
				.url(serverUrl + "/api/banks/" + groupName + "/" + name)
				.get()
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

	/**
	 * Gets all banks of a group.
	 * <p>
	 * Endpoint: GET /api/banks/{groupName}/list
	 *
	 * @param groupName the name of the group
	 * @param JWT       the authentication token of the user
	 * @return a JSON schema containing all the banks of a group
	 * @throws IOException if there was a connection error
	 */
	public String getBanksOfGroup(String groupName, String JWT)
			throws IOException {
		Request request = new Request.Builder()
				.url(serverUrl + "/api/banks/" + groupName + "/list")
				.get()
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

	/**
	 * Edits bank information (only the owner of the group has such privileges).
	 * <p>
	 * Endpoint: PATCH /api/banks/{groupName}/{bankName}
	 *
	 * @param groupName       the name of the group
	 * @param JWT             the authentication token of the user
	 * @param name            the name of the bank
	 * @param new_name        a new name for the bank
	 * @param new_description a new description for the bank
	 * @throws IOException         if there was a connection error
	 * @throws ForbiddenException  if the user is not the owner of the group
	 * @throws BadRequestException if that bank name already exists
	 */
	public void editBank(String groupName, String JWT, String name, String new_name, String new_description)
			throws IOException, ForbiddenException, BadRequestException {
		MediaType JSON = MediaType.parse("application/json; charset=utf-8");
		RequestBody body = RequestBody.create(JSON, "{\n" +
				"\t\"name\":\"" + new_name + "\",\n" +
				"\t\"description\":" + (new_description.equals("") ? "null" : "\"" + new_description + "\"") + "\n" +
				"}");

		Request request = new Request.Builder()
				.url(serverUrl + "/api/banks/" + groupName + "/" + name)
				.patch(body)
				.addHeader("Authorization", "Bearer " + JWT)
				.build();
		Response response = httpClient.newCall(request).execute();

		if (response.code() == 403) {
			throw new ForbiddenException("Unauthorized", "You are not the owner of the group!");
		}

		if (response.code() == 400) {
			throw new BadRequestException("Name taken", "A bank with that name already exists!");
		}

		String result;
		if (response.body() != null) {
			result = response.body().string();
		} else {
			throw new NullPointerException("Response body came out null! Try again!");
		}
		response.close();
	}

	/**
	 * Deletes a bank (only the owner of the group has such privileges).
	 * <p>
	 * Endpoint: DELETE /api/banks/{groupName}/{bankName}
	 *
	 * @param groupName the name of the group
	 * @param JWT       the authentication token of the user
	 * @param name      the name of the bank
	 * @throws IOException        if there was a connection error
	 * @throws ForbiddenException if the user is not the owner of the group
	 */
	public void deleteBank(String groupName, String JWT, String name)
			throws IOException, ForbiddenException {
		Request request = new Request.Builder()
				.url(serverUrl + "/api/banks/" + groupName + "/" + name)
				.delete()
				.addHeader("Authorization", "Bearer " + JWT)
				.build();

		Response response = httpClient.newCall(request).execute();

		if (response.code() == 403) {
			throw new ForbiddenException("Unauthorized", "You are not the owner of the group!");
		}

		String result;
		if (response.body() != null) {
			result = response.body().string();
		} else {
			throw new NullPointerException("Response body came out null! Try again!");
		}
		response.close();
	}

	/*------------------------- Ending of banks -------------------------*/

	/*------------------------- Beginning of goals -------------------------*/

	/**
	 * Adds a goal (only the owner of the group has such privileges).
	 * <p>
	 * Endpoint: POST /api/goals/{groupName}/{bankName}/new
	 *
	 * @param groupName     the name of the group
	 * @param bankName      the name of the bank
	 * @param JWT           the authentication token of the user
	 * @param name          the name of the goal
	 * @param description   the description of the goal
	 * @param target_amount the target amount of money for the goal
	 * @param deadline      the deadline for the goal
	 * @throws IOException         if there was a connection error
	 * @throws ForbiddenException  if the user is not the owner of the group
	 * @throws BadRequestException if the goal name already exists
	 */
	public void addGoal(String groupName, String bankName, String JWT, String name, String description,
	                    int target_amount, String deadline)
			throws IOException, ForbiddenException, BadRequestException {
		MediaType JSON = MediaType.parse("application/json; charset=utf-8");
		RequestBody body = RequestBody.create(JSON, "{\n" +
				"\t\"name\":\"" + name + "\",\n" +
				"\t\"description\":" + (description.equals("") ? "null" : "\"" + description + "\"") + ",\n" +
				"\t\"target_amount\":" + target_amount + ",\n" +
				"\t\"deadline\":\"" + deadline + "\"\n" +
				"}");

		Request request = new Request.Builder()
				.url(serverUrl + "/api/goals/" + groupName + "/" + bankName + "/new")
				.post(body)
				.addHeader("Authorization", "Bearer " + JWT)
				.build();

		Response response = httpClient.newCall(request).execute();

		if (response.code() == 403) {
			throw new ForbiddenException("Unauthorized", "You are not the owner of the group!");
		}

		if (response.code() == 400) {
			throw new BadRequestException("Name taken", "Goal name already exists, please choose another name!");
		}

		String result;
		if (response.body() != null) {
			result = response.body().string();
		} else {
			throw new NullPointerException("Response body came out null! Try again!");
		}
		response.close();
	}

	/**
	 * Gets goal information.
	 * <p>
	 * Endpoint: GET /api/goals/{groupName}/{bankName}/{goalName}
	 *
	 * @param groupName the name of the group
	 * @param bankName  the name of the bank
	 * @param JWT       the authentication token of the user
	 * @param name      the name of the goal
	 * @return a JSON schema containing information about the goal
	 * @throws IOException if there was a connection error
	 */
	public String goalInfo(String groupName, String bankName, String JWT, String name)
			throws IOException {
		Request request = new Request.Builder()
				.url(serverUrl + "/api/goals/" + groupName + "/" + bankName + "/" + name)
				.get()
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

	/**
	 * Gets all goals of a bank.
	 *
	 * @param groupName the name of the group
	 * @param bankName  the name of the bank
	 * @param JWT       the authentication token of the user
	 * @return a JSON schema containing all the goals of a bank
	 * @throws IOException if there was a connection error
	 */
	public String getGoalsOfBank(String groupName, String bankName, String JWT)
			throws IOException {
		Request request = new Request.Builder()
				.url(serverUrl + "/api/goals/" + groupName + "/" + bankName + "/list")
				.get()
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

	/**
	 * Edits goal information (only the owner of the group has such privileges).
	 * <p>
	 * Endpoint: PATCH /api/goals/{groupName}/{bankName}/{goalName}
	 *
	 * @param groupName         the name of the group
	 * @param bankName          the name of the bank
	 * @param JWT               the authentication token of the user
	 * @param name              the name of the goal
	 * @param new_name          a new name for the goal
	 * @param new_description   a new description for the goal
	 * @param new_target_amount a new target amount for the goal
	 * @param new_deadline      a new deadline for the goal
	 * @throws IOException         if there was a connection error
	 * @throws ForbiddenException  if the user is not the owner of the group
	 * @throws BadRequestException if that goal name already exists
	 */
	public void editGoal(String groupName, String bankName, String JWT, String name, String new_name,
	                     String new_description, int new_target_amount, String new_deadline)
			throws IOException, ForbiddenException, BadRequestException {
		MediaType JSON = MediaType.parse("application/json; charset=utf-8");
		RequestBody body = RequestBody.create(JSON, "{\n" +
				"\t\"name\":\"" + new_name + "\",\n" +
				"\t\"description\":" + (new_description.equals("") ? "null" : "\"" + new_description + "\"") + "\",\n" +
				"\t\"target_amount\":\"" + new_target_amount + "\",\n" +
				"\t\"deadline\":\"" + new_deadline + "\"" +
				"}");

		Request request = new Request.Builder()
				.url(serverUrl + "/api/goals/" + groupName + "/" + bankName + "/" + name)
				.patch(body)
				.addHeader("Authorization", "Bearer " + JWT)
				.build();
		Response response = httpClient.newCall(request).execute();

		if (response.code() == 403) {
			throw new ForbiddenException("Unauthorized", "You are not the owner of the group!");
		}

		if (response.code() == 400) {
			throw new BadRequestException("Name taken", "A goal with that name already exists in this bank!");
		}

		String result;
		if (response.body() != null) {
			result = response.body().string();
		} else {
			throw new NullPointerException("Response body came out null! Try again!");
		}
		response.close();
	}

	/**
	 * Deletes a goal (only the owner of the group has such privileges).
	 * <p>
	 * Endpoint: /api/goals/{groupName}/{bankName}/{goalName}
	 *
	 * @param groupName the name of the group
	 * @param bankName  the name of the bank
	 * @param JWT       the authentication token of the user
	 * @param name      the name of the goal
	 * @throws IOException        if there was a connection error
	 * @throws ForbiddenException if the user is not the owner of the group
	 */
	public void deleteGoal(String groupName, String bankName, String JWT, String name)
			throws IOException, ForbiddenException {
		Request request = new Request.Builder()
				.url(serverUrl + "/api/goals/" + groupName + "/" + bankName + "/" + name)
				.delete()
				.addHeader("Authorization", "Bearer " + JWT)
				.build();

		Response response = httpClient.newCall(request).execute();

		if (response.code() == 403) {
			throw new ForbiddenException("Unauthorized", "You are not the owner of the group!");
		}

		String result;
		if (response.body() != null) {
			result = response.body().string();
		} else {
			throw new NullPointerException("Response body came out null! Try again!");
		}
		response.close();
	}

	/*------------------------- Ending of goals -------------------------*/

	/*------------------------- Beginning of transactions -------------------------*/

	//TODO transactions

	/*------------------------- Ending of transactions -------------------------*/

}