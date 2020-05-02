package com.srnjak.sortbox.bean;

public interface SortWriter<O, R> {
    R write(BeanSortBox<O> beanSortBox);
}
