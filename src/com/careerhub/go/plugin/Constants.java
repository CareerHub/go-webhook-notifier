package com.careerhub.go.plugin;

import static java.util.Arrays.asList;

import java.util.List;

public class Constants {

    public static final String PLUGIN_ID = "webhook.notifier";
    public static final String EXTENSION_NAME = "notification";
    public static final List<String> GO_SUPPORTED_VERSIONS = asList("1.0");
    
    public static final String PLUGIN_SETTINGS_CALLBACK_URL = "callback_url";
    
    public static final String PLUGIN_SETTINGS_GET_CONFIGURATION = "go.plugin-settings.get-configuration";
    public static final String PLUGIN_SETTINGS_GET_VIEW = "go.plugin-settings.get-view";
    public static final String PLUGIN_SETTINGS_VALIDATE_CONFIGURATION = "go.plugin-settings.validate-configuration";
    public static final String REQUEST_NOTIFICATIONS_INTERESTED_IN = "notifications-interested-in";
    public static final String REQUEST_STAGE_STATUS = "stage-status";


    public static final int SUCCESS_RESPONSE_CODE = 200;
    public static final int NOT_FOUND_RESPONSE_CODE = 404;
    public static final int INTERNAL_ERROR_RESPONSE_CODE = 500;
}
