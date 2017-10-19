package org.sagebionetworks.bridge.researchstack;

import android.content.Context;

import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.answerformat.BooleanAnswerFormat;
import org.researchstack.backbone.answerformat.ChoiceAnswerFormat;
import org.researchstack.backbone.answerformat.DateAnswerFormat;
import org.researchstack.backbone.answerformat.IntegerAnswerFormat;
import org.researchstack.backbone.model.Choice;
import org.researchstack.backbone.step.FormStep;
import org.researchstack.backbone.step.InstructionStep;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.OrderedTask;
import org.researchstack.backbone.task.Task;
import org.researchstack.skin.TaskProvider;
import org.researchstack.skin.task.ConsentTask;
import org.researchstack.skin.task.SignInTask;
import org.researchstack.skin.task.SignUpTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
