package org.abia.Blackboard;

public class DummyAgentData extends AgentData {
	public DummyAgentData(int timestamp){
		Timestamp=new Long(timestamp);		
	}
	
	public String getAgentDataNameInBlackboard(){
		return "";
	}
	
	public void setTimestamp(int timestamp){
		Timestamp=new Long(timestamp);		
	}
	
}