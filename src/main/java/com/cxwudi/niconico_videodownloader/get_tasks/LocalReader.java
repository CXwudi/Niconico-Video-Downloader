package com.cxwudi.niconico_videodownloader.get_tasks;

import com.cxwudi.niconico_videodownloader.entity.Vsong;
import com.cxwudi.niconico_videodownloader.util.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
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
		listDownloadedTxt = Config.getDownloadedList();
		try {
			if (!listDownloadedTxt.exists()) {
				if (!listDownloadedTxt.createNewFile()) {
					logger.error("unable to create new downloader.txt");
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
			logger.info("local record is: \n{}", collection);
			isDone = true;
		} catch (IOException e) {
			logger.error("exception shouldn't happen in reading records: \n{}", e);
			isDone = false;
		}
		
	}

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
}
