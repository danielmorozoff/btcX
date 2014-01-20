package merchant.stripe;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.shell.util.json.JSONException;
import org.neo4j.shell.util.json.JSONObject;
import server.loggers.ServerLoggers;

import com.stripe.Stripe;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.CustomerCollection;
import com.stripe.model.DeletedCustomer;

import databases.BTCxDatabase;

public class StripeCustomer implements StripeMerchantInterface {
	private final String secretToUse = API_CUR_SECRET;
	private static GraphDatabaseService bDB = BTCxDatabase.bDB;
	
	public boolean createNewCustomer(Map cardParams, 
			String cryptRexUserName, String cryptRexPublicId, String email) //to be stored in the Stripe object metadata 
																		throws JSONException{
		try(Transaction tx = bDB.beginTx()){
			Node custNode = BTCxDatabase.USER_INDEX.get("public_uniqueId", cryptRexPublicId).getSingle();
			if(custNode!=null){
				if(custNode.getProperty("stripeId").equals("")){	
					Stripe.apiKey = secretToUse;
					Map customerParams = new HashMap<String, Object>();
					
					customerParams.put("email", email);
					JSONObject crUserObj = new JSONObject();
						crUserObj.put("cryptRexUsername", cryptRexUserName);
						crUserObj.put("cryptRexPublicId",cryptRexPublicId);	
					customerParams.put("metadata", crUserObj);
					
					//Stripe will make this the default card and assign this map to the customer object
					/*
					 * Stripe requires the following options:
					 * 
					 * number - cardNumber
					 * exp_month
					 * exp_year
					 * cvc - cvc security code //this is not required but we will ask for it.
					 * 
					 * -- The following info is optional --
					 * 
					 *  name - cardholder's full name
					 *  address_line1
					 *  address_line2
					 *  address_city
					 *  address_zip
					 *  address_state
					 *  address_country
					 *   
					 *  -------------------------------------
					 */
					customerParams.put("card", cardParams);
					
					try{
						Customer cust =  Customer.create(customerParams);
						if(cust!=null){
							//Store object information in User Class in DB
							String custId = cust.getId();
							custNode.setProperty("stripeId", custId);
							
							//Store in our DB index
							BTCxDatabase.USER_INDEX.add(custNode, "stripe_uniqueId", custId);
						}
					}catch (StripeException e){
						e.printStackTrace();
						ServerLoggers.errorLog.error("*** Could not store new Stripe customer object. StripeCustomer.createNewCustomer***" );
					}
				}
				else{
					//Stripe id assigned in DB.
				}
			}
		tx.success();
		}catch(RuntimeException e){
			e.printStackTrace();
			ServerLoggers.errorLog.error("***Error storing customer object in database. StripeCustomer.createNewCustomer");
		}
		return false;
	}
	/**
	 * Retrieve a stripe customer object with Cryptrex Identifiers. Accepts username,public_uniqueId,private_uniqueId,strip_uniqueId
	 * @param identifier
	 * @param type
	 * @return
	 * @throws APIException 
	 * @throws CardException 
	 * @throws APIConnectionException 
	 * @throws InvalidRequestException 
	 * @throws AuthenticationException 
	 */
	public Customer getStripeCustomerObject(String identifier, String type) throws AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException{
		Node custNode = null;
		String stripeId = null;
		try(Transaction tx = bDB.beginTx()){
			switch(type){
			case "public_uniqueId":
				 custNode = BTCxDatabase.USER_INDEX.get("public_uniqueId",identifier).getSingle();
				break;
			case "private_uniqueId":
				custNode = BTCxDatabase.USER_INDEX.get("private_uniqueId",identifier).getSingle();
				break;
			case "userName":
				custNode = BTCxDatabase.USER_INDEX.get("userName",identifier).getSingle();
				break;
			case "stripe_uniqueId":
				stripeId = identifier;
				break;
			}
			if(custNode!=null || type.equals("stripe_uniqueId")){
				if(!type.equals("stripe_uniqueId")) stripeId = (String)custNode.getProperty("stripe_uniqueId");
				Stripe.apiKey  = secretToUse;
				return Customer.retrieve(stripeId);
			}
			tx.success();
		}
		return null;
	}
	/**
	 * Delete customer based on various identifiers. Permanently deletes a customer. It cannot be undone. 
	 * Also immediately cancels any active subscription on the customer.
	 * @param identifier
	 * @param type
	 * @return
	 * @throws APIException 
	 * @throws CardException 
	 * @throws APIConnectionException 
	 * @throws InvalidRequestException 
	 * @throws AuthenticationException 
	 */
	public boolean deleteCustomer(String identifier, String type) throws AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException{
		Customer cust = getStripeCustomerObject(identifier,type);
		if(cust!=null){
			Stripe.apiKey  = secretToUse;
			DeletedCustomer delCust  = cust.delete();
			return delCust.getDeleted();
		}
		return false;
	}
	/**
	 * Get all Stripe registered customers.
	 * Options types supported by Stripe API
	 * count optional — default is 10
			A limit on the number of objects to be returned. Count can range between 1 and 100 items.
			created optional
			A filter on the list based on the object created field. The value can be a string with an exact UTC timestamp, or it can be a Map with the following options:
			gt optional
			Return values where the created field is after this timestamp.
			gte optional
			Return values where the created field is after or equal to this timestamp.
			lt optional
			Return values where the created field is before this timestamp.
			lte optional
			Return values where the created field is before or equal to this timestamp.
			offset optional — default is 0
			An offset into the list of returned items. The API will return the requested number of items starting at that offset.
	 * @param options
	 * @return
	 * @throws AuthenticationException
	 * @throws InvalidRequestException
	 * @throws APIConnectionException
	 * @throws CardException
	 * @throws APIException
	 */
	public CustomerCollection getAllCustomer(Map options) throws AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException{
		Stripe.apiKey  = secretToUse;
		return Customer.all(options);
	}
	/**
	 * Update a users params
	 * @param identifier
	 * @param type
	 * @param updatedParams
	 * @return
	 * @throws AuthenticationException
	 * @throws InvalidRequestException
	 * @throws APIConnectionException
	 * @throws CardException
	 * @throws APIException
	 */
	public Customer updateCustomer(String identifier, String type,Map updatedParams) throws AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException{
		Customer cu = getStripeCustomerObject(identifier,type);
		Stripe.apiKey  = secretToUse;
		return cu.update(updatedParams);
	}
}
