package org.abia.utils.BackpropagationNeuralNetwork;

/** Essa classe implementa uma tabela customizada para permitir que possamos enxergar as amostras carregadas dos arquivos */


/** Essa classe implementa uma tabela customizada para permitir que possamos enxergar as amostras carregadas dos arquivos */
public class AmostraRedeNeural {
	public    Double      Valor[][];
	protected int          NLinhas,NColunas;
	protected RedeNeural   Pai;
	
	public AmostraRedeNeural (int Colunas, int Linhas,RedeNeural newPai){
		int f,g;
		
		Pai=newPai;
		Valor=new Double[Colunas][Linhas];
		for(f=0;f<Colunas;f++){
			for(g=0;g<Linhas;g++){
				Valor[f][g]=new Double(0);
			}
		}
		NColunas=Colunas;
		NLinhas=Linhas;
	}

	public double[] getMatrix(){
		double Retorno[];
		int f,g;

		Retorno=new double[Pai.ColunaAmostra*Pai.LinhaAmostra];
		for(g=0;g<Pai.LinhaAmostra;g++){
		    for(f=0;f<Pai.ColunaAmostra;f++){
			    Retorno[g*Pai.ColunaAmostra+f]=Valor[f][g].doubleValue()*0.1;
		    }
		}
		return Retorno;
	}

	
}
