package com.ziroh.chabby.groups.common;

import java.security.SecureRandom;

import org.apache.commons.codec.binary.Base64;

import com.ziroh.chabby.utils.DigestSHA512;

class RandomNumberSecure 
{
	byte[] RandomNumberGenerator(int size)
	{
		SecureRandom random = new SecureRandom();
		byte[] randomNumber = new byte[size];
		random.nextBytes(randomNumber);
		return randomNumber;
	}
	
	String RandomNumberGeneratorString(int size)
	{
		 DigestSHA512 digestofKey = new DigestSHA512();
         return Base64.encodeBase64String(digestofKey
        		 .computeDigest(RandomNumberGenerator(size)));
	}
	
}
