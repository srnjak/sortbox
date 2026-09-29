package com.srnjak.sortbox.bean.plugins;

import com.srnjak.sortbox.bean.BeanSortBox;
import com.srnjak.sortbox.SortOrder;
import org.junit.jupiter.api.Test;

import static com.srnjak.sortbox.SortOrder.ASCENDING;
import static com.srnjak.sortbox.SortOrder.DESCENDING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompactSortTest {

    @Test
    public void write_Success() throws Exception {

        CompactSort<Object> tut = new CompactSort<>();

        BeanSortBox<Object> beanSortBox_full = new BeanSortBox<>();
        beanSortBox_full.addSortElement("a", ASCENDING);
        beanSortBox_full.addSortElement("b", SortOrder.DESCENDING);
        beanSortBox_full.addSortElement("c", ASCENDING);
        beanSortBox_full.addSortElement("d", SortOrder.DESCENDING);

        BeanSortBox<Object> propertySortBox_empty =
                new BeanSortBox<>();

        String result_full = tut.write(beanSortBox_full);
        String result_empty = tut.write(propertySortBox_empty);

        assertEquals("a,-b,c,-d", result_full);
        assertEquals("", result_empty);
    }

    @Test
    public void read_Success() throws Exception {

        CompactSort<Object> tut = new CompactSort<>();

        BeanSortBox<Object> beanSortBox = tut.read("a,-b,+c,-d");

        assertEquals("a", beanSortBox.get(0).getSortBy());
        assertEquals(ASCENDING, beanSortBox.get(0).getSortOrder());
        assertEquals("b", beanSortBox.get(1).getSortBy());
        assertEquals(DESCENDING, beanSortBox.get(1).getSortOrder());
        assertEquals("c", beanSortBox.get(2).getSortBy());
        assertEquals(ASCENDING, beanSortBox.get(2).getSortOrder());
        assertEquals("d", beanSortBox.get(3).getSortBy());
        assertEquals(DESCENDING, beanSortBox.get(3).getSortOrder());
    }

    @Test
    public void read_Treats_A_Plus_As_Ascending() {

        CompactSort<Object> tut = new CompactSort<>();

        BeanSortBox<Object> beanSortBox = tut.read("+a");

        assertEquals(ASCENDING, beanSortBox.get(0).getSortOrder());
        // The plus is optional sugar, so it is not written back out.
        assertEquals("a", tut.write(beanSortBox));
    }

    @Test
    public void read_Keeps_A_Nested_Property() {

        CompactSort<Object> tut = new CompactSort<>();

        BeanSortBox<Object> beanSortBox = tut.read("-address.city");

        assertEquals("address.city", beanSortBox.get(0).getSortBy());
        assertEquals(DESCENDING, beanSortBox.get(0).getSortOrder());
    }

    @Test
    public void read_Returns_Empty_SortBox_For_Blank_Input() {

        CompactSort<Object> tut = new CompactSort<>();

        assertTrue(tut.read("").isEmpty());
        assertTrue(tut.read("   ").isEmpty());
        assertTrue(tut.read(null).isEmpty());
    }

    @Test
    public void write_Then_read_Round_Trips() {

        CompactSort<Object> tut = new CompactSort<>();

        String notation = "a,-b,c.d";

        assertEquals(notation, tut.write(tut.read(notation)));
    }

    @Test
    public void isValid_Accepts_Well_Formed_Notation() {

        CompactSort<Object> tut = new CompactSort<>();

        assertTrue(tut.isValid("a"));
        assertTrue(tut.isValid("+a,-b"));
        assertTrue(tut.isValid("a.b.c"));
        assertTrue(tut.isValid("_a$b"));
    }

    @Test
    public void isValid_Rejects_Anything_Else() {

        CompactSort<Object> tut = new CompactSort<>();

        assertFalse(tut.isValid("---"));
        assertFalse(tut.isValid("1a"));
        assertFalse(tut.isValid("a b"));
        assertFalse(tut.isValid("a,"));
        // Nothing with a quote or a space can reach a JPQL query string.
        assertFalse(tut.isValid("a' OR '1'='1"));
    }

    @Test
    public void read_Fail_When_Illegal() throws Exception {

        CompactSort<Object> tut = new CompactSort<>();

        assertThrows(IllegalArgumentException.class, () -> {
            tut.read("---");
        });
    }

}