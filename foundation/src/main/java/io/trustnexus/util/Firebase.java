/*
 * (c) Copyright 2022 ~ Trust Nexus, Inc.
 * All technologies described here in are "Patent Pending". 
 * License information:  http://www.trustnexus.io/license.htm
 * 
 * AS LONG AS THIS NOTICE IS MAINTAINED THE LICENSE PERMITS REDISTRIBUTION OR RE-POSTING  
 * OF THIS SOURCE CODE TO A PUBLIC REPOSITORY (WITH OR WITHOUT MODIFICATIONS)! 
 * 
 * Report License Violations:  trustnexus.io@austin.rr.com 
 * 
 * This is a beta version:  0.0.1
 * Suggestions for code improvements:  trustnexus.io@austin.rr.com
 */

package io.trustnexus.util;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Firebase {

    private static Logger logger = LogManager.getLogger(Firebase.class);

    public static void sendMessage(String notificationBody, HashMap<String, String> firebaseTransferDataMap, String userDeviceIdKey) {

        logger.info("###Entering");

        try {
            URL url = new URL(PropertyManager.getInstance().getProperty(Constants.FIREBASE_API_URL));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "key=" + PropertyManager.getInstance().getProperty(Constants.FIREBASE_AUTH_KEY));
            conn.setRequestProperty("Content-Type", "application/json");

            JsonObject info = Json.createObjectBuilder().add("title", PropertyManager.getInstance().getProperty(Constants.NOTIFICATION_TITLE)).add("body", notificationBody).add("sound", "mySound").build();

            JsonObjectBuilder dataBuilder = Json.createObjectBuilder();

            for (Iterator<String> iterator = firebaseTransferDataMap.keySet().iterator(); iterator.hasNext();) {
                String key = iterator.next();
                dataBuilder.add(key, firebaseTransferDataMap.get(key));
            }

            JsonObject data = dataBuilder.build();

            JsonObject json = Json.createObjectBuilder().add("to", userDeviceIdKey.trim()).add("notification", info).add("data", data).build();

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(json.toString());
            wr.flush();
            conn.getInputStream();

            logger.debug("###!!!Message sent:" + json.toString().length() + " B  " + json.toString());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
