#include <windows.h>
#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include "CLIPServer.h"

struct WorkerThreadParam {
	 SOCKET *socket;
};

struct DataToExecute {
	char *StringToExecute;
	void *next;

	DataToExecute(){
		StringToExecute=NULL;
		next=NULL;
	}

	DataToExecute(char *Mess){
		StringToExecute=new char[strlen(Mess)+10];
		strcpy(StringToExecute,Mess);
		next=NULL;
	}

};

struct Analisys {
	char *StringToExecute;
	void *next;

	Analisys(){
		StringToExecute=NULL;
		next=NULL;
	}

	Analisys(char *Mess){
		StringToExecute=new char[strlen(Mess)+10];
		strcpy(StringToExecute,Mess);
		next=NULL;
	}

};



int               CurrentPort;
SOCKET			 *ListenerGeral;
HANDLE            hMutex;
DataToExecute    *CurrentData=NULL,*LastData=NULL;
Analisys         *CurrentAnalisys=NULL,*LastAnalisys=NULL;

extern "C" void addDataToExecute(DataToExecute *newData);
extern "C" Analisys *getAnalisysMade();

unsigned long __stdcall WorkerThread(void *t){
	WorkerThreadParam *Parametro;
	int                BytesLidos,Tamanho=10000;
	char               Buffer[100000];
	char               BufferTotal[100000];


	Parametro=(WorkerThreadParam *)t;
	memset(Buffer,0,100000);
	memset(BufferTotal,0,100000);
	while(1){
		BytesLidos = recv(*Parametro->socket,Buffer,Tamanho,0);
		strcat(BufferTotal,Buffer);		
		if(strstr(BufferTotal,"</Message>")){
			if(strstr(BufferTotal,"<Message Type=Execute>")){
				char            BufferResponse[10000],*BufferToExecute;
				DataToExecute  *TempData;

				strcpy(BufferResponse,"<Message Type=ExecuteResponse>\r\n</Message>\r\n");							
				BytesLidos = send(*Parametro->socket,BufferResponse,strlen(BufferResponse),0);
				BufferToExecute=BufferTotal+strlen("<Message Type=Execute>");
				*strstr(BufferToExecute,"</Message>")=0;
				TempData=new DataToExecute(BufferToExecute);
				addDataToExecute(TempData);
			}
			if(strstr(BufferTotal,"<Message Type=\"GetAnalisys\">")){
				char            BufferResponse[10000];
				Analisys        *newAnalisys;

				newAnalisys=getAnalisysMade();
				if(newAnalisys==NULL){
				   strcpy(BufferResponse,"<Message Type=\"GetAnalisysResponse\">\r\n</Message>\r\n");
				   BytesLidos = send(*Parametro->socket,BufferResponse,strlen(BufferResponse),0);
				}else{
				   BytesLidos = send(*Parametro->socket,newAnalisys->StringToExecute,strlen(newAnalisys->StringToExecute),0);
				   if(BytesLidos<strlen(newAnalisys->StringToExecute)){
					   MessageBox(NULL,"Erro em Send...","Erro",48);
				   }
				}
			}
	        memset(Buffer,0,100000);
	        memset(BufferTotal,0,100000);
		}
		Sleep(0);
	}
	return 0;
} 

unsigned long __stdcall ListenerThread(void *t){
	struct		sockaddr_in local, from;
	WSADATA		wsaData;
	SOCKET	    listener, *msgsock;	
	int		    fromlen;
	long        retorno;
    
	struct WorkerThreadParam *Parametro;
    
	HANDLE      hThread;
	int		    timeout;	
	int		    err;
	char        DebugStr[1000];

	
	timeout=30;

	if (WSAStartup(0x202,&wsaData) == SOCKET_ERROR) {
		MessageBox(NULL,"Erro na Thread Criadora","Nao consegui inicializar Winsock",48);
		WSACleanup();
		exit(0);
	}
	
	local.sin_family = AF_INET;
	local.sin_addr.s_addr = INADDR_ANY;
	local.sin_port = htons(CurrentPort);
	listener = socket(AF_INET, SOCK_STREAM,0); // TCP socket	
	
	if(listener == INVALID_SOCKET){
	   MessageBox(NULL,"Erro na Thread Criadora","Nao consegui criar listener",48);
	   WSACleanup();
	   exit(0);
	}
	if (bind(listener,(struct sockaddr*)&local,sizeof(local))==SOCKET_ERROR){
		MessageBox(NULL,"Erro na Thread Criadora","Nao consegui dar bind do socket com o IP:Porta necessario",48);
		WSACleanup();
		exit(0);
	}
	sprintf(DebugStr,"Listening in %d",CurrentPort);
	OutputDebugString(DebugStr);

	if (listen(listener,5) == SOCKET_ERROR) {
		MessageBox(NULL,"Erro na Thread Criadora","Nao consegui escutar no socket",48);
		WSACleanup();
		exit(0);
	}
	
	ListenerGeral=&listener;
	err = setsockopt(listener,SOL_SOCKET,SO_RCVTIMEO,(char *)&timeout,sizeof(timeout));
    while(1){          
		fromlen=sizeof(from);
		msgsock=(SOCKET *)malloc(sizeof(SOCKET));
		*msgsock = accept(listener,(struct sockaddr*)&from, &fromlen);
		err = setsockopt(*msgsock,SOL_SOCKET,SO_RCVTIMEO,(char *)&timeout,sizeof(timeout));
		if (*msgsock == INVALID_SOCKET) {
			MessageBox(NULL,"Erro na Thread Criadora","Houce erro na operacao de accept",48);
			WSACleanup();
			exit(0);
		}
		
		Parametro=(WorkerThreadParam *)malloc(sizeof(struct WorkerThreadParam));
		Parametro->socket=msgsock;

		hThread=CreateThread(NULL,0,&WorkerThread,(void *)Parametro,0,(unsigned long *)&retorno);
		if(!hThread){
			MessageBox(NULL,"Erro na Thread Criadora","Nao consegui criar Thread Filha",48);
			WSACleanup();
			exit(0);				
		}
		Sleep(0);
	}
	return 0;
}

extern "C" DataToExecute *getDataToExecute(){
	DataToExecute *toReturn;
	WaitForSingleObject(hMutex,INFINITE);
		if(CurrentData==NULL){
			toReturn=NULL;
		}else{
			toReturn=CurrentData;
			CurrentData=(DataToExecute *)CurrentData->next;
		}
	ReleaseMutex(hMutex);
	return toReturn;
}

extern "C" void addDataToExecute(DataToExecute *newData){
	WaitForSingleObject(hMutex,INFINITE);
		if(CurrentData==NULL){
			CurrentData=newData;
			LastData=CurrentData;
		}else{
			LastData->next=newData;
			LastData=newData;
		}
	ReleaseMutex(hMutex);
}

extern "C" Analisys *getAnalisysMade(){
	Analisys *toReturn;
	WaitForSingleObject(hMutex,INFINITE);
		if(CurrentAnalisys==NULL){
			toReturn=NULL;
		}else{
			toReturn=CurrentAnalisys;
			CurrentAnalisys=(Analisys *)CurrentAnalisys->next;
		}
	ReleaseMutex(hMutex);
	return toReturn;
}

extern "C" void addAnalisysMade(char *Mess){
	Analisys *newData;
	
	newData=new Analisys(Mess);
	WaitForSingleObject(hMutex,INFINITE);
		if(CurrentAnalisys==NULL){
			CurrentAnalisys=newData;
			LastAnalisys=CurrentAnalisys;
		}else{
			LastAnalisys->next=newData;
			LastAnalisys=newData;
		}
	ReleaseMutex(hMutex);
}

BOOL HeapSetInformation(HANDLE,int,PVOID,SIZE_T);

extern "C" void Inicializar();

extern "C" void StartListenerThread(int Port){
	unsigned long   retorno;
	HANDLE          hThread;
	char            NameMutex[1000];
    int ret;
    ULONG teste=2;
   

    //ret=HeapSetInformation(GetProcessHeap(),2,&teste,sizeof(ULONG));



	sprintf(NameMutex,"CLIPSERVER%d",GetCurrentProcessId());
	
	CurrentPort=Port;
	hMutex=CreateMutex(NULL,FALSE,NameMutex);
	hThread=CreateThread(NULL,0,&ListenerThread,NULL,0,&retorno);
}

