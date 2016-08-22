   /*******************************************************/
   /*      "C" Language Integrated Production System      */
   /*                                                     */
   /*             CLIPS Version 6.20  01/31/02            */
   /*                                                     */
   /*                 ENGINE HEADER FILE                  */
   /*******************************************************/

/*************************************************************/
/* Purpose: Provides functionality primarily associated with */
/*   the run and focus commands.                             */
/*                                                           */
/* Principal Programmer(s):                                  */
/*      Gary D. Riley                                        */
/*                                                           */
/* Contributing Programmer(s):                               */
/*                                                           */
/* Revision History:                                         */
/*                                                           */
/*************************************************************/

#ifndef _H_engine

#define _H_engine

#ifndef _H_lgcldpnd
#include "lgcldpnd.h"
#endif
#ifndef _H_ruledef
#include "ruledef.h"
#endif
#ifndef _H_network
#include "network.h"
#endif
#ifndef _H_moduldef
#include "moduldef.h"
#endif
#ifndef _H_retract
#include "retract.h"
#endif

struct focus
  {
   struct defmodule *theModule;
   struct defruleModule *theDefruleModule;
   struct focus *next;
  };
  
#define ENGINE_DATA 18

struct engineData
  { 
   struct defrule *ExecutingRule;
   BOOLEAN HaltRules;
#if LOGICAL_DEPENDENCIES
   struct joinNode *TheLogicalJoin;
   struct dependency *UnsupportedDataEntities;
   int alreadyEntered;
#endif
   struct callFunctionItem *ListOfRunFunctions;
   struct focus *CurrentFocus;
   int FocusChanged;
#if DEBUGGING_FUNCTIONS
   unsigned WatchStatistics;
   unsigned WatchFocus;
#endif
#if INCREMENTAL_RESET
   BOOLEAN IncrementalResetInProgress;
   BOOLEAN IncrementalResetFlag;
#endif
   BOOLEAN JoinOperationInProgress;
   struct partialMatch *GlobalLHSBinds;
   struct partialMatch *GlobalRHSBinds;
   struct joinNode *GlobalJoin;
   struct rdriveinfo *DriveRetractionList;
   struct partialMatch *GarbagePartialMatches;
   struct alphaMatch *GarbageAlphaMatches;
   int AlreadyRunning;
  };

#define EngineData(theEnv) ((struct engineData *) GetEnvironmentData(theEnv,ENGINE_DATA))

#ifdef LOCALE
#undef LOCALE
#endif

#ifdef _ENGINE_SOURCE_
#define LOCALE
#else
#define LOCALE extern
#endif

/**************************************************************/
/* The GetFocus function is remapped under certain conditions */
/* because it conflicts with a Windows 3.1 function.          */
/**************************************************************/
//#if ! ((GENERIC || IBM) && WINDOW_INTERFACE)
//#define WRGetFocus GetFocus
//#endif

#define MAX_PATTERNS_CHECKED 64

#if ENVIRONMENT_API_ONLY
#define ClearFocusStack(theEnv) EnvClearFocusStack(theEnv)
#define DefruleHasBreakpoint(theEnv,a) EnvDefruleHasBreakpoint(theEnv,a)
#define Focus(theEnv,a) EnvFocus(theEnv,a)
#define GetFocus(theEnv) EnvGetFocus(theEnv)
#define GetFocusStack(theEnv,a) EnvGetFocusStack(theEnv,a)
#define ListFocusStack(theEnv,a) EnvListFocusStack(theEnv,a)
#define PopFocus(theEnv) EnvPopFocus(theEnv)
#define RemoveBreak(theEnv,a) EnvRemoveBreak(theEnv,a)
#define RemoveRunFunction(theEnv,a) EnvRemoveRunFunction(theEnv,a)
#define Run(theEnv,a) EnvRun(theEnv,a)
#define SetBreak(theEnv,a) EnvSetBreak(theEnv,a)
#define ShowBreaks(theEnv,a,b) EnvShowBreaks(theEnv,a,b)
#else
#define ClearFocusStack() EnvClearFocusStack(GetCurrentEnvironment())
#define DefruleHasBreakpoint(a) EnvDefruleHasBreakpoint(GetCurrentEnvironment(),a)
#define Focus(a) EnvFocus(GetCurrentEnvironment(),a)
#define GetFocus() EnvGetFocus(GetCurrentEnvironment())
#define GetFocusStack(a) EnvFocusStack(GetCurrentEnvironment(),a)
#define ListFocusStack(a) EnvListFocusStack(GetCurrentEnvironment(),a)
#define PopFocus() EnvPopFocus(GetCurrentEnvironment())
#define RemoveBreak(a) EnvRemoveBreak(GetCurrentEnvironment(),a)
#define RemoveRunFunction(a) EnvRemoveRunFunction(GetCurrentEnvironment(),a)
#define Run(a) EnvRun(GetCurrentEnvironment(),a)
#define SetBreak(a) EnvSetBreak(GetCurrentEnvironment(),a)
#define ShowBreaks(a,b) EnvShowBreaks(GetCurrentEnvironment(),a,b)
#endif

   LOCALE BOOLEAN                 EnvAddRunFunction(void *,char *,
                                                    void (*)(void *),int);
   LOCALE BOOLEAN                 AddRunFunction(char *,void (*)(void),int);
   LOCALE long                    EnvRun(void *,long);
   LOCALE BOOLEAN                 EnvRemoveRunFunction(void *,char *);
   LOCALE void                    InitializeEngine(void *);
   LOCALE void                    EnvSetBreak(void *,void *);
   LOCALE BOOLEAN                 EnvRemoveBreak(void *,void *);
   LOCALE void                    RemoveAllBreakpoints(void *);
   LOCALE void                    EnvShowBreaks(void *,char *,void *);
   LOCALE BOOLEAN                 EnvDefruleHasBreakpoint(void *,void *);
   LOCALE void                    RunCommand(void *);
   LOCALE void                    SetBreakCommand(void *);
   LOCALE void                    RemoveBreakCommand(void *);
   LOCALE void                    ShowBreaksCommand(void *);
   LOCALE void                    HaltCommand(void *);
   LOCALE int                     FocusCommand(void *);
   LOCALE void                    ClearFocusStackCommand(void *);
   LOCALE void                    EnvClearFocusStack(void *);
   LOCALE void                   *GetNextFocus(void *,void *);
   LOCALE void                    EnvFocus(void *,void *);
   LOCALE int                     GetFocusChanged(void *);
   LOCALE void                    SetFocusChanged(void *,int);
   LOCALE void                    ListFocusStackCommand(void *);
   LOCALE void                    EnvListFocusStack(void *,char *);
   LOCALE void                    GetFocusStackFunction(void *,DATA_OBJECT_PTR);
   LOCALE void                    EnvGetFocusStack(void *,DATA_OBJECT_PTR);
   LOCALE SYMBOL_HN              *PopFocusFunction(void *);
   LOCALE SYMBOL_HN              *GetFocusFunction(void *);
   LOCALE void                   *EnvPopFocus(void *);
   LOCALE void                   *EnvGetFocus(void *);

#endif






