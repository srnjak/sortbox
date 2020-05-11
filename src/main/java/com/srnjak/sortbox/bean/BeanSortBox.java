package com.srnjak.sortbox.bean;

import com.srnjak.sortbox.PropertySortElement;
import com.srnjak.sortbox.SortBox;
import com.srnjak.sortbox.SortOrder;
import lombok.Getter;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;
import java.util.stream.Collector;
import java.util.stream.Stream;

/**
 * @author Grega
 * @param <O> Object to sort
 *
 */
public class BeanSortBox<O>
		implements Comparator<O>, Iterable<PropertySortElement<O>> {

	public static <O, C extends PropertySortElement<O>>
	Collector<C, ?, BeanSortBox<O>> collector() {
		return Collector.of(
				BeanSortBox::new,
				BeanSortBox::addSortElement,
				(left, right) -> {
					left.addAll(right);
					return left;
				});
	}

	public static <O, I> BeanSortBox<O> from(
			I input, SortReader<O, I> sortReader) {
		return sortReader.read(input);
	}

	@Getter
	private SortBox<O, PropertySortElement<O>> sortBox;

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	public BeanSortBox() {
		this.sortBox = new SortBox<>();
	}

	public BeanSortBox(SortBox<O, PropertySortElement<O>> sortBox) {
		this.sortBox = sortBox;
	}
	
	/**
	 * Adds SortElement into the SortBox by property and sort.
	 * 
	 * @param property
	 * @param sort
	 */
	public void addSortElement(String property, SortOrder sort) {
		this.sortBox.addSortElement(new PropertySortElement<>(property, sort));
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

		this.sortBox.addSortElement(
				new PropertySortElement<>(property, sort, locale));
	} // end addSortElement()
	
	/**
	 * Adds SortElement into the SortBox by property and sort to the beginning.
	 * 
	 * @param property
	 * @param sort
	 */
	public void addSortElementHead(String property, SortOrder sort) {

		this.sortBox.addSortElementHead(
				new PropertySortElement<>(property, sort));
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

		this.sortBox.addSortElementHead(
				new PropertySortElement<>(property, sort, locale));
	} // end addSortElementHead()

	/**
	 * Adds SortElement into the SortBox.
	 *
	 * @param sortElement
	 */
	public void addSortElement(PropertySortElement<O> sortElement) {
		sortBox.addSortElement(sortElement);
	}

	/**
	 * Adds all SortElements from the given sortBox.
	 *
	 * @param beanSortBox
	 */
	public void addAll(BeanSortBox<O> beanSortBox) {
		this.sortBox.addAll(beanSortBox.getSortBox());
	}

	/**
	 * Adds SortElement into the SortBox to the beginning.
	 *
	 * @param sortElement
	 */
	public void addSortElementHead(PropertySortElement<O> sortElement) {
		sortBox.addSortElementHead(sortElement);
	}

	/**
	 * @return new {@link BeanSortBox} instance with the opposite sort order.
	 */
	public BeanSortBox<O> reverse() {
		return this.stream()
				.map(PropertySortElement::reverse)
				.collect(BeanSortBox.collector());
	}

	/**
	 * Verifies, if SortBox contains given sortElement.
	 *
	 * @param sortElement
	 * @return true, if contains; false, if doesn't contain
	 */
	public boolean contains(PropertySortElement<O> sortElement) {
		return sortBox.contains(sortElement);
	}

	/**
	 * Removes comparator from SortBox for sortBy object.
	 *
	 * @param sortElement
	 */
	public void remove(PropertySortElement<O> sortElement) {
		sortBox.remove(sortElement);
	}

	/**
	 * Removes all comparators from SortBox which are members
	 * of the given sortBox.
	 *
	 * @param beanSortBox
	 */
	public void removeAll(BeanSortBox<O> beanSortBox) {
		this.sortBox.removeAll(beanSortBox.getSortBox());
	}

	/**
	 * Clears SortElements from the BeanSortBox.
	 */
	public void clear() {
		sortBox.clear();
	}

	/**
	 * Verifies if SortBox is empty.
	 *
	 * @return true, if SortBox is emty; false if it is not empty
	 */
	public boolean isEmpty() {
		return sortBox.isEmpty();
	}

	/**
	 * Returns number of SortElements in SortBox.
	 *
	 * @return size of SortBox
	 */
	public int size() {
		return sortBox.size();
	}

	/**
	 * Returns sort element at a specified index.
	 *
	 * @param index The specified index.
	 * @return The sort element
	 */
	public PropertySortElement<O> get(int index) {
		return sortBox.get(index);
	}

	/**
	 * @return Iterator
	 */
	public Iterator<PropertySortElement<O>> iterator() {
		return sortBox.iterator();
	}

	/**
	 * @return stream of sort elements
	 */
	public Stream<PropertySortElement<O>> stream() {
		return sortBox.stream();
	}

	@Override
	public int compare(O o1, O o2) {
		return this.sortBox.compare(o1, o2);
	}

	public <R> R export(SortWriter<O, R> sortWriter) {
		return sortWriter.write(this);
	}
}
