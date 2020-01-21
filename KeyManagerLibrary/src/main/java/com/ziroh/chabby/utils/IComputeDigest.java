package com.ziroh.chabby.utils;

interface IComputeDigest 
{
	byte[] computeDigest(byte[] message) throws Exception;
    byte[] computeDigest(String message) throws Exception;
}
