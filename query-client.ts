import {HttpClient} from "../aggregate/outsideDependencies";

/*This is an experimental service that will be expanded*/
const query = (query: Array<any>) => {
    return HttpClient.queryService.post("/custom-query/", query);
}

export const QueryClient = {
    query: query
};