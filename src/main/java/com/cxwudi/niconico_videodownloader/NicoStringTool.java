package com.cxwudi.niconico_videodownloader;
import java.util.stream.Collectors;

/**
 * Tool for String replacement or modification, keep this class for future development.
 * @author CXwudi
 *
 */
public interface NicoStringTool {
	/**
	 * remove any backslash, frontslash, and question mark that can make troubles during File or Directory creation
	 * @param target the target string that need to be fix
	 * @return the proper string that all illegal characters were removed or replaced.
	 */
	public static String fixFileName(String target) {
		return target
				.replaceAll("/", "-")
				.replaceAll("\\\\", "-")
				.replaceAll("\\?", " ")
				.replaceAll("\\*", " ");
	}
	
	/**
	 * a function to filter chars from string and keep the integers.
	 * this function is designed because in some cases, the smXXXXXXXX doesn't read properly
	 * from download.txt file.
	 * @param smid
	 * @return
	 */
	public static int filterIntsFromString(String smid) {
		return Integer.parseInt(
				smid.chars().parallel()
				.filter(Character::isDigit)
				.mapToObj(Character::toString)
				.collect(Collectors.joining())
				);
		//return Integer.parseInt(smid.substring(2, smid.length()));
	}
}
