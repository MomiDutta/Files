package com.ziroh.chabby.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class DigestSHA512 implements IComputeDigest
{
	
	public byte[] computeDigest(byte[] finalByteArray)
	{
		byte[] digestBytes = null;
		
		try
		{
			MessageDigest digestFunction = MessageDigest.getInstance("SHA-512");
			byte[] digestBytesInSigned = digestFunction.digest(finalByteArray);		
			
		
			digestBytes = digestBytesInSigned;
		}
		catch(IllegalArgumentException iaex)
		{
			iaex.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return digestBytes;
	}

	public byte[] computeDigest(String message) 
	{
		return this.computeDigest(message.getBytes(StandardCharsets.UTF_8));
	}
	
}
