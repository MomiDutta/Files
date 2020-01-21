package com.ziroh.chabby.groups.client;

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
import com.ziroh.chabby.groups.common.Group;
import com.ziroh.chabby.operationalResults.Result;

/**
 * This class represents group client
 */
public class GroupClient
{
	/**
	 * Base Url
	 */
	String groupClientUrl = "http://localhost:53005";

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
	public GroupClient(String sessionId, String userId)
	{
		this.sessionId = sessionId;
		this.userId = userId;
	}
	
	static final int iCPU = Runtime.getRuntime().availableProcessors();
	static ExecutorService service = Executors.newFixedThreadPool(iCPU);

	public Group getGroupByResourceid(String resourceId) 
	{
		Client getGroupByResourceidClient = Client.create();
		String getGroupByResourceidUrl = resourceId + "/group/" +resourceId;
		WebResource getGroupByResourceidResource = getGroupByResourceidClient.resource(getGroupByResourceidUrl);
		Group group = null;
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
			
			ClientResponse httpResponse = getGroupByResourceidResource.accept("application/json").type("application/json")
					.header("sessionId", getSessionId()).header("userId", getUserId())
					.get(ClientResponse.class);
			if(httpResponse.getStatus()==200)
			{
				String responseBody = httpResponse.getEntity(String.class);
				Gson gson = new GsonBuilder().create();
				group = gson.fromJson(responseBody, com.ziroh.chabby.groups.common.Group.class);
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
			getGroupByResourceidClient = null;
		}
		return group;		 
	}
	
	/**
	 * Get group by resourceID
	 * @param resourceId resourceID is required
	 * @return Group object
	 * @throws IllegalArgumentException
	 * The resource id of a record cannot be null
	 * or
	 * The resource id of a record cannot be empty
	 */
	public CompletableFuture<Group> getGroupByResourceidAsync(String resourceId) throws IllegalArgumentException
	{
		CompletableFuture<Group> future = new CompletableFuture<Group>();
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
				
				Group group = getGroupByResourceid(resourceId);
				if (group !=null)
				{
					future.complete(group);
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

	public Group getGroupById(String groupId) throws IllegalArgumentException
	{
		Client getGroupByIdClient = Client.create();
		String getGroupByIdUrl = groupClientUrl + "/group/" +groupId;
		WebResource getGroupByIdResource = getGroupByIdClient.resource(getGroupByIdUrl);
		Group group = null;
		try
		{
			if(groupId==null)
			{
				throw new IllegalArgumentException("The group id cannot be null");
			}
			else if(StringUtils.isEmpty(groupId))
			{
				throw new IllegalArgumentException("The group id cannot be empty");
			}
			
			ClientResponse httpResponse = getGroupByIdResource.accept("application/json").type("application/json")
					.header("sessionId", getSessionId()).header("userId", getSessionId())
					.get(ClientResponse.class);
			if(httpResponse.getStatus()==200)
			{
				String responseBody = httpResponse.getEntity(String.class);
				Gson gson = new GsonBuilder().create();
				group = gson.fromJson(responseBody, com.ziroh.chabby.groups.common.Group.class);
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
			getGroupByIdClient = null;
		}
		return group;
	}
	
	/**
	 * Get group by groupID
	 * @param groupId groupID is required
	 * @return Group object
	 * @throws IllegalArgumentException
	 * The group id cannot be null
	 * or
	 * The group id cannot be empty
	 */
	public CompletableFuture<Group> getGroupByIdAsync(String groupId) throws IllegalArgumentException
	{
		CompletableFuture<Group> future = new CompletableFuture<Group>();
		service.submit(()->
		{
			try
			{
				if(groupId==null)
				{
					throw new IllegalArgumentException("The group id cannot be null");
				}
				else if(StringUtils.isEmpty(groupId))
				{
					throw new IllegalArgumentException("The group id cannot be empty");
				}

				Group group = getGroupById(groupId);
				if (group!=null)
				{
					future.complete(group);
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
	
	public Group[] getAllGroupsbyUserid(String userId) 
	{
		Client getAllGroupsbyUseridClient = Client.create();
		String getAllGroupsbyUseridUrl = groupClientUrl + "/group/" +userId;
		WebResource getAllGroupsbyUseridResource = getAllGroupsbyUseridClient.resource(getAllGroupsbyUseridUrl);
		Group[] groups = null;
		try
		{
			if(userId==null)
			{
				throw new IllegalArgumentException("The user id cannot be null");
			}
			else if(StringUtils.isEmpty(userId))
			{
				throw new IllegalArgumentException("The user id cannot be empty");
			}
			
			ClientResponse httpResponse = getAllGroupsbyUseridResource.accept("application/json").type("application/json")
					.header("sessionId", getSessionId()).header("userId", getUserId()).get(ClientResponse.class);
			if(httpResponse.getStatus()==200)
			{
				String responseBody = httpResponse.getEntity(String.class);
				Gson gson = new GsonBuilder().create();
				groups = gson.fromJson(responseBody, com.ziroh.chabby.groups.common.Group[].class);
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
			getAllGroupsbyUseridClient = null;
		}
		return groups;
	}
	
	/**
	 * Get all groups by UserID
	 * @param userId UserID is required
	 * @return group array
	 * @throws IllegalArgumentException
	 * The user id cannot be null
	 * or
	 * The user id cannot be empty
	 */
	public CompletableFuture<Group[]> getAllGroupsbyUseridAsync(String userId) throws IllegalArgumentException
	{
		CompletableFuture<Group[]> future = new CompletableFuture<Group[]>();
		service.submit(()->
		{
			try
			{
				if(userId==null)
				{
					throw new IllegalArgumentException("The user id cannot be null");
				}
				else if(StringUtils.isEmpty(userId))
				{
					throw new IllegalArgumentException("The user id cannot be empty");
				}

				Group[] groups = getAllGroupsbyUserid(userId);
				if (groups.length>0)
				{
					future.complete(groups);
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
	
	public Result putGroup(Group grp) 
	{
		Client putGroupClient = Client.create();
		String putGroupUrl = groupClientUrl + "/group";
		WebResource putGroupResource = putGroupClient.resource(putGroupUrl);
		Result insertResult = null;
		try
		{
			if(grp==null)
			{
				throw new IllegalArgumentException("Group cannot be null");
			}
			
			Gson gson = new GsonBuilder().create();
			String groupInJson = gson.toJson(grp);
			ClientResponse httpResponse = putGroupResource.accept("application/json").type("application/json")
					.header("sessionId", getSessionId()).header("userId", getUserId()).post(ClientResponse.class, groupInJson);
			if(httpResponse.getStatus()==200)
			{
				String responseBody = httpResponse.getEntity(String.class);
				insertResult = gson.fromJson(responseBody, com.ziroh.chabby.operationalResults.Result.class);
			}
			else
			{
				insertResult = new Result();
				insertResult.seterror_code(1);
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
			putGroupClient = null;
		}
		return insertResult;
	}
	
	/**
	 * Add group
	 * @param grp Group class is required
	 * @return Result object
	 * @throws IllegalArgumentException
	 * Group cannot be null
	 */
	public CompletableFuture<Result> putGroupAsync(Group grp) throws IllegalArgumentException
	{
		CompletableFuture<Result> future = new CompletableFuture<Result>();
		service.submit(()->
		{
			try
			{
				if(grp==null)
				{
					throw new IllegalArgumentException("Group cannot be null");
				}

				Result result = putGroup(grp);
				if (result.error_code==0)
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
	
	public Result updateGroup(String ResourceId, String UserId)
    {
        Client updateGroupClient = Client.create();
		String updateGroupUrl = groupClientUrl + "/groups/group/" +ResourceId +UserId;
		WebResource updateGroupResource = updateGroupClient.resource(updateGroupUrl);
		Result insertResult = null;
		try
		{
			if (ResourceId==null ||StringUtils.isEmpty(ResourceId))
	        {
	        	throw new IllegalArgumentException("ResourceID cannot be null");
	        }
	        else if (UserId==null ||StringUtils.isEmpty(UserId))
	        {
	        	throw new IllegalArgumentException("UserId cannot be null");
	        }
					
			ClientResponse httpResponse = updateGroupResource.accept("application/json").type("application/json")
					.header("sessionId", getSessionId()).header("userId", getUserId()).post(ClientResponse.class);
			if(httpResponse.getStatus()==200)
			{
				String responseBody = httpResponse.getEntity(String.class);
				Gson gson = new GsonBuilder().create();
				insertResult = gson.fromJson(responseBody, com.ziroh.chabby.operationalResults.Result.class);
			}
			else
			{
				insertResult = new Result();
				insertResult.seterror_code(1);
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
			updateGroupClient = null;
		}
		return insertResult;
        
    }
	
	/**
	 * Update group
	 * @param ResourceId resource identifier is required
	 * @param UserId User identifier is required
	 * @return Result object
	 * @throws IllegalArgumentException
	 * ResourceID cannot be null
	 * or
	 * ResourceID cannot be empty
	 */
	public CompletableFuture<Result> updateGroupAsync(String ResourceId, String UserId) throws IllegalArgumentException
	{
		CompletableFuture<Result> future = new CompletableFuture<Result>();
		service.submit(()->
		{
			try
			{
				if (ResourceId==null ||StringUtils.isEmpty(ResourceId))
		        {
		        	throw new IllegalArgumentException("ResourceID cannot be null");
		        }
		        else if (UserId==null ||StringUtils.isEmpty(UserId))
		        {
		        	throw new IllegalArgumentException("UserId cannot be null");
		        }

				Result result = updateGroup(ResourceId, UserId);
				if (result.error_code==0)
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
