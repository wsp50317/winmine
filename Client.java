

import java.io.*;
import java.net.*;
import java.lang.*;
public class Client
{
  public static void main(String[] args)
  {
    String message, stdin , ip;// messageŪ�JServer�ݿ�X , stdin �g�X Client �ݿ�X
    int port;
    try
    {
      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
      System.out.print("�п�J�n�s��port : ");
      port = Integer.parseInt(br.readLine());//Ū�J�ϥΪ̿�J��port
      Socket con = new Socket("127.0.0.1",port);//�إ߳s�u
      if(con.isConnected())
      {
        System.out.println("Connect Success");
       // System.out.println("��J�m�W");
        //String name = br.readLine();
        //
        Thread thread = new Thread(new ClientTalk(con));//�إߦh�����(���b�o)
        
        thread.start(); //�Ұ�
        DataOutputStream dos = new DataOutputStream(con.getOutputStream());//�إ�DataOutputStream�N��Ƽg��Server
        int beginstart=1;
		while(true)
        {          
	//////////
          //System.out.print("�п�J �G ");
         if(beginstart==1)
	{
		stdin="start"; //�@�}�l����"start"��SERVER
		beginstart=0;
	}
	else
	{
		stdin = br.readLine(); //��J
	}
	dos.writeUTF(stdin); //��X
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
class ClientTalk implements Runnable //�r�J��LClient�ݿ�J����Ѥ��e
{
  DataInputStream dis;//�إ�DataInput Ū���ϥΪ̶ǰe���e
  Socket clientSocket;
  public ClientTalk(Socket clientSocket) 
  {
    this.clientSocket = clientSocket;    
  }
  public void run() 
  {
    try 
    {
      dis = new DataInputStream(clientSocket.getInputStream());//�ŧi�@�ӱNserver�ݸ�Ƽg�X���ܼ�
      while (true) 
      {        
        /////
		String read = dis.readUTF(); 
	System.out.println(read);
	if(read.endsWith("�C������"))
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