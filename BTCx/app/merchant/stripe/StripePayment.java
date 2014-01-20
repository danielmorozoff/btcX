package merchant.stripe;

import java.util.HashMap;
import java.util.Map;


import server.loggers.ServerLoggers;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;



public class StripePayment implements StripeMerchantInterface {
	private final String secretToUse = API_CUR_SECRET;
	/**
	 * Charge a credit card with proper amount and currency. This accepts the raw inputs
	 * @param cardNumber
	 * @param exp_month
	 * @param exp_year
	 * @param amount
	 * @param currency
	 */
	public boolean chargeCreditCard (String cardNumber, int exp_month, int exp_year,double amount, String currency){
		Stripe.apiKey = secretToUse;
		    Map<String, Object> chargeMap = new HashMap<String, Object>();
		    chargeMap.put("amount", amount);
		    chargeMap.put("currency", currency);
		    Map<String, Object> cardMap = new HashMap<String, Object>();
		    cardMap.put("number", cardNumber);
		    cardMap.put("exp_month", exp_month);
		    cardMap.put("exp_year", exp_year);
		    chargeMap.put("card", cardMap);
		    try {
		        Charge charge = Charge.create(chargeMap);
		        ServerLoggers.infoLog.info("Charging credit card: "+cardNumber+ "for "+amount+" in "+currency);
		        ServerLoggers.infoLog.info("Charg object:  "+charge);
		        return true;
		    } catch (StripeException e) {
		        e.printStackTrace();
		    }
		 return false; 
	}
	
}
