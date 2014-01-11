package signup;

import org.apache.commons.validator.EmailValidator;
import org.neo4j.shell.util.json.JSONException;
import org.neo4j.shell.util.json.JSONObject;

public class Verification {
	JSONObject signupUser;
	public Verification(JSONObject obj){
		this.signupUser =obj;
	}
	/**
	 * Test if the signup object passes all tests.
	 * @return
	 * @throws JSONException 
	 */
	public boolean verifySignupObject() throws JSONException{
		boolean basicTest=false, merchantTest=false;
		basicTest = testBasicParams();
		boolean isMerch = signupUser.get("type").equals("merchant");
		if(isMerch) merchantTest=testMerchantObject();
		else if(basicTest) return true;
		if(basicTest && (isMerch && merchantTest)) return true;
		return false;
	}
	/**
	 * Basic input test
	 * @return
	 * @throws JSONException
	 */
	private boolean testBasicParams() throws JSONException{
		EmailValidator eValid = EmailValidator.getInstance();
		 boolean emailValid = eValid.isValid((String) signupUser.get("email"));
		 boolean fNameValid  = ((String)signupUser.get("firstName")).length() >0;
		if(fNameValid && emailValid ) return true;		 
		return false;
	}
	/**
	 * Test parameters for merchant
	 * @return
	 * @throws JSONException
	 */
	private boolean testMerchantObject() throws JSONException{
		boolean lName = ((String)signupUser.get("lastName")).length() >0;
		boolean phone = ((String)signupUser.get("phoneNumber")).length()>3; //what is the minimum phone number length
		boolean address = ((String)signupUser.get("address")).length() >0;
		boolean city = ((String)signupUser.get("city")).length() >0;
		boolean state = ((String)signupUser.get("state")).length() >0;
		//boolean percentChargedByCC = ((String)signupUser.get("percentChargedByCC")).length() >0;
		boolean sTitle = ((String)signupUser.get("storeName")).length() >0;
		boolean sDesc = ((String)signupUser.get("storeDescription")).length() >0;
		//boolean acceptsBTC = (Boolean) signupUser.get("acceptsBTC");
		if(lName && phone && address && city && state && sTitle && sDesc) return true;
		return false;
	}
}
