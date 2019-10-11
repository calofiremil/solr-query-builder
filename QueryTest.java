package ro.esolutions.rp;

import org.junit.Test;
import ro.esolutions.rp.aco.domain.Aco;
import ro.esolutions.rp.acoproductshopquantity.domain.AcoProductShopQuantity;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static ro.esolutions.rp.solr.Query.*;

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
		eq.put("q", "clazz:ro.esolutions.rp.aco.domain.Aco");

		assertEquals(eq.get("verb"), q.get("verb"));
		assertEquals(eq.get("q"), q.get("q"));
	}

	@Test
	public void run_count_verb() {
		Map<String, Object> q = query(
				s(COUNT, Aco.class));

		Map<String, Object> eq = new HashMap<>();
		eq.put("verb", COUNT);
		eq.put("q", "clazz:ro.esolutions.rp.aco.domain.Aco");

		assertEquals(eq.get("verb"), q.get("verb"));
		assertEquals(eq.get("q"), q.get("q"));
	}

	@Test
	public void run_find_byAggId() throws Exception {
		Map<String, Object> q = query(
				s(FIND, Aco.class,
						s(WHERE,
								s("=", "aco/aggregateId", id(Aco.class, "1234"))
						)));

		Map<String, Object> eq = new HashMap<>();
		eq.put("q", "clazz:ro.esolutions.rp.aco.domain.Aco");


		List<String> filters = new ArrayList<>();
		filters.add("id:\"ro.esolutions.rp.aco.domain.Aco/1234\"");
		eq.put("fq", filters);

		assertEquals(eq.get("fq"), q.get("fq"));
		assertEquals(eq.get("q"), q.get("q"));
	}

	@Test
	public void run_find_columns() throws Exception {
		Map<String, Object> q = query(
				s(FIND, s("aco/name", "aco/minPriceDaysBefore"), Aco.class,
						s(WHERE,
								s("=", "aco/name", "as")
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
								s("=", "aco/name", "as")
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
								s("!=", "aco/name", "as")
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
								s("like", "aco/name", "as")
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
								s("like", "aco/name", "as"),
								s("=", "aco/minPriceDaysBefore", 1)
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
								s("like", "aco/name", "as"),
								s("=", "aco/minPriceDaysBefore", 1),
								s("gte", "aco/startDate", dateTime),
								s("gt", "aco/endDate", dateTime)
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
								s(AND, s("like", "aco/name", "as"), s("like", "aco/description", "asd sa"))
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
								s(OR, s("like", "aco/name", "as"), s("like", "aco/description", "asd sa"))
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
								s(OR, s("like", "aco/name", "as"),
										s(AND, s("like", "aco/description", "asd sa"), s("gt", "aco/minPriceDaysBefore", 4))
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
								s(OR, s("like", "aco/name", "as"),
										s(AND, s("like", "aco/description", "asd sa"), s("gt", "aco/minPriceDaysBefore", 4))),
								s("=", "aco/minPriceDaysBefore", 4))));

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
								s(OR, s("like", "aco/name", "as"),
										s(AND, s("like", "aco/description", "asd sa"), s("gt", "aco/minPriceDaysBefore", "4"))),
								s("=", "aco/minPriceDaysBefore", 4))));

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
							s("=", "aco/minPriceDaysBefore", 4))));
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
				s(FIND, s("aco/name"), Aco.class,
						s(WHERE,
								s(OR, s("like", "aco/name", name),
										s(AND, s("like", "aco/description", description), s("gt", "aco/minPriceDaysBefore", price))),
								s("=", "aco/minPriceDaysBefore", 4))));

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
								s(OR, s("like", "aco/name", name),
										s(AND, s("like", "aco/description", description), s("gt", "aco/minPriceDaysBefore", 4))),
								s("in", "aco/minPriceDaysBefore", prices))));

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
								s(OR, s("like", "aco/name", name),
										s(AND, s("like", "aco/description", description), s("gt", "aco/minPriceDaysBefore", 4))),
								s("in", "aco/minPriceDaysBefore", prices))));

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
								s(OR, s("like", "aco/name", name),
										s(AND, s("like", "aco/description", description), s("gt", "aco/minPriceDaysBefore", 4))),
								s("in", "aco/minPriceDaysBefore", Arrays.asList(3, 4, 6)))));

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
								s(OR, s("like", "aco/name", name),
										s(AND, s("=", "aco/description", description), s("gt", "aco/minPriceDaysBefore", 4))),
								s("in", "aco/minPriceDaysBefore", Arrays.asList(3, 4, 6))),
						s(ORDER_BY,
								s("aco/name", ASC),
								s("aco/minPriceDaysBefore", DESC))));

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
				s(COUNT, AcoProductShopQuantity.class,
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
