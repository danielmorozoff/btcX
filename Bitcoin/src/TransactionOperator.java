import java.math.BigInteger;
import java.net.InetAddress;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.BlockChain;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.Peer;
import com.google.bitcoin.core.PeerAddress;
import com.google.bitcoin.core.PeerGroup;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.Utils;
import com.google.bitcoin.core.Wallet;
import com.google.bitcoin.core.Wallet.SendRequest;
import com.google.bitcoin.store.BlockStore;
import com.google.bitcoin.store.MemoryBlockStore;


public class TransactionOperator {

	NetworkParameters networkParams;
	// structure for block chain storage
	BlockStore blockStore;
	// object to store block chain
	BlockChain blockChain;

	Wallet wallet;
	public TransactionOperator(NetworkParameters networkParams,Wallet wallet)
	{
		this.networkParams = networkParams;
		this.wallet = wallet;
		this.blockStore = new MemoryBlockStore(networkParams);

	}
	
	public void sendBTC(BigInteger amount, String recipient) throws Exception
	{
		int keychainSize = wallet.getKeychainSize();
		if(keychainSize >= 1)
		{
			blockChain = new BlockChain(networkParams, wallet, blockStore);
			
			Address recipientAddress = new Address(networkParams, recipient);
			Wallet.SendRequest request = Wallet.SendRequest.to(recipientAddress,amount);
			request.fee = Utils.toNanoCoins(0, 1);
			wallet.completeTx(request);
			Transaction tx = request.tx;
			wallet.commitTx(tx);
		}
		else
		{
			System.out.println("At least one key needed for change.");
		}
	}
}
