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
Dim conn As New ADODB.Connection
'3. 建立数据库的连接
'Set rs = New ADODB.Recordset
conn.Open connStr

MsgBox ("连接成功!" & vbCrLf & "数据库状态：" & conn.State & vbCrLf & "数据库版本：" & conn.Version)
conn.Close
''''''''''''''''''''''''''''''''''''''''
'TODO

'取得列的个数
conn.Open connStr

Dim queryCountColsSql As String
queryCountColsSql = "select count(syscolumns.name) as colname from syscolumns,systypes where syscolumns.xusertype = systypes.xusertype and syscolumns.id = object_id ('" + tableName + "')"

Dim rsCountCols As New ADODB.Recordset
rsCountCols.Open queryCountColsSql, conn

Dim countCols As Integer
countCols = Val(rsCountCols.Fields(0).Value)

rsCountCols.Close
conn.Close

'取得表结构
conn.Open connStr

Dim querySql As String
querySql = "select syscolumns.name as colname from syscolumns,systypes where syscolumns.xusertype = systypes.xusertype and syscolumns.id = object_id ('" + tableName + "')"

Dim rsCols As New ADODB.Recordset
rsCols.Open querySql, conn

'绘制表结构
Cells(ActiveCell.Row + 1, ActiveCell.Column - 1).Value = "列名"

Dim i As Integer
For i = 0 To countCols - 1 Step 1
    
    Cells(ActiveCell.Row + 1, ActiveCell.Column + i).Value = rsCols(0).Value
    rsCols.MoveNext
        
Next

rsCols.Close
conn.Close

'取得表数据
'select row_number()over(order by guid) as colnum,* from URL_LONG_TO_SHROT;

conn.Open connStr
Dim queryDataSql As String
queryDataSql = "select row_number()over(order by " + Cells(ActiveCell.Row + 1, ActiveCell.Column).Value + ") as colnum,* from " + tableName + ";"

Dim rsData As New ADODB.Recordset
Set rsData = conn.Execute(queryDataSql)

Range(Chr(ActiveCell.Column + 63) & CStr(ActiveCell.Row + 2)).CopyFromRecordset rsData

conn.Close

''''''''''''''''''''''''''''''''''''''''
'4. 关闭数据库连接
Set conn = Nothing
End Sub
'检查表是否存在
Private Function isTableExists(tableName As String) As Boolean

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
    querySql = "select count(syscolumns.name) as colname from syscolumns,systypes where syscolumns.xusertype = systypes.xusertype and syscolumns.id = object_id ('" + tableName + "')"
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
