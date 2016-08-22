package org.abia.AgentContainer.WebRetriever;

public class HTMLFilter {
	public static int INSERT=0;
	public static int DELETE=1;
	public static int ACCEPT=2;
	public static int ACCEPT_KEEP_ATTRIBUTES=3;
	public static int DECLINE=4;

		
	public String TagElement;
	public int    Behaviour;
	public String AddToEnd;
	
	public HTMLFilter(String newTagElement,int newBehaviour,String newAddToEnd){
		Behaviour=newBehaviour;
		TagElement=newTagElement;
		AddToEnd=newAddToEnd;		
	}
	
	
}