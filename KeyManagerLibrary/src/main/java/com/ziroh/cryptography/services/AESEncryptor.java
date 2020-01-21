package com.ziroh.cryptography.services;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;

public class AESEncryptor implements IEncryptSymmetric
{
	public byte[] Key;

	public byte[] getKey() {
		return Key;
	}

	public void setKey(byte[] key) {
		Key = key;
	}

	public byte[] IV ;

	public byte[] getIV() {
		return IV;
	}

	public void setIV(byte[] iV) {
		IV = iV;
	}

	Cipher cipher;

	public AESEncryptor()
	{
		
	}
	
	public AESEncryptor(byte[] Key, byte[] IV) 
	{
		this.Key = Key;
		this.IV = IV;
		try
		{
		  cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
	}

	public AESEncryptor(byte[] Key, byte[] IV, Cipher Cipher) throws NoSuchProviderException 
	{
		this.Key = Key;
		this.IV = IV;
		cipher = Cipher;
		init();
	}
	

	void init() throws NoSuchProviderException
	{
		try {
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
		} 
		catch (NoSuchAlgorithmException e)
		{
			
			e.printStackTrace();
		} 
		catch (NoSuchPaddingException e) 
		{
			
			e.printStackTrace();
		}

	}

	public byte[] EncryptFromString(String Message)
	{
		if ((Message.length() == 0) || StringUtils.isEmpty(Message) || (Message == null))

		{
			throw new IllegalArgumentException("Input Message cannot be zero or null, valid input is required");
		}


		byte[] plainTextBytes= Message.getBytes(StandardCharsets.UTF_8);
		return this.EncryptFromBytes(plainTextBytes);
	}

	public byte[] EncryptFromBytes(byte[] MessageinBytes) 
	{
		if ((MessageinBytes==null) || (MessageinBytes.length==0))
		{
			throw new IllegalArgumentException("Input Message Bytes cannot be zero or null, valid input is required");

		}
		byte[] encryptedBytes = null;

		try
		{	
			
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING", "SunJCE");
			
			byte[] keySize = new byte[16];
			System.arraycopy(this.Key, 0, keySize, 0, keySize.length);
			IvParameterSpec ivSpec = new IvParameterSpec(this.IV);
			
			java.security.Key key = new SecretKeySpec(keySize, "AES");
			
//			AESEncryptor aesEncryptor = new AESEncryptor(cipher);
			cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
			encryptedBytes = cipher.doFinal(MessageinBytes);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return encryptedBytes;

	}

	public byte[] EncryptFromFilePath(String FilePath) 
	{
		 if ((FilePath.length()==0) || StringUtils.isEmpty(FilePath) || (FilePath==null))
             throw new IllegalArgumentException("Input File Path cannot be zero or null, valid input is required");
		 
         byte[] plainTextBytes=this.readFileBytes(FilePath);
         
         return this.EncryptFromBytes(plainTextBytes);
	}
	
	 @SuppressWarnings("null")
	byte[] readFileBytes(String FileName)
	 {
		 byte[] fileBytes = null;
		 if(FileName.isEmpty()==false)
		 {
			 File file = new File(FileName);
			 try
			 {
				 FileInputStream fis = new FileInputStream(file);
				 DataInputStream dis = new DataInputStream(fis);
				 for (int i = 0; i < file.length(); i++) 
				 {
					byte fileByte = dis.readByte();
					fileBytes[i] = fileByte;
					
				}
				 dis.close();
				 file = null;
			 }
			 catch(Exception ex)
			 {
				 ex.printStackTrace();
				 throw new IllegalArgumentException("Exception io IO while writing file");
			 }
		 }
		 else
		 {
			 throw new IllegalArgumentException("File donot exist");
		 }
		 return fileBytes;
	 }

	public void EncryptFromFileToFile(String FilePathSource, String FilePathDestination)
	{
		if ((FilePathSource.length() == 0) || StringUtils.isEmpty(FilePathSource) || (FilePathSource == null))
        {
            
            throw new IllegalArgumentException("Input Source path cannot be zero or null, valid input is required");
        }
        if ((FilePathDestination.length() == 0) || StringUtils.isEmpty(FilePathDestination) || (FilePathDestination == null))
        {
            
            throw new IllegalArgumentException("Input Destination Path cannot be zero or null, valid input is required");
        }
        byte[] plainTextBytes = this.readFileBytes(FilePathSource);
        
        byte[] cipherText=this.EncryptFromBytes(plainTextBytes);
        this.writeToFile(cipherText, FilePathDestination);
	}
	
	 void writeToFile(byte[] Bytes, String FileName)
	 {
		 try
		 {
			 FileOutputStream fos = new FileOutputStream(FileName);
			 DataOutputStream dos = new DataOutputStream(fos);
			 dos.write(Bytes);
			 dos.close();
		 }
		 catch(Exception ex)
		 {
			 throw new IllegalArgumentException("IO Exception while writing file");
		 }
	 }

	public String DecryptToString(byte[] EncryptedBytes) 
	{
		if ((EncryptedBytes.length == 0) || (EncryptedBytes == null))
			
		{
            throw new IllegalArgumentException("Input Encrypted Bytes cannot be null");
		}
        byte[] plainTextBytes=this.DecryptToBytes(EncryptedBytes);
        String decryptedData = new String(plainTextBytes, StandardCharsets.UTF_8);
        return removePadding(decryptedData.trim());
	}
	
	String removePadding(String paddedString)
    {
       return paddedString.replace("\0", "");
    }

	public void DecryptToFile(byte[] EncryptedBytes, String FilePathDestination) 
	{
		if ((EncryptedBytes.length == 0) || (EncryptedBytes == null))
		{
            throw new IllegalArgumentException("Input Encrypted Bytes cannot be null");
		}

        if ((FilePathDestination.length() == 0) || (FilePathDestination == null))
        {
            throw new IllegalArgumentException("Input destination file path cannot be null or of zero length");
        }

        byte[] plainText = this.DecryptToBytes(EncryptedBytes);
        this.writeToFile(plainText, FilePathDestination);

	}

	public byte[] DecryptToBytes(byte[] EncryptedBytes) 
	{
		if ((EncryptedBytes == null) || (EncryptedBytes.length == 0))
        {
            throw new IllegalArgumentException("Input Message Bytes cannot be zero or null, valid input is required");

        }

        byte[] decryptedBytes = null;

		try
		{			
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding", "SunJCE");
			
			byte[] keySize = new byte[16];
			System.arraycopy(this.Key, 0, keySize, 0, keySize.length);
			IvParameterSpec ivSpec = new IvParameterSpec(this.IV);
			
			java.security.Key key = new SecretKeySpec(keySize, "AES");
			cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
			
			byte[] encrytedBytesCopy = new byte[16];
			System.arraycopy(EncryptedBytes, 0, encrytedBytesCopy, 0, encrytedBytesCopy.length);
			decryptedBytes = cipher.doFinal(encrytedBytesCopy);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return decryptedBytes;
	}

	public byte[] DecryptFromFile(String FileSource) 
	{
		 if ((FileSource == null) || (FileSource.length() == 0))
         {
             throw new IllegalArgumentException("Input File Path cannot be zero or null, valid input is required");
         }
         byte[] fileBytes= this.readFileBytes(FileSource);
         return this.DecryptToBytes(fileBytes);
	}

}
