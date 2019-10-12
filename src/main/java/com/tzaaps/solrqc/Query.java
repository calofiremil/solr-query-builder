package com.tzaaps.solrqc;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@FunctionalInterface
interface Rule {
	String apply(Object left, Object right);
}


public final class Query {

	public static final String FIND = ":find";
	public static final String COUNT = ":count";
	public static final String WHERE = ":where";
	public static final String AND = ":and";
	public static final String OR = ":or";
	public static final String ORDER_BY = ":sort";

	public static final String ASC = "asc";
	public static final String DESC = "desc";

	private Query() {
	}


	private static final Map<String, Rule> conditions = new HashMap<>();

	static {

		conditions.put("=", (Object o, Object o2) -> o + ":" + o2);
		conditions.put("!=", (Object o, Object o2) -> "!" + o + ":" + o2);
		conditions.put("like", (Object o, Object o2) -> o + ":*" + o2 + "*");
		conditions.put("gt", (Object o, Object o2) -> o + ":{" + o2 + " TO *}");
		conditions.put("gte", (Object o, Object o2) -> o + ":[" + o2 + " TO *]");
		conditions.put("in", (Object o, Object o2) -> o + ":(" + o2 + ")");
		conditions.put(OR, (Object o, Object o2) -> "(" + o + " OR " + o2 + ")");
		conditions.put(AND, (Object o, Object o2) -> "(" + o + " AND " + o2 + ")");

	}

	private static final Map<Class, String> stringMap = new HashMap<>();

	static {
		stringMap.put(String.class, "_t");
		stringMap.put(Integer.class, "_i");
		stringMap.put(Float.class, "_f");
		stringMap.put(Double.class, "_d");
		stringMap.put(Boolean.class, "_b");
		stringMap.put(LocalDate.class, "_dt");
		stringMap.put(List.class, "_ss");
		stringMap.put(LocalDateTime.class, "_dt");
	}

	public static Map<String, Object> query(final ArrayList queryList) {

		Map<String, Object> queryMap = new HashMap<>();

		ListIterator query = queryList.listIterator();

		final String word = query.next().toString();
		queryMap.put("verb", word);

		if (COUNT.equals(word)) {
			queryMap.put("fl", "json_document");
		}

		Object selectedAggregate = query.next();
		if (selectedAggregate == null) return queryMap;
		if (selectedAggregate.getClass().equals(ArrayList.class)) {
			queryMap.put("fl", mergeColumns((ArrayList) selectedAggregate));

			Class entity = (Class) query.next();
			if (entity == null) return queryMap;
			queryMap.put("q", "clazz:" + entity.getName());
			queryMap.put("entity", entity);
		} else if (selectedAggregate instanceof String) {
			Class entity;
			try {
				if (selectedAggregate.toString().contains("esolutions"))
					entity = Class.forName(selectedAggregate.toString());
				else
					entity = Class.forName("ro.esolutions.rp." + selectedAggregate.toString().toLowerCase() + ".domain." + camelCase(selectedAggregate.toString()));

				queryMap.put("q", "clazz:" + entity.getName());
				queryMap.put("entity", entity);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			Class entity = (Class) selectedAggregate;
			queryMap.put("q", "clazz:" + entity.getName());
			queryMap.put("entity", entity);
		}

		while (query.hasNext()) {
			ArrayList nextGroup = (ArrayList) query.next();
			if (nextGroup == null) return queryMap;
			Object groupV = nextGroup.get(0);

			if (WHERE.equals(groupV)) {
				List clauses = nextGroup.subList(1, nextGroup.size());
				List<Object> collector = new ArrayList<>();
				clauses.forEach(elem -> interpretClause((List) elem, collector, queryMap));

				queryMap.put("fq", collector);
			}

			if (ORDER_BY.equals(groupV)) {
				List conditions = nextGroup.subList(1, nextGroup.size());
				List<String> collector = new ArrayList<>();
				conditions.forEach(elem -> interpretSortClause((List) elem, collector, queryMap));

				queryMap.put("sort", collector);
			}
		}

		queryMap.put("start", 0);
		queryMap.put("rows", Integer.MAX_VALUE);

		return queryMap;
	}

	private static void interpretSortClause(List clauses, List<String> collector, Map queryMap) {
		final String first = (String) clauses.get(0);
		final String second = (String) clauses.get(1);
		try {
			final String order = validateFieldAndGetPrefix(first, (Class) queryMap.get("entity")) + " " + parseOrder(second);
			collector.add(order);
		} catch (Exception e) {
			collector.add(e.getMessage());
		}
	}

	private static String parseOrder(final String order) {
		if (ASC.equals(order) || DESC.equals(order)) {
			return order;
		}
		throw new RuntimeException("Invalid :sort order " + order);
	}

	private static Object mergeColumns(ArrayList selectedAggregate) {
		return selectedAggregate.stream().map(elem -> {
			try {
				return validateFieldAndGetPrefix(elem.toString(), null);
			} catch (Exception e) {
				return "error";
			}
		}).collect(Collectors.toList());
	}

	private static String interpretClause(List<Object> clauses, List<Object> collector, Map queryMap) {
		try {

			final String condition = (String) clauses.get(0);
			final Rule f = conditions.get(condition);

			Object first = clauses.get(1);
			if (first instanceof ArrayList)
				first = interpretClause((List) first, Collections.emptyList(), queryMap);
			else
				first = validateFieldAndGetPrefix(first.toString(), (Class) queryMap.get("entity"));

			Object second = clauses.get(2);
			if (second instanceof ArrayList && !"in".equals(condition))
				second = interpretClause((List) second, Collections.emptyList(), queryMap);
			else if (second instanceof List && "in".equals(condition))
				second = ((List) second).stream().map(Object::toString).reduce((o, o2) -> o + " OR " + o2).orElse("");
			else if (second instanceof String && conditions.get("=").equals(f)) {
				second = "\"".concat(second.toString()).concat("\"");
			}

			final String expresion = f.apply(first, second);

			if (Collections.emptyList() != collector)
				collector.add(expresion);

			return expresion;

		} catch (Exception e) {
			collector.add(new RuntimeException("Error parsing :where clauses " + e).getMessage());
			return "error";
		}
	}

	private static String validateFieldAndGetPrefix(String column, Class clazz) throws Exception {
		String[] rez = column.split("/");

		Class c;
		String f;
		if (rez.length > 1) {
			c = Class.forName("ro.esolutions.rp." + rez[0].toLowerCase() + ".domain." + camelCase(rez[0]));
			f = rez[1];
		} else {
			c = clazz;
			f = column;
		}

		if ("correlationId".equals(f))
			c = c.getSuperclass();
		if ("aggregateId".equals(f)) {
			return "id";
		}

		Class t = c.getDeclaredField(f).getType();
		return f.concat(stringMap.getOrDefault(t, "_t"));
	}

	private static String camelCase(String str) {
		StringBuilder builder = new StringBuilder(str);
		// Flag to keep track if last visited character is a
		// white space or not
		boolean isLastSpace = true;

		// Iterate String from beginning to end.
		for (int i = 0; i < builder.length(); i++) {
			char ch = builder.charAt(i);

			if (isLastSpace && ch >= 'a' && ch <= 'z') {
				// Character need to be converted to uppercase
				builder.setCharAt(i, (char) (ch + ('A' - 'a')));
				isLastSpace = false;
			} else if (ch != ' ') {
				isLastSpace = false;
			} else {
				isLastSpace = true;
			}
		}

		return builder.toString();
	}
}
