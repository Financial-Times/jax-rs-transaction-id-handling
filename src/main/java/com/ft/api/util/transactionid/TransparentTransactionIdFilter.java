package com.ft.api.util.transactionid;

import static com.ft.api.util.transactionid.TransactionIdUtils.TRANSACTION_ID_HEADER;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

/**
 * Extracts transaction id from existing transaction id header and sets it to each of the given TransactionIdSupport. If
 * header is not present null will be set to each of the given TransactionIdSupport.
 *
 * Benefit:
 *
 * Transaction ids do NOT have to be extracted in each and every resource method (endpoint). They do NOT have to be
 * passed down to all service classes through all layers as additional parameters. They do NOT have to be added to each
 * and every request right next to the business logic if TransactionIdSupport is used in a smart way (e.g. wrapping the
 * client into a class that implements TransactionIdSupport and letting the service use that wrapped client instead).
 *
 * How it works:
 *
 * 1) TransactionIdFilter adds transaction id header if missing
 * 2) TransparentTransactionIdFilter extracts transaction id from header and sets it to all service classes and/or
 *    clients, used for outgoing requests. The service classes and/or clients implement TransactionIdSupport and
 *    create a transaction id header before sending the request.
 *
 * Check content-by-concept-api for reference usage: http://git.svc.ft.com/projects/CP/repos/content-by-concept-api/browse
 */
public class TransparentTransactionIdFilter implements Filter {

    private final List<TransactionIdSupport> transactionIdSupportList;

    public TransparentTransactionIdFilter(TransactionIdSupport... transactionIdSupportList) {
        this.transactionIdSupportList = unmodifiableList(new ArrayList<>(asList(requireNonNull(transactionIdSupportList))));
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String transactionId = httpServletRequest.getHeader(TRANSACTION_ID_HEADER);

        for (TransactionIdSupport transactionIdSupport : transactionIdSupportList) {
            transactionIdSupport.setTransactionId(transactionId);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // do nothing here
    }

    @Override
    public void destroy() {
        // do nothing here
    }
}
