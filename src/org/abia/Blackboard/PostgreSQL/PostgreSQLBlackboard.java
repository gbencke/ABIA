package org.abia.Blackboard.PostgreSQL;

import org.abia.Blackboard.*;
import org.abia.AgentContainer.*;
import java.sql.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.*;

public class PostgreSQLBlackboard extends Blackboard {

    class ElementTransaction {
    	public  boolean   HasDependents;
    	public  AgentData DataToStore;
    	public  int       currentTimestamp;
    	
    	public  ElementTransaction(AgentData currentData){
    		DataToStore=currentData;
    		HasDependents=false;    	
    	}
    	
    	public String toString(){
    		try{
				return Store(DataToStore.getClass(),DataToStore,true);
    		}catch(Exception e){
    			return "";
    		}    		
    	}
    }

    class Transacao {
    	public ElementTransaction            Elements[];
	
    	public Transacao(AgentData DataInTransaction[]){
    		int f,g,h;
			ClassRegisteredInBlackboard   ClassToResolve;
			ClassMethod                   MethodsToResolve[];
			AgentData                     DataToCompare;
    		
    		Elements=new ElementTransaction[DataInTransaction.length];
    		for(f=0;f<DataInTransaction.length;f++){
    			Elements[f]=new ElementTransaction(DataInTransaction[f]);    			
    		}
			for(f=0;f<Elements.length;f++){
				for(g=0;g<Elements.length;g++){
					if(Elements[f]==Elements[g]) continue;
					ClassToResolve=getClassRegisteredInBlackboard(Elements[g].DataToStore.getClass());
					MethodsToResolve=new ClassMethod[ClassToResolve.Values.size()];
					for(h=0;h<ClassToResolve.Values.size();h++){
					        MethodsToResolve[h]=(ClassMethod)ClassToResolve.Values.get(h);
					}
					for(h=0;h<MethodsToResolve.length;h++){
						if(MethodsToResolve[h].ReturnValue.getReturnType().getName().equals("org.abia.Blackboard.AgentData")){
							try{
							    DataToCompare=(AgentData)MethodsToResolve[h].ReturnValue.invoke(Elements[g].DataToStore,null);
							    if(DataToCompare==Elements[f].DataToStore){
/*							    	System.out.println("The AgentData:"+Elements[f].DataToStore.getClass().getName()+
									                   " is used in:"+Elements[g].DataToStore.getClass().getName());*/
									Elements[f].HasDependents=true;
							    }
							}catch(Exception e){
								System.out.println(e);
								e.printStackTrace();
							}
						}						
					}										
				}    			
			}	
    	}
    	
    	public String toString(){
			String StringToReturn="";
			int    f;
			DataOutputStream   out2;
						
			StringToReturn="BEGIN;\r\n";
			for(f=0;f<Elements.length;f++){
				if(Elements[f].HasDependents){
					StringToReturn+=Elements[f];
				}
			}
			for(f=0;f<Elements.length;f++){
				if(!Elements[f].HasDependents){
					StringToReturn+=Elements[f];
				}
			}
			StringToReturn+="COMMIT;\r\n";
			
			//System.out.println(StringToReturn);
			try{			
			   out2=new DataOutputStream(new BufferedOutputStream(new FileOutputStream("C:\\teste3311.txt")));
			   out2.write(StringToReturn.getBytes());
			   out2.close();
			}catch(Exception e){
				
			}
					
    		return StringToReturn;
    	}
    }

	protected int         NumberConnections;
	protected boolean     SITC=false;
	protected int         NumberConnectionsInPool;
	protected Connection  PostgreSQLConnection[];
	protected Boolean     ConnectionInUse[];	
	protected HashMap     ClassesRegistered=new HashMap();
		
	public ClassRegisteredInBlackboard[] getClassRegisteredInBlackboard(){
		int                           f,size;
		ClassRegisteredInBlackboard   Retorno[];
		Iterator                      it;
		
		size=ClassesRegistered.values().size();
		Retorno=new ClassRegisteredInBlackboard[size];
		
		it=ClassesRegistered.values().iterator();f=0;
		while(it.hasNext()){
			Retorno[f]=(ClassRegisteredInBlackboard)it.next();
			f++;			
		}
		return Retorno;
	}	
		
	public ClassRegisteredInBlackboard getClassRegisteredInBlackboard(Class  ClassToRetrieve){
	       return (ClassRegisteredInBlackboard)ClassesRegistered.get(ClassToRetrieve.getName());	
	}
	public ClassRegisteredInBlackboard getClassRegisteredInBlackboard(String ClassToRetrieve){
           try{           
               return getClassRegisteredInBlackboard(Class.forName(ClassToRetrieve));
           }catch(ClassNotFoundException e){
           	   return null;
           }
	}		
		
	public int getNextTimestamp(){
		Connection Con;
		Statement  stmt;
		ResultSet  rs;

		while(true){
			  Con=this.getConnection();
			  if(Con==null){
				 try{
					Thread.sleep(0);
				 }catch(Exception e){}	             
			  }else
				 break;	    
		}		
		try{		
		   stmt=Con.createStatement();
		   rs=stmt.executeQuery("SELECT nextval('seq_timestamp')");
		   if(rs.next()){
			  this.releaseConnection(Con);
		      return rs.getInt(1);
		   }
		}catch(Exception e){
					   
		}
		this.releaseConnection(Con);
		return -1;		
	}


	protected synchronized void releaseConnection(Connection ConToRelease){
		int f;
    	
		for(f=0;f<NumberConnectionsInPool;f++){
			if(PostgreSQLConnection[f]==ConToRelease){
				ConnectionInUse[f]=new Boolean(false);
				break;
			}
		}
	}

    protected synchronized Connection getConnection(){
    	int f;
    	
    	for(f=0;f<NumberConnectionsInPool;f++){
    		if(ConnectionInUse[f].booleanValue()==false){
				ConnectionInUse[f]=new Boolean(true);
    			return PostgreSQLConnection[f];
    		}
    	}
    	return null;    	
    }
    
	public AgentData[] Query(Class ClassToRetrieve,QueryCondition Conditions[]) throws BlackboardException {
		Connection                   Con=null;
		Statement                    stmt;
		ResultSet                    rs;
		ResultSetMetaData            rsMeta;
		String                       TableName="";
		String                       cmdQuery,cmdCondition;
		ClassRegisteredInBlackboard  TargetClass;
		Vector                       TempVector=new Vector();
		int                          f,g,Total;
		ClassMethod                  CurrentMethod=null;
		Object                       TempAgentData;
		AgentData                    TempArrayAgentData[];
		
		TargetClass=(ClassRegisteredInBlackboard)ClassesRegistered.get(ClassToRetrieve.getName());
		TableName=TargetClass.NameInBlackBoard;
		
		cmdCondition=PostgreSQLQueryCondition.GenerateString(ClassToRetrieve,Conditions);
		cmdQuery="Select * from "+TableName+" "+cmdCondition;				
		
		while(true){
			  Con=this.getConnection();
			  if(Con==null){
				 try{
					Thread.sleep(0);
				 }catch(Exception e){}	             
			  }else
				 break;	    
		}
		
		try{
			stmt=Con.createStatement();
			rs=stmt.executeQuery(cmdQuery);
			rsMeta=rs.getMetaData();
			while(rs.next()){
				TempAgentData=TargetClass.ClassInBlackBoard.newInstance();
				((AgentData)TempAgentData).RetrieveTimestamp(rs.getInt(1));
				for(f=1;f<rsMeta.getColumnCount();f++){
					Total=TargetClass.Values.size();
					for(g=0;g<Total;g++){						   
						CurrentMethod=(ClassMethod)TargetClass.Values.get(g);
						if(rsMeta.getColumnName(f+1).toUpperCase().equals(
						   CurrentMethod.ValueToStore.toUpperCase())) break;						   	
					}
					
					if(g<Total){
						Method MethodFound;
						Class  TempClass=CurrentMethod.ReturnValue.getReturnType();
						Class  TempClasses[];
						Object TempObjects[];
						
						DummyAgentData TempDummy; 						
						
						TempClasses=new Class[1];
						TempClasses[0]=TempClass;
						
						MethodFound=TempAgentData.getClass().getMethod("set"+CurrentMethod.ValueToStore,TempClasses);
						TempObjects=new Object[1];
						
						if(CurrentMethod.ReturnValue.getReturnType()==Class.forName("org.abia.Blackboard.AgentData")){						
						   TempDummy=new DummyAgentData(rs.getInt(f+1)); 
						   
						   TempObjects[0]=TempDummy;						   						   
						   MethodFound.invoke(TempAgentData,TempObjects); 					
						}
						if(CurrentMethod.ReturnValue.getReturnType()==Class.forName("java.util.Date")){								
						   TempObjects[0]=rs.getDate(f+1);
						   MethodFound.invoke(TempAgentData,TempObjects);   								   	
						}
						if(CurrentMethod.ReturnValue.getReturnType()==Class.forName("java.lang.Long")){								
						   TempObjects[0]=new Long(rs.getLong(f+1));
						   MethodFound.invoke(TempAgentData,TempObjects);   								   	
						}
						if(CurrentMethod.ReturnValue.getReturnType()==Class.forName("java.lang.String")){								
						   TempObjects[0]=new String(rs.getString(f+1));
						   MethodFound.invoke(TempAgentData,TempObjects);   								   	
						}
						if(CurrentMethod.ReturnValue.getReturnType()==Class.forName("java.lang.Integer")){								
						   TempObjects[0]=new Integer(rs.getInt(f+1));
						   MethodFound.invoke(TempAgentData,TempObjects);   								   	
						}
						if(CurrentMethod.ReturnValue.getReturnType()==Class.forName("java.lang.Boolean")){								
						   TempObjects[0]=new Boolean(rs.getBoolean(f+1));
						   MethodFound.invoke(TempAgentData,TempObjects);   								   	
						}
						if(CurrentMethod.ReturnValue.getReturnType()==Class.forName("java.lang.Double")){								
						   TempObjects[0]=new Double(rs.getDouble(f+1));						  
						   MethodFound.invoke(TempAgentData,TempObjects);   								   	
						}
						
						
					}
				}
				TempVector.add(TempAgentData);									
			}
			
			
		}catch(Exception e){
			releaseConnection(Con);
			System.out.println(e);
			return null;
		}
		
		releaseConnection(Con);
		TempArrayAgentData=new AgentData[TempVector.size()];
		for(f=0;f<TempArrayAgentData.length;f++){
			TempArrayAgentData[f]= (AgentData)TempVector.get(f);			
		}
		return TempArrayAgentData;		
	}
	
	public void Store(Class Origin, AgentData ClassToStore) throws BlackboardException {
		   Store(Origin,ClassToStore,false);
	}
	
	public String Store(Class Origin, AgentData ClassToStore,boolean JustGenerateString) throws BlackboardException {
		Class   ClassToStoreFound;
		Object  FromHash;
		String  cmdStore,TempReturned;
		int     f,Total;
		Connection Con=null;
		Statement  stmt;
		ResultSet  rs;
				
		ClassRegisteredInBlackboard Desc;
		ClassMethod                 Meth;
		
		FromHash=ClassesRegistered.get(ClassToStore.getClass().getName());
		if(FromHash==null) throw (new BlackboardException());
		
		Desc=(ClassRegisteredInBlackboard)FromHash;
		ClassToStore.setOrigin(Origin.getName());
		
		if(ClassToStore.getTimestamp().intValue()==-1){
			ClassToStore.GenerateTimestamp();		
		}
		cmdStore="";
		if(!JustGenerateString) cmdStore=cmdStore+"BEGIN;\r\n";
		cmdStore=cmdStore+"insert into timestamp (timestamp,agentname,agentdata,agentdatatable) values ("+ClassToStore.getTimestamp();
        cmdStore=cmdStore+",'"+Origin.getName();
        cmdStore=cmdStore+"','"+ ClassToStore.getClass().getName();
		cmdStore=cmdStore+"','"+ ClassToStore.getAgentDataNameInBlackboard();
        cmdStore=cmdStore+"');\r\n";
		
		cmdStore=cmdStore+"insert into "+ Desc.NameInBlackBoard + "(timestampval,";
		Total=Desc.Values.size();
		for(f=0;f<Total;f++){
			Meth=(ClassMethod)Desc.Values.get(f);
			if(f==(Total-1)){
			   cmdStore=cmdStore+Meth.ValueToStore+")";
			}else{
			   cmdStore=cmdStore+Meth.ValueToStore+",";
			}			   			
		}
		cmdStore=cmdStore+" values ("+ClassToStore.getTimestamp()+",";		
		for(f=0;f<Total;f++){
			Meth=(ClassMethod)Desc.Values.get(f);
			TempReturned=Meth.ReturnValue.getReturnType().getName();		

			if(TempReturned.equals("java.lang.Integer")){
				Integer i;
				Method  MethodFound;

				try{				
				   MethodFound=ClassToStore.getClass().getMethod(Meth.getMethod,null);
				   i=(Integer)MethodFound.invoke(ClassToStore,null); 
				   cmdStore=cmdStore+i;
				}catch(Exception e){
					System.out.println(e);					
					e.printStackTrace();
				}												
			}
			if(TempReturned.equals("java.lang.Boolean")){
				Boolean b;
				Method  MethodFound;
				
				try{				
				   MethodFound=ClassToStore.getClass().getMethod(Meth.getMethod,null);
				   b=(Boolean)MethodFound.invoke(ClassToStore,null); 
				   cmdStore=cmdStore+b;
				}catch(Exception e){
					System.out.println(e);					
					e.printStackTrace();
				}
			}
			if(TempReturned.equals("java.lang.Double")){
				Double d;
				Method  MethodFound;

				try{				
				   MethodFound=ClassToStore.getClass().getMethod(Meth.getMethod,null);
				   d=(Double)MethodFound.invoke(ClassToStore,null); 
				   cmdStore=cmdStore+d;
				}catch(Exception e){
					System.out.println(e);					
					e.printStackTrace();
				}				
			}
			if(TempReturned.equals("java.lang.String")){
				String s;
				Method  MethodFound;

				try{				
				   MethodFound=ClassToStore.getClass().getMethod(Meth.getMethod,null);
				   s=(String)MethodFound.invoke(ClassToStore,null); 
				   cmdStore=cmdStore+"'"+s+"'";
				}catch(Exception e){
					System.out.println(e);					
					e.printStackTrace();
				}				
			}
			if(TempReturned.equals("java.util.Date")){
				java.util.Date   da;
				Method           MethodFound;				
				String           d1,d2,d3,d4,d5,d6,d7;

				try{				
				   MethodFound=ClassToStore.getClass().getMethod(Meth.getMethod,null);
				   da=(java.util.Date)MethodFound.invoke(ClassToStore,null); 
				   
				   d1=(new Integer(da.getYear()+1900)).toString();
				   d2=(new Integer(da.getMonth()+1)).toString();
				   d3=(new Integer(da.getDate())).toString();
				   
				   d4=(new Integer(da.getHours())).toString();
				   d5=(new Integer(da.getMinutes())).toString();
				   d6=(new Integer(da.getSeconds())).toString();
				   
				   if(d2.length()==1) d2="0"+d2;
				   if(d3.length()==1) d3="0"+d3;
				   if(d4.length()==1) d4="0"+d4;				   
				   if(d5.length()==1) d5="0"+d5;
				   if(d6.length()==1) d6="0"+d6;
				   
				   cmdStore=cmdStore+"'"+d1+"-";
				   cmdStore=cmdStore+d2+"-";
				   cmdStore=cmdStore+d3+" ";
				   cmdStore=cmdStore+d4+":";
				   cmdStore=cmdStore+d5+":";
				   cmdStore=cmdStore+d6+"'";
				}catch(Exception e){
					System.out.println(e);					
					e.printStackTrace();
				}								
			}
			if(TempReturned.equals("java.lang.Long")){
				Long l;
				Method  MethodFound;

				try{				
				   MethodFound=ClassToStore.getClass().getMethod(Meth.getMethod,null);
				   l=(Long)MethodFound.invoke(ClassToStore,null); 
				   cmdStore=cmdStore+l;
				}catch(Exception e){
					System.out.println(e);					
					e.printStackTrace();
				}				
			}
			if(TempReturned.equals("org.abia.Blackboard.AgentData")){
				AgentData a;
				Method  MethodFound;

            
				try{				
				   MethodFound=ClassToStore.getClass().getMethod(Meth.getMethod,null);
				   a=(AgentData)MethodFound.invoke(ClassToStore,null); 
				   if(a==null){
					  cmdStore=cmdStore+"null";
				   }else{
					  cmdStore=cmdStore+a.getTimestamp();
				   }
				}catch(Exception e){
					System.out.println(e);					
					e.printStackTrace();
				}								
			}
			
			if(f==(Total-1)){
			   cmdStore=cmdStore+");\r\n";
			}else{
			   cmdStore=cmdStore+",";
			}			   			
		}
		if(JustGenerateString) return cmdStore; 
		
		cmdStore=cmdStore+"COMMIT;\r\n";		
		while(true){
			  Con=this.getConnection();
			  if(Con==null){
				 try{
					Thread.sleep(0);
				 }catch(Exception e){}	             
			  }else
				 break;	    
		}
		
		try{			
			stmt=Con.createStatement();
			stmt.execute(cmdStore);
			releaseConnection(Con);
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
		}
	
	    return "";	
	}
    

    public void RegisterAgentData(Class ClassesToRegister) throws BlackboardException{
    	Object    ExampleInstance;
    	AgentData DataToStore;
    	String    cmdRegisterAgent="",TryingToFind,MethodName;
		String    PostgreSQLType="";
    	Method    Methods[];
    	int       f,g,Total;
    	boolean   found;    	    
    	Vector    ValuesToStore=new Vector();
		Connection Con=null;
		Statement  stmt;
		ResultSet  rs;
		    	
    	ClassRegisteredInBlackboard ClassToStore=new ClassRegisteredInBlackboard();
    	ClassMethod                 MethodFound;
    	
		try{
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
			
			while(true){
				  Con=this.getConnection();
				  if(Con==null){
					 try{
						Thread.sleep(0);
					 }catch(Exception e){}	             
				  }else
					 break;	    
			}
			
			stmt=Con.createStatement();
			rs=stmt.executeQuery("SELECT COUNT(*) from pg_class where UPPER(relname)=UPPER('"+ ClassToStore.NameInBlackBoard +"')");
			if(rs.next()){
			   if(rs.getInt(1)==0){ // Need to create this table
				  if(SITC) System.out.println(cmdRegisterAgent);
				  stmt.execute(cmdRegisterAgent);			   	
			   }
			}
			
			stmt=Con.createStatement();			
			rs=stmt.executeQuery("SELECT COUNT(*) from agentdata where UPPER(agentdataname)=UPPER('"+ DataToStore.getAgentDataNameInBlackboard() +"')");
			if(rs.next()){
			   if(rs.getInt(1)==0){ // Need to create this table
			  	  stmt.execute("insert into agentdata (agentdataname,agentdataclass) values ('"+DataToStore.getAgentDataNameInBlackboard()+"','"+ DataToStore.getClass().getName() +"')");			   	
			   }
			}
			
		}catch(Exception e){
			System.out.println(e);			
			e.printStackTrace();
		}
		releaseConnection(Con);
    }

	public void RegisterAgentData(Class[] ClassesToRegister) throws BlackboardException {
		int f;
		
		for(f=0;f<ClassesToRegister.length;f++){
			RegisterAgentData(ClassesToRegister[f]);					
		}
		f=0;
	}

	public void RegisterAgent(Agent AgentToRegister) throws BlackboardException {
		Connection Con;
		Statement  stmt;
		ResultSet  rs;
		
		while(true){
			  Con=this.getConnection();
			  if(Con==null){
				 try{
					Thread.sleep(0);
				 }catch(Exception e){}	             
			  }else
				 break;	    
		}
		
		try{		
		    String cmdAgentToRegister="";
		    boolean ret;
		    
			stmt=Con.createStatement();			
			rs=stmt.executeQuery("SELECT COUNT(*) from AGENTS where UPPER(AGENTNAME)=UPPER('"+ AgentToRegister.getAgentNameInBlackboard() +"')");
			if(rs.next()){
			   if(rs.getInt(1)==0){ // Need to create this table
				  cmdAgentToRegister=cmdAgentToRegister+"INSERT INTO AGENTS (IDAGENT,AGENTNAME,AGENTCLASS) ";
				  cmdAgentToRegister=cmdAgentToRegister+" values (nextval('seq_agents'),'";
				  cmdAgentToRegister=cmdAgentToRegister+AgentToRegister.getAgentNameInBlackboard() + "','" + AgentToRegister.getClass().getName() + "')";
				  ret=stmt.execute(cmdAgentToRegister);							    
			   }
			}		   		    
		}catch(Exception e){
			System.out.println(e);			
			e.printStackTrace();
		}
		releaseConnection(Con);
	}
	
	public void CheckDatabase()	throws BlackboardException {
		Connection Con;
		Statement  stmt;
		ResultSet  rs;

	    while(true){
	          Con=this.getConnection();
	          if(Con==null){
	             try{
					Thread.sleep(0);
	             }catch(Exception e){}	             
	          }else
			     break;	    
	    }
	    
		try{
		   boolean ret;
		   String  cmdCreateTable="";		   		   
		   stmt=Con.createStatement();
		   		   
		   rs=stmt.executeQuery("SELECT COUNT(*) from pg_class where UPPER(relname)='SEQ_TIMESTAMP'");
		   if(rs.next()){
			  if(rs.getInt(1)==0){ // Need to create this table
				ret=stmt.execute("CREATE SEQUENCE seq_Timestamp");							     
			  }
		   }		   

		   rs=stmt.executeQuery("SELECT COUNT(*) from pg_class where UPPER(relname)='SEQ_AGENTS'");
		   if(rs.next()){
			  if(rs.getInt(1)==0){ // Need to create this table
				ret=stmt.execute("CREATE SEQUENCE seq_Agents");							     
			  }
		   }		   

		   rs=stmt.executeQuery("SELECT COUNT(*) from pg_class where upper(relname)='AGENTDATA'");
		   if(rs.next()){
			  if(rs.getInt(1)==0){ // Need to create this table
				cmdCreateTable="";
				cmdCreateTable=cmdCreateTable+"CREATE TABLE AGENTDATA (";
				cmdCreateTable=cmdCreateTable+"AGENTDATANAME  VARCHAR(100),";
				cmdCreateTable=cmdCreateTable+"AGENTDATACLASS VARCHAR(1000))";
				cmdCreateTable=cmdCreateTable+"";				
				ret=stmt.execute(cmdCreateTable);							     
			  }
		   }

		   rs=stmt.executeQuery("SELECT COUNT(*) from pg_class where upper(relname)='AGENTS'");
		   if(rs.next()){
		      if(rs.getInt(1)==0){ // Need to create this table
				cmdCreateTable="";
				cmdCreateTable=cmdCreateTable+"CREATE TABLE AGENTS (";
				cmdCreateTable=cmdCreateTable+"IDAGENT    INTEGER,";
				cmdCreateTable=cmdCreateTable+"AGENTNAME  VARCHAR(1000),";
				cmdCreateTable=cmdCreateTable+"AGENTCLASS VARCHAR(1000))";				
				ret=stmt.execute(cmdCreateTable);							     
		      }
		   }
		   
		   rs=stmt.executeQuery("SELECT COUNT(*) from pg_class where upper(relname)='TIMESTAMP'");
		   if(rs.next()){
			  if(rs.getInt(1)==0){ // Need to create this table
				cmdCreateTable="";
				cmdCreateTable=cmdCreateTable+"CREATE TABLE TIMESTAMP (";
				cmdCreateTable=cmdCreateTable+"   Timestamp   integer unique,";
				cmdCreateTable=cmdCreateTable+"   AGENTNAME   varchar(1000),";
				cmdCreateTable=cmdCreateTable+"   AGENTDATA   varchar(1000),";
				cmdCreateTable=cmdCreateTable+"   AGENTDATATABLE   varchar(1000))";
				ret=stmt.execute(cmdCreateTable);							     
			  }
		   }		   		   
		}catch(Exception e){
			System.out.println(e);			
			e.printStackTrace();
		}
		releaseConnection(Con);		
	}

	public void Configure() throws BlackboardException {		
		String Temp1,TempHost,TempUser,TempPassword,TempDatabase,ConnectionString;
		Object TempObj;
		int f;
		
		TempObj=Blackboard.BlackboardParameters.get("PoolConnection");
		if(TempObj==null) 		  
		   throw new BlackboardException();
		   else
		   Temp1=TempObj.toString();			
		NumberConnectionsInPool=(new Integer(Temp1)).intValue();
		
		PostgreSQLConnection=new Connection[NumberConnectionsInPool];
		ConnectionInUse     =new Boolean   [NumberConnectionsInPool];
		
		TempObj=Blackboard.BlackboardParameters.get("PostgreSQLHost");
		if(TempObj==null) 		  
		   throw new BlackboardException();
		   else
		   Temp1=TempObj.toString();

		TempObj=Blackboard.BlackboardParameters.get("PostgreSQLHost");
		if(TempObj==null) 		  
		   throw new BlackboardException();
		   else
		   TempHost=TempObj.toString();
		  
		TempObj=Blackboard.BlackboardParameters.get("UserName");
		if(TempObj==null) 		  
		   throw new BlackboardException();
		   else
		   TempUser=TempObj.toString();

		TempObj=Blackboard.BlackboardParameters.get("Password");
		if(TempObj==null) 		  
		   throw new BlackboardException();
		   else
		   TempPassword=TempObj.toString();

		TempObj=Blackboard.BlackboardParameters.get("Database");
		if(TempObj==null) 		  
		   throw new BlackboardException();
		   else
		   TempDatabase=TempObj.toString();

		 
		ConnectionString="jdbc:postgresql://"+TempHost+"/"+TempDatabase;
		
		try {
			Class.forName("org.postgresql.Driver");
		}catch(Exception e){
			throw new BlackboardException();        		
		}
		
		try {
		    for(f=0;f<NumberConnectionsInPool;f++){
			    PostgreSQLConnection[f]=DriverManager.getConnection(ConnectionString,TempUser,"");
			    ConnectionInUse[f]=new Boolean(false);						
		    }
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
			throw new BlackboardException();			
		}
		
		CheckDatabase();
	}

	public AgentData[] QueryTimestamp(int Timestamp,int TypeOfQuery,String OnlyThisClass){
		   return QueryTimestamp(Timestamp,TypeOfQuery,OnlyThisClass);
	}
  
	public AgentData[] QueryTimestamp(int Timestamp,int TypeOfQuery){
		   return QueryTimestamp(Timestamp,TypeOfQuery,null,-1);		
	}
  
  
    public AgentData[] QueryTimestamp(int Timestamp,int TypeOfQuery,String OnlyThisClass,int MaxAgentData){
		AgentData                  AgentDataToReturn[]=null;
		Connection                 Con;
		Statement                  stmt;
		ResultSet                  rsFromTimestamp=null;
		PostgreSQLFilterCondition  Filters[];

		while(true){
			  Con=this.getConnection();
			  if(Con==null){
				 try{
					Thread.sleep(0);
				 }catch(Exception e){}	             
			  }else
				 break;	    
		}
		
		try{
			boolean ret;
			String  cmdQuery="",cmdConstraint="";
			int     ArraySize=0,currentElement=0;		   		   

		   		   
			stmt=Con.createStatement();			
			if(TypeOfQuery==Blackboard.TIMESTAMP_EXACT)
				cmdConstraint="where timestamp="+Timestamp;
			else
			if(TypeOfQuery==Blackboard.TIMESTAMP_EXACT_AND_GREATER_THAN)
				cmdConstraint="where timestamp>="+Timestamp;
			else 
			if(TypeOfQuery==Blackboard.TIMESTAMP_GREATER_THAN)
				cmdConstraint="where timestamp>"+Timestamp;
	
			if(OnlyThisClass!=null){
				cmdConstraint+=" and agentdata='" + OnlyThisClass + "'";
			}
			
					
			rsFromTimestamp=stmt.executeQuery("SELECT count(*) from timestamp "+cmdConstraint);
			
			while(rsFromTimestamp.next()){
				ArraySize=rsFromTimestamp.getInt(1);
			}			
			if(MaxAgentData>-1){
			   if(ArraySize>MaxAgentData)
			      ArraySize=MaxAgentData;			     			      
			}

			AgentDataToReturn=new AgentData[ArraySize]; 
			
			cmdConstraint+=" order by timestamp asc";		    
			rsFromTimestamp=stmt.executeQuery("SELECT * from timestamp "+cmdConstraint);		    
		    while(rsFromTimestamp.next()){
				  Filters=new PostgreSQLFilterCondition[1];
				  Filters[0]=new PostgreSQLFilterCondition();
				  Filters[0].FieldName="timestamp";
				  Filters[0].Operator =PostgreSQLQueryCondition.EQUALS ;
				  Filters[0].ValueName=new Integer(rsFromTimestamp.getInt(1));
				  if(currentElement==AgentDataToReturn.length) break;
				  AgentDataToReturn[currentElement]=(AgentData)(this.Query(Class.forName(rsFromTimestamp.getString(3)),Filters))[0];
				  currentElement++;
				  if(MaxAgentData>-1){
				     if(currentElement==MaxAgentData) break;
				  }
		    }
		}catch(Exception e){
			System.out.println(e);			
			e.printStackTrace();
		}

        releaseConnection(Con);
    	return AgentDataToReturn;
    }

	public void ResolveReferences(AgentData InstanceToResolve) throws BlackboardException  {
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

	public void Store(AgentData ClassToStore[]) throws BlackboardException {
		Transacao   currentTransaction;
		Connection  Con;
		Statement   stmt;

		currentTransaction=new Transacao(ClassToStore);

		while(true){
			  Con=this.getConnection();
			  if(Con==null){
				 try{
					Thread.sleep(0);
				 }catch(Exception e){}	             
			  }else
				 break;	    
		}

		try{			
			stmt=Con.createStatement();
			if(SITC) System.out.println(currentTransaction.toString());
			stmt.execute(currentTransaction.toString());
			releaseConnection(Con);
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
			System.out.println(currentTransaction.toString());
		}
		
		releaseConnection(Con);		
	}	
} 