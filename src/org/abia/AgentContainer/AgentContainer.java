package org.abia.AgentContainer;

import org.abia.AgentContainer.WebRetriever.*;
import org.abia.AgentContainer.AgentContainerListener.*;
import org.abia.ServletContainer.ServletContainer;
import org.abia.Blackboard.*;
import org.abia.utils.*;

import org.w3c.dom.*;

import java.util.*;
import java.io.*;
import java.net.*;

import javax.xml.parsers.*;

/**
 * @author gbencke
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */

public class AgentContainer extends ServletContainer {
	
	protected static String            BlackBoardClass;
	protected static String            PathToApplication;
	protected static String            PathToAgents;
	protected static String            RawProperties;
	protected static Document          ConfDoc;
	protected static HashMap           Agents=new HashMap();
	protected static HashMap           BlackboardParameters=new HashMap();
	protected static Vector            AgentsList=new Vector();
	protected static AgentContainerLog theLog=new AgentContainerLog();
	protected static WebRetriever      theWebRetriever;
	protected static Vector            ListenersToStart=new Vector(); 

    public static WebRetriever getWebRetriever(){
    	   if(theWebRetriever==null) theWebRetriever=new WebRetriever();
		   return theWebRetriever;    	     
    }

    public static AgentContainerLog getAgentContainerLog(){
		return theLog;    	
    }
    

    public static String   getBlackBoardClass(){
    	return BlackBoardClass;    	
    }
    
    public static String getBinaryDirectory(){
    	return ".\\bin";
    }
	public static String getConfigurationDirectory(){
		return ".\\conf";
	}
    
    public static Document getConfigurationDocument(String DocumentToLoad)  throws AgentContainerException {
		Document DocReturn;
		BufferedReader         in;
		String                 TempReader="",TotalRead="";
		DocumentBuilderFactory Factory;
		DocumentBuilder        ConfDocBuilder;
				    	   
		try{
		   in=new BufferedReader(new FileReader(".\\conf\\"+DocumentToLoad));
		   while( (TempReader=in.readLine()) != null  )    	   	  
		   TotalRead+=TempReader+'\n';
		   in.close();
		}catch(Exception e){
			throw new AgentContainerException();
		}
    	   
		try{
		 Factory=DocumentBuilderFactory.newInstance();
		 Factory.setValidating(true);
		 Factory.setNamespaceAware(false);
		 ConfDocBuilder=Factory.newDocumentBuilder();
		 ConfDoc=ConfDocBuilder.parse(new StringBufferInputStream(TotalRead));
		}catch(Exception e){
			throw new AgentContainerException();
		}
    	return ConfDoc;
    }

    protected static void ReadProperties() throws AgentContainerException {
    	   int f;
    	   
    	   BufferedReader         in;
    	   String                 TempReader,TotalRead;
    	   
    	   DocumentBuilder        ConfDocBuilder;
    	   DocumentBuilderFactory Factory;
    	   NodeList               ListOfNodes;
    	   Node                   BlackboardParentNode;
		   Node					  AgentsParentNode;
		   Node					  ListenerParentNode;
    	   
    	   TotalRead=new String();   
		   TempReader=new String(); 	
    	   
    	   try{
    	   	  in=new BufferedReader(new FileReader(".\\conf\\abia.xml"));
    	   	  while( (TempReader=in.readLine()) != null  )    	   	  
			  TotalRead+=TempReader+'\n';
    	   	  in.close();
    	   }catch(Exception e){
    	   	
    	   }
    	   
		   try{
			Factory=DocumentBuilderFactory.newInstance();
			Factory.setValidating(true);
			Factory.setNamespaceAware(false);
			ConfDocBuilder=Factory.newDocumentBuilder();
			ConfDoc=ConfDocBuilder.parse(new StringBufferInputStream(TotalRead));
			
			// Okay, here we will parse the attributes of the blackboard
			ListOfNodes=ConfDoc.getElementsByTagName("blackboard");			
			if(ListOfNodes.getLength()==0){
				
			}
			BlackboardParentNode=ListOfNodes.item(0);
			BlackBoardClass=BlackboardParentNode.getAttributes().getNamedItem("classname").getNodeValue();
			
			ListOfNodes=ConfDoc.getElementsByTagName("parameter");
			for(f=0;f<ListOfNodes.getLength();f++){
				if(ListOfNodes.item(f).getParentNode()==BlackboardParentNode){
					BlackboardParameters.put(ListOfNodes.item(f).getAttributes().getNamedItem("Name").getNodeValue(),ListOfNodes.item(f).getAttributes().getNamedItem("Value").getNodeValue());					
				}
			}
			
			ListOfNodes=ConfDoc.getElementsByTagName("agents");			
			if(ListOfNodes.getLength()==0){
				
			}
						
			AgentsParentNode=ListOfNodes.item(0);
			PathToAgents=AgentsParentNode.getAttributes().getNamedItem("PathToAgents").getNodeValue();				

			ListOfNodes=ConfDoc.getElementsByTagName("listener");			
			if(ListOfNodes.getLength()==0){

			}
			
			ListenerParentNode=ListOfNodes.item(0);
			ListOfNodes=ConfDoc.getElementsByTagName("listenerThread");			
			if(ListOfNodes.getLength()==0){

			}
			
			for(f=0;f<ListOfNodes.getLength();f++){
				Node   CurrentListener;
				String CurrentPort;
				if(ListOfNodes.item(f).getParentNode()==ListenerParentNode){
					CurrentListener=ListOfNodes.item(f);
					CurrentPort=CurrentListener.getAttributes().getNamedItem("port").getNodeValue();
					ListenersToStart.add(CurrentPort);					
				}
			}				
		   }catch(Exception e){		   	  		   	  
			  System.out.println(e);
			  e.printStackTrace();
		   }
		   
    	   RawProperties=TotalRead;
    }
 
    protected static void LoadAgents() throws AgentContainerException {
    	File             SearchForAgents;
    	String[]         AgentsJar;
    	String           URLJar,ClassForAgent;
    	URL              u;
    	JarURLConnection JarCon;
    	int              f;
    	Class            testIfAgent,AgentOk;  
		URLClassLoader   Loader;
		URL []           ListaURLs;
		Enumeration      itensInJar;
		
    	
    	SearchForAgents=new File(PathToAgents);
    	
    	AgentsJar=SearchForAgents.list();
    	try{
    	    for(f=0;f<AgentsJar.length;f++){
    		    URLJar="jar:file:///ulbra/abia/test/"+PathToAgents +"/"+AgentsJar[f]+"!/";		
		   	    u=new URL(URLJar);
		        JarCon=(JarURLConnection)u.openConnection();
		   
		        ListaURLs=new URL [1];
		        ListaURLs[0]=u;		    
		   		   
		        Loader=new URLClassLoader(ListaURLs);
		        itensInJar=JarCon.getJarFile().entries();
		        while(itensInJar.hasMoreElements()){
		        	  ClassForAgent=itensInJar.nextElement().toString();
					  ClassForAgent=CleanFileName(ClassForAgent);		        
				      try{
				         testIfAgent=Loader.loadClass(ClassForAgent);
				         AgentOk=testIfAgent;				         
						 while(true){
							   if(testIfAgent.getSuperclass().getName().equals("java.lang.Object")) break;
							   if(testIfAgent.getSuperclass().getName().equals("org.abia.AgentContainer.Agent")) break;
							   testIfAgent=testIfAgent.getSuperclass();							   
						 }
						 if(testIfAgent.getSuperclass().getName().equals("org.abia.AgentContainer.Agent")){
							Object NewAgent=AgentOk.newInstance();														
							Agents.put(AgentOk.getName(),NewAgent);
							AgentsList.add(AgentOk.getName());													    	
						 }
				      }catch(Exception e){
			      
				      }
    	        }
    	    }	
    	}catch(Exception e){
    		System.out.println(e);    		
			e.printStackTrace();
    	}	  
    }
    
    /**
     * Executes the agents
     * 
     */

	protected static void   RunAgents(){
		int        f;
		Agent      CurrentAgent;    	

		for(f=0;f<AgentsList.size();f++){
		    try{
			   CurrentAgent=(Agent)Agents.get(AgentsList.elementAt(f));
			   CurrentAgent.setName(CurrentAgent.getAgentNameInBlackboard());
			   CurrentAgent.start();
			}catch(Exception e){
    		
			}			   
	    }    		    		    		
	}

    protected static void   InitializeAgents(){
    	int        f;
    	Agent      CurrentAgent;    	

    	   for(f=0;f<AgentsList.size();f++){
			   try{
    		      CurrentAgent=(Agent)Agents.get(AgentsList.elementAt(f));
			      CurrentAgent.Initialize();
		       }catch(Exception e){
    		
		       }			   
    	   }    		    		    
    }

	protected static String CleanFileName(String ClassForAgent){
		int    f;
		String Return;
		
		if(!ClassForAgent.endsWith(".class")) return "";
		Return=ClassForAgent;
		Return=Return.substring(0,Return.length()-6);		
		Return.replace('\\','.');
		return Return;
	}

	public static void LaunchListeners(){		
		int                    f,Porta;				
		String                 PortaParaIniciar;
		AgentContainerListener CurrentListener;
		
		for(f=0;f<ListenersToStart.size();f++){
			PortaParaIniciar=(String)ListenersToStart.get(f);
			CurrentListener=new AgentContainerListener(new Integer(PortaParaIniciar).intValue());
			CurrentListener.start();						
		}
	
	}
	

	public static void main(String[] args) {
				URL teste;
				InputStream in;
				URLConnection urlCon;
				int f,g,h;
				
				try{
				   
				   //Utils.CriarCVM();	
				   //Utils.DownloadFromYahoo();
				   Utils.ClearDatabase();			   
				   ReadProperties();
				   Blackboard.Initialize(BlackboardParameters);				   
				   LoadAgents();				   				   
				   InitializeAgents();				   
				   //Utils.CriarBDIs();
				   RunAgents();
				   LaunchListeners();
				   //Utils.CriarCVM();				   
				}catch(Exception e){
					System.out.println(e);
					e.printStackTrace();
				}
				
	}
}
