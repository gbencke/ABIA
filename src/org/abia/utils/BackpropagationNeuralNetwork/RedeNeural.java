package org.abia.utils.BackpropagationNeuralNetwork;

import java.io.*;
import java.util.*;

/** Essa Classe representa a RedeNeural que o nosso trabalho ira utilizar */

class Salvar {
	public static double[][] PesosEntrada,PesosEscondida,PesosSaida;
	public static double[] b1,b2,b3;
}


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

public class RedeNeural {
       /** A Camada de Entrada de nossa Rede Neural, veja a classe CamadaNeural para maiores informacoes*/
       protected CamadaNeural CamadaEntrada;
       /** A Camada Escondida de nossa Rede Neural, veja a classe CamadaNeural para maiores informacoes*/
	   protected CamadaNeural CamadaEscondida;
       /** A Camada de Saida de nossa Rede Neural, veja a classe CamadaNeural para maiores informacoes*/
	   protected CamadaNeural CamadaSaida;

       /** A Epoca atual de nossa Rede Neural*/
	   protected int    Epoca;
       /** O Valor de Momentum que nossa RedeNeural Utiliza*/
	   protected double Momentum;
       /** Valor Quadratico de Erro de nossa rede neural*/
	   protected double Erro;
       /** A Taxa de Aprendizado de nossa RedeNeural*/
	   protected double TaxaAprendizado;
       /** O Coeficiente de Momentum de nossa Rede Neural*/
	   protected double MomentumCoeficiente;
       /** O Valor do Erro Maximo tolerado por nossa Rede*/
	   protected double ErroMaximo;
       /** A Taxa de Ajuste de Decrescimo da Taxa de Aprendizado*/
	   protected double DecrescimoTaxaAprendizado;
       /** A Taxa de Ajuste de Acrescimo da Taxa de Aprendizado*/
	   protected double AcrescimoTaxaAprendizado;
	   	       	   
	   protected int                ColunaAmostra;
	   protected int                LinhaAmostra;
	   protected int                nAmostras;
	   protected AmostraRedeNeural  Amostras[];
	   protected double             Alvo[][];

       /** O Construtor de nossa Classe que toma o Numero de Entradas da Camada de Entrada, o Numero de Neuronios
           de entrada que nossa rede deve possuir, o numero de neuronios escondidos, e o numero de amostras que serao
	   apresentados como valores de saida durante a fase de aprendizado */
	
	   public void Aprender(){
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
			double[][] inputs,targets;
			double[][] ResultadoMultiplicacao; 
		     
			int f,g,h,i,j,k;
		     
			Momentum=0;

			Entradas=new double[ColunaAmostra*LinhaAmostra][Amostras.length];

			for(f=0;f<Amostras.length;f++){
				Temp=Amostras[f].getMatrix();
				for(g=0;g<Temp.length;g++){
					Entradas[g][f]=Temp[g];
				}
			}
		     		     
			dPesosEntrada=new double[CamadaEntrada.Neuronios.length][LinhaAmostra*ColunaAmostra];
			dPesosEscondida=new double[CamadaEscondida.Neuronios.length][CamadaEntrada.Neuronios.length];
			dPesosSaida=new double[CamadaSaida.Neuronios.length][CamadaEscondida.Neuronios.length];
		     
			db1=new double[CamadaEntrada.Neuronios.length];
			db2=new double[CamadaEscondida.Neuronios.length];
			db3=new double[CamadaSaida.Neuronios.length];
		     
			Erros=new double[CamadaSaida.Neuronios.length][Amostras.length];
		     
			PesosEntrada=CamadaEntrada.getMatrix();
			PesosEscondida=CamadaEscondida.getMatrix();
			PesosSaida=CamadaSaida.getMatrix();
		     
			b1=CamadaEntrada.getBias();
			b2=CamadaEscondida.getBias();
			b3=CamadaSaida.getBias();
		     	     
			ResultadoMultiplicacao = Utilitarias.MultiplicarMatriz(PesosEntrada,Entradas);
			SaidaCamadaEntrada = Utilitarias.CalcularFuncaoAtivacao (ResultadoMultiplicacao,b1);
			ResultadoMultiplicacao = Utilitarias.MultiplicarMatriz(PesosEscondida,SaidaCamadaEntrada);
			SaidaCamadaEscondida = Utilitarias.CalcularFuncaoAtivacao (ResultadoMultiplicacao,b2);
			ResultadoMultiplicacao = Utilitarias.MultiplicarMatriz(PesosSaida,SaidaCamadaEscondida);
			SaidaCamadaSaida = Utilitarias.CalcularFuncaoAtivacao (ResultadoMultiplicacao,b3);

			SSE=0;
			for (i=0;i<CamadaSaida.Neuronios.length;i++) {
		  		for (j=0;j<Amostras.length;j++) {
					Erros[i][j] = Alvo[i][j] - SaidaCamadaSaida[i][j];
					SSE += Erros[i][j]*Erros[i][j];
		  		}
			}

			d3=Utilitarias.DeltaErro(SaidaCamadaSaida,Erros);
			d2=Utilitarias.DeltaErro(SaidaCamadaEscondida,d3,PesosSaida);
			d1=Utilitarias.DeltaErro(SaidaCamadaEntrada,d2,PesosEscondida);
 
		while(true){
			  Epoca++;
			  double new_SSE;
	   		  double[][] new_SaidaCamadaEntrada,new_SaidaCamadaEscondida,new_SaidaCamadaSaida,new_Erros;
	   		  double[][] swap; 
	   		  double[] swap1;
	   		  new_Erros = new double[CamadaSaida.Neuronios.length][Amostras.length];
		
	   		  double new_PesosEntrada[][] = new double[CamadaEntrada.Neuronios.length][LinhaAmostra*ColunaAmostra];
	   		  double new_PesosEscondida[][] = new double[CamadaEscondida.Neuronios.length][CamadaEntrada.Neuronios.length];
	   		  double new_PesosSaida[][] = new double[CamadaSaida.Neuronios.length][CamadaEscondida.Neuronios.length];
	   		  double new_b1[] = new double[CamadaEntrada.Neuronios.length];
	   		  double new_b2[] = new double[CamadaEscondida.Neuronios.length];
	   		  double new_b3[] = new double[CamadaSaida.Neuronios.length];
		
	   		  for (i=0;i<CamadaEntrada.Neuronios.length;i++) {
		           for (j=0;j<LinhaAmostra*ColunaAmostra;j++) {
	   					dPesosEntrada[i][j] *= Momentum; 
	   					   for (k=0;k<Amostras.length;k++) {
		 						dPesosEntrada[i][j] += TaxaAprendizado * (1-Momentum) * d1[i][k] * Entradas[j][k];
	   					   }
		 		   }
	   			}

	   for (i=0;i<CamadaEntrada.Neuronios.length;i++) {
		 db1[i] *= Momentum; // momentum 
		 for (k=0;k<Amostras.length;k++) {
	   db1[i] += TaxaAprendizado * (1-Momentum) * d1[i][k];
		 }
	   }

	   for (i=0;i<CamadaEscondida.Neuronios.length;i++) {
		 for (j=0;j<CamadaEntrada.Neuronios.length;j++) {
	   dPesosEscondida[i][j] *= Momentum; 
	   for (k=0;k<Amostras.length;k++) {
		 dPesosEscondida[i][j] += TaxaAprendizado * (1-Momentum) * d2[i][k] * SaidaCamadaEntrada[j][k];
	   }
		 }
	   }
	   for (i=0;i<CamadaEscondida.Neuronios.length;i++) {
		 db2[i] *= Momentum;
		 for (k=0;k<Amostras.length;k++) {
	   db2[i] += TaxaAprendizado * (1-Momentum) * d2[i][k];
		 }
	   }

	   for (i=0;i<CamadaSaida.Neuronios.length;i++) {
		 for (j=0;j<CamadaEscondida.Neuronios.length;j++) {
	   dPesosSaida[i][j] *= Momentum; 
	   for (k=0;k<Amostras.length;k++) {
		 dPesosSaida[i][j] += TaxaAprendizado * (1-Momentum) * d3[i][k] * SaidaCamadaEscondida[j][k];
	   }
		 }
	   }

	   for (i=0;i<CamadaSaida.Neuronios.length;i++) {
		 db3[i] *= Momentum;
		 for (k=0;k<Amostras.length;k++) {
	   db3[i] += TaxaAprendizado * (1-Momentum) * d3[i][k];
		 }
	   }

	   Momentum=MomentumCoeficiente;
	   for (i=0;i<CamadaEntrada.Neuronios.length;i++) {
		 new_b1[i] = b1[i] + db1[i];
		 for (j=0;j<LinhaAmostra*ColunaAmostra;j++) {
	   new_PesosEntrada[i][j] = PesosEntrada[i][j] + dPesosEntrada[i][j];
		 }
	   }
	   for (i=0;i<CamadaEscondida.Neuronios.length;i++) {
		 new_b2[i] = b2[i] + db2[i];
		 for (j=0;j<CamadaEntrada.Neuronios.length;j++) {
	   new_PesosEscondida[i][j] = PesosEscondida[i][j] + dPesosEscondida[i][j];
		 }
	   }
	   for (i=0;i<CamadaSaida.Neuronios.length;i++) {
		 new_b3[i] = b3[i] + db3[i];
		 for (j=0;j<CamadaEscondida.Neuronios.length;j++) {
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
	   for (i=0;i<CamadaSaida.Neuronios.length;i++) {
		 for (j=0;j<Amostras.length;j++) {
	          new_Erros[i][j] = Alvo[i][j] - new_SaidaCamadaSaida[i][j];
	          new_SSE += new_Erros[i][j]*new_Erros[i][j];
		 }
	   }
	   System.out.println("TotalErro:"+new_SSE);
	   if ( (new_SSE > (SSE* ErroMaximo)) ) {
		 TaxaAprendizado *= DecrescimoTaxaAprendizado;
		 Momentum=0; //
	   }else {
		 if (new_SSE < SSE) 
	         TaxaAprendizado *= AcrescimoTaxaAprendizado;		
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
	   if(SSE<0.000000001) { break; }

		   }	

		    
	   for(h=0;h<CamadaSaida.Neuronios.length;h++){
		   for(j=0;j<Amostras.length;j++){
			 System.out.println("h:"+h+" j:"+j+" Saida3:"+SaidaCamadaSaida[h][j]);    
		   }
	   }

		CamadaEntrada.RepopularPesos(PesosEntrada);
		CamadaEscondida.RepopularPesos(PesosEscondida);
		CamadaSaida.RepopularPesos(PesosSaida);
		     
		Salvar.PesosEntrada=PesosEntrada;
		Salvar.PesosEscondida=PesosEscondida;
		Salvar.PesosSaida=PesosSaida;
		     
		Salvar.b1=b1;
		Salvar.b2=b2;
		Salvar.b3=b3;		     
		     
		CamadaEntrada.RepopularBias(b1);
		CamadaEscondida.RepopularBias(b2);
		CamadaSaida.RepopularBias(b3);
		System.out.println("Ok, aprendido");		     
	   	
	   }
	   
	   public void Carregar(AmostraRedeNeural newAmostras[],double saida[][]){
			Double Lido[][];
			Random rnd;
			
			int f,NumeroLidos,NumeroOk,g;
		    
		    Alvo=saida;			
			try {
				rnd=new Random();
			  	for(f=0;f<CamadaEntrada.Neuronios.length;f++){
					for(g=0;g<CamadaEntrada.Neuronios[f].Pesos.length;g++){
						CamadaEntrada.Neuronios[f].Pesos[g]=rnd.nextDouble();   				    
					}
			  	}
			  	for(f=0;f<CamadaEscondida.Neuronios.length;f++){
					for(g=0;g<CamadaEscondida.Neuronios[f].Pesos.length;g++){
						CamadaEscondida.Neuronios[f].Pesos[g]=rnd.nextDouble(); 
					}
			  	}
			  	for(f=0;f<CamadaSaida.Neuronios.length;f++){
					for(g=0;g<CamadaSaida.Neuronios[f].Pesos.length;g++){
						CamadaSaida.Neuronios[f].Pesos[g]=rnd.nextDouble(); 
					}
			  	}
			}catch(Exception e){
					
			}
			
			Amostras=newAmostras;
			nAmostras=newAmostras.length;
			System.out.println("Carregado");			
	   }
	   
	   public void Testar(AmostraRedeNeural TesteAmostra){
			Double     Lido[][];
			int        f,NumeroLidos,NumeroOk;		
			double[][] MatrizTempTeste1,MatrizTempSaida1,MatrizTempSaida2,MatrizTempSaida3;
			double[][] PesosMatriz1,PesosMatriz2,PesosMatriz3;
			double[]   BiasMatriz1,BiasMatriz2,BiasMatriz3;
			int[]      tamanho;
			double     MatrizTesteTemp[],MatrizTeste[][];
			int g;
				
			MatrizTeste=new double[ColunaAmostra*LinhaAmostra][1];
			MatrizTesteTemp=TesteAmostra.getMatrix();
		
			for(f=0;f<MatrizTesteTemp.length;f++)
				MatrizTeste[f][0]=MatrizTesteTemp[f];
		
			PesosMatriz1=CamadaEntrada.getMatrix();
			PesosMatriz2=CamadaEscondida.getMatrix();
			PesosMatriz3=CamadaSaida.getMatrix();
		
			BiasMatriz1=CamadaEntrada.getBias();
			BiasMatriz2=CamadaEscondida.getBias();
			BiasMatriz3=CamadaSaida.getBias();		
		
			MatrizTempTeste1=Utilitarias.MultiplicarMatriz(Salvar.PesosEntrada,MatrizTeste);
			MatrizTempSaida1=Utilitarias.CalcularFuncaoAtivacao(MatrizTempTeste1,Salvar.b1);

			MatrizTempTeste1=Utilitarias.MultiplicarMatriz(Salvar.PesosEscondida,MatrizTempSaida1);
			MatrizTempSaida2=Utilitarias.CalcularFuncaoAtivacao(MatrizTempTeste1,Salvar.b2);

			MatrizTempTeste1=Utilitarias.MultiplicarMatriz(Salvar.PesosSaida,MatrizTempSaida2);
			MatrizTempSaida3=Utilitarias.CalcularFuncaoAtivacao(MatrizTempTeste1,Salvar.b3);

			for(f=0;f<CamadaSaida.Neuronios.length;f++){
				for(g=0;g<1;g++){
					System.out.println("f:"+f+" g:"+g+" res:"+MatrizTempSaida3[f][g]);
				}
			}
	   }
	   
	   public void setMomentum(double newMomentum)                                  { Momentum=newMomentum;}
	   public void setTaxaAprendizado(double newTaxaAprendizado)                    { TaxaAprendizado=newTaxaAprendizado;}
	   public void setMomentumCoeficiente(double newMomentumCoeficiente)            { MomentumCoeficiente=newMomentumCoeficiente;}
	   public void setErroMaximo(double newErroMaximo)                              { ErroMaximo=newErroMaximo;}
	   public void setDecrescimoTaxaAprendizado(double newDecrescimoTaxaAprendizado){ DecrescimoTaxaAprendizado=newDecrescimoTaxaAprendizado;}
	   public void setAcrescimoTaxaAprendizado(double newAcrescimoTaxaAprendizado)  { AcrescimoTaxaAprendizado=newAcrescimoTaxaAprendizado;}
	       
	   public int getNumberAmostras(){ return nAmostras; }
	   
	   public double getMomentum()                 { return Momentum;}
	   public double getTaxaAprendizado()          { return TaxaAprendizado;}
	   public double getMomentumCoeficiente()      { return MomentumCoeficiente;}
	   public double getErroMaximo()               { return ErroMaximo;}
	   public double getDecrescimoTaxaAprendizado(){ return DecrescimoTaxaAprendizado;}
	   public double getAcrescimoTaxaAprendizado() { return AcrescimoTaxaAprendizado;}

	   public  int    getColunaAmostra(){	return ColunaAmostra; }
	   public  int    getLinhaAmostra() {	return LinhaAmostra;  }
	   
	   public  void   setColunaAmostra(int newColunaAmostra) {	ColunaAmostra=newColunaAmostra;  }
	   public  void   setLinhaAmostra (int newLinhaAmostra)  {	LinhaAmostra=newLinhaAmostra;    }

       public RedeNeural(int Entradas,int NeuroniosEntrada,int NeuroniosEscondidos,int NeuroniosSaida,int NumeroAmostras){
	       int f,g;

	       /*Epoca=0;
	       Momentum=0;
	       TaxaAprendizado=0.01;
	       MomentumCoeficiente=0.9;
	       ErroMaximo=1.02;
	       DecrescimoTaxaAprendizado=0.7;
	       AcrescimoTaxaAprendizado=1.05;*/
		   
		   ColunaAmostra=5;
		   LinhaAmostra=1;		   
		   
		   Epoca=0;
		   Momentum=0;
		   TaxaAprendizado=0.01;
		   MomentumCoeficiente=0.9;
		   ErroMaximo=1.02;
		   DecrescimoTaxaAprendizado=0.7;
		   AcrescimoTaxaAprendizado=1.05;
			
	       
	       CamadaEntrada=new CamadaNeural(NeuroniosEntrada,Entradas,this);
	       CamadaEscondida=new CamadaNeural(NeuroniosEscondidos,NeuroniosEntrada,this);
	       CamadaSaida=new CamadaNeural(NeuroniosSaida,NeuroniosEscondidos,this);
		   
		   Amostras=new AmostraRedeNeural[NumeroAmostras];
		   for(f=0;f<NumeroAmostras;f++){
		   	   Amostras[f]=new AmostraRedeNeural(ColunaAmostra,LinhaAmostra,this);
		   }	       
       }

       /** Esse metodo multiplica a matriz de EntradasXPesos por ValoresEntradasXEntrada afim de determinar o 
           valor total de entradas por pesos */
       
       public double[][] CalcularEntradasPorPesos(double Pesos[][],double Entradas[][],int NumeroEntradas,int NumeroAmostras,int NumeroNeuronios){
	       double Retorno[][];
	       int f,g,h;

	       Retorno=new double[NumeroNeuronios][NumeroAmostras];

	       for(f=0;f<NumeroNeuronios;f++){
		       for(g=0;g<NumeroAmostras;g++){
			       Retorno[f][g]=0;
			       for(h=0;h<NumeroEntradas;h++){
				       Retorno[f][g]=Retorno[f][g]+(Pesos[f][h]*Entradas[h][g]);
			       }
		       }
	       }
	       
	       return Retorno;
       }

       /** Esse metodo calcula a funcao de ativacao de uma camada da Rede */
       
       public double[][] CalcularFuncaoAtivacao(double EntradaCalculada[][],double Bias[],int NumeroNeuronios,int NumeroAmostras){
	       double retorno[][];
	       int f,g;

	       retorno=new double[NumeroNeuronios][NumeroAmostras];
	       for(f=0;f<NumeroNeuronios;f++){
		       for(g=0;g<NumeroAmostras;g++){
			       EntradaCalculada[f][g]+=Bias[f];
			       retorno[f][g]=1/(1+Math.exp(-EntradaCalculada[f][g]));
		       }
	       }
	       
	       return retorno;
       }

       /** Esse metodo calcula o Erro obtido pela camada de saida de nossa rede neural, pela diferenca entre o valor alcancado e 
           o numero de Amostras */
       
       public double CalcularErro(double ResultadoSaida[][],double MatrizErros[][],int NumeroNeuroniosSaida,int NumeroAmostras){
	       int f,g;
	       double retorno;
	       
	       retorno=0;
	       for(f=0;f<NumeroNeuroniosSaida;f++){
		       for(g=0;g<NumeroAmostras;g++){
			       MatrizErros[f][g]=Alvo[f][g]-ResultadoSaida[f][g];
			       retorno=retorno+MatrizErros[f][g]*MatrizErros[f][g];
		       }
	       }

	       return retorno;
       }
       
       
}
