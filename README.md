# solr-query-builder

This is a Solr builder Lisp like for Java and JavaScript inspired by Datomic Datalog sintax

#Java
```
 Map<String, Object> q = query(
                s(FIND, User.class,
                        s(WHERE,
                                s(OR, s("like", "name", name),
                                        s(AND, s("=", "description", description), s("gt", "age", 18))),
                                s("in", "bages", Arrays.asList(3, 4, 6))),
                        s(ORDER_BY,
                                s("name", ASC),
                                s("age", DESC))));
```

#JavaScript
```
            query(
                [FIND, "User",
                        [WHERE,
                                [OR, ["like", "name", name],
                                        [AND, ["=", "description", description], ["gt", "age", 18]]],
                                ["in", "bages", [3, 4, 6]]],
                        [ORDER_BY,
                                ["name", ASC],
                                ["bages", DESC]]]];


```
