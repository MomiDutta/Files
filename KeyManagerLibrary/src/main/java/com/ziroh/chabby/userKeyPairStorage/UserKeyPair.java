package com.ziroh.chabby.userKeyPairStorage;

/**
 * User Key PAir
 */
public class UserKeyPair 
{
	/**
	 * User identifier
	 */
	public String Userid;

	/**
	 * Get UserID
	 * @return UserID
	 */
	public String getUserid() {
		return Userid;
	}

	/**
	 * Set UserID
	 * @param userid UserID is required
	 */
	public void setUserid(String userid) {
		Userid = userid;
	}
	
	/**
	 * The public key
	 */
	public byte[] PublicKey;

	/**
	 * Get Public key
	 * @return Public Key
	 */
	public byte[] getPublicKey() {
		return PublicKey;
	}

	/**
	 * Set public key
	 * @param publicKey Public KEy is required
	 */
	public void setPublicKey(byte[] publicKey) {
		PublicKey = publicKey;
	}
	
	/**
	 * The private key
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
	 * Set private key
	 * @param privateKey Private key is required
	 */
	public void setPrivateKey(byte[] privateKey) {
		PrivateKey = privateKey;
	}
	
}
