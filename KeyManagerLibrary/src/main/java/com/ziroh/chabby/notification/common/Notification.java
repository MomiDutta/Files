package com.ziroh.chabby.notification.common;

/**
 * Base notification class.
 * All notification type must implement this base class
 *
 */
public class Notification 
{
	/**
	 * The status of the notification
	 */
	public NotificationStatus Status;

	/**
	 * Get status
	 * @return Status
	 */
	public NotificationStatus getStatus() {
		return Status;
	}

	/**
	 * Set notification status
	 * @param status Status is required
	 */
	public void setStatus(NotificationStatus status) {
		Status = status;
	}
	
	/**
	 * The notification identifier
	 */
	public String NotificationId;

	/**
	 * Get notificationID
	 * @return NotificationID
	 */
	public String getNotificationId() {
		return NotificationId;
	}

	/**
	 * Set notificationID
	 * @param notificationId notificationID is required
	 */
	public void setNotificationId(String notificationId) 
	{
		NotificationId = notificationId;
	}
	
	/**
	 * The type of the notification
	 */
	 public NotificationType TypeOfNotification;

	 /**
	  * Get type of notification
	  * @return Type of notification
	  */
	public NotificationType getTypeOfNotification() {
		return TypeOfNotification;
	}

	/**
	 * Set the type of notification
	 * @param typeOfNotification Type of notification is required
	 */
	public void setTypeOfNotification(NotificationType typeOfNotification) {
		TypeOfNotification = typeOfNotification;
	}
	 
	/**
	 * Send by identifier
	 */
	public String SendById;

	/**
	 * Get send by ID
	 * @return sendbyID 
	 */
	public String getSendById() {
		return SendById;
	}

	/**
	 * Set send by ID
	 * @param sendById SendByID is required
	 */
	public void setSendById(String sendById) {
		SendById = sendById;
	}
	
	/**
	 * Owner identifier
	 */
	public String OwnerId;

	/**
	 * Get ownerID
	 * @return ownerID
	 */
	public String getOwnerId() {
		return OwnerId;
	}

	/**
	 * set ownerID
	 * @param ownerId ownerID is required
	 */
	public void setOwnerId(String ownerId) {
		OwnerId = ownerId;
	}
	
	/**
	 * The generated date notification.
	 */
	public long GeneratedOn;

	/**
	 * Get the generated date
	 * @return GeneratedOn
	 */
	public long getGeneratedOn() {
		return GeneratedOn;
	}

	/**
	 * Set the generated date
	 * @param generatedOn GeneratedOn date is required
	 */
	public void setGeneratedOn(long generatedOn) {
		GeneratedOn = generatedOn;
	}
	
}
