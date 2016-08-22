package org.abia.Agents.PortfolioManagerAgent;

import org.abia.Blackboard.*;

public class PapelEmPortfolio extends AgentData {	
	protected AgentData  Portfolio;
	protected AgentData  Papel;
	protected Double     Quantidade;
	protected Double     ValorDinheiro; 
	
	public PapelEmPortfolio(){
		super();		
	}
	
	public AgentData getPortfolio()      { return Portfolio; }
	public AgentData getPapel()          { return Papel; }	
	public Double    getQuantidade()     { return Quantidade; }
	public Double    getValorDinheiro()  { return ValorDinheiro; }	

	public void      setPortfolio      (AgentData newPortfolio)    { Portfolio=newPortfolio; }
	public void      setPapel          (AgentData newPapel)        { Papel=newPapel; }
	public void      setQuantidade     (Double    newQuantidade)   { Quantidade=newQuantidade; }
	public void      setValorDinheiro  (Double    newValorDinheiro){ ValorDinheiro=newValorDinheiro; }

	public String getAgentDataNameInBlackboard(){
		return "PapelEmPortfolio";
	}

}