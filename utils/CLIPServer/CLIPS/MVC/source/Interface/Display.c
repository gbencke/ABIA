   /*******************************************************/
   /*      "C" Language Integrated Production System      */
   /*                                                     */
   /*       Microsoft Windows Version 3.0  01/31/02       */
   /*                                                     */
   /*                   DISPLAY MODULE                    */
   /*******************************************************/

/**************************************************************/
/* Purpose:                                                   */
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

#define _DISPLAY_SOURCE_

#include "StdSDK.h"     // Standard application includes
#include <tchar.h>
#include <windowsx.h>

#include "mdi.h"
#include "display.h"
#include "Initialization.h"
#include "../clips/setup.h"

#include "../clips/commline.h" 

#include "../clips/router.h"
#include "resource.h"
#include "menucmds.h"
#include "dialog2.h"
#include "print.h"
#include "frame.h"
#include "findwnd.h"

#define setProc(hwnd, proc) SetWindowLong(hwnd, GWL_USERDATA, (LPARAM)proc)
#define getProc(hwnd)       (WNDPROC)GetWindowLong(hwnd, GWL_USERDATA)

/***************************************/
/* LOCAL INTERNAL FUNCTION DEFINITIONS */
/***************************************/

   static void                    display_OnClose(HWND);
   static void                    FreeTerminalText(HWND);
   static void                    DeleteTerminal(HWND);
   static void                    RedrawTerminal(HWND,HDC); 
   static void                    SendToScreen(HWND);
   static void                    display_OnSize(HWND,UINT,int,int);
   static void                    display_OnVScroll(HWND,HWND,UINT,int);
   static void                    display_OnHScroll(HWND,HWND,UINT,int);
   static void                    display_OnKillFocus(HWND,HWND);
   static void                    display_OnChar(HWND,TCHAR,int);
   static void                    display_OnMDIActivate(HWND,BOOL,HWND,HWND);
   static void                    display_OnCommand(HWND,int,HWND,UINT);
   static BOOL                    display_OnContextMenu(HWND,HWND,int,int);
   static void                    display_OnTimer(HWND,UINT);
   static void                    display_OnEnterSizeMove(HWND);
   static void                    CreateTerminal(struct displayWindowData *);
   static void                    SaveDisplayWindow(HWND);

/****************************************/
/* GLOBAL INTERNAL VARIABLE DEFINITIONS */
/****************************************/

   globle HWND                 DialogWindow = NULL;
   globle ATOM                 DisplayAtomClass;

/***************************************/
/* LOCAL INTERNAL VARIABLE DEFINITIONS */
/***************************************/

   static HMENU                DisplayMenu = NULL;
   static HMENU                DisplayWindowMenu = NULL;
   static HMENU                DisplayContextMenu = NULL;
   static int                  OriginalPosition = 0;
  
/*********************/
/* displayWndProc:   */
/*********************/
LRESULT CALLBACK displayWndProc(
  HWND hwnd, 
  UINT message, 
  WPARAM wParam, 
  LPARAM lParam)
  {
   switch(message)
     { 
      HANDLE_MSG(hwnd,WM_COMMAND,display_OnCommand);
      HANDLE_MSG(hwnd,WM_CLOSE,display_OnClose);
      HANDLE_MSG(hwnd,WM_SIZE,display_OnSize);
      HANDLE_MSG(hwnd,WM_ENTERSIZEMOVE,display_OnEnterSizeMove);
      HANDLE_MSG(hwnd,WM_PAINT,display_OnPaint);
      HANDLE_MSG(hwnd,WM_VSCROLL,display_OnVScroll);
      HANDLE_MSG(hwnd,WM_HSCROLL,display_OnHScroll);
      HANDLE_MSG(hwnd,WM_SETFOCUS,display_OnSetFocus);
      HANDLE_MSG(hwnd,WM_KILLFOCUS,display_OnKillFocus);
      HANDLE_MSG(hwnd,WM_CHAR,display_OnChar);
      HANDLE_MSG(hwnd,WM_MDIACTIVATE,display_OnMDIActivate);
	  HANDLE_MSG(hwnd,WM_CONTEXTMENU,display_OnContextMenu);
      HANDLE_MSG(hwnd,WM_DESTROY,DeleteTerminal);
      HANDLE_MSG(hwnd,WM_TIMER,display_OnTimer);
	 } 
 	  	  
   return DefMDIChildProc(hwnd,message,wParam,lParam);
  }

/************************************************/
/* display_OnChar:     */
/************************************************/
#if IBM_TBC
#pragma argsused
#endif
static void display_OnChar(
  HWND hwnd, 
  TCHAR ch, 
  int cRepeat)
  {
#if IBM_MCW
#pragma unused(cRepeat)
#endif
   GetUserCmd(hwnd,ch,FALSE,0);
  }
  
/*************************************************************/
/* display_InitInstance: Registers the display window class, */
/*   and loads its menu to a local static variable. Returns  */
/*   TRUE if successful, otherwise FALSE.                    */
/*************************************************************/
BOOL display_InitInstance(
  HINSTANCE hinst)
  {
   if (! (DisplayAtomClass = registerMDIChild(hinst,CS_NOCLOSE,IDR_DISPLAY,displayWndProc,sizeof(WORD))))
     { return FALSE; }

   DisplayMenu = LoadMenu(hinst,MAKEINTRESOURCE(IDR_CLIPS));

   if (DisplayMenu == NULL)
     { return FALSE; }
   
   DisplayContextMenu = LoadMenu(hinst,MAKEINTRESOURCE(IDR_DISPLAY_CONTEXT));

   DisplayWindowMenu = findWindowMenu(DisplayMenu);

   return TRUE;
  }

/*********************************************************/
/* TileDisplayWindow:  */
/*********************************************************/
void TileDisplayWindow()
  {
   RECT rect;
   int width, height;
   int xpos, ypos, wwidth, wheight;
   
   GetClientRect(hMainFrame,&rect);
   width = rect.right - rect.left;
   height = AvailableFrameHeight();
   
   xpos = 2;
   ypos = 2;
   wwidth = (int) (width * 0.66) - 5;
   wheight = (int)(height * 0.66) - 5;

   MoveWindow(DialogWindow,xpos,ypos,wwidth,wheight,TRUE);
  }

/*********************************************************/
/* displayWindow_New:  */
/*********************************************************/
BOOL displayWindow_New(
  HWND hwnd)
  {
   HDC hDC;
   RECT rect;
   int width, height;
   struct displayWindowData *theData;
   TEXTMETRIC tm;
   LOGFONT lf;
   HFONT hfont;

   theData = malloc(sizeof(struct displayWindowData));
   
   if (theData == NULL)
     { return(FALSE); }

   GetClientRect(hwnd,&rect);
   width = rect.right - rect.left;
   height = AvailableFrameHeight();
   
   /*===========================*/
   /* Create the dialog window. */
   /*===========================*/
   
   DialogWindow = mdi_Create(MDIClientWnd,0,IDR_DISPLAY,"Dialog Window");
   
   if (DialogWindow == NULL) 
     {       
      free(theData);
      return(FALSE); 
     }
     
   hDC = GetDC(DialogWindow);
   
   memset(&lf,0,sizeof(LOGFONT));
   lf.lfHeight = -13;
   strcpy(lf.lfFaceName,"Courier New");
   hfont = CreateFontIndirect(&lf);
   if (hfont != NULL)
     { SelectObject(hDC,hfont); }   

   GetTextMetrics(hDC,&tm);
   ReleaseDC(DialogWindow,hDC);
   if (hfont != NULL)
     { DeleteFont(hfont); }  
     
   GetClientRect(DialogWindow,(LPRECT) &rect);

   theData->lastLine = 0;
   theData->oldLine = 0;
   theData->horizScroll = 0;
   theData->caretX = 0;
   theData->caretY = 0;
   theData->lineSize = tm.tmHeight+ tm.tmExternalLeading;
   theData->noLines  = (rect.bottom / theData->lineSize) - 1;
   CreateTerminal(theData);

   SetWindowLong(DialogWindow,GWL_USERDATA,(long) theData);

   SetScrollRange(DialogWindow,SB_HORZ,0,255,TRUE);

   MoveWindow(DialogWindow,2,2,(int) (width * 0.66) - 5,(int) (height * 0.66) - 5,TRUE);
   
   return(TRUE);
  }
    
/******************************************/
/* display_OnUpdateMenu: Updates the menu */
/*   based on the display window state.   */
/******************************************/
#if IBM_TBC
#pragma argsused
#endif
static void display_OnUpdateMenu(
  HWND hdisplay,
  HMENU hmenu)
  {   
#if IBM_MCW
#pragma unused(hdisplay)
#endif
   EnableMenuItem(hmenu,ID_EDIT_PASTE, (unsigned)
                  MF_BYCOMMAND | 
                  (IsClipboardFormatAvailable(CF_TEXT) ? MF_ENABLED : MF_GRAYED));

   EnableMenuItem(hmenu,ID_FILE_CLOSE,MF_GRAYED);
   EnableMenuItem(hmenu,ID_FILE_SAVE,MF_ENABLED);
   EnableMenuItem(hmenu,ID_FILE_SAVE_AS,MF_ENABLED);

   EnableMenuItem(hmenu,ID_EDIT_UNDO,MF_GRAYED);
   EnableMenuItem(hmenu,ID_EDIT_CUT,MF_GRAYED);
   EnableMenuItem(hmenu,ID_EDIT_COPY,MF_GRAYED);
   EnableMenuItem(hmenu,ID_EDIT_CLEAR,MF_GRAYED);
   EnableMenuItem(hmenu,ID_EDIT_SELECT_ALL,MF_GRAYED);
   EnableMenuItem(hmenu,ID_EDIT_BALANCE,MF_GRAYED);
   EnableMenuItem(hmenu,ID_EDIT_COMMENT,MF_GRAYED);
   EnableMenuItem(hmenu,ID_EDIT_UNCOMMENT,MF_GRAYED);
   EnableMenuItem(hmenu,ID_EDIT_SET_FONT,MF_GRAYED);

   EnableMenuItem(hmenu,ID_BUFFER_FIND,MF_GRAYED);
   EnableMenuItem(hmenu,ID_BUFFER_REPLACE,MF_GRAYED);
   EnableMenuItem(hmenu,ID_BUFFER_LOAD,MF_GRAYED);
   EnableMenuItem(hmenu,ID_BUFFER_BATCH,MF_GRAYED);
   EnableMenuItem(hmenu,ID_BUFFER_LOAD_BUFFER,MF_GRAYED);
   
   PostMessage(hMainFrame,UWM_UPDATE_TOOLBAR,0,0);
  }

/************************************************/
/* display_OnClose: Overrides the close handler */
/*   so the display window can't be closed.     */
/************************************************/
#if IBM_TBC
#pragma argsused
#endif
static void display_OnClose(
  HWND hwnd)
  {
#if IBM_MCW
#pragma unused(hwnd)
#endif
   return;
  }
 
/************************************************/
/* display_OnEnterSizeMove:    */
/************************************************/
static void display_OnEnterSizeMove(
  HWND hwnd)
  {
   OriginalPosition = GetScrollPos(hwnd,SB_VERT);
  }

/************************************/
/* display_OnSize: Handles resizing */
/*   of the dialog window.          */
/************************************/
#if IBM_TBC
#pragma argsused
#endif
static void display_OnSize(
  HWND hwnd, 
  UINT state, 
  int cx, 
  int cy)
  {
#if IBM_MCW
#pragma unused(cx)
#pragma unused(state)
#endif
   int min, max;
   struct displayWindowData *theData;
   
   theData = (struct displayWindowData *) GetWindowLong(hwnd,GWL_USERDATA);
   
   if (theData == NULL) 
     { return; }

   GetScrollRange(hwnd,SB_VERT,&min,&max);
     
   theData->noLines = (cy / theData->lineSize) - 1;
   
   if (max < DIALOG_SIZE) 
     { max = theData->lastLine; }
   else 
     { max = DIALOG_SIZE; }
   
   if (max > theData->noLines)
     { 
      SetScrollRange(hwnd,SB_VERT,theData->noLines,max,FALSE); 
      SetScrollPos(hwnd,SB_VERT,OriginalPosition,TRUE);
     }
   else
     { 
      SetScrollRange(hwnd,SB_VERT,0,0,FALSE); 
      SetScrollPos(hwnd,SB_VERT,0,TRUE);
     }

   InvalidateRect(hwnd,NULL,TRUE);
   
   FORWARD_WM_SIZE(hwnd,state,cx,cy,DefMDIChildProc);
  }

/************************************************/
/* display_OnPaint:    */
/************************************************/
void display_OnPaint(
  HWND hwnd)
  {
   HDC hdc;
   PAINTSTRUCT ps;

   hdc = BeginPaint(hwnd,&ps);
   SetMapMode(hdc,MM_TEXT);;
   RedrawTerminal(hwnd,hdc);
   ValidateRect(hwnd,NULL);
   EndPaint(hwnd,&ps) ;
  }

/************************************************/
/* display_OnVScroll: */
/************************************************/
#if IBM_TBC
#pragma argsused
#endif
static void display_OnVScroll(
  HWND hwnd, 
  HWND hwndCtl, 
  UINT code, 
  int pos)
  {
#if IBM_MCW
#pragma unused(hwndCtl)
#endif
   int min, max, cp;
   static int CaretON = TRUE;
   struct displayWindowData *theData;
   
   theData = (struct displayWindowData *) GetWindowLong(hwnd,GWL_USERDATA);
   
   if (theData == NULL) 
     { return; }

   GetScrollRange(hwnd,SB_VERT,&min,&max);
   cp = GetScrollPos(hwnd,SB_VERT);
   
   switch (code)
	 {   
      case SB_LINEDOWN:
        cp++;
        break;

      case SB_LINEUP:
        cp--;
        break;

      case SB_PAGEDOWN:
        cp += theData->noLines;
        break;
        
      case SB_PAGEUP:
        cp -= theData->noLines;
        break;
      
      case SB_THUMBTRACK:
        cp = pos;
        break;

      default:
        return;
	 }

   if (cp >= max)
     {
      cp = max;
      if (FORWARD_WM_MDIGETACTIVE(MDIClientWnd,SendMessage) == hwnd)
        {
         ShowCaret(hwnd);
         CaretON = TRUE;
        }
     }
   else if (CaretON)
     {
      if (FORWARD_WM_MDIGETACTIVE(MDIClientWnd,SendMessage) == hwnd)
        {
         HideCaret(hwnd); 
         CaretON = FALSE;
        }
	 }

   if (cp < theData->noLines)
     { cp = theData->noLines; }

   SetScrollPos(hwnd,SB_VERT,cp,TRUE);
   InvalidateRect(hwnd,NULL,TRUE);
  }

/************************************************/
/* display_OnHScroll: */
/************************************************/
#if IBM_TBC
#pragma argsused
#endif
static void display_OnHScroll(
  HWND hwnd, 
  HWND hwndCtl, 
  UINT code, 
  int pos)
  {
#if IBM_MCW
#pragma unused(hwndCtl)
#endif
   int min, max, cp;
   struct displayWindowData *theData;
   
   theData = (struct displayWindowData *) GetWindowLong(hwnd,GWL_USERDATA);
   
   if (theData == NULL) 
     { return; }

   GetScrollRange(hwnd,SB_HORZ,&min,&max);
   cp = GetScrollPos(hwnd,SB_HORZ);
   
   switch (code)
	 {   
      case SB_LINEDOWN:
        cp++;
        break;

      case SB_LINEUP:
        cp--;
        break;

      case SB_PAGEDOWN:
        cp += theData->noLines; // Should be window width?
        break;
        
      case SB_PAGEUP:
        cp -= theData->noLines; // Should be window width?
        break;
      
      case SB_THUMBTRACK:
        cp = pos;
        break;

      default:
        return;
	 }

   /* Should caret logic be inserted here? */

   if (cp > max) cp = max;
   if (cp < min) cp = min;

   SetScrollPos(hwnd,SB_HORZ,cp,TRUE);
   InvalidateRect(hwnd,NULL,TRUE);
  }

/************************************************/
/* display_OnSetFocus: */
/************************************************/
#if IBM_TBC
#pragma argsused
#endif
void display_OnSetFocus(
  HWND hwnd, 
  HWND oldfocus)
  {
#if IBM_MCW
#pragma unused(oldfocus)
#endif
   struct displayWindowData *theData;
   
   theData = (struct displayWindowData *) GetWindowLong(hwnd,GWL_USERDATA);
   
   if (theData == NULL) 
     { return; }
     
   SetFocus(hwnd);
   CreateCaret(hwnd,NULL,1,theData->lineSize);
   
   SetCaretPos(theData->caretX,theData->caretY);
   ShowCaret(hwnd);
  }
   
/************************************************/
/* display_OnKillFocus: */
/************************************************/
#if IBM_TBC
#pragma argsused
#endif
void display_OnKillFocus(
  HWND hwnd,
  HWND newFocus)
  {
#if IBM_MCW
#pragma unused(newFocus)
#endif
   POINT thePoint;
   struct displayWindowData *theData;
   
   theData = (struct displayWindowData *) GetWindowLong(hwnd,GWL_USERDATA);
   
   if (theData == NULL) 
     { return; }

   GetCaretPos(&thePoint);
   
   theData->caretX = thePoint.x;
   theData->caretY = thePoint.y;
   
   HideCaret(hwnd); 
   DestroyCaret();  
  }

/************************************************/
/* display_OnMDIActivate: */
/************************************************/
#if IBM_TBC
#pragma argsused
#endif
static void display_OnMDIActivate(
  HWND hwnd, 
  BOOL active, 
  HWND hActivate, 
  HWND hDeactivate)
  {
#if IBM_MCW
#pragma unused(hDeactivate)
#pragma unused(hActivate)
#endif
   if (active)
     {       
      display_OnUpdateMenu(hwnd,DisplayMenu);

      if (FORWARD_WM_MDISETMENU(MDIClientWnd,TRUE,DisplayMenu,
                                DisplayWindowMenu,SendMessage) != 0)
        { DrawMenuBar(hMainFrame); }
     }
  }
  
/************************************************/
/* display_OnCommand: */
/************************************************/
static void display_OnCommand(
  HWND hwnd, 
  int id, 
  HWND hctl, 
  UINT codeNotify)
  {	 
   HANDLE hData;
   LPSTR  pData;
   unsigned x;
   char *buffer;
   unsigned length;

   switch (id) 
	 {
      case ID_EDIT_PASTE:
        OpenClipboard(DialogWindow);
        hData = GetClipboardData (CF_TEXT);
        pData = (char *) GlobalLock (hData);

        for (x = 0; x < strlen(pData); x++)
          {
           if (pData[x] == '\r')
             { pData[x] = ' '; }
          }
          
        AppendCommandString(GetCurrentEnvironment(),pData);
        PrintRouter(WPROMPT,pData);

        GlobalUnlock(hData);
        CloseClipboard();     
        return;  
               
      case ID_FILE_PRINT:
        PrintWindow(GetWindowInstance(hMainFrame),hwnd,"Dialog Window");
	    return;
	    
      case ID_FILE_SAVE:
      case ID_FILE_SAVE_AS:
        SaveDisplayWindow(hwnd);
        return;
	    
      case ID_HELP_COMPLETE:
        buffer = GetCommandString(GetCurrentEnvironment());         

        if (buffer == NULL)
          {  
           MessageBeep(0);
           break;
          }

        length = strlen(buffer);
        buffer = GetCommandCompletionString(GetCurrentEnvironment(),buffer,length);

        if (buffer == NULL)
          {
           MessageBeep(0);
           break;
          }

        length = strlen(buffer);

        if (DoCommandCompletion(hwnd,buffer,1))
          {  
           AppendCommandString(GetCurrentEnvironment(),&(CompleteString[length]));
           PrintRouter(WPROMPT,(&(CompleteString[length])));
          }
       break;
	 } 
   
   FORWARD_WM_COMMAND(hwnd, id, hctl, codeNotify, DefMDIChildProc);
  }
  
/***********************************************************/
/* display_OnContextMenu: Pops up a context-specific menu. */
/***********************************************************/
static BOOL display_OnContextMenu(
  HWND hwnd,
  HWND hwndCtl,
  int xPos,
  int yPos)
  {
   return mdi_OnContextMenu(hwnd,hwndCtl,xPos,yPos,DisplayContextMenu);
  }

/**********************************************************/
/* display_OnTimer:  */
/**********************************************************/
#if IBM_TBC
#pragma argsused
#endif
static void display_OnTimer(
  HWND hwnd,
  UINT id)
  {
#if IBM_MCW
#pragma unused(hwnd)
#pragma unused(id)
#endif
   static int value = 0;
   POINT thePoint;

   GetCursorPos(&thePoint);
   ScreenToClient(MDIClientWnd,&thePoint);

   value++;
   
   SetClassLong(DialogWindow,GCL_HCURSOR,(LONG) WAIT[value]);

   if (ChildWindowFromPointEx(MDIClientWnd,thePoint,CWP_SKIPINVISIBLE | CWP_SKIPDISABLED) == DialogWindow)
     { SetCursor(WAIT[value]); }
  
   if (value > 7)
     { value = 0; }
  }

/**************************************************/
/* CreateTerminal: Initialize the structure which */
/*   will hold all the text for the terminal.     */
/**************************************************/
static void CreateTerminal(
  struct displayWindowData *theData)
  {
   int i;
     
   theData->terminal = (char **) malloc (sizeof(char *)*(DIALOG_SIZE+1));
   
   if (theData->terminal == NULL)
     { ExitToShell(); }
   
   for (i = 0; i <= (DIALOG_SIZE); i++)
     { theData->terminal[i] = NULL; }
  }

/*****************************************/
/* FreeTerminalText: Frees all line text */
/*   associated with the Terminal.       */
/*****************************************/
static void FreeTerminalText(
  HWND hwnd)
  { 
   int i;
   struct displayWindowData *theData;
   
   theData = (struct displayWindowData *) GetWindowLong(hwnd,GWL_USERDATA);
   
   if (theData == NULL) 
     { return; }

   for (i = 0; i <= DIALOG_SIZE; i++)
     {  
      if (theData->terminal[i] != NULL)
	    { 
	     free(theData->terminal[i]);
         theData->terminal[i] = NULL;
        }
	}
}

/************************************/
/* DeleteTerminal: Frees all memory */
/*   associated with the Terminal.  */
/************************************/
static void DeleteTerminal(
  HWND hwnd)
  {  
   struct displayWindowData *theData;
   
   theData = (struct displayWindowData *) GetWindowLong(hwnd,GWL_USERDATA);
   
   if (theData == NULL) 
     { return; }
     
   FreeTerminalText(hwnd);
   free(theData->terminal);
  }

/********************************************************/
/* RedrawTerminal: Draw dialog window based on the min, */
/*   max, and current position of the thumbnail in the  */
/*   horizontal and vertical scroll bars.               */
/********************************************************/
static void RedrawTerminal( 
  HWND hwnd,
  HDC hDC)
  {
   int x, max, min, pos, start;
   RECT Rect;
   char *buffer = NULL;
   HFONT hfont;
   struct displayWindowData *theData;
   LOGFONT lf;
   
   theData = (struct displayWindowData *) GetWindowLong(hwnd,GWL_USERDATA);
   
   if (theData == NULL) 
     { return; }
   
   memset(&lf,0,sizeof(LOGFONT));
   lf.lfHeight = -13;
   strcpy(lf.lfFaceName,"Courier New");
   hfont = CreateFontIndirect(&lf);

   if (hfont != NULL)
     { SelectObject(hDC,hfont); }   
       
   /*===============================================*/
   /* Get current value of the vertical scroll bar. */
   /*===============================================*/
   
   GetScrollRange(hwnd,SB_VERT,&min,&max);
   pos = GetScrollPos(hwnd,SB_VERT);

   /*===========================================*/
   /* Locate the first visible line to display. */
   /*===========================================*/
   
   start = theData->lastLine - (max - pos) - theData->noLines;

   if (start == theData->lastLine)
     { start++; }

   /*================================================*/
   /* If the start value is negative, then check if  */
   /* at a 'hard' top or if a wrap around is needed. */
   /*================================================*/
   
   if (start < 0)
     {  
      if (max == DIALOG_SIZE)
	    { start = max + start + 1; }
      else
        { start = 0; }
     }

   /*============================================*/
   /* Get Position of the horizontal scroll bar. */
   /*============================================*/
   
   pos = GetScrollPos(hwnd,SB_HORZ);

   /*============================================================*/
   /* Loop to display text in the visible portion of the screen. */
   /*============================================================*/
   
   for (x = 0; x <= theData->noLines; x++)
     {
      size_t bufsize;

      GetClientRect(hwnd,&Rect);
      Rect.left = 2;
      Rect.top = x * theData->lineSize;

      /*=================================*/
      /* Calculate horizontal scroll bar */ 
      /* max based on what is displayed. */
      /*=================================*/
      
      if (theData->terminal[start] != NULL)
        { bufsize = strlen(theData->terminal[start]); }
      else
        { bufsize = 0; }

      /*====================================*/
      /* Display each line of text adjusted */
      /* for the horizontal scroll bar.     */
      /*====================================*/
      
      buffer = NULL;
      if ((pos <= (int) bufsize ) && (theData->terminal[start] != NULL))
        {  
         buffer = theData->terminal[start] + pos;
	     DrawText(hDC,buffer,-1,&Rect,DT_LEFT | DT_NOCLIP | DT_NOPREFIX );
        }

      /*=================================*/
      /* Check if wrap around is needed. */
      /*=================================*/
      
      if (start == theData->lastLine) 
        { break; }
        
      start++;
      if (start > DIALOG_SIZE)
        { start = 0; }
     }
     
   /*======================================*/
   /* Calculate and display caret adjusted */ 
   /* for horizontal scroll bar.           */
   /*======================================*/

   if (buffer == NULL)
     { buffer = ""; }
   
   DrawText(hDC,buffer,-1,&Rect,DT_LEFT | DT_NOCLIP | DT_CALCRECT);
   
   if (pos != max)
     { return; }
     
   if (FORWARD_WM_MDIGETACTIVE(MDIClientWnd,SendMessage) == hwnd)
     {
      if (Rect.right == 0)
        { Rect.right = 2; } 

      SetCaretPos(Rect.right,Rect.top);
     }
     
   if (hfont != NULL)
     { DeleteFont(hfont); }   
  }
  
/************************************************************/
/* DisplayLineCountAndStart: Returns the number of lines in */
/*   the display window and the index of the first line.    */
/************************************************************/
void DisplayLineCountAndStart( 
  HWND hwnd,
  int *lines,
  int *start)
  {
   struct displayWindowData *theData;
   
   theData = (struct displayWindowData *) GetWindowLong(hwnd,GWL_USERDATA);
   
   if (theData == NULL) 
     { return; }
        
   if (theData->lastLine == DIALOG_SIZE)
     {
      *start = 0;
      *lines = DIALOG_SIZE + 1;
     }
   else if (theData->terminal[theData->lastLine + 1] == NULL)
     {
      *start = 0;
      *lines = theData->lastLine + 1;
     }
   else 
     {
      *start = theData->lastLine + 1;
      *lines = DIALOG_SIZE + 1;
     }
  }

/*****************************************************/
/* DisplayPrint: Function will allocate memory used */
/*   to store strings sent to the Dialog window.     */
/*****************************************************/
#if IBM_TBC
#pragma argsused
#endif
int DisplayPrint( 
  HWND hwnd,
  char *buffer)
  {       
   unsigned Loc = 0; 
   char *str; 
   size_t oldsize, size;
   struct displayWindowData *theData;
   
   theData = (struct displayWindowData *) GetWindowLong(hwnd,GWL_USERDATA);
   
   if (theData == NULL) 
     { return(FALSE); }

   if (buffer[0] == '\b') 
     { return(TRUE); }
   
   /*============================================*/
   /* Allocate room for the buffer to be parsed. */
   /*============================================*/

   str = (char *) malloc(strlen(buffer)+1);
   if (str == NULL)
     { ExitToShell(); }

   /*================================================*/
   /* Loop through each 'part' of the buffer string  */
   /* Note: 'part' is text between carriage returns. */
   /*================================================*/
   
   while (Loc < strlen(buffer))
     {
      str[0] = '\0';
     
      /*====================================*/
      /* Capture text up to the line break. */
      /*====================================*/
      
      sscanf(&(buffer[Loc]),"%[^\n]",str);

      /*==========================================*/
      /* Allocate new memory if new line of text  */
      /* or reallocate if appending line of text. */
      /*==========================================*/

      if (theData->terminal[theData->lastLine] != NULL)
	    { oldsize = strlen(theData->terminal[theData->lastLine]); }
      else
        { oldsize = 0; }

      size = oldsize + strlen(str)+1;
      
      if (theData->terminal[theData->lastLine] != NULL)
        { theData->terminal[theData->lastLine] = (char *) realloc(theData->terminal[theData->lastLine],size); }
      else
        { theData->terminal[theData->lastLine] = (char *) malloc(size); }

      if (theData->terminal[theData->lastLine] == NULL)
        { ExitToShell(); }

      /*===================================*/
      /* Copy string to the dialog window. */
      /*===================================*/
      
      theData->terminal[theData->lastLine][oldsize] = '\0';
      strcat(theData->terminal[theData->lastLine],str);
      Loc += strlen(str);

      if (buffer[Loc] == '\n')
        {  
         int min, max;
         
         /*================================*/
         /* Display line before advancing. */
         /*================================*/
         
         SendToScreen(hwnd);
         theData->lastLine++;
         if (theData->lastLine > DIALOG_SIZE) 
           { theData->lastLine = 0; }

         /*=========================*/
         /* Free next line of text. */
         /*=========================*/
         
         if (theData->terminal[theData->lastLine] != NULL)
           {
            free(theData->terminal[theData->lastLine]);
            theData->terminal[theData->lastLine] = NULL;
           }

         /*=====================*/
         /* Update scroll bars. */
         /*=====================*/
         
         GetScrollRange(hwnd,SB_VERT,&min,&max);
         if (max < DIALOG_SIZE) 
           { 
            max = theData->lastLine;
           }
         else 
           { max = DIALOG_SIZE; }
           
         if (max > theData->noLines)
           { 
            SetScrollRange(hwnd,SB_VERT,theData->noLines,max,FALSE); 
            SetScrollPos(hwnd,SB_VERT,max,TRUE);
           }
         else
           { 
            SetScrollRange(hwnd,SB_VERT,0,0,FALSE); 
            SetScrollPos(hwnd,SB_VERT,0,TRUE);
           }

         GetScrollRange(hwnd,SB_HORZ,&min,&max);
         if (max < (int) size) max = (int) size;
         //SetScrollRange(hwnd,SB_HORZ,0,max,FALSE);
         SetScrollPos(hwnd,SB_HORZ,0,TRUE);
		}

      SendToScreen(hwnd);
      Loc++;
	 }
	 
   free(str);

   return (TRUE);
  }

/**********************************************************************/
/* SendToScreen: Displays the current text line in the Dialog Window. */
/**********************************************************************/
static void SendToScreen(
  HWND hwnd)
  {         
   RECT DRect;                     /* Client Area of Dialog Window */
   RECT Rect;                      /* Adjusted Area for scrolling */
   HDC hDC = GetDC(DialogWindow);  /* Handle to the device context */
   int min, max, pos;              /* Scroll Bar Values */
   int Scroll = 0;                 /* Scrolling Needed? */
   HANDLE OldObject;
   HFONT hfont;
   struct displayWindowData *theData;
   LOGFONT lf;
  
   theData = (struct displayWindowData *) GetWindowLong(hwnd,GWL_USERDATA);
   
   if (theData == NULL) 
     { return; }
   
   GetClientRect(hwnd,&Rect);
   GetClientRect(hwnd,&DRect);
   
   if (hwnd == FORWARD_WM_MDIGETACTIVE(MDIClientWnd,SendMessage))
     { HideCaret(hwnd); }

   memset(&lf,0,sizeof(LOGFONT));
   lf.lfHeight = -13;
   strcpy(lf.lfFaceName,"Courier New");
   hfont = CreateFontIndirect(&lf);
   if (hfont != NULL)
     { SelectFont(hDC,hfont); }
   
   /*========================================================*/
   /* Move to the bottom of the screen if not already there. */
   /*========================================================*/

   GetScrollRange(hwnd,SB_VERT,&min,&max);
   pos = GetScrollPos(hwnd,SB_VERT);
   if (pos != max)
     {  
      InvalidateRect(hwnd,NULL,TRUE);
      SetScrollPos(hwnd,SB_VERT,max,FALSE);
      SendMessage(hwnd,WM_PAINT,0,0);
     }

   /*==========================================================*/
   /* Determine if the screen is full and scrolling is needed. */
   /*==========================================================*/

   if (max > theData->noLines) Scroll = 1;

   /*===============================================================*/
   /* Scroll Window if newline and text will not fit on the screen. */
   /*===============================================================*/

   if (Scroll && theData->lastLine != theData->oldLine)
     {  
      theData->oldLine = theData->lastLine;
      ScrollDC (hDC,0,-theData->lineSize,&DRect,&DRect,NULL,&Rect);
     }

   /*==========================================*/
   /* Calculate where text is to be displayed. */
   /*==========================================*/

   Rect.left = 2;
   if (! Scroll)
     { Rect.top = (theData->lastLine) * theData->lineSize; }
   else
     { Rect.top = (theData->noLines) * theData->lineSize; }

   /*=============================*/
   /* Clear line to be displayed. */
   /*=============================*/
   
   OldObject = SelectObject(hDC,GetStockObject(WHITE_PEN));
   Rectangle(hDC,Rect.left,Rect.top,Rect.right,Rect.bottom);
   SelectObject(hDC,OldObject);

   /*============================*/
   /* Display Text Adjusting     */
   /* for the Horizontal scroll. */
   /*============================*/
   
   pos = GetScrollPos(hwnd,SB_HORZ);
     
   if (theData->terminal[theData->lastLine] == NULL)
     { Rect.right = 2; /* Do Nothing */ }
   else if (pos < (int) strlen(theData->terminal[theData->lastLine]))
	 {
	  DrawText(hDC,theData->terminal[theData->lastLine]+pos,-1,&Rect,
               DT_LEFT | DT_NOCLIP | DT_SINGLELINE | DT_NOPREFIX );
      DrawText(hDC,theData->terminal[theData->lastLine]+pos,-1,&Rect,
               DT_LEFT | DT_NOCLIP | DT_SINGLELINE | DT_CALCRECT | DT_NOPREFIX);
     }
   else
     { Rect.right = 2; }

   /*=============*/
   /* Show caret. */
   /*=============*/
   
   if (FORWARD_WM_MDIGETACTIVE(MDIClientWnd,SendMessage) == hwnd)
     {
      SetCaretPos(Rect.right,Rect.top);
      ShowCaret(hwnd);
     }

   /*=================================*/
   /* Automatic horizontal scrolling. */
   /*=================================*/

   if (theData->terminal[theData->lastLine] != NULL)
     {
      DrawText(hDC,theData->terminal[theData->lastLine]+pos,-1,&Rect,
               DT_LEFT | DT_NOCLIP | DT_SINGLELINE | DT_NOPREFIX );
     }
     
   if (Rect.right > DRect.right && theData->horizScroll)
     {
      GetScrollRange(hwnd,SB_HORZ,&min,&max);
      pos++;
      if (max < pos) max = pos;
      SetScrollPos(hwnd,SB_HORZ,pos,TRUE);
      InvalidateRect(hwnd,NULL,TRUE);
     }

   theData->oldLine = theData->lastLine;
   ReleaseDC(hwnd,hDC);  
    
   if (hfont != NULL)
     { DeleteFont(hfont); }
  }

/*****************************************************************/
/* ClearDialogWnd: Procedure will clear all text from the dialog */
/*    window and free storage in the terminal data structure.    */
/*****************************************************************/
void ClearDialogWnd(void)
  {
   struct displayWindowData *theData;
   
   theData = (struct displayWindowData *) GetWindowLong(DialogWindow,GWL_USERDATA);
   
   if (theData == NULL) 
     { return; }
     
   /*===========================================*/
   /* Free all data associated with the screen. */
   /*===========================================*/
   
   FreeTerminalText(DialogWindow);

   /*=====================================*/
   /* Reset information about the screen. */
   /*=====================================*/
   
   theData->lastLine = 0;
   theData->oldLine = 0;

   /*=====================*/
   /* Update scroll bars. */
   /*=====================*/
  
   SetScrollRange(DialogWindow,SB_VERT,0,0,FALSE); 
   SetScrollPos(DialogWindow,SB_VERT,0,TRUE);
   SetScrollPos(DialogWindow,SB_HORZ,0,TRUE);
   InvalidateRect(DialogWindow,NULL,TRUE);
   FlushCommandString(GetCurrentEnvironment());
  }

/******************************************/
/* ExitToShell: Quit and exit to Windows. */
/******************************************/
void ExitToShell(void)
  {   
   MessageBeep(0);
   MessageBox(DialogWindow, "CLIPS is out of memory!!", "-ERROR-", MB_ICONHAND | MB_OK );
   PostMessage(DialogWindow, WM_COMMAND, ID_APP_EXIT, 0);
  }

/***********************************************/
/* GetUserCmd: Function used to filter/display */ 
/*   characters typed in from the keyboard.    */
/***********************************************/
void GetUserCmd( 
  HWND hwnd,
  WORD wParam,      /* Key Code */
  BOOL ScreenOnly,  /* Send to Screen and or Command Buffer */
  unsigned inputSize)   /* Number of characters send to screen only */
  {
   struct displayWindowData *theData;
   
   theData = (struct displayWindowData *) GetWindowLong(hwnd,GWL_USERDATA);
   
   if (theData == NULL) 
     { return; }
     
   switch (wParam)
     {
      /*===================*/
      /* Handle backspace. */
      /*===================*/

      case VK_BACK:
        {
         char text[2];
         
         /*================================*/
         /* Initialize Values when sending */
         /* to the command buffer.         */
         /*================================*/

         if (! ScreenOnly)
           {
            theData->horizScroll = 1;
	        if (GetCommandString(GetCurrentEnvironment()) != NULL)
              { inputSize = strlen(GetCommandString(GetCurrentEnvironment())); }
            else
              { inputSize = 0; }
           }

         if (inputSize > 0 )
           {
            size_t size;
            
            if (theData->terminal[theData->lastLine] != NULL)
			  { size = strlen(theData->terminal[theData->lastLine]); }
		    else
			  { size = 0; }

		    if (! ScreenOnly)
			  { ExpandCommandString (GetCurrentEnvironment(),'\b'); }

		    if (size > 0)
			  { theData->terminal[theData->lastLine][size - 1] = '\0'; }
		    else
		      {  
		       int min, max;

			   if (theData->terminal[theData->lastLine] != NULL)
			     {
		          free(theData->terminal[theData->lastLine]);
		          theData->terminal[theData->lastLine] = NULL;
	             }

			   theData->lastLine--;
	           theData->oldLine--;

	           if (theData->lastLine < 0)
		       theData->lastLine = DIALOG_SIZE;

	           GetScrollRange(hwnd,SB_VERT,&min,&max);
	           if ((theData->noLines < max) && (max < DIALOG_SIZE))
	             {  
	              SetScrollRange(hwnd,SB_VERT,theData->noLines,max-1,FALSE);
		          SetScrollPos(hwnd,SB_VERT,max-1,FALSE);
	             }
	           else
	             {
                  SetScrollRange(hwnd,SB_VERT,0,0,FALSE); 
                  SetScrollPos(hwnd,SB_VERT,0,TRUE);
	             }
	           InvalidateRect (hwnd,NULL,TRUE);
		      }
		    SendToScreen(hwnd);
		     
	        text[0] = (char) wParam;
	        text[1] = '\0';
	        PrintRouter("stdout",text);
	       }
	     break;
        }

      /*============================*/
      /* Remove special keys (ALT). */
      /*============================*/
      
      case '\f':
      case VK_MENU:
        break;

      /*==================*/
      /* Handle tab keys. */
      /*==================*/
      
      case '\t':  
        if (! ScreenOnly)
	      {  
	       if (GetCommandString(GetCurrentEnvironment()) == NULL)
	         { SetCommandString(GetCurrentEnvironment(),"   "); }
           else
             { AppendCommandString(GetCurrentEnvironment(),"   "); }
          }
        PrintRouter ("stdout", "   " );
        break;
      
      /*=====================*/
      /* Return/newline key. */
      /*=====================*/
      
      case '\r':
      case '\n':
        wParam = (int)'\n';
	    if (GetScrollPos(hwnd,SB_HORZ) != 0)
	      {  
	       SetScrollPos (hwnd,SB_HORZ,0,TRUE);
           InvalidateRect(hwnd, NULL,FALSE);
           SendMessage(hwnd,WM_PAINT,0,0);
          }

      /*=======================*/
      /* All other characters. */
      /*=======================*/
      
      default:
        {  
        char text[2];

        text[0] = (char) wParam;
        text[1] = '\0';

        if (isprint (text[0]) || isspace(text[0]))
          {  
           /*==============================*/
           /* Add to CLIPS command buffer. */
           /*==============================*/
           
           if (! ScreenOnly)
            { 
             if (GetCommandString(GetCurrentEnvironment()) == NULL)
		       { SetCommandString(GetCurrentEnvironment(),text); }
	         else
       	       { AppendCommandString(GetCurrentEnvironment(),text); }
            }
          
          PrintRouter("stdout",text);
         }
       break;
       }
     }
   theData->horizScroll = 0;
  }

/***************************************/
/* SaveDisplayWindow: Saves the output */
/*   of a display window to a file.    */
/***************************************/
static void SaveDisplayWindow( 
  HWND hwnd)
  {  
   OPENFILENAME ofn;
   char File[256], FileTitle[256], Filter[256];
   UINT i;
   int cbString;
   char Replace;
   int x;
   size_t size;
   struct displayWindowData *theData;
   FILE *fp;
   int lines, displayStart, iLineNum;
   
   theData = (struct displayWindowData *) GetWindowLong(hwnd,GWL_USERDATA);
   
   if (theData == NULL) return; 

   sprintf(File,"DialogWindow.txt");
   memset(&ofn,0,sizeof(OPENFILENAME));
   ofn.lpstrTitle = "Select Dialog Window Save File";

   if ((cbString = LoadString(GetWindowInstance(hwnd),IDS_TEXT_FILES,Filter,sizeof(Filter))) == 0)
     { return; }

   Replace = Filter[cbString-1];
   for (i=0; Filter[i] != '\0'; i++)
     {
      if (Filter[i] == Replace)
        { Filter[i] = '\0'; }
     }

   ofn.lStructSize = sizeof(OPENFILENAME);
   ofn.hwndOwner = hwnd;
   ofn.lpstrFilter = Filter;
   ofn.nFilterIndex = 1;
   ofn.lpstrFile = File;
   ofn.nMaxFile = sizeof(File);
   ofn.lpstrFileTitle = FileTitle;
   ofn.nMaxFileTitle = sizeof (FileTitle);
   ofn.lpstrInitialDir = NULL;
   ofn.Flags = OFN_OVERWRITEPROMPT | OFN_HIDEREADONLY;
      
   /*==========================*/
   /* Perform the file dialog. */
   /*==========================*/
   
   if (! GetSaveFileName(&ofn))
     { return; }
        
   /*==========================*/
   /* Adjust Path Information. */
   /*==========================*/
   
   size = strlen(ofn.lpstrFile);
   for (x = 0; x < (int) size; x++)
     {
      if (ofn.lpstrFile[x] == '\\')
        { ofn.lpstrFile[x] = '/'; }
     }
     
   /*=================================*/
   /* Save the contents of the status */
   /* window to the file.             */
   /*=================================*/

   DisplayLineCountAndStart(hwnd,&lines,&displayStart);
  
   if ((fp = fopen(ofn.lpstrFile,"w")) == NULL)
     { return; }
     
   iLineNum = displayStart;
   for (i = 0; i < (unsigned) lines; i++)
     {
      if (theData->terminal[iLineNum] != NULL)
        { fprintf(fp,"%s",theData->terminal[iLineNum]); }
     
      fprintf(fp,"\n");
      
      iLineNum++;
      if (iLineNum > DIALOG_SIZE)
        { iLineNum = 0; }
     }

   fclose(fp);
  }
    
/*************************************/
/* UpdateCursor: Changes the cursor. */
/*************************************/
void UpdateCursor( 
  int theCursor)
  {     
   HCURSOR myCursor;
   POINT thePoint;

   GetCursorPos(&thePoint);
   ScreenToClient(MDIClientWnd,&thePoint);
 
   switch (theCursor)
     {  
      case ARROW_CURSOR:    
        myCursor = ARROW;
        break;
        
      case QUESTION_CURSOR:
        myCursor = QUERY;
        break;
        
      case WAIT_CURSOR:
        myCursor = WAIT[0];
        break;
     }
   
   SetClassLong(DialogWindow,GCL_HCURSOR,(LONG) myCursor);

   if (ChildWindowFromPointEx(MDIClientWnd,thePoint,CWP_SKIPINVISIBLE | CWP_SKIPDISABLED) == DialogWindow)
     { SetCursor(myCursor); }
  }

/*******************************************************/
/* StartWaitCursor: Function is called to set an event */ 
/*   timer used to update the animated wait cursor.    */
/*******************************************************/
void StartWaitCursor()
  {
   SetTimer(DialogWindow,IDT_UPDATEWAIT,500,(TIMERPROC) NULL);
  }

/********************************************************/
/* StopWaitCursor: Function is called to stop the event */ 
/*   timer used to update the animated wait cursor.     */
/********************************************************/
void StopWaitCursor()
  {
   KillTimer(DialogWindow,IDT_UPDATEWAIT);
   UpdateCursor(ARROW_CURSOR);
  }
