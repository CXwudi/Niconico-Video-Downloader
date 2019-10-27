package com.cxwudi.niconico_videodownloader;

import java.util.Set;
import java.util.TreeSet;
/**
 * The abstruct collection reader, should have the following functionalities:
 * 1. reading Vocaloid PV collection from a source
 * 2. Indicate the reading process is done or not
 * 3. able to return the collection if the reading process is done
 * @author CX无敌
 *
 */
public abstract class CollectionReader {
	protected Set<Vsong> collection;			//the Vocaloid Songs Collection
	protected boolean isDone;					//reading process is done or not
	
	public CollectionReader() {
		collection = new TreeSet<>();
		isDone = false;
	}

	/** 
	 * Concrete classes that inheritances this class have to override this function.
	 * read the collection and record it into a TreeSet, then set attribute isDone to true, 
	 * call {@code getCollection()} to get the reference of record.
	 */
	public abstract void readRecord();
	/**
	 * @return the collection of Vocaloid Songs in TreeSet<Vsong>.
	 *  if the reading process has not finished yet, a Null will be returned
	 */
	public Set<Vsong> getCollection() {
		if (isDone) return collection;
		else return null;
	}
	/**
	 * @return {@code true} if reading process is done 
	 */
	public boolean isDone() {
		return isDone;
	}

}
