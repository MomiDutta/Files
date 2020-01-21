package com.ziroh.chabby.utils;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;

public class FileUtils 
{
	private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final SecureRandom RANDOM = new SecureRandom();

	public String getRandomString(int maxSize) throws UnsupportedEncodingException
	{

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < maxSize; ++i) {
			sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
		}
		return sb.toString();
	} 



}
