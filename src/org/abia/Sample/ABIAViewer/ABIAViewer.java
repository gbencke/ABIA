/* $Id: HelloWorldExample.java,v 1.2 2001/11/29 18:27:25 remm Exp $
 *
 */

package org.abia.Sample.ABIAViewer;

import java.io.*;
import java.net.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;

import org.abia.Blackboard.*;
import org.abia.Agents.BovespaAgent.*;
import org.abia.Agents.CVMAgent.*;
import org.abia.Agents.TechnicalAnalisysAgent.*;
import org.abia.Agents.FundamentalAnalisysAgent.*;
import org.abia.Agents.PortfolioManagerAgent.*;
import org.abia.AgentContainer.AgentContainerClient.*;

class ChartGenerator {

	class ValorXY {
		public int X;
		public int Y;
		public double Valor;
		
		public ValorXY(int newX,int newY,double newValor){
			X=newX;
			Y=newY;
			Valor=newValor;			
		}
	}
	
	protected Vector  items;
	protected String  ChartType;
	protected int     Sequence;
	protected int     NumeroValores;
	protected int     MaxValue;
	protected int     MinValue;
	
	
	
	public ChartGenerator(){
		items=new Vector();
		MaxValue=1000;
		MinValue=0;
	}
	
	public void setChartType(String newChart){
		ChartType=newChart;
	}
	
	public void setSequence(int newSequence){
		Sequence=newSequence;		
	}

	public void setMaxValue(int newMaxValue){
		MaxValue=newMaxValue;		
	}
	
	public void setMinValue(int newMinValue){
		MinValue=newMinValue;		
	}	

	
	public void setNumeroValores(int newNumeroValores){
		NumeroValores=newNumeroValores;
	}
	
	public void addDataToSend(int X,int Y,double Valor){
		ValorXY tempXY;
		tempXY=new ValorXY(X,Y,Valor);
		items.add(tempXY);
	}
	
	public void Execute(){
		String         toSend="",TempStr="",MessageReceived="";
		Iterator       it;
		ValorXY        tempXY;
		Socket         currentSocket;
		BufferedReader in;
		
		toSend+="<Message Type=GenerateChart>\r\n";
		toSend+="<TipoGrafico Tipo=\"" + ChartType + "\">\r\n";
		toSend+="<Sequencia Valor=\"" + Sequence + "\">\r\n";		

		toSend+="<NumeroValores Valor=\""+ NumeroValores + "\">\r\n";
		toSend+="<MaxVal Valor=\"" + MaxValue + "\">\r\n";
		toSend+="<MinVal Valor=\"" + MinValue + "\">\r\n";
		
		it=items.iterator();
		while(it.hasNext()){
			tempXY=(ValorXY)it.next();
			toSend+="<ValoresXY X=\""+ tempXY.X + "\" Y=\"" + tempXY.Y  + "\" Valor=\"" + tempXY.Valor + "\"/>\r\n";			
		}
		toSend+="</Message>\r\n";
		
		try{
			currentSocket=new Socket();
			currentSocket.connect(new InetSocketAddress("200.200.200.98",8000));
			currentSocket.getOutputStream().write(toSend.getBytes());		
			in=new BufferedReader(new InputStreamReader(currentSocket.getInputStream()));
			MessageReceived="";
			TempStr="";
			System.out.println("Iniciando...");
			while(true){
		   		if((TempStr=in.readLine())!=null)
			   		MessageReceived+=TempStr;
				System.out.println(TempStr);			   		
		   		if(MessageReceived.indexOf("</Message>")>-1) break;			
			}
		}catch(Exception e){
			System.out.println(e);			
		}
	}
}

class ServletCache {
	public HashMap Pregao;
	public HashMap Papeis;
	public HashMap Info;
	public HashMap Cotacoes;
	
	public HashMap LastCotacoes;	
	
	public int LatestPregao;
	public int LatestPapel;	 
	
	ServletCache(){
		Pregao=new HashMap();
		Papeis=new HashMap();
		Info=new HashMap();
		Cotacoes=new HashMap();
		LastCotacoes=new HashMap();
		
		LatestPregao=0;
		LatestPapel=0;
	}
	
	public void addPregao(AgentData PregaoToAdd){
		String   TempHashKey="";
		HashMap  TempHashMap;
		if(PregaoToAdd.getTimestamp().longValue()>LatestPregao)
		   LatestPregao=(int)PregaoToAdd.getTimestamp().longValue();
		
		TempHashKey=PregaoToAdd.getTimestamp().longValue()+"";
		System.out.println("Adicionando:"+TempHashKey);
		Pregao.put(TempHashKey,PregaoToAdd);
		
		TempHashMap=new HashMap();
		Cotacoes.put(TempHashKey,TempHashMap);
	}

	public void addPapel(AgentData PapelToAdd){
		String TempHashKey="";
		if(PapelToAdd.getTimestamp().longValue()>LatestPapel)
		   LatestPapel=(int)PapelToAdd.getTimestamp().longValue();
		
		TempHashKey=PapelToAdd.getTimestamp().longValue()+"";
		Papeis.put(TempHashKey,PapelToAdd);	
	}	
}

class Mensagem {
	public static String getMensagemInicial(){
		return "Inicial";		
	}
}

class Parameter {
	
}

class QueryTimestampParameter extends Parameter {
	
}

class QueryParameter extends Parameter {
	
}

class Semaphore {
	protected int Counter=0;
	
	public synchronized void setNext(){
		Counter++; 
	}	
}


public class ABIAViewer extends HttpServlet {
	protected static ClientConnection myConnection;
	protected static ServletCache     myCache;
	protected static int              Counter;
	
	public static int MENU_COTACOES=1;
	public static int MENU_MEDIAMOVEL5=2;
	public static int MENU_MEDIAMOVEL10=3;
	public static int MENU_CANDLESTICK=4;
	public static int MENU_BOLLINGERBANDS=5;
	public static int MENU_RSI=6;
	public static int MENU_PARECER=7;	
	public static int MENU_BALANCETE=8;
	public static int MENU_ANALISEFUNDAMENTAL=9;	

    static {
		myCache=new ServletCache();
		myConnection=null;
		Counter=0;		    	
    }

    public void doGet(HttpServletRequest request,
	             HttpServletResponse response)
		throws IOException, ServletException{	       
	       ABIAViewer.doGet2(request,response);
	}
	
	public static void FillStartOfPage(PrintWriter out){
		out.println("<HTML>");
		out.println("<HEAD>");
		out.println("<STYLE> ");
		out.println("A:hover   {color:blue;text-decoration:none}");
		out.println("A:visited {color:blue;text-decoration:none}");
		out.println("A:link    {color:blue;text-decoration:none;border-width:thin}");
		out.println("A:active  {color:blue;border-width:thin}");		
		out.println("BODY      {font-family:Verdana;font-size:10pt}");		
		out.println("</STYLE>");
		out.println("</HEAD>");
		out.println("<BODY>");		
	}
	
	public static String getDateAsString(Date Target){
		String ret="";
		
		if(Target.getDate()<10){
			ret+="0";
		}
		ret+=Target.getDate();
		ret+="/";
		if(Target.getMonth()<10){
			ret+="0";
		}
		ret+=Target.getMonth();		
		ret+="/";
		ret+=(Target.getYear());		
		return ret;
	}
	
	public static void CriarMenu(PrintWriter out,Papel PapelToGenerate,int Selected){
		Empresa currentEmpresa;
		
		currentEmpresa=(Empresa)PapelToGenerate.getEmpresaEmitente();		
		out.println("<FONT STYLE=\"font-size:14pt\"><B>"+ PapelToGenerate.getCodigoPregao() + " - " + currentEmpresa.getNomeEmpresa() + "</B></FONT>");
		out.println("<HR>");
		out.println("<TABLE BORDER=0 CELLSPACING=0 CELLPADDING=0 >");
		out.println("<TR>");
		out.println("<TD COLSPAN=9><FONT STYLE=\"font-size:11pt\"><B>Análise Técnica</B></FONT><BR></TD>");
		out.println("</TR>");
		out.println("<TR HEIGHT=20>");
		if(Selected!=MENU_COTACOES)
			out.println("<TD ALIGN=RIGHT>[</TD><TD ALIGN=CENTER WIDTH=150><FONT STYLE=\"font-size:9pt\" ><A HREF=\"ABIAViewer?a=" + System.currentTimeMillis() + "&Tipo=Cotacoes&Papel=" + PapelToGenerate.getTimestamp() + "\">Cotações</TD><TD ALIGN=LEFT>]</TD>");
		else
		    out.println("<TD ALIGN=RIGHT>[</TD><TD ALIGN=CENTER WIDTH=150><FONT STYLE=\"font-size:9pt\" ><B>Cotações</TD><TD ALIGN=LEFT>]</TD>");			

		if(Selected!=MENU_MEDIAMOVEL5)			
			out.println("<TD ALIGN=RIGHT>[</TD><TD ALIGN=CENTER WIDTH=150><FONT STYLE=\"font-size:9pt\" ><A HREF=\"ABIAViewer?a=" + System.currentTimeMillis() + "&Tipo=MediaMovel5&Papel=" + PapelToGenerate.getTimestamp() + "\">Media Movel 5 dias</TD><TD ALIGN=LEFT>]</TD>");
		else			
			out.println("<TD ALIGN=RIGHT>[</TD><TD ALIGN=CENTER WIDTH=150><FONT STYLE=\"font-size:9pt\" ><B>Media Movel 5 dias</TD><TD ALIGN=LEFT>]</TD>");
			
			
		if(Selected!=MENU_MEDIAMOVEL10)			
			out.println("<TD ALIGN=RIGHT>[</TD><TD ALIGN=CENTER WIDTH=150><FONT STYLE=\"font-size:9pt\" ><A HREF=\"ABIAViewer?a=" + System.currentTimeMillis() + "&Tipo=MediaMovel10&Papel=" + PapelToGenerate.getTimestamp() + "\">Media Movel 10 dias</TD><TD ALIGN=LEFT>]</TD>");
		else			
			out.println("<TD ALIGN=RIGHT>[</TD><TD ALIGN=CENTER WIDTH=150><FONT STYLE=\"font-size:9pt\" ><B>Media Movel 10 dias</TD><TD ALIGN=LEFT>]</TD>");



		out.println("</TR><TR HEIGHT=20>");
		
		if(Selected!=MENU_CANDLESTICK)		
			out.println("<TD ALIGN=RIGHT>[</TD><TD ALIGN=CENTER WIDTH=150><FONT STYLE=\"font-size:9pt\" ><A HREF=\"ABIAViewer?a=" + System.currentTimeMillis() + "&Tipo=CandleStick&Papel=" + PapelToGenerate.getTimestamp() + "\">CandleStick</TD><TD ALIGN=LEFT>]</TD>");
		else
			out.println("<TD ALIGN=RIGHT>[</TD><TD ALIGN=CENTER WIDTH=150><FONT STYLE=\"font-size:9pt\" ><B>CandleStick</TD><TD ALIGN=LEFT>]</TD>");
		
		if(Selected!=MENU_BOLLINGERBANDS)		
			out.println("<TD ALIGN=RIGHT>[</TD><TD ALIGN=CENTER WIDTH=150><FONT STYLE=\"font-size:9pt\" ><A HREF=\"ABIAViewer?a=" + System.currentTimeMillis() + "&Tipo=BollingerBands&Papel=" + PapelToGenerate.getTimestamp() + "\">Boolinger Bands</TD><TD ALIGN=LEFT>]</TD>");
		else		
			out.println("<TD ALIGN=RIGHT>[</TD><TD ALIGN=CENTER WIDTH=150><FONT STYLE=\"font-size:9pt\" ><B>Boolinger Bands</TD><TD ALIGN=LEFT>]</TD>");
		
		if(Selected!=MENU_RSI)		
			out.println("<TD ALIGN=RIGHT>[</TD><TD ALIGN=CENTER WIDTH=150><FONT STYLE=\"font-size:9pt\" ><A HREF=\"ABIAViewer?a=" + System.currentTimeMillis() + "&Tipo=RSI&Papel=" + PapelToGenerate.getTimestamp() + "\">Ind.Forca Relativa</TD><TD ALIGN=LEFT>]</TD>");
		else
			out.println("<TD ALIGN=RIGHT>[</TD><TD ALIGN=CENTER WIDTH=150><FONT STYLE=\"font-size:9pt\" ><B>Ind.Forca Relativa</TD><TD ALIGN=LEFT>]</TD>");			
				
		out.println("</TR><TR HEIGHT=20>");
		if(Selected!=MENU_PARECER)
			out.println("<TD ALIGN=RIGHT>[</TD><TD ALIGN=CENTER WIDTH=150><FONT STYLE=\"font-size:9pt\" ><A HREF=\"ABIAViewer?a=" + System.currentTimeMillis() + "&Tipo=Parecer&Papel=" + PapelToGenerate.getTimestamp() + "\">Parecer Final</TD><TD ALIGN=LEFT>]</TD>");
		else
		    out.println("<TD ALIGN=RIGHT>[</TD><TD ALIGN=CENTER WIDTH=150><FONT STYLE=\"font-size:9pt\" ><B>Parecer Final</TD><TD ALIGN=LEFT>]</TD>");
			
		out.println("<TD ALIGN=RIGHT>[</TD><TD ALIGN=CENTER WIDTH=150><FONT STYLE=\"font-size:9pt\" ></TD><TD ALIGN=LEFT>]</TD>");
		out.println("<TD ALIGN=RIGHT>[</TD><TD ALIGN=CENTER WIDTH=150><FONT STYLE=\"font-size:9pt\" ></TD><TD ALIGN=LEFT>]</TD>");
		
		out.println("</TR>");
		out.println("<TR HEIGHT=20><TD></TD></TR>");
		out.println("<TR>");
		out.println("   <TD COLSPAN=9><FONT STYLE=\"font-size:11pt\"><B>Análise Fundamentalista</B></FONT></TD>");
		out.println("</TR>");
		if(Selected!=MENU_BALANCETE)		
		   out.println("<TD ALIGN=RIGHT>[</TD><TD ALIGN=CENTER WIDTH=150><FONT STYLE=\"font-size:9pt\" ><A HREF=\"ABIAViewer?a=" + System.currentTimeMillis() + "&Tipo=Balancetes&Papel=" + PapelToGenerate.getTimestamp() + "\">Balancetes <BR> e Indicadores Fund.</TD><TD ALIGN=LEFT>]</TD>");		
		else
		   out.println("<TD ALIGN=RIGHT>[</TD><TD ALIGN=CENTER WIDTH=150><FONT STYLE=\"font-size:9pt\" >Balancetes <BR> e Indicadores Fund.</TD><TD ALIGN=LEFT>]</TD>");
		
		if(Selected!=MENU_ANALISEFUNDAMENTAL)		
		   out.println("<TD ALIGN=RIGHT>[</TD><TD ALIGN=CENTER WIDTH=150><FONT STYLE=\"font-size:9pt\" ><A HREF=\"ABIAViewer?a=" + System.currentTimeMillis() + "&Tipo=Analises&Papel=" + PapelToGenerate.getTimestamp() + "\">Analises do Agente</TD><TD ALIGN=LEFT>]</TD>");		
		else
		   out.println("<TD ALIGN=RIGHT>[</TD><TD ALIGN=CENTER WIDTH=150><FONT STYLE=\"font-size:9pt\" >Analises do Agente</TD><TD ALIGN=LEFT>]</TD>");		
		out.println("</TABLE><HR>");
	}

	public static synchronized void doGet2(HttpServletRequest request,
					  HttpServletResponse response)
		throws IOException, ServletException
	{
		int              f,g;
		String           Temp="",TempKey="";
		int              LastValue;
		int              LastTimestamp;
		String           TipoPagina="";
		Empresa          currentEmpresa;
		Papel            currentPapel;
		Pregao           currentPregao;
		Cotacao          currentCotacao;
		BollingerBands   currentBollinger;
		Iterator         it;
		AgentData        Papeis[],Pregoes[],Cotacoes[];
		Vector           TempVector,TempCotacoes;
		FilterCondition  Filters[];
		ChartGenerator   myChartGenerator;

        Counter++;
		response.setContentType("text/html");
		response.setBufferSize(200000);
		PrintWriter out = response.getWriter();
				
		try{
		if(myConnection==null) 
		   myConnection=AgentContainerClient.createClientConnection("200.200.200.33",7200);
		}catch(Exception e){
		   System.out.println(e);			   		
		}
		
		
		
		if(request.getParameter("Tipo")==null)
		   TipoPagina="";
		   else
		   TipoPagina=request.getParameter("Tipo");
		

		
		if(TipoPagina.equals("")){
			out.println("<HTML>");
			out.println("<FRAMESET COLS=\"20%,*\">");
			out.println("    <FRAME NORESIZE FRAMEBORDER=0 NAME=\"Menu\" SRC=\"ABIAViewer?a=" + System.currentTimeMillis() + "&Tipo=MenuLateral\">");
			out.println("    <FRAME NORESIZE FRAMEBORDER=0 NAME=\"Lateral\" SRC=\"ABIAViewer?a=" + System.currentTimeMillis() + "&Tipo=Inicial\">");    
			out.println("</FRAMESET>"); 
			out.println("</HTML>");
			return;
		}
		
		FillStartOfPage(out);
		if(TipoPagina.equals("MenuLateral")){
						
			try{
			   LastValue=myCache.LatestPapel;
			   while(true){
				     Papeis=myConnection.QueryTimestamp(LastValue,ClientConnection.TIMESTAMP_GREATER_THAN,Papel.class.getName(),10);
				     if(Papeis.length==0) break;
				     for(f=0;f<Papeis.length;f++){
				     	 currentPapel=(Papel)Papeis[f];
						 currentPapel.ResolveReferences();
				     	 LastValue=(int)currentPapel.getTimestamp().longValue();
				     	 TempKey=""+currentPapel.getTimestamp().longValue()+"";
				     	 myCache.addPapel(currentPapel);
				     }
			   }			   			  
			   
			   Papeis=new AgentData[myCache.Papeis.values().size()];
			   it=myCache.Papeis.values().iterator();
			   f=0;
			   while(it.hasNext()){
				   Papeis[f]=(AgentData)it.next();  
				   f++;			   	
			   }		   
			   
			   out.println("<FONT FACE=\"Verdana\" SIZE=\"2pt\">Escolha Visao:<BR>");
			   out.println("<A HREF=\"ABIAViewer?a=" + System.currentTimeMillis() + "&Tipo=Portfolio\"  TARGET=Lateral><FONT FACE=\"Verdana\" SIZE=\"2pt\">Portfolio</A><BR><BR><BR>");
			   out.println("<FONT FACE=\"Verdana\" SIZE=\"2pt\">Papeis<BR>");
			   out.println("<TABLE BORDER=0 CELLPADDING=0>");
			   for(f=0;f<Papeis.length;f++){
				   if((f % 3)==0){
				   	   if(f>0)
				   	   	  out.println("</TR>");
				   	   out.println("<TR>");
				   }
				   out.println("<TD ALIGN=CENTER>");				   
				   currentPapel=(Papel)Papeis[f];
				   Temp="<A HREF=\"ABIAViewer?a=" + System.currentTimeMillis() + "&Tipo=Papel&Papel="+currentPapel.getTimestamp()+"\" TARGET=Lateral>";
				   Temp+="<FONT FACE=\"Verdana\" SIZE=\"1pt\">"+currentPapel.getCodigoPregao()+"</FONT>";
				   Temp+="</A><BR>";
				   
				   out.println(Temp);
				   out.println("</TD>");				   
				   out.flush();
			   }
			   out.println("</TR></TABLE></CENTER><BR><BR><BR>");
			   out.println("<A HREF=\"ABIAViewer?Tipo=Explicacao\">");
			   out.println("<FONT FACE=\"Verdana\" SIZE=\"2pt\">Explicacao</FONT>");
			   out.println("</A><BR>");			   
			   out.println("</BODY>");			
			   out.println("</HTML>");		   
			}catch(Exception e){
				out.println(e);
				e.printStackTrace(out);    	   	
			}
			return;
		}
		
		try{
		   LastValue=myCache.LatestPregao;
		   while(true){
				 Pregoes=myConnection.QueryTimestamp(LastValue,ClientConnection.TIMESTAMP_GREATER_THAN,Pregao.class.getName(),10);
				 if(Pregoes.length==0) break;
				 for(f=0;f<Pregoes.length;f++){
					 currentPregao=(Pregao)Pregoes[f];
					 currentPregao.ResolveReferences();					
					 LastValue=(int)currentPregao.getTimestamp().longValue();
					 TempKey=""+currentPregao.getTimestamp().longValue()+"";
					 myCache.addPregao(currentPregao);
				 }
		   }
		}catch(Exception e){
			out.println(e);
			return;			   			  
		}

		if(TipoPagina.equals("MediaMovel5")){			
			Object    Ret;
			int       MinValue=11000,MaxValue=0;
			RSI       currentRSI=null;
			String    currentDate;
			MovingAverage currentMA5;
			
			Cotacoes=null;
			currentPapel=(Papel)myCache.Papeis.get(request.getParameter("Papel"));
			CriarMenu(out,currentPapel,MENU_MEDIAMOVEL5);			
			Ret=myCache.LastCotacoes.get(request.getParameter("Papel"));
			if(Ret==null){
				LastValue=0;
			}else{
				LastValue=((Integer)Ret).intValue();
			}
			  
			try{
				Filters=new FilterCondition[1];
				Filters[0]=new FilterCondition();								
				Filters[0].FieldName="papel";
				Filters[0].Operator=FilterCondition.EQUALS;
				Filters[0].ValueName=((Papel)myCache.Papeis.get(request.getParameter("Papel"))).getTimestamp();
				Cotacoes=myConnection.Query(Class.forName("org.abia.Agents.TechnicalAnalisysAgent.MovingAverage"),Filters);
			}catch(Exception e){
			  out.println(e);
			  e.printStackTrace();					
			}
			
			out.println("<CENTER><IMG SRC=\"/abia/Images/Img"+Counter+".gif?a=" + System.currentTimeMillis() +  "\">");
			out.println("<TABLE BORDER=1 BORDERCOLOR=000000 CELLPADDING=0 CELLSPACING=0 WIDTH=100%>");
			out.println("<TR BORDER=1><TD><FONT FACE=VERDANA SIZE=3><B>Data</TD><TD><FONT FACE=VERDANA SIZE=3><B>Moving Average</TD></TR>");
		    
			g=2;
			myChartGenerator=new ChartGenerator();
			myChartGenerator.setChartType("MA5");
			myChartGenerator.setSequence(Counter);
			
			TempCotacoes=new Vector();
			for(f=0;f<Cotacoes.length;f++){
					currentMA5=(MovingAverage)Cotacoes[f];
					if(currentMA5.getValor().doubleValue()>MaxValue)
					   MaxValue=currentMA5.getValor().intValue();
					if(currentMA5.getValor().doubleValue()<MinValue)
					   MinValue=currentMA5.getValor().intValue();
			}
			myChartGenerator.setMaxValue(MaxValue);
			myChartGenerator.setMinValue(MinValue);

			for(f=0;f<Cotacoes.length;f++){
					currentMA5=(MovingAverage)Cotacoes[f];
					out.println("<TR>");
				    currentMA5.ResolveReferences();

					out.println("<TD STYLE=\"{font-size:11}\"><FONT FACE=VERDANA SIZE=2>");
					out.println(getDateAsString(((Pregao)currentMA5.getPregao()).getData()));
					out.println("</TD>");

					out.println("<TD ALIGN=RIGHT STYLE=\"{font-size:11}\"><FONT FACE=VERDANA SIZE=2>");
					out.println(currentMA5.getValor());
					out.println("</TD>");

					out.println("</TR>");
					myChartGenerator.addDataToSend(1,g,0);
					myChartGenerator.addDataToSend(2,g,currentMA5.getValor().doubleValue());
					g++;										
			}
			myChartGenerator.setNumeroValores(g-2);
			myChartGenerator.Execute();
			out.println("</TABLE></CENTER>");
			return;		
		}
		if(TipoPagina.equals("MediaMovel10")){
			currentPapel=(Papel)myCache.Papeis.get(request.getParameter("Papel"));
			CriarMenu(out,currentPapel,MENU_MEDIAMOVEL10);			
			return;
		}
		if(TipoPagina.equals("CandleStick")){			
			Object    Ret;
			int       MinValue=11000,MaxValue=0;
			
			currentPapel=(Papel)myCache.Papeis.get(request.getParameter("Papel"));
			CriarMenu(out,currentPapel,MENU_CANDLESTICK);			
			Ret=myCache.LastCotacoes.get(request.getParameter("Papel"));
			if(Ret==null){
				LastValue=0;
			}else{
				LastValue=((Integer)Ret).intValue();
			}
			  
			it=myCache.Pregao.values().iterator();
			while(it.hasNext()){
				currentPregao=(Pregao)it.next();
				TempKey=request.getParameter("Papel")+"_"+currentPregao.getTimestamp();
				Ret=myCache.Cotacoes.get(TempKey);
				if(Ret==null){
					try{
						Filters=new FilterCondition[2];
						Filters[0]=new FilterCondition();
						Filters[0].FieldName="pregao";
						Filters[0].Operator=FilterCondition.EQUALS;
						Filters[0].ValueName=currentPregao.getTimestamp();								
						Filters[1]=new FilterCondition();
						Filters[1].FieldName="papel";
						Filters[1].Operator=FilterCondition.EQUALS;
						Filters[1].ValueName=((Papel)myCache.Papeis.get(request.getParameter("Papel"))).getTimestamp();
						Cotacoes=myConnection.Query(Class.forName("org.abia.Agents.BovespaAgent.Cotacao"),Filters);
						for(f=0;f<Cotacoes.length;f++){
							Cotacoes[f].ResolveReferences();
							TempKey=((Cotacao)Cotacoes[f]).getPapel().getTimestamp()+"_"+((Cotacao)Cotacoes[f]).getPregao().getTimestamp();
							myCache.Cotacoes.put(TempKey,Cotacoes[f]);
						}
					}catch(Exception e){
						out.println(e);
						e.printStackTrace();					
					}					
				}
			}
			
			out.println("<CENTER><IMG SRC=\"/abia/Images/Img"+Counter+".gif?a=" + System.currentTimeMillis() +  "\">");
			out.println("<TABLE BORDER=1 BORDERCOLOR=000000 CELLPADDING=0 CELLSPACING=0 WIDTH=100%>");
			out.println("<TR BORDER=1><TD ALIGN=CENTER><FONT FACE=VERDANA SIZE=3><B>Data</TD><TD ALIGN=CENTER><FONT FACE=VERDANA SIZE=3><B>Abertura</TD><TD ALIGN=CENTER><FONT FACE=VERDANA SIZE=3><B>Minima</TD><TD ALIGN=CENTER><B><FONT FACE=VERDANA SIZE=3>Maximo</TD><TD ALIGN=CENTER><FONT FACE=VERDANA SIZE=3><B>Fechamento</TD>");
		    
			g=2;
			myChartGenerator=new ChartGenerator();
			myChartGenerator.setChartType("Candlestick");
			myChartGenerator.setSequence(Counter);
			
			TempCotacoes=new Vector();
			for(f=0;f<myCache.LatestPregao+1;f++){
				TempKey=request.getParameter("Papel")+"_"+f;
				Ret=myCache.Cotacoes.get(TempKey);
				if(Ret!=null){
					currentCotacao=(Cotacao)Ret;
					currentPregao=(Pregao)myCache.Pregao.get(currentCotacao.getPregao().getTimestamp().longValue()+"");
					if(currentPregao==null) continue;
					if(currentCotacao.getFechamento().doubleValue()>MaxValue)
					   MaxValue=currentCotacao.getFechamento().intValue();
					if(currentCotacao.getFechamento().doubleValue()<MinValue)
					   MinValue=currentCotacao.getFechamento().intValue();
				}
			}
			myChartGenerator.setMaxValue(MaxValue);
			myChartGenerator.setMinValue(MinValue);

			for(f=0;f<myCache.LatestPregao+1;f++){
				TempKey=request.getParameter("Papel")+"_"+f;
				Ret=myCache.Cotacoes.get(TempKey);
				if(Ret!=null){
					currentCotacao=(Cotacao)Ret;
					currentPregao=(Pregao)myCache.Pregao.get(currentCotacao.getPregao().getTimestamp().longValue()+"");
					if(currentPregao==null) continue;
					
					TempCotacoes.add(currentCotacao);
					out.println("<TR>");


				    currentCotacao.ResolveReferences();
					out.println("<TD STYLE=\"{font-size:11}\"><FONT FACE=VERDANA SIZE=2>");
					out.println(getDateAsString(((Pregao)currentCotacao.getPregao()).getData()));
					out.println("</TD>");

					out.println("<TD ALIGN=RIGHT  STYLE=\"{font-size:11}\"><FONT FACE=VERDANA SIZE=2>");
					out.println(currentCotacao.getAbertura());
					out.println("</TD>");

					out.println("<TD ALIGN=RIGHT  STYLE=\"{font-size:11}\"><FONT FACE=VERDANA SIZE=2>");
					out.println(currentCotacao.getMaximo());
					out.println("</TD>");

					out.println("<TD ALIGN=RIGHT  STYLE=\"{font-size:11}\"><FONT FACE=VERDANA SIZE=2>");
					out.println(currentCotacao.getMinima());
					out.println("</TD>");

					out.println("<TD ALIGN=RIGHT STYLE=\"{font-size:11}\"><FONT FACE=VERDANA SIZE=2>");
					out.println(currentCotacao.getFechamento());
					out.println("</TD>");

					out.println("</TR>");
					myChartGenerator.addDataToSend(1,g,currentCotacao.getAbertura().doubleValue());
					myChartGenerator.addDataToSend(2,g,currentCotacao.getMaximo().doubleValue());
					myChartGenerator.addDataToSend(3,g,currentCotacao.getMinima().doubleValue());
					myChartGenerator.addDataToSend(4,g,currentCotacao.getFechamento().doubleValue());
					g++;										
				}
			}
			myChartGenerator.setNumeroValores(g-2);
			myChartGenerator.Execute();
			out.println("</TABLE></CENTER>");
			return;			
		}
		if(TipoPagina.equals("BollingerBands")){
			Object    Ret;
			int       MinValue=11000,MaxValue=0;
			
			Cotacoes=null;
			currentPapel=(Papel)myCache.Papeis.get(request.getParameter("Papel"));
			CriarMenu(out,currentPapel,MENU_BOLLINGERBANDS);			
			Ret=myCache.LastCotacoes.get(request.getParameter("Papel"));
			if(Ret==null){
				LastValue=0;
			}else{
				LastValue=((Integer)Ret).intValue();
			}
			  
			try{
				Filters=new FilterCondition[1];
				Filters[0]=new FilterCondition();								
				Filters[0].FieldName="papel";
				Filters[0].Operator=FilterCondition.EQUALS;
				Filters[0].ValueName=((Papel)myCache.Papeis.get(request.getParameter("Papel"))).getTimestamp();
				Cotacoes=myConnection.Query(Class.forName("org.abia.Agents.TechnicalAnalisysAgent.BollingerBands"),Filters);
			}catch(Exception e){
			  out.println(e);
			  e.printStackTrace();					
			}
			
			out.println("<CENTER><IMG SRC=\"/abia/Images/Img"+Counter+".gif?a=" + System.currentTimeMillis() +  "\">");
			out.println("<TABLE BORDER=1 BORDERCOLOR=000000 CELLPADDING=0 CELLSPACING=0 WIDTH=100%>");
			out.println("<TR BORDER=1><TD><FONT FACE=VERDANA SIZE=3>Data</TD><TD><FONT FACE=VERDANA SIZE=3>Lower Band</TD><TD><FONT FACE=VERDANA SIZE=3>Middle Band</TD><TD><FONT FACE=VERDANA SIZE=3>Upper Band</TD></TR>");
		    
			g=2;
			myChartGenerator=new ChartGenerator();
			myChartGenerator.setChartType("Bollinger");
			myChartGenerator.setSequence(Counter);
			
			TempCotacoes=new Vector();
			for(f=0;f<Cotacoes.length;f++){
					currentBollinger=(BollingerBands)Cotacoes[f];
					if(currentBollinger.getupperBand().doubleValue()>MaxValue)
					   MaxValue=currentBollinger.getupperBand().intValue();
					if(currentBollinger.getlowerBand().doubleValue()<MinValue)
					   MinValue=currentBollinger.getlowerBand().intValue();
			}
			myChartGenerator.setMaxValue(MaxValue);
			myChartGenerator.setMinValue(MinValue);

			for(f=0;f<Cotacoes.length;f++){
				    currentBollinger=(BollingerBands)Cotacoes[f];
				    currentBollinger.ResolveReferences();
					out.println("<TR>");

				    out.println("<TD STYLE=\"{font-size:11}\"><FONT FACE=VERDANA SIZE=2>");
				    out.println(getDateAsString(((Pregao)currentBollinger.getPregao()).getData()));
				    out.println("</TD>");
					
					out.println("<TD STYLE=\"{font-size:11}\"><FONT FACE=VERDANA SIZE=2>");
					out.println(currentBollinger.getlowerBand());
					out.println("</TD>");

					out.println("<TD STYLE=\"{font-size:11}\"><FONT FACE=VERDANA SIZE=2>");
					out.println(currentBollinger.getmiddleBand());
					out.println("</TD>");

					out.println("<TD STYLE=\"{font-size:11}\"><FONT FACE=VERDANA SIZE=2>");
					out.println(currentBollinger.getupperBand());
					out.println("</TD>");

					out.println("</TR>");
					myChartGenerator.addDataToSend(2,g,currentBollinger.getlowerBand().doubleValue());
					myChartGenerator.addDataToSend(3,g,currentBollinger.getmiddleBand().doubleValue());
					myChartGenerator.addDataToSend(4,g,currentBollinger.getupperBand().doubleValue());
					g++;										
			}
			myChartGenerator.setNumeroValores(g-2);
			myChartGenerator.Execute();
			out.println("</TABLE></CENTER>");
			return;					
		}
		if(TipoPagina.equals("Portfolio")){
			AgentData Portfolios[]=null;
			AgentData PapelEmPortfolios[]=null;
			
			try{
				Filters=new FilterCondition[0];
/*				Filters[0]=new FilterCondition();								
				Filters[0].FieldName="pregao";
				Filters[0].Operator=FilterCondition.GREATER;
				Filters[0].ValueName=new Long(0);*/
				Portfolios=myConnection.Query(Class.forName("org.abia.Agents.PortfolioManagerAgent.Portfolio"),Filters);
			}catch(Exception e){
			  out.println(e);
			  e.printStackTrace();					
			}
			
			out.println("<FONT FACE=VERDANA SIZE=4>Composição do Portfolio por Pregão:<BR><BR><TABLE BORDER=1 BORDERCOLOR=000000 CELLPADDING=0 CELLSPACING=0 WIDTH=100%>");
			
			out.println("<TR>");
			out.println("<TD><FONT FACE=VERDANA SIZE=2><B>");
			out.println("Data");
			out.println("</TD>");	   	
			it=myCache.Papeis.values().iterator();
			while(it.hasNext()){
				currentPapel=(Papel)it.next();  
				out.println("<TD><FONT FACE=VERDANA SIZE=2><B>");
				out.println(currentPapel.getCodigoPregao());
				out.println("</TD>");	   	
			}	
			out.println("</TR>");	   
			out.flush();
			
			for(f=0;f<Portfolios.length;f++){
				out.println("<TR>");
				try{
					Filters=new FilterCondition[1];
					Filters[0]=new FilterCondition();								
					Filters[0].FieldName="portfolio";
					Filters[0].Operator=FilterCondition.EQUALS;
					Filters[0].ValueName=Portfolios[f].getTimestamp();
					PapelEmPortfolios=myConnection.Query(Class.forName("org.abia.Agents.PortfolioManagerAgent.PapelEmPortfolio"),Filters);
				}catch(Exception e){
				  out.println(e);
				  e.printStackTrace();					
				}
				Portfolios[f].ResolveReferences();
				out.println("<TD ALIGN=LEFT><FONT FACE=VERDANA SIZE=2>");
				out.println(getDateAsString(((Pregao)(((Portfolio)Portfolios[f]).getPregao())).getData()));
				out.println("</TD>");				
				for(g=0;g<PapelEmPortfolios.length;g++){
					out.println("<TD ALIGN=RIGHT><FONT FACE=VERDANA SIZE=2>");
					out.println(((PapelEmPortfolio)PapelEmPortfolios[g]).getQuantidade());
					out.println("%</TD>");
				}				
				out.println("</TR>");
				out.flush();				
			}
			out.println("</TABLE>");
		    return;	
		}
		if(TipoPagina.equals("RSI")){
			Object    Ret;
			int       MinValue=11000,MaxValue=0;
			RSI       currentRSI=null;
			String    currentDate;
			
			Cotacoes=null;
			currentPapel=(Papel)myCache.Papeis.get(request.getParameter("Papel"));
			CriarMenu(out,currentPapel,MENU_RSI);			
			Ret=myCache.LastCotacoes.get(request.getParameter("Papel"));
			if(Ret==null){
				LastValue=0;
			}else{
				LastValue=((Integer)Ret).intValue();
			}
			  
			try{
				Filters=new FilterCondition[1];
				Filters[0]=new FilterCondition();								
				Filters[0].FieldName="papel";
				Filters[0].Operator=FilterCondition.EQUALS;
				Filters[0].ValueName=((Papel)myCache.Papeis.get(request.getParameter("Papel"))).getTimestamp();
				Cotacoes=myConnection.Query(Class.forName("org.abia.Agents.TechnicalAnalisysAgent.RSI"),Filters);
			}catch(Exception e){
			  out.println(e);
			  e.printStackTrace();					
			}
			
			out.println("<CENTER><IMG SRC=\"/abia/Images/Img"+Counter+".gif?a=" + System.currentTimeMillis() +  "\">");
			out.println("<TABLE WIDTH=100% BORDER=0 CELLPADDING=0 CELLSPACING=0> ");
			out.println("<TR BORDER=1><TD STYLE=\"{font-size:15}\"><FONT FACE=VERDANA COLOR=#0000FF>Data</TD><TD STYLE=\"{font-size:11}\"><FONT FACE=VERDANA COLOR=#0000FF>RSI</TD></TR>");
		    
			g=2;
			myChartGenerator=new ChartGenerator();
			myChartGenerator.setChartType("RSI");
			myChartGenerator.setSequence(Counter);
			
			TempCotacoes=new Vector();
			for(f=0;f<Cotacoes.length;f++){
					currentRSI=(RSI)Cotacoes[f];
					if(currentRSI.getValor().doubleValue()>MaxValue)
					   MaxValue=currentRSI.getValor().intValue();
					if(currentRSI.getValor().doubleValue()<MinValue)
					   MinValue=currentRSI.getValor().intValue();
			}
			myChartGenerator.setMaxValue(MaxValue);
			myChartGenerator.setMinValue(MinValue);

			for(f=0;f<Cotacoes.length;f++){
				    currentRSI=(RSI)Cotacoes[f];
				    currentRSI.ResolveReferences();
				    //currentDate=(getDateAsString((Pregao)currentRSI.getPregao()).getData().toString());
					out.println("<TR>");

					out.println("<TD STYLE=\"{font-size:11}\"><FONT FACE=VERDANA SIZE=2>");
					out.println(getDateAsString(((Pregao)currentRSI.getPregao()).getData()));
					out.println("</TD>");

					out.println("<TD STYLE=\"{font-size:11}\"><FONT FACE=VERDANA SIZE=2>");
					out.println(currentRSI.getValor());
					out.println("</TD>");

					out.println("</TR>");
					myChartGenerator.addDataToSend(1,g,0);
					myChartGenerator.addDataToSend(2,g,currentRSI.getValor().doubleValue());
					g++;										
			}
			myChartGenerator.setNumeroValores(g-2);
			myChartGenerator.Execute();
			out.println("</TABLE></CENTER>");
			return;		
		}
		if(TipoPagina.equals("Parecer")){
			AgentData TechnicalAnal[],TechnicalSubAnal[];
			TechnicalAnalisys currentTechnical;
			TechnicalSubAnalisys currentSubTechnical;
			
			currentPapel=(Papel)myCache.Papeis.get(request.getParameter("Papel"));			
			currentPapel.ResolveReferences();
			CriarMenu(out,currentPapel,MENU_PARECER);
			
			try{
				Filters=new FilterCondition[1];
				Filters[0]=new FilterCondition();
				Filters[0].FieldName="papel";
				Filters[0].Operator=FilterCondition.EQUALS;
				Filters[0].ValueName=currentPapel.getTimestamp();								
				TechnicalAnal=myConnection.Query(Class.forName("org.abia.Agents.TechnicalAnalisysAgent.TechnicalAnalisys"),Filters);
			}catch(Exception e){
				out.println(e);
				return;			
			}
			out.println("<TABLE BORDER=1 BORDERCOLOR=000000 CELLPADDING=0 CELLSPACING=0 WIDTH=100%> <TR><TD ALIGN=CENTER><FONT FACE=VERDANA SIZE=3><B>Data</TD><TD><FONT FACE=VERDANA SIZE=3><B>Descricao</TD><TD ALIGN=CENTER><B><FONT FACE=VERDANA SIZE=3>SubAnalise</TD> <TD ALIGN=CENTER><B><FONT FACE=VERDANA SIZE=3>Valor</TD></TR>");
			for(f=0;f<TechnicalAnal.length;f++){				
				try{					
					currentTechnical=(TechnicalAnalisys)TechnicalAnal[f];
					currentTechnical.ResolveReferences();
					Filters=new FilterCondition[2];
					Filters[0]=new FilterCondition();
					Filters[0].FieldName="pregao";
					Filters[0].Operator=FilterCondition.EQUALS;
					Filters[0].ValueName=currentTechnical.getPregao().getTimestamp();								
					Filters[1]=new FilterCondition();
					Filters[1].FieldName="papel";
					Filters[1].Operator=FilterCondition.EQUALS;
					Filters[1].ValueName=currentTechnical.getPapel().getTimestamp();								
					TechnicalSubAnal=myConnection.Query(Class.forName("org.abia.Agents.TechnicalAnalisysAgent.TechnicalSubAnalisys"),Filters);
					out.println("<TR><TD ALIGN=CENTER><FONT SIZE=2>"+getDateAsString(((Pregao)currentTechnical.getPregao()).getData())+"</TD><TD ALIGN=LEFT><FONT FACE=Verdana SIZE=2 COLOR=#000000>"+currentTechnical.getDescricao()+"</TD><TD ALIGN=LEFT><FONT FACE=Verdana SIZE=2 COLOR=#000000>" );
					for(g=0;g<TechnicalSubAnal.length;g++){
						currentSubTechnical=(TechnicalSubAnalisys)TechnicalSubAnal[g];
						currentSubTechnical.ResolveReferences();
						out.println("<LI><B>"+currentSubTechnical.getTipo()+":</B>" +currentSubTechnical.getDescricao());
					}
					out.println("</TD><TD>"+currentTechnical.getValor().intValue());
					out.println("</TD></TR>");
					out.flush();				
				}catch(Exception e){
					out.println(e);
					e.printStackTrace();
					return;					
				}
			}
			out.println("</TABLE><BR><BR><BR>");
			return;	
		}

		
		if(TipoPagina.equals("Papel")){			
			currentPapel=(Papel)myCache.Papeis.get(request.getParameter("Papel"));
			CriarMenu(out,currentPapel,0);			
			return;
		}
		
		if(TipoPagina.equals("Balancetes")){			
			AgentData Balancetes[],ContasContabeis[];
			Balancete currentBalancete;
			ContaContabil currentContaContabil;
			
			currentPapel=(Papel)myCache.Papeis.get(request.getParameter("Papel"));			
			CriarMenu(out,currentPapel,MENU_BALANCETE);
			
			Balancetes=new AgentData[0];			
			ContasContabeis=new AgentData[0];

			try{
				Filters=new FilterCondition[1];
				Filters[0]=new FilterCondition();
				Filters[0].FieldName="empresaemitente";
				Filters[0].Operator=FilterCondition.EQUALS;
				Filters[0].ValueName=currentPapel.getEmpresaEmitente().getTimestamp();								
				Balancetes=myConnection.Query(Class.forName("org.abia.Agents.CVMAgent.Balancete"),Filters);
			}catch(Exception e){
				out.println(e);
				return;			
			}            
            for(f=0;f<Balancetes.length;f++){				
				try{					
					currentBalancete=(Balancete)Balancetes[f];
					currentBalancete.ResolveReferences();
					Filters=new FilterCondition[1];
					Filters[0]=new FilterCondition();
					Filters[0].FieldName="balancete";
					Filters[0].Operator=FilterCondition.EQUALS;
					Filters[0].ValueName=currentBalancete.getTimestamp();								
					ContasContabeis=myConnection.Query(Class.forName("org.abia.Agents.CVMAgent.ContaContabil"),Filters);
					out.println("<FONT FACE=VERDANA SIZE=4>Balancete para "+(((Empresa)currentBalancete.getEmpresaEmitente()).getNomeEmpresa())+" em "+currentBalancete.getStrDataBalancete());
					out.println("<TABLE BORDER=1 BORDERCOLOR=000000 CELLPADDING=0 CELLSPACING=0 WIDTH=100%> <TR><TD><FONT FACE=VERDANA SIZE=3><B>Codigo da Conta</TD>  <TD ALIGN=CENTER><B><FONT FACE=VERDANA SIZE=3>Descrição</TD> <TD ALIGN=CENTER><B><FONT FACE=VERDANA SIZE=3>Valor</TD></TR>");
					for(g=0;g<ContasContabeis.length;g++){
						out.println("<TR>");
						currentContaContabil=(ContaContabil)ContasContabeis[g];
						currentContaContabil.ResolveReferences();
						out.println("<TD ALIGN=LEFT><FONT FACE=Verdana SIZE=2 COLOR=#000000>");
						out.println(currentContaContabil.getCodigoConta());
						out.println("</TD><TD ALIGN=LEFT><FONT FACE=Verdana SIZE=2 COLOR=#000000>");
						out.println(currentContaContabil.getDescricaoConta());
						out.println("</TD><TD  ALIGN=RIGHT><FONT FACE=Verdana SIZE=2 COLOR=#000000>");
						out.println(currentContaContabil.getValor());
						out.println("</TD>");
						out.println("</TR>");
						out.flush();						
					}
					out.println("</TABLE><BR><BR><BR>");
					out.flush();				
				}catch(Exception e){
					out.println(e);
					e.printStackTrace();
					return;					
				}
            }
            return;	
	    }
		 
		if(TipoPagina.equals("Analises")){			
			AgentData FundamentalAnal[],FundamentalSubAnal[];
			FundamentalAnalisys currentFundamental;
			FundamentalSubAnalisys currentSubFundamental;
			
			currentPapel=(Papel)myCache.Papeis.get(request.getParameter("Papel"));			
			CriarMenu(out,currentPapel,MENU_ANALISEFUNDAMENTAL);
			

			currentPapel.ResolveReferences();
			try{
				Filters=new FilterCondition[1];
				Filters[0]=new FilterCondition();
				Filters[0].FieldName="empresa";
				Filters[0].Operator=FilterCondition.EQUALS;
				Filters[0].ValueName=currentPapel.getEmpresaEmitente().getTimestamp();								
				FundamentalAnal=myConnection.Query(Class.forName("org.abia.Agents.FundamentalAnalisysAgent.FundamentalAnalisys"),Filters);
			}catch(Exception e){
				out.println(e);
				return;			
			}
			out.println("<TABLE BORDER=1 BORDERCOLOR=000000 CELLPADDING=0 CELLSPACING=0 WIDTH=100%> <TR><TD><FONT FACE=VERDANA SIZE=3><B>Data</TD><TD><FONT FACE=VERDANA SIZE=3><B>Descricao</TD><TD ALIGN=CENTER><B><FONT FACE=VERDANA SIZE=3>SubAnalise</TD> <TD ALIGN=CENTER><B><FONT FACE=VERDANA SIZE=3>Valor</TD></TR>");
			for(f=0;f<FundamentalAnal.length;f++){				
				try{					
					currentFundamental=(FundamentalAnalisys)FundamentalAnal[f];
					currentFundamental.ResolveReferences();
					Filters=new FilterCondition[1];
					Filters[0]=new FilterCondition();
					Filters[0].FieldName="balancete";
					Filters[0].Operator=FilterCondition.EQUALS;
					Filters[0].ValueName=currentFundamental.getBalancete().getTimestamp();								
					FundamentalSubAnal=myConnection.Query(Class.forName("org.abia.Agents.FundamentalAnalisysAgent.FundamentalSubAnalisys"),Filters);
					out.println("<TR><TD ALIGN=CENTER>"+((Balancete)currentFundamental.getBalancete()).getStrDataBalancete()+"</TD><TD ALIGN=LEFT><FONT FACE=Verdana SIZE=2 COLOR=#000000>"+currentFundamental.getDescricao()+"</TD><TD ALIGN=LEFT><FONT FACE=Verdana SIZE=2 COLOR=#000000>" );
					for(g=0;g<FundamentalSubAnal.length;g++){
						currentSubFundamental=(FundamentalSubAnalisys)FundamentalSubAnal[g];
						currentSubFundamental.ResolveReferences();
						out.println("<LI><B>"+currentSubFundamental.getTipo()+":</B>" +currentSubFundamental.getDescricao());
					}
					out.println("</TD><TD>"+currentFundamental.getValor().intValue());
					out.println("</TD></TR>");
					out.flush();				
				}catch(Exception e){
					out.println(e);
					e.printStackTrace();
					return;					
				}
			}
			out.println("</TABLE><BR><BR><BR>");
			return;	
		}
		
		if(TipoPagina.equals("Cotacoes")){
			Object    Ret;
			int       MinValue=11000,MaxValue=0;
			
			currentPapel=(Papel)myCache.Papeis.get(request.getParameter("Papel"));
			CriarMenu(out,currentPapel,MENU_COTACOES);			
			Ret=myCache.LastCotacoes.get(request.getParameter("Papel"));
			if(Ret==null){
				LastValue=0;
			}else{
				LastValue=((Integer)Ret).intValue();
			}
			  
			it=myCache.Pregao.values().iterator();
			while(it.hasNext()){
				currentPregao=(Pregao)it.next();
				TempKey=request.getParameter("Papel")+"_"+currentPregao.getTimestamp();
				Ret=myCache.Cotacoes.get(TempKey);
				if(Ret==null){
					try{
						Filters=new FilterCondition[2];
						Filters[0]=new FilterCondition();
						Filters[0].FieldName="pregao";
						Filters[0].Operator=FilterCondition.EQUALS;
						Filters[0].ValueName=currentPregao.getTimestamp();								
						Filters[1]=new FilterCondition();
						Filters[1].FieldName="papel";
						Filters[1].Operator=FilterCondition.EQUALS;
						Filters[1].ValueName=((Papel)myCache.Papeis.get(request.getParameter("Papel"))).getTimestamp();
						Cotacoes=myConnection.Query(Class.forName("org.abia.Agents.BovespaAgent.Cotacao"),Filters);
						for(f=0;f<Cotacoes.length;f++){
							Cotacoes[f].ResolveReferences();
							TempKey=((Cotacao)Cotacoes[f]).getPapel().getTimestamp()+"_"+((Cotacao)Cotacoes[f]).getPregao().getTimestamp();
							myCache.Cotacoes.put(TempKey,Cotacoes[f]);
						}
					}catch(Exception e){
						out.println(e);
						e.printStackTrace();					
					}					
				}
			}
			
		    out.println("<CENTER><IMG SRC=\"/abia/Images/Img"+Counter+".gif?a=" + System.currentTimeMillis() +  "\">");
			out.println("<TABLE BORDER=1 BORDERCOLOR=000000 CELLPADDING=0 CELLSPACING=0 WIDTH=100%>");
		    out.println("<TR BORDER=1><TD><FONT FACE=VERDANA SIZE=3>Data</TD><TD><FONT FACE=VERDANA SIZE=3>Abertura</TD><TD><FONT FACE=VERDANA SIZE=3>Minima</TD><TD><FONT FACE=VERDANA SIZE=3>Maximo</TD><TD><FONT FACE=VERDANA SIZE=3>Fechamento</TD>");
		    
		    g=2;
			myChartGenerator=new ChartGenerator();
			myChartGenerator.setChartType("Cotacoes");
			myChartGenerator.setSequence(Counter);
			
		    TempCotacoes=new Vector();
			for(f=0;f<myCache.LatestPregao+1;f++){
				TempKey=request.getParameter("Papel")+"_"+f;
				Ret=myCache.Cotacoes.get(TempKey);
				if(Ret!=null){
					currentCotacao=(Cotacao)Ret;
					currentPregao=(Pregao)myCache.Pregao.get(currentCotacao.getPregao().getTimestamp().longValue()+"");
					if(currentPregao==null) continue;
					if(currentCotacao.getFechamento().doubleValue()>MaxValue)
					   MaxValue=currentCotacao.getFechamento().intValue();
					if(currentCotacao.getFechamento().doubleValue()<MinValue)
					   MinValue=currentCotacao.getFechamento().intValue();
				}
			}
			myChartGenerator.setMaxValue(MaxValue);
			myChartGenerator.setMinValue(MinValue);

			for(f=0;f<myCache.LatestPregao+1;f++){
				TempKey=request.getParameter("Papel")+"_"+f;
				Ret=myCache.Cotacoes.get(TempKey);
				if(Ret!=null){
					currentCotacao=(Cotacao)Ret;
					currentPregao=(Pregao)myCache.Pregao.get(currentCotacao.getPregao().getTimestamp().longValue()+"");
					if(currentPregao==null) continue;
					
					TempCotacoes.add(currentCotacao);
					out.println("<TR>");

					out.println("<TD STYLE=\"{font-size:11}\"><FONT FACE=VERDANA SIZE=2>");
					out.println(getDateAsString(currentPregao.getData()));
					out.println("</TD>");

					out.println("<TD STYLE=\"{font-size:11}\"><FONT FACE=VERDANA SIZE=2>");
					out.println(currentCotacao.getAbertura());
					out.println("</TD>");

					out.println("<TD STYLE=\"{font-size:11}\"><FONT FACE=VERDANA SIZE=2>");
					out.println(currentCotacao.getMinima());
					out.println("</TD>");

					out.println("<TD STYLE=\"{font-size:11}\"><FONT FACE=VERDANA SIZE=2>");
					out.println(currentCotacao.getMaximo());
					out.println("</TD>");

					out.println("<TD STYLE=\"{font-size:11}\"><FONT FACE=VERDANA SIZE=2>");
					out.println(currentCotacao.getFechamento());
					out.println("</TD>");

					out.println("</TR>");
					myChartGenerator.addDataToSend(2,g,currentCotacao.getMaximo().doubleValue());
					myChartGenerator.addDataToSend(3,g,currentCotacao.getMinima().doubleValue());
					myChartGenerator.addDataToSend(4,g,currentCotacao.getFechamento().doubleValue());
					g++;										
				}
			}
			myChartGenerator.setNumeroValores(g-2);
			myChartGenerator.Execute();
			out.println("</TABLE></CENTER>");			
			return;
		}
		
		if(TipoPagina.equals("Inicial")){
			out.println(Mensagem.getMensagemInicial());
			out.println("</BODY>");
			return;
		}		
		out.println(TipoPagina+" nao suportado!!!!");				
	}
}



