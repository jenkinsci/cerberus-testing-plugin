package org.cerberus.launchcampaign.executeCampaign;

import java.net.*;
import java.util.*;

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
	
	public ExecuteCampaignDto(String robot, String ss_ip, String environment, String browser, String browserVersion,
			String platform, String selectedCampaign) {
		super();
		this.robot = robot;
		this.ss_ip = ss_ip;
		this.environment = environment;
		this.browser = browser;
		this.browserVersion = browserVersion;
		this.platform = platform;
		this.selectedCampaign = selectedCampaign;
		
		Random random = new Random(new Date().getTime());
		tagCerberusCampaign = "Jenkins-"+random.nextLong();
	}

	public URL buildUrl(String urlCerberus) throws MalformedURLException, URISyntaxException {
		
		
		
		URIBuilder b = new URIBuilder(urlCerberus + "/" + Constantes.URL_ADD_CAMPAIGN_TO_EXECUTION_QUEUE);
		
		b.addParameter("OutputFormat", "json");
		b.addParameter("Screenshot", "1");
		b.addParameter("Verbose", "1");
		b.addParameter("timeout", "5000");
		b.addParameter("Synchroneous", "Y");
		b.addParameter("PageSource", "Y");
		b.addParameter("Synchroneous", "1");
		b.addParameter("SeleniumLog", "1");
		b.addParameter("retries", "0");
		b.addParameter("manualExecution", "N");

		b.addParameter("ss_ip", ss_ip);
		b.addParameter("Robot", robot);
		b.addParameter("Environment", environment);
		b.addParameter("Browser", browser);
		b.addParameter("BrowserVersion", browserVersion);
		b.addParameter("Platform", platform);
		b.addParameter("SelectedCampaign", selectedCampaign);
		
    	// genere a ramdom tag 
    	b.addParameter("Tag", tagCerberusCampaign);
    	
		return new URL(b.build().toString());
	}

	public String getTagCerberus() {
		return tagCerberusCampaign;
	}

	public String getSelectedCampaign() {
		return selectedCampaign;
	}
	
}
