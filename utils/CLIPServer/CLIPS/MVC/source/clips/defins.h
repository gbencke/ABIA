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

#ifndef _H_defins
#define _H_defins

#if DEFINSTANCES_CONSTRUCT

#define EnvGetDefinstancesName(theEnv,x) GetConstructNameString((struct constructHeader *) x)
#define EnvGetDefinstancesPPForm(theEnv,x) GetConstructPPForm(theEnv,(struct constructHeader *) x)

#define GetDefinstancesNamePointer(x) GetConstructNamePointer((struct constructHeader *) x)
#define SetDefinstancesPPForm(d,ppf) SetConstructPPForm(theEnv,(struct constructHeader *) d,ppf)

#define GetDefinstancesModuleName(x) GetConstructModuleName((struct constructHeader *) x)
#define EnvDefinstancesModule(theEnv,x) GetConstructModuleName((struct constructHeader *) x)

struct definstances;

#ifndef _H_conscomp
#include "conscomp.h"
#endif
#ifndef _H_constrct
#include "constrct.h"
#endif
#ifndef _H_cstrccom
#include "cstrccom.h"
#endif
#ifndef _H_moduldef
#include "moduldef.h"
#endif
#ifndef _H_object
#include "object.h"
#endif

typedef struct definstancesModule
  {
   struct defmoduleItemHeader header;
  } DEFINSTANCES_MODULE;

typedef struct definstances
  {
   struct constructHeader header;
   unsigned busy;
   EXPRESSION *mkinstance;
  } DEFINSTANCES;

#define DEFINSTANCES_DATA 22

struct definstancesData
  { 
   struct construct *DefinstancesConstruct;
   int DefinstancesModuleIndex;
#if CONSTRUCT_COMPILER && (! RUN_TIME)
   struct CodeGeneratorItem *DefinstancesCodeItem;
#endif
  };

#define DefinstancesData(theEnv) ((struct definstancesData *) GetEnvironmentData(theEnv,DEFINSTANCES_DATA))


#ifdef LOCALE
#undef LOCALE
#endif

#ifdef _DEFINS_SOURCE_
#define LOCALE
#else
#define LOCALE extern
#endif

#if ENVIRONMENT_API_ONLY
#define DefinstancesModule(theEnv,x) GetConstructModuleName((struct constructHeader *) x)
#define FindDefinstances(theEnv,a) EnvFindDefinstances(theEnv,a)
#define GetDefinstancesList(theEnv,a,b) EnvGetDefinstancesList(theEnv,a,b)
#define GetDefinstancesName(theEnv,x) GetConstructNameString((struct constructHeader *) x)
#define GetDefinstancesPPForm(theEnv,x) GetConstructPPForm(theEnv,(struct constructHeader *) x)
#define GetNextDefinstances(theEnv,a) EnvGetNextDefinstances(theEnv,a)
#define IsDefinstancesDeletable(theEnv,a) EnvIsDefinstancesDeletable(theEnv,a)
#define ListDefinstances(theEnv,a,b) EnvListDefinstances(theEnv,a,b)
#define Undefinstances(theEnv,a) EnvUndefinstances(theEnv,a)
#else
#define DefinstancesModule(x) GetConstructModuleName((struct constructHeader *) x)
#define FindDefinstances(a) EnvFindDefinstances(GetCurrentEnvironment(),a)
#define GetDefinstancesList(a,b) EnvGetDefinstancesList(GetCurrentEnvironment(),a,b)
#define GetDefinstancesName(x) GetConstructNameString((struct constructHeader *) x)
#define GetDefinstancesPPForm(x) GetConstructPPForm(GetCurrentEnvironment(),(struct constructHeader *) x)
#define GetNextDefinstances(a) EnvGetNextDefinstances(GetCurrentEnvironment(),a)
#define IsDefinstancesDeletable(a) EnvIsDefinstancesDeletable(GetCurrentEnvironment(),a)
#define ListDefinstances(a,b) EnvListDefinstances(GetCurrentEnvironment(),a,b)
#define Undefinstances(a) EnvUndefinstances(GetCurrentEnvironment(),a)
#endif

LOCALE void SetupDefinstances(void *);
LOCALE void *EnvGetNextDefinstances(void *,void *);
LOCALE void *EnvFindDefinstances(void *,char *);
LOCALE int EnvIsDefinstancesDeletable(void *,void *);
LOCALE void UndefinstancesCommand(void *);
LOCALE SYMBOL_HN *GetDefinstancesModuleCommand(void *);
LOCALE BOOLEAN EnvUndefinstances(void *,void *);

#if DEBUGGING_FUNCTIONS
LOCALE void PPDefinstancesCommand(void *);
LOCALE void ListDefinstancesCommand(void *);
LOCALE void EnvListDefinstances(void *,char *,struct defmodule *);
#endif

LOCALE void GetDefinstancesListFunction(void *,DATA_OBJECT *);
LOCALE void EnvGetDefinstancesList(void *,DATA_OBJECT *,struct defmodule *);

#endif

#endif





