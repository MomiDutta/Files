package com.ziroh.chabby.keyStore.common;

import com.ziroh.chabby.operationalResults.Result;

interface IKeyStore 
{
	 KeyRecord getAllKeyRecordsUniqueId(String UniqueId);
	 KeyRecord[] getKeyRecordsByResource(String userId, String resourceId);
	 KeyRecord[] getAllValidKeys(String userId);
	 KeyRecord[] getAllExpiredKeys(String userId);
	 KeyRecord[] getAllKeyRecord();
	 KeyRecord[] getKeyRecordbyUserId(String userId);
	 KeyRecord[] getKeyRecordByUserIdDateTime(String userId, String startDate, String enddate);
	 KeyRecord[] getKeyRecordByDecription(String userId, String description);
	 Result insertNewRecord(KeyRecord record);
	 Result deleteKey(String userId);
	 Result updateKey(String userId, KeyRecord record);
}
