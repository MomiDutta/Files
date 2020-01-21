package com.ziroh.chabby.audit.common;

public class UpdateKeyAuditMessage extends AuditMessage
{
	 public String UserId;
	 
	 public UpdateKeyAuditMessage(String UserId) throws Exception
     {
         this.UserId = UserId;
     }
}
