package databases.objects;

import java.util.UUID;

import org.mindrot.jbcrypt.BCrypt;

import serverLoggers.ServerLoggers;

import login.UserLoginAndSignup;

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
public class User extends Object {

	public String email;
	public String private_uniqueId;
	public String public_uniqueId;
	public String userName;
	public String firstName;
	public String lastName;
	public String country;
	public String password;
	public String salt;
	public long lastLoginDate;
	public long signupDate;
	public boolean accountActive;
	public String emailVerificationStr;
	public String codeToValidateEmail;
	public boolean emailValidated;
	
	/**
	 * Loads user parameters upon signup on login page. Generates unique ids.
	 * @param userName
	 * @param password
	 * @param email
	 * @param fName
	 * @param lName
	 * @return
	 */
	public User loadUserClassForSignup(String userName, String password, String email,String fName, String lName){
		UserLoginAndSignup uEnter = new UserLoginAndSignup();
		this.private_uniqueId = UUID.randomUUID().toString();
		//Added security since it is public.
		this.public_uniqueId = BCrypt.hashpw(UUID.randomUUID().toString(),BCrypt.gensalt());
		this.userName = userName;
		this.salt= BCrypt.hashpw(UUID.randomUUID().toString(),BCrypt.gensalt());
		try {
			this.password = uEnter.encryptPassword(password, "userCreation", null, salt,"2wayEncryption");
		} catch (Exception e) {
			ServerLoggers.errorLog.error("!!! Failed to create User object. Method User.loadUserClassForSignup !!!");
			e.printStackTrace();
		}
		this.email=email;
		this.emailVerificationStr = UUID.randomUUID().toString();
		this.firstName = fName;
		this.lastName=lName;
		this.country="";
		
		long curTime = System.currentTimeMillis();
		this.signupDate  = curTime;
		this.lastLoginDate = curTime;
		
		return this;
	}
	
}
