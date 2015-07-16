package com.careerhub.go.plugin;

import com.careerhub.go.plugin.util.JSONUtils;
import com.careerhub.go.plugin.util.MapUtils;
import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.GoPlugin;
import com.thoughtworks.go.plugin.api.GoPluginIdentifier;
import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.io.IOException;
import java.util.*;

@Extension
public class WebHookNotificationPluginImpl implements GoPlugin {
    private static Logger LOGGER = Logger.getLoggerFor(WebHookNotificationPluginImpl.class);

    private GoApplicationAccessor goApplicationAccessor;

	private final ConfigurationService configurationService;
    private final SettingsService settingsService;
    private NotificationService notificationService;
    
    public WebHookNotificationPluginImpl() {
		this.configurationService = new ConfigurationService();
		this.settingsService = new SettingsService();
		this.notificationService = new NotificationService();
	}
    

    @Override
    public void initializeGoApplicationAccessor(GoApplicationAccessor goApplicationAccessor) {
        this.goApplicationAccessor = goApplicationAccessor;
    }

    @Override
    public GoPluginApiResponse handle(GoPluginApiRequest goPluginApiRequest) {
        String requestName = goPluginApiRequest.requestName();
        
        if (requestName.equals(Constants.PLUGIN_SETTINGS_GET_CONFIGURATION)) {
            return handleGetPluginSettingsConfiguration();
        } else if (requestName.equals(Constants.PLUGIN_SETTINGS_GET_VIEW)) {
            try {
                return handleGetPluginSettingsView();
            } catch (IOException e) {
                return renderJSON(500, String.format("Failed to find template: %s", e.getMessage()));
            }
        } else if (requestName.equals(Constants.PLUGIN_SETTINGS_VALIDATE_CONFIGURATION)) {
            return handleValidatePluginSettingsConfiguration(goPluginApiRequest);
        } else if (requestName.equals(Constants.REQUEST_NOTIFICATIONS_INTERESTED_IN)) {
            return handleNotificationsInterestedIn();
        } else if (requestName.equals(Constants.REQUEST_STAGE_STATUS)) {
            return handleStageNotification(goPluginApiRequest);
        }
        
        return renderJSON(Constants.NOT_FOUND_RESPONSE_CODE, null);
    }

    @Override
    public GoPluginIdentifier pluginIdentifier() {
        return PluginIdentifier.getGoPluginIdentifier();
    }

    private GoPluginApiResponse handleNotificationsInterestedIn() {
        Map<String, Object> response = this.notificationService.getNotificationsInterestedIn();
        
        return renderJSON(Constants.SUCCESS_RESPONSE_CODE, response);
    }

    private GoPluginApiResponse handleStageNotification(GoPluginApiRequest goPluginApiRequest) {
        Map<String, Object> dataMap = (Map<String, Object>) JSONUtils.fromJSON(goPluginApiRequest.requestBody());

        int responseCode = Constants.SUCCESS_RESPONSE_CODE;
        Map<String, Object> response = new HashMap<String, Object>();
        List<String> messages = new ArrayList<String>();
        
        try {
        	PluginSettings settings = this.settingsService.getSettings(goApplicationAccessor);
        	int statusCode = this.notificationService.sendNotification(settings, dataMap);
        	
        	if(statusCode == 200) {
        		response.put("status", "success");
        	} else {
        		response.put("status", "failure");        		
        	}
        } catch (Exception e) {
            LOGGER.warn("Error occurred while trying to deliver an email.", e);

            responseCode = Constants.INTERNAL_ERROR_RESPONSE_CODE;
            response.put("status", "failure");
            if (!isEmpty(e.getMessage())) {
                messages.add(e.getMessage());
            }
        }

        if (!messages.isEmpty()) {
            response.put("messages", messages);
        }
        return renderJSON(responseCode, response);
    }
    

    private GoPluginApiResponse handleGetPluginSettingsConfiguration() {    	
    	Map<String, Object> fields = this.configurationService.getFields();
    	
        return renderJSON(Constants.SUCCESS_RESPONSE_CODE, fields);
    }
    
    private GoPluginApiResponse handleGetPluginSettingsView() throws IOException {
    	Map<String, Object> response = this.configurationService.getTemplateSettings();
    	
        return renderJSON(Constants.SUCCESS_RESPONSE_CODE, response);
    }
    

    private GoPluginApiResponse handleValidatePluginSettingsConfiguration(GoPluginApiRequest goPluginApiRequest) {
        final Map<String, Object> responseMap = (Map<String, Object>) JSONUtils.fromJSON(goPluginApiRequest.requestBody());
        final Map<String, String> configuration = MapUtils.toKeyValuePairs(responseMap, "plugin-settings");
        
        final List<Map<String, Object>> response = this.configurationService.validateFields(configuration);
        
        return renderJSON(Constants.SUCCESS_RESPONSE_CODE, response);
    }

    
    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    private GoPluginApiResponse renderJSON(final int responseCode, Object response) {
        final String json = response == null ? null : new GsonBuilder().create().toJson(response);
        return new GoPluginApiResponse() {
            @Override
            public int responseCode() {
                return responseCode;
            }

            @Override
            public Map<String, String> responseHeaders() {
                return null;
            }

            @Override
            public String responseBody() {
                return json;
            }
        };
    }
}
