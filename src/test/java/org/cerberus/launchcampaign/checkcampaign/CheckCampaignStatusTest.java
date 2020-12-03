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
package org.cerberus.launchcampaign.checkcampaign;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.cerberus.launchcampaign.Constantes;
import org.cerberus.launchcampaign.ResourceFile;
import org.cerberus.launchcampaign.event.LogEvent;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
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

    private CheckCampaignStatus checkCampaignStatus;
    private LogEvent logEvent;

//    @Rule
    private ResourceFile checkCampaignFinishJson = new ResourceFile("/org/cerberus/launchcampaign/checkcampaign/checkCampaignFinish.json");
    private ResourceFile checkCampaignInProgressJson = new ResourceFile("/org/cerberus/launchcampaign/checkcampaign/checkCampaignInProgress.json");

    @Before
    public void before() {
        urlCheckCampaign = "/Cerberus";
        checkCampaignStatus = new CheckCampaignStatus("tag123", "http://localhost:" + mockServerRule.getPort() + urlCheckCampaign, "");

        logEvent = new LogEvent() {
            @Override
            public void log(String error, String warning, String info) {
            }
        };
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

        final List<ResultCIDto> cptFinish = new ArrayList<>();
        // execute
        checkCampaignStatus.execute(new CheckCampaignStatus.CheckCampaignEvent() {
            @Override
            public boolean checkCampaign(ResultCIDto result) {
                return false;
            }
        },
                new CheckCampaignStatus.ResultEvent() {
            @Override
            public void result(ResultCIDto result) {
                cptFinish.add(result);
            }
        },
                logEvent
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

        final List<ResultCIDto> cptWaiting = new ArrayList<>();
        final List<Integer> cptFinish = new ArrayList<>();
        // execute
        checkCampaignStatus.execute(new CheckCampaignStatus.CheckCampaignEvent() {
            @Override
            public boolean checkCampaign(ResultCIDto result) {
                if (!cptWaiting.isEmpty()) {
                    return false;
                }
                cptWaiting.add(result);
                return true;
            }
        },
                new CheckCampaignStatus.ResultEvent() {
            @Override
            public void result(ResultCIDto result) {
                cptFinish.add(0);
            }
        },
                logEvent
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

        boolean exceptionOk = false;
        try {
            checkCampaignStatus.execute(new CheckCampaignStatus.CheckCampaignEvent() {
                @Override
                public boolean checkCampaign(ResultCIDto result) {
                    return true;
                }
            },
                    new CheckCampaignStatus.ResultEvent() {
                @Override
                public void result(ResultCIDto result) {
                    // nothing to do
                }
            },
                    logEvent
            );

        } catch (Exception e) {
            exceptionOk = true;
        }

        assertThat("An Exception must be throw by CheckCampaignStatus.execute when url is not found", true, is(exceptionOk));

    }
}
