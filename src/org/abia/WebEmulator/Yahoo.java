/* $Id: HelloWorldExample.java,v 1.2 2001/11/29 18:27:25 remm Exp $
 *
 */

package org.abia.WebEmulator;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * The simplest possible servlet.
 *
 * @author James Duncan Davidson
 */

public class Yahoo extends HttpServlet {


	public void doGet(HttpServletRequest request,
					  HttpServletResponse response)
		throws IOException, ServletException
	{
		int f,lido;
		BufferedReader         in;
		String TotalRead="",TempReader="",PathToData="";

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		TotalRead="";
		TempReader="";
		
		try{
/*			NomeURL+="?a="+(Mes_Inicial-1);
			NomeURL+="&b="+Dia_Inicial;
			NomeURL+="&c="+Ano_Inicial;
			NomeURL+="&d="+(Mes_Final-1);
			NomeURL+="&e="+Dia_Final;
			NomeURL+="&f="+Ano_Final;
			NomeURL+="&g=d&s="+Simbolo+".sa";*/
			
			PathToData=this.getServletContext().getRealPath("/")+"Downloaded\\_cotacoes_";
			PathToData=PathToData+request.getParameter("b")+"_";
			
			lido=(new Integer(request.getParameter("a"))).intValue()+1;
			PathToData=PathToData+(new Integer(lido)).toString()+"_";

			PathToData=PathToData+request.getParameter("c")+"_";            

			PathToData=PathToData+request.getParameter("e")+"_";

			lido=(new Integer(request.getParameter("d"))).intValue()+1;
			PathToData=PathToData+(new Integer(lido)).toString()+"_";

			PathToData=PathToData+request.getParameter("f")+"_"; 
			PathToData=PathToData+request.getParameter("s").substring(0,5)+".txt";

		   in=new BufferedReader(new FileReader(PathToData));
		   while( (TempReader=in.readLine()) != null  )    	   	  
				   TotalRead+=TempReader+'\n';
		   in.close();		   
		   out.println(TotalRead);
		}catch(Exception e){
		   out.println(e);    	   	
		}
		out.close();
	}
}



