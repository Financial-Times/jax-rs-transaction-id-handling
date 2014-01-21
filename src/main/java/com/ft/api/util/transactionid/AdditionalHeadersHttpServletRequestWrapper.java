package com.ft.api.util.transactionid;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.*;

class AdditionalHeadersHttpServletRequestWrapper extends HttpServletRequestWrapper {

	final private Map<String, String> additionalHeaders = new HashMap<>();

	public AdditionalHeadersHttpServletRequestWrapper(HttpServletRequest request) {
		super(request);
	}

	public void addHeader(String name, String value) {
		additionalHeaders.put(name, value);
	}

	@Override
	public String getHeader(String name) {
		String header = super.getHeader(name);
		return (header != null) ? header : additionalHeaders.get(name);
	}

	@Override
	public Enumeration<String> getHeaderNames() {
		List<String> names = Collections.list(super.getHeaderNames());
		names.addAll(additionalHeaders.keySet());
		return Collections.enumeration(names);
	}

	@Override
	public Enumeration<String> getHeaders(String name) {
		Enumeration<String> headers = super.getHeaders(name);
		if (headers != null && headers.hasMoreElements()) {
			return headers;
		} else {
			return Collections.enumeration(Collections.singleton(additionalHeaders.get(name)));
		}
	}
}
