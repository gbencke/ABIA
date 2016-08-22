package org.abia.WebEmulator;

import org.abia.AgentContainer.*;
import org.abia.AgentContainer.WebRetriever.*;
import org.abia.utils.*;

import java.io.*;
import java.net.*;

public class RetrieveData { 

	protected static URL teste;
	protected static InputStream in;
	protected static URLConnection urlCon;
	protected static int f,g;
	protected static byte b[];
	protected static WebRetrieverClient theClient;
	protected static Object             obj;
	protected static String             Strings[];
	protected static DataOutputStream   out2;

    static {
		try{
		   theClient=AgentContainer.getWebRetriever().createClient();
		   theClient.RegisterContentHandler("text/html",Class.forName("org.abia.AgentContainer.WebRetriever.HTMLContentHandler"));    	
		}catch(Exception e){
			
		}
    }

	public static String RetrieveBovespa(Character Letra){
		        String Base="",NomeURL="",TotalURL="";
			
				try{
					Base="http://www.bovespa.com.br/empresas/consbov/ListaEmpresa.asp?LetraInicial="+ Letra +"&pagina=itr_dfp_ian&site=B";							
				
					obj=theClient.getURL(Base,null);				  
					if(obj instanceof HTMLContentHandler){
					   HTMLContentHandler HTMLHandler;
					   String Headers[];
					   int f;
					  
					   HTMLHandler=(HTMLContentHandler)obj;
					   return HTMLHandler.getContentAsString();
					}					  
				}catch(Exception e){
		        	
				}    	
				return null;
		}

	public static void RetrieveCVM(){
		
	}


	public static String RetrieveYahoo(int Dia_Inicial,
									 int Mes_Inicial,
								     int Ano_Inicial,
									 int Dia_Final,
									 int Mes_Final,
									 int Ano_Final,
									 String Simbolo){
			String Base="",NomeURL="",TotalURL="";
			
			try{
				//Base="http://table.finance.yahoo.com/d";
				Base="http://" + Utils.TargetServer + "/abia/Yahoo";
				
				NomeURL+="?a="+(Mes_Inicial-1);
				NomeURL+="&b="+Dia_Inicial;
				NomeURL+="&c="+Ano_Inicial;
				NomeURL+="&d="+(Mes_Final-1);
				NomeURL+="&e="+Dia_Final;
				NomeURL+="&f="+Ano_Final;
				NomeURL+="&g=d&s="+Simbolo+".sa";
				
				TotalURL=Base+NomeURL;
				
			    obj=theClient.getURL(TotalURL,null);				  
			    if(obj instanceof HTMLContentHandler){
				   HTMLContentHandler HTMLHandler;
				   String Headers[];
				   int f;
					  
				   HTMLHandler=(HTMLContentHandler)obj;
/*				   Headers=HTMLHandler.getHeaders();
				   for(f=0;f<Headers.length;f++){
				   	   System.out.println(Headers[f]);				   	
				   }*/
				   return HTMLHandler.getContentAsString();
			    }					  
			}catch(Exception e){
		        	
			}    	
			return null;
	}
	
	public static void Retrieve(){}

    public static void RetrieveBMF(){


      }
} 

				   