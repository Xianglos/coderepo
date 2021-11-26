Private connStr As String

Sub query()
'
' query 宏
'
' 快捷键: Ctrl+Shift+Q
'
    connStr = "Provider=SQLOLEDB;Server=localhost;Database=XXXXXXXX;Uid=XXXXXXXX;Pwd=XXXXXXXX"
    'MsgBox ("表名:" + ActiveCell.Cells.Value)
    Dim checkTable As Boolean
    checkTable = isTableExists(ActiveCell.Cells.Value)
        
    If checkTable Then
        Call selectData(ActiveCell.Cells.Value)
    Else
        MsgBox ("E1:表不存在")
    End If
    
    
End Sub
'取数据
Sub selectData(tableName As String)
'1. 引用ADO工具
'2. 创建连接对象
Dim con As New ADODB.Connection
'3. 建立数据库的连接
con.connectionString = connStr
con.Open
MsgBox ("连接成功!" & vbCrLf & "数据库状态：" & con.State & vbCrLf & "数据库版本：" & con.Version)
''''''''''''''''''''''''''''''''''''''''
'TODO

''''''''''''''''''''''''''''''''''''''''
'4. 关闭数据库连接
con.Close
Set con = Nothing
End Sub
'检查表是否存在
Private Function isTableExists(tableName As String) As Boolean

    Dim con As New ADODB.Connection
    Dim conn As ADODB.Connection
    Dim rs As ADODB.Recordset
    Set conn = New ADODB.Connection
    Set rs = New ADODB.Recordset
    Dim querySql As String
    Dim countCol As Integer
    
    '配置连接串
    conn.connectionString = connStr
    conn.Open
    '拼接sql，查找指定表的列的个数
    querySql = "SELECT COUNT(*) AS CNT FROM SYSCOLUMNS WHERE ID = OBJECT_ID('" + tableName + "');"
    rs.Open querySql, conn
    countCol = Val(rs.GetString)
    
    '关闭连接
    rs.Close: Set rs = Nothing
    conn.Close: Set conn = Nothing

    If countCol > 0 Then
        isTableExists = True
    Else
        isTableExists = False
    End If
        
End Function
