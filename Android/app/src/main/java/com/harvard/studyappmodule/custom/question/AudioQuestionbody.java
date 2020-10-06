/*
 * Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Copyright 2020 Google LLC
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration (“Funding Agency”) effective 18 September 2014 as Contract no. HHSF22320140030I/HHSF22301006T (the “Prime Contract”).
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.harvard.studyappmodule.custom.question;

import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.harvard.R;
import com.harvard.studyappmodule.custom.QuestionStepCustom;
import com.harvard.utils.Logger;
import java.io.IOException;
import java.util.Random;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.step.body.BodyAnswer;
import org.researchstack.backbone.ui.step.body.StepBody;

public class AudioQuestionbody implements StepBody {
  private StepResult<String> result;
  private MediaRecorder mediaRecorder;
  private boolean next;
  private String audioSavePathInDevice = null;

  public AudioQuestionbody(Step step, StepResult result) {
    QuestionStepCustom step1 = (QuestionStepCustom) step;
    this.result = result == null ? new StepResult<>(step1) : result;
  }

  @Override
  public View getBodyView(int viewType, final LayoutInflater inflater, ViewGroup parent) {
    LinearLayout linearLayout = new LinearLayout(inflater.getContext());
    linearLayout.setOrientation(LinearLayout.VERTICAL);
    Toast.makeText(inflater.getContext(), R.string.recording, Toast.LENGTH_SHORT).show();
    audioSavePathInDevice =
        Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/FDA/"
            + createRandomAudioFileName()
            + ".3gp";
    mediaRecorderReady();

    try {
      mediaRecorder.prepare();
      mediaRecorder.start();
      next = false;
    } catch (IllegalStateException | IOException e) {
      Logger.log(e);
    }

    Handler handler = new Handler();
    handler.postDelayed(
        new Runnable() {
          public void run() {
            next = true;
            Toast.makeText(inflater.getContext(), R.string.stopping, Toast.LENGTH_SHORT).show();
            mediaRecorder.stop();
            mediaRecorder.release();
            // Actions to do after 10 seconds
          }
        },
        10000);

    return linearLayout;
  }

  @Override
  public StepResult getStepResult(boolean skipped) {
    if (skipped) {
      result.setResult(null);
    } else {
      result.setResult("" + audioSavePathInDevice);
    }
    return result;
  }

  @Override
  public BodyAnswer getBodyAnswerState() {
    if (next) {
      return BodyAnswer.VALID;
    }
    return BodyAnswer.INVALID;
  }

  private void mediaRecorderReady() {
    mediaRecorder = new MediaRecorder();
    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
    mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
    mediaRecorder.setOutputFile(audioSavePathInDevice);
  }

  private String createRandomAudioFileName() {
    Random random = new Random();
    String randomAudioFileName = "ABCDEFGHIJKLMNOP";
    StringBuilder stringBuilder = new StringBuilder(5);
    int i = 0;
    while (i < 5) {
      stringBuilder.append(
              randomAudioFileName.charAt(random.nextInt(randomAudioFileName.length())));

      i++;
    }
    return stringBuilder.toString();
  }
}
