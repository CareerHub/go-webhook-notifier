package com.careerhub.go.plugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import com.careerhub.go.plugin.util.JSONUtils;

public class NotificationService {

	
	public Map<String, Object> getNotificationsInterestedIn() {
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("notifications", Arrays.asList(Constants.REQUEST_STAGE_STATUS));
		return response;
	}
	
	public int sendNotification(PluginSettings settings, Map<String, Object> info) throws ClientProtocolException, IOException {
        String callbackUrl = settings.getCallbackUrl();
        
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(callbackUrl);  

		request.setHeader("Content-Type", "application/json");

		// Could process json and parse processed json on
		// However, at this stage their is no need
		// Just pass on as is.
        String json = JSONUtils.toJSON(info);
    	    	
    	StringEntity jsonEntity = new StringEntity(json);
     
    	request.setEntity(jsonEntity);

		Header[] headers = settings.getHeaders();

		request.setHeaders(headers);

		HttpResponse response = httpClient.execute(request);
        
    	int statusCode = response.getStatusLine().getStatusCode();

    	return statusCode;
	}
}
