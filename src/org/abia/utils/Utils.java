package org.abia.utils;

import java.util.*;
import java.io.*;
import java.sql.*;

import org.abia.AgentContainer.*;
import org.abia.AgentContainer.WebRetriever.*;
import org.abia.Agents.BovespaAgent.*;
import org.abia.utils.*;
import org.abia.WebEmulator.*;
import org.abia.Blackboard.*;
import org.abia.Blackboard.PostgreSQL.*;

import java.net.*;

class BDIIndice {
	protected String DateQuote;
	
	protected Double Open;
	protected Double High;
	protected Double Low;
	protected Double Close;	
	protected Double Volume;
	
	protected String NomeIndice;

	public BDIIndice(){
		
	}

	public void setDate(String DateToSet){
		DateQuote=DateToSet;
	}
	
	public void setOpen(Double newOpen){
		Open=newOpen;	
	}
	public void setHigh(Double newHigh){
		High=newHigh;	
	}
	public void setLow(Double newLow){
		Low=newLow;	
	}
	public void setClose(Double newClose){
		Close=newClose;	
	}
	public void setVolume(Double newVolume){
		Volume=newVolume;
	}

	public void setNomeIndice(String newNomeIndice){
		NomeIndice=newNomeIndice;
	}


	protected String getValueIntoBDIFormat(Double Value){
		String  Reais;
		String  TotalValor="";		
		int     IntFromDouble;
		int     f;
		
		Reais=(new Integer(Value.intValue())).toString();
		for(f=Reais.length();f<6;f++){
			TotalValor+="0";
		}
		TotalValor=TotalValor+Reais;
		return TotalValor;		
	}
	
	public String toString(){
		String Retorno="",Espacos1="",Espacos2="";
		
		int f;		
		for(f=NomeIndice.length();f<30;f++) Espacos1+=" ";		
		Retorno=new String("0101"+NomeIndice+Espacos1);
		Retorno+=getValueIntoBDIFormat(Open);
		Retorno+=getValueIntoBDIFormat(Low);
		Retorno+=getValueIntoBDIFormat(High);
		Retorno+=getValueIntoBDIFormat(new Double(0));
		Retorno+=getValueIntoBDIFormat(new Double(0));
		Retorno+=getValueIntoBDIFormat(new Double(0));
		Retorno+=DateQuote;
		Retorno+=getValueIntoBDIFormat(new Double(0));
		Retorno+=DateQuote;
		Retorno+=getValueIntoBDIFormat(Close);
		Retorno+=getValueIntoBDIFormat(Volume);		
		return Retorno;
	}

	
}

class BDICotacao {
	protected String DateQuote;
	
	protected Double Open;
	protected Double High;
	protected Double Low;
	protected Double Close;	
	protected Double Volume;
	
	protected String NomePregao;
	protected String TipoPapel;
	protected String NomeEmpresa;
	protected String CodigoPapel;	
	
	public BDICotacao(){
		
	}
	
	public void setDate(String DateToSet){
		DateQuote=DateToSet;
	}
	
	public void setOpen(Double newOpen){
		Open=newOpen;	
	}
	public void setHigh(Double newHigh){
		High=newHigh;	
	}
	public void setLow(Double newLow){
		Low=newLow;	
	}
	public void setClose(Double newClose){
		Close=newClose;	
	}
	public void setVolume(Double newVolume){
		Volume=newVolume;
	}
	public void setNomePregao(String newNomePregao){
		NomePregao=newNomePregao;
	}
	public void setTipoPapel(String newTipoPapel){
		TipoPapel=newTipoPapel;		
	}
	public void setNomeEmpresa(String newNomeEmpresa){
		NomeEmpresa=newNomeEmpresa;			
	}

	public void setCodigoPapel(String newCodigoPapel){
		CodigoPapel=newCodigoPapel;			
	}
	
	public String getCodigoPapel(){
		return CodigoPapel;			
	}
	
	
	protected String getValueIntoBDIFormat(Double Value){
		String  Cents;
		String  Reais;
		String  TotalValor="";		
		int     IntFromDouble;
		int     f;
		double  Original,Inteiro,Resto;
		
		
		Original=Value.doubleValue();
		Inteiro=Value.intValue();
		Resto=Original-Inteiro;

		Cents=(new Double(Resto)).toString();
		try{
		   Cents=Cents.substring(Cents.indexOf(".")+1);
		   if(Cents.length()>2)
		      Cents=Cents.substring(0,1);
   	       if(Cents.length()==1) Cents+=0;
		}catch(Exception e){
			System.out.println(e);			
		}
		
		Reais=(new Integer(Value.intValue())).toString();
		for(f=Reais.length();f<9;f++){
			TotalValor+="0";
		}
		TotalValor=TotalValor+Reais+Cents;
		System.out.println(TotalValor);
		return TotalValor;		
	}
	
	public String toString(){
		String Retorno,Espacos1="",Espacos2="";
		
		int f;
		
		for(f=NomePregao.length();f<12;f++) Espacos1+=" ";
		for(f=TipoPapel.length();f<11;f++) Espacos2+=" ";
		
		Retorno=new String("0202LOTE PADRAO                   "+
		                   NomePregao+Espacos1+
		                   TipoPapel+Espacos2+CodigoPapel);
		Retorno+="       010VISTA          000";
		Retorno+=getValueIntoBDIFormat(Open);
		Retorno+=getValueIntoBDIFormat(High);
		Retorno+=getValueIntoBDIFormat(Low);
		Retorno+=getValueIntoBDIFormat(new Double(0));
		Retorno+=getValueIntoBDIFormat(Close);
		Retorno+=getValueIntoBDIFormat(Volume);		
		return Retorno;
	}
	
}


class BDI {
	protected String   DateBDI;
	protected HashMap  Indices;
	protected HashMap  Cotacoes;
	
	public BDI(){
		Cotacoes=new HashMap();
		Indices=new HashMap();
	}
	
	public void addBDIIndice(BDIIndice newIndice){
		Indices.put("",newIndice);		
	}

	public void addBDICotacao(BDICotacao newCotacao){
		Cotacoes.put(newCotacao.getCodigoPapel(),newCotacao);		
	}
	
	public void setDate(String newDateBDI){
		DateBDI=newDateBDI;		
	}

	public String  getDate(){
		return DateBDI;	
	}
	
	public String toString(){
		int         f;
		String      Retorno="";
		Object      obj[];
		BDIIndice   currentIndice;
		BDICotacao  currentCotacao;
		
		
		Retorno+="00BDIN9999BOVESPA 9999";
		Retorno+=DateBDI;
		Retorno+=DateBDI;
		Retorno+="\r\n";
		
		obj=Indices.values().toArray();
		for(f=0;f<obj.length;f++){
			currentIndice=(BDIIndice)obj[f];
			Retorno+=currentIndice+"\r\n";						
		}

		obj=Cotacoes.values().toArray();
		for(f=0;f<obj.length;f++){
			currentCotacao=(BDICotacao)obj[f];
			Retorno+=currentCotacao+"\r\n";						
		}		
		return Retorno;
	}
	
}

public class Utils {

	public static String TargetServer="127.0.0.1:8080";
	 	
	public static String ResolveMonth(String MonthToResolve){
		if(MonthToResolve.equals("Jan")) return "01";
		if(MonthToResolve.equals("Feb")) return "02";		
		if(MonthToResolve.equals("Mar")) return "03";
		if(MonthToResolve.equals("Apr")) return "04";
		if(MonthToResolve.equals("May")) return "05";		
		if(MonthToResolve.equals("Jun")) return "06";
		if(MonthToResolve.equals("Jul")) return "07";		
		if(MonthToResolve.equals("Aug")) return "08";
		if(MonthToResolve.equals("Sep")) return "09";
		if(MonthToResolve.equals("Oct")) return "10";		
		if(MonthToResolve.equals("Nov")) return "11";
		if(MonthToResolve.equals("Dec")) return "12";		
		return "";
	}
	public static void Unzip(String target){
		String teste;
		
		try{		
		   teste="C:\\cygwin\\bin\\unzip.exe "+target+" -d .\\temp";		
		   Runtime.getRuntime().exec(teste);
		}catch(Exception e){
		   System.out.println(e);
		}
		
	}
	
	public static void CriarCVM(){
		String                     TotalURL,Retorno,Headers[],CookiesTotal;
		Object                     obj;
		WebRetrieverClient         theClient=null;
		DataOutputStream           out2;
		Vector                     Cookies=new Vector();
		int                        f,h,pos,g;
		AgentData                  PapeisToRetrieve[];
		Empresa                    CurrentEmpresa;
		String                     DataDPR[],DataITR[],TempValue,TempKey;
		
		DataDPR=new String[4];
		DataDPR[0]="31%2F12%2F2002";
		DataDPR[1]="31%2F12%2F2001";
		DataDPR[2]="31%2F12%2F2000";
		DataDPR[3]="31%2F12%2F1999";
		
		DataITR=new String[14];
		DataITR[0]="31%2F03%2F2003";
		DataITR[1]="30%2F06%2F2003";

		DataITR[2]="31%2F03%2F2002";
		DataITR[3]="30%2F06%2F2002";
		DataITR[4]="30%2F09%2F2002";
				
		DataITR[5]="31%2F03%2F2001";
		DataITR[6]="30%2F06%2F2001";
		DataITR[7]="30%2F09%2F2001";
		
		DataITR[8]="31%2F03%2F2000";
		DataITR[9]="30%2F06%2F2000";
		DataITR[10]="30%2F09%2F2000";		

		DataITR[11]="31%2F03%2F1999";
		DataITR[12]="30%2F06%2F1999";
		DataITR[13]="30%2F09%2F1999";
				
		try{
		   
		   PapeisToRetrieve=Blackboard.getBlackboard().Query(Papel.class,null);
		   for(f=0;f<PapeisToRetrieve.length;f++){
		   	   if(f<6) continue;
		   	   PapeisToRetrieve[f].ResolveReferences();
		   	   CurrentEmpresa=(Empresa)(((Papel)PapeisToRetrieve[f]).getEmpresaEmitente());
		   	   for(g=0;g<DataDPR.length;g++){		   
				   theClient=AgentContainer.getWebRetriever().createClient();
				   theClient.RegisterContentHandler("text/html",Class.forName("org.abia.AgentContainer.WebRetriever.HTMLContentHandler"));
				   TotalURL="http://siteempresas.bovespa.com.br/divext/FrDIVEXT.asp?site=B&mercado=1&razao=" + CurrentEmpresa.getNomeEmpresa() + "&pregao=" + CurrentEmpresa.getNomePregao() + "&ccvm=" + CurrentEmpresa.getCodigoCVM() + "&tipo=2&data=" + DataDPR[g];
				   obj=theClient.getURL(TotalURL,null);
				   CookiesTotal="";
				   if(obj instanceof HTMLContentHandler){
					  HTMLContentHandler HTMLHandler;
					  
					  HTMLHandler=(HTMLContentHandler)obj;
					  if(HTMLHandler.getResponseStatus()!=
						 HttpURLConnection.HTTP_OK){

					  }
					  CookiesTotal="";			  
					  Headers=HTMLHandler.getHeaders();
					  for(h=0;h<Headers.length;h++){
				  		  pos=Headers[h].indexOf("Set-Cookie:");
					  	  if(pos>-1){
						    CookiesTotal=CookiesTotal+Headers[h].substring(pos+11,Headers[h].indexOf(";"))+";";
					  	  }
					  }
			  
					 CookiesTotal=CookiesTotal.substring(0,CookiesTotal.length()-1);
	
		             TempKey=CookiesTotal.substring(0,CookiesTotal.indexOf("="));		
					 TempValue=CookiesTotal.substring(CookiesTotal.indexOf("=")+1);
					 theClient.getCookiesHandler().addValue(TempKey,TempValue);
			  	
					 Retorno=HTMLHandler.getContentAsString();
			  						  
					 out2=new DataOutputStream(new BufferedOutputStream(new FileOutputStream("C:\\DPR_"+CurrentEmpresa.getCodigoCVM()+"_"+ DataDPR[g] +"_" +".txt")));
					 out2.writeBytes(Retorno);
					 out2.close();		   	   
		   	   	  }
				TotalURL="http://siteempresas.bovespa.com.br/divext/DIVEXTG1Identificacao.asp";
				obj=theClient.getURL(TotalURL,null);
				if(obj instanceof HTMLContentHandler){
				   HTMLContentHandler HTMLHandler;
					  
				   HTMLHandler=(HTMLContentHandler)obj;
				   if(HTMLHandler.getResponseStatus()!=
					HttpURLConnection.HTTP_OK){

				 }
				 Headers=HTMLHandler.getHeaders();
				 for(h=0;h<Headers.length;h++){
					 pos=Headers[h].indexOf("Set-Cookie:");
					 if(pos>-1)
					   CookiesTotal=CookiesTotal+Headers[h].substring(pos+11,Headers[h].indexOf(";"))+";";
				 }
				CookiesTotal=CookiesTotal.substring(0,CookiesTotal.length()-1);
				TempKey=CookiesTotal.substring(0,CookiesTotal.indexOf("="));		
				TempValue=CookiesTotal.substring(CookiesTotal.indexOf("=")+1);
				theClient.getCookiesHandler().addValue(TempKey,TempValue);

				Retorno=HTMLHandler.getContentAsString();
			  						  
				out2=new DataOutputStream(new BufferedOutputStream(new FileOutputStream("C:\\DPR_Identificao_"+CurrentEmpresa.getCodigoCVM()+"_"+ DataDPR[g] +"_" +".txt")));
				out2.writeBytes(Retorno);
				out2.close();		   	   
			  }

				TotalURL="http://siteempresas.bovespa.com.br/divext/DIVEXTG1Sede.asp";
				obj=theClient.getURL(TotalURL,null);
				if(obj instanceof HTMLContentHandler){
				   HTMLContentHandler HTMLHandler;
					  
				   HTMLHandler=(HTMLContentHandler)obj;
				   if(HTMLHandler.getResponseStatus()!=
					HttpURLConnection.HTTP_OK){

				 }
				 Headers=HTMLHandler.getHeaders();
				 for(h=0;h<Headers.length;h++){
					 pos=Headers[h].indexOf("Set-Cookie:");
					 if(pos>-1)
					   CookiesTotal=CookiesTotal+Headers[h].substring(pos+11,Headers[h].indexOf(";"))+";";
				 }
				CookiesTotal=CookiesTotal.substring(0,CookiesTotal.length()-1);
				TempKey=CookiesTotal.substring(0,CookiesTotal.indexOf("="));		
				TempValue=CookiesTotal.substring(CookiesTotal.indexOf("=")+1);
				theClient.getCookiesHandler().addValue(TempKey,TempValue);

				Retorno=HTMLHandler.getContentAsString();
			  						  
				out2=new DataOutputStream(new BufferedOutputStream(new FileOutputStream("C:\\DPR_Sede_"+CurrentEmpresa.getCodigoCVM()+"_"+ DataDPR[g] +"_" +".txt")));
				out2.writeBytes(Retorno);
				out2.close();		   	   
			  }

				TotalURL="http://siteempresas.bovespa.com.br/divext/DIVEXTG1ComposicaoCapital.asp";
				obj=theClient.getURL(TotalURL,null);
				if(obj instanceof HTMLContentHandler){
				   HTMLContentHandler HTMLHandler;
					  
				   HTMLHandler=(HTMLContentHandler)obj;
				   if(HTMLHandler.getResponseStatus()!=
					HttpURLConnection.HTTP_OK){

				 }
				 Headers=HTMLHandler.getHeaders();
				 for(h=0;h<Headers.length;h++){
					 pos=Headers[h].indexOf("Set-Cookie:");
					 if(pos>-1)
					   CookiesTotal=CookiesTotal+Headers[h].substring(pos+11,Headers[h].indexOf(";"))+";";
				 }
				CookiesTotal=CookiesTotal.substring(0,CookiesTotal.length()-1);
				TempKey=CookiesTotal.substring(0,CookiesTotal.indexOf("="));		
				TempValue=CookiesTotal.substring(CookiesTotal.indexOf("=")+1);
				theClient.getCookiesHandler().addValue(TempKey,TempValue);

				Retorno=HTMLHandler.getContentAsString();
			  						  
				out2=new DataOutputStream(new BufferedOutputStream(new FileOutputStream("C:\\DPR_ComposicaoCapital_"+CurrentEmpresa.getCodigoCVM()+"_"+ DataDPR[g] +"_" +".txt")));
				out2.writeBytes(Retorno);
				out2.close();		   	   
			  }

				TotalURL="http://siteempresas.bovespa.com.br/divext/DIVEXTG1CaracteristicasEmpresa.asp";
				obj=theClient.getURL(TotalURL,null);
				if(obj instanceof HTMLContentHandler){
				   HTMLContentHandler HTMLHandler;
					  
				   HTMLHandler=(HTMLContentHandler)obj;
				   if(HTMLHandler.getResponseStatus()!=
					HttpURLConnection.HTTP_OK){

				 }
				 Headers=HTMLHandler.getHeaders();
				 for(h=0;h<Headers.length;h++){
					 pos=Headers[h].indexOf("Set-Cookie:");
					 if(pos>-1)
					   CookiesTotal=CookiesTotal+Headers[h].substring(pos+11,Headers[h].indexOf(";"))+";";
				 }
				CookiesTotal=CookiesTotal.substring(0,CookiesTotal.length()-1);
				TempKey=CookiesTotal.substring(0,CookiesTotal.indexOf("="));		
				TempValue=CookiesTotal.substring(CookiesTotal.indexOf("=")+1);
				theClient.getCookiesHandler().addValue(TempKey,TempValue);

				Retorno=HTMLHandler.getContentAsString();
			  						  
				out2=new DataOutputStream(new BufferedOutputStream(new FileOutputStream("C:\\DPR_CaracteristicasEmpresa_"+CurrentEmpresa.getCodigoCVM()+"_"+ DataDPR[g] +"_" +".txt")));
				out2.writeBytes(Retorno);
				out2.close();		   	   
			  }

				TotalURL="http://siteempresas.bovespa.com.br/divext/DIVEXTG1ProventosDinheiro.asp";
				obj=theClient.getURL(TotalURL,null);
				if(obj instanceof HTMLContentHandler){
				   HTMLContentHandler HTMLHandler;
					  
				   HTMLHandler=(HTMLContentHandler)obj;
				   if(HTMLHandler.getResponseStatus()!=
					HttpURLConnection.HTTP_OK){

				 }
				 Headers=HTMLHandler.getHeaders();
				 for(h=0;h<Headers.length;h++){
					 pos=Headers[h].indexOf("Set-Cookie:");
					 if(pos>-1)
					   CookiesTotal=CookiesTotal+Headers[h].substring(pos+11,Headers[h].indexOf(";"))+";";
				 }
				CookiesTotal=CookiesTotal.substring(0,CookiesTotal.length()-1);
				TempKey=CookiesTotal.substring(0,CookiesTotal.indexOf("="));		
				TempValue=CookiesTotal.substring(CookiesTotal.indexOf("=")+1);
				theClient.getCookiesHandler().addValue(TempKey,TempValue);

				Retorno=HTMLHandler.getContentAsString();
			  						  
				out2=new DataOutputStream(new BufferedOutputStream(new FileOutputStream("C:\\DPR_ProventosDinheiro_"+CurrentEmpresa.getCodigoCVM()+"_"+ DataDPR[g] +"_" +".txt")));
				out2.writeBytes(Retorno);
				out2.close();		   	   
			  }

				TotalURL="http://siteempresas.bovespa.com.br/divext/DIVEXTBalancoPatrimonial.asp?TipoInfo=C&Tipo=01 - Ativo";
				obj=theClient.getURL(TotalURL,null);
				if(obj instanceof HTMLContentHandler){
				   HTMLContentHandler HTMLHandler;
					  
				   HTMLHandler=(HTMLContentHandler)obj;
				   if(HTMLHandler.getResponseStatus()!=
					HttpURLConnection.HTTP_OK){

				 }
				 Headers=HTMLHandler.getHeaders();
				 for(h=0;h<Headers.length;h++){
					 pos=Headers[h].indexOf("Set-Cookie:");
					 if(pos>-1)
					   CookiesTotal=CookiesTotal+Headers[h].substring(pos+11,Headers[h].indexOf(";"))+";";
				 }
				CookiesTotal=CookiesTotal.substring(0,CookiesTotal.length()-1);
				TempKey=CookiesTotal.substring(0,CookiesTotal.indexOf("="));		
				TempValue=CookiesTotal.substring(CookiesTotal.indexOf("=")+1);
				theClient.getCookiesHandler().addValue(TempKey,TempValue);

				Retorno=HTMLHandler.getContentAsString();
			  						  
				out2=new DataOutputStream(new BufferedOutputStream(new FileOutputStream("C:\\DPR_Ativo_"+CurrentEmpresa.getCodigoCVM()+"_"+ DataDPR[g] +"_" +".txt")));
				out2.writeBytes(Retorno);
				out2.close();		   	   
			  }

				TotalURL="http://siteempresas.bovespa.com.br/divext/DIVEXTBalancoPatrimonial.asp?TipoInfo=C&Tipo=02 - Passivo";
				obj=theClient.getURL(TotalURL,null);
				if(obj instanceof HTMLContentHandler){
				   HTMLContentHandler HTMLHandler;
					  
				   HTMLHandler=(HTMLContentHandler)obj;
				   if(HTMLHandler.getResponseStatus()!=
					HttpURLConnection.HTTP_OK){

				 }
				 Headers=HTMLHandler.getHeaders();
				 for(h=0;h<Headers.length;h++){
					 pos=Headers[h].indexOf("Set-Cookie:");
					 if(pos>-1)
					   CookiesTotal=CookiesTotal+Headers[h].substring(pos+11,Headers[h].indexOf(";"))+";";
				 }
				CookiesTotal=CookiesTotal.substring(0,CookiesTotal.length()-1);
				Retorno=HTMLHandler.getContentAsString();
			  						  
				out2=new DataOutputStream(new BufferedOutputStream(new FileOutputStream("C:\\DPR_Passivo_"+CurrentEmpresa.getCodigoCVM()+"_"+ DataDPR[g] +"_" +".txt")));
				out2.writeBytes(Retorno);
				out2.close();		   	   
			  }

				TotalURL="http://siteempresas.bovespa.com.br/divext/DIVEXTDemonstracaoResultado.asp?TipoInfo=C";
				obj=theClient.getURL(TotalURL,null);
				if(obj instanceof HTMLContentHandler){
				   HTMLContentHandler HTMLHandler;
					  
				   HTMLHandler=(HTMLContentHandler)obj;
				   if(HTMLHandler.getResponseStatus()!=
					HttpURLConnection.HTTP_OK){

				 }
				 Headers=HTMLHandler.getHeaders();
				 for(h=0;h<Headers.length;h++){
					 pos=Headers[h].indexOf("Set-Cookie:");
					 if(pos>-1)
					   CookiesTotal=CookiesTotal+Headers[h].substring(pos+11,Headers[h].indexOf(";"))+";";
				 }
				CookiesTotal=CookiesTotal.substring(0,CookiesTotal.length()-1);
				Retorno=HTMLHandler.getContentAsString();
			  						  
				out2=new DataOutputStream(new BufferedOutputStream(new FileOutputStream("C:\\DPR_DemonstracaoResultado_"+CurrentEmpresa.getCodigoCVM()+"_"+ DataDPR[g] +"_" +".txt")));
				out2.writeBytes(Retorno);
				out2.close();		   	   
			  }

				TotalURL="http://siteempresas.bovespa.com.br/divext/DIVEXTLucroPrejuizo.asp?TipoInfo=C";
				obj=theClient.getURL(TotalURL,null);
				if(obj instanceof HTMLContentHandler){
				   HTMLContentHandler HTMLHandler;
					  
				   HTMLHandler=(HTMLContentHandler)obj;
				   if(HTMLHandler.getResponseStatus()!=
					HttpURLConnection.HTTP_OK){

				 }
				 Headers=HTMLHandler.getHeaders();
				 for(h=0;h<Headers.length;h++){
					 pos=Headers[h].indexOf("Set-Cookie:");
					 if(pos>-1)
					   CookiesTotal=CookiesTotal+Headers[h].substring(pos+11,Headers[h].indexOf(";"))+";";
				 }
				CookiesTotal=CookiesTotal.substring(0,CookiesTotal.length()-1);
				Retorno=HTMLHandler.getContentAsString();
			  						  
				out2=new DataOutputStream(new BufferedOutputStream(new FileOutputStream("C:\\DPR_LucroPrejuizo_"+CurrentEmpresa.getCodigoCVM()+"_"+ DataDPR[g] +"_" +".txt")));
				out2.writeBytes(Retorno);
				out2.close();		   	   
			  }
			    		   	   	  
		   }
		   
		   for(g=0;g<DataITR.length;g++){
			   theClient=AgentContainer.getWebRetriever().createClient();
			   theClient.RegisterContentHandler("text/html",Class.forName("org.abia.AgentContainer.WebRetriever.HTMLContentHandler"));		   	
			   TotalURL="http://siteempresas.bovespa.com.br/divext/FrDIVEXT.asp?site=B&mercado=1&razao=" + CurrentEmpresa.getNomeEmpresa() + "&pregao=" + CurrentEmpresa.getNomePregao() + "&ccvm=" + CurrentEmpresa.getCodigoCVM() + "&tipo=4&data=" + DataITR[g];
			   obj=theClient.getURL(TotalURL,null);
			   CookiesTotal="";
			   if(obj instanceof HTMLContentHandler){
				  HTMLContentHandler HTMLHandler;
					  
				  HTMLHandler=(HTMLContentHandler)obj;
				  if(HTMLHandler.getResponseStatus()!=
					 HttpURLConnection.HTTP_OK){

				  }
				  CookiesTotal="";			  
				  Headers=HTMLHandler.getHeaders();
				  for(h=0;h<Headers.length;h++){
					  pos=Headers[h].indexOf("Set-Cookie:");
					  if(pos>-1){
						CookiesTotal=CookiesTotal+Headers[h].substring(pos+11,Headers[h].indexOf(";"))+";";
					  }
				  }
			  
				 CookiesTotal=CookiesTotal.substring(0,CookiesTotal.length()-1);
	
				 TempKey=CookiesTotal.substring(0,CookiesTotal.indexOf("="));		
				 TempValue=CookiesTotal.substring(CookiesTotal.indexOf("=")+1);
				 theClient.getCookiesHandler().addValue(TempKey,TempValue);
			  	
				 Retorno=HTMLHandler.getContentAsString();
			  						  
				 out2=new DataOutputStream(new BufferedOutputStream(new FileOutputStream("C:\\ITR_"+CurrentEmpresa.getCodigoCVM()+"_"+ DataITR[g] +"_" +".txt")));
				 out2.writeBytes(Retorno);
				 out2.close();		   	   
			  }
			TotalURL="http://siteempresas.bovespa.com.br/divext/DIVEXTG1Identificacao.asp";
			obj=theClient.getURL(TotalURL,null);
			if(obj instanceof HTMLContentHandler){
			   HTMLContentHandler HTMLHandler;
					  
			   HTMLHandler=(HTMLContentHandler)obj;
			   if(HTMLHandler.getResponseStatus()!=
				HttpURLConnection.HTTP_OK){

			 }
			 Headers=HTMLHandler.getHeaders();
			 for(h=0;h<Headers.length;h++){
				 pos=Headers[h].indexOf("Set-Cookie:");
				 if(pos>-1)
				   CookiesTotal=CookiesTotal+Headers[h].substring(pos+11,Headers[h].indexOf(";"))+";";
			 }
			CookiesTotal=CookiesTotal.substring(0,CookiesTotal.length()-1);
			TempKey=CookiesTotal.substring(0,CookiesTotal.indexOf("="));		
			TempValue=CookiesTotal.substring(CookiesTotal.indexOf("=")+1);
			theClient.getCookiesHandler().addValue(TempKey,TempValue);

			Retorno=HTMLHandler.getContentAsString();
			  						  
			out2=new DataOutputStream(new BufferedOutputStream(new FileOutputStream("C:\\ITR_Identificao_"+CurrentEmpresa.getCodigoCVM()+"_"+ DataITR[g] +"_" +".txt")));
			out2.writeBytes(Retorno);
			out2.close();		   	   
		  }

			TotalURL="http://siteempresas.bovespa.com.br/divext/DIVEXTG1Sede.asp";
			obj=theClient.getURL(TotalURL,null);
			if(obj instanceof HTMLContentHandler){
			   HTMLContentHandler HTMLHandler;
					  
			   HTMLHandler=(HTMLContentHandler)obj;
			   if(HTMLHandler.getResponseStatus()!=
				HttpURLConnection.HTTP_OK){

			 }
			 Headers=HTMLHandler.getHeaders();
			 for(h=0;h<Headers.length;h++){
				 pos=Headers[h].indexOf("Set-Cookie:");
				 if(pos>-1)
				   CookiesTotal=CookiesTotal+Headers[h].substring(pos+11,Headers[h].indexOf(";"))+";";
			 }
			CookiesTotal=CookiesTotal.substring(0,CookiesTotal.length()-1);
			TempKey=CookiesTotal.substring(0,CookiesTotal.indexOf("="));		
			TempValue=CookiesTotal.substring(CookiesTotal.indexOf("=")+1);
			theClient.getCookiesHandler().addValue(TempKey,TempValue);

			Retorno=HTMLHandler.getContentAsString();
			  						  
			out2=new DataOutputStream(new BufferedOutputStream(new FileOutputStream("C:\\ITR_Sede_"+CurrentEmpresa.getCodigoCVM()+"_"+ DataITR[g] +"_" +".txt")));
			out2.writeBytes(Retorno);
			out2.close();		   	   
		  }

			TotalURL="http://siteempresas.bovespa.com.br/divext/DIVEXTG1ComposicaoCapital.asp";
			obj=theClient.getURL(TotalURL,null);
			if(obj instanceof HTMLContentHandler){
			   HTMLContentHandler HTMLHandler;
					  
			   HTMLHandler=(HTMLContentHandler)obj;
			   if(HTMLHandler.getResponseStatus()!=
				HttpURLConnection.HTTP_OK){

			 }
			 Headers=HTMLHandler.getHeaders();
			 for(h=0;h<Headers.length;h++){
				 pos=Headers[h].indexOf("Set-Cookie:");
				 if(pos>-1)
				   CookiesTotal=CookiesTotal+Headers[h].substring(pos+11,Headers[h].indexOf(";"))+";";
			 }
			CookiesTotal=CookiesTotal.substring(0,CookiesTotal.length()-1);
			TempKey=CookiesTotal.substring(0,CookiesTotal.indexOf("="));		
			TempValue=CookiesTotal.substring(CookiesTotal.indexOf("=")+1);
			theClient.getCookiesHandler().addValue(TempKey,TempValue);

			Retorno=HTMLHandler.getContentAsString();
			  						  
			out2=new DataOutputStream(new BufferedOutputStream(new FileOutputStream("C:\\ITR_ComposicaoCapital_"+CurrentEmpresa.getCodigoCVM()+"_"+ DataITR[g] +"_" +".txt")));
			out2.writeBytes(Retorno);
			out2.close();		   	   
		  }

			TotalURL="http://siteempresas.bovespa.com.br/divext/DIVEXTG1CaracteristicasEmpresa.asp";
			obj=theClient.getURL(TotalURL,null);
			if(obj instanceof HTMLContentHandler){
			   HTMLContentHandler HTMLHandler;
					  
			   HTMLHandler=(HTMLContentHandler)obj;
			   if(HTMLHandler.getResponseStatus()!=
				HttpURLConnection.HTTP_OK){

			 }
			 Headers=HTMLHandler.getHeaders();
			 for(h=0;h<Headers.length;h++){
				 pos=Headers[h].indexOf("Set-Cookie:");
				 if(pos>-1)
				   CookiesTotal=CookiesTotal+Headers[h].substring(pos+11,Headers[h].indexOf(";"))+";";
			 }
			CookiesTotal=CookiesTotal.substring(0,CookiesTotal.length()-1);
			TempKey=CookiesTotal.substring(0,CookiesTotal.indexOf("="));		
			TempValue=CookiesTotal.substring(CookiesTotal.indexOf("=")+1);
			theClient.getCookiesHandler().addValue(TempKey,TempValue);

			Retorno=HTMLHandler.getContentAsString();
			  						  
			out2=new DataOutputStream(new BufferedOutputStream(new FileOutputStream("C:\\ITR_CaracteristicasEmpresa_"+CurrentEmpresa.getCodigoCVM()+"_"+ DataITR[g] +"_" +".txt")));
			out2.writeBytes(Retorno);
			out2.close();		   	   
		  }

			TotalURL="http://siteempresas.bovespa.com.br/divext/DIVEXTG1ProventosDinheiro.asp";
			obj=theClient.getURL(TotalURL,null);
			if(obj instanceof HTMLContentHandler){
			   HTMLContentHandler HTMLHandler;
					  
			   HTMLHandler=(HTMLContentHandler)obj;
			   if(HTMLHandler.getResponseStatus()!=
				HttpURLConnection.HTTP_OK){

			 }
			 Headers=HTMLHandler.getHeaders();
			 for(h=0;h<Headers.length;h++){
				 pos=Headers[h].indexOf("Set-Cookie:");
				 if(pos>-1)
				   CookiesTotal=CookiesTotal+Headers[h].substring(pos+11,Headers[h].indexOf(";"))+";";
			 }
			CookiesTotal=CookiesTotal.substring(0,CookiesTotal.length()-1);
			TempKey=CookiesTotal.substring(0,CookiesTotal.indexOf("="));		
			TempValue=CookiesTotal.substring(CookiesTotal.indexOf("=")+1);
			theClient.getCookiesHandler().addValue(TempKey,TempValue);

			Retorno=HTMLHandler.getContentAsString();
			  						  
			out2=new DataOutputStream(new BufferedOutputStream(new FileOutputStream("C:\\ITR_ProventosDinheiro_"+CurrentEmpresa.getCodigoCVM()+"_"+ DataITR[g] +"_" +".txt")));
			out2.writeBytes(Retorno);
			out2.close();		   	   
		  }

			TotalURL="http://siteempresas.bovespa.com.br/divext/DIVEXTBalancoPatrimonial.asp?TipoInfo=C&Tipo=01 - Ativo";
			obj=theClient.getURL(TotalURL,null);
			if(obj instanceof HTMLContentHandler){
			   HTMLContentHandler HTMLHandler;
					  
			   HTMLHandler=(HTMLContentHandler)obj;
			   if(HTMLHandler.getResponseStatus()!=
				HttpURLConnection.HTTP_OK){

			 }
			 Headers=HTMLHandler.getHeaders();
			 for(h=0;h<Headers.length;h++){
				 pos=Headers[h].indexOf("Set-Cookie:");
				 if(pos>-1)
				   CookiesTotal=CookiesTotal+Headers[h].substring(pos+11,Headers[h].indexOf(";"))+";";
			 }
			CookiesTotal=CookiesTotal.substring(0,CookiesTotal.length()-1);
			TempKey=CookiesTotal.substring(0,CookiesTotal.indexOf("="));		
			TempValue=CookiesTotal.substring(CookiesTotal.indexOf("=")+1);
			theClient.getCookiesHandler().addValue(TempKey,TempValue);

			Retorno=HTMLHandler.getContentAsString();
			  						  
			out2=new DataOutputStream(new BufferedOutputStream(new FileOutputStream("C:\\ITR_Ativo_"+CurrentEmpresa.getCodigoCVM()+"_"+ DataITR[g] +"_" +".txt")));
			out2.writeBytes(Retorno);
			out2.close();		   	   
		  }

			TotalURL="http://siteempresas.bovespa.com.br/divext/DIVEXTBalancoPatrimonial.asp?TipoInfo=C&Tipo=02 - Passivo";
			obj=theClient.getURL(TotalURL,null);
			if(obj instanceof HTMLContentHandler){
			   HTMLContentHandler HTMLHandler;
					  
			   HTMLHandler=(HTMLContentHandler)obj;
			   if(HTMLHandler.getResponseStatus()!=
				HttpURLConnection.HTTP_OK){

			 }
			 Headers=HTMLHandler.getHeaders();
			 for(h=0;h<Headers.length;h++){
				 pos=Headers[h].indexOf("Set-Cookie:");
				 if(pos>-1)
				   CookiesTotal=CookiesTotal+Headers[h].substring(pos+11,Headers[h].indexOf(";"))+";";
			 }
			CookiesTotal=CookiesTotal.substring(0,CookiesTotal.length()-1);
			Retorno=HTMLHandler.getContentAsString();
			  						  
			out2=new DataOutputStream(new BufferedOutputStream(new FileOutputStream("C:\\ITR_Passivo_"+CurrentEmpresa.getCodigoCVM()+"_"+ DataITR[g] +"_" +".txt")));
			out2.writeBytes(Retorno);
			out2.close();		   	   
		  }

			TotalURL="http://siteempresas.bovespa.com.br/divext/DIVEXTDemonstracaoResultado.asp?TipoInfo=C";
			obj=theClient.getURL(TotalURL,null);
			if(obj instanceof HTMLContentHandler){
			   HTMLContentHandler HTMLHandler;
					  
			   HTMLHandler=(HTMLContentHandler)obj;
			   if(HTMLHandler.getResponseStatus()!=
				HttpURLConnection.HTTP_OK){

			 }
			 Headers=HTMLHandler.getHeaders();
			 for(h=0;h<Headers.length;h++){
				 pos=Headers[h].indexOf("Set-Cookie:");
				 if(pos>-1)
				   CookiesTotal=CookiesTotal+Headers[h].substring(pos+11,Headers[h].indexOf(";"))+";";
			 }
			CookiesTotal=CookiesTotal.substring(0,CookiesTotal.length()-1);
			Retorno=HTMLHandler.getContentAsString();
			  						  
			out2=new DataOutputStream(new BufferedOutputStream(new FileOutputStream("C:\\ITR_DemonstracaoResultado_"+CurrentEmpresa.getCodigoCVM()+"_"+ DataITR[g] +"_" +".txt")));
			out2.writeBytes(Retorno);
			out2.close();		   	   
		  }

			TotalURL="http://siteempresas.bovespa.com.br/divext/DIVEXTLucroPrejuizo.asp?TipoInfo=C";
			obj=theClient.getURL(TotalURL,null);
			if(obj instanceof HTMLContentHandler){
			   HTMLContentHandler HTMLHandler;
					  
			   HTMLHandler=(HTMLContentHandler)obj;
			   if(HTMLHandler.getResponseStatus()!=
				HttpURLConnection.HTTP_OK){

			 }
			 Headers=HTMLHandler.getHeaders();
			 for(h=0;h<Headers.length;h++){
				 pos=Headers[h].indexOf("Set-Cookie:");
				 if(pos>-1)
				   CookiesTotal=CookiesTotal+Headers[h].substring(pos+11,Headers[h].indexOf(";"))+";";
			 }
			CookiesTotal=CookiesTotal.substring(0,CookiesTotal.length()-1);
			Retorno=HTMLHandler.getContentAsString();
			  						  
			out2=new DataOutputStream(new BufferedOutputStream(new FileOutputStream("C:\\ITR_LucroPrejuizo_"+CurrentEmpresa.getCodigoCVM()+"_"+ DataITR[g] +"_" +".txt")));
			out2.writeBytes(Retorno);
			out2.close();		   	   
		  }
			    		   	   	  
	   }		   
		   }
   

		}catch(Exception e){
			System.out.println(e);			
		}

		
		
	}

	
	
	public static void CriarBDIs(){
		String                     Simbolos[],DataFromYahoo,NovoRetorno,TableOk;
		int                        f,g,h,i,Ano,Mes,NumberTRs;		   	    
		String                     Base="",NomeURL="",Simbolo="",TotalURL="",TempDate="";
		int                        Mes_Inicial=0,Dia_Inicial=0,Ano_Inicial=0,Mes_Final=0,Dia_Final=0,Ano_Final=0;
		WebRetrieverClient         theClient=null;
		Object                     obj;
		String                     Strings[],Retorno="",TempString;
		DataOutputStream           out2;        
		HashMap                    FiltersToApply;
		HashMap                    BDIs;
		HTMLFilter                 CurrentFilter;
		PostgreSQLFilterCondition  Filters[];
		AgentData                  Papeis[]=null,Indices[]=null;
		Papel                      currentPapel;
		Indice                     currentIndice;
		BDIIndice                  currentBDIIndice;
		Empresa                    currentEmpresa;
		BDICotacao                 currentCotacao;
		BDI                        currentBDI;
		Object                     BDIsInObject[];
		
		Filters=new PostgreSQLFilterCondition[1]; 				
		FiltersToApply=new HashMap();
		BDIs=new HashMap();
		
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
		   theClient=AgentContainer.getWebRetriever().createClient();
		   theClient.RegisterContentHandler("text/html",Class.forName("org.abia.AgentContainer.WebRetriever.HTMLContentHandler"));    	
		}catch(Exception e){
			System.out.println(e);			
		}
		
		Papeis=null;
		try{
			Papeis=Blackboard.getBlackboard().Query(Papel.class,null);
			Indices=Blackboard.getBlackboard().Query(Indice.class,null);									
		}catch(Exception e){
			System.out.println(e);
		}
		
		for(Ano=2003;Ano>2000;Ano--){
		//for(Ano=2003;Ano>2002;Ano--){
			for(Mes=0;Mes<12;Mes++){
			//for(Mes=0;Mes<1;Mes++){
			System.out.println("Ano:"+Ano+" Mes:"+Mes);					
				for(f=0;f<Indices.length;f++){
					try{
						currentIndice=(Indice)Indices[f];
						if(Mes==11)
						   DataFromYahoo=RetrieveData.RetrieveYahoo(1,Mes+1,Ano,31,Mes+1,Ano,currentIndice.getCodigoPregao());
						else
						   DataFromYahoo=RetrieveData.RetrieveYahoo(1,Mes+1,Ano,1,Mes+2,Ano,currentIndice.getCodigoPregao());
						NovoRetorno=DataFromYahoo;
						TableOk=HTMLContentHandler.RetrieveTable(NovoRetorno,"3.2");
						NumberTRs=HTMLContentHandler.GetNumberOfRows(TableOk);
						for(h=0;h<NumberTRs-1;h++){					   	
							currentBDIIndice=new BDIIndice();
							TempString=HTMLContentHandler.RetrieveCell(TableOk,h+2,1);
							TempDate="20"+TempString.substring(7) +
									 Utils.ResolveMonth(TempString.substring(0,3)) +
									 TempString.substring(4,6);
							currentBDIIndice.setDate(TempDate);
							currentBDI=(BDI)BDIs.get(TempDate);
							if(currentBDI==null){
							   currentBDI=new BDI();
							   currentBDI.setDate(TempDate);
							   BDIs.put(TempDate,currentBDI);							  						   	
							}						   
							TempString=HTMLContentHandler.RetrieveCell(TableOk,h+2,2);
							TempString=TempString.replaceAll(",","");
							if(TempString.indexOf("Dividend")>-1) continue;
							if(TempString.indexOf("Split")>-1) continue;
							currentBDIIndice.setOpen(new Double(TempString));
							TempString=HTMLContentHandler.RetrieveCell(TableOk,h+2,3);
							TempString=TempString.replaceAll(",","");
							currentBDIIndice.setHigh(new Double(TempString));
							TempString=HTMLContentHandler.RetrieveCell(TableOk,h+2,4);
							TempString=TempString.replaceAll(",","");
							currentBDIIndice.setLow(new Double(TempString));
							TempString=HTMLContentHandler.RetrieveCell(TableOk,h+2,5);
							TempString=TempString.replaceAll(",","");
							currentBDIIndice.setClose(new Double(TempString));
							TempString=HTMLContentHandler.RetrieveCell(TableOk,h+2,6);
							TempString=TempString.replaceAll(",","");
							currentBDIIndice.setVolume(new Double(TempString));
							currentBDIIndice.setNomeIndice(currentIndice.getNomeIndice());
							currentBDI.addBDIIndice(currentBDIIndice); 
						}
						   						
					}catch(Exception e){
						System.out.println(e);						
					}					
				}
				for(f=0;f<Papeis.length;f++){
                    try{
                       currentPapel=(Papel)Papeis[f];
                       currentPapel.ResolveReferences();
					   if(Mes==11)
					      DataFromYahoo=RetrieveData.RetrieveYahoo(1,Mes+1,Ano,31,Mes+1,Ano,currentPapel.getCodigoPregao());
					   else
					      DataFromYahoo=RetrieveData.RetrieveYahoo(1,Mes+1,Ano,1,Mes+2,Ano,currentPapel.getCodigoPregao());
					   NovoRetorno=DataFromYahoo;
					   TableOk=HTMLContentHandler.RetrieveTable(NovoRetorno,"3.2");
					   NumberTRs=HTMLContentHandler.GetNumberOfRows(TableOk);
					   for(h=0;h<NumberTRs-1;h++){					   	
						   currentCotacao=new BDICotacao();
						   TempString=HTMLContentHandler.RetrieveCell(TableOk,h+2,1);
						   TempDate="20"+TempString.substring(7) +
						            Utils.ResolveMonth(TempString.substring(0,3)) +
						            TempString.substring(4,6);
						   currentCotacao.setDate(TempDate);
						   currentBDI=(BDI)BDIs.get(TempDate);
						   if(currentBDI==null){
							  currentBDI=new BDI();
							  currentBDI.setDate(TempDate);
							  BDIs.put(TempDate,currentBDI);							  						   	
						   }						   
						   TempString=HTMLContentHandler.RetrieveCell(TableOk,h+2,2);
						   if(TempString.indexOf("Dividend")>-1) continue;
						   if(TempString.indexOf("Split")>-1) continue;
						   currentCotacao.setOpen(new Double(TempString));
						   TempString=HTMLContentHandler.RetrieveCell(TableOk,h+2,3);
						   currentCotacao.setHigh(new Double(TempString));
						   TempString=HTMLContentHandler.RetrieveCell(TableOk,h+2,4);
						   currentCotacao.setLow(new Double(TempString));
						   TempString=HTMLContentHandler.RetrieveCell(TableOk,h+2,5);
						   currentCotacao.setClose(new Double(TempString));
						   TempString=HTMLContentHandler.RetrieveCell(TableOk,h+2,6);
						   TempString=TempString.replaceAll(",","");						   
						   currentCotacao.setVolume(new Double(TempString));
						   currentEmpresa=(Empresa)currentPapel.getEmpresaEmitente();
						   currentCotacao.setNomeEmpresa(currentEmpresa.getNomeEmpresa());
						   currentCotacao.setTipoPapel(currentPapel.getTipo());
						   currentCotacao.setNomePregao(currentEmpresa.getNomePregao());
						   currentCotacao.setCodigoPapel(currentPapel.getCodigoPregao());
						   currentBDI.addBDICotacao(currentCotacao); 
					   }
					   					  
                    }catch(Exception e){
                    	System.out.println(e);       						
                    }
				}				
			}
		}
		
		try{
	       BDIsInObject=BDIs.values().toArray();
	       for(f=0;f<BDIsInObject.length;f++){
			   
			   if(!(BDIsInObject[f] instanceof BDI)) 
			        continue;
			   currentBDI=(BDI)BDIsInObject[f];
			   System.out.println(currentBDI.getDate());
	           out2=new DataOutputStream(new BufferedOutputStream(new FileOutputStream("C:\\"+currentBDI.getDate()+".txt")));
		       out2.writeBytes(BDIsInObject[f].toString());
		       out2.close();
	       }
		}catch(Exception e){
			System.out.println(e);			
		}
			       							   		
	}
	
	public static void DownloadFromYahoo(){
		HashMap   FiltersToApply;
		HTMLFilter CurrentFilter;
		int h,g,f;
		byte b[];
		WebRetrieverClient theClient;
		Object             obj;
		String             Strings[];
		DataOutputStream   out2;
		String   NovoRetorno="",Retorno,NomeArquivo,Simbolos[]=null;		

	 		    Simbolos=new String[54];
				
				Simbolos[0]=new String("TNLP4");
				Simbolos[1]=new String("PETR4");
				Simbolos[2]=new String("EBTP4");
				Simbolos[3]=new String("BBDC4");
				Simbolos[4]=new String("TSPP4");
				Simbolos[5]=new String("ITAU4");
				Simbolos[6]=new String("ELET6");
				Simbolos[7]=new String("VALE5");
				Simbolos[8]=new String("CSNA3");
				Simbolos[9]=new String("EMBR4");
				Simbolos[10]=new String("PETR3");
				Simbolos[11]=new String("CMIG4");
				Simbolos[12]=new String("USIM5");
				Simbolos[13]=new String("BRTP4");
				Simbolos[14]=new String("BRTO4");
				Simbolos[15]=new String("GGBR4");
				Simbolos[16]=new String("TCOC4");
				Simbolos[17]=new String("BBAS3");
				Simbolos[18]=new String("ITSA4");
				Simbolos[19]=new String("TMAR5");
				Simbolos[20]=new String("VALE3");
				Simbolos[21]=new String("AMBV4");
				Simbolos[22]=new String("SBSP3");
				Simbolos[23]=new String("TMCP4");
				Simbolos[24]=new String("CPLE6");
				Simbolos[25]=new String("PLIM4");
				Simbolos[26]=new String("TNLP3");
				Simbolos[27]=new String("ELET3");
				Simbolos[28]=new String("TNEP4");
				Simbolos[29]=new String("CRUZ3");
				Simbolos[30]=new String("CSTB4");
				Simbolos[31]=new String("TCSL4");
				Simbolos[32]=new String("EBTP3");
				Simbolos[33]=new String("ARCZ6");
				Simbolos[34]=new String("EMBR3");
				Simbolos[35]=new String("VCPA4");
				Simbolos[36]=new String("CRTP5");
				Simbolos[37]=new String("TLPP4");
				Simbolos[38]=new String("ACES4");
				Simbolos[39]=new String("ELPL4");
				Simbolos[40]=new String("BRAP4");
				Simbolos[41]=new String("TRPL4");
				Simbolos[42]=new String("BRTP3");
				Simbolos[43]=new String("CLSC6");
				Simbolos[44]=new String("TLCP4");
				Simbolos[45]=new String("BRKM5");
				Simbolos[46]=new String("CESP4");
				Simbolos[47]=new String("TBLE3");
				Simbolos[48]=new String("CGAS5");
				Simbolos[49]=new String("KLBN4");
				Simbolos[50]=new String("TCSL3");
				Simbolos[51]=new String("PTIP4");
				Simbolos[52]=new String("LIGH3");
				Simbolos[53]=new String("CMIG3");

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
				           for(h=0;h<Simbolos.length;h++){
						   for(g=0;g<3;g++){
							   Retorno=RetrieveData.RetrieveYahoo(1,12,2003-g,31,12,2003-g,Simbolos[h]);		 
							   out2=new DataOutputStream(new BufferedOutputStream(new FileOutputStream(
								 "C:\\Program Files\\Apache Group\\Tomcat 4.1\\webapps\\abia\\ok\\_cotacoes_" + 
								 (new Integer(1)) + 
								 "_" + (new Integer(12)) + "_" + (new Integer(2003-g)) +
								 "_" + (new Integer(31)) + "_" + (new Integer(12)) +
								 "_" + (new Integer(2003-g)) + "_" + (new String(Simbolos[h])) + ".txt")));
							   NovoRetorno=HTMLContentHandler.ApplyFilter(Retorno,FiltersToApply,HTMLContentHandler.DEFAULT_DELETE); 
							   out2.writeBytes(NovoRetorno);
							   out2.close();       	
							   for(f=0;f<11;f++){
									Retorno=RetrieveData.RetrieveYahoo(1,11-f,2003-g,1,12-f,2003-g,Simbolos[h]);		 
									out2=new DataOutputStream(new BufferedOutputStream(new FileOutputStream(
										 "C:\\Program Files\\Apache Group\\Tomcat 4.1\\webapps\\abia\\ok\\_cotacoes_" + 
										 (new Integer(1)) + 
										 "_" + (new Integer(11-f)) + "_" + (new Integer(2003-g)) +
										 "_" + (new Integer(1)) + "_" + (new Integer(12-f)) +
										 "_" + (new Integer(2003-g)) + "_" + (new String(Simbolos[h])) + ".txt")));
									NovoRetorno=HTMLContentHandler.ApplyFilter(Retorno,FiltersToApply,HTMLContentHandler.DEFAULT_DELETE);
									out2.writeBytes(NovoRetorno);
									out2.close();		               	   		           	
							   }
						   }
						   }
						   
						   for(f=0;f<26;f++){
								Retorno=RetrieveData.RetrieveBovespa(new Character((char)(f+'A')));		 
								out2=new DataOutputStream(new BufferedOutputStream(new FileOutputStream(
									 "C:\\Program Files\\Apache Group\\Tomcat 4.1\\webapps\\abia\\Downloaded\\ListaEmpresas_" + (new Character(((char)('A'+f))))+".txt")));
								out2.writeBytes(Retorno);
								out2.close();		               	   		           	
						   }
						}catch(Exception e){
						
						}						   
		
						System.out.println("Terminou...");	  
		
						try{
						   theClient=AgentContainer.getWebRetriever().createClient();
						   theClient.RegisterContentHandler("text/html",Class.forName("org.abia.AgentContainer.WebRetriever.HTMLContentHandler"));
						   //theClient.RegisterContentHandler("application/x-zip-compressed",Class.forName("org.abia.AgentContainer.WebRetriever.ZIPContentHandler"));
						   theClient.RegisterContentHandler("application/zip",Class.forName("org.abia.AgentContainer.WebRetriever.ZIPContentHandler"));

						   obj=theClient.getURL("http://" + Utils.TargetServer + "/bencke/bdi0408.zip",null);
				   
						   if(obj instanceof HTMLContentHandler){
							  HTMLContentHandler HTMLHandler;
					  
							  HTMLHandler=(HTMLContentHandler)obj;
					  					  
							  Strings=HTMLHandler.getHeaders();
							  for(f=0;f<Strings.length;f++){
								//System.out.println(Strings[f]);					  
							  }	
							  //System.out.println(HTMLHandler.getContentAsString().substring(23000));
							  out2=new DataOutputStream(new BufferedOutputStream(new FileOutputStream("C:\\teste33.txt")));
							  out2.writeBytes(HTMLHandler.getContentAsString());
							  out2.close();					  
					  
						   } else
						   if(obj instanceof ZIPContentHandler){
							  ZIPContentHandler ZIPHandler;

							  ZIPHandler=(ZIPContentHandler)obj;
							  Strings=ZIPHandler.getHeaders();
							  for(f=0;f<Strings.length;f++){
								System.out.println(Strings[f]);					  
							  }	
							  out2=new DataOutputStream(new BufferedOutputStream(new FileOutputStream("C:\\teste33.zip")));
							  out2.write(ZIPHandler.getContent());
							  out2.close();
					  
							  Utils.Unzip("C:\\teste33.zip");					  
						   }				   
				   				   
						}catch(Exception e){
							System.out.println(e);
						}
		
		
	}
	public static void ClearDatabase(){
		String     ConnectionString;
		Connection PostgreSQLConnection;
		Statement  stmt;
				 
		ConnectionString="jdbc:postgresql://127.0.0.1:5432/abia/";		
		try {
			Class.forName("org.postgresql.Driver");
		}catch(Exception e){
			System.out.println(e);        		
		}
		
		try{
			PostgreSQLConnection=DriverManager.getConnection(ConnectionString,"abia","");
			stmt=PostgreSQLConnection.createStatement();

			try {
				stmt.execute("drop table agentdata");			
			}catch(Exception e){
				System.out.println(e);			
			}		
			try {
				stmt.execute("drop table agents");
			}catch(Exception e){
				System.out.println(e);			
			}		
			try {
				stmt.execute("drop table momentum");
			}catch(Exception e){
				System.out.println(e);			
			}		

			try {
				stmt.execute("drop table bollingerbands");
			}catch(Exception e){
				System.out.println(e);			
			}		

			try {
				stmt.execute("drop table portfolio");
			}catch(Exception e){
				System.out.println(e);			
			}		

			try {
				stmt.execute("drop table estadoportfolio");
			}catch(Exception e){
				System.out.println(e);			
			}		

			try {
				stmt.execute("drop table novoportfolio");
			}catch(Exception e){
				System.out.println(e);			
			}		

			try {
				stmt.execute("drop table novoestadoportfolio");
			}catch(Exception e){
				System.out.println(e);			
			}		


			try {
				stmt.execute("drop table papelemportfolio");
			}catch(Exception e){
				System.out.println(e);			
			}		

			try {
				stmt.execute("drop table novopapelemportfolio");
			}catch(Exception e){
				System.out.println(e);			
			}		


			try {
				stmt.execute("drop table estadopapelemportfolio");
			}catch(Exception e){
				System.out.println(e);			
			}

			try {
				stmt.execute("drop table cotacao");
			}catch(Exception e){
				System.out.println(e);			
			}		

			try {
				stmt.execute("drop table cotacaoindice");
			}catch(Exception e){
				System.out.println(e);			
			}		

			try {
				stmt.execute("drop table empresa");
			}catch(Exception e){
				System.out.println(e);			
			}		

			try {
            	stmt.execute("drop table indice");
			}catch(Exception e){
				System.out.println(e);			
			}		

			try {
				stmt.execute("drop table movingaverage");
			}catch(Exception e){
				System.out.println(e);			
			}		

			try {
				stmt.execute("drop table papel");
			}catch(Exception e){
				System.out.println(e);			
			}		

			try {
				stmt.execute("drop table papelemindice");
			}catch(Exception e){
				System.out.println(e);			
			}		

			try {
				stmt.execute("drop table pregao");
			}catch(Exception e){
				System.out.println(e);			
			}		

			try {
				stmt.execute("drop table rsi");
			}catch(Exception e){
				System.out.println(e);			
			}		

			try {
				stmt.execute("drop sequence seq_agents");
			}catch(Exception e){
				System.out.println(e);			
			}		

			try {
				stmt.execute("drop sequence seq_timestamp");
			}catch(Exception e){
				System.out.println(e);			
			}		

			try {
				stmt.execute("drop table stochasticoscillator");
			}catch(Exception e){
				System.out.println(e);			
			}		

			try {
				stmt.execute("drop table technicalanalisys");
			}catch(Exception e){
				System.out.println(e);			
			}		

			try {
				stmt.execute("drop table technicalsubanalisys");
			}catch(Exception e){
				System.out.println(e);			
			}		

			try {
				stmt.execute("drop table trend");
			}catch(Exception e){
				System.out.println(e);			
			}		

			try {
				stmt.execute("drop table balancete");
			}catch(Exception e){
				System.out.println(e);			
			}		

			try {
				stmt.execute("drop table prediction");
			}catch(Exception e){
				System.out.println(e);			
			}		

			try {
				stmt.execute("drop table contacontabil");
			}catch(Exception e){
				System.out.println(e);			
			}		
			
			try {
				stmt.execute("drop table composicaoindividamento");			
			}catch(Exception e){
				System.out.println(e);			
			}	

			try {
				stmt.execute("drop table fundamentalanalisys");			
			}catch(Exception e){
				System.out.println(e);			
			}	
			try {
				stmt.execute("drop table fundamentalsubanalisys");			
			}catch(Exception e){
				System.out.println(e);			
			}	
			try {
				stmt.execute("drop table girodoativo");			
			}catch(Exception e){
				System.out.println(e);			
			}	
			try {
				stmt.execute("drop table imobilizacaopatrimonioliquido");			
			}catch(Exception e){
				System.out.println(e);			
			}	
			try {
				stmt.execute("drop table liquidezcorrente");			
			}catch(Exception e){
				System.out.println(e);			
			}	
			try {
				stmt.execute("drop table liquidezgeral");			
			}catch(Exception e){
				System.out.println(e);			
			}	
			try {
				stmt.execute("drop table retornosobrevendas");			
			}catch(Exception e){
				System.out.println(e);			
			}
			try {
				stmt.execute("drop table timestamp");			
			}catch(Exception e){
				System.out.println(e);			
			}	

		}catch(Exception e){
			System.out.println(e);			
		}		

	}
	
}