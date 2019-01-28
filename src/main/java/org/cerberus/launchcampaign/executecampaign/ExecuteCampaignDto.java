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

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.cerberus.launchcampaign.Constantes;

public class ExecuteCampaignDto {

    private final String robot;
    private final String ss_ip;
    private final String environment;
    private final String browser;
    private final String selectedCampaign;
    private final String tagCerberusCampaign;
    private final String ss_p;
    private final List<String> countries;
    private final String manualHost;
    private final String manualContextRoot;

    private final String screenshot;
    private final String verbose;
    private final String pageSource;
    private final String seleniumLog;
    private final String timeOut;
    private final String retries;
    private final String priority;

    public ExecuteCampaignDto(final String robot, final String ss_ip, final String environment, final String browser, final String selectedCampaign, final String screenshot, final String verbose,
            final String pageSource, final String seleniumLog, final String timeOut, final String priority, final String retries, final String tag, final String ss_p, final String manualHost, final String manualContextRoot, List<String> countries) {
        super();
        this.robot = robot;
        this.ss_ip = ss_ip;
        this.environment = environment;
        this.browser = browser;
        this.selectedCampaign = selectedCampaign;
        this.screenshot = screenshot;
        this.verbose = verbose;
        this.pageSource = pageSource;
        this.seleniumLog = seleniumLog;
        this.timeOut = timeOut;
        this.retries = retries;
        this.priority = priority;
        this.ss_p = ss_p;
        this.manualHost = manualHost;
        this.manualContextRoot = manualContextRoot;

        this.countries = countries;

        Date time = new Date();
        SimpleDateFormat dt = new SimpleDateFormat("yyyyMMddHHmmssSSS");

        this.tagCerberusCampaign = tag.replace("$[current_timestamp]", dt.format(time));
    }

    public String verifyParameterWarning() {
        // rules : 
        //   - either robot
        //   - or ss_ip+ ss_p + browser

        // Robot is no longuer mandatory as configuration is defined on Cerberus attached to the campaign.
//		if(StringUtils.isEmpty(this.robot) && 
//		        (StringUtils.isEmpty(this.ss_ip) || 
//		         StringUtils.isEmpty(this.ss_p) || 
//		         StringUtils.isEmpty(this.browser))) {
//		    return "Either robot or selenium server ip and selenium server port and browser is required.";
//		}
        return "";
    }

    public String verifyParameterError() {
        String error = "";
        // Servlet has defaulf value is case not defined.
//		error += check0or1or2(this.screenshot, "Screenshot parameter");
//		error += check0or1or2(this.verbose, "Verbose parameter");
//		error += check0or1or2(this.pageSource, "Page Source parameter");
//		error += check0or1or2(this.seleniumLog, "Robot Log parameter");
//		error += checkRetries(this.retries);	

        return error;
    }

    private String checkRetries(int parameter) {
        if (parameter < 0 || parameter > 3) {
            return "Retries parameter must be 0, 1, 2 or 3 but is " + parameter + ", ";
        }
        return "";
    }

    private String check0or1or2(int parameter, String parameterName) {
        if (parameter != 0 && parameter != 1 && parameter != 2) {
            return parameterName + " must be equal to 0, 1 or 2 but is " + parameter + ". ";
        }
        return "";
    }

    public URL buildUrl(String urlCerberus) throws MalformedURLException, URISyntaxException {
        URIBuilder b = new URIBuilder(urlCerberus + "/" + Constantes.URL_ADD_CAMPAIGN_TO_EXECUTION_QUEUE);

        addIfNotEmpty(b, "campaign", this.selectedCampaign);
        addIfNotEmpty(b, "tag", tagCerberusCampaign);
        addArray(b, "environment", environment);
        addArray(b, "robot", robot);
        addIfNotEmpty(b, "ss_ip", ss_ip);
        addIfNotEmpty(b, "ss_p", ss_p);
        addIfNotEmpty(b, "browser", browser);
        addIfNotEmpty(b, "screenshot", this.screenshot + "");
        addIfNotEmpty(b, "verbose", this.verbose + "");
        addIfNotEmpty(b, "pagesource", this.pageSource + "");
        addIfNotEmpty(b, "seleniumlog", this.seleniumLog + "");

        addIfNotEmpty(b, "timeout", this.timeOut + "");
        addIfNotEmpty(b, "retries", this.retries + "");
        addIfNotEmpty(b, "priority", this.priority + "");
        b.addParameter("manualexecution", "N");

        for (String country : countries) {
            b.addParameter("country", country);
        }

        if (!StringUtils.isEmpty(manualHost)) {
            b.addParameter("manualurl", "2");
            addIfNotEmpty(b, "myhost", manualHost);
            addIfNotEmpty(b, "mycontextroot", manualContextRoot);
        }

        return new URL(b.build().toString());
    }

    private void addArray(URIBuilder b, String key, String array) {
        // manage several robot
        if (!StringUtils.isEmpty(array)) {
            String[] elmts = array.split(",");
            for (String elmt : elmts) {
                addIfNotEmpty(b, key, elmt);
            }
        }
    }

    private void addIfNotEmpty(URIBuilder b, String key, String value) {
        if (!StringUtils.isEmpty(value)) {
            b.addParameter(key, value);
        }
    }

    public String getTagCerberus() {
        return tagCerberusCampaign;
    }

    public String getSelectedCampaign() {
        return selectedCampaign;
    }

}
