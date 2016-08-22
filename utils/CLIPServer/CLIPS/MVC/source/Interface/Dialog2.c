   /*******************************************************/
   /*      "C" Language Integrated Production System      */
   /*                                                     */
   /*       Microsoft Windows Version 3.0  01/31/02       */
   /*                                                     */
   /*                   DIALOG2 MODULE                    */
   /*******************************************************/

/**************************************************************/
/* Purpose: Contains the callback functions for all the       */
/*       list manager dialog items.                           */
/*                                                            */
/* Principal Programmer(s):                                   */
/*      Christopher J. Ortiz                                  */
/*                                                            */
/* Contributing Programmer(s):                                */
/*      Gary D. Riley                                         */
/*                                                            */
/* Revision History:                                          */
/*                                                            */
/**************************************************************/

#define _DIALOG2_SOURCE_

/*-------------------------------+
| Windows & System Include Files |
+-------------------------------*/
#include <windows.h>
#include <windowsx.h>

/*------------------------+
| CLIPS 6.0 Include Files |
+------------------------*/

#include "../clips/setup.h"

#include "../clips/classcom.h"
#include "../clips/classexm.h"
#include "../clips/classinf.h"
#include "../clips/commline.h"
#include "../clips/defins.h"
#include "../clips/dffctbsc.h"
#include "../clips/dffctdef.h"
#include "../clips/dffnxfun.h"
#include "../clips/engine.h"
#include "../clips/genrccom.h"
#include "../clips/globlbsc.h"
#include "../clips/globldef.h"
#include "../clips/msgcom.h"
#include "../clips/router.h"
#include "../clips/rulebsc.h"
#include "../clips/rulecom.h"
#include "../clips/tmpltbsc.h"
#include "../clips/tmpltdef.h"

/*------------------------+
| Interface Include Files |
+------------------------*/

#include "initialization.h"
#include "resource.h"
#include "display.h"
#include "dialog2.h"

char DialogName[80];
char CompleteString[255];
struct symbolMatch *GlobalMatches;

void ShowButtons(HWND,int,int,int,int,int,unsigned,unsigned,unsigned,int);
void HandleButton(HWND,int,int );
void HandleCkBox(HWND,unsigned,int );

#define HIDE                      3
#define DISABLE                   0
#define ENABLE                    1
#define CHECK                     1
#define UNCHECK                   0
#define GRAY                      2

/************************************************/
/* HandleCkBox: procedure will update the state */ 
/*   of a check box in the list manager.        */
/************************************************/
void HandleCkBox(
  HWND hDlg, /* Pointer to the Window */
  unsigned state, /* HIDE, CHECK, UNCHECK OR GRAY */
  int ident) /* Checkbox ID */
  { 
   switch (state)
     {  
      case HIDE:
        ShowWindow(GetDlgItem(hDlg,ident),SW_HIDE);
        break;
        
      case CHECK:
      case UNCHECK:
        ShowWindow(GetDlgItem(hDlg,ident), SW_SHOW);
        EnableWindow(GetDlgItem(hDlg,ident), TRUE);
        CheckDlgButton(hDlg,ident,state );
        break;
        
      case GRAY:
        ShowWindow(GetDlgItem(hDlg,ident),SW_SHOW);
        EnableWindow(GetDlgItem(hDlg,ident),FALSE);
        CheckDlgButton(hDlg,ident,DISABLE);
        break;
     }
  }

/********************************************/
/* HandleButton: procedure will update the  */
/*   state of a button in the list manager. */
/********************************************/
void HandleButton(
  HWND hDlg,  /* Window Pointer */
  int State,  /* HIDE, ENABLE, DISABLE */
  int Ident)  /* Button ID */
  {  
   switch (State)
     {  
      case HIDE:
        ShowWindow(GetDlgItem(hDlg, Ident), SW_HIDE); 
        break;
      
      case ENABLE:
      case DISABLE:
        EnableWindow(GetDlgItem(hDlg, Ident), State);
        break;
     }
  }

/****************************************************************/
/* ShowButtons: Common procedure to Activate, Deactivate, Hide, */ 
/*   and Gray controls within the list manager dialog.          */
/****************************************************************/
void ShowButtons( 
  HWND hDlg,
  int PB1, 
  int PB2, 
  int PB3, 
  int PB4, 
  int PB5, 
  unsigned CB1,
  unsigned CB2,
  unsigned CB3,
  int PB6)
  {  
   HandleButton(hDlg,PB1,IDC_PBUTTON1);
   HandleButton(hDlg,PB2,IDC_PBUTTON2);
   HandleButton(hDlg,PB3,IDC_PBUTTON3);
   HandleButton(hDlg,PB4,IDC_PBUTTON4);
   HandleButton(hDlg,PB5,IDC_PBUTTON5);
   HandleButton(hDlg,PB6,IDC_PBUTTON6);
   HandleCkBox(hDlg,CB1,IDC_CBOX1);
   HandleCkBox(hDlg,CB2,IDC_CBOX2);
   HandleCkBox(hDlg,CB3,IDC_CBOX3);
  }

/******************************************/
/* DeffactsManager: Callback Function for */
/*   the Deffacts List Box Manager.       */
/******************************************/
#if IBM_TBC
#pragma argsused
#endif
BOOL FAR PASCAL DeffactsManager(
  HWND hDlg,
  UINT message,
  WPARAM wParam,
  LPARAM lParam)
  {
#if IBM_MCW
#pragma unused(lParam)
#endif
#if DEFFACTS_CONSTRUCT
   HWND hListBox;
   void *deffactPtr = NULL;
   int count;
   unsigned index;
   int stringsize;
   HANDLE hString;
   char *string;
   HCURSOR hSaveCursor;

   hListBox = GetDlgItem(hDlg, IDC_LISTBOX);

   switch (message)
     {
      case WM_INITDIALOG:
        count=0;
        hSaveCursor = SetCursor(hHourGlass);
        while ((deffactPtr = GetNextDeffacts(deffactPtr )) != NULL)
          {  
           count ++;
           SendMessage(hListBox, LB_ADDSTRING, 0,(LPARAM)(LPCSTR) GetDeffactsName (deffactPtr));
           //WinRunEvent();
          }
        SetCursor(hSaveCursor);
        SendMessage(hListBox, LB_SETCURSEL, 0, 0L);
        sprintf(DialogName,"Deffacts Manager - %4d Items",count);
        SetWindowText(hDlg, (LPSTR)DialogName );
        ShowButtons(hDlg,DISABLE,HIDE,HIDE,DISABLE,HIDE,HIDE,HIDE,HIDE,ENABLE);
        return(TRUE);

      case WM_COMMAND:
        switch (LOWORD(wParam))
          {  
           case IDC_PBUTTON6:
             EndDialog(hDlg,IDOK);
             return (TRUE);
         
                              /*--------*/
           case IDC_PBUTTON1: /* Remove */
           case IDC_PBUTTON4: /* PPrint */
           case IDC_LISTBOX:  /*--------*/
             index = (unsigned) SendMessage(hListBox,LB_GETCURSEL,0,0L);
             stringsize = (int) SendMessage(hListBox,LB_GETTEXTLEN,index, 0);

             if (stringsize == LB_ERR)
               break;

             hString = (HANDLE) GlobalAlloc(GMEM_FIXED,(DWORD) stringsize + 1);
             if (hString == NULL)
               {  
                EndDialog( hDlg, IDOK);
                ExitToShell();
               }

             string = (char *) GlobalLock(hString );

             if (string == NULL)   
                {
                 EndDialog( hDlg, IDOK);
                 ExitToShell();
                }
                    
             SendMessage(hListBox, LB_GETTEXT, index,(LPARAM)((LPSTR) string) );

             deffactPtr = FindDeffacts(string );

             /*----------------+
             | Remove Deffacts |
             +----------------*/
             
             if (wParam == IDC_PBUTTON1)
               {   
                Undeffacts(deffactPtr );
                SendMessage(hListBox,LB_DELETESTRING,index,0L );

                count = (int) SendMessage(hListBox,LB_GETCOUNT,0,0);
                sprintf(DialogName,"Deffacts Manager - %4d Items",count);
                SetWindowText(hDlg,(LPSTR) DialogName);

                PrintRouter(WPROMPT,"(undeffacts ");
                PrintRouter(WPROMPT,string );
                PrintRouter(WPROMPT,")\n");
                PrintPrompt(GetCurrentEnvironment());
                SetFocus(hListBox);
                //InvalidateRect (WinDialog.hWnd, NULL, TRUE );
                GlobalUnlock(hString);
                GlobalFree(hString);
                break;
               }

             /*----------------+
             | PPrint Deffacts |
             +----------------*/
             
             if (wParam == IDC_PBUTTON4)
               {  
                PrintRouter (WPROMPT, "(ppdeffacts ");
                PrintRouter (WPROMPT, string );
                PrintRouter (WPROMPT, ")\n");
                PrintRouter (WPROMPT, GetDeffactsPPForm(deffactPtr ));
                PrintPrompt (GetCurrentEnvironment());
                //InvalidateRect (WinDialog.hWnd, NULL, TRUE );
                SetFocus(hListBox);
               }
             ShowButtons(hDlg,IsDeffactsDeletable(deffactPtr),HIDE,HIDE,
                         (GetDeffactsPPForm(deffactPtr) != NULL),
                         HIDE, HIDE, HIDE, HIDE, ENABLE );
             GlobalUnlock(hString);
             GlobalFree(hString);
             return(TRUE);
          }  
        break;
     }
#endif
   return (FALSE);
  }
  
/*********************************************/
/* DeftemplateManager: Callback Function for */
/*   the Deftemplate List Box Manager.       */
/*********************************************/
#if IBM_TBC
#pragma argsused
#endif
BOOL FAR PASCAL DeftemplateManager(
  HWND hDlg,
  UINT message,
  WPARAM wParam,
  LPARAM lParam)
  {
#if IBM_MCW
#pragma unused(lParam)
#endif
#if DEFTEMPLATE_CONSTRUCT
   HWND hListBox = GetDlgItem(hDlg,IDC_LISTBOX);
   void *deftemplatePtr = NULL;
   int count=0;
   unsigned index;
   int stringsize;
   HANDLE hString;
   char *string;
   HCURSOR hSaveCursor;

   switch (message)
     {  
      case WM_INITDIALOG:
        hSaveCursor = SetCursor(hHourGlass);
        
        while ((deftemplatePtr = GetNextDeftemplate(deftemplatePtr )) != NULL)
          {  
           count ++;
           SendMessage(hListBox, LB_ADDSTRING, 0,(LPARAM)(LPCSTR) GetDeftemplateName (deftemplatePtr));
           //WinRunEvent();
          }
          
        SetCursor(hSaveCursor);
        SendMessage(hListBox, LB_SETCURSEL, 0, 0L);
        sprintf(DialogName,"Deftemplate Manager - %4d Items",count);
        SetWindowText(hDlg, (LPSTR)DialogName );
        ShowButtons(hDlg,DISABLE,HIDE,HIDE,DISABLE, HIDE,
                    GRAY, HIDE, HIDE, ENABLE);

        return (TRUE);

      case WM_COMMAND:
        switch (LOWORD(wParam))
          {  
           case IDC_PBUTTON6:
             EndDialog( hDlg, IDOK);
             return (TRUE);
        
                              /*--------*/
           case IDC_PBUTTON1: /* Remove */
           case IDC_PBUTTON4: /* PPrint */
           case IDC_CBOX1:    /* Trace  */
           case IDC_LISTBOX:  /*--------*/
             index = (unsigned) SendMessage(hListBox,LB_GETCURSEL,0,0L);
             stringsize = (int) SendMessage(hListBox,LB_GETTEXTLEN,index,0);
           
             if (stringsize == LB_ERR)
               { break; }

             hString = (HANDLE) GlobalAlloc(GMEM_FIXED,(DWORD) stringsize+1);
             if (hString == NULL)
               {  
                EndDialog( hDlg, IDOK);
                ExitToShell();
               }
               
             string = (char *) GlobalLock(hString );
             SendMessage( hListBox, LB_GETTEXT, index,(LPARAM)((LPSTR) string) );
             deftemplatePtr = FindDeftemplate(string );

             /*-------------------+
             | Remove Deftemplate |
             +-------------------*/
             
             if (wParam == IDC_PBUTTON1)
               {  
                Undeftemplate(deftemplatePtr );
                SendMessage (hListBox, LB_DELETESTRING, index, 0L );

                count = (int) SendMessage (hListBox, LB_GETCOUNT, 0, 0);
                sprintf(DialogName,"Deftemplate Manager - %4d Items",count);
                SetWindowText(hDlg, (LPSTR)DialogName );

                PrintRouter(WPROMPT, "(undeftemplate ");
                PrintRouter(WPROMPT, string );
                PrintRouter(WPROMPT, ")\n");
                PrintPrompt (GetCurrentEnvironment());
                //InvalidateRect(WinDialog.hWnd, NULL, TRUE );
                SetFocus(hListBox);
                GlobalUnlock(hString );
                GlobalFree(hString );
                break;
               }

             /*-------------------+
             | PPrint Deftemplate |
             +-------------------*/
             
             if (wParam == IDC_PBUTTON4)
               {
                PrintRouter(WPROMPT, "(ppdeftemplate ");
                PrintRouter(WPROMPT, string );
                PrintRouter(WPROMPT, ")\n");
                PrintRouter(WPROMPT, GetDeftemplatePPForm(deftemplatePtr));
                PrintPrompt(GetCurrentEnvironment());
                //InvalidateRect(WinDialog.hWnd, NULL, TRUE );
                SetFocus(hListBox);
               }

             /*-----------------+
             | Set-Remove Trace |
             +-----------------*/
             
             if (wParam == IDC_CBOX1)
               {  
                SetDeftemplateWatch (!GetDeftemplateWatch(deftemplatePtr), deftemplatePtr);
                SetFocus (hListBox);
               }

             /*-------------------+
             | Toggle Check Boxes |
             +-------------------*/
             
             GlobalUnlock(hString );
             GlobalFree(hString );
             ShowButtons(hDlg,IsDeftemplateDeletable (deftemplatePtr), HIDE, HIDE,
                         (GetDeftemplatePPForm(deftemplatePtr ) != NULL), HIDE,
                         GetDeftemplateWatch(deftemplatePtr), HIDE, HIDE, ENABLE);
             return (TRUE);
          }
        break;
     }
#endif
   return (FALSE);
  }
  
/*********************************************/
/* DeffunctionManager: Callback Function for */
/*   the Deffunction List Box Manager.       */
/*********************************************/
#if IBM_TBC
#pragma argsused
#endif
BOOL FAR PASCAL DeffunctionManager(
  HWND hDlg,
  UINT message,
  WPARAM wParam,
  LPARAM lParam)  
  {
#if IBM_MCW
#pragma unused(lParam)
#endif
#if DEFFUNCTION_CONSTRUCT
   HWND hListBox = GetDlgItem(hDlg,IDC_LISTBOX);
   void *DeffunctionPtr = NULL;
   int count = 0;
   unsigned index;
   int stringsize;
   HANDLE hString;
   char *string;
   HCURSOR hSaveCursor;

   switch (message)
     {  
      case WM_INITDIALOG:        
        hSaveCursor = SetCursor(hHourGlass);
        while ((DeffunctionPtr = GetNextDeffunction(DeffunctionPtr)) != NULL)
          {  
           count ++;
           //WinRunEvent();
           SendMessage(hListBox,LB_ADDSTRING,0,(LPARAM)(LPCSTR) GetDeffunctionName(DeffunctionPtr));
          }
        SetCursor(hSaveCursor);
        SendMessage(hListBox,LB_SETCURSEL,0,0L);
        sprintf(DialogName,"Deffunction Manager - %4d Items",count);
        SetWindowText(hDlg,(LPSTR) DialogName );
        ShowButtons(hDlg,DISABLE,HIDE,HIDE,DISABLE,HIDE,
                    GRAY,HIDE,HIDE,ENABLE);
        return(TRUE);
      
      case WM_COMMAND:
        switch (LOWORD(wParam))
          {  
           case IDC_PBUTTON6:
             EndDialog(hDlg,IDOK);
             return (TRUE);
        
                              /*--------*/
           case IDC_CBOX1:    /* Trace  */
           case IDC_PBUTTON1: /* Remove */
           case IDC_PBUTTON4: /* PPrint */
           case IDC_LISTBOX:  /*--------*/
             index = (unsigned) SendMessage(hListBox,LB_GETCURSEL,0,0L);
             stringsize = (int) SendMessage(hListBox,LB_GETTEXTLEN,index,0);
           
             if (stringsize == LB_ERR)
               { break; }

             hString = (HANDLE) GlobalAlloc(GMEM_FIXED,(DWORD) stringsize+1);
             if (hString == NULL)
               {  
                EndDialog(hDlg,IDOK);
                ExitToShell();
               }

             string = (char *) GlobalLock(hString );
             SendMessage( hListBox, LB_GETTEXT, index,(LPARAM)((LPSTR) string) );
             DeffunctionPtr = FindDeffunction(string );

             /*-------------------+
             | Remove Deffunction |
             +-------------------*/
             
             if (wParam == IDC_PBUTTON1)
               { 
                Undeffunction(DeffunctionPtr);
                SendMessage(hListBox,LB_DELETESTRING,index,0L);

                count = (int) SendMessage(hListBox,LB_GETCOUNT,0,0);
                sprintf(DialogName,"Deffunction Manager - %4d Items",count);
                SetWindowText(hDlg,(LPSTR)DialogName );

                PrintRouter(WPROMPT,"(undeffunction ");
                PrintRouter(WPROMPT,string );
                PrintRouter(WPROMPT,")\n");
                PrintPrompt(GetCurrentEnvironment());
                //InvalidateRect(WinDialog.hWnd,NULL,TRUE);
                SetFocus(hListBox);
                GlobalUnlock(hString);
                GlobalFree(hString);
                break;
               }

             /*-------------------+
             | PPrint Deffunction |
             +-------------------*/
             
             if (wParam == IDC_PBUTTON4)
               {  
                PrintRouter(WPROMPT, "(ppdeffunction ");
                PrintRouter(WPROMPT, string );
                PrintRouter(WPROMPT, ")\n");
                PrintRouter(WPROMPT, GetDeffunctionPPForm(DeffunctionPtr ));
                PrintPrompt (GetCurrentEnvironment());
                //InvalidateRect (WinDialog.hWnd, NULL, TRUE);
                SetFocus(hListBox );
               }

             /*-----------------+
             | Set-Remove Trace |
             +-----------------*/
             
             if (wParam == IDC_CBOX1)
               {  
                SetDeffunctionWatch(!GetDeffunctionWatch(DeffunctionPtr),DeffunctionPtr);
                SetFocus (hListBox);
               }

             /*-------------------+
             | Toggle Check Boxes |
             +-------------------*/
           
             ShowButtons(hDlg,IsDeffunctionDeletable(DeffunctionPtr),HIDE,HIDE,
                         (GetDeffunctionPPForm(DeffunctionPtr) != NULL),HIDE,
                         GetDeffunctionWatch(DeffunctionPtr),HIDE,HIDE,ENABLE);
             GlobalUnlock(hString);
             GlobalFree(hString);
             return (TRUE);
          }
        break;
     } 
#endif
   return (FALSE);
  }
 
/********************************************/
/* DefmethodsManager: Callback Function for */
/*   the Defmethods List Box Manager.       */
/********************************************/
BOOL FAR PASCAL DefmethodsManager(
  HWND hDlg,
  UINT message,
  WPARAM wParam,
  LPARAM lParam)
  {
#if DEFGENERIC_CONSTRUCT
   HWND hListBox = GetDlgItem(hDlg,IDC_LISTBOX);
   static void *defgenericPtr;
   unsigned theMethod = 0;
   int count=0;
   char buffer[100];
   char Name[100];
   unsigned index;
   HCURSOR hSaveCursor;

   switch (message)
     {  
      case WM_INITDIALOG:      
        defgenericPtr = (void *)lParam;
        SetWindowText(GetDlgItem (hDlg, NIDC_TEXT), "Methods for - ");
        hSaveCursor = SetCursor(hHourGlass);
        while ((theMethod = GetNextDefmethod(defgenericPtr, theMethod )) != 0)
          {  
           GetDefmethodDescription(Name,50,defgenericPtr,theMethod);
           index = (unsigned) SendMessage(hListBox,LB_ADDSTRING,0,(LPARAM)(LPCSTR) Name);
           SendMessage(hListBox,LB_SETITEMDATA,index,(LPARAM) theMethod);
           //WinRunEvent();
           count++;
          }
        SetCursor(hSaveCursor);
        SendMessage(hListBox,LB_SETCURSEL,0,0L);
        sprintf(DialogName,"Defmethod-Handler Manager - %4d Items (in precedence order)",count);
        SetWindowText(hDlg,(LPSTR)DialogName);

        sprintf(buffer,"Methods for - <%s>", GetDefgenericName (defgenericPtr));
        SetWindowText(GetDlgItem (hDlg, NIDC_TEXT), buffer);
 
        ShowButtons(hDlg,DISABLE,HIDE,HIDE,DISABLE,HIDE,
                    DISABLE, HIDE, HIDE, ENABLE);
        return (TRUE);
        

      case WM_COMMAND:
        switch (LOWORD(wParam))
          {  
           /*------*/
           /* Done */
           /*------*/
           
           case IDC_PBUTTON6:
             EndDialog( hDlg, IDOK);
             return (TRUE);
         
                              /*------------------*/
           case IDC_PBUTTON1: /* Remove Defmethod */
           case IDC_PBUTTON4: /* PPrint Defmethod */
           case IDC_CBOX1:    /* Trace Defmethod  */
           case IDC_LISTBOX:  /*------------------*/
             index = (unsigned) SendMessage(hListBox, LB_GETCURSEL,0,0L);
           
             count = (int) SendMessage(hListBox, LB_GETCOUNT, 0, 0);

             if (index == LB_ERR )
               { break; }

             theMethod = (unsigned) SendMessage(hListBox,LB_GETITEMDATA,index ,0);

             /*-------------------*/
             /* Remove Defmethod */
             /*-------------------*/
             
             if (wParam == IDC_PBUTTON1)
               {  
                count--;
                sprintf(DialogName,"Defmethod-Handler Manager - %4d Items",count);
                SetWindowText(hDlg, (LPSTR)DialogName );
        
                SendMessage (hListBox, LB_DELETESTRING, index, 0L );
                FlushCommandString(GetCurrentEnvironment());
                PrintRouter(WPROMPT, "(undefmethod ");
                PrintRouter(WPROMPT, GetDefgenericName(defgenericPtr));
                PrintRouter(WPROMPT," ");
                PrintLongInteger (GetCurrentEnvironment(),"wclips", (long int) theMethod);
                PrintRouter(WPROMPT, ")\n");
                PrintPrompt (GetCurrentEnvironment());
                Undefmethod (defgenericPtr, theMethod);
                //InvalidateRect(WinDialog.hWnd, NULL, TRUE );
                SetFocus (hListBox);
                break;
               }

             /*-------------------*/
             /* Pprint Defmethod */
             /*-------------------*/
             
             if (wParam == IDC_PBUTTON4)
               {  
                FlushCommandString(GetCurrentEnvironment());
                PrintRouter(WPROMPT, "(ppdefmethod ");
                PrintRouter(WPROMPT, GetDefgenericName(defgenericPtr));
                PrintRouter(WPROMPT," ");
                PrintLongInteger (GetCurrentEnvironment(),"wclips", (long int) theMethod);
                PrintRouter(WPROMPT, ")\n");
                PrintRouter(WPROMPT, GetDefmethodPPForm(defgenericPtr, theMethod ));
                PrintPrompt (GetCurrentEnvironment());
                //InvalidateRect(WinDialog.hWnd, NULL, TRUE );
                SetFocus (hListBox);
               }

             /*----------------------------+
             | Set-Remove Defmethod Trace |
             +----------------------------*/
             
             if (wParam == IDC_CBOX1 )
               {  
                SetDefmethodWatch(! GetDefmethodWatch(defgenericPtr, theMethod),
                                  defgenericPtr, theMethod);
                SetFocus (hListBox);
               }

             ShowButtons(hDlg,IsDefmethodDeletable(defgenericPtr,theMethod),
                         HIDE,HIDE,(GetDefmethodPPForm(defgenericPtr, theMethod)!= NULL),
                         HIDE,GetDefmethodWatch(defgenericPtr, theMethod),
                         HIDE,HIDE,ENABLE);
             return (TRUE);
          }  
        break;
     }  
#endif
   return (FALSE);
  }
 
/**********************************************/
/* DefinstancesManager: Callback Function for */
/*   the DefInstances List Box Manager.       */
/**********************************************/
#if IBM_TBC
#pragma argsused
#endif
BOOL FAR PASCAL DefinstancesManager(
  HWND hDlg,
  UINT message,
  WPARAM wParam,
  LPARAM lParam)
  {        
#if IBM_MCW
#pragma unused(lParam)
#endif
#if OBJECT_SYSTEM
   HWND hListBox = GetDlgItem(hDlg,IDC_LISTBOX);
   void *definstancesPtr = NULL;
   int count=0;
   unsigned index;
   int stringsize;
   char *string;
   HANDLE hString;   
   HCURSOR hSaveCursor;
   
   switch (message)
     {  
      case WM_INITDIALOG:
        hSaveCursor = SetCursor(hHourGlass);
        while ((definstancesPtr = GetNextDefinstances(definstancesPtr )) != NULL)
          {  
           count ++;
           //WinRunEvent();
           SendMessage(hListBox, LB_ADDSTRING, 0,(LPARAM)(LPCSTR) GetDefinstancesName (definstancesPtr));
          }
        SetCursor(hSaveCursor);
        SendMessage(hListBox, LB_SETCURSEL, 0, 0L);
        sprintf(DialogName,"Definstances Manager - %4d Items",count);
        SetWindowText(hDlg,(LPSTR) DialogName);

        ShowButtons(hDlg,DISABLE,HIDE,HIDE,DISABLE,HIDE,
                    HIDE,HIDE,HIDE,ENABLE);
        return (TRUE);
      
      case WM_COMMAND:
        switch (LOWORD(wParam))
          {  
           case IDC_PBUTTON6:
             EndDialog( hDlg, IDOK);
             return (TRUE);
         
                              /*--------*/
           case IDC_PBUTTON1: /* Remove */
           case IDC_PBUTTON4: /* PPrint */
           case IDC_LISTBOX:  /*--------*/
             index = (unsigned) SendMessage(hListBox, LB_GETCURSEL,0,0L);
             stringsize = (int) SendMessage(hListBox, LB_GETTEXTLEN, index, 0);

             if (stringsize == LB_ERR)
               { break; }

             hString = (HANDLE) GlobalAlloc(GMEM_FIXED,(DWORD) stringsize+1);
             if (hString == NULL)
               {  
                EndDialog( hDlg, IDOK);
                ExitToShell();
               }

             string = (char *) GlobalLock(hString);

             SendMessage(hListBox,LB_GETTEXT,index,(LPARAM)((LPSTR) string) );
             definstancesPtr = FindDefinstances(string);

             /*--------------------+
             | Remove Definstances |
             +--------------------*/
             
             if (wParam == IDC_PBUTTON1)
               {          
                Undefinstances(definstancesPtr);
                SendMessage(hListBox,LB_DELETESTRING,index,0L);

                count = (int) SendMessage (hListBox, LB_GETCOUNT, 0, 0);
                sprintf(DialogName,"Definstances Manager - %4d Items",count);
                SetWindowText(hDlg, (LPSTR)DialogName );

                PrintRouter(WPROMPT, "(undefinstances ");
                PrintRouter(WPROMPT, string );
                PrintRouter(WPROMPT, ")\n");
                PrintPrompt(GetCurrentEnvironment());
                //InvalidateRect( WinDialog.hWnd, NULL, TRUE );
                GlobalUnlock( hString );
                GlobalFree( hString );
                SetFocus(hListBox );
                break;
               }

             /*----------------+
             | PPrint Deffacts |
             +----------------*/
             
             if (wParam == IDC_PBUTTON4)
               { 
                PrintRouter(WPROMPT, "(ppdefinstances ");
                PrintRouter(WPROMPT, string );
                PrintRouter(WPROMPT, ")\n");
                PrintRouter(WPROMPT, GetDefinstancesPPForm(definstancesPtr ));
                PrintPrompt (GetCurrentEnvironment());
                //InvalidateRect(WinDialog.hWnd, NULL, TRUE );
                SetFocus(hListBox );
               }

             GlobalUnlock(hString );
             GlobalFree(hString );
             ShowButtons(hDlg,IsDefinstancesDeletable(definstancesPtr), 
                         HIDE,HIDE,(GetDefinstancesPPForm (definstancesPtr)!= NULL),
                         HIDE,HIDE, HIDE, HIDE, ENABLE );
             return (TRUE);
          }  
        break;
     }  
#endif
   return(FALSE);
  }

/******************************************/
/* DefclassManager: Callback Function for */ 
/*   the Defclass List Box Manager.       */
/******************************************/
#if IBM_TBC
#pragma argsused
#endif
BOOL FAR PASCAL DefclassManager(
  HWND hDlg,
  UINT message,
  WPARAM wParam,
  LPARAM lParam)   
  {
#if IBM_MCW
#pragma unused(lParam)
#endif
#if OBJECT_SYSTEM
   HWND hListBox = GetDlgItem(hDlg, IDC_LISTBOX);
   void *defclassPtr = NULL;
   int count=0;
   unsigned index;
   int stringsize;
   char *string;
   HANDLE hString;
   unsigned instances = GRAY;
   unsigned slots = GRAY;
   HCURSOR hSaveCursor;

   switch (message)
     {  
      case WM_INITDIALOG: 
        hSaveCursor = SetCursor(hHourGlass);
        while ((defclassPtr = GetNextDefclass(defclassPtr)) != NULL)
          {  
           count ++;
           //WinRunEvent();
           SendMessage(hListBox,LB_ADDSTRING, 0,(LPARAM)(LPCSTR) GetDefclassName (defclassPtr));
          }
        SetCursor(hSaveCursor);
        SendMessage(hListBox, LB_SETCURSEL, 0, 0L);
        sprintf(DialogName,"Defclass Manager - %4d Items",count);
        SetWindowText(hDlg, (LPSTR)DialogName );
        ShowButtons(hDlg, DISABLE, DISABLE ,DISABLE , DISABLE, DISABLE,
                    HIDE, GRAY, GRAY, ENABLE);
        SetWindowText(GetDlgItem (hDlg, IDC_PBUTTON2), "&Describe");
        SetWindowText(GetDlgItem (hDlg, IDC_PBUTTON3), "&Browse");
        SetWindowText(GetDlgItem(hDlg, IDC_CBOX2 ), "Watch &Instances" );
        return (TRUE);
      

      case WM_COMMAND:
        switch (LOWORD(wParam))
          {  
           /*-----+
           | Done |
           +-----*/
           
           case IDC_PBUTTON6:
             EndDialog( hDlg, IDOK);
             return (TRUE);
        
                               /*--------------------*/
           case IDC_CBOX2:    /* Trace Instances    */
           case IDC_CBOX3:    /* Trace Slot Changes */
           case IDC_PBUTTON1: /* Remove             */
           case IDC_PBUTTON2: /* Describe           */
           case IDC_PBUTTON3: /* Browse             */
           case IDC_PBUTTON4: /* PPrint             */
           case IDC_PBUTTON5: /* Message Handlers   */
           case IDC_LISTBOX:  /*--------------------*/
             index = (unsigned) SendMessage(hListBox, LB_GETCURSEL,0,0L);
             stringsize = (int) SendMessage(hListBox, LB_GETTEXTLEN, index, 0);

             if (stringsize == LB_ERR)
               { break; }

             hString = (HANDLE) GlobalAlloc(GMEM_FIXED,(DWORD) stringsize+1);
             if (hString == NULL)
               {  
                EndDialog( hDlg, IDOK);
                ExitToShell();
               }

             string = (char *) GlobalLock(hString );
             SendMessage( hListBox, LB_GETTEXT, index,(LPARAM)((LPSTR) string));
             defclassPtr = FindDefclass(string );

             /*------------------+
             | Remove Defclasses |
             +------------------*/
             
             if (wParam == IDC_PBUTTON1)
               {  
                Undefclass(defclassPtr );
                SendMessage (hListBox, LB_DELETESTRING, index, 0L );

                count = (int) SendMessage (hListBox, LB_GETCOUNT, 0, 0);
                sprintf(DialogName,"Defclasss Manager - %4d Items",count);
                SetWindowText(hDlg,(LPSTR) DialogName );

                PrintRouter(WPROMPT, "(undefclass ");
                PrintRouter(WPROMPT, string );
                PrintRouter(WPROMPT, ")\n");
                PrintPrompt (GetCurrentEnvironment());
                //InvalidateRect(WinDialog.hWnd, NULL, TRUE );
                SetFocus(hListBox );
                GlobalUnlock(hString );
                GlobalFree(hString );
                break;
               }

             /*--------------------+
             | Describe Defclasses |
             +--------------------*/
             
             if (wParam == IDC_PBUTTON2)
               {  
                PrintRouter(WPROMPT, "(describe-class ");
                PrintRouter(WPROMPT, string );
                PrintRouter(WPROMPT, ")\n");
                DescribeClass ("wclips", defclassPtr );
                PrintPrompt (GetCurrentEnvironment());
                //InvalidateRect(WinDialog.hWnd, NULL, TRUE );
               }

             /*------------------+
             | Browse Defclasses |
             +------------------*/
             
             if (wParam == IDC_PBUTTON3)
               {
                PrintRouter(WPROMPT, "(browse-classes ");
                PrintRouter(WPROMPT, string );
                PrintRouter(WPROMPT, ")\n");
                BrowseClasses ("wclips" ,defclassPtr );
                PrintPrompt (GetCurrentEnvironment());
                //InvalidateRect(WinDialog.hWnd, NULL, TRUE );
               }

             /*------------------+
             | PPrint Defclasses |
             +------------------*/
             
             if (wParam == IDC_PBUTTON4)
               { 
                PrintRouter(WPROMPT, "(ppdefclass ");
                PrintRouter(WPROMPT, string );
                PrintRouter(WPROMPT, ")\n");
                PrintRouter(WPROMPT, GetDefclassPPForm(defclassPtr ));
                PrintPrompt (GetCurrentEnvironment());
                //InvalidateRect(WinDialog.hWnd, NULL, TRUE );
               }

             /*----------------+
             | Message Handler |
             +----------------*/
           
             if (wParam == IDC_PBUTTON5)
               { 
                DialogBoxParam((HINSTANCE) GetWindowInstance(hDlg),"List_Manager2",hDlg,
                               DefmessageHandlerManager,(LPARAM) defclassPtr);
               }

             /*---------------------------+
             | Set-Remove Trace Instances |
             +---------------------------*/
             
             if (wParam == IDC_CBOX2)
               {  
                SetDefclassWatchInstances(!GetDefclassWatchInstances(defclassPtr), defclassPtr);
                SetFocus (hListBox);
               }

             /*------------------------------+
             | Set-Remove Trace Slot Changes |
             +------------------------------*/
             
             if (wParam == IDC_CBOX3)
               {  
                SetDefclassWatchSlots (!GetDefclassWatchSlots(defclassPtr), defclassPtr);
                SetFocus (hListBox);
               }

             /*-------------------+
             | Update Check Boxes |
             +-------------------*/

             if (! (ClassAbstractP(defclassPtr)))
               {  
                instances = GetDefclassWatchInstances(defclassPtr);
                slots = GetDefclassWatchSlots(defclassPtr);
               }
             ShowButtons(hDlg,IsDefclassDeletable(defclassPtr),ENABLE,ENABLE,
                         (GetDefclassPPForm(defclassPtr)!= NULL),
                         (GetNextDefmessageHandler(defclassPtr,0) != 0),
                         HIDE,instances,slots,ENABLE);
           
             GlobalUnlock(hString );
             GlobalFree(hString );
             return (TRUE);
          }  
        break;
     }  
#endif
   return (FALSE);
  }
   
/***************************************************/
/* DefmessageHandlerManager: Callback Function for */
/*   the DefmessageHandler List Box Manager.       */
/***************************************************/
BOOL FAR PASCAL DefmessageHandlerManager(
  HWND hDlg,
  UINT message,
  WPARAM wParam,
  LPARAM lParam)
  {
#if OBJECT_SYSTEM
   HWND hListBox = GetDlgItem(hDlg,IDC_LISTBOX);
   static void *defclassPtr;
   unsigned handlerIndex = 0 ;
   int count=0;
   char buffer[100];
   char Name[100];
   unsigned index;
   HCURSOR hSaveCursor;

   switch (message)
     {  
      case WM_INITDIALOG:
        defclassPtr = (void *)lParam;
        hSaveCursor = SetCursor(hHourGlass);
        while ((handlerIndex = GetNextDefmessageHandler(defclassPtr,handlerIndex)) != 0)
          {          
           sprintf(Name,"%s (%s)",
           GetDefmessageHandlerName (defclassPtr, handlerIndex),
           GetDefmessageHandlerType(defclassPtr, handlerIndex));
           index = (unsigned) SendMessage(hListBox, LB_ADDSTRING, 0,(LPARAM)(LPCSTR) Name);
           SendMessage(hListBox,LB_SETITEMDATA,index,(LPARAM) handlerIndex);
           //WinRunEvent();
           count++;
          }
        SetCursor(hSaveCursor);

        SendMessage( hListBox, LB_SETCURSEL, 0, 0L);
        sprintf( DialogName,"Defmessage-Handler Manager - %4d Items",count);

        sprintf(buffer,"Message Handler for - <%s>",GetDefclassName(defclassPtr));
        SetWindowText(GetDlgItem (hDlg, NIDC_TEXT), buffer);

        SetWindowText(hDlg, (LPSTR)DialogName );
        ShowButtons(hDlg,DISABLE,HIDE,HIDE,DISABLE,HIDE,
                    DISABLE,HIDE,HIDE,ENABLE);

        return (TRUE);
      
      case WM_COMMAND:
        switch (LOWORD(wParam))
          {  
           /*------*/
           /* Done */
           /*------*/
           
           case IDC_PBUTTON6:
             EndDialog( hDlg, IDOK);
             return (TRUE);
        
                              /*-------------------*/
           case IDC_PBUTTON1: /* Remove Defmessage */
           case IDC_PBUTTON4: /* PPrint Defmessage */
           case IDC_CBOX1:    /* Trace Defmessage  */
           case IDC_LISTBOX:  /*-------------------*/

             index = (unsigned) SendMessage(hListBox,LB_GETCURSEL,0,0L);
             count = (int) SendMessage(hListBox,LB_GETCOUNT,0,0);

             if (index == LB_ERR)
               { break; }

             handlerIndex = (unsigned) SendMessage(hListBox,LB_GETITEMDATA,index ,0);

             /*-------------------*/
             /* Remove Defmessage */
             /*-------------------*/
             
             if (wParam == IDC_PBUTTON1 )
               {
                count--;
                sprintf(DialogName,"Defmessage-Handler Manager - %4d Items",count);
                SetWindowText(hDlg, (LPSTR)DialogName );

                SendMessage (hListBox, LB_DELETESTRING, index, 0L );
                UndefmessageHandler(defclassPtr, handlerIndex);
                PrintPrompt (GetCurrentEnvironment());
                //InvalidateRect(WinDialog.hWnd, NULL, TRUE );
                SetFocus (hListBox);
                break;  
               }

             /*-------------------*/
             /* Pprint Defmessage */
             /*-------------------*/
           
             if (wParam == IDC_PBUTTON4 )
               {  
                PrintRouter(WPROMPT, "(ppdefmessage-handler ");
                PrintRouter(WPROMPT, GetDefclassName(defclassPtr));
                PrintRouter(WPROMPT," ");
                PrintRouter(WPROMPT, GetDefmessageHandlerName(defclassPtr, handlerIndex));
                PrintRouter(WPROMPT," ");
                PrintRouter(WPROMPT, GetDefmessageHandlerType(defclassPtr, handlerIndex));
                PrintRouter(WPROMPT, ")\n");
                PrintRouter(WPROMPT, GetDefmessageHandlerPPForm(defclassPtr, handlerIndex ));
                PrintPrompt (GetCurrentEnvironment());
                //InvalidateRect(WinDialog.hWnd, NULL, TRUE );
                SetFocus (hListBox);
               }

             /*----------------------------+
             | Set-Remove Defmessage Trace |
             +----------------------------*/
           
             if (wParam == IDC_CBOX1 )
               {  
                SetDefmessageHandlerWatch(! GetDefmessageHandlerWatch(defclassPtr,handlerIndex),
                                          defclassPtr, handlerIndex);
                SetFocus (hListBox);
               }

             ShowButtons(hDlg,IsDefmessageHandlerDeletable(defclassPtr, handlerIndex),
                         HIDE, HIDE,(GetDefmessageHandlerPPForm(defclassPtr, handlerIndex)!= NULL),
                         HIDE,GetDefmessageHandlerWatch(defclassPtr, handlerIndex),
                         HIDE, HIDE, ENABLE);
             return (TRUE);
        
          }  
        break;
     }  
#endif
   return (FALSE);
  }

/****************************************/
/* AgendaManager: Callback Function for */
/*   the Agenda List Box Manager.       */
/****************************************/
#if IBM_TBC
#pragma argsused
#endif
BOOL FAR PASCAL AgendaManager(
  HWND hDlg,
  UINT message,
  WPARAM wParam,
  LPARAM lParam)   
  {
#if IBM_MCW
#pragma unused(lParam)
#endif
#if DEFRULE_CONSTRUCT
   HWND hListBox = GetDlgItem(hDlg, IDC_LISTBOX);
   void *activationPtr = NULL;
   int count=0;
   char Buffer[200];
   unsigned index; 
   HCURSOR hSaveCursor;

   switch (message)
     {  
      case WM_INITDIALOG:
        ShowButtons(hDlg,DISABLE,HIDE,HIDE,DISABLE,HIDE,
                    HIDE,HIDE,HIDE,ENABLE);
        SetWindowText(GetDlgItem(hDlg,IDC_PBUTTON4),"&Fire");

        hSaveCursor = SetCursor(hHourGlass);
        while((activationPtr=GetNextActivation(activationPtr)) != NULL)
          {  
           GetActivationPPForm (Buffer, 199 ,activationPtr);
           index = (unsigned) SendMessage(hListBox, LB_ADDSTRING, 0,(LPARAM)(LPCSTR) Buffer);
           SendMessage(hListBox, LB_SETITEMDATA, (WPARAM)index, (LPARAM)activationPtr );
           //WinRunEvent();
           count++;
          }
        SetCursor(hSaveCursor);
        SendMessage(hListBox, LB_SETCURSEL, 0, 0L);
        sprintf(DialogName,"Agenda Manager - %4d Items",count);
        SetWindowText(hDlg,(LPSTR)DialogName );
        return (TRUE);

      case WM_COMMAND:
        switch (LOWORD(wParam))
          {  
           /*-----+
           | Done |
           +-----*/
           
           case IDC_PBUTTON6:
             EndDialog(hDlg,IDOK);
             return(TRUE);
         
                              /*-------------------*/
           case IDC_PBUTTON1: /* Remove Activation */
           case IDC_PBUTTON4: /* Fire Activation   */
           case IDC_LISTBOX:  /*-------------------*/
             index = (unsigned) SendMessage(hListBox, LB_GETCURSEL,0,0L);
           
             if (index == LB_ERR ) break;

             activationPtr = (void *)SendMessage (hListBox, LB_GETITEMDATA, (WPARAM)index, 0L);

             /*------------------+
             | Remove Activation |
             +------------------*/
             
             if (wParam == IDC_PBUTTON1)
               {  
                SendMessage (hListBox, LB_DELETESTRING, (WPARAM)index, 0L );
                DeleteActivation(activationPtr );

                count = (int) SendMessage(hListBox, LB_GETCOUNT, 0, 0);
                sprintf(DialogName,"Agenda Manager - %4d Items",count);
                SetWindowText(hDlg, (LPSTR)DialogName );

                //InvalidateRect (WinAgenda.hWnd, NULL, TRUE );
                SetFocus(hListBox );
               }

             /*-----+
             | Fire |
             +-----*/
             
             if (wParam == IDC_PBUTTON4)
               {  
                MoveActivationToTop(GetCurrentEnvironment(),activationPtr );
                //InvalidateRect(WinAgenda.hWnd, NULL, TRUE );
                SetAgendaChanged (FALSE);
                FlushCommandString(GetCurrentEnvironment());
                SetCommandString (GetCurrentEnvironment(),"(run 1)\n");
                PrintCLIPS ("stdout","(run 1)\n");
                SendMessage(hDlg, WM_COMMAND, IDC_PBUTTON6, 0L );
                break;
               }

             ShowButtons(hDlg,ENABLE, HIDE, HIDE,ENABLE, HIDE,
                         HIDE, HIDE, HIDE,ENABLE);
             return (TRUE);
          }  
        break;
      
     }  
#endif
   return (FALSE);
  }
  
/*******************************************/
/* DefglobalManager: Callback Function for */ 
/*   the Defglobal List Box Manager.       */
/*******************************************/
#if IBM_TBC
#pragma argsused
#endif
BOOL FAR PASCAL DefglobalManager(
  HWND hDlg,
  UINT message,
  WPARAM wParam,
  LPARAM lParam)
  {
#if IBM_MCW
#pragma unused(lParam)
#endif
#if DEFGLOBAL_CONSTRUCT
   HWND hListBox = GetDlgItem(hDlg,IDC_LISTBOX);
   int count = 0;
   void *Ptr = NULL;
   unsigned index;
   int stringsize;
   HANDLE hString;
   char *string;
   HCURSOR hSaveCursor;
     
   switch (message)
     {  
      case WM_INITDIALOG:  
        hSaveCursor = SetCursor(hHourGlass);
        while ((Ptr = GetNextDefglobal(Ptr )) != NULL)
          {  
           count ++;
           SendMessage(hListBox, LB_ADDSTRING, 0,(LPARAM)(LPCSTR) GetDefglobalName (Ptr));
           //WinRunEvent();
          }
        SetCursor(hSaveCursor);
        SendMessage(hListBox, LB_SETCURSEL, 0, 0L );
        sprintf(DialogName,"Defglobal Manager - %4d Items",count);
        SetWindowText( hDlg, (LPSTR)DialogName );
        ShowButtons(hDlg,DISABLE,HIDE,HIDE,DISABLE,HIDE,
                    HIDE, HIDE, HIDE, ENABLE);
        return (TRUE);
      
      case WM_COMMAND:
        switch (LOWORD(wParam))
          {  
           case IDC_PBUTTON6: /* Done */
             EndDialog( hDlg, IDOK);
             return (TRUE);
        
                              /*--------*/
           case IDC_PBUTTON1: /* Remove */
           case IDC_PBUTTON4: /* PPrint */
           case IDC_LISTBOX:  /*--------*/
             index = (unsigned) SendMessage(hListBox,LB_GETCURSEL,0,0L);
             stringsize = (int) SendMessage(hListBox,LB_GETTEXTLEN,index,0);

             if (stringsize == LB_ERR)
               { break; }
                
             hString = (HANDLE) GlobalAlloc(GMEM_FIXED,(DWORD) stringsize+1);
             if (hString == NULL)
               {  
                EndDialog( hDlg, IDOK);
                ExitToShell();
               }

             string =  (char *) GlobalLock(hString );
             SendMessage( hListBox, LB_GETTEXT, index,(LPARAM)((LPSTR) string) );
             Ptr = FindDefglobal(string );

             if (Ptr == NULL) break;
             
             /*-----------------+
             | Remove Defglobal |
             +-----------------*/
             
             if (wParam == IDC_PBUTTON1)
               {     
                Undefglobal(Ptr );
                SendMessage (hListBox, LB_DELETESTRING, index, 0L );

                count = (int)  SendMessage (hListBox, LB_GETCOUNT, 0, 0);
                sprintf(DialogName,"Defglobal Manager - %4d Items",count);
                SetWindowText(hDlg, (LPSTR)DialogName );

                PrintRouter(WPROMPT, "(undefglobal ");
                PrintRouter(WPROMPT, string );
                PrintRouter(WPROMPT, ")\n");
                PrintPrompt (GetCurrentEnvironment());
                //InvalidateRect (WinDialog.hWnd, NULL, TRUE );
                SetFocus (hListBox);
                GlobalUnlock(hString );
                GlobalFree(hString );
                break;
               }

             /*-----------------+
             | PPrint Defglobal |
             +-----------------*/
           
             if (wParam == IDC_PBUTTON4)
               {  
                PrintRouter(WPROMPT, "(ppdefglobal ");
                PrintRouter(WPROMPT, string );
                PrintRouter(WPROMPT, ")\n");
                PrintRouter(WPROMPT, GetDefglobalPPForm(Ptr ));
                PrintPrompt (GetCurrentEnvironment());
                //InvalidateRect (WinDialog.hWnd, NULL, TRUE );
                SetFocus (hListBox);
               }

             /*-------------------+
             | Update Check Boxes |
             +-------------------*/
            
             ShowButtons(hDlg,IsDefglobalDeletable (Ptr), HIDE, HIDE,
                         (GetDefglobalPPForm (Ptr) != NULL),
                         HIDE, HIDE, HIDE, HIDE, ENABLE);
             GlobalUnlock(hString );
             GlobalFree(hString );
             return (TRUE);
          }
        break; 
     }
#endif

   return (FALSE);
  }
  
/*****************************************/
/* DefruleManager: Callback Function for */
/*   the Defrule List Box Manager.       */
/*****************************************/
#if IBM_TBC
#pragma argsused
#endif
BOOL FAR PASCAL DefruleManager(
  HWND hDlg,
  UINT message,
  WPARAM wParam,
  LPARAM lParam)
  {
#if IBM_MCW
#pragma unused(lParam)
#endif
#if DEFRULE_CONSTRUCT
   HWND hListBox = GetDlgItem(hDlg,IDC_LISTBOX);
   int count = 0;
   void *defrulePtr = NULL;
   unsigned index;
   int stringsize;
   HANDLE hString;
   char *string;
   HCURSOR hSaveCursor;
   
   switch (message)
     {  
      case WM_INITDIALOG:
        { 
         hSaveCursor = SetCursor(hHourGlass);
         while ((defrulePtr = GetNextDefrule(defrulePtr )) != NULL)
           {  
            count ++;
            SendMessage(hListBox,LB_ADDSTRING,0,(LPARAM)(LPCSTR) GetDefruleName (defrulePtr));
            //WinRunEvent();
           }
         SetCursor(hSaveCursor);
         SendMessage(hListBox,LB_SETCURSEL,0,0L);
         sprintf(DialogName,"Defrule Manager - %4d Items",count);
         SetWindowText(hDlg,(LPSTR) DialogName);
         ShowButtons(hDlg,DISABLE,DISABLE,DISABLE,DISABLE,HIDE,
                     GRAY,GRAY,GRAY,ENABLE);
         SetWindowText(GetDlgItem(hDlg,IDC_CBOX1 ),"&Breakpoint");
         SetWindowText(GetDlgItem(hDlg,IDC_CBOX3 ),"Watch &Firings");
         return (TRUE);
        }

      case WM_COMMAND:
        switch (LOWORD(wParam))
          {
           case IDC_PBUTTON6:
             EndDialog( hDlg, IDOK);
             return (TRUE);
        
                               /*------------------------------*/
           case IDC_PBUTTON1: /* Remove                       */
           case IDC_PBUTTON2: /* Refresh                      */
           case IDC_PBUTTON3: /* Matches                      */
           case IDC_PBUTTON4: /* PPrint                       */ 
           case IDC_CBOX1:    /* Remove-Set Break             */
           case IDC_CBOX2:    /* Remove-Set Trace Activations */
           case IDC_CBOX3:    /* Remove-Set Trace Firings     */
           case IDC_LISTBOX:  /*------------------------------*/
             index = (unsigned) SendMessage(hListBox, LB_GETCURSEL,0,0L);
             stringsize = (int) SendMessage(hListBox, LB_GETTEXTLEN, index, 0);
           
             if (stringsize == LB_ERR)
               { break; }
                
             hString = (HANDLE) GlobalAlloc(GMEM_FIXED,(DWORD) stringsize+1);
             if (hString == NULL)
               {  
                EndDialog( hDlg, IDOK);
                ExitToShell();
               }

             string = (char *) GlobalLock(hString);
             SendMessage(hListBox,LB_GETTEXT,index,(LPARAM)((LPSTR) string));
             defrulePtr = FindDefrule(string);

             if (defrulePtr == NULL) break;
             
             /*---------------+
             | Remove Defrule |
             +---------------*/
             
             if (wParam == IDC_PBUTTON1)
               { 
                Undefrule(defrulePtr );
                SendMessage (hListBox, LB_DELETESTRING, index, 0L );

                count = (int)  SendMessage(hListBox,LB_GETCOUNT,0,0);
                sprintf(DialogName,"Defrule Manager - %4d Items",count);
                SetWindowText(hDlg,(LPSTR) DialogName);

                PrintRouter(WPROMPT, "(undefrule ");
                PrintRouter(WPROMPT, string );
                PrintRouter(WPROMPT, ")\n");
                PrintPrompt (GetCurrentEnvironment());
                //InvalidateRect (WinDialog.hWnd, NULL, TRUE );
                SetFocus(hListBox);
                GlobalUnlock(hString);
                GlobalFree(hString);
                break;
               }

             /*----------------+
             | Refresh Defrule |
             +----------------*/
             
             if (wParam == IDC_PBUTTON2)
               {  
                Refresh(defrulePtr);
                PrintRouter(WPROMPT, "(refresh ");
                PrintRouter(WPROMPT, string );
                PrintRouter(WPROMPT, ")\n");
                PrintPrompt (GetCurrentEnvironment());
                //InvalidateRect (WinDialog.hWnd, NULL, TRUE );
                SetFocus(hListBox);
               }

             /*--------------+
             | Match Defrule |
             +--------------*/
             
             if (wParam == IDC_PBUTTON3)
               {  
                PrintRouter(WPROMPT,"(matches ");
                PrintRouter(WPROMPT,string );
                PrintRouter(WPROMPT,")\n");
                Matches(defrulePtr);
                PrintPrompt(GetCurrentEnvironment());
                //InvalidateRect(WinDialog.hWnd,NULL,TRUE);
                SetFocus(hListBox);
               }

             /*---------------+
             | PPrint Defrule |
             +---------------*/
             
             if (wParam == IDC_PBUTTON4)
               {  
                PrintRouter(WPROMPT, "(ppdefrule ");
                PrintRouter(WPROMPT, string );
                PrintRouter(WPROMPT, ")\n");
                PrintRouter(WPROMPT, GetDefrulePPForm(defrulePtr ));
                PrintPrompt(GetCurrentEnvironment());
                //InvalidateRect(WinDialog.hWnd,NULL,TRUE);
                SetFocus(hListBox);
               }

             /*----------------------------+
             | Set-Remove Break on Defrule |
             +----------------------------*/
             
             if (wParam == IDC_CBOX1)
               {  
                if (RemoveBreak(defrulePtr))
                  {  
                   RemoveBreak(defrulePtr);
                   PrintRouter(WPROMPT, "(remove-break ");
                  }
                else
                  {  
                   SetBreak(defrulePtr);
                   PrintRouter(WPROMPT, "(set-break ");
                  }
               
                PrintRouter(WPROMPT, string );
                PrintRouter(WPROMPT, ")\n");
                PrintPrompt(GetCurrentEnvironment());
                SetFocus(hListBox);
               }

             /*----------------------------+
             | Set-Remove Trace Activation |
             +----------------------------*/
             
             if (wParam == IDC_CBOX2)
               {  
                SetDefruleWatchActivations(! GetDefruleWatchActivations(defrulePtr),defrulePtr);
                SetFocus (hListBox);
               }

             /*-------------------------+
             | Set-Remove Trace Firings |
             +-------------------------*/
             
             if (wParam == IDC_CBOX3)
               {  
                SetDefruleWatchFirings (!GetDefruleWatchFirings(defrulePtr), defrulePtr);
                SetFocus (hListBox);
               }

             /*-------------------+
             | Update Check Boxes |
             +-------------------*/
             
             ShowButtons(hDlg,IsDefruleDeletable(defrulePtr),ENABLE,ENABLE,
                         (GetDefrulePPForm(defrulePtr) != NULL),
                         HIDE,(unsigned) DefruleHasBreakpoint(defrulePtr),
                         GetDefruleWatchActivations(defrulePtr),
                         GetDefruleWatchFirings(defrulePtr),ENABLE);
             GlobalUnlock(hString);
             GlobalFree(hString);
             return (TRUE);
          }
     
        break;
     }  
#endif
   return (FALSE);
  }
  
/********************************************/
/* DefgenericManager: Callback Function for */ 
/*   the Defgeneric List Box Manager.       */
/********************************************/
#if IBM_TBC
#pragma argsused
#endif
BOOL FAR PASCAL DefgenericManager(
  HWND hDlg,
  UINT message,
  WPARAM wParam,
  LPARAM lParam)
  {
#if IBM_MCW
#pragma unused(lParam)
#endif
#if DEFGENERIC_CONSTRUCT
   HWND hListBox = GetDlgItem(hDlg,IDC_LISTBOX);
   void *defPtr = NULL;
   int count=0;
   unsigned index;
   int stringsize;
   char *string;
   HANDLE hString;
   HCURSOR hSaveCursor;

   switch (message)
     {  
      case WM_INITDIALOG:
        hSaveCursor = SetCursor(hHourGlass);
        while ((defPtr = GetNextDefgeneric(defPtr )) != NULL)
          {  
           count ++;
           //WinRunEvent();
           SendMessage(hListBox,LB_ADDSTRING,0,(LPARAM)(LPCSTR) GetDefgenericName(defPtr));
          }
        SetCursor(hSaveCursor);
        SendMessage(hListBox, LB_SETCURSEL, 0, 0L);
        sprintf(DialogName,"Defgeneric Manager - %4d Items",count);
        SetWindowText(hDlg, (LPSTR)DialogName );
        ShowButtons(hDlg, DISABLE, HIDE ,HIDE , DISABLE, DISABLE,
                    GRAY, HIDE, HIDE, ENABLE);
        SetWindowText(GetDlgItem (hDlg, IDC_PBUTTON5), "&Methods");
        return (TRUE);

      case WM_COMMAND:
        switch (LOWORD(wParam))
          {  
           /*-----+
           | Done |
           +-----*/
           case IDC_PBUTTON6:
             EndDialog( hDlg, IDOK);
             return (TRUE);
        
                              /*---------*/
           case IDC_CBOX1:    /* Trace   */
           case IDC_PBUTTON1: /* Remove  */
           case IDC_PBUTTON4: /* PPrint  */
           case IDC_PBUTTON5: /* Methods */
           case IDC_LISTBOX:  /*---------*/
             index = (unsigned) SendMessage(hListBox, LB_GETCURSEL,0,0L);
             stringsize = (int) SendMessage(hListBox, LB_GETTEXTLEN, index, 0);
           
             if (stringsize == LB_ERR)
               { break; }

             hString = (HANDLE) GlobalAlloc(GMEM_FIXED,(DWORD) stringsize+1);
             if (hString == NULL)
               {  
                EndDialog( hDlg, IDOK);
                ExitToShell();
               }

             string = (char *) GlobalLock(hString );
             SendMessage( hListBox, LB_GETTEXT, index,(LPARAM)((LPSTR) string));
             defPtr = FindDefgeneric(string );

             /*------------------+
             | Remove Defgeneric |
             +------------------*/
             
             if (wParam == IDC_PBUTTON1 )
               { 
                Undefgeneric(defPtr );
                SendMessage (hListBox, LB_DELETESTRING, index, 0L );

                count = (int) SendMessage (hListBox, LB_GETCOUNT, 0, 0);
                sprintf(DialogName,"Defgenerics Manager - %4d Items",count);
                SetWindowText(hDlg, (LPSTR)DialogName );

                PrintRouter(WPROMPT, "(undefgeneric ");
                PrintRouter(WPROMPT, string );
                PrintRouter(WPROMPT, ")\n");
                PrintPrompt (GetCurrentEnvironment());
                //InvalidateRect(WinDialog.hWnd, NULL, TRUE );
                SetFocus( hListBox );
                GlobalUnlock(hString );
                GlobalFree(hString );
                break;
               }

             /*------------------+
             | PPrint Defgeneric |
             +------------------*/
             
             if (wParam == IDC_PBUTTON4)
               {  
                PrintRouter(WPROMPT, "(ppdefgeneric ");
                PrintRouter(WPROMPT, string );
                PrintRouter(WPROMPT, ")\n");
                PrintRouter(WPROMPT, GetDefgenericPPForm(defPtr ));
                PrintPrompt (GetCurrentEnvironment());
                //InvalidateRect(WinDialog.hWnd, NULL, TRUE );
               }

             /*----------------+
             | Methods Handler |
             +----------------*/
             
             if (wParam == IDC_PBUTTON5 )
               {
                DialogBoxParam((HINSTANCE) GetWindowInstance(hDlg),"List_Manager2",hDlg,
                               DefmethodsManager, (LPARAM)defPtr);
               }

             /*---------------------------+
             | Set-Remove Trace Instances |
             +---------------------------*/
             
             if (wParam == IDC_CBOX1)
               {  
                SetDefgenericWatch (!GetDefgenericWatch(defPtr), defPtr);
                SetFocus (hListBox);
               }

             /*-------------------+
             | Update Check Boxes |
             +-------------------*/
             
             ShowButtons(hDlg,IsDefgenericDeletable(defPtr), HIDE, HIDE,
                         (GetDefgenericPPForm(defPtr)!= NULL),
                         (GetNextDefmethod(defPtr,0) != 0),
                         GetDefgenericWatch(defPtr),HIDE,HIDE,ENABLE);
             GlobalUnlock(hString );
             GlobalFree(hString );
             return (TRUE);
        
          }  
     }
#endif
   return (FALSE);
  }

/******************************************/
/* CommandComplete: Callback Function for */ 
/*   the Command Complete Dialog.         */
/******************************************/
BOOL FAR PASCAL CommandComplete(
  HWND hDlg,
  UINT message,
  WPARAM wParam,
  LPARAM lParam)
  {  
   HWND hListBox = GetDlgItem(hDlg,IDC_LISTBOX);
   int count = 0;
   void *symbolPtr;
   LPSTR foo;
   unsigned index;
   HCURSOR hSaveCursor;

   switch (message)
     {  
      case WM_INITDIALOG:
        CompleteString[0] = '\0';
        symbolPtr = GlobalMatches;
        hSaveCursor = SetCursor(hHourGlass);
        while (symbolPtr != NULL)
          {  
           count ++;
           foo = (LPSTR)ValueToString (((struct symbolMatch *)symbolPtr)->match);
           SendMessage(hListBox,LB_ADDSTRING,0,(LPARAM) foo);
           //WinRunEvent();
           symbolPtr = (((struct symbolMatch *)symbolPtr)->next);
          }
        SetCursor(hSaveCursor);
        SendMessage(hListBox, LB_SETCURSEL,0, 0L);
        sprintf(DialogName,"Command Completion for \"%s\" - %4d Items",(LPSTR)lParam, count);
        SetWindowText( hDlg, (LPSTR)DialogName );
        ShowButtons(hDlg,ENABLE, HIDE, HIDE, ENABLE, HIDE,
                    HIDE, HIDE, HIDE, ENABLE );
        SetWindowText(GetDlgItem(hDlg, IDC_PBUTTON6 ), "&OK" );
        SetWindowText(GetDlgItem(hDlg, IDC_PBUTTON4 ), "&Cancel" );
        SetWindowText(GetDlgItem(hDlg, IDC_PBUTTON1 ), "&Help" );

        return (TRUE);
      

      case WM_COMMAND:
        switch (LOWORD(wParam))
          {  
           /*-------+
           | Cancel |
           +-------*/
       
           case IDC_PBUTTON4:
             CompleteString[0] = '\0';
             EndDialog( hDlg, IDC_CANCEL);
             return (TRUE);
          

           /*------------+
           | Help Button |
           +------------*/
       
           case IDC_PBUTTON1:
             index = (unsigned) SendMessage(hListBox, LB_GETCURSEL, 0, 0L);
             SendMessage(hListBox,LB_GETTEXT,index,(LPARAM)(CompleteString));
             WinHelp (hDlg, "CLIPS6.HLP", HELP_KEY, (DWORD) CompleteString);
             return (TRUE);
       

           case IDC_LISTBOX:
             if (HIWORD(lParam) != LBN_DBLCLK)
             break;

          /*----------+
          | OK Button |
          +----------*/
          
          case IDC_PBUTTON6:
            index = (unsigned) SendMessage(hListBox, LB_GETCURSEL, 0, 0L );
            SendMessage(hListBox,LB_GETTEXT,index,(LPARAM)(CompleteString));
            EndDialog  (hDlg, IDC_OK);
            return (TRUE);
       
          }  
        break;
     }  
   return (FALSE);
  }

