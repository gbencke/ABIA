package org.abia.Agents.BovespaAgent;

import org.abia.AgentContainer.*;
import org.abia.Blackboard.*;
import org.abia.Blackboard.PostgreSQL.*;
import org.abia.AgentContainer.WebRetriever.*;
import org.abia.utils.*;

import java.util.*;
import java.net.*;
import java.util.zip.CRC32;

import org.w3c.dom.*;


class BovespaUtils {
	
	protected static int getLastDay(int Month){
		if(Month==1) return 31;
		if(Month==2) return 28;
		if(Month==3) return 31;
		if(Month==4) return 30;
		if(Month==5) return 31;
		if(Month==6) return 30;
		if(Month==7) return 31;
		if(Month==8) return 31;
		if(Month==9) return 30;
		if(Month==10) return 31;
		if(Month==11) return 30;
		if(Month==12) return 31;
		return 0;
	}
	public static String getNextDate(String startDate,int DaysToAdvance){
		String  Retorno="",AnoReturned,MesReturned,DayReturned,TempStr;
		int     Ano,Mes,Dia,f;
		int     currentAno,currentMes,currentDia;
     
		
		if(startDate.length()!=8) return startDate;
		
		Ano=(new Integer(startDate.substring(0,4))).intValue();
		TempStr=startDate.substring(0,4);
		Mes=(new Integer(startDate.substring(4,6))).intValue();
		TempStr=startDate.substring(4,6);
		Dia=(new Integer(startDate.substring(6,8))).intValue();
		TempStr=startDate.substring(6,8);
		    
		currentAno=Ano;
		currentMes=Mes;
		currentDia=Dia;		
		    
		for(f=0;f<DaysToAdvance;f++){
			currentDia++;
			if(currentDia>getLastDay(currentMes)){
			   currentDia=1;
			   currentMes++;
			   if(currentMes>12){
			      currentMes=1;
			      currentAno++;
			   }
			}
		}
		AnoReturned=(new Integer(currentAno)).toString();
		MesReturned=(new Integer(currentMes)).toString();
		if(MesReturned.length()==1) MesReturned="0"+MesReturned;
		DayReturned=(new Integer(currentDia)).toString();
		if(DayReturned.length()==1) DayReturned="0"+DayReturned;		
		
		return AnoReturned+MesReturned+DayReturned;
	}
}

public class BovespaAgent extends WebMiningAgent {
	protected String             CurrentDate;		   	    
	protected WebRetrieverClient theClient=null;        
	protected HashMap            FiltersToApply;
	protected HTMLFilter         CurrentFilter;
	protected Document           Configuration;


	static {
		System.out.println("Carregado:BovespaAgent");		
	}
		
	public BovespaAgent(){
		   CurrentDate="20020701";
	       FiltersToApply=new HashMap();		
	       CurrentFilter=new HTMLFilter("<TABLE>",HTMLFilter.ACCEPT,"\r\n");
	       FiltersToApply.put("<TABLE>",CurrentFilter);        
	       CurrentFilter=new HTMLFilter("<TR>",HTMLFilter.ACCEPT,"\r\n");
	       FiltersToApply.put("<TR>",CurrentFilter);
	       CurrentFilter=new HTMLFilter("<TD>",HTMLFilter.ACCEPT,null);
	       FiltersToApply.put("<TD>",CurrentFilter);
		   CurrentFilter=new HTMLFilter("<A>",HTMLFilter.ACCEPT_KEEP_ATTRIBUTES,null);
		   FiltersToApply.put("<A>",CurrentFilter);

	       CurrentFilter=new HTMLFilter("</TABLE>",HTMLFilter.ACCEPT,"\r\n");
	       FiltersToApply.put("</TABLE>",CurrentFilter);        
	       CurrentFilter=new HTMLFilter("</TR>",HTMLFilter.ACCEPT,"\r\n");
	       FiltersToApply.put("</TR>",CurrentFilter);
	       CurrentFilter=new HTMLFilter("</TD>",HTMLFilter.ACCEPT,null);
	       FiltersToApply.put("</TD>",CurrentFilter);
	       
		   try{
		      theClient=AgentContainer.getWebRetriever().createClient();
		      theClient.RegisterContentHandler("text/html",Class.forName("org.abia.AgentContainer.WebRetriever.HTMLContentHandler"));
			  theClient.RegisterContentHandler("text/plain",Class.forName("org.abia.AgentContainer.WebRetriever.HTMLContentHandler"));    	
		   }catch(Exception e){
			  System.out.println(e);
			  e.printStackTrace();			
		   }
	}


	
	protected void VerificarEmpresas(){
		String             Base="",NomeURL="",Simbolo="",TotalURL="";
		String NovoRetorno,TableOk,RazaoSocial,NomePregao,TempCodigo;
		int g=0,i=0,f,h,NumberTRs,InicioCodigo,CodigoCVM;
		Empresa CurrentEmpresa=null;
		PostgreSQLFilterCondition Filters[];
		AgentData AgentDataRetorno[];
		Object    obj;	
		String    Retorno;	 
		
		for(f=0;f<26;f++){
		    Base="http://" + Utils.TargetServer +  "/abia/Bovespa?";
				
		    NomeURL="LetraInicial="+(new Character((char)(f+'A')));				
		    TotalURL=Base+NomeURL;
		    
		    try{		    	
			obj=theClient.getURL(TotalURL,null);		    	
		    if(obj instanceof HTMLContentHandler){
		       HTMLContentHandler HTMLHandler;
					  
		       HTMLHandler=(HTMLContentHandler)obj;
		       if(HTMLHandler.getResponseStatus()!=
			      HttpURLConnection.HTTP_OK){
				  continue;
		       }
		       Retorno=HTMLHandler.getContentAsString();
		       NovoRetorno=HTMLContentHandler.ApplyFilter(Retorno,FiltersToApply,HTMLContentHandler.DEFAULT_DELETE); 
		       TableOk=HTMLContentHandler.RetrieveTable(NovoRetorno,"1.1");
		       
			   NumberTRs=HTMLContentHandler.GetNumberOfRows(TableOk);		       
		       for(h=1;h<NumberTRs;h++){
		       	   RazaoSocial=HTMLContentHandler.RetrieveCell(TableOk,h+1,2);
				   NomePregao =HTMLContentHandler.RetrieveCell(TableOk,h+1,1);
				   
				   InicioCodigo=RazaoSocial.indexOf("&ccvm=")+6;
				   TempCodigo=RazaoSocial.substring(InicioCodigo);
				   
				   InicioCodigo=TempCodigo.indexOf("&");
				   TempCodigo=TempCodigo.substring(0,InicioCodigo);
				   
				   CodigoCVM=(new Integer(TempCodigo)).intValue();
				   
				   TempCodigo=RazaoSocial.substring(RazaoSocial.indexOf(">")+1);
				   RazaoSocial=TempCodigo;

				   TempCodigo=NomePregao.substring(NomePregao.indexOf(">")+1);
				   NomePregao=TempCodigo;
				   
				   CurrentEmpresa=new Empresa();
				   CurrentEmpresa.setCodigoCVM(new Integer(CodigoCVM));
				   CurrentEmpresa.setNomeEmpresa(RazaoSocial);
				   CurrentEmpresa.setNomePregao(NomePregao);
				   
				   try{
					  Filters=new PostgreSQLFilterCondition[1];
					  Filters[0]=new PostgreSQLFilterCondition();
					  Filters[0].FieldName="NomePregao";
					  Filters[0].Operator =PostgreSQLQueryCondition.EQUALS ;
					  Filters[0].ValueName=NomePregao;			
					  AgentDataRetorno=Blackboard.getBlackboard().Query(CurrentEmpresa.getClass(),Filters);				   	  
				   	  if(AgentDataRetorno.length==0)
				   	     Blackboard.getBlackboard().Store(CurrentEmpresa.getClass(),CurrentEmpresa);				   	
				   }catch(Exception e){
				   	
				   }
				   
				   System.out.println(CodigoCVM+" - "+ RazaoSocial +" - "+ NomePregao);		       	  		       	
		       }		       
		    }
		     
		    }catch(Exception e){
		    	
		    	
		    }
		  }
		}	
	
	
	protected void VerificarIndices(){
		String             Base="",NomeURL="",Simbolo="",TotalURL="";
		String                     NovoRetorno,TableOk,RazaoSocial,NomePregao,TempCodigo;
		int                        f,h,NumberTRs,InicioCodigo,CodigoCVM;
		PostgreSQLFilterCondition  Filters[];
		AgentData                  AgentDataRetorno[];
		CRC32     		           CurrentCRC;
		Object                     obj;
		String                     Retorno;
		
		
		TotalURL="http://" + Utils.TargetServer + "/abia/BMF";
		try{		    	
		   obj=theClient.getURL(TotalURL,null);		    	
		   if(obj instanceof HTMLContentHandler){
		      HTMLContentHandler HTMLHandler;
					  
		      HTMLHandler=(HTMLContentHandler)obj;
		      if(HTMLHandler.getResponseStatus()!=HttpURLConnection.HTTP_OK)
		         return;
		      Retorno=HTMLHandler.getContentAsString();
			  NovoRetorno=HTMLContentHandler.ApplyFilter(Retorno,FiltersToApply,HTMLContentHandler.DEFAULT_DELETE); 
		      TableOk=HTMLContentHandler.RetrieveTable(NovoRetorno,"2.1");
		      
		      CurrentCRC=new CRC32();
			  CurrentCRC.update(TableOk.getBytes());
			  			  
			  try{
			     Filters=new PostgreSQLFilterCondition[1];
			     Filters[0]=new PostgreSQLFilterCondition();
			     Filters[0].FieldName="CRCConteudo";
			     Filters[0].Operator =PostgreSQLQueryCondition.EQUALS ;
			     Filters[0].ValueName=new Integer((int)CurrentCRC.getValue());			
			     AgentDataRetorno=Blackboard.getBlackboard().Query(Indice.class,Filters);				   	  
			     if(AgentDataRetorno.length>0) return;			     				   	
			  }catch(Exception e){
			  	System.out.println(e);				   	
				e.printStackTrace();
			  }		      

			  VerificarParticipacaoEmIndices(NovoRetorno);		       
		   }		       
		     
		}catch(Exception e){
		    	
		}
	  }
		
		

	protected void VerificarParticipacaoEmIndices(String SourceToParse){
		int                        f;
		Indice                     currentIndice;
		String                     ConteudoAtual;
		String                     CodigoPapel;
		Integer                    NumberPapeis;		
		CRC32                      currentCRC;
		int                        NumberTRs;
		PostgreSQLFilterCondition  Filters[];
		AgentData                  AgentDataRetorno[];
		
		Filters=new PostgreSQLFilterCondition[1];
		ConteudoAtual=HTMLContentHandler.RetrieveTable(SourceToParse,"2.1");				
		NumberTRs=HTMLContentHandler.GetNumberOfRows(ConteudoAtual);
		
		NumberPapeis=new Integer(NumberTRs-5);		
		currentIndice=new Indice();
		currentCRC=new CRC32();
		currentCRC.update(ConteudoAtual.getBytes());
		currentIndice.setCRCConteudo(new Integer((int)currentCRC.getValue()));
		currentIndice.setDataAtualizacao(new Date());
		currentIndice.setNumberPapeis(NumberPapeis);
		currentIndice.setNomeIndice(new String("IBOVESPA"));
		currentIndice.setCodigoPregao(new String("^BVSP"));		
		try{
		   Blackboard.getBlackboard().Store(Indice.class,currentIndice);

		   Filters[0]=new PostgreSQLFilterCondition();
		   Filters[0].FieldName="CRCConteudo";
		   Filters[0].Operator =PostgreSQLQueryCondition.EQUALS ;
		   Filters[0].ValueName=new Integer((int)currentCRC.getValue());			
		   AgentDataRetorno=Blackboard.getBlackboard().Query(Indice.class,Filters);				   	  
		   if(AgentDataRetorno.length==0) 
		      return;
		   else
		      currentIndice=(Indice)AgentDataRetorno[0];		      				   			   
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();			
		}
		for(f=0;f<NumberPapeis.intValue();f++){
			CodigoPapel=HTMLContentHandler.RetrieveCell(ConteudoAtual,f+4,1);
			try{			
				Filters[0].FieldName="CodigoPregao";
				Filters[0].Operator =PostgreSQLQueryCondition.EQUALS ;
				Filters[0].ValueName=CodigoPapel;
				AgentDataRetorno=Blackboard.getBlackboard().Query(Papel.class,Filters);				   	  
				if(AgentDataRetorno.length==0){
					Papel         currentPapel;
					Empresa       EmpEmitente;
					String        EmpresaNaTabela;
					String        TempPart;
					String        TempQuant;
					PapelEmIndice currentPapelEmIndice;
					
					EmpresaNaTabela=HTMLContentHandler.RetrieveCell(ConteudoAtual,f+4,2);
					Filters[0].FieldName="nomepregao";
					Filters[0].Operator =PostgreSQLQueryCondition.EQUALS ;
					Filters[0].ValueName=EmpresaNaTabela;
					AgentDataRetorno=Blackboard.getBlackboard().Query(Empresa.class,Filters);				   	  
					if(AgentDataRetorno.length==1){
						currentPapel=new Papel();
						currentPapel.setCodigoPregao(CodigoPapel);
						currentPapel.setEmpresaEmitente(AgentDataRetorno[0]);
						currentPapel.setTipo(HTMLContentHandler.RetrieveCell(ConteudoAtual,f+4,3));
						Blackboard.getBlackboard().Store(this.getClass(),currentPapel);
					}else{
						System.out.println("Warning:Company "+EmpresaNaTabela+" for "+CodigoPapel+" is not in Blackboard!!!");						
					}
					Filters[0].FieldName="codigopregao";
					Filters[0].Operator =PostgreSQLQueryCondition.EQUALS ;
					Filters[0].ValueName=CodigoPapel;
					AgentDataRetorno=Blackboard.getBlackboard().Query(Papel.class,Filters);
					TempPart=HTMLContentHandler.RetrieveCell(ConteudoAtual,f+4,5);
					TempQuant=HTMLContentHandler.RetrieveCell(ConteudoAtual,f+4,4);
					TempPart=TempPart.replace(',','.');
					TempQuant=TempQuant.replace(',','.');					
					currentPapel=(Papel)AgentDataRetorno[0];																	
					currentPapelEmIndice=new PapelEmIndice();
					currentPapelEmIndice.setIndice(currentIndice);
					currentPapelEmIndice.setPapelParticipante(currentPapel);
					currentPapelEmIndice.setParticipacao(new Double(TempPart));
					currentPapelEmIndice.setQuantidade(new Double(TempQuant));
					Blackboard.getBlackboard().Store(this.getClass(),currentPapelEmIndice);
					System.out.println("Papel:"+currentPapel.getCodigoPregao()+" inserido em "+currentIndice.getNomeIndice());							   			   
				}				   			   				
			}catch(Exception e){
				System.out.println(e);
				e.printStackTrace();				
			}						
		}
	System.out.println("Papeis Entrados...");
	}

	protected void VerificarBDIs(){
		int                        f;
		String                     TotalURL,Token,currentString,TempData,DateSucceeded=null;
		Object                     obj;
		String                     Retorno,TempString;
		Vector                     DataToStore;
		AgentData                  ArrayFromVector[];  
		PostgreSQLFilterCondition  Filters[];
		Pregao                     currentPregao=null;		
				
		DataToStore=new Vector();
	    for(f=1;f<8;f++){		
			TempData=BovespaUtils.getNextDate(CurrentDate,f);
		    TotalURL="http://" + Utils.TargetServer + "/abia/"+TempData+".txt";

		    try{		    	
		        obj=theClient.getURL(TotalURL,null);		    	
		        if(obj instanceof HTMLContentHandler){
			       HTMLContentHandler HTMLHandler;
			  
			       HTMLHandler=(HTMLContentHandler)obj;
				   if(HTMLHandler.getResponseStatus()!=200) continue;
				   System.out.println(TotalURL);
			       Retorno=HTMLHandler.getContentAsString();
			       currentString=Retorno;
				   DateSucceeded=TempData;
			       while(true){
				         if(currentString.indexOf("\r\n")==-1) break;
			  	         Token=currentString.substring(0,currentString.indexOf("\r\n"));
			  	         if(Token.startsWith("00")){
			  	            int Ano,Mes,Dia;
			  	            String A,M,D;
			  	            Date  TempDate;
			  	         	
			  	         	currentPregao=new Pregao();
							currentPregao.setBolsa("BOVESPA");
							A=TempData.substring(0,4);
							M=TempData.substring(4,6);
							D=TempData.substring(6,8);
							
							Ano=(new Integer(TempData.substring(0,4))).intValue()-1900;
							Mes=(new Integer(TempData.substring(4,6))).intValue();
							Dia=(new Integer(TempData.substring(6,8))).intValue();
							TempDate=new Date();
							TempDate.setDate(Dia);
							TempDate.setMonth(Mes-1);
							TempDate.setYear(Ano);
							currentPregao.setData(TempDate);
							System.out.println(currentPregao.getData());
							DataToStore.add(currentPregao);																					
			  	         }
					     if(Token.startsWith("01")){
					     	String        NomeIndice;
					     	AgentData     Retrieved[];
							Indice        IndiceRetrieved;
							CotacaoIndice currentCotacaoIndice;
							Double        TempDouble;														
					     	
					     	if(currentString.length()<99)
					     	   continue;
					     	NomeIndice=currentString.substring(4,34);
							NomeIndice=NomeIndice.trim();
							Filters=new PostgreSQLFilterCondition[1];
							Filters[0]=new  PostgreSQLFilterCondition();
							Filters[0].FieldName="nomeindice";
							Filters[0].Operator =PostgreSQLQueryCondition.EQUALS ;
							Filters[0].ValueName=NomeIndice;
							Retrieved=Blackboard.getBlackboard().Query(Indice.class,Filters);
							IndiceRetrieved=(Indice)Retrieved[Retrieved.length-1];
							
							currentCotacaoIndice=new CotacaoIndice();
							currentCotacaoIndice.setIndice(IndiceRetrieved);
							currentCotacaoIndice.setPregao(currentPregao);
							currentCotacaoIndice.setResponsavel(this.getClass().getName());
							
							TempString=currentString.substring(34,40);
							TempDouble=new Double(TempString);
							currentCotacaoIndice.setAbertura(TempDouble);

							TempString=currentString.substring(40,46);
							TempDouble=new Double(TempString);
							currentCotacaoIndice.setMinima(TempDouble);

							
							TempString=currentString.substring(46,52);
							TempDouble=new Double(TempString);
							currentCotacaoIndice.setMaximo(TempDouble);
							
							
							TempString=currentString.substring(92,98);							
							TempDouble=new Double(TempString);
							currentCotacaoIndice.setFechamento(TempDouble);
							DataToStore.add(currentCotacaoIndice);							
					     }
					     if(Token.startsWith("02")){
					     	Cotacao       currentCotacao;
					     	String        NomePapel;
					     	Papel         currentPapel;
							AgentData     Retrieved[];
							Papel         PapelRetrieved;
							Double        TempDouble;														

							if(currentString.length()<99)
							   continue;
							NomePapel=currentString.substring(57,63);
							NomePapel=NomePapel.trim();
							Filters=new PostgreSQLFilterCondition[1];
							Filters[0]=new  PostgreSQLFilterCondition();
							Filters[0].FieldName="codigopregao";
							Filters[0].Operator =PostgreSQLQueryCondition.EQUALS ;
							Filters[0].ValueName=NomePapel;
							Retrieved=Blackboard.getBlackboard().Query(Papel.class,Filters);
							if(Retrieved.length>0){
								PapelRetrieved=(Papel)Retrieved[Retrieved.length-1];
							
								currentCotacao=new Cotacao();
								currentCotacao.setPapel(PapelRetrieved);
								currentCotacao.setPregao(currentPregao);
								currentCotacao.setResponsavel(this.getClass().getName());
							
								TempString=currentString.substring(90,101);
								TempDouble=new Double(TempString);
								TempDouble=new Double(TempDouble.doubleValue()/100);
								currentCotacao.setAbertura(TempDouble);

								TempString=currentString.substring(101,112);
								TempDouble=new Double(TempString);
								TempDouble=new Double(TempDouble.doubleValue()/100);							
								currentCotacao.setMaximo(TempDouble);

								TempString=currentString.substring(112,123);
								TempDouble=new Double(TempString);
								TempDouble=new Double(TempDouble.doubleValue()/100);							
								currentCotacao.setMinima(TempDouble);
	
								TempString=currentString.substring(134,145);
								TempDouble=new Double(TempString);
								TempDouble=new Double(TempDouble.doubleValue()/100);							
								currentCotacao.setFechamento(TempDouble);
							
								TempString=currentString.substring(145,156);
								TempDouble=new Double(TempString);
								TempDouble=new Double(TempDouble.doubleValue()/100);							
								currentCotacao.setVolume(TempDouble);
							
								DataToStore.add(currentCotacao);
							}
					     }
					     if(currentString.indexOf("\r\n")>-1)
				            currentString=currentString.substring(currentString.indexOf("\r\n")+2);
				            else
				            break;
			       }
			    break;
		        }
		    }catch(Exception e){
		    	System.out.println(e);
		    	e.printStackTrace();			
		    }
	    }
		
		if(DateSucceeded!=null) 
		   CurrentDate=DateSucceeded;
		   else
		   return;

		try{
		   ArrayFromVector=new AgentData[DataToStore.size()];
		   for(f=0;f<ArrayFromVector.length;f++){
		   	   ArrayFromVector[f]=(AgentData)DataToStore.get(f);		   
		   }
		   Blackboard.getBlackboard().Store(ArrayFromVector);
		}catch(Exception e){
		   System.out.println(e);
		   e.printStackTrace();
		}
	}

	
	public void run(){
		int                       f;
		NodeList                  ListOfNodes;
		Node                      BlackboardParentNode;
		Node					  AgentsParentNode;
		Node					  ListenerParentNode;
		String                    TempValue;
		
		int                       TimeToWaitBDI=-1;
		int                       TimeToWaitEmpresas=-1;
		int                       TimeToWaitIndices=-1;
		int                       Seconds=1;
		boolean                   CheckOnStartupBDI=false;
		boolean                   CheckOnStartupEmpresas=false;
		boolean                   CheckOnStartupIndices=false;    	               
		

		
		try{
		   Configuration=AgentContainer.getConfigurationDocument("BovespaAgent.xml");
		   ListOfNodes=Configuration.getElementsByTagName("DataToVerify");
		   f=ListOfNodes.getLength();
		   for(f=0;f<ListOfNodes.getLength();f++){
			   BlackboardParentNode=ListOfNodes.item(f);
			   TempValue=BlackboardParentNode.getAttributes().getNamedItem("Name").getNodeValue();
			   if(TempValue.equals("BDI")){
				  if(BlackboardParentNode.getAttributes().getNamedItem("CheckOnStartup").getNodeValue().equals("true"))
					 CheckOnStartupBDI=true;			   	  
				  else
				     CheckOnStartupBDI=false;				  
				  if(BlackboardParentNode.getAttributes().getNamedItem("TimeBetweenChecks").getNodeValue().length()>0)
					 TimeToWaitBDI=(new Integer(BlackboardParentNode.getAttributes().getNamedItem("TimeBetweenChecks").getNodeValue())).intValue();			   	  
				  else
				     TimeToWaitBDI=600;				  
			   }
			   if(TempValue.equals("Empresas")){
				if(BlackboardParentNode.getAttributes().getNamedItem("CheckOnStartup").getNodeValue().equals("true"))
				   CheckOnStartupEmpresas=true;			   	  
				else
				   CheckOnStartupEmpresas=false;				  
				if(BlackboardParentNode.getAttributes().getNamedItem("TimeBetweenChecks").getNodeValue().length()>0)
				   TimeToWaitEmpresas=(new Integer(BlackboardParentNode.getAttributes().getNamedItem("TimeBetweenChecks").getNodeValue())).intValue();			   	  
				else
				   TimeToWaitEmpresas=600;			   	
			   }		   				
			   if(TempValue.equals("Indices")){
				if(BlackboardParentNode.getAttributes().getNamedItem("CheckOnStartup").getNodeValue().equals("true"))
				   CheckOnStartupIndices=true;			   	  
				else
				   CheckOnStartupIndices=false;				  
				if(BlackboardParentNode.getAttributes().getNamedItem("TimeBetweenChecks").getNodeValue().length()>0)
				   TimeToWaitIndices=(new Integer(BlackboardParentNode.getAttributes().getNamedItem("TimeBetweenChecks").getNodeValue())).intValue();			   	  
				else
				   TimeToWaitIndices=600;			   	
			   }		   				
		   }		   
		}catch(Exception e){
			System.out.println(e);		
			e.printStackTrace();
		}
		
		if(CheckOnStartupEmpresas)  VerificarEmpresas();
		if(CheckOnStartupIndices)   VerificarIndices();
		if(CheckOnStartupBDI)       VerificarBDIs();		
				
		while(true){		      
		      if((Seconds % TimeToWaitEmpresas)==0) VerificarEmpresas();		      
			  if((Seconds % TimeToWaitIndices)==0)  VerificarIndices();
			  if((Seconds % TimeToWaitBDI)==0)      VerificarBDIs();
		      try{
		      	  Thread.sleep(1000);		      
		      }catch(Exception e){
		      	  e.printStackTrace();
		      	  System.out.println(e);		      	
		      }
			  Seconds++;		      
		}		
	}
	
	public void Initialize() throws AgentException {
		Class[] ClassesToRegister;
		
		ClassesToRegister=new Class[7];		
		try {		
		    ClassesToRegister[0]=Class.forName("org.abia.Agents.BovespaAgent.Cotacao");
			ClassesToRegister[1]=Class.forName("org.abia.Agents.BovespaAgent.Empresa");
			ClassesToRegister[2]=Class.forName("org.abia.Agents.BovespaAgent.Indice");
			ClassesToRegister[3]=Class.forName("org.abia.Agents.BovespaAgent.Papel");
			ClassesToRegister[4]=Class.forName("org.abia.Agents.BovespaAgent.Pregao");
			ClassesToRegister[5]=Class.forName("org.abia.Agents.BovespaAgent.PapelEmIndice");
			ClassesToRegister[6]=Class.forName("org.abia.Agents.BovespaAgent.CotacaoIndice");

			Blackboard.getBlackboard().RegisterAgent(this);
			Blackboard.getBlackboard().RegisterAgentData(ClassesToRegister);
		}catch(Exception e){
			throw (new AgentException());
		}		
				
	}
	
	public String getAgentNameInBlackboard(){
		return "BovespaAgent";		
	}
	
}