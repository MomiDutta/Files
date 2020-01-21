package com.ziroh.cryptography.services;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class SymKeyGenerator implements IGenerateSymKey
{
	Cipher cipher;
	public byte[] GetKey(int KeySize) 
	{
		byte[] keyBytes = null;
		try
		{
			KeyGenerator keyGen = KeyGenerator.getInstance("AES");
			keyGen.init(KeySize);
			SecretKey secretKey = keyGen.generateKey();
			keyBytes = secretKey.getFormat().getBytes();  
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return keyBytes;
	}

	public byte[] GetRandomKey() 
	{
		byte[] keyBytes = null;
		try
		{
			KeyGenerator keyGen = KeyGenerator.getInstance("AES");

			keyGen.init(256);
			SecretKey secretKey = keyGen.generateKey();
			keyBytes = secretKey.getFormat().getBytes();  
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return keyBytes;
	}

	public byte[] GetKeyFromMessage(String KeyGenString) 
	{
		byte[] key = KeyGenString.getBytes(StandardCharsets.UTF_8);
		return this.GetKeyFromBytes(key);
	}

	public byte[] GetKeyFromBytes(byte[] KeyGenBytes) 
	{
		byte[] inputByte = new byte[1024];
		try
		{
			Mac hmac = Mac.getInstance("HmacSHA256");

			SecretKeySpec key = new SecretKeySpec(KeyGenBytes, "HmacSHA256");

			hmac.init(key);

			inputByte = hmac.doFinal(KeyGenBytes);

		} 
		catch (InvalidKeyException e)
		{

			e.printStackTrace();
		}
		catch (NoSuchAlgorithmException e) 
		{

			e.printStackTrace();
		}
		return inputByte;

	}

	public byte[] GenerateIV() 
	{
//		byte[] ivBytes = null;
		byte[] iv = null;
		SecureRandom randomSecureRandom;
		try
		{
			randomSecureRandom = new SecureRandom();
			iv = new byte[16];
			randomSecureRandom.nextBytes(iv);
//			IvParameterSpec ivParams = new IvParameterSpec(iv);
//			ivBytes = ivParams.getIV();   

		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return iv;
	}

	public byte[] GenerateKey() 
	{
		byte[] keyBytes = new byte[32];
		try
		{
//			KeyGenerator keyGen = KeyGenerator.getInstance("AES");
//			SecureRandom random = new SecureRandom();
//			keyGen.init(random);
//			SecretKey secretKey = keyGen.generateKey();
//			keyBytes = secretKey.getEncoded();  
			
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES", "SunJCE");

			keyGenerator.init(128); // 192 and 256 bits may not be available
			SecretKey skey = keyGenerator.generateKey();
			keyBytes = skey.getEncoded();
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return keyBytes;
	}

}
