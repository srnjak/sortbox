package com.srnjak.sortbox.bean.plugins.jpa;

import com.srnjak.sortbox.bean.BeanSortBox;
import com.srnjak.sortbox.PropertySortElement;
import com.srnjak.sortbox.bean.SortWriter;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class CriteriaOrderWriter<O> implements SortWriter<O, List<Order>> {

    public static <O> CriteriaOrderWriter<O> forRoot(
            Root<O> root, EntityManager em) {
        return new CriteriaOrderWriter<>(root, em);
    }

    private EntityManager em;

    private Root<O> root;

    public CriteriaOrderWriter(Root<O> root, EntityManager em) {
        this.root = root;
        this.em = em;
    }

    /**
     * Prepares list of orders for CriteriaAPI of JPA.
     *
     * @param beanSortBox	propertySortBox must not be null
     * @return list of orders
     */
    @Override
    public List<Order> write(BeanSortBox<O> beanSortBox) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        List<Order> orderList = new ArrayList<>();

        for (PropertySortElement<O> pse : beanSortBox) {
            String fieldName = pse.getSortBy();

            Path<?> expression;
            if (fieldName.contains(".")) {
                String[] names = StringUtils.split(fieldName, ".");
                expression = root.get(names[0]);
                for (int i = 1; i < names.length; i++) {
                    expression = expression.get(names[i]);
                }
            } else {
                expression = root.get(fieldName);
            }

            if (pse.isAscending()) {
                orderList.add(cb.asc(expression));
            } else if (pse.isDescending()) {
                orderList.add(cb.desc(expression));
            } else {
                throw new IllegalArgumentException(pse.toString());
            } // end if - else if - else
        } // end for

        return orderList;
    }
}
