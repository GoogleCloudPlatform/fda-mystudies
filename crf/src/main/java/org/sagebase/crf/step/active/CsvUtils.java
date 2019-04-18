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

package org.sagebase.crf.step.active;

import android.content.Context;
import android.content.res.AssetManager;
import java.lang.ClassLoader;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVRecord;


public class CsvUtils {

    static final String[] highAndLowPassHeaders = new String[] {"","b1","b2","b3","b4","b5","b6","b7","b8","a1","a2","a3","a4","a5","a6","a7","a8","filter_type","sampling_rate"};

    static final String HIGH_PASS_PARAMS_FILE = "csv/highpass_filter_params.csv";
    static final String LOW_PASS_PARAMS_FILE = "csv/lowpass_filter_params.csv";

    public static Map<Integer, PassFilterParams> getHighPassFilterParams() {
        return loadPassFilter(HIGH_PASS_PARAMS_FILE);
    }

    public static Map<Integer, PassFilterParams> getLowPassFilterParams() {
        return loadPassFilter(LOW_PASS_PARAMS_FILE);
    }

    private static Map<Integer, PassFilterParams> loadPassFilter(String filename) {
        Map<Integer, PassFilterParams> filterParamsMap = new HashMap<>();
        List<CSVRecord> rows = loadFile(filename, highAndLowPassHeaders);
        for (CSVRecord row: rows) {
            PassFilterParams params = new PassFilterParams(row);
            filterParamsMap.put(params.samplingRate, params);
        }
        return filterParamsMap;
    }

    private static List<CSVRecord> loadFile(String filename, String[] headers) {

        try {

            ClassLoader loader = CsvUtils.class.getClassLoader();
            InputStream inputStream = loader.getResourceAsStream(filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            CSVParser csvParser = new CSVParser(br, CSVFormat.DEFAULT
                    .withHeader(headers)
                    .withIgnoreHeaderCase()
                    .withSkipHeaderRecord()
                    .withAllowMissingColumnNames()
                    .withTrim());

            List<CSVRecord> rows = csvParser.getRecords();
            return rows;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class PassFilterParams {

        static final String[] bHeaders = new String[] {"b1","b2","b3","b4","b5","b6","b7","b8"};
        static final String[] aHeaders = new String[] {"a1","a2","a3","a4","a5","a6","a7","a8"};
        static final String SAMPLING_RATE = "sampling_rate";
        static final String FILTER_TYPE = "filter_type";

        public final int samplingRate;
        public final String filterType;

        public final double[] bParams;
        public final double[] aParams;

        public PassFilterParams(CSVRecord record) {
            samplingRate = Integer.parseInt(record.get(SAMPLING_RATE));
            filterType = record.get(FILTER_TYPE);

            bParams = new double[bHeaders.length];
            aParams = new double[aHeaders.length];
            loadParams(record, bParams, bHeaders);
            loadParams(record, aParams, aHeaders);
        }

        private void loadParams(CSVRecord record, double[] params, String[] headers) {
            for (int i=0; i<headers.length; i++) {
                params[i] = Double.parseDouble(record.get(headers[i]));
            }
        }


    }

}
