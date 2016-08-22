package org.abia.Applets;

import java.awt.*;
import java.util.*;

public class SimpleChartPanel extends ChartPanel {
	protected HashMap Overlays;
	
	public SimpleChartPanel(){
		Overlays=new HashMap();		
	}
	
	public void addOverlay(ChartDrawer newOverlay){
		Overlays.put(newOverlay.getClass().getName(),newOverlay);		
	}

	public ChartDrawer getOverlay(Class ToRetrieve){
		Object obj;
		obj=Overlays.get(ToRetrieve.getName());
		if(obj instanceof ChartDrawer){
			return (ChartDrawer)obj;				
		}else{
			return null;
		}
	}
	
	public void paint(Graphics g){
		Dimension CurrentDimension,ParentSize,MySize;
		int f,sizeVector;
		Object obj;
		Iterator i;
		
		super.paint(g);
		
		MySize=this.getSize();
		ParentSize=this.getParent().getSize();
		
		if(MySize.height!=ParentSize.height ||
		   MySize.width!=ParentSize.width){
		   	this.setSize(ParentSize);		   	
		}
		CurrentDimension=this.getSize();
				
		g.setColor(new Color(255,255,255));		
		g.fillRect(1,1,CurrentDimension.width,CurrentDimension.height);
		sizeVector=Overlays.values().size();
		i=Overlays.values().iterator();
		while(i.hasNext()){
			obj=i.next();
			if(obj instanceof ChartDrawer){
				ChartDrawer Dr;
				
				Dr=(ChartDrawer)obj;
				Dr.paint(g,this);
			}
		}
	}
	
}