package org.abia.Agents.BovespaAgent;

import org.abia.Blackboard.*;

import java.util.*;

public class Indice extends AgentData {
	protected Date    DataAtualizacao;
	protected Integer CRCConteudo;
	protected Integer NumberPapeis;
	protected String  NomeIndice;
	protected String  CodigoPregao;
  /**
   * <TODO: Comment for Association here>
   * 
   * @association <mypackage2.JavaAssociation4> org.abia.Agents.BovespaAgent.Papel
   */
  protected Papel _[];

	public Date    getDataAtualizacao(){ return DataAtualizacao; }
	public Integer getCRCConteudo(){ return CRCConteudo; }
	public Integer getNumberPapeis(){ return NumberPapeis;}
	public String  getNomeIndice(){ return NomeIndice; }	  
	public String  getCodigoPregao(){ return CodigoPregao; }

	public void    setCodigoPregao(String newCodigoPregao){ CodigoPregao=newCodigoPregao;}
	public void    setDataAtualizacao(Date newDataAtualizacao){ DataAtualizacao=newDataAtualizacao; }
	public void    setCRCConteudo(Integer  newCRCConteudo){ CRCConteudo=newCRCConteudo; }
	public void    setNumberPapeis(Integer newNumberPapeis){ NumberPapeis=newNumberPapeis;}
	public void    setNomeIndice(String newNomeIndice){ NomeIndice=newNomeIndice; }	  

	public Indice(){
		super();
	}

	
	public String getAgentDataNameInBlackboard(){
		return "Indice";
	}
	
	
}