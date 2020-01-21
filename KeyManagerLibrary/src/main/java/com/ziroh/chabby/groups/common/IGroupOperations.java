package com.ziroh.chabby.groups.common;

import java.util.concurrent.CompletableFuture;

import com.ziroh.chabby.operationalResults.Result;

interface IGroupOperations 
{
	 CompletableFuture<Group> GetGroupByResourceidAsync(String ResourceId);
	 CompletableFuture<Group> GetGroupByIdAsync(String GroupId);
     CompletableFuture<Group[]> GetAllGroupsbyUseridAsync(String UserId);
     CompletableFuture<Result> PutGroupAsync(Group grp);
}
