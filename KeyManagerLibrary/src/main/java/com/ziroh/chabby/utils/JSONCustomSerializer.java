package com.ziroh.chabby.utils;

import java.lang.reflect.Type;

import javax.ws.rs.Produces;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

@Produces("application/json/;charset=utf-8")
public class JSONCustomSerializer implements JsonSerializer<byte[]>, JsonDeserializer<byte[]>
{
	public byte[] deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
		JsonArray jsonArray = arg0.getAsJsonArray();
		byte[] bytes=new byte[jsonArray.size()];
		for(int i=0;i<jsonArray.size();i++)
		{
			int val = jsonArray.get(i).getAsInt()-128;
			bytes[i]=(byte)val;
		}
		
		return bytes;
	}

	public JsonElement serialize(byte[] arg0, Type arg1, JsonSerializationContext arg2)
	{
		int[] intarray=new int[arg0.length];
		for (int ind=0;ind<arg0.length;ind++) 
		{
			int i=arg0[ind];
			i=i+128;
			intarray[ind]=i;
		}
		JsonElement jsonString=new GsonBuilder().create().toJsonTree(intarray);
		return (jsonString);
	}

}
