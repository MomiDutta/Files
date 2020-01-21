package com.ziroh.chabby.audit.client;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.ziroh.chabby.audit.common.AuditMessage;
import com.ziroh.chabby.audit.common.AuditServerRequest;
import com.ziroh.chabby.audit.common.AuditServerResponse;
import com.ziroh.chabby.operationalResults.Result;

/**
 * This class is used to audit User's activity
 */
public class AuditClient 
{
	/**
	 * Base Url
	 */
	String baseUrl = "http://localhost:53021";
	
	/**
	 * User identifier
	 */
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
	 * @return SessionID 
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

	static final int iCPU = Runtime.getRuntime().availableProcessors();
	static ExecutorService service = Executors.newFixedThreadPool(iCPU);
	
	/**
	 * AuditClient constructor
	 * @param userId UserID
	 * @param sessionID SessionID
	 */
	public AuditClient(String userId, String sessionID)
	{
		this.userId = userId;
		this.sessionid = sessionID;
	}
	
	public Result addNewAuditLog(AuditMessage message) 
	{
		Client addNewAuditLogClient = Client.create();
		String addNewAuditLogUrl = baseUrl + "/AddAudit";
		WebResource addNewAuditLogResource = addNewAuditLogClient.resource(addNewAuditLogUrl);
		Result result = new Result();
		try
		{
			if(message==null)
			{
				throw new IllegalArgumentException("AuditMessage cannot be null");
			}
			
			Gson gson = new GsonBuilder().create();
			String messageInJSon = gson.toJson(message);
			ClientResponse httpResponse = addNewAuditLogResource.accept("application/json").type("application/json")
					.header("sessionId", sessionid).header("userId", userId)
					.post(ClientResponse.class, messageInJSon);
			if(httpResponse.getStatus()==200)
			{
				String responseBody = httpResponse.getEntity(String.class);
				result = gson.fromJson(responseBody, com.ziroh.chabby.operationalResults.Result.class);
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
			addNewAuditLogClient = null;
		}
		return result;		 
	}
	
	/**
	 * Adds new audit log
	 * @param message AuditMessage is required
	 * @return Result class
	 * @throws IllegalArgumentException
	 * AuditMessage cannot be null
	 */
	public CompletableFuture<Result> addNewAuditLogAsync(AuditMessage message) throws IllegalArgumentException
	{
		CompletableFuture<Result> future = new CompletableFuture<Result>();
		service.submit(()->
		{
			try
			{
				if(message==null)
				{
					throw new IllegalArgumentException("AuditMessage cannot be null");
				}
				
				Result result = addNewAuditLog(message);
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
	
	public AuditServerResponse getAudit(AuditServerRequest request)
	{
		Client getAuditClient = Client.create();
		String getAuditUrl = baseUrl + "/GetAudit";
		WebResource getAuditResource = getAuditClient.resource(getAuditUrl);
		AuditServerResponse result = new AuditServerResponse();
		try
		{
			if(request==null)
			{
				throw new IllegalArgumentException("AuditServerRequest cannot be null");
			}
			
			Gson gson = new GsonBuilder().create();
			String messageInJSon = gson.toJson(request);
			ClientResponse httpResponse = getAuditResource.accept("application/json").type("application/json")
					.header("sessionId", sessionid).header("userId", userId)
					.put(ClientResponse.class, messageInJSon);
			if(httpResponse.getStatus()==200)
			{
				String responseBody = httpResponse.getEntity(String.class);
				result = gson.fromJson(responseBody, com.ziroh.chabby.audit.common.AuditServerResponse.class);
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
			getAuditClient = null;
		}
		return result;		 
	}
	
	/**
	 * Get Audit
	 * @param request AuditServerRequest is required
	 * @return AuditServerResponse object
	 * @throws IllegalArgumentException
	 * AuditServerRequest cannot be null
	 */
	public CompletableFuture<AuditServerResponse> getAuditAsync(AuditServerRequest request) throws IllegalArgumentException
	{
		CompletableFuture<AuditServerResponse> future = new CompletableFuture<AuditServerResponse>();
		service.submit(()->
		{
			try
			{
				if(request==null)
				{
					throw new IllegalArgumentException("AuditServerRequest cannot be null");
				}
				
				AuditServerResponse result = getAudit(request);
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
}	
