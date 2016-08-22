package org.abia.utils.BackpropagationNeuralNetwork.Validation;

/**
	Classe CamadaNeural
	
	Abstrai uma CamadaNeural de nossa RedeNeural, na nossa rede neural existem 3 camadas Neurais:A de Entrada, a Escondida e a de saida
	Essa classe tem como funcao principal guarda os dados dos neuronios da CamadaNeural
*/

public class CamadaNeural {

/**	
        Array de Neuronios da qual a rede neural possui.
*/
       public Neuronio Neuronios []; 
/**
       Numero de Neuronios da Camada
*/       
       public int      NumNeuronios;
/**
       Numero de Entradas de Camada Neuronio da Camada
*/       
       public int      NumEntradas;

/**
       Construtor Default da CamadaNeural, recebe como parametro o numero de neuronios da camada e o
       numero de entradas que cada neuronio deve ter e instancia os neuronios
*/       

       public CamadaNeural(int NumeroNeuronios,int NumeroEntradas){
	       int f;
	       Neuronios=new Neuronio[NumeroNeuronios];
	       for(f=0;f<NumeroNeuronios;f++){
		       Neuronios[f]=new Neuronio(NumeroEntradas);
	       }
	       NumNeuronios=NumeroNeuronios;
	       NumEntradas=NumeroEntradas;
       }

/**
       Essa funcao retorna uma matriz de valores double vazios que representa NeuroniosXPesos
*/       
       
       public double[][] getMatrixVazia(){
	       int f,g;
	       double retorno[][];

	       retorno=new double[NumNeuronios][NumEntradas];
	       for(f=0;f<NumNeuronios;f++){
		       for(g=0;g<NumEntradas;g++){
			       retorno[f][g]=0;
		       }
	       }
	       return retorno;
       }
/**
       Essa funcao retorna uma matriz de valores double que representa NeuroniosXPesos
*/       
       
       public double[][] getMatrix(){
	       int f,g;
	       double retorno[][];

	       retorno=new double[NumNeuronios][NumEntradas];
	       for(f=0;f<NumNeuronios;f++){
		       for(g=0;g<NumEntradas;g++){
			       retorno[f][g]=Neuronios[f].Pesos[g];
		       }
	       }
	       return retorno;
       }

/** 
	Essa funcao retorna um vetor do Bias dos neuronios de nossa Camada
*/	
       
       public double[] getBias(){
	       double retorno[];
	       int f;

	       retorno=new double[Neuronios.length];
	       for(f=0;f<Neuronios.length;f++){
		       retorno[f]=Neuronios[f].Bias;
	       }
	       return retorno;
       }

/** 
	Essa funcao retorna um vetor do Bias dos neuronios de nossa Camada com valores vazios
*/	
       
       
       public double[] getBiasVazia(){
	       double retorno[];
	       int f;

	       retorno=new double[Neuronios.length];
	       for(f=0;f<Neuronios.length;f++){
		       retorno[f]=0;
	       }
	       return retorno;
       }

/** Essa funcao permite que possamos propagar os erros atraves de nossa camada. Esses erros foram calculados 
   pela diferenca entre os valores pretendidos e os obtidos durante a inferencia de valores. */

       public double[][] PropagarErros(double Saida[][],double MatrizErros[][]){
	       double retorno[][];
	       int f,g;

	       retorno=new double[Neuronios.length][5];
	       for(f=0;f<Neuronios.length;f++){
		       for(g=0;g<5;g++){
			       retorno[f][g]=Saida[f][g]*(1-Neuronios[f].Pesos[g])*MatrizErros[f][g];
			       //System.out.println("----Neuronio:"+f+" Amostra:"+g+" Retorno:"+retorno[f][g]);
		       }
	       }
	       return retorno;
       }
       
/** Essa funcao permite que possamos propagar os erros atraves de nossa camada. Esses erros foram calculados 
   pela diferenca entre os valores pretendidos e os obtidos durante a inferencia de valores. E a mesma funcao da
   acima, porem permite que recebamos tambem uma referencia para a CamadaNeural anterior a essa*/
       
       
       public double[][] PropagarErros(double Saida[][],double Delta[][],CamadaNeural CamadaAnterior){
	       double retorno[][];
	       double PesosCamadaAnterior[][];
	       double PesosTranspostos[][];
	       double ErroCalculado[][];
	       int f,g;

	       retorno=new double[Neuronios.length][5];
	       PesosCamadaAnterior=CamadaAnterior.getMatrix();
	       PesosTranspostos=Transpor(PesosCamadaAnterior,CamadaAnterior.NumNeuronios,CamadaAnterior.NumEntradas);

	       ErroCalculado=TrabG1IA2.Frame.Rede.CalcularEntradasPorPesos(PesosTranspostos,Delta,CamadaAnterior.Neuronios.length,5,Neuronios.length);
	       for(f=0;f<Neuronios.length;f++){
		       for(g=0;g<5;g++){
			       retorno[f][g]=Saida[f][g]*(1-Saida[f][g]*ErroCalculado[f][g]);
		       }
	       }
	       return retorno;
       }

/** Essa funcao faz apenas a transposicao de matrizes, que eh uma operacao executada durante os calculos do Backpropagation */
       
       public double[][] Transpor(double Alvo[][],int Tamanho1,int Tamanho2){
	       double retorno[][];
	       int f,g;

	       retorno=new double[Tamanho2][Tamanho1];
	       for(f=0;f<Tamanho1;f++){
		       for(g=0;g<Tamanho2;g++){
			       retorno[g][f]=Alvo[f][g];
		       }
	       }
	       return retorno;
       }


/** Essa funcao permite que repopulemos o Bias dos neuronios apartir de um vetor de valores*/      
       
       public void RepopularBias(double Valores[]){
	       int f,g;

	       for(f=0;f<Neuronios.length;f++){
		       Neuronios[f].Bias=Valores[f];
	       }
       }
       
/** Essa funcao permite que repopulemos os pesos dos neuronios apartir de uma matriz de valores*/       
       
       public void RepopularPesos(double Valores[][]){
	       int f,g;

	       for(f=0;f<Neuronios.length;f++){
		       for(g=0;g<NumEntradas;g++){
			       Neuronios[f].Pesos[g]=Valores[f][g];
		       }
	       }

       }


}
