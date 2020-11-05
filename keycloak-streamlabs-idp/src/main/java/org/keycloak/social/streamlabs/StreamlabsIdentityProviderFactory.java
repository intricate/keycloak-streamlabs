package org.keycloak.social.streamlabs;

import org.keycloak.broker.provider.AbstractIdentityProviderFactory;
import org.keycloak.broker.social.SocialIdentityProviderFactory;
import org.keycloak.models.IdentityProviderModel;
import org.keycloak.models.KeycloakSession;

/**
 * Streamlabs identity provider factory.
 */
public class StreamlabsIdentityProviderFactory
    extends AbstractIdentityProviderFactory<StreamlabsIdentityProvider>
    implements SocialIdentityProviderFactory<StreamlabsIdentityProvider> {

    public static final String PROVIDER_NAME = "Streamlabs";
    public static final String PROVIDER_ID = "streamlabs";

    @Override
    public String getName() {
        return PROVIDER_NAME;
    }

    @Override
    public StreamlabsIdentityProvider create(
        KeycloakSession session,
        IdentityProviderModel model
    ) {
        return new StreamlabsIdentityProvider(
            session,
            new StreamlabsIdentityProviderConfig(model)
        );
    }

    @Override
    public StreamlabsIdentityProviderConfig createConfig() {
        return new StreamlabsIdentityProviderConfig();
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
