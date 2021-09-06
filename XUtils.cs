//Xianglos的工具箱
public class XUtils
{
    public String GetGUID()
    {
        //生成一个32位长的guid
        return Guid.NewGuid().ToString("N");
    }
    public String GetCurrentDate()
    {
        //2020/9/16 00:00:00
        String year = DateTime.Now.Year.ToString(); //获取年份  // 2008
        String days = DateTime.Now.DayOfYear.ToString(); //获取第几天   // 248
        String hour = DateTime.Now.Hour.ToString();//获取小时   // 20
        String minute = DateTime.Now.Minute.ToString(); //获取分钟   // 31
        String seconds = DateTime.Now.Second.ToString(); //获取秒数   // 45
        return year + days + hour + minute + seconds;
    }
    public String GetAddr()
    {
        //根据IP地址判断大致的地理位置
        //调用搜狐接口 http://pv.sohu.com/cityjson?ie=utf-8
        string str = "";
        try
        {
            WebClient MyWebClient = new WebClient();
            MyWebClient.Credentials = CredentialCache.DefaultCredentials;//获取或设置用于向Internet资源的请求进行身份验证的网络凭据
            Byte[] pageData = MyWebClient.DownloadData("http://pv.sohu.com/cityjson?ie=utf-8"); //从指定网站下载数据
            string pageHtml = Encoding.UTF8.GetString(pageData); //网站页面采用UTF-8
                                                                 //var returnCitySN = {\"cip\": \"58.213.151.194\", \"cid\": \"320100\", \"cname\": \"江苏省南京市\"};
                                                                 //第11个双引号之后，第12个双引号之前
            char[] ans = pageHtml.ToCharArray();
            int head = 0;
            int end = 0;
            int count1 = 0;
            for (int i = 0; i < ans.Length; i++)
            {
                if (ans[i] == '"')
                {
                    count1++;
                }
                if (count1 == 11 && head == 0)
                {
                    head = i + 1;
                }
                if (count1 == 12 && end == 0)
                {
                    end = i - 1;
                    break;
                }
            }
            for (int i = 0; i < end - head + 1; i++)
            {
                str = str + ans[i + head];
            }
            //pageHtml = pageHtml.Substring(pageHtml.Length - 4, pageHtml.Length);
            //Console.WriteLine(pageHtml);//在控制台输入获取的内容
            //using (StreamWriter sw = new StreamWriter("c:\\test\\ouput.html"))//将获取的内容写入文本
            //{
            //    sw.Write(pageHtml);
            //}
            //Console.ReadLine();
        }
        catch (WebException webEx)
        {
            Console.WriteLine(webEx.Message.ToString());
        }
        return str;
    }
    public bool SQLServerExecuteInsert(string SqlString, string ConnectionString)
    {
        //执行insert
        try
        {
            String connstr = System.Configuration.ConfigurationManager.ConnectionStrings[ConnectionString].ToString();
            SqlConnection con = new SqlConnection(connstr);
            string strsql = SqlString;
            SqlCommand cmd = new SqlCommand(strsql, con);
            con.Open();
            cmd.ExecuteNonQuery();
            con.Close();
            return true;
            //能有什么错啊
        }
        catch (Exception)
        {
            //错了自己找原因
            return false;
        }
    }
    public DataSet SQLServerExcuteSelect(string SqlString, string ConnectionString)
    {
        //执行select
        DataSet ans_ds = new DataSet();
        try
        {
            String connstr = System.Configuration.ConfigurationManager.ConnectionStrings[ConnectionString].ToString();
            SqlConnection con = new SqlConnection(connstr);
            string strsql = SqlString;
            SqlCommand cmd = new SqlCommand(strsql, con);
            SqlDataAdapter da = new SqlDataAdapter(cmd);
            con.Open();
            da.Fill(ans_ds);
            con.Close();
            return ans_ds;
        }
        catch (Exception)
        {
            return ans_ds;
        }
    }
    public int GetSecondFromFullyDate(int year, int month, int day, int hour, int minute, int second)
    {
        //返回从指定年月日时分秒开始到现在，经过的秒数
        int sec = 1;
        try
        {
            DateTime date20000101 = new DateTime(year, month, day, hour, minute, second);
            TimeSpan interval = DateTime.Now - date20000101;
            sec = (int)interval.TotalSeconds;
        }
        catch (ArgumentOutOfRangeException)
        {
            //日期格式不对出现异常
            return 0;
        }
        catch (Exception)
        {
            //也不知道什么就发生了异常
            return 0;
        }
        return sec;
    }
    public int GetSecondFromDate(int year, int month, int day)
    {
        //返回从指定年月日开始到现在，经过的秒数
        return GetSecondFromFullyDate(year, month, day, 0, 0, 0);
    }
    public String ConvertDecToHex(int dec)
    {
        //整数int范围，十进制转十六进制
        if (dec < 0)
        {
            return "wrongInt";
        }
        String ans = "";
        try
        {
            ans = Convert.ToString(dec, 16);
        }
        catch (Exception)
        {
            ans = "wrongInt";
        }
        return ans;
    }
}
