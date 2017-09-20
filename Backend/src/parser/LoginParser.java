package parser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Jan Vermeulen
 */
public class LoginParser {

	/**
	 * Retrieve the username
	 * 
	 * @param JSON string
	 * @return Username
	 */
	public static String getUsername(String json) throws JSONException {
		JSONObject root = new JSONObject(json);
		return root.getString("username");
	}

	/**
	 * Retrieve the password
	 * 
	 * @param JSON string
	 * @return Password
	 */
	public static String getPassword(String json) throws JSONException {
		JSONObject root = new JSONObject(json);
		return root.getString("password");
	}
	
	/**
	 * Build a json object containing user credentials and session id
	 * 
	 * @param Username
	 * @param Id of the user
	 * @param Role of the user
	 * @param Current Id of the session
	 * @return JSON string
	 */
	public static String buildCredentials(String username, int userId, String role, String sessionId) throws JSONException {
		JSONObject root = new JSONObject();
		if(userId != 0) {
			root.put("userId", userId);
		}
		root.put("username", username);
		root.put("role", role);
		root.put("sessionId", sessionId);
		return root.toString();
	}
	
}
