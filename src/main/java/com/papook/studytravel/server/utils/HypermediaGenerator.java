package com.papook.studytravel.server.utils;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Responsible for building hypermedia links.
 *
 * @author papook
 */
@Component
public class HypermediaGenerator {

    @Autowired
    private HttpServletRequest servletRequest;

    public static String formatLinkHeader(URI uri, String rel) {
        StringBuilder linkHeader = new StringBuilder();
        String uriString = uri.toString();

        String formattedLinkHeader = linkHeader.append('<')
                .append(uriString)
                .append('>')
                .append("; rel=\"")
                .append(rel)
                .append('\"').toString();

        return formattedLinkHeader;
    }

    /**
     * Builds hypermedia links for pagination. The links include relations for the
     * current (self), previous, and next pages.
     * 
     * @param page Page object of results.
     * @return HttpHeaders object containing the formatted link headers.
     */
    public HttpHeaders buildPagingLinksHeaders(Page<?> page) {
        // Initialize a map to hold the paging links
        Map<String, String> links = new HashMap<>();

        // Get the current request URL
        StringBuffer requestURL = servletRequest.getRequestURL();
        // Get the query string from the current request
        String queryString = servletRequest.getQueryString();

        // Append the query string to the request URL if it exists
        if (queryString != null) {
            requestURL.append('?').append(queryString);
        }

        // Create a URI object from the request URL
        URI requestURI = URI.create(requestURL.toString());
        // Build URI components from the request URI
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(requestURI);

        // Get the current page index (zero-based)
        int selfPageIndex = Math.max(0, page.getNumber());
        // Build the URI string for the current (self) page
        String selfURIString = uriBuilder.replaceQueryParam("page", selfPageIndex)
                .build()
                .toUriString();
        links.put("self", selfURIString);

        // If the current page has a previous page and there are multiple pages
        if (page.hasPrevious() && page.getTotalPages() > 0) {
            // Get the index for the previous page
            int prevPageIndex = page.previousOrFirstPageable().getPageNumber();
            // Get the index for the last page
            int lastPageIndex = page.getTotalPages() - 1;
            // Ensure the previous page index does not exceed the last page index
            prevPageIndex = Math.min(lastPageIndex, prevPageIndex);

            // Build the URI for the previous page
            String prevURIString = uriBuilder.replaceQueryParam("page", prevPageIndex)
                    .build()
                    .toUriString();
            links.put("prev", prevURIString);
        }

        // If the current page has a next page
        if (page.hasNext()) {
            // Get the index for the next page
            int nextPageIndex = page.nextOrLastPageable().getPageNumber();
            // Ensure the next page index is not less than the first page index
            int firstPageIndex = 0;
            nextPageIndex = Math.max(firstPageIndex, nextPageIndex);

            // Build the URI for the next page
            String nextURI = uriBuilder.replaceQueryParam("page", nextPageIndex)
                    .build()
                    .toUriString();
            links.put("next", nextURI);
        }

        // Convert the map of links to an array of link headers
        List<String> linksArray = buildLinkHeaders(links);

        // Create a new HttpHeaders object to hold the link headers
        HttpHeaders headers = new HttpHeaders();
        // Add the link headers to the HttpHeaders object
        headers.addAll("Link", linksArray);

        return headers;
    }

    /**
     * Converts a map of links to an array of link headers.
     *
     * @param links A map containing link relations (e.g., self, prev, next) and
     *              their corresponding URIs.
     * @return An array of link headers.
     */
    private List<String> buildLinkHeaders(Map<String, String> links) {
        return links.entrySet().stream()
                .map(entry -> String.format("<%s>; rel=\"%s\"", entry.getValue(), entry.getKey()))
                .toList();
    }

}
