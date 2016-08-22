package org.abia.utils.BackpropagationNeuralNetwork.Validation;

import java.text.*;
import javax.swing.*;
import java.io.*;

/** Trabalho de IA2 - G1, uma rede que utiliza backpropagation para permitir o reconhecimento de padroes.Ela possui um frame padrao aonde
    sera realizado o calculo
 */ 

public class TrabG1IA2 {
	/** Frame Principal de nossa aplicacao */
	public static FramePrincipal Frame;
	/** Ponto de Entrada de Nossa Aplicacao */
 	public static void main(String args[]){
	      DecimalFormat Formatador=new DecimalFormat("#.###########");
	      int f,g;
	      
	      Frame=new FramePrincipal();
	      if(args.length==2){ // Caso voce queira salvar os pesos atuais (randomicos) 
		      System.out.println("Comando:"+args[0]);
		      System.out.println("Arquivo:"+args[1]);
		      try{
			  PrintWriter out1=new PrintWriter(new BufferedWriter(new FileWriter(args[1])));
			  for(f=0;f<Frame.Rede.CamadaEntrada.Neuronios.length;f++){
				for(g=0;g<Frame.Rede.CamadaEntrada.Neuronios[f].Pesos.length;g++){
				    out1.println(Formatador.format(Frame.Rede.CamadaEntrada.Neuronios[f].Pesos[g]).replace(',','.'));
				}
			  }
			  for(f=0;f<Frame.Rede.CamadaEscondida.Neuronios.length;f++){
				for(g=0;g<Frame.Rede.CamadaEscondida.Neuronios[f].Pesos.length;g++){
				    out1.println(Formatador.format(Frame.Rede.CamadaEscondida.Neuronios[f].Pesos[g]).replace(',','.'));
				}
			  }
			  for(f=0;f<Frame.Rede.CamadaSaida.Neuronios.length;f++){
				for(g=0;g<Frame.Rede.CamadaSaida.Neuronios[f].Pesos.length;g++){
				    out1.println(Formatador.format(Frame.Rede.CamadaSaida.Neuronios[f].Pesos[g]).replace(',','.'));
				}
			  }
			  out1.close();
		      }catch(Exception e){
			      System.out.println(e);
		      }
		      System.exit(0);
	      }	      	      
	      Frame.setVisible(true);
       }

}
