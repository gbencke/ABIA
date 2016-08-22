package org.abia.AgentContainer.WebRetriever;

import java.net.*;
import java.util.*;

public class ZIPContentHandler extends ContentHandler {
	protected URLConnection currentConnection;
	
	public boolean IsContentHandled(String Content){
		return true;
	}
	
	public ZIPContentHandler(URLConnection MyConection){
		currentConnection=MyConection;		
	}

	public byte[]   getContent(){ 
		byte Buffer[]=null;
		int  BytesRead=0,TotalBytes=0;
		try{		
		   Buffer=new byte[currentConnection.getContentLength()];
		   while(TotalBytes<currentConnection.getContentLength()){		
		         BytesRead=currentConnection.getInputStream().read(Buffer,TotalBytes,currentConnection.getContentLength()-TotalBytes);
		         TotalBytes+=BytesRead;
		   }
		}catch(Exception e){
			
		}
		return Buffer;
	}
	public String[] getHeaders(){
		int      f,Count=0;
		String   HeadersToReturn[]=null,Temp;;
		Iterator i;
				
		try{
			Count=currentConnection.getHeaderFields().values().size();
			HeadersToReturn=new String[Count];
			for(f=0;f<Count;f++){
				HeadersToReturn[f]=currentConnection.getHeaderFieldKey(f)+":"+currentConnection.getHeaderField(f);        		
			}
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
		}         
		return HeadersToReturn;	
	}
	
	
}