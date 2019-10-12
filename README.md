# solr-query-builder

This is a Solr builder Lisp like for Java and JavaScript inspired by Datomic Datalog sintax

#Java
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

#JavaScript
```
            query(
                [FIND, "Aco",
                        [WHERE,
                                [OR, ["like", "name", name],
                                        [AND, ["=", "description", description], ["gt", "minPriceDaysBefore", 4]]],
                                ["in", "minPriceDaysBefore", [3, 4, 6]]],
                        [ORDER_BY,
                                ["name", ASC],
                                ["minPriceDaysBefore", DESC]]]];


```
