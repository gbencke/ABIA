package org.abia.utils.BackpropagationNeuralNetwork;

/** Essa classe representa a abstracao de um neuronio... Ela Armazena os dados de Pesos e Bias e inicializa eles com valores 
    randomicos
*/    

public class Neuronio {
	
	/** O vetor abaixo representa os Pesos das entradas de nosso Neuronio */
	
	public double Pesos[];
	
	/** O valor abaixo reprenta o Bias deste neuronio */
	
	public double Bias;
	
	/** Construtor do nosso neuronio que toma como entrada o numero de entradas que o nosso neuronio possui e inicializa 
	    esses valores com valores randomicos */
	
	public Neuronio(int NumeroEntradas){
                int f,i,h;
		double acc,acc2;
                
		Pesos=new double[NumeroEntradas];
		for(f=0;f<1;f++){
			Bias=(2*Math.random())-1;
			acc=0;
			for(h=0;h<NumeroEntradas;h++){
				Pesos[h]=(2*Math.random())-1;
				acc+=(Pesos[f]*Pesos[f]);
			}
			acc=Math.sqrt(1/acc);
			acc2=0;
			for(i=0;i<NumeroEntradas;i++){
				Pesos[i]*=(2*acc);
				acc2+=Pesos[i];
			}
			Bias-=(acc2/2);
		}

		Bias=Math.random();
		for(f=0;f<NumeroEntradas;f++){
			Pesos[f]=Math.random();
		}
	}

   
}
