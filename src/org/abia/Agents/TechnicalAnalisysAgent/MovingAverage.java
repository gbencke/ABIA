package org.abia.Agents.TechnicalAnalisysAgent;

import org.abia.Blackboard.*;
import org.abia.Agents.BovespaAgent.Cotacao;

public class MovingAverage extends AgentData {	
	protected AgentData Papel;
	protected AgentData Indice;
	protected AgentData Pregao;
	protected Integer   Periodo;
	protected Double    Valor;	 
  /**
   * <TODO: Comment for Association here>
   * 
   * @association <mypackage2.JavaAssociation12> org.abia.Agents.BovespaAgent.Cotacao
   */
  protected Cotacao cotacao2;
	
	public MovingAverage(){
		super();		
	}
	
	public AgentData getPapel() { return Papel; }
	public AgentData getIndice() { return Indice; }
	public AgentData getPregao() { return Pregao; }
	public Integer   getPeriodo(){ return Periodo; }
	public Double    getValor()  { return Valor; }	

	public void      setPapel  (AgentData newPapel)   { Papel=newPapel; }
	public void      setIndice (AgentData newIndice)  { Indice=newIndice; }
	public void      setPregao (AgentData newPregao)  { Pregao=newPregao; }
	public void      setPeriodo(Integer newPeriodo)   { Periodo=newPeriodo; }
	public void      setValor  (Double newValor)      { Valor=newValor; }

	public String getAgentDataNameInBlackboard(){
		return "MovingAverage";
	}

}