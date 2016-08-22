package org.abia.Agents.TechnicalAnalisysAgent;

import org.abia.AgentContainer.*;
import org.abia.Blackboard.*;
import org.abia.utils.CLIPServer.*;

public class TechnicalAnalisys extends AgentData {

	public Double Valor;
	public String Descricao;
	public AgentData Papel;
	public AgentData Pregao;
	
	public Double    getValor() { return Valor;}
	public String    getDescricao(){ return Descricao;}
	public AgentData getPapel()    { return Papel;}
	public AgentData getPregao()   { return Pregao;}

	public void setValor    (Double newValor)     { Valor=newValor;}
	public void setDescricao(String newDescricao){ Descricao=newDescricao;}
	public void setPapel    (AgentData     newPapel)    { Papel=newPapel;}
	public void setPregao   (AgentData    newPregao)   { Pregao=newPregao;}
	
	public String getAgentDataNameInBlackboard(){
		return "TechnicalAnalisys";
	}

}