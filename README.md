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

**The same property can be added twice with different orders.** Adding a
property that is already present replaces it only if the *order matches too*,
so `name` ascending followed by `name` descending leaves **both** entries in
the box — writing out as `name,-name`. The second one never has any effect,
since the first already decided every comparison. Check before adding if your
input can contain duplicates.

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
