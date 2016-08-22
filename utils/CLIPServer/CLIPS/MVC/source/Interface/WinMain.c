#define BENCKE_CHANGE 

   /*******************************************************/
   /*      "C" Language Integrated Production System      */
   /*                                                     */
   /*       Microsoft Windows Version 3.0  01/31/02       */
   /*                                                     */
   /*                 WINDOWS MAIN MODULE                 */
   /*******************************************************/

/**************************************************************/
/* Purpose: Main startup functions for Windows interface.     */
/*                                                            */
/* Principal Programmer(s):                                   */
/*      Christopher J. Ortiz                                  */
/*      Gary Riley                                            */
/*                                                            */
/* Contributing Programmer(s):                                */
/*                                                            */
/* Revision History:                                          */
/*                                                            */
/**************************************************************/

/***************************************************************************/
/*                                                                         */
/* Permission is hereby granted, free of charge, to any person obtaining   */
/* a copy of this software and associated documentation files (the         */
/* "Software"), to deal in the Software without restriction, including     */
/* without limitation the rights to use, copy, modify, merge, publish,     */
/* distribute, and/or sell copies of the Software, and to permit persons   */
/* to whom the Software is furnished to do so.                             */
/*                                                                         */
/* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS */
/* OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF              */
/* MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT   */
/* OF THIRD PARTY RIGHTS. IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY  */
/* CLAIM, OR ANY SPECIAL INDIRECT OR CONSEQUENTIAL DAMAGES, OR ANY DAMAGES */
/* WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN   */
/* ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF */
/* OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.          */
/*                                                                         */
/***************************************************************************/

#include <windows.h>

#include "../clips/setup.h"

#include "../clips/commline.h"
#include "../clips/engine.h"
#include "../clips/filertr.h"
#include "../clips/router.h"
#include "../clips/sysdep.h"
#include "../clips/CLIPServer.h"

#include "StdSDK.h"    
#include "Initialization.h"  
#include "Frame.h"   
#include "resource.h"  
#include "mdi.h"
#include "Search.h"

#include "display.h"
#include "status.h"
#include "menu.h"
#include "Registry.h"


#include <winuser.h>

void UserFunctions(void);

/***************************************/
/* LOCAL INTERNAL FUNCTION DEFINITIONS */
/***************************************/

   static void                    SetUpRouters(void);
   static BOOLEAN                 QueryInterfaceRouter(char *);
   static int                     PrintInterfaceRouter(char *,char *);
   static int                     ExitInterfaceRouter(int);
   static int                     GetcInterfaceRouter(char *);
   static int                     InterfaceEventFunction(void *);
   static void                    WinRunEvent(void);

/**************************************************/
/* WinMain: Entry point for the application. This */
/*   function initializes the application and     */
/*   processes the message loop.                  */
/**************************************************/
#if IBM_TBC
#pragma argsused
#endif

int Standalone=1;

void StartListenerThread(int Port);
void Inicializar();


  int WINAPI WinMain(
  HINSTANCE hInstance, 
  HINSTANCE hPrevInstance, 
  LPSTR lpCmdLine, 
  int nCmdShow)
  {   
#if IBM_MCW
#pragma unused(hPrevInstance)
#pragma unused(lpCmdLine)
#endif
   /*=============================*/   
   /* Application initialization. */
   /*=============================*/
   int Port;

   Inicializar();

   if(strlen(lpCmdLine)>0){
      Port=atoi(lpCmdLine);
      StartListenerThread(Port);
	  Standalone=0;
   }else Standalone=1;
      
   InitializeEnvironment(); 

   if (! initInstance(hInstance,IDR_MAINFRAME,nCmdShow)) 
     { 
	   MessageBox(NULL,"Retornando NULL...","",48); 
	   return FALSE; 
   
   }
     
   /*==================================*/
   /* Setup routers for the interface. */
   /*==================================*/
   
   SetUpRouters();

   /*================================================================*/
   /* Set up hook between the command loop and interface event loop. */
   /*================================================================*/

#if ! RUN_TIME
   SetEventFunction(GetCurrentEnvironment(),InterfaceEventFunction);
#endif

   /*====================================*/
   /* Add execution functions to update  */
   /* the interface between rule firings */
   /* and execution of procedural code.  */
   /*====================================*/
   
   AddPeriodicFunction("status_wnd",WinRunEvent,0);
#if DEFRULE_CONSTRUCT
   AddRunFunction("run_function",WinRunEvent,0);
#endif

   /*======================================*/
   /* Set the focus to the display window. */
   /*======================================*/
      
   display_OnSetFocus(DialogWindow,NULL);
      
   /*=====================================*/
   /* Read preferences from the registry. */
   /*=====================================*/
   
   ReadRegistryInformation();
   
   /*====================*/
   /* Main message loop. */
   /*====================*/


   CommandLoop(GetCurrentEnvironment());
   
   return TRUE;
  }

/************************************/
/* UserFunctions: Notifies CLIPS of */
/*   any user-defined functions.    */
/************************************/
void UserFunctions()
  {
  }
  
/************************************/
/* EnvUserFunctions: Notifies CLIPS */
/*   of any user-defined functions. */
/************************************/
#if IBM_TBC
#pragma argsused
#endif
void EnvUserFunctions(
  void *theEnv)
  {
#if IBM_MCW
#pragma unused(theEnv)
#endif
  }

/**************************************/
/* SetUpRouters: Sets up routers used */
/*   by the windowed interface.       */
/**************************************/
static void SetUpRouters()
  {  
   AddRouter("InterfaceExit",60,NULL,NULL,NULL,NULL,ExitInterfaceRouter);
   AddRouter("InterfaceStdIO",10,QueryInterfaceRouter,PrintInterfaceRouter,GetcInterfaceRouter,NULL,NULL);
  }

/**************************************************/
/* ExitInterfaceRouter: Routine to  check an exit */
/*   from the dialog window to make sure that     */
/*   the user has an opportunity to save files.   */
/**************************************************/
static int ExitInterfaceRouter(
  int num)
  {   
   MSG msg;
   if (num >= 0) return(TRUE);
   
   //DoQuit();
   //AbortExit();
   //return(1);
   
   PostMessage(DialogWindow,WM_COMMAND,ID_APP_EXIT,0);
   exitInstance(&msg);
   return(FALSE); 
  }

/**********************************************************/
/* QueryInterfaceRouter: Router function which recognizes */
/*   I/O directed to the display window.                  */
/**********************************************************/
static BOOLEAN QueryInterfaceRouter(
  char *logicalName)
  {
   if ( (strcmp(logicalName,"stdout") == 0) ||
        (strcmp(logicalName,"stdin") == 0) ||
        (strcmp(logicalName,WPROMPT) == 0) ||
        (strcmp(logicalName,WTRACE) == 0) ||
        (strcmp(logicalName,WERROR) == 0) ||
        (strcmp(logicalName,WWARNING) == 0) ||
        (strcmp(logicalName,WDISPLAY) == 0) ||
        (strcmp(logicalName,WDIALOG) == 0) )
     { return(TRUE); }

    return(FALSE);
  }

/******************************************/
/* PrintInterfaceRouter: Router function  */
/*    which prints to the display window. */
/******************************************/
static int PrintInterfaceRouter(
  char *logicalName,
  char *str)
  {
   FILE *fptr;

   fptr = FindFptr(GetCurrentEnvironment(),logicalName);
   if (fptr == stdout)
     { DisplayPrint(DialogWindow,str); }
   else
     { fprintf(fptr,"%s",str); }

   return(TRUE);
  }
  
/*******************************************/
/* GetcInterfaceRouter: Router function to */
/*   get input from the display window and */
/*   process other events.                 */
/*******************************************/


static int GetcInterfaceRouter(
  char *logicalName)
  { 
   FILE *fptr;
   MSG msg;
   static int count = 0;

   if(Standalone){
   fptr = FindFptr(GetCurrentEnvironment(),logicalName);
   if (fptr != stdin) return(getc(fptr));

   UpdateCursor(QUESTION_CURSOR);
   GetMessage(&msg,NULL,0,0);
   TranslateMessage(&msg);

   while (TRUE)
     {  
      if (msg.message == WM_CHAR)
        {  
         switch(msg.wParam)
           {  
            case VK_BACK:
              GetUserCmd(DialogWindow,(WORD) msg.wParam,TRUE,(unsigned) count);
              count--;
              if (count < 0) count = 0;
              msg.wParam = '\b';
              break;

            case VK_RETURN:
              GetUserCmd(DialogWindow,(WORD) msg.wParam,TRUE,(unsigned) count);
              count = 0;
              UpdateCursor(ARROW_CURSOR);
              msg.wParam = '\n';
              break;

            default:
              count++;
              GetUserCmd(DialogWindow,(WORD) msg.wParam,TRUE,(unsigned) count);
              break;
           }
           
         return((int) msg.wParam);
        }
      
      DispatchMessage(&msg);
      UpdateCursor(QUESTION_CURSOR);
      GetMessage(&msg,NULL,0,0);
      TranslateMessage(&msg);
     }
}
return 0;
}
  
/****************************************/
/* InterfaceEventFunction: Executes one */
/*   pass of the main program loop.     */
/****************************************/
#if IBM_TBC
#pragma argsused
#endif
static int InterfaceEventFunction(
  void *theEnv)
  {  
#if IBM_MCW
#pragma unused(theEnv)
#endif
   MSG msg;
   
   UpdateCursor(ARROW_CURSOR);
   
   /*============================*/
   /* Update the status windows. */
   /*============================*/
  
   UpdateStatus();
   
   /*========================*/
   /* Update the menu items. */
   /*========================*/
   
   UpdateMenu(hMainFrame);

   /*========================*/
   /* Handle the next event. */
   /*========================*/
   
if(Standalone){
   GetMessage(&msg,NULL,0,0);
   if (! TranslateAccelerator(hMainFrame,haccel,&msg))
     {  
      TranslateMessage(&msg);
      DispatchMessage(&msg);
     }
}
   Sleep(50);
   return(TRUE);
  }
  
/******************************************************/
/* WinRunEvent: Function which is called periodically */
/*   to update the interface while rules are firing   *
/*   or procedural code is executing.                 */
/******************************************************/
static void WinRunEvent()
  {  
   MSG msg;

   UpdateStatus();
   UpdateMenu(hMainFrame);

   while (PeekMessage(&msg,NULL,0,0,PM_REMOVE))
     {  
      if (! TranslateAccelerator(hMainFrame,haccel,&msg))
        {  
         TranslateMessage(&msg);
	     DispatchMessage(&msg);
        }
     }
  }
  
