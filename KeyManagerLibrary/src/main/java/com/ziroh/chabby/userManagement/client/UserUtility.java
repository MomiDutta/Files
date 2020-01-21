package com.ziroh.chabby.userManagement.client;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * This class is used for credentials validation
 */
class UserUtility 
{
	String regexPatternPassword;
	String regexPatternPassPhrase;
	String USERNAME_PATTERN ;
	private Pattern pattern;
	private Matcher matcher;
	
	/**
	 * UserUtility constructor
	 */
	  public UserUtility()
      {
          regexPatternPassword =  "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\da-zA-Z]).{8,15}$" ;
          regexPatternPassPhrase = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\da-zA-Z]).{8,15}$";
          USERNAME_PATTERN = "^[a-zA-Z0-9_-]{5,20}$";
          pattern = Pattern.compile(USERNAME_PATTERN);
          
      }
	  
	  
	  
	  /**
	   * This method is used to validate UserID
	   * @param user User name is required
	   * @return boolean response
	   */
	  public boolean ValidateUserId(String user)
	  {
		  boolean isValid = false;
		  
		  if(user==null || StringUtils.isEmpty(user))
		  {
			  isValid = false;
		  }
		  else
		  {
			  matcher = pattern.matcher(user);
			  boolean match = matcher.matches();
			  if(match==true)
			  {
				  isValid=true;
			  }
		  }
		  return isValid;
	  }

	  /**
	   * This method is used to validate Password
	   * @param password Password is required
	   * @return boolean response
	   */
	public boolean isPasswordValid(String password) 
	{
		boolean isValid = false;
		if(password!=null)
		{
			if(password.equals(""))
			{
				throw new IllegalArgumentException("Password cannot be empty");
			}
			
			boolean match = Pattern.matches(regexPatternPassword, password);
			if(match == true)
			{
				isValid = true;
			}
		}
		else
		{
			isValid = false;
			
		}
		
		return isValid;
	}
}
