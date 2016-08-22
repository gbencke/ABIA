package org.abia.Agents.BovespaAgent;

import org.abia.Blackboard.*;

public class Papel extends AgentData {
	protected AgentData EmpresaEmitente;
	protected String  CodigoPregao;
	protected String  Tipo;
  /**
   * <TODO: Comment for Association here>
   * 
   * @association <mypackage2.JavaAssociation6> org.abia.Agents.BovespaAgent.Cotacao
   */
  protected Cotacao _[];

	public Papel(){
		super();
	}

	public AgentData getEmpresaEmitente(){ return EmpresaEmitente; }
	public String  getCodigoPregao(){ return CodigoPregao; }
	public String  getTipo(){ return Tipo; }

	public void  setEmpresaEmitente(AgentData newEmpresa) { EmpresaEmitente=newEmpresa; }
	public void  setCodigoPregao(String newCodigoPregao){ CodigoPregao=newCodigoPregao; }
	public void  setTipo(String newTipo)                { Tipo=newTipo; } 

	public String getAgentDataNameInBlackboard(){
		return "Papel";
	}

	
}