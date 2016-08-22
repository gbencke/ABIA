   /*******************************************************/
   /*      "C" Language Integrated Production System      */
   /*                                                     */
   /*             CLIPS Version 6.20  01/31/02            */
   /*                                                     */
   /*              FACT FUNCTIONS HEADER FILE             */
   /*******************************************************/

/*************************************************************/
/* Purpose:                                                  */
/*                                                           */
/* Principal Programmer(s):                                  */
/*      Gary D. Riley                                        */
/*                                                           */
/* Contributing Programmer(s):                               */
/*                                                           */
/* Revision History:                                         */
/*                                                           */
/*************************************************************/

#ifndef _H_factfun
#define _H_factfun

#ifndef _H_factmngr
#include "factmngr.h"
#endif

#ifdef LOCALE
#undef LOCALE
#endif

#ifdef _FACTFUN_SOURCE_
#define LOCALE
#else
#define LOCALE extern
#endif

   LOCALE void                           FactFunctionDefinitions(void *);
   LOCALE void                          *FactRelationFunction(void *);
   LOCALE void                          *FactRelation(void *);
   LOCALE long int                       FactExistpFunction(void *);
   LOCALE long int                       FactExistp(void *);
   LOCALE void                           FactSlotValueFunction(void *,DATA_OBJECT *);
   LOCALE void                           FactSlotValue(void *,void *,char *,DATA_OBJECT *);
   LOCALE void                           FactSlotNamesFunction(void *,DATA_OBJECT *);
   LOCALE void                           FactSlotNames(void *,void *,DATA_OBJECT *);
   LOCALE void                           GetFactListFunction(void *,DATA_OBJECT *);
   LOCALE void                           GetFactList(void *,DATA_OBJECT *,void *);
   LOCALE struct fact                   *GetFactAddressOrIndexArgument(void *,char *,int,int);

#endif

