package com.plnyyanks.frcnotebook;

import android.widget.LinearLayout;

import java.util.HashMap;

/**
 * File created by phil on 2/18/14.
 * Copyright 2015, Phil Lopreiato
 * This file is part of FRC Notebook
 * FRC Notebook is licensed under the MIT License
 * (http://opensource.org/licenses/MIT)
 */
public class Constants {

    public static final String LOG_TAG = "frcnotebook";
    public static final String TBA_HEADER      = "X-TBA-App-Id";
    public static final String TBA_HEADER_TEXT = "plnyyanks:frcNotebook:v2.4";

    public static final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    public static final String DB_BACKUP_NAME = "backup.json";

    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    public static final int SELECT_FILE_REQUEST_CODE = 101;

    public static final HashMap<String, String> MATCH_LEVELS;
    static {
        MATCH_LEVELS = new HashMap<String,String>();
        MATCH_LEVELS.put("Quals","q");
        MATCH_LEVELS.put("q","q");
        MATCH_LEVELS.put("qm","q");
        MATCH_LEVELS.put("Qtr", "qf");
        MATCH_LEVELS.put("Quarters","qf");
        MATCH_LEVELS.put("qf","qf");
        MATCH_LEVELS.put("Semi", "sf");
        MATCH_LEVELS.put("Semis","sf");
        MATCH_LEVELS.put("sf","sf");
        MATCH_LEVELS.put("Finals","f");
        MATCH_LEVELS.put("Final", "f");
        MATCH_LEVELS.put("f","f");
    }

    public enum DATAFEED_SOURCES{
        //TBAv1,
        TBAv2,
        //USFIRST;
    }
}
