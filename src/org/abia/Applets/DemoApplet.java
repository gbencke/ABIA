package org.abia.Applets; 

import javax.swing.*;

public class DemoApplet extends JApplet implements Runnable {
	public JLabel Teste;
	
	public void run(){

	}

		
	public DemoApplet(){
		Teste=new JLabel("ok");
		this.getRootPane().setLayout(null);
		this.getRootPane().add(Teste);
		Teste.setBounds(1,1,100,20);
		Teste.setVisible(true); 		
	}
	
	public void start(){

	}
}