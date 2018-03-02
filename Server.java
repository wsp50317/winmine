

import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.Scanner;

public class Server extends Thread {

	
	
private Socket clientSocket;


public Server(Socket clientSocket) {
this.clientSocket = clientSocket;
}

////////////////////////////////////////////
static int[][] 		MapN=new int[12][12];		//�Ʀr�}�C
static String[][]	MapS=new String[12][12];	//�r��}�C
static Random r=new Random();				//�w�]�C���I��1/4���v���ͦa�p
static int R;						//���쪺�H���ܼ�
static int CountB=0;					//�p��w�g���ʹX���a�p
static int Check=2;					//�ӭt�P�_
public static StringBuilder Str=new StringBuilder();

public static void MakeMap(){				//�s��
	Check=2;//�ӭt�P�_�k�s
	CountB=0;//�a�p�k�s
	for(int i=0;i<12;i++){				//�}�C�k0
		for(int j=0;j<12;j++){
			MapN[i][j]=0;
			MapS[i][j]="?";
		}
	}
	
	while(CountB!=30){				//��s�@��30���a�p���X�j��
	for(int i=1;i<=10;i++){
		for(int j=1;j<=10;j++){
			R=r.nextInt(4);
			if(MapN[i][j]==9){		//�Y���I�w���a�p�h���L
				continue;
			}
			else
			if(R==0){			//���ͦa�p
				MapN[i][j]=9;
				CountB++;
				if(CountB==30){		//�a�p��������
					break;
				}
			}
		}
		if(CountB==30){				//�a�p��������		
			break;
		}
	}
	}
	//CallMap();	//��e�w���a�p�Ϥ������p
	
	

}
public static void OpenMap(){		//�}�ϼҦ�
	for(int i=1;i<=10;i++){
		for(int j=1;j<=10;j++){
			if(MapN[i][j]==9)
			{
				System.out.print('*'+" ");
			}
			else
			{
				System.out.print(MapN[i][j]+" ");
			}
		}
		System.out.println();
	}
}

public static void CallMap(){		//�ثe�w���a�p�������p
	for(int i=1;i<=10;i++){
		for(int j=1;j<=10;j++){
			System.out.print(MapS[i][j]+" ");
			Str.append(MapS[i][j]+" ");
		}
		System.out.println();
		Str.append('\n');
	}
	System.out.println();
	Str.append('\n');
}

public static void Sweep(int a,int b){		//��a�p
	Str=new StringBuilder();//�NSTR�k�s
	//System.out.print("�п�J��ӼƦr[�C ��]:");
	Str.append("�п�J��ӼƦr[�C ��]:");
	int row,col;
	int NearB;		
	//Scanner in=new Scanner(System.in);///�o����SERVER��num[0] num[1]
	row=a;
	col=b;
	NearB=0;
	if(row>10 || col>10 || row==0 || col==0 ){	//�w����J���~
		System.out.println("��J���ŦX�d��!!\n");
		Str.append("��J���ŦX�d��!!\n");
		System.out.print("�п�J��ӼƦr[�C ��]:");
		Str.append("�п�J��ӼƦr[�C ��]:");
		row=a;
		col=b;
	}
	

	if(MapN[row][col]==9){			//�򤤦a�p�P�w
		Check=0;
	}
	else{
		for(int i=(row-1);i<row+2;i++){	//�ˬd����a�p�ƶq
			for(int j=(col-1);j<col+2;j++){
				if(i==row && j==col){
					continue;
				}
				else{
					if(MapN[i][j]==9){
						NearB++;
					}
				}
			}
		}						
		MapS[row][col]= Integer.toString(NearB);
	}
	for(int i=0;i<13;i++){  ///��13��
		System.out.println();
		Str.append('\n');
	}
}

public static void AnswerMap(){  ///�̫ᵪ��
	for(int i=1;i<=10;i++){
		for(int j=1;j<=10;j++){
			if(MapN[i][j]==9){
				MapS[i][j]="*";
			}
		}			
	}
	CallMap();
}
//////////////////////////////////////////////////////



public void run() {
	
DataInputStream dis = null;
DataOutputStream dos = null;
int times=0;

//try{
	

try {
	
	dis = new DataInputStream(clientSocket.getInputStream());
	dos = new DataOutputStream(clientSocket.getOutputStream());
	
	MakeMap();
	//CallMap();

while (true) {
	String temp = dis.readUTF();
	
	///////////�a�p����/////////	
	if(temp.startsWith("start"))///��l��
	{	
		System.out.println("�п�J��ӼƦr[�C ��]:");
		Str.append("�п�J��ӼƦr[�C ��]:");
		for(int i=0;i<13;i++){  ///��13��
			System.out.println("");
			Str.append('\n');
		}
		System.out.println("���T�ѵ�:");
		OpenMap();
		System.out.println("�ثe��p:");
		CallMap();
		dos.writeUTF(Str.toString());
		continue;
	}
	
	String strs[] = temp.split(" "); //�N��Ƥ��}
	int nums[] = new int[strs.length]; 
	for(int i=0;i<strs.length;++i)
	{ 
		nums[i] = Integer.valueOf(strs[i]); 
	}

		
	//for(int i=0;i<70;i++){		//�w�p�̦h�u��70������(100���I-30�Ӧa�p�I)
	times++;
		Sweep(nums[0], nums[1]);					
		if(Check==0){		//���ѧP�w
			AnswerMap();				
			System.out.println("BOOOOOOOOOOOM!!!!! YOU LOSE........\n");
			
			//Str=new StringBuilder();
			Str.append("BOOOOOOOOOOOM!!!!! YOU LOSE........\n");
			Str.append("�C������");
			dos.writeUTF(Str.toString());
			dos.flush();
			MakeMap();//�N�ϭ��m
			Str=new StringBuilder();
			times=0;
			//break;
			Check=2;
			
		}
		else if(Check==2)
		{
			System.out.println("���T�ѵ�:");
			OpenMap();
			System.out.println("�ثe��p:");
			CallMap();
			dos.writeUTF(Str.toString());
		}
	//}
	if(times==69)
	{
		Check=1;
	}
	if(Check==1){			//���\�P�w
		AnswerMap();
		System.out.println("�F���n!!\n");
		
		//Str=new StringBuilder();
		Str.append("�F���n!!\n");
		Str.append("�C������");
		dos.writeUTF(Str.toString());
		dos.flush();
		times=0;
		MakeMap();//�N�ϭ��m
		//break;
		Check=2;
	}	
	
	///////////�a�pEND/////////

	
	//int aaa=nums[0]+nums[1];//�ۥ[
	if ("over".equals(temp)) {
		break;
	}
	



}//WHILE END

} catch (Exception e) {
//e.printStackTrace();
} finally {
try {
if (dis != null) {
dis.close();
}
if (dis != null) {
dos.close();
}

if (clientSocket != null) {
clientSocket.close();
}
} catch (IOException e) {
e.printStackTrace();
} finally {
System.out.println("Server Thread is shutdown.");
}
}
}

public static void main(String[] args) throws Exception {

ServerSocket ss = new ServerSocket(5507);
System.out.println("Wating for connection(Server port=5507)");
while (true) {
Socket clientSocket = ss.accept();

Server server = new Server(clientSocket);
server.start();
}

}

}

