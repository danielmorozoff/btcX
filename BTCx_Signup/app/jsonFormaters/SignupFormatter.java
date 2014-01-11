package jsonFormaters;

import org.neo4j.shell.util.json.JSONException;
import org.neo4j.shell.util.json.JSONObject;

import serverLoggers.ServerLoggers;

public class SignupFormatter extends JSONFormatter {
	public boolean login;
	public String message;
	/**
	 * Method loads the response to the singup object
	 * @param login
	 * @param msg
	 * @return
	 */
	public JSONObject loadSignupFormatter(){
		JSONObject rtObj = super.returnedJSONObj;
			try {
				rtObj.put("pass", this.login);
				rtObj.put("message", this.message);
			} catch (JSONException e) {
				e.printStackTrace();
				ServerLoggers.errorLog.error("!Failed signing up user in DB. SignupFormatter.loadSignupFormatter!");
			}
		return rtObj;
	}
	
}
