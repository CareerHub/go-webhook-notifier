package com.careerhub.go.plugin;

import com.thoughtworks.go.plugin.api.GoPluginIdentifier;

public class PluginIdentifier {

    public static GoPluginIdentifier getGoPluginIdentifier() {
        return new GoPluginIdentifier(Constants.EXTENSION_NAME, Constants.GO_SUPPORTED_VERSIONS);
    }
}
