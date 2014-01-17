package databases.objects;
/**
 * This is the Transaction class. It is an object of transactions BTCx supports.
 * Current properties:
 * Transaction
+ Number of BTC: Double
+ Unique ID: String
+ SellerCurrency: String 
+ CurrentExchangeRate: Double
+ PlacedTxDate: Long
+ StarTxDate:Long
+ FinishTxDate:Long
+ Exchange: String
+ OrderedBy: String
+ Type: String (BUY or SELL or CONDUCTED) 
+ Amount (in $) : Double
 * @author danielmorozoff
 *
 */
public class Transaction extends Object {
	
	public double numberOfBTC;
	public String uniqueId;
	public String sellerCurrency;
	public double currentExchangeRate;
	public long placedTxDate;
	public long startTxDate;
	public long finishTxDate;
	public String exchagedOnId; //Cannot store Exchange Object...
	public String OrderedById; //Cannot store User Object...
	public String typeOfTx;
	public double amount;
}
