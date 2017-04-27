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
package org.cerberus.jenkinsci.plugins.executeCerberusTest;
import org.apache.commons.lang.StringUtils;
import org.cerberus.launchcampaign.checkCampaign.*;
import org.cerberus.launchcampaign.executeCampaign.*;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.*;

import hudson.*;
import hudson.model.*;
import hudson.tasks.*;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;

/**
 * ExecuteCerberusCampaign {@link Builder}.
 *
 * <p>
 * When the user configures the project and enables this builder,
 * {@link DescriptorImpl#newInstance(StaplerRequest)} is invoked
 * and a new {@link ExecuteCerberusCampaign} is created. The created
 * instance is persisted to the project configuration XML by using
 * XStream, so this allows you to use instance fields (like {@link #campaignName})
 * to remember the configuration.
 *
 * <p>
 * When a build is performed, the {@link #perform} method will be invoked. 
 *
 * @author Nicolas Deblock
 */
public class ExecuteCerberusCampaign extends Builder implements SimpleBuildStep {

	private final String campaignName;
	private final String platform;        
	private final String environment;
	private String browser;
	private String browserVersion;

	// Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
	@DataBoundConstructor
	public ExecuteCerberusCampaign(final String campaignName, final String platform, final String environment, final String browser, final String browserVersion) {
		this.campaignName = campaignName;
		this.platform=platform;
		this.environment=environment;
		this.browser =  browser; 
		this.browserVersion = browserVersion;
	}

	public String getCampaignName() {
		return campaignName;
	}

	@Override
	public void perform(final Run<?,?> build, final FilePath workspace, final Launcher launcher, final TaskListener listener) {
		final JenkinsLogger logger = new JenkinsLogger(listener.getLogger());

		// overide attribute if local settings is empty
		this.browser =   StringUtils.isEmpty(browser) ? getDescriptor().getBrowser() : browser; 
		this.browserVersion = StringUtils.isEmpty(browserVersion) ? getDescriptor().getBrowserVersion() : browserVersion;

		try {
			// 1 - Launch cerberus campaign    		
			ExecuteCampaignDto executeCampaignDto = new ExecuteCampaignDto(getDescriptor().getRobot(), getDescriptor().getSsIp(), 
					environment, browser, browserVersion, platform, campaignName);
			
			logger.info("Launch campaign " + executeCampaignDto.getSelectedCampaign() + " on " + getDescriptor().getUrlCerberus() + " (" +  executeCampaignDto.buildUrl(getDescriptor().getUrlCerberus()) + ")");
			final ExecuteCampaign executeCampaign = new ExecuteCampaign(getDescriptor().getUrlCerberus(), executeCampaignDto);

			if(executeCampaign.execute()) {
				// 2 - check if cerberus campaign is finish
				CheckCampaignStatus checkCampaignStatus = new CheckCampaignStatus(executeCampaignDto.getTagCerberus(), getDescriptor().getUrlCerberus(), getDescriptor().timeToRefreshCheckCampaignStatus, getDescriptor().timeOutForCampaignExecution);
				checkCampaignStatus.execute(new CheckCampaignStatus.CheckCampaignEvent() {
					
					@Override
					public boolean checkCampaign(ResultCIDto resultDto) {
						logger.info(resultDto.getTotalTestExecuted() + " test executed ... ("+ resultDto.logDetailExecution() + ")");
						logger.info(resultDto.getStatusPE() + resultDto.getStatusNE() + " test pending ...");
						logger.info("cerberus message : " + resultDto.getMessage());
						logger.info("Advancement : " + resultDto.getPercentOfTestExecuted() + "%");
						return true;
					}
				}, new CheckCampaignStatus.ResultEvent() {
					
					@Override
					public void result(ResultCIDto resultDto) {
						// display result and shutdown
						long timeToExecuteTest = resultDto.getExecutionEnd().getTime() - resultDto.getExecutionStart().getTime();
						logger.info("---------------------------------------------------------------------------------------------");
						logger.info("Result : " + resultDto.getResult() + ", test executed in " + ((int)(timeToExecuteTest/1000)) + "s " + ((int)(timeToExecuteTest%1000)) + "ms");
						logger.info(resultDto.logDetailExecution());		    							    					
						logger.info("---------------------------------------------------------------------------------------------");
	
						// fail if test is not OK
						if(!"OK".equals(resultDto.getResult())) {
							logger.error("FAIL");
							build.setResult(Result.FAILURE);
						}
					}
				});
			} else {
				logger.error("Fail to add campaign " + campaignName + " in cerberus queue");
				logger.error("UNSTABLE");
				build.setResult(Result.UNSTABLE);
			}
		} catch (Exception e) {
			logger.error("error for campaign  " + campaignName + " : " , e);
			logger.error("UNSTABLE");
			build.setResult(Result.UNSTABLE);
		} 
	}

	// Overridden for better type safety.
	// If your plugin doesn't really define any property on Descriptor,
	// you don't have to do this.
	private DescriptorImpl descr;
	@Override
	public DescriptorImpl getDescriptor() {
		try {
			return (DescriptorImpl)super.getDescriptor();
		} catch (NullPointerException e) {
			if(descr == null) {
				descr = new DescriptorImpl(true);
			}
			return descr;
		}
	}

	/**
	 * Descriptor for {@link ExecuteCerberusCampaign}. Used as a singleton.
	 * The class is marked as public so that it can be accessed from views.
	 *
	 * <p>
	 * See {@code src/main/resources/hudson/plugins/hello_world/HelloWorldBuilder/*.jelly}
	 * for the actual HTML fragment for the configuration screen.
	 */
	@Symbol("executeCerberusCampaign")
	@Extension // This indicates to Jenkins that this is an implementation of an extension point.
	public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
		/**
		 * To persist global configuration information,
		 * simply store it in a field and call save().
		 *
		 * <p>
		 * If you don't want fields to be persisted, use {@code transient}.
		 */
		private String urlCerberus;
		private String robot;
		private String ssIp;
		private String browser;
		private String browserVersion;
		private long timeToRefreshCheckCampaignStatus;
		private int timeOutForCampaignExecution;


		/**
		 * In order to load the persisted global configuration, you have to 
		 * call load() in the constructor.
		 */
		public DescriptorImpl() {
			load();
		}

		/**
		 * In order to load the persisted global configuration, you have to 
		 * call load() in the constructor.
		 */
		public DescriptorImpl(boolean fortest) {

		}

		@Override
		public boolean isApplicable(Class<? extends AbstractProject> aClass) {
			// Indicates that this builder can be used with all kinds of project types 
			return true;
		}

		/**
		 * This human readable name is used in the configuration screen.
		 */
		@Override
		public String getDisplayName() {
			return "Execute Cerberus Campaign";
		}

		@Override
		public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
			// To persist global configuration information,
			// set that to properties and call save().
			urlCerberus = formData.getString("urlCerberus");
			robot = formData.getString("robot");
			ssIp = formData.getString("ssIp");        
			browser = formData.getString("browser");
			browserVersion = formData.getString("browserVersion");
			timeToRefreshCheckCampaignStatus = formData.getLong("timeToRefreshCheckCampaignStatus");
			timeOutForCampaignExecution = formData.getInt("timeOutForCampaignExecution");
			// Can also use req.bindJSON(this, formData);
			//  (easier when there are many fields; need set* methods for this)
			save();
			return super.configure(req,formData);
		}

		public String getUrlCerberus() {
			return urlCerberus;
		}      
		public void setUrlCerberus(String url) {
			this.urlCerberus=url;
		}

		public String getRobot() {
			return robot;
		}

		public void setRobot(String robot) {
			this.robot = robot;
		}

		public String getSsIp() {
			return ssIp;
		}

		public void setSsIp(String ssIp) {
			this.ssIp = ssIp;
		}

		public String getBrowser() {
			return browser;
		}

		public void setBrowser(String browser) {
			this.browser = browser;
		}

		public String getBrowserVersion() {
			return browserVersion;
		}

		public void setBrowserVersion(String browserVersion) {
			this.browserVersion = browserVersion;
		}

		public long getTimeToRefreshCheckCampaignStatus() {
			return timeToRefreshCheckCampaignStatus;
		}

		public void setTimeToRefreshCheckCampaignStatus(long timeToRefreshCheckCampaignStatus) {
			this.timeToRefreshCheckCampaignStatus = timeToRefreshCheckCampaignStatus;
		}

		public int getTimeOutForCampaignExecution() {
			return timeOutForCampaignExecution;
		}

		public void setTimeOutForCampaignExecution(int timeOutForCampaignExecution) {
			this.timeOutForCampaignExecution = timeOutForCampaignExecution;
		}

	}

}

