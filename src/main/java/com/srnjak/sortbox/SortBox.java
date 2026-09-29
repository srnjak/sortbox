package com.srnjak.sortbox;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Stream;

/**
 * Holder of SortElements.
 *
 * @author Grega Krajnc
 * 
 * @param <O> Type of owner object for PropertyMetaData
 * @param <C> Type of Comparator 
 */
public class SortBox<O, C extends Comparator<O>> 
		implements Serializable, Iterable<C>, Comparator<O> {
	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	public static <O, C extends Comparator<O>>
	Collector<C, ?, ? extends SortBox<O, C>> collector() {
		return Collector.of(
				SortBox::new,
				SortBox::addSortElement,
				(left, right) -> {
					left.addAll(right);
					return left;
				});
	}
	
	/**
	 * List of SortElements
	 */
	protected List<C> sortList;
	
	/**
	 * Constructor.
	 */
	public SortBox() {
		sortList = new ArrayList<>();
	} // end SortBox()
	
	/**
	 * Adds SortElement into the SortBox.
	 * 
	 * @param sortElement
	 */
	public void addSortElement(C sortElement) {
		/*
		 * If property already exists in the sortList, we remove it first.
		 */
		removeSameSortBy(sortElement);
		
		sortList.add(sortElement);
	} // end addSortElement()

	/**
	 * Removes any element sorting by the same thing as the given one,
	 * whatever its sort order, so that adding a property twice replaces
	 * it rather than leaving a second entry that can never take effect.
	 *
	 * Elements which are not SortElements have no sortBy to compare, so
	 * for those this falls back to removal by equality.
	 *
	 * @param sortElement the element about to be added
	 */
	private void removeSameSortBy(C sortElement) {

		if (!(sortElement instanceof SortElement)) {
			remove(sortElement);
			return;
		} // end if

		Object sortBy = ((SortElement<?>) sortElement).getSortBy();

		sortList.removeIf(e -> e instanceof SortElement
				&& Objects.equals(((SortElement<?>) e).getSortBy(), sortBy));
	} // end removeSameSortBy()

	/**
	 * Adds all SortElements from the given sortBox.
	 *
	 * @param sortBox
	 */
	public void addAll(SortBox<O, C> sortBox) {
		/*
		 * If properties already exists in the sortList, we remove it first.
		 */
		sortBox.sortList.forEach(this::removeSameSortBy);

		sortList.addAll(sortBox.sortList);
	} // end addSortElement()
	
	/**
	 * Adds SortElement into the SortBox to the beginning.
	 * 
	 * @param sortElement
	 */
	public void addSortElementHead(C sortElement) {
		/*
		 * If property already exists in the sortList, we remove it first.
		 */
		removeSameSortBy(sortElement);
		
		sortList.add(0, sortElement);
	} // end addSortElementHead()
	
	/**
	 * Verifies, if SortBox contains given comparator.
	 * 
	 * @param comparator
	 * @return true, if contains; false, if doesn't contain
	 */
	public boolean contains(Comparator<O> comparator) {
		return sortList.contains(comparator);
	} // end contains()
	
	/**
	 * Removes comparator from SortBox for sortBy object.
	 * 
	 * @param comparator
	 */
	public void remove(Comparator<O> comparator) {
		sortList.remove(comparator);
 	} // end remove()

	/**
	 * Removes all comparators from SortBox which are members
	 * of the given sortBox.
	 *
	 * @param sortBox
	 */
	public void removeAll(SortBox<O, C> sortBox) {
		sortList.removeAll(sortBox.sortList);
	} // end remove()
	
	/**
	 * Clears SortElements from the SortBox.
	 */
	public void clear() {
		this.sortList.clear();
	} // end clear()
	
	/**
	 * Verifies if SortBox is empty.
	 * 
	 * @return true, if SortBox is emty; false if it is not empty
	 */
	public boolean isEmpty() {
		return this.sortList.isEmpty();
	} // end isEmty()
	
	/**
	 * Returns number of SortElements in SortBox.
	 * 
	 * @return size of SortBox
	 */
	public int size() {
		return this.sortList.size();
	} // end size()

	/**
	 * Returns sort element at a specified index.
	 *
	 * @param index The specified index.
	 * @return The sort element
	 */
	public C get(int index) {
		return this.sortList.get(index);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object
	 * #toString()
	 */
	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append("[");

		int i = 0;
		for (C p : sortList) {
			if (i > 0) {
				sb.append(",");
			} // end if

			sb.append(p.toString());

			i++;
		} // end for

		sb.append("]");

		return sb.toString();
	} // end toString()

	/**
	 * @return Iterator
	 */
	@Override
	public Iterator<C> iterator() {
		return sortList.iterator();
	} // end iterator()

	/**
	 * @return stream of sort elements
	 */
	public Stream<C> stream() {
		return sortList.stream();
	}

	/**
	 * Compares its two arguments for order.
	 * 
	 * @param o1
	 * @param o2
	 * @return a negative integer, zero, 
	 * or a positive integer as the first argument is less than, 
	 * equal to, or greater than the second.
	 */
	@Override
	public int compare(O o1, O o2) {
		
		int result = 0;
		
		sortBoxLoop:
		for (Comparator<O> se : this) {
			
			result = se.compare(o1, o2);
			
			if (result != 0) {
				break sortBoxLoop;
			} // end if
		} // end for
		
		return result;
	} // end compare()
}
