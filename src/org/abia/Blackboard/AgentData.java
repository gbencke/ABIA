package org.abia.Blackboard;

import java.util.*;
import org.abia.AgentContainer.AgentContainerClient.*;

public abstract class AgentData {
	protected ClientConnection  ConnectionToAgentContainer=null;
	protected Long              Timestamp;
	protected Date              ValidFrom;
	protected Date              ValidTo;
	protected String            Origin;
	protected Boolean           Private;
	protected boolean           ReferencesResolved=false;   
		

	public AgentData(){
		Private=new Boolean(false);
		ValidTo=new Date();
		ValidFrom=new Date();
	}
		
	public void GenerateTimestamp(){
		Timestamp=new Long(Blackboard.getBlackboard().getNextTimestamp());
	}

	public void RetrieveTimestamp(int timestamp) throws BlackboardException {
		Timestamp=new Long(timestamp);
	}

	
	public Long getTimestamp(){
		if(Timestamp==null){
			Timestamp=new Long(-1);			
		}
		return Timestamp;
	}
	
	public Date getValidFrom(){
		return ValidFrom;
	}

	public Date getValidTo(){
		return ValidTo;
	}

	public String getOrigin(){
		return Origin;
	}

	public Boolean getPrivate(){
		return Private;
	}

	public void setValidFrom(Date newValidFrom){
		ValidFrom=newValidFrom;
	}

	public void setValidTo(Date newValidTo){
		ValidTo=newValidTo;
	}

	public void setOrigin(String newOrigin){
		Origin=newOrigin;
	}

	public void setPrivate(Boolean newPrivate){
		Private=newPrivate;
	}
    
	public void setConnectionToAgentContainer(ClientConnection Con){
		ConnectionToAgentContainer=Con;		
	}
    
    public abstract String getAgentDataNameInBlackboard();
 
    public void ResolveReferences(){
		
		if(ReferencesResolved) return;		
		try{
		   if(ConnectionToAgentContainer==null){
			  Blackboard.getBlackboard().ResolveReferences(this);
			  ReferencesResolved=true;		   
		   }else{
			  ConnectionToAgentContainer.ResolveReferences(this);		   	
		   }
		}catch(Exception e){
		   System.out.println(e);
		}
    }
    
    public String toString(){
    	return Timestamp.toString();
    }
    
    public void SetTimestampManually(int newTimestamp){
    	Timestamp=new Long(newTimestamp);
    }
 
}