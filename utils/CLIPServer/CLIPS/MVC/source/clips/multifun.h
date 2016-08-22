   /*******************************************************/
   /*      "C" Language Integrated Production System      */
   /*                                                     */
   /*             CLIPS Version 6.20  01/31/02            */
   /*                                                     */
   /*           MULTIFIELD FUNCTIONS HEADER FILE          */
   /*******************************************************/

/*************************************************************/
/* Purpose:                                                  */
/*                                                           */
/* Principal Programmer(s):                                  */
/*      Gary Riley and Brian Donnell                         */
/*                                                           */
/* Contributing Programmer(s):                               */
/*                                                           */
/* Revision History:                                         */
/*                                                           */
/*************************************************************/

#ifndef _H_multifun
#define _H_multifun

#ifndef _H_evaluatn
#include "evaluatn.h"
#endif

#ifdef LOCALE
#undef LOCALE
#endif

#ifdef _MULTIFUN_SOURCE_
#define LOCALE
#else
#define LOCALE extern
#endif

   LOCALE void                    MultifieldFunctionDefinitions(void *);
#if MULTIFIELD_FUNCTIONS
   LOCALE void                    DeleteFunction(void *,DATA_OBJECT_PTR);
   LOCALE void                    MVDeleteFunction(void *,DATA_OBJECT_PTR);
   LOCALE void                    ReplaceFunction(void *,DATA_OBJECT_PTR);
   LOCALE void                    MVReplaceFunction(void *,DATA_OBJECT_PTR);
   LOCALE void                    DeleteMemberFunction(void *,DATA_OBJECT_PTR);
   LOCALE void                    ReplaceMemberFunction(void *,DATA_OBJECT_PTR);
   LOCALE void                    InsertFunction(void *,DATA_OBJECT_PTR);
   LOCALE void                    ExplodeFunction(void *,DATA_OBJECT_PTR);
   LOCALE void                   *ImplodeFunction(void *);
   LOCALE void                    SubseqFunction(void *,DATA_OBJECT_PTR);
   LOCALE void                    MVSubseqFunction(void *,DATA_OBJECT_PTR);
   LOCALE void                    FirstFunction(void *,DATA_OBJECT_PTR);
   LOCALE void                    RestFunction(void *,DATA_OBJECT_PTR);
   LOCALE void                    NthFunction(void *,DATA_OBJECT_PTR);
   LOCALE BOOLEAN                 SubsetpFunction(void *);
   LOCALE void                    MemberFunction(void *,DATA_OBJECT_PTR);
   LOCALE void                    MultifieldPrognFunction(void *,DATA_OBJECT_PTR);
   LOCALE void                    GetMvPrognField(void *,DATA_OBJECT_PTR);
   LOCALE long                    GetMvPrognIndex(void *);
   LOCALE BOOLEAN                 FindDOsInSegment(DATA_OBJECT_PTR,int,DATA_OBJECT_PTR,
                                                   long *,long *,long *,int);
   LOCALE void                   *ImplodeMultifield(void *,DATA_OBJECT *);
#endif
   LOCALE int                     ReplaceMultiValueField(void *,struct dataObject *,
                                                         struct dataObject *,
                                                         long,
                                                         long,struct dataObject *,char *);
   LOCALE int                     InsertMultiValueField(void *,struct dataObject *,
                                                        struct dataObject *,
                                                        long,struct dataObject *,char *);
   LOCALE int                     DeleteMultiValueField(void *,struct dataObject *,struct dataObject *,
                                                        long,long,char *);

#endif

