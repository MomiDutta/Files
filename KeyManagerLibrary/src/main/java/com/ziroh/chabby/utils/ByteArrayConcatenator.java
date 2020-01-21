package com.ziroh.chabby.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ByteArrayConcatenator 
{

	static byte[] GetHeaderLengthByteToByteArray(byte[] source) throws IOException
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		dos.writeInt(source.length);
		dos.flush();
		byte[] arr1Size = bos.toByteArray();
		return arr1Size;
	}

	static byte[] GetHeaderLengthIntToByteArray(int source) throws IOException
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		dos.writeInt(source);
		dos.flush();
		byte[] arr1Size = bos.toByteArray();
		return arr1Size;
	}

	static int GetHeaderLengthByteToIntArray(byte[] source) throws IOException
	{
		return ByteBuffer.wrap(source).getInt();
	}

	public static byte[] Concat2Arrays(byte[] array1,byte[] array2)
	{
		int preambleSize = 8;
		byte[] resultBytes = new byte[preambleSize+array1.length + array2.length];
		try
		{
			byte[] arr1Size = GetHeaderLengthByteToByteArray(array1);
			byte[] arr2Size = GetHeaderLengthByteToByteArray(array2);	
			System.arraycopy(arr1Size, 0, resultBytes,0, arr1Size.length);
			System.arraycopy(arr2Size, 0, resultBytes, arr1Size.length, arr2Size.length);
			System.arraycopy(array1, 0, resultBytes, arr1Size.length + arr2Size.length, array1.length);
			System.arraycopy(array2, 0, resultBytes, arr1Size.length + arr2Size.length + array1.length, array2.length);
		}
		catch(ArrayIndexOutOfBoundsException arrex)
		{
			arrex.printStackTrace();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return resultBytes;
	}

	public static byte[][] Split2Arrays(byte[] array) throws IOException
	{
		byte[][] result = new byte[2][];
		try
		{
			byte[] firstArrayLength=new byte[4];
			byte[] secondArrayLength=new byte[4];
			System.arraycopy(array, 0, firstArrayLength,0,4);
			System.arraycopy(array, 4, secondArrayLength, 0, 4);
			
			int array1Size = GetHeaderLengthByteToIntArray(firstArrayLength);
			int array2Size = GetHeaderLengthByteToIntArray(secondArrayLength);

			result[0] = new byte[array1Size];
			result[1] = new byte[array2Size];
			System.arraycopy(array, 8, result[0], 0, array1Size);
			System.arraycopy(array, 8+array1Size, result[1], 0, array2Size);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return result;
	}
}
