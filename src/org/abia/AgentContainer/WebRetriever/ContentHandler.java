package org.abia.AgentContainer.WebRetriever;


public abstract class ContentHandler {
	public abstract boolean IsContentHandled(String ContentType);
	public abstract byte[]   getContent();
	public abstract String[] getHeaders();	
}