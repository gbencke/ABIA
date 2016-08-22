package org.abia.utils.BackpropagationNeuralNetwork.Validation;

/** Essa classe implementa uma tabela customizada para permitir que possamos enxergar as amostras carregadas dos arquivos */

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

class TableAmostraModelo extends AbstractTableModel {
	/** Numero de Colunas de nossa Tabela */
	int NColunas;
	/** Numero de Linhas de nossa Tabela */
	int NLinhas;
	/** Matriz interna que determina os valores de cada uma de nossas celulas */
	public Double Valor[][];
	
	/** Construtor padrao que recebe o numero de colunas por Linhas */
	
	public TableAmostraModelo(int Colunas, int Linhas){
		int f,g;
		Valor=new Double[Colunas][Linhas];
		for(f=0;f<Colunas;f++){
			for(g=0;g<Linhas;g++){
				Valor[f][g]=new Double(0);
			}
		}
		NColunas=Colunas;
		NLinhas=Linhas;
	}
	
	/** Obtem o numero de Colunas */

	public int getColumnCount(){
                //System.out.println("GetColumnCount");
		return NColunas;
	}

	/** Obtem o numero de linhas */
	
	public int getRowCount(){
		return NLinhas;
	}
	
	/** Permite receber o nome de uma coluna */

        public String getColumnName(int column){
               //System.out.println(column);
 		return new String((new Integer(column)).toString());	 
	}
	
	/** Obtem um valor de uma celula em uma coluna e linha */ 
	
	public Object getValueAt(int row,int col){
		return Valor[col][row];
	}
}


class TableAmostraCellRenderer extends DefaultTableCellRenderer {
      TableAmostraModelo Dados;
  
      TableAmostraCellRenderer(TableAmostraModelo DadosIn){
	      Dados=DadosIn;
      }
      
      public void setValue(Object value){
              if(value==null){
		      System.out.println("Nulo");

	      }
	      if(value instanceof Double){
		      Double ValorOk;
		      ValorOk=(Double)value;
		      setBackground(new Color(((int)(255*ValorOk.doubleValue())),
					      ((int)(255*ValorOk.doubleValue())),
					      ((int)(255*ValorOk.doubleValue()))));

		      setForeground(new Color(((int)(255*ValorOk.doubleValue())),
					      ((int)(255*ValorOk.doubleValue())),
					      ((int)(255*ValorOk.doubleValue()))));
	      }
      }
      
}


/** Essa classe implementa uma tabela customizada para permitir que possamos enxergar as amostras carregadas dos arquivos */
public class TableAmostra extends JTable {
  	public TableAmostraModelo       Modelo;
	public TableAmostraCellRenderer Renderer;
	

	/** Construtor padrao */
	TableAmostra(int Colunas,int Linhas){
             super();
	     int f;
	     
	     Modelo=new TableAmostraModelo(Colunas,Linhas);
	     Renderer=new TableAmostraCellRenderer(Modelo);
	     //System.out.println("Ok"); 
	     setModel(Modelo);
	     setShowHorizontalLines(false);
	     setShowVerticalLines(false);
	     setShowGrid(false);
	     setIntercellSpacing(new Dimension(0,0));

	     for(f=0;f<Colunas;f++){
	         getColumn(new String((new Integer(f)).toString())).setCellRenderer(Renderer);
	         getColumn(new String((new Integer(f)).toString())).setPreferredWidth(2);
	         getColumn(new String((new Integer(f)).toString())).setMinWidth(8);
	         getColumn(new String((new Integer(f)).toString())).setMaxWidth(8);
			 }
	}
	
	/** Retorna uma matriz dos valores da amostra */

	public double[] getMatrix(){
		double Retorno[];
		int f,g;

		Retorno=new double[FramePrincipal.ColunaAmostra*FramePrincipal.LinhaAmostra];
		//System.out.println(FramePrincipal.ColunaAmostra*FramePrincipal.LinhaAmostra);
		for(g=0;g<FramePrincipal.LinhaAmostra;g++){
		    for(f=0;f<FramePrincipal.ColunaAmostra;f++){
			//System.out.println(g+" "+f);
			Retorno[g*FramePrincipal.ColunaAmostra+f]=Modelo.Valor[f][g].doubleValue()*0.1;
			//System.out.println(g*FramePrincipal.ColunaAmostra+f);
		    }
		}
		return Retorno;
	}

	
}
