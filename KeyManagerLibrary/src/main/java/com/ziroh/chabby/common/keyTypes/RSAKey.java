package com.ziroh.chabby.common.keyTypes;

import java.io.Serializable;

/**
 * This class represents an RSAKey
 */
public class RSAKey extends Key implements Serializable
{
	/**
	 * RSAKey constructor
	 */
	public RSAKey()
	{
		KeyType = "RSAKey";
        GeneratedOn = System.currentTimeMillis();
	}
	
	/**
	 * D is the private key
	 */
	public byte[] D;

	/**
	 * Get D
	 * @return D
	 */
	public byte[] getD() {
		return D;
	}

	/**
	 * Set  D
	 * @param d D is required
	 */
	public void setD(byte[] d) {
		D = d;
	}

	/**
	 * DP
	 */
	public byte[] DP;

	/**
	 * Get DP
	 * @return DP
	 */
	public byte[] getDP() {
		return DP;
	}

	/**
	 * Set DP
	 * @param dP DP is required
	 */
	public void setDP(byte[] dP) {
		DP = dP;
	}
	
	/**
	 * DQ
	 */
	public byte[] DQ;

	/**
	 * Get DQ
	 * @return DQ
	 */
	public byte[] getDQ() {
		return DQ;
	}

	/**
	 * Set DQ
	 * @param dQ DQ is required
	 */
	public void setDQ(byte[] dQ) {
		DQ = dQ;
	}
	
	/**
	 * Exponent is part of the public key
	 */
	public byte[] Exponent;

	/**
	 * Get exponent
	 * @return exponent
	 */
	public byte[] getExponent() {
		return Exponent;
	}

	/**
	 * Set exponent
	 * @param exponent Exponent is required
	 */
	public void setExponent(byte[] exponent) {
		Exponent = exponent;
	}
	
	/**
	 * InverseQ
	 */
	public byte[] InverseQ;

	/**
	 * Get inverseQ
	 * @return InverseQ
	 */
	public byte[] getInverseQ() {
		return InverseQ;
	}

	/**
	 * Set inverseQ
	 * @param inverseQ InverseQ is required
	 */
	public void setInverseQ(byte[] inverseQ) {
		InverseQ = inverseQ;
	}
	
	/**
	 * Modulus is part of the public key
	 */
	public byte[] Modulus;

	/**
	 * Get the modulus
	 * @return  Modulus
	 */
	public byte[] getModulus() {
		return Modulus;
	}

	/**
	 * Set the modulus
	 * @param modulus Modulus is required
	 */
	public void setModulus(byte[] modulus) {
		Modulus = modulus;
	}
	
	/**
	 * P
	 */
	public byte[] P ;

	/**
	 * Get P
	 * @return P
	 */
	public byte[] getP() {
		return P;
	}

	/**
	 * Set P
	 * @param p P is required
	 */
	public void setP(byte[] p) {
		P = p;
	}
	
	/**
	 * Q
	 */
	public byte[] Q;

	/**
	 * Get Q
	 * @return Q
	 */
	public byte[] getQ() {
		return Q;
	}

	/**
	 * Set Q
	 * @param q Q is required
	 */
	public void setQ(byte[] q) {
		Q = q;
	}
	
}
