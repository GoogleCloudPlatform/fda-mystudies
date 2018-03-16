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

import com.google.gson.Gson;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.answerformat.ChoiceAnswerFormat;
import org.researchstack.backbone.answerformat.IntegerAnswerFormat;
import org.researchstack.backbone.model.Choice;
import org.researchstack.backbone.result.FileResult;
import org.researchstack.backbone.result.Result;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;
import org.sagebionetworks.bridge.data.Archive;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by TheMDP on 12/9/17.
 */

public class CrfTaskHelperTest {

    private CrfTaskHelper taskHelper;

    private List<File> files;
    @Before
    public void setUp() throws Exception {
        taskHelper = mock(CrfTaskHelper.class);
        Mockito.doCallRealMethod().when(taskHelper).setArchiveFileFactory(any());
        taskHelper.setArchiveFileFactory(new CrfTaskHelper.CrfArchiveFileFactory());
        Mockito.doCallRealMethod().when(taskHelper).addFiles(any(), any(), anyString());
    }
    
    @After
    public void cleanUp() {
        for(File f : files) {
            f.delete();
        }
    }

    @Test
    public void answerMapGrouping12MinWalk_test() {
        // Background surveys should be parsed by default behavior and not bundled in the answer map
        String taskId = CrfTaskFactory.TASK_ID_CARDIO_12MT;
        List<Result> flattenedResults = new ArrayList<>();

        // The 12 min walk has 3 recorders, 2 heart rates and a walk
        flattenedResults.add(createFileResult("HeartRateCamera_heartRate.before", "HeartRateCamera_heartRate.before.json", "json"));
        flattenedResults.add(createFileResult("HeartRateCamera_heartRate.after", "HeartRateCamera_heartRate.after.json", "json"));
        flattenedResults.add(createFileResult("location_run",    "location_run",    "json"));
        // These should get ignored by the answer groups since they only contain FileResults
        flattenedResults.add(createFileStepResult("HeartRateCamera_heartRate.before", "HeartRateCamera_heartRate.before.json", "json"));
        flattenedResults.add(createFileStepResult("HeartRateCamera_heartRate.after", "HeartRateCamera_heartRate.after.json", "json"));
        flattenedResults.add(createFileStepResult("location_run",    "location_run",    "json"));
        // Create a question step that should be bundled in the answers map, but shouldn't with background survey
        flattenedResults.add(createHeartBeatResult("beat1", 51));
        flattenedResults.add(createHeartBeatResult("beat2", 52));
        flattenedResults.add(createHeartBeatResult("beat3", 53));
        flattenedResults.add(createHeartBeatResult("beat4", 54));
        // Create a question step that should be bundled in the answers map
        flattenedResults.add(createSurveyResult("choice", "value"));

        Archive.Builder builder = createArchiveBuilder(taskId);
        taskHelper.addFiles(builder, flattenedResults, taskId);
        String[] archiveFiles = filenameList(builder);

        // 3 recorders, and 1 answers file
        assertEquals(12, archiveFiles.length);

        // CRF_RESULT_CONVERSION_MAP should change the json filenames to these below
        assertEquals("heartRate_before_recorder.json", archiveFiles[0]);
        assertEquals("heartRate_after_recorder.json", archiveFiles[1]);
        assertEquals("location.json",    archiveFiles[2]);
        assertEquals(CrfTaskHelper.ANSWERS_FILENAME, archiveFiles[11]);

        String answerMapJson = jsonForFilename(builder, CrfTaskHelper.ANSWERS_FILENAME);
        String expectedJson = "{\"beat2\":52,\"beat3\":53,\"beat4\":54,\"choice\":\"value\",\"beat1\":51}";
        assertEquals(expectedJson, answerMapJson.replaceAll("\\s",""));
    }

    @Test
    public void answerMapGroupingStairStep_test() {
        // Background surveys should be parsed by default behavior and not bundled in the answer map
        String taskId = CrfTaskFactory.TASK_ID_STAIR_STEP;
        List<Result> flattenedResults = new ArrayList<>();

        // The stair step has 3 recorders, 2 heart rates and a mtion
        flattenedResults.add(createFileResult("HeartRateCamera_heartRate.before", "HeartRateCamera_heartRate.before.json", "json"));
        flattenedResults.add(createFileResult("HeartRateCamera_heartRate.after", "HeartRateCamera_heartRate.after.json", "json"));
        flattenedResults.add(createFileResult("motion_stairStep",    "motion_stairStep",    "json"));
        flattenedResults.add(createFileResult("stairPhoto",    "stairPhoto.jpeg",    "image/jpeg"));
        // These should get ignored by the answer groups since they only contain FileResults
        flattenedResults.add(createFileStepResult("HeartRateCamera_heartRate.before", "HeartRateCamera_heartRate.before.json", "json"));
        flattenedResults.add(createFileStepResult("HeartRateCamera_heartRate.after", "HeartRateCamera_heartRate.after.json", "json"));
        flattenedResults.add(createFileStepResult("motion_stairStep",    "motion_stairStep",    "json"));
        flattenedResults.add(createFileStepResult("stairPhoto",    "stairPhoto.jpeg",    "image/jpeg"));
        // Create a question step that should be bundled in the answers map
        flattenedResults.add(createHeartBeatResult("beat1", 51));
        flattenedResults.add(createHeartBeatResult("beat2", 52));
        flattenedResults.add(createHeartBeatResult("beat3", 53));
        flattenedResults.add(createHeartBeatResult("beat4", 54));
        // Create a question step that should be bundled in the answers map
        flattenedResults.add(createSurveyResult("choice", "value"));

        Archive.Builder builder = createArchiveBuilder(taskId);
        taskHelper.addFiles(builder, flattenedResults, taskId);
        String[] archiveFiles = filenameList(builder);

        // 3 recorders, and 1 answers file
        assertEquals(14, archiveFiles.length);

        assertEquals("heartRate_before_recorder.json", archiveFiles[0]);
        assertEquals("heartRate_after_recorder.json", archiveFiles[1]);
        assertEquals("stairStep_motion.json", archiveFiles[2]);
        assertEquals("stairPhoto.jpeg",     archiveFiles[3]);
        assertEquals(CrfTaskHelper.ANSWERS_FILENAME, archiveFiles[13]);

        String answerMapJson = jsonForFilename(builder, CrfTaskHelper.ANSWERS_FILENAME);
        String expectedJson = "{\"beat2\":52,\"beat3\":53,\"beat4\":54,\"choice\":\"value\",\"beat1\":51}";
        assertEquals(expectedJson, answerMapJson.replaceAll("\\s",""));
    }

    @Test
    public void answerMapGroupingCardioTest_test() {
        // Not needed since it the schema is the same as 12 min walk
    }

    @Test
    public void backgroundSurveyResultParsing_test() {
        // Background surveys should be parsed by default behavior and not bundled in the answer map
        String taskId = CrfTaskFactory.TASK_ID_BACKGROUND_SURVEY;
        List<Result> flattenedResults = new ArrayList<>();

        // Create a question step that would normally be bundled in the answers map, but shouldn't with background survey
        flattenedResults.add(createIntegerQuestionResult());
        // Create a question step that would normally be bundled in the answers map, but shouldn't with background survey
        flattenedResults.add(createChoiceQuestionResult("choice"));

        Archive.Builder builder = createArchiveBuilder(taskId);
        taskHelper.addFiles(builder, flattenedResults, taskId);
        String[] archiveFiles = filenameList(builder);

        assertEquals(3, archiveFiles.length);
        assertEquals("year.json", archiveFiles[0]);
        assertEquals("choice.json", archiveFiles[1]);
    }

    private Archive.Builder createArchiveBuilder(String taskId) {
        return Archive.Builder.forActivity(taskId)
                .withAppVersionName("1.0")
                .withPhoneInfo("Android");
    }

    private FileResult createFileResult(String id, String filename, String contentType) {
        File f = new File(filename);
        try {
            f.createNewFile();
        } catch (IOException e) {
            fail();
        }
        files.add(f);
    
        FileResult fileResult = new FileResult(id, f, contentType);
        fileResult.setEndDate(new Date());
        return fileResult;
    }

    private StepResult<Result> createFileStepResult(String id, String filename, String contentType) {
        StepResult<Result> fileStepResult = new StepResult<>(new Step(id));
        FileResult fileResult = createFileResult(id, filename, contentType);
        fileStepResult.setResult(fileResult);
        return fileStepResult;
    }

    private StepResult<Integer> createIntegerQuestionResult() {
        QuestionStep yearStep = new QuestionStep("year", null, new IntegerAnswerFormat(0, 10));
        StepResult<Integer> yearResult = new StepResult<>(yearStep);
        yearResult.setResult(5);
        return yearResult;
    }

    private StepResult<Integer> createHeartBeatResult(String id, int heartRate) {
        StepResult<Integer> result = new StepResult<>(new Step(id));
        result.setResult(heartRate);
        return result;
    }

    private StepResult<String> createSurveyResult(String id, String answer) {
        StepResult<String> result = new StepResult<>(new Step(id));
        result.setResult(answer);
        return result;
    }

    private StepResult<String> createChoiceQuestionResult(String id) {
        QuestionStep choiceStep = new QuestionStep(id, null,
                new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle.SingleChoice,
                        new Choice<>("A", "A"), new Choice<>("B", "B")));
        StepResult<String> choiceResult = new StepResult<>(choiceStep);
        choiceResult.setResult("A");
        return choiceResult;
    }

    private FilenameListHolder filenameListHolderFromBuilder(Archive.Builder builder) {
        // Use gson to find and test the relevant assumptions of what is in the builder object
        Gson gson = new Gson();
        String builderJson = gson.toJson(builder);
        FilenameListHolder filenameListHolder = gson.fromJson(builderJson, FilenameListHolder.class);
        return filenameListHolder;
    }

    private String[] filenameList(Archive.Builder builder) {
        FilenameListHolder filenameListHolder = filenameListHolderFromBuilder(builder);
        String[] filenames = new String[filenameListHolder.files.length];
        for(int i = 0; i < filenameListHolder.files.length; i++) {
            filenames[i] = filenameListHolder.files[i].filename;
        }
        return filenames;
    }

    private String jsonForFilename(Archive.Builder builder, String filename) {
        FilenameListHolder filenameListHolder = filenameListHolderFromBuilder(builder);
        for (FilenameListHolder.FileHolder holder : filenameListHolder.files) {
            if (holder.filename.equals(filename)) {
                return holder.json;
            }
        }
        return null;
    }

    class FilenameListHolder {
        FileHolder[] files;
        class FileHolder {
            String filename;
            String json;
        }
    }
}
