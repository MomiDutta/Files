package com.ziroh.chabby.utils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

public class ByteUtils 
{
	public static String byteArrayToString(byte[] input)
    {
		String byteArrayToString= new String(input, StandardCharsets.UTF_8);
       return byteArrayToString;
    }

    public static byte[] StringToByteArray(String Input)
    {
        return Input.getBytes(StandardCharsets.UTF_8);
    }

    public static byte[] concatenateBytes(byte[] input1, byte[] input2)
    {
        if (input1.length==0 || input1 == null)
        {
            throw new IllegalArgumentException("Input cannot be null or empty");
        }
        if (input2.length==0 || input2 == null)
        {
            throw new IllegalArgumentException("Input cannot be null or empty");
        }
        
        List<Byte> finalByteArray = new ArrayList<Byte>();
        for (int i = 0; i < input1.length; i++)
        {
            finalByteArray.add(input1[i]);
        }
        for (int j = 0; j < input2.length; j++)
        {
            finalByteArray.add(input2[j]);
        }
        Byte[] bytes =  finalByteArray.toArray(new Byte[finalByteArray.size()]);
        return ArrayUtils.toPrimitive(bytes);
        
    }
}
