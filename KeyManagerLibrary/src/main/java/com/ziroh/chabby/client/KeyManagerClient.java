package com.ziroh.chabby.client;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.crypto.NoSuchPaddingException;

import org.apache.commons.lang3.StringUtils;

import com.ziroh.chabby.ACL.AccessControlClient;
import com.ziroh.chabby.ACL.ResourceUserWrapper;
import com.ziroh.chabby.audit.client.AuditClient;
import com.ziroh.chabby.audit.common.AuditMessageType;
import com.ziroh.chabby.audit.common.LogMessage;
import com.ziroh.chabby.audit.common.NewSignUpAuditMessage;
import com.ziroh.chabby.audit.common.ShareKeyAuditMessage;
import com.ziroh.chabby.audit.common.SignInAuditMessage;
import com.ziroh.chabby.audit.common.StoreKeyAuditMessage;
import com.ziroh.chabby.audit.common.UpdateKeyAuditMessage;
import com.ziroh.chabby.common.AsymmetricKey;
import com.ziroh.chabby.common.EncryptResourceKeyGetSymIV;
import com.ziroh.chabby.common.KeyManagerUserCredentials;
import com.ziroh.chabby.common.ResourceKey;
import com.ziroh.chabby.common.SigninResult;
import com.ziroh.chabby.common.UserResetCredentials;
import com.ziroh.chabby.common.keyTypes.Key;
import com.ziroh.chabby.common.keyTypes.RSAKey;
import com.ziroh.chabby.groups.client.GroupClient;
import com.ziroh.chabby.groups.common.Group;
import com.ziroh.chabby.keyGeneration.client.KeyGenerationClient;
import com.ziroh.chabby.keyStore.client.KeyStoreClient;
import com.ziroh.chabby.keyStore.common.KeyRecord;
import com.ziroh.chabby.notification.client.NotificationClient;
import com.ziroh.chabby.notification.common.NewSharedKeyGeneratedNotification;
import com.ziroh.chabby.notification.common.Notification;
import com.ziroh.chabby.operationalResults.Result;
import com.ziroh.chabby.operationalResults.ShareKeyResult;
import com.ziroh.chabby.userKeyPairStorage.UserKeyPair;
import com.ziroh.chabby.userKeyPairStorageClient.UserKeyPairClient;
import com.ziroh.chabby.userManagement.client.UserManagementClient;
import com.ziroh.chabby.userManagement.common.AuthResult;
import com.ziroh.chabby.userManagement.common.UserCredentials;
import com.ziroh.chabby.userManagement.common.UserCryptoUtils;
import com.ziroh.chabby.utils.ByteArrayConcatenator;
import com.ziroh.chabby.utils.DigestSHA256;
import com.ziroh.cryptography.services.AESEncryptor;
import com.ziroh.cryptography.services.RSAEncryptMethod;
import com.ziroh.cryptography.services.SymKeyGenerator;

/**
 * This class represents all the functions related to KeyManager, notifications and ShareKey operations
 */
public class KeyManagerClient implements IKeyManagerOperation, INotifications, IShareKeyOperations
{
	private String CurrentUserId;
	private RSAEncryptMethod rsaMethod;
	private UserCryptoUtils utils;
	//	private String hashUserID;
	private byte[] userKey;
	private AuditClient auditClient;
	String sessionId;

	static final int iCPU = Runtime.getRuntime().availableProcessors();
	static ExecutorService service = Executors.newFixedThreadPool(iCPU);

	/**
	 * User Identifier
	 */
	public String userId;

	/**
	 * Get userID
	 * @return UserID
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * Set userID
	 * @param userId USerID is required
	 */
	void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * The token
	 */
	public String Token;

	/**
	 * Get the token
	 * @return Token
	 */
	public String getToken() {
		return Token;
	}

	/**
	 * Set the token
	 * @param token Token is required
	 */
	void setToken(String token) {
		Token = token;
	}

	/**
	 * The MasterKey
	 */
	public byte[] MasterKey;

	/**
	 * Get MasterKey
	 * @return MasterKey
	 */
	public byte[] getMasterKey() {
		return MasterKey;
	}

	/**
	 * Set masterKey
	 * @param masterKey MasterKey is required
	 */
	void setMasterKey(byte[] masterKey) {
		MasterKey = masterKey;
	}

	/**
	 * The user public private key pairs.
	 */
	public AsymmetricKey UserPublicPrivateKeyPairs;

	/**
	 * Get the user public private key pairs.
	 * @return UserPublicPrivateKeyPairs
	 */
	public AsymmetricKey getUserPublicPrivateKeyPairs() {
		return UserPublicPrivateKeyPairs;
	}

	/**
	 * Set the user public private key pairs.
	 * @param userPublicPrivateKeyPairs UserPublicPrivateKeyPairs is required
	 */
	void setUserPublicPrivateKeyPairs(AsymmetricKey userPublicPrivateKeyPairs) 
	{
		UserPublicPrivateKeyPairs = userPublicPrivateKeyPairs;
	}

	boolean isAudit=false;

	/**
	 * KeyManagerClient constructor
	 * @param doAudit Boolean value is required
	 */
	public KeyManagerClient(boolean doAudit)
	{ 
		utils = new UserCryptoUtils();
		isAudit=doAudit;
	}

	/**
	 * User Sign's up
	 * @param userCredentials LogIn credentials are required
	 * @return Result object
	 * @throws IllegalArgumentException
	 * KeyManager UserCredentials cannot be null or empty
	 */
	public Result SignUp(KeyManagerUserCredentials userCredentials)
	{
		Result userSignUpResult = new Result();
		AuthResult authResult = new AuthResult();

		try
		{
			if(this.checkCredentialsValidity(userCredentials)==true)
			{
				UserCredentials credentials = new UserCredentials();
				credentials.setPassword(userCredentials.Password);
				credentials.setUserName(userCredentials.UserName);
				UserManagementClient userManagementClient = new UserManagementClient();
				authResult = userManagementClient.signupAsync(credentials).get();

				if(authResult.geterror_code()==0)
				{
					sessionId = authResult.getToken();
					CurrentUserId = credentials.UserName;

					if(!StringUtils.isEmpty(sessionId))
					{
						userKey = utils.GenerateUserKey(CurrentUserId, credentials.getPassword());
						UserPublicPrivateKeyPairs = utils.GenerateUserAsymkeys(sessionId, CurrentUserId);
						//											byte[] publicKey = UserPublicPrivateKeyPairs.PublicKey;
						//						
						//											System.out.println(publicKey);
						//											File fout = new File("F:/FIle/EncryptedText.txt");
						//											FileOutputStream fos = new FileOutputStream(fout);
						//											fos.write(publicKey);
						//											fos.close();
						MasterKey = utils.GenerateMasterKey();

						//Encrypt keys before storing
						KeyRecord userKeyRecord = GetEncryptedUserKeyRecordAsync().get();
						UserKeyPair asymKeyPair = GetEncryptedUserKeyPair();
						KeyRecord masterKeyRecord= GetEncryptedMasterKey();

						//Store Keys in Server
						Result storeResult = StorekeysAync(userKeyRecord, asymKeyPair, masterKeyRecord).get();
						if (storeResult.geterror_code()== 0)
						{
							if (isAudit)
							{
								NewSignUpAuditMessage newGenerateKeyAuditMessage = new NewSignUpAuditMessage(CurrentUserId);
								LogMessage logMessage = new LogMessage();
								logMessage.setMessage_Itself("");
								newGenerateKeyAuditMessage.Message = logMessage;
								newGenerateKeyAuditMessage.MessageType = AuditMessageType.SignUp;	  
								userSignUpResult = new AuditClient(sessionId, CurrentUserId).addNewAuditLogAsync(newGenerateKeyAuditMessage).get();

							}
							userSignUpResult=new Result();
							userSignUpResult.seterror_code(0);
						}
						else
						{
							userSignUpResult = new Result();
							userSignUpResult.seterror_code(1);
							userSignUpResult.seterror_message("Keys are failed to store");
						}
					}
				} 
				else 
				{ 
					authResult = new AuthResult();
					authResult.seterror_code(1);
					authResult.seterror_message("Validation failed or User already exist");

				}
			}
			else
			{
				userSignUpResult = new Result();
				userSignUpResult.seterror_code(1);
				userSignUpResult.seterror_message("UserName or Password is empty");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			userSignUpResult = new Result();
			userSignUpResult.seterror_code(4000);
			userSignUpResult.seterror_message("User Signup failed");
		}
		return userSignUpResult;
	}

	/**
	 * User Sign's up
	 * @param credentials LogIn credentials are required
	 * @return Result object
	 * @throws IllegalArgumentException
	 * KeyManager UserCredentials cannot be null or empty
	 */
	public CompletableFuture<Result> SignUpAsync(KeyManagerUserCredentials credentials) throws IllegalArgumentException
	{
		CompletableFuture<Result> future = new CompletableFuture<Result>();
		service.submit(()->
		{
			try
			{
				if(this.checkCredentialsValidity(credentials)==true)
				{
					Result result = SignUp(credentials);
					if (result != null)
					{
						future.complete(result);
					}
					else
					{
						result = new Result();
						result.seterror_code(4000);
						result.seterror_message("User Signup failed");
					}
				}
			} 

			catch(Exception e)
			{
				e.printStackTrace();
				service.shutdown();
			}

		});

		return future;
	}

	private boolean checkCredentialsValidity(KeyManagerUserCredentials credentials)
	{
		boolean checkValidity = false;
		if(credentials==null || StringUtils.isEmpty(credentials.UserName) || StringUtils.isEmpty(credentials.Password))
		{
			throw new IllegalArgumentException("KeyManager UserCredentials cannot be null or empty");
		}
		else
		{
			checkValidity = true;
		}
		return checkValidity;
	}

	private KeyRecord GetEncryptedUserKeyRecord() throws IllegalArgumentException, InterruptedException, ExecutionException, IOException, NoSuchAlgorithmException, InvalidKeySpecException
	{
		//		byte[] adminPublicKey = GetAdminPublicKeyAsync().get();
		KeyRecord userKeyRecord = new KeyRecord();
		//userKeyRecord.setKey(utils.EncryptKeyAsym(userKey, adminPublicKeyOld));
		userKeyRecord.setResourceKey(userKey);
		userKeyRecord.setGeneratedOn(System.currentTimeMillis());
		userKeyRecord.setKeyDescription("User Key");
		userKeyRecord.setUniqueid("user_key_" + CurrentUserId);
		userKeyRecord.setKeyStatus("Active");
		userKeyRecord.setUserId(CurrentUserId);
		userKeyRecord.setResourceId("user key");
		userKeyRecord.setExpiryDate(0);
		return userKeyRecord;
	}

	private CompletableFuture<KeyRecord> GetEncryptedUserKeyRecordAsync()
	{
		CompletableFuture<KeyRecord> future = new CompletableFuture<KeyRecord>();
		service.submit(()->
		{
			try
			{
				KeyRecord result = GetEncryptedUserKeyRecord();
				if (result != null)
				{
					future.complete(result);
				}
			} 
			catch(IllegalArgumentException ae)
			{
				ae.printStackTrace();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				service.shutdown();
			}

		});
		return future;
	}

	private byte[] GetAdminPublicKey() throws IllegalArgumentException, InterruptedException, ExecutionException
	{
		byte[] key = null;
		UserKeyPairClient userKeyPairClient = new UserKeyPairClient(sessionId, CurrentUserId);
		{
			UserKeyPair keyPair = userKeyPairClient.getKeyPairAsync("Admin").get();
			if (keyPair != null)
			{
				key = keyPair.PublicKey;
			}
		}
		return key;
	}

	private CompletableFuture<byte[]> GetAdminPublicKeyAsync() throws IllegalArgumentException, InterruptedException, ExecutionException
	{
		CompletableFuture<byte[]> future = new CompletableFuture<byte[]>();
		service.submit(()->
		{
			try
			{
				byte[] result = GetAdminPublicKey();
				if (result != null)
				{
					future.complete(result);
				}
			} 
			catch(IllegalArgumentException ae)
			{
				ae.printStackTrace();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				service.shutdown();
			}

		});
		return future;
	}

	private UserKeyPair GetEncryptedUserKeyPair() throws InterruptedException, ExecutionException, IOException
	{
		if (UserPublicPrivateKeyPairs == null)
		{
			RetrieveUserAsymKeys(userKey);
		}
		UserKeyPair asymKeyPair = new UserKeyPair();
		asymKeyPair.setPrivateKey(utils.EncryptUserPrivateKey(UserPublicPrivateKeyPairs.getPrivateKey(), userKey));
		asymKeyPair.setPublicKey(UserPublicPrivateKeyPairs.PublicKey);
		asymKeyPair.setUserid(CurrentUserId);
		return asymKeyPair;
	}


	/**
	 * Retrieve User Asymmetric keys
	 * @param userKey UserKey is required
	 * @throws IllegalArgumentException 
	 * User Key pair cannot be null
	 * @throws InterruptedException
	 * User Key pair cannot be null 
	 * @throws ExecutionException
	 * User Key pair cannot be null
	 * @throws IOException
	 * Encrypted Private Key and User Key cannot be null
	 */
	public void RetrieveUserAsymKeys(byte[] userKey) throws IllegalArgumentException, InterruptedException, ExecutionException, IOException
	{
		UserKeyPairClient userKeyPairClient = new UserKeyPairClient(sessionId, CurrentUserId);

		UserKeyPair userKeyPair = userKeyPairClient.getKeyPairAsync(CurrentUserId).get();
		if(userKeyPair == null)
		{
			throw new IllegalArgumentException("User Key pair cannot be null");
		}
		else
		{
			byte[] encryptedPrivateKey = userKeyPair.PrivateKey;
			byte[] privateKey = utils.DecryptUserPrivateKey(encryptedPrivateKey, userKey);
			if(privateKey == null)
			{
				throw new IOException("Encrypted Private Key and User Key cannot be null");
			}
			else
			{
				UserPublicPrivateKeyPairs = new AsymmetricKey();
				UserPublicPrivateKeyPairs.setPrivateKey(privateKey);
				UserPublicPrivateKeyPairs.setPublicKey(userKeyPair.PublicKey);
			}
		}
	}

	private KeyRecord GetEncryptedMasterKey() throws IOException
	{
		KeyRecord masterKeyRecord = new KeyRecord();
		masterKeyRecord.setResourceKey(utils.EncryptKeyAsym(MasterKey, UserPublicPrivateKeyPairs.PublicKey));
		masterKeyRecord.setGeneratedOn(System.currentTimeMillis()); 
		masterKeyRecord.setKeyDescription("Master Key");
		masterKeyRecord.setKeyStatus("Active");
		masterKeyRecord.setUserId(CurrentUserId);
		masterKeyRecord.setResourceId("master_key");
		masterKeyRecord.setUniqueid("master_key_" + CurrentUserId);
		masterKeyRecord.setExpiryDate(0);
		return masterKeyRecord;
	}

	/**
	 * Store key
	 * @param userKeyRecord User key record is required
	 * @param asymKeys Asymmetric keys are required
	 * @param masterKeyRecord MAsterKey record is required
	 * @return Result class
	 */
	public Result Storekeys(KeyRecord userKeyRecord, UserKeyPair asymKeys, KeyRecord masterKeyRecord)
	{
		if(userKeyRecord==null || asymKeys==null || masterKeyRecord==null)
		{
			throw new IllegalArgumentException("UserKeyRecord,AsymKeys and MasterKeyRecord cannot be null");
		}

		Result storeResult = new Result();
		try
		{
			//Store UserKey in KeyStore; UserKey is encrypted by Admin's Public Key
			KeyStoreClient keyStoreClient = new KeyStoreClient(sessionId, userKeyRecord.getUserId());
			{
				storeResult = keyStoreClient.insertNewRecordAsync(userKeyRecord).get();
			}

			//Store User Master Key
			//Master Key is  encrypted by users Asymmetric public Key
			if (storeResult.error_code == 0)
			{
				KeyStoreClient keyStoreClient1 = new KeyStoreClient(sessionId,masterKeyRecord.getUserId());
				{
					storeResult = keyStoreClient1.insertNewRecordAsync(masterKeyRecord).get();
				}

				//Store User  Asymmetric Keys
				//RSA keys are encrypted using UserKey
				if (storeResult.geterror_code() == 0)
				{
					UserKeyPairClient userKeyPairClient = new UserKeyPairClient(sessionId, userKeyRecord.getUserId());
					{
						storeResult = userKeyPairClient.storeKeyPairAsync(asymKeys).get();
					}
				}
				else
				{
					storeResult = new Result();
					storeResult.seterror_code(1);
					storeResult.seterror_message("Asymmetric keys are not stored successfully");
				}
			}
			else
			{
				storeResult = new Result();
				storeResult.seterror_code(1);
				storeResult.seterror_message("Master key is not stored successfully");
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			storeResult = new Result();
			storeResult.seterror_code(4000);
			storeResult.seterror_message("Something went wrong while storing keys");
		}

		return storeResult;
	}

	/**
	 * Store key
	 * @param userKeyRecord User key record is required
	 * @param asymKeys Asymmetric keys are required
	 * @param masterKeyRecord MAsterKey record is required
	 * @return Result class
	 */
	public CompletableFuture<Result> StorekeysAync(KeyRecord userKeyRecord, UserKeyPair asymKeys, KeyRecord masterKeyRecord)
	{
		CompletableFuture<Result> future = new CompletableFuture<Result>();
		service.submit(()->
		{
			try
			{
				if(userKeyRecord==null || asymKeys==null || masterKeyRecord==null)
				{
					throw new IllegalArgumentException("UserKeyRecord,AsymKeys and MasterKeyRecord cannot be null");
				}

				Result result = Storekeys(userKeyRecord, asymKeys, masterKeyRecord);
				if(result.geterror_code()==0 )
				{
					future.complete(result);
				}

			} 
			catch(IllegalArgumentException ae)
			{
				ae.printStackTrace();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				service.shutdown();
			}

		});
		return future;
	}

	/**
	 * User Sign's in
	 * @param userCredentials KeyManagerUser's credentials are required
	 * @return Result object
	 * @throws IllegalArgumentException
	 * KeyManager UserCredentials cannot be null or empty
	 */
	public Result SignIn(KeyManagerUserCredentials userCredentials) 
	{
		Result signinResult = new Result();
		AuthResult result = new AuthResult();
		try
		{
			if(this.checkCredentialsValidity(userCredentials))
			{
				UserCredentials newUserCredentials = new UserCredentials();
				newUserCredentials.setPassword(userCredentials.getPassword());
				newUserCredentials.setUserName(userCredentials.getUserName());
				UserManagementClient userManagementClient = new UserManagementClient();
				result = userManagementClient.signInAsync(newUserCredentials).get();

				if (result.error_code == 0)
				{
					CurrentUserId = userCredentials.UserName;
					sessionId = result.getToken();

					if(!StringUtils.isEmpty(sessionId))
					{
						//Generate user key using Username and password
						byte[] userKey = utils.GenerateUserKey(CurrentUserId, userCredentials.Password);
						RetrieveUserAsymKeys(userKey);
						RetrieveMasterKey();

						if (isAudit)
						{
							SignInAuditMessage newSignInAuditMessage = new SignInAuditMessage(CurrentUserId);
							LogMessage logMessage = new LogMessage();
							logMessage.setMessage_Itself("");
							newSignInAuditMessage.Message = logMessage;
							newSignInAuditMessage.MessageType = AuditMessageType.SignUp;

							signinResult = new AuditClient(CurrentUserId, sessionId).addNewAuditLogAsync(newSignInAuditMessage).get();
						}
						signinResult = new Result();
						signinResult.seterror_code(0);
					}
					else
					{
						throw new IllegalArgumentException("SessionID cannot be null");
					}
				}
				else
				{
					signinResult = new Result();
					signinResult.seterror_code(4001);
					signinResult.seterror_message("Authentication failed");
				}
			}
			else
			{
				signinResult = new Result();
				signinResult.seterror_code(4000);
				signinResult.seterror_message("User Signin failed");
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			signinResult = new SigninResult();
			signinResult.seterror_code(4000);
			signinResult.seterror_message("SignIn failed");
		}
		return signinResult;
	}

	/**
	 * User Sign's in
	 * @param userCredentials KeyManagerUser's credentials are required
	 * @return Result object
	 * @throws IllegalArgumentException
	 * KeyManager UserCredentials cannot be null or empty
	 */
	public CompletableFuture<Result> SignInAsync(KeyManagerUserCredentials userCredentials) 
	{
		CompletableFuture<Result> future = new CompletableFuture<Result>();
		service.submit(()->
		{
			try
			{
				if(this.checkCredentialsValidity(userCredentials))
				{
					Result result = SignIn(userCredentials);
					if(result!=null )
					{
						future.complete(result);
					}
					else
					{
						result = new Result();
						result.seterror_code(4000);
						result.seterror_message("User Signin failed");
					}
				}

			} 
			catch(IllegalArgumentException ae)
			{
				ae.printStackTrace();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				service.shutdown();
			}

		});
		return future;
	}

	/**
	 * Retrieve master key
	 * @throws ExecutionException 
	 * Key records cannot be null
	 * @throws InterruptedException 
	 * Key records cannot be null
	 * @throws IllegalArgumentException 
	 * Key records cannot be null
	 */
	public void RetrieveMasterKey() throws IllegalArgumentException, InterruptedException, ExecutionException
	{
		KeyStoreClient keyStoreClient = new KeyStoreClient(sessionId,CurrentUserId);

		KeyRecord[] keyRecords = keyStoreClient.getKeyRecordByDecriptionAsync(CurrentUserId, "Master Key").get();
		if (keyRecords != null)
			if (keyRecords.length > 0)
			{
				byte[] encryptedMasterKey = keyRecords[0].ResourceKey;
				MasterKey = utils.DecryptKeyAsym(encryptedMasterKey, UserPublicPrivateKeyPairs.PrivateKey);
			}
	}

	/**
	 * Generate User's Key
	 * @param keyType KeyType is required
	 * @return Key object
	 * @throws ExecutionException 
	 * KeyType cannot be null or empty
	 * @throws InterruptedException 
	 * KeyType cannot be null or empty
	 * @throws IllegalArgumentException 
	 * KeyType cannot be null or empty
	 */
	public Key GenerateKey(String keyType) throws IllegalArgumentException, InterruptedException, ExecutionException
	{
		if(keyType==null || StringUtils.isEmpty(keyType))
		{
			throw new IllegalArgumentException("KeyType cannot be null or empty");
		}

		Key key = null;
		KeyGenerationClient keyGenerationClient = new KeyGenerationClient(sessionId, CurrentUserId);
		{
			key = keyGenerationClient.generateKeyAsync(keyType).get();
		}

		NewSignUpAuditMessage newSignUPAuditMessage;
		try 
		{
			newSignUPAuditMessage = new NewSignUpAuditMessage(CurrentUserId);

			LogMessage logMessage = new LogMessage();
			logMessage.setMessage_Itself("");
			newSignUPAuditMessage.Message = logMessage;
			newSignUPAuditMessage.MessageType = AuditMessageType.SignUp;

			new AuditClient(CurrentUserId, sessionId).addNewAuditLogAsync(newSignUPAuditMessage).get();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return key;
	}

	/**
	 * Generate User's Key
	 * @param keyType KeyType is required
	 * @return Key object
	 * @throws IllegalArgumentException
	 * KeyType cannot be null or empty
	 */
	public CompletableFuture<Key> GenerateKeyAsync(String keyType) throws IllegalArgumentException
	{
		CompletableFuture<Key> future = new CompletableFuture<Key>();
		service.submit((Runnable) ()->
		{
			try
			{
				if(keyType==null || StringUtils.isEmpty(keyType))
				{
					throw new IllegalArgumentException("KeyType cannot be null or empty");
				}

				Key key = GenerateKey(keyType);
				if(key != null )
				{
					future.complete(key);
				}

			} 

			catch(Exception e)
			{
				e.printStackTrace();
				service.shutdown();
			}

		});
		return future;
	}

	/**
	 * Stores User's Key
	 * @param keyRecord Keyrecord is required
	 * @return Result object
	 * @throws IllegalArgumentException
	 * KeyRecord cannot be null
	 * @throws ExecutionException 
	 * Insert new record result cannot be null
	 * @throws InterruptedException 
	 * Insert new record result cannot be null
	 */
	public Result StoreKey(KeyRecord keyRecord) throws IllegalArgumentException, InterruptedException, ExecutionException 
	{
		if(keyRecord==null)
		{
			throw new IllegalArgumentException("KeyRecord cannot be null");
		}

		KeyStoreClient keyStoreClient = new KeyStoreClient(sessionId, CurrentUserId);

		byte[] symKey = null;
		byte[] IV = null;

		EncryptResourceKeyGetSymIV encryptResourceKeyGetSymIV = new EncryptResourceKeyGetSymIV();
		UserCryptoUtils utils = new UserCryptoUtils(sessionId, CurrentUserId);
		EncryptResourceKeyGetSymIV getsymKeyIv = utils.getSymKeyIV();

		//Encrypt Resource Key
		byte[] encryptedResourceKey = encryptResourceKeyGetSymIV.EncryptResourceKey(keyRecord.ResourceKey,getsymKeyIv);

		IV = getsymKeyIv.getIV();
		symKey = getsymKeyIv.getSym();
		byte[] plainSymKeyIV = ByteArrayConcatenator.Concat2Arrays(IV, symKey);

		//Encrypt SymKey IV
		byte[] encryptedSymKeyIV = utils.EncryptSymKeyIV(plainSymKeyIV, MasterKey);

		//encrypting symkeyIv and resource key
		keyRecord.SymKeyIV = encryptedSymKeyIV;
		keyRecord.ResourceKey = encryptedResourceKey;

		Result result = keyStoreClient.insertNewRecordAsync(keyRecord).get();
		if (result.geterror_code()== 0)
		{
			if(isAudit)
			{
				StoreKeyAuditMessage storeKeyAuditMessage;
				try 
				{
					storeKeyAuditMessage = new StoreKeyAuditMessage(CurrentUserId);

					LogMessage logMessage = new LogMessage();
					logMessage.setMessage_Itself("");
					storeKeyAuditMessage.Message = logMessage;
					storeKeyAuditMessage.MessageType = AuditMessageType.StoreKey;  
					result = new AuditClient(sessionId, CurrentUserId).addNewAuditLogAsync(storeKeyAuditMessage).get();
				} 
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			result = new Result();
			result.seterror_code(0);
		}
		return result;
	}

	/**
	 * Stores User's Key
	 * @param keyRecord Keyrecord is required
	 * @return Result object
	 * @throws IllegalArgumentException
	 * KeyRecord cannot be null
	 */
	@Override
	public CompletableFuture<Result> StoreKeyAsync(KeyRecord keyRecord) throws IllegalArgumentException
	{
		CompletableFuture<Result> future = new CompletableFuture<Result>();
		service.submit(()->
		{
			try
			{
				if(keyRecord==null)
				{
					throw new IllegalArgumentException("KeyRecord cannot be null");
				}

				Result result = StoreKey(keyRecord);
				if(result.geterror_code()==0)
				{
					future.complete(result);
				}

			} 

			catch(Exception e)
			{
				e.printStackTrace();
				service.shutdown();
			}

		});
		return future;
	}

	/**
	 * Get all expired keys
	 * @param userId UserID is required
	 * @return KeyRecord array
	 * @throws IllegalArgumentException
	 * UserID cannot be null or empty
	 * @throws ExecutionException 
	 * All received expired keys cannot be null
	 * @throws InterruptedException 
	 * All received expired keys cannot be null
	 */
	public KeyRecord[] GetAllExpiredKeys(String userId) throws IllegalArgumentException, InterruptedException, ExecutionException
	{
		if(userId==null || StringUtils.isEmpty(userId))
		{
			throw new IllegalArgumentException("UserID cannot be null or empty");
		}

		KeyStoreClient keyStoreClient = new KeyStoreClient(sessionId, CurrentUserId);
		KeyRecord[] expiredKeys = keyStoreClient.getAllExpiredKeysAsync(userId).get();
		return expiredKeys;
	}

	@Override
	/**
	 * Get all expired keys
	 * @param userId UserID is required
	 * @return KeyRecord array
	 * @throws IllegalArgumentException
	 * UserID cannot be null or empty
	 */
	public CompletableFuture<KeyRecord[]> GetAllExpiredKeysAsync(String userId) throws IllegalArgumentException
	{
		CompletableFuture<KeyRecord[]> future = new CompletableFuture<KeyRecord[]>();
		service.submit(()->
		{
			try
			{
				if(userId==null || StringUtils.isEmpty(userId))
				{
					throw new IllegalArgumentException("UserID cannot be null or empty");
				}

				KeyRecord[] result = GetAllExpiredKeys(userId);
				if(result != null)
				{
					future.complete(result);
				}

			} 

			catch(Exception e)
			{
				e.printStackTrace();
				service.shutdown();
			}

		});
		return future;
	}

	/**
	 * Get all valid keys
	 * @param userId UserID is required
	 * @return KeyRecord array
	 * @throws IllegalArgumentException
	 * UserID cannot be null
	 * @throws ExecutionException 
	 * Received all valid keys cannot be null
	 * @throws InterruptedException 
	 * Received all valid keys cannot be null
	 */
	public KeyRecord[] GetAllValidKeys(String userId) throws IllegalArgumentException, InterruptedException, ExecutionException
	{
		if(userId==null || StringUtils.isEmpty(userId))
		{
			throw new IllegalArgumentException("UserID cannot be null or empty");
		}

		KeyStoreClient keyStoreClient = new KeyStoreClient(sessionId, CurrentUserId);
		KeyRecord[] validKeys =  keyStoreClient.getAllValidKeysAsync(userId).get();
		return validKeys;
	}

	@Override
	/**
	 * Get all valid keys
	 * @param userId UserID is required
	 * @return KeyRecord array
	 * @throws IllegalArgumentException
	 * UserID cannot be null
	 */
	public CompletableFuture<KeyRecord[]> GetAllValidKeysAsync(String userId) throws IllegalArgumentException
	{
		CompletableFuture<KeyRecord[]> future = new CompletableFuture<KeyRecord[]>();
		service.submit(()->
		{
			try
			{
				if(userId==null || StringUtils.isEmpty(userId))
				{
					throw new IllegalArgumentException("UserID cannot be null or empty");
				}

				KeyRecord[] result = GetAllValidKeys(userId);
				if(result != null)
				{
					future.complete(result);
				}

			} 

			catch(Exception e)
			{
				e.printStackTrace();
				service.shutdown();
			}

		});
		return future;
	}

	/**
	 * Get all Key Records
	 * @return Array of KeyRecords
	 * @throws ExecutionException 
	 * Updated key audit message cannot be null
	 * @throws InterruptedException 
	 * Updated key audit message cannot be null
	 */
	public KeyRecord[] GetAllKeyRecord() throws InterruptedException, ExecutionException  
	{
		KeyStoreClient keyStoreClient = new KeyStoreClient(sessionId, CurrentUserId);
		KeyRecord[] records =  keyStoreClient.getAllKeyRecordAsync().get();
		Result result = null;
		if(records.length!=0)
		{
			if(isAudit)
			{
				UpdateKeyAuditMessage updateKeyAuditMessage;
				try 
				{
					updateKeyAuditMessage = new UpdateKeyAuditMessage(CurrentUserId);

					LogMessage logMessage = new LogMessage();
					logMessage.setMessage_Itself("");
					updateKeyAuditMessage.Message = logMessage;
					updateKeyAuditMessage.MessageType = AuditMessageType.SignUp;
					result = new AuditClient(sessionId, CurrentUserId).addNewAuditLogAsync(updateKeyAuditMessage).get();
				}
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
			result = new Result();
			result.seterror_code(0);
		}
		return records;
	}

	@Override
	/**
	 * Get all Key Records
	 * @return Array of KeyRecords
	 */
	public CompletableFuture<KeyRecord[]> GetAllKeyRecordAsync()
	{
		CompletableFuture<KeyRecord[]> future = new CompletableFuture<KeyRecord[]>();
		service.submit(()->
		{
			try
			{
				KeyRecord[] result = GetAllKeyRecord();
				if(result != null)
				{
					future.complete(result);
				}

			} 

			catch(Exception e)
			{
				e.printStackTrace();
				service.shutdown();
			}

		});
		return future;
	}

	/**
	 * Get KeyRecords by description
	 * @param userId UserID is required
	 * @param description Description is required
	 * @return KeyRecord array
	 * @throws IllegalArgumentException
	 * UserID cannot be null or empty
	 * or
	 * Description cannot be null or empty
	 * @throws ExecutionException 
	 * Received all keyRecords by description cannot be null
	 * @throws InterruptedException 
	 * Received all keyRecords by description cannot be null
	 */
	public KeyRecord[] GetKeyRecordByDecription(String userId, String description) throws IllegalArgumentException, InterruptedException, ExecutionException 
	{
		if(userId==null || StringUtils.isEmpty(userId))
		{
			throw new IllegalArgumentException("UserID cannot be null or empty");
		}
		else if(description==null || StringUtils.isEmpty(description))
		{
			throw new IllegalArgumentException("Description cannot be null or empty");
		}

		KeyStoreClient keyStoreClient = new KeyStoreClient(sessionId, CurrentUserId);
		KeyRecord[] records =  keyStoreClient.getKeyRecordByDecriptionAsync(userId, description).get();
		return records;
	}

	@Override
	/**
	 * Get KeyRecords by description
	 * @param userId UserID is required
	 * @param description Description is required
	 * @return KeyRecord array
	 * @throws IllegalArgumentException
	 * UserID cannot be null or empty
	 * or
	 * Description cannot be null or empty
	 */
	public CompletableFuture<KeyRecord[]> GetKeyRecordByDecriptionAsync(String userId, String description)
	{
		CompletableFuture<KeyRecord[]> future = new CompletableFuture<KeyRecord[]>();
		service.submit(()->
		{
			try
			{
				if(userId==null || StringUtils.isEmpty(userId))
				{
					throw new IllegalArgumentException("UserID cannot be null or empty");
				}
				else if(description==null || StringUtils.isEmpty(description))
				{
					throw new IllegalArgumentException("Description cannot be null or empty");
				}

				KeyRecord[] result = GetKeyRecordByDecription(userId, description);
				if(result != null)
				{
					future.complete(result);
				}

			} 
			catch(IllegalArgumentException ae)
			{
				ae.printStackTrace();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				service.shutdown();
			}

		});
		return future;
	}

	/**
	 * Get KeyRecords by UserID
	 * @param userId UserID is required
	 * @return KeyRecord array
	 * @throws IllegalArgumentException
	 * UserID cannot be null or empty
	 * @throws ExecutionException 
	 * Received KeyRecordbyUserId cannot be null
	 * @throws InterruptedException 
	 *  Received KeyRecordbyUserId cannot be null
	 */
	public KeyRecord[] GetKeyRecordbyUserId(String userId) throws IllegalArgumentException, InterruptedException, ExecutionException
	{
		if(userId==null || StringUtils.isEmpty(userId))
		{
			throw new IllegalArgumentException("UserID cannot be null or empty");
		}

		KeyStoreClient keyStoreClient = new KeyStoreClient(sessionId, CurrentUserId);
		KeyRecord[] records =  keyStoreClient.getKeyRecordbyUserIdAsync(userId).get();
		return records;
	}

	@Override
	/**
	 * Get KeyRecords by UserID
	 * @param userId UserID is required
	 * @return KeyRecord array
	 * @throws IllegalArgumentException
	 * UserID cannot be null or empty
	 */
	public CompletableFuture<KeyRecord[]> GetKeyRecordbyUserIdAsync(String userId)
	{
		CompletableFuture<KeyRecord[]> future = new CompletableFuture<KeyRecord[]>();
		service.submit(()->
		{
			try
			{
				if(userId==null || StringUtils.isEmpty(userId))
				{
					throw new IllegalArgumentException("UserID cannot be null or empty");
				}

				KeyRecord[] result = GetKeyRecordbyUserId(userId);
				if(result != null)
				{
					future.complete(result);
				}

			} 
			catch(IllegalArgumentException ae)
			{
				ae.printStackTrace();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				service.shutdown();
			}

		});
		return future;
	}

	/**
	 * Get KeyRecords by UserID Date and Time
	 * @param userId UserID is required
	 * @param StartDate StartDate is required
	 * @param EndDate EndDate is required
	 * @return KeyRecord array
	 * @throws IllegalArgumentException
	 * UserID cannot be null or empty
	 * or
	 * StartDate cannot be null or empty
	 * or
	 * EndDate cannot be null or empty
	 * @throws ExecutionException 
	 * Received KeyRecordByUserIdDateTime cannot be null
	 * @throws InterruptedException 
	 *  Received KeyRecordByUserIdDateTime cannot be null
	 */
	public KeyRecord[] GetKeyRecordByUserIdDateTime(String userId, String StartDate, String EndDate) throws IllegalArgumentException, InterruptedException, ExecutionException
	{
		if(userId==null || StringUtils.isEmpty(userId))
		{
			throw new IllegalArgumentException("UserID cannot be null or empty");
		}
		else if(StartDate==null || StringUtils.isEmpty(StartDate))
		{
			throw new IllegalArgumentException("StartDate cannot be null or empty");
		}
		else if(EndDate==null || StringUtils.isEmpty(EndDate))
		{
			throw new IllegalArgumentException("EndDate cannot be null or empty");
		}

		KeyStoreClient keyStoreClient = new KeyStoreClient(sessionId, CurrentUserId);
		KeyRecord[] records =  keyStoreClient.getKeyRecordByUserIdDateTimeAsync(userId, StartDate, EndDate).get();
		return records;
	}

	@Override
	/**
	 * Get KeyRecords by UserID Date and Time
	 * @param UserId UserID is required
	 * @param StartDate StartDate is required
	 * @param EndDate EndDate is required
	 * @return KeyRecord array
	 * @throws IllegalArgumentException
	 * UserID cannot be null or empty
	 * or
	 * StartDate cannot be null or empty
	 * or
	 * EndDate cannot be null or empty
	 */
	public CompletableFuture<KeyRecord[]> GetKeyRecordByUserIdDateTimeAsync(String UserId, String StartDate, String EndDate) 
	{
		CompletableFuture<KeyRecord[]> future = new CompletableFuture<KeyRecord[]>();
		service.submit(()->
		{
			try
			{
				if(UserId==null || StringUtils.isEmpty(UserId))
				{
					throw new IllegalArgumentException("UserID cannot be null or empty");
				}
				else if(StartDate==null || StringUtils.isEmpty(StartDate))
				{
					throw new IllegalArgumentException("StartDate cannot be null or empty");
				}
				else if(EndDate==null || StringUtils.isEmpty(EndDate))
				{
					throw new IllegalArgumentException("EndDate cannot be null or empty");
				}

				KeyRecord[] result = GetKeyRecordByUserIdDateTime(UserId, StartDate, EndDate);
				if(result != null)
				{
					future.complete(result);
				}

			} 

			catch(Exception e)
			{
				e.printStackTrace();
				service.shutdown();
			}

		});
		return future;
	}

	/**
	 * Get KeyRecords by UserID Date and Time
	 * @param userId UserID is required
	 * @param resourceId ResourceId is required
	 * @return KeyRecord array
	 * @throws IllegalArgumentException
	 * UserID cannot be null or empty
	 * or
	 * ResourceID cannot be null or empty
	 * @throws ExecutionException 
	 * Received KeyRecordsByResource cannot be null
	 * @throws InterruptedException 
	 * Received KeyRecordsByResource cannot be null
	 */
	public KeyRecord[] GetKeyRecordsByResource(String userId, String resourceId) throws IllegalArgumentException, InterruptedException, ExecutionException
	{
		if(userId==null || StringUtils.isEmpty(userId))
		{
			throw new IllegalArgumentException("UserID cannot be null or empty");
		}
		else if(resourceId==null || StringUtils.isEmpty(resourceId))
		{
			throw new IllegalArgumentException("ResourceID cannot be null or empty");
		}

		KeyStoreClient keyStoreClient = new KeyStoreClient(sessionId, CurrentUserId);
		KeyRecord[] records =  keyStoreClient.getKeyRecordsByResourceAsync(userId, resourceId).get();
		return records;
	}

	@Override
	/**
	 * Get KeyRecords by UserID Date and Time
	 * @param UserId UserID is required
	 * @param ResourceId ResourceId is required
	 * @return KeyRecord array
	 * @throws IllegalArgumentException
	 * UserID cannot be null or empty
	 * or
	 * ResourceID cannot be null or empty
	 */
	public CompletableFuture<KeyRecord[]> GetKeyRecordsByResourceAsync(String userId, String resourceId) throws IllegalArgumentException
	{
		CompletableFuture<KeyRecord[]> future = new CompletableFuture<KeyRecord[]>();
		service.submit(()->
		{
			try
			{
				if(userId==null || StringUtils.isEmpty(userId))
				{
					throw new IllegalArgumentException("UserID cannot be null or empty");
				}
				else if(resourceId==null || StringUtils.isEmpty(resourceId))
				{
					throw new IllegalArgumentException("ResourceID cannot be null or empty");
				}

				KeyRecord[] result = GetKeyRecordsByResource(userId, resourceId);
				if(result != null)
				{
					future.complete(result);
				}

			} 

			catch(Exception e)
			{
				e.printStackTrace();
				service.shutdown();
			}

		});
		return future;
	}

	/**
	 * Update key
	 * @param uniqueId UniqueId is required
	 * @param record Record is required
	 * @return Result object
	 * @throws IllegalArgumentException
	 * UserID cannot be null or empty
	 * or
	 * Record cannot be null
	 */
	public Result UpdateKey(String uniqueId, KeyRecord record) throws IllegalArgumentException
	{
		if(uniqueId==null || StringUtils.isEmpty(uniqueId))
		{
			throw new IllegalArgumentException("uniqueId cannot be null or empty");
		}
		else if(record==null )
		{
			throw new IllegalArgumentException("Record cannot be null");
		}

		KeyStoreClient keyStoreClient = new KeyStoreClient(sessionId, CurrentUserId);
		Result result = null;
		try 
		{
			result = keyStoreClient.updateKeyAsync(uniqueId, record).get();
		} 
		catch (InterruptedException | ExecutionException e)
		{
			e.printStackTrace();
		}
		return result;
	}

	@Override
	/**
	 * Update key
	 * @param uniqueId UniqueId is required
	 * @param record Record is required
	 * @return Result object
	 * @throws IllegalArgumentException
	 * UserID cannot be null or empty
	 * or
	 * Record cannot be null
	 */
	public CompletableFuture<Result> UpdateKeyAsync(String uniqueId, KeyRecord record) throws IllegalArgumentException
	{
		CompletableFuture<Result> future = new CompletableFuture<Result>();
		service.submit(()->
		{
			try
			{
				if(uniqueId==null || StringUtils.isEmpty(uniqueId))
				{
					throw new IllegalArgumentException("uniqueId cannot be null or empty");
				}
				else if(record==null )
				{
					throw new IllegalArgumentException("Record cannot be null");
				}

				Result result = UpdateKey(uniqueId, record);
				if(result.geterror_code()==0)
				{
					future.complete(result);
				}

			} 
			catch(IllegalArgumentException ae)
			{
				ae.printStackTrace();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				service.shutdown();
			}

		});
		return future;
	}

	/**
	 * Change Master Key
	 * @return Boolean value
	 * @throws IllegalArgumentException 
	 * Retrieved master key cannot be null
	 */
	public boolean ChangeMasterKey() throws IllegalArgumentException
	{
		if (MasterKey == null)
		{
			throw new IllegalArgumentException("You need to sign in first to change the master key");
		}
		else if(UserPublicPrivateKeyPairs==null)
		{
			throw new IllegalArgumentException("You need to sign in first to change the master key");
		}

		boolean masterKeyChanged = false;
		try 
		{
		if (MasterKey == null)
		{
			RetrieveMasterKey();
		}

		KeyRecord[] keyRecordstoModify = GetAllNonExpiredKeysAsync().get();
		
		
			keyRecordstoModify = DecryptAllKeyRecords(keyRecordstoModify);
		

		byte[] newMasterKey = GetNewMasterKey();
		keyRecordstoModify = ReEncryptAll(keyRecordstoModify, newMasterKey);
		MasterKey = newMasterKey;
		UpdateAll(keyRecordstoModify);
		KeyRecord encryptedMaterKey = GetEncryptedMasterKey();
		UpdateKeyAsync(encryptedMaterKey.Uniqueid, encryptedMaterKey);

		masterKeyChanged = true;
		} 
		catch (NoSuchAlgorithmException | NoSuchPaddingException | IOException e) 
		{
			e.printStackTrace();
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		} 
		catch (ExecutionException e) 
		{
			e.printStackTrace();
		}
		return masterKeyChanged;
	}


	@Override
	/**
	 * Change Master Key
	 * @return Boolean value
	 */
	public CompletableFuture<Boolean> ChangeMasterKeyAsync() 
	{
		CompletableFuture<Boolean> future = new CompletableFuture<Boolean>();
		service.submit(()->
		{
			try
			{
				Boolean result = ChangeMasterKey();
				if(result != null)
				{
					future.complete(result);
				}

			} 
			catch(Exception e)
			{
				e.printStackTrace();
				service.shutdown();
			}

		});
		return future;
	}

	/**
	 * Get all non expired keys
	 * @return Array of KeyRecords
	 * 
	 */
	public KeyRecord[] GetAllNonExpiredKeys() 
	{
		KeyRecord[] allNonExpiredKeys = null;

		KeyStoreClient client = new KeyStoreClient(sessionId, CurrentUserId);
		{
			try 
			{
				allNonExpiredKeys= client.getAllValidKeysAsync(CurrentUserId).get();
			} 
			catch (IllegalArgumentException | InterruptedException | ExecutionException e) 
			{
				e.printStackTrace();
			}
		}

		return allNonExpiredKeys;
	}

	/**
	 * Get all non expired keys
	 * @return Array of KeyRecords
	 */
	public CompletableFuture<KeyRecord[]> GetAllNonExpiredKeysAsync()
	{
		CompletableFuture<KeyRecord[]> future = new CompletableFuture<KeyRecord[]>();
		service.submit(()->
		{
			try
			{
				KeyRecord[] result = GetAllNonExpiredKeys();
				if(result != null)
				{
					future.complete(result);
				}

			} 
			catch(IllegalArgumentException ae)
			{
				ae.printStackTrace();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				service.shutdown();
			}

		});
		return future;
	}

	private KeyRecord[] DecryptAllKeyRecords(KeyRecord[] records) throws NoSuchAlgorithmException, NoSuchPaddingException, IOException
	{
		for (KeyRecord record : records)
		{
			byte[] cipherText = Process(record.ResourceKey);
			@SuppressWarnings("unused")
			byte[] iv; // need to find iv from process
			byte[] plainText = null;

			AESEncryptor decryptor = new AESEncryptor(MasterKey, cipherText);
			{
				plainText = decryptor.DecryptToBytes(record.ResourceKey);
			}
			record.ResourceKey = plainText;

		}

		return records;
	}

	@SuppressWarnings("unused")
	private byte[] Process(byte[] key ) throws IOException
	{
		byte[] iv;
		byte[][] ivWithKey= ByteArrayConcatenator.Split2Arrays(key);
		iv = ivWithKey[0];
		return ivWithKey[1];
	}

	private byte[] GetNewMasterKey()
	{
		return utils.GenerateMasterKey();
	}

	private KeyRecord[] ReEncryptAll(KeyRecord[] records, byte[] MasterKey)
	{
		for (KeyRecord record : records)
		{
			SymKeyGenerator keyGen = new SymKeyGenerator();
			byte[] iv=keyGen.GenerateIV();
			byte[] cipherText = null;
			AESEncryptor decryptor;
			try 
			{
				decryptor = new AESEncryptor(MasterKey, iv);
				cipherText = decryptor.EncryptFromBytes(record.ResourceKey);
			} 
			catch(Exception ex)
			{
				ex.printStackTrace();
			}

			// add here 
			record.ResourceKey= ByteArrayConcatenator.Concat2Arrays(iv, cipherText);     
		}
		return records;
	}

	private void UpdateAll(KeyRecord[] records)
	{
		for (KeyRecord record : records)
		{
			KeyStoreClient client = new KeyStoreClient(sessionId, CurrentUserId);
			client.updateKeyAsync(record.Uniqueid, record);
		}

	}

	/**
	 * Reset Password
	 * @param resetCredentials User's reset credential is required
	 * @return Result object
	 * @throws IllegalArgumentException
	 * User's reset credentials cannot be null or empty
	 */
	public Result ResetPassword(UserResetCredentials resetCredentials)  
	{
		if(resetCredentials==null)
		{
			throw new IllegalArgumentException("User's reset credentials cannot be null or empty");
		}

		Result resetAuthResult = new Result();
		try
		{

			UserManagementClient userManagementClient = new UserManagementClient();
			{
				//Add User to Auth Server
				UserCredentials credentials = new UserCredentials();
				credentials.setUserName(resetCredentials.Username);
				credentials.setPassword(resetCredentials.NewPassword);
				resetAuthResult = userManagementClient.ResetUserPasswordAsync(credentials).get();
			}
			if (resetAuthResult.geterror_code()== 0)
			{
				//Encrypt keys before storing
				KeyRecord userKeyRecord = GetEncryptedUserKeyRecordAsync().get();
				UserKeyPair asymKeyPair = GetEncryptedUserKeyPair();
				resetAuthResult = UpdateKeyPairStoreAsync(asymKeyPair).get();
				resetAuthResult = UpdateUserKeyAsync(userKeyRecord).get();
			}
		}
		catch(IllegalArgumentException arex)
		{
			arex.printStackTrace();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return resetAuthResult;
	}

	@Override
	/**
	 * Reset Password
	 * @param resetCredentials User's reset credential is required
	 * @return Result object
	 * @throws IllegalArgumentException
	 * User's reset credentials cannot be null or empty
	 */
	public CompletableFuture<Result> ResetPasswordAsync(UserResetCredentials resetCredentials) throws IllegalArgumentException
	{
		CompletableFuture<Result> future = new CompletableFuture<Result>();
		service.submit(()->
		{
			try
			{
				if(resetCredentials==null)
				{
					throw new IllegalArgumentException("User's reset credentials cannot be null or empty");
				}

				Result result = ResetPassword(resetCredentials);
				if(result.geterror_code()==0)
				{
					future.complete(result);
				}

			} 

			catch(Exception e)
			{
				e.printStackTrace();
				service.shutdown();
			}

		});
		return future;
	}


	private Result UpdateKeyPairStore(UserKeyPair asymKeyPair) throws InterruptedException, ExecutionException
	{
		Result updateResult = new Result();
		UserKeyPairClient userKeyPairClient = new UserKeyPairClient(userId, sessionId);
		{
			updateResult = userKeyPairClient.updateKeyPairAsync(asymKeyPair).get();
		}
		return updateResult;
	}

	private CompletableFuture<Result> UpdateKeyPairStoreAsync(UserKeyPair asymKeyPair) throws InterruptedException, ExecutionException
	{
		CompletableFuture<Result> future = new CompletableFuture<Result>();
		service.submit(()->
		{
			try
			{
				Result result = UpdateKeyPairStore(asymKeyPair);
				if(result.geterror_code()==0)
				{
					future.complete(result);
				}

			} 
			catch(IllegalArgumentException ae)
			{
				ae.printStackTrace();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				service.shutdown();
			}

		});
		return future;
	}

	private Result UpdateUserKey(KeyRecord userKey) throws IllegalArgumentException, InterruptedException, ExecutionException
	{
		Result updateResult = new Result();
		KeyStoreClient keyStoreClient = new KeyStoreClient(userId, sessionId);
		{
			updateResult = keyStoreClient.updateKeyAsync(userKey.Uniqueid, userKey).get();
		}
		return updateResult;
	}

	private CompletableFuture<Result> UpdateUserKeyAsync(KeyRecord userKey) throws IllegalArgumentException, InterruptedException, ExecutionException
	{
		CompletableFuture<Result> future = new CompletableFuture<Result>();
		service.submit(()->
		{
			try
			{
				Result result = UpdateUserKey(userKey);
				if(result.geterror_code()==0)
				{
					future.complete(result);
				}

			} 
			catch(IllegalArgumentException ae)
			{
				ae.printStackTrace();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				service.shutdown();
			}

		});
		return future;
	}

	/**
	 * Reset Public Private key
	 * @return Result object
	 * @throws IllegalArgumentException
	 * You need to sign in first to change the public private keys
	 * or
	 * You need to sign in first to change the public private keys
	 */
	public Result ResetPublicPrivatekeys() throws IllegalArgumentException
	{
		if (MasterKey == null)
		{
			throw new IllegalArgumentException("You need to sign in first to change the public private keys");
		}
		else if(UserPublicPrivateKeyPairs==null)
		{
			throw new IllegalArgumentException("You need to sign in first to change the public private keys");
		}

		Result resetResult = new Result();

		UserPublicPrivateKeyPairs = utils.GenerateUserAsymkeys(sessionId, CurrentUserId);
		try {
		if (MasterKey == null)
		{
			RetrieveMasterKey();
		}
		KeyRecord masterKeyRecord;
	
			masterKeyRecord = GetEncryptedMasterKey();
		
		UserKeyPair asymKeyPair = GetEncryptedUserKeyPair();
		resetResult = UpdateKeyPairStoreAsync(asymKeyPair).get();
		resetResult = UpdateMasterKeyAsync(masterKeyRecord).get();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		catch (InterruptedException e)
		{
			e.printStackTrace();
		} 
		catch (ExecutionException e)
		{
			e.printStackTrace();
		}
		return resetResult;
	}

	@Override
	/**
	 * Reset Public Private key
	 * @return Result object
	 */
	public CompletableFuture<Result> ResetPublicPrivatekeysAsync()
	{
		CompletableFuture<Result> future = new CompletableFuture<Result>();
		service.submit(()->
		{
			try
			{
				Result result = ResetPublicPrivatekeys();
				if(result.geterror_code()==0)
				{
					future.complete(result);
				}

			} 

			catch(Exception e)
			{
				e.printStackTrace();
				service.shutdown();
			}

		});
		return future;
	}


	private Result UpdateMasterKey(KeyRecord masterKeyRecord) throws IllegalArgumentException, InterruptedException, ExecutionException
	{
		Result updateResult = new Result();
		KeyStoreClient keyStoreClient = new KeyStoreClient(userId, sessionId);
		{
			updateResult = keyStoreClient.updateKeyAsync(masterKeyRecord.Uniqueid, masterKeyRecord).get();
		}
		return updateResult;
	}

	private CompletableFuture<Result> UpdateMasterKeyAsync(KeyRecord masterKeyRecord) throws IllegalArgumentException, InterruptedException, ExecutionException
	{
		CompletableFuture<Result> future = new CompletableFuture<Result>();
		service.submit(()->
		{
			try
			{
				Result result = UpdateMasterKey(masterKeyRecord);
				if(result.geterror_code()==0)
				{
					future.complete(result);
				}

			} 
			catch(IllegalArgumentException ae)
			{
				ae.printStackTrace();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				service.shutdown();
			}

		});
		return future;
	}

	/**
	 * Get all unread notifications
	 * @param UserId UserId is required
	 * @return Notification array
	 * @throws IllegalArgumentException
	 * UserID cannot be null or empty
	 */
	public Notification[] GetAllUnreadNotifications(String UserId) throws IllegalArgumentException
	{
		if(UserId == null || StringUtils.isEmpty(UserId))
		{
			throw new IllegalArgumentException("UserID cannot be null or empty");
		}
		
		Notification[] unReadNotifications = null;
		NotificationClient client = new NotificationClient(sessionId,UserId);
		try 
		{
			unReadNotifications = client.getNotificationUnreadAsync(UserId).get();
		} 
		catch (InterruptedException | ExecutionException e)
		{
			e.printStackTrace();
		}
		return unReadNotifications;
	}

	@Override
	/**
	 * Get all unread notifications
	 * @param UserId UserId is required
	 * @return Notification array
	 * @throws IllegalArgumentException
	 * UserID cannot be null or empty
	 */
	public CompletableFuture<Notification[]> GetAllUnreadNotificationsAsync(String UserId) throws IllegalArgumentException
	{
		CompletableFuture<Notification[]> future = new CompletableFuture<Notification[]>();
		service.submit(()->
		{
			try
			{
				if(UserId == null || StringUtils.isEmpty(UserId))
				{
					throw new IllegalArgumentException("UserID cannot be null or empty");
				}
				Notification[] result = GetAllUnreadNotifications(UserId);
				if(result !=null)
				{
					future.complete(result);
				}

			} 

			catch(Exception e)
			{
				e.printStackTrace();
				service.shutdown();
			}

		});
		return future;
	}

	/**
	 * Get all Key shared notifications
	 * @param UserId UserId is required
	 * @return Notification array
	 * @throws IllegalArgumentException
	 * UserID cannot be null or empty
	 */
	public Notification[] GetAllKeySharedNotifications(String UserId) throws IllegalArgumentException
	{
		Notification[] unReadNotifications = null;
		NotificationClient client = new NotificationClient(sessionId, CurrentUserId);
		try 
		{
			unReadNotifications = client.getNotificationSharesAsync(UserId).get();
		} 
		catch (InterruptedException | ExecutionException e)
		{
			e.printStackTrace();
		}
		return unReadNotifications;
	}

	@Override
	/**
	 * Get all Key shared notifications
	 * @param UserId UserId is required
	 * @return Notification array
	 * @throws IllegalArgumentException
	 * UserID cannot be null or empty
	 */
	public CompletableFuture<Notification[]> GetAllKeySharedNotificationsAsync(String UserId) throws IllegalArgumentException
	{
		CompletableFuture<Notification[]> future = new CompletableFuture<Notification[]>();
		service.submit(()->
		{
			try
			{
				if(UserId == null || StringUtils.isEmpty(UserId))
				{
					throw new IllegalArgumentException("UserID cannot be null or empty");
				}
				Notification[] result = GetAllKeySharedNotifications(UserId);
				if(result !=null)
				{
					future.complete(result);
				}

			} 

			catch(Exception e)
			{
				e.printStackTrace();
				service.shutdown();
			}

		});
		return future;
	}

	@Override
	/**
	 * Update notification
	 * @param notification Notification is required
	 * @return Result object
	 */
	public Result UpdateNotification(Notification notification) 
	{
		//not implemented
		try {
			throw new Exception();
		} catch (Exception e) {

			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Update notification
	 * @param record KeyRecord is required
	 * @param ResourceKey ResourceKey is required
	 * @return ShareKeyResult object
	 * @throws IllegalArgumentException 
	 * Receiver ID cannot be null or empty
	 * or
	 * Resource ID cannot be null or empty
	 * or
	 * Current user id cannot be null or empty
	 */
	public ShareKeyResult ShareResourceKey(KeyRecord record, byte[] ResourceKey) throws IllegalArgumentException
	{
		if (record.UserId ==null || StringUtils.isEmpty(record.UserId))
		{
			throw new IllegalArgumentException("Receiver ID cannot be null or empty");
		}

		else if (record.ResourceId ==null || StringUtils.isEmpty(record.ResourceId))
		{
			throw new IllegalArgumentException("Resource ID cannot be null or empty");
		}

		else if (CurrentUserId ==null || StringUtils.isEmpty(CurrentUserId))
		{
			throw new IllegalArgumentException("Current user id cannot be null or empty");
		}
		else if(ResourceKey==null)
		{
			
		}
		ShareKeyResult shareKeyResult = new ShareKeyResult();
		shareKeyResult.seterror_code(1);

		// share a key of a resource to a user B. 
		// get the public key of user B
		UserKeyPairClient userKeyPairClient = new UserKeyPairClient(sessionId, CurrentUserId);
		UserKeyPair userKeyPair;
		try {
			
			userKeyPair = userKeyPairClient.getKeyPairAsync(record.UserId).get();
		

		if (userKeyPair == null)
		{
			// if there is no user return 
			shareKeyResult = new ShareKeyResult();
			shareKeyResult.seterror_code(1);
			shareKeyResult.seterror_message(record.UserId +"does not exist");

		}
		else
		{

			byte[] publicKeyByte = userKeyPair.PublicKey;

			// store it in the key store 
			record.Uniqueid = utils.getUserIDinHash(record.UserId + record.ResourceId 
					+System.currentTimeMillis());

			byte[] decryptedSymKeyIV = utils.DecryptSymKeyIV(record.SymKeyIV, MasterKey);
			byte[] toShareEncryptedSymKey = utils.EncryptSymKeyUsingPublicKey(decryptedSymKeyIV, publicKeyByte);
			record.SymKeyIV = toShareEncryptedSymKey;
			Result keyStoreUpdateResult = new Result();

			KeyStoreClient keyStoreClient = new KeyStoreClient(sessionId, CurrentUserId);
			keyStoreUpdateResult = keyStoreClient.insertNewRecordAsync(record).get();
			keyStoreUpdateResult = StoreKeyAsync(record).get();

			if (keyStoreUpdateResult.error_code == 0)
			{
				// create a group because there are now more than one user
				Group resourceGroup = DoesResourceExistAsync(record.ResourceId).get();
				Result groupOpResult = null;
				if (resourceGroup != null)
				{
					// update the group already present 
					groupOpResult = UpdateGroupAsync(resourceGroup.ResourceId, record.UserId).get();
				}
				else
				{
					// group does not exist. i create a new group
					groupOpResult = CreateNewGroupAndUpdateAsync(record.ResourceId, record.UserId).get();
				}

				if (groupOpResult.geterror_code() == 0)
				{
					// updated the ACL
					Result updateACLResult = UpdateACLListAsync(record.UserId, record.ResourceId).get();
					if (updateACLResult.geterror_code() == 0)
					{

						// i sent the notification to the user
						Result notificationResult = NotifyAsync(record.UserId, record.ResourceId, record.Uniqueid).get();
						if (notificationResult.geterror_code() == 0)
						{		
							ShareKeyAuditMessage shareKeyAuditMessage = new ShareKeyAuditMessage(CurrentUserId);
							LogMessage logMessage = new LogMessage();
							logMessage.setMessage_Itself("");
							shareKeyAuditMessage.Message = logMessage;
							shareKeyAuditMessage.MessageType = AuditMessageType.ShareKey;

							auditClient.addNewAuditLogAsync(shareKeyAuditMessage).get();

							shareKeyResult.seterror_code(0);
						}
					}
				}
			} 

		}
		} 
		catch (InterruptedException | ExecutionException e) 
		{
			e.printStackTrace();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return shareKeyResult;
	}

	@Override
	/**
	 * Get shared key Notification
	 * @param record record is required
	 * @param ResourceKey ResourceKey is required
	 * @return ShareKeyResult object
	 * @throws IllegalArgumentException
	 * record cannot be null 
	 * or
	 * ResourceKey cannot be null
	 */
	public CompletableFuture<ShareKeyResult> ShareResourceKeyAsync(KeyRecord record, byte[] ResourceKey) throws IllegalArgumentException
	{
		CompletableFuture<ShareKeyResult> future = new CompletableFuture<ShareKeyResult>();
		service.submit(()->
		{
			try
			{
				if(record==null )
				{
					throw new IllegalArgumentException("record cannot be null ");
				}
				else if(ResourceKey==null )
				{
					throw new IllegalArgumentException("ResourceKey cannot be null ");
				}
				ShareKeyResult result = ShareResourceKey(record, ResourceKey);
				if(result.geterror_code()==0)
				{
					future.complete(result);
				}

			} 

			catch(Exception e)
			{
				e.printStackTrace();
				service.shutdown();
			}

		});
		return future;
	}


	Result Notify(String ReceiverUserId, String ResourceId, String NotificationId) throws IllegalArgumentException, InterruptedException, ExecutionException
	{
		Result notificationResult = new Result();
		NotificationClient notificationClient = new NotificationClient(userId, sessionId);
		{
			NewSharedKeyGeneratedNotification newKeyGeneratedNotification = new NewSharedKeyGeneratedNotification();
			newKeyGeneratedNotification.ReceiverId = ReceiverUserId;
			newKeyGeneratedNotification.SendById = CurrentUserId;
			newKeyGeneratedNotification.ResourceId = ResourceId;
			newKeyGeneratedNotification.OwnerId = CurrentUserId;
			newKeyGeneratedNotification.NotificationId = NotificationId;
			notificationResult = notificationClient.addNewNotificationAsync(newKeyGeneratedNotification).get();
		}
		return notificationResult;

	}

	CompletableFuture<Result> NotifyAsync(String ReceiverUserId, String ResourceId, String NotificationId)
	{
		CompletableFuture<Result> future = new CompletableFuture<Result>();
		service.submit(()->
		{
			try
			{
				Result result = Notify(ReceiverUserId, ResourceId, NotificationId);
				if(result.geterror_code()==0)
				{
					future.complete(result);
				}

			} 
			catch(IllegalArgumentException ae)
			{
				ae.printStackTrace();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				service.shutdown();
			}

		});
		return future;
	}



	Result CreateNewGroupAndUpdate(String ResourceId, String ReceiverUserId) throws IllegalArgumentException, 
	InterruptedException, ExecutionException, NoSuchAlgorithmException
	{
		Group newGroup = new Group();
		newGroup.CreatedOn = System.currentTimeMillis();
		newGroup.GroupId = new ByteToHex().GetHexString(new DigestSHA256().computeDigest(ReceiverUserId + ResourceId + System.currentTimeMillis()));
		newGroup.ResourceId = ResourceId;
		newGroup.OwnerName = CurrentUserId;
		newGroup.Users = new String[]{ ReceiverUserId };

		GroupClient client = new GroupClient(userId, sessionId);
		Result updateResult = client.putGroupAsync(newGroup).get();
		return updateResult;
	}

	CompletableFuture<Result> CreateNewGroupAndUpdateAsync(String ResourceId, String ReceiverUserId) throws IllegalArgumentException, InterruptedException, ExecutionException, NoSuchAlgorithmException
	{
		CompletableFuture<Result> future = new CompletableFuture<Result>();
		service.submit(()->
		{
			try
			{
				Result result = CreateNewGroupAndUpdate(ResourceId, ReceiverUserId);
				if(result.geterror_code()==0)
				{
					future.complete(result);
				}

			} 
			catch(IllegalArgumentException ae)
			{
				ae.printStackTrace();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				service.shutdown();
			}

		});
		return future;
	}

	Result UpdateACLList(String UserIdToAdd, String ResourceId) throws IllegalArgumentException, InterruptedException, ExecutionException
	{
		Result result = new Result();
		AccessControlClient client = new AccessControlClient(sessionId, CurrentUserId);
		{
			String[] userList = client.getUserListAsync(ResourceId).get();
			if (userList == null)
			{
				result = AddNewAclListAsync(UserIdToAdd, ResourceId).get();
			}
			else
			{
				result = UpdatateExistingAclListAsync(UserIdToAdd, ResourceId).get();
			}
		}
		return result;
	}

	CompletableFuture<Result> UpdateACLListAsync(String UserIdToAdd, String ResourceId)
	{
		CompletableFuture<Result> future = new CompletableFuture<Result>();
		service.submit(()->
		{
			try
			{
				Result result = UpdateACLList(UserIdToAdd, ResourceId);
				if(result.geterror_code()==0)
				{
					future.complete(result);
				}

			} 
			catch(IllegalArgumentException ae)
			{
				ae.printStackTrace();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				service.shutdown();
			}

		});
		return future;
	}

	Result AddNewAclList(String UserIdToAdd, String ResourceId) throws IllegalArgumentException, InterruptedException, ExecutionException
	{
		Result result = new Result();
		AccessControlClient aclClient = new AccessControlClient(sessionId, CurrentUserId);
		{
			ResourceUserWrapper newAcl = new ResourceUserWrapper();
			newAcl.ResourceId = ResourceId;
			newAcl.Users = new String[]
					{
							UserIdToAdd
					};

			result = aclClient.addNewResourceUserListAsync(newAcl).get();
		}

		return result;
	}

	CompletableFuture<Result> AddNewAclListAsync(String UserIdToAdd, String ResourceId)
	{
		CompletableFuture<Result> future = new CompletableFuture<Result>();
		service.submit(()->
		{
			try
			{
				Result result = AddNewAclList(UserIdToAdd, ResourceId);
				if(result.geterror_code()==0)
				{
					future.complete(result);
				}

			} 
			catch(IllegalArgumentException ae)
			{
				ae.printStackTrace();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				service.shutdown();
			}

		});
		return future;
	}

	private Result UpdateGroup(String resourceId, String ReceiverUserId) throws InterruptedException, ExecutionException 
	{
		// group is present 
		Result updateExistingGroupResult = null;
		GroupClient groupClient = new GroupClient(sessionId, CurrentUserId);
		{
			updateExistingGroupResult = groupClient.updateGroupAsync(resourceId, ReceiverUserId).get();
		}
		return updateExistingGroupResult;

	}

	private CompletableFuture<Result> UpdateGroupAsync(String resourceId, String ReceiverUserId) 
	{
		CompletableFuture<Result> future = new CompletableFuture<Result>();
		service.submit(()->
		{
			try
			{
				Result result = UpdateGroup(resourceId, ReceiverUserId);
				if(result.geterror_code()==0)
				{
					future.complete(result);
				}

			} 
			catch(IllegalArgumentException ae)
			{
				ae.printStackTrace();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				service.shutdown();
			}

		});
		return future;
	}

	private Group DoesResourceExist(String resourceId) throws IllegalArgumentException, InterruptedException, ExecutionException 
	{
		GroupClient groupClient = new GroupClient(sessionId, CurrentUserId);
		return groupClient.getGroupByResourceidAsync(resourceId).get();

	}

	private CompletableFuture<Group> DoesResourceExistAsync(String resourceId) throws IllegalArgumentException, InterruptedException, ExecutionException 
	{
		CompletableFuture<Group> future = new CompletableFuture<Group>();
		service.submit(()->
		{
			try
			{
				Group result = DoesResourceExist(resourceId);
				if(result !=null)
				{
					future.complete(result);
				}

			} 
			catch(IllegalArgumentException ae)
			{
				ae.printStackTrace();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				service.shutdown();
			}

		});
		return future;
	}

	private byte[] EncryptShareKey(byte[] resourceKey, byte[] publicKeyByte) throws IOException 
	{
		byte[] cipherText = null;
		byte[][] rsaPublic = ByteArrayConcatenator.Split2Arrays(publicKeyByte);
		rsaMethod = new RSAEncryptMethod();
		{
			rsaMethod.Key = new RSAKey();
			rsaMethod.Key.Exponent = rsaPublic[0];
			rsaMethod.Key.Modulus = rsaPublic[1];

		};
		cipherText = rsaMethod.EncryptFromBytes(resourceKey);
		return cipherText;
	}

	Result UpdatateExistingAclList(String UserIdToAdd, String ResourceId) throws IllegalArgumentException, InterruptedException, ExecutionException
	{
		Result result = new Result();
		AccessControlClient aclClient = new AccessControlClient(sessionId, CurrentUserId);
		result = aclClient.addUserAsync(ResourceId, UserIdToAdd).get();

		return result;
	}

	CompletableFuture<Result> UpdatateExistingAclListAsync(String UserIdToAdd, String ResourceId) throws IllegalArgumentException, InterruptedException, ExecutionException
	{
		CompletableFuture<Result> future = new CompletableFuture<Result>();
		service.submit(()->
		{
			try
			{
				Result result = UpdatateExistingAclList(UserIdToAdd, ResourceId);
				if(result.geterror_code()==0)
				{
					future.complete(result);
				}

			} 
			catch(IllegalArgumentException ae)
			{
				ae.printStackTrace();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				service.shutdown();
			}

		});
		return future;
	}

	@SuppressWarnings("null")
	private ResourceKey[] GetSharedKeys() throws Exception
	{
		BlockingQueue<ResourceKey> resourceKeys = null;
		if (StringUtils.isEmpty(CurrentUserId))
		{
			throw new IllegalArgumentException("You need to sign in first in order to access the location");
		}

		Notification[] notifications= GetAllKeySharedNotificationsAsync(CurrentUserId).get();

		for (Notification notification : notifications)
		{
			// retrieve from the key store 
			KeyStoreClient client = new KeyStoreClient(sessionId, CurrentUserId);
			{
				NewSharedKeyGeneratedNotification sharedNotification =(NewSharedKeyGeneratedNotification) notification;
				KeyRecord record = client.getAllKeyRecordsUniqueIdAsync(sharedNotification.NotificationId).get();
				byte[] key= utils.DecryptKeyAsym(record.ResourceKey, UserPublicPrivateKeyPairs.PrivateKey);
				ResourceKey resourceKey = new ResourceKey();
				resourceKey.Key = key;
				resourceKey.ResourceId = record.ResourceId;

				resourceKeys.add(resourceKey);
			}

		}

		return (ResourceKey[]) resourceKeys.toArray();
	}

	@Override
	/**
	 * Get shared keys
	 * @param record record is required
	 * @return ResourceKey array
	 */
	public CompletableFuture<ResourceKey[]> GetSharedKeysAsync() 
	{
		CompletableFuture<ResourceKey[]> future = new CompletableFuture<ResourceKey[]>();
		service.submit(()->
		{
			try
			{
				ResourceKey[] result = GetSharedKeys();
				if(result != null)
				{
					future.complete(result);
				}

			} 

			catch(Exception e)
			{
				e.printStackTrace();
				service.shutdown();
			}

		});
		return future;
	}

}
