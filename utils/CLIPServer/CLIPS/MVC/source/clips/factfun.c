   /*******************************************************/
   /*      "C" Language Integrated Production System      */
   /*                                                     */
   /*             CLIPS Version 6.20  01/31/02            */
   /*                                                     */
   /*               FACT FUNCTIONS MODULE                 */
   /*******************************************************/

/*************************************************************/
/* Purpose:                                                  */
/*                                                           */
/*                                                           */
/* (fact-existp <fact-address-or-index>)                     */
/*    Returns TRUE if the fact exists, otherwise FALSE is    */
/*    returned.                                              */
/*                                                           */
/* (fact-relation <fact-address-or-index>)                   */
/*    Returns the deftemplate name of the fact. Returns      */
/*    False if the specified fact doesn't exist.             */
/*                                                           */
/* (fact-slot-value <fact-address-or-index> <slot-name>)     */
/*    Returns the contents of a slot (use the slot name      */
/*    implied for the implied multifield slot of an ordered  */
/*    fact). Returns the value FALSE if the slot name is     */
/*    invalid or the fact doesn't exist.                     */
/*                                                           */
/* (fact-slot-names <fact-address-or-index>)                 */
/*    Returns the slot names associated with a fact in a     */
/*    multifield value. Returns FALSE if the fact doesn't    */
/*    exist.                                                 */
/*                                                           */
/* (get-fact-list [<module-name>])                           */
/*    Returns the list of facts visible to the specified     */
/*    module or to the current module if none is specified.  */
/*    If * is specified then all facts are returned.         */
/*                                                           */
/* Principal Programmer(s):                                  */
/*      Gary D. Riley                                        */
/*                                                           */
/* Contributing Programmer(s):                               */
/*                                                           */
/* Revision History:                                         */
/*                                                           */
/*************************************************************/

#include <stdio.h>
#define _STDIO_INCLUDED_
#include <string.h>

#include "setup.h"

#if DEFTEMPLATE_CONSTRUCT

#define _FACTFUN_SOURCE_

#include "extnfunc.h"
#include "envrnmnt.h"
#include "argacces.h"
#include "prntutil.h"
#include "tmpltutl.h"

#include "factfun.h"

/****************************************************/
/* FactFunctionDefinitions: Defines fact functions. */
/****************************************************/
globle void FactFunctionDefinitions(
  void *theEnv)
  {
#if ! RUN_TIME
   EnvDefineFunction2(theEnv,"fact-existp",  'b', PTIEF FactExistpFunction,  "FactExistpFunction", "11z");
   EnvDefineFunction2(theEnv,"fact-relation",'w', PTIEF FactRelationFunction,"FactRelationFunction", "11z");
   EnvDefineFunction2(theEnv,"fact-slot-value",'u', PTIEF FactSlotValueFunction,"FactSlotValueFunction", "22*zw");
   EnvDefineFunction2(theEnv,"fact-slot-names",'u', PTIEF FactSlotNamesFunction,"FactSlotNamesFunction", "11z");
   EnvDefineFunction2(theEnv,"get-fact-list",'m',PTIEF GetFactListFunction,"GetFactListFunction","01w");
#else
#if MAC_MPW || MAC_MCW || IBM_MCW
#pragma unused(theEnv)
#endif
#endif
  }

/**********************************************/
/* FactRelationFunction: H/L access routine   */
/*   for the fact-relation function.          */
/**********************************************/
globle void *FactRelationFunction(
  void *theEnv)
  {
   struct fact *theFact;

   if (EnvArgCountCheck(theEnv,"fact-relation",EXACTLY,1) == -1) return(SymbolData(theEnv)->FalseSymbol);

   theFact = GetFactAddressOrIndexArgument(theEnv,"fact-relation",1,FALSE);

   if (theFact == NULL) return(SymbolData(theEnv)->FalseSymbol);

   return(FactRelation(theFact));
  }

/**************************************/
/* FactRelation: C access routine for */
/*   the fact-relation function.      */
/**************************************/
globle void *FactRelation(
  void *vTheFact)
  {
   struct fact *theFact = (struct fact *) vTheFact;

   return((void *) theFact->whichDeftemplate->header.name);
  }

/********************************************/
/* FactExistpFunction: H/L access routine   */
/*   for the fact-existp function.          */
/********************************************/
globle long int FactExistpFunction(
  void *theEnv)
  {
   struct fact *theFact;

   if (EnvArgCountCheck(theEnv,"fact-existp",EXACTLY,1) == -1) return(-1L);

   theFact = GetFactAddressOrIndexArgument(theEnv,"fact-existp",1,FALSE);

   return(FactExistp(theFact));
  }

/************************************/
/* FactExistp: C access routine for */
/*   the fact-existp function.      */
/************************************/
globle long int FactExistp(
  void *vTheFact)
  {
   struct fact *theFact = (struct fact *) vTheFact;

   if (theFact == NULL) return(FALSE);

   if (theFact->garbage) return(FALSE);

   return(TRUE);
  }

/***********************************************/
/* FactSlotValueFunction: H/L access routine   */
/*   for the fact-slot-value function.         */
/***********************************************/
globle void FactSlotValueFunction(
  void *theEnv,
  DATA_OBJECT *returnValue)
  {
   struct fact *theFact;
   DATA_OBJECT theValue;

   /*=============================================*/
   /* Set up the default return value for errors. */
   /*=============================================*/

   returnValue->type = SYMBOL;
   returnValue->value = SymbolData(theEnv)->FalseSymbol;

   /*============================================*/
   /* Check for the correct number of arguments. */
   /*============================================*/

   if (EnvArgCountCheck(theEnv,"fact-slot-value",EXACTLY,2) == -1) return;

   /*================================*/
   /* Get the reference to the fact. */
   /*================================*/

   theFact = GetFactAddressOrIndexArgument(theEnv,"fact-slot-value",1,TRUE);
   if (theFact == NULL) return;

   /*===========================*/
   /* Get the name of the slot. */
   /*===========================*/

   if (EnvArgTypeCheck(theEnv,"fact-slot-value",2,SYMBOL,&theValue) == FALSE)
     { return; }

   /*=======================*/
   /* Get the slot's value. */
   /*=======================*/

   FactSlotValue(theEnv,theFact,DOToString(theValue),returnValue);
  }

/***************************************/
/* FactSlotValue: C access routine for */
/*   the fact-slot-value function.     */
/***************************************/
globle void FactSlotValue(
  void *theEnv,
  void *vTheFact,
  char *theSlotName,
  DATA_OBJECT *returnValue)
  {
   struct fact *theFact = (struct fact *) vTheFact;
   short position;

   /*==================================================*/
   /* Make sure the slot exists (the symbol implied is */
   /* used for the implied slot of an ordered fact).   */
   /*==================================================*/

   if (theFact->whichDeftemplate->implied)
     {
      if (strcmp(theSlotName,"implied") != 0)
        {
         SetEvaluationError(theEnv,TRUE);
         InvalidDeftemplateSlotMessage(theEnv,theSlotName,
                                       ValueToString(theFact->whichDeftemplate->header.name));
         return;
        }
     }

   else if (FindSlot(theFact->whichDeftemplate,(SYMBOL_HN *) EnvAddSymbol(theEnv,theSlotName),&position) == NULL)
     {
      SetEvaluationError(theEnv,TRUE);
      InvalidDeftemplateSlotMessage(theEnv,theSlotName,
                                    ValueToString(theFact->whichDeftemplate->header.name));
      return;
     }

   /*==========================*/
   /* Return the slot's value. */
   /*==========================*/

   if (theFact->whichDeftemplate->implied)
     { EnvGetFactSlot(theEnv,theFact,NULL,returnValue); }
   else
     { EnvGetFactSlot(theEnv,theFact,theSlotName,returnValue); }
  }

/***********************************************/
/* FactSlotNamesFunction: H/L access routine   */
/*   for the fact-slot-names function.         */
/***********************************************/
globle void FactSlotNamesFunction(
  void *theEnv,
  DATA_OBJECT *returnValue)
  {
   struct fact *theFact;

   /*=============================================*/
   /* Set up the default return value for errors. */
   /*=============================================*/

   returnValue->type = SYMBOL;
   returnValue->value = SymbolData(theEnv)->FalseSymbol;

   /*============================================*/
   /* Check for the correct number of arguments. */
   /*============================================*/

   if (EnvArgCountCheck(theEnv,"fact-slot-names",EXACTLY,1) == -1) return;

   /*================================*/
   /* Get the reference to the fact. */
   /*================================*/

   theFact = GetFactAddressOrIndexArgument(theEnv,"fact-slot-names",1,TRUE);
   if (theFact == NULL) return;

   /*=====================*/
   /* Get the slot names. */
   /*=====================*/

   FactSlotNames(theEnv,theFact,returnValue);
  }

/***************************************/
/* FactSlotNames: C access routine for */
/*   the fact-slot-names function.     */
/***************************************/
globle void FactSlotNames(
  void *theEnv,
  void *vTheFact,
  DATA_OBJECT *returnValue)
  {
   struct fact *theFact = (struct fact *) vTheFact;
   struct multifield *theList;
   struct templateSlot *theSlot;
   unsigned long count;

   /*===============================================*/
   /* If we're dealing with an implied deftemplate, */
   /* then the only slot names is "implied."        */
   /*===============================================*/

   if (theFact->whichDeftemplate->implied)
     {
      SetpType(returnValue,MULTIFIELD);
      SetpDOBegin(returnValue,1);
      SetpDOEnd(returnValue,1);
      theList = (struct multifield *) EnvCreateMultifield(theEnv,(int) 1);
      SetMFType(theList,1,SYMBOL);
      SetMFValue(theList,1,EnvAddSymbol(theEnv,"implied"));
      SetpValue(returnValue,(void *) theList);
      return;
     }

   /*=================================*/
   /* Count the number of slot names. */
   /*=================================*/

   for (count = 0, theSlot = theFact->whichDeftemplate->slotList;
        theSlot != NULL;
        count++, theSlot = theSlot->next)
     { /* Do Nothing */ }

   /*=============================================================*/
   /* Create a multifield value in which to store the slot names. */
   /*=============================================================*/

   SetpType(returnValue,MULTIFIELD);
   SetpDOBegin(returnValue,1);
   SetpDOEnd(returnValue,(long) count);
   theList = (struct multifield *) EnvCreateMultifield(theEnv,count);
   SetpValue(returnValue,(void *) theList);

   /*===============================================*/
   /* Store the slot names in the multifield value. */
   /*===============================================*/

   for (count = 1, theSlot = theFact->whichDeftemplate->slotList;
        theSlot != NULL;
        count++, theSlot = theSlot->next)
     {
      SetMFType(theList,count,SYMBOL);
      SetMFValue(theList,count,theSlot->slotName);
     }
  }

/*********************************************/
/* GetFactListFunction: H/L access routine   */
/*   for the get-fact-list function.         */
/*********************************************/
globle void GetFactListFunction(
  void *theEnv,
  DATA_OBJECT_PTR returnValue)
  {
   struct defmodule *theModule;
   DATA_OBJECT result;
   int numArgs;

   /*===========================================*/
   /* Determine if a module name was specified. */
   /*===========================================*/

   if ((numArgs = EnvArgCountCheck(theEnv,"get-fact-list",NO_MORE_THAN,1)) == -1)
     {
      EnvSetMultifieldErrorValue(theEnv,returnValue);
      return;
     }

   if (numArgs == 1)
     {
      EnvRtnUnknown(theEnv,1,&result);

      if (GetType(result) != SYMBOL)
        {
         EnvSetMultifieldErrorValue(theEnv,returnValue);
         ExpectedTypeError1(theEnv,"get-fact-list",1,"defmodule name");
         return;
        }

      if ((theModule = (struct defmodule *) EnvFindDefmodule(theEnv,DOToString(result))) == NULL)
        {
         if (strcmp("*",DOToString(result)) != 0)
           {
            EnvSetMultifieldErrorValue(theEnv,returnValue);
            ExpectedTypeError1(theEnv,"get-fact-list",1,"defmodule name");
            return;
           }

         theModule = NULL;
        }
     }
   else
     { theModule = ((struct defmodule *) EnvGetCurrentModule(theEnv)); }

   /*=====================*/
   /* Get the constructs. */
   /*=====================*/

   GetFactList(theEnv,returnValue,theModule);
  }

/*************************************/
/* GetFactList: C access routine for */
/*   the get-fact-list function.     */
/*************************************/
globle void GetFactList(
  void *theEnv,
  DATA_OBJECT_PTR returnValue,
  void *vTheModule)
  {
   struct fact *theFact;
   unsigned long count;
   struct multifield *theList;
   struct defmodule *theModule = (struct defmodule *) vTheModule;

   /*==========================*/
   /* Save the current module. */
   /*==========================*/

   SaveCurrentModule(theEnv);

   /*============================================*/
   /* Count the number of facts to be retrieved. */
   /*============================================*/

   if (theModule == NULL)
     {
      for (theFact = (struct fact *) EnvGetNextFact(theEnv,NULL), count = 0;
           theFact != NULL;
           theFact = (struct fact *) EnvGetNextFact(theEnv,theFact), count++)
        { /* Do Nothing */ }
     }
   else
     {
      EnvSetCurrentModule(theEnv,(void *) theModule);
      UpdateDeftemplateScope(theEnv);
      for (theFact = (struct fact *) GetNextFactInScope(theEnv,NULL), count = 0;
           theFact != NULL;
           theFact = (struct fact *) GetNextFactInScope(theEnv,theFact), count++)
        { /* Do Nothing */ }
     }

   /*===========================================================*/
   /* Create the multifield value to store the construct names. */
   /*===========================================================*/

   SetpType(returnValue,MULTIFIELD);
   SetpDOBegin(returnValue,1);
   SetpDOEnd(returnValue,(long) count);
   theList = (struct multifield *) EnvCreateMultifield(theEnv,count);
   SetpValue(returnValue,(void *) theList);

   /*==================================================*/
   /* Store the fact pointers in the multifield value. */
   /*==================================================*/

   if (theModule == NULL)
     {
      for (theFact = (struct fact *) EnvGetNextFact(theEnv,NULL), count = 1;
           theFact != NULL;
           theFact = (struct fact *) EnvGetNextFact(theEnv,theFact), count++)
        {
         SetMFType(theList,count,FACT_ADDRESS);
         SetMFValue(theList,count,(void *) theFact);
        }
     }
   else
     {
      for (theFact = (struct fact *) GetNextFactInScope(theEnv,NULL), count = 1;
           theFact != NULL;
           theFact = (struct fact *) GetNextFactInScope(theEnv,theFact), count++)
        {
         SetMFType(theList,count,FACT_ADDRESS);
         SetMFValue(theList,count,(void *) theFact);
        }
     }

   /*=============================*/
   /* Restore the current module. */
   /*=============================*/

   RestoreCurrentModule(theEnv);
   UpdateDeftemplateScope(theEnv);
  }

/**************************************************************/
/* GetFactAddressOrIndexArgument: Retrieves an argument for a */
/*   function which should be a reference to a valid fact.    */
/**************************************************************/
globle struct fact *GetFactAddressOrIndexArgument(
  void *theEnv,
  char *theFunction,
  int position,
  int noFactError)
  {
   DATA_OBJECT item;
   long factIndex;
   struct fact *theFact;
   char tempBuffer[20];

   EnvRtnUnknown(theEnv,position,&item);

   if (GetType(item) == FACT_ADDRESS)
     {
      if (((struct fact *) GetValue(item))->garbage) return(NULL);
      else return (((struct fact *) GetValue(item)));
     }
   else if (GetType(item) == INTEGER)
     {
      factIndex = ValueToLong(item.value);
      if (factIndex < 0)
        {
         ExpectedTypeError1(theEnv,theFunction,position,"fact-address or fact-index");
         return(NULL);
        }

      theFact = FindIndexedFact(theEnv,factIndex);
      if ((theFact == NULL) && noFactError)
        {
         sprintf(tempBuffer,"f-%ld",factIndex);
         CantFindItemErrorMessage(theEnv,"fact",tempBuffer);
         return(NULL);
        }

      return(theFact);
     }

   ExpectedTypeError1(theEnv,theFunction,position,"fact-address or fact-index");
   return(NULL);
  }

#endif /* DEFTEMPLATE_CONSTRUCT */


