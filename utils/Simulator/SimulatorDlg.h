// SimulatorDlg.h : header file
//

#if !defined(AFX_SIMULATORDLG_H__C98E9B1F_FAFD_4BD0_9A47_13BB80E4E0DA__INCLUDED_)
#define AFX_SIMULATORDLG_H__C98E9B1F_FAFD_4BD0_9A47_13BB80E4E0DA__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

/////////////////////////////////////////////////////////////////////////////
// CSimulatorDlg dialog

class CSimulatorDlg : public CDialog
{
// Construction   
public:
	CSimulatorDlg(CWnd* pParent = NULL);	// standard constructor

// Dialog Data
	//{{AFX_DATA(CSimulatorDlg)
	enum { IDD = IDD_SIMULATOR_DIALOG };
	CEdit	m_CurrentStatement;
	CButton	m_CopyStatement;
	CButton	m_Cancel;
	CButton	m_Copy;
	CEdit	m_CurrentDay;
	CEdit	m_Target;
	CEdit	m_Origin;
	//}}AFX_DATA

	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CSimulatorDlg)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);	// DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:
	HICON     m_hIcon;
	CString  *StatementDates,*StatementDatesDescription;
	int       NumberPossibleStatements;
	int       CurrentStatement;
	int       LastCurrentDay,LastCurrentMonth,LastCurrentYear;

	// Generated message map functions
	//{{AFX_MSG(CSimulatorDlg)
	virtual BOOL OnInitDialog();
	afx_msg void OnSysCommand(UINT nID, LPARAM lParam);
	afx_msg void OnPaint();
	afx_msg HCURSOR OnQueryDragIcon();
	virtual void OnOK();
	afx_msg void OnChangeEdit2();
	afx_msg void OnChangeEdit1();
	afx_msg void OnChangeEdit3();
	virtual void OnCancel();
	afx_msg void OnButton1();
	afx_msg void OnChangeEdit4();
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_SIMULATORDLG_H__C98E9B1F_FAFD_4BD0_9A47_13BB80E4E0DA__INCLUDED_)
