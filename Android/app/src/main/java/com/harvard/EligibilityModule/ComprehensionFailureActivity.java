package com.harvard.EligibilityModule;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.harvard.AppConfig;
import com.harvard.R;
import com.harvard.storagemodule.DBServiceSubscriber;
import com.harvard.studyAppModule.ConsentCompletedActivity;
import com.harvard.studyAppModule.StandaloneActivity;
import com.harvard.studyAppModule.StudyActivity;
import com.harvard.studyAppModule.consent.ConsentBuilder;
import com.harvard.studyAppModule.consent.CustomConsentViewTaskActivity;
import com.harvard.studyAppModule.consent.model.Consent;
import com.harvard.studyAppModule.consent.model.EligibilityConsent;
import com.harvard.utils.AppController;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfPCell;

import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.OrderedTask;
import org.researchstack.backbone.task.Task;

import java.util.List;

import io.realm.Realm;

import static com.harvard.studyAppModule.StudyFragment.CONSENT;

public class ComprehensionFailureActivity extends AppCompatActivity {

    private static final int CONSENT_RESPONSECODE = 100;
    DBServiceSubscriber dbServiceSubscriber;
    EligibilityConsent eligibilityConsent;
    Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comprehension_failure);

        TextView retrybutton = (TextView) findViewById(R.id.retrybutton);
        dbServiceSubscriber = new DBServiceSubscriber();
        mRealm = AppController.getRealmobj(this);
        retrybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eligibilityConsent = dbServiceSubscriber.getConsentMetadata(getIntent().getStringExtra("studyId"), mRealm);
                startconsent(eligibilityConsent.getConsent());

            }
        });
    }

    @Override
    protected void onDestroy() {
        dbServiceSubscriber.closeRealmObj(mRealm);
        super.onDestroy();
    }

    private void startconsent(Consent consent) {
        ConsentBuilder consentBuilder = new ConsentBuilder();
        List<Step> consentstep = consentBuilder.createsurveyquestion(this, consent, getIntent().getStringExtra("title"));
        Task consentTask = new OrderedTask(CONSENT, consentstep);
        Intent intent = CustomConsentViewTaskActivity.newIntent(this, consentTask, getIntent().getStringExtra("studyId"), getIntent().getStringExtra("enrollId"), getIntent().getStringExtra("title"), getIntent().getStringExtra("eligibility"), getIntent().getStringExtra("type"));
        startActivityForResult(intent, CONSENT_RESPONSECODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONSENT_RESPONSECODE) {
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent(this, ConsentCompletedActivity.class);
                intent.putExtra("enrollId", getIntent().getStringExtra("enrollId"));
                intent.putExtra("studyId", getIntent().getStringExtra("studyId"));
                intent.putExtra("title", getIntent().getStringExtra("title"));
                intent.putExtra("eligibility", getIntent().getStringExtra("eligibility"));
                intent.putExtra("type", data.getStringExtra(CustomConsentViewTaskActivity.TYPE));
                intent.putExtra("PdfPath", data.getStringExtra("PdfPath"));
                startActivity(intent);
                finish();
            } else if (resultCode == 12345) {
                if (AppConfig.AppType.equalsIgnoreCase(getString(R.string.app_gateway))) {
                    Intent intent = new Intent(this, StudyActivity.class);
                    ComponentName cn = intent.getComponent();
                    Intent mainIntent = Intent.makeRestartActivityTask(cn);
                    startActivity(mainIntent);
                    finish();
                } else {
                    Intent intent = new Intent(this, StandaloneActivity.class);
                    ComponentName cn = intent.getComponent();
                    Intent mainIntent = Intent.makeRestartActivityTask(cn);
                    startActivity(mainIntent);
                    finish();
                }
            } else {
                finish();
            }
        }
    }


    public PdfPCell getImage(Image image, int alignment) {
        PdfPCell cell;
        if (image != null) {
            cell = new PdfPCell(image);
        } else {
            cell = new PdfPCell();
        }
        cell.setPadding(10);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(PdfPCell.ALIGN_BOTTOM);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }



}
