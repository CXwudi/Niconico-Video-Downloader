package com.cxwudi.niconico_video_downloader.v2;
import java.io.File;

public class PrintFilePathAndName {

	public static void main(String[] args) {
		String path = "D:\\11134\\Videos\\2018年V家新曲";   
        getFile(path);   
	}
	
	public static void getFile(String path) {
		File file = new File(path); 
		// get the folder list
		File[] array = file.listFiles(); 
		
		for(int i=0;i<array.length;i++){
			if(array[i].isFile()){ 
				// only take file name
				System.out.println(array[i].getName());
				// take file path and name
				//System.out.println("#####" + array[i]);
				// take file path and name
				//System.out.println("*****" + array[i].getPath()); 
			}
		}
//		--------------------- 
//		作者：damonZengg 
//		来源：CSDN 
//		原文：https://blog.csdn.net/tomorrowzm/article/details/3693653 
//		版权声明：本文为博主原创文章，转载请附上博文链接！
	}

}
