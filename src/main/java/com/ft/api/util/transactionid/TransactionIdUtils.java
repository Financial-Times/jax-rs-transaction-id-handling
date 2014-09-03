package com.ft.api.util.transactionid;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.HttpHeaders;
import java.util.List;

public class TransactionIdUtils {

	public static final String TRANSACTION_ID_HEADER = "X-Request-Id";

	private static final Logger LOGGER = LoggerFactory.getLogger(TransactionIdUtils.class);

	public static String generateTransactionId() {
		return "tid_" + randomChars(10);
	}

	private static String randomChars(int howMany) {
		return RandomStringUtils.randomAlphanumeric(howMany).toLowerCase();
	}

    public static String getTransactionIdOrDie(HttpHeaders httpHeaders) {
		String transactionId = getHeaderValue(httpHeaders, TRANSACTION_ID_HEADER);
		if (StringUtils.isEmpty(transactionId)) {
			LOGGER.error("Transaction ID ({} header) not found.", TRANSACTION_ID_HEADER);
			throw new IllegalStateException("Transaction ID not found.");
		} else {
			return transactionId;
		}
	}

	private static String getHeaderValue(HttpHeaders httpHeaders, String headerName) {
		List<String> headerValues = httpHeaders.getRequestHeader(headerName);
		if (headerValues == null || headerValues.isEmpty()) {
			return null;
		} else {
			return headerValues.get(0);
		}
	}
}
