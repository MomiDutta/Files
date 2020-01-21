package com.ziroh.chabby.audit.common;

/**
 * This class represents auditMessage of new generated key
 */
public class NewGenerateKeyAuditMessage extends AuditMessage
{
	/**
	 * The user identifier
	 */
	public String UserId;

	/**
	 * NewGenerateKeyAuditMessage constructor
	 * @param UserId UserID is required
	 * @param KeyType KeyType is required
	 * @throws Exception
	 * Retrieved IP address which is in done in Audit Message class must be handled
	 */
	public NewGenerateKeyAuditMessage(String UserId, String KeyType) throws Exception
	{
		this.UserId = UserId;
		Message.Message_Itself = KeyType;
		MessageType = AuditMessageType.NewKey;
	}
}
