package org.abia.Agents.PortfolioManagerAgent;

import org.abia.Blackboard.*;

public class NovoPapelEmPortfolio extends AgentData {	
	protected AgentData  Pregao;
	protected AgentData  Papel;
	protected Double     Porcentagem; 
	
	public NovoPapelEmPortfolio(){
		super();		
	}
	
	public AgentData getPregao()      { return Pregao; }
	public AgentData getPapel()          { return Papel; }	
	public Double    getPorcentagem()     { return Porcentagem; }	

	public void      setPregao         (AgentData newPregao)    { Pregao=newPregao; }
	public void      setPapel          (AgentData newPapel)        { Papel=newPapel; }
	public void      setPorcentagem    (Double    newPorcentagem)   { Porcentagem=newPorcentagem; }

	public String getAgentDataNameInBlackboard(){
		return "NovoPapelEmPortfolio";
	}

}