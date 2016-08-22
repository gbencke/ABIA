package org.abia.Agents.CVMAgent;

/*			CurrentURL=TotalURL+"Assunto=ProventosDinheiro";
			obj=theClient.getURL(CurrentURL,null);		    	
			if(obj instanceof HTMLContentHandler){
			   HTMLContentHandler HTMLHandler;
					  
			   HTMLHandler=(HTMLContentHandler)obj;
			   if(HTMLHandler.getResponseStatus()!=
				  HttpURLConnection.HTTP_OK){
				  return false;
			   }
			   Retorno=HTMLHandler.getContentAsString();
			   if(Retorno.indexOf("Possui Dados para Carregar")==-1){ 
				  NovoRetorno=HTMLContentHandler.ApplyFilter(Retorno,FiltersToApply,HTMLContentHandler.DEFAULT_DELETE); 
				  TableOk=HTMLContentHandler.RetrieveTable(NovoRetorno,"1.1");		       
				  NumberTRs=HTMLContentHandler.GetNumberOfRows(TableOk);
			   }			   
			}
*/

import org.abia.AgentContainer.*;
import org.abia.Blackboard.*;
import org.abia.Agents.BovespaAgent.*;
import org.abia.AgentContainer.WebRetriever.*;
import org.abia.Blackboard.PostgreSQL.*;

import java.util.*;
import java.net.*;
import java.io.*;
import java.util.*;

public class CVMAgent extends WebMiningAgent {
	protected String               BalancetesITR[],BalancetesDTR[];
	protected HashMap              FiltersToApply;
	protected HTMLFilter           CurrentFilter;
	protected WebRetrieverClient   theClient=null;
	
	static {
		System.out.println("Carregado:CVMAgent");		
	}
	
	public CVMAgent(){
		BalancetesITR=new String[20];
		
		BalancetesITR[0]=new String("31031999");
		BalancetesITR[1]=new String("30061999");
		BalancetesITR[2]=new String("30091999");;
		BalancetesITR[3]=new String("31121999");		
		BalancetesITR[4]=new String("31032000");
		BalancetesITR[5]=new String("30062000");
		BalancetesITR[6]=new String("30092000");;
		BalancetesITR[7]=new String("31122000");		
		BalancetesITR[8]=new String("31032001");
		BalancetesITR[9]=new String("30062001");
		BalancetesITR[10]=new String("30092001");;
		BalancetesITR[11]=new String("31122001");		
		BalancetesITR[12]=new String("31032002");
		BalancetesITR[13]=new String("30062002");
		BalancetesITR[14]=new String("30092002");;
		BalancetesITR[15]=new String("31122002");
		BalancetesITR[16]=new String("31032003");
		BalancetesITR[17]=new String("30062003");
		BalancetesITR[18]=new String("30092003");
		BalancetesITR[19]=new String("31122003");
	}

	public void Initialize() throws AgentException{
		Class[] ClassesToRegister;
		
		ClassesToRegister=new Class[2];		
		try {		
			ClassesToRegister[0]=Class.forName("org.abia.Agents.CVMAgent.Balancete");
			ClassesToRegister[1]=Class.forName("org.abia.Agents.CVMAgent.ContaContabil");		
			Blackboard.getBlackboard().RegisterAgent(this);
			Blackboard.getBlackboard().RegisterAgentData(ClassesToRegister);
		}catch(Exception e){
			throw (new AgentException());
		}		
	}
	
	public String getAgentNameInBlackboard(){
		return "CVMAgent";
	}
	
	protected String Melhorar(String inStr){
		String ret="";
		int f;
		
		inStr=inStr.toUpperCase();
		for(f=0;f<inStr.length();f++){
		if(inStr.charAt(f)=='\''){
				ret+=" ";
				continue;
		}
		if(inStr.charAt(f)=='\t'){
				ret+=" ";
				continue;
		}
		if(inStr.charAt(f)=='\n'){
				ret+=" ";
				continue;
		}
		if(inStr.charAt(f)=='\"'){
				ret+=" ";
				continue;
		}
		if(inStr.charAt(f)=='Ç'){
			ret+="C";
			continue;
		}
		if(inStr.charAt(f)=='Ã'){
			ret+="A";
			continue;
		}
		if(inStr.charAt(f)=='Â'){
			ret+="A";
			continue;
		}
		if(inStr.charAt(f)=='Á'){
			ret+="A";
			continue;
		}
		if(inStr.charAt(f)=='À'){
			ret+="A";
			continue;
		}
		if(inStr.charAt(f)=='É'){
			ret+="E";
			continue;
		}
		if(inStr.charAt(f)=='È'){
			ret+="E";
			continue;
		}
		if(inStr.charAt(f)=='Ê'){
			ret+="E";
			continue;
		}
		if(inStr.charAt(f)=='Ô'){
			ret+="O";
			continue;
		}
		if(inStr.charAt(f)=='Õ'){
			ret+="O";
			continue;
		}
		if(inStr.charAt(f)=='Ó'){
			ret+="O";
			continue;
		}
		if(inStr.charAt(f)=='Ò'){
			ret+="O";
			continue;
		}
		if(inStr.charAt(f)=='Ô'){
			ret+="O";
			continue;
		}
		if(inStr.charAt(f)=='Í'){
			ret+="I";
			continue;
		}
		if(inStr.charAt(f)=='Ì'){
			ret+="I";
			continue;
		}
		if(inStr.charAt(f)=='Ú'){
			ret+="U";
			continue;
		}
		if(inStr.charAt(f)=='Ù'){
			ret+="U";
			continue;
		}
		if(inStr.charAt(f)=='Û'){
			ret+="U";
			continue;
		}
		ret+=inStr.charAt(f);
		}
		return ret;
	}
	
	protected String Limpar(String Tempstr){
		String  Temp;
		int     f;
		boolean Negativo=false;
		
		Temp=new String("");
		for(f=0;f<Tempstr.length();f++){
			if(Tempstr.charAt(f)=='('){
			   Negativo=true;
			   continue;
			}
			if(Tempstr.charAt(f)==')'){
			   Negativo=true;
			   continue;
			}
		    if(Tempstr.charAt(f)=='.')
		        continue;
			if(Tempstr.charAt(f)=='\n')
				continue;
			if(Tempstr.charAt(f)=='\t')
				continue;
			if(Tempstr.charAt(f)=='\r')
				continue;
		    if(Tempstr.charAt(f)==','){
		    	Temp+=".";
		    	continue;		     
		    }
	        Temp+=Tempstr.charAt(f);
	    }  
	    if(Temp.length()==0) return "0";
	    if(Negativo) return ("-"+Temp);
		return Temp;
	}
	
	protected boolean ProcessStatement(Empresa toProcess,String Date){
		Object            obj;
		String            TotalURL,Retorno,TableOk,NovoRetorno,CurrentURL;
		String            TempNIPE,TempCNPJ,TempDenominacaoSocial,TempCodigoCVM;
		int               NumberTRs,f;
		DataOutputStream  out2;
		Balancete         currentBalancete;
		ContaContabil     currentContaContabil;
		Vector            DataToStore;
		String            TempEnderecoCompleto="",TempBairro="",TempCEP="",TempMunicipio="",TempUF="",TempDDD="",TempTelefone1="",TempTelex="",TempFax="";
		String            TempCodigoAtividade="",TempAtividadePrincipal="";
		String            TempCapitalIntegralizadoOrdinarias,TempCapitalIntegralizadoPreferenciais,TempCapitalIntegralizadoTotal;
		String            TempEmTesourariaOrdinarias,TempEmTesourariaPreferenciais,TempEmTesourariaTotal;
		String            TempNumeroAcoes,TempLucroPorAcao,TempPrejuizoPorAcao;
		String            TempCodigoConta,TempDescricaoConta,TempValor;
		String            TempLast="";
		AgentData                 DataToSave[],CheckIfExists[];
		PostgreSQLFilterCondition Filters[];    		
		Integer                   PosToRetrieve[]=null,Inicial=null;
				
		DataToStore=new Vector();
		currentBalancete=new Balancete();
		try{
			
			Filters=new PostgreSQLFilterCondition[2];
			Filters[0]=new PostgreSQLFilterCondition();
			Filters[0].FieldName="EmpresaEmitente";
			Filters[0].Operator =PostgreSQLQueryCondition.EQUALS ;
			Filters[0].ValueName=new Integer(toProcess.getTimestamp().intValue());
			Filters[1]=new PostgreSQLFilterCondition();
			Filters[1].FieldName="StrDataBalancete";
			Filters[1].Operator =PostgreSQLQueryCondition.EQUALS ;
			Filters[1].ValueName=new String(Date);
			CheckIfExists=Blackboard.getBlackboard().Query(Balancete.class,Filters);
			if(CheckIfExists.length>0) return true;

			DataToStore.add(currentBalancete);
			
			currentBalancete.setStrDataBalancete(Date);
			currentBalancete.setEmpresaEmitente(toProcess);
			
			System.out.println("Processando Balancete para "+toProcess.getNomeEmpresa());
			TotalURL="http://127.0.0.1:8080/abia/CVM?";
			if(Date.startsWith("3112"))
			   TotalURL+="Tipo=DPR&";
			else
			   TotalURL+="Tipo=ITR&";		

			TotalURL+="CVM=" + toProcess.getCodigoCVM() +"&";
			TotalURL+="Dia=" + Date.substring(0,2) +"&";
			TotalURL+="Mes=" + Date.substring(2,4) +"&";
			TotalURL+="Ano=" + Date.substring(4,8) +"&";
						
			CurrentURL=TotalURL+"Assunto=Identificao";						   			
			obj=theClient.getURL(CurrentURL,null);		    	
			if(obj instanceof HTMLContentHandler){
		   	   HTMLContentHandler HTMLHandler;
					  
		   	   HTMLHandler=(HTMLContentHandler)obj;
		   	   if(HTMLHandler.getResponseStatus()!=
			      HttpURLConnection.HTTP_OK){
			      return false;
		       }
			   			   
		       Retorno=HTMLHandler.getContentAsString();
			   if(Retorno.indexOf("Possui Dados para Carregar")==-1){ 
		          NovoRetorno=HTMLContentHandler.ApplyFilter(Retorno,FiltersToApply,HTMLContentHandler.DEFAULT_DELETE); 
		          TableOk=HTMLContentHandler.RetrieveTable(NovoRetorno,"1.2");		       
				  TempCodigoCVM        =HTMLContentHandler.RetrieveCell(TableOk,2,1);
				  currentBalancete.setCodigoCVM(TempCodigoCVM);
				  TempDenominacaoSocial=HTMLContentHandler.RetrieveCell(TableOk,2,2);
				  currentBalancete.setDenominacaoSocial(Melhorar(TempDenominacaoSocial));
				  TempCNPJ             =HTMLContentHandler.RetrieveCell(TableOk,4,1);
				  currentBalancete.setCNPJ(TempCNPJ);
				  TempNIPE             =HTMLContentHandler.RetrieveCell(TableOk,4,2);
				  currentBalancete.setNIRE(TempNIPE);				
		          NumberTRs=HTMLContentHandler.GetNumberOfRows(TableOk);
			   }			   
		    }
/*			CurrentURL=TotalURL+"Assunto=Sede";
			obj=theClient.getURL(CurrentURL,null);		    	
			if(obj instanceof HTMLContentHandler){
			   HTMLContentHandler HTMLHandler;
					  
			   HTMLHandler=(HTMLContentHandler)obj;
			   if(HTMLHandler.getResponseStatus()!=
				  HttpURLConnection.HTTP_OK){
				  return false;
			   }
			   Retorno=HTMLHandler.getContentAsString();
			   if(Retorno.indexOf("Possui Dados para Carregar")==-1){ 
				NovoRetorno=HTMLContentHandler.ApplyFilter(Retorno,FiltersToApply,HTMLContentHandler.DEFAULT_DELETE); 
				TableOk=HTMLContentHandler.RetrieveTable(NovoRetorno,"1.2");				  
				
				TempEnderecoCompleto=HTMLContentHandler.RetrieveCell(TableOk,2,1);
				currentBalancete.setEnderecoCompleto(TempEnderecoCompleto);
				TempBairro=HTMLContentHandler.RetrieveCell(TableOk,2,2);
				currentBalancete.setBairro(TempBairro);				
				TempCEP=HTMLContentHandler.RetrieveCell(TableOk,4,1);
				currentBalancete.setCEP(TempCEP);
				TempMunicipio=HTMLContentHandler.RetrieveCell(TableOk,4,2);
				currentBalancete.setMunicipio(TempMunicipio);
				TempUF=HTMLContentHandler.RetrieveCell(TableOk,4,3);
				currentBalancete.setUF(TempUF);
				TempDDD=HTMLContentHandler.RetrieveCell(TableOk,6,1);
				currentBalancete.setDDD(TempDDD);
				TempTelefone1=HTMLContentHandler.RetrieveCell(TableOk,6,2);
				currentBalancete.setTelefone1(TempTelefone1);
				TempTelex=HTMLContentHandler.RetrieveCell(TableOk,6,5);
				currentBalancete.setTelex(TempTelex);
				TempFax=HTMLContentHandler.RetrieveCell(TableOk,8,2);
				currentBalancete.setFax(TempFax);
			   }			   
			}
*/

			CurrentURL=TotalURL+"Assunto=CaracteristicasEmpresa";
			obj=theClient.getURL(CurrentURL,null);		    	
			if(obj instanceof HTMLContentHandler){
			   HTMLContentHandler HTMLHandler;
					  
			   HTMLHandler=(HTMLContentHandler)obj;
			   if(HTMLHandler.getResponseStatus()!=
				  HttpURLConnection.HTTP_OK){
				  return false;
			   }
			   Retorno=HTMLHandler.getContentAsString();
			   if(Retorno.indexOf("Possui Dados para Carregar")==-1){ 
				  NovoRetorno=HTMLContentHandler.ApplyFilter(Retorno,FiltersToApply,HTMLContentHandler.DEFAULT_DELETE); 
				  TableOk=HTMLContentHandler.RetrieveTable(NovoRetorno,"1.2");				  
				  TempCodigoAtividade=HTMLContentHandler.RetrieveCell(TableOk,15,1);
				  currentBalancete.setCodigoAtividade(Melhorar(TempCodigoAtividade));  
				  TempAtividadePrincipal=HTMLContentHandler.RetrieveCell(TableOk,10,1);
				  currentBalancete.setAtividadePrincipal(Melhorar(TempAtividadePrincipal));
			   }			   
			}
/*
			CurrentURL=TotalURL+"Assunto=ComposicaoCapital";
			obj=theClient.getURL(CurrentURL,null);		    	
			if(obj instanceof HTMLContentHandler){
			   HTMLContentHandler HTMLHandler;
					  
			   HTMLHandler=(HTMLContentHandler)obj;
			   if(HTMLHandler.getResponseStatus()!=
				  HttpURLConnection.HTTP_OK){
				  return false;
			   }
			   Retorno=HTMLHandler.getContentAsString();
			   if(Retorno.indexOf("Possui Dados para Carregar")==-1){ 
				  NovoRetorno=HTMLContentHandler.ApplyFilter(Retorno,FiltersToApply,HTMLContentHandler.DEFAULT_DELETE); 
				  TableOk=HTMLContentHandler.RetrieveTable(NovoRetorno,"1.2");
				  TempCapitalIntegralizadoOrdinarias=HTMLContentHandler.RetrieveCell(TableOk,3,2);
				  currentBalancete.setCapitalIntegralizadoOrdinarias(new Double(Limpar(TempCapitalIntegralizadoOrdinarias)));
				  TempCapitalIntegralizadoPreferenciais=HTMLContentHandler.RetrieveCell(TableOk,4,2);
				  currentBalancete.setCapitalIntegralizadoPreferenciais(new Double(Limpar(TempCapitalIntegralizadoPreferenciais)));
				  TempCapitalIntegralizadoTotal=HTMLContentHandler.RetrieveCell(TableOk,5,2);
				  currentBalancete.setCapitalIntegralizadoTotal(new Double(Limpar(TempCapitalIntegralizadoTotal)));
				  TempEmTesourariaOrdinarias=HTMLContentHandler.RetrieveCell(TableOk,7,2);
				  currentBalancete.setEmTesourariaOrdinarias(new Double(Limpar(TempEmTesourariaOrdinarias)));
				  TempEmTesourariaPreferenciais=HTMLContentHandler.RetrieveCell(TableOk,8,2);
				  currentBalancete.setEmTesourariaPreferenciais(new Double(Limpar(TempEmTesourariaPreferenciais)));
				  TempEmTesourariaTotal=HTMLContentHandler.RetrieveCell(TableOk,9,2);
				  currentBalancete.setEmTesourariaTotal(new Double(Limpar(TempEmTesourariaTotal)));
			   }			   
			}
*/
			CurrentURL=TotalURL+"Assunto=LucroPrejuizo";
			obj=theClient.getURL(CurrentURL,null);		    	
			if(obj instanceof HTMLContentHandler){
			   HTMLContentHandler HTMLHandler;
					  
			   HTMLHandler=(HTMLContentHandler)obj;
			   if(HTMLHandler.getResponseStatus()!=
				  HttpURLConnection.HTTP_OK){
				  return false;
			   }
			   Retorno=HTMLHandler.getContentAsString();
			   if(Retorno.indexOf("Possui Dados para Carregar")==-1){ 
				  NovoRetorno=HTMLContentHandler.ApplyFilter(Retorno,FiltersToApply,HTMLContentHandler.DEFAULT_DELETE); 
				  TableOk=HTMLContentHandler.RetrieveTable(NovoRetorno,"1.2");
				  TempNumeroAcoes=HTMLContentHandler.RetrieveCell(TableOk,2,2);
				  currentBalancete.setNumeroAcoes(new Double(Limpar(TempNumeroAcoes)));
				  TempLucroPorAcao=HTMLContentHandler.RetrieveCell(TableOk,3,2);
				  currentBalancete.setLucroPorAcao(new Double(Limpar(TempLucroPorAcao)));
				  TempPrejuizoPorAcao=HTMLContentHandler.RetrieveCell(TableOk,4,2);
				  currentBalancete.setPrejuizoPorAcao(new Double(Limpar(TempPrejuizoPorAcao)));
			   }			   
			}

			CurrentURL=TotalURL+"Assunto=Ativo";
			obj=theClient.getURL(CurrentURL,null);		    	
			if(obj instanceof HTMLContentHandler){
			   HTMLContentHandler HTMLHandler;
					  
			   HTMLHandler=(HTMLContentHandler)obj;
			   if(HTMLHandler.getResponseStatus()!=
				  HttpURLConnection.HTTP_OK){
				  return false;
			   }
			   Retorno=HTMLHandler.getContentAsString();
			   if(Retorno.indexOf("Possui Dados para Carregar")==-1){ 
				  PosToRetrieve=new Integer[1];
				  PosToRetrieve[0]=new Integer(0);
				  
				  NovoRetorno=HTMLContentHandler.ApplyFilter(Retorno,FiltersToApply,HTMLContentHandler.DEFAULT_DELETE); 
				  TableOk=HTMLContentHandler.RetrieveTable(NovoRetorno,"1.3");		       
				  NumberTRs=HTMLContentHandler.GetNumberOfRows(TableOk);
				  
				  PosToRetrieve=new Integer[1];
				  PosToRetrieve[0]=new Integer(0);
				  for(f=0;f<1;f++)
				      TempCodigoConta=HTMLContentHandler.RetrieveNextCell(TableOk,f,1,PosToRetrieve);
				      
				  Inicial=PosToRetrieve[0];
				  TempLast="";
				  for(f=3;f<NumberTRs;f++){					  
					  currentContaContabil=new ContaContabil();
					  currentContaContabil.setBalancete(currentBalancete);
					  TempCodigoConta=HTMLContentHandler.RetrieveNextCell(TableOk,f,1,PosToRetrieve);
					  currentContaContabil.setCodigoConta(TempCodigoConta);
					  PosToRetrieve[0]=Inicial;					  
					  TempDescricaoConta=HTMLContentHandler.RetrieveNextCell(TableOk,f,2,PosToRetrieve);
					  currentContaContabil.setDescricaoConta(Melhorar(TempDescricaoConta));
					  PosToRetrieve[0]=Inicial;
					  TempValor=HTMLContentHandler.RetrieveNextCell(TableOk,f,3,PosToRetrieve);
					  currentContaContabil.setValor(new Double(Limpar(TempValor)));
					  if(!TempCodigoConta.equals(TempLast)){
					     DataToStore.add(currentContaContabil);
						 TempLast=TempCodigoConta;
					  }
					  Inicial=PosToRetrieve[0];
				  }				  
			   }			   
			}

			CurrentURL=TotalURL+"Assunto=Passivo";
			obj=theClient.getURL(CurrentURL,null);		    	
			if(obj instanceof HTMLContentHandler){
			   HTMLContentHandler HTMLHandler;
					  
			   HTMLHandler=(HTMLContentHandler)obj;
			   if(HTMLHandler.getResponseStatus()!=
				  HttpURLConnection.HTTP_OK){
				  return false;
			   }
			   Retorno=HTMLHandler.getContentAsString();
			   if(Retorno.indexOf("Possui Dados para Carregar")==-1){ 
				NovoRetorno=HTMLContentHandler.ApplyFilter(Retorno,FiltersToApply,HTMLContentHandler.DEFAULT_DELETE); 
				TableOk=HTMLContentHandler.RetrieveTable(NovoRetorno,"1.3");		       
				NumberTRs=HTMLContentHandler.GetNumberOfRows(TableOk);

				PosToRetrieve=new Integer[1];
				PosToRetrieve[0]=new Integer(0);				
				for(f=0;f<1;f++)
					TempCodigoConta=HTMLContentHandler.RetrieveNextCell(TableOk,f,1,PosToRetrieve);				
				
				Inicial=PosToRetrieve[0];
				TempLast="";
				for(f=3;f<NumberTRs;f++){
				    currentContaContabil=new ContaContabil(); 
					currentContaContabil.setBalancete(currentBalancete);				    
				    TempCodigoConta=HTMLContentHandler.RetrieveNextCell(TableOk,f,1,PosToRetrieve);
					currentContaContabil.setCodigoConta(TempCodigoConta);
					PosToRetrieve[0]=Inicial;
				    TempDescricaoConta=HTMLContentHandler.RetrieveNextCell(TableOk,f,2,PosToRetrieve);
					currentContaContabil.setDescricaoConta(Melhorar(TempDescricaoConta));
					PosToRetrieve[0]=Inicial;
				    TempValor=HTMLContentHandler.RetrieveNextCell(TableOk,f,3,PosToRetrieve);
					currentContaContabil.setValor(new Double(Limpar(TempValor)));
					if(!TempCodigoConta.equals(TempLast)){
					   DataToStore.add(currentContaContabil);
					   TempLast=TempCodigoConta;
					}
					Inicial=PosToRetrieve[0];
				}				  
			   }			   
			}

			CurrentURL=TotalURL+"Assunto=DemonstracaoResultado";
			obj=theClient.getURL(CurrentURL,null);		    	
			if(obj instanceof HTMLContentHandler){
			   HTMLContentHandler HTMLHandler;
					  
			   HTMLHandler=(HTMLContentHandler)obj;
			   if(HTMLHandler.getResponseStatus()!=
				  HttpURLConnection.HTTP_OK){
				  return false;
			   }
			   Retorno=HTMLHandler.getContentAsString();
			   if(Retorno.indexOf("Possui Dados para Carregar")==-1){ 
				NovoRetorno=HTMLContentHandler.ApplyFilter(Retorno,FiltersToApply,HTMLContentHandler.DEFAULT_DELETE); 
				TableOk=HTMLContentHandler.RetrieveTable(NovoRetorno,"1.3");		       
				NumberTRs=HTMLContentHandler.GetNumberOfRows(TableOk);

				PosToRetrieve=new Integer[1];
				PosToRetrieve[0]=new Integer(0);		
				for(f=0;f<1;f++)
					TempCodigoConta=HTMLContentHandler.RetrieveNextCell(TableOk,f,1,PosToRetrieve);				
				
				Inicial=PosToRetrieve[0];
				TempLast="";				
				for(f=3;f<NumberTRs;f++){
				    currentContaContabil=new ContaContabil();
					currentContaContabil.setBalancete(currentBalancete);
				    TempCodigoConta=HTMLContentHandler.RetrieveNextCell(TableOk,f,1,PosToRetrieve);
					currentContaContabil.setCodigoConta(TempCodigoConta);
					PosToRetrieve[0]=Inicial;
				    TempDescricaoConta=HTMLContentHandler.RetrieveNextCell(TableOk,f,2,PosToRetrieve);
					currentContaContabil.setDescricaoConta(Melhorar(TempDescricaoConta));
					PosToRetrieve[0]=Inicial;
				    TempValor=HTMLContentHandler.RetrieveNextCell(TableOk,f,3,PosToRetrieve);
					currentContaContabil.setValor(new Double(Limpar(TempValor)));
					if(!TempCodigoConta.equals(TempLast)){
					   DataToStore.add(currentContaContabil);
					   TempLast=TempCodigoConta;
					}
					Inicial=PosToRetrieve[0];
				}				  
			   }			   
			}
		}catch(Exception e){
			System.out.println(e);					       
		}
		
		DataToSave=new AgentData[DataToStore.size()];
		for(f=0;f<DataToSave.length;f++){
			DataToSave[f]=(AgentData)DataToStore.get(f);	
		}
		System.out.println("Tamanho:"+DataToSave.length);
		try{
			Blackboard.getBlackboard().Store(DataToSave);			
		}catch(Exception e){
			System.out.println(e);			
		}
		
/*
        try{		
			DataToSave=null;
			for(f=0;f<DataToStore.size();f++){
				DataToSave=new AgentData[1];
				DataToSave[0]=(AgentData)DataToStore.get(f);
				Blackboard.getBlackboard().Store(DataToSave);	
			}
		}catch(Exception e){
			System.out.println(e);			
		}
*/		


		return true;
		
	}

	public void run(){
		AgentData        Empresas[]=null,Papeis[]=null,Pregoes[]=null;
		Papel            currentPapel;
		int              f,lastSize=-1,g;
		int              CurrentITR,CurrentDTR;
		
		CurrentITR=0;
		CurrentDTR=0;		

		FiltersToApply=new HashMap();		
		CurrentFilter=new HTMLFilter("<TABLE>",HTMLFilter.ACCEPT,"\r\n");
		FiltersToApply.put("<TABLE>",CurrentFilter);        
		CurrentFilter=new HTMLFilter("<TR>",HTMLFilter.ACCEPT,"\r\n");
		FiltersToApply.put("<TR>",CurrentFilter);
		CurrentFilter=new HTMLFilter("<TD>",HTMLFilter.ACCEPT,null);
		FiltersToApply.put("<TD>",CurrentFilter);

		CurrentFilter=new HTMLFilter("</TABLE>",HTMLFilter.ACCEPT,"\r\n");
		FiltersToApply.put("</TABLE>",CurrentFilter);        
		CurrentFilter=new HTMLFilter("</TR>",HTMLFilter.ACCEPT,"\r\n");
		FiltersToApply.put("</TR>",CurrentFilter);
		CurrentFilter=new HTMLFilter("</TD>",HTMLFilter.ACCEPT,null);
		FiltersToApply.put("</TD>",CurrentFilter);
	       
		try{
		   //Thread.sleep(10000000);
		   theClient=AgentContainer.getWebRetriever().createClient();
		   theClient.RegisterContentHandler("text/html",Class.forName("org.abia.AgentContainer.WebRetriever.HTMLContentHandler"));
		   theClient.RegisterContentHandler("text/plain",Class.forName("org.abia.AgentContainer.WebRetriever.HTMLContentHandler"));    	
		}catch(Exception e){
		   System.out.println(e);
		   e.printStackTrace();			
		}

		
		while(true){
			try{
			    Pregoes=Blackboard.getBlackboard().Query(Pregao.class,null);
			    if(Pregoes.length>0) break;
			    Thread.sleep(1000);
			}catch(Exception e){
				System.out.println(e);
			}
		}
		
		try{		
		    Papeis=Blackboard.getBlackboard().Query(Papel.class,null);
		}catch(Exception e){
			System.out.println(e);			
		}
		
		for(f=0;f<Papeis.length;f++){
			Papeis[f].ResolveReferences();			
		}
				
		while(true){
			try{
				for(g=12;g<BalancetesITR.length;g++){
				    for(f=0;f<Papeis.length;f++){				    	
				    	currentPapel=(Papel)Papeis[f];
						System.out.println("Verificando Papel:"+currentPapel.getCodigoPregao());
					    if(!ProcessStatement((Empresa)currentPapel.getEmpresaEmitente(),BalancetesITR[g])){
						   System.out.println("Nao encontrado:"+currentPapel.getCodigoPregao());
					       Thread.sleep(5000);
					       if(f==0) f--;
					       continue;
					    }				
				    }
				}				
				System.out.println("Verificados...");
				Thread.sleep(5000);				
			}catch(Exception e){
				System.out.println(e);
				e.printStackTrace();
			}
		}
	}



}