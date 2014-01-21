package merchant;

/**
 * Recovered from the internet this algorithm certifies the valdidity of card
 * @author danielmorozoff
 *
 */
public class LuhnValidator {

	    public boolean validateCard (String cardNumber) {
	    	if (isValidNumber(cardNumber)) {
	            return true;
	        }
	        else {
	           return false;
	        }
	    }

	    private  boolean isValidNumber(String s) {
	        return doLuhn(s, false) % 10 == 0;
	    }

//	    private  String generateDigit(String s) {
//	        int digit = 10 - doLuhn(s, true) % 10;
//	        return "" + digit;
//	    }

	    private  int doLuhn(String s, boolean evenPosition) {
	        int sum = 0;
	        for (int i = s.length() - 1; i >= 0; i--) {
	            int n = Integer.parseInt(s.substring(i, i + 1));
	            if (evenPosition) {
	                n *= 2;
	                if (n > 9) {
	                    n = (n % 10) + 1;
	                }
	            }
	            sum += n;
	            evenPosition = !evenPosition;
	        }

	        return sum;
	    }
}
