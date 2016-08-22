#include <windows.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "Markowitz.h"

Papeis myPapeis;

FILE *templog=NULL;

void Inicializar(){
	
	templog=fopen("C:\\DebugFunc.txt","a");
	fprintf(templog,"-----------\r\nInicializar...\r\n");
	fclose(templog);

	myPapeis.NumeroPapeis=0;
	myPapeis.myQuotes=(Quotes **)malloc(MAX_PAPEIS*sizeof(Quotes *));
	memset(myPapeis.myQuotes,0,MAX_PAPEIS*sizeof(Quotes *));
}

void AddNewPapel(int Papel){
	templog=fopen("C:\\DebugFunc.txt","a");
	fprintf(templog,"AddPapel(%d)...\r\n",Papel);
	fclose(templog);

	
	myPapeis.myQuotes[myPapeis.NumeroPapeis]=(Quotes *)malloc(sizeof(Quotes));
	myPapeis.myQuotes[myPapeis.NumeroPapeis]->Papel=Papel;
	myPapeis.myQuotes[myPapeis.NumeroPapeis]->NumeroPregoes=0;
	
	myPapeis.myQuotes[myPapeis.NumeroPapeis]->myPregoes=(Pregao **)malloc(sizeof(Pregao *)*MAX_PREGOES);
	memset(myPapeis.myQuotes[myPapeis.NumeroPapeis]->myPregoes,0,MAX_PREGOES*sizeof(Pregao *));
	myPapeis.NumeroPapeis++;
}

void AddNewQuote(int Papel,int NewPregao,double Quote){
	int      f;
    Quotes  *currentQuote;
    Pregao  *currentPregao;

	templog=fopen("C:\\DebugFunc.txt","a");
	fprintf(templog,"AddQuote(Papel:%d,NewPregao:%d,Quote:%.02f)...\r\n",Papel,NewPregao,Quote);
	fclose(templog);

	for(f=0;f<myPapeis.NumeroPapeis;f++){
		if(myPapeis.myQuotes[f]->Papel==Papel){
			currentQuote=myPapeis.myQuotes[f];
			break;
		}
	}
	if(f==myPapeis.NumeroPapeis){
		MessageBox(NULL,"Erro no AddNewQuote","Erro no AddNewQuote",48);
		return;
	}
	currentQuote->myPregoes[currentQuote->NumeroPregoes]=(Pregao *)malloc(sizeof(Pregao));
	currentQuote->myPregoes[currentQuote->NumeroPregoes]->PregaoAtual=NewPregao;
	currentQuote->myPregoes[currentQuote->NumeroPregoes]->Cotacao=Quote;
	currentQuote->NumeroPregoes++;
}

void AddNewEstimativaRetorno(int Papel,int NewPregao,double Quote){
	int      f,g;
    Quotes  *currentQuote;
    Pregao  *currentPregao;

	templog=fopen("C:\\DebugFunc.txt","a");
	fprintf(templog,"AddNewEstimativaRetorno(Papel:%d,NewPregao:%d,Quote:%.02f)...\r\n",Papel,NewPregao,Quote);
	fclose(templog);

	for(f=0;f<myPapeis.NumeroPapeis;f++){
		if(myPapeis.myQuotes[f]->Papel==Papel){
			currentQuote=myPapeis.myQuotes[f];
			break;
		}
	}
	if(f==myPapeis.NumeroPapeis){
		MessageBox(NULL,"Erro no AddNewQuote","Erro no AddNewQuote",48);
		return;
	}
	for(f=0;f<currentQuote->NumeroPregoes;f++){
		if(NewPregao==currentQuote->myPregoes[f]->PregaoAtual){
			g=f;
			break;
		}
	}
	if(f==currentQuote->NumeroPregoes){
		MessageBox(NULL,"Erro no AddNewEstimativa","Erro no AddNewEstimativa",48);
		return;
	}
	currentQuote->myPregoes[g]->EstimativaRetorno=Quote;
}

