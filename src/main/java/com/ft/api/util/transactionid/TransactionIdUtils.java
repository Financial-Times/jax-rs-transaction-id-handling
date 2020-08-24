package com.ft.api.util.transactionid;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;

import com.ft.membership.logging.Operation;

import javax.ws.rs.core.HttpHeaders;

import static com.ft.membership.logging.Operation.operation;

import java.util.List;

public class TransactionIdUtils {

	public static final String TRANSACTION_ID_HEADER = "X-Request-Id";
	public static final String TRANSACTION_ID_MDC_KEY = "transaction_id";
	public static final Operation operationJson = operation("getTransactionIdOrDie").jsonLayout()
		.initiate(TransactionIdUtils.class); 
	
	public static String generateTransactionId() {
		return "tid_" + randomChars(10);
	}

	private static String randomChars(int howMany) {
		return RandomStringUtils.randomAlphanumeric(howMany).toLowerCase();
	}

    public static String getTransactionIdOrDie(HttpHeaders httpHeaders) {
		String transactionId = getHeaderValue(httpHeaders, TRANSACTION_ID_HEADER);
		if (StringUtils.isEmpty(transactionId)) {
			operationJson.logIntermediate()
				.yielding(TRANSACTION_ID_MDC_KEY, transactionId)
				.yielding("msg", "Transaction ID (" + TRANSACTION_ID_HEADER + " header) not found.")
				.logError();

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
