package org.abia.Agents.FundamentalAnalisysAgent;

import org.abia.AgentContainer.*;
import org.abia.Blackboard.*;
import org.abia.Blackboard.PostgreSQL.*;
import org.abia.Agents.CVMAgent.*;
import org.abia.Agents.BovespaAgent.*;
import org.abia.utils.CLIPServer.*;

public class FundamentalAnalisysAgent extends AnalisysAgent {
	protected CLIPServer myCLIPServer;
	
	static {
		System.out.println("Carregado:FundamentalAnalisysAgent");		
	}	
	
	public FundamentalAnalisysAgent(){
				
	}
	

	public void Initialize() throws AgentException{
		Class[] ClassesToRegister;
		
		ClassesToRegister=new Class[8];		
		try {		
			ClassesToRegister[0]=Class.forName("org.abia.Agents.FundamentalAnalisysAgent.ComposicaoIndividamento");
			ClassesToRegister[1]=Class.forName("org.abia.Agents.FundamentalAnalisysAgent.FundamentalAnalisys");		
			ClassesToRegister[2]=Class.forName("org.abia.Agents.FundamentalAnalisysAgent.FundamentalSubAnalisys");
			ClassesToRegister[3]=Class.forName("org.abia.Agents.FundamentalAnalisysAgent.GiroDoAtivo");		
			ClassesToRegister[4]=Class.forName("org.abia.Agents.FundamentalAnalisysAgent.ImobilizacaoPatrimonioLiquido");
			ClassesToRegister[5]=Class.forName("org.abia.Agents.FundamentalAnalisysAgent.LiquidezCorrente");		
			ClassesToRegister[6]=Class.forName("org.abia.Agents.FundamentalAnalisysAgent.LiquidezGeral");
			ClassesToRegister[7]=Class.forName("org.abia.Agents.FundamentalAnalisysAgent.RetornoSobreVendas");
			Blackboard.getBlackboard().RegisterAgentData(ClassesToRegister);
			Blackboard.getBlackboard().RegisterAgent(this);
		}catch(Exception e){
			throw (new AgentException());
		}		
	}

	public String getAgentNameInBlackboard(){
		return "FundamentalAnalisysAgent";		
	}
	
	public void run(){
		int               f;
		AgentData         Balancetes[]=null;
		AgentData         LastBalancete=null;
		QueryCondition[]  Filters;
		Class             DataToUseInExpertSystem[];		
		
		try{		
			DataToUseInExpertSystem=new Class[9];
			DataToUseInExpertSystem[0]=Class.forName("org.abia.Agents.CVMAgent.Balancete");
			DataToUseInExpertSystem[1]=Class.forName("org.abia.Agents.FundamentalAnalisysAgent.FundamentalAnalisys");
			DataToUseInExpertSystem[2]=Class.forName("org.abia.Agents.FundamentalAnalisysAgent.FundamentalSubAnalisys");
			DataToUseInExpertSystem[3]=Class.forName("org.abia.Agents.FundamentalAnalisysAgent.ComposicaoIndividamento");
			DataToUseInExpertSystem[4]=Class.forName("org.abia.Agents.FundamentalAnalisysAgent.GiroDoAtivo");
			DataToUseInExpertSystem[5]=Class.forName("org.abia.Agents.FundamentalAnalisysAgent.ImobilizacaoPatrimonioLiquido");
			DataToUseInExpertSystem[6]=Class.forName("org.abia.Agents.FundamentalAnalisysAgent.LiquidezCorrente");
			DataToUseInExpertSystem[7]=Class.forName("org.abia.Agents.FundamentalAnalisysAgent.LiquidezGeral");
			DataToUseInExpertSystem[8]=Class.forName("org.abia.Agents.FundamentalAnalisysAgent.RetornoSobreVendas");			
		    myCLIPServer=new CLIPServer("FundamentalAnalisysCLIPServer",7903,"FundamentalAnalisys.clp",DataToUseInExpertSystem);
		}catch(Exception e){
		   System.out.println(e);
		   e.printStackTrace();
		}
		
		while(true){
			if(LastBalancete==null){
				try{			
					Balancetes=Blackboard.getBlackboard().Query(Balancete.class,null);
					if(Balancetes.length==0){
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
					((PostgreSQLFilterCondition)Filters[0]).ValueName=LastBalancete.getTimestamp();
					Filters[1]= new PostgreSQLOrderCondition();
					((PostgreSQLOrderCondition)Filters[1]).Field="timestampval";					
					((PostgreSQLOrderCondition)Filters[1]).Order=OrderCondition.ASC;					
					Balancetes=Blackboard.getBlackboard().Query(Balancete.class,Filters);
					if(Balancetes.length==0){
						Thread.sleep(1000);
						continue;
					}
				}catch(Exception e){
					System.out.println(e);
					e.printStackTrace();
				}				
			}
			for(f=0;f<Balancetes.length;f++){
				LastBalancete=(Balancete)Balancetes[f];
				if(((Balancete)LastBalancete).getDenominacaoSocial().length()==0) 
				     continue;
				
				LastBalancete.ResolveReferences();
				CalcularComposicaoIndividamento      ((Balancete)LastBalancete);			
				CalcularGiroDoAtivo                  ((Balancete)LastBalancete);
				CalcularImobilizacaoPatrimonioLiquido((Balancete)LastBalancete);
				CalcularLiquidezCorrente             ((Balancete)LastBalancete);
				CalcularLiquidezGeral                ((Balancete)LastBalancete);
				CalcularRetornoSobreVendas           ((Balancete)LastBalancete);
				
			}
		}
		
	}
	
	protected void CalcularComposicaoIndividamento      (Balancete currentBalancete){
		AgentData                PassivoCirculante[]=null;
		AgentData                ExigivelLongoPrazo[]=null;
		AgentData                toStore[]=null;
		ComposicaoIndividamento  currentComposicaoIndividamento;
		QueryCondition[]         Filters;
				
				

        System.out.println("Gerando ComposicaoIndividamento para "+((Empresa)currentBalancete.getEmpresaEmitente()).getNomeEmpresa());
		currentComposicaoIndividamento=new ComposicaoIndividamento(); 				
	 	try{
			Filters=new QueryCondition[2];
			Filters[0]=new PostgreSQLFilterCondition();
			((PostgreSQLFilterCondition)Filters[0]).FieldName="descricaoconta";
			((PostgreSQLFilterCondition)Filters[0]).Operator =PostgreSQLQueryCondition.STARTS_WITH;
			((PostgreSQLFilterCondition)Filters[0]).ValueName=new String("PASSIVO EXIGIVEL");
			Filters[1]= new PostgreSQLFilterCondition();
			((PostgreSQLFilterCondition)Filters[1]).FieldName="balancete";					
			((PostgreSQLFilterCondition)Filters[1]).Operator =PostgreSQLQueryCondition.EQUALS;					
			((PostgreSQLFilterCondition)Filters[1]).ValueName=currentBalancete.getTimestamp();
			ExigivelLongoPrazo=Blackboard.getBlackboard().Query(ContaContabil.class,Filters);
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();			 
		}

		try{
			Filters=new QueryCondition[2];
			Filters[0]=new PostgreSQLFilterCondition();
			((PostgreSQLFilterCondition)Filters[0]).FieldName="descricaoconta";
			((PostgreSQLFilterCondition)Filters[0]).Operator =PostgreSQLQueryCondition.STARTS_WITH;
			((PostgreSQLFilterCondition)Filters[0]).ValueName=new String("PASSIVO CIRCULANTE");
			Filters[1]= new PostgreSQLFilterCondition();
			((PostgreSQLFilterCondition)Filters[1]).FieldName="balancete";					
			((PostgreSQLFilterCondition)Filters[1]).Operator =PostgreSQLQueryCondition.EQUALS;					
			((PostgreSQLFilterCondition)Filters[1]).ValueName=currentBalancete.getTimestamp();
			PassivoCirculante=Blackboard.getBlackboard().Query(ContaContabil.class,Filters);
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();			 
		}
		
		currentComposicaoIndividamento.setBalancete(currentBalancete);
		if(PassivoCirculante.length==0 || ExigivelLongoPrazo.length==0 ){
		    currentComposicaoIndividamento.setValor(new Double(0));
		}else{
			double val;
			
			val=(((ContaContabil)PassivoCirculante[0]).getValor().doubleValue())/(((ContaContabil)PassivoCirculante[0]).getValor().doubleValue()+((ContaContabil)ExigivelLongoPrazo[0]).getValor().doubleValue());
			if( ((ContaContabil)PassivoCirculante[0]).getValor().doubleValue()+((ContaContabil)ExigivelLongoPrazo[0]).getValor().doubleValue()==0) {
			   currentComposicaoIndividamento.setValor(new Double(0));	
			}else{
			   currentComposicaoIndividamento.setValor(new Double(val));
			}
		}
		
		try{
			toStore=new AgentData[1];
			toStore[0]=currentComposicaoIndividamento;
			Blackboard.getBlackboard().Store(toStore);
		}catch(Exception e){
			System.out.println(e);
		}
	}
	protected void CalcularGiroDoAtivo                  (Balancete currentBalancete){
		AgentData                VendasLiquidas[]=null;
		AgentData                AtivoTotal[]=null;
		AgentData                toStore[]=null;
		GiroDoAtivo              currentGiroDoAtivo;
		QueryCondition[]         Filters;
				
				

		System.out.println("Gerando GiroDoAtivo para "+((Empresa)currentBalancete.getEmpresaEmitente()).getNomeEmpresa());
		currentGiroDoAtivo=new GiroDoAtivo(); 				
		try{
			Filters=new QueryCondition[2];
			Filters[0]=new PostgreSQLFilterCondition();
			((PostgreSQLFilterCondition)Filters[0]).FieldName="descricaoconta";
			((PostgreSQLFilterCondition)Filters[0]).Operator =PostgreSQLQueryCondition.STARTS_WITH;
			((PostgreSQLFilterCondition)Filters[0]).ValueName=new String("ATIVO TOTAL");
			Filters[1]= new PostgreSQLFilterCondition();
			((PostgreSQLFilterCondition)Filters[1]).FieldName="balancete";					
			((PostgreSQLFilterCondition)Filters[1]).Operator =PostgreSQLQueryCondition.EQUALS;					
			((PostgreSQLFilterCondition)Filters[1]).ValueName=currentBalancete.getTimestamp();
			AtivoTotal=Blackboard.getBlackboard().Query(ContaContabil.class,Filters);
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();			 
		}

		try{
			Filters=new QueryCondition[2];
			Filters[0]=new PostgreSQLFilterCondition();
			((PostgreSQLFilterCondition)Filters[0]).FieldName="descricaoconta";
			((PostgreSQLFilterCondition)Filters[0]).Operator =PostgreSQLQueryCondition.STARTS_WITH;
			((PostgreSQLFilterCondition)Filters[0]).ValueName=new String("RECEITA BRUTA");
			Filters[1]= new PostgreSQLFilterCondition();
			((PostgreSQLFilterCondition)Filters[1]).FieldName="balancete";					
			((PostgreSQLFilterCondition)Filters[1]).Operator =PostgreSQLQueryCondition.EQUALS;					
			((PostgreSQLFilterCondition)Filters[1]).ValueName=currentBalancete.getTimestamp();
			VendasLiquidas=Blackboard.getBlackboard().Query(ContaContabil.class,Filters);
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();			 
		}
		
		currentGiroDoAtivo.setBalancete(currentBalancete);
		if(VendasLiquidas.length==0 || AtivoTotal.length==0 ){
			currentGiroDoAtivo.setValor(new Double(0));
		}else{
			double val,VL,AT;
			
			VL=((ContaContabil)VendasLiquidas[0]).getValor().doubleValue();
			AT=((ContaContabil)AtivoTotal[0]).getValor().doubleValue();
			if(AT==0) {
				currentGiroDoAtivo.setValor(new Double(0));	
			}else{
				currentGiroDoAtivo.setValor(new Double(VL/AT));
			}
		}
		
		try{
			toStore=new AgentData[1];
			toStore[0]=currentGiroDoAtivo;
			Blackboard.getBlackboard().Store(toStore);
		}catch(Exception e){
			System.out.println(e);
		}


	}
	protected void CalcularImobilizacaoPatrimonioLiquido(Balancete currentBalancete){
		AgentData                     AtivoPermanente[]=null;
		AgentData                     PatrimonioLiquido[]=null;
		AgentData                     toStore[]=null;
		ImobilizacaoPatrimonioLiquido currentImobilizacaoPatrimonioLiquido;
		QueryCondition[]              Filters;
				
				

		System.out.println("Gerando ImobilizacaoPatrimonioLiquido para "+((Empresa)currentBalancete.getEmpresaEmitente()).getNomeEmpresa());
		currentImobilizacaoPatrimonioLiquido=new ImobilizacaoPatrimonioLiquido(); 				
		try{
			Filters=new QueryCondition[2];
			Filters[0]=new PostgreSQLFilterCondition();
			((PostgreSQLFilterCondition)Filters[0]).FieldName="descricaoconta";
			((PostgreSQLFilterCondition)Filters[0]).Operator =PostgreSQLQueryCondition.STARTS_WITH;
			((PostgreSQLFilterCondition)Filters[0]).ValueName=new String("ATIVO PERMANENTE");
			Filters[1]= new PostgreSQLFilterCondition();
			((PostgreSQLFilterCondition)Filters[1]).FieldName="balancete";					
			((PostgreSQLFilterCondition)Filters[1]).Operator =PostgreSQLQueryCondition.EQUALS;					
			((PostgreSQLFilterCondition)Filters[1]).ValueName=currentBalancete.getTimestamp();
			AtivoPermanente=Blackboard.getBlackboard().Query(ContaContabil.class,Filters);
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();			 
		}

		try{
			Filters=new QueryCondition[2];
			Filters[0]=new PostgreSQLFilterCondition();
			((PostgreSQLFilterCondition)Filters[0]).FieldName="descricaoconta";
			((PostgreSQLFilterCondition)Filters[0]).Operator =PostgreSQLQueryCondition.STARTS_WITH;
			((PostgreSQLFilterCondition)Filters[0]).ValueName=new String("PATRIMONIO LIQUIDO");
			Filters[1]= new PostgreSQLFilterCondition();
			((PostgreSQLFilterCondition)Filters[1]).FieldName="balancete";					
			((PostgreSQLFilterCondition)Filters[1]).Operator =PostgreSQLQueryCondition.EQUALS;					
			((PostgreSQLFilterCondition)Filters[1]).ValueName=currentBalancete.getTimestamp();
			PatrimonioLiquido=Blackboard.getBlackboard().Query(ContaContabil.class,Filters);
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();			 
		}
		
		currentImobilizacaoPatrimonioLiquido.setBalancete(currentBalancete);
		if(AtivoPermanente.length==0 || PatrimonioLiquido.length==0 ){
			currentImobilizacaoPatrimonioLiquido.setValor(new Double(0));
		}else{
			double val,AP,PL;
			
			AP=((ContaContabil)AtivoPermanente[0]).getValor().doubleValue();
			PL=((ContaContabil)PatrimonioLiquido[0]).getValor().doubleValue();
			if(PL==0) {
				currentImobilizacaoPatrimonioLiquido.setValor(new Double(0));	
			}else{
				currentImobilizacaoPatrimonioLiquido.setValor(new Double(AP/PL));
			}
		}
		
		try{
			toStore=new AgentData[1];
			toStore[0]=currentImobilizacaoPatrimonioLiquido;
			Blackboard.getBlackboard().Store(toStore);
		}catch(Exception e){
			System.out.println(e);
		}
	}
	protected void CalcularLiquidezGeral(Balancete currentBalancete){
		AgentData                     AtivoCirculante[]=null;
		AgentData                     RealizavelLongoPrazo[]=null;
		AgentData                     PassivoCirculante[]=null;
		AgentData                     ExigivelLongoPrazo[]=null;
		AgentData                     toStore[]=null;
		LiquidezGeral                 currentLiquidezGeral;
		QueryCondition[]              Filters;
				
				

		System.out.println("Gerando LiquidezGeral para "+((Empresa)currentBalancete.getEmpresaEmitente()).getNomeEmpresa());
		currentLiquidezGeral=new LiquidezGeral(); 				
		try{
			Filters=new QueryCondition[2];
			Filters[0]=new PostgreSQLFilterCondition();
			((PostgreSQLFilterCondition)Filters[0]).FieldName="descricaoconta";
			((PostgreSQLFilterCondition)Filters[0]).Operator =PostgreSQLQueryCondition.STARTS_WITH;
			((PostgreSQLFilterCondition)Filters[0]).ValueName=new String("ATIVO CIRCULANTE");
			Filters[1]= new PostgreSQLFilterCondition();
			((PostgreSQLFilterCondition)Filters[1]).FieldName="balancete";					
			((PostgreSQLFilterCondition)Filters[1]).Operator =PostgreSQLQueryCondition.EQUALS;					
			((PostgreSQLFilterCondition)Filters[1]).ValueName=currentBalancete.getTimestamp();
			AtivoCirculante=Blackboard.getBlackboard().Query(ContaContabil.class,Filters);
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();			 
		}

		try{
			Filters=new QueryCondition[2];
			Filters[0]=new PostgreSQLFilterCondition();
			((PostgreSQLFilterCondition)Filters[0]).FieldName="descricaoconta";
			((PostgreSQLFilterCondition)Filters[0]).Operator =PostgreSQLQueryCondition.STARTS_WITH;
			((PostgreSQLFilterCondition)Filters[0]).ValueName=new String("ATIVO REALIZAVEL A LONGO PRAZO");
			Filters[1]= new PostgreSQLFilterCondition();
			((PostgreSQLFilterCondition)Filters[1]).FieldName="balancete";					
			((PostgreSQLFilterCondition)Filters[1]).Operator =PostgreSQLQueryCondition.EQUALS;					
			((PostgreSQLFilterCondition)Filters[1]).ValueName=currentBalancete.getTimestamp();
			RealizavelLongoPrazo=Blackboard.getBlackboard().Query(ContaContabil.class,Filters);
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();			 
		}

		try{
			Filters=new QueryCondition[2];
			Filters[0]=new PostgreSQLFilterCondition();
			((PostgreSQLFilterCondition)Filters[0]).FieldName="descricaoconta";
			((PostgreSQLFilterCondition)Filters[0]).Operator =PostgreSQLQueryCondition.STARTS_WITH;
			((PostgreSQLFilterCondition)Filters[0]).ValueName=new String("PASSIVO CIRCULANTE");
			Filters[1]= new PostgreSQLFilterCondition();
			((PostgreSQLFilterCondition)Filters[1]).FieldName="balancete";					
			((PostgreSQLFilterCondition)Filters[1]).Operator =PostgreSQLQueryCondition.EQUALS;					
			((PostgreSQLFilterCondition)Filters[1]).ValueName=currentBalancete.getTimestamp();
			PassivoCirculante=Blackboard.getBlackboard().Query(ContaContabil.class,Filters);
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();			 
		}

		try{
			Filters=new QueryCondition[2];
			Filters[0]=new PostgreSQLFilterCondition();
			((PostgreSQLFilterCondition)Filters[0]).FieldName="descricaoconta";
			((PostgreSQLFilterCondition)Filters[0]).Operator =PostgreSQLQueryCondition.STARTS_WITH;
			((PostgreSQLFilterCondition)Filters[0]).ValueName=new String("PASSIVO EXIGIVEL A LONGO PRAZO");
			Filters[1]= new PostgreSQLFilterCondition();
			((PostgreSQLFilterCondition)Filters[1]).FieldName="balancete";					
			((PostgreSQLFilterCondition)Filters[1]).Operator =PostgreSQLQueryCondition.EQUALS;					
			((PostgreSQLFilterCondition)Filters[1]).ValueName=currentBalancete.getTimestamp();
			ExigivelLongoPrazo=Blackboard.getBlackboard().Query(ContaContabil.class,Filters);
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();			 
		}

		
		currentLiquidezGeral.setBalancete(currentBalancete);
		if(AtivoCirculante.length==0 || RealizavelLongoPrazo.length==0 || PassivoCirculante.length==0 || ExigivelLongoPrazo.length==0 ){
			currentLiquidezGeral.setValor(new Double(0));
		}else{
			double val,AC,RLP,PC,ELP;
			
			AC=((ContaContabil)AtivoCirculante[0]).getValor().doubleValue();
			RLP=((ContaContabil)RealizavelLongoPrazo[0]).getValor().doubleValue();
			PC=((ContaContabil)PassivoCirculante[0]).getValor().doubleValue();
			ELP=((ContaContabil)ExigivelLongoPrazo[0]).getValor().doubleValue();
			if((PC+ELP)==0) {
				currentLiquidezGeral.setValor(new Double(0));	
			}else{
				currentLiquidezGeral.setValor(new Double((AC+RLP)/(PC+ELP)));
			}
		}
		
		try{
			toStore=new AgentData[1];
			toStore[0]=currentLiquidezGeral;
			Blackboard.getBlackboard().Store(toStore);
		}catch(Exception e){
			System.out.println(e);
		}
	}
	protected void CalcularLiquidezCorrente(Balancete currentBalancete){
		AgentData                     AtivoCirculante[]=null;
		AgentData                     PassivoCirculante[]=null;
		AgentData                     toStore[]=null;
		LiquidezCorrente              currentLiquidezCorrente;
		QueryCondition[]              Filters;
				
				

		System.out.println("Gerando LiquidezCorrente para "+((Empresa)currentBalancete.getEmpresaEmitente()).getNomeEmpresa());
		currentLiquidezCorrente=new LiquidezCorrente(); 				
		try{
			Filters=new QueryCondition[2];
			Filters[0]=new PostgreSQLFilterCondition();
			((PostgreSQLFilterCondition)Filters[0]).FieldName="descricaoconta";
			((PostgreSQLFilterCondition)Filters[0]).Operator =PostgreSQLQueryCondition.STARTS_WITH;
			((PostgreSQLFilterCondition)Filters[0]).ValueName=new String("ATIVO CIRCULANTE");
			Filters[1]= new PostgreSQLFilterCondition();
			((PostgreSQLFilterCondition)Filters[1]).FieldName="balancete";					
			((PostgreSQLFilterCondition)Filters[1]).Operator =PostgreSQLQueryCondition.EQUALS;					
			((PostgreSQLFilterCondition)Filters[1]).ValueName=currentBalancete.getTimestamp();
			AtivoCirculante=Blackboard.getBlackboard().Query(ContaContabil.class,Filters);
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();			 
		}

		try{
			Filters=new QueryCondition[2];
			Filters[0]=new PostgreSQLFilterCondition();
			((PostgreSQLFilterCondition)Filters[0]).FieldName="descricaoconta";
			((PostgreSQLFilterCondition)Filters[0]).Operator =PostgreSQLQueryCondition.STARTS_WITH;
			((PostgreSQLFilterCondition)Filters[0]).ValueName=new String("PASSIVO CIRCULANTE");
			Filters[1]= new PostgreSQLFilterCondition();
			((PostgreSQLFilterCondition)Filters[1]).FieldName="balancete";					
			((PostgreSQLFilterCondition)Filters[1]).Operator =PostgreSQLQueryCondition.EQUALS;					
			((PostgreSQLFilterCondition)Filters[1]).ValueName=currentBalancete.getTimestamp();
			PassivoCirculante=Blackboard.getBlackboard().Query(ContaContabil.class,Filters);
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();			 
		}

		currentLiquidezCorrente.setBalancete(currentBalancete);
		if(AtivoCirculante.length==0 || PassivoCirculante.length==0){
			currentLiquidezCorrente.setValor(new Double(0));
		}else{
			double val,AC,PC;
			
			AC=((ContaContabil)AtivoCirculante[0]).getValor().doubleValue();
			PC=((ContaContabil)PassivoCirculante[0]).getValor().doubleValue();
			if((PC)==0) {
				currentLiquidezCorrente.setValor(new Double(0));	
			}else{
				currentLiquidezCorrente.setValor(new Double((AC)/(PC)));
			}
		}
		
		try{
			toStore=new AgentData[1];
			toStore[0]=currentLiquidezCorrente;
			Blackboard.getBlackboard().Store(toStore);
		}catch(Exception e){
			System.out.println(e);
		}		
	}
	protected void CalcularRetornoSobreVendas(Balancete currentBalancete){
		AgentData                     LucroLiquido[]=null;
		AgentData                     ReceitaLiquida[]=null;
		AgentData                     toStore[]=null;
		RetornoSobreVendas            currentRetornoSobreVendas;
		QueryCondition[]              Filters;
				
				

		System.out.println("Gerando RetornoSobreVendas para "+((Empresa)currentBalancete.getEmpresaEmitente()).getNomeEmpresa());
		currentRetornoSobreVendas=new RetornoSobreVendas(); 				
		try{
			Filters=new QueryCondition[2];
			Filters[0]=new PostgreSQLFilterCondition();
			((PostgreSQLFilterCondition)Filters[0]).FieldName="descricaoconta";
			((PostgreSQLFilterCondition)Filters[0]).Operator =PostgreSQLQueryCondition.STARTS_WITH;
			((PostgreSQLFilterCondition)Filters[0]).ValueName=new String("RESULTADO OPERACIONAL");
			Filters[1]= new PostgreSQLFilterCondition();
			((PostgreSQLFilterCondition)Filters[1]).FieldName="balancete";					
			((PostgreSQLFilterCondition)Filters[1]).Operator =PostgreSQLQueryCondition.EQUALS;					
			((PostgreSQLFilterCondition)Filters[1]).ValueName=currentBalancete.getTimestamp();
			LucroLiquido=Blackboard.getBlackboard().Query(ContaContabil.class,Filters);
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();			 
		}

		try{
			Filters=new QueryCondition[2];
			Filters[0]=new PostgreSQLFilterCondition();
			((PostgreSQLFilterCondition)Filters[0]).FieldName="descricaoconta";
			((PostgreSQLFilterCondition)Filters[0]).Operator =PostgreSQLQueryCondition.STARTS_WITH;
			((PostgreSQLFilterCondition)Filters[0]).ValueName=new String("RECEITA LIQUIDA");
			Filters[1]= new PostgreSQLFilterCondition();
			((PostgreSQLFilterCondition)Filters[1]).FieldName="balancete";					
			((PostgreSQLFilterCondition)Filters[1]).Operator =PostgreSQLQueryCondition.EQUALS;					
			((PostgreSQLFilterCondition)Filters[1]).ValueName=currentBalancete.getTimestamp();
			ReceitaLiquida=Blackboard.getBlackboard().Query(ContaContabil.class,Filters);
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();			 
		}

		currentRetornoSobreVendas.setBalancete(currentBalancete);
		if(LucroLiquido.length==0 || ReceitaLiquida.length==0){
			currentRetornoSobreVendas.setValor(new Double(0));
		}else{
			double val,LL,RL;
			
			LL=((ContaContabil)LucroLiquido[0]).getValor().doubleValue();
			RL=((ContaContabil)ReceitaLiquida[0]).getValor().doubleValue();
			if((RL)==0) {
				currentRetornoSobreVendas.setValor(new Double(0));	
			}else{
				currentRetornoSobreVendas.setValor(new Double((LL)/(RL)));
			}
		}
		
		try{
			toStore=new AgentData[1];
			toStore[0]=currentRetornoSobreVendas;
			Blackboard.getBlackboard().Store(toStore);
		}catch(Exception e){
			System.out.println(e);
		}		
	}
}