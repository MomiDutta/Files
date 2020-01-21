package Ziroh.Chabby.ACL;

import java.security.Security;

public class KeyManagerClientTest {

	public static void main(String[] args) 
	{
		Security.setProperty("crypto.policy", "unlimited");
		UserCreationSample userCreationSample = new UserCreationSample();

		try
		{

			userCreationSample.keyManagerConsole();
			
		} 
		catch (Exception e) 
		{
		
			e.printStackTrace();
		}


	}

}
