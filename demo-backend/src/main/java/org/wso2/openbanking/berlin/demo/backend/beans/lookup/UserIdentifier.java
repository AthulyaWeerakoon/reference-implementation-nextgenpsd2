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

package org.wso2.openbanking.berlin.demo.backend.beans.lookup;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * UserIdentifier class
 */
public class UserIdentifier {

    private String customerIdentification;

    private String userIdentification;

    /**
     * Get customerIdentification
     *
     * @return customerIdentification
     **/
    @JsonProperty("customerIdentification")
    public String getCustomerIdentification() {
        return customerIdentification;
    }

    public void setCustomerIdentification(String customerIdentification) {
        this.customerIdentification = customerIdentification;
    }

    public UserIdentifier customerIdentification(String customerIdentification) {
        this.customerIdentification = customerIdentification;
        return this;
    }

    /**
     * Get userIdentification
     *
     * @return userIdentification
     **/
    @JsonProperty("userIdentification")
    public String getUserIdentification() {
        return userIdentification;
    }

    public void setUserIdentification(String userIdentification) {
        this.userIdentification = userIdentification;
    }

    public UserIdentifier userIdentification(String userIdentification) {
        this.userIdentification = userIdentification;
        return this;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class UserIdentifier {\n");

        sb.append("    customerIdentification: ").append(toIndentedString(customerIdentification)).append("\n");
        sb.append("    userIdentification: ").append(toIndentedString(userIdentification)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private static String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}

