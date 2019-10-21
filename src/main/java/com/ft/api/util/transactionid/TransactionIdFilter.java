package com.ft.api.util.transactionid;

import static com.ft.membership.logging.Operation.operation;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.MDC;

import com.ft.membership.logging.Operation;

public class TransactionIdFilter implements Filter {

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
		AdditionalHeadersHttpServletRequestWrapper requestWithTransactionId = new AdditionalHeadersHttpServletRequestWrapper(httpServletRequest);
		String transactionId = ensureTransactionIdIsPresent(requestWithTransactionId);

        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        httpServletResponse.setHeader(TransactionIdUtils.TRANSACTION_ID_HEADER, transactionId);

        MDC.put("transaction_id", "transaction_id=" + transactionId);
		final Operation operationJson = operation("doFilter").jsonLayout()
			.initiate(this);
        
		long startTime = System.currentTimeMillis();
		boolean success = false;
		try {
        	filterChain.doFilter(requestWithTransactionId, httpServletResponse);
			success = true;
		} finally {
			long endTime = System.currentTimeMillis();
			long timeTakenMillis = (endTime - startTime);

			operationJson.logIntermediate()
				.yielding("msg", "REQUEST HANDLED")
				.yielding("transaction_id", transactionId)
				.yielding("responsetime", timeTakenMillis)
				.yielding("protocol", httpServletRequest.getProtocol())
				.yielding("uri", httpServletRequest.getRequestURI())
				.yielding("path", httpServletRequest.getPathInfo())
				.yielding("method", httpServletRequest.getMethod())
				.yielding("status", httpServletResponse.getStatus())
				.yielding("content_type", httpServletResponse.getContentType())
				.yielding("size", httpServletResponse.getBufferSize())
				.yielding("host", httpServletRequest.getRemoteHost())
				.yielding("userAgent", httpServletRequest.getRemoteUser())
				.yielding("exception_was_thrown", !success)
				.logInfo();
			MDC.remove("transaction_id");
		}
	}

	private String ensureTransactionIdIsPresent(AdditionalHeadersHttpServletRequestWrapper request) {
		String transactionId = request.getHeader(TransactionIdUtils.TRANSACTION_ID_HEADER);
		if (isTransactionIdProvided(transactionId)) {
			transactionId = TransactionIdUtils.generateTransactionId();
			final Operation operationJson = operation("ensureTransactionIdIsPresent").jsonLayout()
				.initiate(this);

			operationJson.logIntermediate()
				.yielding("transaction_id", transactionId)
				.yielding("msg", "Transaction ID (" + TransactionIdUtils.TRANSACTION_ID_HEADER + " header) not provided. It was generated: " + transactionId)
				.logWarn();

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
