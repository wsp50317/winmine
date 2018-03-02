

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
static int[][] 		MapN=new int[12][12];		//數字陣列
static String[][]	MapS=new String[12][12];	//字串陣列
static Random r=new Random();				//預設每個點有1/4機率產生地雷
static int R;						//取到的隨機變數
static int CountB=0;					//計算已經產生幾顆地雷
static int Check=2;					//勝負判斷
public static StringBuilder Str=new StringBuilder();

public static void MakeMap(){				//製圖
	Check=2;//勝負判斷歸零
	CountB=0;//地雷歸零
	for(int i=0;i<12;i++){				//陣列歸0
		for(int j=0;j<12;j++){
			MapN[i][j]=0;
			MapS[i][j]="?";
		}
	}
	
	while(CountB!=30){				//當製作滿30顆地雷跳出迴圈
	for(int i=1;i<=10;i++){
		for(int j=1;j<=10;j++){
			R=r.nextInt(4);
			if(MapN[i][j]==9){		//若此點已有地雷則跳過
				continue;
			}
			else
			if(R==0){			//產生地雷
				MapN[i][j]=9;
				CountB++;
				if(CountB==30){		//地雷足夠跳離
					break;
				}
			}
		}
		if(CountB==30){				//地雷足夠跳離		
			break;
		}
	}
	}
	//CallMap();	//當前已知地雷圖分布情況
	
	

}
public static void OpenMap(){		//開圖模式
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

public static void CallMap(){		//目前已知地雷分布情況
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

public static void Sweep(int a,int b){		//踩地雷
	Str=new StringBuilder();//將STR歸零
	//System.out.print("請輸入兩個數字[列 行]:");
	Str.append("請輸入兩個數字[列 行]:");
	int row,col;
	int NearB;		
	//Scanner in=new Scanner(System.in);///這邊改用SERVER的num[0] num[1]
	row=a;
	col=b;
	NearB=0;
	if(row>10 || col>10 || row==0 || col==0 ){	//預防輸入錯誤
		System.out.println("輸入不符合範圍!!\n");
		Str.append("輸入不符合範圍!!\n");
		System.out.print("請輸入兩個數字[列 行]:");
		Str.append("請輸入兩個數字[列 行]:");
		row=a;
		col=b;
	}
	

	if(MapN[row][col]==9){			//踩中地雷判定
		Check=0;
	}
	else{
		for(int i=(row-1);i<row+2;i++){	//檢查附近地雷數量
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
	for(int i=0;i<13;i++){  ///空13行
		System.out.println();
		Str.append('\n');
	}
}

public static void AnswerMap(){  ///最後答案
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
	
	///////////地雷部分/////////	
	if(temp.startsWith("start"))///初始化
	{	
		System.out.println("請輸入兩個數字[列 行]:");
		Str.append("請輸入兩個數字[列 行]:");
		for(int i=0;i<13;i++){  ///空13行
			System.out.println("");
			Str.append('\n');
		}
		System.out.println("正確解答:");
		OpenMap();
		System.out.println("目前實況:");
		CallMap();
		dos.writeUTF(Str.toString());
		continue;
	}
	
	String strs[] = temp.split(" "); //將兩數切開
	int nums[] = new int[strs.length]; 
	for(int i=0;i<strs.length;++i)
	{ 
		nums[i] = Integer.valueOf(strs[i]); 
	}

		
	//for(int i=0;i<70;i++){		//預計最多只踩70次結束(100個點-30個地雷點)
	times++;
		Sweep(nums[0], nums[1]);					
		if(Check==0){		//失敗判定
			AnswerMap();				
			System.out.println("BOOOOOOOOOOOM!!!!! YOU LOSE........\n");
			
			//Str=new StringBuilder();
			Str.append("BOOOOOOOOOOOM!!!!! YOU LOSE........\n");
			Str.append("遊戲結束");
			dos.writeUTF(Str.toString());
			dos.flush();
			MakeMap();//將圖重置
			Str=new StringBuilder();
			times=0;
			//break;
			Check=2;
			
		}
		else if(Check==2)
		{
			System.out.println("正確解答:");
			OpenMap();
			System.out.println("目前實況:");
			CallMap();
			dos.writeUTF(Str.toString());
		}
	//}
	if(times==69)
	{
		Check=1;
	}
	if(Check==1){			//成功判定
		AnswerMap();
		System.out.println("幹的好!!\n");
		
		//Str=new StringBuilder();
		Str.append("幹的好!!\n");
		Str.append("遊戲結束");
		dos.writeUTF(Str.toString());
		dos.flush();
		times=0;
		MakeMap();//將圖重置
		//break;
		Check=2;
	}	
	
	///////////地雷END/////////

	
	//int aaa=nums[0]+nums[1];//相加
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

