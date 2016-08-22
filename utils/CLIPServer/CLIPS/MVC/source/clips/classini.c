   /*******************************************************/
   /*      "C" Language Integrated Production System      */
   /*                                                     */
   /*               CLIPS Version 6.20  01/31/02          */
   /*                                                     */
   /*               CLASS INITIALIZATION MODULE           */
   /*******************************************************/

/**************************************************************/
/* Purpose: Defclass Initialization Routines                  */
/*                                                            */
/* Principal Programmer(s):                                   */
/*      Brian L. Donnell                                      */
/*                                                            */
/* Contributing Programmer(s):                                */
/*                                                            */
/* Revision History:                                          */
/*                                                            */
/**************************************************************/

/* =========================================
   *****************************************
               EXTERNAL DEFINITIONS
   =========================================
   ***************************************** */
#include "setup.h"

#if OBJECT_SYSTEM

#ifndef _STDIO_INCLUDED_
#define _STDIO_INCLUDED_
#include <stdio.h>
#endif

#include "classcom.h"
#include "classexm.h"
#include "classfun.h"
#include "classinf.h"
#include "classpsr.h"
#include "cstrccom.h"
#include "cstrcpsr.h"
#include "envrnmnt.h"
#include "extnfunc.h"
#include "inscom.h"
#include "memalloc.h"
#include "modulpsr.h"
#include "modulutl.h"
#include "msgcom.h"
#include "watch.h"

#if DEFINSTANCES_CONSTRUCT
#include "defins.h"
#endif

#if INSTANCE_SET_QUERIES
#include "insquery.h"
#endif

#if BLOAD_AND_BSAVE || BLOAD || BLOAD_ONLY
#include "bload.h"
#include "objbin.h"
#endif

#if CONSTRUCT_COMPILER && (! RUN_TIME)
#include "objcmp.h"
#endif

#if INSTANCE_PATTERN_MATCHING
#include "objrtbld.h"
#endif

#if RUN_TIME
#include "insfun.h"
#include "msgfun.h"
#endif

#include "classini.h"

/* =========================================
   *****************************************
                   CONSTANTS
   =========================================
   ***************************************** */
#define SUPERCLASS_RLN       "is-a"
#define NAME_RLN             "name"
#define INITIAL_OBJECT_NAME  "initial-object"

/* =========================================
   *****************************************
      INTERNALLY VISIBLE FUNCTION HEADERS
   =========================================
   ***************************************** */

static void SetupDefclasses(void *);
static void DeallocateDefclassData(void *);
static void DestroyDefclassAction(void *,struct constructHeader *,void *);

#if (! RUN_TIME)
static DEFCLASS *AddSystemClass(void *,char *,DEFCLASS *);
static void *AllocateModule(void *);
static void  ReturnModule(void *,void *);
#endif

#if (! BLOAD_ONLY) && (! RUN_TIME) && DEFMODULE_CONSTRUCT
static void UpdateDefclassesScope(void *);
#endif

/* =========================================
   *****************************************
          EXTERNALLY VISIBLE FUNCTIONS
   =========================================
   ***************************************** */

/**********************************************************
  NAME         : SetupObjectSystem
  DESCRIPTION  : Initializes all COOL constructs, functions,
                   and data structures
  INPUTS       : None
  RETURNS      : Nothing useful
  SIDE EFFECTS : COOL initialized
  NOTES        : Order of setup calls is important
 **********************************************************/
globle void SetupObjectSystem(
  void *theEnv)
  {   
   ENTITY_RECORD defclassEntityRecord = { "DEFCLASS_PTR", DEFCLASS_PTR,1,0,0,
                                              NULL,NULL,NULL,NULL,NULL,
                                              DecrementDefclassBusyCount,
                                              IncrementDefclassBusyCount,
                                              NULL,NULL,NULL,NULL };

   AllocateEnvironmentData(theEnv,DEFCLASS_DATA,sizeof(struct defclassData),NULL);
   AddEnvironmentCleanupFunction(theEnv,"defclasses",DeallocateDefclassData,-500);

   memcpy(&DefclassData(theEnv)->DefclassEntityRecord,&defclassEntityRecord,sizeof(struct entityRecord));   

#if ! RUN_TIME
   DefclassData(theEnv)->ClassDefaultsMode = CONVENIENCE_MODE;
   DefclassData(theEnv)->ISA_SYMBOL = (SYMBOL_HN *) EnvAddSymbol(theEnv,SUPERCLASS_RLN);
   IncrementSymbolCount(DefclassData(theEnv)->ISA_SYMBOL);
   DefclassData(theEnv)->NAME_SYMBOL = (SYMBOL_HN *) EnvAddSymbol(theEnv,NAME_RLN);
   IncrementSymbolCount(DefclassData(theEnv)->NAME_SYMBOL);
#if INSTANCE_PATTERN_MATCHING
   DefclassData(theEnv)->INITIAL_OBJECT_SYMBOL = (SYMBOL_HN *) EnvAddSymbol(theEnv,INITIAL_OBJECT_NAME);
   IncrementSymbolCount(DefclassData(theEnv)->INITIAL_OBJECT_SYMBOL);
#endif
#endif

   SetupDefclasses(theEnv);
   SetupInstances(theEnv);
   SetupMessageHandlers(theEnv);

#if DEFINSTANCES_CONSTRUCT
   SetupDefinstances(theEnv);
#endif

#if INSTANCE_SET_QUERIES
   SetupQuery(theEnv);
#endif

#if BLOAD_AND_BSAVE || BLOAD || BLOAD_ONLY
   SetupObjectsBload(theEnv);
#endif

#if CONSTRUCT_COMPILER && (! RUN_TIME)
   SetupObjectsCompiler(theEnv);
#endif

#if INSTANCE_PATTERN_MATCHING
   SetupObjectPatternStuff(theEnv);
#endif
  }
  
/***************************************************/
/* DeallocateDefclassData: Deallocates environment */
/*    data for the defclass construct.             */
/***************************************************/
static void DeallocateDefclassData(
  void *theEnv)
  {
#if ! RUN_TIME   
   SLOT_NAME *tmpSNPPtr, *nextSNPPtr;
   int i;
   struct defclassModule *theModuleItem;
   void *theModule;
   int bloaded = FALSE;
   
#if BLOAD || BLOAD_AND_BSAVE
   if (Bloaded(theEnv)) bloaded = TRUE;
#endif

   /*=============================*/
   /* Destroy all the defclasses. */
   /*=============================*/
   
   if (! bloaded)
     {
      DoForAllConstructs(theEnv,DestroyDefclassAction,DefclassData(theEnv)->DefclassModuleIndex,FALSE,NULL); 

      for (theModule = EnvGetNextDefmodule(theEnv,NULL);
           theModule != NULL;
           theModule = EnvGetNextDefmodule(theEnv,theModule))
        {
         theModuleItem = (struct defclassModule *)
                         GetModuleItem(theEnv,(struct defmodule *) theModule,
                                       DefclassData(theEnv)->DefclassModuleIndex);
         rtn_struct(theEnv,defclassModule,theModuleItem);
        }
     }

   /*==========================*/
   /* Remove the class tables. */
   /*==========================*/
   
   if (! bloaded)
     {
      if (DefclassData(theEnv)->ClassIDMap != NULL)
        {
         genfree(theEnv,DefclassData(theEnv)->ClassIDMap,DefclassData(theEnv)->AvailClassID * sizeof(DEFCLASS *));
        }
     }
     
   if (DefclassData(theEnv)->ClassTable != NULL)
     {
      genfree(theEnv,DefclassData(theEnv)->ClassTable,sizeof(DEFCLASS *) * CLASS_TABLE_HASH_SIZE);
     }

   /*==============================*/
   /* Free up the slot name table. */
   /*==============================*/

   if (! bloaded)
     {
      for (i = 0; i < SLOT_NAME_TABLE_HASH_SIZE; i++)
        {
         tmpSNPPtr = DefclassData(theEnv)->SlotNameTable[i];
      
         while (tmpSNPPtr != NULL)
           {
            nextSNPPtr = tmpSNPPtr->nxt;
            rtn_struct(theEnv,slotName,tmpSNPPtr);
            tmpSNPPtr = nextSNPPtr;
           }
        }
     }
          
   if (DefclassData(theEnv)->SlotNameTable != NULL)
     {
      genfree(theEnv,DefclassData(theEnv)->SlotNameTable,sizeof(SLOT_NAME *) * SLOT_NAME_TABLE_HASH_SIZE);
     }
#else
   DEFCLASS *cls;
   void *tmpexp;
   register int i,j;
   
   if (DefclassData(theEnv)->ClassTable != NULL)
     {
      for (j = 0 ; j < CLASS_TABLE_HASH_SIZE ; j++)
        for (cls = DefclassData(theEnv)->ClassTable[j] ; cls != NULL ; cls = cls->nxtHash)
          {
           for (i = 0 ; i < cls->slotCount ; i++)
             {
              if ((cls->slots[i].defaultValue != NULL) && (cls->slots[i].dynamicDefault == 0))
                {
                 tmpexp = ((DATA_OBJECT *) cls->slots[i].defaultValue)->supplementalInfo;
                 rtn_struct(theEnv,dataObject,cls->slots[i].defaultValue);
                 cls->slots[i].defaultValue = tmpexp;
                }
             }
          }
     }
#endif
  }
  
/*********************************************************/
/* DestroyDefclassAction: Action used to remove defclass */
/*   as a result of DestroyEnvironment.                  */
/*********************************************************/
#if IBM_TBC
#pragma argsused
#endif
static void DestroyDefclassAction(
  void *theEnv,
  struct constructHeader *theConstruct,
  void *buffer)
  {
#if MAC_MPW || MAC_MCW || IBM_MCW
#pragma unused(buffer)
#endif
   struct defclass *theDefclass = (struct defclass *) theConstruct;

   if (theDefclass == NULL) return;

#if (! BLOAD_ONLY) 
   DestroyDefclass(theEnv,theDefclass);
#endif
  }

#if RUN_TIME

/***************************************************
  NAME         : ObjectsRunTimeInitialize
  DESCRIPTION  : Initializes objects system lists
                   in a run-time module
  INPUTS       : 1) Pointer to new class hash table
                 2) Pointer to new slot name table
  RETURNS      : Nothing useful
  SIDE EFFECTS : Global pointers set
  NOTES        : None
 ***************************************************/
globle void ObjectsRunTimeInitialize(
  void *theEnv,
  DEFCLASS *ctable[],
  SLOT_NAME *sntable[],
  DEFCLASS **cidmap,
  unsigned mid)
  {
   DEFCLASS *cls;
   void *tmpexp;
   register int i,j;

   if (DefclassData(theEnv)->ClassTable != NULL)
     {
      for (j = 0 ; j < CLASS_TABLE_HASH_SIZE ; j++)
        for (cls = DefclassData(theEnv)->ClassTable[j] ; cls != NULL ; cls = cls->nxtHash)
          {
           for (i = 0 ; i < cls->slotCount ; i++)
             {
              /* =====================================================================
                 For static default values, the data object value needs to deinstalled
                 and deallocated, and the expression needs to be restored (which was
                 temporarily stored in the supplementalInfo field of the data object)
                 ===================================================================== */
              if ((cls->slots[i].defaultValue != NULL) && (cls->slots[i].dynamicDefault == 0))
                {
                 tmpexp = ((DATA_OBJECT *) cls->slots[i].defaultValue)->supplementalInfo;
                 ValueDeinstall(theEnv,(DATA_OBJECT *) cls->slots[i].defaultValue);
                 rtn_struct(theEnv,dataObject,cls->slots[i].defaultValue);
                 cls->slots[i].defaultValue = tmpexp;
                }
             }
          }
     }

   InstanceQueryData(theEnv)->QUERY_DELIMETER_SYMBOL = FindSymbol(theEnv,QUERY_DELIMETER_STRING);
   MessageHandlerData(theEnv)->INIT_SYMBOL = FindSymbol(theEnv,INIT_STRING);
   MessageHandlerData(theEnv)->DELETE_SYMBOL = FindSymbol(theEnv,DELETE_STRING);
   MessageHandlerData(theEnv)->CREATE_SYMBOL = FindSymbol(theEnv,CREATE_STRING);
   DefclassData(theEnv)->ISA_SYMBOL = FindSymbol(theEnv,SUPERCLASS_RLN);
   DefclassData(theEnv)->NAME_SYMBOL = FindSymbol(theEnv,NAME_RLN);
#if INSTANCE_PATTERN_MATCHING
   DefclassData(theEnv)->INITIAL_OBJECT_SYMBOL = FindSymbol(theEnv,INITIAL_OBJECT_NAME);
#endif

   DefclassData(theEnv)->ClassTable = (DEFCLASS **) ctable;
   DefclassData(theEnv)->SlotNameTable = (SLOT_NAME **) sntable;
   DefclassData(theEnv)->ClassIDMap = (DEFCLASS **) cidmap;
   DefclassData(theEnv)->MaxClassID = (unsigned short) mid;
   DefclassData(theEnv)->PrimitiveClassMap[FLOAT] =
     LookupDefclassByMdlOrScope(theEnv,FLOAT_TYPE_NAME);
   DefclassData(theEnv)->PrimitiveClassMap[INTEGER] =
     LookupDefclassByMdlOrScope(theEnv,INTEGER_TYPE_NAME);
   DefclassData(theEnv)->PrimitiveClassMap[STRING] =
     LookupDefclassByMdlOrScope(theEnv,STRING_TYPE_NAME);
   DefclassData(theEnv)->PrimitiveClassMap[SYMBOL] =
     LookupDefclassByMdlOrScope(theEnv,SYMBOL_TYPE_NAME);
   DefclassData(theEnv)->PrimitiveClassMap[MULTIFIELD] =
     LookupDefclassByMdlOrScope(theEnv,MULTIFIELD_TYPE_NAME);
   DefclassData(theEnv)->PrimitiveClassMap[EXTERNAL_ADDRESS] =
     LookupDefclassByMdlOrScope(theEnv,EXTERNAL_ADDRESS_TYPE_NAME);
   DefclassData(theEnv)->PrimitiveClassMap[FACT_ADDRESS] =
     LookupDefclassByMdlOrScope(theEnv,FACT_ADDRESS_TYPE_NAME);
   DefclassData(theEnv)->PrimitiveClassMap[INSTANCE_NAME] =
     LookupDefclassByMdlOrScope(theEnv,INSTANCE_NAME_TYPE_NAME);
   DefclassData(theEnv)->PrimitiveClassMap[INSTANCE_ADDRESS] =
     LookupDefclassByMdlOrScope(theEnv,INSTANCE_ADDRESS_TYPE_NAME);

   for (j = 0 ; j < CLASS_TABLE_HASH_SIZE ; j++)
     for (cls = DefclassData(theEnv)->ClassTable[j] ; cls != NULL ; cls = cls->nxtHash)
     {
      for (i = 0 ; i < cls->slotCount ; i++)
        {
         if ((cls->slots[i].defaultValue != NULL) && (cls->slots[i].dynamicDefault == 0))
           {
            tmpexp = cls->slots[i].defaultValue;
            cls->slots[i].defaultValue = (void *) get_struct(theEnv,dataObject);
            EvaluateAndStoreInDataObject(theEnv,(int) cls->slots[i].multiple,(EXPRESSION *) tmpexp,
                                         (DATA_OBJECT *) cls->slots[i].defaultValue);
            ValueInstall(theEnv,(DATA_OBJECT *) cls->slots[i].defaultValue);
            ((DATA_OBJECT *) cls->slots[i].defaultValue)->supplementalInfo = tmpexp;
           }
        }
     }

  }

#else

/***************************************************************
  NAME         : CreateSystemClasses
  DESCRIPTION  : Creates the built-in system classes
  INPUTS       : None
  RETURNS      : Nothing useful
  SIDE EFFECTS : System classes inserted in the
                   class hash table
  NOTES        : The binary/load save indices for the primitive
                   types (integer, float, symbol and string,
                   multifield, external-address and fact-address)
                   are very important.  Need to be able to refer
                   to types with the same index regardless of
                   whether the object system is installed or
                   not.  Thus, the bsave/blaod indices of these
                   classes match their integer codes.
                WARNING!!: Assumes no classes exist yet!
 ***************************************************************/
globle void CreateSystemClasses(
  void *theEnv)
  {
   DEFCLASS *user,*any,*primitive,*number,*lexeme,*address,*instance;
#if INSTANCE_PATTERN_MATCHING
   DEFCLASS *initialObject;
#endif
   
   /* ===================================
      Add canonical slot name entries for
      the is-a and name fields - used for
      object patterns
      =================================== */
   AddSlotName(theEnv,DefclassData(theEnv)->ISA_SYMBOL,ISA_ID,TRUE);
   AddSlotName(theEnv,DefclassData(theEnv)->NAME_SYMBOL,NAME_ID,TRUE);

   /* =========================================================
      Bsave Indices for non-primitive classes start at 9
               Object is 9, Primitive is 10, Number is 11,
               Lexeme is 12, Address is 13, and Instance is 14.
      because: float = 0, integer = 1, symbol = 2, string = 3,
               multifield = 4, and external-address = 5 and
               fact-address = 6, instance-adress = 7 and
               instance-name = 8.
      ========================================================= */
   any = AddSystemClass(theEnv,OBJECT_TYPE_NAME,NULL);
   primitive = AddSystemClass(theEnv,PRIMITIVE_TYPE_NAME,any);
   user = AddSystemClass(theEnv,USER_TYPE_NAME,any);

   number = AddSystemClass(theEnv,NUMBER_TYPE_NAME,primitive);
   DefclassData(theEnv)->PrimitiveClassMap[INTEGER] = AddSystemClass(theEnv,INTEGER_TYPE_NAME,number);
   DefclassData(theEnv)->PrimitiveClassMap[FLOAT] = AddSystemClass(theEnv,FLOAT_TYPE_NAME,number);
   lexeme = AddSystemClass(theEnv,LEXEME_TYPE_NAME,primitive);
   DefclassData(theEnv)->PrimitiveClassMap[SYMBOL] = AddSystemClass(theEnv,SYMBOL_TYPE_NAME,lexeme);
   DefclassData(theEnv)->PrimitiveClassMap[STRING] = AddSystemClass(theEnv,STRING_TYPE_NAME,lexeme);
   DefclassData(theEnv)->PrimitiveClassMap[MULTIFIELD] = AddSystemClass(theEnv,MULTIFIELD_TYPE_NAME,primitive);
   address = AddSystemClass(theEnv,ADDRESS_TYPE_NAME,primitive);
   DefclassData(theEnv)->PrimitiveClassMap[EXTERNAL_ADDRESS] = AddSystemClass(theEnv,EXTERNAL_ADDRESS_TYPE_NAME,address);
   DefclassData(theEnv)->PrimitiveClassMap[FACT_ADDRESS] = AddSystemClass(theEnv,FACT_ADDRESS_TYPE_NAME,address);
   instance = AddSystemClass(theEnv,INSTANCE_TYPE_NAME,primitive);
   DefclassData(theEnv)->PrimitiveClassMap[INSTANCE_ADDRESS] = AddSystemClass(theEnv,INSTANCE_ADDRESS_TYPE_NAME,instance);
   DefclassData(theEnv)->PrimitiveClassMap[INSTANCE_NAME] = AddSystemClass(theEnv,INSTANCE_NAME_TYPE_NAME,instance);
#if INSTANCE_PATTERN_MATCHING
   initialObject = AddSystemClass(theEnv,INITIAL_OBJECT_CLASS_NAME,user);
   initialObject->abstract = 0;
   initialObject->reactive = 1;
#endif

   /* ================================================================================
       INSTANCE-ADDRESS is-a INSTANCE and ADDRESS.  The links between INSTANCE-ADDRESS
       and ADDRESS still need to be made.
       =============================================================================== */
   AddClassLink(theEnv,&DefclassData(theEnv)->PrimitiveClassMap[INSTANCE_ADDRESS]->directSuperclasses,address,-1);
   AddClassLink(theEnv,&DefclassData(theEnv)->PrimitiveClassMap[INSTANCE_ADDRESS]->allSuperclasses,address,2);
   AddClassLink(theEnv,&address->directSubclasses,DefclassData(theEnv)->PrimitiveClassMap[INSTANCE_ADDRESS],-1);

   /* =======================================================================
      The order of the class in the list MUST correspond to their type codes!
      See CONSTANT.H
      ======================================================================= */
   AddConstructToModule((struct constructHeader *) DefclassData(theEnv)->PrimitiveClassMap[FLOAT]);
   AddConstructToModule((struct constructHeader *) DefclassData(theEnv)->PrimitiveClassMap[INTEGER]);
   AddConstructToModule((struct constructHeader *) DefclassData(theEnv)->PrimitiveClassMap[SYMBOL]);
   AddConstructToModule((struct constructHeader *) DefclassData(theEnv)->PrimitiveClassMap[STRING]);
   AddConstructToModule((struct constructHeader *) DefclassData(theEnv)->PrimitiveClassMap[MULTIFIELD]);
   AddConstructToModule((struct constructHeader *) DefclassData(theEnv)->PrimitiveClassMap[EXTERNAL_ADDRESS]);
   AddConstructToModule((struct constructHeader *) DefclassData(theEnv)->PrimitiveClassMap[FACT_ADDRESS]);
   AddConstructToModule((struct constructHeader *) DefclassData(theEnv)->PrimitiveClassMap[INSTANCE_ADDRESS]);
   AddConstructToModule((struct constructHeader *) DefclassData(theEnv)->PrimitiveClassMap[INSTANCE_NAME]);
   AddConstructToModule((struct constructHeader *) any);
   AddConstructToModule((struct constructHeader *) primitive);
   AddConstructToModule((struct constructHeader *) number);
   AddConstructToModule((struct constructHeader *) lexeme);
   AddConstructToModule((struct constructHeader *) address);
   AddConstructToModule((struct constructHeader *) instance);
   AddConstructToModule((struct constructHeader *) user);
#if INSTANCE_PATTERN_MATCHING
   AddConstructToModule((struct constructHeader *) initialObject);
#endif
   for (any = (DEFCLASS *) EnvGetNextDefclass(theEnv,NULL) ;
        any != NULL ;
        any = (DEFCLASS *) EnvGetNextDefclass(theEnv,(void *) any))
     AssignClassID(theEnv,any);
  }

#endif

/* =========================================
   *****************************************
          INTERNALLY VISIBLE FUNCTIONS
   =========================================
   ***************************************** */

/*********************************************************
  NAME         : SetupDefclasses
  DESCRIPTION  : Initializes Class Hash Table,
                   Function Parsers, and Data Structures
  INPUTS       : None
  RETURNS      : Nothing useful
  SIDE EFFECTS :
  NOTES        : None
 *********************************************************/
static void SetupDefclasses(
  void *theEnv)
  {
   InstallPrimitive(theEnv,&DefclassData(theEnv)->DefclassEntityRecord,DEFCLASS_PTR);

   DefclassData(theEnv)->DefclassModuleIndex =
                RegisterModuleItem(theEnv,"defclass",
#if (! RUN_TIME)
                                    AllocateModule,ReturnModule,
#else
                                    NULL,NULL,
#endif
#if BLOAD_AND_BSAVE || BLOAD || BLOAD_ONLY
                                    BloadDefclassModuleReference,
#else
                                    NULL,
#endif
#if CONSTRUCT_COMPILER && (! RUN_TIME)
                                    DefclassCModuleReference,
#else
                                    NULL,
#endif
                                    EnvFindDefclass);

   DefclassData(theEnv)->DefclassConstruct =  AddConstruct(theEnv,"defclass","defclasses",
#if (! BLOAD_ONLY) && (! RUN_TIME)
                                     ParseDefclass,
#else
                                     NULL,
#endif
                                     EnvFindDefclass,
                                     GetConstructNamePointer,GetConstructPPForm,
                                     GetConstructModuleItem,EnvGetNextDefclass,
                                     SetNextConstruct,EnvIsDefclassDeletable,
                                     EnvUndefclass,
#if (! RUN_TIME)
                                     RemoveDefclass
#else
                                     NULL
#endif
                                     );

   AddClearReadyFunction(theEnv,"defclass",InstancesPurge,0);

#if ! RUN_TIME
   EnvAddClearFunction(theEnv,"defclass",CreateSystemClasses,0);
   InitializeClasses(theEnv);

#if ! BLOAD_ONLY
#if DEFMODULE_CONSTRUCT
   AddPortConstructItem(theEnv,"defclass",SYMBOL);
   AddAfterModuleDefinedFunction(theEnv,"defclass",UpdateDefclassesScope,0);
#endif
   EnvDefineFunction2(theEnv,"undefclass",'v',PTIEF UndefclassCommand,"UndefclassCommand","11w");

   AddSaveFunction(theEnv,"defclass",SaveDefclasses,10);
#endif

#if DEBUGGING_FUNCTIONS
   EnvDefineFunction2(theEnv,"list-defclasses",'v',PTIEF ListDefclassesCommand,"ListDefclassesCommand","01");
   EnvDefineFunction2(theEnv,"ppdefclass",'v',PTIEF PPDefclassCommand,"PPDefclassCommand","11w");
   EnvDefineFunction2(theEnv,"describe-class",'v',PTIEF DescribeClassCommand,"DescribeClassCommand","11w");
   EnvDefineFunction2(theEnv,"browse-classes",'v',PTIEF BrowseClassesCommand,"BrowseClassesCommand","01w");
#endif

   EnvDefineFunction2(theEnv,"get-defclass-list",'m',PTIEF GetDefclassListFunction,
                   "GetDefclassListFunction","01");
   EnvDefineFunction2(theEnv,"superclassp",'b',PTIEF SuperclassPCommand,"SuperclassPCommand","22w");
   EnvDefineFunction2(theEnv,"subclassp",'b',PTIEF SubclassPCommand,"SubclassPCommand","22w");
   EnvDefineFunction2(theEnv,"class-existp",'b',PTIEF ClassExistPCommand,"ClassExistPCommand","11w");
   EnvDefineFunction2(theEnv,"message-handler-existp",'b',
                   PTIEF MessageHandlerExistPCommand,"MessageHandlerExistPCommand","23w");
   EnvDefineFunction2(theEnv,"class-abstractp",'b',PTIEF ClassAbstractPCommand,"ClassAbstractPCommand","11w");
#if INSTANCE_PATTERN_MATCHING
   EnvDefineFunction2(theEnv,"class-reactivep",'b',PTIEF ClassReactivePCommand,"ClassReactivePCommand","11w");
#endif
   EnvDefineFunction2(theEnv,"class-slots",'m',PTIEF ClassSlotsCommand,"ClassSlotsCommand","12w");
   EnvDefineFunction2(theEnv,"class-superclasses",'m',
                   PTIEF ClassSuperclassesCommand,"ClassSuperclassesCommand","12w");
   EnvDefineFunction2(theEnv,"class-subclasses",'m',
                   PTIEF ClassSubclassesCommand,"ClassSubclassesCommand","12w");
   EnvDefineFunction2(theEnv,"get-defmessage-handler-list",'m',
                   PTIEF GetDefmessageHandlersListCmd,"GetDefmessageHandlersListCmd","02w");
   EnvDefineFunction2(theEnv,"slot-existp",'b',PTIEF SlotExistPCommand,"SlotExistPCommand","23w");
   EnvDefineFunction2(theEnv,"slot-facets",'m',PTIEF SlotFacetsCommand,"SlotFacetsCommand","22w");
   EnvDefineFunction2(theEnv,"slot-sources",'m',PTIEF SlotSourcesCommand,"SlotSourcesCommand","22w");
   EnvDefineFunction2(theEnv,"slot-types",'m',PTIEF SlotTypesCommand,"SlotTypesCommand","22w");
   EnvDefineFunction2(theEnv,"slot-allowed-values",'m',PTIEF SlotAllowedValuesCommand,"SlotAllowedValuesCommand","22w");
   EnvDefineFunction2(theEnv,"slot-range",'m',PTIEF SlotRangeCommand,"SlotRangeCommand","22w");
   EnvDefineFunction2(theEnv,"slot-cardinality",'m',PTIEF SlotCardinalityCommand,"SlotCardinalityCommand","22w");
   EnvDefineFunction2(theEnv,"slot-writablep",'b',PTIEF SlotWritablePCommand,"SlotWritablePCommand","22w");
   EnvDefineFunction2(theEnv,"slot-initablep",'b',PTIEF SlotInitablePCommand,"SlotInitablePCommand","22w");
   EnvDefineFunction2(theEnv,"slot-publicp",'b',PTIEF SlotPublicPCommand,"SlotPublicPCommand","22w");
   EnvDefineFunction2(theEnv,"slot-direct-accessp",'b',PTIEF SlotDirectAccessPCommand,
                   "SlotDirectAccessPCommand","22w");
   EnvDefineFunction2(theEnv,"slot-default-value",'u',PTIEF SlotDefaultValueCommand,
                   "SlotDefaultValueCommand","22w");
   EnvDefineFunction2(theEnv,"defclass-module",'w',PTIEF GetDefclassModuleCommand,
                   "GetDefclassModuleCommand","11w");
   EnvDefineFunction2(theEnv,"get-class-defaults-mode", 'w', PTIEF GetClassDefaultsModeCommand,  "GetClassDefaultsModeCommand", "00");
   EnvDefineFunction2(theEnv,"set-class-defaults-mode", 'w', PTIEF SetClassDefaultsModeCommand,  "SetClassDefaultsModeCommand", "11w");
#endif

#if DEBUGGING_FUNCTIONS
   AddWatchItem(theEnv,"instances",0,&DefclassData(theEnv)->WatchInstances,75,DefclassWatchAccess,DefclassWatchPrint);
   AddWatchItem(theEnv,"slots",1,&DefclassData(theEnv)->WatchSlots,74,DefclassWatchAccess,DefclassWatchPrint);
#endif
  }

#if (! RUN_TIME)

/*********************************************************
  NAME         : AddSystemClass
  DESCRIPTION  : Performs all necessary allocations
                   for adding a system class
  INPUTS       : 1) The name-string of the system class
                 2) The address of the parent class
                    (NULL if none)
  RETURNS      : The address of the new system class
  SIDE EFFECTS : Allocations performed
  NOTES        : Assumes system-class name is unique
                 Also assumes SINGLE INHERITANCE for
                   system classes to simplify precedence
                   list determination
                 Adds classes to has table but NOT to
                  class list (this is responsibility
                  of caller)
 *********************************************************/
static DEFCLASS *AddSystemClass(
  void *theEnv,
  char *name,
  DEFCLASS *parent)
  {
   DEFCLASS *sys;
   register unsigned i;
   char defaultScopeMap[1];

   sys = NewClass(theEnv,(SYMBOL_HN *) EnvAddSymbol(theEnv,name));
   sys->abstract = 1;
#if INSTANCE_PATTERN_MATCHING
   sys->reactive = 0;
#endif
   IncrementSymbolCount(sys->header.name);
   sys->installed = 1;
   sys->system = 1;
   sys->hashTableIndex = HashClass(sys->header.name);

   AddClassLink(theEnv,&sys->allSuperclasses,sys,-1);
   if (parent != NULL)
     {
      AddClassLink(theEnv,&sys->directSuperclasses,parent,-1);
      AddClassLink(theEnv,&parent->directSubclasses,sys,-1);
      AddClassLink(theEnv,&sys->allSuperclasses,parent,-1);
      for (i = 1 ; i < parent->allSuperclasses.classCount ; i++)
        AddClassLink(theEnv,&sys->allSuperclasses,parent->allSuperclasses.classArray[i],-1);
     }
   sys->nxtHash = DefclassData(theEnv)->ClassTable[sys->hashTableIndex];
   DefclassData(theEnv)->ClassTable[sys->hashTableIndex] = sys;

   /* =========================================
      Add default scope maps for a system class
      There is only one module (MAIN) so far -
      which has an id of 0
      ========================================= */
   ClearBitString((void *) defaultScopeMap,(int) sizeof(char));
   SetBitMap(defaultScopeMap,0);
#if DEFMODULE_CONSTRUCT
   sys->scopeMap = (BITMAP_HN *) AddBitMap(theEnv,(void *) defaultScopeMap,(int) sizeof(char));
   IncrementBitMapCount(sys->scopeMap);
#endif
   return(sys);
  }

/*****************************************************
  NAME         : AllocateModule
  DESCRIPTION  : Creates and initializes a
                 list of deffunctions for a new module
  INPUTS       : None
  RETURNS      : The new deffunction module
  SIDE EFFECTS : Deffunction module created
  NOTES        : None
 *****************************************************/
static void *AllocateModule(
  void *theEnv)
  {
   return((void *) get_struct(theEnv,defclassModule));
  }

/***************************************************
  NAME         : ReturnModule
  DESCRIPTION  : Removes a deffunction module and
                 all associated deffunctions
  INPUTS       : The deffunction module
  RETURNS      : Nothing useful
  SIDE EFFECTS : Module and deffunctions deleted
  NOTES        : None
 ***************************************************/
static void ReturnModule(
  void *theEnv,
  void *theItem)
  {
   FreeConstructHeaderModule(theEnv,(struct defmoduleItemHeader *) theItem,DefclassData(theEnv)->DefclassConstruct);
   DeleteSlotName(theEnv,FindIDSlotNameHash(theEnv,ISA_ID));
   DeleteSlotName(theEnv,FindIDSlotNameHash(theEnv,NAME_ID));
   rtn_struct(theEnv,defclassModule,theItem);
  }

#endif

#if (! BLOAD_ONLY) && (! RUN_TIME) && DEFMODULE_CONSTRUCT

/***************************************************
  NAME         : UpdateDefclassesScope
  DESCRIPTION  : This function updates the scope
                 bitmaps for existing classes when
                 a new module is defined
  INPUTS       : None
  RETURNS      : Nothing
  SIDE EFFECTS : Class scope bitmaps are updated
  NOTES        : None
 ***************************************************/
static void UpdateDefclassesScope(
  void *theEnv)
  {
   register unsigned i;
   DEFCLASS *theDefclass;
   int newModuleID,count;
   char *newScopeMap;
   unsigned newScopeMapSize;
   char *className;
   struct defmodule *matchModule;

   newModuleID = (int) ((struct defmodule *) EnvGetCurrentModule(theEnv))->bsaveID;
   newScopeMapSize = (sizeof(char) * ((GetNumberOfDefmodules(theEnv) / BITS_PER_BYTE) + 1));
   newScopeMap = (char *) gm2(theEnv,newScopeMapSize);
   for (i = 0 ; i < CLASS_TABLE_HASH_SIZE ; i++)
     for (theDefclass = DefclassData(theEnv)->ClassTable[i] ;
          theDefclass != NULL ;
          theDefclass = theDefclass->nxtHash)
       {
        matchModule = theDefclass->header.whichModule->theModule;
        className = ValueToString(theDefclass->header.name);
        ClearBitString((void *) newScopeMap,newScopeMapSize);
        GenCopyMemory(char,theDefclass->scopeMap->size,
                   newScopeMap,ValueToBitMap(theDefclass->scopeMap));
        DecrementBitMapCount(theEnv,theDefclass->scopeMap);
        if (theDefclass->system)
          SetBitMap(newScopeMap,newModuleID);
        else if (FindImportedConstruct(theEnv,"defclass",matchModule,
                                       className,&count,TRUE,NULL) != NULL)
          SetBitMap(newScopeMap,newModuleID);
        theDefclass->scopeMap = (BITMAP_HN *) AddBitMap(theEnv,(void *) newScopeMap,newScopeMapSize);
        IncrementBitMapCount(theDefclass->scopeMap);
       }
   rm(theEnv,(void *) newScopeMap,newScopeMapSize);
  }

#endif

#endif
