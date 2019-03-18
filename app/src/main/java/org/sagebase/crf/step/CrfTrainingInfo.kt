/*
 *    Copyright 2019 Sage Bionetworks
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

package org.sagebase.crf.step

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.webkit.WebView
import android.widget.Button
import org.sagebionetworks.research.crf.R

class CrfTrainingInfo : AppCompatActivity() {

    val trainingInfoUrl = "file:///android_asset/html/crf_heart_rate_training.html"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crf_training_info)
        val webview = findViewById<WebView>(R.id.crf_webview)
        webview.loadUrl(trainingInfoUrl)

        val doneButton = findViewById<Button>(R.id.button_go_forward)
        doneButton.setBackgroundResource(R.drawable.crf_rounded_button_gray)
        doneButton.setTextColor(ResourcesCompat.getColor(resources, R.color.rsb_white, null))
        doneButton.setOnClickListener {
            finish()
        }

    }
}
