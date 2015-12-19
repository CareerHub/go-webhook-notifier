package com.careerhub.go.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.careerhub.go.plugin.util.JSONUtils;
import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.GoPluginIdentifier;
import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.request.GoApiRequest;
import com.thoughtworks.go.plugin.api.response.GoApiResponse;

public class SettingsService {
    private static Logger LOGGER = Logger.getLoggerFor(SettingsService.class);

    public static final String GET_PLUGIN_SETTINGS = "go.processor.plugin-settings.get";

    public PluginSettings getSettings(GoApplicationAccessor goApplicationAccessor) {
        Map<String, Object> requestMap = new HashMap<String, Object>();
        requestMap.put("plugin-id", Constants.PLUGIN_ID);
        
        GoApiResponse response = goApplicationAccessor.submit(createGoApiRequest(GET_PLUGIN_SETTINGS, JSONUtils.toJSON(requestMap)));
        if (response.responseBody() == null || response.responseBody().trim().isEmpty()) {
            throw new RuntimeException("plugin is not configured. please provide plugin settings.");
        }
        
        Map<String, String> responseBodyMap = ((Map<String, String>) JSONUtils.fromJSON(response.responseBody()));
        
        String callbackUrl = responseBodyMap.get(Constants.PLUGIN_SETTINGS_CALLBACK_URL);
        String headersJson = responseBodyMap.get(Constants.PLUGIN_SETTINGS_HEADERS);

        LOGGER.warn(String.format("Got settings\r\ncallbackUrl: %s\r\nheaders: %s", callbackUrl, headersJson));

        ArrayList<HeaderEntry> headers = new ArrayList<HeaderEntry>();

        if(!StringUtils.isBlank(headersJson)) {
            headers = ((ArrayList<HeaderEntry>) JSONUtils.fromJSON(headersJson));
        }
        return new PluginSettings(callbackUrl, headers);
    }

    public static Map<String, String> getHeaders(String sourceDestinationsString) {
        return null;
    }
    
    private GoApiRequest createGoApiRequest(final String api, final String responseBody) {
        return new GoApiRequest() {
            @Override
            public String api() {
                return api;
            }

            @Override
            public String apiVersion() {
                return "1.0";
            }

            @Override
            public GoPluginIdentifier pluginIdentifier() {
                return PluginIdentifier.getGoPluginIdentifier();
            }

            @Override
            public Map<String, String> requestParameters() {
                return null;
            }

            @Override
            public Map<String, String> requestHeaders() {
                return null;
            }

            @Override
            public String requestBody() {
                return responseBody;
            }
        };
    }
}
