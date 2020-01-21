package com.ziroh.chabby.client;

import java.util.concurrent.CompletableFuture;

import com.ziroh.chabby.common.ResourceKey;
import com.ziroh.chabby.keyStore.common.KeyRecord;
import com.ziroh.chabby.operationalResults.ShareKeyResult;

interface IShareKeyOperations 
{
	CompletableFuture<ShareKeyResult> ShareResourceKeyAsync(KeyRecord record, byte[] ResourceKey);
	CompletableFuture<ResourceKey[]> GetSharedKeysAsync();
}
