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
 * Account class
 */
public class Account {

    /**
     * IBAN of account
     **/
    private String iban;

    /**
     * BBAN of account
     **/
    private String bban;

    /**
     * PAN of account
     **/
    private String pan;

    private String maskedPan;

    private String msisdn;

    private String currency;

    /**
     * IBAN of account
     *
     * @return iban
     **/
    @JsonProperty("iban")
    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public Account iban(String iban) {
        this.iban = iban;
        return this;
    }

    /**
     * BBAN of account
     *
     * @return bban
     **/
    @JsonProperty("bban")
    public String getBban() {
        return bban;
    }

    public void setBban(String bban) {
        this.bban = bban;
    }

    public Account bban(String bban) {
        this.bban = bban;
        return this;
    }

    /**
     * PAN of account
     *
     * @return pan
     **/
    @JsonProperty("pan")
    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public Account pan(String pan) {
        this.pan = pan;
        return this;
    }

    @JsonProperty("maskedPan")
    public String getMaskedPan() {

        return maskedPan;
    }

    public void setMaskedPan(String maskedPan) {

        this.maskedPan = maskedPan;
    }

    @JsonProperty("msisdn")
    public String getMsisdn() {

        return msisdn;
    }

    public void setMsisdn(String msisdn) {

        this.msisdn = msisdn;
    }

    @JsonProperty("currency")
    public String getCurrency() {

        return currency;
    }

    public void setCurrency(String currency) {

        this.currency = currency;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Account {\n");

        sb.append("    iban: ").append(toIndentedString(iban)).append("\n");
        sb.append("    bban: ").append(toIndentedString(bban)).append("\n");
        sb.append("    pan: ").append(toIndentedString(pan)).append("\n");
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

