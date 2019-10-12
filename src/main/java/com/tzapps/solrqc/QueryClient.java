package com.tzapps.solrqc;

import lombok.extern.slf4j.Slf4j;

import java.util.*;


@Slf4j
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

  //  private final String queryServiceUrl;

    //private static final HashMap<Class, ParameterizedTypeReference> paramTypeRefMap = new HashMap<>();
/*
    public QueryClient(@Value("${query.service.url}") final String queryServiceUrl) {
        this.queryServiceUrl = queryServiceUrl;
    }
*/
    public <U> U query(final ArrayList<Object> query) {
/*
        final HttpEntity httpEntity = new HttpEntity<>(query, new HttpHeaders());

        try {
            final ResponseEntity<U> responseEntity =
                    restTemplate.exchange(queryServiceUrl + "/custom-query", HttpMethod.POST, httpEntity, paramTypeRefMap.get(query.get(1)));

            if (HttpStatus.OK.equals(responseEntity.getStatusCode())) {
                return responseEntity.hasBody() ? responseEntity.getBody() : (U) Collections.emptyList();
            }
        } catch (final HttpClientErrorException e) {
            log.error("An error occurred for getting {} , error = {}", query.get(1), e);
        }*/
        return (U) Collections.emptyList();
    }

    public static ArrayList<Object> s(final Object... args) {
        return new ArrayList(Arrays.asList(args));
    }

    public static String id(final Class aggClass, final String aggregateId) {
        return aggClass.getName().concat("/").concat(aggregateId);
    }
/*
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
    }*/
}
