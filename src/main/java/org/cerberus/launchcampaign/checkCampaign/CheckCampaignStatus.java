package org.cerberus.launchcampaign.checkCampaign;

import java.net.URL;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

import org.cerberus.launchcampaign.Constantes;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Check all 5 seconds the status of campaign's execution. 
 * Use example : <pre>
 * checkCampaignStatus.execute(resultDto -> {	    			
 *		logger.println("Advancement : " + resultDto.getPercentOfTestExecuted() + "%");
 *  }, resultDto -> {
 *  	// display result and shutdown
 * 		logger.println("Result : " + resultDto.getResult() + ");  							    						    				
 *  });
 *   		</pre>
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
	 * @param timeToRefreshCampaignStatus Time to refresh the campaign status (seconds). 5s by default
	 * @param timeoutForCampaignExecution Timeout for campaign execution (hours). After this time, if campaign is not finished, job failed
	 */
	public CheckCampaignStatus(final String tagCerberus, final String urlCerberus, final long timeToRefreshCampaignStatus, final int timeoutForCampaignExecution) {
		this.tagCerberus=tagCerberus;
		this.urlCerberus=urlCerberus;
		this.timeToRefreshCampaignStatus=timeToRefreshCampaignStatus;
		this.timeoutForCampaignExecution=timeoutForCampaignExecution;
	}
	
	/**
	 * Check all 5 seconds the status of campaign's execution. 
	 * @param checkCampaign call method checkCampaign() all 5 seconds with parameter {@link ResultCIDto}.  
	 * 						{@link ResultCIDto} contains all information of execution of campaing at the instant t
	 * @param result call method result() when campaign execution is finish.
	 * 				{@link ResultCIDto} contains all information of execution at finish time
	 * @throws Exception 
	 */
	public void execute(final CheckCampaignEvent checkCampaign, final ResultEvent result) throws Exception {
		final ScheduledThreadPoolExecutor sch = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(5);

		final AtomicReference<Exception> exceptionOnThread = new AtomicReference<Exception>();
		
		sch.scheduleWithFixedDelay(new Runnable() {
			
			@Override
			public void run() {
				try {
					URL resultURL =  new URL(urlCerberus + "/" + Constantes.URL_RESULT_CI + "?tag=" + tagCerberus);
					ResultCIDto resultDto = new ObjectMapper().readValue(resultURL, ResultCIDto.class); 	
										
					// condition to finish task
					if(!"PE".equals(resultDto.getResult())) {
						result.result(resultDto);
						sch.shutdown(); // when campaign is finish, we shutdown the schedule thread
					}
					
					if(!checkCampaign.checkCampaign(resultDto)) {
						sch.shutdown();
					}
				} catch (Exception e ) {
					exceptionOnThread.set(e);
					sch.shutdown();
				}
			}
		}
		, 0, this.timeToRefreshCampaignStatus, TimeUnit.SECONDS);

		sch.awaitTermination(this.timeoutForCampaignExecution, TimeUnit.HOURS);
		
		// pass exeption of thread to called method
		if (exceptionOnThread.get() != null) {
		   throw exceptionOnThread.get();
		}
	}	
	
	/**
	 * Use to show return of a check of campaign's execution 
	 * @author ndeblock
	 *
	 */
	public interface CheckCampaignEvent {
		/**
		 * 
		 * @param resultDto {@link ResultCIDto} contains all information of execution of campaing at the instant t
		 * @return true if continue process, false if you want finish process
		 */
		public boolean checkCampaign(ResultCIDto resultDto);
	}
	
	/**
	 * Use to show the result of a cerberus campaign
	 * @author ndeblock
	 *
	 */
	public interface ResultEvent {
		/**
		 * 
		 * @param resultDto {@link ResultCIDto} contains all information of execution at finish time
		 */
		public void result(ResultCIDto resultDto);
	}
}
