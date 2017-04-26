package org.cerberus.launchcampaign.checkCampaign;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.io.FileNotFoundException;
import java.util.*;

import org.cerberus.launchcampaign.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class CheckCampaignStatusTest {

	private String urlCheckCampaign;
	
	@Rule
	public MockServerRule mockServerRule = new MockServerRule(this);
	private MockServerClient mockServerClient;
	
	CheckCampaignStatus checkCampaignStatus;

//    @Rule
    public ResourceFile checkCampaignFinishJson = new ResourceFile("/org/cerberus/launchcampaign/checkCampaign/checkCampaignFinish.json");
    public ResourceFile checkCampaignInProgressJson = new ResourceFile("/org/cerberus/launchcampaign/checkCampaign/checkCampaignInProgress.json");
    
	@Before
	public void before() {
		urlCheckCampaign = "/Cerberus" ;
		checkCampaignStatus = new CheckCampaignStatus("tag123", "http://localhost:" + mockServerRule.getPort() + urlCheckCampaign);
	}
	
	@Test
	public void campaignIsFinished() throws Exception {
		mockServerClient.when(
				request().
				withMethod("GET").
				withPath(urlCheckCampaign + "/" + Constantes.URL_RESULT_CI).
				withQueryStringParameter("tag", "tag123"))
		.respond(
				response().
				withStatusCode(200).
				withBody(checkCampaignFinishJson.getContent())
		);
		
		final List<ResultCIDto> cptFinish= new ArrayList<>();
		// execute
		checkCampaignStatus.execute(
				result -> {
					// TODO FAIL
					return false;
				},
				result -> {
					cptFinish.add(result);
				}
		);
		
		assertThat("Campaign must be notify as finished", cptFinish.size(), is(1)); // test if campaign Is notify as Finished
		
		ResultCIDto result = cptFinish.get(0);
		
		assertThat("Result of campaign must be OK", "OK", is(result.getResult())); 

	}
	
	@Test
	public void campaignIsWaiting() throws Exception {
		mockServerClient.when(
				request().
				withMethod("GET").
				withPath(urlCheckCampaign + "/" + Constantes.URL_RESULT_CI).
				withQueryStringParameter("tag", "tag123"))
		.respond(
				response().
				withStatusCode(200).
				withBody(checkCampaignInProgressJson.getContent())
		);
		
		final List<ResultCIDto> cptWaiting= new ArrayList<>();
		final List<Integer> cptFinish= new ArrayList<>();
		// execute
		checkCampaignStatus.execute(
				result -> {
					if(!cptWaiting.isEmpty()) {
						return false;
					}
					cptWaiting.add(result);
					return true;
				},
				result -> {
					cptFinish.add(0);
				}
		);
		
		assertThat("Campaign must be waiting, and call 1st parameter of method execute", cptWaiting.size(), is(1));
		assertThat("Campaign is marked as finish, but campaign is pending", cptFinish.size(), is(0)); // verify process is not finish
	}
	
	@Test
	public void exceptionThrowExceptionInThread() throws Exception {
		mockServerClient.when(
				request().
				withMethod("GET").
				withPath(urlCheckCampaign + "/" + Constantes.URL_RESULT_CI).
				withQueryStringParameter("tag", "tag123"))
		.respond(
				response().
				withStatusCode(404)
		);
		
		boolean exceptionOk=false;
		try {
			checkCampaignStatus.execute(
					result -> {
						return true;
					},
					result -> {
					}
			);
		} catch(FileNotFoundException e) {
			exceptionOk=true;
		}
		
		assertThat("A FileNotFoundException must be throw by CheckCampaignStatus.execute when url is not found", true, is(exceptionOk));
		
	}
}
