package com.srnjak.sortbox;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static com.srnjak.sortbox.SortOrder.ASCENDING;
import static com.srnjak.sortbox.SortOrder.DESCENDING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ComparatorSortElementTest {

    private final Comparator<String> byLength =
            Comparator.comparingInt(String::length);

    private List<String> words() {
        return new ArrayList<>(Arrays.asList("ccc", "a", "bb"));
    }

    @Test
    public void compare_Applies_The_Comparator_Ascending() {

        List<String> list = words();
        list.sort(new ComparatorSortElement<>(byLength, ASCENDING));

        assertEquals("[a, bb, ccc]", list.toString());
    }

    @Test
    public void compare_Inverts_The_Comparator_Descending() {

        List<String> list = words();
        list.sort(new ComparatorSortElement<>(byLength, DESCENDING));

        assertEquals("[ccc, bb, a]", list.toString());
    }

    @Test
    public void getSortBy_Returns_The_Comparator_Itself() {

        ComparatorSortElement<String> tut =
                new ComparatorSortElement<>(byLength, ASCENDING);

        assertSame(byLength, tut.getSortBy());
    }

    @Test
    public void isAscending_And_isDescending_Reflect_The_Order() {

        ComparatorSortElement<String> asc =
                new ComparatorSortElement<>(byLength, ASCENDING);
        ComparatorSortElement<String> desc =
                new ComparatorSortElement<>(byLength, DESCENDING);

        assertTrue(asc.isAscending());
        assertFalse(asc.isDescending());
        assertTrue(desc.isDescending());
        assertFalse(desc.isAscending());
    }

    @Test
    public void equals_Compares_Comparator_And_Order() {

        ComparatorSortElement<String> asc =
                new ComparatorSortElement<>(byLength, ASCENDING);

        assertEquals(asc, new ComparatorSortElement<>(byLength, ASCENDING));
        assertEquals(
                asc.hashCode(),
                new ComparatorSortElement<>(byLength, ASCENDING).hashCode());
        assertNotEquals(asc, new ComparatorSortElement<>(byLength, DESCENDING));
    }

    @Test
    public void constructor_Rejects_Null_Arguments() {

        assertThrows(IllegalArgumentException.class,
                () -> new ComparatorSortElement<String>(null, ASCENDING));
        assertThrows(IllegalArgumentException.class,
                () -> new ComparatorSortElement<>(byLength, null));
    }
}
