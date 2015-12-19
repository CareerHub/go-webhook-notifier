package com.careerhub.go.plugin.test;

import com.careerhub.go.plugin.Constants;
import com.careerhub.go.plugin.PluginSettings;
import com.careerhub.go.plugin.SettingsService;
import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.request.GoApiRequest;
import com.thoughtworks.go.plugin.api.response.GoApiResponse;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PluginSettingsTests {
    private GoApplicationAccessor goApplicationAccessor;
    private SettingsService settingsService;

    @Before
    public void setUp() throws Exception {
        goApplicationAccessor = mock(GoApplicationAccessor.class);
        settingsService = new SettingsService();
    }

    @Test
    public void getHeaders() {
        String callbackUrl = "http://localhost";

        String responseBody = "{" +
            Constants.PLUGIN_SETTINGS_CALLBACK_URL + ": \"" + callbackUrl+ "\"," +
            Constants.PLUGIN_SETTINGS_HEADERS + ":\"[{name:'Accept',value:'application/json'}]\"" +
        "}";
        GoApiResponse response = createGoApiResponse(200, responseBody);
        when(goApplicationAccessor.submit(any(GoApiRequest.class))).thenReturn(response);

        PluginSettings settings = settingsService.getSettings(goApplicationAccessor);

        assertThat(settings.getCallbackUrl(), is(callbackUrl));
        assertThat(settings.getHttpHeaders().length, is(1));
    }

    private GoApiResponse createGoApiResponse(final int statusCode, final String body) {
        return new GoApiResponse() {
            @Override
            public int responseCode() {
                return statusCode;
            }

            @Override
            public Map<String, String> responseHeaders() {
                return null;
            }

            @Override
            public String responseBody() {
                return body;
            }
        };
    }
}
