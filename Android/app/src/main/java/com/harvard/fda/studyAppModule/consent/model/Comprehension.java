/*
 * Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
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

package com.harvard.fda.studyappmodule.consent.model;

import com.harvard.fda.studyappmodule.activitybuilder.model.servicemodel.Steps;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Naveen Raj on 03/28/2017.
 */

public class Comprehension extends RealmObject {
    private RealmList<ComprehensionCorrectAnswers> correctAnswers;

    private RealmList<Steps> questions;

    private String passScore;

    public String getPassScore() {
        return passScore;
    }

    public void setPassScore(String passScore) {
        this.passScore = passScore;
    }

    public RealmList<ComprehensionCorrectAnswers> getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(RealmList<ComprehensionCorrectAnswers> correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public RealmList<Steps> getQuestions() {
        return questions;
    }

    public void setQuestions(RealmList<Steps> questions) {
        this.questions = questions;
    }
}
