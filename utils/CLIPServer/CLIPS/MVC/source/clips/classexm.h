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

#ifndef _H_classexm
#define _H_classexm

#ifdef LOCALE
#undef LOCALE
#endif

#ifdef _CLASSEXM_SOURCE_
#define LOCALE
#else
#define LOCALE extern
#endif

#if ENVIRONMENT_API_ONLY
#define BrowseClasses(theEnv,a,b) EnvBrowseClasses(theEnv,a,b)
#define DescribeClass(theEnv,a,b) EnvBrowseClasses(theEnv,a,b)
#define SlotDirectAccessP(theEnv,a,b) EnvSlotDirectAccessP(theEnv,a,b)
#define SlotExistP(theEnv,a,b,c) EnvSlotExistP(theEnv,a,b,c)
#define SlotInitableP(theEnv,a,b) EnvSlotInitableP(theEnv,a,b)
#define SlotPublicP(theEnv,a,b) EnvSlotPublicP(theEnv,a,b)
#define SlotWritableP(theEnv,a,b) EnvSlotWritableP(theEnv,a,b)
#define SubclassP(theEnv,a,b) EnvSubclassP(theEnv,a,b)
#define SuperclassP(theEnv,a,b) EnvSuperclassP(theEnv,a,b)
#else
#define BrowseClasses(a,b) EnvBrowseClasses(GetCurrentEnvironment(),a,b)
#define DescribeClass(a,b) EnvBrowseClasses(GetCurrentEnvironment(),a,b)
#define SlotDirectAccessP(a,b) EnvSlotDirectAccessP(GetCurrentEnvironment(),a,b)
#define SlotExistP(a,b,c) EnvSlotExistP(GetCurrentEnvironment(),a,b,c)
#define SlotInitableP(a,b) EnvSlotInitableP(GetCurrentEnvironment(),a,b)
#define SlotPublicP(a,b) EnvSlotPublicP(GetCurrentEnvironment(),a,b)
#define SlotWritableP(a,b) EnvSlotWritableP(GetCurrentEnvironment(),a,b)
#define SubclassP(a,b) EnvSubclassP(GetCurrentEnvironment(),a,b)
#define SuperclassP(a,b) EnvSuperclassP(GetCurrentEnvironment(),a,b)
#endif

#if DEBUGGING_FUNCTIONS
LOCALE void BrowseClassesCommand(void *);
LOCALE void EnvBrowseClasses(void *,char *,void *);
LOCALE void DescribeClassCommand(void *);
LOCALE void EnvDescribeClass(void *,char *,void *);
#endif

LOCALE char *GetCreateAccessorString(void *);

LOCALE SYMBOL_HN *GetDefclassModuleCommand(void *);
LOCALE BOOLEAN SuperclassPCommand(void *);
LOCALE BOOLEAN EnvSuperclassP(void *,void *,void *);
LOCALE BOOLEAN SubclassPCommand(void *);
LOCALE BOOLEAN EnvSubclassP(void *,void *,void *);
LOCALE int SlotExistPCommand(void *);
LOCALE BOOLEAN EnvSlotExistP(void *,void *,char *,BOOLEAN);
LOCALE int MessageHandlerExistPCommand(void *);
LOCALE BOOLEAN SlotWritablePCommand(void *);
LOCALE BOOLEAN EnvSlotWritableP(void *,void *,char *);
LOCALE BOOLEAN SlotInitablePCommand(void *);
LOCALE BOOLEAN EnvSlotInitableP(void *,void *,char *);
LOCALE BOOLEAN SlotPublicPCommand(void *);
LOCALE BOOLEAN EnvSlotPublicP(void *,void *,char *);
LOCALE BOOLEAN SlotDirectAccessPCommand(void *);
LOCALE BOOLEAN EnvSlotDirectAccessP(void *,void *,char *);
LOCALE void SlotDefaultValueCommand(void *,DATA_OBJECT_PTR);
LOCALE BOOLEAN SlotDefaultValue(void *,void *,char *,DATA_OBJECT_PTR);
LOCALE int ClassExistPCommand(void *);

#ifndef _CLASSEXM_SOURCE_
#endif

#endif
