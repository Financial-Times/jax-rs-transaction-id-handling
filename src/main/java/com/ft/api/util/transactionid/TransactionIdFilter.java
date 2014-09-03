package com.ft.api.util.transactionid;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

public class TransactionIdFilter implements Filter {

	private static final Logger LOGGER = LoggerFactory.getLogger(TransactionIdFilter.class);

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
		AdditionalHeadersHttpServletRequestWrapper requestWithTransactionId = new AdditionalHeadersHttpServletRequestWrapper(httpServletRequest);
		String transactionId = ensureTransactionIdIsPresent(requestWithTransactionId);

        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        httpServletResponse.setHeader(TransactionIdUtils.TRANSACTION_ID_HEADER, transactionId);

        MDC.put("transaction_id", "transaction_id=" + transactionId);
		LOGGER.info("[REQUEST RECEIVED] uri={}", httpServletRequest.getPathInfo());

		long startTime = System.currentTimeMillis();
		boolean success = false;
		try {
        	filterChain.doFilter(requestWithTransactionId, httpServletResponse);
			success = true;
		} finally {
			long endTime = System.currentTimeMillis();
			long timeTakenMillis = (endTime - startTime);

			LOGGER.info("[REQUEST HANDLED] uri={} time_ms={} status={} exception_was_thrown={}",
					httpServletRequest.getPathInfo(), timeTakenMillis, httpServletResponse.getStatus(), !success);

			MDC.remove("transaction_id");
		}
	}

	private String ensureTransactionIdIsPresent(AdditionalHeadersHttpServletRequestWrapper request) {
		String transactionId = request.getHeader(TransactionIdUtils.TRANSACTION_ID_HEADER);
		if (isTransactionIdProvided(transactionId)) {
			LOGGER.warn("Transaction ID ({} header) not provided. It will be generated.", TransactionIdUtils.TRANSACTION_ID_HEADER);
			transactionId = TransactionIdUtils.generateTransactionId();

			request.addHeader(TransactionIdUtils.TRANSACTION_ID_HEADER, transactionId);
		}
		return transactionId;
	}

	private boolean isTransactionIdProvided(String transactionId) {
		return StringUtils.isEmpty(transactionId) || transactionId.trim().isEmpty();
	}

	@Override
	public void destroy() {
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}
}
