package com.ziroh.chabby.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ziroh.chabby.utils.JSONCustomSerializer;
import com.ziroh.cryptography.services.AESEncryptor;

public class EncryptResourceKeyGetSymIV 
{
	byte[] Sym;
	byte[] IV;
	public byte[] getSym() {
		return Sym;
	}
	public void setSym(byte[] sym) {
		Sym = sym;
	}
	public byte[] getIV() {
		return IV;
	}
	public void setIV(byte[] iV) {
		IV = iV;
	}
	
	public EncryptResourceKeyGetSymIV() {
		
	}
	
	public EncryptResourceKeyGetSymIV(byte[] sym, byte[] iV) {
		super();
		Sym = sym;
		IV = iV;
	}
	
	public byte[] EncryptResourceKey(byte[] resourceKey, EncryptResourceKeyGetSymIV SymKeyIv)
	{
		AESEncryptor aESEncryptor = new AESEncryptor();
		aESEncryptor.Key = SymKeyIv.Sym;
		aESEncryptor.IV = SymKeyIv.IV;
		
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeHierarchyAdapter(byte[].class, new JSONCustomSerializer());
		Gson gson=builder.create();
		byte[] resourceKeyUnsigned = gson.toJson(resourceKey).getBytes();
		
		byte[] encryptedResourceKey = aESEncryptor.EncryptFromBytes(resourceKeyUnsigned);
		return encryptedResourceKey;
	}
}
