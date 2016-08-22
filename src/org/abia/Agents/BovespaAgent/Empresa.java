package org.abia.Agents.BovespaAgent;

import org.abia.Blackboard.*;

public class Empresa extends AgentData {
	protected Integer CodigoCVM;
	protected String  NomePregao;
	protected String  NomeEmpresa;
  /**
   * <TODO: Comment for Association here>
   * 
   * @association <mypackage2.JavaAssociation5> org.abia.Agents.BovespaAgent.Papel
   */
  protected Papel _[];
    

	public Empresa(){
		super();
	}

    public Integer getCodigoCVM()  { return CodigoCVM;   }
	public String  getNomePregao() { return NomePregao;  }
	public String  getNomeEmpresa(){ return NomeEmpresa; }
	
	public void setCodigoCVM  (Integer newCodigoCVM){   CodigoCVM=newCodigoCVM;	  }
	public void setNomePregao (String  newNomePregao){  NomePregao=newNomePregao;  }
	public void setNomeEmpresa(String  newNomeEmpresa){ NomeEmpresa=newNomeEmpresa; }

	public String getAgentDataNameInBlackboard(){
		return "Empresa";
	}
} 



