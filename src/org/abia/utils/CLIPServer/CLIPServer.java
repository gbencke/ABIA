package org.abia.utils.CLIPServer;

import org.abia.AgentContainer.*;
import org.abia.Blackboard.*;
import org.abia.Blackboard.PostgreSQL.*;
import org.abia.Agents.BovespaAgent.*;
import org.abia.Agents.CVMAgent.*;

import java.io.*;
import java.net.*;
import org.w3c.dom.*;
import java.lang.reflect.*;

import javax.xml.parsers.*;


public class CLIPServer extends Thread {

	public static boolean Initialized;
	public static String  Description[];
	
	protected Socket      currentSocket;
	protected String      RulesFile; 
	public    int         currentPort;
	public    Class       DataToBeUsed[];
	
	static {
		Initialized=false;
	}
	
	public static synchronized void Initialize(){
		int                           f,g;
		ClassRegisteredInBlackboard   FromBlackboard[];
		String                        toReturn="",TempFile;
		DataOutputStream              out2;
		
		if(Initialized) return;
		FromBlackboard=Blackboard.getBlackboard().getClassRegisteredInBlackboard();
		Description=new String[FromBlackboard.length];
		for(f=0;f<FromBlackboard.length;f++){			
			toReturn="(defclass "+FromBlackboard[f].NameInBlackBoard.toUpperCase()+" (is-a USER)(role concrete)\r\n";
			toReturn+="     (slot Timestamp)\r\n";
			for(g=0;g<FromBlackboard[f].Values.size();g++){
			    ClassMethod TempClassMethod;

			    TempClassMethod=(ClassMethod)FromBlackboard[f].Values.get(g);
				toReturn+="      (slot ";
				toReturn+=TempClassMethod.ValueToStore; 
				toReturn+=")\r\n";
			}
			toReturn+=")\r\n";
			Description[f]=new String(toReturn);			
		}
		
		
		TempFile=AgentContainer.getConfigurationDirectory()+"\\defclasses.clp";
		try{
			out2=new DataOutputStream(new BufferedOutputStream(new FileOutputStream(TempFile)));
			out2.writeBytes(toReturn);
			out2.close();
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
		}
		Initialized=true;
	}

	protected void SendAgentDataDefinitions(){
		String         Message="",TempStr="",MessageReceived="";
		BufferedReader in;
		int f;
		
		try{
			for(f=0;f<CLIPServer.Description.length;f++){
				Message="<Message Type=Execute>\r\n";
				Message+=CLIPServer.Description[f];			
				Message+="</Message>";
				currentSocket.getOutputStream().write(Message.getBytes());
				in=new BufferedReader(new InputStreamReader(currentSocket.getInputStream()));
				while(true){
			   		if((TempStr=in.readLine())!=null)
					   	MessageReceived+=TempStr;
				   	if(MessageReceived.indexOf("</Message>")>-1) break;
			    }
			}
						
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
		}
		
	}
	protected void SendAgentRules(){
		String         Message="",TempStr="",MessageReceived="",DataToSend="";
		String         TempReader="",TotalRead="",toSend;
		BufferedReader in;
		int            f,NivelAtual,Inicio;
		
		try{
			in=new BufferedReader(new FileReader(".\\conf\\"+RulesFile));
			while( (TempReader=in.readLine()) != null  )    	   	  
			TotalRead+=TempReader+"\r\n";
			in.close();						
			
			NivelAtual=0;
			toSend="";
			Inicio=0;
			for(f=0;f<TotalRead.length();f++){
				toSend=toSend+TotalRead.substring(f,f+1);
				if(TotalRead.substring(f,f+1).equals("(")){
					NivelAtual++;				
				}
				if(TotalRead.substring(f,f+1).equals(")")){
					NivelAtual--;				
					if(NivelAtual==0){				
						Message+="<Message Type=Execute>\r\n";
						Message+=TotalRead.substring(Inicio,f+1);			
						Message+="\r\n</Message>\r\n";
						currentSocket.getOutputStream().write(Message.getBytes());
						in=new BufferedReader(new InputStreamReader(currentSocket.getInputStream()));
						while(true){
			   				if((TempStr=in.readLine())!=null)
				   				MessageReceived+=TempStr;
			   				if(MessageReceived.indexOf("</Message>")>-1) break;
						}
						Message="";TempStr="";MessageReceived="";DataToSend="";
						toSend="";
						Inicio=f+1;						
					}
					
				}
			}						
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
		}		
	}

	protected void SendAgentData(String DataToSend){
		String         Message="",TempStr="",MessageReceived="";
		BufferedReader in;
		
		try{
			Message+="<Message Type=Execute>\r\n";
			Message+=DataToSend;			
			Message+="</Message>";
			currentSocket.getOutputStream().write(Message.getBytes());
			in=new BufferedReader(new InputStreamReader(currentSocket.getInputStream()));
			while(true){
			   if((TempStr=in.readLine())!=null)
				   MessageReceived+=TempStr;
			   if(MessageReceived.indexOf("</Message>")>-1) break;			
			}
						
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
		}

	}

	protected void ReadAgentData(){
		int                     f,g,h,i;
		BufferedReader          in;
		String                  Message="",TempStr="",MessageReceived="";
		Document                DocReturn;
		DocumentBuilderFactory  Factory;
		DocumentBuilder         ConfDocBuilder;
		NodeList                ListOfNodes,ListOfChildrenNodes;
		Node                    MessageParentNode;
		AgentData               DataToReturn[];
		Method                  MethodFound;
		Class                   TempClass,ClassFound=null;
		Class                   TempClasses[];
		Object                  TempObjects[];
		ClassRegisteredInBlackboard currentClass=null,ClassesRegistered[];
		ClassMethod             currentClassMethod;
		String                  PropertyName,TempValue,TempNodeName,TempUpper1,TempUpper2;
		DummyAgentData          TempDummy;
		String                  NameClass,TimestampToSet;
		
		
		try{
			while(true){
			Message="";
			TempStr="";
			MessageReceived="";
			Message+="<Message Type=\"GetAnalisys\">\r\n";			
			Message+="</Message>\r\n";
			currentSocket.getOutputStream().write(Message.getBytes());
			in=new BufferedReader(new InputStreamReader(currentSocket.getInputStream()));
			while(true){
			   if((TempStr=in.readLine())!=null)
				   MessageReceived+=TempStr;
			   if(MessageReceived.indexOf("</Message>")>-1) break;			
			}
			if(MessageReceived.length()<60) return;
			Factory=DocumentBuilderFactory.newInstance();
			Factory.setValidating(true);
			Factory.setNamespaceAware(false);
			ConfDocBuilder=Factory.newDocumentBuilder();
			DocReturn=ConfDocBuilder.parse(new StringBufferInputStream(MessageReceived));
			ListOfNodes=DocReturn.getElementsByTagName("Message");
			if(ListOfNodes.getLength()==0) return;			
			ListOfNodes=DocReturn.getElementsByTagName("AgentData");
			DataToReturn=new AgentData[ListOfNodes.getLength()];
			for(f=0;f<ListOfNodes.getLength();f++){
				MessageParentNode=ListOfNodes.item(f);				
				NameClass=ListOfNodes.item(f).getAttributes().getNamedItem("ClassName").getNodeValue();
				ClassesRegistered=Blackboard.getBlackboard().getClassRegisteredInBlackboard();
				for(i=0;i<ClassesRegistered.length;i++){
					if(ClassesRegistered[i].NameInBlackBoard.toUpperCase().equals(NameClass.toUpperCase())){
						ClassFound=ClassesRegistered[i].ClassInBlackBoard;
						currentClass=ClassesRegistered[i];
						break;
					}					
				}
				if(ClassFound==null) return;
				DataToReturn[f]=(AgentData)ClassFound.newInstance();
				DataToReturn[f].GenerateTimestamp();
				ListOfChildrenNodes=ListOfNodes.item(f).getChildNodes();				
				for(g=0;g<ListOfChildrenNodes.getLength();g++){
					TempNodeName=ListOfChildrenNodes.item(g).getNodeName().toUpperCase();
					if(TempNodeName.equals("PROPERTY")){
						PropertyName=ListOfChildrenNodes.item(g).getAttributes().getNamedItem("Name").getNodeValue();
						TempValue=ListOfChildrenNodes.item(g).getAttributes().getNamedItem("Value").getNodeValue();
						for(h=0;h<currentClass.Values.size();h++){
							currentClassMethod=(ClassMethod)currentClass.Values.get(h);
							if(PropertyName.toUpperCase().equals(currentClassMethod.ValueToStore.toUpperCase())){
								TempClass=currentClassMethod.ReturnValue.getReturnType();
								TempClasses=new Class[1];
								TempClasses[0]=TempClass;
								MethodFound=DataToReturn[f].getClass().getMethod("set"+currentClassMethod.ValueToStore,TempClasses);
								TempObjects=new Object[1];
								if(currentClassMethod.ReturnValue.getReturnType()==Class.forName("org.abia.Blackboard.AgentData")){						
									TempDummy=new DummyAgentData((new Integer(TempValue)).intValue()); 
						   
									TempObjects[0]=TempDummy;						   						   
									MethodFound.invoke(DataToReturn[f],TempObjects); 					
								}
								if(currentClassMethod.ReturnValue.getReturnType()==Class.forName("java.util.Date")){								
									String tempDia,tempMes,tempAno;
									TempObjects[0]=new java.util.Date();
									tempAno=TempValue.substring(0,4);
									tempMes=TempValue.substring(4,6);
									tempDia=TempValue.substring(6,8);
									((java.util.Date)TempObjects[0]).setYear((new Integer(tempAno)).intValue());
									((java.util.Date)TempObjects[0]).setMonth((new Integer(tempMes)).intValue()-1);
									((java.util.Date)TempObjects[0]).setDate((new Integer(tempDia)).intValue());
									MethodFound.invoke(DataToReturn[f],TempObjects);   								   	
								}
								if(currentClassMethod.ReturnValue.getReturnType()==Class.forName("java.lang.Long")){								
									TempObjects[0]=new Long(TempValue);
									MethodFound.invoke(DataToReturn[f],TempObjects);   								   	
								}
								if(currentClassMethod.ReturnValue.getReturnType()==Class.forName("java.lang.String")){								
									TempObjects[0]=new String(TempValue);
									MethodFound.invoke(DataToReturn[f],TempObjects);   								   	
								}
								if(currentClassMethod.ReturnValue.getReturnType()==Class.forName("java.lang.Integer")){								
									TempObjects[0]=new Integer(TempValue);
									MethodFound.invoke(DataToReturn[f],TempObjects);   								   	
								}
								if(currentClassMethod.ReturnValue.getReturnType()==Class.forName("java.lang.Boolean")){								
									TempObjects[0]=new Boolean(TempValue);
									MethodFound.invoke(DataToReturn[f],TempObjects);   								   	
								}
								if(currentClassMethod.ReturnValue.getReturnType()==Class.forName("java.lang.Double")){								
									TempObjects[0]=new Double(TempValue);						  
									MethodFound.invoke(DataToReturn[f],TempObjects);   								   	
								}
							}
						}
					}
				}

			}
			Blackboard.getBlackboard().Store(DataToReturn);			
			System.out.println("Agent Data got from CLIPServer...");
			}
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
		}
	}


    protected synchronized void StartIt(){
		try{   
			Thread.sleep(15000);
			Runtime.getRuntime().exec(AgentContainer.getBinaryDirectory()+"\\CLIPSServer.exe "+currentPort);
			Thread.sleep(5000);
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
		}    	
    }
	
	public CLIPServer(String Name,int Port,String RulesFileToLoad,Class inDataToBeUsed[]) throws CLIPServerException {
		setName(Name);
		
		if(!CLIPServer.Initialized){
			CLIPServer.Initialize();			
		}
		
		currentPort=Port;
		RulesFile=RulesFileToLoad;
		DataToBeUsed=inDataToBeUsed;
		
		StartIt();
		start();
	}
	
	public void run(){
		AgentData                     DataToSend[];
		int                           LastTimestamp=0,f,g,i,ReallySent;
		ClassMethod                   TempMethod;
		java.util.Date                TempDate;
		ClassRegisteredInBlackboard   ClassRegistered;
		String                        ValueToReturn="",ReturnString="",TempFile;
		DataOutputStream              out2;
		
		//if(LastTimestamp==0) return;
		
		try{
			currentSocket=new Socket();
			currentSocket.connect(new InetSocketAddress("127.0.0.1",currentPort));
			SendAgentDataDefinitions();
			SendAgentRules();
			out2=new DataOutputStream(new BufferedOutputStream(new FileOutputStream("C:\\"+ this.getName() +".txt")));			
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
			return;
		}	       	
		
		while(true){			
			try{
				DataToSend=Blackboard.getBlackboard().QueryTimestamp(LastTimestamp,Blackboard.TIMESTAMP_GREATER_THAN);
				ReturnString="";
				ReallySent=0;
				for(f=0;f<DataToSend.length;f++){
					if(DataToSend[f].getTimestamp().intValue()!=(LastTimestamp+1)){
						Thread.sleep(1000);
						break;												
					}
					LastTimestamp=DataToSend[f].getTimestamp().intValue();
					if(DataToBeUsed!=null){
						for(i=0;i<DataToBeUsed.length;i++){
							if(DataToSend[f].getClass().getName().equals(DataToBeUsed[i].getName())) break;							
						}
						if(i==DataToBeUsed.length) continue;
					}					
					ReallySent++; 					
					ClassRegistered=Blackboard.getBlackboard().getClassRegisteredInBlackboard(DataToSend[f].getClass());
					ReturnString+="(make-instance ["+ClassRegistered.NameInBlackBoard+DataToSend[f].getTimestamp()+"] of " + ClassRegistered.NameInBlackBoard.toUpperCase()+"\r\n";
					ReturnString+="   (Timestamp "+DataToSend[f].getTimestamp()+")\r\n";

					for(g=0;g<ClassRegistered.Values.size();g++){
						TempMethod=(ClassMethod)ClassRegistered.Values.get(g);
						ReturnString+=" ("+TempMethod.ValueToStore;
						ValueToReturn=" ";
						if(TempMethod.ReturnValue.getReturnType().getName().equals("java.util.Date")){							
							TempDate=(java.util.Date)TempMethod.ReturnValue.invoke(DataToSend[f],null);
							ValueToReturn=" ";
							ValueToReturn+=(new Integer(TempDate.getYear()+1900));
							if((TempDate.getMonth()+1)<10){
								ValueToReturn+="0";
							}
							ValueToReturn+=(TempDate.getMonth()+1);
							if(TempDate.getDate()<10){
								ValueToReturn+="0";
							}
							ValueToReturn+=TempDate.getDate();
						}else{							
							if(TempMethod.ReturnValue.getReturnType().getName().equals("java.lang.String")){
								Object tmp;
								try{								
									tmp=TempMethod.ReturnValue.invoke(DataToSend[f],null);
								    if(tmp!=null){
								       ValueToReturn+="\""+tmp.toString()+"\"";
								    }else{
										ValueToReturn+="\" \"";								    	
								    }
								}catch(Exception e){
									System.out.println(e);
									e.printStackTrace();
								}
							}else{
								Object tmp;
								try{								
									tmp=TempMethod.ReturnValue.invoke(DataToSend[f],null);
									if(tmp!=null){
									   ValueToReturn+=tmp.toString();
									}else{
										ValueToReturn+=" ";								    	
									}
								}catch(Exception e){
									System.out.println(e);
									e.printStackTrace();
								}
							}
						}
						ReturnString+=ValueToReturn;							
						ReturnString+=" )\r\n";
					}
					ReturnString+=")\r\n";
					out2.writeBytes(new String(LastTimestamp+"-------------------------------------------------------------"));
					out2.writeBytes(ReturnString);
					out2.flush();					
					SendAgentData(ReturnString);
					ReadAgentData();
					ReturnString="";					
					/*if((f % 100)==0 && f>0){							
					    TempFile=AgentContainer.getConfigurationDirectory()+"\\" + getName() + LastTimestamp  +".clp";
					    out2=new DataOutputStream(new BufferedOutputStream(new FileOutputStream(TempFile)));
					    out2.writeBytes(ReturnString);
					    out2.close();
						ReturnString="";
					}*/
					Thread.sleep(50);
				}
				if(ReallySent==0) Thread.sleep(7000); 
			}catch(Exception e){
				System.out.println(e);
				e.printStackTrace();
			}
		}
		
		
	}
}