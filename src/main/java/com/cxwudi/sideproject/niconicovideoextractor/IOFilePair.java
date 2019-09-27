package com.cxwudi.sideproject.niconicovideoextractor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;
/**
 * A pair of files representing an input file and an output file.
 * Typically used for representing an input/output files of a {@link Process}.
 * For example, FFmpeg Process, or some file manipulating process.
 * @author CX无敌
 *
 */
public class IOFilePair implements Comparable<IOFilePair>{
	private File inputFile;
	private File outputFile;
	/**
	 * just a constructor
	 * @param inputFile 
	 * @param outputFile
	 */
	public IOFilePair(File inputFile, File outputFile) {
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

	@Override
	/**
	 * order of last modify date of input file
	 */
	public int compareTo(IOFilePair o) {
		if(this.inputFile == null && o.inputFile == null) {
			return 0;
		} else if (this.inputFile == null) { // this is null, o is something
			return -1;
		} else if (o.inputFile == null){							// this is something, o is null
			return 1;
		} else {
			return this.inputFile.lastModified() - o.inputFile.lastModified() < 0 ? -1 : 1;
		}

	}

	@Override
	public int hashCode() {
		return Objects.hash(inputFile);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof IOFilePair)) return false;
		IOFilePair other = (IOFilePair) obj;
		return Objects.equals(inputFile, other.inputFile);
	}
	
}
