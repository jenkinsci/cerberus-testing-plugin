package org.cerberus.launchcampaign.event;

import org.cerberus.launchcampaign.checkcampaign.ResultCIDto;

/**
 * Use to show the result of a cerberus campaign
 * @author ndeblock
 *
 */
public interface LogEvent {
	/**
	 * 
	 * @param resultDto {@link ResultCIDto} contains all information of execution at finish time
	 */
	public void log(String error, String warning, String info);
}