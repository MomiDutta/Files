package com.ziroh.chabby.userManagement.common;

/**
 * User Credential object. 
 */
public class UserData 
{
	/**
	 * User Identifier
	 */
	public String ID ;

	/**
	 * Get USerID
	 * @return UserID
	 */
	public String getID() {
		return ID;
	}

	/**
	 * Set UserID
	 * @param iD UserID is required
	 */
	public void setID(String iD) {
		ID = iD;
	}
	
	/**
	 * Salt
	 */
	public byte[] Salt;

	/**
	 * Get Salt
	 * @return Salt in byte array
	 */
	public byte[] getSalt() {
		return Salt;
	}

	/**
	 * Set salt
	 * @param salt Salt is required
	 */
	public void setSalt(byte[] salt) {
		Salt = salt;
	}
	
	/**
	 * Digest
	 */
	public byte[] Digest;

	/**
	 * Get digest
	 * @return digest
	 */
	public byte[] getDigest() {
		return Digest;
	}

	/**
	 * Set digest
	 * @param digest Digest is required
	 */
	public void setDigest(byte[] digest) {
		Digest = digest;
	}
	
}
