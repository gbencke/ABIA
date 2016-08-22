   /*******************************************************/
   /*      "C" Language Integrated Production System      */
   /*                                                     */
   /*             CLIPS Version 6.20  01/31/02            */
   /*                                                     */
   /*              DEFFUNCTION HEADER FILE                */
   /*******************************************************/

/*************************************************************/
/* Purpose:                                                  */
/*                                                           */
/* Principal Programmer(s):                                  */
/*      Brian L. Donnell                                     */
/*      Gary D. Riley                                        */
/*                                                           */
/* Contributing Programmer(s):                               */
/*                                                           */
/* Revision History:                                         */
/*                                                           */
/*************************************************************/

#ifndef _H_dffnxfun
#define _H_dffnxfun

#define EnvGetDeffunctionName(theEnv,x) GetConstructNameString((struct constructHeader *) x)
#define EnvGetDeffunctionPPForm(theEnv,x) GetConstructPPForm(theEnv,(struct constructHeader *) x)

#define GetDeffunctionNamePointer(x) GetConstructNamePointer((struct constructHeader *) x)
#define SetDeffunctionPPForm(d,ppf) SetConstructPPForm(theEnv,(struct constructHeader *) d,ppf)

#define EnvDeffunctionModule(theEnv,x) GetConstructModuleName((struct constructHeader *) x)

typedef struct deffunctionStruct DEFFUNCTION;
typedef struct deffunctionModule DEFFUNCTION_MODULE;

#ifndef _H_conscomp
#include "conscomp.h"
#endif
#ifndef _H_cstrccom
#include "cstrccom.h"
#endif
#ifndef _H_moduldef
#include "moduldef.h"
#endif
#ifndef _H_evaluatn
#include "evaluatn.h"
#endif
#ifndef _H_expressn
#include "expressn.h"
#endif
#ifndef _H_symbol
#include "symbol.h"
#endif

#ifdef LOCALE
#undef LOCALE
#endif
#ifdef _DFFNXFUN_SOURCE_
#define LOCALE
#else
#define LOCALE extern
#endif

struct deffunctionModule
  {
   struct defmoduleItemHeader header;
  };

struct deffunctionStruct
  {
   struct constructHeader header;
   unsigned busy,
            executing;
   unsigned short trace;
   EXPRESSION *code;
   int minNumberOfParameters,
       maxNumberOfParameters,
       numberOfLocalVars;
  };
  
#define DEFFUNCTION_DATA 23

struct deffunctionData
  { 
   struct construct *DeffunctionConstruct;
   int DeffunctionModuleIndex;
   ENTITY_RECORD DeffunctionEntityRecord;
#if DEBUGGING_FUNCTIONS
   unsigned WatchDeffunctions;
#endif
   struct CodeGeneratorItem *DeffunctionCodeItem;
   DEFFUNCTION *ExecutingDeffunction;
#if (! BLOAD_ONLY) && (! RUN_TIME)
   struct token DFInputToken;
#endif
  };

#define DeffunctionData(theEnv) ((struct deffunctionData *) GetEnvironmentData(theEnv,DEFFUNCTION_DATA))

#if ENVIRONMENT_API_ONLY
#define DeffunctionModule(theEnv,x) GetConstructModuleName((struct constructHeader *) x)
#define FindDeffunction(theEnv,a) EnvFindDeffunction(theEnv,a)
#define GetDeffunctionList(theEnv,a,b) EnvGetDeffunctionList(theEnv,a,b)
#define GetDeffunctionName(theEnv,x) GetConstructNameString((struct constructHeader *) x)
#define GetDeffunctionPPForm(theEnv,x) GetConstructPPForm(theEnv,(struct constructHeader *) x)
#define GetDeffunctionWatch(theEnv,a) EnvGetDeffunctionWatch(theEnv,a)
#define GetNextDeffunction(theEnv,a) EnvGetNextDeffunction(theEnv,a)
#define IsDeffunctionDeletable(theEnv,a) EnvIsDeffunctionDeletable(theEnv,a)
#define ListDeffunctions(theEnv,a,b) EnvListDeffunctions(theEnv,a,b)
#define SetDeffunctionWatch(theEnv,a,b) EnvSetDeffunctionWatch(theEnv,a,b)
#define Undeffunction(theEnv,a) EnvUndeffunction(theEnv,a)
#else
#define DeffunctionModule(x) GetConstructModuleName((struct constructHeader *) x)
#define FindDeffunction(a) EnvFindDeffunction(GetCurrentEnvironment(),a)
#define GetDeffunctionList(a,b) EnvGetDeffunctionList(GetCurrentEnvironment(),a,b)
#define GetDeffunctionName(x) GetConstructNameString((struct constructHeader *) x)
#define GetDeffunctionPPForm(x) GetConstructPPForm(GetCurrentEnvironment(),(struct constructHeader *) x)
#define GetDeffunctionWatch(a) EnvGetDeffunctionWatch(GetCurrentEnvironment(),a)
#define GetNextDeffunction(a) EnvGetNextDeffunction(GetCurrentEnvironment(),a)
#define IsDeffunctionDeletable(a) EnvIsDeffunctionDeletable(GetCurrentEnvironment(),a)
#define ListDeffunctions(a,b) EnvListDeffunctions(GetCurrentEnvironment(),a,b)
#define SetDeffunctionWatch(a,b) EnvSetDeffunctionWatch(GetCurrentEnvironment(),a,b)
#define Undeffunction(a) EnvUndeffunction(GetCurrentEnvironment(),a)
#endif

LOCALE void SetupDeffunctions(void *);
LOCALE void *EnvFindDeffunction(void *,char *);
LOCALE DEFFUNCTION *LookupDeffunctionByMdlOrScope(void *,char *);
LOCALE DEFFUNCTION *LookupDeffunctionInScope(void *,char *);
LOCALE BOOLEAN EnvUndeffunction(void *,void *);
LOCALE void *EnvGetNextDeffunction(void *,void *);
LOCALE int EnvIsDeffunctionDeletable(void *,void *);
LOCALE void UndeffunctionCommand(void *);
LOCALE SYMBOL_HN *GetDeffunctionModuleCommand(void *);
LOCALE void DeffunctionGetBind(DATA_OBJECT *);
LOCALE void DFRtnUnknown(DATA_OBJECT *);
LOCALE void DFWildargs(DATA_OBJECT *);
LOCALE int CheckDeffunctionCall(void *,void *,int);
#if DEBUGGING_FUNCTIONS
LOCALE void PPDeffunctionCommand(void *);
LOCALE void ListDeffunctionsCommand(void *);
LOCALE void EnvListDeffunctions(void *,char *,struct defmodule *);
LOCALE void EnvSetDeffunctionWatch(void *,unsigned,void *);
LOCALE unsigned EnvGetDeffunctionWatch(void *,void *);
#endif
#if (! BLOAD_ONLY) && (! RUN_TIME)
LOCALE void RemoveDeffunction(void *,void *);
#endif

LOCALE void GetDeffunctionListFunction(void *,DATA_OBJECT *);
globle void EnvGetDeffunctionList(void *,DATA_OBJECT *,struct defmodule *);

#endif






