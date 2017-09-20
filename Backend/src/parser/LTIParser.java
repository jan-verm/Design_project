package parser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Jan Vermeulen
 */
public class LTIParser {

	/**
	 * Retrieve the LTI key
	 * 
	 * @param JSON string
	 * @return LTI key (String)
	 */
	public static String getKey(String json) throws JSONException {
		JSONObject root = new JSONObject(json);
		return root.getString("key");
	}
	
	/**
	 * Build a json object containing key and secret
	 * 
	 * @param LTI key
	 * @param LTI secret
	 * @return LTI keypair
	 */
	public static JSONObject buildKeyPair(String key, String secret) throws JSONException {
		JSONObject root = new JSONObject();
		root.put("key", key);
		root.put("secret", secret);
		return root;
	}
	
}
