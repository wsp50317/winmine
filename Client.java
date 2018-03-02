

import java.io.*;
import java.net.*;
import java.lang.*;
public class Client
{
  public static void main(String[] args)
  {
    String message, stdin , ip;// message讀入Server端輸出 , stdin 寫出 Client 端輸出
    int port;
    try
    {
      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
      System.out.print("請輸入要連接port : ");
      port = Integer.parseInt(br.readLine());//讀入使用者輸入的port
      Socket con = new Socket("127.0.0.1",port);//建立連線
      if(con.isConnected())
      {
        System.out.println("Connect Success");
       // System.out.println("輸入姓名");
        //String name = br.readLine();
        //
        Thread thread = new Thread(new ClientTalk(con));//建立多執行緒(錯在這)
        
        thread.start(); //啟動
        DataOutputStream dos = new DataOutputStream(con.getOutputStream());//建立DataOutputStream將資料寫至Server
        int beginstart=1;
		while(true)
        {          
	//////////
          //System.out.print("請輸入 ： ");
         if(beginstart==1)
	{
		stdin="start"; //一開始先傳"start"給SERVER
		beginstart=0;
	}
	else
	{
		stdin = br.readLine(); //輸入
	}
	dos.writeUTF(stdin); //輸出
	if ("over".equals(stdin)) {
		break;
	}
	/////////
        }      
      }
      else
        System.out.println("Connect fails");
    }
    catch(Exception e)
    {
      System.out.println(e.toString());
    }
  }//end of main
}//end of class Client
class ClientTalk implements Runnable //毒入其他Client端輸入的聊天內容
{
  DataInputStream dis;//建立DataInput 讀取使用者傳送內容
  Socket clientSocket;
  public ClientTalk(Socket clientSocket) 
  {
    this.clientSocket = clientSocket;    
  }
  public void run() 
  {
    try 
    {
      dis = new DataInputStream(clientSocket.getInputStream());//宣告一個將server端資料寫出的變數
      while (true) 
      {        
        /////
		String read = dis.readUTF(); 
	System.out.println(read);
	if(read.endsWith("遊戲結束"))
	{
		clientSocket.close();
		System.exit(1);
		break;
	}
		/////
      }
    }
    catch(IOException e) 
    {
      //System.out.println(e.toString());
    }
  }
}