package com.ziroh.chabby.audit.common;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * This class represents Audit Message
 */
public class AuditMessage
{
	/**
	 * User Identifier
	 */
	public String UserID;

	/**
	 * Get the user identifier
	 * @return String
	 */
	public String getUserID() {
		return UserID;
	}

	/**
	 * Set user identfier
	 * @param userID UserID is required
	 */
	public void setUserID(String userID) {
		UserID = userID;
	}

	/**
	 * Type of message
	 */
	public AuditMessageType MessageType;

	/**
	 * Get message type
	 * @return AuditMessageType object
	 */
	public AuditMessageType getMessageType() {
		return MessageType;
	}

	/**
	 * Set messageType
	 * @param messageType AuditMessageType is required
	 */
	public void setMessageType(AuditMessageType messageType) {
		MessageType = messageType;
	}

	/**
	 * The message
	 */
	public LogMessage Message;

	/**
	 * Get the message
	 * @return LogMessage is required
	 */
	public LogMessage getMessage() {
		return Message;
	}

	/**
	 * Set the message
	 * @param message Message is required
	 */
	public void setMessage(LogMessage message) {
		Message = message;
	}

	/**
	 * Initializes a new instance of the AuditMessage class.
	 * @throws UnknownHostException
	 * the IP address of a host received should be determined.
	 */
	public AuditMessage() throws UnknownHostException 
	{
		Message = new LogMessage();
		Message.TimeStamp = (int) System.currentTimeMillis();
		Message.HostName = InetAddress.getLocalHost().getHostName();
		Message.Process_Name = ManagementFactory.getRuntimeMXBean().getName();

	}

}
