package api.exchange.interfaces;
/**
 * This is the interface that handles all data for Mt. Gox
 * @author danielmorozoff
 *
 */
public interface MtGoxInterface {

	public static final String API_URL= "https://data.mtgox.com";

	public static final String WEB_SOCKET_URI ="ws://websocket.mtgox.com";
	public static final String SSL_WEB_SOCKET_URI="wss://websocket.mtgox.com";
	
	public static final String API_1_KEY = "7dc62e27-d2ca-406f-906f-887b54f8e54d";
	public static final String API_1_SECRET = "5pfBnCuiCNgnor1T/6yj+NwdWJHXbhAT5f6C/MmmwGH01pWDFpE1SGuk9Toqy2xnr5dlfq7pQczeHill1uyAtg==";
	public static final String API_1_EMAIL = "danielmorozoff@gmail.com";
	
	public static final boolean API_1_PERMISSION_INFO = true;
	public static final boolean API_1_PERMISSION_TRADE = true;
}
