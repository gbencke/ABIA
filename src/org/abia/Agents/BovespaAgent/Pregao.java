package org.abia.Agents.BovespaAgent;

import org.abia.Blackboard.*;
import java.util.*;

public class Pregao extends AgentData {
	protected String Bolsa;
	protected Date   Data;
  /**
   * <TODO: Comment for Association here>
   * 
   * @association <mypackage2.JavaAssociation7> org.abia.Agents.BovespaAgent.Cotacao
   */
  protected Cotacao _[];
	
	public Pregao(){
		super();
	}
	
	public String getBolsa(){
		return Bolsa;
	}

	public void setBolsa(String newBolsa){
		Bolsa=newBolsa;
	}

	public Date getData(){
		return Data;
	}

	public void setData(Date newData){
		Data=newData;
	}

	public String getAgentDataNameInBlackboard(){
		return "Pregao";
	}

	
}