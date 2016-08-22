package org.abia.Applets;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.util.*;

import org.abia.AgentContainer.AgentContainerClient.*;
import org.abia.Blackboard.*;


import org.abia.Agents.BovespaAgent.*;

public class TesteFrame extends JFrame
	implements ActionListener {
    	
	public static TesteFrame MyFrame;

	public JPanel             PanelNorth,PanelCenter,PanelSouth,Panel2;
	public JButton            Botao1,Botao2,Botao3;
	public JComboBox          ComboBoxPapel;
	public JCheckBox          AutomaticUpdate;
	public javax.swing.Timer  Eventos;
	public SimpleChartPanel   theChart;
	public ChartDrawer        Overlays[];

	public String             ContainerIP="127.0.0.1";
	public int                ContainerPort=7202;
	public ClientConnection   myConnection;
	
	public AgentData          Papeis[],TimestampQueryTemp[];
	public Vector             Pregao;

	
	protected Pregao          lastPregao=null;
		

	protected void UpdateChartDrawers(AgentData newTimeSeries[]){
		int         f;
		ChartDrawer currentChartDrawer;
		
		for(f=0;f<Overlays.length;f++){
			currentChartDrawer=(ChartDrawer)Overlays[f];
			currentChartDrawer.UpdateTimeSeries(newTimeSeries);			
		}
	}

	public void actionPerformed(ActionEvent e){
		FilterCondition Filters[];
		AgentData       Returned[];
		
		if(e.getActionCommand()==null){
			int Valor;
			
			System.out.println("Timer...");
			if(((CandleStickDrawer)(Overlays[0])).getLastPregao()==null)
			   Valor=0;
			else
			   Valor=((CandleStickDrawer)(Overlays[0])).getLastPregao().getTimestamp().intValue();
			Returned=myConnection.QueryTimestamp(Valor,ClientConnection.TIMESTAMP_GREATER_THAN,"org.abia.Agents.BovespaAgent.Pregao");
			if(Returned.length>0){
				UpdateChartDrawers(Returned);
			}
			return;
		}
		
		if(e.getSource()==ComboBoxPapel && this.isVisible() ){
			Papel             tempPapel;
			CandleStickDrawer tempDrawer;
				
			tempPapel=(Papel)Papeis[ComboBoxPapel.getSelectedIndex()];
			tempDrawer=(CandleStickDrawer)Overlays[0];
			tempDrawer.setPapel(tempPapel);					
		}
		
		if(e.getActionCommand().equals("Teste3")){
			try{
				Filters=new FilterCondition[1];
				Filters[0]=new FilterCondition();
				Filters[0].FieldName="abertura";
				Filters[0].Operator=FilterCondition.GREATER;
				Filters[0].ValueName=new Integer(10);								
				Returned=myConnection.Query(Class.forName("org.abia.Agents.BovespaAgent.Cotacao"),Filters);
				System.out.println("Returned AgentData"+Returned.length);								
			}catch(Exception ex){
				System.out.println(ex);			
			}
		}
	}
	
	public TesteFrame(){
		Pregao=new Vector();
		setTitle("Teste de Acesso ao Container de Agentes");
	}
	
	public static void main(String args[]){
		Dimension newDimension;
		
		MyFrame=new TesteFrame();
		MyFrame.init();
		MyFrame.start();
		MyFrame.setSize(new Dimension(600,400));
		MyFrame.setVisible(true);		
	}
	
	public void init(){
		QueryCondition  Filters[];
		Papel           PapelAtual;
		Empresa         EmpresaAtual;
		int             f;
		AgentData          Pregoes[];
				
		try{		
		   UIManager.setLookAndFeel(
				"com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		}catch(Exception e){
			
		}


		PanelNorth=new JPanel();
		PanelCenter=new JPanel();
		PanelSouth=new JPanel();
		Panel2=new JPanel();
		
		Eventos=new javax.swing.Timer(1000,this);
		
		Botao1=new JButton("Teste1");
		Botao2=new JButton("Teste2");
		Botao3=new JButton("Teste3");
		
		this.getContentPane().setLayout(new BorderLayout());
		
		this.getContentPane().add(PanelNorth,BorderLayout.NORTH);
		this.getContentPane().add(PanelCenter,BorderLayout.CENTER);
		this.getContentPane().add(PanelSouth,BorderLayout.SOUTH);
		
		PanelNorth.setLayout(new BorderLayout());
		PanelCenter.setLayout(new BorderLayout());
		PanelSouth.setLayout(new BorderLayout());

		theChart=new SimpleChartPanel();
		
		Overlays=new ChartDrawer[1];
		Overlays[0]=new CandleStickDrawer(theChart,ContainerIP,ContainerPort);
		
		theChart.addOverlay(Overlays[0]);					
		
		ComboBoxPapel=new JComboBox();		
		ComboBoxPapel.setSize(100,40);

		AutomaticUpdate=new JCheckBox("Automatic Update");
		
		PanelNorth.add(ComboBoxPapel,BorderLayout.WEST);
		PanelNorth.add(Panel2,BorderLayout.CENTER);
		PanelNorth.add(AutomaticUpdate,BorderLayout.EAST);
		
		PanelCenter.add(theChart,BorderLayout.NORTH);
		PanelSouth.add(Botao3,BorderLayout.NORTH);
		
		Botao1.addActionListener(this);
		Botao2.addActionListener(this);
		Botao3.addActionListener(this);

		try{
			myConnection=AgentContainerClient.createClientConnection(ContainerIP,ContainerPort);

			Filters=new QueryCondition[1];
			Filters[0]=new OrderCondition();
			((OrderCondition)Filters[0]).Field="codigopapel";
			((OrderCondition)Filters[0]).Order=OrderCondition.ASC;							
			Papeis=myConnection.Query(Class.forName("org.abia.Agents.BovespaAgent.Papel"),Filters);
			
			if(Papeis.length>0){
				Papel             tempPapel;
				CandleStickDrawer tempDrawer;
				
				tempPapel=(Papel)Papeis[0];
				tempDrawer=(CandleStickDrawer)Overlays[0];
				tempDrawer.setPapel(tempPapel);		
			}
			for(f=0;f<Papeis.length;f++){
				PapelAtual=(Papel)Papeis[f];
				PapelAtual.ResolveReferences();
				EmpresaAtual=(Empresa)PapelAtual.getEmpresaEmitente();
				ComboBoxPapel.addItem(PapelAtual.getCodigoPregao()+ " - " + EmpresaAtual.getNomePregao());				
			}
			ComboBoxPapel.addActionListener(this);
		}catch(Exception e){
			System.out.println(e);			
		}
		
		
	}
	
	public void start(){
		Dimension r1,r2,r3;
		int newHeight=0;
		
		r1=this.getSize();
		r2=PanelSouth.getSize();
		r3=PanelNorth.getSize();
		newHeight=r1.height-r2.height-r3.height;		
		PanelCenter.setSize(new Dimension(r1.width,newHeight));			
		Eventos.start();
	}
}
