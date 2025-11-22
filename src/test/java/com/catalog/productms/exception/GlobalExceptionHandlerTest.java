package com.catalog.productms.exception;

import com.catalog.productms.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleProductNotFound_ShouldReturn404WithNoBody() {
        ProductNotFoundException exception = new ProductNotFoundException("123");

        ResponseEntity<Void> response = exceptionHandler.handleProductNotFound(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void handleProductAlreadyExists_ShouldReturn400WithErrorMessage() {
        ProductAlreadyExistsException exception = new ProductAlreadyExistsException("Product already exists");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleProductAlreadyExists(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatusCode());
        assertEquals("Product already exists", response.getBody().getMessage());
    }

    @Test
    void handleValidationException_ShouldReturn400WithFieldErrors() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("product", "name", "Name is required");
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Name is required"));
    }

    @Test
    void handleHttpMessageNotReadable_ShouldReturn400() {
        HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleHttpMessageNotReadable(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Invalid request format"));
    }

    @Test
    void handleTypeMismatch_ShouldReturn400WithParameterName() {
        MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);
        when(exception.getName()).thenReturn("page");
        when(exception.getRequiredType()).thenReturn((Class) Integer.class);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleTypeMismatch(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatusCode());
        assertTrue(response.getBody().getMessage().contains("page"));
        assertTrue(response.getBody().getMessage().contains("Integer"));
    }

    @Test
    void handleMissingParameter_ShouldReturn400() {
        MissingServletRequestParameterException exception = 
            new MissingServletRequestParameterException("size", "Integer");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleMissingParameter(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatusCode());
        assertTrue(response.getBody().getMessage().contains("size"));
    }

    @Test
    void handleMethodNotSupported_ShouldReturn405() {
        HttpRequestMethodNotSupportedException exception = 
            new HttpRequestMethodNotSupportedException("PATCH");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleMethodNotSupported(exception);

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(405, response.getBody().getStatusCode());
        assertTrue(response.getBody().getMessage().contains("PATCH"));
    }

    @Test
    void handleMediaTypeNotSupported_ShouldReturn415() {
        HttpMediaTypeNotSupportedException exception = mock(HttpMediaTypeNotSupportedException.class);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleMediaTypeNotSupported(exception);

        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(415, response.getBody().getStatusCode());
        assertTrue(response.getBody().getMessage().contains("application/json"));
    }

    @Test
    void handleNoResourceFound_ShouldReturn404() {
        NoResourceFoundException exception = mock(NoResourceFoundException.class);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleNoResourceFound(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatusCode());
        assertTrue(response.getBody().getMessage().contains("endpoint does not exist"));
    }

    @Test
    void handleDataIntegrityViolation_ShouldReturn400() {
        DataIntegrityViolationException exception = mock(DataIntegrityViolationException.class);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleDataIntegrityViolation(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Data integrity violation"));
    }

    @Test
    void handleGenericException_ShouldReturn500() {
        Exception exception = new Exception("Unexpected error");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatusCode());
        assertEquals("An unexpected error occurred", response.getBody().getMessage());
    }
}

