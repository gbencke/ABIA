package org.abia.AgentContainer.WebRetriever;

import java.util.*;
import java.net.*;

public class WebRetrieverClient {
	protected WebRetriever       currentRetriever;
	protected HashMap            RegisteredContentHandlersClass=new HashMap();
	protected HashMap            RegisteredContentHandlersString=new HashMap();
	protected HTMLCookiesHandler currentCokiesHandler;
	
	public HTMLCookiesHandler getCookiesHandler(){
		return currentCokiesHandler;
	}
	
	
	public WebRetrieverClient(WebRetriever Retriever){
		currentRetriever=Retriever;
		currentCokiesHandler=new HTMLCookiesHandler();
	}
	
	public void RegisterContentHandler(String ContentToHandle,Class Content){
		if(RegisteredContentHandlersClass.get(ContentToHandle)==null){
			RegisteredContentHandlersClass.put(ContentToHandle,Content);
			RegisteredContentHandlersString.put(ContentToHandle,ContentToHandle);
		}
	}
		
	public ContentHandler getURL(String theURL,String CookiesToAdd) throws WebRetrieverException {		
		int                f;
		URL                RetrievedURL;
		URLConnection      ConnectionToURL;
		HttpURLConnection  HTTPConnection;
		Iterator           i,h;
		String             TypeContent,FirstComparison,SecondComparison;
		
		
		try{
			RetrievedURL=currentRetriever.RetrieveURL(theURL);			
			ConnectionToURL=RetrievedURL.openConnection();
			if(ConnectionToURL instanceof HttpURLConnection){				
				HTTPConnection=(HttpURLConnection)ConnectionToURL;
				Class          TargetClass;

				if(((currentCokiesHandler.toString()).length())>0)
				   HTTPConnection.addRequestProperty("Cookie",currentCokiesHandler.toString());
				i=RegisteredContentHandlersClass.values().iterator();
				h=RegisteredContentHandlersString.values().iterator();
				while(i.hasNext()){
					TypeContent=(String)h.next();
					TargetClass=(Class)i.next();
					FirstComparison=HTTPConnection.getContentType().toUpperCase();
					SecondComparison=TypeContent.toUpperCase();
					
					if(FirstComparison.indexOf(SecondComparison)>-1){
						Object ParametersOk[];
						Class  ParametersToConstructor[];
						
						ParametersOk=new Object[1];
						ParametersToConstructor=new Class[1];
						
						ParametersOk[0]=HTTPConnection;
						ParametersToConstructor[0]=URLConnection.class;
						
						
						return (ContentHandler)TargetClass.getConstructor(ParametersToConstructor).newInstance(ParametersOk);						
					}					
				}			
			}
		}catch(Exception e){
			System.out.println(e);					
			e.printStackTrace();
		}
		return null;
	}
	
}