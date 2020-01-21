package com.ziroh.chabby.audit.common;

public class SignInAuditMessage extends AuditMessage
{
	/**
	 *  The user identifier
	 */
	public String UserId;

	/**
	 * SignInAuditMessage constructor
	 * @param UserId UserID is required
	 * @throws Exception
	 * Retrieved IP address which is in done in Audit Message class must be handled
	 */
	public SignInAuditMessage(String UserId) throws Exception
	{
		this.UserId = UserId;
	}
}
