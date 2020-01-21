package com.ziroh.chabby.audit.common;

import java.rmi.UnknownHostException;

/**
 * This class is used to represent AuditMessage while signUp
 */
public class NewSignUpAuditMessage extends AuditMessage
{
	/**
	 * The user identifier
	 */
	public String UserId;
	
	/**
	 * NewSignUpAuditMessage constructor
	 * @param UserId UserID is required
	 * @throws Exception
	 * Retrieved IP address which is in done in Audit Message class must be handled
	 */
	public NewSignUpAuditMessage(String UserId) throws Exception
    {
        this.UserId = UserId;
        Message.Message_Itself = "";
        MessageType = AuditMessageType.NewKey;
    }
	
}
