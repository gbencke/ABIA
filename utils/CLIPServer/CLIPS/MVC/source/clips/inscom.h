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

#ifndef _H_inscom
#define _H_inscom

#ifndef _H_object
#include "object.h"
#endif

#ifndef _H_insfun
#include "insfun.h"
#endif

#define INSTANCE_DATA 29

struct instanceData
  { 
   INSTANCE_TYPE DummyInstance;
   INSTANCE_TYPE **InstanceTable;
   int MaintainGarbageInstances;
   int MkInsMsgPass;
   int ChangesToInstances;
   IGARBAGE *InstanceGarbageList;
   struct patternEntityRecord InstanceInfo;
   INSTANCE_TYPE *InstanceList;  
   unsigned long GlobalNumberOfInstances;
   INSTANCE_TYPE *CurrentInstance;
   INSTANCE_TYPE *InstanceListBottom;
   BOOLEAN ObjectModDupMsgValid;
  };

#define InstanceData(theEnv) ((struct instanceData *) GetEnvironmentData(theEnv,INSTANCE_DATA))

#ifdef LOCALE
#undef LOCALE
#endif

#ifdef _INSCOM_SOURCE_
#define LOCALE
#else
#define LOCALE extern
#endif

#if ENVIRONMENT_API_ONLY
#define CreateRawInstance(theEnv,a,b) EnvCreateRawInstance(theEnv,a,b)
#define DeleteInstance(theEnv,a) EnvDeleteInstance(theEnv,a)
#define DirectGetSlot(theEnv,a,b,c) EnvDirectGetSlot(theEnv,a,b,c)
#define DirectPutSlot(theEnv,a,b,c) EnvDirectPutSlot(theEnv,a,b,c)
#define FindInstance(theEnv,a,b,c) EnvFindInstance(theEnv,a,b,c)
#define GetInstanceClass(theEnv,a) EnvGetInstanceClass(theEnv,a)
#define GetInstanceName(theEnv,a) EnvGetInstanceName(theEnv,a)
#define GetInstancePPForm(theEnv,a,b,c) EnvGetInstancePPForm(theEnv,a,b,c)
#define GetNextInstance(theEnv,a) EnvGetNextInstance(theEnv,a)
#define GetNextInstanceInClass(theEnv,a,b) EnvGetNextInstanceInClass(theEnv,a,b)
#define Instances(theEnv,a,b,c,d) EnvInstances(theEnv,a,b,c,d)
#define MakeInstance(theEnv,a) EnvMakeInstance(theEnv,a)
#define UnmakeInstance(theEnv,a) EnvUnmakeInstance(theEnv,a)
#define ValidInstanceAddress(theEnv,a) EnvValidInstanceAddress(theEnv,a)
#else
#define CreateRawInstance(a,b) EnvCreateRawInstance(GetCurrentEnvironment(),a,b)
#define DeleteInstance(a) EnvDeleteInstance(GetCurrentEnvironment(),a)
#define DirectGetSlot(a,b,c) EnvDirectGetSlot(GetCurrentEnvironment(),a,b,c)
#define DirectPutSlot(a,b,c) EnvDirectPutSlot(GetCurrentEnvironment(),a,b,c)
#define FindInstance(a,b,c) EnvFindInstance(GetCurrentEnvironment(),a,b,c)
#define GetInstanceClass(a) EnvGetInstanceClass(GetCurrentEnvironment(),a)
#define GetInstanceName(a) EnvGetInstanceName(GetCurrentEnvironment(),a)
#define GetInstancePPForm(a,b,c) EnvGetInstancePPForm(GetCurrentEnvironment(),a,b,c)
#define GetNextInstance(a) EnvGetNextInstance(GetCurrentEnvironment(),a)
#define GetNextInstanceInClass(a,b) EnvGetNextInstanceInClass(GetCurrentEnvironment(),a,b)
#define Instances(a,b,c,d) EnvInstances(GetCurrentEnvironment(),a,b,c,d)
#define MakeInstance(a) EnvMakeInstance(GetCurrentEnvironment(),a)
#define UnmakeInstance(a) EnvUnmakeInstance(GetCurrentEnvironment(),a)
#define ValidInstanceAddress(a) EnvValidInstanceAddress(GetCurrentEnvironment(),a)
#endif

LOCALE void SetupInstances(void *);
LOCALE BOOLEAN EnvDeleteInstance(void *,void *);
LOCALE BOOLEAN EnvUnmakeInstance(void *,void *);
#if DEBUGGING_FUNCTIONS
LOCALE void InstancesCommand(void *);
LOCALE void PPInstanceCommand(void *);
LOCALE void EnvInstances(void *,char *,void *,char *,int);
#endif
LOCALE void *EnvMakeInstance(void *,char *);
LOCALE void *EnvCreateRawInstance(void *,void *,char *);
LOCALE void *EnvFindInstance(void *,void *,char *,unsigned);
LOCALE int EnvValidInstanceAddress(void *,void *);
LOCALE void EnvDirectGetSlot(void *,void *,char *,DATA_OBJECT *);
LOCALE int EnvDirectPutSlot(void *,void *,char *,DATA_OBJECT *);
LOCALE char *EnvGetInstanceName(void *,void *);
LOCALE void *EnvGetInstanceClass(void *,void *);
LOCALE unsigned long GetGlobalNumberOfInstances(void *);
LOCALE void *EnvGetNextInstance(void *,void *);
LOCALE void *GetNextInstanceInScope(void *,void *);
LOCALE void *EnvGetNextInstanceInClass(void *,void *,void *);
LOCALE void *GetNextInstanceInClassAndSubclasses(void *,void **,void *,DATA_OBJECT *);
LOCALE void EnvGetInstancePPForm(void *,char *,unsigned,void *);
LOCALE void ClassCommand(void *,DATA_OBJECT *);
LOCALE BOOLEAN DeleteInstanceCommand(void *);
LOCALE BOOLEAN UnmakeInstanceCommand(void *);
LOCALE void SymbolToInstanceName(void *,DATA_OBJECT *);
LOCALE SYMBOL_HN *InstanceNameToSymbol(void *);
LOCALE void InstanceAddressCommand(void *,DATA_OBJECT *);
LOCALE void InstanceNameCommand(void *,DATA_OBJECT *);
LOCALE BOOLEAN InstanceAddressPCommand(void *);
LOCALE BOOLEAN InstanceNamePCommand(void *);
LOCALE BOOLEAN InstancePCommand(void *);
LOCALE BOOLEAN InstanceExistPCommand(void *);
LOCALE BOOLEAN CreateInstanceHandler(void *);

#endif





