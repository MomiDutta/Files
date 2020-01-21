package com.ziroh.chabby.common;

import com.ziroh.chabby.operationalResults.Result;

/**
 * This class is used to return result object that represents after an user sign in
 */
public class SigninResult extends Result
{
	/**
	 * Private Key
	 */
	public byte[] PrivateKey;

	/**
	 * Get the privateKey
	 * @return PrivateKey
	 */
	public byte[] getPrivateKey() {
		return PrivateKey;
	}

	/**
	 * Set the PrivateKey
	 * @param privateKey PrivateKey is required
	 */
	public void setPrivateKey(byte[] privateKey) {
		PrivateKey = privateKey;
	}
	
	/**
	 * Master Key
	 */
	 public byte[] MasterKey;

	 /**
	  * Get master key
	  * @return MasterKey
	  */
	public byte[] getMasterKey() {
		return MasterKey;
	}

	/**
	 * Set master key
	 * @param masterKey MasterKey is required
	 */
	public void setMasterKey(byte[] masterKey) {
		MasterKey = masterKey;
	}
	 
}
