package org.abia.Agents.TechnicalAnalisysAgent;

import org.abia.AgentContainer.*;
import org.abia.Blackboard.*;
import org.abia.utils.CLIPServer.*;
import org.abia.Agents.BovespaAgent.Cotacao;

public class StochasticOscillator extends AgentData {
	protected AgentData Papel;
	protected AgentData Indice;
	protected AgentData Pregao;
	protected Integer   Periodo;
	protected Double    K;
	protected Double    D;	

	public AgentData getPapel(){ return Papel;}
	public AgentData getIndice(){ return Indice;}
	public AgentData getPregao(){ return Pregao;}
	public Integer   getPeriodo(){ return Periodo;}
	public Double    getK(){ return K;}
	public Double    getD(){ return D;}

	public void setPapel(AgentData newPapel){ Papel=newPapel;}
	public void setIndice(AgentData newIndice){ Indice=newIndice;}
	public void setPregao(AgentData newPregao){ Pregao=newPregao;}
	public void setPeriodo(Integer newPeriodo){ Periodo=newPeriodo;}
	public void setK(Double newK){ K=newK;}
	public void setD(Double newD){ D=newD;}

	public String getAgentDataNameInBlackboard(){
		return "StochasticOscillator";
	}

}