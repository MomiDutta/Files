package com.ziroh.chabby.audit.common;

import java.util.concurrent.CompletableFuture;

import com.ziroh.chabby.operationalResults.Result;

interface IAuditLogSpecification 
{
	CompletableFuture<Result> AddNewAuditLog(AuditMessage message);
	CompletableFuture<AuditServerResponse> GetAudit(AuditServerRequest request);
}
