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

package org.wso2.openbanking.nextgenpsd2.extensions.model;

import org.wso2.openbanking.nextgenpsd2.extensions.constants.ExtensionEnums;

/**
 * SCA approach class.
 */
public class ScaApproach {

    private ExtensionEnums.ScaApproachEnum approach;
    private boolean isDefault;

    public ExtensionEnums.ScaApproachEnum getApproach() {
        return approach;
    }

    public void setApproach(ExtensionEnums.ScaApproachEnum approach) {
        this.approach = approach;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}
