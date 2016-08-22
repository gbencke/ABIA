package org.abia.utils.BackpropagationNeuralNetwork.Validation;

import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;

/** EsperarAteCarregar

Como notamos que a Maquina Virtual do Java demora para atribuir REALMENTE os valores double passados para as camadas, decidimos criar
essa Thread que espera 2 segundos para que o Java Virtual Machine se organize internamente e realize os calculos de aprendizado de
maneira correta...
*/

class EsperarAteCarregar extends Thread {
        
	
	
	FramePrincipal Pai;

	
	public EsperarAteCarregar(FramePrincipal Entrada){
	       Pai=Entrada;
	
        }

public void run(){

        /** Vamos Esperar 2 segundos e dai podemos liberar o botao... */ 
	
	try{
		Thread.sleep(2000);
	}catch(Exception e){
		
	}
	Pai.btnAprenderAmostra.setEnabled(true);
}

}

/** Essa classe eh o actionlistener que permite a implementacao da funcao de Carregar Pesos e Amostras */

class ActionListenerCarregar implements ActionListener {
	FramePrincipal Alvo;
	
	public ActionListenerCarregar(FramePrincipal FrameAlvo){
		Alvo=FrameAlvo;
	}

	public void actionPerformed(ActionEvent e){
		Double Lido[][];
		String Arquivos[];
		BufferedReader in;
		int f,NumeroLidos,NumeroOk,g;
		
		File Diretorio=new File(Alvo.txtCaminhoAmostras.getText());
		Arquivos=Diretorio.list();
		NumeroLidos=0;
		
	
		boolean Pesostxt=false;
		boolean Testetxt=false;
		boolean Amostra0txt=false;
		boolean Amostra1txt=false;
		boolean Amostra2txt=false;
		boolean Amostra3txt=false;
		boolean Amostra4txt=false;
	        for(f=0;f<Arquivos.length;f++){
		    if(Arquivos[f].toUpperCase().equals("PESOS.TXT")) Pesostxt=true;
		    if(Arquivos[f].toUpperCase().equals("TESTE.TXT")) Testetxt=true;
		    if(Arquivos[f].toUpperCase().equals("AMOSTRA0.TXT")) Amostra0txt=true;
		    if(Arquivos[f].toUpperCase().equals("AMOSTRA1.TXT")) Amostra1txt=true;
		    if(Arquivos[f].toUpperCase().equals("AMOSTRA2.TXT")) Amostra2txt=true;
		    if(Arquivos[f].toUpperCase().equals("AMOSTRA3.TXT")) Amostra3txt=true;
		    if(Arquivos[f].toUpperCase().equals("AMOSTRA4.TXT")) Amostra4txt=true;
	        }

		String Saida=new String("");
		if(!Pesostxt)   {  Saida=Saida+"-"+Alvo.txtCaminhoAmostras.getText()+"Pesos.txt\n"; }  
		if(!Testetxt)   {  Saida=Saida+"-"+Alvo.txtCaminhoAmostras.getText()+"Teste.txt\n"; }
		if(!Amostra0txt){  Saida=Saida+"-"+Alvo.txtCaminhoAmostras.getText()+"Amostras0.txt\n"; }
		if(!Amostra1txt){  Saida=Saida+"-"+Alvo.txtCaminhoAmostras.getText()+"Amostras1.txt\n"; }
		if(!Amostra2txt){  Saida=Saida+"-"+Alvo.txtCaminhoAmostras.getText()+"Amostras2.txt\n"; }
		if(!Amostra3txt){  Saida=Saida+"-"+Alvo.txtCaminhoAmostras.getText()+"Amostras3.txt\n"; }
		if(!Amostra4txt){  Saida=Saida+"-"+Alvo.txtCaminhoAmostras.getText()+"Amostras4.txt\n"; }
		
		
		if(Saida.length()>0){

			
		}else{		

			
		try {
		     BufferedReader LeitorPesos=new BufferedReader(new FileReader(Alvo.txtCaminhoAmostras.getText()+"Pesos.txt"));
			  for(f=0;f<Alvo.Rede.CamadaEntrada.Neuronios.length;f++){
				for(g=0;g<Alvo.Rede.CamadaEntrada.Neuronios[f].Pesos.length;g++){
				    Alvo.Rede.CamadaEntrada.Neuronios[f].Pesos[g]=(new Double(LeitorPesos.readLine())).doubleValue();				    
				}
			  }
			  for(f=0;f<Alvo.Rede.CamadaEscondida.Neuronios.length;f++){
				for(g=0;g<Alvo.Rede.CamadaEscondida.Neuronios[f].Pesos.length;g++){
				    Alvo.Rede.CamadaEscondida.Neuronios[f].Pesos[g]=(new Double(LeitorPesos.readLine())).doubleValue();
				}
			  }
			  for(f=0;f<Alvo.Rede.CamadaSaida.Neuronios.length;f++){
				for(g=0;g<Alvo.Rede.CamadaSaida.Neuronios[f].Pesos.length;g++){
                                    Alvo.Rede.CamadaSaida.Neuronios[f].Pesos[g]=(new Double(LeitorPesos.readLine())).doubleValue();
				    System.out.println("Valor:"+Alvo.Rede.CamadaSaida.Neuronios[f].Pesos[g]);
				}
			  }
		
		}catch(Exception E){
			System.out.println(E);
			JOptionPane.showMessageDialog(Alvo,"Nao consegui ler o arquivo de Pesos,\nesta corrompido...","Erro!!",JOptionPane.ERROR_MESSAGE);			
		}
		
		
		
		try {
		    for(f=0;f<Arquivos.length;f++){
			    if(Arquivos[f].indexOf("Amostra")==0){
			       int Inicio,Final,CharLido;
			       int Linha,Coluna;
				    
			       in=new BufferedReader(new FileReader(Alvo.txtCaminhoAmostras.getText()+Arquivos[f]));
			       
			       NumeroOk=(new Integer(Arquivos[f].substring(7,Arquivos[f].indexOf(".")))).intValue();
			       System.out.println(" ");
			       Linha=0;Coluna=0;
			       while(true){
				       CharLido=in.read();
				       if(CharLido==-1){
					       break;
				       }else{
					       if(CharLido==48||CharLido==49){
						       Alvo.Amostras[NumeroOk].Modelo.Valor[Coluna][Linha]=new Double(CharLido-48);
						       System.out.println("Amostras["+NumeroOk+"].Valor["+Coluna+"]["+Linha+ "]=new Double("+(CharLido-48)+");");
						       Coluna++;
					       }
					       if(CharLido==10){
						       Coluna=0;
						       Linha++;
					       }
					       
				       }
			       } 
				       
			       NumeroLidos++;		       
			       if(NumeroLidos==6){
				       break;
			       }
			    }
		    }
		}catch(IOException ex){
			System.out.println(ex);
		}
		
		Lido=new Double[FramePrincipal.ColunaAmostra][FramePrincipal.LinhaAmostra];
		Alvo.PopularAmostra(Lido);
		for(f=0;f<5;f++){
			Alvo.Amostras[f].repaint();
		}
		Alvo.Carregado=true;
		System.out.println("Carregado");
		EsperarAteCarregar Esperar=new EsperarAteCarregar(Alvo);
		Esperar.start();
	}
	}
}     


/** A classe utilitarias sao funcoes utilitarias extremamente uteis para os calculos da BackPropagation */

class Utilitarias {
  public static int[] Tamanho(double[][] A) {

    double temp;
    boolean gotCols;
    int[] dims=new int[2];
    int stepsize,Cols;

    dims[0]=A.length;  

    stepsize=1024;
    Cols=stepsize;
    gotCols=false;
    while (gotCols==false) {
      try {
        temp=A[0][Cols];
        Cols += stepsize;
      }
      catch (java.lang.ArrayIndexOutOfBoundsException e) {
        if (stepsize==1) { gotCols=true; }
        else {
          Cols -= stepsize;
          stepsize /= 2;
        }
      }
    }
    dims[1]=Cols;
    return dims;
  }	     
	     
  public static double[][] CalcularFuncaoAtivacao( double[][] ip, double[] b ) {
    int[] ip_dims = Tamanho(ip);
    double[][] out = new double[ip_dims[0]][ip_dims[1]];
    for (int i=0;i<ip_dims[0];i++) {
      for (int j=0;j<ip_dims[1];j++) {
        ip[i][j] += b[i];
        out[i][j] = 1/(1+Math.exp(-ip[i][j]));
      }
    }
    return out;
  }


  public static double[][] DeltaErro( double[][] out, double[][] err) {
    int[] dims=Tamanho(out); 
    double[][] delta= new double[dims[0]][dims[1]];

    for (int i=0;i<dims[0];i++){
      for (int j=0;j<dims[1];j++) {
        delta[i][j]=out[i][j]*(1-out[i][j])*err[i][j];
      }
    }
    return delta;
  }

  public static double[][] DeltaErro( double[][] out, double[][] d, double[][] w) {

    int[] dims=Tamanho(out);
    double[][] delta = new double[dims[0]][dims[1]];
    double[][] wt = Transpor(w);
    double[][] err = MultiplicarMatriz(wt,d);
    for (int i=0;i<dims[0];i++){
      for (int j=0;j<dims[1];j++) {
        delta[i][j]=out[i][j]*(1-out[i][j])*err[i][j];
      }
    }
    return delta;
  }
  public static double[][] MultiplicarMatriz(double[][] A, double[][] B) {

    int[] dimA = Tamanho(A);
    int[] dimB = Tamanho(B);
    int Am = dimA[0];
    int An = dimA[1];
    int Bm = dimB[0];
    int Bn = dimB[1];
    int[] tmp = Tamanho(B);

    double[][] C = new double[Am][Bn];
    for (int i=0;i<Am;i++) {
      for (int j=0;j<Bn;j++) {
        C[i][j]=0;
        for (int k=0;k<An;k++) {
          C[i][j] += A[i][k]*B[k][j];
        }
      }
    }
    return C;
  }

  public static double[][] Transpor(double[][] A) {
    int[] dim_A = Tamanho(A);
    int m=dim_A[0];
    int n=dim_A[1];
    double[][] At = new double[n][m];
    for (int i=0;i<m;i++) {
      for (int j=0;j<n;j++) {
        At[j][i]=A[i][j];
      }
    }
    return At;
  }
	
	
}

/** Essa classe permite que realmente executamos o calculo da Nossa Rede Neural de maneira assincrona... Ela implementa uma
    Thread que calcula o Aprendizado do Backpropagation em Background...
*/

class Calcular extends Thread {
	FramePrincipal Pai;

	
	public Calcular(FramePrincipal Alvo){
		Pai=Alvo;
        }
	
	public void run(){
		     double Entradas[][],Temp[],PesosCamadaEntrada[][];
		     double CalculoEntrada[][],SaidaCamadaEntrada[][];
		     double PesosCamadaEscondida[][];   
		     double CalculoEscondida[][],SaidaCamadaEscondida[][];
		     double PesosCamadaSaida[][];
		     double CalculoSaida[][],SaidaCamadaSaida[][];
		     double ErroTotal,Erros[][],TempErroTotal;
		     double DeltaCamadaSaida[][];
		     double DeltaCamadaEscondida[][];
		     double DeltaCamadaEntrada[][];
			     
		     double BiasCamada[];
		     double TempBiasCamada[];
		     int R,S1,S2,S3,epoch;
		     double[][] PesosEntrada,PesosEscondida,PesosSaida;
		     double[] b1,b2,b3;
		     double lr,im,dm,mc,er;
		     int Q; 
		     double SSE; 
		     //double[][] Erros;
		     double[][] d1,d2,d3,dPesosEntrada,dPesosEscondida,dPesosSaida;
		     double[] db1,db2,db3;
		     double[][] inputs,targets,Alvo;
		     double[][] ResultadoMultiplicacao; 
		     
		     int f,g,h,i,j,k;
		     
		     Pai.Rede.Momentum=0;


		     Entradas=new double[Pai.ColunaAmostra*Pai.LinhaAmostra][Pai.Amostras.length];
		     Alvo=new double[5][5];
		     for(f=0;f<5;f++){
			     Temp=Pai.Amostras[f].getMatrix();
			     for(g=0;g<Temp.length;g++){
				     Entradas[g][f]=Temp[g];
			     }
		     }
		     
		     Alvo=new double[5][5];
		     
		     for(f=0;f<5;f++){
			     for(g=0;g<5;g++){
				     Alvo[f][g]=0;
			     }
		     }
		     
		     
		     // Abaixo temos os alvos
		     
		     Alvo[0][0]=1;
		     Alvo[1][1]=1;
		     Alvo[2][2]=1;
		     Alvo[3][3]=1;
		     Alvo[4][4]=1;
		     
                     dPesosEntrada=new double[Pai.Rede.CamadaEntrada.Neuronios.length][Pai.LinhaAmostra*Pai.ColunaAmostra];
		     dPesosEscondida=new double[Pai.Rede.CamadaEscondida.Neuronios.length][Pai.Rede.CamadaEntrada.Neuronios.length];
		     dPesosSaida=new double[Pai.Rede.CamadaSaida.Neuronios.length][Pai.Rede.CamadaEscondida.Neuronios.length];
		     
		     db1=new double[Pai.Rede.CamadaEntrada.Neuronios.length];
		     db2=new double[Pai.Rede.CamadaEscondida.Neuronios.length];
		     db3=new double[Pai.Rede.CamadaSaida.Neuronios.length];
		     
		     Erros=new double[Pai.Rede.CamadaSaida.Neuronios.length][Pai.Amostras.length];
		     
		     PesosEntrada=Pai.Rede.CamadaEntrada.getMatrix();
		     PesosEscondida=Pai.Rede.CamadaEscondida.getMatrix();
		     PesosSaida=Pai.Rede.CamadaSaida.getMatrix();
		     
		     b1=Pai.Rede.CamadaEntrada.getBias();
		     b2=Pai.Rede.CamadaEscondida.getBias();
		     b3=Pai.Rede.CamadaSaida.getBias();
		     	     
		     ResultadoMultiplicacao = Utilitarias.MultiplicarMatriz(PesosEntrada,Entradas);
		     SaidaCamadaEntrada = Utilitarias.CalcularFuncaoAtivacao (ResultadoMultiplicacao,b1);
		     ResultadoMultiplicacao = Utilitarias.MultiplicarMatriz(PesosEscondida,SaidaCamadaEntrada);
		     SaidaCamadaEscondida = Utilitarias.CalcularFuncaoAtivacao (ResultadoMultiplicacao,b2);
		     ResultadoMultiplicacao = Utilitarias.MultiplicarMatriz(PesosSaida,SaidaCamadaEscondida);
		     SaidaCamadaSaida = Utilitarias.CalcularFuncaoAtivacao (ResultadoMultiplicacao,b3);

		     SSE=0;
		     for (i=0;i<Pai.Rede.CamadaSaida.Neuronios.length;i++) {
		       for (j=0;j<Pai.Amostras.length;j++) {
		 	 Erros[i][j] = Alvo[i][j] - SaidaCamadaSaida[i][j];
			 SSE += Erros[i][j]*Erros[i][j];
		       }
		     }

                     d3=Utilitarias.DeltaErro(SaidaCamadaSaida,Erros);
    		     d2=Utilitarias.DeltaErro(SaidaCamadaEscondida,d3,PesosSaida);
    		     d1=Utilitarias.DeltaErro(SaidaCamadaEntrada,d2,PesosEscondida);
 

		     while(true){
			     Pai.Rede.Epoca++;
		    double new_SSE;
		    double[][] new_SaidaCamadaEntrada,new_SaidaCamadaEscondida,new_SaidaCamadaSaida,new_Erros;
		    double[][] swap; 
		    double[] swap1;
		    new_Erros = new double[Pai.Rede.CamadaSaida.Neuronios.length][Pai.Amostras.length];
		
		
		    double new_PesosEntrada[][] = new double[Pai.Rede.CamadaEntrada.Neuronios.length][Pai.LinhaAmostra*Pai.ColunaAmostra];
		    double new_PesosEscondida[][] = new double[Pai.Rede.CamadaEscondida.Neuronios.length][Pai.Rede.CamadaEntrada.Neuronios.length];
		    double new_PesosSaida[][] = new double[Pai.Rede.CamadaSaida.Neuronios.length][Pai.Rede.CamadaEscondida.Neuronios.length];
		    double new_b1[] = new double[Pai.Rede.CamadaEntrada.Neuronios.length];
		    double new_b2[] = new double[Pai.Rede.CamadaEscondida.Neuronios.length];
		    double new_b3[] = new double[Pai.Rede.CamadaSaida.Neuronios.length];
		
		    for (i=0;i<Pai.Rede.CamadaEntrada.Neuronios.length;i++) {
		      for (j=0;j<Pai.LinhaAmostra*Pai.ColunaAmostra;j++) {
			dPesosEntrada[i][j] *= Pai.Rede.Momentum; 
			for (k=0;k<Pai.Amostras.length;k++) {
			  dPesosEntrada[i][j] += Pai.Rede.TaxaAprendizado * (1-Pai.Rede.Momentum) * d1[i][k] * Entradas[j][k];
			}
		      }
		    }

		    for (i=0;i<Pai.Rede.CamadaEntrada.Neuronios.length;i++) {
		      db1[i] *= Pai.Rede.Momentum; // momentum 
		      for (k=0;k<Pai.Amostras.length;k++) {
			db1[i] += Pai.Rede.TaxaAprendizado * (1-Pai.Rede.Momentum) * d1[i][k];
		      }
		    }

		    for (i=0;i<Pai.Rede.CamadaEscondida.Neuronios.length;i++) {
		      for (j=0;j<Pai.Rede.CamadaEntrada.Neuronios.length;j++) {
			dPesosEscondida[i][j] *= Pai.Rede.Momentum; 
			for (k=0;k<Pai.Amostras.length;k++) {
			  dPesosEscondida[i][j] += Pai.Rede.TaxaAprendizado * (1-Pai.Rede.Momentum) * d2[i][k] * SaidaCamadaEntrada[j][k];
			}
		      }
		    }
		    for (i=0;i<Pai.Rede.CamadaEscondida.Neuronios.length;i++) {
		      db2[i] *= Pai.Rede.Momentum;
		      for (k=0;k<Pai.Amostras.length;k++) {
			db2[i] += Pai.Rede.TaxaAprendizado * (1-Pai.Rede.Momentum) * d2[i][k];
		      }
		    }

		    for (i=0;i<Pai.Rede.CamadaSaida.Neuronios.length;i++) {
		      for (j=0;j<Pai.Rede.CamadaEscondida.Neuronios.length;j++) {
			dPesosSaida[i][j] *= Pai.Rede.Momentum; 
			for (k=0;k<Pai.Amostras.length;k++) {
			  dPesosSaida[i][j] += Pai.Rede.TaxaAprendizado * (1-Pai.Rede.Momentum) * d3[i][k] * SaidaCamadaEscondida[j][k];
			}
		      }
		    }

		    for (i=0;i<Pai.Rede.CamadaSaida.Neuronios.length;i++) {
		      db3[i] *= Pai.Rede.Momentum;
		      for (k=0;k<Pai.Amostras.length;k++) {
			db3[i] += Pai.Rede.TaxaAprendizado * (1-Pai.Rede.Momentum) * d3[i][k];
		      }
		    }

		    Pai.Rede.Momentum=Pai.Rede.MomentumCoeficiente;
		    for (i=0;i<Pai.Rede.CamadaEntrada.Neuronios.length;i++) {
		      new_b1[i] = b1[i] + db1[i];
		      for (j=0;j<Pai.LinhaAmostra*Pai.ColunaAmostra;j++) {
			new_PesosEntrada[i][j] = PesosEntrada[i][j] + dPesosEntrada[i][j];
		      }
		    }
		    for (i=0;i<Pai.Rede.CamadaEscondida.Neuronios.length;i++) {
		      new_b2[i] = b2[i] + db2[i];
		      for (j=0;j<Pai.Rede.CamadaEntrada.Neuronios.length;j++) {
			new_PesosEscondida[i][j] = PesosEscondida[i][j] + dPesosEscondida[i][j];
		      }
		    }
		    for (i=0;i<Pai.Rede.CamadaSaida.Neuronios.length;i++) {
		      new_b3[i] = b3[i] + db3[i];
		      for (j=0;j<Pai.Rede.CamadaEscondida.Neuronios.length;j++) {
			new_PesosSaida[i][j] = PesosSaida[i][j] + dPesosSaida[i][j];
		      }
		    }

		    ResultadoMultiplicacao = Utilitarias.MultiplicarMatriz(new_PesosEntrada,Entradas);
		    new_SaidaCamadaEntrada = Utilitarias.CalcularFuncaoAtivacao (ResultadoMultiplicacao,new_b1);
		    ResultadoMultiplicacao = Utilitarias.MultiplicarMatriz(new_PesosEscondida,new_SaidaCamadaEntrada);
		    new_SaidaCamadaEscondida = Utilitarias.CalcularFuncaoAtivacao (ResultadoMultiplicacao,new_b2);
		    ResultadoMultiplicacao= Utilitarias.MultiplicarMatriz(new_PesosSaida,new_SaidaCamadaEscondida);
		    new_SaidaCamadaSaida = Utilitarias.CalcularFuncaoAtivacao (ResultadoMultiplicacao,new_b3);
		    

		    new_SSE=0;
		    for (i=0;i<Pai.Rede.CamadaSaida.Neuronios.length;i++) {
		      for (j=0;j<Pai.Amostras.length;j++) {
			new_Erros[i][j] = Alvo[i][j] - new_SaidaCamadaSaida[i][j];
			new_SSE += new_Erros[i][j]*new_Erros[i][j];
		      }
		    }
		    System.out.println("TotalErro:"+new_SSE);
		    if (new_SSE > SSE* Pai.Rede.ErroMaximo) {
		      Pai.Rede.TaxaAprendizado *= Pai.Rede.DecrescimoTaxaAprendizado;
		      Pai.Rede.Momentum=0; //
		    }
		    else {
		      if (new_SSE < SSE) {
			Pai.Rede.TaxaAprendizado *= Pai.Rede.AcrescimoTaxaAprendizado;
		      }
		      swap=PesosEntrada; PesosEntrada=new_PesosEntrada; new_PesosEntrada=swap;
		      swap=PesosEscondida; PesosEscondida=new_PesosEscondida; new_PesosEscondida=swap;
		      swap=PesosSaida; PesosSaida=new_PesosSaida; new_PesosSaida=swap;
		      swap1=b1; b1=new_b1; new_b1=swap1;
		      swap1=b2; b2=new_b2; new_b2=swap1;
		      swap1=b3; b3=new_b3; new_b3=swap1;
		      swap=SaidaCamadaEntrada; SaidaCamadaEntrada=new_SaidaCamadaEntrada; new_SaidaCamadaEntrada=swap;
		      swap=SaidaCamadaEscondida; SaidaCamadaEscondida=new_SaidaCamadaEscondida; new_SaidaCamadaEscondida=swap;
		      swap=SaidaCamadaSaida; SaidaCamadaSaida=new_SaidaCamadaSaida; new_SaidaCamadaSaida=swap;
		      swap=Erros; Erros=new_Erros; new_Erros=swap;
		      SSE=new_SSE;
		    }
		    d3 = Utilitarias.DeltaErro(SaidaCamadaSaida,Erros);
		    d2 = Utilitarias.DeltaErro(SaidaCamadaEscondida,d3,PesosSaida);
		    d1 = Utilitarias.DeltaErro(SaidaCamadaEntrada,d2,PesosEscondida);
		    Pai.Rede.Erro=SSE;
		    if(SSE<0.000000001) { break; }
		    DadosCompartilhados.setAcrescimoTaxaAprendizado(Pai.Rede.AcrescimoTaxaAprendizado);
		    DadosCompartilhados.setDecrescimoTaxaAprendizado(Pai.Rede.DecrescimoTaxaAprendizado);
		    DadosCompartilhados.setErroMaximo(Pai.Rede.ErroMaximo);
		    DadosCompartilhados.setMomentumCoeficiente(Pai.Rede.MomentumCoeficiente);
		    DadosCompartilhados.setTaxaAprendizado(Pai.Rede.TaxaAprendizado);
		    DadosCompartilhados.setMomentum(Pai.Rede.Momentum);
		    DadosCompartilhados.setEpoca(Pai.Rede.Epoca);
		    DadosCompartilhados.setErro(Pai.Rede.Erro);
  	            }	

		    DadosCompartilhados.setAbandonar(true);
		    
		    for(h=0;h<5;h++){
			    for(j=0;j<5;j++){
				  System.out.println("h:"+h+" j:"+j+" Saida3:"+SaidaCamadaSaida[h][j]);    
			    }
		    }

		     Pai.Rede.CamadaEntrada.RepopularPesos(PesosEntrada);
		     Pai.Rede.CamadaEscondida.RepopularPesos(PesosEscondida);
		     Pai.Rede.CamadaSaida.RepopularPesos(PesosSaida);
		     
		     Salvar.PesosEntrada=PesosEntrada;
		     Salvar.PesosEscondida=PesosEscondida;
		     Salvar.PesosSaida=PesosSaida;
		     
		     Salvar.b1=b1;
		     Salvar.b2=b2;
		     Salvar.b3=b3;		     
		     
		     Pai.Rede.CamadaEntrada.RepopularBias(b1);
		     Pai.Rede.CamadaEscondida.RepopularBias(b2);
		     Pai.Rede.CamadaSaida.RepopularBias(b3);
		     System.out.println("Ok, aprendido");		     
	}
}

class Salvar {
	public static double[][] PesosEntrada,PesosEscondida,PesosSaida;
	public static double[] b1,b2,b3;
}

class DadosCompartilhados {
       static double m_AcrescimoTaxaAprendizado;
       static double m_DecrescimoTaxaAprendizado;
       static double m_ErroMaximo;
       static double m_MomentumCoeficiente;
       static double m_TaxaAprendizado;
       static double m_Momentum;
       static double m_Epoca;
       static double m_Erro;
       static boolean m_Abandonar;

public static  synchronized void setAbandonar(boolean Entrada){
       m_Abandonar=Entrada;
}

       
public static  synchronized void setAcrescimoTaxaAprendizado(double Entrada){
       m_AcrescimoTaxaAprendizado=Entrada;
}
public static  synchronized void setDecrescimoTaxaAprendizado(double Entrada){
       m_DecrescimoTaxaAprendizado=Entrada;
}
public static  synchronized void setErroMaximo(double Entrada){
       m_ErroMaximo=Entrada;
}
public static  synchronized void setMomentumCoeficiente(double Entrada){
       m_MomentumCoeficiente=Entrada;
}
public static  synchronized void setTaxaAprendizado(double Entrada){
       m_TaxaAprendizado=Entrada;
}
public static  synchronized void setMomentum(double Entrada){
       m_Momentum=Entrada;
}
public static  synchronized void setEpoca(double Entrada){
       m_Epoca=Entrada;
}
public static synchronized void setErro(double Entrada){
       m_Erro=Entrada;
}

public static  synchronized double getAcrescimoTaxaAprendizado(){
       return m_AcrescimoTaxaAprendizado;
}
public static  synchronized double getDecrescimoTaxaAprendizado(){
       return m_DecrescimoTaxaAprendizado;
}
public static  synchronized double getErroMaximo(){
       return m_ErroMaximo;
}
public static  synchronized double getMomentumCoeficiente(){
       return m_MomentumCoeficiente;
}
public static  synchronized double getTaxaAprendizado(){
       return m_TaxaAprendizado;
}
public static  synchronized double getMomentum(){
       return m_Momentum;
}
public static  synchronized double getEpoca(){
       return m_Epoca;
}
public static synchronized double getErro(){
       return m_Erro;
}

public static synchronized boolean getAbandonar(){
       return m_Abandonar;
}


}

class Atualizador extends Thread {
	FramePrincipal Pai;
	
	public Atualizador(FramePrincipal Alvo){
		Pai=Alvo;
        }
	
	public void run(){
		while(true){
			DecimalFormat Formatador=new DecimalFormat("#.###########");
			
			Pai.txtErro.setText(Formatador.format(DadosCompartilhados.getErro()));		     
		        Pai.txtEpoca.setText(Formatador.format(DadosCompartilhados.getEpoca()));
			Pai.txtMomentum.setText(Formatador.format(DadosCompartilhados.getMomentum()));
			Pai.txtTaxaAprendizado.setText(Formatador.format(DadosCompartilhados.getTaxaAprendizado()));
			Pai.txtMomentumCoeficiente.setText(Formatador.format(DadosCompartilhados.getMomentumCoeficiente()));
			Pai.txtErroMaximo.setText(Formatador.format(DadosCompartilhados.getErroMaximo()));
			Pai.txtDecrescimoTaxaAprendizado.setText(Formatador.format(DadosCompartilhados.getDecrescimoTaxaAprendizado()));
			Pai.txtAcrescimoTaxaAprendizado.setText(Formatador.format(DadosCompartilhados.getAcrescimoTaxaAprendizado()));
			


			if(DadosCompartilhados.getAbandonar()){ break;} 
			
			Pai.txtErro.repaint();
			Pai.txtEpoca.repaint();
			Pai.txtMomentum.repaint();
			Pai.txtTaxaAprendizado.repaint();
			Pai.txtMomentumCoeficiente.repaint();
			Pai.txtErroMaximo.repaint();
			Pai.txtDecrescimoTaxaAprendizado.repaint();
			Pai.txtAcrescimoTaxaAprendizado.repaint();
		}
	}
}

class ActionListenerAprender implements ActionListener {
	FramePrincipal Alvo;
	
	public ActionListenerAprender(FramePrincipal FrameAlvo){
		Alvo=FrameAlvo;
	}

	public void actionPerformed(ActionEvent e){
		Double Lido[][];
		System.out.println("OkActionOk");

		if(Alvo.Carregado){
		   Alvo.Aprender();
		}else{
		   System.out.println("Nao esta carregado");
		}
		

	}
}     


class ActionListenerTestar implements ActionListener {
	FramePrincipal Alvo;
	
	public ActionListenerTestar(FramePrincipal FrameAlvo){
		Alvo=FrameAlvo;
	}

	public void actionPerformed(ActionEvent e){
		Double Lido[][];
		String Arquivos[];
		BufferedReader in;
		int f,NumeroLidos,NumeroOk;
		
		File Diretorio=new File(Alvo.txtCaminhoTeste.getText());
		Arquivos=Diretorio.list();
		NumeroLidos=0;
		try {
		    for(f=0;f<Arquivos.length;f++){
			    if(Arquivos[f].indexOf("Teste")==0){
			       int Inicio,Final,CharLido;
			       int Linha,Coluna;
				    
			       in=new BufferedReader(new FileReader(Alvo.txtCaminhoAmostras.getText()+Arquivos[f]));
			       Linha=0;Coluna=0;
			       while(true){
				       CharLido=in.read();
				       if(CharLido==-1){
					       break;
				       }else{
					       if(CharLido==48||CharLido==49){
						       Alvo.TesteAmostra.Modelo.Valor[Coluna][Linha]=new Double(CharLido-48);
							   System.out.println("AmostraTeste.Valor["+Coluna+"]["+Linha+ "]=new Double("+(CharLido-48)+");");						       
						       Coluna++;
					       }
					       if(CharLido==10){
						       Coluna=0;
						       Linha++;
					       }
					       
				       }
			       } 
				       
			       NumeroLidos++;		       
			       if(NumeroLidos==6){
				       break;
			       }
			    }
		    }
		}catch(IOException ex){
			System.out.println(ex);
		}
		Alvo.TesteAmostra.repaint();
		
		double MatrizTesteTemp[],MatrizTeste[][];
		int g;
		
		MatrizTesteTemp=new double[Alvo.ColunaAmostra*Alvo.LinhaAmostra];
		MatrizTesteTemp=Alvo.Amostras[1].getMatrix();
		
		MatrizTeste=new double[Alvo.ColunaAmostra*Alvo.LinhaAmostra][1];
		MatrizTesteTemp=Alvo.TesteAmostra.getMatrix();
		
		for(f=0;f<MatrizTesteTemp.length;f++){
			MatrizTeste[f][0]=MatrizTesteTemp[f];
		}
		
		
		double[][] MatrizTempTeste1,MatrizTempSaida1,MatrizTempSaida2,MatrizTempSaida3;
		double[][] PesosMatriz1,PesosMatriz2,PesosMatriz3;
		double[]   BiasMatriz1,BiasMatriz2,BiasMatriz3;
		
		
		PesosMatriz1=Alvo.Rede.CamadaEntrada.getMatrix();
		PesosMatriz2=Alvo.Rede.CamadaEscondida.getMatrix();
		PesosMatriz3=Alvo.Rede.CamadaSaida.getMatrix();
		
		BiasMatriz1=Alvo.Rede.CamadaEntrada.getBias();
		BiasMatriz2=Alvo.Rede.CamadaEscondida.getBias();
		BiasMatriz3=Alvo.Rede.CamadaSaida.getBias();		
		
		MatrizTempTeste1=Utilitarias.MultiplicarMatriz(Salvar.PesosEntrada,MatrizTeste);
		MatrizTempSaida1=Utilitarias.CalcularFuncaoAtivacao(MatrizTempTeste1,Salvar.b1);

		MatrizTempTeste1=Utilitarias.MultiplicarMatriz(Salvar.PesosEscondida,MatrizTempSaida1);
		MatrizTempSaida2=Utilitarias.CalcularFuncaoAtivacao(MatrizTempTeste1,Salvar.b2);

		MatrizTempTeste1=Utilitarias.MultiplicarMatriz(Salvar.PesosSaida,MatrizTempSaida2);
		MatrizTempSaida3=Utilitarias.CalcularFuncaoAtivacao(MatrizTempTeste1,Salvar.b3);
		
		int[] tamanho;

		for(f=0;f<5;f++){
			for(g=0;g<1;g++){
				System.out.println("f:"+f+" g:"+g+" res:"+MatrizTempSaida3[f][g]);
			}
		}
		DecimalFormat Formatador=new DecimalFormat("#.#######");
		for(f=0;f<5;f++){
		    Alvo.txtSimilaridade[f].setText(Formatador.format(MatrizTempSaida3[f][0]));
		}
		
		
		
	}
}     


public class FramePrincipal extends JFrame {
	            JPanel       PainelNorte;
	            JPanel       PainelSul;
	            JPanel       PainelNorteAmostras;
	            JPanel       PainelSulOeste;
	            JPanel       PainelSulLeste;
		    JPanel       PainelNorteDummy;
	            JLabel       CaminhoAmostras;
	     public JTextField   txtCaminhoAmostras;
	     public JTextField   txtCaminhoTeste;
	     public TableAmostra Amostras[];
	     public JTextField   txtSimilaridade[];
	     public TableAmostra TesteAmostra;
	     public JButton      btnCarregarAmostra;
	     public JButton      btnAprenderAmostra;
	     public RedeNeural   Rede;
	     public JTextField txtEpoca;
	     public JTextField txtMomentum;
	     public JTextField txtTaxaAprendizado;
	     public JTextField txtMomentumCoeficiente;
	     public JTextField txtErroMaximo;
	     public JTextField txtDecrescimoTaxaAprendizado;
	     public JTextField txtAcrescimoTaxaAprendizado;
	     public JTextField txtErro;
	     public JButton btnTestarAmostra;	     

      	     public static int    ColunaAmostra=11,LinhaAmostra=8;     
	     public boolean Carregado;
    
 	     FramePrincipal() {
		  int f;   
		  
		  Carregado=false;
		  setTitle("Trabalho de G1 - Rede Neural com Backpropagation - Guilherme,Alex,Carlos");
		  Rede=new RedeNeural(ColunaAmostra*LinhaAmostra,5,10,5,5);

     	          setVisible(true);
		  setBounds(1,1,500,470);

		  PainelNorte=new JPanel();
		  PainelSul=new JPanel();
		  PainelNorteAmostras=new JPanel();
		  PainelSulOeste=new JPanel();
		  PainelSulLeste=new JPanel();
		  PainelNorteDummy=new JPanel();

		  getContentPane().setLayout(new BorderLayout());
		  getContentPane().add(PainelNorte,BorderLayout.NORTH);
		  getContentPane().add(PainelSul,BorderLayout.CENTER);
		  PainelNorte.setLayout(new BorderLayout());
		  PainelSul.setLayout(new BorderLayout());
		  CaminhoAmostras=new JLabel("Entre Caminho Amostras");
		  txtCaminhoAmostras=new JTextField();
		  txtCaminhoTeste=new JTextField();
		  txtCaminhoTeste.setText("C:\\");
		  txtCaminhoAmostras.setText("C:\\");
		  PainelNorte.add(CaminhoAmostras,BorderLayout.NORTH);
		  PainelNorte.add(txtCaminhoAmostras,BorderLayout.CENTER);
		  PainelNorte.add(PainelNorteDummy,BorderLayout.SOUTH);
		  PainelNorteDummy.add(PainelNorteAmostras,BorderLayout.NORTH);

		  Amostras=new TableAmostra[5];
		  txtSimilaridade=new JTextField[5];
		  TesteAmostra=new TableAmostra(11,8);
		  PainelNorteAmostras.setLayout(new FlowLayout());
		  JPanel Dummy;
		  
                  for(f=0;f<5;f++){
			  Dummy=new JPanel();
			  Dummy.setLayout(new BorderLayout());
                          Amostras[f]=new TableAmostra(11,8);
			  txtSimilaridade[f]=new JTextField("0.00");
			  Dummy.add(Amostras[f],BorderLayout.NORTH);
			  Dummy.add(txtSimilaridade[f],BorderLayout.SOUTH);
			  Dummy.setAlignmentX(CENTER_ALIGNMENT);
    			  PainelNorteAmostras.add(Dummy);
		  }

		  
                  JPanel PainelSulLesteDummy;
                  JPanel PainelSulLesteDummy2;
		  
		  
		  PainelSulLesteDummy=new JPanel();
		  PainelSulLesteDummy2=new JPanel();

		  PainelSul.add(PainelSulOeste,BorderLayout.WEST);
		  PainelSul.add(PainelSulLeste,BorderLayout.EAST);
		  
		  PainelSulLeste.setLayout(new BorderLayout());
		  PainelSulLeste.add(PainelSulLesteDummy,BorderLayout.NORTH);
		  PainelSulLesteDummy.setLayout(new BorderLayout());
		  
		  PainelSulLesteDummy.add(new JLabel("Caminho Teste:                                     "),BorderLayout.NORTH); 
		  PainelSulLesteDummy.add(txtCaminhoTeste,BorderLayout.CENTER);		 
		  PainelSulLesteDummy2.setLayout(new BorderLayout());
		  PainelSulLesteDummy2.add(TesteAmostra,BorderLayout.CENTER);
		  PainelSulLesteDummy2.add(new JLabel("                     "),BorderLayout.WEST);
		  PainelSulLesteDummy2.add(new JLabel("                     "),BorderLayout.EAST);
		  btnTestarAmostra=new JButton("Testar Acima");
		  PainelSulLesteDummy2.add(btnTestarAmostra,BorderLayout.SOUTH);
		  
		  PainelSulLesteDummy.add(PainelSulLesteDummy2,BorderLayout.SOUTH);		  
		  PainelSulOeste.setLayout(new GridLayout(10,2));
		  PainelSulOeste.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		  btnCarregarAmostra=new JButton("Carregar");
		  btnAprenderAmostra=new JButton("Aprender");
		  btnAprenderAmostra.setEnabled(false);
		  
		  
		  btnCarregarAmostra.addActionListener(new ActionListenerCarregar(this));
		  btnTestarAmostra.addActionListener(new ActionListenerTestar(this));		  
		  btnAprenderAmostra.addActionListener(new ActionListenerAprender(this));
		  PainelSulOeste.add(btnCarregarAmostra);		  
		  PainelSulOeste.add(btnAprenderAmostra);		  
	  
		PainelSulOeste.add(new JLabel("ErroAtual:"));
		txtErro=new JTextField(new Double(Rede.Erro).toString());
		PainelSulOeste.add(txtErro);
		
		PainelSulOeste.add(new JLabel("Epoca:"));
		txtEpoca=new JTextField(new Double(Rede.Epoca).toString());
		PainelSulOeste.add(txtEpoca);
		
		PainelSulOeste.add(new JLabel("Momentum:"));
		txtMomentum=new JTextField(new Double(Rede.Momentum).toString());
		txtMomentum.setAlignmentX(RIGHT_ALIGNMENT);
		PainelSulOeste.add(txtMomentum);		
		
		PainelSulOeste.add(new JLabel("TaxaAprendizado:"));
		txtTaxaAprendizado=new JTextField(new Double(Rede.TaxaAprendizado).toString());
		PainelSulOeste.add(txtTaxaAprendizado);
		
		PainelSulOeste.add(new JLabel("Momentum Coef.:"));
		txtMomentumCoeficiente=new JTextField(new Double(Rede.MomentumCoeficiente).toString());
		PainelSulOeste.add(txtMomentumCoeficiente);
		
		PainelSulOeste.add(new JLabel("Erro Maximo:"));
		txtErroMaximo=new JTextField(new Double(Rede.ErroMaximo).toString());
		PainelSulOeste.add(txtErroMaximo);
		
		PainelSulOeste.add(new JLabel("(-)Taxa Aprend:"));
		txtDecrescimoTaxaAprendizado=new JTextField(new Double(Rede.DecrescimoTaxaAprendizado).toString());
		PainelSulOeste.add(txtDecrescimoTaxaAprendizado);
		
		PainelSulOeste.add(new JLabel("(+)Taxa Aprend:"));
		txtAcrescimoTaxaAprendizado=new JTextField(new Double(Rede.AcrescimoTaxaAprendizado).toString());
		PainelSulOeste.add(txtAcrescimoTaxaAprendizado);
		  
	     }

	     public void PopularAmostra(Double entrada[][]){

	     }
	     public void Aprender(){
		     Calcular Cal;
		     Atualizador Atu;
		    
		     DadosCompartilhados.setAbandonar(false);
		     Cal=new Calcular(this);
		     Atu=new Atualizador(this);
		     
		     try{
			  Thread.sleep(2000);     
		     }catch(Exception e){
			     
		     }
		     
		     Cal.start();
		     Atu.start();
	     }

	     public void AtualizarCampos(){
		        System.out.println("Ok, Atualizar");
		        txtErro.setText(new Double(Rede.Erro).toString());		     
		        txtEpoca.setText(new Double(Rede.Epoca).toString());
			txtMomentum.setText(new Double(Rede.Momentum).toString());
			txtTaxaAprendizado.setText(new Double(Rede.TaxaAprendizado).toString());
			txtMomentumCoeficiente.setText(new Double(Rede.MomentumCoeficiente).toString());
			txtErroMaximo.setText(new Double(Rede.ErroMaximo).toString());
			txtDecrescimoTaxaAprendizado.setText(new Double(Rede.DecrescimoTaxaAprendizado).toString());
			txtAcrescimoTaxaAprendizado.setText(new Double(Rede.AcrescimoTaxaAprendizado).toString());
			
			txtErro.repaint();
			txtEpoca.repaint();
			txtMomentum.repaint();
			txtTaxaAprendizado.repaint();
			txtMomentumCoeficiente.repaint();
			txtErroMaximo.repaint();
			txtDecrescimoTaxaAprendizado.repaint();
			txtAcrescimoTaxaAprendizado.repaint();

		     
	     }
	     
	     
}
