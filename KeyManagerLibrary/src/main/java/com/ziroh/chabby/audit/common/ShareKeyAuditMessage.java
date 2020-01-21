package com.ziroh.chabby.audit.common;

/**
 * This class is used to represent AuditMessage while Sharing Key
 */
public class ShareKeyAuditMessage extends AuditMessage
{
	/**
	 * User Identifier
	 */
	public String UserId;

	/**
	 * ShareKeyAuditMessage constructor
	 * @param UserId UserID is required
	 * @throws Exception
	 * Retrieved IP address which is in done in Audit Message class must be handled
	 */
	public ShareKeyAuditMessage(String UserId) throws Exception
	{
		this.UserId = UserId;
	}
}
