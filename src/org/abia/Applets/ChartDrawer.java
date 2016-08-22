package org.abia.Applets;

import org.abia.Blackboard.*;

import javax.swing.*;
import java.awt.*;

public abstract class ChartDrawer { 
	public AgentData DataToDraw[];
	
	public int getNumberData(){
		if(DataToDraw!=null) 
		   return DataToDraw.length; 
		else 
		   return 0;
	}
	
	public abstract void        UpdateTimeSeries(AgentData newTimeSeries[]);	
	public abstract void        paint(Graphics g,JPanel Target);
	public abstract void        setData(AgentData[] newData);
	public abstract AgentData[] getData();	
}