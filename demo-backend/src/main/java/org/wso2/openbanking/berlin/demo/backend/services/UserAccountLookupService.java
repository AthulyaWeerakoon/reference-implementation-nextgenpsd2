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
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static org.wso2.openbanking.berlin.demo.backend.utils.Constants.AccountService.CODE;
import static org.wso2.openbanking.berlin.demo.backend.utils.Constants.AccountService.MESSAGE;
import static org.wso2.openbanking.berlin.demo.backend.utils.Constants.AccountService.VALID;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

/**
 * Mock backend containing read user data lookup endpoint.
 */

@Path("/")
public class UserAccountLookupService {

    private static final Log log = LogFactory.getLog(UserAccountLookupService.class);

    /**
     * Returns a list of account identification for user
     * <p>
     * When provided with an user Id endpoint returns list of accounts bound to user.
     */
    @GET
    @Path("/{type}/{userId}")
    @Produces({"application/json"})
    public Response getAccountsForUser(@PathParam("userId") String userId, @PathParam("type") String type) {
        log.info("GET /{type}/{userId} endpoint called.");
        JSONObject responseJSON = validateRequestHeader(userId, type);
        if (!(VALID.equals(responseJSON.get(MESSAGE)))) {
            return Response.status(BAD_REQUEST).entity(responseJSON.toJSONString()).build();
        }

        return Response.ok(getAccountsResponsePayload(userId, null)).build();

    }

    /**
     * Returns a list of accounts bound to user when provided with an user Id and the user store domain
     *
     * @param userId    user id of the user
     * @param userStore user store domain of the user
     * @param type      type of the account
     * @return list of accounts bound the user
     */
    @GET
    @Path("/{type}/{userStore}/{userId}")
    @Produces({"application/json"})
    public Response getAccountsForUser(@PathParam("userId") String userId, @PathParam("userStore") String userStore,
                                       @PathParam("type") String type) {
        log.info("GET /{type}/{userStore}/{userId} endpoint called.");
        JSONObject responseJSON = validateRequestHeader(userId, type);
        if (!(VALID.equals(responseJSON.get(MESSAGE)))) {
            return Response.status(BAD_REQUEST).entity(responseJSON.toJSONString()).build();
        }

        return Response.ok(getAccountsResponsePayload(userId, userStore)).build();
    }

    /**
     * Get the accounts response payload.
     *
     * @param userId          user id of the user
     * @param userStoreDomain user store domain of the user
     * @return accounts response payload for a given user id and user store domain
     */
    private String getAccountsResponsePayload(String userId, String userStoreDomain) {
        return "{\n" +
                "  \"customerIdentification\": {\n" +
                "    \"customerIdentification\": \"JEAN123\",\n" +
                "    \"userIdentification\": \"" + userId + "\"\n" +
                "  },\n" +
                "  \"accounts\": [\n" +
                "    {\n" +
                "      \"iban\": \"DE12345678901234567890\",\n" +
                "      \"isDefault\": true,\n" +
                "      \"currency\": \"USD\"\n" +
                "    }, \n" +
                "    {\n" +
                "      \"bban\": \"BARC12345612345678\",\n" +
                "      \"isDefault\": true,\n" +
                "      \"currency\": \"USD\"\n" +
                "    }, \n" +
                "    {\n" +
                "      \"pan\": \"5409050000000000\",\n" +
                "      \"isDefault\": true,\n" +
                "      \"currency\": \"USD\"\n" +
                "    }, \n" +
                "    {\n" +
                "      \"maskedPan\": \"123456xxxxxx1234\",\n" +
                "      \"isDefault\": true,\n" +
                "      \"currency\": \"USD\"\n" +
                "    }, \n" +
                "    {\n" +
                "      \"iban\": \"DE12345678901234567890\",\n" +
                "      \"isDefault\": false,\n" +
                "      \"currency\": \"EUR\"\n" +
                "    }, \n" +
                "    {\n" +
                "      \"bban\": \"BARC12345612345678\",\n" +
                "      \"isDefault\": false,\n" +
                "      \"currency\": \"EUR\"\n" +
                "    }, \n" +
                "    {\n" +
                "      \"pan\": \"5409050000000000\",\n" +
                "      \"isDefault\": false,\n" +
                "      \"currency\": \"EUR\"\n" +
                "    }, \n" +
                "    {\n" +
                "      \"maskedPan\": \"123456xxxxxx1234\",\n" +
                "      \"isDefault\": false,\n" +
                "      \"currency\": \"EUR\"\n" +
                "    }, \n" +
                "    {\n" +
                "      \"iban\": \"DE12345678901234567890\",\n" +
                "      \"isDefault\": false,\n" +
                "      \"currency\": \"GBP\"\n" +
                "    }, \n" +
                "    {\n" +
                "      \"bban\": \"BARC12345612345678\",\n" +
                "      \"isDefault\": false,\n" +
                "      \"currency\": \"GBP\"\n" +
                "    }, \n" +
                "    {\n" +
                "      \"pan\": \"5409050000000000\",\n" +
                "      \"isDefault\": false,\n" +
                "      \"currency\": \"GBP\"\n" +
                "    }, \n" +
                "    {\n" +
                "      \"maskedPan\": \"123456xxxxxx1234\",\n" +
                "      \"isDefault\": false,\n" +
                "      \"currency\": \"GBP\"\n" +
                "    }, \n" +
                "    {\n" +
                "      \"iban\": \"DE98765432109876543210\",\n" +
                "      \"isDefault\": true,\n" +
                "      \"currency\": \"EUR\"\n" +
                "    }, \n" +
                "    {\n" +
                "      \"bban\": \"BARC87654321654321\",\n" +
                "      \"isDefault\": true,\n" +
                "      \"currency\": \"EUR\"\n" +
                "    }, \n" +
                "    {\n" +
                "      \"pan\": \"9181230000000000\",\n" +
                "      \"isDefault\": true,\n" +
                "      \"currency\": \"EUR\"\n" +
                "    }, \n" +
                "    {\n" +
                "      \"maskedPan\": \"123456xxxxxx1235\",\n" +
                "      \"isDefault\": true,\n" +
                "      \"currency\": \"EUR\"\n" +
                "    }, \n" +
                "    {\n" +
                "      \"iban\": \"DE73459340345034563141\",\n" +
                "      \"isDefault\": true,\n" +
                "      \"currency\": \"GBP\"\n" +
                "    }, \n" +
                "    {\n" +
                "      \"bban\": \"BARC96503450034033\",\n" +
                "      \"isDefault\": true,\n" +
                "      \"currency\": \"GBP\"\n" +
                "    }, \n" +
                "    {\n" +
                "      \"pan\": \"6548830000000000\",\n" +
                "      \"isDefault\": true,\n" +
                "      \"currency\": \"GBP\"\n" +
                "    }, \n" +
                "    {\n" +
                "      \"maskedPan\": \"123456xxxxxx1236\",\n" +
                "      \"isDefault\": true,\n" +
                "      \"currency\": \"GBP\"\n" +
                "    }, \n" +
                "    {\n" +
                "      \"iban\": \"DE84563457493493534546\",\n" +
                "      \"isDefault\": true,\n" +
                "      \"currency\": \"AUD\"\n" +
                "    }, \n" +
                "    {\n" +
                "      \"bban\": \"BARC95645364533323\",\n" +
                "      \"isDefault\": true,\n" +
                "      \"currency\": \"AUD\"\n" +
                "    }, \n" +
                "    {\n" +
                "      \"pan\": \"1234560000000000\",\n" +
                "      \"isDefault\": true,\n" +
                "      \"currency\": \"AUD\"\n" +
                "    }, \n" +
                "    {\n" +
                "      \"maskedPan\": \"123456xxxxxx1237\",\n" +
                "      \"isDefault\": true,\n" +
                "      \"currency\": \"AUD\"\n" +
                "    }, \n" +
                "    {\n" +
                "      \"iban\": \"DE34534456343478667544\",\n" +
                "      \"isDefault\": true,\n" +
                "      \"currency\": \"EUR\"\n" +
                "    }, \n" +
                "    {\n" +
                "      \"bban\": \"BARC09456045686545\",\n" +
                "      \"isDefault\": true,\n" +
                "      \"currency\": \"EUR\"\n" +
                "    }, \n" +
                "    {\n" +
                "      \"pan\": \"9368240000000000\",\n" +
                "      \"isDefault\": true,\n" +
                "      \"currency\": \"EUR\"\n" +
                "    }, \n" +
                "    {\n" +
                "      \"maskedPan\": \"123456xxxxxx1238\",\n" +
                "      \"isDefault\": true,\n" +
                "      \"currency\": \"EUR\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    }

    /**
     * Check if user ID and type are set.
     *
     * @param userId User ID of the request.
     * @param type   Type of the account lookup (Sharable/Payable).
     * @return JSON response with either an error message or "valid".
     */
    private static JSONObject validateRequestHeader(String userId, String type) {

        JSONObject responseJSON = new JSONObject();
        if (StringUtils.isBlank(userId)) {
            responseJSON.put(CODE, BAD_REQUEST.getStatusCode());
            responseJSON.put(MESSAGE, "userId not found");
            return responseJSON;
        } else if (StringUtils.isBlank(type)) {
            responseJSON.put(CODE, BAD_REQUEST.getStatusCode());
            responseJSON.put(MESSAGE, "type not found");
            return responseJSON;
        } else if (!("shareable".equals(type) || "payable".equals(type))) {
            responseJSON.put(CODE, BAD_REQUEST.getStatusCode());
            responseJSON.put(MESSAGE, "Invalid type");
            return responseJSON;
        } else {
            responseJSON.put(MESSAGE, VALID);
            return responseJSON;
        }
    }

}
