package com.cxwudi.niconico_video_downloader.side_project.niconico_video_extractor;


import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.cxwudi.niconico_video_downloader.side_project.niconico_video_extractor.AbstractAudioTask.IsNotFileException;

import ws.schild.jave.AudioAttributes;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.EncoderProgressListener;
import ws.schild.jave.EncodingAttributes;
import ws.schild.jave.InputFormatException;
import ws.schild.jave.MultimediaInfo;
import ws.schild.jave.MultimediaObject;

public class AudioExtractor {

	private static final int MAX_T = 16;

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
		Queue<AbstractAudioTask> ffmpegTasks = getFFmpegTasks();
		
		extractAudios(ffmpegTasks);
	}

	/**
	 * @param ffmpegTasks
	 * @return
	 * @throws IsNotFileException
	 */
	private Queue<AbstractAudioTask> getFFmpegTasks() throws IOException {
		Queue<AbstractAudioTask> ffmpegTasks = new LinkedList<>();
	
		// for each folder in folders
		for (File subDir : folders) {
			var inputFolder = new File(inputRoot, subDir.getName());
			var outputFolder = new File(outputRoot, subDir.getName());
	
			if (inputFolder.isDirectory()) {
				String[] songNameArray = inputFolder.list();
				
				// for each songs in the current folder
				for (String inputSongName : songNameArray) {
					var inputFile = new File(inputFolder, inputSongName);
					// remove the .mp4 or .flv extension, and add .aac
					var outputSongName = inputSongName.substring(0, inputSongName.length() - 4) + ".aac";
					var outputFile = new File(outputFolder, outputSongName);
	
					ffmpegTasks.offer(new ExtractTask(inputFile, outputFile));
				}
			} else {
				System.err.println("Invalid directory, Miku and CXwudi are very angry, skip!! " + inputFolder.toString());
			}
	
		}
	
		System.out.println("we have these following ffmpeg tasks:");
		for (var abstractAudioTask : ffmpegTasks) {
			System.out.println(abstractAudioTask);
		}
		System.out.println("in total " + ffmpegTasks.size() + " of them");
	
		return ffmpegTasks;
	}

	/**
	 * @param ffmpegTasks
	 */
	//TODO: change this method to return a list of output Files 
	private void extractAudios(Queue<AbstractAudioTask> ffmpegTasks) {
		// ffmpeg attributes
		var audio = new AudioAttributes();
		audio.setCodec(AudioAttributes.DIRECT_STREAM_COPY);
	
		var attributes = new EncodingAttributes();
		attributes.setAudioAttributes(audio);
		attributes.setVideoAttributes(null);

		// perpare for multithread task;
		ExecutorService executor = Executors.newFixedThreadPool(MAX_T);
	
		while (!ffmpegTasks.isEmpty()) {
			AbstractAudioTask task = ffmpegTasks.remove();
			
			executor.execute(() -> {
				try {
					var ffmpegEncoder = new Encoder(); //to multi-threaded, creates instance for each thread
					System.out.println("start processing " + task.getInputFile().toString());
					ffmpegEncoder.encode(
							new MultimediaObject(
									task.getInputFile()),
							task.getOutputFile(), attributes, new EncoderProgressListener() {
	
						@Override
						public void sourceInfo(MultimediaInfo info) {
							System.out.println("format: " + info.toString());
						}
	
						@Override
						public void progress(int permil) {
							System.out.println("current progress: " + (permil / 10.0) + "%");
						}
	
						@Override
						public void message(String message) {
							System.err.println("message: ");
							System.err.println(message);
	
						}
					});
					
				} catch (IllegalArgumentException e) {
					System.err.println("IllegalArgumentException: " + e);
				} catch (InputFormatException e) {
					System.err.println("Cannot recognize this format: " + e);
					e.printStackTrace();
				} catch (EncoderException e) {
					System.err.println("Cannot encode: " + e);
				}
				
			});
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
