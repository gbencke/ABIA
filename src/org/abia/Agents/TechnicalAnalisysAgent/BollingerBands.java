package org.abia.Agents.TechnicalAnalisysAgent;

import org.abia.AgentContainer.*;
import org.abia.Blackboard.*;
import org.abia.utils.CLIPServer.*;
import org.abia.Agents.BovespaAgent.Cotacao;

public class BollingerBands extends AgentData {
	protected AgentData Papel;
	protected AgentData Indice;
	protected AgentData Pregao;
	protected Integer   Periodo;
	protected Double    lowerBand;
	protected Double    middleBand;
	protected Double    upperBand;
	protected Double    Fechamento;	

	public AgentData getPapel(){ return Papel;}
	public AgentData getIndice(){ return Indice;}
	public AgentData getPregao(){ return Pregao;}
	public Integer   getPeriodo(){ return Periodo;}
	public Double    getlowerBand(){ return lowerBand;}
	public Double    getmiddleBand(){ return middleBand;}
	public Double    getupperBand(){ return upperBand;}
	public Double    getFechamento(){ return Fechamento;}

	public void setPapel(AgentData newPapel){ Papel=newPapel;}
	public void setIndice(AgentData newIndice){ Indice=newIndice;}
	public void setPregao(AgentData newPregao){ Pregao=newPregao;}
	public void setPeriodo(Integer newPeriodo){ Periodo=newPeriodo;}
	public void setlowerBand(Double newlowerBand){ lowerBand=newlowerBand;}
	public void setmiddleBand(Double newmiddleBand){ middleBand=newmiddleBand;}
	public void setupperBand(Double newupperBand){ upperBand=newupperBand;}
	public void setFechamento(Double newFechamento){ Fechamento=newFechamento;}

	public String getAgentDataNameInBlackboard(){
		return "BollingerBands";
	}

}