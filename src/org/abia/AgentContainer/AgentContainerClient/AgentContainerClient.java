package org.abia.AgentContainer.AgentContainerClient;

import java.util.*;


public class AgentContainerClient {
	protected static AgentContainerClient  theContainerClient=null;
	protected static boolean               Initialized=false;
	protected HashMap                      ClassesRegistered=new HashMap();
	
	protected void Initialize() throws AgentContainerClientException {
		
	}
	
	public static ClientConnection createClientConnection(String host,int Port)
	throws AgentContainerClientException
	{
		ClientConnection ReturnedClientConnection;		
		ReturnedClientConnection=new ClientConnection(host,Port); 
		return ReturnedClientConnection;
	}
}