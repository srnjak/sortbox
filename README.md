# SortBox

A small Java library for describing **how** a collection should be sorted, as
data you can pass around — then applying that description to an in-memory
list, a JPQL query, or a JPA Criteria query.

A sort order is an object here, not a hand-written `Comparator` buried in a
method. That makes it something you can read from a query string, hand to a
repository, reverse, or send to another layer.

[![Maven Central](https://img.shields.io/maven-central/v/com.srnjak/sortbox)](https://central.sonatype.com/artifact/com.srnjak/sortbox)
[![License](https://img.shields.io/badge/license-GPL--3.0-blue)](https://www.gnu.org/licenses/gpl-3.0.html)

## Requirements

| SortBox | Java |
|---|---|
| 3.x | 17 or newer |
| 2.x | 11 or newer |

## Installation

```xml
<dependency>
  <groupId>com.srnjak</groupId>
  <artifactId>sortbox</artifactId>
  <version>3.0.0</version>
</dependency>
```

The JPA support is optional: `jakarta.persistence-api` is a `provided`
dependency, so it is not pulled in transitively. Add it yourself if you use
the classes under `bean.plugins.jpa`.

## Sorting a list

`BeanSortBox` collects properties to sort by and is itself a `Comparator`:

```java
BeanSortBox<Person> sort = new BeanSortBox<>();
sort.addSortElement("lastName", SortOrder.ASCENDING);
sort.addSortElement("age", SortOrder.DESCENDING);

people.sort(sort);
```

Properties are read through Apache Commons BeanUtils, so **nested paths work**:

```java
sort.addSortElement("address.city", SortOrder.ASCENDING);
```

Values are compared with their natural ordering (`Comparable`). Pass a
`Locale` to compare strings with a `Collator` instead, which is what you want
for languages whose alphabet is not ASCII order:

```java
sort.addSortElement("lastName", SortOrder.ASCENDING, new Locale("sl"));
```

`reverse()` returns a new box with every order flipped.

## The compact notation

`CompactSort` reads and writes a sort order as a short string — convenient for
a URL query parameter, where a client asks for an order and the server applies
it without trusting the client with anything more:

```java
CompactSort<Person> compact = new CompactSort<>();

BeanSortBox<Person> sort = compact.read("lastName,-age");
String text = compact.write(sort);   // "lastName,-age"
```

| notation | meaning |
|---|---|
| `name` | ascending |
| `+name` | ascending, explicitly |
| `-name` | descending |
| `a,-b,c` | several properties, applied in order |
| `address.city` | nested property |

Input is validated against a grammar: a property must look like a Java
identifier, optionally dotted. `read` throws `IllegalArgumentException` on
anything else, and returns an empty box for blank input. `isValid` answers the
same question without throwing.

## JPA

Instead of sorting in memory, hand the same box to the database.

**JPQL** — `write` returns a ready `ORDER BY` fragment, **with a leading
space**, or an empty string for an empty box:

```java
String orderBy = new JpqlOrderByWriter<Person>().write(sort);
// " ORDER BY lastName ASC, age DESC"

em.createQuery("SELECT p FROM Person p" + orderBy, Person.class);
```

**Criteria API** — `write` returns the list of `Order` objects, resolving
nested paths into joins on the root:

```java
CriteriaQuery<Person> query = cb.createQuery(Person.class);
Root<Person> root = query.from(Person.class);

query.orderBy(CriteriaOrderWriter.forRoot(root, em).write(sort));
```

### A worked example

The case this library is built for: a client asks for an order in a query
parameter — `GET /people?sort=lastName,-address.city` — and a repository turns
that into a database ordering, without either side knowing about the other.

```java
@Entity
public class Person {
    @Id private Long id;
    private String firstName;
    private String lastName;
    @ManyToOne private Address address;
    // getters
}
```

```java
public class PersonRepository {

    private static final CompactSort<Person> COMPACT = new CompactSort<>();

    private static final Set<String> SORTABLE =
            Set.of("firstName", "lastName", "address.city");

    private final EntityManager em;

    public PersonRepository(EntityManager em) {
        this.em = em;
    }

    public List<Person> find(String sort) {

        String orderBy = new JpqlOrderByWriter<Person>().write(parse(sort));

        return em.createQuery(
                        "SELECT p FROM Person p" + orderBy, Person.class)
                .getResultList();
    }

    public List<Person> findWithCriteria(String sort) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Person> query = cb.createQuery(Person.class);
        Root<Person> root = query.from(Person.class);

        query.select(root).orderBy(
                CriteriaOrderWriter.forRoot(root, em).write(parse(sort)));

        return em.createQuery(query).getResultList();
    }

    private BeanSortBox<Person> parse(String sort) {

        BeanSortBox<Person> order = COMPACT.read(sort);

        order.stream()
                .map(PropertySortElement::getSortBy)
                .filter(p -> !SORTABLE.contains(p))
                .findFirst()
                .ifPresent(p -> {
                    throw new IllegalArgumentException("Not sortable: " + p);
                });

        if (order.isEmpty()) {
            order.addSortElement("lastName", SortOrder.ASCENDING);
        }

        return order;
    }
}
```

What that gives you:

| request | resulting order |
|---|---|
| `?sort=lastName,-address.city` | ` ORDER BY lastName ASC, address.city DESC` |
| no `sort` parameter | ` ORDER BY lastName ASC` — the default |
| `?sort=password` | `IllegalArgumentException: Not sortable: password` |
| `?sort=drop table` | `IllegalArgumentException` from `CompactSort`, on the grammar |

Two things in that repository are worth copying, not just the happy path.

**The whitelist.** The JPQL writer puts the property name into the query
string, so the set of sortable properties belongs to the server, never to the
caller. This is not about injection — `CompactSort` only accepts Java
identifier characters and dots, so quotes, spaces and parentheses never get
through — but about a well-formed name for a property that does not exist,
which fails deep in the persistence provider with a confusing message. Reject
it where you can still explain it.

**The default.** `read` returns an *empty* box for blank input, and an empty
box writes an empty `ORDER BY`, which means an unordered result. If your
pagination assumes a stable order, give the box a default before using it.

`address.city` works in both writers: JPQL takes the dotted path as it is, and
`CriteriaOrderWriter` walks it into `root.get("address").get("city")`.

## Comparators instead of properties

`BeanSortBox` is the convenient case. Underneath it is `SortBox`, which holds
any `Comparator`, so an ordering that is not a simple property lookup fits too:

```java
SortBox<Person, ComparatorSortElement<Person>> sort = new SortBox<>();
sort.addSortElement(new ComparatorSortElement<>(byDistanceFromOffice, ASCENDING));
```

Both implement `Comparator`, and both are `Iterable` over their elements.

## Behavior worth knowing

**Nulls sort last ascending, first descending.** A null property value is
treated as greater than any non-null one, and the descending case inverts that
along with everything else.

**Adding a property twice replaces it.** `name` ascending followed by `name`
descending leaves one entry, descending — the later call wins. The replacement
goes to the end of the box, so re-adding a property also moves it to lowest
priority; `addSortElementHead` moves it to highest instead. The same holds for
`addAll`, and for `SortBox` with comparators, where the comparator instance
takes the place of the property name.

**A property that does not exist throws.** Comparison wraps the BeanUtils
reflection failure in a `RuntimeException`. If the property names come from
outside your code, validate them against your own whitelist — `CompactSort`
checks the *shape* of a name, not that it exists.

## Building

```bash
mvn clean package
```

Requires a JDK 17 or newer. Releases are published to Maven Central through
the Sonatype Central Portal by GitHub Actions: pushes to `develop` publish a
snapshot, and the **Merge Develop to Master** workflow promotes `develop` and
cuts a release.

## License

GNU General Public License v3.0 — see
[gnu.org/licenses/gpl-3.0](https://www.gnu.org/licenses/gpl-3.0.html).
