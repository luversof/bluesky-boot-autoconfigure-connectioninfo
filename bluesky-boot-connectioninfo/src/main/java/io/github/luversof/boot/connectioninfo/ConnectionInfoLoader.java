package io.github.luversof.boot.connectioninfo;

import java.util.List;

/**
 * interface for calling connection information
 * @author bluesky
 *
 * @param <T> Target type to load via Loader
 */
public interface ConnectionInfoLoader<T> {
	
	/**
	 * Key to be used among the keys in the ConnectionInfoLoaderProperties loader map
	 * 
	 * @return Target key of loader map
	 */
	String getLoaderKey();

	/**
	 * Method for handling load without parameters
	 * Used when making a call based on information registered in properties
	 * 
	 * @return List containing loaded connection information
	 */
	List<ConnectionInfo<T>> load();

	/**
	 * Method for performing load processing based on the received connectionList
	 * 
	 * @param connectionList List of connections to load
	 * @return List containing loaded connection information
	 */
	List<ConnectionInfo<T>> load(List<String> connectionList);
	
}
