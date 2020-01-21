package com.ziroh.chabby.sessionManager.client;

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

/**
 * This class represents all the session related functions
 */
class SessionCheckerClient
{
	/**
	 * base url
	 */
	private String baseUri = "http://localhost:53011";
	
	String userId;

	/**
	 * Session Identifier
	 */
	String sessionid;

	/**
	 * Get userID
	 * @return UserID
	 */
	public String getUserid() {
		return userId;
	}

	/**
	 * Set UserID
	 * @param userid UserID is required
	 */
	public void setUserid(String userid) {
		this.userId = userid;
	}

	/**
	 * Get SessionID
	 * @returnSessionID
	 */
	public String getSessionid() {
		return sessionid;
	}

	/**
	 * Set SessionID
	 * @param sessionid SessionID is required
	 */
	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
	}

	/**
	 * SessionCheckerClient constructor
	 * @param userId UserID is required
	 * @param sessionId sessionID is required
	 */
	public SessionCheckerClient(String userId, String sessionId)
	{
		this.userId = userId;
		this.sessionid = sessionId;
	}
	
	static final int iCPU = Runtime.getRuntime().availableProcessors();
	static ExecutorService service = Executors.newFixedThreadPool(iCPU);
	
	/**
	 * Default constructor
	 */
	SessionCheckerClient()
    {

    }
	
	
	private boolean checkSession(String userid, String sessionid) 
	{
		Client checkSessionClient = Client.create();
		String checkSessionUrl = baseUri + "/sessions/" +sessionid +"/" +userid;
		WebResource checkSessionResource = checkSessionClient.resource(checkSessionUrl);
		boolean isRecordInserted = false;
		try
		{
			ClientResponse httpResponse = checkSessionResource.accept("application/json").type("application/json")
					.header("sessionId", getSessionid()).header("userId", userId).get(ClientResponse.class);
			if(httpResponse.getStatus()==200)
			{
				String responseBody = httpResponse.getEntity(String.class);
				Gson gson = new GsonBuilder().create();
				isRecordInserted = gson.fromJson(responseBody, java.lang.Boolean.class);
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
			checkSessionClient = null;
		}
		return isRecordInserted;
	}
	
	/**
	 * This method check if a session is valid 
	 * @param userid UserID is required
	 * @param sessionid sessionID is required
	 * @return Boolean value
	 * @throws IllegalArgumentException
	 * The User id cannot be null
	 * or
	 * The user id record cannot be empty
	 * or
	 * sessionId cannot be null or empty
	 */
	public CompletableFuture<Boolean> checkSessionAsync(String userid, String sessionid) throws IllegalArgumentException
	{
		CompletableFuture<Boolean> future = new CompletableFuture<Boolean>();
		service.submit(()->
		{
			try
			{
				if(userid==null)
				{
					throw new IllegalArgumentException("The User id cannot be null");
				}
				else if(StringUtils.isEmpty(userid))
				{
					throw new IllegalArgumentException("The user id record cannot be empty");
				}
				else if(sessionid==null || StringUtils.isEmpty(sessionid))
				{
					throw new IllegalArgumentException("sessionId cannot be null or empty");
				}
				
				boolean isRecordInserted = checkSession(userid, sessionid);
				if (isRecordInserted != false)
				{
					future.complete(isRecordInserted);
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

	private boolean insertNewSession(String userid, String sessionid)
	{
		Client insertNewSessionClient = Client.create();
		String insertNewSessionUrl = baseUri + "/sessions/" +sessionid +"/" +userid;
		WebResource insertNewSessionResource = insertNewSessionClient.resource(insertNewSessionUrl);
		boolean isRecordInserted = false;
		try
		{
			ClientResponse httpResponse = insertNewSessionResource.accept("application/json").type("application/json")
					.header("sessionId", getSessionid()).header("userId", userId).put(ClientResponse.class);
			if(httpResponse.getStatus()==200)
			{
				String responseBody = httpResponse.getEntity(String.class);
				Gson gson = new GsonBuilder().create();
				isRecordInserted = gson.fromJson(responseBody, java.lang.Boolean.class);
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
			insertNewSessionClient = null;
		}
		return isRecordInserted;
	}
	
	/**
	 * This method inserts a new session id after sign in
	 * @param userid UsrID is required
	 * @param sessionid sessionID is required
	 * @return Boolean value
	 * @throws IllegalArgumentException
	 * The User id cannot be null
	 * or
	 * The user id record cannot be empty
	 * or
	 * SessionId cannot be null or empty
	 */
	public CompletableFuture<Boolean> insertNewSessionAsync(String userid, String sessionid) throws IllegalArgumentException
	{
		CompletableFuture<Boolean> future = new CompletableFuture<Boolean>();
		service.submit(()->
		{
			try
			{
				if(userid==null)
				{
					throw new IllegalArgumentException("The User id cannot be null");
				}
				else if(StringUtils.isEmpty(userid))
				{
					throw new IllegalArgumentException("The user id record cannot be empty");
				}
				else if(sessionid==null || StringUtils.isEmpty(sessionid))
				{
					throw new IllegalArgumentException("SessionId cannot be null or empty");
				}
				
				boolean isRecordInserted = insertNewSession(userid, sessionid);
				if (isRecordInserted != false)
				{
					future.complete(isRecordInserted);
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
