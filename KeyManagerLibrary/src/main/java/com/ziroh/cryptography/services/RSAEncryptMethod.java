package com.ziroh.cryptography.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ziroh.chabby.common.keyTypes.RSAKey;
import com.ziroh.chabby.utils.JSONCustomSerializer;

public class RSAEncryptMethod implements IEncryptAsymmetric, Serializable
{
	public RSAKey Key;

	public RSAKey getKey() {
		return Key;
	}

	public void setKey(RSAKey key) 
	{
		Key = key;
	}

	public byte[] convertObjectToByteArray(Object obj) throws IOException
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(obj);
		oos.flush();
		byte [] data = bos.toByteArray();
		return data;
	}

	public byte[] EncryptFromString(String Message) 
	{
		if ((Message == null) || StringUtils.isEmpty(Message))
		{
			throw new IllegalArgumentException("Input cannot be null or empty");
		}

		byte[] cipher = null;

		byte[] plainTextBytes = Message.getBytes(StandardCharsets.UTF_8);
		cipher= EncryptFromBytes(plainTextBytes);

		return cipher;
	}


	public byte[] EncryptFromBytes(byte[] MessageinBytes) 
	{
		byte[] encryptedBytes = null;
		byte[] encryptedBytesNew = new byte[256];

		try
		{		

			byte[] Exponent = Key.getExponent();
			byte[] modulus = Key.getModulus();

			GsonBuilder builder = new GsonBuilder();
			builder.registerTypeHierarchyAdapter(byte[].class, new JSONCustomSerializer());
			Gson gson=builder.create();
			//String exponentInJSon = gson.toJson(exponent);
			String modulusInJSon = gson.toJson(modulus);

			BigInteger exponentInBigINteger = new BigInteger(Exponent);
			BigInteger modulusInBigINteger = new BigInteger(modulusInJSon.getBytes(StandardCharsets.UTF_8));

			RSAPublicKeySpec spec = new RSAPublicKeySpec(modulusInBigINteger, exponentInBigINteger);

			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			KeyFactory kf = KeyFactory.getInstance("RSA");
			PublicKey publicKey = kf.generatePublic(spec);
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			encryptedBytes = cipher.doFinal(MessageinBytes);
			System.arraycopy(encryptedBytes, 0, encryptedBytesNew, 0, encryptedBytesNew.length);

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return encryptedBytes;
	}

	public String DecryptToString(byte[] EncryptedBytes) throws Exception 
	{
		String plainText = null;
		byte[] plainTextBytes = this.DecryptToBytes(EncryptedBytes);
		if ((plainTextBytes != null))
		{
			plainText = new String(EncryptedBytes, StandardCharsets.UTF_8);
		}


		return plainText;
	}

	public byte[] DecryptToBytes(byte[] EncryptedBytes) throws IllegalArgumentException
	{
		byte[] plainTextBytes = null;
		byte[] encryptedBytesNew = new byte[256];
		this.checkKey();
		if ((EncryptedBytes == null) || (EncryptedBytes.length == 0)) 	
		{
			throw new IllegalArgumentException("Input cannot be null or empty");
		}

		try
		{       

			byte[] Exponent = this.Key.getExponent();
			byte[] modulus = this.Key.getModulus();		
//			byte[] D = this.Key.getD();
//			byte[] DP = this.Key.getDP();
//			byte[] DQ =this.Key.getDQ();
//			byte[] P =this.Key.getP();
//			byte[] Q =this.Key.getQ();
//			byte[] InverseQ =this.Key.getInverseQ();

			GsonBuilder builder = new GsonBuilder();
			builder.registerTypeHierarchyAdapter(byte[].class, new JSONCustomSerializer());
			Gson gson=builder.create();
			//String exponentInJSon = gson.toJson(exponent);
			String modulusInJSon = gson.toJson(modulus);

			BigInteger exponentInBigINteger = new BigInteger(Exponent);
			BigInteger modulusInBigINteger = new BigInteger(modulusInJSon.getBytes(StandardCharsets.UTF_8));
			//		    BigInteger dInteger = new BigInteger(d);
			//		    BigInteger dqInteger = new BigInteger(dq);
			//		    BigInteger pInteger = new BigInteger(p);
			//		    BigInteger qInteger = new BigInteger(q);
			//		    BigInteger inverseQInteger = new BigInteger(inverseQ);
			//		    BigInteger dpInteger = new BigInteger(dp);
			
			RSAPrivateKeySpec spec = new RSAPrivateKeySpec(modulusInBigINteger, exponentInBigINteger);

			//Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			KeyFactory kf = KeyFactory.getInstance("RSA");
			PrivateKey privateKey = kf.generatePrivate(spec);
			Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			
			byte[] encryptedUnsigned = new byte[256];
			encryptedUnsigned = gson.toJson(EncryptedBytes).getBytes();
			System.arraycopy(encryptedUnsigned, 0, encryptedBytesNew, 0, encryptedBytesNew.length);
			
			plainTextBytes = cipher.doFinal(encryptedBytesNew);
			// System.arraycopy(encryptedBytesUnsigned, 0, encryptedBytesSize, 0, encryptedBytesSize.length);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return plainTextBytes;

	}


	void checkKey()
	{
		if (this.Key.D== null)
		{
			throw new IllegalArgumentException("Key cannot be null");
		}
	}

}
