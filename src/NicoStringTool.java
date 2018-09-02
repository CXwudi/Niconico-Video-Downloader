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
}
