package com.ft.api.util.transactionid;

/**
 * Anything that supports a transaction id (e.g. service classes, using HttpClients to communicate to other systems).
 */
public interface TransactionIdSupport {

    void setTransactionId(String transactionId);
}
