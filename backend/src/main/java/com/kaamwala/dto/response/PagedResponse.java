package com.kaamwala.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Generic paged response wrapper for paginated endpoints.
 *
 * @param <T> the type of items in the page
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse<T> {

    /** The items in the current page. */
    private List<T> content;

    /** Current page number (0-based). */
    private int page;

    /** Number of items per page. */
    private int size;

    /** Total number of items across all pages. */
    private long totalElements;

    /** Total number of pages. */
    private int totalPages;

    /** Whether this is the last page. */
    private boolean last;
}
