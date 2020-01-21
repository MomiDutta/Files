package com.ziroh.chabby.userManagement.backEnd;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.ziroh.chabby.operationalResults.Result;
import com.ziroh.chabby.userManagement.common.AuthResult;
import com.ziroh.chabby.userManagement.common.UserData;
import com.ziroh.chabby.utils.JSONCustomSerializer;

/**
 * This client operates all UserManagement backEnd Operations
 */
public class UserManagementBackEndClient 
{
	/**
	 * Base Url
	 */
	String baseUrl = "http://localhost:53004/UserManagement";
	
	static final int iCPU = Runtime.getRuntime().availableProcessors();
	static ExecutorService service = Executors.newFixedThreadPool(iCPU);

	
	public AuthResult storeUserData(UserData userInfo) 
	{
		Client storeUserDataClient = Client.create();
		String storeUserDataUrl = baseUrl + "/Users/User";
		WebResource storeUserDataResource = storeUserDataClient.resource(storeUserDataUrl);
		AuthResult result = new AuthResult();
		try
		{ 
			if(userInfo==null)
			{
				throw new IllegalArgumentException("UserInfo cannot be null");
			}
			else if(StringUtils.isEmpty(userInfo.ID) || userInfo.ID==null)
			{
				throw new IllegalArgumentException("UserInfo id cannot be null or empty");
			}
			else if(userInfo.Digest==null || userInfo.Digest.length==0)
			{
				throw new IllegalArgumentException("Digest cannot be null or empty");
			}
			else if(userInfo.Salt==null || userInfo.Salt.length==0)
			{
				throw new IllegalArgumentException("UserId cannot be null or empty");
			}
			
			GsonBuilder builder = new GsonBuilder();
			builder.registerTypeHierarchyAdapter(byte[].class, new JSONCustomSerializer());
			Gson gson=builder.create();
			//Gson gson = new GsonBuilder().create();
			String userInfoInJSon = gson.toJson(userInfo);
			ClientResponse httpResponse = storeUserDataResource.accept("application/json").type("application/json").post(ClientResponse.class, userInfoInJSon);
			if(httpResponse.getStatus()==200)
			{
				String responseBody = httpResponse.getEntity(String.class);
				result = gson.fromJson(responseBody, com.ziroh.chabby.userManagement.common.AuthResult.class);
			}
			else
			{
				result = new AuthResult();
				result.seterror_code(1);
				result.seterror_message(httpResponse.toString());
			}

		}
		catch(UniformInterfaceException ue)
		{
			ue.printStackTrace(); 
		}
		catch(ClientHandlerException ce)
		{
			ce.printStackTrace();
		}
		catch (JsonSyntaxException je) 
		{
			je.printStackTrace();
		} 
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			storeUserDataClient = null;
		}
		return result;
	}

	/**
	 * Stores User Informations
	 * @param userInfo User information is required
	 * @return Result object
	 * @throws IllegalArgumentException
	 * UserInfo cannot be null
	 * or
	 * UserInfo id cannot be null or empty
	 * or
	 * Digest cannot be null or empty
	 * or
	 * UserId cannot be null or empty
	 */
	public CompletableFuture<AuthResult> storeUserDataAsync(UserData userInfo) throws IllegalArgumentException
	{
		CompletableFuture<AuthResult> future = new CompletableFuture<AuthResult>();
		service.submit(()->
		{
			try
			{
				if(userInfo==null)
				{
					throw new IllegalArgumentException("UserInfo cannot be null");
				}
				else if(StringUtils.isEmpty(userInfo.ID) || userInfo.ID==null)
				{
					throw new IllegalArgumentException("UserInfo id cannot be null or empty");
				}
				else if(userInfo.Digest==null || userInfo.Digest.length==0)
				{
					throw new IllegalArgumentException("Digest cannot be null or empty");
				}
				else if(userInfo.Salt==null || userInfo.Salt.length==0)
				{
					throw new IllegalArgumentException("UserId cannot be null or empty");
				}

				AuthResult result = storeUserData(userInfo);
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

	public Result updateUserInfo(UserData user) 
	{
		Client updateUserInfoClient = Client.create();
		String updateUserInfoUrl = baseUrl + "/Users/User";
		WebResource updateUserInfoResource= updateUserInfoClient.resource(updateUserInfoUrl);
		Result userUpdateResultsult = new Result();
		try
		{
			if(user==null)
			{
				throw new IllegalArgumentException("User cannot be null");
			}
			else if(StringUtils.isEmpty(user.ID) || user.ID==null)
			{
				throw new IllegalArgumentException("User id cannot be null or empty");
			}
			else if(user.Digest==null || user.Digest.length==0)
			{
				throw new IllegalArgumentException("Digest cannot be null or empty");
			}
			else if(user.Salt==null || user.Salt.length==0)
			{
				throw new IllegalArgumentException("Salt cannot be null or empty");
			}

			
			Gson gson = new GsonBuilder().create();
			String userInJSon = gson.toJson(user);
			ClientResponse httpResponse = updateUserInfoResource.accept("application/json").type("application/json").put(ClientResponse.class, userInJSon);
			if(httpResponse.getStatus()==200)
			{
				String responseBody = httpResponse.getEntity(String.class);
				userUpdateResultsult = gson.fromJson(responseBody, com.ziroh.chabby.operationalResults.Result.class);
			}
			else
			{
				userUpdateResultsult = new Result();
				userUpdateResultsult.seterror_code(1);
				userUpdateResultsult.seterror_message(httpResponse.toString());
			}

		}
		catch(UniformInterfaceException ue)
		{
			ue.printStackTrace(); 
		}
		catch(ClientHandlerException ce)
		{
			ce.printStackTrace();
		}
		catch (JsonSyntaxException je) 
		{
			je.printStackTrace();
		} 
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			updateUserInfoClient = null;
		}
		return userUpdateResultsult;
	}

	/**
	 * Update user information
	 * @param user UserData is required
	 * @return Result object
	 * @throws IllegalArgumentException
	 * User cannot be null
	 * or
	 * User id cannot be null or empty
	 * or
	 * Digest cannot be null or empty
	 * or
	 * Salt cannot be null or empty
	 */
	public CompletableFuture<Result> updateUserInfoAsync(UserData user) throws IllegalArgumentException
	{
		CompletableFuture<Result> future = new CompletableFuture<Result>();
		service.submit(()->
		{
			try
			{
				if(user==null)
				{
					throw new IllegalArgumentException("User cannot be null");
				}
				else if(StringUtils.isEmpty(user.ID) || user.ID==null)
				{
					throw new IllegalArgumentException("User id cannot be null or empty");
				}
				else if(user.Digest==null || user.Digest.length==0)
				{
					throw new IllegalArgumentException("Digest cannot be null or empty");
				}
				else if(user.Salt==null || user.Salt.length==0)
				{
					throw new IllegalArgumentException("Salt cannot be null or empty");
				}

				Result result = updateUserInfo(user);
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

	public UserData getUser(String userId) 
	{
		Client getUserClient = Client.create();
		String getUserUrl = baseUrl + "/Users/User/" +userId;
		WebResource getUserResource = getUserClient.resource(getUserUrl);
		UserData userCredential = null;
		try
		{
			if(userId==null)
			{
				throw new IllegalArgumentException("User identifier cannot be null");
			}
			else if(StringUtils.isEmpty(userId))
			{
				throw new IllegalArgumentException("User identifier cannot be empty");
			}
			
			ClientResponse httpResponse = getUserResource.accept("application/json").type("application/json").get(ClientResponse.class);
			if(httpResponse.getStatus()==200)
			{
				String responseBody = httpResponse.getEntity(String.class);
				Gson gson = new GsonBuilder().create();
				userCredential = gson.fromJson(responseBody, com.ziroh.chabby.userManagement.common.UserData.class);
			}

		}
		catch(UniformInterfaceException ue)
		{
			ue.printStackTrace(); 
		}
		catch(ClientHandlerException ce)
		{
			ce.printStackTrace();
		}
		catch (JsonSyntaxException je) 
		{
			je.printStackTrace();
		} 
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			getUserClient = null;
		}
		return userCredential;
	}

	/**
	 * Get user
	 * @param userId User identifier is required
	 * @return UserData object
	 * @throws IllegalArgumentException
	 * User identifier cannot be null
	 * or
	 * User identifier cannot be empty
	 */
	public CompletableFuture<UserData> getUserAsync(String userId) throws IllegalArgumentException
	{
		CompletableFuture<UserData> future = new CompletableFuture<UserData>();
		service.submit(()->
		{
			try
			{
				if(userId==null)
				{
					throw new IllegalArgumentException("User identifier cannot be null");
				}
				else if(StringUtils.isEmpty(userId))
				{
					throw new IllegalArgumentException("User identifier cannot be empty");
				}

				UserData userCredential = getUser(userId);
				if (userCredential != null)
				{
					future.complete(userCredential);
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

	public Result deleteUser(String userId) 
	{
		Client deleteUserClient = Client.create();
		String deleteUserUrl = baseUrl + "/Users/User/" +userId;
		WebResource deleteUserResource = deleteUserClient.resource(deleteUserUrl);
		Result result = new Result();
		try
		{
			if(userId==null)
			{
				throw new IllegalArgumentException("User identifier cannot be null");
			}
			else if(StringUtils.isEmpty(userId))
			{
				throw new IllegalArgumentException("User identifier cannot be empty");
			}
			
			ClientResponse httpResponse = deleteUserResource.accept("application/json").type("application/json").delete(ClientResponse.class);
			if(httpResponse.getStatus()==200)
			{
				String responseBody = httpResponse.getEntity(String.class);
				Gson gson = new GsonBuilder().create();
				result = gson.fromJson(responseBody, com.ziroh.chabby.operationalResults.Result.class);
			}
			else
			{
				result = new Result();
				result.seterror_code(1);
				result.seterror_message(httpResponse.toString());
			}

		}
		catch(UniformInterfaceException ue)
		{
			ue.printStackTrace(); 
		}
		catch(ClientHandlerException ce)
		{
			ce.printStackTrace();
		}
		catch (JsonSyntaxException je) 
		{
			je.printStackTrace();
		} 
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			deleteUserClient = null;
		}
		return result;
	}

	/**
	 * Delete user
	 * @param userId UserID is required
	 * @return Result object 
	 * @throws IllegalArgumentException
	 * User identifier cannot be null
	 * or
	 * User identifier cannot be empty
	 */
	public CompletableFuture<Result> deleteUserAsync(String userId) throws IllegalArgumentException
	{
		CompletableFuture<Result> future = new CompletableFuture<Result>();
		service.submit(()->
		{
			try
			{
				if(userId==null)
				{
					throw new IllegalArgumentException("User identifier cannot be null");
				}
				else if(StringUtils.isEmpty(userId))
				{
					throw new IllegalArgumentException("User identifier cannot be empty");
				}

				Result result = deleteUser(userId);
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

	public boolean checkIfUserExist(String userId) 
	{
		Client checkIfUserExistClient = Client.create();
		String checkIfUserExistUrl = baseUrl + "/Users/User/" +userId;
		WebResource storeUserDataResource = checkIfUserExistClient.resource(checkIfUserExistUrl);
		boolean doesUserExist = false;
		try
		{
			if(userId==null)
			{
				throw new IllegalArgumentException("User identifier cannot be null");
			}
			else if(StringUtils.isEmpty(userId))
			{
				throw new IllegalArgumentException("User identifier cannot be empty");
			}
			
			Gson gson = new GsonBuilder().create();
			String userIDInJSon = gson.toJson(userId);
			ClientResponse httpResponse = storeUserDataResource.accept("application/json").type("application/json").post(ClientResponse.class, userIDInJSon);
			if(httpResponse.getStatus()==200)
			{
				String responseBody = httpResponse.getEntity(String.class);
				
				doesUserExist = gson.fromJson(responseBody, java.lang.Boolean.class);
			}
			
		}
		catch(UniformInterfaceException ue)
		{
			ue.printStackTrace(); 
		}
		catch(ClientHandlerException ce)
		{
			ce.printStackTrace();
		}
		catch (JsonSyntaxException je) 
		{
			je.printStackTrace();
		} 
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			checkIfUserExistClient = null;
		}
		return doesUserExist;
	}

	/**
	 * Check if User Exist in the database
	 * @param userId UserID is required
	 * @return Boolean result
	 * @throws IllegalArgumentException
	 * User identifier cannot be null
	 * or
	 * User identifier cannot be empty
	 */
	public CompletableFuture<Boolean> checkIfUserExistAsync(String userId) throws IllegalArgumentException
	{
		CompletableFuture<Boolean> future = new CompletableFuture<Boolean>();
		service.submit(()->
		{
			try
			{
				if(userId==null)
				{
					throw new IllegalArgumentException("User identifier cannot be null");
				}
				else if(StringUtils.isEmpty(userId))
				{
					throw new IllegalArgumentException("User identifier cannot be empty");
				}

				Boolean doesUserExist = checkIfUserExist(userId);
				
				if (doesUserExist != null)
				{
					future.complete(doesUserExist);
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

	public byte[] getUserSalt(String userId) 
	{
		Client getUserSaltClient = Client.create();
		String getUserSaltUrl = baseUrl + "/Users/User/salt/" +userId;
		WebResource getUserSaltResource = getUserSaltClient.resource(getUserSaltUrl);
		byte[] userSalt = null;
		try
		{		
			if(userId==null)
			{
				throw new IllegalArgumentException("User identifier cannot be null");
			}
			else if(StringUtils.isEmpty(userId))
			{
				throw new IllegalArgumentException("User identifier cannot be empty");
			}
			
			ClientResponse httpResponse = getUserSaltResource.accept("application/json").type("application/json").get(ClientResponse.class);
			if(httpResponse.getStatus()==200)
			{
				String responseBody = httpResponse.getEntity(String.class);
				GsonBuilder builder = new GsonBuilder();
				builder.registerTypeHierarchyAdapter(byte[].class, new JSONCustomSerializer());
				Gson gson=builder.create();
				userSalt = gson.fromJson(responseBody, byte[].class);
			}
		}
		catch(UniformInterfaceException ue)
		{
			ue.printStackTrace(); 
		}
		catch(ClientHandlerException ce)
		{
			ce.printStackTrace();
		}
		catch (JsonSyntaxException je) 
		{
			je.printStackTrace();
		} 
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			getUserSaltClient = null;
		}
		return userSalt;
	}

	/**
	 * Get user's salt
	 * @param userId UserId is required
	 * @return Salt in byte array
	 * @throws IllegalArgumentException
	 * User identifier cannot be null
	 * or
	 * User identifier cannot be empty
	 */
	public CompletableFuture<byte[]> getUserSaltAsync(String userId) throws IllegalArgumentException
	{
		CompletableFuture<byte[]> future = new CompletableFuture<byte[]>();
		service.submit(()->
		{
			try
			{
				if(userId==null)
				{
					throw new IllegalArgumentException("User identifier cannot be null");
				}
				else if(StringUtils.isEmpty(userId))
				{
					throw new IllegalArgumentException("User identifier cannot be empty");
				}

				byte[] userSalt = getUserSalt(userId);
				if (userSalt != null)
				{
					future.complete(userSalt);
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

	public AuthResult  authenticateUser(String userId, byte[] digest) throws IllegalArgumentException
	{
		Client authenticateUserClient = Client.create();
		String authenticateUserUrl = baseUrl + "/Users/User/authenticate/" +userId;
		WebResource authenticateUserResource = authenticateUserClient.resource(authenticateUserUrl);
		AuthResult authResult = new AuthResult();
		try
		{
			if(userId==null)
			{
				throw new IllegalArgumentException("User identifier cannot be null");
			}
			else if(StringUtils.isEmpty(userId))
			{
				throw new IllegalArgumentException("User identifier cannot be empty");
			}
			else if(digest==null || digest.length==0)
			{
				throw new IllegalArgumentException("Digest cannot be null or empty");
			} 
			
			GsonBuilder builder = new GsonBuilder();
			builder.registerTypeHierarchyAdapter(byte[].class, new JSONCustomSerializer());
			Gson gson=builder.create();
			String digestInJSon = gson.toJson(digest);
			ClientResponse httpResponse = authenticateUserResource.accept("application/json").type("application/json").post(ClientResponse.class, digestInJSon);
			if(httpResponse.getStatus()==200)
			{
				String responseBody = httpResponse.getEntity(String.class);
				authResult = gson.fromJson(responseBody, com.ziroh.chabby.userManagement.common.AuthResult.class);
			}
			else
			{
				authResult = new AuthResult();
				authResult.seterror_code(1);
			}
		}
		catch(UniformInterfaceException ue)
		{
			ue.printStackTrace(); 
		}
		catch(ClientHandlerException ce)
		{
			ce.printStackTrace();
		}
		catch (JsonSyntaxException je) 
		{
			je.printStackTrace();
		} 
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			authenticateUserClient = null;
		}
		return authResult;
	}

	/**
	 * Authentcate user
	 * @param userId UserID is required
	 * @param digest Digest is required
	 * @return AuthResult object
	 * @throws IllegalArgumentException
	 * User identifier cannot be null
	 * or
	 * User identifier cannot be empty
	 * or
	 * Digest cannot be null or empty
	 */
	public CompletableFuture<AuthResult>  authenticateUserAsync(String userId, byte[] digest) throws IllegalArgumentException
	{
		CompletableFuture<AuthResult> future = new CompletableFuture<AuthResult>();
		service.submit(()->
		{
			try
			{
				if(userId==null)
				{
					throw new IllegalArgumentException("User identifier cannot be null");
				}
				else if(StringUtils.isEmpty(userId))
				{
					throw new IllegalArgumentException("User identifier cannot be empty");
				}
				else if(digest==null || digest.length==0)
				{
					throw new IllegalArgumentException("Digest cannot be null or empty");
				} 


				AuthResult authResult = authenticateUser(userId, digest);
				if (authResult != null)
				{
					future.complete(authResult);
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
