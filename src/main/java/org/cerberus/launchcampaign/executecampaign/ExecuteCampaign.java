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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.StringUtils;
import org.cerberus.launchcampaign.event.LogEvent;

public class ExecuteCampaign {

    private String urlCerberus;
    private ExecuteCampaignDto executeCampaignDto;
    private String apikey;

    public ExecuteCampaign(String urlCerberus, ExecuteCampaignDto executeCampaignDto, String apikey) {
        this.urlCerberus = urlCerberus;
        this.executeCampaignDto = executeCampaignDto;
        this.apikey = apikey;
    }

    /**
     * launch cerberus campaign (added it into the queue of cerberus)
     *
     * @param logEvent
     * @return false if launch fail, true if success.
     * @throws URISyntaxException
     * @throws IOException
     */
    public boolean execute(LogEvent logEvent) throws URISyntaxException, IOException {

        String warning = executeCampaignDto.verifyParameterWarning();
        String error = executeCampaignDto.verifyParameterError();
        logEvent.log(error, warning, "");

        if (!StringUtils.isEmpty(error)) {
            throw new IllegalArgumentException(error);
        }

        URL urlExecuteCampaign = executeCampaignDto.buildUrl(urlCerberus);

        logEvent.log("", "", "Trigger Cerberus call : " + urlExecuteCampaign.toString().replace("?" + urlExecuteCampaign.getQuery(), "") + " with query String : " + urlExecuteCampaign.getQuery());

        HttpURLConnection conn = (HttpURLConnection) urlExecuteCampaign.openConnection();
        conn.setRequestProperty("apikey", apikey);
        conn.setRequestMethod("GET");
        conn.connect();
        int code = conn.getResponseCode();
        logEvent.log("", "", "HTTP response : " + code);

        StringBuilder sb;
        sb = new StringBuilder();
        String output;

        InputStream inputStream;
        if (200 <= conn.getResponseCode() && conn.getResponseCode() <= 299) {
            inputStream = conn.getInputStream();
        } else {
            inputStream = conn.getErrorStream();
        }

        try (InputStreamReader s = new InputStreamReader(inputStream, Charset.forName("UTF-8"))) {
            try (BufferedReader br = new BufferedReader(s)) {
                while ((output = br.readLine()) != null) {
                    sb.append(output);
                }
            }
        }

        String contains = sb.toString();

        if (HttpStatus.SC_OK == code) {
            logEvent.log("", "", "Response : " + contains);
            return true;
        }

        // log error message
        logEvent.log("Error message when trying to add a new execution in queue : " + contains, "", "");

        return false;
    }
}
