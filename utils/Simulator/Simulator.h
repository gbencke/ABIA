// Simulator.h : main header file for the SIMULATOR application
//

#if !defined(AFX_SIMULATOR_H__F9203C65_2A99_4114_B8F5_B7C7F7218072__INCLUDED_)
#define AFX_SIMULATOR_H__F9203C65_2A99_4114_B8F5_B7C7F7218072__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#ifndef __AFXWIN_H__
	#error include 'stdafx.h' before including this file for PCH
#endif

#include "resource.h"		// main symbols

/////////////////////////////////////////////////////////////////////////////
// CSimulatorApp:
// See Simulator.cpp for the implementation of this class
//

class CSimulatorApp : public CWinApp
{
public:
	CSimulatorApp();

// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CSimulatorApp)
	public:
	virtual BOOL InitInstance();
	//}}AFX_VIRTUAL

// Implementation

	//{{AFX_MSG(CSimulatorApp)
		// NOTE - the ClassWizard will add and remove member functions here.
		//    DO NOT EDIT what you see in these blocks of generated code !
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};


/////////////////////////////////////////////////////////////////////////////

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_SIMULATOR_H__F9203C65_2A99_4114_B8F5_B7C7F7218072__INCLUDED_)
