package parser;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import courses.Role;
import courses.User;

/**
 * @author Jan Vermeulen
 */
public class UserParser {

	private static final String USER_ID = "userId";
	private static final String INCORRECT_ROLENAME_EXCEPTION = "Incorrect Rolename";
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	private static final String ROLE = "role";
	private static final String SUBSCRIPTIONS = "subscriptions";

	/**
	 * JSON User to User object
	 * 
	 * @param JSON String
	 * @return User object
	 */
	public static User to(String json) throws JSONException {
		JSONObject root = new JSONObject(json);
		String user = root.getString(USERNAME);
		String password = root.getString(PASSWORD);
		Role role = null;
		try {
			role = Role.valueOf(root.getString(ROLE).toUpperCase());
		} catch(IllegalArgumentException e) {
			throw new JSONException(INCORRECT_ROLENAME_EXCEPTION);
		}
		
		User newUser =  new User(user, role, password);		
		return newUser;
	}
	
	/**
	 * User object to JSON User
	 * 
	 * @param User object
	 * @return JSON String
	 */
	public static JSONObject from(User user) throws JSONException {
		JSONObject root = new JSONObject();
		root.put(USER_ID, user.getId());
		root.put(USERNAME, user.getUsername());
		root.put(ROLE, user.getRole().toString());
		root.put(SUBSCRIPTIONS, CourseParser.fromList(user.getSubscriptions()));
		return root;
	}
	
	/**
	 * User object list to JSON User list
	 * 
	 * @param List of User objects
	 * @return JSON String
	 */
	public static JSONArray fromList(List<User> userList) throws JSONException {
		JSONArray root = new JSONArray();
		for(User user : userList) {
			JSONObject listElem = from(user);
			root.put(listElem);
		}
		return root;
	}
	
}
