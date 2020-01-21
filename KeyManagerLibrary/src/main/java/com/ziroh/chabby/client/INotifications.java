package com.ziroh.chabby.client;

import java.util.concurrent.CompletableFuture;

import com.ziroh.chabby.notification.common.Notification;
import com.ziroh.chabby.operationalResults.Result;

interface INotifications 
{
	CompletableFuture<Notification[]> GetAllUnreadNotificationsAsync(String UserId);
	CompletableFuture<Notification[]> GetAllKeySharedNotificationsAsync(String UserId);
	 Result UpdateNotification(Notification notification);
}
