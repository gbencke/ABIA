package org.abia.Applets;

import java.util.*;
import javax.swing.*;
import java.awt.*;

import org.abia.Agents.BovespaAgent.*;
import org.abia.Blackboard.*;
import org.abia.AgentContainer.AgentContainerClient.*;

public class CandleStickDrawer extends ChartDrawer {
	protected ChartPanel       myParent;
	protected Papel            myPapel=null;
	protected Vector           TimeSeries=new Vector();
	protected Vector           DataSerie=new Vector();	
	protected ClientConnection myConnection;
	protected Pregao           lastPregao;
	
	public Pregao getLastPregao(){
		return lastPregao;	
    }	
	
	public CandleStickDrawer(ChartPanel Parent,String IPAgentContainer,int PortAgentContainer){
		myParent=Parent;
		
		try{
			myConnection=AgentContainerClient.createClientConnection(IPAgentContainer,PortAgentContainer);
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	public void paint(Graphics g,JPanel Target){
		Dimension ParentSize;
		int       Width,Espacamento,f;
		Cotacao   CurrentCotacao;
		Pregao    CurrentPregao;
		double    gap,pos1,pos2,pos3,pos4,posY1,posY2,posY3,posY4,Maximo=0,Minimo=1000000,Altura,VerticalInicial;
		Color     CorPreenchimento;
		String    TempData;
		boolean   DrawChartReally=true;
		boolean   DrawCandleStickReally=true;
		boolean   DrawXAxisReally=false,DrawValuesReally=false;
				
		try{
		ParentSize=Target.getSize();
		Width=ParentSize.width;		
		if(myPapel==null){
		   g.setColor(new Color(255,0,0));
		   g.drawLine(1,1,ParentSize.width,ParentSize.height);
		   return;
		}else{
			Espacamento=Width/(TimeSeries.size()+1);
		
			for(f=0;f<DataSerie.size();f++){
				CurrentCotacao=(Cotacao)DataSerie.get(f);
				if(CurrentCotacao.getAbertura().doubleValue()<Minimo) Minimo=CurrentCotacao.getAbertura().doubleValue();
				if(CurrentCotacao.getFechamento().doubleValue()<Minimo) Minimo=CurrentCotacao.getFechamento().doubleValue();
				if(CurrentCotacao.getMaximo().doubleValue()<Minimo) Minimo=CurrentCotacao.getMaximo().doubleValue();
				if(CurrentCotacao.getMinima().doubleValue()<Minimo) Minimo=CurrentCotacao.getMinima().doubleValue();

				if(CurrentCotacao.getAbertura().doubleValue()>Maximo) Maximo=CurrentCotacao.getAbertura().doubleValue();
				if(CurrentCotacao.getFechamento().doubleValue()>Maximo) Maximo=CurrentCotacao.getFechamento().doubleValue();
				if(CurrentCotacao.getMaximo().doubleValue()>Maximo) Maximo=CurrentCotacao.getMaximo().doubleValue();
				if(CurrentCotacao.getMinima().doubleValue()>Maximo) Maximo=CurrentCotacao.getMinima().doubleValue();
			}
			gap=Maximo-Minimo;
			for(f=0;f<DataSerie.size();f++){
				    try{
				    g.setColor(new Color(0,0,255));
				    CurrentCotacao=(Cotacao)DataSerie.get(f);
				    if(CurrentCotacao.getPregao() instanceof DummyAgentData)
					   CurrentCotacao.ResolveReferences();					
				    CurrentPregao=(Pregao)CurrentCotacao.getPregao();
					pos1=CurrentCotacao.getMaximo().doubleValue()-Minimo;
					pos2=CurrentCotacao.getMinima().doubleValue()-Minimo;
					pos3=CurrentCotacao.getAbertura().doubleValue()-Minimo;
					pos4=CurrentCotacao.getFechamento().doubleValue()-Minimo;				
					posY1=(pos1/gap)*ParentSize.height;
					posY2=(pos2/gap)*ParentSize.height;
					posY3=(pos3/gap)*ParentSize.height;
					posY4=(pos4/gap)*ParentSize.height;				
				
if(DrawChartReally){
	
					
				    if(posY4>posY3){
				       Altura=posY4-posY3;
					   VerticalInicial=posY3;				    				       
					   CorPreenchimento=new Color(255,255,255);
				    }else{
				       Altura=posY3-posY4;
				       VerticalInicial=posY4;				    
				       CorPreenchimento=new Color(0,0,255);
				    }
				    if(Altura==0) Altura=1;
	                if(DrawCandleStickReally){ 				
						g.drawRect((Espacamento*(f+1))-3,((int)VerticalInicial),6,(int)Altura);
				    	g.setColor(CorPreenchimento);					
				    	g.fillRect((Espacamento*(f+1))-2,((int)VerticalInicial+1),5,(int)Altura-1);
				    	g.drawLine(Espacamento*(f+1),(int)posY1,Espacamento*(f+1),(int)posY2);
					}					
					g.setColor(new Color(0,0,255));				    
                    if(DrawValuesReally){
				    	g.drawString(CurrentCotacao.getAbertura().toString(),(Espacamento*(f+1))+6,((int)VerticalInicial+1));
				    	g.drawString(CurrentCotacao.getMaximo().toString(),(Espacamento*(f+1))+6,((int)VerticalInicial+1)+10);
				    	g.drawString(CurrentCotacao.getMinima().toString(),(Espacamento*(f+1))+6,((int)VerticalInicial+1)+20);				    
				    	g.drawString(CurrentCotacao.getFechamento().toString(),(Espacamento*(f+1))+6,((int)VerticalInicial+1)+30);
					}				    
}

                    if(DrawXAxisReally){
				    	TempData="";
				    	if(CurrentPregao.getData().getDate()<10){
							TempData+="0"+CurrentPregao.getData().getDate();
				    	}else{
							TempData+=CurrentPregao.getData().getDate();				    	
				    	}
				    	if(CurrentPregao.getData().getMonth()<10){
							TempData+="0"+(CurrentPregao.getData().getMonth()+1);
						}else{
							TempData+=(CurrentPregao.getData().getMonth());				    	
						}
				    	g.drawString(TempData,(Espacamento*(f+1)),ParentSize.height-30);
				    	System.out.println(TempData);
                    }
				    }catch(Exception e){
				    	System.out.println(e);
				    }
			}
		}
		f=0;
		}catch(Exception e){
			System.out.println(e);
		}
	} 
	
	public AgentData[] getData(){
		return null;
	}
	
	public void setData(AgentData[] newData){
	}
	
	public void setPapel(Papel newPapel){
		int               f,g;
		QueryCondition    Filters[];
		FilterCondition    Filters2[];
		AgentData         Returned[]; 
		AgentData          Pregoes[];
				
		try{
			DataSerie=new Vector();
			TimeSeries=new Vector();
			myPapel=newPapel;
			
			Filters=new QueryCondition[1];
			Filters[0]=new OrderCondition();
			((OrderCondition)Filters[0]).Field="data";
			((OrderCondition)Filters[0]).Order=OrderCondition.ASC;							
			
			Pregoes=myConnection.Query(Class.forName("org.abia.Agents.BovespaAgent.Pregao"),Filters);
			if(Pregoes.length>0){
				UpdateTimeSeries(Pregoes);				
			}
		}catch(Exception e){
			System.out.println(e);		
		}
	}
	
	public void UpdateTimeSeries(AgentData[] newData){
		int f,g;
		FilterCondition   Filters[];
		AgentData         Returned[];
		
		for(f=0;f<newData.length;f++){
			TimeSeries.add(newData[f]);					
		}
		if(myPapel==null) return;
		try{
			for(f=0;f<newData.length;f++){
				Filters=new FilterCondition[2];
				Filters[0]=new FilterCondition();
				Filters[0].FieldName="papel";
				Filters[0].Operator=FilterCondition.EQUALS;
				Filters[0].ValueName=myPapel;
				
				Filters[1]=new FilterCondition();
				Filters[1].FieldName="pregao";
				Filters[1].Operator=FilterCondition.EQUALS;
				Filters[1].ValueName=newData[f];								
				Returned=myConnection.Query(Class.forName("org.abia.Agents.BovespaAgent.Cotacao"),Filters);
				for(g=0;g<Returned.length;g++){
					DataSerie.add(Returned[g]);
				}
			}
		}catch(Exception e){
			System.out.println(e);
		}
		lastPregao=(Pregao)newData[newData.length-1];		
		myParent.repaint();
	}
} 