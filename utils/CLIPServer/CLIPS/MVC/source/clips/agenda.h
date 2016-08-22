   /*******************************************************/
   /*      "C" Language Integrated Production System      */
   /*                                                     */
   /*             CLIPS Version 6.20  01/31/02            */
   /*                                                     */
   /*                 AGENDA HEADER FILE                  */
   /*******************************************************/

/*************************************************************/
/* Purpose:                                                  */
/*   Provides functionality for examining, manipulating,     */
/*   adding, and removing activations from the agenda.       */
/*                                                           */
/* Principal Programmer(s):                                  */
/*      Gary D. Riley                                        */
/*                                                           */
/* Contributing Programmer(s):                               */
/*                                                           */
/* Revision History:                                         */
/*                                                           */
/*************************************************************/

#ifndef _H_agenda

#define _H_agenda

#ifndef _H_ruledef
#include "ruledef.h"
#endif
#ifndef _H_symbol
#include "symbol.h"
#endif
#ifndef _H_match
#include "match.h"
#endif

#define WHEN_DEFINED 0
#define WHEN_ACTIVATED 1
#define EVERY_CYCLE 2

#define MAX_DEFRULE_SALIENCE  10000
#define MIN_DEFRULE_SALIENCE -10000

/*******************/
/* DATA STRUCTURES */
/*******************/

struct activation
  {
   struct defrule *theRule;
   struct partialMatch *basis;
   int salience;
   unsigned long int timetag;
#if CONFLICT_RESOLUTION_STRATEGIES
   struct partialMatch *sortedBasis;
   int randomID;
#endif
   struct activation *prev;
   struct activation *next;
  };

typedef struct activation ACTIVATION;

#define AGENDA_DATA 17

struct agendaData
  { 
#if DEBUGGING_FUNCTIONS
   unsigned WatchActivations;
#endif
   unsigned long NumberOfActivations;
   unsigned long CurrentTimetag;
   int AgendaChanged;
#if DYNAMIC_SALIENCE
   BOOLEAN SalienceEvaluation;
#endif
#if CONFLICT_RESOLUTION_STRATEGIES
   int Strategy;
#endif
  };

#define EnvGetActivationSalience(actPtr) (((struct activation *) actPtr)->salience)
#define GetActivationRule(actPtr) (((struct activation *) actPtr)->theRule)
#define GetActivationBasis(actPtr) (((struct activation *) actPtr)->basis)
#define GetActivationSortedBasis(actPtr) (((struct activation *) actPtr)->sortedBasis)
#define AgendaData(theEnv) ((struct agendaData *) GetEnvironmentData(theEnv,AGENDA_DATA))

#ifdef LOCALE
#undef LOCALE
#endif

#ifdef _AGENDA_SOURCE_
#define LOCALE
#else
#define LOCALE extern
#endif

/****************************************/
/* GLOBAL EXTERNAL FUNCTION DEFINITIONS */
/****************************************/

#if ENVIRONMENT_API_ONLY
#define Agenda(theEnv,a) EnvAgenda(theEnv,a)
#define DeleteActivation(theEnv,a) EnvDeleteActivation(theEnv,a)
#define GetActivationName(theEnv,a) EnvGetActivationName(theEnv,a)
#define GetActivationPPForm(theEnv,a,b,c) EnvGetActivationPPForm(theEnv,a,b,c)
#define GetActivationSalience(theEnv,actPtr) (((struct activation *) actPtr)->salience)
#define GetAgendaChanged(theEnv) EnvGetAgendaChanged(theEnv)
#define GetNextActivation(theEnv,a) EnvGetNextActivation(theEnv,a)
#define GetSalienceEvaluation(theEnv) EnvGetSalienceEvaluation(theEnv)
#define Refresh(theEnv,a) EnvRefresh(theEnv,a)
#define RefreshAgenda(theEnv,a) EnvRefreshAgenda(theEnv,a)
#define ReorderAgenda(theEnv,a) EnvReorderAgenda(theEnv,a)
#define SetActivationSalience(theEnv,a,b) EnvSetActivationSalience(theEnv,a,b)
#define SetAgendaChanged(theEnv,a) EnvSetAgendaChanged(theEnv,a)
#define SetSalienceEvaluation(theEnv,a) EnvSetSalienceEvaluation(theEnv,a)
#else
#define Agenda(a) EnvAgenda(GetCurrentEnvironment(),a)
#define DeleteActivation(a) EnvDeleteActivation(GetCurrentEnvironment(),a)
#define GetActivationName(a) EnvGetActivationName(GetCurrentEnvironment(),a)
#define GetActivationPPForm(a,b,c) EnvGetActivationPPForm(GetCurrentEnvironment(),a,b,c)
#define GetActivationSalience(actPtr) (((struct activation *) actPtr)->salience)
#define GetAgendaChanged() EnvGetAgendaChanged(GetCurrentEnvironment())
#define GetNextActivation(a) EnvGetNextActivation(GetCurrentEnvironment(),a)
#define GetSalienceEvaluation() EnvGetSalienceEvaluation(GetCurrentEnvironment())
#define Refresh(a) EnvRefresh(GetCurrentEnvironment(),a)
#define RefreshAgenda(a) EnvRefreshAgenda(GetCurrentEnvironment(),a)
#define ReorderAgenda(a) EnvReorderAgenda(GetCurrentEnvironment(),a)
#define SetActivationSalience(a,b) EnvSetActivationSalience(GetCurrentEnvironment(),a,b)
#define SetAgendaChanged(a) EnvSetAgendaChanged(GetCurrentEnvironment(),a)
#define SetSalienceEvaluation(a) EnvSetSalienceEvaluation(GetCurrentEnvironment(),a)
#endif

   LOCALE void                    AddActivation(void *,void *,void *);
   LOCALE void                    ClearRuleFromAgenda(void *,void *);
   LOCALE void                   *EnvGetNextActivation(void *,void *);
   LOCALE char                   *EnvGetActivationName(void *,void *);
   LOCALE int                     EnvSetActivationSalience(void *,void *,int);
   LOCALE void                    EnvGetActivationPPForm(void *,char *,unsigned,void *);
   LOCALE BOOLEAN                 MoveActivationToTop(void *,void *);
   LOCALE BOOLEAN                 EnvDeleteActivation(void *,void *);
   LOCALE BOOLEAN                 DetachActivation(void *,void *);
   LOCALE void                    EnvAgenda(void *,char *,void *);
   LOCALE void                    RemoveActivation(void *,void *,int,int);
   LOCALE void                    RemoveAllActivations(void *);
   LOCALE int                     EnvGetAgendaChanged(void *);
   LOCALE void                    EnvSetAgendaChanged(void *,int);
   LOCALE unsigned long           GetNumberOfActivations(void *);
   LOCALE BOOLEAN                 EnvGetSalienceEvaluation(void *);
   LOCALE BOOLEAN                 EnvSetSalienceEvaluation(void *,BOOLEAN);
   LOCALE void                    EnvRefreshAgenda(void *,void *);
   LOCALE void                    EnvReorderAgenda(void *,void *);
   LOCALE void                    InitializeAgenda(void *);
   LOCALE SYMBOL_HN              *SetSalienceEvaluationCommand(void *);
   LOCALE SYMBOL_HN              *GetSalienceEvaluationCommand(void *);
   LOCALE void                    RefreshAgendaCommand(void *);
   LOCALE void                    RefreshCommand(void *);
   LOCALE BOOLEAN                 EnvRefresh(void *,void *);
#if DEBUGGING_FUNCTIONS
   LOCALE void                    AgendaCommand(void *);
#endif

#endif






