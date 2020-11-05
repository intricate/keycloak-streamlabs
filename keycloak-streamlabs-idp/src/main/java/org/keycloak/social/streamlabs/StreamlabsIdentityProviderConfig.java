package org.keycloak.social.streamlabs;

import org.keycloak.broker.oidc.OAuth2IdentityProviderConfig;
import org.keycloak.models.IdentityProviderModel;

/**
 * Streamlabs identity provider configuration.
 */
public class StreamlabsIdentityProviderConfig extends OAuth2IdentityProviderConfig {

    public StreamlabsIdentityProviderConfig(IdentityProviderModel model) {
        super(model);
    }

    public StreamlabsIdentityProviderConfig() {
    }
}
