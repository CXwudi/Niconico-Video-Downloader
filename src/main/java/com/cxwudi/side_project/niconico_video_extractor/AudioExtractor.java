package com.cxwudi.side_project.niconico_video_extractor;


import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.cxwudi.side_project.niconico_video_extractor.ExtractTaskThread.MiddleFileMismatchException;

import ws.schild.jave.AudioAttributes;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.EncoderProgressListener;
import ws.schild.jave.EncodingAttributes;
import ws.schild.jave.InputFormatException;
import ws.schild.jave.MultimediaInfo;
import ws.schild.jave.MultimediaObject;

public class AudioExtractor {

	private static final int MAX_T = 16, MAX_HOLD = 1024;

	private File inputRoot;
	private File outputRoot;
	private File[] folders;

	/**
	 * @param inputRoot
	 * @param outputRoot
	 * @param folders
	 */
	public AudioExtractor(File inputRoot, File outputRoot, File[] folders) {
		this.inputRoot = inputRoot;
		this.outputRoot = outputRoot;
		this.folders = folders;
	}

	public void dojob() throws Exception {
		// get all input song files
		Queue<ExtractTaskThread> ffmpegTasks = getTasks();
		
		extractAudios(ffmpegTasks);
	}

	
	/**
	 * @param ffmpegTasks
	 * @return
	 * @throws MiddleFileMismatchException 
	 * @throws IsNotFileException
	 */
	private Queue<ExtractTaskThread> getTasks() throws IOException, MiddleFileMismatchException {
		Queue<ExtractTaskThread> taskThreads = new LinkedList<>();
	
		// for each folder in folders
		for (File subDir : folders) {
			var inputFolder = new File(inputRoot, subDir.getName());
			var outputFolder = new File(outputRoot, subDir.getName());
	
			if (inputFolder.isDirectory()) {
				String[] songNameArray = inputFolder.list();
				
				// for each songs in the current folder
				for (String inputSongFileName : songNameArray) {
					var inputFile = new File(inputFolder, inputSongFileName);
					// remove the .mp4 or .flv extension, and add .aac
					String outputAACFileName = inputSongFileName.substring(0, inputSongFileName.length() - 4) + ".aac";
					var outputAACFile = new File(outputFolder, outputAACFileName);
					//create first i/o file apir
					var ffmpegIOFilePair = new IOFilePair(inputFile, outputAACFile);
					
					// remove the .aac, and add .m4a
					String outputM4AFileName = inputSongFileName.substring(0, inputSongFileName.length() - 4) + ".m4a";
					var outputM4AFile = new File(outputFolder, outputM4AFileName);
					//create second i/o file apir
					var mp4praserIOFilePair = new IOFilePair(outputAACFile, outputM4AFile);
					
					taskThreads.offer(new ExtractTaskThread(ffmpegIOFilePair, mp4praserIOFilePair));
				}
			} else {
				System.err.println("Invalid directory, Miku and CXwudi are very angry, skip!! " + inputFolder.toString());
			}
	
		}
	
		System.out.println("we have these following ffmpeg tasks:");
		for (var abstractAudioTask : taskThreads) {
			System.out.println(abstractAudioTask);
		}
		System.out.println("in total " + taskThreads.size() + " of them");
	
		return taskThreads;
	}

	/**
	 * @param taskThreads
	 */
	//TODO: create a new entity obj that extends thread class and handle all 2 task
	private void extractAudios(Queue<ExtractTaskThread> taskThreads) {
		// ffmpeg attributes
		var audio = new AudioAttributes();
		audio.setCodec(AudioAttributes.DIRECT_STREAM_COPY);
	
		var attributes = new EncodingAttributes();
		attributes.setAudioAttributes(audio);
		attributes.setVideoAttributes(null);

		// perpare for multithread task;
		ThreadPoolExecutor executor = new ThreadPoolExecutor(MAX_T
				, MAX_HOLD, 0L, TimeUnit.MILLISECONDS
				, new LinkedBlockingDeque<>());
		//Executors.newFixedThreadPool(MAX_T);
		while (!taskThreads.isEmpty()) {
			executor.execute(taskThreads.remove());
		}
		executor.shutdown();
	}

	public static void main(String[] args) throws Exception {
		File inputRoot = new File(System.getProperty("user.home") + "\\Videos");
		File outputRoot = new File(inputRoot, "2019年V家新曲合集");
		File[] folders = new File[] { 
				new File(inputRoot, "2019年V家新曲"), 
				new File(inputRoot, "2019年V家可能好听") 
				};

		var extractAudioProcess = new AudioExtractor(inputRoot, outputRoot, folders);
		extractAudioProcess.dojob();

	}

}
