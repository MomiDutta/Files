package com.ziroh.chabby.userManagement.client;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;

import com.ziroh.chabby.operationalResults.Result;
import com.ziroh.chabby.userManagement.backEnd.UserManagementBackEndClient;
import com.ziroh.chabby.userManagement.common.AuthResult;
import com.ziroh.chabby.userManagement.common.UserCredentials;
import com.ziroh.chabby.userManagement.common.UserCryptoUtils;
import com.ziroh.chabby.userManagement.common.UserData;

/**
 * This class represents all User Management related functions
 */
public class UserManagementClient
{
	
	UserManagementBackEndClient backEndClient;
	UserCryptoUtils utils;
	static final int iCPU = Runtime.getRuntime().availableProcessors();
	static ExecutorService service = Executors.newFixedThreadPool(iCPU);

	/**
	 * UserManagementClient constructor
	 */
	public UserManagementClient()
	{
		backEndClient = new UserManagementBackEndClient();
		utils = new UserCryptoUtils();
	}

	public AuthResult signup(UserCredentials userLogin) 
	{

		AuthResult userCreationResult  = new AuthResult();

		try
		{
			//Check useraname & password validity
			if(checkCredentialsValidity(userLogin) )
			{
				if(false == CheckIfUserExist(userLogin.UserName))
				{
					userCreationResult = createNewUserAsync(userLogin.UserName, userLogin.Password).get();
					if(userCreationResult.geterror_code()==0)
					{
						userCreationResult.seterror_message("Stored user successfully");
					}
					else
					{
						userCreationResult = new AuthResult();
						userCreationResult.seterror_code(1);
						userCreationResult.seterror_message("Failed to store user");
					}

				}
				else
				{
					userCreationResult = new AuthResult();
					userCreationResult.seterror_code(3002);
					userCreationResult.seterror_message("User exist");
				}
			}
			else
			{
				userCreationResult = new AuthResult();
				userCreationResult.seterror_code(3001);
				userCreationResult.seterror_message("UserCredentials not valid");
			}

		}

		catch(Exception e)
		{
			e.printStackTrace();
			userCreationResult = new AuthResult();
			userCreationResult.seterror_code(3000);
			userCreationResult.seterror_message("New user creation failed");
		}

		return userCreationResult;
	}

	/**
	 * User sign's up 
	 * @param userLogin User credentials is required
	 * @return AuthResult object
	 * @throws IllegalArgumentException
	 * UserCredentials cannot be null
	 */
	public CompletableFuture<AuthResult> signupAsync(UserCredentials userLogin) throws IllegalArgumentException
	{
		CompletableFuture<AuthResult> future = new CompletableFuture<AuthResult>();
		service.submit(()->
		{
			try
			{
				if(userLogin==null)
				{
					throw new IllegalArgumentException("UserCredentials cannot be null");
				}

				AuthResult result = signup(userLogin);
				if (result.geterror_code()==0)
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

	public AuthResult createNewUser(String hashUserID, String password) throws InterruptedException, ExecutionException 
	{
		if(hashUserID==null || StringUtils.isEmpty(hashUserID) || password==null || StringUtils.isEmpty(password))
		{
			throw new IllegalArgumentException("HashUserID and password cannpt be null or empty");
		}
		
		byte[] salt = utils.GenerateSalt();

		byte[] digest = utils.CreateDigest(password, salt);

		//Add new user to  backend Auth server
		UserData data = new UserData();
		data.setID(hashUserID);;
		data.setSalt(salt);
		data.setDigest(digest);
		AuthResult storeResult = backEndClient.storeUserDataAsync(data).get();	
		return storeResult;
	}

	public CompletableFuture<AuthResult> createNewUserAsync(String hashUserID, String password) 
	{
		CompletableFuture<AuthResult> future = new CompletableFuture<AuthResult>();
		service.submit(()->
		{
			try
			{
				if(hashUserID==null || StringUtils.isEmpty(hashUserID) || password==null || StringUtils.isEmpty(password))
				{
					throw new IllegalArgumentException("HashUserID and password cannpt be null or empty");
				}

				AuthResult result = createNewUser(hashUserID, password);
				if (result.geterror_code()==0)
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

	boolean CheckIfUserExist(String userid)
	{
		boolean doesUserExist = false;
		try
		{
			doesUserExist = backEndClient.checkIfUserExistAsync(userid).get();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return doesUserExist;
	}

	// do a check on user credentials validity
	boolean checkCredentialsValidity(UserCredentials credentials)
	{
		boolean userValid = false;
		boolean passValid = false;
		//		boolean emailValid = false;

		if(credentials!= null)
		{
			UserUtility validator = new UserUtility();
			userValid = validator.ValidateUserId(credentials.UserName);
			passValid = validator.isPasswordValid(credentials.Password);
		}
		return userValid && passValid;
	}

	public AuthResult signIn(UserCredentials newUserCredentials) 
	{
		if(newUserCredentials==null)
		{
			throw new IllegalArgumentException("Credentials cannot be null");
		}
		
		AuthResult authResult = new AuthResult();
		try
		{			
			if(false == CheckIfUserExist(newUserCredentials.UserName))
			{
				authResult = new AuthResult();
				authResult.seterror_code(3003);
				authResult.seterror_message("User donot exist");

			}
			else
			{
				//Authenticate User
				AuthResult result = AuthenticateUserAsync(newUserCredentials.UserName, newUserCredentials.Password).get();
				authResult = result;
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			authResult = new AuthResult();
			authResult.seterror_code(3000);
			authResult.seterror_message("user signin failed");
			authResult.setToken(null);
		}

		return authResult;
	}

	/**
	 * User sign's in
	 * @param newUserCredentials User credential is required
	 * @return AuthResult object
	 * @throws IllegalArgumentException
	 * Credentials cannot be null
	 */
	public CompletableFuture<AuthResult> signInAsync(UserCredentials newUserCredentials) throws IllegalArgumentException
	{
		CompletableFuture<AuthResult> future = new CompletableFuture<AuthResult>();
		service.submit(()->
		{
			try
			{
				if(newUserCredentials==null)
				{
					throw new IllegalArgumentException("Credentials cannot be null");
				}

				AuthResult result = signIn(newUserCredentials);
				if (result.geterror_code()==0)
				{
					future.complete(result);
				}
				else
				{
					System.out.println("Not complete");
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

	private AuthResult AuthenticateUser(String hashUserID, String password) throws IllegalArgumentException, InterruptedException, ExecutionException 
	{

		byte[] salt = backEndClient.getUserSaltAsync(hashUserID).get();
		byte[] digest = utils.CreateDigest(password, salt);

		AuthResult result = backEndClient.authenticateUserAsync(hashUserID, digest).get();
		return result;
	}

	private CompletableFuture<AuthResult> AuthenticateUserAsync(String hashUserID, String password) throws IllegalArgumentException, InterruptedException, ExecutionException 
	{
		CompletableFuture<AuthResult> future = new CompletableFuture<AuthResult>();
		service.submit(()->
		{
			try
			{
				if(hashUserID==null || StringUtils.isEmpty(hashUserID) || password==null || StringUtils.isEmpty(password))
				{
					throw new IllegalArgumentException("HashUserID and password cannot be null or empty");
				}

				AuthResult result = AuthenticateUser(hashUserID, password);
				if (result.geterror_code()==0)
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

	public Result ResetUserPassword(UserCredentials credentials)
	{
		if(credentials==null)
		{
			throw new IllegalArgumentException("Credential cannot be null");
		}
		
		Result resetResult = new Result();
		try
		{
			if(checkCredentialsValidity(credentials) )
			{
				//				String hashUserID = new UserCryptoUtils
				//						().getUserId(userLogIn.UserName);
				if(true == CheckIfUserExist(credentials.UserName))
				{
					resetResult = ResetUserDataAsync(credentials.UserName, credentials.Password).get();
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return resetResult;
	}

	/**
	 * Reset User's Password
	 * @param credentials Credential is required
	 * @return Result object
	 * @throws IllegalArgumentException
	 * Credential cannot be null
	 */
	public CompletableFuture<Result> ResetUserPasswordAsync(UserCredentials credentials) throws IllegalArgumentException
	{
		CompletableFuture<Result> future = new CompletableFuture<Result>();
		service.submit(()->
		{
			try
			{
				if(credentials==null)
				{
					throw new IllegalArgumentException("Credential cannot be null");
				}

				Result result = ResetUserPassword(credentials);
				if (result.geterror_code()==0)
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

	private Result ResetUserData(String hashUserID, String password) throws IllegalArgumentException, InterruptedException, ExecutionException
	{
		byte[] salt = backEndClient.getUserSaltAsync(hashUserID).get();
		byte[] digest = utils.CreateDigest(password, salt);

		UserData data = new UserData();
		data.ID = hashUserID;
		data.Salt = salt;
		data.Digest = digest;
		Result updateResult = backEndClient.updateUserInfoAsync(data).get();
		return updateResult;
	}

	private CompletableFuture<Result> ResetUserDataAsync(String hashUserID, String password) throws IllegalArgumentException, InterruptedException, ExecutionException
	{
		CompletableFuture<Result> future = new CompletableFuture<Result>();
		service.submit(()->
		{
			try
			{
				Result result = ResetUserData(hashUserID, password);
				if (result.geterror_code()==0)
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
}
