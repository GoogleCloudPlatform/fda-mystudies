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

package com.harvard.studyappmodule.consent;

import android.content.Context;
import com.harvard.R;
import com.harvard.eligibilitymodule.StepsBuilder;
import com.harvard.studyappmodule.activitybuilder.model.servicemodel.Steps;
import com.harvard.studyappmodule.consent.consentsharingstepcustom.ConsentSharingStepCustom;
import com.harvard.studyappmodule.consent.model.Consent;
import com.harvard.studyappmodule.consent.model.ConsentSectionCustomImage;
import com.harvard.studyappmodule.custom.ChoiceAnswerFormatCustom;
import com.harvard.studyappmodule.custom.QuestionStepCustom;
import com.harvard.studyappmodule.custom.question.TextAnswerFormatRegex;
import io.realm.RealmList;
import java.util.ArrayList;
import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.answerformat.ChoiceAnswerFormat;
import org.researchstack.backbone.model.Choice;
import org.researchstack.backbone.model.ConsentSection;
import org.researchstack.backbone.model.ConsentSignature;
import org.researchstack.backbone.step.ConsentDocumentStep;
import org.researchstack.backbone.step.ConsentSignatureStep;
import org.researchstack.backbone.step.ConsentVisualStep;
import org.researchstack.backbone.step.InstructionStep;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.step.layout.ConsentSignatureStepLayout;

public class ConsentBuilder {

  public ArrayList<Step> createsurveyquestion(Context context, Consent consent, String pdftitle) {
    ArrayList<Step> visualSteps = new ArrayList<>();
    ConsentSection consentSection;
    ConsentVisualStep visualStep;
    for (int i = 0; i < consent.getVisualScreens().size(); i++) {
      if (consent.getVisualScreens().get(i).isVisualStep()) {
        if (!consent.getVisualScreens().get(i).getType().equalsIgnoreCase("Custom")) {
          switch (consent.getVisualScreens().get(i).getType().toLowerCase()) {
            case "datagathering":
              consentSection = new ConsentSection(ConsentSection.Type.DataGathering);
              consentSection.setTitle(consent.getVisualScreens().get(i).getTitle());
              consentSection.setContent(consent.getVisualScreens().get(i).getDescription());
              consentSection.setSummary(consent.getVisualScreens().get(i).getText());
              consentSection.setHtmlContent(consent.getVisualScreens().get(i).getHtml());

              visualStep = new ConsentVisualStep(consent.getVisualScreens().get(i).getTitle());
              visualStep.setStepTitle(R.string.notxt);
              visualStep.setSection(consentSection);
              visualStep.setNextButtonString(context.getResources().getString(R.string.next1));
              visualSteps.add(visualStep);
              break;
            case "datause":
              consentSection = new ConsentSection(ConsentSection.Type.DataUse);
              consentSection.setTitle(consent.getVisualScreens().get(i).getTitle());
              consentSection.setContent(consent.getVisualScreens().get(i).getDescription());
              consentSection.setSummary(consent.getVisualScreens().get(i).getText());
              consentSection.setHtmlContent(consent.getVisualScreens().get(i).getHtml());

              visualStep = new ConsentVisualStep(consent.getVisualScreens().get(i).getTitle());
              visualStep.setStepTitle(R.string.notxt);
              visualStep.setSection(consentSection);
              visualStep.setNextButtonString(context.getResources().getString(R.string.next1));
              visualSteps.add(visualStep);
              break;
            case "onlyindocument":
              consentSection = new ConsentSection(ConsentSection.Type.OnlyInDocument);
              consentSection.setTitle(consent.getVisualScreens().get(i).getTitle());
              consentSection.setContent(consent.getVisualScreens().get(i).getDescription());
              consentSection.setSummary(consent.getVisualScreens().get(i).getText());
              consentSection.setHtmlContent(consent.getVisualScreens().get(i).getHtml());

              visualStep = new ConsentVisualStep(consent.getVisualScreens().get(i).getTitle());
              visualStep.setStepTitle(R.string.notxt);
              visualStep.setSection(consentSection);
              visualStep.setNextButtonString(context.getResources().getString(R.string.next1));
              visualSteps.add(visualStep);
              break;
            case "overview":
              consentSection = new ConsentSection(ConsentSection.Type.Overview);
              consentSection.setTitle(consent.getVisualScreens().get(i).getTitle());
              consentSection.setContent(consent.getVisualScreens().get(i).getDescription());
              consentSection.setSummary(consent.getVisualScreens().get(i).getText());
              consentSection.setHtmlContent(consent.getVisualScreens().get(i).getHtml());

              visualStep = new ConsentVisualStep(consent.getVisualScreens().get(i).getTitle());
              visualStep.setStepTitle(R.string.notxt);
              visualStep.setSection(consentSection);
              visualStep.setNextButtonString(context.getResources().getString(R.string.next1));
              visualSteps.add(visualStep);
              break;
            case "privacy":
              consentSection = new ConsentSection(ConsentSection.Type.Privacy);
              consentSection.setTitle(consent.getVisualScreens().get(i).getTitle());
              consentSection.setContent(consent.getVisualScreens().get(i).getDescription());
              consentSection.setSummary(consent.getVisualScreens().get(i).getText());
              consentSection.setHtmlContent(consent.getVisualScreens().get(i).getHtml());

              visualStep = new ConsentVisualStep(consent.getVisualScreens().get(i).getTitle());
              visualStep.setStepTitle(R.string.notxt);
              visualStep.setSection(consentSection);
              visualStep.setNextButtonString(context.getResources().getString(R.string.next1));
              visualSteps.add(visualStep);
              break;
            case "studysurvey":
              consentSection = new ConsentSection(ConsentSection.Type.StudySurvey);
              consentSection.setTitle(consent.getVisualScreens().get(i).getTitle());
              consentSection.setContent(consent.getVisualScreens().get(i).getDescription());
              consentSection.setSummary(consent.getVisualScreens().get(i).getText());
              consentSection.setHtmlContent(consent.getVisualScreens().get(i).getHtml());

              visualStep = new ConsentVisualStep(consent.getVisualScreens().get(i).getTitle());
              visualStep.setStepTitle(R.string.notxt);
              visualStep.setSection(consentSection);
              visualStep.setNextButtonString(context.getResources().getString(R.string.next1));
              visualSteps.add(visualStep);
              break;
            case "studytasks":
              consentSection = new ConsentSection(ConsentSection.Type.StudyTasks);
              consentSection.setTitle(consent.getVisualScreens().get(i).getTitle());
              consentSection.setContent(consent.getVisualScreens().get(i).getDescription());
              consentSection.setSummary(consent.getVisualScreens().get(i).getText());
              consentSection.setHtmlContent(consent.getVisualScreens().get(i).getHtml());

              visualStep = new ConsentVisualStep(consent.getVisualScreens().get(i).getTitle());
              visualStep.setStepTitle(R.string.notxt);
              visualStep.setSection(consentSection);
              visualStep.setNextButtonString(context.getResources().getString(R.string.next1));
              visualSteps.add(visualStep);
              break;
            case "timecommitment":
              consentSection = new ConsentSection(ConsentSection.Type.TimeCommitment);
              consentSection.setTitle(consent.getVisualScreens().get(i).getTitle());
              consentSection.setContent(consent.getVisualScreens().get(i).getDescription());
              consentSection.setSummary(consent.getVisualScreens().get(i).getText());
              consentSection.setHtmlContent(consent.getVisualScreens().get(i).getHtml());

              visualStep = new ConsentVisualStep(consent.getVisualScreens().get(i).getTitle());
              visualStep.setStepTitle(R.string.notxt);
              visualStep.setSection(consentSection);
              visualStep.setNextButtonString(context.getResources().getString(R.string.next1));
              visualSteps.add(visualStep);
              break;
            case "withdrawing":
              consentSection = new ConsentSection(ConsentSection.Type.Withdrawing);
              consentSection.setTitle(consent.getVisualScreens().get(i).getTitle());
              consentSection.setContent(consent.getVisualScreens().get(i).getDescription());
              consentSection.setSummary(consent.getVisualScreens().get(i).getText());
              consentSection.setHtmlContent(consent.getVisualScreens().get(i).getHtml());

              visualStep = new ConsentVisualStep(consent.getVisualScreens().get(i).getTitle());
              visualStep.setStepTitle(R.string.notxt);
              visualStep.setSection(consentSection);
              visualStep.setNextButtonString(context.getResources().getString(R.string.next1));
              visualSteps.add(visualStep);

              break;
          }
        } else {
          // custom consent
          ConsentSectionCustomImage consentSection1 =
              new ConsentSectionCustomImage(ConsentSection.Type.Custom);
          consentSection1.setTitle(consent.getVisualScreens().get(i).getTitle());
          consentSection1.setContent(consent.getVisualScreens().get(i).getDescription());
          consentSection1.setSummary(consent.getVisualScreens().get(i).getText());
          consentSection1.setHtmlContent(consent.getVisualScreens().get(i).getHtml());
          consentSection1.setCustomImageName("task_img2");

          visualStep = new ConsentVisualStep(consent.getVisualScreens().get(i).getTitle());
          visualStep.setStepTitle(R.string.notxt);
          visualStep.setSection(consentSection1);
          visualStep.setNextButtonString(context.getResources().getString(R.string.next1));
          visualSteps.add(visualStep);
        }
      }
    }

    if (consent.getComprehension() != null
        && consent.getComprehension().getQuestions() != null
        && consent.getComprehension().getQuestions().size() > 0) {

      InstructionStep instructionStep =
          new InstructionStep(
              "key",
              "Comprehension",
              "Let's do a quick and simple test of your understanding of this Study.");
      instructionStep.setStepTitle(R.string.notxt);
      instructionStep.setOptional(false);
      visualSteps.add(instructionStep);

      RealmList<Steps> stepsRealmList = consent.getComprehension().getQuestions();
      StepsBuilder stepsBuilder = new StepsBuilder(context, stepsRealmList, true);

      visualSteps.addAll(stepsBuilder.getsteps());
    }

    if (!consent.getSharing().getTitle().equalsIgnoreCase("")
        && !consent.getSharing().getText().equalsIgnoreCase("")
        && !consent.getSharing().getShortDesc().equalsIgnoreCase("")
        && !consent.getSharing().getLongDesc().equalsIgnoreCase("")) {
      ConsentSharingStepCustom consentSharingStep =
          new ConsentSharingStepCustom("sharing", consent.getSharing().getLearnMore());
      consentSharingStep.setText(consent.getSharing().getText());
      consentSharingStep.setTitle(consent.getSharing().getTitle());
      Choice[] choices = new Choice[2];
      choices[0] =
          new Choice(
              "Share my data with "
                  + consent.getSharing().getShortDesc()
                  + " and qualified researchers worldwide",
              "Provided",
              "yes");
      choices[1] =
          new Choice(
              "Only share my data with " + consent.getSharing().getLongDesc(), "Not Provided", "no");

      AnswerFormat choiceAnswerFormat =
          new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle.SingleChoice, choices);
      consentSharingStep.setAnswerFormat(choiceAnswerFormat);
      consentSharingStep.setOptional(false);
      consentSharingStep.setStepTitle(R.string.notxt);
      visualSteps.add(consentSharingStep);
    }

    if (consent.getReview() != null
        && consent.getReview().getReviewHTML() != null
        && !consent.getReview().getReviewHTML().equalsIgnoreCase("")) {
      StringBuilder docBuilder =
          new StringBuilder("</br><div style=\"padding: 10px 10px 10px 10px;\" class='header'>");
      String title = context.getString(R.string.review);
      docBuilder.append(
          String.format(
              "<h1 style=\"text-align: center; font-family:sans-serif-light;color:#007cba;\">%1$s</h1>",
              title));
      String detail = context.getString(R.string.reviewmsg);
      docBuilder.append(String.format("<p style=\"text-align: center\">%1$s</p>", detail));
      docBuilder.append("</div></br>");
      docBuilder.append("<div> <h2 style=\"color:#007cba;\"> " + pdftitle + "<h2> </div>");
      docBuilder.append("</div></br>");
      docBuilder.append("<div>" + consent.getReview().getReviewHTML() + "</div>");

      ConsentDocumentStep documentStep = new ConsentDocumentStepCustom("review");
      documentStep.setConsentHTML(docBuilder.toString());
      documentStep.setStepTitle(R.string.notxt);
      documentStep.setConfirmMessage(context.getString(R.string.consentConfirmation));
      visualSteps.add(documentStep);
    } else {
      if (consent.getVisualScreens().size() > 0) {
        // Create our HTML to show the user and have them accept or decline.
        StringBuilder docBuilder =
            new StringBuilder("</br><div style=\"padding: 10px 10px 10px 10px;\" class='header'>");
        String title = context.getString(R.string.review);
        docBuilder.append(
            String.format(
                "<h1 style=\"text-align: center; font-family:sans-serif-light;color:#007cba;\">%1$s</h1>",
                title));
        String detail = context.getString(R.string.reviewmsg);
        docBuilder.append(String.format("<p style=\"text-align: center\">%1$s</p>", detail));
        docBuilder.append("</div></br>");
        docBuilder.append(
            "<div> <h2 style=\"font-family:sans-serif-light;color:#007cba;\"> "
                + pdftitle
                + " <h2> </div>");
        docBuilder.append("</div></br>");
        for (int i = 0; i < consent.getVisualScreens().size(); i++) {
          docBuilder.append(
              "<div> <h3 style=\"font-family:sans-serif-light;color:#007cba;\"> "
                  + consent.getVisualScreens().get(i).getTitle()
                  + "<h3> </div>");
          docBuilder.append("</br>");
          docBuilder.append("<div>" + consent.getVisualScreens().get(i).getHtml() + "</div>");
          docBuilder.append("</br>");
        }
        ConsentDocumentStep documentStep = new ConsentDocumentStepCustom("review");
        documentStep.setConsentHTML(docBuilder.toString());
        documentStep.setStepTitle(R.string.notxt);
        documentStep.setOptional(false);
        documentStep.setConfirmMessage(context.getString(R.string.consentConfirmation));
        visualSteps.add(documentStep);
      }
    }

    TextAnswerFormatRegex textAnswerFormat =
            new TextAnswerFormatRegex(Integer.MAX_VALUE,
                    "",
                    "");
    textAnswerFormat.setIsMultipleLines(false);
    QuestionStepCustom firstName =
            new QuestionStepCustom(
                    context.getResources().getString(R.string.first_name),
                    context.getResources().getString(R.string.first_name),
                    textAnswerFormat);
    firstName.setPlaceholder(context.getResources().getString(R.string.required));
    firstName.setAnswerFormat1(textAnswerFormat);
    firstName.setOptional(false);

    QuestionStepCustom lastName =
            new QuestionStepCustom(
                    context.getResources().getString(R.string.last_name),
                    context.getResources().getString(R.string.last_name),
                    textAnswerFormat);
    lastName.setPlaceholder(context.getResources().getString(R.string.required));
    lastName.setAnswerFormat1(textAnswerFormat);
    lastName.setOptional(false);

    ArrayList<QuestionStep> questionStepCustom = new ArrayList<>();
    questionStepCustom.add(firstName);
    questionStepCustom.add(lastName);

    QuestionStepCustom formStep =
            new QuestionStepCustom(context.getResources().getString(R.string.signature_form_step));

    ChoiceAnswerFormatCustom formAnswerFormat =
        new ChoiceAnswerFormatCustom(
            ChoiceAnswerFormatCustom.CustomAnswerStyle.Form,
            formStep,
            questionStepCustom,
            false,
            "");
    formStep.setAnswerFormat1(formAnswerFormat);
    formStep.setOptional(false);
    visualSteps.add(formStep);

    ConsentSignature signature = new ConsentSignature();
    signature.setRequiresName(true);
    signature.setRequiresSignatureImage(true);

    ConsentSignatureStep signatureStep =
        new ConsentSignatureStep(context.getResources().getString(R.string.signature));
    signatureStep.setStepTitle(R.string.notxt);
    signatureStep.setTitle(context.getString(R.string.signtitle));
    signatureStep.setText(context.getString(R.string.signdesc));
    signatureStep.setSignatureDateFormat(signature.getSignatureDateFormatString());
    signatureStep.setOptional(false);
    signatureStep.setStepLayoutClass(ConsentSignatureStepLayout.class);

    visualSteps.add(signatureStep);
    return visualSteps;
  }
}
