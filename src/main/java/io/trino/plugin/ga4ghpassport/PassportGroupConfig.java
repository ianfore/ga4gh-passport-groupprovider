/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.trino.plugin.ga4ghpassport;

import io.airlift.configuration.Config;
import io.airlift.configuration.ConfigDescription;

import javax.validation.constraints.NotNull;

public class PassportGroupConfig
{
    private String clearingHouseURL;
    //private Duration refreshPeriod = new Duration(5, SECONDS);

    @NotNull
    public String getClearingHouseURL()
    {
        return clearingHouseURL;
    }

    @Config("passport.clearing-house-url")
    @ConfigDescription("URL of Clearing House to provide group membership from a passport or task specific token")
    public PassportGroupConfig setClearingHouseURL(String clearingHouseURL)
    {
        this.clearingHouseURL = clearingHouseURL;
        return this;
    }
}
