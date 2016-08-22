   /*******************************************************/
   /*      "C" Language Integrated Production System      */
   /*                                                     */
   /*             CLIPS Version 6.20  11/06/01            */
   /*                                                     */
   /*                REGISTRY HEADER FILE                 */
   /*******************************************************/

/*************************************************************/
/* Purpose: Provides basic routines for storing information  */
/*   in the  registry.                                       */
/*                                                           */
/* Principal Programmer(s):                                  */
/*      Gary D. Riley                                        */
/*                                                           */
/* Contributing Programmer(s):                               */
/*                                                           */
/* Revision History:                                         */
/*                                                           */
/*************************************************************/

#ifndef _H_registry

#define _H_registry

#ifdef LOCALE
#undef LOCALE
#endif

#ifdef _REGISTRY_SOURCE_
#define LOCALE
#else
#define LOCALE extern
#endif

   LOCALE void                           ReadRegistryInformation(void);
   LOCALE void                           SaveWatchInformation(void);
   LOCALE void                           SaveExecutionInformation(void);
   LOCALE void                           SavePreferenceInformation(void);

#ifndef _REGISTRY_SOURCE_
#endif

#endif

