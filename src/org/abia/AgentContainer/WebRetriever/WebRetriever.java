package org.abia.AgentContainer.WebRetriever;

import java.net.*;

public class WebRetriever {
	
	public WebRetriever(){
		
	}
	
	public WebRetrieverClient createClient(){
		WebRetrieverClient newWebRetrieverClient;
		
		newWebRetrieverClient=new WebRetrieverClient(this);
		return newWebRetrieverClient; 		   		
	}
	
	public URL RetrieveURL(String theURL) throws WebRetrieverException{
		URL theNewURL=null;
		
		try{		
			theNewURL=new URL(theURL);		 
			theNewURL.openConnection().connect();
		}catch(Exception e){
			System.out.println(e);		
			e.printStackTrace();
		}
		return theNewURL;
	}
	
}