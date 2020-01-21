package com.ziroh.cryptography.services;

public interface IGenerateSymKey 
{
	byte[] GetKey(int KeySize);
    byte[] GetRandomKey();
    byte[] GetKeyFromMessage(String KeyGenString);
    byte[] GetKeyFromBytes(byte[] KeyGenBytes);
    byte[] GenerateIV();
}
