package com.ziroh.chabby.notification.common;

/**
 * This class represents notifications which are generated when a new key is being shared
 */
public class NewSharedKeyGeneratedNotification extends Notification
{
	/**
	 * NewSharedKeyGeneratedNotification constructor
	 */
	public NewSharedKeyGeneratedNotification()
    {
        TypeOfNotification = NotificationType.Key_Shared;
    }
	
	/**
	 * Resource identifier
	 */
	public String ResourceId;

	/**
	 * Get the resourceID
	 * @return ResourceID
	 */
	public String getResourceId() {
		return ResourceId;
	}

	/**
	 * Set resourceID
	 * @param resourceId ResourceID is required
	 */
	public void setResourceId(String resourceId) {
		ResourceId = resourceId;
	}
	
	/**
	 * Receiver identifier
	 */
	public String ReceiverId;

	/**
	 * Get Receiver ID
	 * @return ReceiverID
	 */
	public String getReceiverId() {
		return ReceiverId;
	}

	/**
	 * Set receiverID
	 * @param receiverId ReceiverID is required
	 */
	public void setReceiverId(String receiverId) {
		ReceiverId = receiverId;
	}
	
}
