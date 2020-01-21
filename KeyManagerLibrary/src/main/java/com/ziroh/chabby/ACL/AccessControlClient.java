package com.ziroh.chabby.ACL;

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

/**
 * Access Control Client
 * 
 * see also ref="IAccessControl"
 *
 */
public class AccessControlClient implements IAccessControl
{
	String AccessControlClientUrl = "http://localhost:53010/accesscontrol";

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
	public AccessControlClient(String sessionID,String userId)
	{

		this.sessionId = sessionID;
		this.userId = userId;
	}

	static final int iCPU = Runtime.getRuntime().availableProcessors();
	static ExecutorService service = Executors.newFixedThreadPool(iCPU);

	
	public Result addResource(String resourceId)
	{
		Client addResourceClient = Client.create();
		String addResourceUrl = AccessControlClientUrl + "/resource";
		WebResource addResource = addResourceClient.resource(addResourceUrl);
		Result result = new Result();
		try
		{
			if(resourceId==null)
			{
				throw new IllegalArgumentException("ResourceID cannot be null");
			}
			else if(StringUtils.isEmpty(resourceId))
			{
				throw new IllegalArgumentException("ResourceID cannot be empty");
			}
			
			Gson gson = new GsonBuilder().create();
			String resourceIdInJson = gson.toJson(resourceId);
			ClientResponse httpResponse = addResource.accept("application/json").type("application/json")
					.header("sessionId", getSessionId()).header("userId", getUserId())
					.post(ClientResponse.class, resourceIdInJson);
			if(httpResponse.getStatus()==200)
			{
				String responseBody = httpResponse.getEntity(String.class);
				result = gson.fromJson(responseBody, com.ziroh.chabby.operationalResults.Result.class);
			}
			else
			{
				result = new Result();
				result.seterror_code(1);
				result.seterror_message("error in connecting to the server");
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
			addResourceClient = null;
		}
		return result;
	}

	/**
	 * Add resource
	 * @param resourceId The resource identifier.
	 * @return Result object
	 * @throws IllegalArgumentException
	 * ResourceID cannot be null
	 * or
	 * ResourceID cannot be empty
	 */
	public CompletableFuture<Result> addResourceAsync(String resourceId) throws IllegalArgumentException
	{
		CompletableFuture<Result> future = new CompletableFuture<Result>();
		service.submit(()->
		{
			try
			{
				if(resourceId==null)
				{
					throw new IllegalArgumentException("ResourceID cannot be null");
				}
				else if(StringUtils.isEmpty(resourceId))
				{
					throw new IllegalArgumentException("ResourceID cannot be empty");
				}
				Result result = addResource(resourceId);
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


	public Result addUser(String resourceId, String userId) 
	{
		Client addUserClient = Client.create();
		String addUserUrl = AccessControlClientUrl + "/resource/user/" +resourceId;
		WebResource addUserResource = addUserClient.resource(addUserUrl);
		Result result = new Result();
		try
		{
			if(resourceId==null || StringUtils.isEmpty(resourceId))
			{
				throw new IllegalArgumentException("ResourceID cannot be null");
			}
			else if(userId==null || StringUtils.isEmpty(userId))
			{
				throw new IllegalArgumentException("USerID cannot be empty");
			}
			
			Gson gson = new GsonBuilder().create();
			String userIDInJson = gson.toJson(userId);
			ClientResponse httpResponse = addUserResource.accept("application/json").type("application/json")
					.header("sessionId", getSessionId()).header("userId", getUserId())
					.post(ClientResponse.class, userIDInJson);
			if(httpResponse.getStatus()==200)
			{
				String responseBody = httpResponse.getEntity(String.class);
				result = gson.fromJson(responseBody, com.ziroh.chabby.operationalResults.Result.class);
			}
			else
			{
				result = new Result();
				result.seterror_code(1);
				result.seterror_message("error in connecting to the server");
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
			addUserClient = null;
		}
		return result;
	}

	
	public CompletableFuture<Result> addUserAsync(String resourceId, String userId) throws IllegalArgumentException
	{
		CompletableFuture<Result> future = new CompletableFuture<Result>();
		service.submit(()->
		{
			try
			{
				if(resourceId==null || StringUtils.isEmpty(resourceId))
				{
					throw new IllegalArgumentException("ResourceID cannot be null");
				}
				else if(userId==null || StringUtils.isEmpty(userId))
				{
					throw new IllegalArgumentException("USerID cannot be empty");
				}
				Result result = addUser(resourceId, userId);
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

	
	public Result addUserList(String resourceId, String[] userId)
	{
		Client addUserListClient = Client.create();
		String addUserListUrl = AccessControlClientUrl + "/resource/users/" +resourceId;
		WebResource addUserListResource = addUserListClient.resource(addUserListUrl);
		Result result = new Result();
		try
		{
			if(resourceId==null || StringUtils.isEmpty(resourceId))
			{
				throw new IllegalArgumentException("ResourceID cannot be null");
			}
			else if(userId==null)
			{
				throw new IllegalArgumentException("UserID cannot be empty");
			}
			
			Gson gson = new GsonBuilder().create();
			String userIDInJson = gson.toJson(userId);
			ClientResponse httpResponse = addUserListResource.accept("application/json").type("application/json")
					.header("sessionId", getSessionId()).header("userId", getUserId())
					.post(ClientResponse.class, userIDInJson);
			if(httpResponse.getStatus()==200)
			{
				String responseBody = httpResponse.getEntity(String.class);
				result = gson.fromJson(responseBody, com.ziroh.chabby.operationalResults.Result.class);
			}
			else
			{
				result = new Result();
				result.seterror_code(1);
				result.seterror_message("error in connecting to the server");
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
			addUserListClient = null;
		}
		return result;
	}

	/**
	 * Add user
	 * @param resourceId Resource identifier
	 * @param userId User identifier
	 * @return Result object
	 * @throws IllegalArgumentException
	 * ResourceID cannot be null
	 * or
	 * ResourceID cannot be empty
	 */
	public CompletableFuture<Result> addUserListAsync(String resourceId, String[] userId) throws IllegalArgumentException
	{
		CompletableFuture<Result> future = new CompletableFuture<Result>();
		service.submit(()->
		{
			try
			{
				if(resourceId==null || StringUtils.isEmpty(resourceId))
				{
					throw new IllegalArgumentException("ResourceID cannot be null");
				}
				else if(userId==null)
				{
					throw new IllegalArgumentException("UserID cannot be empty");
				}
				Result result = addUserList(resourceId, userId);
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
	
	
	public Result addNewResourceUserList(ResourceUserWrapper resourceUser) 
	{
		
		Client addNewResourceUserListClient = Client.create();
		String addNewResourceUserListUrl = AccessControlClientUrl + "/resource/users";
		WebResource addNewResourceUserListResource = addNewResourceUserListClient.resource(addNewResourceUserListUrl);
		Result result = new Result();
		try
		{
			if(resourceUser==null)
			{
				throw new IllegalArgumentException("ResourceUser cannot be null");
			}
			else if(resourceUser.ResourceId==null || StringUtils.isEmpty(resourceUser.ResourceId))
			{
				throw new IllegalArgumentException("ResourceID cannot be null or empty");
			}
			else if(resourceUser.Users==null )
			{
				throw new IllegalArgumentException("Users cannot be null or empty");
			}

			
			Gson gson = new GsonBuilder().create();
			String resourceUserInJson = gson.toJson(resourceUser);
			ClientResponse httpResponse = addNewResourceUserListResource.accept("application/json").type("application/json")
					.header("sessionId", getSessionId()).header("userId", getUserId())
					.post(ClientResponse.class, resourceUserInJson);
			if(httpResponse.getStatus()==200)
			{
				String responseBody = httpResponse.getEntity(String.class);
				result = gson.fromJson(responseBody, com.ziroh.chabby.operationalResults.Result.class);
			}
			else
			{
				result = new Result();
				result.seterror_code(1);
				result.seterror_message("error in connecting to the server");
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
			addNewResourceUserListClient = null;
		}
		return result;
	}

	/**
	 * Add new resource to the userList
	 * @param resourceUser ResourceUserWrapper is required
	 * @return Result object
	 * @throws IllegalArgumentException
	 * ResourceUser cannot be null
	 * or
	 * ResourceID cannot be null or empty
	 * or
	 * Users cannot be null or empty
	 */
	public CompletableFuture<Result> addNewResourceUserListAsync(ResourceUserWrapper resourceUser) throws IllegalArgumentException
	{
		CompletableFuture<Result> future = new CompletableFuture<Result>();
		service.submit(()->
		{
			try
			{
				if(resourceUser==null)
				{
					throw new IllegalArgumentException("ResourceUser cannot be null");
				}
				else if(resourceUser.ResourceId==null || StringUtils.isEmpty(resourceUser.ResourceId))
				{
					throw new IllegalArgumentException("ResourceID cannot be null or empty");
				}
				else if(resourceUser.Users==null )
				{
					throw new IllegalArgumentException("Users cannot be null or empty");
				}

				Result result = addNewResourceUserList(resourceUser);
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

	public Result deleteResource(String resourceId)
	{
		Client deleteResourceClient = Client.create();
		String deleteResourceUrl = AccessControlClientUrl + "/resource/" +resourceId;
		WebResource deleteResource = deleteResourceClient.resource(deleteResourceUrl);
		Result result = new Result();
		try
		{
			if(resourceId==null || StringUtils.isEmpty(resourceId))
			{
				throw new IllegalArgumentException("ResourceID cannot be null or empty");
			}
			
			ClientResponse httpResponse = deleteResource.accept("application/json").type("application/json")
					.header("sessionId", getSessionId()).header("userId", getUserId())
					.post(ClientResponse.class);
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
				result.seterror_message("error in connecting to the server");
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
			deleteResourceClient = null;
		}
		return result;
	}

	/**
	 * Delete the resource
	 * @param resourceId ResourceId is required
	 * @return Result object 
	 * @throws IllegalArgumentException
	 * ResourceId cannot be null or empty
	 */
	public CompletableFuture<Result> deleteResourceAsync(String resourceId) throws IllegalArgumentException
	{
		CompletableFuture<Result> future = new CompletableFuture<Result>();
		service.submit(()->
		{
			try
			{
				if(resourceId==null || StringUtils.isEmpty(resourceId))
				{
					throw new IllegalArgumentException("ResourceID cannot be null or empty");
				}

				Result result = deleteResource(resourceId);
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

	public Result deleteUser(ResourceUserWrapper resourceUser) 
	{
		Client deleteUserClient = Client.create();
		String deleteUserUrl = AccessControlClientUrl + "/resource/users/deletion";
		WebResource deleteUserResource = deleteUserClient.resource(deleteUserUrl);
		Result result = new Result();
		try
		{
			if(resourceUser==null)
			{
				throw new IllegalArgumentException("ResourceUser cannot be null");
			}
			else if(resourceUser.ResourceId==null || StringUtils.isEmpty(resourceUser.ResourceId))
			{
				throw new IllegalArgumentException("ResourceID cannot be null or empty");
			}
			else if(resourceUser.Users==null )
			{
				throw new IllegalArgumentException("Users cannot be null or empty");
			}
			
			ClientResponse httpResponse = deleteUserResource.accept("application/json").type("application/json")
					.header("sessionId", getSessionId()).header("userId", getUserId())
					.post(ClientResponse.class);
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
				result.seterror_message("error in connecting to the server");
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
	 * @param resourceUser ResourceUserWrapper is required
	 * @return Result object 
	 * @throws IllegalArgumentException
	 * ResourceUser cannot be null
	 * or
	 * ResourceID cannot be null or empty
	 * or
	 * Users cannot be null or empty
	 */
	public CompletableFuture<Result> deleteUserAsync(ResourceUserWrapper resourceUser) throws IllegalArgumentException
	{
		CompletableFuture<Result> future = new CompletableFuture<Result>();
		service.submit(()->
		{
			try
			{
				if(resourceUser==null)
				{
					throw new IllegalArgumentException("ResourceUser cannot be null");
				}
				else if(resourceUser.ResourceId==null || StringUtils.isEmpty(resourceUser.ResourceId))
				{
					throw new IllegalArgumentException("ResourceID cannot be null or empty");
				}
				else if(resourceUser.Users==null )
				{
					throw new IllegalArgumentException("Users cannot be null or empty");
				}

				Result result = deleteUser(resourceUser);
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

	public String[] getUserList(String resourceId)
	{
		Client getUserListClient = Client.create();
		String getUserListUrl = AccessControlClientUrl + "/resource/" +resourceId;
		WebResource getUserListResource = getUserListClient.resource(getUserListUrl);
		String[] result = null;
		try
		{
			if(resourceId==null)
			{
				throw new IllegalArgumentException("The resource id of a record cannot be null");
			}
			else if(StringUtils.isEmpty(resourceId))
			{
				throw new IllegalArgumentException("The resource id of a record cannot be empty");
			}
			
			ClientResponse httpResponse = getUserListResource.accept("application/json").type("application/json")
					.header("sessionId", getSessionId()).header("userId", getUserId())
					.get(ClientResponse.class);
			if(httpResponse.getStatus()==200)
			{
				String responseBody = httpResponse.getEntity(String.class);
				Gson gson = new GsonBuilder().create();
				result = gson.fromJson(responseBody, String[].class);
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
			getUserListClient = null;
		}
		return result;		 
	}

	/**
	 * Get the userList
	 * @param resourceId ResourceID is required
	 * @return String array
	 * @throws IllegalArgumentException
	 * The resource id of a record cannot be null
	 * or
	 * The resource id of a record cannot be empty
	 */
	public CompletableFuture<String[]> getUserListAsync(String resourceId) throws IllegalArgumentException
	{
		CompletableFuture<String[]> future = new CompletableFuture<String[]>();
		service.submit(()->
		{
			try
			{
				if(resourceId==null)
				{
					throw new IllegalArgumentException("The resource id of a record cannot be null");
				}
				else if(StringUtils.isEmpty(resourceId))
				{
					throw new IllegalArgumentException("The resource id of a record cannot be empty");
				}

				String[] result = getUserList(resourceId);
				if (result!= null)
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

	public String getAccess(String resourceId, String userId) 
	{
		Client getAccessClient = Client.create();
		String getAccessUrl = AccessControlClientUrl + "/UserAccess/" +resourceId +"/" +userId;
		WebResource getAccessResource = getAccessClient.resource(getAccessUrl);
		String result = null;
		try
		{
			if(resourceId==null)
			{
				throw new IllegalArgumentException("The resource id of a record cannot be null");
			}
			else if(StringUtils.isEmpty(resourceId))
			{
				throw new IllegalArgumentException("The resource id of a record cannot be empty");
			}
			else if(userId==null || StringUtils.isEmpty(userId))
			{
				throw new IllegalArgumentException("UserId cannot be null or empty");
			}

			
			ClientResponse httpResponse = getAccessResource.accept("application/json").type("application/json")
					.header("sessionId", getSessionId()).header("userId", getUserId())
					.get(ClientResponse.class);
			if(httpResponse.getStatus()==200)
			{
				String responseBody = httpResponse.getEntity(String.class);
				Gson gson = new GsonBuilder().create();
				result = gson.fromJson(responseBody, String.class);
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
			getAccessClient = null;
		}
		return result;
	}
	
	/**
	 * Get the access
	 * @param resourceId Resource identifier is required
	 * @param userId UserID is required
	 * @return String
	 * @throws IllegalArgumentException
	 * The resource id of a record cannot be null
	 * or
	 * The resource id of a record cannot be empty
	 * or
	 * UserId cannot be null or empty
	 */
	public CompletableFuture<String> getAccessAsync(String resourceId, String userId) throws IllegalArgumentException
	{
		CompletableFuture<String> future = new CompletableFuture<String>();
		service.submit(()->
		{
			try
			{
				if(resourceId==null)
				{
					throw new IllegalArgumentException("The resource id of a record cannot be null");
				}
				else if(StringUtils.isEmpty(resourceId))
				{
					throw new IllegalArgumentException("The resource id of a record cannot be empty");
				}
				else if(userId==null || StringUtils.isEmpty(userId))
				{
					throw new IllegalArgumentException("UserId cannot be null or empty");
				}

				String result = getAccess(resourceId, userId);
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

}
