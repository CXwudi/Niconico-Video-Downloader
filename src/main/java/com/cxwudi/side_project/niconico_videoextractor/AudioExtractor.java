package com.cxwudi.side_project.niconico_videoextractor;


import com.cxwudi.side_project.niconico_videoextractor.ExtractTaskThread.MiddleFileMismatchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.schild.jave.AudioAttributes;
import ws.schild.jave.EncodingAttributes;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
/**
 * A small script to extract audio tracks from multiple PV video files.
 * It support mp4 and flv format, indeed, it should support all video with aac audio track.
 * This project, first collect all input files, 
 * for each input files, it use ffmpeg to extract the raw aac-lc format audio track, then use mp4praser to wrap it to m4a file.
 * This Project use multi-threads, to speed up the whole process. By default, it opens 16 threads at the time.
 * <br></br>
 * How to use:<br></br>
 * {@code new AudioExtractor(inputRoot, outputRoot, folders).doJob();} <br></br>
 * where {@code inputRoot} is the root folder contains all videos in these following {@code folders}
 * and {@code outputRoot} is the output root folder that contains 
 * 
 * @author CX无敌
 *
 */
public class AudioExtractor {

	private static int MAX_T = 16;
	/**
	 * simply telling the thread pool executor how many thread can be waiting to be executed
	 */
	private static final int MAX_HOLD = 1024;

	private File outputRoot;
	private File[] inputFolders;

	/**
	 * @param outputRoot
	 * @param inputFolders
	 */
	public AudioExtractor(File[] inputFolders, File outputRoot) {
		this.outputRoot = outputRoot;
		this.inputFolders = inputFolders;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	/**
	 * like what it said, it simply just do job LOL
	 * @throws Exception
	 */
	public void dojob() throws Exception {
		// get all input song files
		List<ExtractTaskThread> ffmpegTasks = getTasks();
		//sort the List
		ffmpegTasks = sortByModifyDate(ffmpegTasks); //since multithread, it can't follow exact order but roughly sorted
		//process it
		extractAudios(ffmpegTasks);
	}

	@SuppressWarnings("untested") //TODO: once tested, remove compareTo method in other classes
	private List<ExtractTaskThread> sortByModifyDate(List<ExtractTaskThread> ffmpegTasks) {
		return ffmpegTasks.stream() //trasfer to map which task->modify date
				.collect(Collectors.toMap(Function.identity(),ExtractTaskThread::getInputFileLastModify))
				.entrySet() 
				.stream()
				.sorted((t1, t2) -> t1.getValue() - t2.getValue() < 0 ? -1 : 1) //sort by modify date stored
				.map(Entry::getKey) // convert back to list
				.collect(Collectors.toList());
	}
	/**
	 * @return
	 * @throws MiddleFileMismatchException 
	 */
	private List<ExtractTaskThread> getTasks() throws IOException, MiddleFileMismatchException {
		List<ExtractTaskThread> taskThreads = new LinkedList<>();
		
		var audio = new AudioAttributes();
		audio.setCodec(AudioAttributes.DIRECT_STREAM_COPY);
		
		// for each folder in folders
		for (File inputFolder : inputFolders) {
			var outputFolder = new File(outputRoot, inputFolder.getName());
	
			if (inputFolder.isDirectory()) {
				String[] songNameArray = inputFolder.list();
				
				// for each songs in the current folder
				for (String inputSongFileName : songNameArray) {
					var inputFile = new File(inputFolder, inputSongFileName);
					
					// remove the .mp4 or .flv extension, and add .aac
					var songName = inputSongFileName.substring(0, inputSongFileName.length() - 4);
					String outputAACFileName = songName + ".aac";
					var outputAACFile = new File(outputFolder, outputAACFileName);
					
					//create first i/o file apir
					var ffmpegIOFilePair = new IOFilePair(inputFile, outputAACFile);
					
					// remove the .aac, and add .m4a
					String outputM4AFileName = songName + ".m4a";
					var outputM4AFile = new File(outputFolder, outputM4AFileName);
					//create second i/o file apir
					var mp4praserIOFilePair = new IOFilePair(outputAACFile, outputM4AFile);
					
					//create task
					var attributes = new EncodingAttributes();
					attributes.setAudioAttributes(audio);
					attributes.setVideoAttributes(null);
					var taskThread = new ExtractTaskThread(ffmpegIOFilePair, mp4praserIOFilePair);
					taskThread.setEncodingAttributes(attributes);
					
					taskThreads.add(taskThread);
				}
			} else {
				logger.warn("Invalid directory, Miku and CXwudi are very angry, skip!! " + inputFolder.toString());
			}
	
		}
	
		logger.info("we have these following ffmpeg tasks:");
		for (var abstractAudioTask : taskThreads) {
			logger.info("{}", abstractAudioTask);
		}
		logger.info("in total " + taskThreads.size() + " of them");
	
		return taskThreads;
	}

	/**
	 * @param taskThreads
	 */
	private void extractAudios(List<ExtractTaskThread> taskThreads) {
		//prepare for multi-thread task
		ThreadPoolExecutor executor = new ThreadPoolExecutor(MAX_T
				, MAX_HOLD, 0L, TimeUnit.MILLISECONDS
				, new LinkedBlockingDeque<>(MAX_HOLD));
		while (!taskThreads.isEmpty()) {
			executor.execute(taskThreads.remove(0));
		}
		executor.shutdown();
	}

	public static void main(String[] args) throws Exception {
		Main.main(args);
	}

}
