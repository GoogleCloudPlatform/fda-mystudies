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

package org.sagebionetworks.bridge.researchstack;

import org.researchstack.backbone.ResourceManager;
import org.researchstack.backbone.storage.NotificationHelper;
import org.researchstack.backbone.AppPrefs;
import org.sagebionetworks.bridge.android.manager.BridgeManagerProvider;
import org.sagebionetworks.bridge.researchstack.wrapper.StorageAccessWrapper;

import java.util.HashMap;

/**
 * Created by TheMDP on 10/20/17.
 */

public class CrfTaskHelper extends TaskHelper {

    private HashMap<String, String> mCrfResultMap = new HashMap<String, String>() {{
        put("HeartRateCamera_camera","camera_cameraHeartRate_heartRate");
    }};

    public CrfTaskHelper(StorageAccessWrapper storageAccess, ResourceManager resourceManager, AppPrefs appPrefs, NotificationHelper notificationHelper, BridgeManagerProvider bridgeManagerProvider) {
        super(storageAccess, resourceManager, appPrefs, notificationHelper, bridgeManagerProvider);
    }

    /**
     * @param identifier identifier for the result
     * @return the filename to use for the bridge result
     */
    @Override
    public String bridgifyIdentifier(String identifier) {
        String trueIdentifier = identifier;
        if (mCrfResultMap.containsKey(identifier)) {
            trueIdentifier = mCrfResultMap.get(trueIdentifier);
        }
        return super.bridgifyIdentifier(trueIdentifier);
    }
}
