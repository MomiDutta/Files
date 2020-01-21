package com.ziroh.chabby.keyStore.client;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.ziroh.chabby.keyStore.common.KeyRecord;
import com.ziroh.chabby.operationalResults.Result;
import com.ziroh.chabby.utils.JSONCustomSerializer;

/**
 * KeyStore Client implements all methods to do IO with the KeyStore server
 */
public class KeyStoreClient
{
	/**
	 * Base Url
	 */
	String KeyStoreUrl = "http://localhost:53009";
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
	public void setUserid(String userId) 
	{
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
	public void setSessionid(String sessionId) 
	{
		this.sessionId = sessionId;
	}

	/**
	 * KeyStoreClient constructor
	 * @param userId User identifier
	 * @param sessionID Session identifier
	 */
	public KeyStoreClient(String sessionID,String userId)
	{

		this.sessionId = sessionID;
		this.userId = userId;
	}

	static final int iCPU = Runtime.getRuntime().availableProcessors();
	static ExecutorService service = Executors.newFixedThreadPool(iCPU);

	public KeyRecord getAllKeyRecordsUniqueId(String uniqueId)
	{
		Client getAllKeyRecordsUniqueIdClient = Client.create();
		String getAllKeyRecordsUniqueIdUrl = KeyStoreUrl + "/keystore/" +uniqueId;
		WebResource getAllKeyRecordsUniqueIdResource = getAllKeyRecordsUniqueIdClient.resource(getAllKeyRecordsUniqueIdUrl);
		KeyRecord uniqueRecord = null;
		try
		{
			if(uniqueId==null)
			{
				throw new IllegalArgumentException("The unique id of a record cannot be null");
			}
			else if(StringUtils.isEmpty(uniqueId))
			{
				throw new IllegalArgumentException("The unique id of a record cannot be empty");
			}


			
			GsonBuilder builder = new GsonBuilder();
			builder.serializeNulls();
			builder.registerTypeHierarchyAdapter(byte[].class, new JSONCustomSerializer());	
			Gson gson=builder.create();
			
			ClientResponse httpResponse = getAllKeyRecordsUniqueIdResource.accept("application/json").type("application/json")
					.header("sessionId", getSessionId()).header("userId", getUserId())
					.get(ClientResponse.class);
			if(httpResponse.getStatus()==200)
			{
				String responseBody = httpResponse.getEntity(String.class);
				uniqueRecord = gson.fromJson(responseBody, com.ziroh.chabby.keyStore.common.KeyRecord.class);
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
			getAllKeyRecordsUniqueIdClient = null;
		}
		return uniqueRecord;		 
	}

	/**
	 * Gets the key record by unique identifier.
	 * @param uniqueId Unique identifier
	 * @return KeyRecord object
	 * @throws IllegalArgumentException
	 * The unique id of a record cannot be null
	 * or
	 * The unique id of a record cannot be empty
	 */
	public CompletableFuture<KeyRecord> getAllKeyRecordsUniqueIdAsync(String uniqueId) throws IllegalArgumentException
	{
		CompletableFuture<KeyRecord> future = new CompletableFuture<KeyRecord>();
		service.submit(()->
		{
			try
			{
				if(uniqueId==null)
				{
					throw new IllegalArgumentException("The unique id of a record cannot be null");
				}
				else if(StringUtils.isEmpty(uniqueId))
				{
					throw new IllegalArgumentException("The unique id of a record cannot be empty");
				}


				KeyRecord uniqueRecord = getAllKeyRecordsUniqueId(uniqueId);
				if (uniqueRecord != null)
				{
					future.complete(uniqueRecord);
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

	public KeyRecord[] getKeyRecordsByResource(String userId, String resourceId) 
	{
		Client getKeyRecordsByResourceClient = Client.create();
		String getKeyRecordsByResourceUrl = KeyStoreUrl + "/keystore/" +userId +"/" +resourceId;
		WebResource getKeyRecordsByResource = getKeyRecordsByResourceClient.resource(getKeyRecordsByResourceUrl);
		KeyRecord[] keyRecordCollections = null;
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
			else if(resourceId == null || StringUtils.isEmpty(resourceId))
			{
				throw new IllegalArgumentException("Resource identifier cannot be empty");
			}
			
			GsonBuilder builder = new GsonBuilder();
			builder.serializeNulls();
			builder.registerTypeHierarchyAdapter(byte[].class, new JSONCustomSerializer());	
			Gson gson=builder.create();
			
			ClientResponse httpResponse = getKeyRecordsByResource.accept("application/json").type("application/json")
					.header("sessionId", getSessionId()).header("userId", getUserId()).get(ClientResponse.class);
			if(httpResponse.getStatus()==200)
			{
				String responseBody = httpResponse.getEntity(String.class);
				keyRecordCollections = gson.fromJson(responseBody, com.ziroh.chabby.keyStore.common.KeyRecord[].class);
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
			getKeyRecordsByResourceClient = null;
		}
		return keyRecordCollections;
	}

	/**
	 * Gets the key records by resource.
	 * @param userId User identifier
	 * @param resourceId resource identifier
	 * @return Array of KeyRecords
	 * @throws IllegalArgumentException
	 * User identifier cannot be null
	 * or
	 * User identifier cannot be empty
	 * or
	 * Resource identifier cannot be empty
	 */
	public CompletableFuture<KeyRecord[]> getKeyRecordsByResourceAsync(String userId, String resourceId) throws IllegalArgumentException
	{
		CompletableFuture<KeyRecord[]> future = new CompletableFuture<KeyRecord[]>();
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
				else if(resourceId == null || StringUtils.isEmpty(resourceId))
				{
					throw new IllegalArgumentException("Resource identifier cannot be empty");
				}

				KeyRecord[] uniqueRecord = getKeyRecordsByResource(userId, resourceId);
				if (uniqueRecord != null)
				{
					future.complete(uniqueRecord);
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


	public KeyRecord[] getAllValidKeys(String userId) 
	{
		Client getAllValidKeysClient = Client.create();
		String getAllValidKeysUrl = KeyStoreUrl + "/keystore/" +userId +"/validkeys";
		WebResource getAllValidKeysResource = getAllValidKeysClient.resource(getAllValidKeysUrl);
		KeyRecord[] keyRecordCollections = null;
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
			
			GsonBuilder builder = new GsonBuilder();
			builder.serializeNulls();
			builder.registerTypeHierarchyAdapter(byte[].class, new JSONCustomSerializer());	
			Gson gson=builder.create();
			
			ClientResponse httpResponse = getAllValidKeysResource.accept("application/json").type("application/json")
					.header("sessionId", getSessionId()).header("userId", getUserId())
					.get(ClientResponse.class);
			if(httpResponse.getStatus()==200)
			{
				String responseBody = httpResponse.getEntity(String.class);
				keyRecordCollections = gson.fromJson(responseBody, com.ziroh.chabby.keyStore.common.KeyRecord[].class);
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
			getAllValidKeysClient = null;
		}
		return keyRecordCollections;
	}

	/**
	 * Get all valid keys
	 * @param userId User identifier
	 * @return Array of KeyRecords
	 * @throws IllegalArgumentException
	 * User identifier cannot be null
	 * or
	 * User identifier cannot be empty
	 */
	public CompletableFuture<KeyRecord[]> getAllValidKeysAsync(String userId) throws IllegalArgumentException
	{
		CompletableFuture<KeyRecord[]> future = new CompletableFuture<KeyRecord[]>();
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

				KeyRecord[] keyRecordCollections = getAllValidKeys(userId);
				if (keyRecordCollections != null)
				{
					future.complete(keyRecordCollections);
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

	public KeyRecord[] getAllExpiredKeys(String userId)
	{
		Client getAllExpiredKeysClient = Client.create();
		String getAllExpiredKeysUrl = KeyStoreUrl + "/keystore/" +userId +"/expiredkeys";
		WebResource getAllExpiredKeysResource = getAllExpiredKeysClient.resource(getAllExpiredKeysUrl);
		KeyRecord[] keyRecordCollections = null;
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
			
			GsonBuilder builder = new GsonBuilder();
			builder.serializeNulls();
			builder.registerTypeHierarchyAdapter(byte[].class, new JSONCustomSerializer());	
			Gson gson=builder.create();
			
			ClientResponse httpResponse = getAllExpiredKeysResource.accept("application/json").type("application/json")
					.header("sessionId", getSessionId()).header("userId", getUserId())
					.get(ClientResponse.class);
			if(httpResponse.getStatus()==200)
			{
				String responseBody = httpResponse.getEntity(String.class);
				keyRecordCollections = gson.fromJson(responseBody, com.ziroh.chabby.keyStore.common.KeyRecord[].class);
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
			getAllExpiredKeysClient = null;
		}
		return keyRecordCollections;
	}

	/**
	 * Get all expired keys
	 * @param userId User identifier
	 * @return Array of KeyRecords
	 * @throws IllegalArgumentException
	 * User identifier cannot be null
	 * or
	 * User identifier cannot be empty
	 */
	public CompletableFuture<KeyRecord[]> getAllExpiredKeysAsync(String userId) throws IllegalArgumentException
	{
		CompletableFuture<KeyRecord[]> future = new CompletableFuture<KeyRecord[]>();
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

				KeyRecord[] keyRecordCollections = getAllExpiredKeys(userId);
				if (keyRecordCollections != null)
				{
					future.complete(keyRecordCollections);
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

	public KeyRecord[] getAllKeyRecord() 
	{
		Client getAllKeyRecordClient = Client.create();
		String getAllKeyRecordUrl = KeyStoreUrl + "/keystore/all";
		WebResource getAllKeyRecordResource = getAllKeyRecordClient.resource(getAllKeyRecordUrl);
		KeyRecord[] keyRecordCollections = null;
		try
		{
			GsonBuilder builder = new GsonBuilder();
			builder.serializeNulls();
			builder.registerTypeHierarchyAdapter(byte[].class, new JSONCustomSerializer());	
			Gson gson=builder.create();
			
			ClientResponse httpResponse = getAllKeyRecordResource.accept("application/json").type("application/json")
					.header("sessionId", getSessionId()).header("userId", getSessionId()).get(ClientResponse.class);
			if(httpResponse.getStatus()==200)
			{
				String responseBody = httpResponse.getEntity(String.class);
				keyRecordCollections = gson.fromJson(responseBody, com.ziroh.chabby.keyStore.common.KeyRecord[].class);
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
			getAllKeyRecordClient = null;
		}
		return keyRecordCollections;
	}

	/**
	 * Get all key records
	 * @return Array of KeyRecords
	 */
	public CompletableFuture<KeyRecord[]> getAllKeyRecordAsync() 
	{
		CompletableFuture<KeyRecord[]> future = new CompletableFuture<KeyRecord[]>();
		service.submit(()->
		{
			try
			{			
				KeyRecord[] keyRecordCollections = getAllKeyRecord();
				if (keyRecordCollections != null)
				{
					future.complete(keyRecordCollections);
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


	public KeyRecord[] getKeyRecordbyUserId(String userId) 
	{
		Client getKeyRecordbyUserIdClient = Client.create();
		String getKeyRecordbyUserIdUrl = KeyStoreUrl + "/keystore/" +userId +"/all";
		WebResource getKeyRecordbyUserIdResource = getKeyRecordbyUserIdClient.resource(getKeyRecordbyUserIdUrl);
		KeyRecord[] keyRecordCollections = null;
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
			
			GsonBuilder builder = new GsonBuilder();
			builder.serializeNulls();
			builder.registerTypeHierarchyAdapter(byte[].class, new JSONCustomSerializer());	
			Gson gson=builder.create();
			
			ClientResponse httpResponse = getKeyRecordbyUserIdResource.accept("application/json").type("application/json")
					.header("sessionId", getSessionId()).header("userId", getUserId())
					.get(ClientResponse.class);
			if(httpResponse.getStatus()==200)
			{
				String responseBody = httpResponse.getEntity(String.class);
				keyRecordCollections = gson.fromJson(responseBody, com.ziroh.chabby.keyStore.common.KeyRecord[].class);
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
			getKeyRecordbyUserIdClient = null;
		}
		return keyRecordCollections;
	}

	/**
	 * Gets the key record by user identifier.
	 * @param userId UserID is required
	 * @return Array of KeyRecords
	 * @throws IllegalArgumentException
	 * User identifier cannot be null
	 * or
	 * User identifier cannot be empty
	 */
	public CompletableFuture<KeyRecord[]> getKeyRecordbyUserIdAsync(String userId) throws IllegalArgumentException
	{
		CompletableFuture<KeyRecord[]> future = new CompletableFuture<KeyRecord[]>();
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

				KeyRecord[] keyRecordCollections = getKeyRecordbyUserId(userId);
				if (keyRecordCollections != null)
				{
					future.complete(keyRecordCollections);
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


	public KeyRecord[] getKeyRecordByUserIdDateTime(String userId, String startDate, String enddate) 
	{
		Client getKeyRecordByUserIdDateTimeClient = Client.create();
		String getKeyRecordByUserIdDateTimeUrl = KeyStoreUrl + "/keystore/" +userId +"?fromdate=" +startDate +"&enddate=" +enddate;
		WebResource getKeyRecordByUserIdDateTimeResource = getKeyRecordByUserIdDateTimeClient.resource(getKeyRecordByUserIdDateTimeUrl);
		KeyRecord[] keyRecordCollections = null;
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
			else if(startDate == null || StringUtils.isEmpty(startDate))
			{
				throw new IllegalArgumentException("StartDate cannot be null or empty");
			}
			else if(enddate == null || StringUtils.isEmpty(enddate))
			{
				throw new IllegalArgumentException("EndDate cannot be null or empty");
			}

			
			GsonBuilder builder = new GsonBuilder();
			builder.serializeNulls();
			builder.registerTypeHierarchyAdapter(byte[].class, new JSONCustomSerializer());	
			Gson gson=builder.create();
			
			ClientResponse httpResponse = getKeyRecordByUserIdDateTimeResource.accept("application/json").type("application/json")
					.header("sessionId", getSessionId()).header("userId", getUserId())
					.get(ClientResponse.class);
			if(httpResponse.getStatus()==200)
			{
				String responseBody = httpResponse.getEntity(String.class);
				keyRecordCollections = gson.fromJson(responseBody, com.ziroh.chabby.keyStore.common.KeyRecord[].class);
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
			getKeyRecordByUserIdDateTimeClient = null;
		}
		return keyRecordCollections;
	}

	/**
	 * Gets the key record by user identifier date time.
	 * @param userId UserID is required
	 * @param startDate start date
	 * @param enddate end date
	 * @return Array of KeyRecords
	 * @throws IllegalArgumentException
	 * User identifier cannot be null
	 * or
	 * User identifier cannot be empty
	 * or
	 * StartDate cannot be null or empty
	 * or
	 * EndDate cannot be null or empty
	 */
	public CompletableFuture<KeyRecord[]> getKeyRecordByUserIdDateTimeAsync(String userId, String startDate, String enddate) throws IllegalArgumentException
	{
		CompletableFuture<KeyRecord[]> future = new CompletableFuture<KeyRecord[]>();
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
				else if(startDate == null || StringUtils.isEmpty(startDate))
				{
					throw new IllegalArgumentException("StartDate cannot be null or empty");
				}
				else if(enddate == null || StringUtils.isEmpty(enddate))
				{
					throw new IllegalArgumentException("EndDate cannot be null or empty");
				}

				KeyRecord[] keyRecordCollections = getKeyRecordByUserIdDateTime(userId, startDate, enddate);
				if (keyRecordCollections != null)
				{
					future.complete(keyRecordCollections);
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


	public KeyRecord[] getKeyRecordByDecription(String userId, String description) 
	{
		Client getKeyRecordByDecriptionClient = Client.create();

		String getKeyRecordByDecriptionUrl = KeyStoreUrl + "/keystore/" +userId +"/all";;

		WebResource getKeyRecordByDecriptionResource = getKeyRecordByDecriptionClient.resource(getKeyRecordByDecriptionUrl);

		KeyRecord[] keyRecordCollections = null;

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
			else if(description == null || StringUtils.isEmpty(description))
			{
				throw new IllegalArgumentException("Description cannot be null or empty");
			}

			
			MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
			queryParams.add("match", description);
			ClientResponse httpResponse = getKeyRecordByDecriptionResource.queryParams(queryParams)
					.accept("application/json").type("application/json")
					.header("Accept", "Content-Type: application/json;charset=utf-8")
					.header("userId", getUserId()).header("sessionId", getSessionId())
					.get(ClientResponse.class);

			if(httpResponse.getStatus()==200)
			{
				String responseBody = httpResponse.getEntity(String.class);
				Gson gson = new GsonBuilder().create();										
				keyRecordCollections = gson.fromJson(responseBody, KeyRecord[].class);
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
		catch (IllegalStateException | JsonSyntaxException je) 
		{
			je.printStackTrace();
		} 
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			getKeyRecordByDecriptionClient = null;
		}
		return keyRecordCollections;
	}

	/**
	 * Get the key records by description
	 * @param userId UserID is required
	 * @param description Description is required
	 * @return Array of KeyRecords
	 * @throws IllegalArgumentException
	 * User identifier cannot be null
	 * or
	 * User identifier cannot be empty
	 * or
	 * Description cannot be null or empty
	 */
	public CompletableFuture<KeyRecord[]> getKeyRecordByDecriptionAsync(String userId, String description) throws IllegalArgumentException
	{
		CompletableFuture<KeyRecord[]> future = new CompletableFuture<KeyRecord[]>();
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
				else if(description == null || StringUtils.isEmpty(description))
				{
					throw new IllegalArgumentException("Description cannot be null or empty");
				}

				KeyRecord[] keyRecordCollections = getKeyRecordByDecription(userId, description);
				if (keyRecordCollections != null)
				{
					future.complete(keyRecordCollections);
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


	public Result insertNewRecord(KeyRecord record) 
	{

		Client insertNewRecordClient = Client.create();
		String insertNewRecordUrl = KeyStoreUrl + "/keystore";
		WebResource insertNewRecordResource = insertNewRecordClient.resource(insertNewRecordUrl);
		Result result = new Result();
		try
		{
			if(record==null)
			{
				throw new IllegalArgumentException("Record cannot be null");
			}
			else if(StringUtils.isEmpty(record.ResourceId))
			{
				throw new IllegalArgumentException("ResourceId cannot be empty");
			}
			else if(StringUtils.isEmpty(record.Uniqueid))
			{
				throw new IllegalArgumentException("UniqueId cannot be empty");
			}
			else if(StringUtils.isEmpty(record.UserId))
			{
				throw new IllegalArgumentException("UserId cannot be empty");
			}
			
			GsonBuilder builder = new GsonBuilder();
			builder.serializeNulls();
			builder.registerTypeHierarchyAdapter(byte[].class, new JSONCustomSerializer());	
			Gson gson=builder.create();
			String recordInJson = gson.toJson(record);

			ClientResponse httpResponse = insertNewRecordResource.accept("application/json").type("application/json")
					.header("Accept", "Content-Type: application/json;charset=utf-8")
					.header("sessionId", getSessionId()).header("userId", getUserId())
					.post(ClientResponse.class, recordInJson);
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
			insertNewRecordClient = null;
		}
		return result;
	}


	/**
	 * Insert new record
	 * @param record Record is required
	 * @return Result object
	 * @throws IllegalArgumentException
	 * Record cannot be null
	 * or
	 * ResourceId cannot be empty
	 * or
	 * UniqueId cannot be empty
	 * or
	 * UserId cannot be empty
	 */
	public CompletableFuture<Result> insertNewRecordAsync(KeyRecord record) throws IllegalArgumentException
	{
		CompletableFuture<Result> future = new CompletableFuture<Result>();
		service.submit(()->
		{
			try
			{		
				if(record==null)
				{
					throw new IllegalArgumentException("Record cannot be null");
				}
				else if(StringUtils.isEmpty(record.ResourceId))
				{
					throw new IllegalArgumentException("ResourceId cannot be empty");
				}
				else if(StringUtils.isEmpty(record.Uniqueid))
				{
					throw new IllegalArgumentException("UniqueId cannot be empty");
				}
				else if(StringUtils.isEmpty(record.UserId))
				{
					throw new IllegalArgumentException("UserId cannot be empty");
				}

				Result result = insertNewRecord(record);
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


	public Result deleteKey(String uniqueId) 
	{
		Client deleteKeyClient = Client.create();
		String deleteKeyUrl = KeyStoreUrl + "/keystore/" +uniqueId;
		WebResource deleteKeyUrlResource = deleteKeyClient.resource(deleteKeyUrl);
		Result result = new Result();
		try
		{
			if(uniqueId==null)
			{
				throw new IllegalArgumentException("User identifier cannot be null");
			}
			else if(StringUtils.isEmpty(uniqueId))
			{
				throw new IllegalArgumentException("User identifier cannot be empty");
			}
			
			ClientResponse httpResponse = deleteKeyUrlResource.accept("application/json").type("application/json")
					.header("sessionId", sessionId).header("userId", userId).delete(ClientResponse.class);
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
			deleteKeyClient = null;
		}
		return result;
	}

	/**
	 * Delete the key
	 * @param uniqueId UniqueID is required
	 * @return Result object
	 * @throws IllegalArgumentException
	 * User identifier cannot be null
	 * or
	 * User identifier cannot be empty
	 */
	public CompletableFuture<Result> deleteKeyAsync(String uniqueId) throws IllegalArgumentException
	{
		CompletableFuture<Result> future = new CompletableFuture<Result>();
		service.submit(()->
		{
			try
			{		
				if(uniqueId==null)
				{
					throw new IllegalArgumentException("User identifier cannot be null");
				}
				else if(StringUtils.isEmpty(uniqueId))
				{
					throw new IllegalArgumentException("User identifier cannot be empty");
				}

				Result result = deleteKey(uniqueId);
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


	public Result updateKey(String uniqueId, KeyRecord record)
	{
		Client updateKeyClient = Client.create();
		String updateKeyUrl = KeyStoreUrl + "/keystore/" +uniqueId;
		WebResource updateKeyResource = updateKeyClient.resource(updateKeyUrl);
		Result result = new Result();
		try
		{
			if(uniqueId==null || StringUtils.isEmpty(uniqueId))
			{
				throw new IllegalArgumentException("UniqueId cannot be null or empty");
			}
			else if(record==null)
			{
				throw new IllegalArgumentException("User identifier cannot be null");
			}
			else if(StringUtils.isEmpty(record.ResourceId))
			{
				throw new IllegalArgumentException("ResourceId cannot be empty");
			}
			else if(StringUtils.isEmpty(record.Uniqueid))
			{
				throw new IllegalArgumentException("UniqueId cannot be empty");
			}
			else if(StringUtils.isEmpty(record.UserId))
			{
				throw new IllegalArgumentException("UserId cannot be empty");
			}

			
			Gson gson = new GsonBuilder().create();
			String recordInJson = gson.toJson(record);
			ClientResponse httpResponse = updateKeyResource.accept("application/json").type("application/json")
					.header("sessionId", sessionId).header("userId", userId).put(ClientResponse.class, recordInJson);
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
			updateKeyClient = null;
		}
		return result;
	}

	/**
	 * Update the key
	 * @param uniqueId UniqueID is required
	 * @param record Record is required
	 * @return Result object
	 * @throws IllegalArgumentException
	 * UniqueId cannot be null or empty
	 * or
	 * User identifier cannot be null
	 * or
	 * ResourceId cannot be empty
	 * or
	 * UniqueId cannot be empty
	 */
	public CompletableFuture<Result> updateKeyAsync(String uniqueId, KeyRecord record) throws IllegalArgumentException
	{
		CompletableFuture<Result> future = new CompletableFuture<Result>();
		service.submit(()->
		{
			try
			{		
				if(uniqueId==null || StringUtils.isEmpty(uniqueId))
				{
					throw new IllegalArgumentException("UniqueId cannot be null or empty");
				}
				else if(record==null)
				{
					throw new IllegalArgumentException("User identifier cannot be null");
				}
				else if(StringUtils.isEmpty(record.ResourceId))
				{
					throw new IllegalArgumentException("ResourceId cannot be empty");
				}
				else if(StringUtils.isEmpty(record.Uniqueid))
				{
					throw new IllegalArgumentException("UniqueId cannot be empty");
				}
				else if(StringUtils.isEmpty(record.UserId))
				{
					throw new IllegalArgumentException("UserId cannot be empty");
				}

				Result result = updateKey(uniqueId, record);
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
