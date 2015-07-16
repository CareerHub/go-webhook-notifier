package com.careerhub.go.plugin;

public class PluginSettings {
    private String callbackUrl;
    
	public PluginSettings(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}

	public String getCallbackUrl() {
		return callbackUrl;
	}
}
