/*
 *    Copyright 2018 Sage Bionetworks
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

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Strings;

import net.openid.appauth.AuthState;

import org.json.JSONException;
import org.sagebionetworks.bridge.android.manager.AuthenticationManager;
import org.sagebionetworks.bridge.android.manager.dao.SharedPreferencesJsonDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by liujoshua on 2/9/2018.
 */

public class OAuthDAO extends
        SharedPreferencesJsonDAO implements AuthenticationManager.AuthenticationEventListener{
    private static final Logger logger = LoggerFactory.getLogger(OAuthDAO.class);

    private static final String PREFERENCES_FILE  = "oauth";

    // in case we store additional objects, let's "namespace" the oauth key
    private static final String OAUTH_KEY_PREFIX = "oAuthProvider-";

    public OAuthDAO(@NonNull Context applicationContext,
                    @NonNull AuthenticationManager authenticationManager) {
        super(applicationContext, PREFERENCES_FILE);

        authenticationManager.addEventListener(this);
    }
    @Nullable
    public AuthState getOAuthState(@NonNull String oAuthProviderKey) {
        checkNotNull(oAuthProviderKey);
        AuthState authState = null;

        try {
            String json = getValue(getOAuthStateKey(oAuthProviderKey), String.class);
            if (!Strings.isNullOrEmpty(json)) {
                authState = AuthState.jsonDeserialize(
                        getValue(getOAuthStateKey(oAuthProviderKey), String.class));
            }
        } catch (JSONException e) {
            logger.warn("Error parsing AuthState", e);
        }

        logger.debug("getAuthState called for oAuthProviderKey: " + oAuthProviderKey
                + ", found: " + (authState != null));

        return authState;
    }

    public void putOAuthState(@NonNull String oAuthProviderKey,
                           @NonNull AuthState authState) {
        checkNotNull(oAuthProviderKey);
        checkNotNull(authState);

        logger.debug("putOAuthState called for oAuthProviderKey " + oAuthProviderKey);

        setValue(getOAuthStateKey(oAuthProviderKey), authState.jsonSerializeString(), String.class);
    }

    private String getOAuthStateKey(String subpopulationGuid) {
        return OAUTH_KEY_PREFIX + subpopulationGuid;
    }

    @Override
    public void onSignedOut(String email) {
        sharedPreferences.edit().clear().apply();
    }

    @Override
    public void onSignedIn(String email) {
        // no-op
    }
}
