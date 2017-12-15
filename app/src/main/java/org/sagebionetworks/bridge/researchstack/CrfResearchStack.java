package org.sagebionetworks.bridge.researchstack;

import android.content.Context;

import org.researchstack.backbone.storage.database.AppDatabase;
import org.researchstack.backbone.storage.file.EncryptionProvider;
import org.researchstack.backbone.storage.file.FileAccess;
import org.researchstack.backbone.storage.file.PinCodeConfig;
import org.researchstack.backbone.storage.file.SimpleFileAccess;
import org.researchstack.backbone.storage.file.aes.AesProvider;
import org.researchstack.backbone.AppPrefs;
import org.researchstack.backbone.DataProvider;
import org.researchstack.backbone.PermissionRequestManager;
import org.researchstack.backbone.ResearchStack;
import org.researchstack.backbone.ResourceManager;
import org.researchstack.backbone.TaskProvider;
import org.researchstack.backbone.UiManager;
import org.researchstack.backbone.notification.NotificationConfig;
import org.researchstack.backbone.notification.SimpleNotificationConfig;
import org.researchstack.backbone.onboarding.OnboardingManager;

/**
 * Created by TheMDP on 12/12/16.
 */

public class CrfResearchStack extends ResearchStack {

    CrfEmptyAppDatabase mEmptyDb;
    AesProvider mEncryptionProvider;

    CrfResourceManager mResourceManager;
    CrfUiManager mUiManager;

    CrfDataProvider mDataProvider;

    SimpleFileAccess mFileAccess;
    PinCodeConfig mPinCodeConfig;

    TaskProvider mTaskProvider;

    SimpleNotificationConfig mNotificationConfig;

    CrfPermissionRequestManager mPermissionManager;

    CrfOnboardingManager mOnboardingManager;

    public CrfResearchStack(Context context) {

        CrfPrefs.init(context);

        mFileAccess = new SimpleFileAccess();

        mEncryptionProvider = new AesProvider();

        mResourceManager = new CrfResourceManager();

        mNotificationConfig = new SimpleNotificationConfig();

        mPermissionManager = new CrfPermissionRequestManager();
    }

    @Override
    protected AppDatabase createAppDatabaseImplementation(Context context) {
        if (mEmptyDb == null) {
            mEmptyDb = new CrfEmptyAppDatabase();
        }
        return mEmptyDb;
    }

    @Override
    protected FileAccess createFileAccessImplementation(Context context) {
        return mFileAccess;
    }

    @Override
    protected PinCodeConfig getPinCodeConfig(Context context) {
        if (mPinCodeConfig == null) {
            long autoLockTime = AppPrefs.getInstance(context).getAutoLockTime();
            mPinCodeConfig = new PinCodeConfig(autoLockTime);
        }
        return mPinCodeConfig;
    }

    @Override
    protected EncryptionProvider getEncryptionProvider(Context context) {
        return mEncryptionProvider;
    }

    @Override
    protected ResourceManager createResourceManagerImplementation(Context context) {
        return mResourceManager;
    }

    @Override
    protected UiManager createUiManagerImplementation(Context context) {
        if (mUiManager == null) {
            mUiManager = new CrfUiManager();
        }
        return mUiManager;
    }

    @Override
    protected DataProvider createDataProviderImplementation(Context context) {
        if (mDataProvider == null) {
            mDataProvider = new CrfDataProvider();
        }
        return mDataProvider;
    }

    @Override
    protected TaskProvider createTaskProviderImplementation(Context context) {
        if (mTaskProvider == null) {
            mTaskProvider = new CrfTaskProvider(context);
        }
        return mTaskProvider;
    }

    @Override
    protected NotificationConfig createNotificationConfigImplementation(Context context) {
        return mNotificationConfig;
    }

    @Override
    protected PermissionRequestManager createPermissionRequestManagerImplementation(Context context) {
        return mPermissionManager;
    }

    @Override
    public OnboardingManager getOnboardingManager() {
        return mOnboardingManager;
    }

    @Override
    public void createOnboardingManager(Context context) {
        mOnboardingManager = new CrfOnboardingManager(context);
    }
}
