package com.ziroh.chabby.client;

import java.util.concurrent.CompletableFuture;

import com.ziroh.chabby.common.KeyManagerUserCredentials;
import com.ziroh.chabby.common.UserResetCredentials;
import com.ziroh.chabby.common.keyTypes.Key;
import com.ziroh.chabby.keyStore.common.KeyRecord;
import com.ziroh.chabby.operationalResults.Result;

interface IKeyManagerOperation 
{
	CompletableFuture<Result> SignUpAsync(KeyManagerUserCredentials logIn);
	CompletableFuture<Result> SignInAsync(KeyManagerUserCredentials logIn);
	CompletableFuture<Key> GenerateKeyAsync(String KeyType);
	CompletableFuture<Result> StoreKeyAsync(KeyRecord KeyRecord);
	CompletableFuture<KeyRecord[]> GetAllExpiredKeysAsync(String UserId);
	CompletableFuture<KeyRecord[]> GetAllValidKeysAsync(String UserId);
	CompletableFuture<KeyRecord[]> GetAllKeyRecordAsync();
	CompletableFuture<KeyRecord[]> GetKeyRecordByDecriptionAsync(String UserId, String Description);
	CompletableFuture<KeyRecord[]> GetKeyRecordbyUserIdAsync(String UserId);
	CompletableFuture<KeyRecord[]> GetKeyRecordByUserIdDateTimeAsync(String UserId, String StartDate, String EndDate);
	CompletableFuture<KeyRecord[]> GetKeyRecordsByResourceAsync(String UserId, String ResourceId);
	CompletableFuture<Result> UpdateKeyAsync(String UniqueId, KeyRecord Record);
	CompletableFuture<Boolean> ChangeMasterKeyAsync();
	CompletableFuture<Result> ResetPasswordAsync(UserResetCredentials resetCredentials);
	CompletableFuture<Result> ResetPublicPrivatekeysAsync();
	
}
