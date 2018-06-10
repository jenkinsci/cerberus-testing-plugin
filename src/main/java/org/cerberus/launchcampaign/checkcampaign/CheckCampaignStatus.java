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

import java.net.SocketException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.cerberus.launchcampaign.Constantes;
import org.cerberus.launchcampaign.event.LogEvent;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Check all 5 seconds the status of campaign's execution. Use example :
 * <pre>
 * checkCampaignStatus.execute(resultDto -> {
 *		logger.println("Advancement : " + resultDto.getPercentOfTestExecuted() + "%");
 *  }, resultDto -> {
 *  	// display result and shutdown
 * 		logger.println("Result : " + resultDto.getResult() + ");
 *  });
 * </pre>
 *
 * @author ndeblock
 *
 */
public class CheckCampaignStatus {

    private String tagCerberus;
    private String urlCerberus;
    private long timeToRefreshCampaignStatus;
    private int timeoutForCampaignExecution;

    /**
     *
     * @param tagCerberus the tag use when campaign was added to cerberus queue
     * @param urlCerberus url of cerberus (ex : http://cerberus/Cerberus)
     */
    public CheckCampaignStatus(final String tagCerberus, final String urlCerberus) {
        this(tagCerberus, urlCerberus, Constantes.TIME_TO_REFRESH_CAMPAIGN_STATUS_DEFAULT, Constantes.TIMEOUT_FOR_CAMPAIGN_EXECUTION);
    }

    /**
     *
     * @param tagCerberus the tag use when campaign was added to cerberus queue
     * @param urlCerberus url of cerberus (ex : http://cerberus/Cerberus)
     * @param timeToRefreshCampaignStatus Time to refresh the campaign status
     * (seconds). 5s by default
     * @param timeoutForCampaignExecution Timeout for campaign execution
     * (seconds). After this time, if campaign is not finished, job failed
     */
    public CheckCampaignStatus(final String tagCerberus, final String urlCerberus, final long timeToRefreshCampaignStatus, final int timeoutForCampaignExecution) {
        this.tagCerberus = tagCerberus;
        this.urlCerberus = urlCerberus;
        this.timeToRefreshCampaignStatus = timeToRefreshCampaignStatus;
        this.timeoutForCampaignExecution = timeoutForCampaignExecution;
    }

    /**
     * Check all 5 seconds the status of campaign's execution.
     *
     * @param checkCampaign call method checkCampaign() all 5 seconds with
     * parameter {@link ResultCIDto}. {@link ResultCIDto} contains all
     * information of execution of campaing at the instant t
     * @param result call method result() when campaign execution is finish.
     * {@link ResultCIDto} contains all information of execution at finish time
     * @param logEvent
     * @throws Exception
     */
    public void execute(final CheckCampaignEvent checkCampaign, final ResultEvent result, final LogEvent logEvent) throws Exception {
        final ScheduledThreadPoolExecutor sch = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);

        final AtomicReference<Exception> exceptionOnThread = new AtomicReference<Exception>();

        if (this.timeToRefreshCampaignStatus == 0) {
            logEvent.log("", "", "'Time to refresh the campaign status' parameter empty. Running with default : " + Constantes.TIME_TO_REFRESH_CAMPAIGN_STATUS_DEFAULT);
            this.timeToRefreshCampaignStatus = Constantes.TIME_TO_REFRESH_CAMPAIGN_STATUS_DEFAULT;
        }
        if (this.timeoutForCampaignExecution == 0) {
            logEvent.log("", "", "'Timeout for campaign execution' parameter empty. Running with default : " + Constantes.TIMEOUT_FOR_CAMPAIGN_EXECUTION);
            this.timeoutForCampaignExecution = Constantes.TIMEOUT_FOR_CAMPAIGN_EXECUTION;
        }

        logEvent.log("", "", "Starting to get Result from Cerberus : " + urlCerberus + "/" + Constantes.URL_RESULT_CI + "?tag=" + tagCerberus);
        logEvent.log("", "", "Looping every : " + this.timeToRefreshCampaignStatus + " s. - Timeout after : " + this.timeoutForCampaignExecution + "s.");

        sch.scheduleWithFixedDelay(new Runnable() {

            @Override
            public void run() {
                try {

                    URL resultURL = new URL(urlCerberus + "/" + Constantes.URL_RESULT_CI + "?tag=" + tagCerberus);
                    ResultCIDto resultDto = new ObjectMapper().readValue(resultURL, ResultCIDto.class);

                    // condition to finish task
                    if (!"PE".equals(resultDto.getResult())) {
                        result.result(resultDto);
                        sch.shutdown(); // when campaign is finish, we shutdown the schedule thread
                    }

                    if (!checkCampaign.checkCampaign(resultDto)) {
                        sch.shutdown();
                    }
                } catch (SocketException e) {
                    // do nothing during network problem. Wait the timeout to shutdown, and notify the error to logEvent
                    logEvent.log("", e.getMessage() + "\n" + ExceptionUtils.getStackTrace(e), "");
                } catch (Exception e) {
                    exceptionOnThread.set(e);
                    sch.shutdown();
                }
            }
        },
                0, this.timeToRefreshCampaignStatus, TimeUnit.SECONDS);

        if (!sch.awaitTermination(this.timeoutForCampaignExecution, TimeUnit.SECONDS)) {
            logEvent.log("Interruped by timeout of " + this.timeoutForCampaignExecution + "s (see 'Global Settings' of Cerberus Plugin)", "", "");
            result.result(null);
        }

        // pass exeption of thread to called method
        if (exceptionOnThread.get() != null) {
            throw exceptionOnThread.get();
        }
    }

    /**
     * Use to show return of a check of campaign's execution
     *
     * @author ndeblock
     *
     */
    public interface CheckCampaignEvent {

        /**
         *
         * @param resultDto {@link ResultCIDto} contains all information of
         * execution of campaing at the instant t
         * @return true if continue process, false if you want finish process
         */
        public boolean checkCampaign(ResultCIDto resultDto);
    }

    /**
     * Use to show the result of a cerberus campaign
     *
     * @author ndeblock
     *
     */
    public interface ResultEvent {

        /**
         *
         * @param resultDto {@link ResultCIDto} contains all information of
         * execution at finish time
         */
        public void result(ResultCIDto resultDto);
    }
}
