#include "setup.h"

typedef struct  {
    int    PregaoAtual;
	double Cotacao;
	double EstimativaRetorno;
}Pregao;

typedef struct  {
    int       Papel;
	int       NumeroPregoes;
	Pregao  **myPregoes;

}Quotes;

typedef struct  {
	int           NumeroPapeis;
    Quotes      **myQuotes;
}Papeis;

#define MAX_PAPEIS  100
#define MAX_PREGOES 500 

globle int AddPapel(void *theEnv);
globle int AddQuote(void *theEnv);
globle int AddEstimativaRetorno(void *theEnv);
globle int LogCLIPS(void *theEnv);

extern void Inicializar();
extern void AddNewPapel(int Papel);
extern void AddNewQuote(int Papel,int NewPregao,double Quote);	
extern void AddNewEstimativaRetorno(int Papel,int NewPregao,double Quote);	