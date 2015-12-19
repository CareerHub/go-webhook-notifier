package com.careerhub.go.plugin;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class PluginSettings {
    private final String callbackUrl;
    private final ArrayList<HeaderEntry> headers;

	public PluginSettings(String callbackUrl, ArrayList<HeaderEntry> headers) {
		this.callbackUrl = callbackUrl;
		this.headers = headers;
	}

	public String getCallbackUrl() {
		return callbackUrl;
	}

	public Header[] getHeaders() {
		ArrayList<Header> headerList = new ArrayList<Header>();

		for (Iterator<HeaderEntry> i = headers.iterator(); i.hasNext();) {
			HeaderEntry headerEntry = i.next();

			Header header = new BasicHeader(headerEntry.getName(), headerEntry.getValue());
			headerList.add(header);
		}

		Header[] headerArr = new Header[headerList.size()];
		headerArr = headerList.toArray(headerArr);

		return headerArr;
	}
}
