package org.abia.AgentContainer.AgentContainerListener;

import java.net.*;

public class AgentContainerListener extends Thread {
	ServerSocket theSocket;
	Socket       currentSocket;
	int          MyPort;
	
	public AgentContainerListener(int Port){
		MyPort=Port;
		this.setName(new String("Listener_"+Port));
	}
	
	public void run(){
		AgentWorkerThread currentWorkerThread;

		      try{		
		        theSocket=new ServerSocket(MyPort);
		        System.out.println("Escutando em "+MyPort);
				while(true){
		            currentSocket=theSocket.accept();
		            System.out.println("Aceitei Conexao...");
					currentWorkerThread=new AgentWorkerThread(this,currentSocket);
					currentWorkerThread.start();
				}
		      }catch(Exception e){
			    System.out.println(e);
				e.printStackTrace();
		      }		      
		}
				
}