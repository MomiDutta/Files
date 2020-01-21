package com.ziroh.chabby.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;

public class ByteArrayObjectConversion implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Object ConvertByteArrayToObject(byte[] data) throws IOException, ClassNotFoundException 
	{
		if (data == null)
		{
			return null;
		}
		Object obj = null ;
		ByteArrayInputStream in = null;
		ObjectInputStream is = null;
		try
		{
			in = new ByteArrayInputStream(data);
			is = new ObjectInputStream(in);
			obj =  is.readObject();
			
			
		}
		catch(EOFException eof)
		{
			eof.printStackTrace();
		}
		catch(StreamCorruptedException strmex)
		
		{
			strmex.printStackTrace();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return obj;
	}

	public byte[] ConvertObjectToByteArray(Object obj) throws IOException 
	{
		if (obj == null)
		{
			return null;
		}

		ByteArrayOutputStream bos = null;
		ObjectOutputStream oos = null;
		try
		{
			bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			oos.flush();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		byte [] data = bos.toByteArray();
		return data;

	}

}
