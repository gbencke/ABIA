package org.abia.AgentContainer.AgentContainerListener;

import java.util.*;
import java.io.*;
import java.net.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.lang.reflect.*;

import org.abia.Blackboard.*;
import org.abia.Blackboard.PostgreSQL.*;

public class AgentWorkerThread extends Thread {
	AgentContainerListener   MyParent;
	Socket                   theSocket;
	protected  boolean       SITC=true;

	public AgentWorkerThread(AgentContainerListener Parent,Socket toReceive){
		MyParent=Parent;
		theSocket=toReceive;
		this.setPriority(Thread.MAX_PRIORITY);		
	}

	protected String ProcessQueryMessage(Document Parameters){
		AgentData                    DataToReturn[];
		int                          f,g,totalSent,tot;
		boolean                      Found;
		BufferedReader               out;
		Node                         MessageParentNode;		
		NodeList                     ListOfNodes;
		Vector                       Classes=new Vector();
		Vector                       Conditions=new Vector();
		String                       TempString,TempResponseMessage="",ClassToRetrieve=null;
		String                       ReturnString="";
		QueryCondition               ConditionsToQuery[];
		PostgreSQLFilterCondition    currentFilter;
		PostgreSQLOrderCondition     currentOrder;
		ClassRegisteredInBlackboard  ClassRegistered;
		ClassMethod                  TempMethod;
		String                       ValueToReturn;
		java.util.Date               TempDate;		
		String                       TempFieldName;
		ClassMethod                  TempClassMethod;
		Class                        Param[];


		try{
			ListOfNodes=Parameters.getElementsByTagName("Message");
			if(ListOfNodes.getLength()==0) return "";
			MessageParentNode=ListOfNodes.item(0);
			ListOfNodes=MessageParentNode.getChildNodes();
			
			Found=false;
			for(f=0;f<ListOfNodes.getLength();f++){
				TempString=ListOfNodes.item(f).getNodeName();
				TempString=TempString.toUpperCase();				
				if(TempString.equals("CLASSNAME")){
					ClassToRetrieve=ListOfNodes.item(f).getAttributes().getNamedItem("Name").getNodeValue();
					Classes.add(TempString);
					Found=true;					
				}
				if(TempString.equals("ORDER")){
					currentOrder= new PostgreSQLOrderCondition();
					TempString=ListOfNodes.item(f).getAttributes().getNamedItem("Fieldname").getNodeValue();
					currentOrder.Field=TempString;
					TempString=ListOfNodes.item(f).getAttributes().getNamedItem("Order").getNodeValue();					
					if(TempString.equals("1"))
						currentOrder.Order=OrderCondition.ASC;						
					if(TempString.equals("2"))
						currentOrder.Order=OrderCondition.DESC;						
				}				
				if(TempString.equals("CONDITION")){
					
					Param=new Class[1];
					Param[0]=Class.forName("java.lang.String");					
					currentFilter= new PostgreSQLFilterCondition();
					TempString=ListOfNodes.item(f).getAttributes().getNamedItem("Fieldname").getNodeValue();
					currentFilter.FieldName=TempString;
					TempString=ListOfNodes.item(f).getAttributes().getNamedItem("Operation").getNodeValue();
					currentFilter.Operator=(new Integer(TempString)).intValue();
					TempString=ListOfNodes.item(f).getAttributes().getNamedItem("ValueName").getNodeValue();
					
					ClassRegistered=Blackboard.getBlackboard().getClassRegisteredInBlackboard(ClassToRetrieve);
					for(g=0;g<ClassRegistered.Values.size();g++){
						if(currentFilter.FieldName.toUpperCase().equals("TIMESTAMP") ||
						   currentFilter.FieldName.toUpperCase().equals("TIMESTAMPVAL")){
						   currentFilter.ValueName=new Integer(TempString);
						   continue;
						}
						TempClassMethod=(ClassMethod)ClassRegistered.Values.get(g);
						TempFieldName=TempClassMethod.ValueToStore.toUpperCase();						
						if(currentFilter.FieldName.toUpperCase().equals(TempFieldName)){
							TempFieldName=TempClassMethod.ReturnValue.getReturnType().getName();
							if(TempClassMethod.ReturnValue.getReturnType().getName().equals("org.abia.Blackboard.AgentData")){
							   currentFilter.ValueName=(AgentData)(new DummyAgentData((new Integer(TempString)).intValue()));
							}else{
								DummyAgentData  TempDummyAgentData;
								Constructor     Const;
								Object          obj[];
								Class           ClassToInvoke;
								
								obj=new Object[1];
								obj[0]=TempString;
								ClassToInvoke=TempClassMethod.ReturnValue.getReturnType();
								Const=ClassToInvoke.getConstructor(Param);
								currentFilter.ValueName=Const.newInstance(obj);								
							}
							break;						
						}
					}
				Conditions.add(currentFilter);
				}
			}
			ConditionsToQuery=new QueryCondition[Conditions.size()];
			for(f=0;f<Conditions.size();f++){
				ConditionsToQuery[f]=(QueryCondition)Conditions.get(f);
			}			
			DataToReturn=Blackboard.getBlackboard().Query(Class.forName(ClassToRetrieve),ConditionsToQuery);
			ReturnString+="<?xml version=\"1.0\" ?>\r\n";
			ReturnString+="<Message Type=\"QueryResponse\">\r\n";
			if(DataToReturn.length>0){

				for(f=0;f<DataToReturn.length;f++){
					ClassRegistered=Blackboard.getBlackboard().getClassRegisteredInBlackboard(DataToReturn[f].getClass());
					ReturnString+="<AgentData ClassName=\""+ClassRegistered.ClassInBlackBoard.getName() + "\" Timestamp=\"" + DataToReturn[f].getTimestamp() + "\" >\r\n";
					for(g=0;g<ClassRegistered.Values.size();g++){
						TempMethod=(ClassMethod)ClassRegistered.Values.get(g);												
						
						if(TempMethod.ReturnValue.getReturnType().getName().equals("java.util.Date")){							
							TempDate=(java.util.Date)TempMethod.ReturnValue.invoke(DataToReturn[f],null);
							ValueToReturn="";
							ValueToReturn+=(new Integer(TempDate.getYear()+1900));
							if((TempDate.getMonth()+1)<10){
								ValueToReturn+="0";
							}
							ValueToReturn+=(TempDate.getMonth()+1);
							if(TempDate.getDate()<10){
								ValueToReturn+="0";
							}
							ValueToReturn+=TempDate.getDate();
							
							/*if(TempDate.getHours()<10){
								ValueToReturn+="0";								
							}
							ValueToReturn+=TempDate.getHours();	

							if(TempDate.getMinutes()<10){
								ValueToReturn+="0";								
							}
							ValueToReturn+=TempDate.getMinutes();	

							if(TempDate.getSeconds()<10){
								ValueToReturn+="0";								
							}
							ValueToReturn+=TempDate.getSeconds();*/													
						}else{							
							ValueToReturn=(TempMethod.ReturnValue.invoke(DataToReturn[f],null)).toString();
						}							
						ReturnString+="  <Property Name=\"" + TempMethod.ValueToStore + "\" Value=\"" + ValueToReturn + "\"/>\r\n";
					}
					ReturnString+="</AgentData>\r\n";
				}
			}
		ReturnString+="</Message>\r\n";
		tot=ReturnString.getBytes().length;
		if(SITC) System.out.println(ReturnString);
		theSocket.getOutputStream().write(ReturnString.getBytes());
		System.out.println("Query Resolved...");							
		}catch(Exception e){
			System.out.println(e);
		e.printStackTrace();
		}	
		return "";	
	}
    protected void ProcessQueryTimestampMessage(Document Parameters){
		AgentData                    DataToReturn[];
		int                          f,g;
		boolean                      Found;
		BufferedReader               out;
		Node                         MessageParentNode;		
		NodeList                     ListOfNodes;
		Vector                       Classes=new Vector();
		Vector                       Conditions=new Vector();
		String                       TempString,TempResponseMessage="",ClassToRetrieve=null;
		String                       ReturnString="";
		PostgreSQLQueryCondition     ConditionsToQuery[];
		PostgreSQLFilterCondition    currentFilter;
		PostgreSQLOrderCondition     currentOrder;
		ClassRegisteredInBlackboard  ClassRegistered;
		ClassMethod                  TempMethod;
		String                       ValueToReturn;
		java.util.Date               TempDate;		
		String                       TempFieldName,tempClassName;
		ClassMethod                  TempClassMethod;
		Class                        Param[];
		Integer                      tempCondition=null,tempTimestamp=null,tempMaxAgentData;

		try{
			ListOfNodes=Parameters.getElementsByTagName("Message");
			if(ListOfNodes.getLength()==0) return;
			MessageParentNode=ListOfNodes.item(0);
			ListOfNodes=MessageParentNode.getChildNodes();
			
			Found=false;
			tempClassName=null;
			tempMaxAgentData=new Integer(-1);
			for(f=0;f<ListOfNodes.getLength();f++){
				TempString=ListOfNodes.item(f).getNodeName();
				TempString=TempString.toUpperCase();				
				if(TempString.equals("TIMESTAMP")){				
					TempString=ListOfNodes.item(f).getAttributes().getNamedItem("Value").getNodeValue();
					tempTimestamp=new Integer(TempString);
				}
				if(TempString.equals("CONDITION")){
					TempString=ListOfNodes.item(f).getAttributes().getNamedItem("Type").getNodeValue();
					tempCondition=new Integer(TempString);
				}
				if(TempString.equals("CLASSNAME")){
					TempString=ListOfNodes.item(f).getAttributes().getNamedItem("Name").getNodeValue();
					tempClassName=TempString;
				}
				if(TempString.equals("MAXAGENTDATA")){
					TempString=ListOfNodes.item(f).getAttributes().getNamedItem("Size").getNodeValue();
					tempMaxAgentData=new Integer(TempString);
				}
			}					
			DataToReturn=Blackboard.getBlackboard().QueryTimestamp(tempTimestamp.intValue(),tempCondition.intValue(),tempClassName,tempMaxAgentData.intValue());
			ReturnString+="<?xml version=\"1.0\" ?>\r\n";
			ReturnString+="<Message Type=\"QueryResponse\">\r\n";
			if(DataToReturn.length>0){
				for(f=0;f<DataToReturn.length;f++){
					ClassRegistered=Blackboard.getBlackboard().getClassRegisteredInBlackboard(DataToReturn[f].getClass());
					ReturnString+="<AgentData ClassName=\""+ClassRegistered.ClassInBlackBoard.getName() + "\" Timestamp=\"" + DataToReturn[f].getTimestamp() + "\" >\r\n";
					for(g=0;g<ClassRegistered.Values.size();g++){
						TempMethod=(ClassMethod)ClassRegistered.Values.get(g);												
						
						if(TempMethod.ReturnValue.getReturnType().getName().equals("java.util.Date")){							
							TempDate=(java.util.Date)TempMethod.ReturnValue.invoke(DataToReturn[f],null);
							ValueToReturn="";
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
							ValueToReturn=(TempMethod.ReturnValue.invoke(DataToReturn[f],null)).toString();
						}							
						ReturnString+="  <Property Name=\"" + TempMethod.ValueToStore + "\" Value=\"" + ValueToReturn + "\"/>\r\n";
					}
					ReturnString+="</AgentData>\r\n";
				}
			}
		ReturnString+="</Message>\r\n";
		if(SITC) System.out.println(ReturnString);
		theSocket.getOutputStream().write(ReturnString.getBytes());
		System.out.println("Query Resolved...");				
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
		}	    	
    } 

	protected void ProcessDescriptionMessage(Document Parameters){
		int                    f,g;
		boolean                Found;
		BufferedReader         out;
		Node                   MessageParentNode;		
		NodeList               ListOfNodes;
		Vector                 Classes=new Vector();
		String                 TempString,TempResponseMessage="";
				
		try{
			ListOfNodes=Parameters.getElementsByTagName("Message");
			if(ListOfNodes.getLength()==0) return;
			MessageParentNode=ListOfNodes.item(0);
			ListOfNodes=MessageParentNode.getChildNodes();
			
			Found=false;
			for(f=0;f<ListOfNodes.getLength();f++){
				TempString=ListOfNodes.item(f).getNodeName();
				TempString=TempString.toUpperCase();				
				if(TempString.equals("CLASSNAME")){
					TempString=ListOfNodes.item(f).getAttributes().getNamedItem("Name").getNodeValue();
					Classes.add(TempString);
					Found=true;					
				}
			}
			if(Found){
				
			}else{
				ClassRegisteredInBlackboard FromBlackboard[];
				
				TempResponseMessage+="<?xml version=\"1.0\" ?>\r\n";
				TempResponseMessage+="<Message Type=\"DescribeResponse\">\r\n";
				FromBlackboard=Blackboard.getBlackboard().getClassRegisteredInBlackboard();
				for(f=0;f<FromBlackboard.length;f++){
					TempResponseMessage+="<AgentDataClassDescription>\r\n";
					TempResponseMessage+="   <ClassName ClassInBlackboard=\""+FromBlackboard[f].ClassInBlackBoard.getName()  +"\" ";
					TempResponseMessage+="NameInBlackboard=\""+FromBlackboard[f].NameInBlackBoard +"\"/>\r\n";
					for(g=0;g<FromBlackboard[f].Values.size();g++){
						ClassMethod TempClassMethod;
						
						TempClassMethod=(ClassMethod)FromBlackboard[f].Values.get(g);
						TempResponseMessage+="<Method ";
						TempResponseMessage+=" getMethod=\"" + TempClassMethod.getMethod + "\" ";
						TempResponseMessage+=" PostgreSQLType=\"" + TempClassMethod.getMethod + "\" ";
						TempResponseMessage+=" setMethod=\"" + TempClassMethod.getMethod + "\" ";
						TempResponseMessage+=" ValueToStore=\"" + TempClassMethod.getMethod + "\"/>\r\n";
					}					
					TempResponseMessage+="</AgentDataClassDescription>\r\n";					
				}
				TempResponseMessage+="</Message>\r\n";
				if(SITC) System.out.println(TempResponseMessage);
				theSocket.getOutputStream().write(TempResponseMessage.getBytes()); 
			}			
			
   	    }catch(Exception e){
   	    	System.out.println(e);		
			e.printStackTrace();
	    }
	}

	public void run(){
		byte                   bytesRecebidos[];
		int                    nBytes;
		BufferedReader         in;
		Document               DocReturn;
		DocumentBuilderFactory Factory;
		DocumentBuilder        ConfDocBuilder;
		String                 MessageType,Message="",MessageReceived="",TempStr="";
		String                 ReturnMessage;
		NodeList               ListOfNodes;
		Node                   MessageParentNode;
				
		while(true){
			try{
				MessageReceived="";
				in=new BufferedReader(new InputStreamReader(theSocket.getInputStream()));
				while(true){
				   if((TempStr=in.readLine())!=null){
					   MessageReceived+=TempStr;
					   System.out.println(MessageReceived);
				   }
				   if(MessageReceived.indexOf("</Message>")>-1) break;			
				}
			
				Factory=DocumentBuilderFactory.newInstance();
				Factory.setValidating(true);
				Factory.setNamespaceAware(false);
				ConfDocBuilder=Factory.newDocumentBuilder();
				DocReturn=ConfDocBuilder.parse(new StringBufferInputStream(MessageReceived));
				ListOfNodes=DocReturn.getElementsByTagName("Message");			
				if(ListOfNodes.getLength()==0) continue;			
				MessageParentNode=ListOfNodes.item(0);
				MessageType=MessageParentNode.getAttributes().getNamedItem("Type").getNodeValue();
				MessageType=MessageType.toUpperCase();
				if(MessageType.equals("DESCRIBE")){
				   ProcessDescriptionMessage(DocReturn);
				}
				if(MessageType.equals("QUERY")){
					ProcessQueryMessage(DocReturn);					
				}
				if(MessageType.equals("QUERYTIMESTAMP")){
					ProcessQueryTimestampMessage(DocReturn);					
				}
				MessageReceived="";
								
			}catch(Exception e){
				System.out.println(e);
				e.printStackTrace();
				break;				
			}
		}
		while(true){
			try{
				Thread.sleep(10000);
			}catch(Exception e){
				
			}
		}
	}
	
}