package com.srnjak.sortbox;

import com.srnjak.sortbox.bean.BeanSortBox;
import org.junit.jupiter.api.Test;

import java.util.Comparator;

import static com.srnjak.sortbox.SortOrder.ASCENDING;
import static com.srnjak.sortbox.SortOrder.DESCENDING;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SortBoxTest {

    @Test
    public void addSortElement_Replaces_Same_Property_With_Other_Order() {

        BeanSortBox<Object> tut = new BeanSortBox<>();
        tut.addSortElement("name", ASCENDING);
        tut.addSortElement("name", DESCENDING);

        assertEquals(1, tut.size());
        assertEquals(DESCENDING, tut.get(0).getSortOrder());
    }

    @Test
    public void addSortElement_Replaces_Same_Property_With_Same_Order() {

        BeanSortBox<Object> tut = new BeanSortBox<>();
        tut.addSortElement("name", ASCENDING);
        tut.addSortElement("name", ASCENDING);

        assertEquals(1, tut.size());
    }

    @Test
    public void addSortElement_Moves_Replaced_Property_To_The_End() {

        BeanSortBox<Object> tut = new BeanSortBox<>();
        tut.addSortElement("a", ASCENDING);
        tut.addSortElement("b", ASCENDING);
        tut.addSortElement("a", DESCENDING);

        assertEquals(2, tut.size());
        assertEquals("b", tut.get(0).getSortBy());
        assertEquals("a", tut.get(1).getSortBy());
    }

    @Test
    public void addSortElementHead_Replaces_Same_Property() {

        BeanSortBox<Object> tut = new BeanSortBox<>();
        tut.addSortElement("a", ASCENDING);
        tut.addSortElement("b", ASCENDING);
        tut.addSortElementHead("b", DESCENDING);

        assertEquals(2, tut.size());
        assertEquals("b", tut.get(0).getSortBy());
        assertEquals(DESCENDING, tut.get(0).getSortOrder());
    }

    @Test
    public void addAll_Replaces_Properties_Already_Present() {

        BeanSortBox<Object> tut = new BeanSortBox<>();
        tut.addSortElement("a", ASCENDING);
        tut.addSortElement("b", ASCENDING);

        BeanSortBox<Object> other = new BeanSortBox<>();
        other.addSortElement("b", DESCENDING);

        tut.addAll(other);

        assertEquals(2, tut.size());
        assertEquals("a", tut.get(0).getSortBy());
        assertEquals("b", tut.get(1).getSortBy());
        assertEquals(DESCENDING, tut.get(1).getSortOrder());
    }

    @Test
    public void addSortElement_Replaces_Same_Comparator_With_Other_Order() {

        Comparator<String> byLength = Comparator.comparingInt(String::length);

        SortBox<String, ComparatorSortElement<String>> tut = new SortBox<>();
        tut.addSortElement(new ComparatorSortElement<>(byLength, ASCENDING));
        tut.addSortElement(new ComparatorSortElement<>(byLength, DESCENDING));

        assertEquals(1, tut.size());
        assertEquals(DESCENDING, tut.get(0).getSortOrder());
    }

    @Test
    public void addSortElement_Keeps_Different_Properties() {

        BeanSortBox<Object> tut = new BeanSortBox<>();
        tut.addSortElement("a", ASCENDING);
        tut.addSortElement("b", DESCENDING);
        tut.addSortElement("c", ASCENDING);

        assertEquals(3, tut.size());
    }
}
