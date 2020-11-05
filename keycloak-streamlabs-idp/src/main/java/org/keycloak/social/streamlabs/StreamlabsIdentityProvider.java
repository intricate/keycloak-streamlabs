package org.keycloak.social.streamlabs;

import com.fasterxml.jackson.databind.JsonNode;
import org.keycloak.broker.oidc.AbstractOAuth2IdentityProvider;
import org.keycloak.broker.oidc.mappers.AbstractJsonUserAttributeMapper;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.broker.provider.IdentityBrokerException;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.broker.social.SocialIdentityProvider;
import org.keycloak.events.EventBuilder;
import org.keycloak.models.KeycloakSession;

/**
 * Streamlabs identity provider.
 */
public class StreamlabsIdentityProvider
    extends AbstractOAuth2IdentityProvider<StreamlabsIdentityProviderConfig>
    implements SocialIdentityProvider<StreamlabsIdentityProviderConfig> {

    public static final String AUTH_URL = "https://streamlabs.com/api/v1.0/authorize";
    public static final String TOKEN_URL = "https://streamlabs.com/api/v1.0/token";
    public static final String PROFILE_URL = "https://streamlabs.com/api/v1.0/user";
    public static final String DEFAULT_SCOPE = "alerts.create";

    /**
     * JSON responses from the Streamlabs "user" API endpoint should contain
     * this key which maps to an object containing details about the requested
     * Streamlabs user.
     */
    public static final String STREAMLABS_PROFILE_JSON_KEY = "streamlabs";

    public StreamlabsIdentityProvider(
        KeycloakSession session,
        StreamlabsIdentityProviderConfig config
    ) {
        super(session, config);
        config.setAuthorizationUrl(AUTH_URL);
        config.setTokenUrl(TOKEN_URL);
        config.setUserInfoUrl(PROFILE_URL);
    }

    /**
     * Build a URL that can be used to retrieve details about a Streamlabs
     * user.
     *
     * @param accessToken Access token for an authenticated Streamlabs user.
     * @return Streamlabs user URL.
     */
    public static String buildStreamlabsProfileUrl(String accessToken) {
        return String.format("%s?access_token=%s", PROFILE_URL, accessToken);
    }

    @Override
    protected String getDefaultScopes() {
        return DEFAULT_SCOPE;
    }

    @Override
    protected boolean supportsExternalExchange() {
        return true;
    }

    @Override
    protected String getProfileEndpointForValidation(EventBuilder event) {
        return PROFILE_URL;
    }

    @Override
    protected BrokeredIdentityContext extractIdentityFromProfile(
        EventBuilder event,
        JsonNode profile
    ) {
        if (!profile.isObject()
            || !profile.hasNonNull(STREAMLABS_PROFILE_JSON_KEY)
        ) {
            String errorMessage = String.format(
                "Failed to extract identity from invalid Streamlabs user " +
                    "profile JSON value. Expected a JSON object containing " +
                    "a key \"%s\" that maps to a non-null value.",
                STREAMLABS_PROFILE_JSON_KEY
            );
            logger.errorv(errorMessage + " Got: {0}", profile.toString());
            throw new IdentityBrokerException(errorMessage);
        }

        JsonNode streamlabsProfileJsonNode = profile.get(
            STREAMLABS_PROFILE_JSON_KEY
        );

        BrokeredIdentityContext user = new BrokeredIdentityContext(
            getJsonProperty(streamlabsProfileJsonNode, "id")
        );

        user.setUsername(
            getJsonProperty(streamlabsProfileJsonNode, "display_name")
        );
        user.setIdpConfig(getConfig());
        user.setIdp(this);

        AbstractJsonUserAttributeMapper.storeUserProfileForMapper(
            user,
            profile,
            getConfig().getAlias()
        );

        return user;
    }

    @Override
    protected BrokeredIdentityContext doGetFederatedIdentity(String accessToken) {
        try {
            JsonNode profile = SimpleHttp
                .doGet(buildStreamlabsProfileUrl(accessToken), session)
                .asJson();
            return extractIdentityFromProfile(null, profile);
        } catch (Exception e) {
            throw new IdentityBrokerException(
                "Could not obtain user profile from Streamlabs.",
                e
            );
        }
    }
}
