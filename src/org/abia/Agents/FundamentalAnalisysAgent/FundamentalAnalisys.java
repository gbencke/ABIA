package org.abia.Agents.FundamentalAnalisysAgent;

import org.abia.Blackboard.*;

public class FundamentalAnalisys extends AgentData {	
	protected AgentData Balancete;
	protected Double    Valor;
	protected String    Tipo;
	protected String    Descricao;
	
	public AgentData getBalancete() { return Balancete; }
	public Double    getValor    () { return Valor; }	
	public String    getTipo     () { return Tipo; }
	public String    getDescricao() { return Descricao; }


	public void    setBalancete(AgentData newBalancete){ Balancete=newBalancete; }
	public void    setValor    (Double    newValor)    { Valor=newValor; }
	public void    setTipo     (String    newTipo)     { Tipo=newTipo; }
	public void    setDescricao(String    newDescricao){ Descricao=newDescricao; }
	
	public FundamentalAnalisys(){
		super();		
	}
	

	public String getAgentDataNameInBlackboard(){
		return "FundamentalAnalisys";
	}

}