

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
      //System.out.print("�п�Jport : ");///Proxy �Х�5508
      //port = Integer.parseInt(in.readLine());
      port = 5508;
      System.out.println("Wating for connection(Proxy port=5508)");
      
      ServerSocket serverSocket = new ServerSocket(port);//�}�l��ťport�s�u�ШD�C
      
      while (true) 
      {
        Socket clientSocket = serverSocket.accept();
        if(clientSocket.isConnected())
        {
          try
          {
            count ++ ;
            System.out.println("��" + count + "��Client�[�J");
            DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
            clientOutputStreams.add(dos);
            dos.writeUTF("�z�O��" + count + "��Client");
            //
            Thread thread = new Thread(new ClientTalk2(clientSocket , clientOutputStreams));//�إߤ@�Ӧh�����(���b�o)
            thread.start(); //�ҰʸӦh�����
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
  DataInputStream dis;//�ŧi�@��Ū��Client�ǰe�L�Ӫ��r�ꪫ��
  
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
      dis = new DataInputStream(clientSocket.getInputStream());//�ŧi�@�ӱNserver�ݸ�Ƽg�X���ܼ�
    }
    catch(Exception e)
    {
      //System.out.println(e.toString());
    }    
  }
  public void run() //�h������Arun
  {
	DataInputStream dis = null;
	DataOutputStream dos = null;
	
	DataOutputStream dosToSer=null;
	DataInputStream disToSer=null;

	  try{
		dis = new DataInputStream(clientSocket.getInputStream());
		dos = new DataOutputStream(clientSocket.getOutputStream());
		
        ///�إ�Proxy�PServer���s�u//////
		int port;
        port = 5507;//�w�]��5507 ���ɶ��A��
        Socket con = new Socket("127.0.0.1",port);//�إ߳s�u
        this.con = con;
        
        //if(con.isConnected())
        //{
          //System.out.println("Connect Success");
          //System.out.println("�m�W:guest");
          //String name = "guest";
          
          dosToSer = new DataOutputStream(con.getOutputStream());//�إ�DataOutputStream�N��Ƽg��Server
          disToSer = new DataInputStream(con.getInputStream());
       // }
        /////////////////
	  }catch (Exception e) 
      {  
		  //System.out.println(e.toString());
	  }
		
		
      while (true) 
      { 
    	  String aa;//�s���Ϊ�
    	  int nums[] = null;
    	  int len = 0;
    	  String strs[]=null;
    	  try{
    		
	        String temp = dis.readUTF();///�qclient�ǹL�Ӫ�
	        
	        if(!(temp.startsWith("start")))///�H�}�l�T���ɡA������
	        {
		    	strs = temp.split(" "); //�N��Ƥ��}
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
	        

	        if((!(temp.startsWith("start"))) && (nums[0]>10 || nums[1]>10 || nums[0]==0 || nums[1]==0)){//��J�����Ʀr
	    		aa="���X�z����J!!\n";
	    		broadCast(aa);
	    	}
	        else if((!(temp.startsWith("start"))) && len!=2)
	        {
	    		aa="���X�z����J!!\n";
	    		broadCast(aa);
	        }
	    	else //�A����J�η��F!!!
	    	{
		        dosToSer.writeUTF(temp);///�ǵ�Server
		        
		        String read = disToSer.readUTF();///�qSERVER�L�Ӫ�
			
				aa=read.toString();
		        broadCast(aa);//�s����client
	    	}
	    	
	    	
	    	if(aa.endsWith("�C������"))
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
    
  public void broadCast(String message)//�s���\��A�NClient�r�J����ƶǰe���Ҧ�Client��
  {
    Iterator<DataOutputStream> it = clientOutputStreams.iterator();
    while( it.hasNext() )
    {
      try
      {
        DataOutputStream writer = it.next();//�ŧi�@�ӱN��Ƽg�X������
        writer.writeUTF(message);//�N��Ƽg�X
        writer.flush();//�M�Ÿ�Ʀ�y�C
        if(message=="�C������")
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
