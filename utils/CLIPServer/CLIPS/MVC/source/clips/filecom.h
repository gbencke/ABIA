   /*******************************************************/
   /*      "C" Language Integrated Production System      */
   /*                                                     */
   /*             CLIPS Version 6.20  01/31/02            */
   /*                                                     */
   /*              FILE COMMANDS HEADER FILE              */
   /*******************************************************/

/*************************************************************/
/* Purpose: Contains the code for file commands including    */
/*   batch, dribble-on, dribble-off, save, load, bsave, and  */
/*   bload.                                                  */
/*                                                           */
/* Principal Programmer(s):                                  */
/*      Gary D. Riley                                        */
/*                                                           */
/* Contributing Programmer(s):                               */
/*                                                           */
/* Revision History:                                         */
/*                                                           */
/*************************************************************/

#ifndef _H_filecom

#define _H_filecom

#ifdef LOCALE
#undef LOCALE
#endif

#ifdef _FILECOM_SOURCE_
#define LOCALE
#else
#define LOCALE extern
#endif

#if ENVIRONMENT_API_ONLY
#define DribbleActive(theEnv) EnvDribbleActive(theEnv)
#define DribbleOn(theEnv,a) EnvDribbleOn(theEnv,a)
#define DribbleOff(theEnv) EnvDribbleOff(theEnv)
#else
#define DribbleActive() EnvDribbleActive(GetCurrentEnvironment())
#define DribbleOn(a) EnvDribbleOn(GetCurrentEnvironment(),a)
#define DribbleOff() EnvDribbleOff(GetCurrentEnvironment())
#endif

   LOCALE void                           FileCommandDefinitions(void *);
   LOCALE BOOLEAN                        EnvDribbleOn(void *,char *);
   LOCALE BOOLEAN                        EnvDribbleActive(void *);
   LOCALE BOOLEAN                        EnvDribbleOff(void *);
   LOCALE void                           SetDribbleStatusFunction(void *,int (*)(int));
   LOCALE int                            LLGetcBatch(void *,char *,int);
   LOCALE int                            Batch(void *,char *);
   LOCALE int                            OpenBatch(void *,char *,int);
   LOCALE int                            OpenStringBatch(void *,char *,char *,int);
   LOCALE int                            RemoveBatch(void *);
   LOCALE BOOLEAN                        BatchActive(void *);
   LOCALE void                           CloseAllBatchSources(void *);
   LOCALE int                            BatchCommand(void *);
   LOCALE int                            BatchStarCommand(void *);
   LOCALE int                            BatchStar(void *,char *);
   LOCALE int                            LoadCommand(void *);
   LOCALE int                            LoadStarCommand(void *);
   LOCALE int                            SaveCommand(void *);
   LOCALE int                            DribbleOnCommand(void *);
   LOCALE int                            DribbleOffCommand(void *);

#endif






