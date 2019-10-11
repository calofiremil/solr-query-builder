package ro.esolutions.rp.solr;

import java.util.ArrayList;
import java.util.List;

public class SolrList<T> {
    private long numFound = 0;
    private long start = 0;
    private List<T> content = new ArrayList<>();

    public long getNumFound() {
        return numFound;
    }

    public SolrList<T> setNumFound(final long numFound) {
        this.numFound = numFound;
        return this;
    }

    public long getStart() {
        return start;
    }

    public SolrList<T> setStart(final long start) {
        this.start = start;
        return this;
    }

    public List<T> getContent() {
        return content;
    }

    public SolrList<T> setContent(final List<T> content) {
        this.content = content;
        return this;
    }
}
