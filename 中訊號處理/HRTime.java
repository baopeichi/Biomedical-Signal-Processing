
public class HRTime {
	public static int findPeak(int index,double[] NewPPG,double threshold,int intV){//intV取樣率,一秒多少
	    int peak=0; //peak的總數量
	    int sec=0;
	    int num=0;
	    int sumPeak=0;
	    int i;
	    /*
	       0,1,2,....index-3,index-2,index-1 
	       判斷波峰從陣列的第1筆開始
	       因為第1筆可以開始觀察到前後資料(第0筆跟第2筆)
	       同理,做到陣列的倒數第2筆,因為此筆可以看到前後筆
	       所以,迴圈的數列是:1,2,...,index-2
	    */
	    for(i=1;i<=index-2;i++){
	     num++;
	     if(NewPPG[i]>NewPPG[i-1])
	       if(NewPPG[i]>NewPPG[i+1])
	        if(NewPPG[i]>threshold) {
	          peak++;
	          sumPeak++;
	        }
	     if(num==1000/intV) {
	    	 Main.SecPeak[sec]=sumPeak;
	    	 sec++;num=0;sumPeak=0;
	     }
	     //HR(); System.out.println(HR+" BPM"); //即時心率
	    } 
	    return peak;
	  }
	public int rectangular(int m, int[] data,int size) //類別不用static
	  {
	     int i,j;
	     int sum=0;
	     
	     if(m%2==0)
	    	 return -1;  //無法處理偶數點平均,回傳error code: -1

	     for (i=0;i<size;i++) // 所有的資料筆數
	      {
	    	//System.out.println(data[i]);
	    	sum = 0;
	        // i-1,i,i+1
	        for (j=i-1;j<=i+1;j++) // 任一筆資料i算3點平均
	        {
	          if(j<0) 
	            sum=sum+data[0];
	          else if(j>size-1) 
	            sum=sum+data[size-1];
	          else 
	            sum= sum+data[j];
	        }
	        Main.NewPPG[i] = (double)sum / m;
	        System.out.println("sum="+sum+",NewPPG="+Main.NewPPG[i]);
	      }
	    return 0;
	  }
	public int savitzky(int m, int[] num, int size) {
		int[] P5 = { -3, 12, 17, 12, -3 }; //5點的權重
		int[] P7={-2,3,6,7,6,3,-2}; //7點的權重
		int[] P9={-21,14,39,54,59,54,39,14,-21}; //9點的權重 
		int totalP5=35; //5點的權重總和
		int totalP7=21; //7點的權重總和
		int totalP9=231; //9點的權重總和
		int i,j;
		int sum = 0, index = 0; //sum:總和, index:m點平均的某一點0~m-1

		if (m % 2 == 0)
			return -1; // 無法處理偶數點平均,回傳error code: -1
		if (m != 5 && m != 7 && m != 9)
			return -2; // 只能處理5,7,9點的平均

		switch (m) {
		case 5:
			for (i = 0; i < size; i++){ //所有的資料筆數   
				index = 0;
				sum = 0;
			// i-2,i-1,i,i+1,i+2
				for (j = i - 2; j <= i + 2; j++){ //任一筆資料i算5點平均
					//如果取到第一筆資料之前,複製第一筆資料data[0]進行平均
					if(j<0) 
						sum+=P5[index] *num[0];
					//如果取到最後一筆資料之後,複製最後一筆資料data[size-1]進行平均
					else if(j>size-1) 
						sum+=P5[index]*num[size-1];
					else 
						sum += P5[index] * num[j];
					index++; //指向m點的下一點的權重
				}
			Main.NewPPG[i] = (double)sum / (double)totalP5;
			//System.out.println("sum="+sum+",NewPPG="+NewPPG[i]);
			}
			break;
		case 7:
			for (i = 0; i < size; i++){ // 所有的資料筆數
				index = 0;
				sum = 0;
				// i-3,i-2,i-1,i,i+1,i+2,i+3
				for (j = i - 3; j <= i + 3; j++){ // 任一筆資料i算7點平均
					//如果取到第一筆資料之前,複製第一筆資料data[0]進行平均
					if(j<0) 
						sum+=P7[index] *num[0];
					//如果取到最後一筆資料之後,複製最後一筆資料data[size-1]進行平均
					else if(j>size-1) 
						sum+=P7[index]*num[size-1];
					else 
						sum += P7[index] * num[j];
					index++; //指向m點的下一點的權重
				}
				Main.NewPPG[i] = (double)sum / (double)totalP7;
				//System.out.println("sum="+sum+",NewPPG="+NewPPG[i]);
			}
			break;
		case 9:
			for (i = 0; i < size; i++){ // 所有的資料筆數
				index = 0;
				sum = 0;
				// i-4,i-3,i-2,i-1,i,i+1,i+2,i+3,i+4
				for (j = i - 4; j <= i + 4; j++){ // 任一筆資料i算9點平均
					//如果取到第一筆資料之前,複製第一筆資料data[0]進行平均
					if(j<0) 
						sum+=P9[index] *num[0];
					//如果取到最後一筆資料之後,複製最後一筆資料data[size-1]進行平均
					else if(j>size-1) 
						sum+=P9[index]*num[size-1];
					else 
						sum += P9[index] * num[j];
					index++; //指向m點的下一點的權重
				}
				Main.NewPPG[i] = (double)sum / (double)totalP9;
				//System.out.println("sum="+sum+",NewPPG="+NewPPG[i]);
			}
			break;
		default:
			break;
		}
		return 0;
	}
	public int triangular(int m, double[] data, int size){
		int i,j,temp=0;
	    int w=1; //weight權重 
	    int sum=0,wsum=0; //sum: 平均的分子總和 wsum:權重總和
	    int flag=0; //0:w遞增 1:w遞減

	    if (m % 2 == 0)
	      return -1; // 無法處理偶數點平均,回傳error code: -1
	    
	    for(i=0;i<size;i++){
	      sum=0;w=1;flag=0;temp=1;wsum=0;
	      // i-(m-1)/2,..,i-1,i,i+1,i+2,..,i+(m-1)/2
	      for(j=i-(m-1)/2;j<=i+(m-1)/2;j++){
	        System.out.print(w+","); 
	        wsum+=w; //wsum=wsum+w
	          
	        if(j<0) {
	          sum+=w*data[0]; //sum=sum+(w*data[0])
	        //如果取到最後一筆資料之後,複製最後一筆資料data[size-1]進行平均
	          //System.out.println("j<0="+sum);
	        }
	        else if(j>size-1) {
	          sum+=w*data[size-1];
	          //System.out.println("j>size-1="+sum);
	        }
	        else { 
	          sum+=w*data[j];
	        }

	        if((temp)>=((m-1)/2)+1) //m點的權重最大值是(m-1)/2+1
	            flag=1;
	        else
	            flag=0;
	        if(flag==0)
	            w++;
	        else
	            w--;
	          temp++;   
	      }
	      System.out.print("wsum="+wsum);
	      System.out.println("\n");
	      Main.NewPPG[i]=(double)sum/(double)wsum;
	    }
	    return 0;
	}
}
