# Microsoft Developer Studio Project File - Name="CLIPSWin" - Package Owner=<4>
# Microsoft Developer Studio Generated Build File, Format Version 6.00
# ** DO NOT EDIT **

# TARGTYPE "Win32 (x86) Application" 0x0101

CFG=CLIPSWin - Win32 Debug
!MESSAGE This is not a valid makefile. To build this project using NMAKE,
!MESSAGE use the Export Makefile command and run
!MESSAGE 
!MESSAGE NMAKE /f "CLIPSWin.mak".
!MESSAGE 
!MESSAGE You can specify a configuration when running NMAKE
!MESSAGE by defining the macro CFG on the command line. For example:
!MESSAGE 
!MESSAGE NMAKE /f "CLIPSWin.mak" CFG="CLIPSWin - Win32 Debug"
!MESSAGE 
!MESSAGE Possible choices for configuration are:
!MESSAGE 
!MESSAGE "CLIPSWin - Win32 Release" (based on "Win32 (x86) Application")
!MESSAGE "CLIPSWin - Win32 Debug" (based on "Win32 (x86) Application")
!MESSAGE 

# Begin Project
# PROP AllowPerConfigDependencies 0
# PROP Scc_ProjName ""
# PROP Scc_LocalPath ""
CPP=cl.exe
MTL=midl.exe
RSC=rc.exe

!IF  "$(CFG)" == "CLIPSWin - Win32 Release"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 0
# PROP BASE Output_Dir "Release"
# PROP BASE Intermediate_Dir "Release"
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 0
# PROP Output_Dir "Objects"
# PROP Intermediate_Dir "Objects"
# PROP Ignore_Export_Lib 0
# PROP Target_Dir ""
# ADD BASE CPP /nologo /W3 /GX /O2 /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /D "_MBCS" /Yu"stdafx.h" /FD /c
# ADD CPP /nologo /W3 /GX /O2 /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /D "_MBCS" /FD /c
# SUBTRACT CPP /YX /Yc /Yu
# ADD BASE MTL /nologo /D "NDEBUG" /mktyplib203 /win32
# ADD MTL /nologo /D "NDEBUG" /mktyplib203 /win32
# ADD BASE RSC /l 0x409 /d "NDEBUG"
# ADD RSC /l 0x409 /d "NDEBUG"
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LINK32=link.exe
# ADD BASE LINK32 kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib /nologo /subsystem:windows /machine:I386
# ADD LINK32 kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib comctl32.lib /nologo /subsystem:windows /machine:I386 /out:"CLIPSServerR.exe"

!ELSEIF  "$(CFG)" == "CLIPSWin - Win32 Debug"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 1
# PROP BASE Output_Dir "Debug"
# PROP BASE Intermediate_Dir "Debug"
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 1
# PROP Output_Dir "Objects"
# PROP Intermediate_Dir "Objects"
# PROP Ignore_Export_Lib 0
# PROP Target_Dir ""
# ADD BASE CPP /nologo /W3 /Gm /GX /ZI /Od /D "WIN32" /D "_DEBUG" /D "_WINDOWS" /D "_MBCS" /Yu"stdafx.h" /FD /GZ /c
# ADD CPP /nologo /G6 /MTd /W3 /Gm /GX /ZI /Od /I "C:\Program Files\Microsoft Visual Studio\VC98\crt" /D "WIN32" /D "_DEBUG" /D "_WINDOWS" /D "_MBCS" /FR /FD /GZ /c
# SUBTRACT CPP /YX /Yc /Yu
# ADD BASE MTL /nologo /D "_DEBUG" /mktyplib203 /win32
# ADD MTL /nologo /D "_DEBUG" /mktyplib203 /win32
# ADD BASE RSC /l 0x409 /d "_DEBUG"
# ADD RSC /l 0x409 /d "_DEBUG"
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LINK32=link.exe
# ADD BASE LINK32 kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib /nologo /subsystem:windows /debug /machine:I386 /pdbtype:sept
# ADD LINK32 KERNEL32Ancien.LIB kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib comctl32.lib ws2_32.lib /nologo /subsystem:windows /debug /machine:I386 /out:"CLIPSServer.exe" /pdbtype:sept

!ENDIF 

# Begin Target

# Name "CLIPSWin - Win32 Release"
# Name "CLIPSWin - Win32 Debug"
# Begin Group "Source Files"

# PROP Default_Filter "cpp;c;cxx;rc;def;r;odl;idl;hpj;bat"
# Begin Source File

SOURCE=.\Source\Clips\agenda.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\analysis.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\argacces.c
# End Source File
# Begin Source File

SOURCE=.\Source\Interface\Balance.C
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\bload.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\bmathfun.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\bsave.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\classcom.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\classexm.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\classfun.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\classinf.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\classini.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\classpsr.c
# End Source File
# Begin Source File

SOURCE=.\source\clips\CLIPServer.cpp
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\clsltpsr.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\commline.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\conscomp.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\constrct.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\constrnt.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\crstrtgy.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\cstrcbin.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\cstrccom.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\cstrcpsr.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\cstrnbin.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\cstrnchk.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\cstrncmp.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\cstrnops.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\cstrnpsr.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\cstrnutl.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\default.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\defins.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\developr.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\dffctbin.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\dffctbsc.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\dffctcmp.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\dffctdef.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\dffctpsr.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\dffnxbin.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\dffnxcmp.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\dffnxexe.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\dffnxfun.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\dffnxpsr.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\dfinsbin.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\dfinscmp.c
# End Source File
# Begin Source File

SOURCE=.\Source\Interface\Dialog1.c
# End Source File
# Begin Source File

SOURCE=.\Source\Interface\Dialog2.c
# End Source File
# Begin Source File

SOURCE=.\Source\Interface\Display.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\drive.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\edbasic.c
# End Source File
# Begin Source File

SOURCE=.\Source\Interface\Edit.C
# End Source File
# Begin Source File

SOURCE=.\Source\Interface\EditUtil.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\edmain.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\edmisc.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\edstruct.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\edterm.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\emathfun.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\engine.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\envrnmnt.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\evaluatn.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\expressn.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\exprnbin.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\exprnops.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\exprnpsr.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\extnfunc.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\factbin.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\factbld.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\factcmp.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\factcom.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\factfun.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\factgen.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\facthsh.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\factlhs.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\factmch.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\factmngr.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\factprt.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\factrete.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\factrhs.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\filecom.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\filertr.c
# End Source File
# Begin Source File

SOURCE=.\Source\Interface\FindWnd.C
# End Source File
# Begin Source File

SOURCE=.\Source\Interface\Frame.C
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\generate.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\genrcbin.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\genrccmp.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\genrccom.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\genrcexe.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\genrcfun.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\genrcpsr.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\globlbin.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\globlbsc.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\globlcmp.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\globlcom.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\globldef.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\globlpsr.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\immthpsr.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\incrrset.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\inherpsr.c
# End Source File
# Begin Source File

SOURCE=.\Source\Interface\Initialization.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\inscom.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\insfile.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\insfun.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\insmngr.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\insmoddp.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\insmult.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\inspsr.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\insquery.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\insqypsr.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\iofun.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\lgcldpnd.c
# End Source File
# Begin Source File

SOURCE=.\Source\Interface\MainFrameToolbar.c
# End Source File
# Begin Source File

SOURCE=.\source\clips\Markowitz.c
# End Source File
# Begin Source File

SOURCE=.\Source\Interface\Mdi.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\memalloc.c
# End Source File
# Begin Source File

SOURCE=.\Source\Interface\Menu.c
# End Source File
# Begin Source File

SOURCE=.\Source\Interface\Menucmds.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\miscfun.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\modulbin.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\modulbsc.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\modulcmp.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\moduldef.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\modulpsr.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\modulutl.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\msgcom.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\msgfun.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\msgpass.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\msgpsr.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\multifld.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\multifun.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\objbin.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\objcmp.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\objrtbin.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\objrtbld.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\objrtcmp.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\objrtfnx.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\objrtgen.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\objrtmch.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\parsefun.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\pattern.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\pprint.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\prccode.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\prcdrfun.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\prcdrpsr.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\prdctfun.c
# End Source File
# Begin Source File

SOURCE=.\Source\Interface\Print.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\prntutil.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\proflfun.c
# End Source File
# Begin Source File

SOURCE=.\Source\Interface\Registry.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\reorder.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\reteutil.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\retract.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\router.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\rulebin.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\rulebld.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\rulebsc.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\rulecmp.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\rulecom.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\rulecstr.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\ruledef.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\ruledlt.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\rulelhs.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\rulepsr.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\scanner.c
# End Source File
# Begin Source File

SOURCE=.\Source\Interface\Search.C
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\sortfun.c
# End Source File
# Begin Source File

SOURCE=.\Source\Interface\Status.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\strngfun.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\strngrtr.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\symblbin.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\symblcmp.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\symbol.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\sysdep.c
# End Source File
# Begin Source File

SOURCE=.\Source\Interface\Text.C
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\textpro.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\tmpltbin.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\tmpltbsc.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\tmpltcmp.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\tmpltdef.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\tmpltfun.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\tmpltlhs.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\tmpltpsr.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\tmpltrhs.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\tmpltutl.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\userdata.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\utility.c
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\watch.c
# End Source File
# Begin Source File

SOURCE=.\Source\Interface\WinMain.c
# End Source File
# End Group
# Begin Group "Header Files"

# PROP Default_Filter "h;hpp;hxx;hm;inl"
# Begin Source File

SOURCE=.\Source\Clips\agenda.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\analysis.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\argacces.h
# End Source File
# Begin Source File

SOURCE=.\Source\Interface\Balance.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\bload.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\bmathfun.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\bsave.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\classcom.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\classexm.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\classfun.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\classinf.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\classini.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\classpsr.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\clips.h
# End Source File
# Begin Source File

SOURCE=.\source\clips\CLIPServer.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\clsltpsr.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\cmptblty.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\commline.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\conscomp.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\constant.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\constrct.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\constrnt.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\crstrtgy.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\cstrcbin.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\cstrccmp.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\cstrccom.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\cstrcpsr.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\cstrnbin.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\cstrnchk.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\cstrncmp.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\cstrnops.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\cstrnpsr.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\cstrnutl.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\default.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\defins.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\developr.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\dffctbin.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\dffctbsc.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\dffctcmp.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\dffctdef.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\dffctpsr.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\dffnxbin.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\dffnxcmp.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\dffnxexe.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\dffnxfun.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\dffnxpsr.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\dfinsbin.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\dfinscmp.h
# End Source File
# Begin Source File

SOURCE=.\Source\Interface\Dialog1.h
# End Source File
# Begin Source File

SOURCE=.\Source\Interface\Dialog2.h
# End Source File
# Begin Source File

SOURCE=.\Source\Interface\Display.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\drive.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\ed.h
# End Source File
# Begin Source File

SOURCE=.\Source\Interface\Edit.H
# End Source File
# Begin Source File

SOURCE=.\Source\Interface\EditUtil.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\emathfun.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\engine.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\envrnmnt.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\evaluatn.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\expressn.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\exprnbin.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\exprnops.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\exprnpsr.h
# End Source File
# Begin Source File

SOURCE=.\Source\Interface\Extensions.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\extnfunc.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\factbin.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\factbld.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\factcmp.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\factcom.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\factfun.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\factgen.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\facthsh.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\factlhs.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\factmch.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\factmngr.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\factprt.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\factrete.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\factrhs.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\filecom.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\filertr.h
# End Source File
# Begin Source File

SOURCE=.\Source\Interface\FindWnd.H
# End Source File
# Begin Source File

SOURCE=.\Source\Interface\Frame.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\generate.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\genrcbin.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\genrccmp.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\genrccom.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\genrcexe.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\genrcfun.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\genrcpsr.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\globlbin.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\globlbsc.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\globlcmp.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\globlcom.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\globldef.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\globlpsr.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\immthpsr.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\incrrset.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\inherpsr.h
# End Source File
# Begin Source File

SOURCE=.\Source\Interface\Initialization.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\inscom.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\insfile.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\insfun.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\insmngr.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\insmoddp.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\insmult.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\inspsr.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\insquery.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\insqypsr.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\iofun.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\lgcldpnd.h
# End Source File
# Begin Source File

SOURCE=.\Source\Interface\MainFrameToolbar.h
# End Source File
# Begin Source File

SOURCE=.\source\clips\Markowitz.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\match.h
# End Source File
# Begin Source File

SOURCE=.\Source\Interface\Mdi.h
# End Source File
# Begin Source File

SOURCE=.\Source\Interface\MDICLIPS.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\memalloc.h
# End Source File
# Begin Source File

SOURCE=.\Source\Interface\Menu.h
# End Source File
# Begin Source File

SOURCE=.\Source\Interface\Menucmds.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\miscfun.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\modulbin.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\modulbsc.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\modulcmp.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\moduldef.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\modulpsr.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\modulutl.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\msgcom.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\msgfun.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\msgpass.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\msgpsr.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\multifld.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\multifun.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\network.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\objbin.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\objcmp.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\object.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\objrtbin.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\objrtbld.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\objrtcmp.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\objrtfnx.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\objrtgen.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\objrtmch.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\parsefun.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\pattern.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\pprint.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\prccode.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\prcdrfun.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\prcdrpsr.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\prdctfun.h
# End Source File
# Begin Source File

SOURCE=.\Source\Interface\Print.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\prntutil.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\proflfun.h
# End Source File
# Begin Source File

SOURCE=.\Source\Interface\Registry.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\reorder.h
# End Source File
# Begin Source File

SOURCE=.\Source\Interface\Resource.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\reteutil.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\retract.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\router.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\rulebin.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\rulebld.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\rulebsc.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\rulecmp.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\rulecom.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\rulecstr.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\ruledef.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\ruledlt.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\rulelhs.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\rulepsr.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\scanner.h
# End Source File
# Begin Source File

SOURCE=.\Source\Interface\Search.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\setup.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\shrtlnkn.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\sortfun.h
# End Source File
# Begin Source File

SOURCE=.\Source\Interface\Status.h
# End Source File
# Begin Source File

SOURCE=.\Source\Interface\StdSDK.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\strngfun.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\strngrtr.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\symblbin.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\symblcmp.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\symbol.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\sysdep.h
# End Source File
# Begin Source File

SOURCE=.\Source\Interface\Text.H
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\textpro.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\tmpltbin.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\tmpltbsc.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\tmpltcmp.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\tmpltdef.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\tmpltfun.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\tmpltlhs.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\tmpltpsr.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\tmpltrhs.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\tmpltutl.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\userdata.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\usrsetup.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\utility.h
# End Source File
# Begin Source File

SOURCE=.\Source\Clips\watch.h
# End Source File
# End Group
# Begin Group "Resource Files"

# PROP Default_Filter "ico;cur;bmp;dlg;rc2;rct;bin;rgs;gif;jpg;jpeg;jpe"
# Begin Source File

SOURCE=.\source\Interface\1.cur
# End Source File
# Begin Source File

SOURCE=.\source\Interface\2.cur
# End Source File
# Begin Source File

SOURCE=.\source\Interface\3.cur
# End Source File
# Begin Source File

SOURCE=.\source\Interface\4.cur
# End Source File
# Begin Source File

SOURCE=.\source\Interface\5.cur
# End Source File
# Begin Source File

SOURCE=.\source\Interface\6.cur
# End Source File
# Begin Source File

SOURCE=.\source\Interface\7.cur
# End Source File
# Begin Source File

SOURCE=.\source\Interface\8.cur
# End Source File
# Begin Source File

SOURCE=.\source\Interface\9.cur
# End Source File
# Begin Source File

SOURCE=.\source\Interface\CLIPSedit.ICO
# End Source File
# Begin Source File

SOURCE=.\source\Interface\CLIPSwinc.ICO
# End Source File
# Begin Source File

SOURCE=.\Source\Interface\MDICLIPS.rc
# End Source File
# Begin Source File

SOURCE=.\source\Interface\Query.cur
# End Source File
# Begin Source File

SOURCE=.\source\Interface\Wait.cur
# End Source File
# End Group
# End Target
# End Project
