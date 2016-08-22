package org.abia.Agents.BovespaAgent;

import org.abia.Blackboard.*;

public class PapelEmIndice extends AgentData {
	
	protected AgentData PapelParticipante;
	protected AgentData Indice;
	protected Double    Quantidade;
	protected Double    Participacao;
  /**
   * <TODO: Comment for Association here>
   * 
   * @association <mypackage2.JavaAssociation8> org.abia.Agents.BovespaAgent.Indice
   */
  protected Indice indice1;
  /**
   * <TODO: Comment for Association here>
   * 
   * @association <mypackage2.JavaAssociation15> org.abia.Agents.BovespaAgent.Papel
   */
  protected Papel _;

	public AgentData getPapelParticipante(){ return PapelParticipante;}
	public AgentData getIndice(){ return Indice;}
	public Double    getQuantidade(){ return Quantidade;}
	public Double    getParticipacao(){ return Participacao;}

	public void      setPapelParticipante(AgentData newPapelParticipante){ PapelParticipante=newPapelParticipante; }
	public void      setIndice(AgentData newIndice){ Indice=newIndice;}
	public void      setQuantidade(Double newQuantidade){ Quantidade=newQuantidade;}
	public void      setParticipacao(Double newParticipacao){ Participacao=newParticipacao;}
	
	public PapelEmIndice(){
		super();
	}
	
	public String getAgentDataNameInBlackboard(){
		return "PapelEmIndice";
	}
}