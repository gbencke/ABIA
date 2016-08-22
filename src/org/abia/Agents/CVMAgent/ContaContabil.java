package org.abia.Agents.CVMAgent;

import org.abia.Blackboard.*;

public class ContaContabil extends AgentData {
	protected AgentData Balancete;    
	protected String CodigoConta;
	protected String DescricaoConta;
	protected Double Valor;

	public void setBalancete(AgentData newBalancete){ Balancete=newBalancete; }
	public void setCodigoConta(String newCodigoConta){ CodigoConta=newCodigoConta; }
	public void setDescricaoConta(String newDescricaoConta){ DescricaoConta=newDescricaoConta; }
	public void setValor(Double newValor){ Valor=newValor; }

	public AgentData getBalancete(){ return Balancete; }
	public String getCodigoConta(){ return CodigoConta; }
	public String getDescricaoConta(){ return DescricaoConta; }
	public Double getValor(){ return Valor; }
    
	public String getAgentDataNameInBlackboard(){
		return "ContaContabil";
	}
	
}