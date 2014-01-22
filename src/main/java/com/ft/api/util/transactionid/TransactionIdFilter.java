package com.ft.api.util.transactionid;

import org.apache.commons.lang.RandomStringUtils;
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
        filterChain.doFilter(requestWithTransactionId, servletResponse);
        MDC.remove("transaction_id");
	}

	private String ensureTransactionIdIsPresent(AdditionalHeadersHttpServletRequestWrapper request) {
		String transactionId = request.getHeader(TransactionIdUtils.TRANSACTION_ID_HEADER);
		if (StringUtils.isEmpty(transactionId)) {
			LOGGER.warn("Transaction ID ({} header) not provided. It will be generated.", TransactionIdUtils.TRANSACTION_ID_HEADER);
			transactionId = generateTransactionId();

			request.addHeader(TransactionIdUtils.TRANSACTION_ID_HEADER, transactionId);
		}

		LOGGER.info("message=\"Publish request.\" ");
		return transactionId;
	}

	private String generateTransactionId() {
		return "tid_" + randomChars(10);
	}

	private String randomChars(int howMany) {
		return RandomStringUtils.randomAlphanumeric(howMany).toLowerCase();
	}

	@Override
	public void destroy() {
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}
}
