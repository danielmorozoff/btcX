import java.io.File;
import java.util.List;

import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.Sha256Hash;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.Wallet;


public class WalletOperator {

	/**
	 * Param ensures that one cannot mix transactions from the production and test networks in the same wallet.
	 * @param networkParams
	 */
	NetworkParameters networkParams;
	Wallet wallet;
	File walletFile;
	
	public WalletOperator(NetworkParameters networkParams, String walletName) throws Exception
	{
			this.networkParams = networkParams;
			walletFile = new File(walletName+".wallet");
			
			if(walletFile.isFile())
			{
				//Wallet exists
				wallet = Wallet.loadFromFile(walletFile);
				System.out.println("Wallet: "+walletName+" exists and is consistent: "+wallet.isConsistent());
			}
			else
			{
				//Wallet does not exist
				walletFile.createNewFile();
				wallet = new Wallet(networkParams);
				System.out.println("Wallet: "+walletName+" created.");
			}
			System.out.println("Wallet state: "+wallet);
			System.out.println("Wallet balance: "+wallet.getBalance());
			
	}
	public List<Transaction> getRecentTransactions(int number, boolean includingDead)
	{
		return wallet.getRecentTransactions(number, includingDead);
	}
	public Transaction getTransaction(Sha256Hash hash)
	{
		return wallet.getTransaction(hash);
	}
	
	public void addKeyToWallet(ECKey key)
	{
		this.wallet.addKey(key);
	}
	public void finish() throws Exception
	{
		this.wallet.saveToFile(this.walletFile);
	}

	
}
