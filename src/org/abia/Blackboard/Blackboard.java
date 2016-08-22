package org.abia.Blackboard;

import  org.abia.AgentContainer.*;
import  java.util.*;

public abstract class Blackboard {
	   public static int     TIMESTAMP_EXACT=0;
	   public static int     TIMESTAMP_GREATER_THAN=1;
	   public static int     TIMESTAMP_EXACT_AND_GREATER_THAN=2; 

	   public static Blackboard currentBlackboard=null;
	   public static HashMap    BlackboardParameters;
	  	   
	   public static void Initialize(HashMap Parameters) throws BlackboardException {
	   	      Class newBlackboard;
	   	      
		      BlackboardParameters=Parameters;
	   	      try{	   	      
				newBlackboard=Class.forName(AgentContainer.getBlackBoardClass());
				currentBlackboard=(Blackboard)newBlackboard.newInstance();
				currentBlackboard.Configure();
			  }catch(Exception e){
			  	 System.out.println(e);			  	
			  }
	   
	   }
	   
	   public static Blackboard getBlackboard(){
	   	      return currentBlackboard;	   		   	
	   }
	   
	   public abstract void Configure() throws BlackboardException;
	   public abstract void RegisterAgent(Agent AgentToRegister) throws BlackboardException;
	   public abstract void RegisterAgentData(Class ClassesToRegister) throws BlackboardException;	   
	   public abstract void RegisterAgentData(Class[] ClassesToRegister) throws BlackboardException;
	   public abstract int getNextTimestamp();
	   
	   public abstract void Store(Class Origin,AgentData ClassToStore) throws BlackboardException;
	   public abstract void Store(AgentData ClassToStore[]) throws BlackboardException;	   
	   public abstract AgentData[] Query(Class ClassToRetrieve,QueryCondition Conditions[]) throws BlackboardException;	   
 
	   public abstract void                        ResolveReferences(AgentData InstanceToResolve) throws BlackboardException;	   
	   public abstract ClassRegisteredInBlackboard[] getClassRegisteredInBlackboard();
	   public abstract ClassRegisteredInBlackboard getClassRegisteredInBlackboard(Class  ClassToRetrieve);
	   public abstract ClassRegisteredInBlackboard getClassRegisteredInBlackboard(String ClassToRetrieve);
	   public abstract AgentData[] QueryTimestamp(int Timestamp,int TypeOfQuery);
	   public abstract AgentData[] QueryTimestamp(int Timestamp,int TypeOfQuery,String OnlyThisClass);
	   public abstract AgentData[] QueryTimestamp(int Timestamp,int TypeOfQuery,String OnlyThisClass,int MaxClasses);	   
	    	   	   
}