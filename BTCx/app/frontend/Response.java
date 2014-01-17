package frontend;

import org.neo4j.shell.util.json.JSONObject;

public class Response {
 
	JSONObject responseObject;
	public static String method;
	public static boolean success;
	public static String message;
	
	public Response()
	{
		this.responseObject = new JSONObject();
		this.message = "Please try again later.";
		this.method = "";
		this.success = false;
	}
	
	public void standard()
	{
		this.message = "Please try again later";
		this.success = false;
	}
	public String toString()
	{
		try
		{
		this.responseObject.put("method",method);
		this.responseObject.put("message",message);
		this.responseObject.put("success", success);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return "{'success':false,'message':'Please try again later'}";
		}
		return this.responseObject.toString();
	}
	
	
}
