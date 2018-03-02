

import java.io.*;
import java.net.*;
import java.lang.*;
import java.util.*;

public class Proxy
{
  public static void main(String[] args)
  {
    int count = 0;
    ArrayList<DataOutputStream> clientOutputStreams = new ArrayList<DataOutputStream>();
    try
    {
      BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
      int port;
      //System.out.print("請輸入port : ");///Proxy 請打5508
      //port = Integer.parseInt(in.readLine());
      port = 5508;
      System.out.println("Wating for connection(Proxy port=5508)");
      
      ServerSocket serverSocket = new ServerSocket(port);//開始監聽port連線請求。
      
      while (true) 
      {
        Socket clientSocket = serverSocket.accept();
        if(clientSocket.isConnected())
        {
          try
          {
            count ++ ;
            System.out.println("第" + count + "位Client加入");
            DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
            clientOutputStreams.add(dos);
            dos.writeUTF("您是第" + count + "位Client");
            //
            Thread thread = new Thread(new ClientTalk2(clientSocket , clientOutputStreams));//建立一個多執行緒(錯在這)
            thread.start(); //啟動該多執行緒
          }
          catch (Exception e) 
          {
            count --;
            System.out.println(e.toString() + "count = " + count);
          }
        }
      }
    }
    catch (Exception e) 
    {
      //System.out.println(e.toString());
    }
    
 
  }
}
 
class ClientTalk2 implements Runnable 
{
  DataInputStream dis;//宣告一個讀取Client傳送過來的字串物件
  
  ArrayList<DataOutputStream> clientOutputStreams = new ArrayList<DataOutputStream>();
  protected int count ;
  protected Socket clientSocket;

private Socket con;
  
  public ClientTalk2(Socket clientSocket , ArrayList<DataOutputStream> clientOutputStreams )
  {
    this.clientOutputStreams = clientOutputStreams;
    this.clientSocket = clientSocket;
    try
    {
      dis = new DataInputStream(clientSocket.getInputStream());//宣告一個將server端資料寫出的變數
    }
    catch(Exception e)
    {
      //System.out.println(e.toString());
    }    
  }
  public void run() //多執行緒，run
  {
	DataInputStream dis = null;
	DataOutputStream dos = null;
	
	DataOutputStream dosToSer=null;
	DataInputStream disToSer=null;

	  try{
		dis = new DataInputStream(clientSocket.getInputStream());
		dos = new DataOutputStream(clientSocket.getOutputStream());
		
        ///建立Proxy與Server的連線//////
		int port;
        port = 5507;//預設為5507 有時間再改
        Socket con = new Socket("127.0.0.1",port);//建立連線
        this.con = con;
        
        //if(con.isConnected())
        //{
          //System.out.println("Connect Success");
          //System.out.println("姓名:guest");
          //String name = "guest";
          
          dosToSer = new DataOutputStream(con.getOutputStream());//建立DataOutputStream將資料寫至Server
          disToSer = new DataInputStream(con.getInputStream());
       // }
        /////////////////
	  }catch (Exception e) 
      {  
		  //System.out.println(e.toString());
	  }
		
		
      while (true) 
      { 
    	  String aa;//廣播用的
    	  int nums[] = null;
    	  int len = 0;
    	  String strs[]=null;
    	  try{
    		
	        String temp = dis.readUTF();///從client傳過來的
	        
	        if(!(temp.startsWith("start")))///寄開始訊號時，不給切
	        {
		    	strs = temp.split(" "); //將兩數切開
		    	len=strs.length;
		    	if(len==2)
		    	{
			    	nums = new int[strs.length]; 
			    	for(int i=0;i<strs.length;++i)
			    	{ 
			    		nums[i] = Integer.valueOf(strs[i]); 
			    	}
		    	}
		    	else
		    	{
		    		nums = new int[2]; 
		    		nums[0]=-1;
		    		nums[1]=-1;
		    	}
	        }
	        

	        if((!(temp.startsWith("start"))) && (nums[0]>10 || nums[1]>10 || nums[0]==0 || nums[1]==0)){//輸入錯的數字
	    		aa="不合理之輸入!!\n";
	    		broadCast(aa);
	    	}
	        else if((!(temp.startsWith("start"))) && len!=2)
	        {
	    		aa="不合理之輸入!!\n";
	    		broadCast(aa);
	        }
	    	else //你的輸入棒極了!!!
	    	{
		        dosToSer.writeUTF(temp);///傳給Server
		        
		        String read = disToSer.readUTF();///從SERVER過來的
			
				aa=read.toString();
		        broadCast(aa);//廣播給client
	    	}
	    	
	    	
	    	if(aa.endsWith("遊戲結束"))
	    	{
	    		try {
					con.close();
					clientSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}  		
	    		break;
	    	}
    	  }catch(IOException e) 
    	    {
    	      //System.out.println(e.toString());
    	    }
      }
  }
    
  public void broadCast(String message)//廣播功能，將Client毒入的資料傳送給所有Client端
  {
    Iterator<DataOutputStream> it = clientOutputStreams.iterator();
    while( it.hasNext() )
    {
      try
      {
        DataOutputStream writer = it.next();//宣告一個將資料寫出的物件
        writer.writeUTF(message);//將資料寫出
        writer.flush();//清空資料串流。
        if(message=="遊戲結束")
        {
        	it.remove();
        }
      }
      catch (Exception e) 
      {  
        //System.out.println(e.toString());
      }
    }
    }
}
