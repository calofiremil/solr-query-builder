package com.tzapps.solrqc;


import org.junit.Test;

import java.time.LocalDateTime;
import java.util.*;

import static com.tzapps.solrqc.Query.*;
import static org.junit.Assert.assertEquals;

public class QueryTest {

    public static ArrayList s(final Object... args) {
        return new ArrayList(Arrays.asList(args));
    }

    public static String id(final Class aggClass, final String aggregateId) {
        return aggClass.getName().concat("/").concat(aggregateId);
    }

    @Test
    public void run_find() throws Exception {
        Map<String, Object> q = query(
                s(FIND, Aco.class));

        Map<String, Object> eq = new HashMap<>();
        eq.put("verb", FIND);
        eq.put("q", "clazz:com.tzapps.solrqc");

        assertEquals(eq.get("verb"), q.get("verb"));
        assertEquals(eq.get("q"), q.get("q"));
    }

    @Test
    public void run_count_verb() {
        Map<String, Object> q = query(
                s(COUNT, Aco.class));

        Map<String, Object> eq = new HashMap<>();
        eq.put("verb", COUNT);
        eq.put("q", "clazz:com.tzapps.solrqc.Aco");

        assertEquals(eq.get("verb"), q.get("verb"));
        assertEquals(eq.get("q"), q.get("q"));
    }

    @Test
    public void run_find_byAggId() throws Exception {
        Map<String, Object> q = query(
                s(FIND, Aco.class,
                        s(WHERE,
                                s("=", "aggregateId", id(Aco.class, "1234"))
                        )));

        Map<String, Object> eq = new HashMap<>();
        eq.put("q", "clazz:com.tzapps.solrqc");


        List<String> filters = new ArrayList<>();
        filters.add("id:\"com.tzapps.solrqc.Aco/1234\"");
        eq.put("fq", filters);

        assertEquals(eq.get("fq"), q.get("fq"));
        assertEquals(eq.get("q"), q.get("q"));
    }

    @Test
    public void run_find_columns() throws Exception {
        Map<String, Object> q = query(
                s(FIND, s("name", "minPriceDaysBefore"), Aco.class,
                        s(WHERE,
                                s("=", "name", "as")
                        )));

        Map<String, Object> eq = new HashMap<>();
        List<String> columns = new ArrayList<>();
        columns.add("name_t");
        columns.add("minPriceDaysBefore_i");
        eq.put("fl", columns);

        assertEquals(eq.get("fl"), q.get("fl"));
    }

    @Test
    public void run_where_single_condition() throws Exception {
        Map<String, Object> q = query(
                s(FIND, Aco.class,
                        s(WHERE,
                                s("=", "name", "as")
                        )));

        Map<String, Object> eq = new HashMap<>();
        List<String> filters = new ArrayList<>();
        filters.add("name_t:\"as\"");
        eq.put("fq", filters);

        assertEquals(eq.get("fq"), q.get("fq"));
    }

    @Test
    public void run_where_single_condition_not() throws Exception {
        Map<String, Object> q = query(
                s(FIND, Aco.class,
                        s(WHERE,
                                s("!=", "name", "as")
                        )));

        Map<String, Object> eq = new HashMap<>();
        List<String> filters = new ArrayList<>();
        filters.add("!name_t:as");
        eq.put("fq", filters);

        assertEquals(eq.get("fq"), q.get("fq"));
    }

    @Test
    public void run_where_single_condition_Like() throws Exception {
        Map<String, Object> q = query(
                s(FIND, Aco.class,
                        s(WHERE,
                                s("like", "name", "as")
                        )));

        Map<String, Object> eq = new HashMap<>();
        List<String> filters = new ArrayList<>();
        filters.add("name_t:*as*");
        eq.put("fq", filters);

        assertEquals(eq.get("fq"), q.get("fq"));
    }

    @Test
    public void run_where_two_condition() throws Exception {
        Map<String, Object> q = query(
                s(FIND, Aco.class,
                        s(WHERE,
                                s("like", "name", "as"),
                                s("=", "minPriceDaysBefore", 1)
                        )));

        Map<String, Object> eq = new HashMap<>();
        List<String> filters = new ArrayList<>();
        filters.add("name_t:*as*");
        filters.add("minPriceDaysBefore_i:1");
        eq.put("fq", filters);

        assertEquals(eq.get("fq"), q.get("fq"));
    }

    @Test
    public void run_where_many_condition() throws Exception {
        final LocalDateTime dateTime = LocalDateTime.now();
        Map<String, Object> q = query(
                s(FIND, Aco.class,
                        s(WHERE,
                                s("like", "name", "as"),
                                s("=", "minPriceDaysBefore", 1),
                                s("gte", "startDate", dateTime),
                                s("gt", "endDate", dateTime)
                        )));

        Map<String, Object> eq = new HashMap<>();
        List<String> filters = new ArrayList<>();
        filters.add("name_t:*as*");
        filters.add("minPriceDaysBefore_i:1");
        filters.add("startDate_dt:[" + dateTime + " TO *]");
        filters.add("endDate_dt:{" + dateTime + " TO *}");
        eq.put("fq", filters);

        assertEquals(eq.get("fq"), q.get("fq"));
    }


    @Test
    public void run_where_and() throws Exception {
        Map<String, Object> q = query(
                s(FIND, Aco.class,
                        s(WHERE,
                                s(AND, s("like", "name", "as"), s("like", "description", "asd sa"))
                        )));

        Map<String, Object> eq = new HashMap<>();
        List<String> filters = new ArrayList<>();
        filters.add("(name_t:*as* AND description_t:*asd sa*)");
        eq.put("fq", filters);

        assertEquals(eq.get("fq"), q.get("fq"));
    }

    @Test
    public void run_where_or() throws Exception {
        Map<String, Object> q = query(
                s(FIND, Aco.class,
                        s(WHERE,
                                s(OR, s("like", "name", "as"), s("like", "description", "asd sa"))
                        )));

        Map<String, Object> eq = new HashMap<>();
        List<String> filters = new ArrayList<>();
        filters.add("(name_t:*as* OR description_t:*asd sa*)");
        eq.put("fq", filters);

        assertEquals(eq.get("fq"), q.get("fq"));
    }

    @Test
    public void run_where_or_and() throws Exception {
        Map<String, Object> q = query(
                s(FIND, Aco.class,
                        s(WHERE,
                                s(OR, s("like", "name", "as"),
                                        s(AND, s("like", "description", "asd sa"), s("gt", "minPriceDaysBefore", 4))
                                ))));

        Map<String, Object> eq = new HashMap<>();
        List<String> filters = new ArrayList<>();
        filters.add("(name_t:*as* OR (description_t:*asd sa* AND minPriceDaysBefore_i:{4 TO *}))");
        eq.put("fq", filters);

        assertEquals(eq.get("fq"), q.get("fq"));
    }

    @Test
    public void run_where_or_and_x2() throws Exception {
        Map<String, Object> q = query(
                s(FIND, Aco.class,
                        s(WHERE,
                                s(OR, s("like", "name", "as"),
                                        s(AND, s("like", "description", "asd sa"), s("gt", "minPriceDaysBefore", 4))),
                                s("=", "minPriceDaysBefore", 4))));

        Map<String, Object> eq = new HashMap<>();
        List<String> filters = new ArrayList<>();
        filters.add("(name_t:*as* OR (description_t:*asd sa* AND minPriceDaysBefore_i:{4 TO *}))");
        filters.add("minPriceDaysBefore_i:4");
        eq.put("fq", filters);

        assertEquals(eq.get("fq"), q.get("fq"));
    }

    @Test
    public void run_where_or_and_x2_conversion() throws Exception {
        Map<String, Object> q = query(
                s(FIND, Aco.class,
                        s(WHERE,
                                s(OR, s("like", "name", "as"),
                                        s(AND, s("like", "description", "asd sa"), s("gt", "minPriceDaysBefore", "4"))),
                                s("=", "minPriceDaysBefore", 4))));

        Map<String, Object> eq = new HashMap<>();
        List<String> filters = new ArrayList<>();
        filters.add("(name_t:*as* OR (description_t:*asd sa* AND minPriceDaysBefore_i:{4 TO *}))");
        filters.add("minPriceDaysBefore_i:4");
        eq.put("fq", filters);

        assertEquals(eq.get("fq"), q.get("fq"));
    }

    @Test
    public void run_where_not_found() throws Exception {
        try {
            query(s(FIND, Aco.class,
                    s("no_where",
                            s("=", "minPriceDaysBefore", 4))));
        } catch (Exception e) {
            assertEquals("No :where clause found", e.getMessage());
        }
    }

    @Test
    public void run_where_or_and_x2_conversion_aaa() throws Exception {
        final String name = "as";
        final String description = "asd sa";
        final Integer price = 4;

        Map<String, Object> q = query(
                s(FIND, s("name"), Aco.class,
                        s(WHERE,
                                s(OR, s("like", "name", name),
                                        s(AND, s("like", "description", description), s("gt", "minPriceDaysBefore", price))),
                                s("=", "minPriceDaysBefore", 4))));

        Map<String, Object> eq = new HashMap<>();

        List<String> filters = new ArrayList<>();
        filters.add("(name_t:*as* OR (description_t:*asd sa* AND minPriceDaysBefore_i:{4 TO *}))");
        filters.add("minPriceDaysBefore_i:4");
        eq.put("fq", filters);

        List<String> columns = new ArrayList<>();
        columns.add("name_t");
        eq.put("fl", columns);

        assertEquals(eq.get("fq"), q.get("fq"));
        assertEquals(eq.get("fl"), q.get("fl"));
    }


    @Test
    public void run_where_IN() throws Exception {
        final String name = "as";
        final String description = "asd sa";
        final List<Integer> prices = Arrays.asList(3, 4, 6);

        Map<String, Object> q = query(
                s(FIND, Aco.class,
                        s(WHERE,
                                s(OR, s("like", "name", name),
                                        s(AND, s("like", "description", description), s("gt", "minPriceDaysBefore", 4))),
                                s("in", "minPriceDaysBefore", prices))));

        Map<String, Object> eq = new HashMap<>();

        List<String> filters = new ArrayList<>();
        filters.add("(name_t:*as* OR (description_t:*asd sa* AND minPriceDaysBefore_i:{4 TO *}))");
        filters.add("minPriceDaysBefore_i:(3 OR 4 OR 6)");
        eq.put("fq", filters);

        assertEquals(eq.get("fq"), q.get("fq"));
    }

    @Test
    public void run_where_IN2() throws Exception {
        final String name = "as";
        final String description = "asd sa";
        final List<String> prices = Arrays.asList("3", "4", "6");

        Map<String, Object> q = query(
                s(FIND, Aco.class,
                        s(WHERE,
                                s(OR, s("like", "name", name),
                                        s(AND, s("like", "description", description), s("gt", "minPriceDaysBefore", 4))),
                                s("in", "minPriceDaysBefore", prices))));

        Map<String, Object> eq = new HashMap<>();

        List<String> filters = new ArrayList<>();
        filters.add("(name_t:*as* OR (description_t:*asd sa* AND minPriceDaysBefore_i:{4 TO *}))");
        filters.add("minPriceDaysBefore_i:(3 OR 4 OR 6)");
        eq.put("fq", filters);

        assertEquals(eq.get("fq"), q.get("fq"));
    }

    @Test
    public void run_where_IN3() throws Exception {
        final String name = "as";
        final String description = "asd sa";

        Map<String, Object> q = query(
                s(FIND, Aco.class,
                        s(WHERE,
                                s(OR, s("like", "name", name),
                                        s(AND, s("like", "description", description), s("gt", "minPriceDaysBefore", 4))),
                                s("in", "minPriceDaysBefore", Arrays.asList(3, 4, 6)))));

        Map<String, Object> eq = new HashMap<>();

        List<String> filters = new ArrayList<>();
        filters.add("(name_t:*as* OR (description_t:*asd sa* AND minPriceDaysBefore_i:{4 TO *}))");
        filters.add("minPriceDaysBefore_i:(3 OR 4 OR 6)");
        eq.put("fq", filters);

        assertEquals(eq.get("fq"), q.get("fq"));
    }

    @Test
    public void run_where_IN_SORT() throws Exception {
        final String name = "as";
        final String description = "asd sa";
        final List<Object> prices = Arrays.asList(3, 4, 6);

        Map<String, Object> q = query(
                s(FIND, Aco.class,
                        s(WHERE,
                                s(OR, s("like", "name", name),
                                        s(AND, s("=", "description", description), s("gt", "minPriceDaysBefore", 4))),
                                s("in", "minPriceDaysBefore", Arrays.asList(3, 4, 6))),
                        s(ORDER_BY,
                                s("name", ASC),
                                s("minPriceDaysBefore", DESC))));

        Map<String, Object> eq = new HashMap<>();

        List<String> filters = new ArrayList<>();
        filters.add("(name_t:*as* OR (description_t:\"asd sa\" AND minPriceDaysBefore_i:{4 TO *}))");
        filters.add("minPriceDaysBefore_i:(3 OR 4 OR 6)");
        eq.put("fq", filters);

        List<String> sorts = new ArrayList<>();
        sorts.add("name_t asc");
        sorts.add("minPriceDaysBefore_i desc");
        eq.put("sort", sorts);

        assertEquals(eq.get("fq"), q.get("fq"));
        assertEquals(eq.get("sort"), q.get("sort"));
    }

    @Test
    public void run_where_IN_SORT_bad_order() throws Exception {
        final String name = "as";
        final String description = "asd sa";
        final List<Object> prices = new ArrayList<>();
        prices.add(3);
        prices.add(4);
        prices.add(6);

        Map<String, Object> q = query(
                s(FIND, Aco.class,
                        s(WHERE,
                                s(OR, s("like", "name", name),
                                        s(AND, s("like", "description", description), s("gt", "minPriceDaysBefore", 4))),
                                s("in", "minPriceDaysBefore", prices)),
                        s(ORDER_BY,
                                s("name", ASC),
                                s("minPriceDaysBefore", "bad_order"))));

        Map<String, Object> eq = new HashMap<>();

        List<String> filters = new ArrayList<>();
        filters.add("(name_t:*as* OR (description_t:*asd sa* AND minPriceDaysBefore_i:{4 TO *}))");
        filters.add("minPriceDaysBefore_i:(3 OR 4 OR 6)");
        eq.put("fq", filters);

        List<String> sorts = new ArrayList<>();
        sorts.add("name_t asc");
        sorts.add("Invalid :sort order bad_order");
        eq.put("sort", sorts);

        assertEquals(eq.get("fq"), q.get("fq"));
        assertEquals(eq.get("sort"), q.get("sort"));
    }


    @Test
    public void run_count() throws Exception {
        final String correlationId = "f29c3afc-062c-486d-8955-6c3f696ee246";

        Map<String, Object> q = query(
                s(COUNT, Aco.class,
                        s(WHERE,
                                s("=", "correlationId", correlationId))
                ));

        Map<String, Object> eq = new HashMap<>();

        List<String> filters = new ArrayList<>();
        filters.add("correlationId_t:\"" + correlationId + "\"");
        eq.put("fq", filters);


        eq.put("fl", "json_document");


        assertEquals(eq.get("fq"), q.get("fq"));
        assertEquals(eq.get("fl"), q.get("fl"));
    }
}
