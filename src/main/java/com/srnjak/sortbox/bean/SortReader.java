package com.srnjak.sortbox.bean;

public interface SortReader<O, I> {

    BeanSortBox<O> read(I input);

    boolean isValid(I input);
}
