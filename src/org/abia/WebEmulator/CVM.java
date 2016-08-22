/* $Id: HelloWorldExample.java,v 1.2 2001/11/29 18:27:25 remm Exp $
 *
 */

package org.abia.WebEmulator;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.net.*;

/**
 * The simplest possible servlet.
 *
 * @author James Duncan Davidson
 */

public class CVM extends HttpServlet {


	public void doGet(HttpServletRequest request,
					  HttpServletResponse response)
		throws IOException, ServletException
	{
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		BufferedReader         in;
		String  TempReader,TotalRead;
		String  PathToGet,DataAtual;

		PathToGet=this.getServletContext().getRealPath("/")+"\\";
		PathToGet+=request.getParameter("Tipo")+"_";
		PathToGet+=request.getParameter("Assunto")+"_";
		PathToGet+=request.getParameter("CVM")+"_";		
		PathToGet+=request.getParameter("Dia")+"%2F";
		PathToGet+=request.getParameter("Mes")+"%2F";
		PathToGet+=request.getParameter("Ano");				
		PathToGet+="_.txt";
		

		TotalRead="";
		TempReader="";		
		try{
		   in=new BufferedReader(new FileReader(PathToGet));
		   while( (TempReader=in.readLine()) != null  )    	   	  
				   TotalRead+=TempReader+'\n';
		   in.close();		   
		   out.println(TotalRead);
		}catch(Exception e){
		   response.setStatus(404);
		   out.println(e);    	   	
		}

	}
}



