   /*******************************************************/
   /*      "C" Language Integrated Production System      */
   /*                                                     */
   /*             CLIPS Version 6.20  01/31/02            */
   /*                                                     */
   /*                    ENGINE MODULE                    */
   /*******************************************************/

/*************************************************************/
/* Purpose: Provides functionality primarily associated with */
/*   the run and focus commands.                             */
/*                                                           */
/* Principal Programmer(s):                                  */
/*      Gary D. Riley                                        */
/*                                                           */
/* Contributing Programmer(s):                               */
/*      Bebe Ly                                              */
/*      Brian L. Donnell                                     */
/*                                                           */
/* Revision History:                                         */
/*                                                           */
/*************************************************************/

#define _ENGINE_SOURCE_

#include <stdio.h>
#define _STDIO_INCLUDED_
#include <string.h>

#include "setup.h"

#if DEFRULE_CONSTRUCT

#include "agenda.h"
#include "argacces.h"
#include "constant.h"
#include "envrnmnt.h"
#include "factmngr.h"
#include "inscom.h"
#include "memalloc.h"
#include "modulutl.h"
#include "prccode.h"
#include "prcdrfun.h"
#include "proflfun.h"
#include "reteutil.h"
#include "retract.h"
#include "router.h"
#include "ruledlt.h"
#include "sysdep.h"
#include "utility.h"
#include "watch.h"

#include "engine.h"

/***************************************/
/* LOCAL INTERNAL FUNCTION DEFINITIONS */
/***************************************/

   static struct activation      *NextActivationToFire(void *);
   static struct defmodule       *RemoveFocus(void *,struct defmodule *);
   static void                    DeallocateEngineData(void *);

/*****************************************************************************/
/* InitializeEngine: Initializes the activations and statistics watch items. */
/*****************************************************************************/
globle void InitializeEngine(
  void *theEnv)
  {   
   AllocateEnvironmentData(theEnv,ENGINE_DATA,sizeof(struct engineData),DeallocateEngineData);

   EngineData(theEnv)->IncrementalResetFlag = TRUE;
   
#if DEBUGGING_FUNCTIONS
   AddWatchItem(theEnv,"statistics",0,&EngineData(theEnv)->WatchStatistics,20,NULL,NULL);
   AddWatchItem(theEnv,"focus",0,&EngineData(theEnv)->WatchFocus,0,NULL,NULL);
#endif
  }
  
/*************************************************/
/* DeallocateEngineData: Deallocates environment */
/*    data for engine functionality.             */
/*************************************************/
static void DeallocateEngineData(
  void *theEnv)
  {
   struct focus *tmpPtr, *nextPtr;
   
   DeallocateCallList(theEnv,EngineData(theEnv)->ListOfRunFunctions);

   tmpPtr = EngineData(theEnv)->CurrentFocus;
   while (tmpPtr != NULL)
     {
      nextPtr = tmpPtr->next;
      rtn_struct(theEnv,focus,tmpPtr);
      tmpPtr = nextPtr;
     }
  }

/*************************************************/
/* EnvRun: C access routine for the run command. */
/*************************************************/
globle long int EnvRun(
  void *theEnv,
  long int runLimit)
  {
   long int rulesFired = 0;
   DATA_OBJECT result;
   struct callFunctionItem *theRunFunction;
#if DEBUGGING_FUNCTIONS
   unsigned long maxActivations = 0, sumActivations = 0;
#if DEFTEMPLATE_CONSTRUCT
   unsigned long maxFacts = 0, sumFacts = 0;
#endif
#if OBJECT_SYSTEM
   unsigned long maxInstances = 0, sumInstances = 0;
#endif
   double endTime, startTime = 0.0;
   unsigned long tempValue;
#endif
   unsigned int i;
   struct patternEntity *theMatchingItem;
   struct partialMatch *theBasis;
   ACTIVATION *theActivation;
   char *ruleFiring;
#if PROFILING_FUNCTIONS
   struct profileFrameInfo profileFrame;
#endif

   /*=====================================================*/
   /* Make sure the run command is not already executing. */
   /*=====================================================*/

   if (EngineData(theEnv)->AlreadyRunning) return(0);
   EngineData(theEnv)->AlreadyRunning = TRUE;

   /*================================*/
   /* Set up statistics information. */
   /*================================*/

#if DEBUGGING_FUNCTIONS
   if (EngineData(theEnv)->WatchStatistics)
     {
#if DEFTEMPLATE_CONSTRUCT
      maxFacts = GetNumberOfFacts(theEnv);
      sumFacts = maxFacts;
#endif
#if OBJECT_SYSTEM
      maxInstances = GetGlobalNumberOfInstances(theEnv);
      sumInstances = maxInstances;
#endif
      maxActivations = GetNumberOfActivations(theEnv);
      sumActivations = maxActivations;
      startTime = gentime();
     }
#endif

   /*=============================*/
   /* Set up execution variables. */
   /*=============================*/

   if (EvaluationData(theEnv)->CurrentEvaluationDepth == 0) SetHaltExecution(theEnv,FALSE);
   EngineData(theEnv)->HaltRules = FALSE;

   /*=====================================================*/
   /* Fire rules until the agenda is empty, the run limit */
   /* has been reached, or a rule execution error occurs. */
   /*=====================================================*/

   theActivation = NextActivationToFire(theEnv);
   while ((theActivation != NULL) &&
          (runLimit != 0) &&
          (EvaluationData(theEnv)->HaltExecution == FALSE) &&
          (EngineData(theEnv)->HaltRules == FALSE))
     {
      /*===========================================*/
      /* Detach the activation from the agenda and */
      /* determine which rule is firing.           */
      /*===========================================*/

      DetachActivation(theEnv,theActivation);
      ruleFiring = EnvGetActivationName(theEnv,theActivation);
      theBasis = (struct partialMatch *) GetActivationBasis(theActivation);
      EngineData(theEnv)->ExecutingRule = (struct defrule *) GetActivationRule(theActivation);

      /*=============================================*/
      /* Update the number of rules that have fired. */
      /*=============================================*/

      rulesFired++;
      if (runLimit > 0) { runLimit--; }

      /*==================================*/
      /* If rules are being watched, then */
      /* print an information message.    */
      /*==================================*/

#if DEBUGGING_FUNCTIONS
      if (EngineData(theEnv)->ExecutingRule->watchFiring)
        {
         char printSpace[60];

         sprintf(printSpace,"FIRE %4ld ",rulesFired);
         EnvPrintRouter(theEnv,WTRACE,printSpace);
         EnvPrintRouter(theEnv,WTRACE,ruleFiring);
         EnvPrintRouter(theEnv,WTRACE,": ");
         PrintPartialMatch(theEnv,WTRACE,theBasis);
         EnvPrintRouter(theEnv,WTRACE,"\n");
        }
#endif

      /*=================================================*/
      /* Remove the link between the activation and the  */
      /* completed match for the rule. Set the busy flag */
      /* for the completed match to TRUE (so the match   */
      /* upon which our RHS variables are dependent is   */
      /* not deleted while our rule is firing). Set up   */
      /* the global pointers to the completed match for  */
      /* routines which do variable extractions.         */
      /*=================================================*/

      theBasis->binds[theBasis->bcount].gm.theValue = NULL;
      theBasis->busy = TRUE;

      EngineData(theEnv)->GlobalLHSBinds = theBasis;
      EngineData(theEnv)->GlobalRHSBinds = NULL;

      /*===================================================================*/
      /* Increment the count for each of the facts/objects associated with */
      /* the rule activation so that the facts/objects cannot be deleted   */
      /* by garbage collection while the rule is executing.                */
      /*===================================================================*/

      for (i = 0; i < theBasis->bcount; i++)
        {
         theMatchingItem = theBasis->binds[i].gm.theMatch->matchingItem;
         if (theMatchingItem != NULL)
           { (*theMatchingItem->theInfo->incrementBasisCount)(theEnv,theMatchingItem); }
        }

      /*====================================================*/
      /* Execute the rule's right hand side actions. If the */
      /* rule has logical CEs, set up the pointer to the    */
      /* rules logical join so the assert command will      */
      /* attach the appropriate dependencies to the facts.  */
      /*====================================================*/

#if LOGICAL_DEPENDENCIES
      EngineData(theEnv)->TheLogicalJoin = EngineData(theEnv)->ExecutingRule->logicalJoin;
#endif
      EvaluationData(theEnv)->CurrentEvaluationDepth++;
      SetEvaluationError(theEnv,FALSE);
      EngineData(theEnv)->ExecutingRule->executing = TRUE;

#if PROFILING_FUNCTIONS
      StartProfile(theEnv,&profileFrame,
                   &EngineData(theEnv)->ExecutingRule->header.usrData,
                   ProfileFunctionData(theEnv)->ProfileConstructs);
#endif

      EvaluateProcActions(theEnv,EngineData(theEnv)->ExecutingRule->header.whichModule->theModule,
                          EngineData(theEnv)->ExecutingRule->actions,EngineData(theEnv)->ExecutingRule->localVarCnt,
                          &result,NULL);

#if PROFILING_FUNCTIONS
      EndProfile(theEnv,&profileFrame);
#endif

      EngineData(theEnv)->ExecutingRule->executing = FALSE;
      SetEvaluationError(theEnv,FALSE);
      EvaluationData(theEnv)->CurrentEvaluationDepth--;
#if LOGICAL_DEPENDENCIES
      EngineData(theEnv)->TheLogicalJoin = NULL;
#endif

      /*=====================================================*/
      /* If rule execution was halted, then print a message. */
      /*=====================================================*/

#if DEBUGGING_FUNCTIONS
      if ((EvaluationData(theEnv)->HaltExecution) || (EngineData(theEnv)->HaltRules && EngineData(theEnv)->ExecutingRule->watchFiring))
#else
      if ((EvaluationData(theEnv)->HaltExecution) || (EngineData(theEnv)->HaltRules))
#endif

        {
         PrintErrorID(theEnv,"PRCCODE",4,FALSE);
         EnvPrintRouter(theEnv,WERROR,"Execution halted during the actions of defrule ");
         EnvPrintRouter(theEnv,WERROR,ruleFiring);
         EnvPrintRouter(theEnv,WERROR,".\n");
        }

      /*===================================================================*/
      /* Decrement the count for each of the facts/objects associated with */
      /* the rule activation. If the last match for the activation         */
      /* is from a not CE, then we need to make sure that the last         */
      /* match is an actual match for the CE and not a counter.            */
      /*===================================================================*/

      theBasis->busy = FALSE;

      for (i = 0; i < (theBasis->bcount - 1); i++)
        {
         theMatchingItem = theBasis->binds[i].gm.theMatch->matchingItem;
         if (theMatchingItem != NULL)
           { (*theMatchingItem->theInfo->decrementBasisCount)(theEnv,theMatchingItem); }
        }

      i = (unsigned) (theBasis->bcount - 1);
      if (theBasis->counterf == FALSE)
        {
         theMatchingItem = theBasis->binds[i].gm.theMatch->matchingItem;
         if (theMatchingItem != NULL)
           { (*theMatchingItem->theInfo->decrementBasisCount)(theEnv,theMatchingItem); }
        }

      /*========================================*/
      /* Return the agenda node to free memory. */
      /*========================================*/

      RemoveActivation(theEnv,theActivation,FALSE,FALSE);

      /*======================================*/
      /* Get rid of partial matches discarded */
      /* while executing the rule's RHS.      */
      /*======================================*/

      FlushGarbagePartialMatches(theEnv);

      /*==================================*/
      /* Get rid of other garbage created */
      /* while executing the rule's RHS.  */
      /*==================================*/

      PeriodicCleanup(theEnv,FALSE,TRUE);

      /*==========================*/
      /* Keep up with statistics. */
      /*==========================*/

#if DEBUGGING_FUNCTIONS
      if (EngineData(theEnv)->WatchStatistics)
        {
#if DEFTEMPLATE_CONSTRUCT
         tempValue = GetNumberOfFacts(theEnv);
         if (tempValue > maxFacts) maxFacts = tempValue;
         sumFacts += tempValue;
#endif
#if OBJECT_SYSTEM
         tempValue = GetGlobalNumberOfInstances(theEnv);
         if (tempValue > maxInstances) maxInstances = tempValue;
         sumInstances += tempValue;
#endif
         tempValue = GetNumberOfActivations(theEnv);
         if (tempValue > maxActivations) maxActivations = tempValue;
         sumActivations += tempValue;
        }
#endif

      /*==================================*/
      /* Update saliences if appropriate. */
      /*==================================*/

#if DYNAMIC_SALIENCE
      if (EnvGetSalienceEvaluation(theEnv) == EVERY_CYCLE) EnvRefreshAgenda(theEnv,NULL);
#endif

      /*========================================*/
      /* Execute the list of functions that are */
      /* to be called after each rule firing.   */
      /*========================================*/

      for (theRunFunction = EngineData(theEnv)->ListOfRunFunctions;
           theRunFunction != NULL;
           theRunFunction = theRunFunction->next)
        { 
         if (theRunFunction->environmentAware)
           { (*theRunFunction->func)(theEnv); }
         else            
           { ((void (*)(void))(*theRunFunction->func))(); }
        }

      /*========================================*/
      /* If a return was issued on the RHS of a */
      /* rule, then remove *that* rule's module */
      /* from the focus stack                   */
      /*========================================*/

      if (ProcedureFunctionData(theEnv)->ReturnFlag == TRUE)
        { RemoveFocus(theEnv,EngineData(theEnv)->ExecutingRule->header.whichModule->theModule); }
      ProcedureFunctionData(theEnv)->ReturnFlag = FALSE;

      /*========================================*/
      /* Determine the next activation to fire. */
      /*========================================*/

      theActivation = (struct activation *) NextActivationToFire(theEnv);

      /*==============================*/
      /* Check for a rule breakpoint. */
      /*==============================*/

      if (theActivation != NULL)
        {
         if (((struct defrule *) GetActivationRule(theActivation))->afterBreakpoint)
           {
            EngineData(theEnv)->HaltRules = TRUE;
            EnvPrintRouter(theEnv,WDIALOG,"Breaking on rule ");
            EnvPrintRouter(theEnv,WDIALOG,EnvGetActivationName(theEnv,theActivation));
            EnvPrintRouter(theEnv,WDIALOG,".\n");
           }
        }
     }

   /*=====================================================*/
   /* Make sure run functions are executed at least once. */
   /*=====================================================*/

   if (rulesFired == 0)
     {
      for (theRunFunction = EngineData(theEnv)->ListOfRunFunctions;
           theRunFunction != NULL;
           theRunFunction = theRunFunction->next)
        { 
         if (theRunFunction->environmentAware)
           { (*theRunFunction->func)(theEnv); }
         else            
           { ((void (*)(void))(*theRunFunction->func))(); }
        }
     }

   /*======================================================*/
   /* If rule execution was halted because the rule firing */
   /* limit was reached, then print a message.             */
   /*======================================================*/

   if (runLimit == rulesFired)
     { EnvPrintRouter(theEnv,WDIALOG,"rule firing limit reached\n"); }

   /*==============================*/
   /* Restore execution variables. */
   /*==============================*/

   EngineData(theEnv)->ExecutingRule = NULL;
   EngineData(theEnv)->HaltRules = FALSE;

   /*=================================================*/
   /* Print out statistics if they are being watched. */
   /*=================================================*/

#if DEBUGGING_FUNCTIONS
   if (EngineData(theEnv)->WatchStatistics)
     {
      char printSpace[60];

      endTime = gentime();

      PrintLongInteger(theEnv,WDIALOG,rulesFired);
      EnvPrintRouter(theEnv,WDIALOG," rules fired");

#if (! GENERIC)
      if (startTime != endTime)
        {
         EnvPrintRouter(theEnv,WDIALOG,"        Run time is ");
         PrintFloat(theEnv,WDIALOG,endTime - startTime);
         EnvPrintRouter(theEnv,WDIALOG," seconds.\n");
         PrintFloat(theEnv,WDIALOG,(double) rulesFired / (endTime - startTime));
         EnvPrintRouter(theEnv,WDIALOG," rules per second.\n");
        }
      else
        { EnvPrintRouter(theEnv,WDIALOG,"\n"); }
#endif

#if DEFTEMPLATE_CONSTRUCT
      sprintf(printSpace,"%ld mean number of facts (%ld maximum).\n",
                          (long) (((double) sumFacts / (rulesFired + 1)) + 0.5),
                          maxFacts);
      EnvPrintRouter(theEnv,WDIALOG,printSpace);
#endif

#if OBJECT_SYSTEM
      sprintf(printSpace,"%ld mean number of instances (%ld maximum).\n",
                          (long) (((double) sumInstances / (rulesFired + 1)) + 0.5),
                          maxInstances);
      EnvPrintRouter(theEnv,WDIALOG,printSpace);
#endif

      sprintf(printSpace,"%ld mean number of activations (%ld maximum).\n",
                          (long) (((double) sumActivations / (rulesFired + 1)) + 0.5),
                          maxActivations);
      EnvPrintRouter(theEnv,WDIALOG,printSpace);
     }
#endif

   /*==========================================*/
   /* The current module should be the current */
   /* focus when the run finishes.             */
   /*==========================================*/

   if (EngineData(theEnv)->CurrentFocus != NULL)
     {
      if (EngineData(theEnv)->CurrentFocus->theModule != ((struct defmodule *) EnvGetCurrentModule(theEnv)))
        { EnvSetCurrentModule(theEnv,(void *) EngineData(theEnv)->CurrentFocus->theModule); }
     }

   /*===================================*/
   /* Return the number of rules fired. */
   /*===================================*/

   EngineData(theEnv)->AlreadyRunning = FALSE;
   return(rulesFired);
  }

/***********************************************************/
/* NextActivationToFire: Returns the next activation which */
/*   should be executed based on the current focus.        */
/***********************************************************/
static struct activation *NextActivationToFire(
  void *theEnv)
  {
   struct activation *theActivation;
   struct defmodule *theModule;

   /*====================================*/
   /* If there is no current focus, then */
   /* focus on the MAIN module.          */
   /*====================================*/

   if (EngineData(theEnv)->CurrentFocus == NULL)
     {
      theModule = (struct defmodule *) EnvFindDefmodule(theEnv,"MAIN");
      EnvFocus(theEnv,theModule);
     }

   /*===========================================================*/
   /* Determine the top activation on the agenda of the current */
   /* focus. If the current focus has no activations on its     */
   /* agenda, then pop the focus off the focus stack until      */
   /* a focus that has an activation on its agenda is found.    */
   /*===========================================================*/

   theActivation = EngineData(theEnv)->CurrentFocus->theDefruleModule->agenda;
   while ((theActivation == NULL) && (EngineData(theEnv)->CurrentFocus != NULL))
     {
      if (EngineData(theEnv)->CurrentFocus != NULL) EnvPopFocus(theEnv);
      if (EngineData(theEnv)->CurrentFocus != NULL) theActivation = EngineData(theEnv)->CurrentFocus->theDefruleModule->agenda;
     }

   /*=========================================*/
   /* Return the next activation to be fired. */
   /*=========================================*/

   return(theActivation);
  }

/***************************************************/
/* RemoveFocus: Removes the first occurence of the */
/*   specified module from the focus stack.        */
/***************************************************/
static struct defmodule *RemoveFocus(
  void *theEnv,
  struct defmodule *theModule)
  {
   struct focus *tempFocus,*prevFocus, *nextFocus;
   int found = FALSE;
   int currentFocusRemoved = FALSE;

   /*====================================*/
   /* Return NULL if there is nothing on */
   /* the focus stack to remove.         */
   /*====================================*/

   if (EngineData(theEnv)->CurrentFocus == NULL) return(NULL);

   /*=============================================*/
   /* Remove the first occurence of the specified */
   /* module from the focus stack.                */
   /*=============================================*/

   prevFocus = NULL;
   tempFocus = EngineData(theEnv)->CurrentFocus;
   while ((tempFocus != NULL) && (! found))
     {
      if (tempFocus->theModule == theModule)
        {
         found = TRUE;

         nextFocus = tempFocus->next;
         rtn_struct(theEnv,focus,tempFocus);
         tempFocus = nextFocus;

         if (prevFocus == NULL)
           {
            currentFocusRemoved = TRUE;
            EngineData(theEnv)->CurrentFocus = tempFocus;
           }
         else
           { prevFocus->next = tempFocus; }
        }
      else
        {
         prevFocus = tempFocus;
         tempFocus = tempFocus->next;
        }
     }

   /*=========================================*/
   /* If the given module is not in the focus */
   /* stack, simply return the current focus  */
   /*=========================================*/

   if (! found) return(EngineData(theEnv)->CurrentFocus->theModule);

   /*========================================*/
   /* If the current focus is being watched, */
   /* then print an informational message.   */
   /*========================================*/

#if DEBUGGING_FUNCTIONS
   if (EngineData(theEnv)->WatchFocus)
     {
      EnvPrintRouter(theEnv,WTRACE,"<== Focus ");
      EnvPrintRouter(theEnv,WTRACE,ValueToString(theModule->name));

      if ((EngineData(theEnv)->CurrentFocus != NULL) && currentFocusRemoved)
        {
         EnvPrintRouter(theEnv,WTRACE," to ");
         EnvPrintRouter(theEnv,WTRACE,ValueToString(EngineData(theEnv)->CurrentFocus->theModule->name));
        }

      EnvPrintRouter(theEnv,WTRACE,"\n");
     }
#endif

   /*======================================================*/
   /* Set the current module to the module associated with */
   /* the current focus (if it changed) and set a boolean  */
   /* flag indicating that the focus has changed.          */
   /*======================================================*/

   if ((EngineData(theEnv)->CurrentFocus != NULL) && currentFocusRemoved)
     { EnvSetCurrentModule(theEnv,(void *) EngineData(theEnv)->CurrentFocus->theModule); }
   EngineData(theEnv)->FocusChanged = TRUE;

   /*====================================*/
   /* Return the module that was removed */
   /* from the focus stack.              */
   /*====================================*/

   return(theModule);
  }

/*************************************************************/
/* EnvPopFocus: C access routine for the pop-focus function. */
/*************************************************************/
globle void *EnvPopFocus(
  void *theEnv)
  {
   if (EngineData(theEnv)->CurrentFocus == NULL) return(NULL);
   return((void *) RemoveFocus(theEnv,EngineData(theEnv)->CurrentFocus->theModule));
  }

/*************************************************************/
/* GetNextFocus: Returns the next focus on the focus stack. */
/*************************************************************/
globle void *GetNextFocus(
  void *theEnv,
  void *theFocus)
  {
   /*==================================================*/
   /* If NULL is passed as an argument, return the top */
   /* focus on the focus stack (the current focus).    */
   /*==================================================*/

   if (theFocus == NULL) return((void *) EngineData(theEnv)->CurrentFocus);

   /*=======================================*/
   /* Otherwise, return the focus following */
   /* the focus passed as an argument.      */
   /*=======================================*/

   return((void *) ((struct focus *) theFocus)->next);
  }

/******************************************************/
/* EnvFocus: C access routine for the focus function. */
/******************************************************/
globle void EnvFocus(
  void *theEnv,
  void *vTheModule)
  {
   struct defmodule *theModule = (struct defmodule *) vTheModule;
   struct focus *tempFocus;

   /*==================================================*/
   /* Make the specified module be the current module. */
   /* If the specified module is the current focus,    */
   /* then no further action is needed.                */
   /*==================================================*/

   EnvSetCurrentModule(theEnv,(void *) theModule);
   if (EngineData(theEnv)->CurrentFocus != NULL)
     { if (EngineData(theEnv)->CurrentFocus->theModule == theModule) return; }

   /*=====================================*/
   /* If the focus is being watched, then */
   /* print an information message.       */
   /*=====================================*/

#if DEBUGGING_FUNCTIONS
   if (EngineData(theEnv)->WatchFocus)
     {
      EnvPrintRouter(theEnv,WTRACE,"==> Focus ");
      EnvPrintRouter(theEnv,WTRACE,ValueToString(theModule->name));
      if (EngineData(theEnv)->CurrentFocus != NULL)
        {
         EnvPrintRouter(theEnv,WTRACE," from ");
         EnvPrintRouter(theEnv,WTRACE,ValueToString(EngineData(theEnv)->CurrentFocus->theModule->name));
        }
      EnvPrintRouter(theEnv,WTRACE,"\n");
     }
#endif

   /*=======================================*/
   /* Add the new focus to the focus stack. */
   /*=======================================*/

   tempFocus = get_struct(theEnv,focus);
   tempFocus->theModule = theModule;
   tempFocus->theDefruleModule = GetDefruleModuleItem(theEnv,theModule);
   tempFocus->next = EngineData(theEnv)->CurrentFocus;
   EngineData(theEnv)->CurrentFocus = tempFocus;
   EngineData(theEnv)->FocusChanged = TRUE;
  }

/************************************************/
/* ClearFocusStackCommand: H/L access routine   */
/*   for the clear-focus-stack command.         */
/************************************************/
globle void ClearFocusStackCommand(
  void *theEnv)
  {
   if (EnvArgCountCheck(theEnv,"list-focus-stack",EXACTLY,0) == -1) return;

   EnvClearFocusStack(theEnv);
  }

/****************************************/
/* EnvClearFocusStack: C access routine */
/*   for the clear-focus-stack command. */
/****************************************/
globle void EnvClearFocusStack(
  void *theEnv)
  {
   while (EngineData(theEnv)->CurrentFocus != NULL) EnvPopFocus(theEnv);

   EngineData(theEnv)->FocusChanged = TRUE;
  }

#if (! ENVIRONMENT_API_ONLY) && ALLOW_ENVIRONMENT_GLOBALS
/***********************************/
/* AddRunFunction: Adds a function */
/*   to the ListOfRunFunctions.    */
/***********************************/
globle BOOLEAN AddRunFunction(
  char *name,
  void (*functionPtr)(void),
  int priority)
  {
   void *theEnv;
   
   theEnv = GetCurrentEnvironment();

   EngineData(theEnv)->ListOfRunFunctions = 
       AddFunctionToCallList(theEnv,name,priority,(void (*)(void *)) functionPtr,
                             EngineData(theEnv)->ListOfRunFunctions,TRUE);
   return(1);
  }
#endif

/**************************************/
/* EnvAddRunFunction: Adds a function */
/*   to the ListOfRunFunctions.       */
/**************************************/
globle BOOLEAN EnvAddRunFunction(
  void *theEnv,
  char *name,
  void (*functionPtr)(void *),
  int priority)
  {
   EngineData(theEnv)->ListOfRunFunctions = AddFunctionToCallList(theEnv,name,priority,
                                              functionPtr,
                                              EngineData(theEnv)->ListOfRunFunctions,TRUE);
   return(1);
  }

/********************************************/
/* EnvRemoveRunFunction: Removes a function */
/*   from the ListOfRunFunctions.           */
/********************************************/
globle BOOLEAN EnvRemoveRunFunction(
  void *theEnv,
  char *name)
  {
   int found;

   EngineData(theEnv)->ListOfRunFunctions = 
      RemoveFunctionFromCallList(theEnv,name,EngineData(theEnv)->ListOfRunFunctions,&found);

   if (found) return(TRUE);

   return(FALSE);
  }

/*********************************************************/
/* RunCommand: H/L access routine for the run command.   */
/*********************************************************/
globle void RunCommand(
  void *theEnv)
  {
   int numArgs;
   long int runLimit = -1;
   DATA_OBJECT argPtr;

   if ((numArgs = EnvArgCountCheck(theEnv,"run",NO_MORE_THAN,1)) == -1) return;

   if (numArgs == 0)
     { runLimit = -1; }
   else if (numArgs == 1)
     {
      if (EnvArgTypeCheck(theEnv,"run",1,INTEGER,&argPtr) == FALSE) return;
      runLimit = DOToLong(argPtr);
     }

   EnvRun(theEnv,runLimit);

   return;
  }

/***********************************************/
/* HaltCommand: Causes rule execution to halt. */
/***********************************************/
globle void HaltCommand(
  void *theEnv)
  {
   EnvArgCountCheck(theEnv,"halt",EXACTLY,0);
   EngineData(theEnv)->HaltRules = TRUE;
  }

#if DEBUGGING_FUNCTIONS

/*********************************/
/* EnvSetBreak: C access routine */
/*   for the set-break command.  */
/*********************************/
#if IBM_TBC
#pragma argsused
#endif
globle void EnvSetBreak(
  void *theEnv,
  void *theRule)
  {
#if MAC_MPW || MAC_MCW || IBM_MCW
#pragma unused(theEnv)
#endif
   struct defrule *thePtr;

   for (thePtr = (struct defrule *) theRule;
        thePtr != NULL;
        thePtr = thePtr->disjunct)
     { thePtr->afterBreakpoint = 1; }
  }

/************************************/
/* EnvRemoveBreak: C access routine */
/*   for the remove-break command.  */
/************************************/
#if IBM_TBC
#pragma argsused
#endif
globle BOOLEAN EnvRemoveBreak(
  void *theEnv,
  void *theRule)
  {
#if MAC_MPW || MAC_MCW || IBM_MCW
#pragma unused(theEnv)
#endif
   struct defrule *thePtr;
   int rv = FALSE;

   for (thePtr = (struct defrule *) theRule;
        thePtr != NULL;
        thePtr = thePtr->disjunct)
     {
      if (thePtr->afterBreakpoint == 1)
        {
         thePtr->afterBreakpoint = 0;
         rv = TRUE;
        }
     }

   return(rv);
  }

/**************************************************/
/* RemoveAllBreakpoints: Removes all breakpoints. */
/**************************************************/
globle void RemoveAllBreakpoints(
  void *theEnv)
  {
   void *theRule;
   void *theDefmodule = NULL;

   while ((theDefmodule = EnvGetNextDefmodule(theEnv,theDefmodule)) != NULL)
     {
      theRule = NULL;
      while ((theRule = EnvGetNextDefrule(theEnv,theRule)) != NULL)
        { EnvRemoveBreak(theEnv,theRule); }
     }
  }

/***********************************/
/* EnvShowBreaks: C access routine */
/*   for the show-breaks command.  */
/***********************************/
globle void EnvShowBreaks(
  void *theEnv,
  char *logicalName,
  void *vTheModule)
  {
   ListItemsDriver(theEnv,logicalName,(struct defmodule *) vTheModule,
                   NULL,NULL,
                   EnvGetNextDefrule,(char *(*)(void *)) GetConstructNameString,
                   NULL,EnvDefruleHasBreakpoint);
   }

/**********************************************/
/* EnvDefruleHasBreakpoint: Indicates whether */
/*   the specified rule has a breakpoint set. */
/**********************************************/
#if IBM_TBC
#pragma argsused
#endif
globle BOOLEAN EnvDefruleHasBreakpoint(
  void *theEnv,
  void *theRule)
  {
#if MAC_MPW || MAC_MCW || IBM_MCW
#pragma unused(theEnv)
#endif

   return(((struct defrule *) theRule)->afterBreakpoint);
  }

/*****************************************/
/* SetBreakCommand: H/L access routine   */
/*   for the set-break command.          */
/*****************************************/
globle void SetBreakCommand(
  void *theEnv)
  {
   DATA_OBJECT argPtr;
   char *argument;
   void *defrulePtr;

   if (EnvArgCountCheck(theEnv,"set-break",EXACTLY,1) == -1) return;

   if (EnvArgTypeCheck(theEnv,"set-break",1,SYMBOL,&argPtr) == FALSE) return;

   argument = DOToString(argPtr);

   if ((defrulePtr = EnvFindDefrule(theEnv,argument)) == NULL)
     {
      CantFindItemErrorMessage(theEnv,"defrule",argument);
      return;
     }

   EnvSetBreak(theEnv,defrulePtr);
  }

/********************************************/
/* RemoveBreakCommand: H/L access routine   */
/*   for the remove-break command.          */
/********************************************/
globle void RemoveBreakCommand(
  void *theEnv)
  {
   DATA_OBJECT argPtr;
   char *argument;
   int nargs;
   void *defrulePtr;

   if ((nargs = EnvArgCountCheck(theEnv,"remove-break",NO_MORE_THAN,1)) == -1)
     { return; }

   if (nargs == 0)
     {
      RemoveAllBreakpoints(theEnv);
      return;
     }

   if (EnvArgTypeCheck(theEnv,"remove-break",1,SYMBOL,&argPtr) == FALSE) return;

   argument = DOToString(argPtr);

   if ((defrulePtr = EnvFindDefrule(theEnv,argument)) == NULL)
     {
      CantFindItemErrorMessage(theEnv,"defrule",argument);
      return;
     }

   if (EnvRemoveBreak(theEnv,defrulePtr) == FALSE)
     {
      EnvPrintRouter(theEnv,WERROR,"Rule ");
      EnvPrintRouter(theEnv,WERROR,argument);
      EnvPrintRouter(theEnv,WERROR," does not have a breakpoint set.\n");
     }
  }

/*******************************************/
/* ShowBreaksCommand: H/L access routine   */
/*   for the show-breaks command.          */
/*******************************************/
globle void ShowBreaksCommand(
  void *theEnv)
  {
   int numArgs, error;
   struct defmodule *theModule;

   if ((numArgs = EnvArgCountCheck(theEnv,"show-breaks",NO_MORE_THAN,1)) == -1) return;

   if (numArgs == 1)
     {
      theModule = GetModuleName(theEnv,"show-breaks",1,&error);
      if (error) return;
     }
   else
     { theModule = ((struct defmodule *) EnvGetCurrentModule(theEnv)); }

   EnvShowBreaks(theEnv,WDISPLAY,theModule);
  }

/***********************************************/
/* ListFocusStackCommand: H/L access routine   */
/*   for the list-focus-stack command.         */
/***********************************************/
globle void ListFocusStackCommand(
  void *theEnv)
  {
   if (EnvArgCountCheck(theEnv,"list-focus-stack",EXACTLY,0) == -1) return;

   EnvListFocusStack(theEnv,WDISPLAY);
  }

/***************************************/
/* EnvListFocusStack: C access routine */
/*   for the list-focus-stack command. */
/***************************************/
globle void EnvListFocusStack(
  void *theEnv,
  char *logicalName)
  {
   struct focus *theFocus;

   for (theFocus = EngineData(theEnv)->CurrentFocus;
        theFocus != NULL;
        theFocus = theFocus->next)
     {
      EnvPrintRouter(theEnv,logicalName,EnvGetDefmoduleName(theEnv,theFocus->theModule));
      EnvPrintRouter(theEnv,logicalName,"\n");
     }
  }

#endif

/***********************************************/
/* GetFocusStackFunction: H/L access routine   */
/*   for the get-focus-stack function.         */
/***********************************************/
globle void GetFocusStackFunction(
  void *theEnv,
  DATA_OBJECT_PTR returnValue)
  {
   if (EnvArgCountCheck(theEnv,"get-focus-stack",EXACTLY,0) == -1) return;

   EnvGetFocusStack(theEnv,returnValue);
  }

/***************************************/
/* EnvGetFocusStack: C access routine  */
/*   for the get-focus-stack function. */
/***************************************/
globle void EnvGetFocusStack(
  void *theEnv,
  DATA_OBJECT_PTR returnValue)
  {
   struct focus *theFocus;
   struct multifield *theList;
   unsigned long count = 0;

   /*===========================================*/
   /* If there is no current focus, then return */
   /* a multifield value of length zero.        */
   /*===========================================*/

   if (EngineData(theEnv)->CurrentFocus == NULL)
     {
      SetpType(returnValue,MULTIFIELD);
      SetpDOBegin(returnValue,1);
      SetpDOEnd(returnValue,0);
      SetpValue(returnValue,(void *) EnvCreateMultifield(theEnv,0L));
      return;
     }

   /*=====================================================*/
   /* Determine the number of modules on the focus stack. */
   /*=====================================================*/

   for (theFocus = EngineData(theEnv)->CurrentFocus; theFocus != NULL; theFocus = theFocus->next)
     { count++; }

   /*=============================================*/
   /* Create a multifield of the appropriate size */
   /* in which to store the module names.         */
   /*=============================================*/

   SetpType(returnValue,MULTIFIELD);
   SetpDOBegin(returnValue,1);
   SetpDOEnd(returnValue,(long) count);
   theList = (struct multifield *) EnvCreateMultifield(theEnv,count);
   SetpValue(returnValue,(void *) theList);

   /*=================================================*/
   /* Store the module names in the multifield value. */
   /*=================================================*/

   for (theFocus = EngineData(theEnv)->CurrentFocus, count = 1;
        theFocus != NULL;
        theFocus = theFocus->next, count++)
     {
      SetMFType(theList,count,SYMBOL);
      SetMFValue(theList,count,theFocus->theModule->name);
     }
  }

/******************************************/
/* PopFocusFunction: H/L access routine   */
/*   for the pop-focus function.          */
/******************************************/
globle SYMBOL_HN *PopFocusFunction(
  void *theEnv)
  {
   struct defmodule *theModule;

   EnvArgCountCheck(theEnv,"pop-focus",EXACTLY,0);

   theModule = (struct defmodule *) EnvPopFocus(theEnv);
   if (theModule == NULL) return((SYMBOL_HN *) SymbolData(theEnv)->FalseSymbol);
   return(theModule->name);
  }

/******************************************/
/* GetFocusFunction: H/L access routine   */
/*   for the get-focus function.          */
/******************************************/
globle SYMBOL_HN *GetFocusFunction(
  void *theEnv)
  {
   struct defmodule *rv;

   EnvArgCountCheck(theEnv,"get-focus",EXACTLY,0);
   rv = (struct defmodule *) EnvGetFocus(theEnv);
   if (rv == NULL) return((SYMBOL_HN *) SymbolData(theEnv)->FalseSymbol);
   return(rv->name);
  }

/*********************************/
/* EnvGetFocus: C access routine */
/*   for the get-focus function. */
/*********************************/
globle void *EnvGetFocus(
  void *theEnv)
  {
   if (EngineData(theEnv)->CurrentFocus == NULL) return(NULL);

   return((void *) EngineData(theEnv)->CurrentFocus->theModule);
  }

/**************************************/
/* FocusCommand: H/L access routine   */
/*   for the focus function.          */
/**************************************/
globle int FocusCommand(
  void *theEnv)
  {
   DATA_OBJECT argPtr;
   char *argument;
   struct defmodule *theModule;
   int argCount, i;

   /*=====================================================*/
   /* Check for the correct number and type of arguments. */
   /*=====================================================*/

   if ((argCount = EnvArgCountCheck(theEnv,"focus",AT_LEAST,1)) == -1)
     { return(FALSE); }

   /*===========================================*/
   /* Focus on the specified defrule module(s). */
   /*===========================================*/

   for (i = argCount; i > 0; i--)
     {
      if (EnvArgTypeCheck(theEnv,"focus",i,SYMBOL,&argPtr) == FALSE)
        { return(FALSE); }

      argument = DOToString(argPtr);
      theModule = (struct defmodule *) EnvFindDefmodule(theEnv,argument);

      if (theModule == NULL)
        {
         CantFindItemErrorMessage(theEnv,"defmodule",argument);
         return(FALSE);
        }

      EnvFocus(theEnv,(void *) theModule);
     }

   /*===================================================*/
   /* Return TRUE to indicate success of focus command. */
   /*===================================================*/

   return(TRUE);
  }

/********************************************************************/
/* GetFocusChanged: Returns the value of the variable FocusChanged. */
/********************************************************************/
globle int GetFocusChanged(
  void *theEnv)
  {
   return(EngineData(theEnv)->FocusChanged);
  }

/*****************************************************************/
/* SetFocusChanged: Sets the value of the variable FocusChanged. */
/*****************************************************************/
globle void SetFocusChanged(
  void *theEnv,
  int value)
  {
   EngineData(theEnv)->FocusChanged = value;
  }

#endif /* DEFRULE_CONSTRUCT */

