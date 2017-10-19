package org.sagebionetworks.bridge.researchstack;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.storage.database.AppDatabase;

import java.util.List;

/**
 * Created by TheMDP on 3/21/17.
 *
 * The CrfEmptyAppDatabase is a no-op implementation of the AppDatabase,
 * since Crf does not use a sql database
 */

public class CrfEmptyAppDatabase implements AppDatabase {

    public CrfEmptyAppDatabase() {
        super();
    }

    @Override
    public void saveTaskResult(TaskResult result) {
        // no-op
    }

    @Override
    public TaskResult loadLatestTaskResult(String taskIdentifier) {
        // no-op
        return null;
    }

    @Override
    public List<TaskResult> loadTaskResults(String taskIdentifier) {
        // no-op
        return null;
    }

    @Override
    public List<StepResult> loadStepResults(String stepIdentifier) {
        // no-op
        return null;
    }

    @Override
    public void setEncryptionKey(String key) {
        // no-op
    }
}
