import java.util.TreeSet;

public abstract class CollectionReader {
	protected TreeSet<Vsong> collection;		//the Vocaloid Songs Collection
	protected boolean isDone;					//reading process is done or not
	
	public CollectionReader() {
		collection = new TreeSet<>();
		isDone = false;
	}

	/** 
	 * Concrete classes that inheritances this class have to override this function.
	 * read the collection and record it into a TreeSet.
	 * call {@code getCollection()} to get the reference of record.
	 * 
	 * @return boolean that indicate the reading process is done or not
	 */
	public boolean readRecord() {
		return false;
	}
	/**
	 * @return the collection of Vocaloid Songs in TreeSet<Vsong>.
	 *  if the reading process has not finished yet, a Null will be returned
	 */
	public TreeSet<Vsong> getCollection() {
		return collection;
	}

	/**
	 * @return boolean to indicate the reading process is done or not
	 */
	public boolean isDone() {
		return isDone;
	}

}
