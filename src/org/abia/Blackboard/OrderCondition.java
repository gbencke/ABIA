package org.abia.Blackboard;

public class OrderCondition extends QueryCondition {
	public static int ASC =1;
	public static int DESC=2;
		
	public String Field;
	public int    Order;
		
	public OrderCondition(){
		Field="";
		Order=0;			
	}
          	
	public OrderCondition(String newField,int newOrder){
		Field=newField;
		Order=newOrder;
	}
}
