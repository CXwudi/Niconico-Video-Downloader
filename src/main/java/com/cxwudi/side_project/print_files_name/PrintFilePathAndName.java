package com.cxwudi.side_project.print_files_name;
import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PrintFilePathAndName {

	public static void main(String[] args) {
		String path = "C:\\Users\\11134\\Videos\\2019年V家新曲合集\\2010年V家可能好听新曲";   
        getFile(path);   
	}
	
	public static void getFile(String path) {
		File file = new File(path); 
		// get the folder list
		File[] array = file.listFiles(); 
		//sort by modify date, increasing order
		array = Arrays.stream(array).collect(Collectors.toMap(Function.identity(), File::lastModified))
				.entrySet().stream()
				.sorted((t1, t2) -> t1.getValue() - t2.getValue() < 0 ? -1 : 1)
				.map(Entry::getKey)
				.collect(Collectors.toList()).toArray(new File[0]);
		
		
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
