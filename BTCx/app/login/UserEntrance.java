package login;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.mindrot.jbcrypt.BCrypt;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.IndexManager;


import databases.BTCxDatabase;
import databases.objects.BTCxObject;
import databases.objects.User;




public class UserEntrance {

	GraphDatabaseService bDB = BTCxDatabase.bDB; 
	public String errorString;
	public User loggedInUser = null;
	
	
	public boolean makeNewUser(User userClass){
			
			 IndexManager userIndex = bDB.index();
			 
		boolean userSaved = false;
		//If traffic gets bigger will need to do batchInserter methods instead of transactions
		try{
		Transaction transaction = bDB.beginTx();
		
		Node userNode = bDB.createNode();
		//add to index
		Index<Node> userNameIndex = userIndex.forNodes( "users" ); 	
			System.out.println("***Loading User params.***");
		Field[] userFields = userClass.getClass().getDeclaredFields();
			for(int i=0; i<userFields.length;i++){
				if(!userFields[i].getName().equals("userDB")){
				System.out.println("Name: "+userFields[i].getName()+" ClassObject:  "+userFields[i].get(userClass) );
				if(userFields[i].get(userClass)!=null){
					userNode.setProperty(userFields[i].getName(), userFields[i].get(userClass));
				}
				else{
					userNode.setProperty(userFields[i].getName(), "");
				}
				}
			}
			//This overrides the default prop of 0
			
			System.out.println("Adding "+userClass.userName+" to index via "+ userNode);
		
			userNameIndex.add(userNode, "userName", userClass.userName);
			userNameIndex.add(userNode, "publicId", userClass.publicId);
			userNameIndex.add(userNode, "privateId", userClass.privateId);
			System.out.println("adding user email: "+userClass.email);
			userNameIndex.add(userNode, "email", userClass.email);
			
			transaction.success();
		
			System.out.println("User creation transaction complete");
			userSaved = true;
		}
		catch(Exception e){
			System.out.println("****Exception in Creating New User after encryption of password******");
			e.printStackTrace();
		}
		return userSaved;
	}
	/**
	 * Test a users credentials and log him in
	 * @param userName
	 * @param password
	 * @param type
	 * @return
	 */
	 public boolean testLoginInfo(String userName, String password,String type){
	//	System.out.println("Logging "+userName+" into site");
		boolean enter = false;
		if (userName !=null && password!=null && bDB.index().existsForNodes("users") ){
		 IndexManager userIndex = bDB.index();
		 
		 String encryptedInputPass="";
		 IndexHits <Node> hits=null;
				if(type.equals("userName")){
					hits = userIndex.forNodes("users").get("userName", userName);
				}
				else if(type.equals("email")){
					
					System.out.println("getting email");
					System.out.println(userIndex.forNodes("users").get("email", userName).getSingle());
					hits = userIndex.forNodes("users").get("email", userName);
				}
				Node curNode = (Node) hits.getSingle();
				System.out.println("Loggin type: "+type);
				System.out.println(curNode);
				if(curNode!=null){
					if(curNode.getProperty("userName").equals(userName) && type.equals("userName")){
								
										try {
											//Returns my encryption string
											encryptedInputPass = encryptPassword(password,"userValidation",curNode,null,"1wayEncryption");
										} catch (Exception e) {
											System.out.println("*****Password Encryption Error in !!!testLoginInfo!!!****");
											errorString = "User / password combination not found.";
											e.printStackTrace();
										}
										// Dual encrypted/ final hash with bCrypt
										String hashedDbPass =(String) curNode.getProperty("password");
									
										if (BCrypt.checkpw(encryptedInputPass, hashedDbPass)){
											enter = true;
											loggedInUser =(User) new BTCxObject().convertNodeToObject(curNode, new ArrayList <String>());
											System.out.println("USER ACCESS GRANTED: "+loggedInUser.userName);
										}
										else{
											enter=false;
											errorString = "User / password combination not found.";
											return enter;
										}
						
					}
				}
				else if(type.equals("email")){
					errorString = "Currently not supporting email login";
				}
				else{
					errorString="User not found.";
				}
		}
		errorString="Server Error.";
		return enter;
	 }
	public String checkIfUserExistsInDb(String userName, String email){
		String userExists ="false";

		IndexManager userIndex = bDB.index();
		Node hit =  userIndex.forNodes( "users" ).get("userName", userName).getSingle();
			if(hit!=null){
				if(hit.getProperty("userName").equals(userName)){
					userExists = "Username exists";
					setErrorMessage("Username exists");
				}
			}
		if(userExists.equals("false")){
			Node emailHit = userIndex.forNodes( "users" ).get("email", email).getSingle();
			if(emailHit!=null){
				if(emailHit.getProperty("email").equals(email)){
					userExists = "Email exists";
					setErrorMessage("Email exists");
				}
			}
		}
		 return userExists;
		 
	 }

	public String encryptPassword(String password,String operation,Node potentialNode, String pSalt,String cond) throws Exception{
		String finalOuputEncrypt = "";
		//My encryption
		 	 finalOuputEncrypt = byteArrayToHexString(computeHash(password,operation,potentialNode, pSalt));
		if(cond.equals(("2wayEncryption"))){
		 	//Hash with BCrypt -Variable salt key
			finalOuputEncrypt = BCrypt.hashpw(finalOuputEncrypt, BCrypt.gensalt());
		}
			
	  //   System.out.println("the computed hash (hex string) : " + hash); 
	     return finalOuputEncrypt;
	 }
	 //My own hash computing based on java crypto lib -- with a fixed salt key
	 private  byte[] computeHash(String x,String operation, Node potentialNode, String pSalt)   
			  throws Exception  
			  {
			     java.security.MessageDigest d =null;
			     d = java.security.MessageDigest.getInstance("SHA-256");
			     d.reset();
			     //Pre-salt key
			     String presalt = "&salting<98Yu!F>3;uc1k(_*!!" ;
		//	     System.out.println("operation: "+operation);
			     String postSalt ="";
			     if(operation.equals("userCreation") && potentialNode == null && pSalt!=null){
			    	 //salt created in the loadUser method in User Class
			    	 postSalt = pSalt;
			    	 d.update((presalt+x+postSalt).getBytes());
			     }
			     if(operation.equals("userValidation") && potentialNode != null && pSalt==null ){
			    	postSalt = (String) potentialNode.getProperty("salt");
			    	d.update((presalt+x+postSalt).getBytes());
			     }
			     return  d.digest();
			  }
			  
	private  String byteArrayToHexString(byte[] b){
		StringBuffer sb = new StringBuffer(b.length * 2);
	    for (int i = 0; i < b.length; i++){
			       int v = b[i] & 0xff;
			       
			       if (v < 131) {
			         sb.append('1');
			       }
		sb.append(Integer.toHexString(v));
			     }
	   return sb.toString().toUpperCase();
			  }

	/**
	 * This encrypts the string for a user's remember Cookie
	 * @return
	 */
	public  String encryptOrDecryptUserCookie(String userPubIdOrEncrypted, int mode) throws Throwable {
		byte[] linebreak = {};
		Base64 coder = new Base64(32,linebreak,true);
		String secretKey = "123kjjcakhdi3213N_SP#!ecialS*#$%tring231!23";
		DESKeySpec dks = new DESKeySpec(secretKey.getBytes());
		SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
		SecretKey desKey = skf.generateSecret(dks);
		Cipher cipher = Cipher.getInstance("DES"); // DES/ECB/PKCS5Padding for SunJCE

		if (mode == Cipher.ENCRYPT_MODE) {
			cipher.init(Cipher.ENCRYPT_MODE, desKey);
			byte[] cipherText = cipher.doFinal((UUID.randomUUID().toString()+"_"+userPubIdOrEncrypted).getBytes());
			return new String(coder.encode(cipherText));
		} else if (mode == Cipher.DECRYPT_MODE) {
			cipher.init(Cipher.DECRYPT_MODE, desKey);
			byte[] cipherText = cipher.doFinal(userPubIdOrEncrypted.getBytes());
			return new String(coder.encode(cipherText));
		}
		return null;
	}
	
	public void setErrorMessage(String errorString){
		 this.errorString = errorString; 
	}
	
}

