package com.ziroh.chabby.common;

/**
 * This class represents reset credentials for resetting user password
 */
public class UserResetCredentials 
{
	/**
	 * The username
	 */
	public String Username;

	/**
	 * Get userName
	 * @return UserName
	 */
	public String getUsername() {
		return Username;
	}
/**
 * Set userName
 * @param username UserName is required
 */
	public void setUsername(String username) {
		Username = username;
	}
	
	/**
	 * The new password
	 */
	public String NewPassword;

	/**
	 * Get new Password
	 * @return New password
	 */
	public String getNewPassword() {
		return NewPassword;
	}

	/**
	 * Set new password
	 * @param newPassword New password is required
	 */
	public void setNewPassword(String newPassword) {
		NewPassword = newPassword;
	}
	
	/**
	 * The old user key
	 */
	public byte[] OldUserkey;

	/**
	 * Get old userKey
	 * @return Old UserKey
	 */
	public byte[] getOldUserkey() {
		return OldUserkey;
	}

	/**
	 * Set old userKey
	 * @param oldUserkey Old UserKey is required
	 */
	public void setOldUserkey(byte[] oldUserkey) {
		OldUserkey = oldUserkey;
	}
	
}
