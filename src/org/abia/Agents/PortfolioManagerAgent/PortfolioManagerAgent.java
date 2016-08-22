package org.abia.Agents.PortfolioManagerAgent;

import org.abia.AgentContainer.*;
import org.abia.Blackboard.*;
import org.abia.Blackboard.PostgreSQL.*;
import org.abia.utils.CLIPServer.*;

import org.abia.Agents.BovespaAgent.*;
import java.util.*;

class PapelToMonitor {
	public Papel  PapelCurrent;
	public Vector ValoresFechamento;
	public Vector ValoresRetorno;
	
	public PapelToMonitor(Papel newPapel){
		PapelCurrent=newPapel;		
		ValoresFechamento=new Vector();
		ValoresRetorno=new Vector();
	}
	
	public String toString(){
		return (new String(PapelCurrent.getTimestamp()+""));
	}
	
}

public class PortfolioManagerAgent extends AnalisysAgent {
    protected CLIPServer myCLIPServer;
    protected int        LastPregao=0;
	protected double     ValorInicial=150000;
    
	static {
		System.out.println("Carregado:PortfolioManagerAgent");		
	}
	
	public PortfolioManagerAgent(){
	}

	public void Initialize() throws AgentException{
		Class[] ClassesToRegister;
		
		ClassesToRegister=new Class[6];		
		try {		
			ClassesToRegister[0]=Class.forName("org.abia.Agents.PortfolioManagerAgent.Portfolio");
			ClassesToRegister[1]=Class.forName("org.abia.Agents.PortfolioManagerAgent.PapelEmPortfolio");
			ClassesToRegister[2]=Class.forName("org.abia.Agents.PortfolioManagerAgent.NovoPortfolio");
			ClassesToRegister[3]=Class.forName("org.abia.Agents.PortfolioManagerAgent.NovoPapelEmPortfolio");
			ClassesToRegister[4]=Class.forName("org.abia.Agents.PortfolioManagerAgent.EstadoPortfolio");
			ClassesToRegister[5]=Class.forName("org.abia.Agents.PortfolioManagerAgent.EstadoPapelEmPortfolio");
			Blackboard.getBlackboard().RegisterAgentData(ClassesToRegister);
			Blackboard.getBlackboard().RegisterAgent(this);			
		}catch(Exception e){
			System.out.println(e);
			throw (new AgentException());
		}		
	}
	
	private double CalcularValorPortfolioAtual(Portfolio currentPortfolio,
	                                           AgentData currentPapelEmPortfolio[],
											   AgentData currentCotacoes[]){
		double   ret=0;
		int      f,g;
		
		ret+=currentPortfolio.getValorDinheiro().doubleValue();
		for(f=0;f<currentPapelEmPortfolio.length;f++){
			if(currentPapelEmPortfolio[f] instanceof PapelEmPortfolio){
				for(g=0;g<currentCotacoes.length;g++){
					if(((Cotacao)currentCotacoes[g]).getPapel().getTimestamp().intValue()==((PapelEmPortfolio)currentPapelEmPortfolio[f]).getPapel().getTimestamp().intValue()){
					   	double val;

						val=currentPortfolio.getValor().doubleValue()*(((PapelEmPortfolio)currentPapelEmPortfolio[f]).getQuantidade().doubleValue()/100);
					   	ret=ret+val;
						((PapelEmPortfolio)currentPapelEmPortfolio[f]).setValorDinheiro(new Double(val));
					}					
				}
			}
		}
		return ret;

	}

	public void run(){
		AgentData                Pregoes[]=null,Papeis[]=null;
		AgentData                Cotacoes[]=null;
		AgentData                DataToSave[]=null;
		AgentData                Portfolios[]=null;
		AgentData                currentPapelEmPortfolio[]=null;
		AgentData                TempPapelEmPortfolio[]=null;
		AgentData                NovosPortfolio[]=null;
		AgentData                NovosPapelEmPortfolio[]=null;
		Class                    DataToUseInExpertSystem[];
		QueryCondition           Filters[];
		Portfolio                currentPortfolio,currentPortfolioAntigo;
		PapelEmPortfolio         newPapelEmPortfolio;
		EstadoPortfolio          currentEstadoPortfolio;
		EstadoPapelEmPortfolio   currentEstadoPapelEmPortfolio;
		int                      f,g,h,i;
		double                   ValorPortfolioAtual=0;
				
		
		LastPregao=0;
		
		try{		
			DataToUseInExpertSystem=new Class[7];
			DataToUseInExpertSystem[0]=Class.forName("org.abia.Agents.BovespaAgent.Papel");
			DataToUseInExpertSystem[1]=Class.forName("org.abia.Agents.BovespaAgent.Pregao");
			DataToUseInExpertSystem[2]=Class.forName("org.abia.Agents.BovespaAgent.Cotacao");
			DataToUseInExpertSystem[3]=Class.forName("org.abia.Agents.TechnicalAnalisysAgent.TechnicalAnalisys");
			DataToUseInExpertSystem[4]=Class.forName("org.abia.Agents.FundamentalAnalisysAgent.FundamentalAnalisys");			
			DataToUseInExpertSystem[5]=Class.forName("org.abia.Agents.PortfolioManagerAgent.EstadoPortfolio");
			DataToUseInExpertSystem[6]=Class.forName("org.abia.Agents.PortfolioManagerAgent.EstadoPapelEmPortfolio");
			myCLIPServer=new CLIPServer("PortfolioManagerCLIPServer",7902,"PortfolioManager.clp",DataToUseInExpertSystem);
		}catch(Exception e){
			System.out.println(e);	
			e.printStackTrace();
		}

        //Vamos esperar ate iniciar os pregoes...
		while(true){
			try{
				Pregoes=Blackboard.getBlackboard().QueryTimestamp(0,Blackboard.TIMESTAMP_GREATER_THAN,"org.abia.Agents.BovespaAgent.Pregao",1);
				if(Pregoes.length>0) break;
				Thread.sleep(1000);
			}catch(Exception e){
				System.out.println(e);
			}
		}

		try{		
			Papeis=Blackboard.getBlackboard().Query(Papel.class,null);
		}catch(Exception e){
			System.out.println(e);
		}
		
		currentPortfolio=new Portfolio();
		currentPortfolio.setPregao(Pregoes[0]);
		currentPortfolio.setDevoComprar(new Integer(1));
		currentPortfolio.setValor(new Double(ValorInicial));
		currentPortfolio.setValorDinheiro(new Double(ValorInicial));
		
		DataToSave=new AgentData[1+Papeis.length];
		DataToSave[0]=currentPortfolio;
		
		currentPapelEmPortfolio=new AgentData[Papeis.length];
		for(f=0;f<Papeis.length;f++){
			newPapelEmPortfolio=new PapelEmPortfolio();
			newPapelEmPortfolio.setPapel(Papeis[f]);
			newPapelEmPortfolio.setQuantidade(new Double(0));
			newPapelEmPortfolio.setValorDinheiro(new Double(0));
			newPapelEmPortfolio.setPortfolio(currentPortfolio);
			DataToSave[f+1]=newPapelEmPortfolio;
			currentPapelEmPortfolio[f]=newPapelEmPortfolio;
		}

		try{		
			Blackboard.getBlackboard().Store(DataToSave);
		}catch(Exception e){
			System.out.println(e);
		}
		
        LastPregao=Pregoes[0].getTimestamp().intValue();
		while(true){
			try{
				Pregoes=Blackboard.getBlackboard().QueryTimestamp(LastPregao,Blackboard.TIMESTAMP_GREATER_THAN,"org.abia.Agents.BovespaAgent.Pregao",1);
				if(Pregoes.length==0){
				   Thread.sleep(1000);
				   continue;
				}

				Filters=new QueryCondition[1];
				Filters[0]=new PostgreSQLFilterCondition();
				((PostgreSQLFilterCondition)Filters[0]).FieldName="pregao";
				((PostgreSQLFilterCondition)Filters[0]).Operator =PostgreSQLQueryCondition.EQUALS;
				((PostgreSQLFilterCondition)Filters[0]).ValueName=Pregoes[0].getTimestamp();
				Cotacoes=Blackboard.getBlackboard().Query(Cotacao.class,Filters);
				if(Cotacoes.length==Papeis.length){
					PapelEmPortfolio TempPapelEmPortfolioSelected=null;
					Cotacao          currentCotacao=null;
					
					DataToSave=new AgentData[1+Papeis.length];					
					currentEstadoPortfolio=new EstadoPortfolio();
					currentEstadoPortfolio.setValorDinheiro(currentPortfolio.getValorDinheiro());
					DataToSave[0]=currentEstadoPortfolio;
					for(f=0;f<Papeis.length;f++){
						currentEstadoPapelEmPortfolio=new EstadoPapelEmPortfolio();
						currentEstadoPapelEmPortfolio.setPapel(Papeis[f]);
						for(g=0;g<currentPapelEmPortfolio.length;g++){
							if(((PapelEmPortfolio)currentPapelEmPortfolio[g]).getPapel().getTimestamp().longValue()==
							    ((Papel)Papeis[f]).getTimestamp().longValue()){
									TempPapelEmPortfolioSelected=(PapelEmPortfolio)currentPapelEmPortfolio[g];
									break;
							    }
						}
						for(g=0;g<Cotacoes.length;g++){
							if(((Cotacao)Cotacoes[g]).getPapel().getTimestamp().longValue()==
							   ((Papel)Papeis[f]).getTimestamp().longValue()){
							   	   currentCotacao=(Cotacao)Cotacoes[g];
							   	   break;
							   }							
						}
						
						currentEstadoPapelEmPortfolio.setQuantidade(TempPapelEmPortfolioSelected.getQuantidade());												
						currentEstadoPapelEmPortfolio.setValorDinheiro(new Double(currentCotacao.getFechamento().doubleValue()*TempPapelEmPortfolioSelected.getQuantidade().doubleValue()));
						currentEstadoPapelEmPortfolio.setPortfolio(currentPortfolio);
						DataToSave[f+1]=currentEstadoPapelEmPortfolio;
					}
					Blackboard.getBlackboard().Store(DataToSave);					
				}else{
					Thread.sleep(1000);
				}
				while(true){
					NovosPortfolio=Blackboard.getBlackboard().QueryTimestamp(currentPortfolio.getTimestamp().intValue(),Blackboard.TIMESTAMP_GREATER_THAN,"org.abia.Agents.PortfolioManagerAgent.NovoPortfolio",1);
					if(NovosPortfolio.length==0){
					   Thread.sleep(1000);
					   continue;
					}
					Filters=new QueryCondition[1];
					Filters[0]=new PostgreSQLFilterCondition();
					((PostgreSQLFilterCondition)Filters[0]).FieldName="pregao";
					((PostgreSQLFilterCondition)Filters[0]).Operator =PostgreSQLQueryCondition.EQUALS;
					((PostgreSQLFilterCondition)Filters[0]).ValueName=((NovoPortfolio)NovosPortfolio[0]).getPregao().getTimestamp();
					NovosPapelEmPortfolio=Blackboard.getBlackboard().Query(NovoPapelEmPortfolio.class,Filters);
					if(NovosPapelEmPortfolio.length!=Papeis.length){
						Thread.sleep(1000);
						continue;
					}
					ValorPortfolioAtual=this.CalcularValorPortfolioAtual(currentPortfolio,currentPapelEmPortfolio,Cotacoes);
					
					DataToSave=new AgentData[Papeis.length+1];
					currentPortfolioAntigo=currentPortfolio;
					currentPortfolio=new Portfolio();
					currentPortfolio.setDevoComprar(((NovoPortfolio)NovosPortfolio[0]).getDevoComprar());
					currentPortfolio.setPregao(((NovoPortfolio)NovosPortfolio[0]).getPregao());
					currentPortfolio.setValor(new Double(ValorPortfolioAtual));
					currentPortfolio.setValorDinheiro(currentPortfolioAntigo.getValorDinheiro());
					currentPapelEmPortfolio=new AgentData[Papeis.length];
					DataToSave[0]=currentPortfolio;
					for(f=0;f<NovosPapelEmPortfolio.length;f++){
						currentPapelEmPortfolio[f]=new PapelEmPortfolio();
						((PapelEmPortfolio)currentPapelEmPortfolio[f]).setPapel(((NovoPapelEmPortfolio)NovosPapelEmPortfolio[f]).getPapel());
						((PapelEmPortfolio)currentPapelEmPortfolio[f]).setPortfolio(currentPortfolio);
						((PapelEmPortfolio)currentPapelEmPortfolio[f]).setQuantidade(((NovoPapelEmPortfolio)NovosPapelEmPortfolio[f]).getPorcentagem());
						DataToSave[f+1]=currentPapelEmPortfolio[f];
					}
					currentPortfolio.setValor(new Double(this.CalcularValorPortfolioAtual(currentPortfolio,currentPapelEmPortfolio,Cotacoes)));
					Blackboard.getBlackboard().Store(DataToSave);					
				}
			}catch(Exception e){
				System.out.println(e);
			}
		}
	}

		
/*        
		try{		
			Filters=new QueryCondition[2];
			Filters[0]=new PostgreSQLFilterCondition();
			((PostgreSQLFilterCondition)Filters[0]).FieldName="timestamp";
			((PostgreSQLFilterCondition)Filters[0]).Operator =PostgreSQLQueryCondition.GREATER;
			((PostgreSQLFilterCondition)Filters[0]).ValueName=LastBalancete.getTimestamp();
			Filters[1]= new PostgreSQLOrderCondition();
			((PostgreSQLOrderCondition)Filters[1]).Field="timestampval";					
			((PostgreSQLOrderCondition)Filters[1]).Order=OrderCondition.ASC;					
			Papeis=Blackboard.getBlackboard().Query(Balancete.class,Filters);
		    
		}catch(Exception e){
			System.out.println(e);
		}
*/	

		

		
/*		while(true){
			try{
				Thread.sleep(100);				
			}catch(Exception e){
				System.out.println(e);
				e.printStackTrace();
			}
		}
		
	}*/

	public String getAgentNameInBlackboard(){
		return "PortfolioManagerAgent";
	}


}