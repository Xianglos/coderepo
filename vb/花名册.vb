Private n As Integer

Sub copy()
'
' 快捷键: Ctrl+w
'
    '定义一个文件系统对象
    Dim fso As New FileSystemObject
    Dim fld As Folder, filepath As String
    n = 0
    '文件夹的绝对路径，这里手动修改
    filepath = InputBox("请输入文件路径") & "\"
    'filepath = filepath
    'filepath = "C:\Users\83429\Desktop\test\emp\"
    '判断文件是否存在
    If fso.FolderExists(filepath) Then
        'Range("a:a").ClearContents
        Set fld = fso.GetFolder(filepath)
        '调用函数
        Call LookUpAllFiles(fld, filepath)
    Else
        MsgBox "文件夹不存在"
    End If
End Sub

'遍历文件的过程，并填充到工作表
Sub LookUpAllFiles(fld As Folder, filepath As String)
    '定义一个文件夹和文件变量
    Dim fil As File, outFld As Folder
    '获取文件夹下所有文件
    Set subfiles = fld.Files()
    '获取文件夹下所有文件夹
    Set SubFolders = fld.SubFolders
    '遍历文件
    n = 2
    For Each fil In fld.Files
        
        '打开文件、拷贝内容
        Call copyValue(filepath, fil.Name, n)
        n = n + 1
        
    Next

    '遍历文件夹
    For Each outFld In SubFolders
        '调用函数自身
        LookUpAllFiles outFld, filepath
    Next
End Sub
'拷贝内容[文件名，当前操作的行]
Sub copyValue(filepath As String, filename As String, lineIndex As Integer)

    '打开文件
    Dim openfile As String
    openfile = filepath & filename
    Workbooks.Open filename:=openfile
    '隐藏文件
    'ActiveWorkbook.Visible = False
    'svalue=activeworkbook.sheets(1).range("a1").value '这句是用变量取得该文件表1中a1单元格的值
    '显示文件
    'ActiveWorkbook.viseble = True

    '目标文件
    Dim targetFilename
    targetFilename = "00 花名册.xlsm"
    
    Dim targetPosition As String
    'Dim sourcePosition As String
    
    '姓名
    Windows(filename).Activate
    Range("B1").Select
    Selection.copy
    Windows(targetFilename).Activate
    targetPosition = "B" & lineIndex
    Range(targetPosition).Select
    Selection.PasteSpecial Paste:=xlPasteValues, Operation:=xlNone, SkipBlanks:=False, Transpose:=False
    
    '性别
    Windows(filename).Activate
    Range("D1").Select
    Selection.copy
    Windows(targetFilename).Activate
    targetPosition = "C" & lineIndex
    Range(targetPosition).Select
    'Range("C2").Select
    Selection.PasteSpecial Paste:=xlPasteValues, Operation:=xlNone, SkipBlanks:=False, Transpose:=False
    
    '身份证号
    Windows(filename).Activate
    Range("F1").Select
    Selection.copy
    Windows(targetFilename).Activate
    targetPosition = "D" & lineIndex
    Range(targetPosition).Select
    'Range("D2").Select
    Selection.PasteSpecial Paste:=xlPasteValues, Operation:=xlNone, SkipBlanks:=False, Transpose:=False
    
    '文化程度
    Windows(filename).Activate
    Range("F2").Select
    Selection.copy
    Windows(targetFilename).Activate
    targetPosition = "E" & lineIndex
    Range(targetPosition).Select
    'Range("E2").Select
    Selection.PasteSpecial Paste:=xlPasteValues, Operation:=xlNone, SkipBlanks:=False, Transpose:=False
    
    '所在部门
    Windows(filename).Activate
    Range("B2").Select
    Selection.copy
    Windows(targetFilename).Activate
    targetPosition = "G" & lineIndex
    Range(targetPosition).Select
    'Range("G2").Select
    Selection.PasteSpecial Paste:=xlPasteValues, Operation:=xlNone, SkipBlanks:=False, Transpose:=False
    
    '职务
    Windows(filename).Activate
    Range("D2").Select
    Selection.copy
    Windows(targetFilename).Activate
    targetPosition = "H" & lineIndex
    Range(targetPosition).Select
    'Range("H2").Select
    Selection.PasteSpecial Paste:=xlPasteValues, Operation:=xlNone, SkipBlanks:=False, Transpose:=False
    
    '岗位
    Windows(filename).Activate
    Range("F2").Select
    Selection.copy
    Windows(targetFilename).Activate
    targetPosition = "I" & lineIndex
    Range(targetPosition).Select
    'Range("I2").Select
    Selection.PasteSpecial Paste:=xlPasteValues, Operation:=xlNone, SkipBlanks:=False, Transpose:=False
    
    '进厂日期
    Windows(filename).Activate
    Range("H2").Select
    Selection.copy
    Windows(targetFilename).Activate
    targetPosition = "F" & lineIndex
    Range(targetPosition).Select
    'Range("F2").Select
    ActiveSheet.Paste
    
    '户籍地
    Dim tmphome1
    Windows(filename).Activate
    Range(targetPosition).Select
    tmphome1 = Range("B3:H3").Value
    Selection.copy
    Windows(targetFilename).Activate
    targetPosition = "M" & lineIndex
    Range(targetPosition).Value = tmphome1
    'Range("M2").Select
    'Selection.PasteSpecial Paste:=xlPasteValues, Operation:=xlNone, SkipBlanks:=False, Transpose:=False
    
    '常住地
    Dim tmphome2
    Windows(filename).Activate
    tmphome2 = Range("B4:H4").Value
    Selection.copy
    Windows(targetFilename).Activate
    targetPosition = "N" & lineIndex
    Range(targetPosition).Value = tmphome2
    'Range("N2").Select
    'Selection.PasteSpecial Paste:=xlPasteValues, Operation:=xlNone, SkipBlanks:=False, Transpose:=False

    '关闭文件，close有两个参数，true是关闭保存修改，false是关闭时不保存修改
    Windows(filename).Activate
    ActiveWorkbook.Close False '
End Sub
