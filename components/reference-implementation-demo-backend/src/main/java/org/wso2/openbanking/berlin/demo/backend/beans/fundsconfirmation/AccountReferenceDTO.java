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
 * AccountReferenceDTO class
 */
public class AccountReferenceDTO {

    private String iban = null;

    private String bban = null;

    private String pan = null;

    private String maskedPan = null;

    private String msisdn = null;

    private String currency = null;

    public void setIban(String iban) {

        this.iban = iban;
    }

    public AccountReferenceDTO iban(String iban) {

        this.iban = iban;
        return this;
    }

    public void setBban(String bban) {

        this.bban = bban;
    }

    public AccountReferenceDTO bban(String bban) {

        this.bban = bban;
        return this;
    }

    public void setPan(String pan) {

        this.pan = pan;
    }

    public AccountReferenceDTO pan(String pan) {

        this.pan = pan;
        return this;
    }

    public void setMaskedPan(String maskedPan) {

        this.maskedPan = maskedPan;
    }

    public AccountReferenceDTO maskedPan(String maskedPan) {

        this.maskedPan = maskedPan;
        return this;
    }

    public void setMsisdn(String msisdn) {

        this.msisdn = msisdn;
    }

    public AccountReferenceDTO msisdn(String msisdn) {

        this.msisdn = msisdn;
        return this;
    }

    public void setCurrency(String currency) {

        this.currency = currency;
    }

    public AccountReferenceDTO currency(String currency) {

        this.currency = currency;
        return this;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class AccountReferenceDTO {\n");

        sb.append("    iban: ").append(toIndentedString(iban)).append("\n");
        sb.append("    bban: ").append(toIndentedString(bban)).append("\n");
        sb.append("    pan: ").append(toIndentedString(pan)).append("\n");
        sb.append("    maskedPan: ").append(toIndentedString(maskedPan)).append("\n");
        sb.append("    msisdn: ").append(toIndentedString(msisdn)).append("\n");
        sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
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
