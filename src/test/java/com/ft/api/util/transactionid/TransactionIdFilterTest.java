package com.ft.api.util.transactionid;

import static org.mockito.Matchers.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.hamcrest.Matchers;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by vaishakh.nadgir on 05/02/14.
 */
@RunWith(MockitoJUnitRunner.class)
public class TransactionIdFilterTest {

    private static final String EMPTY_STRING = "";
    public static final String WHITESPACE = " ";
    public static final String SAMPLE_TRANSACTION_ID = "Testing";

    @Mock private HttpServletRequest servletRequest;
    @Mock private HttpServletResponse servletResponse;
    @Mock private FilterChain filterChain;

    private TransactionIdFilter transactionIdFilter = new TransactionIdFilter();

    @Test
    public void whenEmptyRequestIdIsPassedANewOneisMinted() throws IOException, ServletException {
        when(servletRequest.getHeader(TransactionIdUtils.TRANSACTION_ID_HEADER)).thenReturn(EMPTY_STRING);

        transactionIdFilter.doFilter(servletRequest, servletResponse, filterChain);

        verify(servletResponse).setHeader(argThat(is(TransactionIdUtils.TRANSACTION_ID_HEADER)), argThat(not(isOneOf(EMPTY_STRING, WHITESPACE))));

    }

    @Test
    public void whenWhitespaceIsPassedInRequestIdANewOneisMinted()throws IOException, ServletException {
        Mockito.when(servletRequest.getHeader(TransactionIdUtils.TRANSACTION_ID_HEADER)).thenReturn(WHITESPACE);

        transactionIdFilter.doFilter(servletRequest, servletResponse, filterChain);

        verify(servletResponse).setHeader(argThat(is(TransactionIdUtils.TRANSACTION_ID_HEADER)), argThat(not(isOneOf(EMPTY_STRING, WHITESPACE))));
    }

    @Test
    public void whenRequestIdIsNotPassedANewOneisMinted()throws IOException, ServletException {
        Mockito.when(servletRequest.getHeader(TransactionIdUtils.TRANSACTION_ID_HEADER)).thenReturn(null);

        transactionIdFilter.doFilter(servletRequest, servletResponse, filterChain);

        verify(servletResponse).setHeader(argThat(is(TransactionIdUtils.TRANSACTION_ID_HEADER)), argThat(not(isOneOf(EMPTY_STRING, WHITESPACE))));
    }

    @Test
    public void whenValidRequestIdIsPassedTheSameIdIsReused()throws IOException, ServletException {
        Mockito.when(servletRequest.getHeader(TransactionIdUtils.TRANSACTION_ID_HEADER)).thenReturn(SAMPLE_TRANSACTION_ID);

        transactionIdFilter.doFilter(servletRequest, servletResponse, filterChain);

        verify(servletResponse).setHeader(eq(TransactionIdUtils.TRANSACTION_ID_HEADER), eq(SAMPLE_TRANSACTION_ID));
    }
}
