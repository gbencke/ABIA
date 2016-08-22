package org.abia.Agents.CVMAgent;

import org.abia.Blackboard.*;
import java.util.*;
import org.abia.Agents.BovespaAgent.Empresa;

public class Balancete extends AgentData {
	
	public Balancete(){
		CodigoCVM="";
		DenominacaoSocial="";
		CNPJ="";
		NIRE="";
		EnderecoCompleto="";
		Bairro="";
		CEP="";
		Municipio="";
		UF="";
		DDD="";
		Telefone1="";
		Telex="";
		Fax="";
		TipoEmpresa="";
		TipoSituacao="";
		NaturezaControleAcionario="";
		CodigoAtividade="";
		AtividadePrincipal="";
		TipoConsolidado="";        
		CapitalIntegralizadoOrdinarias=new Double(0);
		CapitalIntegralizadoPreferenciais=new Double(0);
		CapitalIntegralizadoTotal=new Double(0);
		EmTesourariaOrdinarias=new Double(0);
		EmTesourariaPreferenciais=new Double(0);
		EmTesourariaTotal=new Double(0);
		NumeroAcoes=new Double(0);
		LucroPorAcao=new Double(0);
		PrejuizoPorAcao=new Double(0);
	}
	
	protected Date      DataBalancete;
	protected String    StrDataBalancete;
    protected AgentData EmpresaEmitente;
    
    protected String CodigoCVM;
	protected String DenominacaoSocial;
	protected String CNPJ;
	protected String NIRE;
	
	protected String EnderecoCompleto;
	protected String Bairro;
	protected String CEP;
	protected String Municipio;
	protected String UF;
	protected String DDD;
	protected String Telefone1;
	protected String Telex;
	protected String Fax;
	
	protected String TipoEmpresa;
	protected String TipoSituacao;
	protected String NaturezaControleAcionario;
	protected String CodigoAtividade;
	protected String AtividadePrincipal;
	protected String TipoConsolidado;
        
	protected Double CapitalIntegralizadoOrdinarias;
	protected Double CapitalIntegralizadoPreferenciais;
	protected Double CapitalIntegralizadoTotal;
	protected Double EmTesourariaOrdinarias;
	protected Double EmTesourariaPreferenciais; 
	protected Double EmTesourariaTotal;

	protected Double NumeroAcoes;
	protected Double LucroPorAcao;
	protected Double PrejuizoPorAcao;
  /**
   * <TODO: Comment for Association here>
   * 
   * @association <mypackage2.JavaAssociation1> org.abia.Agents.CVMAgent.ContaContabil
   */
  protected ContaContabil _[];
  /**
   * <TODO: Comment for Association here>
   * 
   * @association <mypackage2.JavaAssociation9> org.abia.Agents.BovespaAgent.Empresa
   */
  protected Empresa empresa1;

	public AgentData getEmpresaEmitente(){ return EmpresaEmitente; }
	public void      setEmpresaEmitente(AgentData newEmpresaEmitente){ EmpresaEmitente=newEmpresaEmitente; } 

	public String getStrDataBalancete(){ return StrDataBalancete; }
	public String getCodigoCVM(){  return CodigoCVM; }
	public String getDenominacaoSocial(){  return DenominacaoSocial; }
	public String getCNPJ(){  return CNPJ; }
	public String getNIRE(){  return NIRE; }
	
	public String getEnderecoCompleto(){  return EnderecoCompleto; }
	public String getBairro(){  return Bairro; }
	public String getCEP(){  return CEP; }
	public String getMunicipio(){  return Municipio; }
	public String getUF(){  return UF; }
	public String getDDD(){  return DDD; }
	public String getTelefone1(){  return Telefone1; }
	public String getTelex(){  return Telex; }
	public String getFax(){  return Fax; }
	
	public String getTipoEmpresa(){  return TipoEmpresa; }
	public String getTipoSituacao(){  return TipoSituacao; }
	public String getNaturezaControleAcionario(){  return NaturezaControleAcionario; }
	public String getCodigoAtividade(){  return CodigoAtividade; }
	public String getAtividadePrincipal(){  return AtividadePrincipal; }
	public String getTipoConsolidado(){  return TipoConsolidado; }
        
	public Double getCapitalIntegralizadoOrdinarias(){  return CapitalIntegralizadoOrdinarias; }
	public Double getCapitalIntegralizadoPreferenciais(){  return CapitalIntegralizadoPreferenciais; }
	public Double getCapitalIntegralizadoTotal(){  return CapitalIntegralizadoTotal; }
	public Double getEmTesourariaOrdinarias(){  return EmTesourariaOrdinarias; }
	public Double getEmTesourariaPreferenciais(){  return EmTesourariaPreferenciais; }
	public Double getEmTesourariaTotal(){  return EmTesourariaTotal; }

	public Double getNumeroAcoes(){  return NumeroAcoes; }
	public Double getLucroPorAcao(){  return LucroPorAcao; }
	public Double getPrejuizoPorAcao(){  return PrejuizoPorAcao; }

    public void setStrDataBalancete(String newStrDataBalancete){ StrDataBalancete=newStrDataBalancete; }
	public void setCodigoCVM(String newCodigoCVM){  CodigoCVM=newCodigoCVM; }
	public void setDenominacaoSocial(String newDenominacaoSocial){  DenominacaoSocial=newDenominacaoSocial; }
	public void setCNPJ(String newCNPJ){  CNPJ=newCNPJ; }
	public void setNIRE(String newNIRE){  NIRE=newNIRE; }
	
	public void setEnderecoCompleto(String newEnderecoCompleto){  EnderecoCompleto=newEnderecoCompleto; }
	public void setBairro(String newBairro){  Bairro=newBairro; }
	public void setCEP(String newCEP){  CEP=newCEP; }
	public void setMunicipio(String newMunicipio){  Municipio=newMunicipio; }
	public void setUF(String newUF){  UF=newUF; }
	public void setDDD(String newDDD){  DDD=newDDD; }
	public void setTelefone1(String newTelefone1){  Telefone1=newTelefone1; }
	public void setTelex(String newTelex){  Telex=newTelex; }
	public void setFax(String newFax){  Fax=newFax; }
	
	public void setTipoEmpresa(String newTipoEmpresa){  TipoEmpresa=newTipoEmpresa; }
	public void setTipoSituacao(String newTipoSituacao){  TipoSituacao=newTipoSituacao; }
	public void setNaturezaControleAcionario(String newNaturezaControleAcionario){  NaturezaControleAcionario=newNaturezaControleAcionario; }
	public void setCodigoAtividade(String newCodigoAtividade){  CodigoAtividade=newCodigoAtividade; }
	public void setAtividadePrincipal(String newAtividadePrincipal){  AtividadePrincipal=newAtividadePrincipal; }
	public void setTipoConsolidado(String newTipoConsolidado){  TipoConsolidado=newTipoConsolidado; }
        
	public void setCapitalIntegralizadoOrdinarias(Double newCapitalIntegralizadoOrdinarias){  CapitalIntegralizadoOrdinarias=newCapitalIntegralizadoOrdinarias; }
	public void setCapitalIntegralizadoPreferenciais(Double newCapitalIntegralizadoPreferenciais){  CapitalIntegralizadoPreferenciais=newCapitalIntegralizadoPreferenciais; }
	public void setCapitalIntegralizadoTotal(Double newCapitalIntegralizadoTotal){  CapitalIntegralizadoTotal=newCapitalIntegralizadoTotal; }
	public void setEmTesourariaOrdinarias(Double newEmTesourariaOrdinarias){  EmTesourariaOrdinarias=newEmTesourariaOrdinarias; }
	public void setEmTesourariaPreferenciais(Double newEmTesourariaPreferenciais){  EmTesourariaPreferenciais=newEmTesourariaPreferenciais; }
	public void setEmTesourariaTotal(Double newEmTesourariaTotal){  EmTesourariaTotal=newEmTesourariaTotal; }

	public void setNumeroAcoes(Double newNumeroAcoes){  NumeroAcoes=newNumeroAcoes; }
	public void setLucroPorAcao(Double newLucroPorAcao){  LucroPorAcao=newLucroPorAcao; }
	public void setPrejuizoPorAcao(Double newPrejuizoPorAcao){  PrejuizoPorAcao=newPrejuizoPorAcao; }

	public String getAgentDataNameInBlackboard(){
		return "Balancete";
	}
	
}