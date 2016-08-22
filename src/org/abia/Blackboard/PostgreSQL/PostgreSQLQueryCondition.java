package org.abia.Blackboard.PostgreSQL;

import org.abia.Blackboard.*;

public abstract class PostgreSQLQueryCondition extends QueryCondition {
	
	public static int LESS=0;
	public static int LESS_EQUAL=1;
	public static int GREATER=2;
	public static int GREATER_EQUAL=3;	

	public static int EQUALS=4;
	public static int DIFFERENT=5;	
       	
	public static int CONTAINS=6;	
	public static int STARTS_WITH=7;
	public static int ENDS_WITH=8;
	
	public static int ASC=9;	
	public static int DESC_WITH=10;	

    public static String ResolveOperatorSymbol(int Constant){
		    if(Constant==PostgreSQLQueryCondition.LESS) return "<";
		    if(Constant==PostgreSQLQueryCondition.LESS_EQUAL) return "<=";
		    if(Constant==PostgreSQLQueryCondition.GREATER) return ">";
		    if(Constant==PostgreSQLQueryCondition.GREATER_EQUAL) return ">=";
		    if(Constant==EQUALS) return "=";
		    if(Constant==DIFFERENT) return "<>";       	
		    if(Constant==ASC) return " asc ";
		    if(Constant==DESC_WITH) return " desc ";
		    return ""; 	
    }

	public static String GenerateString(Class ClassTarget,QueryCondition Condition[]) throws BlackboardException {
		ClassRegisteredInBlackboard TargetTable;
		int f,Total,g;		
		String cmdWhere="",cmdOrder="",cmdReturn="";

        if(Condition==null) return "";
        
		TargetTable=Blackboard.getBlackboard().getClassRegisteredInBlackboard(ClassTarget);
		
		for(f=0;f<Condition.length;f++){
			if(!(Condition[f] instanceof QueryCondition)) continue;
			
			
			if(Condition[f] instanceof FilterCondition){
				PostgreSQLFilterCondition  CurrentFilter=(PostgreSQLFilterCondition)Condition[f];
				ClassMethod                CurrentMethod=null;;
								
				Total=TargetTable.Values.size();
				if(CurrentFilter.FieldName.toUpperCase().equals("TIMESTAMP")){
					CurrentFilter.FieldName="TIMESTAMPVAL";
				    g=0;
					cmdWhere=" timestampval"+" "+ResolveOperatorSymbol(CurrentFilter.Operator)+ " "+CurrentFilter.ValueName;
					cmdWhere=cmdWhere+" and ";
					continue;					
			    }
			    
				for(g=0;g<Total;g++){
					CurrentMethod=(ClassMethod)TargetTable.Values.get(g);
					if(CurrentFilter.FieldName.toUpperCase().equals(
					 (CurrentMethod.ValueToStore.toUpperCase()))) break;
				}
				if(g<Total){
					//if(CurrentFilter.ValueName.getClass()!=CurrentMethod.ReturnValue.getReturnType()) continue;
					if(CurrentFilter.Operator<6){
						cmdWhere=cmdWhere+CurrentFilter.FieldName;
						cmdWhere=cmdWhere+" "+ResolveOperatorSymbol(CurrentFilter.Operator)+ " ";
						
						if(CurrentFilter.ValueName.getClass().getName().equals("java.lang.String"))
						   cmdWhere=cmdWhere+"'";
						
						cmdWhere=cmdWhere+CurrentFilter.ValueName.toString();
						
						if(CurrentFilter.ValueName.getClass().getName().equals("java.lang.String"))
						   cmdWhere=cmdWhere+"'";
						
						cmdWhere=cmdWhere+" and ";						
					}
					if(CurrentFilter.Operator>5 && CurrentFilter.Operator<9){
						cmdWhere=cmdWhere+CurrentFilter.FieldName;						
						if(CurrentFilter.Operator==PostgreSQLQueryCondition.CONTAINS){
							cmdWhere=cmdWhere+" like '%"+CurrentFilter.ValueName.toString()+"%'";
						}	
						if(CurrentFilter.Operator==PostgreSQLQueryCondition.STARTS_WITH){
							cmdWhere=cmdWhere+" like '"+CurrentFilter.ValueName.toString()+"%'";
						}						
						if(CurrentFilter.Operator==PostgreSQLQueryCondition.ENDS_WITH){
							cmdWhere=cmdWhere+" like '%"+CurrentFilter.ValueName.toString()+"'";						
						}
						cmdWhere=cmdWhere+" and ";
					}
					
				}
			}			
			if(Condition[f] instanceof OrderCondition){
				PostgreSQLOrderCondition TempCondition;
				
				
				TempCondition=(PostgreSQLOrderCondition)Condition[f];
				cmdOrder+=TempCondition.Field;
				cmdOrder+=" ";
				if(TempCondition.Order==PostgreSQLOrderCondition.ASC){
				   cmdOrder+="ASC, ";
				}
				if(TempCondition.Order==PostgreSQLOrderCondition.DESC){
				   cmdOrder+="DESC, ";
				}
			}						
		}
		if(!cmdWhere.equals("")){
			cmdReturn=cmdReturn+"where "+cmdWhere.substring(0,cmdWhere.length()-4);			
		}
		if(!cmdOrder.equals("")){
			cmdReturn=cmdReturn+"order by "+cmdOrder.substring(0,cmdOrder.length()-2);			
		}
		return cmdReturn;		
	}
       	

}