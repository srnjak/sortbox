/**
 * 
 */
package com.srnjak.sortbox;

import java.io.Serializable;
import java.util.Comparator;

/**
 * @author Grega Krajnc
 * 
 * @param <O> Type of sorted object.
 */
public interface SortElement<O> extends Comparator<O>, Serializable {

	/**
	 * Returns sortBy object.
	 * 
	 * @return sortBy object
	 */
	Object getSortBy();
	
	/**
	 * Returns sortOrder object.
	 * 
	 * @return sortOrder object
	 */
	SortOrder getSortOrder();
	
	/**
	 * @return true, if sort is ascending
	 */
	boolean isAscending();
	
	/**
	 * @return true, if sort is descending
	 */
	boolean isDescending();
	
	/**
	 * Compares two objects
	 * 
	 * @param o1
	 * @param o2
	 * @return positive number, if obj1 is bigger;
	 * 			negative number, if obj2 is bigger;
	 * 			zero, if they are equal.
	 */
	@Override
	int compare(O o1, O o2);
}
