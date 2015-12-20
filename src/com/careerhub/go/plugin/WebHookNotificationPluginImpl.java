package com.careerhub.go.plugin;

import com.careerhub.go.plugin.util.JSONUtils;
import com.careerhub.go.plugin.util.MapUtils;
import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.GoPlugin;
import com.thoughtworks.go.plugin.api.GoPluginIdentifier;
import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.annotation.Load;
import com.thoughtworks.go.plugin.api.annotation.UnLoad;
import com.thoughtworks.go.plugin.api.info.PluginContext;
import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.io.IOException;
import java.util.*;

@com.thoughtworks.go.plugin.api.annotation.Extension
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
    
    @Load
    public void onLoad(PluginContext context) {
    	LOGGER.info("Plugin loaded");
    }
    
    @UnLoad
    public void onUnload(PluginContext context) {
    	LOGGER.info("Plugin unloaded");
    }

    @Override
    public void initializeGoApplicationAccessor(GoApplicationAccessor goApplicationAccessor) {
        this.goApplicationAccessor = goApplicationAccessor;
    }

    @Override
    public GoPluginApiResponse handle(GoPluginApiRequest goPluginApiRequest) {
        String requestName = goPluginApiRequest.requestName();
        
        LOGGER.warn(String.format("Received request with name %s", requestName));
        
        if (requestName.equals(Constants.PLUGIN_SETTINGS_GET_CONFIGURATION)) {
            return handleGetPluginSettingsConfiguration();
        } else if (requestName.equals(Constants.PLUGIN_SETTINGS_GET_VIEW)) {
            try {
                return handleGetPluginSettingsView();
            } catch (IOException e) {
            	String message = String.format("Failed to find template: %s", e.getMessage());
            	
                LOGGER.warn(message);
                
                return renderJSON(500, message);
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
        Map<String, Object> dataMap = getJsonMap(goPluginApiRequest);

        int responseCode = Constants.SUCCESS_RESPONSE_CODE;
        Map<String, Object> response = new HashMap<String, Object>();
        List<String> messages = new ArrayList<String>();
        
        try {
        	PluginSettings settings = this.settingsService.getSettings(goApplicationAccessor);
        	int statusCode = this.notificationService.sendNotification(settings, dataMap);
        	
        	if(statusCode == 200) {
        		response.put("status", "success");
        	} else {
                LOGGER.error(String.format("Webhook notification failed. Status Code: %d", statusCode));
        		response.put("status", "failure");        		
        	}
        } catch (Exception e) {
            LOGGER.error("Error occurred while trying to send a webhook notification", e);

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
        LOGGER.warn("Handling plugin setting request");
        
    	Map<String, Object> fields = this.configurationService.getFields();
    	
        return renderJSON(Constants.SUCCESS_RESPONSE_CODE, fields);
    }
    
    private GoPluginApiResponse handleGetPluginSettingsView() throws IOException {
        LOGGER.debug("Handling plugin view request");
        
    	Map<String, Object> response = this.configurationService.getTemplateSettings();
    	
        return renderJSON(Constants.SUCCESS_RESPONSE_CODE, response);
    }
    

    private GoPluginApiResponse handleValidatePluginSettingsConfiguration(GoPluginApiRequest goPluginApiRequest) {
    	final Map<String, Object> responseMap = getJsonMap(goPluginApiRequest);
    	
        final Map<String, String> configuration = MapUtils.toKeyValuePairs(responseMap, "plugin-settings");
        
        final List<Map<String, Object>> response = this.configurationService.validateFields(configuration);
        
        return renderJSON(Constants.SUCCESS_RESPONSE_CODE, response);
    }
    
    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }


	private Map<String, Object> getJsonMap(GoPluginApiRequest goPluginApiRequest) {
		final String jsonBody = goPluginApiRequest.requestBody();
    	
    	LOGGER.warn(String.format("Received json request name %s message: %s", goPluginApiRequest.requestName(), jsonBody));
    	
        final Map<String, Object> responseMap = (Map<String, Object>) JSONUtils.fromJSON(jsonBody);
		return responseMap;
	}

	
    private GoPluginApiResponse renderJSON(final int responseCode, Object response) {
        final String json = response == null ? null : new GsonBuilder().create().toJson(response);
        
        LOGGER.warn(String.format("Responding with status %d and message: %s", responseCode, json));
        
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
