package org.abia.Agents.FundamentalAnalisysAgent;

import org.abia.Blackboard.*;

public class GiroDoAtivo extends AgentData {	
	
	protected AgentData Balancete;
	protected Double    Valor;
	
	public AgentData getBalancete(){ return Balancete; }
	public Double    getValor    (){ return Valor; }	

	public void    setBalancete(AgentData newBalancete){ Balancete=newBalancete; }
	public void    setValor    (Double    newValor)    { Valor=newValor; }
	
	
	public GiroDoAtivo(){
		super();		
	}
	

	public String getAgentDataNameInBlackboard(){
		return "GiroDoAtivo";
	}

}
