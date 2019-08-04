package com.cxwudi.niconico_video_downloader.v2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.stream.Collector;
import java.util.stream.Collectors;
/**
 * the collection reader that read a local file to get a collection of downloaded Vocaloid videos list
 * @see CollectionReader
 * @author CX无敌
 *
 */

public class LocalReader extends CollectionReader{
	private File listDownloadedTxt;

	public LocalReader() {
		super();
		listDownloadedTxt = new File(new File("data/"), "downloaded.txt");
		try {
			if (!listDownloadedTxt.exists()) {
				if (!listDownloadedTxt.createNewFile()) {
					System.err.println("unable to create new downloader.txt");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void readRecord() {
		//convert into a smarter line-by-line reader from a char-by-char reader.
		//we first open the file, then use the better buffer reader
		try (BufferedReader reader = new BufferedReader(new FileReader(listDownloadedTxt));){
			
			for (String i = reader.readLine(); i != null && !i.equals(""); i = reader.readLine()) {
				String[] detialArray = i.split("------");
				collection.add(new Vsong(detialArray[0] ,detialArray[1]));
			}
			System.out.println("local record is: " + collection);
			reader.close();
			isDone = true;
		} catch (IOException e) {
			System.err.println("this shouldn't happen");
			e.printStackTrace();
			isDone = false;
		}
		
	}


	public static void main(String[] args) throws IOException {
		testReading();
	}
	public static void testReading() {
		LocalReader localReader = new LocalReader();
		//Map<String, String> map = localRecorder.getIsDownloaded();
		System.out.println(new File(new File("."), "downloaded.txt").getAbsolutePath());
		System.out.println(System.getProperty("user.dir"));
		localReader.readRecord();
		System.out.println(localReader.getCollection());
		
	}
	
}
