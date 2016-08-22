// SimulatorDlg.cpp : implementation file
//

#include "stdafx.h"
#include "Simulator.h"
#include "SimulatorDlg.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CAboutDlg dialog used for App About

class CAboutDlg : public CDialog
{
public:
	CAboutDlg();

// Dialog Data
	//{{AFX_DATA(CAboutDlg)
	enum { IDD = IDD_ABOUTBOX };
	//}}AFX_DATA

	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CAboutDlg)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:
	//{{AFX_MSG(CAboutDlg)
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};

CAboutDlg::CAboutDlg() : CDialog(CAboutDlg::IDD)
{
	//{{AFX_DATA_INIT(CAboutDlg)
	//}}AFX_DATA_INIT
}

void CAboutDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(CAboutDlg)
	//}}AFX_DATA_MAP
}

BEGIN_MESSAGE_MAP(CAboutDlg, CDialog)
	//{{AFX_MSG_MAP(CAboutDlg)
		// No message handlers
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CSimulatorDlg dialog

CSimulatorDlg::CSimulatorDlg(CWnd* pParent /*=NULL*/)
	: CDialog(CSimulatorDlg::IDD, pParent)
{
	//{{AFX_DATA_INIT(CSimulatorDlg)
		// NOTE: the ClassWizard will add member initialization here
	//}}AFX_DATA_INIT
	// Note that LoadIcon does not require a subsequent DestroyIcon in Win32
	m_hIcon = AfxGetApp()->LoadIcon(IDR_MAINFRAME);
}

void CSimulatorDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(CSimulatorDlg)
	DDX_Control(pDX, IDC_EDIT4, m_CurrentStatement);
	DDX_Control(pDX, IDC_BUTTON1, m_CopyStatement);
	DDX_Control(pDX, IDCANCEL, m_Cancel);
	DDX_Control(pDX, IDOK, m_Copy);
	DDX_Control(pDX, IDC_EDIT3, m_CurrentDay);
	DDX_Control(pDX, IDC_EDIT2, m_Target);
	DDX_Control(pDX, IDC_EDIT1, m_Origin);
	//}}AFX_DATA_MAP
}

BEGIN_MESSAGE_MAP(CSimulatorDlg, CDialog)
	//{{AFX_MSG_MAP(CSimulatorDlg)
	ON_WM_SYSCOMMAND()
	ON_WM_PAINT()
	ON_WM_QUERYDRAGICON()
	ON_EN_CHANGE(IDC_EDIT2, OnChangeEdit2)
	ON_EN_CHANGE(IDC_EDIT1, OnChangeEdit1)
	ON_EN_CHANGE(IDC_EDIT3, OnChangeEdit3)
	ON_BN_CLICKED(IDC_BUTTON1, OnButton1)
	ON_EN_CHANGE(IDC_EDIT4, OnChangeEdit4)
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CSimulatorDlg message handlers

BOOL CSimulatorDlg::OnInitDialog()
{
	CDialog::OnInitDialog();

	// Add "About..." menu item to system menu.

	// IDM_ABOUTBOX must be in the system command range.
	ASSERT((IDM_ABOUTBOX & 0xFFF0) == IDM_ABOUTBOX);
	ASSERT(IDM_ABOUTBOX < 0xF000);

	NumberPossibleStatements=20;
	CurrentStatement=-1;



	StatementDates=new CString[20];
	StatementDatesDescription=new CString[20];

	StatementDates[0]="31031999";
	StatementDates[1]="30061999";
	StatementDates[2]="30091999";
	StatementDates[3]="31121999";

	StatementDates[4]="31032000";
	StatementDates[5]="30062000";
	StatementDates[6]="30092000";
	StatementDates[7]="31122000";

	StatementDates[8]="31032001";
	StatementDates[9]="30062001";
	StatementDates[10]="30092001";
	StatementDates[11]="31122001";
	
	StatementDates[12]="31032002";
	StatementDates[13]="30062002";
	StatementDates[14]="30092002";
	StatementDates[15]="31122002";

	StatementDates[16]="31032003";
	StatementDates[17]="30062003";
	StatementDates[18]="30092003";
	StatementDates[19]="31122003";

	StatementDatesDescription[0]="31/03/1999";
	StatementDatesDescription[1]="30/06/1999";
	StatementDatesDescription[2]="30/09/1999";
	StatementDatesDescription[3]="31/12/1999";

	StatementDatesDescription[4]="31/03/2000";
	StatementDatesDescription[5]="30/06/2000";
	StatementDatesDescription[6]="30/09/2000";
	StatementDatesDescription[7]="31/12/2000";

	StatementDatesDescription[8]="31/03/2001";
	StatementDatesDescription[9]="30/06/2001";
	StatementDatesDescription[10]="30/09/2001";
	StatementDatesDescription[11]="31/12/2001";
	
	StatementDatesDescription[12]="31/03/2002";
	StatementDatesDescription[13]="30/06/2002";
	StatementDatesDescription[14]="30/09/2002";
	StatementDatesDescription[15]="31/12/2002";

	StatementDatesDescription[16]="31/03/2003";
	StatementDatesDescription[17]="30/06/2003";
	StatementDatesDescription[18]="30/09/2003";
	StatementDatesDescription[19]="31/12/2003";


	CMenu* pSysMenu = GetSystemMenu(FALSE);
	if (pSysMenu != NULL)
	{
		CString strAboutMenu;
		strAboutMenu.LoadString(IDS_ABOUTBOX);
		if (!strAboutMenu.IsEmpty())
		{
			pSysMenu->AppendMenu(MF_SEPARATOR);
			pSysMenu->AppendMenu(MF_STRING, IDM_ABOUTBOX, strAboutMenu);
		}
	}

	// Set the icon for this dialog.  The framework does this automatically
	//  when the application's main window is not a dialog
	SetIcon(m_hIcon, TRUE);			// Set big icon
	SetIcon(m_hIcon, FALSE);		// Set small icon
	
	this->m_CurrentDay.SetWindowText("01/01/2003");
	this->m_CurrentStatement.SetWindowText("31/12/1998");
	this->m_Origin.SetWindowText("C:\\ulbra\\abia\\Downloaded");
	this->m_Target.SetWindowText("C:\\Program Files\\Apache Group\\Tomcat 4.1\\webapps\\abia");

	
	// TODO: Add extra initialization here
	return TRUE;  // return TRUE  unless you set the focus to a control
}

void CSimulatorDlg::OnSysCommand(UINT nID, LPARAM lParam)
{
	if ((nID & 0xFFF0) == IDM_ABOUTBOX)
	{
		CAboutDlg dlgAbout;
		dlgAbout.DoModal();
	}
	else
	{
		CDialog::OnSysCommand(nID, lParam);
	}
}

// If you add a minimize button to your dialog, you will need the code below
//  to draw the icon.  For MFC applications using the document/view model,
//  this is automatically done for you by the framework.

void CSimulatorDlg::OnPaint() 
{
	if (IsIconic())
	{
		CPaintDC dc(this); // device context for painting

		SendMessage(WM_ICONERASEBKGND, (WPARAM) dc.GetSafeHdc(), 0);

		// Center icon in client rectangle
		int cxIcon = GetSystemMetrics(SM_CXICON);
		int cyIcon = GetSystemMetrics(SM_CYICON);
		CRect rect;
		GetClientRect(&rect);
		int x = (rect.Width() - cxIcon + 1) / 2;
		int y = (rect.Height() - cyIcon + 1) / 2;

		// Draw the icon
		dc.DrawIcon(x, y, m_hIcon);
	}
	else
	{
		CDialog::OnPaint();
	}
}

// The system calls this to obtain the cursor to display while the user drags
//  the minimized window.
HCURSOR CSimulatorDlg::OnQueryDragIcon()
{
	return (HCURSOR) m_hIcon;
}

int GetLastDay(int Month){
		if(Month==1) return 31;
		if(Month==2) return 28;
		if(Month==3) return 31;
		if(Month==4) return 30;
		if(Month==5) return 31;
		if(Month==6) return 30;
		if(Month==7) return 31;
		if(Month==8) return 31;
		if(Month==9) return 30;
		if(Month==10) return 31;
		if(Month==11) return 30;
		if(Month==12) return 31;
		return 0;
}

void CSimulatorDlg::OnOK() 
{
	CString  strCurrentDay,strPathTemp;
	char     lpCurrentDay[1000],strTemp[1000],strTemp2[1000];
	char     strPathToOrigin[1000],strPathToTarget[1000];
	int      CurrentDay,CurrentMonth,CurrentYear,found=0;	
	HANDLE   hTestExist;


	this->m_CurrentDay.GetWindowText(strCurrentDay);
	strcpy(lpCurrentDay,strCurrentDay);
	if(strlen(lpCurrentDay)!=10 || lpCurrentDay[2]!='/' || lpCurrentDay[5]!='/'){
		MessageBox("Invalid Date","Error",48);
		return;
	}

	strcpy(strTemp,lpCurrentDay);
	strTemp[2]=0;
	CurrentDay=atoi(strTemp);

	strcpy(lpCurrentDay,strCurrentDay);
	strcpy(strTemp,&lpCurrentDay[3]);
	strTemp[2]=0;
	CurrentMonth=atoi(strTemp);

	strcpy(lpCurrentDay,strCurrentDay);
	strcpy(strTemp,&lpCurrentDay[6]);
	CurrentYear=atoi(strTemp);

	if(CurrentMonth<1 || CurrentMonth>12){
		MessageBox("Invalid Date","Error",48);
		return;
	}
	if(CurrentDay>GetLastDay(CurrentMonth)){
		MessageBox("Invalid Date","Error",48);
		return;
	}

	while(1){
	      CurrentDay++;
		  if(CurrentDay>GetLastDay(CurrentMonth)){
		     CurrentMonth++;
		     CurrentDay=1;
		     if(CurrentMonth==13){
			    CurrentYear++;
			    CurrentMonth=1;
			 }
		  }
		  this->m_Origin.GetWindowText(strPathTemp);
		  sprintf(strTemp,"%s\\%04d%02d%02d.txt",strPathTemp,CurrentYear,CurrentMonth,CurrentDay);
		  hTestExist=CreateFile(strTemp,
								GENERIC_READ,
								FILE_SHARE_READ,
								NULL,
								OPEN_EXISTING,
								NULL,
								NULL);
		  if(hTestExist!=INVALID_HANDLE_VALUE){
			 char strOriginTemp[1000],strTargetTemp[1000];

			 strcpy(strOriginTemp,strTemp);
		     this->m_Target.GetWindowText(strPathTemp);
		     sprintf(strTargetTemp,"%s\\%04d%02d%02d.txt",strPathTemp,CurrentYear,CurrentMonth,CurrentDay);
			 if(!CopyFile(strOriginTemp,strTargetTemp,FALSE)){
				 MessageBox("Unable to Copy","ERROR",48);
				 return;
			 }
		     CloseHandle(hTestExist);
			 found=0;
			 LastCurrentDay=CurrentDay;
			 LastCurrentMonth=CurrentMonth;
			 LastCurrentYear=CurrentYear;
			 break;
		  }
		  found++;
		  if(found>30) break;
	}
	sprintf(strTemp2,"%02d/%02d/%04d",LastCurrentDay,LastCurrentMonth,LastCurrentYear);
	this->m_CurrentDay.SetWindowText(strTemp2);

}

void CSimulatorDlg::OnChangeEdit2() 
{
	// TODO: If this is a RICHEDIT control, the control will not
	// send this notification unless you override the CDialog::OnInitDialog()
	// function and call CRichEditCtrl().SetEventMask()
	// with the ENM_CHANGE flag ORed into the mask.
	
	// TODO: Add your control notification handler code here
	
}

void CSimulatorDlg::OnChangeEdit1() 
{
	// TODO: If this is a RICHEDIT control, the control will not
	// send this notification unless you override the CDialog::OnInitDialog()
	// function and call CRichEditCtrl().SetEventMask()
	// with the ENM_CHANGE flag ORed into the mask.
	
	// TODO: Add your control notification handler code here
	
}

void CSimulatorDlg::OnChangeEdit3() 
{
	// TODO: If this is a RICHEDIT control, the control will not
	// send this notification unless you override the CDialog::OnInitDialog()
	// function and call CRichEditCtrl().SetEventMask()
	// with the ENM_CHANGE flag ORed into the mask.
	
	// TODO: Add your control notification handler code here
	
}

void CSimulatorDlg::OnCancel() 
{
	// TODO: Add extra cleanup here
	
	CDialog::OnCancel();
}

void CSimulatorDlg::OnButton1() 
{

	char TempPath[10000],strSource[10000],strTarget[10000],StrSearch[10000];
	CString strPathTemp,strPathTempTarget,strSearch;
	WIN32_FIND_DATA FindData;
	HANDLE hTemp;

	CurrentStatement++;
	this->m_Origin.GetWindowText(strPathTemp);
	sprintf(TempPath,"%s\\%s",strPathTemp,StatementDates[CurrentStatement]);
	this->m_CurrentStatement.SetWindowText(StatementDatesDescription[CurrentStatement]);		 	
	
	this->m_Target.GetWindowText(strPathTempTarget);
	sprintf(StrSearch,"%s\\*.txt",TempPath);

	hTemp=FindFirstFile(StrSearch,&FindData);
	if(hTemp==INVALID_HANDLE_VALUE) return;

	sprintf(strSource,"%s\\%s",TempPath,FindData.cFileName);
	sprintf(strTarget,"%s\\%s",strPathTempTarget,FindData.cFileName);
	CopyFile(strSource,strTarget,FALSE);
	while(FindNextFile(hTemp,&FindData)){
	      sprintf(strSource,"%s\\%s",TempPath,FindData.cFileName);
	      sprintf(strTarget,"%s\\%s",strPathTempTarget,FindData.cFileName);
		  CopyFile(strSource,strTarget,FALSE);
	}

}

void CSimulatorDlg::OnChangeEdit4() 
{
	// TODO: If this is a RICHEDIT control, the control will not
	// send this notification unless you override the CDialog::OnInitDialog()
	// function and call CRichEditCtrl().SetEventMask()
	// with the ENM_CHANGE flag ORed into the mask.
	
	// TODO: Add your control notification handler code here
	
}
