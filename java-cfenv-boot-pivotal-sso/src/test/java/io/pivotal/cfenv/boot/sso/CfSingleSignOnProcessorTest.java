/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.pivotal.cfenv.boot.sso;

import org.junit.Test;

import org.springframework.core.env.Environment;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Pivotal Application Single Sign-On
 */
public class CfSingleSignOnProcessorTest extends CfSingleSignOnTestSupport {

    @Test
    public void testBasicSpringSecurityProperties() {
        String authDomain = "https://my-plan.login.run.pivotal.io";
        mockVcapServices(getServicesPayload(getSsoServicePayload(authDomain)));
        Environment env = getEnvironment();

        assertThat(env.getProperty("ssoServiceUrl"))
                .isEqualTo(authDomain);

        assertThat(env.getProperty("spring.security.oauth2.client.registration.sso.client-id"))
                .isEqualTo("my-sso-client-id");

        assertThat(env.getProperty("spring.security.oauth2.client.registration.sso.client-secret"))
                .isEqualTo("my-sso-client-secret");

        assertThat(env.getProperty("spring.security.oauth2.client.registration.sso.client-name"))
                .isEqualTo("sso");

        assertThat(env.getProperty("spring.security.oauth2.client.registration.sso.redirect-uri"))
                .isEqualTo("{baseUrl}/login/oauth2/code/{registrationId}");

        assertThat(env.getProperty("spring.security.oauth2.client.provider.sso.authorization-uri"))
                .isEqualTo(authDomain + "/oauth/authorize");
    }

    @Test
    public void testScopePropertyFromPlaceHolders() {
        mockVcapServices(getServicesPayload(getSsoServicePayload("https://my-plan.login.run.pivotal.io")));
        Environment env = getEnvironment();

        assertThat(env.getProperty("spring.security.oauth2.client.registration.sso.scope"))
                .isEqualTo("openid");
    }

    @Test
    public void testAuthorizationGrantTypePropertyFromPlaceHolders() {
        mockVcapServices(getServicesPayload(getSsoServicePayload("https://my-plan.login.run.pivotal.io")));
        Environment env = getEnvironment();

        assertThat(env.getProperty("spring.security.oauth2.client.registration.sso.authorization-grant-type"))
                .isEqualTo("authorization_code");
    }

    @Test
    public void testIssuerUriNonSystemZoneIssuer() {
        mockVcapServices(getServicesPayload(getSsoServicePayload("https://my-plan.login.run.pivotal.io")));
        Environment env = getEnvironment();

        assertThat(env.getProperty("spring.security.oauth2.client.provider.sso.issuer-uri"))
                .isEqualTo("https://my-plan.uaa.run.pivotal.io/oauth/token");
    }

    @Test
    public void testIssuerUriSystemZoneIssuer() {
        mockVcapServices(getServicesPayload(getSsoServicePayload("https://login.run.pivotal.io")));
        Environment env = getEnvironment();

        assertThat(env.getProperty("spring.security.oauth2.client.provider.sso.issuer-uri"))
                .isEqualTo("https://uaa.run.pivotal.io/oauth/token");
    }
}
