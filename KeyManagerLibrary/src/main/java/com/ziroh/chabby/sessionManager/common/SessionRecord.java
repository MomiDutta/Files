package com.ziroh.chabby.sessionManager.common;

class SessionRecord 
{
	public String SessionId;

	public String getSessionId() {
		return SessionId;
	}

	public void setSessionId(String sessionId) {
		SessionId = sessionId;
	}
	
	public long GeneratedOn;

	public long getGeneratedOn() {
		return GeneratedOn;
	}

	public void setGeneratedOn(long generatedOn) {
		GeneratedOn = generatedOn;
	}
	
}
