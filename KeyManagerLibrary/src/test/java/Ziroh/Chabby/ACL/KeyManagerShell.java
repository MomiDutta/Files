package Ziroh.Chabby.ACL;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ziroh.chabby.client.KeyManagerClient;
import com.ziroh.chabby.common.AsymmetricKey;
import com.ziroh.chabby.common.KeyManagerUserCredentials;
import com.ziroh.chabby.common.keyTypes.Key;
import com.ziroh.chabby.keyStore.common.KeyRecord;
import com.ziroh.chabby.operationalResults.Result;
import com.ziroh.chabby.operationalResults.ShareKeyResult;
import com.ziroh.chabby.userManagement.common.UserCryptoUtils;

public class KeyManagerShell 
{
	static final int iCPU = Runtime.getRuntime().availableProcessors();
	private static final String userID = null;
	ExecutorService service= Executors.newFixedThreadPool(iCPU);
	KeyManagerClient client;
	Scanner scanner;
	boolean doAudit = false;

	public void SignUp() 
	{
		client = new KeyManagerClient(doAudit);

		scanner = new Scanner(System.in);
		String userName = null;
		String password = null;

		System.out.println("Enter UserName : ");
		userName = scanner.nextLine();
		System.out.println("Enter password : ");
		password = scanner.nextLine();
		byte[] resourceKey = null;
		String resourceID = null;
		String uniqueId = null;
		try
		{
			KeyManagerUserCredentials credentials = new KeyManagerUserCredentials();
			credentials.setUserName(userName);
			credentials.setPassword(password);

			try
			{
				Result result = client.SignUpAsync(credentials).get();
				if(result.geterror_code()==0)
				{
					System.out.println("Successfully signed up");
				}
				
				
				
				if(result.geterror_code()==0)
				{
					KeyRecord[] records = client.GetKeyRecordbyUserIdAsync(userName).get();
					if(records.length!=0)
					{
						for (int i = 0; i < 4; i++) 
						{
							KeyRecord record = records[i+1];
							resourceID = record.getResourceId();
							System.out.println(resourceID);
							resourceKey = record.getResourceKey();
							System.out.println(resourceKey);
							
							uniqueId = record.getUniqueid();
							record.setUniqueid(resourceID+"Momidutta1");
							uniqueId = record.getUniqueid();
							
							Result shareKeyResult = client.StoreKeyAsync(record).get();
							if(shareKeyResult.geterror_code()==0)
							{
								System.out.println("Stored successfully");
							}
						}
					}
				}
			}
			catch (RuntimeException e) 
			{
				e.printStackTrace();
				service.shutdown();

			}
		}
		catch(IllegalArgumentException iaex)
		{
			iaex.printStackTrace();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

	}
	
	public void ShareKey() 
	{
		client = new KeyManagerClient(doAudit);

		scanner = new Scanner(System.in);
		String userName = null;
		String password = null;

		System.out.println("Enter UserName : ");
		userName = scanner.nextLine();
		System.out.println("Enter password : ");
		password = scanner.nextLine();
		byte[] resourceKey = null;
		String resourceID = null;
		try
		{
			KeyManagerUserCredentials credentials = new KeyManagerUserCredentials();
			credentials.setUserName(userName);
			credentials.setPassword(password);


			try
			{
				Result result = client.SignInAsync(credentials).get();
				Key key = client.GenerateKeyAsync("RSAKeyGen").get();
				System.out.println(key.KeyType);
				if(result.geterror_code()==0)
				{
					KeyRecord[] records = client.GetKeyRecordbyUserIdAsync(userName).get();
					if(records.length!=0)
					{
						for (int i = 0; i < records.length; i++) 
						{
							if(records[i].SymKeyIV!=null)
							{
							KeyRecord record = records[i];
							resourceID = record.getResourceId();
							System.out.println(resourceID);
							resourceKey = record.getResourceKey();
							System.out.println(resourceKey);
							byte[] symKeyIv = record.getSymKeyIV();
							for (int j = 0; j < symKeyIv.length; j++) 
							{
								byte b = symKeyIv[j];
								System.out.println(b);
								
							}
							
							ShareKeyResult shareKeyResult = client.ShareResourceKeyAsync(record, resourceKey).get();
							if(shareKeyResult.geterror_code()==0)
							{
								System.out.println("Shared successfully");
							}
						}
					}
					
//					KeyRecord[] records1 = client.GetKeyRecordsByResourceAsync(userName, resourceID).get();
//					if(records1.length!=0)
//					{
//						for (int i = 0; i < records1.length; i++) 
//						{
//							KeyRecord record = records1[i];
//							resourceID = record.getResourceId();
//							System.out.println(resourceID);
//							resourceKey = record.getResourceKey();	
//							
//						}
//					}
											
					}
				}
			}
			catch (RuntimeException e) 
			{
				e.printStackTrace();
				service.shutdown();

			}
		}
		catch(IllegalArgumentException iaex)
		{
			iaex.printStackTrace();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public void Signin()
	{
		client = new KeyManagerClient(doAudit);

		scanner = new Scanner(System.in);
		String userName = null;
		String password = null;

		System.out.println("Enter UserName : ");
		userName = scanner.nextLine();
		System.out.println("Enter password : ");
		password = scanner.nextLine();

		try
		{
			KeyManagerUserCredentials credentials = new KeyManagerUserCredentials();
			credentials.setUserName(userName);
			credentials.setPassword(password);


			try
			{
				Result result = client.SignInAsync(credentials).get();
				if(result.geterror_code()==0)
				{
					System.out.println("Successfully signedin");
				}
				//KeyRecord[] records = client.GetAllKeyRecordAsync().get();
				//KeyRecord[] records = client.GetKeyRecordbyUserIdAsync(userName).get();
				//KeyRecord[] records = client.GetAllExpiredKeysAsync(userName).get();
				//System.out.println(records.length);
			}
			catch (RuntimeException e) 
			{
				e.printStackTrace();
				service.shutdown();

			}
		}
		catch(IllegalArgumentException iaex)
		{
			iaex.printStackTrace();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	//	public void GenerateKey() throws IllegalArgumentException, InterruptedException, ExecutionException 
	//	{
	//		client = new KeyManagerClient();
	//
	//		scanner = new Scanner(System.in);
	//		String userName = null;
	//		String password = null;
	//
	//		System.out.println("Enter AdminName : ");
	//		userName = scanner.nextLine();
	//		System.out.println("Enter password : ");
	//		password = scanner.nextLine();
	//
	//		try
	//		{	UserCredentials crendtials = new UserCredentials();
	//		crendtials.setUserName(userName);
	//		crendtials.setPassword(password);
	//			UserManagementClient userClient = new UserManagementClient();
	//			Result result = userClient.signupAsync(crendtials).get();
	//			if(result.geterror_code()==0)
	//			{
	//				UserCryptoUtils utils = new UserCryptoUtils();
	//				AsymmetricKey asymmetricKeys = utils.GenerateUserAsymkeys("Admin","");
	//				System.out.println(asymmetricKeys.getPublicKey());
	//			}
	//			
	//
	//		}
	//		catch(Exception ex)
	//		{
	//			ex.printStackTrace();
	//		}
	//		
	//		
	//	}

	public void SignupAdmin() 
	{
		client = new KeyManagerClient(doAudit);

		scanner = new Scanner(System.in);
		String userName = null;
		String password = null;

		System.out.println("Enter AdminName : ");
		userName = scanner.nextLine();
		System.out.println("Enter password : ");
		password = scanner.nextLine();

		try
		{		
			KeyManagerUserCredentials login = new KeyManagerUserCredentials();
			login.setUserName(userName);
			login.setPassword(password);
			client = new KeyManagerClient(doAudit);
					//	Result resuclient.SignUpAsync(login);
			UserCryptoUtils utils = new UserCryptoUtils();
			

			AsymmetricKey asymmetricKeys = utils.GenerateUserAsymkeys(utils.getSessionId(),"Admin");
			byte[] publicKey = asymmetricKeys.PublicKey;

			System.out.println(publicKey);

			File fout = new File("F:/FIle/EncryptedText.txt");
			FileOutputStream fos = new FileOutputStream(fout);
			fos.write(publicKey);
			fos.close();

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

	}

	public void Test() 
	{
		//		KeyStoreClient client = new KeyStoreClient();
		//		
		//		KeyRecord record = new KeyRecord();
		//		record.setUniqueid("user_key_Ziroh123");
		//		record.setUserId("Ziroh123");
		//		record.setSymKeyIV(null);
		//		record.setKeyDescription("User Key");
		//		record.setGeneratedOn(1576748520043L);
		//		record.setKeyStatus("Active");
		//		record.setExpiryDate(0);
		//		byte[] resourceKey = { 80, 65, 78, 75, 65, 74};
		//		record.setResourceKey(resourceKey);
		//		record.setResourceId("user key");
		//		
		//		Boolean bool = client.TestMethod(record);
		//		System.out.println(bool);	
	}



	

}
