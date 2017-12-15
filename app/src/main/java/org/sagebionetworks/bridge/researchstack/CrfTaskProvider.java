package org.sagebionetworks.bridge.researchstack;

import android.content.Context;

import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.TaskProvider;

import java.util.HashMap;

/**
 * Created by TheMDP on 12/12/16.
 */

public class CrfTaskProvider extends TaskProvider {

    private HashMap<String, Task> map = new HashMap<>();

    public CrfTaskProvider(Context context) {
    }

    @Override
    public Task get(String taskId) {
        return map.get(taskId);
    }

    @Override
    public void put(String id, Task task) {
        map.put(id, task);
    }
}
