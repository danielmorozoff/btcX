package controllers;

import java.util.Calendar;

import merchant.stripe.StripePayment;

import org.neo4j.shell.util.json.JSONObject;

import com.stripe.model.Card;

import play.cache.Cache;
import play.mvc.Before;
import play.mvc.Controller;

public class PaymentController extends Controller {
	@Before
		public static void authentify(){
			//Test if user has previously logged in
			if(Cache.get(session.getAuthenticityToken())!=null){
				System.out.println("Logging in: "+session.getAuthenticityToken());
				//User has already logged in
				Cache.set(session.getAuthenticityToken(), Cache.get(session.getAuthenticityToken()));
				//Render index page
				MainSystemController.renderIndexPage();
			}
	}
	/**
	 * Entry point to charge a CC.
	 * @param cardNumber
	 * @param exp_month
	 * @param exp_year
	 * @param amount
	 * @param currency
	 */
	public static void chargeCard(String cardNumber, int exp_month, int exp_year, double amount, String currency){
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
	   
		if(cardNumber!=null && currency !=null && exp_month<13 && exp_month>0 && exp_year >= currentYear){
			new StripePayment().chargeCreditCard(cardNumber, exp_month, exp_year, amount, currency);
		}
	}

	



}
