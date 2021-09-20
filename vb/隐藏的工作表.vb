Private Sub Worksheet_Activate()
    Dim sh As Object
    For Each sh In Sheets
  If sh.Name <> "DIR" Then sh.Visible = xlSheetHidden 'DIR是目录的工作表名
    Next
End Sub

Private Sub Worksheet_FollowHyperlink(ByVal Target As Hyperlink)
    Dim sh As Object
    On Error Resume Next
    Set sh = Sheets(Replace(Split(Target.SubAddress, "!")(0), "'", ""))
    If Not sh Is Nothing Then
    Application.EnableEvents = False
    sh.Visible = xlSheetVisible
    Target.Follow
    Application.EnableEvents = True
    End If
End Sub
