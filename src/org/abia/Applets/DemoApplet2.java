package org.abia.Applets;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;

import org.abia.AgentContainer.AgentContainerClient.*;

public class DemoApplet2 extends JApplet 
    implements ActionListener {
    	
	public JPanel PanelNorth,PanelCenter,PanelSouth,Panel2;
	public JButton Botao1,Botao2,Botao3;
	public JComboBox ComboBoxPapel;
	public JCheckBox AutomaticUpdate;
	
	public Timer Eventos;
	public SimpleChartPanel theChart;
	public ChartDrawer Overlays[];
	public Socket theSocket;
	
	public ClientConnection myConnection;
	
	public void actionPerformed(ActionEvent e){
		
		if(e.getActionCommand()==null) return;
		
		if(e.getActionCommand().equals("Teste3")){
			try{
				myConnection=AgentContainerClient.createClientConnection("127.0.0.1",7200);
				myConnection.Query(Class.forName("org.abia.BovespaAgent.Cotacao"),null);
            }catch(Exception ex){
				System.out.println(ex);			
			}
		}
	}
	
	public DemoApplet2(){
		
	}
	
	public void init(){

		
		try{		
		   UIManager.setLookAndFeel(
				"com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		}catch(Exception e){
			
		}


		PanelNorth=new JPanel();
		PanelCenter=new JPanel();
		PanelSouth=new JPanel();
		Panel2=new JPanel();
		
		Eventos=new Timer(1000,this);
		
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
		//Overlays[0]=new CandleStickDrawer(theChart);
		
		theChart.addOverlay(Overlays[0]);					
		
		ComboBoxPapel=new JComboBox();		
		ComboBoxPapel.addItem("teste1   ");
		ComboBoxPapel.addItem("teste2   ");
		ComboBoxPapel.addItem("teste3   ");
		ComboBoxPapel.addItem("teste4   ");
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
