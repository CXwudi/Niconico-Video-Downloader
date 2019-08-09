package com.cxwudi.niconico_video_downloader.side_project.niconico_video_extractor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

public abstract class AbstractAudioTask {
	private File inputFile;
	private File outputFile;
	/**
	 * @param inputFile
	 * @param outputFile
	 * @throws IsNotFileException 
	 */
	public AbstractAudioTask(File inputFile, File outputFile) throws IOException {
		if (!inputFile.isFile()) {
			throw new IsNotFileException(inputFile);
		}
		if (!inputFile.exists()) {
			throw new FileNotFoundException("This doesn't exist: " + inputFile.toString());
		}
		this.inputFile = inputFile;
		this.outputFile = outputFile;
	}
	/**
	 * @return the inputFile
	 */
	public File getInputFile() {
		return inputFile;
	}
	/**
	 * @return the outputFile
	 */
	public File getOutputFile() {
		return outputFile;
	}
	
	public boolean isDone() {
		return outputFile.exists();
	}
	
	public boolean deleteInputFile() throws IOException {
		if (inputFile.exists()) {
			Files.delete(inputFile.toPath()); //didn't use deleteIfExist, it return false if file not found
		}
		return true;
		
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AbstractAudioTask [inputFile=").append(inputFile).append(", expected outputFile=").append(outputFile).append("]");
		return builder.toString();
	}
	/**
	 * @param inputFile the inputFile to set
	 * @throws Exception 
	 */
	public final void setInputFile(File inputFile) throws SetUnsupportException {
		throw new SetUnsupportException();
	}
	/**
	 * @param outputFile the outputFile to set
	 * @throws Exception 
	 */
	public final void setOutputFile(File outputFile) throws SetUnsupportException {
		throw new SetUnsupportException();
	}
	
	class SetUnsupportException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = -7510175207422500010L;
		
		public SetUnsupportException() {
			super("doesn't support set");
		}
	}
	
	class IsNotFileException extends IOException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 840127318666270921L;
		public IsNotFileException(File file) {
			super("This is not a file: " + file.getAbsolutePath());
		}
	}
	
}
