package org.abia.Agents.PortfolioManagerAgent;

import org.abia.Blackboard.*;

public class Variacao extends AgentData {	
	protected AgentData Papel;
	protected AgentData Indice;
	protected AgentData Pregao;
	protected Double    Valor;	 
	
	public Variacao(){
		super();		
	}
	
	public AgentData getPapel() { return Papel; }
	public AgentData getIndice() { return Indice; }
	public AgentData getPregao() { return Pregao; }
	public Double    getValor()  { return Valor; }	

	public void      setPapel  (AgentData newPapel)   { Papel=newPapel; }
	public void      setIndice (AgentData newIndice)  { Indice=newIndice; }
	public void      setPregao (AgentData newPregao)  { Pregao=newPregao; }
	public void      setValor  (Double newValor)      { Valor=newValor; }

	public String getAgentDataNameInBlackboard(){
		return "Prediction";
	}

}