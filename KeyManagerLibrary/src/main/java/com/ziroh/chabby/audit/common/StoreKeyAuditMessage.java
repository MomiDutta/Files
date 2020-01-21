package com.ziroh.chabby.audit.common;

/**
 * This class is used to represent AuditMessage while Storing key
 */
public class StoreKeyAuditMessage extends AuditMessage
{
	/**
	 * The user identifier
	 */
	public String UserId;

	/**
	 * StoreKeyAuditMessage constructor
	 * @param UserId UserID is required
	 * @throws Exception
	 * Retrieved IP address which is in done in Audit Message class must be handled
	 */
	public StoreKeyAuditMessage(String UserId) throws Exception
	{
		this.UserId = UserId;
	}

}
