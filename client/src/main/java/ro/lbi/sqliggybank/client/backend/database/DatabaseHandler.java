package ro.lbi.sqliggybank.client.backend.database;

import okhttp3.*;
import ro.lbi.sqliggybank.client.backend.account.Account;
import ro.lbi.sqliggybank.client.backend.user.User;

import java.io.IOException;

public class DatabaseHandler {

	private String serverUrl = "http://localhost:8080";

	public String loginUser(Account account) throws IOException, IllegalAccessException {
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
			/*
			Username doesn't exist in the database.
			 */
			throw new IllegalAccessException("Incorrect username or password!");
		}
		if (response.code() == 403) {
			/*
			Wrong username/password combination.
			 */
			throw new IllegalAccessException("Invalid username and password combination!");
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

	public String findAuthenticatedUserInformation(Account account, String JWT) throws IOException, IllegalAccessException {
		OkHttpClient httpClient = new OkHttpClient();

		Request request = new Request.Builder()
				.url(serverUrl + "/api/users/" + account.getUsername())
				.get()
				.addHeader("Authorization", "Bearer " + JWT)
				.build();

		Response response = httpClient.newCall(request).execute();

		if (response.code() == 401) {
			throw new IllegalAccessException("Invalid authorization header!");
		}

		String result;
		if (response.body() != null) {
			result = response.body().string();
		} else {
			throw new NullPointerException("Reponse body came out null! Try again!");
		}
		response.close();

		return result;
	}

	@SuppressWarnings("Duplicates")
	public String removeUser(User user, String JWT) throws IOException, IllegalAccessException {
		OkHttpClient httpClient = new OkHttpClient();

		Request request = new Request.Builder()
				.url(serverUrl + "/api/users/" + user.getUsername())
				.delete()
				.addHeader("Authorization", "Bearer " + JWT)
				.build();

		Response response = httpClient.newCall(request).execute();

		if (response.code() == 401) {
			throw new IllegalAccessException("Invalid authorization header!");
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

	public String registerUser(String username, String password, String first_name, String last_name, String email)
			throws IOException, IllegalStateException {
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

		if (response.code() == 500) {
			/*
			Username/email already exists in the database.
			 */
			throw new IllegalStateException("Username or email already exists in the database!");
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

//this is a typical GET request to get all the users in the database
        /*try {
            OkHttpClient httpClient = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("http://localhost:8080/api/users/")
                    .build();

            Response response = null;

            try {
                response = httpClient.newCall(request).execute();
                //System.out.printf(response.body().string());
            } catch (IOException e) {
                System.out.println("error1");
            }
            String jsonData = response.body().string();
            System.out.println(jsonData);
            JSONArray jsonArray = new JSONArray(response.body().string());

            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject jsonObj = jsonArray.getJSONObject(i);

                System.out.println(jsonObj.getString("username"));

                System.out.println(jsonObj);
            }

            JSONObject jsonObject = new JSONObject(jsonData);
            System.out.println(jsonObject);

            JSONArray jsonArray = Jobject.getJSONArray("users");

            for (int i = 0; i < Jarray.length(); i++) {
                JSONObject object = Jarray.getJSONObject(i);
                System.out.println(object.get("username"));
            }
        }
        catch (Exception e) {
            System.out.println("error2");
            e.printStackTrace();
        }*/

	// this is a typical POST request to create a user
        /*MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, "{\"username\":\"mihai\",\"password\":\"pass\",\"firstName\":null,\"lastName\":null,\"email\":null}");

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://localhost:8080/api/users")
                .post(body)
                //.addHeader("Authorization", "header value") //Notice this request has header if you don't need to send a header just erase this part
                .build();
        try {
            Response response = client.newCall(request).execute();

            // Do something with the response.
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
}
