package org.abia.Agents.PortfolioManagerAgent;

import org.abia.Blackboard.*;

public class NovoPortfolio extends AgentData {	
	public static int DEVO_VENDER_TUDO =0;
	public static int DEVO_AJUSTAR     =1;
	public static int DEVO_IGNORAR_NOVO=2;
	
	protected AgentData  Pregao;
	protected Double     Valor;
	protected Double     ValorDinheiro;
	protected Integer    DevoComprar;
	protected Double     RetornoEsperado;
	protected Double     RiscoEsperado;	 
	
	public NovoPortfolio(){
		super();		
	}
	
	public AgentData getPregao() { return Pregao; }
	public Double    getValor()  { return Valor; }
	public Double    getValorDinheiro()  { return Valor; }
	public Integer   getDevoComprar(){ return DevoComprar; }
	public Double    getRetornoEsperado(){ return RetornoEsperado; }
	public Double    getRiscoEsperado(){ return RetornoEsperado; }	

	public void      setPregao           (AgentData newPregao)         { Pregao=newPregao; }
	public void      setValor            (Double    newValor)          { Valor=newValor; }
	public void      setValorDinheiro    (Double    newValorDinheiro)  { ValorDinheiro=newValorDinheiro; }
	public void      setDevoComprar      (Integer   newDevoComprar)    { DevoComprar=newDevoComprar; }
	public void      setRetornoEsperado  (Double    newRetornoEsperado){ RetornoEsperado=newRetornoEsperado; }
	public void      setRiscoEsperado    (Double    newRiscoEsperado)  { RiscoEsperado=newRiscoEsperado; }	

	public String getAgentDataNameInBlackboard(){
		return "NovoPortfolio";
	}

}