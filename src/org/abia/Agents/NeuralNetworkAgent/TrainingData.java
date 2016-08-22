package org.abia.Agents.NeuralNetworkAgent;

import org.abia.Blackboard.*;

public class TrainingData extends AgentData {	
	protected AgentData Papel;
	protected AgentData Indice;
	protected AgentData Pregao;
	protected Double    ValorM;
	protected Double    ValorIt;
	protected Double    ValorMA5;
	protected Double    ValorMA10;
	protected Double    ValorRSI;	
	protected Double    ValorSaida;
		 
	
	public TrainingData(){
		super();		
	}
	
	public void Mult(double Val){
		ValorM=new Double(Val*ValorM.doubleValue());
		ValorIt=new Double(Val*ValorIt.doubleValue());
		ValorMA5=new Double(Val*ValorMA5.doubleValue());
		ValorMA10=new Double(Val*ValorMA10.doubleValue());
		ValorRSI=new Double(Val*ValorRSI.doubleValue());
		ValorSaida=new Double(Val*ValorSaida.doubleValue());
	}
	
	public void Ajust(){
		if(ValorM.doubleValue()<0)
		   ValorM=new Double(ValorM.doubleValue()*-1);
		if(ValorIt.doubleValue()<0)
		   ValorIt=new Double(ValorIt.doubleValue()*-1);
		if(ValorMA5.doubleValue()<0)
		   ValorMA5=new Double(ValorMA5.doubleValue()*-1);
		if(ValorMA10.doubleValue()<0)
  		   ValorMA10=new Double(ValorMA10.doubleValue()*-1);
		if(ValorRSI.doubleValue()<0)
		   ValorRSI=new Double(ValorRSI.doubleValue()*-1);
		if(ValorSaida.doubleValue()<0)
		   ValorSaida=new Double(ValorSaida.doubleValue()*-1);
	}

	public Double    getMinNormalizedValorM(){
		double Valores[],min;
		int    f;
		
		Valores=new double[3];
		Valores[0]=ValorIt.doubleValue();
		Valores[1]=ValorMA5.doubleValue();
		Valores[2]=ValorMA10.doubleValue();
		
		min=-1;
		for(f=0;f<3;f++){
			if(min==-1) min=Valores[f];
			else{
				if(Valores[f]<min) min=Valores[f];
			}
		}
		
		return new Double(min);
	}
	
	public AgentData getPapel()       { return Papel; }
	public AgentData getIndice()      { return Indice; }
	public AgentData getPregao()      { return Pregao; }
	public Double    getValorM()      { return ValorM; }
	public Double    getValorIt()     { return ValorIt; }
	public Double    getValorMA5()    { return ValorMA5; }
	public Double    getValorMA10()   { return ValorMA10; }
	public Double    getValorRSI()    { return ValorRSI; }
	public Double    getValorSaida()  { return ValorSaida; }	
		
		

	public void      setPapel      (AgentData newPapel)    { Papel=newPapel; }
	public void      setIndice     (AgentData newIndice)   { Indice=newIndice; }
	public void      setPregao     (AgentData newPregao)   { Pregao=newPregao; }
	public void      setValorM     (Double newValorM)      { ValorM=newValorM; }
	public void      setValorIt    (Double newValorIt)     { ValorIt=newValorIt; }
	public void      setValorMA5   (Double newValorMA5)    { ValorMA5=newValorMA5; }
	public void      setValorMA10  (Double newValorMA10)   { ValorMA10=newValorMA10; }
	public void      setValorRSI   (Double newValorRSI)    { ValorRSI=newValorRSI; }
	public void      setValorSaida (Double newValorSaida)  { ValorSaida=newValorSaida; }

	public String getAgentDataNameInBlackboard(){
		return "Prediction";
	}
	
	public Double getMaxValue(){
		double Valores[],Max=-1;
		int    f;
		
		
		Valores=new double[5];		
		Valores[0]=ValorM.doubleValue();
		Valores[1]=ValorIt.doubleValue();
		Valores[2]=ValorMA5.doubleValue();
		Valores[3]=ValorMA10.doubleValue();
		Valores[4]=ValorRSI.doubleValue();
				
		for(f=0;f<5;f++){
			if(Max==-1)
				Max=Valores[f];
			else{
				if(Valores[f]>Max) Max=Valores[f]; 					
			}
		}
		return (new Double(Max));
	}

	public Double getMinValue(){
		double Valores[],Min=-1;
		int    f;
		
		
		Valores=new double[5];		
		Valores[0]=ValorM.doubleValue();
		Valores[1]=ValorIt.doubleValue();
		Valores[2]=ValorMA5.doubleValue();
		Valores[3]=ValorMA10.doubleValue();
		Valores[4]=ValorRSI.doubleValue();
				
		for(f=0;f<5;f++){
			if(Min==-1)
				Min=Valores[f];
			else{
				if(Valores[f]<Min) Min=Valores[f]; 					
			}
		}
		return (new Double(Min));
	}

}