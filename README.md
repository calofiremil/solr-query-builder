# solr-query-builder

This is a Solr builder Lisp like for Java and JavaScript inspired by Datomic Datalog sintax


```
 Map<String, Object> q = query(
                s(FIND, Aco.class,
                        s(WHERE,
                                s(OR, s("like", "name", name),
                                        s(AND, s("=", "description", description), s("gt", "minPriceDaysBefore", 4))),
                                s("in", "minPriceDaysBefore", Arrays.asList(3, 4, 6))),
                        s(ORDER_BY,
                                s("name", ASC),
                                s("minPriceDaysBefore", DESC))));
```
