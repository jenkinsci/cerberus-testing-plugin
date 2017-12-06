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

import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.cerberus.launchcampaign.Constantes;

public class ExecuteCampaignDto {

	private String robot;
	private String ss_ip;
	private String environment;
	private String browser;
	private String browserVersion;
	private String platform;
	private String selectedCampaign;
	private String tagCerberusCampaign;
	private String ss_p;
    private String screensize;
    
	private final int screenshot; 
	private final int verbose;        
	private final int pageSource; 
	private final int seleniumLog; 
	private final int timeOut; 
	private final int retries; 
	
	
	public ExecuteCampaignDto(final String robot, final String ss_ip, final String environment, final String browser, final String browserVersion,
			final String platform, final String selectedCampaign, final int screenshot, final int verbose, 
			final int pageSource, final int seleniumLog, final int timeOut, final int retries, String tag, String ss_p, String screensize) {
		super();
		this.robot = robot;
		this.ss_ip = ss_ip;
		this.environment = environment;
		this.browser = browser;
		this.browserVersion = browserVersion;
		this.platform = platform;
		this.selectedCampaign = selectedCampaign;
		
		this.screenshot = screenshot; 
		this.verbose = verbose;
		this.pageSource = pageSource; 
		this.seleniumLog = seleniumLog;
		this.timeOut = timeOut;
		this.retries = retries;
		
		Date time = new Date();
		
		SimpleDateFormat dt = new SimpleDateFormat("yyyyMMddHHmmssSSS"); 	
		
		this.tagCerberusCampaign = tag.replace("$[current_timestamp]", dt.format(time));
	}
	
	public String verifyParameterWarning() {
		String warning = "";
		
		// rules : 
		//   - either robot
		//   - or ss_ip+ ss_p + browser + browserVersion + platform
	
		if(StringUtils.isEmpty(this.robot) && 
		        (StringUtils.isEmpty(this.ss_ip) || 
		         StringUtils.isEmpty(this.ss_p) || 
		         StringUtils.isEmpty(this.browser) || 
		         StringUtils.isEmpty(this.browserVersion) || 
		         StringUtils.isEmpty(this.platform)) ) {
		    warning += "either robot or selenium server ip + selenium server port + browser + browser Version + platform is required, ";
		    warning += checkIsEmpty(this.robot, "robot");
		    warning += checkIsEmpty(this.ss_ip, "selenium server ip");
	        warning += checkIsEmpty(this.ss_p, "selenium server port");		    
		    warning += checkIsEmpty(this.browser, "browser");
		    warning += checkIsEmpty(this.browserVersion, "browser Version");
		    warning += checkIsEmpty(this.platform, "platform");
		}
		
        warning += checkIsEmpty(this.selectedCampaign, "selectedCampaign");

		return warning;
		
		
	}
	
	public String verifyParameterError() {
		String error = "";
		
		error += check0or1or2(this.screenshot, "screenshot");
		error += check0or1or2(this.verbose, "verbose");
		error += check0or1or2(this.pageSource, "pageSource");
		error += check0or1or2(this.seleniumLog, "seleniumLog");
		error += checkRetries(this.retries);	
		
		return error;
	}
	
	private String checkIsEmpty(String parameter, String parameterName) {
		if(StringUtils.isEmpty(parameter)) {
			return parameterName + " is empty, ";
		}
		return "";
	}
	private String checkRetries(int parameter) {
		if(parameter < 0 || parameter > 3) {
			return "retries must be 0, 1, 2 or 3 but is " + parameter + ", ";
		}
		return "";
	}
	private String check0or1or2(int parameter, String parameterName) {
	    if(parameter != 0 && parameter != 1 && parameter != 2) {
	        return parameterName + " must be 0, 1 or 2 but is " + parameter + ", ";
	    }
	    return "";
	}

	public URL buildUrl(String urlCerberus) throws MalformedURLException, URISyntaxException {
	    URIBuilder b = new URIBuilder(urlCerberus + "/" + Constantes.URL_ADD_CAMPAIGN_TO_EXECUTION_QUEUE);

	    b.addParameter("screenshot",this.screenshot +"");
	    b.addParameter("verbose", this.verbose+"");
	    b.addParameter("timeout", this.timeOut+"");
	    b.addParameter("pagesource",this.pageSource + "");
	    b.addParameter("seleniumlog", this.seleniumLog +"");
	    b.addParameter("retries", this.retries+"");
	    b.addParameter("manualexecution", "N");

	    b.addParameter("ss_ip", ss_ip);
	    b.addParameter("ss_p", ss_p);
	    b.addParameter("robot", robot);
	    b.addParameter("environment", environment);
	    b.addParameter("browser", browser);
	    b.addParameter("version", browserVersion);
	    b.addParameter("platform", platform);
	    b.addParameter("campaign", selectedCampaign);
	    b.addParameter("screensize", screensize);

	    // genere a random tag
	    b.addParameter("tag", tagCerberusCampaign);


	    return new URL(b.build().toString());
	}

	public String getTagCerberus() {
		return tagCerberusCampaign;
	}

	public String getSelectedCampaign() {
		return selectedCampaign;
	}
	
}
