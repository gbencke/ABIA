   /*******************************************************/
   /*      "C" Language Integrated Production System      */
   /*                                                     */
   /*             CLIPS Version 6.20  01/31/02            */
   /*                                                     */
   /*            MISCELLANEOUS FUNCTIONS MODULE           */
   /*******************************************************/

/*************************************************************/
/* Purpose:                                                  */
/*                                                           */
/* Principal Programmer(s):                                  */
/*      Gary D. Riley                                        */
/*                                                           */
/* Contributing Programmer(s):                               */
/*      Brian L. Donnell                                     */
/*                                                           */
/* Revision History:                                         */
/*                                                           */
/*************************************************************/

#define _MISCFUN_SOURCE_

#include <stdio.h>
#define _STDIO_INCLUDED_
#include <string.h>

#include "setup.h"

#include "argacces.h"
#include "envrnmnt.h"
#include "exprnpsr.h"
#include "memalloc.h"
#include "multifld.h"
#include "router.h"
#include "sysdep.h"
#include "utility.h"

#if DEFFUNCTION_CONSTRUCT
#include "dffnxfun.h"
#endif

#include "miscfun.h"

#define MISCFUN_DATA 9

struct miscFunctionData
  { 
   long int GensymNumber;
  };

#define MiscFunctionData(theEnv) ((struct miscFunctionData *) GetEnvironmentData(theEnv,MISCFUN_DATA))

/***************************************/
/* LOCAL INTERNAL FUNCTION DEFINITIONS */
/***************************************/

   static void                    ExpandFuncMultifield(void *,DATA_OBJECT *,EXPRESSION *,
                                                       EXPRESSION **,void *);

/*****************************************************************/
/* MiscFunctionDefinitions: Initializes miscellaneous functions. */
/*****************************************************************/
globle void MiscFunctionDefinitions(
  void *theEnv)
  {
   AllocateEnvironmentData(theEnv,MISCFUN_DATA,sizeof(struct miscFunctionData),NULL);
   MiscFunctionData(theEnv)->GensymNumber = 1;
   
#if ! RUN_TIME
   EnvDefineFunction2(theEnv,"gensym",           'w', PTIEF GensymFunction,      "GensymFunction", "00");
   EnvDefineFunction2(theEnv,"gensym*",          'w', PTIEF GensymStarFunction,  "GensymStarFunction", "00");
   EnvDefineFunction2(theEnv,"setgen",           'l', PTIEF SetgenFunction,      "SetgenFunction", "11i");
   EnvDefineFunction2(theEnv,"system",           'v', PTIEF gensystem,           "gensystem", "1*k");
   EnvDefineFunction2(theEnv,"length",           'l', PTIEF LengthFunction,      "LengthFunction", "11q");
   EnvDefineFunction2(theEnv,"length$",          'l', PTIEF LengthFunction,      "LengthFunction", "11q");
   EnvDefineFunction2(theEnv,"time",             'd', PTIEF TimeFunction,        "TimeFunction", "00");
   EnvDefineFunction2(theEnv,"random",           'l', PTIEF RandomFunction,      "RandomFunction", "02i");
   EnvDefineFunction2(theEnv,"seed",             'v', PTIEF SeedFunction,        "SeedFunction", "11i");
   EnvDefineFunction2(theEnv,"conserve-mem",     'v', PTIEF ConserveMemCommand,  "ConserveMemCommand", "11w");
   EnvDefineFunction2(theEnv,"release-mem",      'l', PTIEF ReleaseMemCommand,   "ReleaseMemCommand", "00");
#if DEBUGGING_FUNCTIONS
   EnvDefineFunction2(theEnv,"mem-used",         'l', PTIEF MemUsedCommand,      "MemUsedCommand", "00");
   EnvDefineFunction2(theEnv,"mem-requests",     'l', PTIEF MemRequestsCommand,  "MemRequestsCommand", "00");
#endif
   EnvDefineFunction2(theEnv,"options",          'v', PTIEF OptionsCommand,      "OptionsCommand", "00");
   EnvDefineFunction2(theEnv,"(expansion-call)", 'u', PTIEF ExpandFuncCall,      "ExpandFuncCall",NULL);
   EnvDefineFunction2(theEnv,"expand$",'u', PTIEF DummyExpandFuncMultifield,
                                           "DummyExpandFuncMultifield","11m");
   FuncSeqOvlFlags(theEnv,"expand$",FALSE,FALSE);
   EnvDefineFunction2(theEnv,"(set-evaluation-error)",
                                       'w', PTIEF CauseEvaluationError,"CauseEvaluationError",NULL);
   EnvDefineFunction2(theEnv,"set-sequence-operator-recognition",
                                       'b', PTIEF SetSORCommand,"SetSORCommand","11w");
   EnvDefineFunction2(theEnv,"get-sequence-operator-recognition",'b',
                    PTIEF EnvGetSequenceOperatorRecognition,"EnvGetSequenceOperatorRecognition","00");
   EnvDefineFunction2(theEnv,"get-function-restrictions",'s',
                   PTIEF GetFunctionRestrictions,"GetFunctionRestrictions","11w");
   EnvDefineFunction2(theEnv,"create$",     'm', PTIEF CreateFunction,  "CreateFunction", NULL);
   EnvDefineFunction2(theEnv,"mv-append",   'm', PTIEF CreateFunction,  "CreateFunction", NULL);
   EnvDefineFunction2(theEnv,"apropos",   'v', PTIEF AproposCommand,  "AproposCommand", "11w");
   EnvDefineFunction2(theEnv,"get-function-list",   'm', PTIEF GetFunctionListFunction,  "GetFunctionListFunction", "00");
   EnvDefineFunction2(theEnv,"funcall",'u', PTIEF FuncallFunction,"FuncallFunction","1**k");
   EnvDefineFunction2(theEnv,"timer",'d', PTIEF TimerFunction,"TimerFunction","**");
#endif
  }

/******************************************************************/
/* CreateFunction: H/L access routine for the create$ function.   */
/******************************************************************/
globle void CreateFunction(
  void *theEnv,
  DATA_OBJECT_PTR returnValue)
  {
   StoreInMultifield(theEnv,returnValue,GetFirstArgument(),TRUE);
  }

/*****************************************************************/
/* SetgenFunction: H/L access routine for the setgen function.   */
/*****************************************************************/
globle long int SetgenFunction(
  void *theEnv)
  {
   long theLong;
   DATA_OBJECT theValue;

   /*==========================================================*/
   /* Check to see that a single integer argument is provided. */
   /*==========================================================*/

   if (EnvArgCountCheck(theEnv,"setgen",EXACTLY,1) == -1) return(MiscFunctionData(theEnv)->GensymNumber);
   if (EnvArgTypeCheck(theEnv,"setgen",1,INTEGER,&theValue) == FALSE) return(MiscFunctionData(theEnv)->GensymNumber);

   /*========================================*/
   /* The integer must be greater than zero. */
   /*========================================*/

   theLong = ValueToLong(theValue.value);

   if (theLong < 1L)
     {
      ExpectedTypeError1(theEnv,"setgen",1,"number (greater than or equal to 1)");
      return(MiscFunctionData(theEnv)->GensymNumber);
     }

   /*====================================*/
   /* Set the gensym index to the number */
   /* provided and return this value.    */
   /*====================================*/

   MiscFunctionData(theEnv)->GensymNumber = theLong;
   return(theLong);
  }

/****************************************/
/* GensymFunction: H/L access routine   */
/*   for the gensym function.           */
/****************************************/
globle void *GensymFunction(
  void *theEnv)
  {
   char genstring[15];
   
   /*===========================================*/
   /* The gensym function accepts no arguments. */
   /*===========================================*/

   EnvArgCountCheck(theEnv,"gensym",EXACTLY,0);

   /*================================================*/
   /* Create a symbol using the current gensym index */
   /* as the postfix.                                */
   /*================================================*/

   sprintf(genstring,"gen%ld",MiscFunctionData(theEnv)->GensymNumber);
   MiscFunctionData(theEnv)->GensymNumber++;

   /*====================*/
   /* Return the symbol. */
   /*====================*/

   return(EnvAddSymbol(theEnv,genstring));
  }

/************************************************/
/* GensymStarFunction: H/L access routine for   */
/*   the gensym* function.                      */
/************************************************/
globle void *GensymStarFunction(
  void *theEnv)
  {
   /*============================================*/
   /* The gensym* function accepts no arguments. */
   /*============================================*/

   EnvArgCountCheck(theEnv,"gensym*",EXACTLY,0);

   /*====================*/
   /* Return the symbol. */
   /*====================*/

   return(GensymStar(theEnv));
  }

/************************************/
/* GensymStar: C access routine for */
/*   the gensym* function.          */
/************************************/
globle void *GensymStar(
  void *theEnv)
  {
   char genstring[15];
   
   /*=======================================================*/
   /* Create a symbol using the current gensym index as the */
   /* postfix. If the symbol is already present in the      */
   /* symbol table, then continue generating symbols until  */
   /* a unique symbol is found.                             */
   /*=======================================================*/

   do
     {
      sprintf(genstring,"gen%ld",MiscFunctionData(theEnv)->GensymNumber);
      MiscFunctionData(theEnv)->GensymNumber++;
     }
   while (FindSymbol(theEnv,genstring) != NULL);

   /*====================*/
   /* Return the symbol. */
   /*====================*/

   return(EnvAddSymbol(theEnv,genstring));
  }

/********************************************/
/* RandomFunction: H/L access routine for   */
/*   the random function.                   */
/********************************************/
globle long RandomFunction(
  void *theEnv)
  {
   int argCount;
   long rv;
   DATA_OBJECT theValue;
   long begin, end;

   /*====================================*/
   /* The random function accepts either */
   /* zero or two arguments.             */
   /*====================================*/

   argCount = EnvRtnArgCount(theEnv);
   
   if ((argCount != 0) && (argCount != 2))
     {
      PrintErrorID(theEnv,"MISCFUN",2,FALSE);
      EnvPrintRouter(theEnv,WERROR,"Function random expected either 0 or 2 arguments\n"); 
     }

   /*========================================*/
   /* Return the randomly generated integer. */
   /*========================================*/

   rv = genrand();
   
   if (argCount == 2)
     {
      if (EnvArgTypeCheck(theEnv,"random",1,INTEGER,&theValue) == FALSE) return(rv);
      begin = DOToLong(theValue);
      if (EnvArgTypeCheck(theEnv,"random",2,INTEGER,&theValue) == FALSE) return(rv);
      end = DOToLong(theValue);
      if (end < begin)
        {
         PrintErrorID(theEnv,"MISCFUN",3,FALSE);
         EnvPrintRouter(theEnv,WERROR,"Function random expected argument #1 to be less than argument #2\n"); 
         return(rv);
        }
        
      rv = begin + (rv % ((end - begin) + 1));
     }
   
   
   return(rv);
  }

/******************************************/
/* SeedFunction: H/L access routine for   */
/*   the seed function.                   */
/******************************************/
globle void SeedFunction(
  void *theEnv)
  {
   DATA_OBJECT theValue;

   /*==========================================================*/
   /* Check to see that a single integer argument is provided. */
   /*==========================================================*/

   if (EnvArgCountCheck(theEnv,"seed",EXACTLY,1) == -1) return;
   if (EnvArgTypeCheck(theEnv,"seed",1,INTEGER,&theValue) == FALSE) return;

   /*=============================================================*/
   /* Seed the random number generator with the provided integer. */
   /*=============================================================*/

   genseed((int) DOToLong(theValue));
  }

/********************************************/
/* LengthFunction: H/L access routine for   */
/*   the length$ function.                  */
/********************************************/
globle long int LengthFunction(
  void *theEnv)
  {
   DATA_OBJECT item;

   /*====================================================*/
   /* The length$ function expects exactly one argument. */
   /*====================================================*/

   if (EnvArgCountCheck(theEnv,"length$",EXACTLY,1) == -1) return(-1L);
   EnvRtnUnknown(theEnv,1,&item);

   /*====================================================*/
   /* If the argument is a string or symbol, then return */
   /* the number of characters in the argument.          */
   /*====================================================*/

   if ((GetType(item) == STRING) || (GetType(item) == SYMBOL))
     {  return( (long) strlen(DOToString(item))); }

   /*====================================================*/
   /* If the argument is a multifield value, then return */
   /* the number of fields in the argument.              */
   /*====================================================*/

   if (GetType(item) == MULTIFIELD)
     { return ( (long) GetDOLength(item)); }

   /*=============================================*/
   /* If the argument wasn't a string, symbol, or */
   /* multifield value, then generate an error.   */
   /*=============================================*/

   SetEvaluationError(theEnv,TRUE);
   ExpectedTypeError2(theEnv,"length$",1);
   return(-1L);
  }

/*******************************************/
/* ReleaseMemCommand: H/L access routine   */
/*   for the release-mem function.         */
/*******************************************/
globle long ReleaseMemCommand(
  void *theEnv)
  {
   /*================================================*/
   /* The release-mem function accepts no arguments. */
   /*================================================*/

   if (EnvArgCountCheck(theEnv,"release-mem",EXACTLY,0) == -1) return(0);

   /*========================================*/
   /* Release memory to the operating system */
   /* and return the amount of memory freed. */
   /*========================================*/

   return(EnvReleaseMem(theEnv,-1L,FALSE));
  }

/******************************************/
/* ConserveMemCommand: H/L access routine */
/*   for the conserve-mem command.        */
/******************************************/
globle void ConserveMemCommand(
  void *theEnv)
  {
   char *argument;
   DATA_OBJECT theValue;

   /*===================================*/
   /* The conserve-mem function expects */
   /* a single symbol argument.         */
   /*===================================*/

   if (EnvArgCountCheck(theEnv,"conserve-mem",EXACTLY,1) == -1) return;
   if (EnvArgTypeCheck(theEnv,"conserve-mem",1,SYMBOL,&theValue) == FALSE) return;

   argument = DOToString(theValue);

   /*====================================================*/
   /* If the argument is the symbol "on", then store the */
   /* pretty print representation of a construct when it */
   /* is defined.                                        */
   /*====================================================*/

   if (strcmp(argument,"on") == 0)
     { EnvSetConserveMemory(theEnv,TRUE); }

   /*======================================================*/
   /* Otherwise, if the argument is the symbol "off", then */
   /* don't store the pretty print representation of a     */
   /* construct when it is defined.                        */
   /*======================================================*/

   else if (strcmp(argument,"off") == 0)
     { EnvSetConserveMemory(theEnv,FALSE); }

   /*=====================================================*/
   /* Otherwise, generate an error since the only allowed */
   /* arguments are "on" or "off."                        */
   /*=====================================================*/

   else
     {
      ExpectedTypeError1(theEnv,"conserve-mem",1,"symbol with value on or off");
      return;
     }

   return;
  }

#if DEBUGGING_FUNCTIONS

/****************************************/
/* MemUsedCommand: H/L access routine   */
/*   for the mem-used command.          */
/****************************************/
globle long int MemUsedCommand(
  void *theEnv)
  {
   /*=============================================*/
   /* The mem-used function accepts no arguments. */
   /*=============================================*/

   if (EnvArgCountCheck(theEnv,"mem-used",EXACTLY,0) == -1) return(0);

   /*============================================*/
   /* Return the amount of memory currently held */
   /* (both for current use and for later use).  */
   /*============================================*/

   return(EnvMemUsed(theEnv));
  }

/********************************************/
/* MemRequestsCommand: H/L access routine   */
/*   for the mem-requests command.          */
/********************************************/
globle long int MemRequestsCommand(
  void *theEnv)
  {
   /*=================================================*/
   /* The mem-requests function accepts no arguments. */
   /*=================================================*/

   if (EnvArgCountCheck(theEnv,"mem-requests",EXACTLY,0) == -1) return(0);

   /*==================================*/
   /* Return the number of outstanding */
   /* memory requests.                 */
   /*==================================*/

   return(EnvMemRequests(theEnv));
  }

#endif

/****************************************/
/* AproposCommand: H/L access routine   */
/*   for the apropos command.           */
/****************************************/
globle void AproposCommand(
  void *theEnv)
  {
   char *argument;
   DATA_OBJECT argPtr;
   struct symbolHashNode *hashPtr = NULL;
   size_t theLength;

   /*=======================================================*/
   /* The apropos command expects a single symbol argument. */
   /*=======================================================*/

   if (EnvArgCountCheck(theEnv,"apropos",EXACTLY,1) == -1) return;
   if (EnvArgTypeCheck(theEnv,"apropos",1,SYMBOL,&argPtr) == FALSE) return;

   /*=======================================*/
   /* Determine the length of the argument. */
   /*=======================================*/

   argument = DOToString(argPtr);
   theLength = strlen(argument);

   /*====================================================================*/
   /* Print each entry in the symbol table that contains the argument as */
   /* a substring. When using a non-ANSI compiler, only those strings    */
   /* that contain the substring starting at the beginning of the string */
   /* are printed.                                                       */
   /*====================================================================*/

   while ((hashPtr = GetNextSymbolMatch(theEnv,argument,theLength,hashPtr,TRUE,NULL)) != NULL)
     {
      EnvPrintRouter(theEnv,WDISPLAY,ValueToString(hashPtr));
      EnvPrintRouter(theEnv,WDISPLAY,"\n");
     }
  }

/****************************************/
/* OptionsCommand: H/L access routine   */
/*   for the options command.           */
/****************************************/
globle void OptionsCommand(
  void *theEnv)
  {
   /*===========================================*/
   /* The options command accepts no arguments. */
   /*===========================================*/

   if (EnvArgCountCheck(theEnv,"options",EXACTLY,0) == -1) return;

   /*=================================*/
   /* Print the state of the compiler */
   /* flags for this executable.      */
   /*=================================*/

   EnvPrintRouter(theEnv,WDISPLAY,"Machine type: ");

#if GENERIC
   EnvPrintRouter(theEnv,WDISPLAY,"Generic ");
#endif
#if VAX_VMS
   EnvPrintRouter(theEnv,WDISPLAY,"VAX VMS ");
#endif
#if UNIX_V
   EnvPrintRouter(theEnv,WDISPLAY,"UNIX System V or 4.2BSD ");
#endif
#if UNIX_7
   EnvPrintRouter(theEnv,WDISPLAY,"UNIX System III Version 7 or Sun Unix ");
#endif
#if MAC_SC6
   EnvPrintRouter(theEnv,WDISPLAY,"Apple Macintosh with Symantec C 6.0");
#endif
#if MAC_SC7
   EnvPrintRouter(theEnv,WDISPLAY,"Apple Macintosh with Symantec C 7.0");
#endif
#if MAC_SC8
   EnvPrintRouter(theEnv,WDISPLAY,"Apple Macintosh with Symantec C 8.0");
#endif
#if MAC_MPW
   EnvPrintRouter(theEnv,WDISPLAY,"Apple Macintosh with MPW C");
#endif
#if MAC_MCW
   EnvPrintRouter(theEnv,WDISPLAY,"Apple Macintosh with CodeWarrior");
#endif
#if IBM_MSC
   EnvPrintRouter(theEnv,WDISPLAY,"IBM PC with Microsoft C");
#endif
#if IBM_ZTC
   EnvPrintRouter(theEnv,WDISPLAY,"IBM PC with Zortech C");
#endif
#if IBM_SC
   EnvPrintRouter(theEnv,WDISPLAY,"IBM PC with Symantec C++");
#endif
#if IBM_ICB
   EnvPrintRouter(theEnv,WDISPLAY,"IBM PC with Intel C Code Builder");
#endif
#if IBM_TBC
   EnvPrintRouter(theEnv,WDISPLAY,"IBM PC with Turbo C");
#endif
#if IBM_MCW
   EnvPrintRouter(theEnv,WDISPLAY,"IBM PC with Metrowerks CodeWarrior");
#endif
EnvPrintRouter(theEnv,WDISPLAY,"\n");

EnvPrintRouter(theEnv,WDISPLAY,"Defrule construct is ");
#if DEFRULE_CONSTRUCT
  EnvPrintRouter(theEnv,WDISPLAY,"ON\n");
#else
  EnvPrintRouter(theEnv,WDISPLAY,"OFF\n");
#endif

#if DEFRULE_CONSTRUCT

EnvPrintRouter(theEnv,WDISPLAY,"  Conflict resolution strategies are ");
#if CONFLICT_RESOLUTION_STRATEGIES
  EnvPrintRouter(theEnv,WDISPLAY,"ON\n");
#else
  EnvPrintRouter(theEnv,WDISPLAY,"OFF\n");
#endif

EnvPrintRouter(theEnv,WDISPLAY,"  Dynamic salience is ");
#if DYNAMIC_SALIENCE
  EnvPrintRouter(theEnv,WDISPLAY,"ON\n");
#else
  EnvPrintRouter(theEnv,WDISPLAY,"OFF\n");
#endif

EnvPrintRouter(theEnv,WDISPLAY,"  Incremental reset is ");
#if INCREMENTAL_RESET
  EnvPrintRouter(theEnv,WDISPLAY,"ON\n");
#else
  EnvPrintRouter(theEnv,WDISPLAY,"OFF\n");
#endif

EnvPrintRouter(theEnv,WDISPLAY,"  Logical dependencies (truth maintenance) are ");
#if LOGICAL_DEPENDENCIES
  EnvPrintRouter(theEnv,WDISPLAY,"ON\n");
#else
  EnvPrintRouter(theEnv,WDISPLAY,"OFF\n");
#endif

#endif

EnvPrintRouter(theEnv,WDISPLAY,"Defmodule construct is ");
#if DEFMODULE_CONSTRUCT
  EnvPrintRouter(theEnv,WDISPLAY,"ON\n");
#else
  EnvPrintRouter(theEnv,WDISPLAY,"OFF\n");
#endif

EnvPrintRouter(theEnv,WDISPLAY,"Deftemplate construct is ");
#if DEFTEMPLATE_CONSTRUCT
  EnvPrintRouter(theEnv,WDISPLAY,"ON\n");
#else
  EnvPrintRouter(theEnv,WDISPLAY,"OFF\n");
#endif

#if DEFTEMPLATE_CONSTRUCT

EnvPrintRouter(theEnv,WDISPLAY,"  Deffacts construct is ");
#if DEFFACTS_CONSTRUCT
  EnvPrintRouter(theEnv,WDISPLAY,"ON\n");
#else
  EnvPrintRouter(theEnv,WDISPLAY,"OFF\n");
#endif

#endif

EnvPrintRouter(theEnv,WDISPLAY,"Defglobal construct is ");
#if DEFGLOBAL_CONSTRUCT
  EnvPrintRouter(theEnv,WDISPLAY,"ON\n");
#else
  EnvPrintRouter(theEnv,WDISPLAY,"OFF\n");
#endif

EnvPrintRouter(theEnv,WDISPLAY,"Deffunction construct is ");
#if DEFFUNCTION_CONSTRUCT
  EnvPrintRouter(theEnv,WDISPLAY,"ON\n");
#else
  EnvPrintRouter(theEnv,WDISPLAY,"OFF\n");
#endif

EnvPrintRouter(theEnv,WDISPLAY,"Defgeneric/Defmethod constructs are ");
#if DEFGENERIC_CONSTRUCT
  EnvPrintRouter(theEnv,WDISPLAY,"ON\n");
#else
  EnvPrintRouter(theEnv,WDISPLAY,"OFF\n");
#endif

#if DEFGENERIC_CONSTRUCT

EnvPrintRouter(theEnv,WDISPLAY,"  Imperative methods are ");
#if IMPERATIVE_METHODS
  EnvPrintRouter(theEnv,WDISPLAY,"ON\n");
#else
  EnvPrintRouter(theEnv,WDISPLAY,"OFF\n");
#endif

#endif

EnvPrintRouter(theEnv,WDISPLAY,"Object System is ");
#if OBJECT_SYSTEM
  EnvPrintRouter(theEnv,WDISPLAY,"ON\n");
#else
  EnvPrintRouter(theEnv,WDISPLAY,"OFF\n");
#endif

#if OBJECT_SYSTEM

EnvPrintRouter(theEnv,WDISPLAY,"  Definstances construct is ");
#if DEFINSTANCES_CONSTRUCT
  EnvPrintRouter(theEnv,WDISPLAY,"ON\n");
#else
  EnvPrintRouter(theEnv,WDISPLAY,"OFF\n");
#endif

EnvPrintRouter(theEnv,WDISPLAY,"  Imperative (around/shadowed) message-handlers are ");
#if IMPERATIVE_MESSAGE_HANDLERS
  EnvPrintRouter(theEnv,WDISPLAY,"ON\n");
#else
  EnvPrintRouter(theEnv,WDISPLAY,"OFF\n");
#endif

EnvPrintRouter(theEnv,WDISPLAY,"  Auxiliary (before/after) message-handlers are ");
#if AUXILIARY_MESSAGE_HANDLERS
  EnvPrintRouter(theEnv,WDISPLAY,"ON\n");
#else
  EnvPrintRouter(theEnv,WDISPLAY,"OFF\n");
#endif

EnvPrintRouter(theEnv,WDISPLAY,"  Instance-set queries are ");
#if INSTANCE_SET_QUERIES
  EnvPrintRouter(theEnv,WDISPLAY,"ON\n");
#else
  EnvPrintRouter(theEnv,WDISPLAY,"OFF\n");
#endif

EnvPrintRouter(theEnv,WDISPLAY,"  Direct pattern-matching on instances is ");
#if INSTANCE_PATTERN_MATCHING
  EnvPrintRouter(theEnv,WDISPLAY,"ON\n");
#else
  EnvPrintRouter(theEnv,WDISPLAY,"OFF\n");
#endif

EnvPrintRouter(theEnv,WDISPLAY,"  Binary loading of instances is ");
#if BLOAD_INSTANCES
  EnvPrintRouter(theEnv,WDISPLAY,"ON\n");
#else
  EnvPrintRouter(theEnv,WDISPLAY,"OFF\n");
#endif

EnvPrintRouter(theEnv,WDISPLAY,"  Binary saving of instances is ");
#if BSAVE_INSTANCES
  EnvPrintRouter(theEnv,WDISPLAY,"ON\n");
#else
  EnvPrintRouter(theEnv,WDISPLAY,"OFF\n");
#endif

#endif

EnvPrintRouter(theEnv,WDISPLAY,"Extended math package is ");
#if EX_MATH
  EnvPrintRouter(theEnv,WDISPLAY,"ON\n");
#else
  EnvPrintRouter(theEnv,WDISPLAY,"OFF\n");
#endif

EnvPrintRouter(theEnv,WDISPLAY,"Text processing package is ");
#if TEXTPRO_FUNCTIONS
  EnvPrintRouter(theEnv,WDISPLAY,"ON\n");
#else
  EnvPrintRouter(theEnv,WDISPLAY,"OFF\n");
#endif

EnvPrintRouter(theEnv,WDISPLAY,"Help system is ");
#if HELP_FUNCTIONS
  EnvPrintRouter(theEnv,WDISPLAY,"ON\n");
#else
  EnvPrintRouter(theEnv,WDISPLAY,"OFF\n");
#endif

EnvPrintRouter(theEnv,WDISPLAY,"Bload capability is ");
#if BLOAD_ONLY
  EnvPrintRouter(theEnv,WDISPLAY,"BLOAD ONLY");
#endif
#if BLOAD
  EnvPrintRouter(theEnv,WDISPLAY,"BLOAD");
#endif
#if BLOAD_AND_BSAVE
  EnvPrintRouter(theEnv,WDISPLAY,"BLOAD AND BSAVE");
#endif
#if (! BLOAD_ONLY) && (! BLOAD) && (! BLOAD_AND_BSAVE)
  EnvPrintRouter(theEnv,WDISPLAY,"OFF ");
#endif
EnvPrintRouter(theEnv,WDISPLAY,"\n");

EnvPrintRouter(theEnv,WDISPLAY,"EMACS Editor is ");
#if EMACS_EDITOR
  EnvPrintRouter(theEnv,WDISPLAY,"ON\n");
#else
  EnvPrintRouter(theEnv,WDISPLAY,"OFF\n");
#endif

EnvPrintRouter(theEnv,WDISPLAY,"Construct compiler is ");
#if CONSTRUCT_COMPILER
  EnvPrintRouter(theEnv,WDISPLAY,"ON\n");
#else
  EnvPrintRouter(theEnv,WDISPLAY,"OFF\n");
#endif

EnvPrintRouter(theEnv,WDISPLAY,"Basic I/O is ");
#if BASIC_IO
  EnvPrintRouter(theEnv,WDISPLAY,"ON\n");
#else
  EnvPrintRouter(theEnv,WDISPLAY,"OFF\n");
#endif

EnvPrintRouter(theEnv,WDISPLAY,"Extended I/O is ");
#if EXT_IO
  EnvPrintRouter(theEnv,WDISPLAY,"ON\n");
#else
  EnvPrintRouter(theEnv,WDISPLAY,"OFF\n");
#endif

EnvPrintRouter(theEnv,WDISPLAY,"String function package is ");
#if STRING_FUNCTIONS
  EnvPrintRouter(theEnv,WDISPLAY,"ON\n");
#else
  EnvPrintRouter(theEnv,WDISPLAY,"OFF\n");
#endif

EnvPrintRouter(theEnv,WDISPLAY,"Multifield function package is ");
#if MULTIFIELD_FUNCTIONS
  EnvPrintRouter(theEnv,WDISPLAY,"ON\n");
#else
  EnvPrintRouter(theEnv,WDISPLAY,"OFF\n");
#endif

EnvPrintRouter(theEnv,WDISPLAY,"Debugging functions are ");
#if DEBUGGING_FUNCTIONS
  EnvPrintRouter(theEnv,WDISPLAY,"ON\n");
#else
  EnvPrintRouter(theEnv,WDISPLAY,"OFF\n");
#endif

EnvPrintRouter(theEnv,WDISPLAY,"Block memory is ");
#if BLOCK_MEMORY
  EnvPrintRouter(theEnv,WDISPLAY,"ON\n");
#else
  EnvPrintRouter(theEnv,WDISPLAY,"OFF\n");
#endif

EnvPrintRouter(theEnv,WDISPLAY,"Window Interface flag is ");
#if WINDOW_INTERFACE
   EnvPrintRouter(theEnv,WDISPLAY,"ON\n");
#else
   EnvPrintRouter(theEnv,WDISPLAY,"OFF\n");
#endif

EnvPrintRouter(theEnv,WDISPLAY,"Short link names are ");
#if SHORT_LINK_NAMES
   EnvPrintRouter(theEnv,WDISPLAY,"ON\n");
#else
   EnvPrintRouter(theEnv,WDISPLAY,"OFF\n");
#endif

EnvPrintRouter(theEnv,WDISPLAY,"Developer flag is ");
#if DEVELOPER
   EnvPrintRouter(theEnv,WDISPLAY,"ON\n");
#else
   EnvPrintRouter(theEnv,WDISPLAY,"OFF\n");
#endif

EnvPrintRouter(theEnv,WDISPLAY,"Run time module is ");
#if RUN_TIME
  EnvPrintRouter(theEnv,WDISPLAY,"ON\n");
#else
  EnvPrintRouter(theEnv,WDISPLAY,"OFF\n");
#endif
  }

/********************************************************************
  NAME         : ExpandFuncCall
  DESCRIPTION  : This function is a wrap-around for a normal
                   function call.  It preexamines the argument
                   expression list and expands any references to the
                   sequence operator.  It builds a copy of the
                   function call expression with these new arguments
                   inserted and evaluates the function call.
  INPUTS       : A data object buffer
  RETURNS      : Nothing useful
  SIDE EFFECTS : Expressions alloctaed/deallocated
                 Function called and arguments evaluated
                 EvaluationError set on errors
  NOTES        : None
 *******************************************************************/
globle void ExpandFuncCall(
  void *theEnv,
  DATA_OBJECT *result)
  {
   EXPRESSION *newargexp,*fcallexp;
   struct FunctionDefinition *func;

   /* ======================================================================
      Copy the original function call's argument expression list.
      Look for expand$ function callsexpressions and replace those
        with the equivalent expressions of the expansions of evaluations
        of the arguments.
      ====================================================================== */
   newargexp = CopyExpression(theEnv,GetFirstArgument()->argList);
   ExpandFuncMultifield(theEnv,result,newargexp,&newargexp,
                        (void *) FindFunction(theEnv,"expand$"));

   /* ===================================================================
      Build the new function call expression with the expanded arguments.
      Check the number of arguments, if necessary, and call the thing.
      =================================================================== */
   fcallexp = get_struct(theEnv,expr);
   fcallexp->type = GetFirstArgument()->type;
   fcallexp->value = GetFirstArgument()->value;
   fcallexp->nextArg = NULL;
   fcallexp->argList = newargexp;
   if (fcallexp->type == FCALL)
     {
      func = (struct FunctionDefinition *) fcallexp->value;
      if (CheckFunctionArgCount(theEnv,ValueToString(func->callFunctionName),
                                func->restrictions,CountArguments(newargexp)) == FALSE)
        {
         result->type = SYMBOL;
         result->value = SymbolData(theEnv)->FalseSymbol;
         ReturnExpression(theEnv,fcallexp);
         return;
        }
     }
#if DEFFUNCTION_CONSTRUCT
   else if (fcallexp->type == PCALL)
     {
      if (CheckDeffunctionCall(theEnv,fcallexp->value,
              CountArguments(fcallexp->argList)) == FALSE)
        {
         result->type = SYMBOL;
         result->value = SymbolData(theEnv)->FalseSymbol;
         ReturnExpression(theEnv,fcallexp);
         SetEvaluationError(theEnv,TRUE);
         return;
        }
     }
#endif

   EvaluateExpression(theEnv,fcallexp,result);
   ReturnExpression(theEnv,fcallexp);
  }

/***********************************************************************
  NAME         : DummyExpandFuncMultifield
  DESCRIPTION  : The expansion of multifield arguments is valid only
                 when done for a function call.  All these expansions
                 are handled by the H/L wrap-around function
                 (expansion-call) - see ExpandFuncCall.  If the H/L
                 function, epand-multifield is ever called directly,
                 it is an error.
  INPUTS       : Data object buffer
  RETURNS      : Nothing useful
  SIDE EFFECTS : EvaluationError set
  NOTES        : None
 **********************************************************************/
globle void DummyExpandFuncMultifield(
  void *theEnv,
  DATA_OBJECT *result)
  {
   result->type = SYMBOL;
   result->value = SymbolData(theEnv)->FalseSymbol;
   SetEvaluationError(theEnv,TRUE);
   PrintErrorID(theEnv,"MISCFUN",1,FALSE);
   EnvPrintRouter(theEnv,WERROR,"expand$ must be used in the argument list of a function call.\n");
  }

/***********************************************************************
  NAME         : ExpandFuncMultifield
  DESCRIPTION  : Recursively examines an expression and replaces
                   PROC_EXPAND_MULTIFIELD expressions with the expanded
                   evaluation expression of its argument
  INPUTS       : 1) A data object result buffer
                 2) The expression to modify
                 3) The address of the expression, in case it is
                    deleted entirely
                 4) The address of the H/L function expand$
  RETURNS      : Nothing useful
  SIDE EFFECTS : Expressions allocated/deallocated as necessary
                 Evaluations performed
                 On errors, argument expression set to call a function
                   which causes an evaluation error when evaluated
                   a second time by actual caller.
  NOTES        : THIS ROUTINE MODIFIES EXPRESSIONS AT RUNTIME!!  MAKE
                 SURE THAT THE EXPRESSION PASSED IS SAFE TO CHANGE!!
 **********************************************************************/
static void ExpandFuncMultifield(
  void *theEnv,
  DATA_OBJECT *result,
  EXPRESSION *exp,
  EXPRESSION **sto,
  void *expmult)
  {
   EXPRESSION *newexp,*top,*bot;
   register long i; /* 6.04 Bug Fix */

   while (exp != NULL)
     {
      if (exp->value == expmult)
        {
         EvaluateExpression(theEnv,exp->argList,result);
         ReturnExpression(theEnv,exp->argList);
         if ((EvaluationData(theEnv)->EvaluationError) || (result->type != MULTIFIELD))
           {
            exp->argList = NULL;
            if ((EvaluationData(theEnv)->EvaluationError == FALSE) && (result->type != MULTIFIELD))
              ExpectedTypeError2(theEnv,"expand$",1);
            exp->value = (void *) FindFunction(theEnv,"(set-evaluation-error)");
            EvaluationData(theEnv)->EvaluationError = FALSE;
            EvaluationData(theEnv)->HaltExecution = FALSE;
            return;
           }
         top = bot = NULL;
         for (i = GetpDOBegin(result) ; i <= GetpDOEnd(result) ; i++)
           {
            newexp = get_struct(theEnv,expr);
            newexp->type = GetMFType(result->value,i);
            newexp->value = GetMFValue(result->value,i);
            newexp->argList = NULL;
            newexp->nextArg = NULL;
            if (top == NULL)
              top = newexp;
            else
              bot->nextArg = newexp;
            bot = newexp;
           }
         if (top == NULL)
           {
            *sto = exp->nextArg;
            rtn_struct(theEnv,expr,exp);
            exp = *sto;
           }
         else
           {
            bot->nextArg = exp->nextArg;
            *sto = top;
            rtn_struct(theEnv,expr,exp);
            sto = &bot->nextArg;
            exp = bot->nextArg;
           }
        }
      else
        {
         if (exp->argList != NULL)
           ExpandFuncMultifield(theEnv,result,exp->argList,&exp->argList,expmult);
         sto = &exp->nextArg;
         exp = exp->nextArg;
        }
     }
  }

/****************************************************************
  NAME         : CauseEvaluationError
  DESCRIPTION  : Dummy function use to cause evaluation errors on
                   a function call to generate error messages
  INPUTS       : None
  RETURNS      : A pointer to the FalseSymbol
  SIDE EFFECTS : EvaluationError set
  NOTES        : None
 ****************************************************************/
globle SYMBOL_HN *CauseEvaluationError(
  void *theEnv)
  {
   SetEvaluationError(theEnv,TRUE);
   return((SYMBOL_HN *) SymbolData(theEnv)->FalseSymbol);
  }

/****************************************************************
  NAME         : SetSORCommand
  DESCRIPTION  : Toggles SequenceOpMode - if TRUE, multifield
                   references are replaced with sequence
                   expansion operators
  INPUTS       : None
  RETURNS      : The old value of SequenceOpMode
  SIDE EFFECTS : SequenceOpMode toggled
  NOTES        : None
 ****************************************************************/
globle BOOLEAN SetSORCommand(
  void *theEnv)
  {
#if (! RUN_TIME) && (! BLOAD_ONLY)
   DATA_OBJECT arg;

   if (EnvArgTypeCheck(theEnv,"set-sequence-operator-recognition",1,SYMBOL,&arg) == FALSE)
     return(ExpressionData(theEnv)->SequenceOpMode);
   return(EnvSetSequenceOperatorRecognition(theEnv,(arg.value == SymbolData(theEnv)->FalseSymbol) ?
                                         FALSE : TRUE));
#else
     return(ExpressionData(theEnv)->SequenceOpMode);
#endif
  }

/********************************************************************
  NAME         : GetFunctionRestrictions
  DESCRIPTION  : Gets DefineFunction2() restriction list for function
  INPUTS       : None
  RETURNS      : A string containing the function restriction codes
  SIDE EFFECTS : EvaluationError set on errors
  NOTES        : None
 ********************************************************************/
globle SYMBOL_HN *GetFunctionRestrictions(
  void *theEnv)
  {
   DATA_OBJECT temp;
   struct FunctionDefinition *fptr;

   if (EnvArgTypeCheck(theEnv,"get-function-restrictions",1,SYMBOL,&temp) == FALSE)
     return((SYMBOL_HN *) EnvAddSymbol(theEnv,""));
   fptr = FindFunction(theEnv,DOToString(temp));
   if (fptr == NULL)
     {
      CantFindItemErrorMessage(theEnv,"function",DOToString(temp));
      SetEvaluationError(theEnv,TRUE);
      return((SYMBOL_HN *) EnvAddSymbol(theEnv,""));
     }
   if (fptr->restrictions == NULL)
     return((SYMBOL_HN *) EnvAddSymbol(theEnv,"0**"));
   return((SYMBOL_HN *) EnvAddSymbol(theEnv,fptr->restrictions));
  }

/*************************************************/
/* GetFunctionListFunction: H/L access routine   */
/*   for the get-function-list function.         */
/*************************************************/
globle void GetFunctionListFunction(
  void *theEnv,
  DATA_OBJECT *returnValue)
  {
   struct FunctionDefinition *theFunction;
   struct multifield *theList;
   unsigned long functionCount = 0;

   if (EnvArgCountCheck(theEnv,"get-function-list",EXACTLY,0) == -1)
     {
      EnvSetMultifieldErrorValue(theEnv,returnValue);
      return;
     }

   for (theFunction = GetFunctionList(theEnv);
        theFunction != NULL;
        theFunction = theFunction->next)
     { functionCount++; }

   SetpType(returnValue,MULTIFIELD);
   SetpDOBegin(returnValue,1);
   SetpDOEnd(returnValue,functionCount);
   theList = (struct multifield *) EnvCreateMultifield(theEnv,functionCount);
   SetpValue(returnValue,(void *) theList);

   for (theFunction = GetFunctionList(theEnv), functionCount = 1;
        theFunction != NULL;
        theFunction = theFunction->next, functionCount++)
     {
      SetMFType(theList,functionCount,SYMBOL);
      SetMFValue(theList,functionCount,theFunction->callFunctionName);
     }
  }

/***************************************/
/* FuncallFunction: H/L access routine */
/*   for the funcall function.         */
/***************************************/
globle void FuncallFunction(
  void *theEnv,
  DATA_OBJECT *returnValue)
  {
   int argCount, i, j;
   DATA_OBJECT theValue;
   FUNCTION_REFERENCE theReference;
   char *name;
   struct multifield *theMultifield;
   struct expr *lastAdd = NULL, *nextAdd;
    
   /*==================================*/
   /* Set up the default return value. */
   /*==================================*/
   
   SetpType(returnValue,SYMBOL);
   SetpValue(returnValue,SymbolData(theEnv)->FalseSymbol);
   
   /*=================================================*/
   /* The funcall function has at least one argument: */
   /* the name of the function being called.          */
   /*=================================================*/
   
   if ((argCount = EnvArgCountCheck(theEnv,"funcall",AT_LEAST,1)) == -1) return;
   
   /*============================================*/
   /* Get the name of the function to be called. */
   /*============================================*/
   
   if (EnvArgTypeCheck(theEnv,"funcall",1,SYMBOL_OR_STRING,&theValue) == FALSE) 
     { return; }
   
   /*====================*/
   /* Find the function. */
   /*====================*/

   name = DOToString(theValue);
   if (! GetFunctionReference(theEnv,name,&theReference))
     {
      ExpectedTypeError1(theEnv,"funcall",1,"function, deffunction, or generic function name");
      return; 
     }
     
   ExpressionInstall(theEnv,&theReference);
     
   /*======================================*/
   /* Add the arguments to the expression. */
   /*======================================*/
     
   for (i = 2; i <= argCount; i++)
     {
      EnvRtnUnknown(theEnv,i,&theValue);
      if (GetEvaluationError(theEnv))
        {  
         ExpressionDeinstall(theEnv,&theReference);
         return; 
        }
      
      switch(GetType(theValue))
        {
         case MULTIFIELD:
           theMultifield = (struct multifield *) GetValue(theValue);
           for (j = GetDOBegin(theValue); j <= GetDOEnd(theValue); j++)
             {
              nextAdd = GenConstant(theEnv,GetMFType(theMultifield,j),GetMFValue(theMultifield,j));
              if (lastAdd == NULL)
                { theReference.argList = nextAdd; }
              else
                { lastAdd->nextArg = nextAdd; }
              lastAdd = nextAdd;
              ExpressionInstall(theEnv,lastAdd);
             }
           break;
         
         default:
           nextAdd = GenConstant(theEnv,GetType(theValue),GetValue(theValue));
           if (lastAdd == NULL)
             { theReference.argList = nextAdd; }
           else
             { lastAdd->nextArg = nextAdd; }
           lastAdd = nextAdd;
           ExpressionInstall(theEnv,lastAdd);
           break;    
        }
     }
     
   /*======================*/
   /* Call the expression. */
   /*======================*/
   
   EvaluateExpression(theEnv,&theReference,returnValue);
   
   /*========================================*/
   /* Return the expression data structures. */
   /*========================================*/
   
   ExpressionDeinstall(theEnv,&theReference);
   ReturnExpression(theEnv,theReference.argList);
  }
  
/************************************/
/* TimeFunction: H/L access routine */
/*   for the time function.         */
/************************************/
globle double TimeFunction(
  void *theEnv)
  {
   /*=========================================*/
   /* The time function accepts no arguments. */
   /*=========================================*/

   EnvArgCountCheck(theEnv,"time",EXACTLY,0);

   /*==================*/
   /* Return the time. */
   /*==================*/

   return(gentime());
  }

/***************************************/
/* TimerFunction: H/L access routine   */
/*   for the timer function.           */
/***************************************/
globle double TimerFunction(
  void *theEnv)
  {
   int numa, i;
   double startTime;
   DATA_OBJECT returnValue;

   startTime = gentime();
   
   numa = EnvRtnArgCount(theEnv);

   i = 1;
   while ((i <= numa) && (GetHaltExecution(theEnv) != TRUE))
     {
      EnvRtnUnknown(theEnv,i,&returnValue);
      i++;
     }

   return(gentime() - startTime);
  }
 