package database.objects;

/**
 * This is the User class. It is an object of users BTCx supports.
 * Current properties:
 * User
+ Email: String
+ Username: String
+ Unique ID: String
+ Firstname: String
+ Lastname: String
+ Country: String
+ Password: String (delete when leaves)
+ LoginDate: Long
+ SignupDate: Long
+ ActiveAccount: boolean
+ emailValidated: boolean
 
 * @author danielmorozoff
 *
 */
public class User {

	public String email;
	public String uniqueId;
	public String userName;
	public String firstName;
	public String lastName;
	public String country;
	public String password;
	public long lastLoginDate;
	public long signupDate;
	public boolean accountActive;
	public boolean emailValidated;
	
	
}
