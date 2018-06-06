/**
 * Cerberus Copyright (C) 2013 - 2017 cerberustesting
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This file is part of Cerberus.
 *
 * Cerberus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cerberus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cerberus.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.cerberus.launchcampaign.executecampaign;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.StringUtils;
import org.cerberus.launchcampaign.event.LogEvent;

public class ExecuteCampaign {

	
	private String urlCerberus;
	private ExecuteCampaignDto executeCampaignDto;
	
	public ExecuteCampaign(String urlCerberus, ExecuteCampaignDto executeCampaignDto) {
		this.urlCerberus=urlCerberus;
		this.executeCampaignDto=executeCampaignDto;
	}

	/**
	 * launch cerberus campaign (added it into the queue of cerberus)
	 * @param urlCerberus
	 * @return false if launch fail,  true if success.
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public boolean execute(LogEvent logEvent) throws URISyntaxException, IOException {

		String warning = executeCampaignDto.verifyParameterWarning();
		String error = executeCampaignDto.verifyParameterError();
		logEvent.log(error, warning, "");
		
		if(!StringUtils.isEmpty(error)) {
			throw new IllegalArgumentException(error);
		}
		
		URL urlExecuteCampaign = executeCampaignDto.buildUrl(urlCerberus);
		HttpURLConnection  conn = (HttpURLConnection) urlExecuteCampaign.openConnection();
		conn.setRequestMethod("GET");
		conn.connect();
		int code = conn.getResponseCode();
		logEvent.log("", "", "HTTP response : " + code);

		if(HttpStatus.SC_OK == code) {
			logEvent.log("", "", "Cerberus response message : " + conn.getResponseMessage());
			logEvent.log("", "", "Requesting Cerberus with the following parameters : ");
			for (String param : Arrays.asList(urlExecuteCampaign.getQuery().split("&"))) {
				logEvent.log("", "", param);
			}
			return true;
		}

		String contains="";

		try {
			contains = conn.getInputStream().toString();
		} catch (Exception e) {
			// do nothing
		}

		// log error message
		logEvent.log("Error message when trying to add a new execution in queue : " + conn.getResponseMessage(), contains, "");

		return false;
	}
}
