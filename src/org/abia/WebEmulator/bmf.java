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

public class bmf extends HttpServlet {


	public void doGet(HttpServletRequest request,
					  HttpServletResponse response)
		throws IOException, ServletException
	{
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		BufferedReader         in;
		String  TempReader,TotalRead;
		
		TotalRead="";
		TempReader="";
		
		try{
		   in=new BufferedReader(new FileReader(this.getServletContext().getRealPath("/")+"Downloaded\\bmf.txt"));
		   while( (TempReader=in.readLine()) != null  )    	   	  
		           TotalRead+=TempReader+'\n';
		   in.close();
		   
		   out.println(TotalRead);
		}catch(Exception e){
			out.println(e);    	   	
		}
		out.println();		
	}
}



