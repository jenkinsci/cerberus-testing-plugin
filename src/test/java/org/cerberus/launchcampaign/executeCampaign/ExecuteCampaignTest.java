package org.cerberus.launchcampaign.executeCampaign;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import org.cerberus.launchcampaign.Constantes;
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
