package com.ft.api.util.transactionid;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Enumeration;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AdditionalHeadersHttpServletRequestWrapperTest {

    public static final Enumeration<String> TRANSACTION_ID_HEADERS = Collections.enumeration(Collections.singleton(TransactionIdUtils.TRANSACTION_ID_HEADER));
    public static final String WHITESPACE = " ";
    @Mock
    private HttpServletRequest servletRequest;
    private static final Enumeration<String> EMPTY_ENUMERATION = Collections.emptyEnumeration();

    @Test
    public void shouldReturnAdditionalHeaderWhenAddedToRequest(){
        AdditionalHeadersHttpServletRequestWrapper requestWrapper = new AdditionalHeadersHttpServletRequestWrapper(servletRequest);
        requestWrapper.addHeader(TransactionIdUtils.TRANSACTION_ID_HEADER, "testing");
        when(servletRequest.getHeaders(anyString())).thenReturn(EMPTY_ENUMERATION);
        when(servletRequest.getHeader(anyString())).thenReturn("");
        when(servletRequest.getHeaderNames()).thenReturn(EMPTY_ENUMERATION);
        Enumeration<String> headers = requestWrapper.getHeaders(TransactionIdUtils.TRANSACTION_ID_HEADER);
        assertThat(Collections.list(headers), hasItem("testing"));
    }

    @Test
    public void whenEmptyTrasactionIdIsProvidedShouldReturnTheNewlyGeneratedOne(){
        AdditionalHeadersHttpServletRequestWrapper requestWrapper = new AdditionalHeadersHttpServletRequestWrapper(servletRequest);
        requestWrapper.addHeader(TransactionIdUtils.TRANSACTION_ID_HEADER, "testing");

        when(servletRequest.getHeaders(TransactionIdUtils.TRANSACTION_ID_HEADER)).thenReturn(TRANSACTION_ID_HEADERS);
        when(servletRequest.getHeader(TransactionIdUtils.TRANSACTION_ID_HEADER)).thenReturn(WHITESPACE);
        when(servletRequest.getHeaderNames()).thenReturn(TRANSACTION_ID_HEADERS);
        Enumeration<String> headers = requestWrapper.getHeaders(TransactionIdUtils.TRANSACTION_ID_HEADER);
        assertThat(Collections.list(headers), hasItem("testing"));
        assertThat(requestWrapper.getHeader(TransactionIdUtils.TRANSACTION_ID_HEADER), is("testing"));
    }

}
