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

import com.google.inject.Injector;
import com.google.inject.Scopes;
import io.airlift.bootstrap.Bootstrap;
import io.trino.spi.security.GroupProvider;
import io.trino.spi.security.GroupProviderFactory;

import java.util.Map;

import static io.airlift.configuration.ConfigBinder.configBinder;

public class PassportGroupProviderFactory
        implements GroupProviderFactory
{
    @Override
    public String getName()
    {
        return "ga4ghpassport";
    }

    @Override
    public GroupProvider create(Map<String, String> config)
    {
        Bootstrap app = new Bootstrap(
                binder -> {
                    configBinder(binder).bindConfig(PassportGroupConfig.class);
                    binder.bind(PassportGroupProvider.class).in(Scopes.SINGLETON);
                });

        Injector injector = app
                .doNotInitializeLogging()
                .setRequiredConfigurationProperties(config)
                .initialize();

        return injector.getInstance(PassportGroupProvider.class);
    }
}
