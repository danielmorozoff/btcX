import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.NetworkParameters;


public class AddressGenerator {

	NetworkParameters networkParams;
	/**
	 * Used to generate a public bitcoin Address
	 * @param networkParams
	 */
	public AddressGenerator(NetworkParameters networkParams)
	{
		this.networkParams = networkParams;
	}
	
	public Address createAddress()
	{
		ECKey key = new ECKey();
		// get valid Bitcoin address from public key
		Address addressFromKey = key.toAddress(networkParams);
		System.out.println("Generated address: " + addressFromKey+ "for network: "+networkParams.getId());
		return addressFromKey;
	}
}
