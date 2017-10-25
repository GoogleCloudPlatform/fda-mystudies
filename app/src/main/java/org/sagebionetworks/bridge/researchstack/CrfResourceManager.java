package org.sagebionetworks.bridge.researchstack;

import android.text.TextUtils;

import org.researchstack.backbone.ResourcePathManager;
import org.researchstack.backbone.model.ConsentDocument;
import org.researchstack.backbone.ResourceManager;
import org.researchstack.skin.model.ConsentSectionModel;
import org.researchstack.skin.model.InclusionCriteriaModel;
import org.researchstack.backbone.model.SchedulesAndTasksModel;
import org.researchstack.skin.model.SectionModel;
import org.researchstack.skin.model.StudyOverviewModel;
import org.researchstack.skin.model.TaskModel;
import org.researchstack.backbone.onboarding.OnboardingManager;

/**
 * Created by TheMDP on 12/12/16.
 */

public class CrfResourceManager extends ResourceManager {
    public static final int PEM     = 4;
    public static final int SURVEY  = 5;
    
    private static final String BASE_PATH_HTML          = "html";
    private static final String BASE_PATH_JSON          = "json";
    private static final String BASE_PATH_JSON_SURVEY   = "json/survey";
    private static final String BASE_PATH_PDF           = "pdf";
    private static final String BASE_PATH_VIDEO         = "mp4";

    public CrfResourceManager() {
        super();

        // Add all custom resources that we will need access to
        addResource("instruction_test", new Resource(Resource.TYPE_JSON, "json", "instruction_test"));
        addResource("consent", new Resource(Resource.TYPE_JSON, "json", "consent"));
        addResource("eligibility_requirements", new Resource(Resource.TYPE_JSON, "json", "eligibility_requirements"));
    }

    @Override
    public Resource getStudyOverview() {
        return new Resource(Resource.TYPE_JSON,
                BASE_PATH_JSON,
                "study_overview",
                StudyOverviewModel.class);
    }

    @Override
    public Resource getConsentHtml() {
        return new Resource(Resource.TYPE_HTML, BASE_PATH_HTML, "asthma_fullconsent");
    }

    @Override
    public Resource getConsentPDF() {
        return new Resource(Resource.TYPE_PDF, BASE_PATH_HTML, "study_overview_consent_form");
    }

    @Override
    public Resource getConsentSections() {
        return new Resource(Resource.TYPE_JSON,
                BASE_PATH_JSON,
                "consent",
                ConsentSectionModel.class);
    }

    @Override
    public Resource getLearnSections() {
        return new Resource(Resource.TYPE_JSON, BASE_PATH_JSON, "learn", SectionModel.class);
    }

    @Override
    public Resource getPrivacyPolicy() {
        return new Resource(Resource.TYPE_HTML, BASE_PATH_HTML, "app_privacy_policy");
    }

    @Override
    public Resource getSoftwareNotices() {
        return new Resource(Resource.TYPE_HTML, BASE_PATH_HTML, "software_notices");
    }

    @Override
    public Resource getTasksAndSchedules() {
        return new Resource(Resource.TYPE_JSON,
                BASE_PATH_JSON,
                "tasks_and_schedules",
                SchedulesAndTasksModel.class);
    }

    @Override
    public Resource getTask(String taskFileName) {
        return new Resource(Resource.TYPE_JSON,
                BASE_PATH_JSON_SURVEY,
                taskFileName,
                TaskModel.class);
    }

    @Override
    public Resource getInclusionCriteria() {
        return new Resource(Resource.TYPE_JSON,
                BASE_PATH_JSON,
                "eligibility_requirements",
                InclusionCriteriaModel.class);
    }

    @Override
    public Resource getOnboardingManager() {
        return new Resource(Resource.TYPE_JSON,
                BASE_PATH_JSON,
                "onboarding",
                OnboardingManager.class);
    }

    public Resource getConsent() {
        return new Resource(Resource.TYPE_JSON,
                BASE_PATH_JSON,
                "consent",
                ConsentDocument.class);
    }

    @Override
    public String generatePath(int type, String name)
    {
        String dir;
        switch (type) {
            default:
                dir = null;
                break;
            case Resource.TYPE_HTML:
                dir = BASE_PATH_HTML;
                break;
            case Resource.TYPE_JSON:
                dir = BASE_PATH_JSON;
                break;
            case Resource.TYPE_PDF:
                dir = BASE_PATH_PDF;
                break;
            case Resource.TYPE_MP4:
                dir = BASE_PATH_VIDEO;
                break;
            case SURVEY:
                dir = BASE_PATH_JSON_SURVEY;
                break;
        }

        StringBuilder path = new StringBuilder();
        if (!TextUtils.isEmpty(dir)) {
            path.append(dir).append("/");
        }

        return path.append(name).append(".").append(getFileExtension(type)).toString();
    }

    @Override
    public String getFileExtension(int type) {
        switch (type) {
            case PEM:
                return "pem";
            case SURVEY:
                return "json";
            default:
                return super.getFileExtension(type);
        }
    }

    public static class PemResource extends ResourcePathManager.Resource {
        public PemResource(String name) {
            super(CrfResourceManager.PEM, null, name);
        }
    }
}
