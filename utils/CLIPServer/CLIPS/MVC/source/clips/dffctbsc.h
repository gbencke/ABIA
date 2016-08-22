   /*******************************************************/
   /*      "C" Language Integrated Production System      */
   /*                                                     */
   /*             CLIPS Version 6.20  01/31/02            */
   /*                                                     */
   /*         DEFFACTS BASIC COMMANDS HEADER FILE         */
   /*******************************************************/

/*************************************************************/
/* Purpose: Implements core commands for the deffacts        */
/*   construct such as clear, reset, save, undeffacts,       */
/*   ppdeffacts, list-deffacts, and get-deffacts-list.       */
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

#ifndef _H_dffctbsc
#define _H_dffctbsc

#ifndef _H_evaluatn
#include "evaluatn.h"
#endif

#ifdef LOCALE
#undef LOCALE
#endif

#ifdef _DFFCTBSC_SOURCE_
#define LOCALE
#else
#define LOCALE extern
#endif

#if ENVIRONMENT_API_ONLY
#define GetDeffactsList(theEnv,a,b) EnvGetDeffactsList(theEnv,a,b)
#define ListDeffacts(theEnv,a,b) EnvListDeffacts(theEnv,a,b)
#define Undeffacts(theEnv,a) EnvUndeffacts(theEnv,a)
#else
#define GetDeffactsList(a,b) EnvGetDeffactsList(GetCurrentEnvironment(),a,b)
#define ListDeffacts(a,b) EnvListDeffacts(GetCurrentEnvironment(),a,b)
#define Undeffacts(a) EnvUndeffacts(GetCurrentEnvironment(),a)
#endif

   LOCALE void                           DeffactsBasicCommands(void *);
   LOCALE void                           UndeffactsCommand(void *);
   LOCALE BOOLEAN                        EnvUndeffacts(void *,void *);
   LOCALE void                           GetDeffactsListFunction(void *,DATA_OBJECT_PTR);
   LOCALE void                           EnvGetDeffactsList(void *,DATA_OBJECT_PTR,void *);
   LOCALE SYMBOL_HN                     *DeffactsModuleFunction(void *);
   LOCALE void                           PPDeffactsCommand(void *);
   LOCALE int                            PPDeffacts(void *,char *,char *);
   LOCALE void                           ListDeffactsCommand(void *);
   LOCALE void                           EnvListDeffacts(void *,char *,void *);

#endif

