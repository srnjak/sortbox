package com.srnjak.sortbox.bean.plugins;

import com.srnjak.sortbox.bean.BeanSortBox;
import com.srnjak.sortbox.SortOrder;
import org.junit.jupiter.api.Test;

import static com.srnjak.sortbox.SortOrder.ASCENDING;
import static com.srnjak.sortbox.SortOrder.DESCENDING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    public void read_Fail_When_Illegal() throws Exception {

        CompactSort<Object> tut = new CompactSort<>();

        assertThrows(IllegalArgumentException.class, () -> {
            tut.read("---");
        });
    }

}