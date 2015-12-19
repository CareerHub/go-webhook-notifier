package com.careerhub.go.plugin;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class PluginSettings {
    private String callbackUrl;
    private HeaderEntry[] headersMap;

	public PluginSettings(String callbackUrl, HeaderEntry[] headersMap) {
		this.callbackUrl = callbackUrl;
		this.headersMap = headersMap;
	}

	public String getCallbackUrl() {
		return callbackUrl;
	}

	public Header[] getHttpHeaders() {
		ArrayList<Header> headerList = new ArrayList<Header>();

		if(headersMap != null) {
			for (int i = 0; i< headersMap.length; ++i) {
				HeaderEntry headerEntry = headersMap[i];

				Header header = new BasicHeader(headerEntry.getName(), headerEntry.getValue());
				headerList.add(header);
			}
		}

		Header[] headerArr = new Header[headerList.size()];
		headerArr = headerList.toArray(headerArr);

		return headerArr;
	}
}
