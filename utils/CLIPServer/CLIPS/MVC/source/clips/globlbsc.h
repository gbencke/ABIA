   /*******************************************************/
   /*      "C" Language Integrated Production System      */
   /*                                                     */
   /*             CLIPS Version 6.20  01/31/02            */
   /*                                                     */
   /*         DEFGLOBAL BASIC COMMANDS HEADER FILE        */
   /*******************************************************/

/*************************************************************/
/* Purpose:                                                  */
/* Principal Programmer(s):                                  */
/*      Gary D. Riley                                        */
/*                                                           */
/* Contributing Programmer(s):                               */
/*      Brian L. Donnell                                     */
/*                                                           */
/* Revision History:                                         */
/*                                                           */
/*************************************************************/

#ifndef _H_globlbsc
#define _H_globlbsc

#ifndef _H_evaluatn
#include "evaluatn.h"
#endif

#ifdef LOCALE
#undef LOCALE
#endif

#ifdef _GLOBLBSC_SOURCE_
#define LOCALE
#else
#define LOCALE extern
#endif

#if ENVIRONMENT_API_ONLY
#define GetDefglobalList(theEnv,a,b) EnvGetDefglobalList(theEnv,a,b)
#define GetDefglobalWatch(theEnv,a) EnvGetDefglobalWatch(theEnv,a)
#define ListDefglobals(theEnv,a,b) EnvListDefglobals(theEnv,a,b)
#define SetDefglobalWatch(theEnv,a,b) EnvSetDefglobalWatch(theEnv,a,b)
#define Undefglobal(theEnv,a) EnvUndefglobal(theEnv,a)
#else
#define GetDefglobalList(a,b) EnvGetDefglobalList(GetCurrentEnvironment(),a,b)
#define GetDefglobalWatch(a) EnvGetDefglobalWatch(GetCurrentEnvironment(),a)
#define ListDefglobals(a,b) EnvListDefglobals(GetCurrentEnvironment(),a,b)
#define SetDefglobalWatch(a,b) EnvSetDefglobalWatch(GetCurrentEnvironment(),a,b)
#define Undefglobal(a) EnvUndefglobal(GetCurrentEnvironment(),a)
#endif

   LOCALE void                           DefglobalBasicCommands(void *);
   LOCALE void                           UndefglobalCommand(void *);
   LOCALE BOOLEAN                        EnvUndefglobal(void *,void *);
   LOCALE void                           GetDefglobalListFunction(void *,DATA_OBJECT_PTR);
   LOCALE void                           EnvGetDefglobalList(void *,DATA_OBJECT_PTR,void *);
   LOCALE SYMBOL_HN                     *DefglobalModuleFunction(void *);
   LOCALE void                           PPDefglobalCommand(void *);
   LOCALE int                            PPDefglobal(void *,char *,char *);
   LOCALE void                           ListDefglobalsCommand(void *);
   LOCALE void                           EnvListDefglobals(void *,char *,void *);
   LOCALE unsigned                       EnvGetDefglobalWatch(void *,void *);
   LOCALE void                           EnvSetDefglobalWatch(void *,unsigned,void *);
   LOCALE void                           ResetDefglobals(void *);

#ifndef _GLOBLBSC_SOURCE_
   extern unsigned                       WatchGlobals;
#endif

#endif


