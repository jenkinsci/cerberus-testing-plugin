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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import org.cerberus.launchcampaign.Constantes;
import org.cerberus.launchcampaign.executecampaign.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class ExecuteCampaignTest {

	private String urlAddCampaign;
	
	@Rule
	public MockServerRule mockServerRule = new MockServerRule(this);
	private MockServerClient mockServerClient;
	
	ExecuteCampaign executeCampaign;

	@Before
	public void before() {
		ExecuteCampaignDto executeCampaignDto = new ExecuteCampaignDto("", "", "", "", "", "", "");
		
		urlAddCampaign = "http://localhost:" + mockServerRule.getPort() + "/Cerberus/"+Constantes.URL_ADD_CAMPAIGN_TO_EXECUTION_QUEUE;
		executeCampaign = new ExecuteCampaign(urlAddCampaign, executeCampaignDto);
	}
	
	@Test
	public void executeSucess() throws Exception {
		mockServerClient.when(request().withMethod("GET")).respond(response().withStatusCode(200));
		
		// execute
		boolean success = executeCampaign.execute();

		// assert
		assertThat(success, is(true));
	}
	
	@Test
	public void executeFail() throws Exception {
		mockServerClient.when(request().withMethod("GET")).respond(response().withStatusCode(404));
		
		// execute
		boolean success = executeCampaign.execute();

		// assert
		assertThat(success, is(false));
	}
}
