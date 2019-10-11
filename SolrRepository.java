package ro.esolutions.rp.solr;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ro.esolutions.rp.solr.SortUtils.toFilterQuery;
import static ro.esolutions.rp.solr.SortUtils.toSolrField;

@Slf4j
public class SolrRepository {

	private final String solrCatalog;
	private final int solrCommitWithinMs;
	private final SolrClient solrClient;
	private final ObjectMapper objectMapper;

	public SolrRepository(String solrCatalog, int solrCommitWithinMs, SolrClient solrClient, ObjectMapper objectMapper) {
		this.solrCatalog = solrCatalog;
		this.solrCommitWithinMs = solrCommitWithinMs;
		this.solrClient = solrClient;
		this.objectMapper = objectMapper;
	}

	public SolrList customQuery(final Map<String, Object> qs) {

		SolrQuery query = new SolrQuery(qs.get("q").toString())
				.setStart(calculateOffset((Integer) qs.get("start"), (Integer) qs.get("rows")))
				.setRows((Integer) qs.get("rows"));

		if(qs.containsKey("fl")) {
			String fl = (String) qs.getOrDefault("fl", "");
			query.addField(fl);
		}

		((List) qs.getOrDefault("fq", Collections.emptyList())).forEach(filterQuery -> query.addFilterQuery(filterQuery.toString()));
		((List) qs.getOrDefault("sort", Collections.emptyList())).forEach(sortCondition -> query.addSort(calculateSort(sortCondition.toString())));

		query.addFilterQuery("deleted_b: false");
		log.info(query.toString());
		return query(query, (Class) qs.get("entity"));
	}

	public Integer calculateOffset(Integer pageNumber, Integer pageSize) {
		return pageNumber * pageSize;
	}

	public SolrQuery.SortClause calculateSort(final String sortCondition) {
		Object[] r = sortCondition.split("\\s");
		return new SolrQuery.SortClause(r[0].toString(),
				"asc".equals(r[1]) ? SolrQuery.ORDER.asc : SolrQuery.ORDER.desc);
	}

	public <T> SolrList<T> fromQueryRequest(final QueryRequest queryRequest, Class<T> clazz) throws NoSuchFieldException {

		SolrQuery query = new SolrQuery("clazz:\"" + clazz.getName() + "\"")
				.setStart(queryRequest.getPageRequest().calculateOffset())
				.setRows(queryRequest.getPageRequest().getPageSize());

		for (QueryCondition condition : queryRequest.getConditions()) {
			query.addFilterQuery(toFilterQuery(condition, clazz));
		}

		for (QuerySort sort : queryRequest.getSortList()) {
			query.addSort(toSolrField(sort.getColumnName(), clazz), sort.getSortDirection());
		}

		query.addFilterQuery("deleted_b: false");
		return query(query, clazz);
	}

	public <T> SolrList<T> getAll(Class<T> clazz) {
		SolrQuery query = new SolrQuery("clazz:\"" + clazz.getName() + "\"");
		return query(query, clazz);
	}

	public <T> SolrList<T> getById(final String id, final Class<T> clazz) {

		SolrQuery solrQuery = new SolrQuery("clazz:\"" + clazz.getName() + "\"")
				.addFilterQuery("id:" + clazz.getName() + "/" + id);

		return query(solrQuery, clazz);
	}

	public <T> SolrList<T> query(final SolrQuery solrQuery, Class<T> clazz) {
		try {
			final QueryResponse response = solrClient.query(solrCatalog, solrQuery);
			final SolrDocumentList documentList = response.getResults();
			List<T> list = documentList.stream()
					.map(document -> {
						final String documentValue = (String) document.getFieldValue("json_document");
						return toMapConvertor(documentValue, clazz);
					})
					.collect(Collectors.toList());
			return new SolrList<T>()
					.setContent(list)
					.setNumFound(documentList.getNumFound())
					.setStart(documentList.getStart());
		} catch (final SolrServerException | IOException e) {
			log.error("Failed to query the solr catalog! Query used: {}", solrQuery.toQueryString());
			return new SolrList<T>().setContent(new ArrayList<>());
		}
	}

	private <T> T toMapConvertor(String documentValue, Class<T> clazz) {
		try {
			return objectMapper.readValue(documentValue, clazz);
		} catch (final IOException e) {
			log.error("Object mapper failed to convert documentValue to " + clazz);
			throw new RuntimeException(e);
		}
	}

	public <T> void save(IndexedDocument<T> indexedDocument) {
		log.info("save: " + indexedDocument);
		try {
			solrClient.add(solrCatalog, indexedDocument.solrInputDocument(objectMapper), solrCommitWithinMs);
		} catch (final IOException ex) {
			log.error("Failed to add document. Failed to communicate with SOLR server", ex);
		} catch (final SolrServerException ex) {
			log.error("Failed to add document. SOLR internal server error", ex);
		}
	}
}
