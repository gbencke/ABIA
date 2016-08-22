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

#ifndef _H_insfile
#define _H_insfile

#ifndef _H_expressn
#include "expressn.h"
#endif

#define INSTANCE_FILE_DATA 30

struct instanceFileData
  { 
#if BLOAD_INSTANCES || BSAVE_INSTANCES
   char *InstanceBinaryPrefixID;
   char *InstanceBinaryVersionID;
   unsigned long BinaryInstanceFileSize;

#if BLOAD_INSTANCES
   unsigned long BinaryInstanceFileOffset;
   char *CurrentReadBuffer;
   unsigned long CurrentReadBufferSize;
   unsigned long CurrentReadBufferOffset;
#endif

#endif
  };

#define InstanceFileData(theEnv) ((struct instanceFileData *) GetEnvironmentData(theEnv,INSTANCE_FILE_DATA))


#ifdef LOCALE
#undef LOCALE
#endif

#ifdef _INSFILE_SOURCE_
#define LOCALE
#else
#define LOCALE extern
#endif

#if ENVIRONMENT_API_ONLY
#define BinaryLoadInstances(theEnv,a) EnvBinaryLoadInstances(theEnv,a)
#define BinarySaveInstances(theEnv,a,b,c,d) EnvBinarySaveInstances(theEnv,a,b,c,d)
#define LoadInstances(theEnv,a) EnvLoadInstances(theEnv,a)
#define RestoreInstances(theEnv,a) EnvRestoreInstances(theEnv,a)
#define SaveInstances(theEnv,a,b,c,d) EnvSaveInstances(theEnv,a,b,c,d)
#else
#define BinaryLoadInstances(a) EnvBinaryLoadInstances(GetCurrentEnvironment(),a)
#define BinarySaveInstances(a,b,c,d) EnvBinarySaveInstances(GetCurrentEnvironment(),a,b,c,d)
#define LoadInstances(a) EnvLoadInstances(GetCurrentEnvironment(),a)
#define RestoreInstances(a) EnvRestoreInstances(GetCurrentEnvironment(),a)
#define SaveInstances(a,b,c,d) EnvSaveInstances(GetCurrentEnvironment(),a,b,c,d)
#endif

LOCALE void SetupInstanceFileCommands(void *);

LOCALE long SaveInstancesCommand(void *);
LOCALE long LoadInstancesCommand(void *);
LOCALE long RestoreInstancesCommand(void *);
LOCALE long EnvSaveInstances(void *,char *,int,EXPRESSION *,BOOLEAN);

#if BSAVE_INSTANCES
LOCALE long BinarySaveInstancesCommand(void *);
LOCALE long EnvBinarySaveInstances(void *,char *,int,EXPRESSION *,BOOLEAN);
#endif

#if BLOAD_INSTANCES
LOCALE long BinaryLoadInstancesCommand(void *);
LOCALE long EnvBinaryLoadInstances(void *,char *);
#endif

LOCALE long EnvLoadInstances(void *,char *);
LOCALE long LoadInstancesFromString(void *,char *,unsigned);
LOCALE long EnvRestoreInstances(void *,char *);
LOCALE long RestoreInstancesFromString(void *,char *,unsigned);

#ifndef _INSFILE_SOURCE_
#endif

#endif



