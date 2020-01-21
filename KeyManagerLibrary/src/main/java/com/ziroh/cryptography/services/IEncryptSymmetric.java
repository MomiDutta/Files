package com.ziroh.cryptography.services;

interface IEncryptSymmetric 
{
	byte[] EncryptFromString(String Message);
    byte[] EncryptFromBytes(byte[] MessageinBytes);
    byte[] EncryptFromFilePath(String FilePath);
    void EncryptFromFileToFile(String FilePathSource, String FilePathDestination);
    String DecryptToString(byte[] EncryptedBytes);
    void DecryptToFile(byte[] EncryptedBytes, String FilePathDestination);
    byte[] DecryptToBytes(byte[] EncryptedBytes);
    byte[] DecryptFromFile(String FileSource);
}
