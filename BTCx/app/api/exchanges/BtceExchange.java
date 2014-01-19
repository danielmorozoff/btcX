package api.exchanges;

import java.io.IOException;
import java.lang.reflect.Field;

import api.exchange.interfaces.BtceInterface;

import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeException;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.ExchangeSpecification;
import com.xeiam.xchange.NotAvailableFromExchangeException;
import com.xeiam.xchange.NotYetImplementedForExchangeException;

import com.xeiam.xchange.btce.BTCEExchange;
import com.xeiam.xchange.currency.Currencies;
import com.xeiam.xchange.dto.account.AccountInfo;
import com.xeiam.xchange.dto.marketdata.OrderBook;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.service.polling.PollingAccountService;
import com.xeiam.xchange.service.polling.PollingMarketDataService;

/**
 * This is our wrapper class that creates the BTCe instance and uses our API permissions
 * @author danielmorozoff
 *
 */
public class BtceExchange implements BtceInterface {
	private Exchange BTCEExchangeInstance;
	private PollingAccountService accountService;
	private PollingMarketDataService marketDataService;
	
	public BtceExchange(Integer API_NUMBER) throws IllegalArgumentException, IllegalAccessException{
		ExchangeSpecification exSpec = new ExchangeSpecification(BTCEExchange.class.getName());
		exSpec.setSslUri(API_URL);
		
		if(API_NUMBER != null){
			Field [] interfaceFields = BtceInterface.class.getDeclaredFields();
				for(Field curField:interfaceFields){
					if(curField.getName().contains(String.valueOf(API_NUMBER))){
						if(curField.getName().contains("KEY") || curField.getName().contains("SECRET")) exSpec.setApiKey((String)curField.get(BtceInterface.class));
					}
				}
		}
		else{
		 	exSpec.setApiKey(API_1_KEY);
		 	exSpec.setSecretKey(API_1_SECRET);
		   
		}
	    this.BTCEExchangeInstance =ExchangeFactory.INSTANCE.createExchange(exSpec);
	    this.accountService = this.BTCEExchangeInstance.getPollingAccountService();
	}
	
	/**
	 * Retrieve private account info -- for our account on the exchange
	 * @return
	 * @throws ExchangeException
	 * @throws NotAvailableFromExchangeException
	 * @throws NotYetImplementedForExchangeException
	 * @throws IOException
	 */
	public AccountInfo getAccountInfo() throws ExchangeException, NotAvailableFromExchangeException, NotYetImplementedForExchangeException, IOException{
		// Interested in the private account functionality (authentication)
	    return accountService.getAccountInfo();
	}
	/**
	 * Retrieves the ticker information for BTCe
	 * @return
	 * @throws IOException 
	 * @throws NotYetImplementedForExchangeException 
	 * @throws NotAvailableFromExchangeException 
	 * @throws ExchangeException 
	 */
	public Ticker getTickerInfo() throws ExchangeException, NotAvailableFromExchangeException, NotYetImplementedForExchangeException, IOException{
	    // Get the latest ticker data showing BTC to CAD
	    Ticker ticker = marketDataService.getTicker(Currencies.BTC, Currencies.USD);
	    
	    double value = ticker.getLast().getAmount().doubleValue();
	    String currency = ticker.getLast().getCurrencyUnit().toString();
	    //To test return
	    System.out.println("Last: " + currency + "-" + value);
	    System.out.println("Last: " + ticker.getLast().toString());
	    System.out.println("Volume: " + ticker.getVolume().toString());
	    System.out.println("High: " + ticker.getHigh().toString());
	    System.out.println("Low: " + ticker.getLow().toString());
	    System.out.println("tradeable ID: " + ticker.getTradableIdentifier());
	   return ticker;
	}
	/**
	 * Get the current order book for the exchange -- buys sells/ asks etc.
	 * @return
	 * @throws ExchangeException
	 * @throws NotAvailableFromExchangeException
	 * @throws NotYetImplementedForExchangeException
	 * @throws IOException
	 */
	public OrderBook retrievePublicOrderBook() throws ExchangeException, NotAvailableFromExchangeException, NotYetImplementedForExchangeException, IOException{
	    // Interested in the public polling market data feed (no authentication)
	    // Get the latest full order book data for LTC/USD
	    OrderBook orderBook = marketDataService.getFullOrderBook(Currencies.LTC, Currencies.USD);
	    System.out.println(orderBook.toString());
	    System.out.println("size: " + (orderBook.getAsks().size() + orderBook.getBids().size()));

	    // Not Sure what this does need to test
	    orderBook = marketDataService.getPartialOrderBook("BTC",Currencies.BTC);
	    System.out.println(orderBook.toString());
	    System.out.println("size: " + (orderBook.getAsks().size() + orderBook.getBids().size()));

	    // Not Sure what this does need to test
	    orderBook = marketDataService.getPartialOrderBook("BTC", Currencies.USD);
	    System.out.println(orderBook.toString());
	    System.out.println("size: " + (orderBook.getAsks().size() + orderBook.getBids().size()));
	    
	    return orderBook;
	}

}
