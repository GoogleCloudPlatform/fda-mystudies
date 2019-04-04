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
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.widget.Button
import android.widget.TextView
import org.sagebase.crf.R

const val EXTRA_HTML_FILENAME = "EXTRA_FILENAME"
const val EXTRA_TITLE = "EXTRA_TITLE"

class CrfTrainingInfo : AppCompatActivity() {

    val baseFilePath = "file:///android_asset/html/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crf_training_info)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val titleString = intent.extras.getString(EXTRA_TITLE)
        val titleTextView = findViewById<TextView>(R.id.title)
        titleTextView.text = titleString

        val webview = findViewById<WebView>(R.id.crf_webview)
        val url = intent.extras.getString(EXTRA_HTML_FILENAME)
        webview.loadUrl(baseFilePath + url)

        val doneButton = findViewById<Button>(R.id.button_go_forward)
//        doneButton.setBackgroundResource(R.drawable.crf_rounded_button_gray)
//        doneButton.setTextColor(ResourcesCompat.getColor(resources, R.color.rsb_white, null))
//        doneButton.setOnClickListener {
//            finish()
//        }

    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

}
