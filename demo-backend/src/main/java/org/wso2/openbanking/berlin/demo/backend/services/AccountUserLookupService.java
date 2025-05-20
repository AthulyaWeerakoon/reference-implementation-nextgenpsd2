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

import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.openbanking.berlin.demo.backend.beans.lookup.Account;
import org.wso2.openbanking.berlin.demo.backend.beans.lookup.UsersRequest;

import java.io.IOException;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static org.wso2.openbanking.berlin.demo.backend.utils.Constants.AccountService.CODE;
import static org.wso2.openbanking.berlin.demo.backend.utils.Constants.AccountService.MESSAGE;
import static org.wso2.openbanking.berlin.demo.backend.utils.Constants.AccountService.VALID;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

/**
 * Mock backend containing account data lookup endpoint.
 */

@Path("/")
public class AccountUserLookupService {

    private static final Log log = LogFactory.getLog(AccountUserLookupService.class);
    private ObjectMapper mapper = new ObjectMapper();

    /**
     * Returns list of authroisation parties
     */
    @POST
    @Path("/accounts/{type}")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response getUsersForAccounts(@PathParam("type") String type, String request) {
        log.info("POST /accounts/{type} endpoint called.");

        UsersRequest usersRequest;
        try {
            usersRequest = mapper.readValue(request, UsersRequest.class);
        } catch (IOException e) {
            log.error("Error in casting JSON body " + e.toString());
            return Response.status(400).build();
        }

        JSONObject responseJSON = validateRequest(usersRequest, type);
        if (!(VALID.equals(responseJSON.get(MESSAGE)))) {
            return Response.status(BAD_REQUEST).entity(responseJSON).build();
        }

        List<Account> accountsList = usersRequest.getAccounts();
        StringBuilder response = new StringBuilder();
        response.append(
                "{\n" +
                        "  \"accounts\": [\n"
        );

        for (int i = 0; i < accountsList.size(); i++) {
            boolean accountAdded = false;

            response.append(
                    "    {\n" +
                            "      \"identification\": {\n"
            );
            if (accountsList.get(i).getIban() != null) {
                response.append("        \"iban\": \"" + accountsList.get(i).getIban() + "\"");
                accountAdded = true;
            }
            if (accountsList.get(i).getBban() != null) {
                if (accountAdded) {
                    response.append(",\n");
                }
                response.append("        \"bban\": \"" + accountsList.get(i).getBban() + "\"");
                accountAdded = true;
            }
            if (accountsList.get(i).getMsisdn() != null) {
                if (accountAdded) {
                    response.append(",\n");
                }
                response.append("        \"msisdn\": \"" + accountsList.get(i).getMsisdn() + "\"");
                accountAdded = true;
            }
            if (accountsList.get(i).getMaskedPan() != null) {
                if (accountAdded) {
                    response.append(",\n");
                }
                response.append("        \"maskedPan\": \"" + accountsList.get(i).getMaskedPan() + "\"");
                accountAdded = true;
            }
            if (accountsList.get(i).getPan() != null) {
                if (accountAdded) {
                    response.append(",\n");
                }
                response.append("        \"pan\": \"" + accountsList.get(i).getPan() + "\"");
            }
            response.append(
                    "\n      },\n" +
                            "      \"users\": [\n"

            );
            if ("DE12345678901234567890".equals(accountsList.get(i).getIban())) {
                response.append(
                        "        {\n" +
                                "          \"customerIdentification\": \"JEAN123\",\n" +
                                "          \"userIdentification\": \"psu1@wso2.com\"\n" +
                                "        }, \n" +
                                "        {\n" +
                                "          \"customerIdentification\": \"SAM456\",\n" +
                                "          \"userIdentification\": \"psu2@wso2.com\"\n" +
                                "        }\n" +
                                "      ]\n"
                );
            } else {
                response.append(
                        "        {\n" +
                                "          \"customerIdentification\": \"JEAN123\",\n" +
                                "          \"userIdentification\": \"psu1@wso2.com\"\n" +
                                "        }\n" +
                                "      ]\n"
                );
            }
            if (i == accountsList.size() - 1) {
                response.append("    }\n");
            } else {
                response.append("    },\n");
            }
        }

        response.append(
                "  ]\n" +
                        "}"
        );

        return Response.ok(response.toString()).build();
    }

    /**
     * Check if account data and type are set.
     *
     * @param usersRequest Account data where user data is needed.
     * @param type         Type of the account lookup (Sharable/Payable).
     * @return JSON response with either an error message or "valid".
     */
    private static JSONObject validateRequest(UsersRequest usersRequest, String type) {

        JSONObject responseJSON = new JSONObject();
        if (usersRequest.getAccounts().isEmpty()) {
            responseJSON.put(CODE, BAD_REQUEST.getStatusCode());
            responseJSON.put(MESSAGE, "Account data not found");
            return responseJSON;
        } else if (StringUtils.isBlank(type)) {
            responseJSON.put(CODE, BAD_REQUEST.getStatusCode());
            responseJSON.put(MESSAGE, "Type not found");
            return responseJSON;
        } else if (!(type.equals("shareable") || type.equals("payable"))) {
            responseJSON.put(CODE, BAD_REQUEST.getStatusCode());
            responseJSON.put(MESSAGE, "Invalid type");
            return responseJSON;
        } else {
            responseJSON.put(MESSAGE, VALID);
            return responseJSON;
        }
    }


}
