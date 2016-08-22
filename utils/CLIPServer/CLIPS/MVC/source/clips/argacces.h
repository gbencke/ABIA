   /*******************************************************/
   /*      "C" Language Integrated Production System      */
   /*                                                     */
   /*             CLIPS Version 6.20  01/31/02            */
   /*                                                     */
   /*             ARGUMENT ACCESS HEADER FILE             */
   /*******************************************************/

/*************************************************************/
/* Purpose: Provides access routines for accessing arguments */
/*   passed to user or system functions defined using the    */
/*   DefineFunction protocol.                                */
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

#ifndef _H_argacces

#define _H_argacces

#ifndef _H_expressn
#include "expressn.h"
#endif
#ifndef _H_evaluatn
#include "evaluatn.h"
#endif
#ifndef _H_moduldef
#include "moduldef.h"
#endif

#ifdef LOCALE
#undef LOCALE
#endif

#ifdef _ARGACCES_SOURCE_
#define LOCALE
#else
#define LOCALE extern
#endif

#if ENVIRONMENT_API_ONLY
#define RtnArgCount(theEnv,a,b,c,d) EnvRtnArgCount(theEnv)
#define ArgCountCheck(theEnv,a,b,c) EnvArgCountCheck(theEnv,a,b,c)
#define ArgRangeCheck(theEnv,a,b,c) EnvArgRangeCheck(theEnv,a,b,c)
#define RtnLexeme(theEnv,a) EnvRtnLexeme(theEnv,a)
#define RtnDouble(theEnv,a) EnvRtnDouble(theEnv,a)
#define RtnLong(theEnv,a) EnvRtnLong(theEnv,a)
#define RtnUnknown(theEnv,a,b) EnvRtnUnknown(theEnv,a,b)
#define ArgTypeCheck(theEnv,a,b,c,d) EnvArgTypeCheck(theEnv,a,b,c,d)
#else
#define RtnArgCount(a,b,c,d) EnvRtnArgCount(GetCurrentEnvironment())
#define ArgCountCheck(a,b,c) EnvArgCountCheck(GetCurrentEnvironment(),a,b,c)
#define ArgRangeCheck(a,b,c) EnvArgRangeCheck(GetCurrentEnvironment(),a,b,c)
#define RtnLexeme(a) EnvRtnLexeme(GetCurrentEnvironment(),a)
#define RtnDouble(a) EnvRtnDouble(GetCurrentEnvironment(),a)
#define RtnLong(a) EnvRtnLong(GetCurrentEnvironment(),a)
#define RtnUnknown(a,b) EnvRtnUnknown(GetCurrentEnvironment(),a,b)
#define ArgTypeCheck(a,b,c,d) EnvArgTypeCheck(GetCurrentEnvironment(),a,b,c,d)
#endif

   LOCALE int                            EnvRtnArgCount(void *);
   LOCALE int                            EnvArgCountCheck(void *,char *,int,int);
   LOCALE int                            EnvArgRangeCheck(void *,char *,int,int);
   LOCALE char                          *EnvRtnLexeme(void *,int);
   LOCALE double                         EnvRtnDouble(void *,int);
   LOCALE long                           EnvRtnLong(void *,int);
   LOCALE struct dataObject             *EnvRtnUnknown(void *,int,struct dataObject *);
   LOCALE int                            EnvArgTypeCheck(void *,char *,int,int,struct dataObject *);
   LOCALE BOOLEAN                        GetNumericArgument(void *,struct expr *,char *,struct dataObject *,int,int);
   LOCALE char                          *GetLogicalName(void *,int,char *);
   LOCALE char                          *GetFileName(void *,char *,int);
   LOCALE char                          *GetConstructName(void *,char *,char *);
   LOCALE void                           ExpectedCountError(void *,char *,int,int);
   LOCALE void                           OpenErrorMessage(void *,char *,char *);
   LOCALE BOOLEAN                        CheckFunctionArgCount(void *,char *,char *,int);
   LOCALE void                           ExpectedReturnTypeError(char *,char *);
   LOCALE void                           ExpectedTypeError1(void *,char *,int,char *);
   LOCALE void                           ExpectedTypeError2(void *,char *,int);
   LOCALE struct defmodule              *GetModuleName(void *,char *,int,int *);
   LOCALE void                          *GetFactOrInstanceArgument(void *,int,DATA_OBJECT *,char *);

#endif






