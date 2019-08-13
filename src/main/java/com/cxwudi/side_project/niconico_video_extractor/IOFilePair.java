package com.cxwudi.side_project.niconico_video_extractor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
/**
 * A pair of files representing an input file and an output file.
 * Typically used for representing an input/output files of a {@link Process}.
 * For example, FFmpeg Process, or some file manipulating process.
 * @author CX无敌
 *
 */
public class IOFilePair {
	private File inputFile;
	private File outputFile;
	/**
	 * just a constructor
	 * @param inputFile 
	 * @param outputFile
	 * @throws IsNotFileException 
	 */
	public IOFilePair(File inputFile, File outputFile) throws IOException {
		if (!inputFile.exists()) {
			throw new FileNotFoundException("This doesn't exist: " + inputFile.toString());
		}
		if (!inputFile.isFile()) {
			throw new IsNotFileException(inputFile);
		}
		this.inputFile = inputFile;
		this.outputFile = outputFile;
	}
	
	/**
	 * check if the input file exists and is a file
	 * @return {@code true} if above condition pass
	 */
	public boolean isInputFileExists() {
		return inputFile.exists() && inputFile.isFile();
	}
	
	/**
	 * Check if the output file exists.
	 * Usually used for check if the process have successfully come up with the right output file from the input file
	 * @return
	 */
	public boolean isOutputFileExist() {
		return outputFile.exists(); //shouldn't add .isFile() check because a folder with same name is valid
	}
	
	/**
	 * To delete the input file, usually call it after the process is done and we don't want the temp input file
	 * @return {@code true} if file successfully deleted, otherwise false
	 */
	public boolean deleteInputFile() {
		if (isInputFileExists()) {
			try {
				Files.delete(inputFile.toPath());
			} catch (IOException e) {
				System.err.println("fail to delete input file, " + e.toString());
				return false;
			} //didn't use deleteIfExist, it return false if file not found
		}
		return true;
	}
	
	/**
	 * To delete the output file, usually call it before the process starts, in order to make sure no overlamping writting
	 * @return {@code true} if file successfully deleted, otherwise false
	 */
	public boolean deleteOutputFileIfExists() {
		if (isOutputFileExist()) {
			try {
				Files.delete(outputFile.toPath());
			} catch (IOException e) {
				System.err.println("fail to delete output file, " + e.toString());
				return false;
			} //didn't use deleteIfExist, it return false if file not found
		}
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("IOFilePair [inputFile=").append(inputFile).append(", expected outputFile=").append(outputFile).append("]");
		return builder.toString();
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

	class IsNotFileException extends IOException {

		private static final long serialVersionUID = 840127318666270921L;
		
		public IsNotFileException(File file) {
			super("This is not a file: " + file.getAbsolutePath());
		}
	}
	
}
