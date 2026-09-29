package com.srnjak.sortbox.bean.plugins.jpa;

import com.srnjak.sortbox.bean.BeanSortBox;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.srnjak.sortbox.SortOrder.ASCENDING;
import static com.srnjak.sortbox.SortOrder.DESCENDING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CriteriaOrderWriterTest {

    @Mock
    private EntityManager em;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private Root<Object> root;

    @Mock
    private Path<Object> namePath;

    @Mock
    private Order ascOrder;

    private CriteriaOrderWriter<Object> tut;

    @BeforeEach
    public void setUp() {
        when(em.getCriteriaBuilder()).thenReturn(cb);
        tut = CriteriaOrderWriter.forRoot(root, em);
    }

    @Test
    public void write_Builds_An_Ascending_Order_From_The_Root() {

        when(root.<Object>get("name")).thenReturn(namePath);
        when(cb.asc(namePath)).thenReturn(ascOrder);

        BeanSortBox<Object> sortBox = new BeanSortBox<>();
        sortBox.addSortElement("name", ASCENDING);

        List<Order> result = tut.write(sortBox);

        assertEquals(1, result.size());
        assertSame(ascOrder, result.get(0));
        verify(cb).asc(namePath);
    }

    @Test
    public void write_Builds_A_Descending_Order() {

        when(root.<Object>get("name")).thenReturn(namePath);

        BeanSortBox<Object> sortBox = new BeanSortBox<>();
        sortBox.addSortElement("name", DESCENDING);

        tut.write(sortBox);

        verify(cb).desc(namePath);
    }

    @Test
    public void write_Walks_A_Nested_Property_Into_Chained_Paths(
            @Mock Path<Object> addressPath, @Mock Path<Object> cityPath) {

        when(root.<Object>get("address")).thenReturn(addressPath);
        when(addressPath.<Object>get("city")).thenReturn(cityPath);

        BeanSortBox<Object> sortBox = new BeanSortBox<>();
        sortBox.addSortElement("address.city", ASCENDING);

        tut.write(sortBox);

        // address.city must become root.get("address").get("city"),
        // not a single lookup of a property literally named "address.city".
        verify(cb).asc(cityPath);
    }

    @Test
    public void write_Returns_Empty_List_For_Empty_SortBox() {
        assertTrue(tut.write(new BeanSortBox<>()).isEmpty());
    }
}
