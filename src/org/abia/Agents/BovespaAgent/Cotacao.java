package org.abia.Agents.BovespaAgent;

import org.abia.Blackboard.*;

public class Cotacao extends AgentData {
    
    protected AgentData Papel;
    protected AgentData Pregao;
    protected Double    Abertura;
	protected Double    Fechamento;
	protected Double    Maximo;
	protected Double    Minima;
	protected Double    Negocios;
	protected Double    Volume;
	protected String    Responsavel;
    
    public Cotacao(){
    	super();
    }
    
    public AgentData getPapel()        { return Papel;}
	public AgentData getPregao()       { return Pregao;}
	public Double    getAbertura()     { return Abertura;}
	public Double    getFechamento()   { return Fechamento;}
	public Double    getMaximo()       { return Maximo;}
	public Double    getMinima()       { return Minima;}
	public Double    getNegocios()     { return Negocios;}
	public Double    getVolume()       { return Volume;}
	public String    getResponsavel()  { return Responsavel;}

	public void setPapel(AgentData newPapel)       { Papel=newPapel;}
	public void setPregao(AgentData newPregao)     { Pregao=newPregao;}
	public void setAbertura(Double newAbertura)    { Abertura=newAbertura;}
	public void setFechamento(Double newFechamento){ Fechamento=newFechamento;}
	public void setMaximo(Double newMaximo)        { Maximo=newMaximo;}
	public void setMinima(Double newMinima)        { Minima=newMinima;}
	public void setNegocios(Double newNegocios)    { Negocios=newNegocios;}
	public void setVolume(Double newVolume)        { Volume=newVolume;}

	public void setResponsavel(String newResponsavel){ Responsavel=newResponsavel; }


	public String getAgentDataNameInBlackboard(){
		return "Cotacao";
	}
	
}