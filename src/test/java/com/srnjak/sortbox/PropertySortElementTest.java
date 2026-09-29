package com.srnjak.sortbox;

import com.srnjak.sortbox.bean.BeanSortBox;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.srnjak.sortbox.SortOrder.ASCENDING;
import static com.srnjak.sortbox.SortOrder.DESCENDING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PropertySortElementTest {

    public static class Address {
        private final String city;

        public Address(String city) {
            this.city = city;
        }

        public String getCity() {
            return city;
        }
    }

    public static class Person {
        private final String name;
        private final int age;
        private final Address address;

        public Person(String name, int age, String city) {
            this.name = name;
            this.age = age;
            this.address = new Address(city);
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        public Address getAddress() {
            return address;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private List<Person> people() {
        return new ArrayList<>(Arrays.asList(
                new Person("Carol", 25, "Ljubljana"),
                new Person("Alice", 31, "Maribor"),
                new Person("Bob", 19, "Celje")));
    }

    @Test
    public void compare_Sorts_Ascending() {

        PropertySortElement<Person> tut =
                new PropertySortElement<>("name", ASCENDING);

        List<Person> list = people();
        list.sort(tut);

        assertEquals("[Alice, Bob, Carol]", list.toString());
    }

    @Test
    public void compare_Sorts_Descending() {

        PropertySortElement<Person> tut =
                new PropertySortElement<>("age", DESCENDING);

        List<Person> list = people();
        list.sort(tut);

        assertEquals("[Alice, Carol, Bob]", list.toString());
    }

    @Test
    public void compare_Sorts_By_Nested_Property() {

        PropertySortElement<Person> tut =
                new PropertySortElement<>("address.city", ASCENDING);

        List<Person> list = people();
        list.sort(tut);

        assertEquals("[Bob, Carol, Alice]", list.toString());
    }

    @Test
    public void compare_Returns_Zero_For_Equal_Values() {

        PropertySortElement<Person> tut =
                new PropertySortElement<>("name", ASCENDING);

        assertEquals(0, tut.compare(
                new Person("Alice", 31, "Maribor"),
                new Person("Alice", 19, "Celje")));
    }

    @Test
    public void compare_Throws_RuntimeException_When_Property_Is_Unknown() {

        PropertySortElement<Person> tut =
                new PropertySortElement<>("nope", ASCENDING);

        List<Person> list = people();

        // Not merely "throws something": excluding commons-collections
        // from BeanUtils made this path fail with NoClassDefFoundError,
        // an Error that no caller catches.
        RuntimeException e = assertThrows(
                RuntimeException.class, () -> list.sort(tut));

        assertTrue(e.getMessage().contains("nope"), e.getMessage());
    }

    @Test
    public void beanSortBox_Sorts_By_Several_Properties() {

        BeanSortBox<Person> tut = new BeanSortBox<>();
        tut.addSortElement("address.city", ASCENDING);
        tut.addSortElement("name", ASCENDING);

        List<Person> list = people();
        list.add(new Person("Ana", 40, "Celje"));
        list.sort(tut);

        assertEquals("[Ana, Bob, Carol, Alice]", list.toString());
    }
}
