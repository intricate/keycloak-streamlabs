package org.keycloak.social.streamlabs;

import org.keycloak.broker.oidc.mappers.AbstractJsonUserAttributeMapper;

/**
 * Streamlabs user attribute mapper.
 */
public class StreamlabsUserAttributeMapper extends AbstractJsonUserAttributeMapper {

    private static final String[] cp = new String[]{
        StreamlabsIdentityProviderFactory.PROVIDER_ID,
    };

    @Override
    public String[] getCompatibleProviders() {
        return cp;
    }

    @Override
    public String getId() {
        return "streamlabs-user-attribute-mapper";
    }

}
