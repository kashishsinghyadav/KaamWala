package com.kaamwala.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic API response wrapper used across all endpoints.
 *
 * @param <T> the type of the response data payload
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /** Whether the operation was successful. */
    private boolean success;

    /** Human-readable message describing the result. */
    private String message;

    /** The response data payload. */
    private T data;

    /**
     * Create a successful response with data.
     *
     * @param data    the response payload
     * @param message the success message
     * @param <T>     the data type
     * @return a successful ApiResponse
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Create a successful response with data and default message.
     *
     * @param data the response payload
     * @param <T>  the data type
     * @return a successful ApiResponse
     */
    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Operation successful");
    }

    /**
     * Create an error response.
     *
     * @param message the error message
     * @param <T>     the data type
     * @return an error ApiResponse
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .build();
    }
}
