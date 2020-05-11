package com.srnjak.sortbox;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.InvocationTargetException;
import java.text.Collator;
import java.util.Locale;

import static com.srnjak.sortbox.SortOrder.ASCENDING;
import static com.srnjak.sortbox.SortOrder.DESCENDING;

/**
 * Sort elements, which holds data about which property to sort and ordering.
 * 
 * @author Grega Krajnc
 *
 * @param <O> Type of sorted object
 */
public class PropertySortElement<O>
		implements SortElement<O> {
	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Pair of property and sort.
	 */
	private final Pair<String, SortOrder> sortPair;
	
	/**
	 * Locale
	 */
	private Locale locale = null;
	
	/**
	 * Constructor.
	 * 
	 * @param property
	 * @param sort
	 * @param locale 
	 */
	public PropertySortElement(
			String property,
			SortOrder sort,
			Locale locale) {
		
		if (property == null || sort == null) {
			throw new IllegalArgumentException(
					"property=" + property + " sort=" + sort);
		} // end if
		
		this.sortPair = Pair.of(property, sort);
		
		this.locale = locale;
	} // end PropertySortElement()
	
	/**
	 * Constructor
	 * 
	 * @param property 
	 * @param sort 
	 */
	public PropertySortElement(
			String property,
			SortOrder sort) {
		this(property, sort, null);
	} // end PropertySortElement()

	/* (non-Javadoc)
	 * @see com.srnjak.seed.structure.sort.SortElement
	 * #getSortBy()
	 */
	@Override
	public String getSortBy() {
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
		return (ASCENDING.equals(this.sortPair.getRight()));
	} // end isAscending()
	
	/* (non-Javadoc)
	 * @see com.srnjak.seed.structure.sort.SortElement
	 * #isDescending()
	 */
	@Override
	public boolean isDescending() {
		return (SortOrder.DESCENDING.equals(this.sortPair.getRight()));
	} // end isDescending()

	/**
	 * @return new instance of {@link PropertySortElement} with
	 * the opposite sort order.
	 */
	public PropertySortElement<O> reverse() {

		SortOrder order;
		if (ASCENDING.equals(this.getSortOrder())) {
			order = DESCENDING;
		} else {
			order = ASCENDING;
		}

		return new PropertySortElement<>(this.getSortBy(), order);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object
	 * #equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		boolean result = false;
    	if (o instanceof PropertySortElement<?>) {
    		
    		PropertySortElement<?> other = (PropertySortElement<?>) o;
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
	@SuppressWarnings("unchecked")
	@Override
	public int compare(O o1, O o2) {
		
		Object o1Value;
		Object o2Value;
		try {
			o1Value = PropertyUtils.getProperty(o1, getSortBy());
			o2Value = PropertyUtils.getProperty(o1, getSortBy());
		} catch (IllegalArgumentException
				| IllegalAccessException
				| InvocationTargetException
				| NoSuchMethodException e) {
			throw new RuntimeException(e);
		} // end try - catch

		int result;
		
		if (o1Value == null) {
			result = 1;
			
		} else if (o2Value == null) {
			result = -1;
			
		} else if (locale != null 
				&& o1Value instanceof String 
				&& o2Value instanceof String) {
			
			final Collator collator = Collator.getInstance(locale);
			result = collator.compare(o1Value, o2Value);
			
		} else {
			
			if (o1Value instanceof Comparable 
					&& o2Value instanceof Comparable) {
				
				@SuppressWarnings("rawtypes")
				Comparable com1 = (Comparable) o1Value;
				
				@SuppressWarnings("rawtypes")
				Comparable com2 = (Comparable) o2Value;
				result = com1.compareTo(com2);
			} else {
				throw new IllegalArgumentException(
						"Not comparable parameters: " + o1 + ", " + o2);
			} // end if - else
			
		} // end if - else if ... - else if - else
		
		if (SortOrder.DESCENDING.equals(getSortOrder())) {
			result = -result;
		} // end if
		
		return result;
	} // end compare()
}
