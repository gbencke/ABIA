package org.abia.Agents.BovespaAgent;

import org.abia.Blackboard.*;

public class TesteClass extends AgentData {
	protected String Nome;
	protected String End;
	protected Integer CPF;

	public void setNome(String NomeEnt){
		
	}

	public void setEnd(String EndEnt){
		
	}

	public void setCPF(Integer CPFEnt){
		
	}

	
    public String getNome(){
    	return Nome;
    }
	
	public String getEnd(){
		return End;
	}

	public Integer getCPF(){
		return CPF;
	}
	
	public String getAgentDataNameInBlackboard(){
		return "TesteClass";
	}
	
	
};