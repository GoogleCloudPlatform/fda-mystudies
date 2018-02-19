/*
 *    Copyright 2017 Sage Bionetworks
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package org.sagebase.crf.fitbit;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ResponseTypeValues;

import org.json.JSONException;
import org.researchstack.backbone.ui.MainActivity;
import org.sagebionetworks.bridge.android.manager.AuthenticationManager;
import org.sagebionetworks.bridge.android.manager.BridgeManagerProvider;
import org.sagebionetworks.bridge.android.util.retrofit.RxUtils;
import org.sagebionetworks.bridge.rest.model.OAuthAuthorizationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Created by rianhouston on 10/11/17.
 */

public class FitbitManager {
    private static final Logger LOG = LoggerFactory.getLogger(FitbitManager.class);

    // Configured in Study Manager
    private static final String STUDY_OAUTH_PROVIDER_KEY = "fitbit";

    private static class OAuthParams {
        private static final Uri URI_AUTHORIZE =
                Uri.parse("https://www.fitbit.com/oauth2/authorize");
        private static final Uri URI_TOKEN = Uri.parse("https://api.fitbit.com/oauth2/token");
        // registered with Fitbit, and specified in intent filter in the manifest
        private static final Uri URI_CALLBACK = Uri.parse("org.sagebase.crf-module://oauth2");
        private static final String CLIENT_ID = "22CK8G";
        private static final Set<String> REQUIRED_SCOPES =
                ImmutableSet.of("heartrate","activity");
    }

    private static final AuthorizationServiceConfiguration AUTHORIZATION_SERVICE_CONFIGURATION =
            new AuthorizationServiceConfiguration(
                    OAuthParams.URI_AUTHORIZE, OAuthParams.URI_TOKEN);

    private static final AuthorizationRequest AUTHORIZATION_REQUEST =
            new AuthorizationRequest.Builder(
                    AUTHORIZATION_SERVICE_CONFIGURATION,
                    OAuthParams.CLIENT_ID,
                    ResponseTypeValues.CODE,
                    OAuthParams.URI_CALLBACK)
                    .setScope(Joiner.on(" ").join(OAuthParams.REQUIRED_SCOPES)).build();

    private static final String PREFS_AUTH_STATE = "org.sagebase.crf.fitbit.AUTH_STATE_PREF_KEY";

    private final Context mContext;

    private final OAuthDAO mOAuthDAO;

    private final AuthorizationService authorizationService;

    public interface ErrorHandler {
        void showAuthorizationErrorMessage(String errorMessage);
    }

    public FitbitManager(Context applicationContext, OAuthDAO oAuthDAO) {
        mContext = applicationContext.getApplicationContext();
        authorizationService = new AuthorizationService(mContext);
        mOAuthDAO = oAuthDAO;
    }

    /**
     * @return received authorization including the required scopes
     */
    public boolean isAuthorized() {
        AuthState authState = getAuthState();
        return authState != null && authState.getLastAuthorizationResponse() != null
                && authState.getScopeSet().containsAll(OAuthParams.REQUIRED_SCOPES);
    }

    /**
     * @see AuthorizationService#getAuthorizationRequestIntent(AuthorizationRequest)
     */
    public Intent getAuthorizationIntent() {
        return authorizationService.getAuthorizationRequestIntent(AUTHORIZATION_REQUEST);
    }

    public void authenticate() {

        Intent authIntent = authorizationService.getAuthorizationRequestIntent
                (AUTHORIZATION_REQUEST);

        Intent postAuthIntent = new Intent(mContext, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        authorizationService.performAuthorizationRequest(AUTHORIZATION_REQUEST,
                PendingIntent.getActivity(mContext,
                        AUTHORIZATION_REQUEST.hashCode(),
                        postAuthIntent,
                        0));
    }

    public void handleResponse(@NonNull Intent intent, @NonNull ErrorHandler errorHandler) {
        AuthorizationResponse resp = AuthorizationResponse.fromIntent(intent);
        AuthorizationException ex = AuthorizationException.fromIntent(intent);

        if (resp == null && ex == null) {
            LOG.warn("Failed to process AuthorizationResponse and AuthorizationException");
            return;
        }
        if (ex != null) {
            LOG.warn(ex.error + ": " + ex.errorDescription, ex);
            errorHandler.showAuthorizationErrorMessage(ex.errorDescription);
        }
        AuthState state = new AuthState(resp, ex);
        mOAuthDAO.putOAuthState(STUDY_OAUTH_PROVIDER_KEY, state);

        getAccessToken(state);
    }

    /**
     * @param state authentication state to use to pass authorizationCode to Bridge and exchange for
     *              an accessToken
     */
    public void getAccessToken(@NonNull AuthState state) {
        AuthorizationResponse response = state.getLastAuthorizationResponse();
        if (response == null) {
            LOG.warn("No AuthorizationResponse found");
            return;
        }
        OAuthAuthorizationToken token = new OAuthAuthorizationToken()
                .authToken(state.getLastAuthorizationResponse().authorizationCode);

        RxUtils.toBodySingle(BridgeManagerProvider.getInstance()
                .getAuthenticationManager().getAuthStateReference().get()
                .forConsentedUsersApi
                .requestOAuthAccessToken(STUDY_OAUTH_PROVIDER_KEY, token))
                .subscribe(accessToken -> {
                    LOG.debug("Successfully retrieved accessToken");
                    accessToken.getProviderUserId();
                }, t -> {
                    LOG.warn("Failed to request access token", t);
                    // TODO: retry later using saved AuthState
                });
    }

    public AuthState getAuthState() {
        AuthState authState = mOAuthDAO.getOAuthState(STUDY_OAUTH_PROVIDER_KEY);
        if (authState != null) {
            return authState;
        }
        return new AuthState(AUTHORIZATION_SERVICE_CONFIGURATION);
    }
}
