package com.ziroh.chabby.userKeyPairStorage;

import java.util.concurrent.CompletableFuture;

import com.ziroh.chabby.operationalResults.Result;

interface IUserPublicKeyStore 
{
	CompletableFuture<UserKeyPair> GetKeyPairAsync(String UserId);
	CompletableFuture<Result> StoreKeyPairAsync(UserKeyPair KeyPair);
	CompletableFuture<Result> UpdateKeyPairAsync(UserKeyPair KeyPair);
}
