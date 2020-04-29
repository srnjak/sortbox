package com.srnjak.sortbox;

import java.util.Locale;

/**
 * @author Grega
 * @param <O> Object to sort
 *
 */
public class PropertySortBox<O> extends SortBox<O, PropertySortElement<O>> {

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
}
