package com.cxwudi.niconico_video_downloader.v2;

import java.util.Iterator;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
/**
 * The manager that can generate a set of new videos that need to be downloaded 
 * by operates two small model---local recorder and list grabber. 
 * 
 * Details of these two small models will be discussed in their class description. 
 * @author CX无敌
 */
public class TaskManager{
	
	private LocalReader localReader;			//manage to read collection of downloaded video
	private NicoListGrabber nicoListGrabber;	//manage to get collection from Niconico douga favorite lists
	
	private TreeSet<Vsong> task, done;

	public TaskManager(NicoDriver d, TreeSet<Vsong> task, TreeSet<Vsong> done) {	
		localReader = new LocalReader();
		nicoListGrabber = new NicoListGrabber(d);
		this.task = task;
		this.done = done;
	}
	
	/**
	 * Assign localRecord and NicoListGrabber to read their own Vocaloid Songs Collection.
	 */
	public void readRecord() {
		Thread a = new Thread(localReader::readRecord);
		Thread b = new Thread(nicoListGrabber::readRecord);
		a.start();
		b.start();
		try {
			a.join();
			b.join();
		} catch (InterruptedException e) {
			System.err.println(e + "\nthis shouldn't happen");
		}
	}
	
	/**
	 * Grab localRecord and NicoListGrabber's songs collection to determine the task for downloading Vocaloid Songs,
	 * and the list of downloaded songs for record.
	 * @return {@code true} if the function fulfills both task and done.
	 */
	public boolean getTaskAndUpdate() {
		if (!localReader.isDone() || !nicoListGrabber.isDone()) 
			return false;
		TreeSet<Vsong> local = getLocalCollection(), online = getOnlineCollection();
		/*for (Iterator<Vsong> iterator = online.iterator(); iterator.hasNext();) {
			Vsong vsong = iterator.next();
			TreeSet<Vsong> pointer = local.contains(vsong)? done : task;
			pointer.add(vsong);
		}*/
		var map = online.parallelStream().collect(
		        Collectors.groupingByConcurrent(
		                local::contains,
		                Collectors.mapping(//Learning Java: in fact, you don't need mapping function here, but this is a good way to learn collectors.mapping
		                        Function.identity(),
		                        Collectors.toCollection(ConcurrentSkipListSet<Vsong>::new)
		                        )
		                )
		        );
		
		//var b = new Thread(() -> online.parallelStream().filter(vsong -> !local.contains(vsong)).forEach(concurrentTask::add));
		
		if (map.get(true) != null) done.addAll(map.get(true));
		if (map.get(false) != null) task.addAll(map.get(false));
		System.out.println("PV that needed to be downloaded: \n" + task + "\ntotally " + task.size() + " PVs");
		System.out.println("PV that already been downloaded: \n" + done + "\ntotally " + done.size() + " PVs");
		return true;
	}
	/**
	 * mark all PV in niconico collections as downloaded
	 * @return {@code true} if success
	 */
	public boolean setAllDownload() {
		if(!nicoListGrabber.isDone()) return false;
		done.clear();
		done.addAll(getOnlineCollection());
		return true;
	}
	
	public void setDriver(NicoDriver driver) {
		nicoListGrabber.setDriver(driver);
	}
	/**
	 * @return the local record of downloaded PV
	 */
	public TreeSet<Vsong> getLocalCollection() {
		return localReader.getCollection();
	}

	/**
	 * @return the online collection of Vocaloid PV
	 */
	public TreeSet<Vsong> getOnlineCollection() {
		return nicoListGrabber.getCollection();
	}
	/**
	 * @return the task
	 */
	public TreeSet<Vsong> getTask() {
		return task;
	}
	/**
	 * @return the done
	 */
	public TreeSet<Vsong> getUpdate() {
		return done;
	}
	
}
