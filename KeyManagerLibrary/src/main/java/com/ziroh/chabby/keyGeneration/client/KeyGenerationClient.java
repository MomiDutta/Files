package com.ziroh.chabby.keyGeneration.client;

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
import com.ziroh.chabby.common.keyTypes.Key;
import com.ziroh.chabby.common.keyTypes.RSAKey;

/**
 * This client needs to be used to generate Keys from the server
 */
public class KeyGenerationClient 
{
	/**
	 * Base Url
	 */
	String KeyStoreUrl = "http://localhost:53002";

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
	 * KeyStoreClient constructor
	 * @param userId User identifier
	 * @param sessionID Session identifier
	 */
	public KeyGenerationClient(String sessionID,String userId)
	{
		this.sessionId = sessionID;
		this.userId = userId;
	}
	
	static final int iCPU = Runtime.getRuntime().availableProcessors();
	static ExecutorService service = Executors.newFixedThreadPool(iCPU);
	
	public Key generateKey(String keyType) 
	{
		Client generateKeyClient = Client.create();
		String generateKeyUrl = KeyStoreUrl + "/KeyGenerator/" +keyType;
		WebResource generateKeyResource = generateKeyClient.resource(generateKeyUrl);
		RSAKey key = null;
		try
		{
			if(keyType==null)
			{
				throw new IllegalArgumentException("KeyType cannot be null");
			}
			else if(StringUtils.isEmpty(keyType))
			{
				throw new IllegalArgumentException("keyType cannot be empty");
			}
			
			Gson gson = new GsonBuilder().create();
			String keyTypeInJSon = gson.toJson(keyType);
			
			ClientResponse httpResponse = generateKeyResource.accept("application/json").type("application/json")
					.header("sessionId", getSessionId()).header("userId", getUserId())
					.post(ClientResponse.class, keyTypeInJSon);
			
			if(httpResponse.getStatus()==200)
			{
				String responseBody = httpResponse.getEntity(String.class);
				
				key = gson.fromJson(responseBody, com.ziroh.chabby.common.keyTypes.RSAKey.class);
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
			generateKeyClient = null;
		}
		return key;
	}
	
	/**
	 * This method generates a specific key
	 * @param keyType KeyType is required
	 * @return Key object
	 * @throws IllegalArgumentException
	 * KeyType cannot be null
	 * or
	 * KeyType cannot be empty
	 */
	public CompletableFuture<Key> generateKeyAsync(String keyType) throws IllegalArgumentException
	{
		CompletableFuture<Key> future = new CompletableFuture<Key>();
		service.submit(()->
		{
			try
			{
				if(keyType==null)
				{
					throw new IllegalArgumentException("KeyType cannot be null");
				}
				else if(StringUtils.isEmpty(keyType))
				{
					throw new IllegalArgumentException("keyType cannot be empty");
				}
				
				Key key = generateKey(keyType);
				if (key != null)
				{
					future.complete(key);
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
