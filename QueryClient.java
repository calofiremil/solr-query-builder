package ro.esolutions.rp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ro.esolutions.rp.aco.domain.Aco;
import ro.esolutions.rp.acoposter.domain.AcoPoster;
import ro.esolutions.rp.acoproductshopquantity.domain.AcoProductShopQuantity;
import ro.esolutions.rp.acotype.domain.AcoType;
import ro.esolutions.rp.campaign.domain.Campaign;
import ro.esolutions.rp.flow.domain.Flow;
import ro.esolutions.rp.marketingresponsible.domain.MarketingResponsible;
import ro.esolutions.rp.posterimage.domain.PosterImage;
import ro.esolutions.rp.productreleve.domain.ProductReleve;
import ro.esolutions.rp.promotiontype.domain.PromotionType;
import ro.esolutions.rp.publicitytype.domain.PublicityType;
import ro.esolutions.rp.task.domain.Task;

import java.util.*;


@Slf4j
@Repository
public class QueryClient {

	public static final String FIND = ":find";
	public static final String COUNT = ":count";
	public static final String WHERE = ":where";
	public static final String AND = ":and";
	public static final String OR = ":or";
	public static final String ORDER_BY = ":sort";

	public static final String ASC = "asc";
	public static final String DESC = "desc";

	private final RestTemplate restTemplate = new RestTemplate();

	private final String queryServiceUrl;

	private static final HashMap<Class, ParameterizedTypeReference> paramTypeRefMap = new HashMap<>();

	public QueryClient(@Value("${query.service.url}") final String queryServiceUrl) {
		this.queryServiceUrl = queryServiceUrl;
	}

	public <U> U query(final ArrayList<Object> query) {

		final HttpEntity httpEntity = new HttpEntity<>(query, new HttpHeaders());

		try {
			final ResponseEntity<U> responseEntity =
					restTemplate.exchange(queryServiceUrl +  "/custom-query", HttpMethod.POST, httpEntity, paramTypeRefMap.get(query.get(1)));

			if (HttpStatus.OK.equals(responseEntity.getStatusCode())) {
				return responseEntity.hasBody() ? responseEntity.getBody() : (U) Collections.emptyList();
			}
		} catch (final HttpClientErrorException e) {
			log.error("An error occurred for getting {} , error = {}", query.get(1), e);
		}
		return (U) Collections.emptyList();
	}

	public static ArrayList<Object> s(final Object... args) {
		return new ArrayList(Arrays.asList(args));
	}

	public static String id(final Class aggClass, final String aggregateId) {
		return aggClass.getName().concat("/").concat(aggregateId);
	}

	static {
		paramTypeRefMap.put(MarketingResponsible.class, new ParameterizedTypeReference<List<MarketingResponsible>>() {
		});
		paramTypeRefMap.put(Aco.class, new ParameterizedTypeReference<List<Aco>>() {
		});
		paramTypeRefMap.put(PublicityType.class, new ParameterizedTypeReference<List<PublicityType>>() {
		});
		paramTypeRefMap.put(PromotionType.class, new ParameterizedTypeReference<List<PromotionType>>() {
		});
		paramTypeRefMap.put(AcoType.class, new ParameterizedTypeReference<List<AcoType>>() {
		});
		paramTypeRefMap.put(Campaign.class, new ParameterizedTypeReference<List<Campaign>>() {
		});
		paramTypeRefMap.put(Flow.class, new ParameterizedTypeReference<List<Flow>>() {
		});
		paramTypeRefMap.put(Task.class, new ParameterizedTypeReference<List<Task>>() {
		});
		paramTypeRefMap.put(AcoPoster.class, new ParameterizedTypeReference<List<AcoPoster>>() {
		});
		paramTypeRefMap.put(PosterImage.class, new ParameterizedTypeReference<List<PosterImage>>() {
		});
		paramTypeRefMap.put(ProductReleve.class, new ParameterizedTypeReference<List<ProductReleve>>() {
		});
		paramTypeRefMap.put(AcoProductShopQuantity.class, new ParameterizedTypeReference<List<AcoProductShopQuantity>>() {
		});
	}
}
