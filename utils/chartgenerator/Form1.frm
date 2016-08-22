VERSION 5.00
Object = "{248DD890-BB45-11CF-9ABC-0080C7E7B78D}#1.0#0"; "MSWINSCK.OCX"
Begin VB.Form frmGenerator 
   Caption         =   "Chart Generator"
   ClientHeight    =   630
   ClientLeft      =   60
   ClientTop       =   345
   ClientWidth     =   7665
   LinkTopic       =   "Form1"
   ScaleHeight     =   630
   ScaleWidth      =   7665
   StartUpPosition =   3  'Windows Default
   Begin MSWinsockLib.Winsock Winsock1 
      Left            =   555
      Top             =   765
      _ExtentX        =   741
      _ExtentY        =   741
      _Version        =   393216
      LocalPort       =   8000
   End
   Begin VB.CommandButton Command1 
      Caption         =   "Simulate"
      Height          =   480
      Left            =   6120
      TabIndex        =   2
      Top             =   60
      Width           =   1545
   End
   Begin VB.TextBox txtTarget 
      Height          =   285
      Left            =   735
      TabIndex        =   1
      Text            =   "C:\Program Files\Apache Group\Tomcat 4.1\webapps\abia\Images"
      Top             =   75
      Width           =   5310
   End
   Begin VB.Label Label1 
      Caption         =   "Diretorio"
      Height          =   255
      Left            =   120
      TabIndex        =   0
      Top             =   120
      Width           =   735
   End
End
Attribute VB_Name = "frmGenerator"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = True
Attribute VB_Exposed = False
Option Explicit

Dim obj As Object
Dim Retorno As String

    'Worksheets("Plan1").ChartObjects(1).Chart.Export 'FileName:="C:\current_sales.gif", FilterName:="GIF"

    'ActiveSheet.ChartObjects("Gráfico 1").Activate
    'ActiveChart.SetSourceData Source:=Sheets("Cotacoes").Range("A3:D3"), PlotBy '    :=xlColumns

Private Sub Generate(mensagem As String)
    Dim NumeroValores As Integer, Sequence As Long, f As Integer, final As Integer, inicial As Integer
    Dim TempToken As String, TipoGrafico As String
    Dim ValorX As String, ValorY As String, ValorValor As String
    Dim sequencia As String
    Dim valores As Collection
    Dim continuar As Boolean
    Dim TempclsValor As clsValor
    Dim MinVal As Long
    Dim MaxVal As Long
    
    
    Set valores = New Collection
    
    inicial = InStr(mensagem, "<TipoGrafico")
    TempToken = Mid(mensagem, InStr(mensagem, "<TipoGrafico"))
    TempToken = Mid(TempToken, 1, InStr(TempToken, "/>") - 1)
    
    TempToken = Mid(TempToken, InStr(TempToken, "Tipo=""") + 6)
    TempToken = Mid(TempToken, 1, InStr(TempToken, """") - 1)
    TipoGrafico = TempToken
    
    mensagem = Mid(mensagem, inicial)
    inicial = InStr(mensagem, "<Sequencia")
    TempToken = Mid(mensagem, InStr(mensagem, "<Sequencia"))
    TempToken = Mid(TempToken, 1, InStr(TempToken, "/>") - 1)
    
    TempToken = Mid(TempToken, InStr(TempToken, "Valor=""") + 7)
    TempToken = Mid(TempToken, 1, InStr(TempToken, """") - 1)
    sequencia = TempToken
    
    mensagem = Mid(mensagem, inicial)
    inicial = InStr(mensagem, "<NumeroValores")
    TempToken = Mid(mensagem, InStr(mensagem, "<NumeroValores"))
    TempToken = Mid(TempToken, 1, InStr(TempToken, "/>") - 1)
    
    TempToken = Mid(TempToken, InStr(TempToken, "Valor=""") + 7)
    TempToken = Mid(TempToken, 1, InStr(TempToken, """") - 1)
    NumeroValores = TempToken
   
    mensagem = Mid(mensagem, inicial)
    inicial = InStr(mensagem, "<MaxVal")
    TempToken = Mid(mensagem, InStr(mensagem, "<MaxVal"))
    TempToken = Mid(TempToken, 1, InStr(TempToken, "/>") - 1)
    
    TempToken = Mid(TempToken, InStr(TempToken, "Valor=""") + 7)
    TempToken = Mid(TempToken, 1, InStr(TempToken, """") - 1)
    MaxVal = TempToken
   
    mensagem = Mid(mensagem, inicial)
    inicial = InStr(mensagem, "<MinVal")
    TempToken = Mid(mensagem, InStr(mensagem, "<MinVal"))
    TempToken = Mid(TempToken, 1, InStr(TempToken, "/>") - 1)
    
    TempToken = Mid(TempToken, InStr(TempToken, "Valor=""") + 7)
    TempToken = Mid(TempToken, 1, InStr(TempToken, """") - 1)
    MinVal = TempToken
   
   
    mensagem = Mid(mensagem, inicial)
    continuar = True
    While continuar
        inicial = InStr(mensagem, "<ValoresXY")
        If inicial > 0 Then
            Set TempclsValor = New clsValor
            TempToken = Mid(mensagem, InStr(mensagem, "<ValoresXY"))
            final = inicial + InStr(TempToken, "/>") + 2
            TempToken = Mid(TempToken, 1, InStr(TempToken, "/>") - 1)
    
            TempToken = Mid(TempToken, InStr(TempToken, "X=""") + 3)
            TempToken = Mid(TempToken, 1, InStr(TempToken, """") - 1)
            ValorX = TempToken
                  
            TempToken = Mid(mensagem, InStr(mensagem, "<ValoresXY"))
            TempToken = Mid(TempToken, 1, InStr(TempToken, "/>") - 1)
    
            TempToken = Mid(TempToken, InStr(TempToken, "Y=""") + 3)
            TempToken = Mid(TempToken, 1, InStr(TempToken, """") - 1)
            ValorY = TempToken
        
            TempToken = Mid(mensagem, InStr(mensagem, "<ValoresXY"))
            TempToken = Mid(TempToken, 1, InStr(TempToken, "/>") - 1)
    
            TempToken = Mid(TempToken, InStr(TempToken, "Valor=""") + 7)
            TempToken = Mid(TempToken, 1, InStr(TempToken, """") - 1)
            ValorValor = TempToken
            
            TempclsValor.X = CDbl(ValorX)
            TempclsValor.Y = CDbl(ValorY)
            TempclsValor.Valor = CDbl(ValorValor)
            valores.Add TempclsValor
            mensagem = Mid(mensagem, final)
        Else
            continuar = False
        End If
    Wend
    
    If obj Is Nothing Then
       Set obj = CreateObject("Excel.Application")
       obj.Workbooks.Open "ChartGenerator.xls"
    End If
    
    obj.Workbooks(1).Worksheets(TipoGrafico).ChartObjects(1).Chart.Axes(2).MinimumScale = MinVal
    obj.Workbooks(1).Worksheets(TipoGrafico).ChartObjects(1).Chart.Axes(2).MaximumScale = MaxVal
    For f = 1 To valores.Count
          obj.Workbooks(1).Worksheets(TipoGrafico).Cells(valores.Item(f).Y, valores.Item(f).X) = valores.Item(f).Valor
    Next
    If TipoGrafico = "Cotacoes" Or TipoGrafico = "Bollinger" Then
       obj.Workbooks(1).Worksheets(TipoGrafico).ChartObjects(1).Chart.SetSourceData obj.Workbooks(1).Sheets(TipoGrafico).Range("B2:D" & (NumeroValores + 1)), 2
    End If
    If TipoGrafico = "Candlestick" Then
       obj.Workbooks(1).Worksheets(TipoGrafico).ChartObjects(1).Chart.SetSourceData obj.Workbooks(1).Sheets(TipoGrafico).Range("A2:D" & (NumeroValores + 1)), 2
    End If
    If TipoGrafico = "MA5" Then
       obj.Workbooks(1).Worksheets(TipoGrafico).ChartObjects(1).Chart.SetSourceData obj.Workbooks(1).Sheets(TipoGrafico).Range("A2:B" & (NumeroValores + 1)), 2
    End If
    If TipoGrafico = "RSI" Then
       obj.Workbooks(1).Worksheets(TipoGrafico).ChartObjects(1).Chart.SetSourceData obj.Workbooks(1).Sheets(TipoGrafico).Range("A2:B" & (NumeroValores + 1)), 2
    End If
    
    obj.Workbooks(1).Worksheets(TipoGrafico).ChartObjects("Gráfico 1").Chart.Export Me.txtTarget & "\" & "Img" & sequencia & ".gif", "GIF"
    obj.Workbooks(1).Save
    Retorno = ""
    Me.Winsock1.SendData "<Message Type=GenerateChart>" & Chr(13) & Chr(10) & "</Message>" & Chr(13) & Chr(10)
    DoEvents
    Me.Winsock1.Close
    Me.Winsock1.Listen
End Sub


Private Sub Command1_Click()
    Dim mensagem As String
    Dim contador
    
    contador = 5
    
    mensagem = ""
    mensagem = mensagem & "<Message Type=GenerateChart>" & Chr(13) & Chr(10)
    mensagem = mensagem & "   <TipoGrafico Tipo=""Cotacoes""/>" & Chr(13) & Chr(10)
    mensagem = mensagem & "   <Sequencia   Valor=""" & contador & """/>" & Chr(13) & Chr(10)
    mensagem = mensagem & "   <NumeroValores  Valor=""8""/>" & Chr(13) & Chr(10)
    mensagem = mensagem & "   <ValoresXY   X=""2"" Y=""2"" Valor=""10"" />" & Chr(13) & Chr(10)
    mensagem = mensagem & "   <ValoresXY   X=""2"" Y=""3"" Valor=""11"" />" & Chr(13) & Chr(10)
    mensagem = mensagem & "   <ValoresXY   X=""2"" Y=""4"" Valor=""12"" />" & Chr(13) & Chr(10)
    mensagem = mensagem & "   <ValoresXY   X=""2"" Y=""5"" Valor=""13"" />" & Chr(13) & Chr(10)
    mensagem = mensagem & "   <ValoresXY   X=""2"" Y=""6"" Valor=""14"" />" & Chr(13) & Chr(10)
    mensagem = mensagem & "   <ValoresXY   X=""2"" Y=""7"" Valor=""15"" />" & Chr(13) & Chr(10)
    mensagem = mensagem & "   <ValoresXY   X=""2"" Y=""8"" Valor=""16"" />" & Chr(13) & Chr(10)
    mensagem = mensagem & "   <ValoresXY   X=""2"" Y=""9"" Valor=""17"" />" & Chr(13) & Chr(10)
    
    mensagem = mensagem & "   <ValoresXY   X=""3"" Y=""2"" Valor=""20"" />" & Chr(13) & Chr(10)
    mensagem = mensagem & "   <ValoresXY   X=""3"" Y=""3"" Valor=""21"" />" & Chr(13) & Chr(10)
    mensagem = mensagem & "   <ValoresXY   X=""3"" Y=""4"" Valor=""22"" />" & Chr(13) & Chr(10)
    mensagem = mensagem & "   <ValoresXY   X=""3"" Y=""5"" Valor=""23"" />" & Chr(13) & Chr(10)
    mensagem = mensagem & "   <ValoresXY   X=""3"" Y=""6"" Valor=""24"" />" & Chr(13) & Chr(10)
    mensagem = mensagem & "   <ValoresXY   X=""3"" Y=""7"" Valor=""25"" />" & Chr(13) & Chr(10)
    mensagem = mensagem & "   <ValoresXY   X=""3"" Y=""8"" Valor=""26"" />" & Chr(13) & Chr(10)
    mensagem = mensagem & "   <ValoresXY   X=""3"" Y=""9"" Valor=""27"" />" & Chr(13) & Chr(10)
    
    mensagem = mensagem & "   <ValoresXY   X=""4"" Y=""2"" Valor=""18"" />" & Chr(13) & Chr(10)
    mensagem = mensagem & "   <ValoresXY   X=""4"" Y=""3"" Valor=""18"" />" & Chr(13) & Chr(10)
    mensagem = mensagem & "   <ValoresXY   X=""4"" Y=""4"" Valor=""19"" />" & Chr(13) & Chr(10)
    mensagem = mensagem & "   <ValoresXY   X=""4"" Y=""5"" Valor=""20"" />" & Chr(13) & Chr(10)
    mensagem = mensagem & "   <ValoresXY   X=""4"" Y=""6"" Valor=""21"" />" & Chr(13) & Chr(10)
    mensagem = mensagem & "   <ValoresXY   X=""4"" Y=""7"" Valor=""22"" />" & Chr(13) & Chr(10)
    mensagem = mensagem & "   <ValoresXY   X=""4"" Y=""8"" Valor=""24"" />" & Chr(13) & Chr(10)
    mensagem = mensagem & "   <ValoresXY   X=""4"" Y=""9"" Valor=""20"" />" & Chr(13) & Chr(10)
    mensagem = mensagem & "</Message>"
    
    Generate mensagem
End Sub

Private Sub Form_Load()
    Me.Winsock1.LocalPort = 8000
    Me.Winsock1.Listen
End Sub

Private Sub Form_Unload(Cancel As Integer)
    If obj Is Nothing Then Exit Sub
    obj.Workbooks(1).Save
    obj.Quit
End Sub

Private Sub Winsock1_ConnectionRequest(ByVal requestID As Long)
      Me.Winsock1.Close
      Me.Winsock1.Accept requestID
End Sub

Private Sub Winsock1_DataArrival(ByVal bytesTotal As Long)
      Dim TempStr As String
      
      Me.Winsock1.GetData TempStr
      Retorno = Retorno & TempStr
      Debug.Print Retorno
      If InStr(Retorno, "</Message") > 0 Then
         Generate Retorno
      End If
End Sub

