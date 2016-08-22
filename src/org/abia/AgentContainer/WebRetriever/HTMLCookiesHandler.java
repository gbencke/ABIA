package org.abia.AgentContainer.WebRetriever;

import java.util.*;

public class HTMLCookiesHandler {
	protected Vector Keys,Values;
	
	public HTMLCookiesHandler(){
		Keys=new Vector();
		Values=new Vector();
	}
	
	public void resetCookies(){
		Keys=new Vector();
		Values=new Vector();
	}
	
	public void addValue(String Chave,String Valor){
		Keys.add(Chave);
		Values.add(Valor);		
	}
	
	public String toString(){
		String toReturn="";
		int f;
		
		if(Keys.size()==0) return "";
		for(f=0;f<Keys.size();f++){
			toReturn+=Keys.get(f)+"="+Values.get(f)+"; ";
		}
		toReturn=toReturn.substring(0,toReturn.length()-2);
		
		return toReturn;
	}
	
	
	
}