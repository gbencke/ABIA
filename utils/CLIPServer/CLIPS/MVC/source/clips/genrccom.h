   /*******************************************************/
   /*      "C" Language Integrated Production System      */
   /*                                                     */
   /*               CLIPS Version 6.20  01/31/02          */
   /*                                                     */
   /*                                                     */
   /*******************************************************/

/*************************************************************/
/* Purpose:                                                  */
/*                                                           */
/* Principal Programmer(s):                                  */
/*      Brian L. Donnell                                     */
/*                                                           */
/* Contributing Programmer(s):                               */
/*                                                           */
/* Revision History:                                         */
/*                                                           */
/*************************************************************/

#ifndef _H_genrccom
#define _H_genrccom

#define EnvGetDefgenericName(theEnv,x) GetConstructNameString((struct constructHeader *) x)
#define EnvGetDefgenericPPForm(theEnv,x) GetConstructPPForm(theEnv,(struct constructHeader *) x)

#define SetNextDefgeneric(g,t) SetNextConstruct((struct constructHeader *) g, \
                                                (struct constructHeader *) t)
#define GetDefgenericNamePointer(x) GetConstructNamePointer((struct constructHeader *) x)
#define SetDefgenericPPForm(g,ppf) SetConstructPPForm(theEnv,(struct constructHeader *) g,ppf)

#define EnvDefgenericModule(theEnv,x) GetConstructModuleName((struct constructHeader *) x)

#ifndef _H_constrct
#include "constrct.h"
#endif
#ifndef _H_cstrccom
#include "cstrccom.h"
#endif
#ifndef _H_evaluatn
#include "evaluatn.h"
#endif
#ifndef _H_moduldef
#include "moduldef.h"
#endif
#ifndef _H_genrcfun
#include "genrcfun.h"
#endif
#ifndef _H_symbol
#include "symbol.h"
#endif

#ifdef LOCALE
#undef LOCALE
#endif

#ifdef _GENRCCOM_SOURCE_
#define LOCALE
#else
#define LOCALE extern
#endif

#if ENVIRONMENT_API_ONLY
#define DefgenericModule(theEnv,x) GetConstructModuleName((struct constructHeader *) x)
#define FindDefgeneric(theEnv,a) EnvFindDefgeneric(theEnv,a)
#define GetDefgenericList(theEnv,a,b) EnvGetDefgenericList(theEnv,a,b)
#define GetDefgenericName(theEnv,x) GetConstructNameString((struct constructHeader *) x)
#define GetDefgenericPPForm(theEnv,x) GetConstructPPForm(theEnv,(struct constructHeader *) x)
#define GetDefgenericWatch(theEnv,a) EnvGetDefgenericWatch(theEnv,a)
#define GetNextDefgeneric(theEnv,a) EnvGetNextDefgeneric(theEnv,a)
#define IsDefgenericDeletable(theEnv,a) EnvIsDefgenericDeletable(theEnv,a)
#define ListDefgenerics(theEnv,a,b) EnvListDefgenerics(theEnv,a,b)
#define SetDefgenericWatch(theEnv,a,b) EnvSetDefgenericWatch(theEnv,a,b)
#define Undefgeneric(theEnv,a) EnvUndefgeneric(theEnv,a)
#define GetDefmethodDescription(theEnv,a,b,c,d) EnvGetDefmethodDescription(theEnv,a,b,c,d)
#define GetDefmethodList(theEnv,a,b) EnvGetDefmethodList(theEnv,a,b)
#define GetDefmethodPPForm(theEnv,a,b) EnvGetDefmethodPPForm(theEnv,a,b)
#define GetDefmethodWatch(theEnv,a,b) EnvGetDefmethodWatch(theEnv,a,b)
#define GetMethodRestrictions(theEnv,a,b,c) EnvGetMethodRestrictions(theEnv,a,b,c)
#define GetNextDefmethod(theEnv,a,b) EnvGetNextDefmethod(theEnv,a,b)
#define IsDefmethodDeletable(theEnv,a,b) EnvIsDefmethodDeletable(theEnv,a,b)
#define ListDefmethods(theEnv,a,b) EnvListDefmethods(theEnv,a,b)
#define SetDefmethodWatch(theEnv,a,b,c) EnvSetDefmethodWatch(theEnv,a,b,c)
#define Undefmethod(theEnv,a,b) EnvUndefmethod(theEnv,a,b)
#else
#define DefgenericModule(x) GetConstructModuleName((struct constructHeader *) x)
#define FindDefgeneric(a) EnvFindDefgeneric(GetCurrentEnvironment(),a)
#define GetDefgenericList(a,b) EnvGetDefgenericList(GetCurrentEnvironment(),a,b)
#define GetDefgenericName(x) GetConstructNameString((struct constructHeader *) x)
#define GetDefgenericPPForm(x) GetConstructPPForm(GetCurrentEnvironment(),(struct constructHeader *) x)
#define GetDefgenericWatch(a) EnvGetDefgenericWatch(GetCurrentEnvironment(),a)
#define GetNextDefgeneric(a) EnvGetNextDefgeneric(GetCurrentEnvironment(),a)
#define IsDefgenericDeletable(a) EnvIsDefgenericDeletable(GetCurrentEnvironment(),a)
#define ListDefgenerics(a,b) EnvListDefgenerics(GetCurrentEnvironment(),a,b)
#define SetDefgenericWatch(a,b) EnvSetDefgenericWatch(GetCurrentEnvironment(),a,b)
#define Undefgeneric(a) EnvUndefgeneric(GetCurrentEnvironment(),a)
#define GetDefmethodDescription(a,b,c,d) EnvGetDefmethodDescription(GetCurrentEnvironment(),a,b,c,d)
#define GetDefmethodList(a,b) EnvGetDefmethodList(GetCurrentEnvironment(),a,b)
#define GetDefmethodPPForm(a,b) EnvGetDefmethodPPForm(GetCurrentEnvironment(),a,b)
#define GetDefmethodWatch(a,b) EnvGetDefmethodWatch(GetCurrentEnvironment(),a,b)
#define GetMethodRestrictions(a,b,c) EnvGetMethodRestrictions(GetCurrentEnvironment(),a,b,c)
#define GetNextDefmethod(a,b) EnvGetNextDefmethod(GetCurrentEnvironment(),a,b)
#define IsDefmethodDeletable(a,b) EnvIsDefmethodDeletable(GetCurrentEnvironment(),a,b)
#define ListDefmethods(a,b) EnvListDefmethods(GetCurrentEnvironment(),a,b)
#define SetDefmethodWatch(a,b,c) EnvSetDefmethodWatch(GetCurrentEnvironment(),a,b,c)
#define Undefmethod(a,b) EnvUndefmethod(GetCurrentEnvironment(),a,b)
#endif

LOCALE void SetupGenericFunctions(void *);
LOCALE void *EnvFindDefgeneric(void *,char *);
LOCALE DEFGENERIC *LookupDefgenericByMdlOrScope(void *,char *);
LOCALE DEFGENERIC *LookupDefgenericInScope(void *,char *);
LOCALE void *EnvGetNextDefgeneric(void *,void *);
LOCALE unsigned EnvGetNextDefmethod(void *,void *,unsigned);
LOCALE int EnvIsDefgenericDeletable(void *,void *);
LOCALE int EnvIsDefmethodDeletable(void *,void *,unsigned);
LOCALE void UndefgenericCommand(void *);
LOCALE SYMBOL_HN *GetDefgenericModuleCommand(void *);
LOCALE void UndefmethodCommand(void *);
LOCALE DEFMETHOD *GetDefmethodPointer(void *,unsigned);

LOCALE BOOLEAN EnvUndefgeneric(void *,void *);
LOCALE BOOLEAN EnvUndefmethod(void *,void *,unsigned);

#if ! OBJECT_SYSTEM
LOCALE void TypeCommand(void *,DATA_OBJECT *);
#endif

#if DEBUGGING_FUNCTIONS
LOCALE void EnvGetDefmethodDescription(void *,char *,int,void *,unsigned);
LOCALE unsigned EnvGetDefgenericWatch(void *,void *);
LOCALE void EnvSetDefgenericWatch(void *,unsigned,void *);
LOCALE unsigned EnvGetDefmethodWatch(void *,void *,unsigned);
LOCALE void EnvSetDefmethodWatch(void *,unsigned,void *,unsigned);
LOCALE void PPDefgenericCommand(void *);
LOCALE void PPDefmethodCommand(void *);
LOCALE void ListDefmethodsCommand(void *);
LOCALE char *EnvGetDefmethodPPForm(void *,void *,unsigned);
LOCALE void ListDefgenericsCommand(void *);
LOCALE void EnvListDefgenerics(void *,char *,struct defmodule *);
LOCALE void EnvListDefmethods(void *,char *,void *);
#endif

LOCALE void GetDefgenericListFunction(void *,DATA_OBJECT *);
globle void EnvGetDefgenericList(void *,DATA_OBJECT *,struct defmodule *);
LOCALE void GetDefmethodListCommand(void *,DATA_OBJECT *);
LOCALE void EnvGetDefmethodList(void *,void *,DATA_OBJECT *);
LOCALE void GetMethodRestrictionsCommand(void *,DATA_OBJECT *);
LOCALE void EnvGetMethodRestrictions(void *,void *,unsigned,DATA_OBJECT *);

#endif





