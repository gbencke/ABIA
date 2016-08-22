package org.abia.Agents.PortfolioManagerAgent;

import org.abia.Blackboard.*;

public class Portfolio extends AgentData {	
	protected AgentData  Pregao;
	protected Double     Valor;
	protected Double     ValorDinheiro;
	protected Integer    DevoComprar;	 
	
	public Portfolio(){
		super();		
	}
	
	public AgentData getPregao() { return Pregao; }
	public Double    getValor()  { return Valor; }
	public Double    getValorDinheiro()  { return Valor; }
	public Integer   getDevoComprar(){ return DevoComprar; }	

	public void      setPregao       (AgentData newPregao)         { Pregao=newPregao; }
	public void      setValor        (Double    newValor)          { Valor=newValor; }
	public void      setValorDinheiro(Double    newValorDinheiro)  { ValorDinheiro=newValorDinheiro; }
	public void      setDevoComprar  (Integer   newDevoComprar)    { DevoComprar=newDevoComprar; }

	public String getAgentDataNameInBlackboard(){
		return "Portfolio";
	}

}