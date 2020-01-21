package com.ziroh.cryptography.services;

interface IEncryptAsymmetric 
{
	byte[] EncryptFromString(String Message);
    byte[] EncryptFromBytes(byte[] MessageinBytes);
    String DecryptToString(byte[] EncryptedBytes) throws Exception;
    byte[] DecryptToBytes(byte[] EncryptedBytes) throws Exception;
}
