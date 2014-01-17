import com.google.bitcoin.*;
import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.Block;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.NetworkParameters;


public class BitcoinService {
	
	/**
	 * a NetworkParameters instance which selects the network (production or test) you are on.
	 * a Wallet instance to store your ECKeys and other data.
     * a PeerGroup instance to manage the network connections.
	 * a BlockChain instance which manages the shared, global data structure which makes Bitcoin work.
	 * a BlockStore instance which keeps the block chain data structure somewhere, like on disk.
	 * a WalletEventListener implementations, which receive wallet events
	 * @param args
	 */
	
	//NetworkParameters contains the data needed for working with an instantiation of a BitCoin chain
	static NetworkParameters networkParams;
	
	//address is a textual encoding of a public key
	static Address forwardingAddress;
	static String requestAddress = "1ufAmsv62F1hDNuwKaKraBK3xn3JAJ81c";  
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try
		{
			setNetwork("testnet");
			
			//For Production
			//forwardingAddress = new Address(networkParams,requestAddress);
			
			//For Testnet 
			ECKey localKey = new ECKey();
			Address localAddress = localKey.toAddress(networkParams);
			WalletOperator walletOperator = new WalletOperator(networkParams, "testwallet");
			walletOperator.addKeyToWallet(localKey);
				
			//forwardingAddress = new AddressGenerator(networkParams).createAddress();
			//Get Genesis Block
			//Block genesisBlock = networkParams.genesisBlock;
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
			
	}
	
	/**
	 * The main or "production" network where people buy and sell things
	 * The public test network (testnet) which is reset from time to time and exists for us to play about with new features.
	 *  
	 */
	public static void setNetwork(String network)
	{
		if(network.equalsIgnoreCase("testnet"))
		{
			/** Sets up the given NetworkParameters with testnet values. */
			networkParams = NetworkParameters.testNet();
		}
		else if(network.equalsIgnoreCase("main"))
		{
			/** The primary BitCoin chain created by Satoshi. */
			networkParams = NetworkParameters.prodNet();
		}
	}

}
