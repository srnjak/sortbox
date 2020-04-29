/**
 * 
 */
package com.srnjak.sortbox;

import java.util.Comparator;
import java.util.Locale;

/**
 * @author Grega Krajnc
 * @param <O> Object to sort
 *
 */
public class SimpleSortBox<O> extends SortBox<O, SortElement<O>> {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Adds SortElement into the SortBox by property and sort.
	 * 
	 * @param property
	 * @param sort
	 */
	public void addSortElement(String property, SortOrder sort) {
		
		addSortElement(new PropertySortElement<>(property, sort));
	} // end addSortElement()
	
	/**
	 * Adds SortElement into the SortBox by property and sort.
	 * 
	 * @param property
	 * @param sort
	 * @param locale 
	 */
	public void addSortElement(
			String property,
			SortOrder sort, 
			Locale locale) {
		
		addSortElement(new PropertySortElement<>(property, sort, locale));
	} // end addSortElement()
	
	/**
	 * Adds SortElement into the SortBox by comparator and sort.
	 * 
	 * @param comparator
	 * @param sort
	 */
	public void addSortElement(Comparator<O> comparator, SortOrder sort) {
		
		addSortElement(new ComparatorSortElement<>(comparator, sort));
	} // end addSortElement()
	
	/**
	 * Adds SortElement into the SortBox by property and sort to the beginning.
	 * 
	 * @param property
	 * @param sort
	 */
	public void addSortElementHead(String property, SortOrder sort) {
		
		addSortElementHead(new PropertySortElement<>(property, sort));
	} // end addSortElementHead()
	
	/**
	 * Adds SortElement into the SortBox by property and sort to the beginning.
	 * 
	 * @param property
	 * @param sort
	 * @param locale 
	 */
	public void addSortElementHead(
			String property,
			SortOrder sort, 
			Locale locale) {
		
		addSortElementHead(new PropertySortElement<>(property, sort, locale));
	} // end addSortElementHead()
	
	/**
	 * Adds SortElement into the SortBox by 
	 * comparator and sort to the beginning.
	 * 
	 * @param comparator
	 * @param sort
	 */
	public void addSortElementHead(
			Comparator<O> comparator, SortOrder sort) {
		
		addSortElementHead(new ComparatorSortElement<>(comparator, sort));
	} // end addSortElementHead()
	
	/**
	 * Returns sort element from SortBox for the given sortBy object.
	 * If SortBox doesn't conatins it, returns null.
	 * 
	 * @param sortBy
	 * @return sortElement
	 */
	private SortElement<O> getSortElementImpl(Object sortBy) {
		
		SortElement<O> result = null; 

		for (SortElement<O> sortElement : this) {
			if (sortElement.getSortBy().equals(sortBy)) {
				result = sortElement;
				break;
			} // end if
		} // end for
		
		return result;
	} // end getSortElement()
	
	/**
	 * Returns sort element from SortBox for the given sortBy property.
	 * If SortBox doesn't conatins it, returns null.
	 * 
	 * @param property
	 * @return sortElement
	 */
	public SortElement<O> getSortElement(String property) {
		return getSortElementImpl(property);
	} // end getSortElement()
	
	/**
	 * Returns sort element from SortBox for the given sortBy comparator.
	 * If SortBox doesn't conatins it, returns null.
	 * 
	 * @param comparator
	 * @return sortElement
	 */
	public SortElement<O> getSortElement(Comparator<O> comparator) {
		return getSortElementImpl(comparator);
	} // end getSortElement()
	
	/**
	 * Verifies, if SortBox contains SortElement for the given property.
	 * 
	 * @param property
	 * @return true, if contains; false, if doesn't contain
	 */
	public boolean containsSortBy(String property) {
		return (getSortElement(property) != null);
	} // end containsSortBy()
	
	/**
	 * Verifies, if SortBox contains SortElement for the given comparator.
	 * 
	 * @param comparator
	 * @return true, if contains; false, if doesn't contain
	 */
	public boolean containsSortBy(Comparator<O> comparator) {
		return (getSortElement(comparator) != null);
	} // end containsSortBy()
	
	/**
	 * Removes SortElement from SortBox for sortBy object.
	 * 
	 * @param property
	 */
	public void removeBySortBy(String property) {
		sortList.remove(getSortElement(property));
 	} // end remove()
	
	/**
	 * Removes SortElement from SortBox for sortBy object.
	 * 
	 * @param comparator
	 */
	public void removeBySortBy(Comparator<O> comparator) {
		sortList.remove(getSortElement(comparator));
 	} // end remove()
}
