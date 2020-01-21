package com.ziroh.chabby.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DigestSHA256 implements IComputeDigest
{

	public byte[] computeDigest(byte[] message) throws NoSuchAlgorithmException
	{
		byte[] hash = null;
		if(message==null || message.length == 0)
		{
			throw new IllegalArgumentException("Message cannot be null or empty");
		}
		
		MessageDigest sha1 = MessageDigest.getInstance("SHA-256");
		hash = sha1.digest(message);
		return hash;
	}

	public byte[] computeDigest(String message) throws NoSuchAlgorithmException
	{
		if(message==null)
		{
			throw new IllegalArgumentException("Message cannot be null");
		}
		byte[] inputBytes = message.getBytes(StandardCharsets.UTF_8);
		return this.computeDigest(inputBytes);
	
	}
	
}
