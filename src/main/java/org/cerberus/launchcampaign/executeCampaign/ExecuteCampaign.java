package org.cerberus.launchcampaign.executeCampaign;

import java.io.IOException;
import java.net.*;

import org.apache.commons.httpclient.HttpStatus;

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
	public boolean execute() throws URISyntaxException, IOException {

		URL urlExecuteCampaign = executeCampaignDto.buildUrl(urlCerberus);
		HttpURLConnection  conn = (HttpURLConnection) urlExecuteCampaign.openConnection();
		conn.setRequestMethod("GET");

		conn.connect();

		int code = conn.getResponseCode();
		if(HttpStatus.SC_OK == code) 
			return true;
		
		return false;
	}
}
