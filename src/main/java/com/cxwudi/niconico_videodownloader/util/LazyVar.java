package com.cxwudi.niconico_videodownloader.util;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * A wrapper class of any class instance to achieve lazy evaluation, 
 * which allow such instance been not created only until {@link LazyVar#get()} is called. 
 * And such instance is stored for next {@link LazyVar#get()} function call to avoid re-creation
 * @author CX无敌
 *
 * @param <T> an instance to be created lazily
 */
public class LazyVar<T> {
	/**
	 * the variable to be stored
	 */
	private Optional<T> objectOpt;
	
	/**
	 * the create function 
	 */
	private Supplier<T> createFunction;

	/**
	 * create a lazy variable with a creation function that only been executed when {@link LazyVar#get()} called
	 * @param createInstanceFunc the creation function
	 */
	public LazyVar(Supplier<T> createInstanceFunc){
		objectOpt = Optional.empty();
		createFunction = createInstanceFunc;
	}

	/** 
	 * get or create the object using {@link LazyVar#createFunction}
	 * @return the object
	 */
	public T get() {
		if (objectOpt.isEmpty()) {
			objectOpt = Optional.of(createFunction.get());
		} 
		return objectOpt.get();
	}

	/**
	 * set the object, do so will replace {@link LazyVar#createFunction} with this object
	 * @param object the object to set
	 */
	public void set(T object) {
		objectOpt = Optional.of(object);
	}
}
