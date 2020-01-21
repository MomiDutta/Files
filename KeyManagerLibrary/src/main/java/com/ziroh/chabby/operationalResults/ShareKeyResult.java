package com.ziroh.chabby.operationalResults;

/**
 * This class represents result of Shared Key
 */
public class ShareKeyResult extends Result
{
	/**
	 * The key
	 */
	public byte[] Key;

	/**
	 * Get key
	 * @return Key
	 */
	public byte[] getKey() {
		return Key;
	}
	/**
	 * Set key
	 * @param key Key is required
	 */
	public void setKey(byte[] key) {
		Key = key;
	}

}
