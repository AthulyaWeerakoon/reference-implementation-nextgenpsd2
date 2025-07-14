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

package org.wso2.openbanking.berlin.demo.backend.beans.fundsconfirmation;

/**
 * FundsConfirmationDTO class
 */
public class FundsConfirmationDTO {

    private String cardNumber = null;

    private AccountReferenceDTO account = null;

    private String payee = null;

    private AmountDTO instructedAmount = null;

    public String getCardNumber() {

        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {

        this.cardNumber = cardNumber;
    }

    public FundsConfirmationDTO cardNumber(String cardNumber) {

        this.cardNumber = cardNumber;
        return this;
    }

    public AccountReferenceDTO getAccount() {

        return account;
    }

    public void setAccount(AccountReferenceDTO account) {

        this.account = account;
    }

    public FundsConfirmationDTO account(AccountReferenceDTO account) {

        this.account = account;
        return this;
    }

    public void setPayee(String payee) {

        this.payee = payee;
    }

    public FundsConfirmationDTO payee(String payee) {

        this.payee = payee;
        return this;
    }

    public AmountDTO getInstructedAmount() {

        return instructedAmount;
    }

    public void setInstructedAmount(AmountDTO instructedAmount) {

        this.instructedAmount = instructedAmount;
    }

    public FundsConfirmationDTO instructedAmount(AmountDTO instructedAmount) {

        this.instructedAmount = instructedAmount;
        return this;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class FundsConfirmationDTO {\n");

        sb.append("    cardNumber: ").append(toIndentedString(cardNumber)).append("\n");
        sb.append("    account: ").append(toIndentedString(account)).append("\n");
        sb.append("    payee: ").append(toIndentedString(payee)).append("\n");
        sb.append("    instructedAmount: ").append(toIndentedString(instructedAmount)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    private static String toIndentedString(java.lang.Object o) {

        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
