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
    
	private final int screenshot; 
	private final int verbose;        
	private final int pageSource; 
	private final int seleniumLog; 
	private final int timeOut; 
	private final int retries;
	private final int priority; 
	
	
	public ExecuteCampaignDto(final String robot, final String ss_ip, final String environment, final String browser, final String selectedCampaign, final int screenshot, final int verbose, 
			final int pageSource, final int seleniumLog, final int timeOut, final int retries, final int priority, String tag, String ss_p, List<String> countries) {
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
		this.ss_p=ss_p;
		this.countries=countries;
		
		Date time = new Date();
		SimpleDateFormat dt = new SimpleDateFormat("yyyyMMddHHmmssSSS"); 	
		
		this.tagCerberusCampaign = tag.replace("$[current_timestamp]", dt.format(time));
	}
	
	public String verifyParameterWarning() {
		// rules : 
		//   - either robot
		//   - or ss_ip+ ss_p + browser
	
		if(StringUtils.isEmpty(this.robot) && 
		        (StringUtils.isEmpty(this.ss_ip) || 
		         StringUtils.isEmpty(this.ss_p) || 
		         StringUtils.isEmpty(this.browser))) {
		    return "Either robot or selenium server ip and selenium server port and browser is required.";
		}

		return "";
	}
	
	public String verifyParameterError() {
		String error = "";
		error += check0or1or2(this.screenshot, "Screenshot parameter");
		error += check0or1or2(this.verbose, "Verbose parameter");
		error += check0or1or2(this.pageSource, "Page Source parameter");
		error += check0or1or2(this.seleniumLog, "Robot Log parameter");
		error += checkRetries(this.retries);	
		
		return error;
	}

	private String checkRetries(int parameter) {
		if(parameter < 0 || parameter > 3) {
			return "Retries parameter must be 0, 1, 2 or 3 but is " + parameter + ", ";
		}
		return "";
	}
	private String check0or1or2(int parameter, String parameterName) {
	    if(parameter != 0 && parameter != 1 && parameter != 2) {
	        return parameterName + " must be equal to 0, 1 or 2 but is " + parameter + ". ";
	    }
	    return "";
	}

	public URL buildUrl(String urlCerberus) throws MalformedURLException, URISyntaxException {
	    URIBuilder b = new URIBuilder(urlCerberus + "/" + Constantes.URL_ADD_CAMPAIGN_TO_EXECUTION_QUEUE);

	    b.addParameter("campaign", selectedCampaign);
	    b.addParameter("tag", tagCerberusCampaign);
	    b.addParameter("environment", environment);
	    b.addParameter("robot", robot);
	    b.addParameter("ss_ip", ss_ip);
	    b.addParameter("ss_p", ss_p);
	    b.addParameter("browser", browser);
	    b.addParameter("screenshot",this.screenshot+"");
	    b.addParameter("verbose", this.verbose+"");
	    b.addParameter("pagesource",this.pageSource+"");
	    b.addParameter("seleniumlog", this.seleniumLog+"");
	    b.addParameter("timeout", this.timeOut+"");
	    b.addParameter("retries", this.retries+"");
	    b.addParameter("priority", this.priority+"");	    
	    for (String country : countries) {
	    	b.addParameter("country", country);
	    }
	    b.addParameter("manualexecution", "N");

	    return new URL(b.build().toString());
	}

	public String getTagCerberus() {
		return tagCerberusCampaign;
	}

	public String getSelectedCampaign() {
		return selectedCampaign;
	}
	
}
