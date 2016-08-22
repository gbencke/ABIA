package org.abia.utils.BackpropagationNeuralNetwork.Validation;

/** Essa Classe representa a RedeNeural que o nosso trabalho ira utilizar */

public class RedeNeural {
       /** A Camada de Entrada de nossa Rede Neural, veja a classe CamadaNeural para maiores informacoes*/
       public CamadaNeural CamadaEntrada;
       /** A Camada Escondida de nossa Rede Neural, veja a classe CamadaNeural para maiores informacoes*/
       public CamadaNeural CamadaEscondida;
       /** A Camada de Saida de nossa Rede Neural, veja a classe CamadaNeural para maiores informacoes*/
       public CamadaNeural CamadaSaida;
       /** Os Valores de saida que nossa rede neural deve buscar enquanto estiver aprendendo*/
       public double ValoresAlvo[][];
       /** A Epoca atual de nossa Rede Neural*/
       public int    Epoca;
       /** O Valor de Momentum que nossa RedeNeural Utiliza*/
       public double Momentum;
       /** Valor Quadratico de Erro de nossa rede neural*/
       public double Erro;
       /** A Taxa de Aprendizado de nossa RedeNeural*/
       public double TaxaAprendizado;
       /** O Coeficiente de Momentum de nossa Rede Neural*/
       public double MomentumCoeficiente;
       /** O Valor do Erro Maximo tolerado por nossa Rede*/
       public double ErroMaximo;
       /** A Taxa de Ajuste de Decrescimo da Taxa de Aprendizado*/
       public double DecrescimoTaxaAprendizado;
       /** A Taxa de Ajuste de Acrescimo da Taxa de Aprendizado*/
       public double AcrescimoTaxaAprendizado;
	       

       /** O Construtor de nossa Classe que toma o Numero de Entradas da Camada de Entrada, o Numero de Neuronios
           de entrada que nossa rede deve possuir, o numero de neuronios escondidos, e o numero de amostras que serao
	   apresentados como valores de saida durante a fase de aprendizado */
	       
       public RedeNeural(int Entradas,int NeuroniosEntrada,
		         int NeuroniosEscondidos
			 ,int NeuroniosSaida,int NumeroAmostras){
	       int f,g;

	       Epoca=0;Momentum=0;TaxaAprendizado=0.01;MomentumCoeficiente=0.9;
	       ErroMaximo=1.02;DecrescimoTaxaAprendizado=0.7;AcrescimoTaxaAprendizado=1.05;

	       
	       CamadaEntrada=new CamadaNeural(NeuroniosEntrada,Entradas);
	       CamadaEscondida=new CamadaNeural(NeuroniosEscondidos,NeuroniosEntrada);
	       CamadaSaida=new CamadaNeural(NeuroniosSaida,NeuroniosEscondidos);
	       ValoresAlvo=new double[NeuroniosSaida][NumeroAmostras];
	       
	       for(f=0;f<NeuroniosSaida;f++){
		       for(g=0;g<NumeroAmostras;g++){
			       ValoresAlvo[f][g]=0;
		       }
	       }
	       ValoresAlvo[0][0]=1;
	       ValoresAlvo[1][1]=1;
	       ValoresAlvo[2][2]=1;
	       ValoresAlvo[3][3]=1;
	       ValoresAlvo[4][4]=1;
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
			       MatrizErros[f][g]=ValoresAlvo[f][g]-ResultadoSaida[f][g];
			       retorno=retorno+MatrizErros[f][g]*MatrizErros[f][g];
		       }
	       }

	       return retorno;
       }
}
