package org.abia.AgentContainer.WebRetriever;

import java.net.*;
import java.util.*;

public class HTMLContentHandler extends ContentHandler {
	public static int DEFAULT_INSERT=0;
	public static int DEFAULT_DELETE=1;
	
	
	protected URLConnection currentConnection;
	
	protected static String ProcessarTagElement(String InToken){
		String ReturnedString="";
		int f;
		boolean Started=false;
		
		for(f=1;f<InToken.length()-1;f++){
			if(!InToken.substring(f,f+1).equals(" ")){
					ReturnedString+=InToken.substring(f,f+1);
					Started=true;
			}else{
					break;
			}
		}
		
		return ("<"+ReturnedString.toUpperCase()+">");
	}

	public static String ApplyFilter(String InPage,HashMap Filters,int Behaviour){
		int     f,g,h;
		String  ReturnedString="",CurrentToken="";
		boolean PaginaIniciou=false;
		
		for(f=0;f<InPage.length();f++){
			CurrentToken=InPage.substring(f,f+1);
			if(CurrentToken.equals("<")){
				String Token,TokenLimpo;				
				
				for(g=f;g<InPage.length();g++){
					if(InPage.substring(g,g+1).equals(">")) break;
				}
				Token=InPage.substring(f,g+1);
				TokenLimpo=ProcessarTagElement(Token);
				if(TokenLimpo.equals("<BODY>"))
				   PaginaIniciou=true;
				if(PaginaIniciou){
				   if(Filters==null){
					  if(Behaviour==HTMLContentHandler.DEFAULT_INSERT)
					     ReturnedString=ReturnedString+TokenLimpo;				   	  
				   }else{
				   	  Object obj;
				   	  
					  obj=Filters.get(TokenLimpo);
				   	  if(!(obj!=null && (obj instanceof HTMLFilter))){
						if(Behaviour==HTMLContentHandler.DEFAULT_INSERT)
						   ReturnedString=ReturnedString+TokenLimpo;				   	  				   	  	
				   	  }else{
				   	  	HTMLFilter htmlfilter;
						
						htmlfilter=(HTMLFilter)obj;
						if(htmlfilter.Behaviour==HTMLFilter.ACCEPT){
						   ReturnedString=ReturnedString+TokenLimpo;
						   if(htmlfilter.AddToEnd!=null)
						      ReturnedString=ReturnedString+htmlfilter.AddToEnd;
						}
						if(htmlfilter.Behaviour==HTMLFilter.ACCEPT_KEEP_ATTRIBUTES){
						   ReturnedString=ReturnedString+InPage.substring(f,g+1);
						   if(htmlfilter.AddToEnd!=null)
							  ReturnedString=ReturnedString+htmlfilter.AddToEnd;
						}						  										      
				      }				   				
				   }
				}
			  f=g;																			
			}else{
				if(PaginaIniciou)
				   ReturnedString=ReturnedString+CurrentToken;				
			}
		}
		return ReturnedString;		
	}
	
	public boolean IsContentHandled(String Content){
		return true;
	}
	
	public HTMLContentHandler(URLConnection MyConection){
		currentConnection=MyConection;		
	}


	public String getContentAsString(){ 
			byte Buffer[]=null;
			String ReturnedString="";
			int nRead=0,nLastRead=0,SizeBuffer;
			
			
			try{		
			   currentConnection.connect();
			   SizeBuffer=currentConnection.getContentLength();
			   if(SizeBuffer==-1){
			      Buffer=new byte[200000];
			      while(true){
			            nLastRead=currentConnection.getInputStream().read(Buffer);
			            if(nLastRead==-1) break;
			            nRead=nRead+nLastRead;
			            ReturnedString=ReturnedString+(new String(Buffer)).substring(0,nLastRead);
			      }
			   }else{
				Buffer=new byte[currentConnection.getContentLength()];
				while(nRead!=currentConnection.getContentLength()){
					  nLastRead=currentConnection.getInputStream().read(Buffer);
					  nRead=nRead+nLastRead;
					  ReturnedString=ReturnedString+(new String(Buffer)).substring(0,nLastRead);
				}
			   }
			}catch(Exception e){
				System.out.println(e);			
				e.printStackTrace();
			}
			return ReturnedString;
	}
	
	public byte[] getContent(){ 
		byte Buffer[]=null,ReturnedBuffer[];
		int nRead=0,nLastRead=0;
		
		try{		
		   Buffer=new byte[currentConnection.getContentLength()];		
		   while(nRead!=currentConnection.getContentLength()){
				 nLastRead=currentConnection.getInputStream().read(Buffer,0,currentConnection.getContentLength());
				 nRead=nRead+nLastRead;
		   }			   

		}catch(Exception e){
			
		}
		return Buffer;
	}
	
	public String[] getHeaders(){
		int      f,Count=0;
		String   HeadersToReturn[]=null,Temp;;
		Iterator i;
				
        try{
			Count=currentConnection.getHeaderFields().values().size();
			HeadersToReturn=new String[Count];
        	for(f=0;f<Count;f++){
				HeadersToReturn[f]=currentConnection.getHeaderFieldKey(f)+":"+currentConnection.getHeaderField(f);        		
        	}
        }catch(Exception e){
        	System.out.println(e);
			e.printStackTrace();
        }         
		return HeadersToReturn;		
	}

    public static String ReturnStringFromStack(int Stack[],int Pos){
    	int f,g;
    	String ReturnedString="";
    	
		if(Pos==-1) return "";		
    	for(f=0;f<Pos+1;f++){
			ReturnedString=ReturnedString+(new Integer(Stack[f])).toString();
			ReturnedString=ReturnedString+".";			
    	}
    	//System.out.println(ReturnedString.substring(2,ReturnedString.length()-1));    	
    	return ReturnedString.substring(2,ReturnedString.length()-1);
    }
	
	public static String RetrieveTable(String Source,String Pos){
		int     f,g,h,PosInicial=0,PosFinal=0;
		String  ReturnedString="",CurrentToken="";
		boolean PaginaIniciou=false,IniciouPagina=false;
		int     CurrentStack[],PosStack;
		
		
		CurrentStack=new int[100];
		PosStack=0;
		
		for(f=0;f<100;f++)
			CurrentStack[f]=0;

		for(f=0;f<Source.length();f++){
			CurrentToken=Source.substring(f,f+1);
			if(CurrentToken.equals("<")){
				String Token,TokenLimpo;				
				
				for(g=f;g<Source.length();g++){
					if(Source.substring(g,g+1).equals(">")) break;
				}
				Token=Source.substring(f,g+1);
				TokenLimpo=ProcessarTagElement(Token);
				if(TokenLimpo.equals("<TABLE>")){
					PosStack++;
					CurrentStack[PosStack]++;
					if(ReturnStringFromStack(CurrentStack,PosStack).equals(Pos)){
					   PosInicial=f+7;					
					}
				}
				if(TokenLimpo.equals("</TABLE>")){					
					if(ReturnStringFromStack(CurrentStack,PosStack).equals(Pos)){
					   PosFinal=f;					   
					   break;					
					}
					//CurrentStack[PosStack]--;
					PosStack--;
				}				
			  f=g;
			  ReturnedString=ReturnedString+TokenLimpo;																			
			}else{
			  ReturnedString=ReturnedString+CurrentToken;				
			}
		}
		if(PosInicial==0 || PosFinal==0) return "";
		return Source.substring(PosInicial,PosFinal);		
	}

    public static int GetNumberOfRows(String Source){
		int     f,g,h,PosInicial=0,PosFinal=0;
		int     CurrentRow=0,CurrentCol=0;
		String  ReturnedString="",CurrentToken="";
		boolean PaginaIniciou=false,IniciouPagina=false;
	
				
		for(f=0;f<Source.length();f++){
			CurrentToken=Source.substring(f,f+1);
			if(CurrentToken.equals("<")){
				String Token,TokenLimpo;				
				
				for(g=f;g<Source.length();g++){
					if(Source.substring(g,g+1).equals(">")) break;
				}
				Token=Source.substring(f,g+1);
				TokenLimpo=ProcessarTagElement(Token);
				if(TokenLimpo.equals("<TR>")){
					CurrentRow++;					
				}
			}
		}
		return CurrentRow;		
    }

    public static String RetrieveNextCell(String Source,int Row,int Col,Integer[] Pos){		
		return RetrieveCell(Source,1,Col,Pos);
    }
    
	public static String RetrieveCell(String Source,int Row,int Col){
		Integer Test[];
		
		Test=new Integer[1];
		Test[0]=new Integer(0);
		return RetrieveCell(Source,Row,Col,Test);		
	}

	public static String RetrieveCell(String Source,int Row,int Col,Integer Pos[]){
		int     f,g,h,PosInicial=0,PosFinal=0;
		int     CurrentRow=0,CurrentCol=0;
		String  ReturnedString="",CurrentToken="";
		boolean PaginaIniciou=false,IniciouPagina=false;
	
				
		for(f=Pos[0].intValue();f<Source.length();f++){
			CurrentToken=Source.substring(f,f+1);
			if(CurrentToken.equals("<")){
				String Token,TokenLimpo;				
				
				for(g=f;g<Source.length();g++){
					if(Source.substring(g,g+1).equals(">")) break;
				}
				Token=Source.substring(f,g+1);
				TokenLimpo=ProcessarTagElement(Token);
				if(TokenLimpo.equals("<TR>")){
					CurrentRow++;					
					CurrentCol=0;										
				}
				if(TokenLimpo.equals("<TD>")){
					CurrentCol++;
					Pos[0]=new Integer(f-1);					
					PosInicial=f+4;
				}
				if(TokenLimpo.equals("</TD>")){
					if(CurrentRow==Row &&
					   CurrentCol==Col){
					   	PosFinal=f;
					   	break;
					}
					if(CurrentRow>Row){
						return "";
					}
				}				
			  f=g;																			
			}
		}
		
		if(PosInicial==0 || PosFinal==0) return "";
		return Source.substring(PosInicial,PosFinal);		
	}

	
	public int getResponseStatus(){
		HttpURLConnection cur;
		int ret;
		
		try{
		   cur=(HttpURLConnection)currentConnection;
		   ret=cur.getResponseCode();
		}catch(Exception e){
		   return 0;	
		}
		return ret;
	}
}