package org.abia.Agents.TechnicalAnalisysAgent;

import org.abia.AgentContainer.*;
import org.abia.Agents.BovespaAgent.*;
import org.abia.Blackboard.*;
import org.abia.utils.CLIPServer.*;
import org.abia.Blackboard.PostgreSQL.*;

import java.util.*;

public class TechnicalAnalisysAgent extends AnalisysAgent {
	protected CLIPServer myCLIPServer;
	protected Vector     Pregao;
	protected Pregao     LastPregao;
	protected int        PeriodoAnalise=20;
	protected int        PeriodoMinimoTrend=4;
	protected double     MediaAlta=0,MediaBaixa=0;
	
	static {
		System.out.println("Carregado:TechnicalAnalisysAgent");		
	}
	
	public TechnicalAnalisysAgent() {
		Pregao=new Vector();
	}
	
	protected void CalcularBollinger(int Periodo){
		AgentData   	  Papeis[]=null;
		AgentData   	  Indices[]=null;
		AgentData   	  CotacoesIndice[];
		AgentData   	  Cotacoes[];
		AgentData   	  CotacoesAcoes[];
		Indice      	  currentIndice;
		Papel       	  currentPapel;
		Cotacao           currentCotacao=null;
		CotacaoIndice     currentCotacaoIndice;
		BollingerBands    currentBollingerBands;
		QueryCondition[]  Filters;
		double            middleBand,UpperBand,LowerBand;
				
		int f,g;
		
		if(Pregao.size()<Periodo) return;
		
		try{
			Papeis=Blackboard.getBlackboard().Query(Papel.class,null);
			Indices=Blackboard.getBlackboard().Query(Indice.class,null);
		}catch(Exception e){
			System.out.println(e);			
			e.printStackTrace();
		}
		
		for(f=0;f<Indices.length;f++){
			currentIndice=(Indice)Indices[f];
			try{
				Filters=new QueryCondition[3];
				Filters[0]=new PostgreSQLFilterCondition();
				((PostgreSQLFilterCondition)Filters[0]).FieldName="Indice";
				((PostgreSQLFilterCondition)Filters[0]).Operator =PostgreSQLQueryCondition.EQUALS;
				((PostgreSQLFilterCondition)Filters[0]).ValueName=currentIndice.getTimestamp();
				Filters[1]=new PostgreSQLFilterCondition();
				((PostgreSQLFilterCondition)Filters[1]).FieldName="Pregao";
				((PostgreSQLFilterCondition)Filters[1]).Operator =PostgreSQLQueryCondition.LESS_EQUAL;
				((PostgreSQLFilterCondition)Filters[1]).ValueName=LastPregao.getTimestamp();
				Filters[2]= new PostgreSQLOrderCondition();
				((PostgreSQLOrderCondition)Filters[2]).Field="timestampval";					
				((PostgreSQLOrderCondition)Filters[2]).Order=OrderCondition.DESC;
				CotacoesIndice=Blackboard.getBlackboard().Query(CotacaoIndice.class,Filters);
				if(CotacoesIndice.length>0){								
					middleBand=0;
					UpperBand=0;
					LowerBand=0;
					currentCotacaoIndice=null;
					for(g=0;g<Periodo;g++){
						currentCotacaoIndice=(CotacaoIndice)CotacoesIndice[g];
						middleBand+=currentCotacaoIndice.getFechamento().doubleValue();
					}
					middleBand=middleBand/Periodo;
					for(g=0;g<Periodo;g++){				
						currentCotacaoIndice=(CotacaoIndice)CotacoesIndice[g];
					    UpperBand+=Math.pow((currentCotacaoIndice.getFechamento().doubleValue()-middleBand),2);
					    LowerBand+=Math.pow((currentCotacaoIndice.getFechamento().doubleValue()-middleBand),2);
					}
					UpperBand=middleBand+2*(Math.pow(UpperBand/Periodo,0.5));
					LowerBand=middleBand+2*(Math.pow(LowerBand/Periodo,0.5));
								
					currentBollingerBands=new BollingerBands();
					currentBollingerBands.setIndice(currentIndice);
					currentBollingerBands.setPeriodo(new Integer(Periodo));
					currentBollingerBands.setPregao(LastPregao);
					currentBollingerBands.setmiddleBand(new Double(middleBand));
					currentBollingerBands.setlowerBand(new Double(LowerBand));
					currentBollingerBands.setupperBand(new Double(UpperBand));
					currentBollingerBands.setFechamento(currentCotacaoIndice.getMaximo());				
					Blackboard.getBlackboard().Store(currentBollingerBands.getClass(),currentBollingerBands);
				}								
			}catch(Exception e){
				System.out.println(e);
				e.printStackTrace();				
			}
		}

		for(f=0;f<Papeis.length;f++){
			currentPapel=(Papel)Papeis[f];
			try{
				Filters=new QueryCondition[3];
				Filters[0]=new PostgreSQLFilterCondition();
				((PostgreSQLFilterCondition)Filters[0]).FieldName="Papel";
				((PostgreSQLFilterCondition)Filters[0]).Operator =PostgreSQLQueryCondition.EQUALS;
				((PostgreSQLFilterCondition)Filters[0]).ValueName=currentPapel.getTimestamp();
				Filters[1]=new PostgreSQLFilterCondition();
				((PostgreSQLFilterCondition)Filters[1]).FieldName="Pregao";
				((PostgreSQLFilterCondition)Filters[1]).Operator =PostgreSQLQueryCondition.LESS_EQUAL;
				((PostgreSQLFilterCondition)Filters[1]).ValueName=LastPregao.getTimestamp();
				Filters[2]= new PostgreSQLOrderCondition();
				((PostgreSQLOrderCondition)Filters[2]).Field="timestampval";					
				((PostgreSQLOrderCondition)Filters[2]).Order=OrderCondition.DESC;
				Cotacoes=Blackboard.getBlackboard().Query(Cotacao.class,Filters);								
				middleBand=0;
				UpperBand=0;
				LowerBand=0;
				for(g=0;g<Periodo;g++){
					currentCotacao=(Cotacao)Cotacoes[g];
					middleBand+=currentCotacao.getFechamento().doubleValue();
				}
				middleBand=middleBand/Periodo;
				for(g=0;g<Periodo;g++){				
					currentCotacao=(Cotacao)Cotacoes[g];
					UpperBand+=Math.pow((currentCotacao.getFechamento().doubleValue()-middleBand),2);
					LowerBand+=Math.pow((currentCotacao.getFechamento().doubleValue()-middleBand),2);
				}
				UpperBand=middleBand+2*(Math.pow(UpperBand/Periodo,0.5));
				LowerBand=middleBand-2*(Math.pow(LowerBand/Periodo,0.5));
								
				currentBollingerBands=new BollingerBands();
				currentBollingerBands.setPapel(currentPapel);
				currentBollingerBands.setPeriodo(new Integer(Periodo));
				currentBollingerBands.setPregao(LastPregao);
				currentBollingerBands.setmiddleBand(new Double(middleBand));
				currentBollingerBands.setlowerBand(new Double(LowerBand));
				currentBollingerBands.setupperBand(new Double(UpperBand));
				currentBollingerBands.setFechamento(currentCotacao.getMaximo());
				Blackboard.getBlackboard().Store(currentBollingerBands.getClass(),currentBollingerBands);								
			}catch(Exception e){
				System.out.println(e);
				e.printStackTrace();				
			}
		}				
	}

	protected void CalcularStochasticOscillator(int Periodo){
		AgentData   	  Papeis[]=null;
		AgentData   	  Indices[]=null;
		AgentData   	  CotacoesIndice[];
		AgentData   	  CotacoesAcoes[];
		AgentData   	  Cotacoes[];
		AgentData   	  Stochastics[];
		Indice      	  currentIndice;
		Papel       	  currentPapel;
		CotacaoIndice     currentCotacaoIndice;
		Cotacao           currentCotacao,lastCotacao;
		StochasticOscillator    currentStochasticOscillator,currentStochastic;
		QueryCondition[]  Filters;
		double            fechamentoHoje,Menor=0,Maior=0,K=0,D=0;
		int f,g;
		
		if(Pregao.size()<Periodo) return;
		
		try{
			Papeis=Blackboard.getBlackboard().Query(Papel.class,null);
			Indices=Blackboard.getBlackboard().Query(Indice.class,null);
		}catch(Exception e){
			System.out.println(e);			
			e.printStackTrace();
		}
		
		for(f=0;f<Indices.length;f++){
			currentIndice=(Indice)Indices[f];
			
			try{
				Filters=new QueryCondition[3];
				Filters[0]=new PostgreSQLFilterCondition();
				((PostgreSQLFilterCondition)Filters[0]).FieldName="Indice";
				((PostgreSQLFilterCondition)Filters[0]).Operator =PostgreSQLQueryCondition.EQUALS;
				((PostgreSQLFilterCondition)Filters[0]).ValueName=currentIndice.getTimestamp();
				Filters[1]=new PostgreSQLFilterCondition();
				((PostgreSQLFilterCondition)Filters[1]).FieldName="Pregao";
				((PostgreSQLFilterCondition)Filters[1]).Operator =PostgreSQLQueryCondition.LESS_EQUAL;
				((PostgreSQLFilterCondition)Filters[1]).ValueName=LastPregao.getTimestamp();
				Filters[2]= new PostgreSQLOrderCondition();
				((PostgreSQLOrderCondition)Filters[2]).Field="timestampval";					
				((PostgreSQLOrderCondition)Filters[2]).Order=OrderCondition.DESC;
				CotacoesIndice=Blackboard.getBlackboard().Query(CotacaoIndice.class,Filters);								

				currentCotacaoIndice=(CotacaoIndice)CotacoesIndice[0];
				fechamentoHoje=currentCotacaoIndice.getFechamento().doubleValue();				
				for(g=0;g<Periodo;g++){
					currentCotacaoIndice=(CotacaoIndice)CotacoesIndice[g];
					if(Menor==0){
						Menor=currentCotacaoIndice.getFechamento().doubleValue();
					}else{
						if(Menor>currentCotacaoIndice.getMinima().doubleValue()){
							Menor=currentCotacaoIndice.getMinima().doubleValue();
						}
					}
					if(Maior==0){
						Maior=currentCotacaoIndice.getFechamento().doubleValue();
					}else{
						if(Maior<currentCotacaoIndice.getMaximo().doubleValue()){
							Maior=currentCotacaoIndice.getMaximo().doubleValue();
						}						
					}
				}
				if(Pregao.size()>Periodo*2+5){
					Filters=new QueryCondition[3];
					Filters[0]=new PostgreSQLFilterCondition();
					((PostgreSQLFilterCondition)Filters[0]).FieldName="Indice";
					((PostgreSQLFilterCondition)Filters[0]).Operator =PostgreSQLQueryCondition.EQUALS;
					((PostgreSQLFilterCondition)Filters[0]).ValueName=currentIndice.getTimestamp();
					Filters[1]=new PostgreSQLFilterCondition();
					((PostgreSQLFilterCondition)Filters[1]).FieldName="Pregao";
					((PostgreSQLFilterCondition)Filters[1]).Operator =PostgreSQLQueryCondition.LESS_EQUAL;
					((PostgreSQLFilterCondition)Filters[1]).ValueName=LastPregao.getTimestamp();
					Filters[2]= new PostgreSQLOrderCondition();
					((PostgreSQLOrderCondition)Filters[2]).Field="timestampval";					
					((PostgreSQLOrderCondition)Filters[2]).Order=OrderCondition.DESC;
					Stochastics=Blackboard.getBlackboard().Query(StochasticOscillator.class,Filters);
					for(g=0;g<Periodo;g++){
						currentStochastic=(StochasticOscillator)Stochastics[g];
						D+=currentStochastic.getK().doubleValue();						
					}
					D=D/Periodo;
				}else D=0;

				currentStochasticOscillator=new StochasticOscillator();
				currentStochasticOscillator.setIndice(currentIndice);
				currentStochasticOscillator.setPeriodo(new Integer(Periodo));
				currentStochasticOscillator.setPregao(LastPregao);
				currentStochasticOscillator.setK(new Double(K));
				currentStochasticOscillator.setD(new Double(D));				
				Blackboard.getBlackboard().Store(currentStochasticOscillator.getClass(),currentStochasticOscillator);								
			}catch(Exception e){
				System.out.println(e);
				e.printStackTrace();				
			}
			
		}				
		for(f=0;f<Papeis.length;f++){
			currentPapel=(Papel)Papeis[f];			
			try{
				Filters=new QueryCondition[3];
				Filters[0]=new PostgreSQLFilterCondition();
				((PostgreSQLFilterCondition)Filters[0]).FieldName="Papel";
				((PostgreSQLFilterCondition)Filters[0]).Operator =PostgreSQLQueryCondition.EQUALS;
				((PostgreSQLFilterCondition)Filters[0]).ValueName=currentPapel.getTimestamp();
				Filters[1]=new PostgreSQLFilterCondition();
				((PostgreSQLFilterCondition)Filters[1]).FieldName="Pregao";
				((PostgreSQLFilterCondition)Filters[1]).Operator =PostgreSQLQueryCondition.LESS_EQUAL;
				((PostgreSQLFilterCondition)Filters[1]).ValueName=LastPregao.getTimestamp();
				Filters[2]= new PostgreSQLOrderCondition();
				((PostgreSQLOrderCondition)Filters[2]).Field="timestampval";					
				((PostgreSQLOrderCondition)Filters[2]).Order=OrderCondition.DESC;
				Cotacoes=Blackboard.getBlackboard().Query(Cotacao.class,Filters);								

				currentCotacao=(Cotacao)Cotacoes[0];
				fechamentoHoje=currentCotacao.getFechamento().doubleValue();				
				for(g=0;g<Periodo;g++){
					currentCotacao=(Cotacao)Cotacoes[g];
					if(Menor==0){
						Menor=currentCotacao.getFechamento().doubleValue();
					}else{
						if(Menor>currentCotacao.getMinima().doubleValue()){
							Menor=currentCotacao.getMinima().doubleValue();
						}
					}
					if(Maior==0){
						Maior=currentCotacao.getFechamento().doubleValue();
					}else{
						if(Maior<currentCotacao.getMaximo().doubleValue()){
							Maior=currentCotacao.getMaximo().doubleValue();
						}						
					}
				}
				if(Pregao.size()>Periodo*2+5){
					Filters=new QueryCondition[3];
					Filters[0]=new PostgreSQLFilterCondition();
					((PostgreSQLFilterCondition)Filters[0]).FieldName="papel";
					((PostgreSQLFilterCondition)Filters[0]).Operator =PostgreSQLQueryCondition.EQUALS;
					((PostgreSQLFilterCondition)Filters[0]).ValueName=currentPapel.getTimestamp();
					Filters[1]=new PostgreSQLFilterCondition();
					((PostgreSQLFilterCondition)Filters[1]).FieldName="Pregao";
					((PostgreSQLFilterCondition)Filters[1]).Operator =PostgreSQLQueryCondition.LESS_EQUAL;
					((PostgreSQLFilterCondition)Filters[1]).ValueName=LastPregao.getTimestamp();
					Filters[2]= new PostgreSQLOrderCondition();
					((PostgreSQLOrderCondition)Filters[2]).Field="timestampval";					
					((PostgreSQLOrderCondition)Filters[2]).Order=OrderCondition.DESC;
					Stochastics=Blackboard.getBlackboard().Query(StochasticOscillator.class,Filters);
					for(g=0;g<Periodo;g++){
						currentStochastic=(StochasticOscillator)Stochastics[g];
						D+=currentStochastic.getK().doubleValue();						
					}
					D=D/Periodo;
				}else D=0;

				currentStochasticOscillator=new StochasticOscillator();
				currentStochasticOscillator.setPapel(currentPapel);
				currentStochasticOscillator.setPeriodo(new Integer(Periodo));
				currentStochasticOscillator.setPregao(LastPregao);
				currentStochasticOscillator.setK(new Double(K));
				currentStochasticOscillator.setD(new Double(D));				
				Blackboard.getBlackboard().Store(currentStochasticOscillator.getClass(),currentStochasticOscillator);								
			}catch(Exception e){
				System.out.println(e);
				e.printStackTrace();				
			}
			
		}				
	}
	
	protected void CalcularTrend(){
		int               Ponto,PontoInicial,f,g,h,TrendAlta=0,TrendBaixa=0,LastValor;
		Pregao            PregaoInicial; 
		AgentData   	  Papeis[]=null;
		AgentData   	  Indices[]=null;
		AgentData   	  CotacoesIndice[]=null;
		AgentData   	  CotacoesAcoes[]=null;
		AgentData   	  Cotacoes[]=null;		
		AgentData   	  Encontrado[]=null;
		QueryCondition[]  Filters;
		Indice      	  currentIndice;
		Papel       	  currentPapel;		
		CotacaoIndice     currentCotacaoIndice,lastCotacaoIndice;
		Cotacao           currentCotacao,lastCotacao;
		Trend             TrendsAnalised[];		
				
		if(Pregao.size()<(PeriodoAnalise+5)) return;

		try{
			Papeis=Blackboard.getBlackboard().Query(Papel.class,null);
			Indices=Blackboard.getBlackboard().Query(Indice.class,null);
		}catch(Exception e){
			System.out.println(e);			
			e.printStackTrace();
		}

		for(f=0;f<Indices.length;f++){
			currentIndice=(Indice)Indices[f];					

			try{
				Filters=new QueryCondition[4];
				
				Filters[0]=new PostgreSQLFilterCondition();
				((PostgreSQLFilterCondition)Filters[0]).FieldName="Indice";
				((PostgreSQLFilterCondition)Filters[0]).Operator =PostgreSQLQueryCondition.EQUALS;
				((PostgreSQLFilterCondition)Filters[0]).ValueName=currentIndice.getTimestamp();
				
				Filters[1]=new PostgreSQLFilterCondition();
				((PostgreSQLFilterCondition)Filters[1]).FieldName="Pregao";
				((PostgreSQLFilterCondition)Filters[1]).Operator =PostgreSQLQueryCondition.GREATER_EQUAL;
				((PostgreSQLFilterCondition)Filters[1]).ValueName=((Pregao)Pregao.get(Pregao.size()-20)).getTimestamp();
				
				Filters[2]= new PostgreSQLOrderCondition();
				((PostgreSQLOrderCondition)Filters[2]).Field="timestampval";					
				((PostgreSQLOrderCondition)Filters[2]).Order=OrderCondition.ASC;
				
				Filters[3]= new PostgreSQLFilterCondition();
				((PostgreSQLFilterCondition)Filters[3]).FieldName="Pregao";
				((PostgreSQLFilterCondition)Filters[3]).Operator =PostgreSQLQueryCondition.LESS_EQUAL;
				((PostgreSQLFilterCondition)Filters[3]).ValueName=LastPregao.getTimestamp();				
				CotacoesIndice=Blackboard.getBlackboard().Query(CotacaoIndice.class,Filters);								
			}catch(Exception e){
				System.out.println(e);			
				e.printStackTrace();
			}
			
			TrendsAnalised=new Trend[PeriodoAnalise];
			for(h=0;h<TrendsAnalised.length;h++){
			    TrendsAnalised[h]=new Trend();
				TrendsAnalised[h].setIndice(currentIndice);			
			}
			for(g=1;g<CotacoesIndice.length;g++){
				currentCotacaoIndice=(CotacaoIndice)CotacoesIndice[g];
				lastCotacaoIndice=(CotacaoIndice)CotacoesIndice[g-1];
				
				TrendsAnalised[g].setPregao(currentCotacaoIndice.getPregao());
				TrendsAnalised[g-1].setPregao(lastCotacaoIndice.getPregao());
				
				if(currentCotacaoIndice.getFechamento().doubleValue()>
				   lastCotacaoIndice.getFechamento().doubleValue()
				   ){
					Ponto=0;
					if(TrendBaixa>=PeriodoMinimoTrend) Ponto=1;
				 	TrendBaixa=0;
				 	TrendAlta++;
				 	if(TrendAlta>=PeriodoMinimoTrend){
				 		for(h=g-TrendAlta;h<g;h++){
				 			TrendsAnalised[h].setDirecao(new Integer(Trend.TREND_UPWARD));				 			
				 		}
				 	}
				 	if(Ponto==1)
				 		TrendsAnalised[g-1].setDirecao(new Integer(Trend.TREND_SUPPORT));
				   }
				if(currentCotacaoIndice.getFechamento().doubleValue()<
				   lastCotacaoIndice.getFechamento().doubleValue()){
					Ponto=0;
				   	if(TrendAlta>=PeriodoMinimoTrend) Ponto=1;
				   	TrendAlta=0;
					TrendBaixa++;				   	
					if(TrendBaixa>=PeriodoMinimoTrend){
/*						for(h=g-TrendBaixa;h<g;h++){
							TrendsAnalised[h].setDirecao(new Integer(Trend.TREND_DOWNWARD));				 			
						}*/
					}
					if(Ponto==1)
						TrendsAnalised[g-1].setDirecao(new Integer(Trend.TREND_RESISTENCE));
				}
			}
		    try{
				LastValor=0;
		        for(h=0;h<TrendsAnalised.length;h++){
					Filters=new QueryCondition[2];
					Filters[0]=new PostgreSQLFilterCondition();
					((PostgreSQLFilterCondition)Filters[0]).FieldName="Indice";
					((PostgreSQLFilterCondition)Filters[0]).Operator =PostgreSQLQueryCondition.EQUALS;
					((PostgreSQLFilterCondition)Filters[0]).ValueName=TrendsAnalised[h].getIndice().getTimestamp();
					Filters[1]=new PostgreSQLFilterCondition();
					((PostgreSQLFilterCondition)Filters[1]).FieldName="Pregao";
					((PostgreSQLFilterCondition)Filters[1]).Operator =PostgreSQLQueryCondition.EQUALS;
					((PostgreSQLFilterCondition)Filters[1]).ValueName=TrendsAnalised[h].getPregao().getTimestamp();
					Encontrado=Blackboard.getBlackboard().Query(Trend.class,Filters);
					if(Encontrado.length>0) continue;		            		        
					if(TrendsAnalised[h].getDirecao().intValue()>0)
		               Blackboard.getBlackboard().Store(TrendsAnalised[h].getClass(),TrendsAnalised[h]);		               
		        }
		        
		        for(h=0;h<TrendsAnalised.length;h++){
		        	if(TrendsAnalised[h].getDirecao().intValue()>0) LastValor=h;		        	
		        }
		        
		        if(LastValor==0) LastValor=TrendsAnalised.length;
		        
		        for(h=0;h<LastValor;h++){
					Filters=new QueryCondition[2];
					Filters[0]=new PostgreSQLFilterCondition();
					((PostgreSQLFilterCondition)Filters[0]).FieldName="Indice";
					((PostgreSQLFilterCondition)Filters[0]).Operator =PostgreSQLQueryCondition.EQUALS;
					((PostgreSQLFilterCondition)Filters[0]).ValueName=TrendsAnalised[f].getIndice().getTimestamp();
					Filters[1]=new PostgreSQLFilterCondition();
					((PostgreSQLFilterCondition)Filters[1]).FieldName="Pregao";
					((PostgreSQLFilterCondition)Filters[1]).Operator =PostgreSQLQueryCondition.EQUALS;
					((PostgreSQLFilterCondition)Filters[1]).ValueName=TrendsAnalised[f].getPregao().getTimestamp();
					Encontrado=Blackboard.getBlackboard().Query(Trend.class,Filters);
					if(Encontrado.length>0) continue;		            		        
					if(TrendsAnalised[h].getDirecao().intValue()==0)
					   Blackboard.getBlackboard().Store(TrendsAnalised[h].getClass(),TrendsAnalised[h]);		        	
		        }
			}catch(Exception e){
				System.out.println(e);			
				e.printStackTrace();
			}
		}

		for(f=0;f<Papeis.length;f++){
			currentPapel=(Papel)Papeis[f];					

			try{
				Filters=new QueryCondition[4];
				
				Filters[0]=new PostgreSQLFilterCondition();
				((PostgreSQLFilterCondition)Filters[0]).FieldName="Papel";
				((PostgreSQLFilterCondition)Filters[0]).Operator =PostgreSQLQueryCondition.EQUALS;
				((PostgreSQLFilterCondition)Filters[0]).ValueName=currentPapel.getTimestamp();
				
				Filters[1]=new PostgreSQLFilterCondition();
				((PostgreSQLFilterCondition)Filters[1]).FieldName="Pregao";
				((PostgreSQLFilterCondition)Filters[1]).Operator =PostgreSQLQueryCondition.GREATER_EQUAL;
				((PostgreSQLFilterCondition)Filters[1]).ValueName=((Pregao)Pregao.get(Pregao.size()-20)).getTimestamp();
				
				Filters[2]= new PostgreSQLOrderCondition();
				((PostgreSQLOrderCondition)Filters[2]).Field="timestampval";					
				((PostgreSQLOrderCondition)Filters[2]).Order=OrderCondition.ASC;
				
				Filters[3]= new PostgreSQLFilterCondition();
				((PostgreSQLFilterCondition)Filters[3]).FieldName="Pregao";
				((PostgreSQLFilterCondition)Filters[3]).Operator =PostgreSQLQueryCondition.LESS_EQUAL;
				((PostgreSQLFilterCondition)Filters[3]).ValueName=LastPregao.getTimestamp();				
				Cotacoes=Blackboard.getBlackboard().Query(Cotacao.class,Filters);								
			}catch(Exception e){
				System.out.println(e);			
				e.printStackTrace();
			}
			
			TrendsAnalised=new Trend[PeriodoAnalise];
			for(h=0;h<TrendsAnalised.length;h++){
				TrendsAnalised[h]=new Trend();
				TrendsAnalised[h].setPapel(currentPapel);			
			}
			for(g=1;g<Cotacoes.length;g++){
				currentCotacao=(Cotacao)Cotacoes[g];
				lastCotacao=(Cotacao)Cotacoes[g-1];
				
				TrendsAnalised[g].setPregao(currentCotacao.getPregao());
				TrendsAnalised[g-1].setPregao(lastCotacao.getPregao());
				
				if(currentCotacao.getFechamento().doubleValue()>
				   lastCotacao.getFechamento().doubleValue()
				   ){
					Ponto=0;
					if(TrendBaixa>=PeriodoMinimoTrend) Ponto=1;
					TrendBaixa=0;
					TrendAlta++;
					if(TrendAlta>=PeriodoMinimoTrend){
						for(h=g-TrendAlta;h<g;h++){
							TrendsAnalised[h].setDirecao(new Integer(Trend.TREND_UPWARD));				 			
						}
					}
					if(Ponto==1)
						TrendsAnalised[g-1].setDirecao(new Integer(Trend.TREND_SUPPORT));
				   }
				if(currentCotacao.getFechamento().doubleValue()<
				   lastCotacao.getFechamento().doubleValue()){
					Ponto=0;
					if(TrendAlta>=PeriodoMinimoTrend) Ponto=1;
					TrendAlta=0;
					TrendBaixa++;				   	
					if(TrendBaixa>=PeriodoMinimoTrend){
						for(h=g-TrendBaixa;h<g;h++){
							TrendsAnalised[h].setDirecao(new Integer(Trend.TREND_DOWNWARD));				 			
						}
					}
					if(Ponto==1)
						TrendsAnalised[g-1].setDirecao(new Integer(Trend.TREND_RESISTENCE));
				}
			}
			try{
				LastValor=0;
				for(h=0;h<TrendsAnalised.length;h++){
					Filters=new QueryCondition[2];
					Filters[0]=new PostgreSQLFilterCondition();
					((PostgreSQLFilterCondition)Filters[0]).FieldName="Indice";
					((PostgreSQLFilterCondition)Filters[0]).Operator =PostgreSQLQueryCondition.EQUALS;
					((PostgreSQLFilterCondition)Filters[0]).ValueName=TrendsAnalised[h].getIndice().getTimestamp();
					Filters[1]=new PostgreSQLFilterCondition();
					((PostgreSQLFilterCondition)Filters[1]).FieldName="Pregao";
					((PostgreSQLFilterCondition)Filters[1]).Operator =PostgreSQLQueryCondition.EQUALS;
					((PostgreSQLFilterCondition)Filters[1]).ValueName=TrendsAnalised[h].getPregao().getTimestamp();
					Encontrado=Blackboard.getBlackboard().Query(Trend.class,Filters);
					if(Encontrado.length>0) continue;		            		        
					if(TrendsAnalised[h].getDirecao().intValue()>0)
					   Blackboard.getBlackboard().Store(TrendsAnalised[h].getClass(),TrendsAnalised[h]);		               
				}
		        
				for(h=0;h<TrendsAnalised.length;h++){
					if(TrendsAnalised[h].getDirecao().intValue()>0) LastValor=h;		        	
				}
		        
				if(LastValor==0) LastValor=TrendsAnalised.length;
		        
				for(h=0;h<LastValor;h++){
					Filters=new QueryCondition[2];
					Filters[0]=new PostgreSQLFilterCondition();
					((PostgreSQLFilterCondition)Filters[0]).FieldName="Indice";
					((PostgreSQLFilterCondition)Filters[0]).Operator =PostgreSQLQueryCondition.EQUALS;
					((PostgreSQLFilterCondition)Filters[0]).ValueName=TrendsAnalised[f].getIndice().getTimestamp();
					Filters[1]=new PostgreSQLFilterCondition();
					((PostgreSQLFilterCondition)Filters[1]).FieldName="Pregao";
					((PostgreSQLFilterCondition)Filters[1]).Operator =PostgreSQLQueryCondition.EQUALS;
					((PostgreSQLFilterCondition)Filters[1]).ValueName=TrendsAnalised[f].getPregao().getTimestamp();
					Encontrado=Blackboard.getBlackboard().Query(Trend.class,Filters);
					if(Encontrado.length>0) continue;		            		        
					if(TrendsAnalised[h].getDirecao().intValue()==0)
					   Blackboard.getBlackboard().Store(TrendsAnalised[h].getClass(),TrendsAnalised[h]);		        	
				}
			}catch(Exception e){
				System.out.println(e);			
				e.printStackTrace();
			}
		}


	}
	
	protected void CalcularMomentum(int Periodo){
		AgentData   	  Papeis[]=null;
		AgentData   	  Indices[]=null;
		AgentData   	  CotacoesIndice[];
		AgentData   	  CotacoesAcoes[];
		AgentData   	  Cotacoes[];
		Indice      	  currentIndice;
		Papel       	  currentPapel;
		CotacaoIndice     currentCotacaoIndice,lastCotacaoIndice;
		Cotacao           currentCotacao,lastCotacao;
		MovingAverage     currentMovingAverage;
		Momentum          currentMomentum;
		QueryCondition[]  Filters;
		double            Somatorio;
				
		int f,g;
		
		if(Pregao.size()<Periodo) return;
		
		try{
			Papeis=Blackboard.getBlackboard().Query(Papel.class,null);
			Indices=Blackboard.getBlackboard().Query(Indice.class,null);
		}catch(Exception e){
			System.out.println(e);			
			e.printStackTrace();
		}
		
		for(f=0;f<Indices.length;f++){
			currentIndice=(Indice)Indices[f];			
			try{
				Filters=new QueryCondition[3];
				Filters[0]=new PostgreSQLFilterCondition();
				((PostgreSQLFilterCondition)Filters[0]).FieldName="Indice";
				((PostgreSQLFilterCondition)Filters[0]).Operator =PostgreSQLQueryCondition.EQUALS;
				((PostgreSQLFilterCondition)Filters[0]).ValueName=currentIndice.getTimestamp();
				Filters[1]=new PostgreSQLFilterCondition();
				((PostgreSQLFilterCondition)Filters[1]).FieldName="Pregao";
				((PostgreSQLFilterCondition)Filters[1]).Operator =PostgreSQLQueryCondition.LESS_EQUAL;
				((PostgreSQLFilterCondition)Filters[1]).ValueName=LastPregao.getTimestamp();
				Filters[2]= new PostgreSQLOrderCondition();
				((PostgreSQLOrderCondition)Filters[2]).Field="timestampval";					
				((PostgreSQLOrderCondition)Filters[2]).Order=OrderCondition.DESC;
				CotacoesIndice=Blackboard.getBlackboard().Query(CotacaoIndice.class,Filters);								
				Somatorio=0;
				
				currentCotacaoIndice=(CotacaoIndice)CotacoesIndice[0];
				lastCotacaoIndice=(CotacaoIndice)CotacoesIndice[5];
				Somatorio=currentCotacaoIndice.getFechamento().doubleValue()-lastCotacaoIndice.getFechamento().doubleValue();				
				
				currentMomentum=new Momentum();
				currentMomentum.setIndice(currentIndice);
				currentMomentum.setPeriodo(new Integer(Periodo));
				currentMomentum.setPregao(LastPregao);
				currentMomentum.setValor(new Double(Somatorio));				
				Blackboard.getBlackboard().Store(currentMomentum.getClass(),currentMomentum);								
			}catch(Exception e){
				System.out.println(e);
				e.printStackTrace();
			}			
		}
		
		for(f=0;f<Papeis.length;f++){
			currentPapel=(Papel)Papeis[f];
			
			try{
				Filters=new QueryCondition[3];
				Filters[0]=new PostgreSQLFilterCondition();
				((PostgreSQLFilterCondition)Filters[0]).FieldName="Papel";
				((PostgreSQLFilterCondition)Filters[0]).Operator =PostgreSQLQueryCondition.EQUALS;
				((PostgreSQLFilterCondition)Filters[0]).ValueName=currentPapel.getTimestamp();
				Filters[1]=new PostgreSQLFilterCondition();
				((PostgreSQLFilterCondition)Filters[1]).FieldName="Pregao";
				((PostgreSQLFilterCondition)Filters[1]).Operator =PostgreSQLQueryCondition.LESS_EQUAL;
				((PostgreSQLFilterCondition)Filters[1]).ValueName=LastPregao.getTimestamp();
				Filters[2]= new PostgreSQLOrderCondition();
				((PostgreSQLOrderCondition)Filters[2]).Field="timestampval";					
				((PostgreSQLOrderCondition)Filters[2]).Order=OrderCondition.DESC;
				Cotacoes=Blackboard.getBlackboard().Query(Cotacao.class,Filters);								
				Somatorio=0;
				
				currentCotacao=(Cotacao)Cotacoes[0];
				lastCotacao=(Cotacao)Cotacoes[5];
				Somatorio=currentCotacao.getFechamento().doubleValue()-lastCotacao.getFechamento().doubleValue();				
				
				currentMomentum=new Momentum();
				currentMomentum.setPapel(currentPapel);
				currentMomentum.setPeriodo(new Integer(Periodo));
				currentMomentum.setPregao(LastPregao);
				currentMomentum.setValor(new Double(Somatorio));				
				Blackboard.getBlackboard().Store(currentMomentum.getClass(),currentMomentum);								
			}catch(Exception e){
				System.out.println(e);
				e.printStackTrace();
			}			
		}


	}
	
	protected void CalcularMovingAverages(int Periodo){
		AgentData   	  Papeis[]=null;
		AgentData   	  Indices[]=null;
		AgentData   	  CotacoesIndice[];
		AgentData   	  Cotacoes[];
		AgentData   	  CotacoesAcoes[];
		Indice      	  currentIndice;
		Papel       	  currentPapel;
		CotacaoIndice     currentCotacaoIndice;
		Cotacao           currentCotacao;		
		MovingAverage     currentMovingAverage;
		QueryCondition[]  Filters;
		double            Somatorio;
				
		int f,g;
		
		if(Pregao.size()<Periodo) return;
		
		try{
			Papeis=Blackboard.getBlackboard().Query(Papel.class,null);
			Indices=Blackboard.getBlackboard().Query(Indice.class,null);
		}catch(Exception e){
			System.out.println(e);			
			e.printStackTrace();
		}
		
		for(f=0;f<Indices.length;f++){
			currentIndice=(Indice)Indices[f];
			
			try{
				Filters=new QueryCondition[3];
				Filters[0]=new PostgreSQLFilterCondition();
				((PostgreSQLFilterCondition)Filters[0]).FieldName="Indice";
				((PostgreSQLFilterCondition)Filters[0]).Operator =PostgreSQLQueryCondition.EQUALS;
				((PostgreSQLFilterCondition)Filters[0]).ValueName=currentIndice.getTimestamp();
				Filters[1]=new PostgreSQLFilterCondition();
				((PostgreSQLFilterCondition)Filters[1]).FieldName="Pregao";
				((PostgreSQLFilterCondition)Filters[1]).Operator =PostgreSQLQueryCondition.LESS_EQUAL;
				((PostgreSQLFilterCondition)Filters[1]).ValueName=LastPregao.getTimestamp();
				Filters[2]= new PostgreSQLOrderCondition();
				((PostgreSQLOrderCondition)Filters[2]).Field="timestampval";					
				((PostgreSQLOrderCondition)Filters[2]).Order=OrderCondition.DESC;
				CotacoesIndice=Blackboard.getBlackboard().Query(CotacaoIndice.class,Filters);								
				Somatorio=0;
				for(g=0;g<Periodo;g++){
					currentCotacaoIndice=(CotacaoIndice)CotacoesIndice[g];
					Somatorio+=currentCotacaoIndice.getFechamento().doubleValue();
				}
				Somatorio=Somatorio/Periodo;
				currentMovingAverage=new MovingAverage();
				currentMovingAverage.setIndice(currentIndice);
				currentMovingAverage.setPeriodo(new Integer(Periodo));
				currentMovingAverage.setPregao(LastPregao);
				currentMovingAverage.setValor(new Double(Somatorio));				
				Blackboard.getBlackboard().Store(currentMovingAverage.getClass(),currentMovingAverage);								
			}catch(Exception e){
				System.out.println(e);
				e.printStackTrace();				
			}
			
		}
		
		for(f=0;f<Papeis.length;f++){
			currentPapel=(Papel)Papeis[f];
			
			try{
				Filters=new QueryCondition[3];
				Filters[0]=new PostgreSQLFilterCondition();
				((PostgreSQLFilterCondition)Filters[0]).FieldName="Papel";
				((PostgreSQLFilterCondition)Filters[0]).Operator =PostgreSQLQueryCondition.EQUALS;
				((PostgreSQLFilterCondition)Filters[0]).ValueName=currentPapel.getTimestamp();
				Filters[1]=new PostgreSQLFilterCondition();
				((PostgreSQLFilterCondition)Filters[1]).FieldName="Pregao";
				((PostgreSQLFilterCondition)Filters[1]).Operator =PostgreSQLQueryCondition.LESS_EQUAL;
				((PostgreSQLFilterCondition)Filters[1]).ValueName=LastPregao.getTimestamp();
				Filters[2]= new PostgreSQLOrderCondition();
				((PostgreSQLOrderCondition)Filters[2]).Field="timestampval";					
				((PostgreSQLOrderCondition)Filters[2]).Order=OrderCondition.DESC;
				Cotacoes=Blackboard.getBlackboard().Query(Cotacao.class,Filters);								
				Somatorio=0;
				for(g=0;g<Periodo;g++){
					currentCotacao=(Cotacao)Cotacoes[g];
					Somatorio+=currentCotacao.getFechamento().doubleValue();
				}
				Somatorio=Somatorio/Periodo;
				currentMovingAverage=new MovingAverage();
				currentMovingAverage.setPapel(currentPapel);
				currentMovingAverage.setPeriodo(new Integer(Periodo));
				currentMovingAverage.setPregao(LastPregao);
				currentMovingAverage.setValor(new Double(Somatorio));				
				Blackboard.getBlackboard().Store(currentMovingAverage.getClass(),currentMovingAverage);								
			}catch(Exception e){
				System.out.println(e);
				e.printStackTrace();				
			}
			
		}
		
		
	}
	
	protected void CalcularRSI(int Periodo){
		AgentData   	  Papeis[]=null;
		AgentData   	  Indices[]=null;
		AgentData   	  CotacoesIndice[];
		AgentData   	  CotacoesAcoes[];
		AgentData   	  Cotacoes[];
		Indice      	  currentIndice;
		Papel       	  currentPapel;
		CotacaoIndice     currentCotacaoIndice,lastCotacaoIndice;
		Cotacao           currentCotacao,lastCotacao;		
		MovingAverage     currentMovingAverage;
		QueryCondition[]  Filters;
		double            SomatorioAlta=0,SomatorioBaixa=0,Total;
		int               ItensAlta=0,ItensBaixa=0;
		RSI               currentRSI;
				
		int f,g;
		
		if(Pregao.size()<Periodo) return;		
		try{
			Papeis=Blackboard.getBlackboard().Query(Papel.class,null);
			Indices=Blackboard.getBlackboard().Query(Indice.class,null);
		}catch(Exception e){
			System.out.println(e);			
			e.printStackTrace();
		}
		
		for(f=0;f<Indices.length;f++){
			currentIndice=(Indice)Indices[f];

			try{
				Filters=new QueryCondition[3];
				Filters[0]=new PostgreSQLFilterCondition();
				((PostgreSQLFilterCondition)Filters[0]).FieldName="Indice";
				((PostgreSQLFilterCondition)Filters[0]).Operator =PostgreSQLQueryCondition.EQUALS;
				((PostgreSQLFilterCondition)Filters[0]).ValueName=currentIndice.getTimestamp();
				Filters[1]=new PostgreSQLFilterCondition();
				((PostgreSQLFilterCondition)Filters[1]).FieldName="Pregao";
				((PostgreSQLFilterCondition)Filters[1]).Operator =PostgreSQLQueryCondition.LESS_EQUAL;
				((PostgreSQLFilterCondition)Filters[1]).ValueName=LastPregao.getTimestamp();
				Filters[2]= new PostgreSQLOrderCondition();
				((PostgreSQLOrderCondition)Filters[2]).Field="timestampval";					
				((PostgreSQLOrderCondition)Filters[2]).Order=OrderCondition.DESC;
				CotacoesIndice=Blackboard.getBlackboard().Query(CotacaoIndice.class,Filters);								

                if(MediaAlta==0 && MediaBaixa==0){
					for(g=0;g<Periodo;g++){
						currentCotacaoIndice=(CotacaoIndice)CotacoesIndice[g];
						if(currentCotacaoIndice.getFechamento().doubleValue()>
					   		currentCotacaoIndice.getAbertura().doubleValue()){
					     	SomatorioAlta+=currentCotacaoIndice.getFechamento().doubleValue()-currentCotacaoIndice.getAbertura().doubleValue();
					     	ItensAlta++;
					   		}else{
						 	SomatorioBaixa+=currentCotacaoIndice.getAbertura().doubleValue()-currentCotacaoIndice.getFechamento().doubleValue();
						 	ItensBaixa++;						
						}
					}
					MediaAlta =SomatorioAlta /Periodo;
					MediaBaixa=SomatorioBaixa/Periodo;
                }else{
					double ValorAtual=0;
					
					currentCotacaoIndice=(CotacaoIndice)CotacoesIndice[0];
					if(currentCotacaoIndice.getFechamento().doubleValue()>
					   currentCotacaoIndice.getAbertura().doubleValue()){
					    MediaAlta=((MediaAlta*(Periodo-1))+(currentCotacaoIndice.getFechamento().doubleValue()-currentCotacaoIndice.getAbertura().doubleValue()))/Periodo;
					    MediaBaixa=(MediaBaixa*(Periodo-1))/Periodo;	
					}else{
						MediaAlta=(MediaAlta*(Periodo-1))/Periodo;
						MediaBaixa=((MediaBaixa*(Periodo-1))+(currentCotacaoIndice.getAbertura().doubleValue()-currentCotacaoIndice.getFechamento().doubleValue()))/Periodo;					   					   	
					}
                }
				Total=100-(100/(1 + ((MediaAlta)/(MediaBaixa))  ));
				currentRSI=new RSI();
				currentRSI.setIndice(currentIndice);
				currentRSI.setPeriodo(new Integer(Periodo));
				currentRSI.setPregao(LastPregao);
				currentRSI.setValor(new Double(Total));								
				Blackboard.getBlackboard().Store(currentRSI.getClass(),currentRSI);								
			}catch(Exception e){
				System.out.println(e);
				e.printStackTrace();				
			}
			
		}

		for(f=0;f<Papeis.length;f++){
			currentPapel=(Papel)Papeis[f];

			try{
				Filters=new QueryCondition[3];
				Filters[0]=new PostgreSQLFilterCondition();
				((PostgreSQLFilterCondition)Filters[0]).FieldName="Papel";
				((PostgreSQLFilterCondition)Filters[0]).Operator =PostgreSQLQueryCondition.EQUALS;
				((PostgreSQLFilterCondition)Filters[0]).ValueName=currentPapel.getTimestamp();
				Filters[1]=new PostgreSQLFilterCondition();
				((PostgreSQLFilterCondition)Filters[1]).FieldName="Pregao";
				((PostgreSQLFilterCondition)Filters[1]).Operator =PostgreSQLQueryCondition.LESS_EQUAL;
				((PostgreSQLFilterCondition)Filters[1]).ValueName=LastPregao.getTimestamp();
				Filters[2]= new PostgreSQLOrderCondition();
				((PostgreSQLOrderCondition)Filters[2]).Field="timestampval";					
				((PostgreSQLOrderCondition)Filters[2]).Order=OrderCondition.DESC;
				Cotacoes=Blackboard.getBlackboard().Query(Cotacao.class,Filters);								

				if(MediaAlta==0 && MediaBaixa==0){
					for(g=0;g<Periodo;g++){
						currentCotacao=(Cotacao)Cotacoes[g];
						if(currentCotacao.getFechamento().doubleValue()>
							currentCotacao.getAbertura().doubleValue()){
							SomatorioAlta+=currentCotacao.getFechamento().doubleValue()-currentCotacao.getAbertura().doubleValue();
							ItensAlta++;
							}else{
							SomatorioBaixa+=currentCotacao.getAbertura().doubleValue()-currentCotacao.getFechamento().doubleValue();
							ItensBaixa++;						
						}
					}
					MediaAlta =SomatorioAlta /Periodo;
					MediaBaixa=SomatorioBaixa/Periodo;
				}else{
					double ValorAtual=0;
					
					currentCotacao=(Cotacao)Cotacoes[0];
					if(currentCotacao.getFechamento().doubleValue()>
					   currentCotacao.getAbertura().doubleValue()){
						MediaAlta=((MediaAlta*(Periodo-1))+(currentCotacao.getFechamento().doubleValue()-currentCotacao.getAbertura().doubleValue()))/Periodo;
						MediaBaixa=(MediaBaixa*(Periodo-1))/Periodo;	
					}else{
						MediaAlta=(MediaAlta*(Periodo-1))/Periodo;
						MediaBaixa=((MediaBaixa*(Periodo-1))+(currentCotacao.getAbertura().doubleValue()-currentCotacao.getFechamento().doubleValue()))/Periodo;					   					   	
					}
				}
				Total=100-(100/(1 + ((MediaAlta)/(MediaBaixa))  ));
				currentRSI=new RSI();
				currentRSI.setPapel(currentPapel);
				currentRSI.setPeriodo(new Integer(Periodo));
				currentRSI.setPregao(LastPregao);
				currentRSI.setValor(new Double(Total));								
				Blackboard.getBlackboard().Store(currentRSI.getClass(),currentRSI);								
			}catch(Exception e){
				System.out.println(e);
				e.printStackTrace();				
			}
			
		}

		
	}
		
	public void run(){
		AgentData                     Pregoes[]=null;
		QueryCondition[]              Filters;		
		int                           f;
		Class                        DataToUseInExpertSystem[];		
		
		try{		
			DataToUseInExpertSystem=new Class[9];
			DataToUseInExpertSystem[0]=Class.forName("org.abia.Agents.BovespaAgent.Papel");
			DataToUseInExpertSystem[1]=Class.forName("org.abia.Agents.TechnicalAnalisysAgent.BollingerBands");
			DataToUseInExpertSystem[2]=Class.forName("org.abia.Agents.TechnicalAnalisysAgent.Momentum");
			DataToUseInExpertSystem[3]=Class.forName("org.abia.Agents.TechnicalAnalisysAgent.MovingAverage");
			DataToUseInExpertSystem[4]=Class.forName("org.abia.Agents.TechnicalAnalisysAgent.RSI");
			DataToUseInExpertSystem[5]=Class.forName("org.abia.Agents.TechnicalAnalisysAgent.StochasticOscillator");
			DataToUseInExpertSystem[6]=Class.forName("org.abia.Agents.TechnicalAnalisysAgent.TechnicalAnalisys");
			DataToUseInExpertSystem[7]=Class.forName("org.abia.Agents.TechnicalAnalisysAgent.TechnicalSubAnalisys");
			DataToUseInExpertSystem[8]=Class.forName("org.abia.Agents.TechnicalAnalisysAgent.Trend");
			myCLIPServer=new CLIPServer("TechnicalAnalisysCLIPServer",8901,"TechnicalAnalisys.clp",DataToUseInExpertSystem);
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
		}
		
		
		while(true){
			if(LastPregao==null){
				try{			
					Pregoes=Blackboard.getBlackboard().Query(Pregao.class,null);
					if(Pregoes.length==0){
						Thread.sleep(1000);
						continue;
					}									   	  
				}catch(Exception e){
					System.out.println(e);					
					e.printStackTrace();
				}
			}else{
				try{										
					Filters=new QueryCondition[2];
					Filters[0]=new PostgreSQLFilterCondition();
					((PostgreSQLFilterCondition)Filters[0]).FieldName="timestamp";
					((PostgreSQLFilterCondition)Filters[0]).Operator =PostgreSQLQueryCondition.GREATER;
					((PostgreSQLFilterCondition)Filters[0]).ValueName=LastPregao.getTimestamp();
					Filters[1]= new PostgreSQLOrderCondition();
					((PostgreSQLOrderCondition)Filters[1]).Field="timestampval";					
					((PostgreSQLOrderCondition)Filters[1]).Order=OrderCondition.ASC;					
					Pregoes=Blackboard.getBlackboard().Query(Pregao.class,Filters);
					if(Pregoes.length==0){
						Thread.sleep(1000);
						continue;
					}
				}catch(Exception e){
					System.out.println(e);
					e.printStackTrace();
				}				
			}
			for(f=0;f<Pregoes.length;f++){
				Pregao.add(Pregoes[f]);
				LastPregao=(Pregao)Pregoes[f];
				/*System.out.println("Calculando Trend...");
				CalcularTrend();*/
				System.out.println("Calculando Moving Averages(5)...");			
				CalcularMovingAverages(5);
/*				System.out.println("Calculando Moving Averages(10)...");
				CalcularMovingAverages(10);
				System.out.println("Calculando Moving Averages(50)...");
				CalcularMovingAverages(50);*/
				System.out.println("Calculando RSI(14)...");
				CalcularRSI(14);
/*				System.out.println("Calculando Momentum...");
				CalcularMomentum(10);*/
				System.out.println("Calculando Bollinger(14)...");
				CalcularBollinger(14);
				System.out.println("Calculando Stochastic(14)...");
				CalcularStochasticOscillator(14);
			}
		}
	}

	public void Initialize() throws AgentException{
		Class[] ClassesToRegister;
		
		ClassesToRegister=new Class[8];		
		try {		
			ClassesToRegister[0]=Class.forName("org.abia.Agents.TechnicalAnalisysAgent.BollingerBands");		
			ClassesToRegister[1]=Class.forName("org.abia.Agents.TechnicalAnalisysAgent.MovingAverage");
			ClassesToRegister[2]=Class.forName("org.abia.Agents.TechnicalAnalisysAgent.RSI");		
			ClassesToRegister[3]=Class.forName("org.abia.Agents.TechnicalAnalisysAgent.StochasticOscillator");
			ClassesToRegister[4]=Class.forName("org.abia.Agents.TechnicalAnalisysAgent.TechnicalAnalisys");		
			ClassesToRegister[5]=Class.forName("org.abia.Agents.TechnicalAnalisysAgent.TechnicalSubAnalisys");
			ClassesToRegister[6]=Class.forName("org.abia.Agents.TechnicalAnalisysAgent.Trend");
			ClassesToRegister[7]=Class.forName("org.abia.Agents.TechnicalAnalisysAgent.Momentum");			
			Blackboard.getBlackboard().RegisterAgentData(ClassesToRegister);
			Blackboard.getBlackboard().RegisterAgent(this);
		}catch(Exception e){
			throw (new AgentException());
		}		
	}

	public String getAgentNameInBlackboard(){
		return "TechnicalAnalisysAgent";		
	}


}