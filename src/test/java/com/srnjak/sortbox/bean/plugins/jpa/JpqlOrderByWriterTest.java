package com.srnjak.sortbox.bean.plugins.jpa;

import com.srnjak.sortbox.bean.BeanSortBox;
import org.junit.jupiter.api.Test;

import static com.srnjak.sortbox.SortOrder.ASCENDING;
import static com.srnjak.sortbox.SortOrder.DESCENDING;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JpqlOrderByWriterTest {

    private final JpqlOrderByWriter<Object> tut = new JpqlOrderByWriter<>();

    @Test
    public void write_Starts_With_A_Space_So_It_Can_Be_Appended() {

        BeanSortBox<Object> sortBox = new BeanSortBox<>();
        sortBox.addSortElement("name", ASCENDING);

        // The leading space is load bearing: callers append this straight
        // onto a query string.
        assertEquals(" ORDER BY name ASC", tut.write(sortBox));
    }

    @Test
    public void write_Joins_Several_Properties_With_Commas() {

        BeanSortBox<Object> sortBox = new BeanSortBox<>();
        sortBox.addSortElement("name", ASCENDING);
        sortBox.addSortElement("age", DESCENDING);

        assertEquals(" ORDER BY name ASC, age DESC", tut.write(sortBox));
    }

    @Test
    public void write_Keeps_A_Nested_Property_As_A_Dotted_Path() {

        BeanSortBox<Object> sortBox = new BeanSortBox<>();
        sortBox.addSortElement("address.city", ASCENDING);

        assertEquals(" ORDER BY address.city ASC", tut.write(sortBox));
    }

    @Test
    public void write_Returns_Empty_String_For_Empty_SortBox() {

        // Not " ORDER BY": appending that to a query would not parse.
        assertEquals("", tut.write(new BeanSortBox<>()));
    }
}
