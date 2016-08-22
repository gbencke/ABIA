package org.abia.Blackboard;

public class FilterCondition extends QueryCondition {	
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
	
	public String FieldName;
	public Object ValueName;
	public int    Operator;	
}