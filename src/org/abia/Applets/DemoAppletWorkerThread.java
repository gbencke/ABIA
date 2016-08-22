package org.abia.Applets;

public class DemoAppletWorkerThread extends Thread {
	protected DemoApplet Parent;
	
	public DemoAppletWorkerThread(DemoApplet newParent){
		//Parent=newParent;		
	}
	public void run(){
		int f=0;
		
		/*while(true){
			f=f+1;			
		    Parent.Teste.setBounds(1,1+f,20,20);
			Parent.repaint();		   
		    try{		    
		       Thread.sleep(100);
		    }catch(Exception e){
		    	
		    }
		}*/		
	}
	
}
