package com.ziroh.chabby.notification.client;

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
import com.ziroh.chabby.notification.common.Notification;
import com.ziroh.chabby.operationalResults.Result;

/**
 * This class implemented calls related to all notifications.
 */
public class NotificationClient
{
	/**
	 * base Url
	 */
	String NotificationClientUrl= "http://localhost:53019";

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

	/**
	 * NotificationClient constructor
	 * @param userId User identifier
	 * @param sessionId session identifier
	 */
	public NotificationClient(String userId, String sessionId)
	{
		this.userId = userId;
		this.sessionid = sessionId;
	}

	static final int iCPU = Runtime.getRuntime().availableProcessors();
	static ExecutorService service = Executors.newFixedThreadPool(iCPU);

	private Result addNewNotification(Notification notification) 
	{
		Client addNewNotificationClient = Client.create();
		String addNewNotificationUrl = NotificationClientUrl + "/Notifications";
		WebResource addNewNotificationResource = addNewNotificationClient.resource(addNewNotificationUrl);
		Result result = new Result();
		try
		{
			Gson gson = new GsonBuilder().create();
			String notificationInJson = gson.toJson(notification);
			ClientResponse httpResponse = addNewNotificationResource.accept("application/json").type("application/json")
					.header("sessionId", getSessionid()).header("userId", userId).post(ClientResponse.class, notificationInJson);
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
			addNewNotificationClient = null;
		}
		return result;
	}

	/**
	 * Adds a new notification.
	 * @param notification Notification is required
	 * @return Result object
	 * @throws IllegalArgumentException
	 * Notification cannot be null
	 */
	public CompletableFuture<Result> addNewNotificationAsync(Notification notification) throws IllegalArgumentException
	{
		CompletableFuture<Result> future = new CompletableFuture<Result>();
		service.submit(()->
		{
			try
			{
				if(notification==null)
				{
					throw new IllegalArgumentException("Notification cannot be null");
				}

				Result result = addNewNotification(notification);
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


	private Notification[] getNotifications(String ownerId)
	{
		Client getNotificationsClient = Client.create();
		String getNotificationsUrl = NotificationClientUrl + "/Notifications/" +ownerId;
		WebResource getNotificationsResource = getNotificationsClient.resource(getNotificationsUrl);
		Notification[] notificationCollection = null;
		try
		{
			ClientResponse httpResponse = getNotificationsResource.accept("application/json").type("application/json")
					.header("sessionId", getSessionid()).header("userId", userId).get(ClientResponse.class);
			if(httpResponse.getStatus()==200)
			{
				String responseBody = httpResponse.getEntity(String.class);
				Gson gson = new GsonBuilder().create();
				notificationCollection = gson.fromJson(responseBody, com.ziroh.chabby.notification.common.Notification[].class);
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
			getNotificationsClient = null;
		}
		return notificationCollection;		 
	}

	/**
	 * Get the notifications
	 * @param ownerId OwnerID is required
	 * @return Array of Notifications
	 * @throws IllegalArgumentException
	 * The owner id cannot be null
	 * or
	 * The owner id cannot be empty
	 */
	public CompletableFuture<Notification[]> getNotificationsAsync(String ownerId) throws IllegalArgumentException
	{
		CompletableFuture<Notification[]> future = new CompletableFuture<Notification[]>();
		service.submit(()->
		{
			try
			{
				if(ownerId==null)
				{
					throw new IllegalArgumentException("The owner id cannot be null");
				}
				else if(StringUtils.isEmpty(ownerId))
				{
					throw new IllegalArgumentException("The owner id cannot be empty");
				}

				Notification[] notificationCollection = getNotifications(ownerId);
				if (notificationCollection != null)
				{
					future.complete(notificationCollection);
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


	private Notification[] getNotificationShares(String ownerId)
	{
		Client getNotificationSharesClient = Client.create();
		String getNotificationSharesUrl = NotificationClientUrl +ownerId +"/SharedTypes";
		WebResource getNotificationSharesResource = getNotificationSharesClient.resource(getNotificationSharesUrl);
		Notification[] notificationCollection = null;
		try
		{
			ClientResponse httpResponse = getNotificationSharesResource.accept("application/json").type("application/json")
					.header("sessionId", getSessionid()).header("userId", userId).get(ClientResponse.class);
			if(httpResponse.getStatus()==200)
			{
				String responseBody = httpResponse.getEntity(String.class);
				Gson gson = new GsonBuilder().create();
				notificationCollection = gson.fromJson(responseBody, com.ziroh.chabby.notification.common.Notification[].class);
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
			getNotificationSharesClient = null;
		}
		return notificationCollection;		 
	}

	/**
	 * Get shared notifications
	 * @param ownerId OwnerID is required
	 * @return Array of notifications
	 * @throws IllegalArgumentException
	 * The owner id cannot be null
	 * or
	 * The owner id cannot be empty
	 */
	public CompletableFuture<Notification[]> getNotificationSharesAsync(String ownerId) throws IllegalArgumentException
	{
		CompletableFuture<Notification[]> future = new CompletableFuture<Notification[]>();
		service.submit(()->
		{
			try
			{
				if(ownerId==null)
				{
					throw new IllegalArgumentException("The owner id cannot be null");
				}
				else if(StringUtils.isEmpty(ownerId))
				{
					throw new IllegalArgumentException("The owner id cannot be empty");
				}

				Notification[] notificationCollection = getNotificationShares(ownerId);
				if (notificationCollection != null)
				{
					future.complete(notificationCollection);
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

	private Notification[] getNotificationUnread(String ownerId) 
	{
		Client getNotificationUnreadClient = Client.create();
		String getNotificationUnreadUrl = NotificationClientUrl +ownerId +"/Unread";
		WebResource getNotificationUnreadResource = getNotificationUnreadClient.resource(getNotificationUnreadUrl);
		Notification[] notificationCollection = null;
		try
		{
			ClientResponse httpResponse = getNotificationUnreadResource.accept("application/json").type("application/json")
					.header("sessionId", getSessionid()).header("userId", userId).get(ClientResponse.class);
			if(httpResponse.getStatus()==200)
			{
				String responseBody = httpResponse.getEntity(String.class);
				Gson gson = new GsonBuilder().create();
				notificationCollection = gson.fromJson(responseBody, com.ziroh.chabby.notification.common.Notification[].class);
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
			getNotificationUnreadClient = null;
		}
		return notificationCollection;	
	}

	/**
	 * Get all unread notifications
	 * @param ownerId OwnerID is required
	 * @return Array of notifications
	 * @throws IllegalArgumentException
	 * Owner Id cannot be null
	 * or
	 * Owner Id cannot be empty
	 */
	public CompletableFuture<Notification[]> getNotificationUnreadAsync(String ownerId) throws IllegalArgumentException
	{
		CompletableFuture<Notification[]> future = new CompletableFuture<Notification[]>();
		service.submit(()->
		{
			try
			{
				if(ownerId==null)
				{
					throw new IllegalArgumentException("The owner id cannot be null");
				}
				else if(StringUtils.isEmpty(ownerId))
				{
					throw new IllegalArgumentException("The owner id cannot be empty");
				}

				Notification[] notificationCollection = getNotificationUnread(ownerId);
				if (notificationCollection != null)
				{
					future.complete(notificationCollection);
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


	private Notification[] getNotificationsBySender(String sendById) 
	{
		if(sendById==null)
		{
			throw new IllegalArgumentException("SendBy Id cannot be null");
		}
		else if(StringUtils.isEmpty(sendById))
		{
			throw new IllegalArgumentException("SendBy Id cannot be empty");
		}

		Client getNotificationsBySenderClient = Client.create();
		String getNotificationsBySenderUrl = NotificationClientUrl +"/Notifications/" +sendById;
		WebResource getNotificationUnreadResource = getNotificationsBySenderClient.resource(getNotificationsBySenderUrl);
		Notification[] notificationCollection = null;
		try
		{
			ClientResponse httpResponse = getNotificationUnreadResource.accept("application/json").type("application/json")
					.header("sessionId", getSessionid()).header("userId", userId).get(ClientResponse.class);
			if(httpResponse.getStatus()==200)
			{
				String responseBody = httpResponse.getEntity(String.class);
				Gson gson = new GsonBuilder().create();
				notificationCollection = gson.fromJson(responseBody, com.ziroh.chabby.notification.common.Notification[].class);
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
			getNotificationsBySenderClient = null;
		}
		return notificationCollection;	
	}

	/**
	 * Get all notifications by sender
	 * @param sendById sendByID is required
	 * @return Array of Notifications
	 * @throws IllegalArgumentException
	 * SendBy Id cannot be null
	 * or
	 * SendBy Id cannot be empty
	 */
	public CompletableFuture<Notification[]> getNotificationsBySenderAsync(String sendById) throws IllegalArgumentException
	{
		CompletableFuture<Notification[]> future = new CompletableFuture<Notification[]>();
		service.submit(()->
		{
			try
			{
				if(sendById==null)
				{
					throw new IllegalArgumentException("SendBy Id cannot be null");
				}
				else if(StringUtils.isEmpty(sendById))
				{
					throw new IllegalArgumentException("SendBy Id cannot be empty");
				}

				Notification[] notificationCollection = getNotificationsBySender(sendById);
				if (notificationCollection != null)
				{
					future.complete(notificationCollection);
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
