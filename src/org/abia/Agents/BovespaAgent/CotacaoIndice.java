package org.abia.Agents.BovespaAgent;

import org.abia.Blackboard.*;

public class CotacaoIndice extends AgentData {
    
	protected AgentData Indice;
	protected AgentData Pregao;
	protected Double    Abertura;
	protected Double    Fechamento;
	protected Double    Maximo;
	protected Double    Minima;
	protected String    Responsavel;
  /**
   * <TODO: Comment for Association here>
   * 
   * @association <mypackage2.JavaAssociation2> org.abia.Agents.BovespaAgent.Indice
   */
  protected Indice indice0;
    
	public CotacaoIndice(){
		super();
	}
    
	public AgentData getIndice()    { return Indice;}
	public AgentData getPregao()    { return Pregao;}
	public Double    getAbertura()  { return Abertura;}
	public Double    getFechamento(){ return Fechamento;}
	public Double    getMaximo()    { return Maximo;}
	public Double    getMinima()    { return Minima;}
	public String    getResponsavel()    { return Responsavel;}

	public void setIndice(AgentData newIndice)       { Indice=newIndice;}
	public void setPregao(AgentData newPregao)     { Pregao=newPregao;}
	public void setAbertura(Double newAbertura)    { Abertura=newAbertura;}
	public void setFechamento(Double newFechamento){ Fechamento=newFechamento;}
	public void setMaximo(Double newMaximo)        { Maximo=newMaximo;}
	public void setMinima(Double newMinima)        { Minima=newMinima;}
	public void setResponsavel(String newResponsavel){ }

	public String getAgentDataNameInBlackboard(){
		return "CotacaoIndice";
	}
	
}