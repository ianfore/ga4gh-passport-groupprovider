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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.airlift.log.Logger;
import io.trino.spi.security.GroupProvider;

import javax.inject.Inject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashSet;
import java.util.Set;

public class PassportGroupProvider
        implements GroupProvider
{
    private static final Logger log = Logger.get(PassportGroupProvider.class);

    private String postEndpoint;

    @Inject
    public PassportGroupProvider(PassportGroupConfig config)
    {
        postEndpoint = config.getClearingHouseURL();
    }

    @Override
    public Set<String> getGroups(String ga4ghtoken)
    {
        log.debug("Checking clearing house for groups");
        String inputJson = "{\"ga4ghpassport\":\"" + ga4ghtoken + "\",\"tracking\":\"PassportGroupProvider\"}";

        var request = HttpRequest.newBuilder()
                .uri(URI.create(postEndpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(inputJson))
                .build();
  
        var client = HttpClient.newHttpClient();

        Set<String> hashSet = new HashSet<String>();
        try {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            log.debug("clearinghouse status code= %1$s", response.statusCode());
            log.debug("response body= %1$s", response.body());

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
            JsonNode jsonNode = objectMapper.readTree(response.body());
            if (jsonNode.has("msg")) {
                log.info("Clearing house responded: %1$s", jsonNode.get("msg").asText());
                return hashSet;
            }
            JsonNode infoNode = jsonNode.get("info");
            // Getting the expiration for info. 
            // Not currently checking expiration here as the clearing house does so via JWT validation
            Long exp = infoNode.get("exp").asLong();
            
            JsonNode consentsNode = jsonNode.get("consents");
            if (consentsNode.isArray()) {
                for (final JsonNode consent : consentsNode) {
                    String groupStr = consent.get("consent_group").asText();
                    log.debug("consented for = %1$s", groupStr);
                    hashSet.add(groupStr);
                }
            }
        }
        catch (Exception e) {
            log.info("clearinghouse request failed");
            log.info("Exception %1$s", e.toString());
        }
        return hashSet;
    }
}
