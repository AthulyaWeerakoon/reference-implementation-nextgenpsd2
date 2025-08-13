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

import java.util.List;
import javax.validation.Valid;

/**
 * AccountLookupItem class
 */
public class AccountLookupItem {

    @Valid
    private Account identification = null;

    @Valid
    private List<UserIdentifier> users = null;

    /**
     * Get identification
     *
     * @return identification
     **/
    @JsonProperty("identification")
    public Account getIdentification() {
        return identification;
    }

    public void setIdentification(Account identification) {
        this.identification = identification;
    }

    public AccountLookupItem identification(Account identification) {
        this.identification = identification;
        return this;
    }

    /**
     * Get users
     *
     * @return users
     **/
    @JsonProperty("users")
    public List<UserIdentifier> getUsers() {
        return users;
    }

    public void setUsers(List<UserIdentifier> users) {
        this.users = users;
    }

    public AccountLookupItem users(List<UserIdentifier> users) {
        this.users = users;
        return this;
    }

    public AccountLookupItem addUsersItem(UserIdentifier usersItem) {
        this.users.add(usersItem);
        return this;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class AccountLookupItem {\n");

        sb.append("    identification: ").append(toIndentedString(identification)).append("\n");
        sb.append("    users: ").append(toIndentedString(users)).append("\n");
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

