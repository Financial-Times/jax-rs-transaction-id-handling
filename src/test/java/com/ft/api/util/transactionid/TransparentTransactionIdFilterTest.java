package com.ft.api.util.transactionid;

import static com.ft.api.util.transactionid.TransactionIdUtils.TRANSACTION_ID_HEADER;
import static org.mockito.Mockito.*;

import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.junit.Test;

public class TransparentTransactionIdFilterTest {

    private TransactionIdSupport transactionIdSupport1 = mock(TransactionIdSupport.class);
    private TransactionIdSupport transactionIdSupport2 = mock(TransactionIdSupport.class);
    private HttpServletRequest servletRequest = mock(HttpServletRequest.class);

    private TransparentTransactionIdFilter filter = new TransparentTransactionIdFilter(transactionIdSupport1, transactionIdSupport2);


    @Test
    public void setsTransactionIdToSupportersIfTransactionIdHeaderIsPresent() throws Exception {
        when(servletRequest.getHeader(TRANSACTION_ID_HEADER)).thenReturn("a_transaction_id");

        filter.doFilter(servletRequest, mock(ServletResponse.class), mock(FilterChain.class));

        verify(transactionIdSupport1).setTransactionId("a_transaction_id");
        verify(transactionIdSupport2).setTransactionId("a_transaction_id");
    }

    @Test
    public void setsNullToSupportIfTransactionIdHeaderIsNotPresent() throws Exception {
        when(servletRequest.getHeader(TRANSACTION_ID_HEADER)).thenReturn(null);

        filter.doFilter(servletRequest, mock(ServletResponse.class), mock(FilterChain.class));

        verify(transactionIdSupport1).setTransactionId(null);
        verify(transactionIdSupport2).setTransactionId(null);
    }
}
