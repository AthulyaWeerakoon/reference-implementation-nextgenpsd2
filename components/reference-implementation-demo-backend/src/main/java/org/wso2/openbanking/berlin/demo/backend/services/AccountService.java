/**
 * Copyright (c) 2025, WSO2 LLC. (https://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.openbanking.berlin.demo.backend.services;

import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import static org.wso2.openbanking.berlin.demo.backend.utils.Constants.AccountService.CODE;
import static org.wso2.openbanking.berlin.demo.backend.utils.Constants.AccountService.MESSAGE;
import static org.wso2.openbanking.berlin.demo.backend.utils.Constants.AccountService.REQUEST_ID;
import static org.wso2.openbanking.berlin.demo.backend.utils.Constants.AccountService.VALID;
import static org.wso2.openbanking.berlin.demo.backend.utils.Utility.validateRequestHeader;

import static javax.ws.rs.core.Response.Status.FORBIDDEN;


/**
 * Mock backend containing read account data endpoints.
 */

@Path("/")
public class AccountService {

    private static final Log log = LogFactory.getLog(AccountService.class);
    private static final String MULTICURRENCY_ACCOUNT = "DE12345678901234567890";

    @OPTIONS
    @Path("/accounts")
    public Response optionsAuth(@HeaderParam("origin") String reqOrigin) {

        log.info("origin: " + reqOrigin);
        return getResponse("GET", reqOrigin);
    }

    @OPTIONS
    @Path("/accounts/accounts")
    public Response optionsAuth2(@HeaderParam("origin") String reqOrigin) {

        log.info("origin: " + reqOrigin);
        return getResponse("GET, POST", reqOrigin);
    }

    private static Response getResponse(String allowedHttpMethods, String reqOrigin) {
        return Response.ok()
                .header("Access-Control-Allow-Origin", reqOrigin)
                .header("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
                .header("Access-Control-Allow-Headers", "Content-Type, X-Token, X-Request-ID, Consent-ID, " +
                        "Origin, Accept, Authorization, Content-Range, Content-Disposition, Content-Description")
                .header("Cache-Control", "no-cache, no-store, must-revalidate, private")
                .header("X-Frame-Options", "DENY")
                .header("X-Content-Type-Options", "nosniff")
                .header("X-XSS-Protection", "1; mode=block")
                .build();
    }


    /**
     * Get list of bank accounts.
     *
     * @param requestID ID of the request.
     * @param consentID ID gotten after consent transaction.
     * @return account details.
     */
    @GET
    @Path("/accounts")
    @Produces("application/json")
    public Response getAccounts(@HeaderParam("X-Request-ID") String requestID,
                                @HeaderParam("Consent-ID") String consentID) {

        log.info("GET /accounts endpoint called.");
        //Example 1 : pg 98
        String response = "{\"accounts\":\n" +
                " [\n" +
                " {\"resourceId\": \"3dc3d5b3-7023-4848-9853-f5400a64e80f\",\n" +
                " \"iban\": \"DE2310010010123456789\",\n" +
                " \"currency\": \"EUR\",\n" +
                "\"product\": \"Wso3123\",\n" +
                " \"cashAccountType\": \"CurrentAccount\",\n" +
                " \"name\": \"Main Account\",\n" +
                " \"_links\": {\n" +
                "\"balances\": {\"href\": \"/accounts/3dc3d5b3-7023-4848-9853-" +
                "f5400a64e80f/balances\"},\n" +
                "\"transactions\": {\"href\": \"/accounts/3dc3d5b3-7023-4848-9853-" +
                "f5400a64e80f/transactions\"}}\n" +
                " },\n" +
                " {\"resourceId\": \"3dc3d5b3-7023-4848-9853-f5400a64e81g\",\n" +
                " \"iban\": \"DE2310010010123456788\",\n" +
                " \"currency\": \"USD\",\n" +
                " \"product\": \"Fremdwährungskonto\",\n" +
                " \"cashAccountType\": \"CurrentAccount\",\n" +
                " \"name\": \"US Dollar Account\",\n" +
                " \"_links\": {\n" +
                "\"balances\": {\"href\": \"/accounts/3dc3d5b3-7023-4848-9853-" +
                "f5400a64e81g/balances\" }}\n" +
                " },\n" +
                "{\"resourceId\": \"3dc3d5b3-7023-4848-9853-f5400a64e80f\",\n" +
                "\"iban\": \"DE12345678901234567890\",\n" +
                "\"currency\": \"XXX\",\n" +
                "\"product\": \"Multi currency account\",\n" +
                "\"cashAccountType\": \"CACC\",\n" +
                "\"name\": \"Aggregation Account\",\n" +
                "\"_links\": {\n" +
                "\"balances\": {\n" +
                "\"href\": \"/v1/accounts/3dc3d5b3-7023-4848-9853-f5400a64e333/balances\"\n" +
                "},\n" +
                "\"transactions\": {\n" +
                "\"href\": \"/v1/accounts/3dc3d5b3-7023-4848-9853-f5400a64e333/transactions\"\n" +
                "}\n" +
                "}\n" +
                "}, {\n" +
                "\"resourceId\": \"3dc3d5b3-7023-4848-9853-f5400a64e80f\",\n" +
                "\"iban\": \"DE12345678901234567890\",\n" +
                "\"currency\": \"EUR\",\n" +
                "\"product\": \"Girokonto\",\n" +
                "\"cashAccountType\": \"CACC\",\n" +
                "\"name\": \"Main Account\",\n" +
                "\"_links\": {\n" +
                "\"balances\": {\n" +
                "\"href\": \"/v1/accounts/3dc3d5b3-7023-4848-9853-f5400a64e80f/balances\"\n" +
                "},\n" +
                "\"transactions\": {\n" +
                "\"href\": \"/v1/accounts/3dc3d5b3-7023-4848-9853-f5400a64e80f/transactions\"\n" +
                "}\n" +
                "}\n" +
                "},\n" +
                "{\n" +
                "\"resourceId\": \"3dc3d5b3-7023-4848-9853-f5400a64e81g\",\n" +
                "\"iban\": \"DE12345678901234567890\",\n" +
                "\"currency\": \"USD\",\n" +
                "\"product\": \"Fremdwährungskonto\",\n" +
                "\"cashAccountType\": \"CACC\",\n" +
                "\"name\": \"US Dollar Account\",\n" +
                "\"_links\": {\n" +
                "\"balances\": {\n" +
                "\"href\": \"/v1/accounts/3dc3d5b3-7023-4848-9853-f5400a64e81g/balances\"\n" +
                "},\n" +
                "\"transactions\": {\n" +
                "\"href\": \"/v1/accounts/3dc3d5b3-7023-4848-9853-f5400a64e81g/transactions\"\n" +
                "}\n" +
                "}\n" +
                "}" +
                "]}";

        return Response.ok(response).header(REQUEST_ID, requestID)
                .header("Access-Control-Allow-Origin", "https://localhost:9446")
                .header("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
                .header("Access-Control-Allow-Headers", "Content-Type, X-Token, X-Request-ID, Consent-ID, " +
                        "Origin, Accept, Authorization, Content-Range, Content-Disposition, Content-Description")
                .build();
    }

    /**
     * Get details on an account.
     *
     * @param accountID ID of the bank account.
     * @param requestID ID of the request.
     * @param consentID ID gotten after consent transaction.
     * @return Account details.
     */
    @GET
    @Path("/accounts/{AccountId}")
    @Produces("application/json")
    public Response getAccount(@PathParam("AccountId") String accountID,
                               @HeaderParam("X-Request-ID") String requestID,
                               @HeaderParam("Consent-ID") String consentID) {

        log.info("GET /accounts/{AccountId} endpoint called.");
        JSONObject responseJSON = validateRequestHeader(requestID, consentID);
        if (!(VALID.equals(responseJSON.get(MESSAGE)))) {
            return Response.status(FORBIDDEN).entity(responseJSON.toJSONString()).build();
        }
        //Response body for a multi-currency account :pg 103
        String response = "{\"account\":\n" +
                " {\"resourceId\": \"3dc3d5b3-7023-4848-9853-f5400a64e81g\",\n" +
                " \"iban\": \"FR7612345987650123456789014\",\n" +
                " \"currency\": \"XXX\",\n" +
                " \"product\": \"Multicurrency Account\",\n" +
                " \"cashAccountType\": \"CurrentAccount\",\n" +
                " \"name\": \"Aggregation Account\",\n" +
                " \"_links\": {\n" +
                "\"balances\": {\"href\": \"/accounts/" + accountID +
                "/balances\"},\n" +
                "\"transactions\": {\"href\": \"/accounts/" + accountID +
                "/transactions\"}}\n" +
                " }\n" +
                "}";

        if (accountID.equalsIgnoreCase(MULTICURRENCY_ACCOUNT)) {
            response = "{\n" +
                    "\"account\": {\n" +
                    "\"resourceId\": \"3dc3d5b3-7023-4848-9853-f5400a64e80f\",\n" +
                    "\"iban\": \"DE12345678901234567890\",\n" +
                    "\"currency\": \"XXX\",\n" +
                    "\"ownerName\": \"Heike Mustermann\",\n" +
                    "\"product\": \"Multicurrency Account\",\n" +
                    "\"cashAccountType\": \"CACC\",\n" +
                    "\"name\": \"Aggregation Account\",\n" +
                    "\"_links\": {\n" +
                    "\"balances\": {\n" +
                    "\"href\": \"/v1/accounts/3dc3d5b3-7023-4848-9853-f5400a64e80f/balances\"\n" +
                    "},\n" +
                    "\"transactions\": {\n" +
                    "\"href\": \"/v1/accounts/3dc3d5b3-7023-4848-9853-f5400a64e80f/transactions\"\n" +
                    "}\n" +
                    "}\n" +
                    "}\n" +
                    "}";
        }
        return Response.ok(response).header(REQUEST_ID, requestID)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS")
                .header("Access-Control-Allow-Headers", "Content-Type, X-Token, Origin, Accept, Authorization, " +
                        "Content-Range, Content-Disposition, Content-Description")
                .allow("OPTIONS").build();
    }

    /**
     * @param accountID ID of the bank account.
     * @param requestID ID of the request.
     * @param consentID ID gotten after consent transaction.
     * @return Account balances.
     */
    @GET
    @Path("/accounts/{AccountId}/balances")
    @Produces("application/json")
    public Response getAccountBalances(@PathParam("AccountId") String accountID,
                                       @HeaderParam("X-Request-ID") String requestID,
                                       @HeaderParam("Consent-ID") String consentID) {

        log.info("GET /accounts/{AccountId}/balances endpoint called.");
        JSONObject responseJSON = validateRequestHeader(requestID, consentID);
        if (!(VALID.equals(responseJSON.get(MESSAGE)))) {
            return Response.status(FORBIDDEN).entity(responseJSON.toJSONString()).build();
        }

        //Example 1: pg 105
        String response = "{\n" +
                " \"account\": {\"iban\": \"FR7612345987650123456789014\"},\n" +
                " \"balances\":\n" +
                " [{\"balanceType\": \"closingBooked\",\n" +
                "\"balanceAmount\": {\"currency\": \"EUR\", \"amount\": \"500.00\"},\n" +
                " \"referenceDate\": \"2017-10-25\"\n" +
                " },\n" +
                " {\"balanceType\": \"expected\",\n" +
                " \"balanceAmount\": {\"currency\": \"EUR\",\"amount\": \"900.00\"},\n" +
                " \"lastChangeDateTime\": \"2017-10-25T15:30:35.035Z\"\n" +
                " }]\n" +
                "}";

        if (accountID.equalsIgnoreCase(MULTICURRENCY_ACCOUNT)) {
            response = "{\n" +
                    " \"account\": {\"iban\": \"DE12345678901234567890\"},\n" +
                    "\"balances\": [{\n" +
                    "\"balanceType\": \"closingBooked\",\n" +
                    "\"balanceAmount\": {\n" +
                    "\"currency\": \"EUR\",\n" +
                    "\"amount\": \"500.00\"\n" +
                    "},\n" +
                    "\"referenceDate\": \"2017-10-25\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"balanceType\": \"expected\",\n" +
                    "\"balanceAmount\": {\n" +
                    "\"currency\": \"EUR\",\n" +
                    "\"amount\": \"900.00\"\n" +
                    "},\n" +
                    "\"lastChangeDateTime\": \"2017-10-25T15:30:35.035Z\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"balanceType\": \"closingBooked\",\n" +
                    "\"balanceAmount\": {\n" +
                    "\"currency\": \"USD\",\n" +
                    "\"amount\": \"350.00\"\n" +
                    "},\n" +
                    "\"referenceDate\": \"2017-10-25\"\n" +
                    "},\n" +
                    "{\n" +
                    "\"balanceType\": \"expected\",\n" +
                    "\"balanceAmount\": {\n" +
                    "\"currency\": \"USD\",\n" +
                    "\"amount\": \"350.00\"\n" +
                    "},\n" +
                    "\"lastChangeDateTime\": \"2017-10-24T14:30:21Z\"\n" +
                    "}\n" +
                    "]\n" +
                    "}";
        }
        return Response.ok(response).header(REQUEST_ID, requestID)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS").build();
    }

    /**
     * Get transaction data for an account.
     *
     * @param accountID     ID of the bank account.
     * @param requestID     ID of the request.
     * @param consentID     ID gotten after consent transaction.
     * @param bookingStatus Permitted codes "booked", "pending", "both".
     * @return transaction details.
     */
    @GET
    @Path("/accounts/{AccountId}/transactions")
    @Produces("application/json")
    public Response getAccountTransactions(@PathParam("AccountId") String accountID,
                                           @HeaderParam("X-Request-ID") String requestID,
                                           @HeaderParam("Consent-ID") String consentID,
                                           @QueryParam("bookingStatus") String bookingStatus) {

        log.info("GET /accounts/{AccountId}/transactions endpoint called.");
        JSONObject responseJSON = validateRequestHeader(requestID, consentID);
        if (!(VALID.equals(responseJSON.get(MESSAGE)))) {
            return Response.status(FORBIDDEN).entity(responseJSON.toJSONString()).build();
        }

        String response;
        if (accountID.equalsIgnoreCase(MULTICURRENCY_ACCOUNT)) {
            response = "{\n" +
                    "  \"account\": {\n" +
                    "    \"iban\": \"DE12345678901234567890\"\n" +
                    "  },\n" +
                    "  \"transactions\": {\n" +
                    "    \"booked\": [\n" +
                    "      {\n" +
                    "        \"transactionId\": \"1234567\",\n" +
                    "        \"creditorName\": \"John Miles\",\n" +
                    "        \"creditorAccount\": {\n" +
                    "          \"iban\": \"DE67100100101306118605\"\n" +
                    "        },\n" +
                    "        \"transactionAmount\": {\n" +
                    "          \"currency\": \"EUR\",\n" +
                    "          \"amount\": \"256.67\"\n" +
                    "        },\n" +
                    "        \"bookingDate\": \"2017-10-25\",\n" +
                    "        \"valueDate\": \"2017-10-26\",\n" +
                    "        \"remittanceInformationUnstructured\": \"Example 1\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"transactionId\": \"1234568\",\n" +
                    "        \"debtorName\": \"Paul Simpson\",\n" +
                    "        \"debtorAccount\": {\n" +
                    "          \"iban\": \"NL76RABO0359400371\"\n" +
                    "        },\n" +
                    "        \"transactionAmount\": {\n" +
                    "          \"currency\": \"EUR\",\n" +
                    "          \"amount\": \"343.01\"\n" +
                    "        },\n" +
                    "        \"bookingDate\": \"2017-10-25\",\n" +
                    "        \"valueDate\": \"2017-10-26\",\n" +
                    "        \"remittanceInformationUnstructured\": \"Example 2\"\n" +
                    "      }\n" +
                    "    ],\n" +
                    "    \"pending\": [\n" +
                    "      {\n" +
                    "        \"transactionId\": \"1234569\",\n" +
                    "        \"creditorName\": \"Claude Renault\",\n" +
                    "        \"creditorAccount\": {\n" +
                    "          \"iban\": \"FR7612345987650123456789014\"\n" +
                    "        },\n" +
                    "        \"transactionAmount\": {\n" +
                    "          \"currency\": \"EUR\",\n" +
                    "          \"amount\": \"-100.03\"\n" +
                    "        },\n" +
                    "        \"valueDate\": \"2017-10-26\",\n" +
                    "        \"remittanceInformationUnstructured\": \"Example 3\"\n" +
                    "      }\n" +
                    "    ],\n" +
                    "    \"_links\": {\n" +
                    "      \"account\": {\n" +
                    "        \"href\": \"/accounts/3dc3d5b3-7023-4848-9853-f5400a64e80f\"\n" +
                    "      }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";
        } else if (!StringUtils.isBlank(accountID)) {
            response = "{\n" +
                    "  \"account\": {\n" +
                    "    \"iban\": \"DE2310010010123456788\"\n" +
                    "  },\n" +
                    "  \"transactions\": {\n" +
                    "    \"booked\": [\n" +
                    "      {\n" +
                    "        \"transactionId\": \"1234567\",\n" +
                    "        \"creditorName\": \"John Miles\",\n" +
                    "        \"creditorAccount\": {\n" +
                    "          \"iban\": \"DE67100100101306118605\"\n" +
                    "        },\n" +
                    "        \"transactionAmount\": {\n" +
                    "          \"currency\": \"EUR\",\n" +
                    "          \"amount\": \"256.67\"\n" +
                    "        },\n" +
                    "        \"bookingDate\": \"2017-10-25\",\n" +
                    "        \"valueDate\": \"2017-10-26\",\n" +
                    "        \"remittanceInformationUnstructured\": \"Example 1\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"transactionId\": \"1234568\",\n" +
                    "        \"debtorName\": \"Paul Simpson\",\n" +
                    "        \"debtorAccount\": {\n" +
                    "          \"iban\": \"NL76RABO0359400371\"\n" +
                    "        },\n" +
                    "        \"transactionAmount\": {\n" +
                    "          \"currency\": \"EUR\",\n" +
                    "          \"amount\": \"343.01\"\n" +
                    "        },\n" +
                    "        \"bookingDate\": \"2017-10-25\",\n" +
                    "        \"valueDate\": \"2017-10-26\",\n" +
                    "        \"remittanceInformationUnstructured\": \"Example 2\"\n" +
                    "      }\n" +
                    "    ],\n" +
                    "    \"_links\": {\n" +
                    "      \"account\": {\n" +
                    "        \"href\": \"/accounts/3dc3d5b3-7023-4848-9853-f5400a64e80f\"\n" +
                    "      }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";
        } else {
            responseJSON.put(CODE, FORBIDDEN.getStatusCode());
            responseJSON.put(MESSAGE, "Permitted bookingStatus query parameter not found");
            return Response.status(FORBIDDEN).entity(responseJSON).build();
        }
        return Response.ok(response).header(REQUEST_ID, requestID)
                .header("Content-Type", "application/json")
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS").build();
    }

    /**
     * Get data on a transaction.
     *
     * @param accountID  ID of the bank account.
     * @param resourceID ID of a transaction.
     * @param requestID  ID of the request.
     * @param consentID  ID gotten after consent transaction.
     * @return transaction details.
     */
    @GET
    @Path("/accounts/{AccountId}/transactions/{resourceId}")
    @Produces("application/json")
    public Response getAccountTransactionsForResource(@PathParam("AccountId") String accountID,
                                                      @PathParam("resourceId") String resourceID,
                                                      @HeaderParam("X-Request-ID") String requestID,
                                                      @HeaderParam("Consent-ID") String consentID) {

        log.info("GET /accounts/{AccountId}/transactions/{resourceId endpoint called.");
        JSONObject responseJSON = validateRequestHeader(requestID, consentID);
        if (!(VALID.equals(responseJSON.get(MESSAGE)))) {
            return Response.status(FORBIDDEN).entity(responseJSON.toJSONString()).build();
        }
        String response = "{\n" +
                "  \"transactionsDetails\": {\n" +
                "    \"transactionId\": \"1234567\",\n" +
                "    \"creditorName\": \"John Miles\",\n" +
                "    \"creditorAccount\": {\n" +
                "      \"iban\": \"DE67100100101306118605\"\n" +
                "    },\n" +
                "    \"mandateId\": \"Mandate-2018-04-20-1234\",\n" +
                "    \"transactionAmount\": {\n" +
                "      \"currency\": \"EUR\",\n" +
                "      \"amount\": \"-256.67\"\n" +
                "    },\n" +
                "    \"bookingDate\": \"2017-10-25\",\n" +
                "    \"valueDate\": \"2017-10-26\",\n" +
                "    \"remittanceInformationUnstructured\": \"Example 1\",\n" +
                "    \"bankTransactionCode\": \"PMNT-RCVD-ESDD\"\n" +
                "  }\n" +
                "}";

        return Response.ok(response).header(REQUEST_ID, requestID)
                .header("Content-Type", "application/json")
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS").build();
    }


    /**
     * Get list of card accounts.
     *
     * @param requestID ID of the request.
     * @param consentID ID gotten after consent transaction.
     * @return card account details.
     */
    @GET
    @Path("/card-accounts")
    @Produces("application/json")
    public Response getCardAccounts(@HeaderParam("X-Request-ID") String requestID,
                                    @HeaderParam("Consent-ID") String consentID) {

        log.info("GET /card-accounts endpoint called.");

        String response = "{\n" +
                "  \"cardAccounts\": [\n" +
                "{\n" +
                "\"resourceId\": \"3d9a81b3-a47d-4130-8765-a9c0ff861b99\", \"maskedPan\": \"525412******3241\",\n" +
                "\"currency\": \"EUR\",\n" +
                "\"name\": \"Main\",\n" +
                "\"product\": \"Basic Credit\",\n" +
                "\"status\": \"enabled\",\n" +
                "\"creditLimit\": { \"currency\": \"EUR\", \"amount\": \"15000.00\" }, \"balances\": [\n" +
                "{\n" +
                "\"balanceType\": \"interimBooked\",\n" +
                "\"balanceAmount\": { \"currency\": \"EUR\", \"amount\": \"14355.78\" }\n" +
                "},{\n" +
                "\"balanceType\": \"nonInvoiced\",\n" +
                "\"balanceAmount\": { \"currency\": \"EUR\", \"amount\": \"4175.86\" }\n" +
                "} ],\n" +
                "      \"_links\": {\n" +
                "        \"transactions\": {\n" +
                "\"href\": \"/card-accounts/3d9a81b3-a47d-4130-8765-a9c0ff861b99/transactions\"\n" +
                "} }\n" +
                "} ]\n" +
                "}";

        return Response.ok(response).header(REQUEST_ID, requestID)
                .header("Access-Control-Allow-Origin", "https://localhost:9446")
                .header("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
                .header("Access-Control-Allow-Headers", "Content-Type, X-Token, X-Request-ID, Consent-ID, " +
                        "Origin, Accept, Authorization, Content-Range, Content-Disposition, Content-Description")
                .build();
    }

    /**
     * Get details of a card account.
     *
     * @param accountID ID of the bank account.
     * @param requestID ID of the request.
     * @param consentID ID gotten after consent transaction.
     * @return Account details.
     */
    @GET
    @Path("/card-accounts/{AccountId}")
    @Produces("application/json")
    public Response getCardAccount(@PathParam("AccountId") String accountID,
                                   @HeaderParam("X-Request-ID") String requestID,
                                   @HeaderParam("Consent-ID") String consentID) {

        log.info("GET /card-accounts/{AccountId} endpoint called.");
        JSONObject responseJSON = validateRequestHeader(requestID, consentID);
        if (!(VALID.equals(responseJSON.get(MESSAGE)))) {
            return Response.status(FORBIDDEN).entity(responseJSON.toJSONString()).build();
        }

        String response = "{\n" +
                "  \"cardAccount\":\n" +
                "{\n" +
                "\"resourceId\": \"3d9a81b3-a47d-4130-8765-a9c0ff861b99\", \"maskedPan\": \"525412******3241\",\n" +
                "\"currency\": \"EUR\",\n" +
                "\"name\": \"Main\",\n" +
                "\"product\": \"Basic Credit\",\n" +
                "\"status\": \"enabled\",\n" +
                "\"creditLimit\": { \"currency\": \"EUR\", \"amount\": \"15000.00\" }, \"balances\": [\n" +
                "{\n" +
                "\"balanceType\": \"interimBooked\",\n" +
                "\"balanceAmount\": { \"currency\": \"EUR\", \"amount\": \"14355.78\" }\n" +
                "},{\n" +
                "\"balanceType\": \"nonInvoiced\",\n" +
                "\"balanceAmount\": { \"currency\": \"EUR\", \"amount\": \"4175.86\" }\n" +
                "} ],\n" +
                "      \"_links\": {\n" +
                "        \"transactions\": {\n" +
                "\"href\": \"/card-accounts/3d9a81b3-a47d-4130-8765-a9c0ff861b99/transactions\"\n" +
                "} }\n" +
                "} }";

        return Response.ok(response).header(REQUEST_ID, requestID)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS")
                .header("Access-Control-Allow-Headers", "Content-Type, X-Token, Origin, Accept, Authorization, " +
                        "Content-Range, Content-Disposition, Content-Description")
                .allow("OPTIONS").build();
    }

    /**
     * Get Balance of a card account
     *
     * @param accountID ID of the bank account.
     * @param requestID ID of the request.
     * @param consentID ID gotten after consent transaction.
     * @return Account balances.
     */
    @GET
    @Path("/card-accounts/{AccountId}/balances")
    @Produces("application/json")
    public Response getCardAccountBalances(@PathParam("AccountId") String accountID,
                                           @HeaderParam("X-Request-ID") String requestID,
                                           @HeaderParam("Consent-ID") String consentID) {

        log.info("GET /card-accounts/{AccountId}/balances endpoint called.");
        JSONObject responseJSON = validateRequestHeader(requestID, consentID);
        if (!(VALID.equals(responseJSON.get(MESSAGE)))) {
            return Response.status(FORBIDDEN).entity(responseJSON.toJSONString()).build();
        }


        String response = "{\n" +
                "\"cardAccount\": {\"maskedPan\": \"525412******3241\"}, \"balances\":[\n" +
                "{\n" +
                "\"balanceType\": \"interimBooked\",\n" +
                "\"balanceAmount\": { \"currency\": \"EUR\", \"amount\": \"14355.78\" }\n" +
                "},{\n" +
                "\"balanceType\": \"nonInvoiced\",\n" +
                "\"balanceAmount\": { \"currency\": \"EUR\", \"amount\": \"4175.86\" }\n" +
                "} ]\n" +
                "}";

        return Response.ok(response).header(REQUEST_ID, requestID)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS").build();
    }

    /**
     * Get transaction data for a card account.
     *
     * @param accountID     ID of the bank account.
     * @param requestID     ID of the request.
     * @param consentID     ID gotten after consent transaction.
     * @param bookingStatus Permitted codes "booked", "pending", "both".
     * @return transaction details.
     */
    @GET
    @Path("/card-accounts/{AccountId}/transactions")
    @Produces("application/json")
    public Response getCardAccountTransactions(@PathParam("AccountId") String accountID,
                                               @HeaderParam("X-Request-ID") String requestID,
                                               @HeaderParam("Consent-ID") String consentID,
                                               @QueryParam("bookingStatus") String bookingStatus) {

        log.info("GET /card-accounts/{AccountId}/transactions endpoint called.");
        JSONObject responseJSON = validateRequestHeader(requestID, consentID);
        if (!(VALID.equals(responseJSON.get(MESSAGE)))) {
            return Response.status(FORBIDDEN).entity(responseJSON.toJSONString()).build();
        }

        String response;
        if (!StringUtils.isBlank(accountID)) {
            response = "\n" +
                    "{\n" +
                    "\"cardAccount\": {\"maskedPan\": \"525412******3241\"\n" +
                    "},\n" +
                    "\"transactions\": {\n" +
                    "  \"booked\": [\n" +
                    "{\n" +
                    "\"cardTransactionId\": \"201710020036959\",\n" +
                    "\"transactionAmount\": { \"currency\": \"EUR\", \"amount\": \"256.67\" }, " +
                    "\"transactionDate\": \"2017-10-25\",\n" +
                    "\"bookingDate\": \"2017-10-26\",\n" +
                    "\"originalAmount\": { \"currency\": \"SEK\", \"amount\": \"2499\" }, " +
                    "\"cardAcceptorAddress\": {\n" +
                    "          \"city\" : \"STOCKHOLM\",\n" +
                    "          \"country\" : \"SE\"\n" +
                    "        },\n" +
                    "\"maskedPan\": \"525412******3241\", \"proprietaryBankTransactionCode\" : \"PURCHASE\", " +
                    "\"invoiced\": false,\n" +
                    "\"transactionDetails\": \"WIFIMARKET.SE\"\n" +
                    "}, {\n" +
                    "\"cardTransactionId\": \"201710020091863\",\n" +
                    "\"transactionAmount\": { \"currency\": \"EUR\", \"amount\": \"10.72\" }, " +
                    "\"transactionDate\": \"2017-10-25\",\n" +
                    "\"bookingDate\": \"2017-10-26\",\n" +
                    "\"originalAmount\": { \"currency\": \"SEK\", \"amount\": \"99\" }, \"cardAcceptorAddress\": {\n" +
                    "          \"city\" : \"STOCKHOLM\",\n" +
                    "          \"country\" : \"SE\"\n" +
                    "        },\n" +
                    "\"maskedPan\": \"525412******8999\", \"proprietaryBankTransactionCode\" : \"PURCHASE\", " +
                    "\"invoiced\": false,\n" +
                    "\"transactionDetails\": \"ICA SUPERMARKET SKOGHA\"\n" +
                    "} ],\n" +
                    "    \"pending\": [ ], " +
                    "\"_links\": {\n" +
                    "      \"cardAccount\": {\n" +
                    "\"href\": \"/card-accounts/3d9a81b3-a47d-4130-8765-a9c0ff861b99\" }\n" +
                    " } }\n" +
                    "}";
        } else {
            responseJSON.put(CODE, FORBIDDEN.getStatusCode());
            responseJSON.put(MESSAGE, "Permitted bookingStatus query parameter not found");
            return Response.status(FORBIDDEN).entity(responseJSON.toJSONString()).build();
        }
        return Response.ok(response).header(REQUEST_ID, requestID)
                .header("Content-Type", "application/json")
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS").build();
    }


}
