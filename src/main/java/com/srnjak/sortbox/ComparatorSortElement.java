package com.srnjak.sortbox;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Comparator;

/**
 * Sort elements, which holds data about which comparator to sort and ordering.
 * 
 * @author Grega Krajnc
 *
 * @param <T> Type of sorted object
 */
public class ComparatorSortElement<T> 
		implements SortElement<T> {
	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Pair of comparator and sort.
	 */
	private Pair<Comparator<T>, SortOrder> sortPair;
	
	/**
	 * Constructor.
	 * 
	 * @param comparator
	 * @param sort
	 */
	public ComparatorSortElement(
			Comparator<T> comparator, 
			SortOrder sort) {
		
		if (comparator == null || sort == null) {
			throw new IllegalArgumentException(
					"comparator=" + comparator + " sort=" + sort);
		} // end if
		
		this.sortPair = Pair.of(comparator, sort);
	} // end SortElement()

	/* (non-Javadoc)
	 * @see com.srnjak.seed.structure.sort.SortElement
	 * #getSortBy()
	 */
	@Override
	public Comparator<T> getSortBy() {
		return sortPair.getLeft();
	} // end getProperty()

	/* (non-Javadoc)
	 * @see com.srnjak.seed.structure.sort.SortElement
	 * #getSortOrder()
	 */
	@Override
	public SortOrder getSortOrder() {
		return sortPair.getRight();
	} // end getSort()
		
	/* (non-Javadoc)
	 * @see com.srnjak.seed.structure.sort.SortElement
	 * #isAscending()
	 */
	@Override
	public boolean isAscending() {
		return (SortOrder.ASCENDING.equals(this.sortPair.getRight()));
	} // end isAscending()
		
	/* (non-Javadoc)
	 * @see com.srnjak.seed.structure.sort.SortElement
	 * #isDescending()
	 */
	@Override
	public boolean isDescending() {
		return (SortOrder.DESCENDING.equals(this.sortPair.getRight()));
	} // end isDescending()

	/* (non-Javadoc)
	 * @see java.lang.Object
	 * #equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		boolean result = false;
    	if (o instanceof ComparatorSortElement<?>) {
    		
    		ComparatorSortElement<?> other = (ComparatorSortElement<?>) o;
    		if (sortPair.equals(other.sortPair)) {
    			result = true;
    		} // end if
    	} // end if
    	
    	return result;
	} // end equals()

	/* (non-Javadoc)
	 * @see java.lang.Object
	 * #hashCode()
	 */
	@Override
	public int hashCode() {
		return this.sortPair.hashCode();
	} // end hashCode()

	/* (non-Javadoc)
	 * @see java.lang.Object
	 * #toString()
	 */
	@Override
	public String toString() {
		return sortPair.toString();
	} // end toString()

	/* (non-Javadoc)
	 * @see com.srnjak.seed.structure.sort.SortElement
	 * #compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(T o1, T o2) {
		int result = getSortBy().compare(o1, o2);
		
		if (SortOrder.DESCENDING.equals(getSortOrder())) {
			result = -result;
		} // end if
		
		return result;
	} // end compare()
}
