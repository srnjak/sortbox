package com.srnjak.sortbox.bean.plugins.jpa;

import com.srnjak.sortbox.bean.BeanSortBox;
import com.srnjak.sortbox.PropertySortElement;
import com.srnjak.sortbox.bean.SortWriter;

public class JpqlOrderByWriter<O> implements SortWriter<O, String> {

    /**
     * Prepares ORDER BY part for JPQL.
     *
     * Each ORDER BY element is make like this:
     * property ASC|DESC
     *
     * @param beanSortBox must not be null
     * @return ORDER BY part for JPQL;
     * 		returns empty string, if propertySortBox is empty.
     */
    @Override
    public String write(BeanSortBox<O> beanSortBox) {
        StringBuilder sb = new StringBuilder();

        for (PropertySortElement<O> pse : beanSortBox) {

            if (sb.length() > 0) {
                sb.append(", ");
            } else {
                sb.append(" ORDER BY ");
            } // end if - else

            sb.append(pse.getSortBy());

            if (pse.isAscending()) {
                sb.append(" ASC");
            } else if (pse.isDescending()) {
                sb.append(" DESC");
            } else {
                throw new IllegalArgumentException(pse.toString());
            } // end if - else if - else
        } // end for

        String str;
        if (beanSortBox.isEmpty()) {
            str = "";
        } else {
            str = sb.toString();
        } // end if - else

        return str;
    }
}
