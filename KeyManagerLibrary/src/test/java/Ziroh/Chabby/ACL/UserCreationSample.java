package Ziroh.Chabby.ACL;

import java.util.Scanner;

public class UserCreationSample
{

	public void keyManagerConsole() throws Exception
	{
		String cmd = null;
		KeyManagerShell shell = new KeyManagerShell();
		
		Scanner scanner = new Scanner(System.in);
		try
		{
			while(true)
			{
				System.out.println("KeyManager :/> ");
				cmd = scanner.next();
				if (cmd.equals("newuser"))
				{
					shell.SignUp();
				}
				if(cmd.equals("admin"))
				{
					shell.SignupAdmin();
				}
				if(cmd.equals("signin"))
				{
					shell.Signin();
				}
				if(cmd.equals("sharekey"))
				{
					shell.ShareKey();
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			scanner.close();
		}
		
	}

}
