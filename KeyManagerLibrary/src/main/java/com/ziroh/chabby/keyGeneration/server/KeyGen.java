package com.ziroh.chabby.keyGeneration.server;

import com.ziroh.chabby.common.keyTypes.Key;

/**
 * Key Generation Code base class. All Key Generators must implement this type.
 */
public abstract class KeyGen 
{
	/**
	 * The name of the keyGen
	 */
    public String KeyGenName;

    /**
     * Get KeyGen name
     * @return KeyGen name
     */
	public String getKeyGenName() {
		return KeyGenName;
	}

	/**
	 * Set KeyGen name
	 * @param keyGenName KeyGen name is required
	 */
	public void setKeyGenName(String keyGenName) {
		KeyGenName = keyGenName;
	}
    
	/**
	 * This function is used to generate key
	 * @return Key object is returned
	 */
	 public abstract Key GenerateKey();
	 
}
