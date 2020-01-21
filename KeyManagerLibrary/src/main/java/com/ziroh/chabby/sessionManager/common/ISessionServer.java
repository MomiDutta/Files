package com.ziroh.chabby.sessionManager.common;

import java.util.concurrent.CompletableFuture;

interface ISessionServer
{
	 CompletableFuture<Boolean> CheckSessionAsync(String userid, String sessionid);
	 CompletableFuture<Boolean> InsertNewSessionAsync(String userid, String sessionid);
}
