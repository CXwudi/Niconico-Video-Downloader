package com.cxwudi.niconico_videodownloader.solve_tasks;

import com.cxwudi.niconico_videodownloader.entity.Vsong;
import com.cxwudi.niconico_videodownloader.setup.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.util.*;
/**
 * @author CX无敌
 *
 */
public class LocalRecorder {
	private File listDownloadedTxt;
	private Set<Vsong> update;		//currently use TreeSet
	
	public LocalRecorder(Set<Vsong> update2) {
		this.update = update2;
		listDownloadedTxt = Config.getDownloadedList();
		try {
			if (!listDownloadedTxt.exists()) {
				if (!listDownloadedTxt.createNewFile()) {
					logger.warn("unable to create new downloader.txt");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @param songs Vocaloid Songs that have been downloaded
	 * @return @{@code true} if {@code treeSet.addAll()} return true;
	 */
	public boolean markDone(Vsong... songs) {
		return update.addAll(Arrays.asList(songs));
	}
	
	public void writeRecord() {
		var inverseOrderSet = new TreeSet<Vsong>(Comparator.reverseOrder());
		inverseOrderSet.addAll(update);
		//LearnJava: whenever we open a txt file to read or write, we first open the FileIO, then BufferIO
		//if writing txt, we add one more warp, PrintWriter
		try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(listDownloadedTxt)), true)){
			for (Iterator<Vsong> iterator = inverseOrderSet.iterator(); iterator.hasNext();) {
				Vsong vsong = iterator.next();
				StringBuilder sb = new StringBuilder();
				sb.append(vsong.getId()).append("------").append(vsong.getTitle());
				writer.println(sb.toString());
			}
		} catch (IOException e) {
			logger.error("this shouldn't happen", e);
		} 
	}
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

}
