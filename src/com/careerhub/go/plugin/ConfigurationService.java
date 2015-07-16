package com.careerhub.go.plugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

public class ConfigurationService {
	
	public Map<String, Object> getFields() {
        final Map<String, Object> fields = new HashMap<String, Object>();
        
        fields.put(Constants.PLUGIN_SETTINGS_CALLBACK_URL, createField("Callback URL", null, true, false, "0"));
		
        return fields;
	}
	
	public List<Map<String, Object>> validateFields(Map<String, String> fields) {
        final List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        final String callbackUrl = fields.get(Constants.PLUGIN_SETTINGS_CALLBACK_URL);
        final Map<String, Object> callbackFieldValidation = new HashMap<String, Object>();
        
        if (callbackUrl == null || callbackUrl.isEmpty()) {
        	callbackFieldValidation.put("key", Constants.PLUGIN_SETTINGS_CALLBACK_URL);
        	callbackFieldValidation.put("message", String.format("'%s' is a required field", "Callback URL"));
        } else if(!callbackUrl.startsWith("https://") || callbackUrl.startsWith("http://")) {
        	callbackFieldValidation.put("key", Constants.PLUGIN_SETTINGS_CALLBACK_URL);
        	callbackFieldValidation.put("message", String.format("'%s' must start with http or https", "Callback URL"));
        }
        
        result.add(callbackFieldValidation);
        
        return result;
	}

	public Map<String, Object> getTemplateSettings() throws IOException {
		final Map<String, Object> response = new HashMap<String, Object>();
        
        response.put("template", IOUtils.toString(getClass().getResourceAsStream("/plugin-settings.template.html"), "UTF-8"));
		
        return response;
	}

    private Map<String, Object> createField(String displayName, String defaultValue, boolean isRequired, boolean isSecure, String displayOrder) {
    	final Map<String, Object> fieldProperties = new HashMap<String, Object>();
    	
        fieldProperties.put("display-name", displayName);
        fieldProperties.put("default-value", defaultValue);
        fieldProperties.put("required", isRequired);
        fieldProperties.put("secure", isSecure);
        fieldProperties.put("display-order", displayOrder);
        
        return fieldProperties;
    }

}
