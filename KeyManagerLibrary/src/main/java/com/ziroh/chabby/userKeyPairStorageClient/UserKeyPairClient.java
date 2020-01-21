package com.ziroh.chabby.userKeyPairStorageClient;

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
import com.ziroh.chabby.userKeyPairStorage.UserKeyPair;
import com.ziroh.chabby.utils.JSONCustomSerializer;

/**
 * This class is used for implementation for retrieval of User Public Key pair 
 */
public class UserKeyPairClient
{
	/**
	 * Base url
	 */
	String UserKeyPairClientUrl = "http://localhost:53015";
	
	/**
	 * User identifier
	 */
	String userId;
	
	/**
	 * Session Identifier
	 */
	String sessionId;
	
	/**
	 * Get userID
	 * @return UserID
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * Set UserID
	 * @param userId UserID is required
	 */
	public void setUserid(String userId) {
		this.userId = userId;
	}

	/**
	 * Get SessionID
	 * @return SessionID
	 */
	public String getSessionId() {
		return sessionId;
	}

	/**
	 * Set SessionID
	 * @param sessionId SessionID is required
	 */
	public void setSessionid(String sessionId) {
		this.sessionId = sessionId;
	}
	/**
	 * UserKeyPairClient constructor
	 * @param userId UserID is required
	 * @param sessionId sessionID is required
	 */
	public UserKeyPairClient(String sessionId, String userId)
	{
		this.sessionId = sessionId;
		this.userId = userId;
	}
	
	static final int iCPU = Runtime.getRuntime().availableProcessors();
	static ExecutorService service = Executors.newFixedThreadPool(iCPU);
	
	
	public UserKeyPair getKeyPair(String userId) 
	{
		Client getKeyPairClient = Client.create();
		String getKeyPairUrl = UserKeyPairClientUrl + "/userkeypair/" +userId;
		WebResource getKeyPairResource = getKeyPairClient.resource(getKeyPairUrl);
		UserKeyPair result = new UserKeyPair();
		try
		{
			if(userId==null)
			{
				throw new IllegalArgumentException("The user id of a record cannot be null");
			}
			else if(StringUtils.isEmpty(userId))
			{
				throw new IllegalArgumentException("The resource id of a record cannot be empty");
			}
			
			GsonBuilder builder = new GsonBuilder();
			builder.registerTypeHierarchyAdapter(byte[].class, new JSONCustomSerializer());	
			Gson gson=builder.create();
			
			ClientResponse httpResponse = getKeyPairResource.accept("application/json").type("application/json")
					.header("Accept", "Content-Type: application/json;charset=utf-8")
					.header("sessionId", getSessionId()).header("userId", getUserId())
					.get(ClientResponse.class);
			
			if(httpResponse.getStatus()==200)
			{
				String responseBody = httpResponse.getEntity(String.class);
				result = gson.fromJson(responseBody, com.ziroh.chabby.userKeyPairStorage.UserKeyPair.class);
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
			getKeyPairClient = null;
		}
		return result;		 
	}
	
	/**
	 * Gets the key pair
	 * @param userId UserID is required
	 * @return UserKeyPair object
	 * @throws IllegalArgumentException
	 * The user id of a record cannot be null
	 * or
	 * The resource id of a record cannot be empty
	 */
	public CompletableFuture<UserKeyPair> getKeyPairAsync(String userId) throws IllegalArgumentException
	{
		CompletableFuture<UserKeyPair> future = new CompletableFuture<UserKeyPair>();
		service.submit(()->
		{
			try
			{
				if(userId==null)
				{
					throw new IllegalArgumentException("The user id of a record cannot be null");
				}
				else if(StringUtils.isEmpty(userId))
				{
					throw new IllegalArgumentException("The resource id of a record cannot be empty");
				}
				
				UserKeyPair result = getKeyPair(userId);
				if (result != null)
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
	
	public Result storeKeyPair(UserKeyPair keyPair) 
	{
		Client storeKeyPairClient = Client.create();
		String storeKeyPairUrl = UserKeyPairClientUrl + "/keypair";
		WebResource storeKeyPairResource = storeKeyPairClient.resource(storeKeyPairUrl);
		Result result = null;
		try
		{
			if(keyPair==null)
			{
				throw new IllegalArgumentException("KeyPair cannot be null");
			}
			
			GsonBuilder builder = new GsonBuilder();
			builder.registerTypeHierarchyAdapter(byte[].class, new JSONCustomSerializer());	
			Gson gson=builder.create();
			String keyPairInJSon = gson.toJson(keyPair);
			
			ClientResponse httpResponse = storeKeyPairResource.accept("application/json").type("application/json")
					.header("Accept", "Content-Type: application/json;charset=utf-8")
					.header("userId", getUserId()).header("sessionId", getSessionId())
					.post(ClientResponse.class, keyPairInJSon);
			
			if(httpResponse.getStatus()==200)
			{
				String responseBody = httpResponse.getEntity(String.class);
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
			storeKeyPairClient = null;
		}
		return result;
	}
	
	/**
	 * Stores the key pair
	 * @param keyPair KeyPair is required
	 * @return Result object
	 * @throws IllegalArgumentException
	 * KeyPair cannot be null
	 */
	public CompletableFuture<Result> storeKeyPairAsync(UserKeyPair keyPair) throws IllegalArgumentException
	{
		CompletableFuture<Result> future = new CompletableFuture<Result>();
		service.submit(()->
		{
			try
			{
				if(keyPair==null)
				{
					throw new IllegalArgumentException("KeyPair cannot be null");
				}
				
				Result result = storeKeyPair(keyPair);
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
	
	public Result updateKeyPair(UserKeyPair keyPair) 
	{
		Client updateKeyPairClient = Client.create();
		String updateKeyPairUrl = UserKeyPairClientUrl + "/keypair/update";
		WebResource updateKeyPairResource = updateKeyPairClient.resource(updateKeyPairUrl);
		Result result = null;
		try
		{
			if(keyPair==null)
			{
				throw new IllegalArgumentException("Keypair cannot be null");
			}
			
			GsonBuilder builder = new GsonBuilder();
			builder.registerTypeHierarchyAdapter(byte[].class, new JSONCustomSerializer());	
			Gson gson=builder.create();
			String keyPairInJSon = gson.toJson(keyPair);
			ClientResponse httpResponse = updateKeyPairResource.accept("application/json").type("application/json")
					.header("Accept", "Content-Type: application/json;charset=utf-8")
					.header("sessionId", getSessionId()).header("userID", getUserId())
					.post(ClientResponse.class, keyPairInJSon);
			if(httpResponse.getStatus()==200)
			{
				String responseBody = httpResponse.getEntity(String.class);
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
			updateKeyPairClient = null;
		}
		return result;
	}
	
	/**
	 * Update the keyPair
	 * @param keyPair KeyPair is required
	 * @return Result object
	 * @throws IllegalArgumentException
	 * KeyPair cannot be null
	 */
	public CompletableFuture<Result> updateKeyPairAsync(UserKeyPair keyPair) throws IllegalArgumentException
	{
		CompletableFuture<Result> future = new CompletableFuture<Result>();
		service.submit(()->
		{
			try
			{
				if(keyPair==null)
				{
					throw new IllegalArgumentException("Keypair cannot be null");
				}
				
				Result result = updateKeyPair(keyPair);
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
