package org.abia.Agents.TechnicalAnalisysAgent;

import org.abia.Blackboard.*;
import org.abia.Agents.BovespaAgent.Cotacao;

public class Trend extends AgentData {
    public static int TREND_UNDEFINED   =0;
	public static int TREND_UPWARD      =1;
	public static int TREND_DOWNWARD    =2;
	public static int TREND_SUPPORT     =3;
	public static int TREND_RESISTENCE  =4;
	
    public Integer   Direcao;
    public AgentData Indice;
	public AgentData Papel;
	public AgentData Pregao;
  /**
   * <TODO: Comment for Association here>
   * 
   * @association <mypackage2.JavaAssociation11> org.abia.Agents.BovespaAgent.Cotacao
   */
  protected Cotacao cotacao1;
	
	public Trend(){
		Direcao=new Integer(0);		
	}

    public void setDirecao(Integer newDirecao){ Direcao=newDirecao; }
	public void setIndice(AgentData newIndice){ Indice=newIndice; }
	public void setPapel(AgentData newPapel){ Papel=newPapel; }
	public void setPregao(AgentData newPregao){ Pregao=newPregao; }

    public Integer getDirecao(){ return Direcao;  }
	public AgentData getIndice(){ return Indice;  }
	public AgentData getPapel(){ return Papel;  }
	public AgentData getPregao(){ return Pregao;  }	

	public String getAgentDataNameInBlackboard(){
		return "Trend";
	}

}