import com.sun.jna.*;
import java.util.Scanner;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

class Main {
	static double[] NewPPG = new double[2048]; // smoothing後的訊號 //全域變數 整個class都可以用
	public static int[] SecPeak=new int[100]; //每秒的波峰數
	static int HR=0; //heart rate
	
	public interface MyLibrary extends Library{
  @SuppressWarnings("deprecation")
	MyLibrary instance = (MyLibrary)Native.loadLibrary("FFT", MyLibrary.class); 
	void myprint(String sometext);
  void Init_FFT(int N_of_FFT); //初始化FFT,N_of_FFT是資料筆數,須為2的次方數
  void Close_FFT();  //釋放FFT計算資源 
  void IFFT(); //Inverse FFT反向運算函式
  void FFT(); //FFT運算函式
  void CREATE_SIN_TABLE(); //建立正弦函數表
  void InputData(double data[], int count); //產生輸入資料
  double RESULT(int x); //FFT的複數(實部+虛部)計算
	}
	
	public static void main(String[] args) throws IOException 
 {
  {
    double[] num = new double[2048]; // 陣列宣告
    int index = 0,num_of_data=0,i;
    double[] amp = new double[2048]; //FFT的Y軸: amp
    double[] freq = new double[2048]; //FFT的X軸: freq
    int sampleRate=100; //取樣率(Hz) 
    int numPeak=0;
    int bpm=0;
    int Fstart=0,Fend=0;
    //double MaxAmp=0,MaxFreq=0;
    FileReader PPGfile = new FileReader("20210325.txt");
    Scanner inf = new Scanner(PPGfile);
    FileWriter Outfile0 = new FileWriter("smooth.txt");
    FileWriter Outfile = new FileWriter("fft.txt");
    HRTime wave=new HRTime(); 

    while (inf.hasNext()){ //讀檔,將資料放到num[]陣列,筆數紀錄到index
    	num[index] = inf.nextDouble();
        // System.out.println(num[index]);
        index++;
      }
    
    /*時域分析開始*/
    
    // Step 1.呼叫平均函數
    wave.triangular(13, num, index);
    // 寫檔
    for (i = 0; i < index; i++) {
      Outfile0.write(Double.toString(NewPPG[i]));
      Outfile0.write("\n");
      Outfile0.flush();
    }

    //Step 2. 尋找波峯
    /*取樣時間間隔 x ms = intV
     *心跳感測器1000/20 intV=20
     *PPG小跟大雜訊1000/10 intV=10
    */
    numPeak=wave.findPeak(index,NewPPG,550,20); //PPG>=36650 才是peak 根據訊號觀察得知
    System.out.println("中訊號：");
    System.out.println("No. of peaks="+numPeak);

    //Step 3. 計算平均HR(BPM)
    //System.out.println((double)25/(((double)index/100)/60));
    //numPeak=25;
    bpm=(int)((double)numPeak/(((double)index*20/1000)/60));
    System.out.println("Average:"+bpm+" BPM");
    
    /*頻域分析開始*/
    //尋找大於資料筆數index的2次方數值,記錄到num_of_data
    num_of_data=1;
    while(num_of_data<index){ 
        num_of_data*=2;
    }
    //num_of_data多出index的個數,其數值為0
    while(index<num_of_data){
    	NewPPG[index]=0;
        index++; 
    }
    //初始化FFT參數,FFT的資料比數須為2的次方
    MyLibrary.instance.Init_FFT(num_of_data);	
    //輸入原始數據
    MyLibrary.instance.InputData(NewPPG, num_of_data); 
    //進行FFT運算
    MyLibrary.instance.FFT();
    for(i=0;i<num_of_data/2;i++){
      //轉換FFT的複數為實數,放到Y軸的amp
      amp[i]=MyLibrary.instance.RESULT(i)/(double)num_of_data;
      //計算amp對應的頻率,放到freq(x軸)
      freq[i]=(double)sampleRate/(double)num_of_data*(double)i;
      //寫檔,每行格式為:freq,amp
      Outfile.write(Double.toString(freq[i]));
      Outfile.write(",");
      Outfile.write(Double.toString(amp[i]));
      Outfile.write("\n");
      Outfile.flush();
    }

    //頻率心率 1.找出頻率0.7Hz所在的資料點
    for(i=0;i<num_of_data/2;i++){
      Fstart=i;
      if(freq[i]>=0.7) break;
    }
    //2.找出中間的資料點
    Fend=num_of_data/2;
    //3.從step1的開始點 到 step2的結束點 找最大值
    double MaxAmp=amp[0],MaxFreq=freq[0];
    for(i=Fstart;i<=Fend;i++){
      if(freq[i]>MaxFreq){ 
        MaxFreq=freq[i];
      }
      if(amp[i]>MaxAmp){
        MaxAmp=amp[i];
      }
    }
    //4.把最大值所對應的頻率乘以60得到心率BPM
    System.out.println("FFT:"+(int)MaxFreq*60+" BPM");//記得轉整數

	MyLibrary.instance.myprint("FFT is successful!\n");
    MyLibrary.instance.Close_FFT();	//釋放FFT資源
	PPGfile.close(); //釋放讀檔資源
    Outfile.close(); //釋放寫檔資源 
    Outfile0.close(); //釋放寫檔資源 

	} //main
 } //IOException
}
