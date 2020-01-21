package com.ziroh.chabby.common;

/**
 * This class represents Asymmetric Key
 */
public class AsymmetricKey 
{
	/**
	 * Public Key
	 */
	public byte[] PublicKey;

	/**
	 * Get the public key
	 * @return PublicKey
	 */
	public byte[] getPublicKey() {
		return PublicKey;
	}

	/**
	 * Set public key
	 * @param publicKey PublicKey is required
	 */
	public void setPublicKey(byte[] publicKey) {
		PublicKey = publicKey;
	}
	
	/**
	 * Private key
	 */
	public byte[] PrivateKey;

	/**
	 * Get private Key
	 * @return Private key 
	 */
	public byte[] getPrivateKey() {
		return PrivateKey;
	}

	/**
	 * Set the private key
	 * @param privateKey PrivateKey is required
	 */
	public void setPrivateKey(byte[] privateKey) {
		PrivateKey = privateKey;
	}
	
}
