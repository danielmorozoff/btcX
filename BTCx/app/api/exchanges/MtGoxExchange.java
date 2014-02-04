package api.exchanges;

import java.io.IOException;
import java.lang.reflect.Field;

import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeException;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.ExchangeSpecification;
import com.xeiam.xchange.NotAvailableFromExchangeException;
import com.xeiam.xchange.NotYetImplementedForExchangeException;
import com.xeiam.xchange.btce.BTCEExchange;
import com.xeiam.xchange.currency.Currencies;
import com.xeiam.xchange.dto.marketdata.OrderBook;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.dto.marketdata.Trades;
import com.xeiam.xchange.service.polling.PollingMarketDataService;

import api.exchange.interfaces.BtceInterface;
import api.exchange.interfaces.MtGoxInterface;

public class MtGoxExchange implements MtGoxInterface {
	private Exchange MTGOXExchangeInstance;
	private PollingMarketDataService marketDataService;
	
	public MtGoxExchange(Integer API_NUMBER) throws IllegalArgumentException, IllegalAccessException{
		ExchangeSpecification exSpec = new ExchangeSpecification(MtGoxExchange.class.getName());
		exSpec.setSslUri(API_URL);
		exSpec.setPlainTextUriStreaming(WEB_SOCKET_URI);
	    exSpec.setSslUriStreaming(SSL_WEB_SOCKET_URI);
		
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
	    this.MTGOXExchangeInstance =ExchangeFactory.INSTANCE.createExchange(exSpec);
	    this.marketDataService = this.MTGOXExchangeInstance.getPollingMarketDataService();
	}
	/**
	 * Polls for the ticker. Currencies object holds all potential currency implementations
	 * @param cur1
	 * @param cur2
	 * @return
	 * @throws ExchangeException
	 * @throws NotAvailableFromExchangeException
	 * @throws NotYetImplementedForExchangeException
	 * @throws IOException
	 */
	public Ticker getTickerInfo(String cur1, String cur2) throws ExchangeException, NotAvailableFromExchangeException, NotYetImplementedForExchangeException, IOException{
		return marketDataService.getTicker(cur1,cur2);

	}
	/**
	 * Retrieves full/ partial orderbook order book 
	 * @param cur1
	 * @param cur2
	 * @return
	 * @throws ExchangeException
	 * @throws NotAvailableFromExchangeException
	 * @throws NotYetImplementedForExchangeException
	 * @throws IOException
	 */
	public OrderBook getOrderBook(String cur1, String cur2, String orderBookType) throws ExchangeException, NotAvailableFromExchangeException, NotYetImplementedForExchangeException, IOException{
		if(orderBookType.equals("full"))return marketDataService.getFullOrderBook(cur1,cur2);
		else if(orderBookType.equals("part")) return marketDataService.getPartialOrderBook(cur1,cur2);
		return null;
	}
	/**
	 * Get the trades since a point in time based on Long. If long ==0 return all trades.
	 * @param cur1
	 * @param cur2
	 * @param timeSince
	 * @return
	 * @throws ExchangeException
	 * @throws NotAvailableFromExchangeException
	 * @throws NotYetImplementedForExchangeException
	 * @throws IOException
	 */
	public Trades getTradesSince(String cur1, String cur2, long timeSince) throws ExchangeException, NotAvailableFromExchangeException, NotYetImplementedForExchangeException, IOException{
	    if(timeSince==0) return marketDataService.getTrades(cur1,cur2);
		return marketDataService.getTrades(cur1,cur2,timeSince);
	}
	
}
