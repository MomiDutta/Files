package com.ziroh.chabby.ACL;

import java.util.concurrent.CompletableFuture;

import com.ziroh.chabby.operationalResults.Result;

interface IAccessControl
{
	 CompletableFuture<Result> addResourceAsync(String resourceId);
	 CompletableFuture<Result> addUserAsync(String resourceId,String userId);
	 CompletableFuture<Result> addUserListAsync(String resourceId,String[] userId);
	 CompletableFuture<Result> addNewResourceUserListAsync(ResourceUserWrapper resourceUser);
	 CompletableFuture<Result> deleteResourceAsync(String resourceId);
	 CompletableFuture<Result> deleteUserAsync(ResourceUserWrapper resourceUser);
	 CompletableFuture<String[]> getUserListAsync(String resourceId);
	 CompletableFuture<String> getAccessAsync(String resourceId,String userId);
}
