package com.ciao.app.utils;

import android.app.Application;

import com.ciao.app.apppreference.AppSharedPreference;
import com.flurry.android.Constants;
import com.flurry.android.FlurryAgent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by anderick on 7/7/15.
 */
public class AnalyticsUtils {

    public static class Param {

        public String name;
        public String value;

        public Param(String name, String value){
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }


    private static final String FLURRY_APIKEY = "PFJZ3F3HWQ3ZGWY2DHYM";

    public static void startAnalytics(Application app) {
        FlurryAgent.setLogEnabled(false);
        FlurryAgent.init(app, FLURRY_APIKEY);
        String userGender = AppSharedPreference.getInstance(app).getUserGender();
        String userDOB = AppSharedPreference.getInstance(app).getUserDOB();

        try {
            long yearMilliseconds = 1000L * 60 * 60 * 24 * 365;

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date dateOfBirth = df.parse(userDOB);
            Date now = new Date();

            long diff = now.getTime() - dateOfBirth.getTime();

            FlurryAgent.setAge((int) (diff / yearMilliseconds));
        } catch (Exception e) {
        }

        if (userGender != null) {
            if ("male".equals(userGender)) {
                FlurryAgent.setGender(Constants.MALE);
            } else {
                FlurryAgent.setGender(Constants.FEMALE);
            }
        }else {
            FlurryAgent.setGender(Constants.UNKNOWN);
        }
    }

    public static void sendEvent(String event, Param... params) {
        Map<String, String> mapParams = new HashMap<String, String>();

        FlurryAgent.logEvent(event, mapParams);
    }

}
