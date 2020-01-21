package com.ziroh.chabby.audit.common;

/**
 * This class represents a single Log object.
 */
public class LogMessage
{
	/**
	 * LogMessage Constructor
	 */
	public LogMessage()
	{
		
	}
	
	/**
	 * In Unix Time Stamp
	 */
	public int TimeStamp ;

	/**
	 * Get Time Stamp
	 * @return Unix TimeStamp
	 */
	public int getTimeStamp() {
		return TimeStamp;
	}

	/**
	 * Set TimeStamp
	 * @param timeStamp TimeStamp is required
	 */
	public void setTimeStamp(int timeStamp) {
		TimeStamp = timeStamp;
	}
	
	/**
	 * Name of the host that has generated this log
	 */
	public String HostName;

	/**
	 * Get HostName
	 * @return HostName
	 */
	public String getHostName() {
		return HostName;
	}

	/**
	 * Set HostName
	 * @param hostName HostName is required
	 */
	public void setHostName(String hostName) {
		HostName = hostName;
	}
	
	/**
	 * Process that generated the log
	 */
	public String Process_Name;

	/**
	 * Get Process_Name
	 * @return Process_Name
	 */
	public String getProcess_Name() {
		return Process_Name;
	}

	/**
	 * Set Process_Name
	 * @param process_Name Process_Name is required
	 */
	public void setProcess_Name(String process_Name) {
		Process_Name = process_Name;
	}
	
//	public SeverityLevel Syslog_Level ;
//
//	public SeverityLevel getSyslog_Level() {
//		return Syslog_Level;
//	}
//
//	public void setSyslog_Level(SeverityLevel syslog_Level) {
//		Syslog_Level = syslog_Level;
//	}
	
	/**
	 * Log Message
	 */
	public String Message_Itself;

	/**
	 * Get Log Message
	 * @return Message_Itself
	 */
	public String getMessage_Itself() {
		return Message_Itself;
	}

	/**
	 * Set Message_Itself
	 * @param message_Itself Message_Itself is required
	 */
	public void setMessage_Itself(String message_Itself) {
		Message_Itself = message_Itself;
	}
	
	/**
	 * LogMessage constructor
	 * @param ProcessName ProcessName is required
	 */
	public LogMessage(String ProcessName)
    {
        Process_Name = ProcessName;
    }
	
	/**
	 * LogMessage constructor
	 * @param ProcessName ProcessName is required
	 * @param Message Message is required
	 */
	public LogMessage(String ProcessName, String Message)
    {
        this.Message_Itself = Message;
        this.Process_Name = ProcessName;
    }
	
	
	
}
