   /*******************************************************/
   /*      "C" Language Integrated Production System      */
   /*                                                     */
   /*       Microsoft Windows Version 3.0  01/31/02       */
   /*                                                     */
   /*                   REGISTRY MODULE                   */
   /*******************************************************/

/**************************************************************/
/* Purpose: Provides basic routines for storing information   */
/*   in the registry.                                         */
/*                                                            */
/* Principal Programmer(s):                                   */
/*      Gary D. Riley                                         */
/*                                                            */
/* Contributing Programmer(s):                                */
/*                                                            */
/* Revision History:                                          */
/*                                                            */
/**************************************************************/

#define _REGISTRY_SOURCE_

#include <windows.h>
#include <windowsx.h>

#include "../clips/setup.h"

#include "../clips/agenda.h"
#include "../clips/bmathfun.h"
#include "../clips/crstrtgy.h"
#include "../clips/exprnpsr.h"
#include "../clips/facthsh.h"
#include "../clips/globlcom.h"
#include "../clips/incrrset.h"
#include "../clips/watch.h"


#include "dialog1.h"

#include "Registry.h"

struct WatchInformation
  {
   boolean compilations;
   boolean facts;
   boolean instances;
   boolean rules;
   boolean genericFunctions;
   boolean messages;
   boolean deffunctions;
   boolean statistics;
   boolean globals;
   boolean slots;
   boolean activations;
   boolean methods;
   boolean focus;
   boolean messageHandlers;
  };
  
struct ExecutionInformation
  {
   int salienceEvaluation;
   int strategy;
   boolean staticConstraintChecking;
   boolean dynamicConstraintChecking;
   boolean autoFloatDividend;
   boolean resetGlobals;
   boolean factDuplication;
   boolean incrementalReset;
   boolean sequenceOperatorRecognition;
  };
  
struct PreferenceInformation
  {
   int ruleStep;
   int warnings;
   int complete;
  };

#define CURRENT_VERSION 1

/***************************************/
/* LOCAL INTERNAL FUNCTION DEFINITIONS */
/***************************************/

   static void                    RestoreWatchInformation(void);
   static void                    RestoreExecutionInformation(void);
   static void                    RestorePreferenceInformation(void);
   static DWORD                   GetVersionInformation(void);
   static void                    SaveVersionInformation(void);

/***************************************************/
/* ReadRegistryInformation:              */
/***************************************************/
void ReadRegistryInformation()
  {
   DWORD theVersion;
   
   theVersion = GetVersionInformation();
   
   if (theVersion == CURRENT_VERSION)
     {
      RestoreWatchInformation();
      RestoreExecutionInformation();
      RestorePreferenceInformation();
     }
   else if (theVersion < CURRENT_VERSION)
     {
      SaveWatchInformation();
      SaveExecutionInformation();
      SavePreferenceInformation();
     }
   else 
     {
      SaveWatchInformation();
      SaveExecutionInformation();
      SavePreferenceInformation();
     }
   
   SaveVersionInformation();
  }

/*************************************/
/* SaveWatchInformation: Saves watch */
/*   information to the registry.    */
/*************************************/
void SaveWatchInformation()
  {
   HKEY hKey;
   DWORD lpdwDisposition;
   struct WatchInformation watchInfo;
    
   if (RegCreateKeyEx(HKEY_CURRENT_USER,TEXT("Software\\CLIPS\\CLIPSWin"),0,"",0,
                      KEY_READ | KEY_WRITE,NULL,&hKey,&lpdwDisposition) != ERROR_SUCCESS)
     { return; }
              
   watchInfo.compilations = (boolean) GetWatchItem("compilations");
   watchInfo.facts = (boolean) GetWatchItem("facts");
   watchInfo.instances = (boolean) GetWatchItem("instances");
   watchInfo.rules = (boolean) GetWatchItem("rules");
   watchInfo.genericFunctions = (boolean) GetWatchItem("generic-functions");
   watchInfo.messages = (boolean) GetWatchItem("messages");
   watchInfo.deffunctions = (boolean) GetWatchItem("deffunctions");
   watchInfo.statistics = (boolean) GetWatchItem("statistics");
   watchInfo.globals = (boolean) GetWatchItem("globals");
   watchInfo.slots = (boolean) GetWatchItem("slots");
   watchInfo.activations = (boolean) GetWatchItem("activations");
   watchInfo.methods = (boolean) GetWatchItem("methods");
   watchInfo.focus = (boolean) GetWatchItem("focus");
   watchInfo.messageHandlers = (boolean) GetWatchItem("message-handlers");

   if (RegSetValueEx(hKey,"Watch",0,REG_BINARY,(BYTE *) &watchInfo,
                     sizeof(struct WatchInformation)) != ERROR_SUCCESS)
     {
      RegCloseKey(hKey);
      return;
     }

   RegCloseKey(hKey);
  }

/*********************************************/
/* SaveExecutionInformation: Saves execution */
/*   information to the registry.            */
/*********************************************/
void SaveExecutionInformation()
  {
   HKEY hKey;
   DWORD lpdwDisposition;
   struct ExecutionInformation executionInfo;
    
   if (RegCreateKeyEx(HKEY_CURRENT_USER,TEXT("Software\\CLIPS\\CLIPSWin"),0,"",0,
                      KEY_READ | KEY_WRITE,NULL,&hKey,&lpdwDisposition) != ERROR_SUCCESS)
     { return; }
              
   executionInfo.salienceEvaluation = GetSalienceEvaluation();
   executionInfo.strategy = GetStrategy();
   executionInfo.staticConstraintChecking = (boolean) GetStaticConstraintChecking();
   executionInfo.dynamicConstraintChecking = (boolean) GetDynamicConstraintChecking();
   executionInfo.autoFloatDividend = (boolean) GetAutoFloatDividend();
   executionInfo.resetGlobals = (boolean) GetResetGlobals();
   executionInfo.factDuplication = (boolean) GetFactDuplication();
   executionInfo.incrementalReset = (boolean) GetIncrementalReset();
   executionInfo.sequenceOperatorRecognition = (boolean) GetSequenceOperatorRecognition();

   if (RegSetValueEx(hKey,"Execution",0,REG_BINARY,(BYTE *) &executionInfo,
                     sizeof(struct ExecutionInformation)) != ERROR_SUCCESS)
     {
      RegCloseKey(hKey);
      return;
     }

   RegCloseKey(hKey);
  }

/***********************************************/
/* SavePreferenceInformation: Saves preference */
/*   information to the registry.              */
/***********************************************/
void SavePreferenceInformation()
  {
   HKEY hKey;
   DWORD lpdwDisposition;
   struct PreferenceInformation preferenceInfo;
    
   if (RegCreateKeyEx(HKEY_CURRENT_USER,TEXT("Software\\CLIPS\\CLIPSWin"),0,"",0,
                      KEY_READ | KEY_WRITE,NULL,&hKey,&lpdwDisposition) != ERROR_SUCCESS)
     { return; }
              
   preferenceInfo.ruleStep = RuleStep;
   preferenceInfo.warnings = Warnings;
   preferenceInfo.complete = Complete;
   
   if (RegSetValueEx(hKey,"Preference",0,REG_BINARY,(BYTE *) &preferenceInfo,
                     sizeof(struct PreferenceInformation)) != ERROR_SUCCESS)
     {
      RegCloseKey(hKey);
      return;
     }

   RegCloseKey(hKey);
  }
  
/*****************************************/
/* SaveVersionInformation: Saves version */
/*   information to the registry.        */
/*****************************************/
static void SaveVersionInformation()
  {
   HKEY hKey;
   DWORD lpdwDisposition;
   DWORD version = CURRENT_VERSION;
    
   if (RegCreateKeyEx(HKEY_CURRENT_USER,TEXT("Software\\CLIPS\\CLIPSWin"),0,"",0,
                      KEY_READ | KEY_WRITE,NULL,&hKey,&lpdwDisposition) != ERROR_SUCCESS)
     { return; }
   
   if (RegSetValueEx(hKey,"Version",0,REG_DWORD,(BYTE *) &version,
                     sizeof(DWORD)) != ERROR_SUCCESS)
     {
      RegCloseKey(hKey);
      return;
     }

   RegCloseKey(hKey);
  }
  
/*******************************************/
/* RestoreWatchInformation: Restores watch */
/*   information from the registry.        */
/*******************************************/
static void RestoreWatchInformation()
  {
   HKEY hKey;
   DWORD lpdwDisposition;
   struct WatchInformation watchInfo;
   DWORD type = REG_BINARY;
   DWORD size = sizeof(struct WatchInformation);
    
   if (RegCreateKeyEx(HKEY_CURRENT_USER,TEXT("Software\\CLIPS\\CLIPSWin"),0,"",0,
                      KEY_READ | KEY_WRITE,NULL,&hKey,&lpdwDisposition) != ERROR_SUCCESS)
     { return; }

   if (RegQueryValueEx(hKey,"Watch",0,&type,(BYTE *) &watchInfo,
                       &size) != ERROR_SUCCESS)
     {
      RegCloseKey(hKey);
      return;
     }

   SetWatchItem(GetCurrentEnvironment(),"compilations",watchInfo.compilations,NULL);
   SetWatchItem(GetCurrentEnvironment(),"facts",watchInfo.facts,NULL);
   SetWatchItem(GetCurrentEnvironment(),"instances",watchInfo.instances,NULL);
   SetWatchItem(GetCurrentEnvironment(),"rules",watchInfo.rules,NULL);
   SetWatchItem(GetCurrentEnvironment(),"generic-functions",watchInfo.genericFunctions,NULL);
   SetWatchItem(GetCurrentEnvironment(),"messages",watchInfo.messages,NULL);
   SetWatchItem(GetCurrentEnvironment(),"deffunctions",watchInfo.deffunctions,NULL);
   SetWatchItem(GetCurrentEnvironment(),"statistics",watchInfo.statistics,NULL);
   SetWatchItem(GetCurrentEnvironment(),"globals",watchInfo.globals,NULL);
   SetWatchItem(GetCurrentEnvironment(),"slots",watchInfo.slots,NULL);
   SetWatchItem(GetCurrentEnvironment(),"activations",watchInfo.activations,NULL);
   SetWatchItem(GetCurrentEnvironment(),"methods",watchInfo.methods,NULL);
   SetWatchItem(GetCurrentEnvironment(),"focus",watchInfo.focus,NULL);
   SetWatchItem(GetCurrentEnvironment(),"message-handlers",watchInfo.messageHandlers,NULL);

   RegCloseKey(hKey);
  }
  
/***************************************************/
/* RestoreExecutionInformation: Restores execution */
/*   information from the registry.                */
/***************************************************/
static void RestoreExecutionInformation()
  {
   HKEY hKey;
   DWORD lpdwDisposition;
   struct ExecutionInformation executionInfo;
   DWORD type = REG_BINARY;
   DWORD size = sizeof(struct ExecutionInformation);
    
   if (RegCreateKeyEx(HKEY_CURRENT_USER,TEXT("Software\\CLIPS\\CLIPSWin"),0,"",0,
                      KEY_READ | KEY_WRITE,NULL,&hKey,&lpdwDisposition) != ERROR_SUCCESS)
     { return; }

   if (RegQueryValueEx(hKey,"Execution",0,&type,(BYTE *) &executionInfo,
                       &size) != ERROR_SUCCESS)
     {
      RegCloseKey(hKey);
      return;
     }

   SetSalienceEvaluation(executionInfo.salienceEvaluation);
   SetStrategy(executionInfo.strategy);
   SetStaticConstraintChecking(executionInfo.staticConstraintChecking);
   SetDynamicConstraintChecking(executionInfo.dynamicConstraintChecking);
   SetAutoFloatDividend(executionInfo.autoFloatDividend);
   SetResetGlobals(executionInfo.resetGlobals);
   SetFactDuplication(executionInfo.factDuplication);
   SetIncrementalReset(executionInfo.incrementalReset);
   SetSequenceOperatorRecognition(executionInfo.sequenceOperatorRecognition);

   RegCloseKey(hKey);
  }
  
/******************************************************/
/* RestorePreferencesInformation: Restores preference */
/*   information from the registry.                   */
/******************************************************/
static void RestorePreferenceInformation()
  {
   HKEY hKey;
   DWORD lpdwDisposition;
   struct PreferenceInformation preferenceInfo;
   DWORD type = REG_BINARY;
   DWORD size = sizeof(struct PreferenceInformation);
    
   if (RegCreateKeyEx(HKEY_CURRENT_USER,TEXT("Software\\CLIPS\\CLIPSWin"),0,"",0,
                      KEY_READ | KEY_WRITE,NULL,&hKey,&lpdwDisposition) != ERROR_SUCCESS)
     { return; }

   if (RegQueryValueEx(hKey,"Preference",0,&type,(BYTE *) &preferenceInfo,
                       &size) != ERROR_SUCCESS)
     {
      RegCloseKey(hKey);
      return;
     }

   RuleStep = preferenceInfo.ruleStep;
   Warnings = preferenceInfo.warnings;
   Complete = preferenceInfo.complete;

   RegCloseKey(hKey);
  }
  
/*******************************************/
/* GetVersionInformation: Gets the version */
/*   number of the registry information.   */
/*******************************************/
static DWORD GetVersionInformation()
  {
   HKEY hKey;
   DWORD lpdwDisposition;
   DWORD version;
   DWORD type = REG_DWORD;
   DWORD size = sizeof(DWORD);
    
   if (RegCreateKeyEx(HKEY_CURRENT_USER,TEXT("Software\\CLIPS\\CLIPSWin"),0,"",0,
                      KEY_READ | KEY_WRITE,NULL,&hKey,&lpdwDisposition) != ERROR_SUCCESS)
     { return(1); }

   if (RegQueryValueEx(hKey,"Version",0,&type,(BYTE *) &version,
                       &size) != ERROR_SUCCESS)
     {
      RegCloseKey(hKey);
      return(1);
     }

   RegCloseKey(hKey);
   
   return(version);
  }
       
