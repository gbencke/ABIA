package org.abia.AgentContainer.AgentContainerClient;

import org.abia.Blackboard.*;
import java.sql.*;
import java.util.*;
import java.net.*;
import java.io.*;
import org.abia.Blackboard.*;
import java.lang.reflect.*;

import org.w3c.dom.*;

import javax.xml.parsers.*;

public class ClientConnection {
	public static int     TIMESTAMP_EXACT=0;
	public static int     TIMESTAMP_GREATER_THAN=1;
	public static int     TIMESTAMP_EXACT_AND_GREATER_THAN=2; 

	protected String      currentHost;
	protected int         currentPort;
	protected Socket      currentSocket;
	protected HashMap     ClassesRegistered=new HashMap();
	protected boolean     SITC=false;

    protected void ResolveClass(String ClassToResolve){
		Object       ExampleInstance;
		AgentData    DataToStore;
		String       cmdRegisterAgent="",TryingToFind,MethodName;
		String       PostgreSQLType="";
		Method       Methods[];
		int          f,g,Total;
		boolean      found;    	    
		Vector       ValuesToStore=new Vector();
		Connection   Con=null;
		Statement    stmt;
		ResultSet    rs;
		Class        ClassesToRegister;		
		ClassRegisteredInBlackboard ClassToStore=new ClassRegisteredInBlackboard();
		ClassMethod                 MethodFound;
    	
		try{
			ClassesToRegister=Class.forName(ClassToResolve);
			ExampleInstance=ClassesToRegister.newInstance();
			if(!(ExampleInstance instanceof AgentData)) throw (new BlackboardException());
			
			DataToStore=(AgentData)ExampleInstance;			
			ClassToStore.ClassInBlackBoard=ClassesToRegister;
			ClassToStore.NameInBlackBoard=DataToStore.getAgentDataNameInBlackboard();			
			
			Methods=DataToStore.getClass().getMethods();
			for(f=0;f<Methods.length;f++){
				if(!Methods[f].getName().startsWith("get")) continue;
				MethodName=Methods[f].getName();				
				found=false;				
				for(g=0;g<Methods.length;g++){
					if(!Methods[g].getName().startsWith("set")) continue;
					if(f!=g && MethodName.substring(3).equals(Methods[g].getName().substring(3))){				    	
						String ReturnType=Methods[f].getReturnType().getName();
						PostgreSQLType=null;
						
						if(ReturnType.equals("java.lang.Integer"))              PostgreSQLType="integer";
						if(ReturnType.equals("java.lang.Boolean"))              PostgreSQLType="boolean";
						if(ReturnType.equals("java.lang.Double"))               PostgreSQLType="double precision";
						if(ReturnType.equals("java.lang.String"))               PostgreSQLType="text";
						if(ReturnType.equals("java.util.Date"))                 PostgreSQLType="timestamp";
						if(ReturnType.equals("java.lang.Long"))                 PostgreSQLType="integer";
						if(ReturnType.equals("org.abia.Blackboard.AgentData"))  PostgreSQLType="integer references timestamp(timestamp) ";						
						
						if(PostgreSQLType!=null){				    	
						   found=true;
						   break;
						}
					}
				}
				if(found){				
				   MethodFound=new ClassMethod();
				   MethodFound.ValueToStore=MethodName.substring(3);
				   MethodFound.PostgreSQLType=PostgreSQLType;
				   MethodFound.getMethod="get"+MethodName.substring(3);
				   MethodFound.setMethod="set"+MethodName.substring(3);
				   MethodFound.ReturnValue=Methods[f];
				   ClassToStore.Values.add(MethodFound);
				}				
			}
			ClassesRegistered.put(ClassesToRegister.getName(),ClassToStore);
						
			cmdRegisterAgent=cmdRegisterAgent+"CREATE TABLE "+DataToStore.getAgentDataNameInBlackboard() +" (timestampval integer references timestamp(timestamp),";
			Total=ClassToStore.Values.size();
			for(f=0;f<Total;f++){
				ClassMethod CurrentMethod;
				
				CurrentMethod=(ClassMethod)(ClassToStore.Values.get(f));
				cmdRegisterAgent=cmdRegisterAgent+"   "+CurrentMethod.ValueToStore;
				cmdRegisterAgent=cmdRegisterAgent+"   "+CurrentMethod.PostgreSQLType;
				if(f==(Total-1)){
				   cmdRegisterAgent=cmdRegisterAgent+")";
				   }else{
				   cmdRegisterAgent=cmdRegisterAgent+",";								
				   }
			}    	
		}catch(Exception e){
			System.out.println(e);			
			e.printStackTrace();
		}
		System.out.println("Class"+ClassToResolve+" Resolved");
    }
	
	public void Initialize() throws AgentContainerClientException {
		int                    f,g;
		String                 TempStr;
		String                 TempString,Message="",MessageReceived="";
		BufferedReader         in;
		Document               DocReturn;
		DocumentBuilderFactory Factory;
		DocumentBuilder        ConfDocBuilder;
		NodeList               ListOfNodes;
		NodeList               ListOfClassDescription;
		NodeList               ListOfChildNode;
		Node                   BlackboardParentNode;

		
		Message=Message+"<?xml version=\"1.0\" ?>\r\n";
		Message=Message+"<Message Type=\"Describe\">\r\n";			
		Message=Message+"</Message>\r\n";
		try{					
		   if(SITC) System.out.println(Message);
		   currentSocket.getOutputStream().write(Message.getBytes());		
		   in=new BufferedReader(new InputStreamReader(currentSocket.getInputStream()));
		   while(true){
		   	  if((TempStr=in.readLine())!=null)
				  MessageReceived+=TempStr;
			  if(MessageReceived.indexOf("</Message>")>-1) break;			
		   }
		   Factory=DocumentBuilderFactory.newInstance();
		   Factory.setValidating(true);
		   Factory.setNamespaceAware(false);
		   ConfDocBuilder=Factory.newDocumentBuilder();
		   DocReturn=ConfDocBuilder.parse(new StringBufferInputStream(MessageReceived));
		   ListOfClassDescription=DocReturn.getElementsByTagName("AgentDataClassDescription");
		   for(f=0;f<ListOfClassDescription.getLength();f++){
			   ListOfChildNode=ListOfClassDescription.item(f).getChildNodes();
			   for(g=0;g<ListOfChildNode.getLength();g++){
				   TempString=ListOfChildNode.item(g).getNodeName();
				   TempString=TempString.toUpperCase();				
				   if(TempString.equals("CLASSNAME")){					  
					  TempStr=ListOfChildNode.item(g).getAttributes().getNamedItem("ClassInBlackboard").getNodeValue();
				   	  ResolveClass(TempStr);
				   }
			   }
		   }
		}catch(Exception e){
			throw new AgentContainerClientException();
		}
		System.out.println("Classes Resolved...");
	}
	
	public ClientConnection(String Host,int Port) 
	       throws AgentContainerClientException {
	       	
	       	try{
				currentSocket=new Socket();
				currentSocket.connect(new InetSocketAddress(Host,Port));	       		
	       	}catch(Exception e){
	       		throw (new AgentContainerClientException());
	       	}	       	
			currentHost=Host;
			currentPort=Port;
			Initialize();		
	}
	
	public PropertyStatus[] QueryContainerStatus(){
		return null;		 
	}
	
	public PropertyStatus[] Describe(){
		return null;
	}
	
	public ClassRegisteredInBlackboard getClassRegisteredInBlackboard(Class  ClassToRetrieve){
		Collection                    col;
		Iterator                      it; 
		ClassRegisteredInBlackboard   TempClass;
		
		col=ClassesRegistered.values();
		it=col.iterator();
		while(it.hasNext()){
			  TempClass=(ClassRegisteredInBlackboard)it.next();
			  if(TempClass.ClassInBlackBoard.getName().toUpperCase().equals(ClassToRetrieve.getName().toUpperCase())){
			  	 return TempClass;			  	
			  }
		}
		return null;	
	}
	public ClassRegisteredInBlackboard getClassRegisteredInBlackboard(String ClassToRetrieve){
		return (ClassRegisteredInBlackboard)ClassesRegistered.get(ClassToRetrieve);		
	}
	public AgentData[] QueryTimestamp(int Timestamp,int TypeOfQuery,String OnlyThisClass){
		return QueryTimestamp(Timestamp,TypeOfQuery,OnlyThisClass,-1); 
	}
	
	public AgentData[] QueryTimestamp(int Timestamp,int TypeOfQuery){
		return QueryTimestamp(Timestamp,TypeOfQuery,null,-1);		
	}
	
	public AgentData[] QueryTimestamp(int Timestamp,int TypeOfQuery,String OnlyThisClass,int MaxAgentData){
		AgentData                  AgentDataToReturn[]=null;
		Connection                 Con;
		Statement                  stmt;
		ResultSet                  rsFromTimestamp=null;
		QueryCondition             Filters[];
		int                    f,g,h;
		String                 RequestMessage="",MessageType="";
		String                 MessageReceived="",TempStr="";
		String                 NumberAgentData,NameClass,TimestampToSet;
		BufferedReader         in;
		Document               DocReturn;
		DocumentBuilderFactory Factory;
		DocumentBuilder        ConfDocBuilder;
		NodeList               ListOfNodes,ListOfChildrenNodes;
		Node                   MessageParentNode;
		Integer                TempInt;
		AgentData              DataToReturn[];
		Method                 MethodFound;
		Class                  TempClass;
		Class                  TempClasses[];
		Object                 TempObjects[];
		ClassRegisteredInBlackboard currentClass;
		ClassMethod            currentClassMethod;
		String                 PropertyName,TempValue,TempNodeName;
		DummyAgentData         TempDummy;
		

		try{
			boolean ret;
			String  cmdQuery="",cmdConstraint="";
			int     ArraySize=0,currentElement=0;		   		   

			RequestMessage+="<?xml version=\"1.0\" ?>\r\n";		
			RequestMessage+="<Message Type=\"QueryTimestamp\">\r\n";
			RequestMessage+="  <Timestamp Value=\""+ Timestamp +"\"/>\r\n";
			RequestMessage+="  <Condition Type=\""+ TypeOfQuery +"\"/>\r\n";
			if(OnlyThisClass!=null){
				RequestMessage+="  <ClassName Name=\""+ OnlyThisClass +"\"/>\r\n";
			}
			if(MaxAgentData>-1){
				RequestMessage+="  <MaxAgentData Size=\""+ MaxAgentData +"\"/>\r\n";			
			}											
			RequestMessage+="</Message>\r\n";



			if(SITC) System.out.println(RequestMessage);
			currentSocket.getOutputStream().write(RequestMessage.getBytes());		
			in=new BufferedReader(new InputStreamReader(currentSocket.getInputStream()));
			while(true){
			   if((TempStr=in.readLine())!=null)
				   MessageReceived+=TempStr;
			   if(MessageReceived.indexOf("</Message>")>-1) break;			
			}
			Factory=DocumentBuilderFactory.newInstance();
			Factory.setValidating(true);
			Factory.setNamespaceAware(false);
			ConfDocBuilder=Factory.newDocumentBuilder();
			DocReturn=ConfDocBuilder.parse(new StringBufferInputStream(MessageReceived));
			ListOfNodes=DocReturn.getElementsByTagName("Message");
			if(ListOfNodes.getLength()==0) return null;			
			ListOfNodes=DocReturn.getElementsByTagName("AgentData");
			DataToReturn=new AgentData[ListOfNodes.getLength()];
			for(f=0;f<ListOfNodes.getLength();f++){
				MessageParentNode=ListOfNodes.item(f);
				NameClass=ListOfNodes.item(f).getAttributes().getNamedItem("ClassName").getNodeValue();				
				DataToReturn[f]=(AgentData)Class.forName(NameClass).newInstance();
				DataToReturn[f].setConnectionToAgentContainer(this);
				TimestampToSet=ListOfNodes.item(f).getAttributes().getNamedItem("Timestamp").getNodeValue();
				DataToReturn[f].SetTimestampManually((new Integer(TimestampToSet)).intValue());
				ListOfChildrenNodes=ListOfNodes.item(f).getChildNodes();				
				for(g=0;g<ListOfChildrenNodes.getLength();g++){
					TempNodeName=ListOfChildrenNodes.item(g).getNodeName().toUpperCase();
					if(TempNodeName.equals("PROPERTY")){
						PropertyName=ListOfChildrenNodes.item(g).getAttributes().getNamedItem("Name").getNodeValue();
						TempValue=ListOfChildrenNodes.item(g).getAttributes().getNamedItem("Value").getNodeValue();
						currentClass=this.getClassRegisteredInBlackboard(NameClass);
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
			System.out.println("Query Resolved...");
			return DataToReturn;
		}catch(Exception e){
			System.out.println(MessageReceived);
			System.out.println(e);
			e.printStackTrace();
		}
		return null;
	}
	public AgentData[] Query(Class ClassToRetrieve,QueryCondition Conditions[]) throws BlackboardException {
		int                    f,g,h;
		String                 RequestMessage="",MessageType="";
		String                 MessageReceived="",TempStr="";
		String                 NumberAgentData,NameClass,TimestampToSet;
		BufferedReader         in;
		Document               DocReturn;
		DocumentBuilderFactory Factory;
		DocumentBuilder        ConfDocBuilder;
		NodeList               ListOfNodes,ListOfChildrenNodes;
		Node                   MessageParentNode;
		Integer                TempInt;
		AgentData              DataToReturn[];
		Method                 MethodFound;
		Class                  TempClass;
		Class                  TempClasses[];
		Object                 TempObjects[];
		ClassRegisteredInBlackboard currentClass;
		ClassMethod            currentClassMethod;
		String                 PropertyName,TempValue,TempNodeName;
		DummyAgentData         TempDummy;
		 
				
		RequestMessage+="<?xml version=\"1.0\" ?>\r\n";		
		RequestMessage+="<Message Type=\"Query\">\r\n";
		RequestMessage+="<ClassName Name=\"" + ClassToRetrieve.getName() +"\"/>\r\n";
		if(Conditions!=null){
			for(f=0;f<Conditions.length;f++){
				if(Conditions[f] instanceof FilterCondition){
			   		FilterCondition TempCondition;
			   	
				   TempCondition=(FilterCondition)Conditions[f];			   
				   RequestMessage+="<Condition Fieldname=\""+ TempCondition.FieldName +"\" Operation=\""+ TempCondition.Operator  +"\" ValueName=\"" +  TempCondition.ValueName + "\"/>";
				}
				if(Conditions[f] instanceof OrderCondition){
					OrderCondition TempCondition;
			   	
					TempCondition=(OrderCondition)Conditions[f];			   
					RequestMessage+="<Order Fieldname=\""+ TempCondition.Field +"\" Order=\""+ TempCondition.Order + "\"/>";				
				}			
			}
		}
		RequestMessage+="</Message>\r\n";
		try{		
			//if(SITC) System.out.println(Message);
		    currentSocket.getOutputStream().write(RequestMessage.getBytes());
			in=new BufferedReader(new InputStreamReader(currentSocket.getInputStream()));
			while(true){
			   if((TempStr=in.readLine())!=null)
				   MessageReceived+=TempStr;
			   if(MessageReceived.indexOf("</Message>")>-1) break;
			   			
			}
			Factory=DocumentBuilderFactory.newInstance();
			Factory.setValidating(true);
			Factory.setNamespaceAware(false);
			ConfDocBuilder=Factory.newDocumentBuilder();
			DocReturn=ConfDocBuilder.parse(new StringBufferInputStream(MessageReceived));
			ListOfNodes=DocReturn.getElementsByTagName("Message");
			if(ListOfNodes.getLength()==0) return null;			
			ListOfNodes=DocReturn.getElementsByTagName("AgentData");
			DataToReturn=new AgentData[ListOfNodes.getLength()];
			for(f=0;f<ListOfNodes.getLength();f++){
				MessageParentNode=ListOfNodes.item(f);
				NameClass=ListOfNodes.item(f).getAttributes().getNamedItem("ClassName").getNodeValue();				
				DataToReturn[f]=(AgentData)Class.forName(NameClass).newInstance();
				DataToReturn[f].setConnectionToAgentContainer(this);
				TimestampToSet=ListOfNodes.item(f).getAttributes().getNamedItem("Timestamp").getNodeValue();
				DataToReturn[f].SetTimestampManually((new Integer(TimestampToSet)).intValue());
				ListOfChildrenNodes=ListOfNodes.item(f).getChildNodes();				
				for(g=0;g<ListOfChildrenNodes.getLength();g++){
					TempNodeName=ListOfChildrenNodes.item(g).getNodeName().toUpperCase();
					if(TempNodeName.equals("PROPERTY")){
						PropertyName=ListOfChildrenNodes.item(g).getAttributes().getNamedItem("Name").getNodeValue();
						TempValue=ListOfChildrenNodes.item(g).getAttributes().getNamedItem("Value").getNodeValue();
						currentClass=this.getClassRegisteredInBlackboard(NameClass);
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
							   		TempObjects[0]=new java.util.Date();
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
			System.out.println("Query Resolved...");
			return DataToReturn;
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
		}
		return null;
	}

	public void        ResolveReferences(AgentData InstanceToResolve) throws BlackboardException {
		ClassRegisteredInBlackboard TargetClass;
		int                         f;
		Object                      Methods[];
		ClassMethod                 currentMethod;
		Class                      TempClasses[];		   
		   
		TargetClass=(ClassRegisteredInBlackboard)ClassesRegistered.get(InstanceToResolve.getClass().getName());
		Methods=TargetClass.Values.toArray();
		try{
		   for(f=0;f<Methods.length;f++){
 
			   currentMethod=(ClassMethod)Methods[f];
			   if(currentMethod.ReturnValue.getReturnType().getName().equals("org.abia.Blackboard.AgentData")){
				  Method     MethodToInvoke;
				  AgentData  TimestampToResolve,ClassesFromBlackboard[],ClassFromBlackboard=null;
				     
					 
				  MethodToInvoke=InstanceToResolve.getClass().getMethod(currentMethod.getMethod,null);
				  TimestampToResolve=(AgentData)MethodToInvoke.invoke(InstanceToResolve,null);
				  ClassesFromBlackboard=this.QueryTimestamp(TimestampToResolve.getTimestamp().intValue(),Blackboard.TIMESTAMP_EXACT);

				  TempClasses=new Class[1];
				  TempClasses[0]=Class.forName("org.abia.Blackboard.AgentData");
				  MethodToInvoke=InstanceToResolve.getClass().getMethod(currentMethod.setMethod,TempClasses);					  
				  MethodToInvoke.invoke(InstanceToResolve,ClassesFromBlackboard);				     			   	
			   }
		   }
		}catch(Exception e){
		   System.out.println(e);
		   e.printStackTrace();
		}
	}
	
	
}