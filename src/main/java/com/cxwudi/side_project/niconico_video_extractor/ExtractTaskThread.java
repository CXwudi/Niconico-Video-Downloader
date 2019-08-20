package com.cxwudi.side_project.niconico_video_extractor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.nio.channels.FileChannel;
import java.util.Objects;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.FileDataSourceImpl;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.tracks.AACTrackImpl;

import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.EncoderProgressListener;
import ws.schild.jave.EncodingAttributes;
import ws.schild.jave.InputFormatException;
import ws.schild.jave.MultimediaInfo;
import ws.schild.jave.MultimediaObject;

public class ExtractTaskThread implements Runnable, Comparable<ExtractTaskThread>{

	private IOFilePair ffmpegFilePair;
	private IOFilePair mp4PhraserFilePair;
	private EncodingAttributes attributes;
	private static final String MP4BOX_PATH = new File("lib/mp4box").getAbsolutePath();
	
	/**
	 * @param ffmpegFilePair
	 * @param mp4PhraserFilePair
	 * @throws MiddleFileMismatchException 
	 */
	public ExtractTaskThread(IOFilePair ffmpegFilePair, IOFilePair mp4PhraserFilePair) throws MiddleFileMismatchException {
		if (ffmpegFilePair.getOutputFile().equals(mp4PhraserFilePair.getInputFile())){
			this.ffmpegFilePair = ffmpegFilePair;
			this.mp4PhraserFilePair = mp4PhraserFilePair;
		} else {
			throw new MiddleFileMismatchException();
		}
	}

	/**
	 * @param attributes the ffmpeg encoding attributes to set
	 */
	public void setEncodingAttributes(EncodingAttributes attributes) {
		this.attributes = attributes;
	}



	@Override
	public void run() {
		if (attributes == null) {
			System.err.println("no ffmpeg attributes found, CXwudi and Miku regrets so much and have to exit");
			return;
		}
		
		//start ffmpeg process
		System.out.println("start video -> aac processing " + ffmpegFilePair.getInputFile());
		if (!ffmpegFilePair.isInputFileExists()) {
			System.err.println("File doesn't exist: " + ffmpegFilePair.getInputFile() + ", CXwudi and Miku are so mad and have to exit");
			return;
		}
		
		//extract audio
		if (!runFFmpegExtraction()) {
			return;
		}
		
		//start the mp4phraser process
		System.out.println("start aac -> m4a processing " + mp4PhraserFilePair.getInputFile());
		if (!mp4PhraserFilePair.isInputFileExists()) {
			System.err.println("File doesn't exist: " + mp4PhraserFilePair.getInputFile() + ", CXwudi and Miku are so mad and have to exit");
			return;
		}
		
		if (runMp4boxTask()) {
			System.out.println("done, final output file: " + mp4PhraserFilePair.getOutputFile());
		}
		
		mp4PhraserFilePair.deleteInputFile();
		
	}

	/**
	 * The actual implementation of using JAVE (Java Audio Video Encoder) library to extract the audio from video
	 */
	private boolean runFFmpegExtraction() {
		
		//start the process
		var ffmpegEncoder = new Encoder(); //to multi-threaded, creates instance for each thread
		try {
			ffmpegEncoder.encode(new MultimediaObject(ffmpegFilePair.getInputFile()),
					ffmpegFilePair.getOutputFile(), attributes, new EncoderProgressListener() {
	
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
			return false;
		} catch (InputFormatException e) {
			System.err.println("Cannot recognize this format: " + e);
			e.printStackTrace();
			return false;
		} catch (EncoderException e) {
			System.err.println("Cannot encode: " + e);
			return false;
		}
		if (ffmpegFilePair.isOutputFileExist()) {
			System.out.println("finish processing " + ffmpegFilePair.getOutputFile());
			return true;
		} else {
			System.err.println("どうしよう, CXwudi and Miku can't find the output file: " + ffmpegFilePair.getOutputFile());
			return false;
		}
	}

	private boolean runMp4parserTask() {
		if(!mp4PhraserFilePair.deleteOutputFileIfExists()) {
			System.err.println("Fail to delete existing output file before execution: " + mp4PhraserFilePair.getOutputFile());
			return false;
		}
		AACTrackImpl aacTrack = null;
		try {
			aacTrack = new AACTrackImpl(new FileDataSourceImpl(mp4PhraserFilePair.getInputFile()));
		} catch (FileNotFoundException e1) {
			System.err.println("this shouldn't happens");
			e1.printStackTrace();
			return false;
		} catch (IOException e1) {
			System.err.println("What is this issue?");
			e1.printStackTrace();
			return false;
		}
		
		var movie = new Movie();
		movie.addTrack(aacTrack);
		
		Container m4aFile = new DefaultMp4Builder().build(movie);
		try (FileChannel ch = new FileOutputStream(mp4PhraserFilePair.getOutputFile()).getChannel()){
			m4aFile.writeContainer(ch);
		} catch (IOException e) {
			System.err.println("どうしよう, CXwudi and miku fail to wrap aac to m4a file");
			e.printStackTrace();
			return false;
		}
		
		try {
			aacTrack.close();
		} catch (IOException e) {
			System.err.println("fail to close input file for mp4praser " + e);
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private boolean runMp4boxTask() {
		if(!mp4PhraserFilePair.deleteOutputFileIfExists()) {
			System.err.println("Fail to delete existing output file before execution: " + mp4PhraserFilePair.getOutputFile());
			return false;
		}
		
		var mp4boxProcessBuilder = new ProcessBuilder(MP4BOX_PATH,
				"-noprog", //don't show progress
				"-add", //add input aac
				mp4PhraserFilePair.getInputFile().getAbsolutePath(),
				mp4PhraserFilePair.getOutputFile().getAbsolutePath()); // to output m4a
		mp4boxProcessBuilder.redirectErrorStream(true).redirectOutput(Redirect.INHERIT);
		
		Process mp4boxProcess = null;
		try {
			mp4boxProcess = mp4boxProcessBuilder.start();
		} catch (IOException e) {
			System.err.println("Fail to create mp4box process, what should Miku do? " + e);
			return false;
		}
		
		try {
			int state;
			if ((state = mp4boxProcess.waitFor()) == 0) {
				return true;
			} else {
				System.err.println("Mp4box exits innormally with value " + state);
				return false;
			}
		} catch (InterruptedException e) {
			System.err.println("Mp4box process is interrupted, what should CXwudi do? " + e);
			return false;
		}
		
	}

	class MiddleFileMismatchException extends Exception {
		
		private static final long serialVersionUID = 6427427163510966720L;
		
		public MiddleFileMismatchException() {
			super("ffmpeg's output file " + ffmpegFilePair.getOutputFile() +
					" is mismatch with mp4pharser input file " + mp4PhraserFilePair.getInputFile());
		}
	}

	@Override
	public int compareTo(ExtractTaskThread o) {
		return this.ffmpegFilePair.compareTo(o.ffmpegFilePair);
	}

	@Override
	public int hashCode() {
		return Objects.hash(ffmpegFilePair, mp4PhraserFilePair);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof ExtractTaskThread)) return false;
		ExtractTaskThread other = (ExtractTaskThread) obj;
		return Objects.equals(ffmpegFilePair, other.ffmpegFilePair) && Objects.equals(mp4PhraserFilePair, other.mp4PhraserFilePair);
	}
	
	
}
