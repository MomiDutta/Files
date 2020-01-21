package com.ziroh.chabby.userManagement.common;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;

import com.ziroh.chabby.common.AsymmetricKey;
import com.ziroh.chabby.common.EncryptResourceKeyGetSymIV;
import com.ziroh.chabby.common.keyTypes.Key;
import com.ziroh.chabby.common.keyTypes.RSAKey;
import com.ziroh.chabby.keyGeneration.client.KeyGenerationClient;
import com.ziroh.chabby.utils.ByteArrayConcatenator;
import com.ziroh.chabby.utils.ByteArrayObjectConversion;
import com.ziroh.chabby.utils.ByteUtils;
import com.ziroh.chabby.utils.DigestSHA256;
import com.ziroh.chabby.utils.DigestSHA512;
import com.ziroh.chabby.utils.FileUtils;
import com.ziroh.cryptography.services.AESEncryptor;
import com.ziroh.cryptography.services.RSAEncryptMethod;
import com.ziroh.cryptography.services.SymKeyGenerator;

public class UserCryptoUtils implements Serializable
{
	String sessionId;
	String userId;
	RSAKey rSAKey;

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public UserCryptoUtils()
	{

	}

	public UserCryptoUtils(String sessionId, String userID)
	{
		this.sessionId = sessionId;
		this.userId = userID;
	}

	/**
	 * Get user Identifier
	 * @param userID UserID is required
	 * @return UserID
	 */
	public String getUserId(String userID) 
	{
		String userIDInHash = null;
		userIDInHash=   getUserIDinHash(userID);
		return userIDInHash;
	}

	/**
	 * Get user ID in Hash
	 * @param userID UsrID is required
	 * @return UserID in hash
	 */
	public String getUserIDinHash(String userID) 
	{
		String id = null;
		try 
		{
			id = Hex.encodeHexString(new DigestSHA256().computeDigest(userID));
		} 
		catch (NoSuchAlgorithmException e) 
		{

			e.printStackTrace();
		}
		return id;
	}

	/**
	 * Generate salt
	 * @return Salt in byte array
	 */
	public byte[] GenerateSalt()
	{
		String randomString = null;
		try 
		{
			randomString = new FileUtils().getRandomString(2048);
		} 
		catch (UnsupportedEncodingException e) 
		{

			e.printStackTrace();
		}
		DigestSHA512 digest = new DigestSHA512();
		return digest.computeDigest(randomString);

	}

	/**
	 * Create Digest
	 * @param Password Password is required
	 * @param Salt Salt is required
	 * @return Digest in byte array
	 */
	public byte[] CreateDigest(String Password, byte[] Salt)
	{
		byte[] passwordBytes = StringUtils.getBytesUtf8(Password);
		byte[] finalByteArray = ByteUtils.concatenateBytes(passwordBytes, Salt);
		DigestSHA512 sha512 = new DigestSHA512();
		byte[] digest = sha512.computeDigest(finalByteArray);
		return digest;
	}

	/**
	 * Generate user key
	 * @param userid User ID is required
	 * @param password Password is required
	 * @return User Key in byte array
	 */
	public byte[] GenerateUserKey(String userid, String password) 
	{
		byte[] userKey = null;
		DigestSHA256 digestSHA256 = new DigestSHA256();
		String message = userid + password;
		try 
		{
			userKey = digestSHA256.computeDigest(message.getBytes(StandardCharsets.US_ASCII));
		} 
		catch (NoSuchAlgorithmException e) 
		{
			e.printStackTrace();
		}
		return userKey;
	}

	/**
	 * Generate user Asymmetric keys
	 * @param sessionId SessionID is required
	 * @param currentUserId Current User ID is required
	 * @return Asymmetric Key 
	 */
	public AsymmetricKey GenerateUserAsymkeys(String sessionId, String currentUserId)
	{
		AsymmetricKey userKeys = new AsymmetricKey();
		try
		{

			RSAKey rSAKey =  (RSAKey)GenerateRSAKey(sessionId, currentUserId);
			if(rSAKey instanceof RSAKey)
			{

				byte[]publicKey = ByteArrayConcatenator.Concat2Arrays(rSAKey.Exponent, rSAKey.Modulus);
ByteArrayObjectConversion conversion = new ByteArrayObjectConversion();
				byte[] privateKey = conversion.ConvertObjectToByteArray(rSAKey);

				userKeys = new AsymmetricKey();
				userKeys.setPrivateKey(privateKey);
				userKeys.setPublicKey(publicKey);

			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return userKeys;
	}

	
	/**
	 * Generate RSA key
	 * @return RSA key
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 * @throws IllegalArgumentException 
	 */
	private Key GenerateRSAKey(String sessionID, String userID) throws IllegalArgumentException, InterruptedException, ExecutionException
	{
		return new KeyGenerationClient(sessionID, userID).generateKeyAsync("RSAKeyGen").get();
	}

	/**
	 * Generate Master Key
	 * @return Master Key
	 */
	public byte[] GenerateMasterKey()
	{

		return new SymKeyGenerator().GenerateKey();

	}

	/**
	 * Encrypt Master key
	 * @param keyToEncrypt Key to Encrypt is required
	 * @param publicKey Public Key of RSA key
	 * @return Encrypted Master Key
	 * @throws IOException 
	 * Public key must be splitted in a particular order
	 */
	public byte[] EncryptKeyAsym(byte[] keyToEncrypt, byte[] publicKey) throws IOException
	{
		byte[] encryptedUserAsymKeys = new byte[32];
		byte[][] rsaPublic = ByteArrayConcatenator.Split2Arrays(publicKey);

		RSAEncryptMethod rsaMethod = new RSAEncryptMethod();

		rsaMethod.Key = new RSAKey();
		rsaMethod.Key.Exponent = rsaPublic[0];
		rsaMethod.Key.Modulus = rsaPublic[1];

		encryptedUserAsymKeys = rsaMethod.EncryptFromBytes(keyToEncrypt);

		return encryptedUserAsymKeys;
	}

	/**
	 * Decrypt User's RSA privateKey
	 * @param encryptedPrivateKey Encrypted key is required
	 * @param key User's Key is required
	 * @return User's decrypted PrivateKey
	 * @throws IOException 
	 * EncryptedPrivate key must be splitted in a particular order
	 */
	public byte[] DecryptUserPrivateKey(byte[] encryptedPrivateKey,byte[] key) throws IOException
	{
		byte[][] privateKeyIV = ByteArrayConcatenator.Split2Arrays(encryptedPrivateKey);
		byte[] IV = privateKeyIV[0];
		byte[] keyCipher = privateKeyIV[1];
		byte[] privateKey = new AESEncryptor(key, IV).DecryptToBytes(keyCipher);
		return privateKey;
	}

	/**
	 * Decrypt User's Master key
	 * @param keyCipher CipherKey is required
	 * @param privateKey RSA privateKey is required 
	 * @return Decrypted Master Key
	 * @throws IllegalArgumentException
	 * Encrypted key and private key cannot be null
	 */
	public byte[] DecryptKeyAsym(byte[] keyCipher, byte[] privateKey) throws IllegalArgumentException
	{
		if(keyCipher==null || privateKey==null)
		{
			throw new IllegalArgumentException("Encrypted key and private key cannot be null");
		}
		byte[] dencryptedUserAsymKeys = null;
		RSAEncryptMethod rsa = new RSAEncryptMethod();
		try
		{
			ByteArrayObjectConversion conversion = new ByteArrayObjectConversion();
			RSAKey chabbyRsa =  (RSAKey) conversion.ConvertByteArrayToObject(privateKey);
			rsa.Key = new RSAKey();
			rsa.Key.D = chabbyRsa.getD();
			rsa.Key.DP = chabbyRsa.getDP();
			rsa.Key.DQ = chabbyRsa.getDQ();
			rsa.Key.Exponent = chabbyRsa.getExponent();
			rsa.Key.InverseQ = chabbyRsa.getInverseQ();
			rsa.Key.Modulus = chabbyRsa.getModulus();
			rsa.Key.P = chabbyRsa.getP();
			rsa.Key.Q = chabbyRsa.getQ();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

		dencryptedUserAsymKeys = rsa.DecryptToBytes(keyCipher);
		return dencryptedUserAsymKeys;
	}

	public byte[] EncryptUserPrivateKey(byte[] privateKey, byte[] key) throws IOException
	{
		byte[] IV = new SymKeyGenerator().GenerateIV();
		//		File f = new File("F:/IV.txt");
		//		FileOutputStream fos = new FileOutputStream(f);
		//		DataOutputStream dos = new DataOutputStream(fos);
		//		dos.write(IV);
		//		fos.close();
		//		System.out.println(IV);
		byte[] encryptedPrivateKey = new AESEncryptor(key, IV).EncryptFromBytes(privateKey);
		encryptedPrivateKey = ByteArrayConcatenator.Concat2Arrays(IV, encryptedPrivateKey);
		return encryptedPrivateKey;
	}

	public byte[] EncryptSymKeyIV(byte[] plainSymKeyIV, byte[] masterKey)
	{
		byte[] newIV = new SymKeyGenerator().GenerateIV();
		byte[] encryptedNewKey = new AESEncryptor(masterKey, newIV).EncryptFromBytes(plainSymKeyIV);
		encryptedNewKey = ByteArrayConcatenator.Concat2Arrays(newIV, encryptedNewKey);
		return encryptedNewKey;
	}

	public byte[] DecryptSymKeyIV(byte[] symKeyIV, byte[] masterKey) throws IOException
	{

		byte[][] resourceKeyIv = ByteArrayConcatenator.Split2Arrays(symKeyIV);
		byte[] IV = resourceKeyIv[0];
		byte[] keyCipher = resourceKeyIv[1];

		//		GsonBuilder builder = new GsonBuilder();
		//		builder.registerTypeHierarchyAdapter(byte[].class, new JSONCustomSerializer());
		//		Gson gson=builder.create();
		//		byte[] masterKeyUnsigned = gson.toJson(masterKey).getBytes();
		//		byte[] ivUnsigned = gson.toJson(IV).getBytes();
		//		byte[] keyCipherUnsigned = gson.toJson(keyCipher).getBytes();
		byte[] resourceKey = new AESEncryptor(masterKey, IV).DecryptToBytes(keyCipher);
		return resourceKey;
	}

	public byte[] EncryptSymKeyUsingPublicKey(byte[] decryptedSymKeyIV, byte[] publicKeyByte) throws IOException
	{
		byte[] encryptedMasterKey = null;
		byte[][] rsaPublic = ByteArrayConcatenator.Split2Arrays(publicKeyByte);
		RSAEncryptMethod rsaMethod = new RSAEncryptMethod();
		{
			rsaMethod.Key = new RSAKey();
			rsaMethod.Key.Exponent = rsaPublic[0];
			rsaMethod.Key.Modulus = rsaPublic[1];

		};
		encryptedMasterKey = rsaMethod.EncryptFromBytes(decryptedSymKeyIV);
		return encryptedMasterKey;
	}

	public EncryptResourceKeyGetSymIV getSymKeyIV()
	{
		byte[] symKey = null;
		byte[] iV = null;
		SymKeyGenerator symKeyGenerator = new SymKeyGenerator();
		symKey = symKeyGenerator.GenerateKey();
		iV = symKeyGenerator.GenerateIV();

		//byte[] encryptedResourceKey = aESEncryptor.EncryptFromBytes(resourceKey);
		EncryptResourceKeyGetSymIV encryptResourceSymIV = new EncryptResourceKeyGetSymIV(iV, symKey);

		return encryptResourceSymIV;
	}

}
