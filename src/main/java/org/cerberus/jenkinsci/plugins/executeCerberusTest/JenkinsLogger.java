package org.cerberus.jenkinsci.plugins.executeCerberusTest;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.cerberus.launchcampaign.Constantes;

/**
 * A specific logger who write on a PrintStream (typically the PrintStream of Jenkins job console == TaskListener.getLogger())
 * Warning, Log.logger of log4j or sl4j write directly on Jenkins log file, not on job console. 
 * It's why we use a specifique class to write on job console
 * @author ndeblock
 *
 */
public class JenkinsLogger {

	private PrintStream logger;
	SimpleDateFormat dt = new SimpleDateFormat(Constantes.DATE_FORMAT); 

	/**
	 * 
	 * @param logger TaskListener.getLogger() to write on Jenkins job console
	 */
	public JenkinsLogger(PrintStream logger) {
		this.logger = logger;
	}
	
	
	private void log(Level level, String message) {
		logger.println(level.getValue() + " [" + dt.format(new Date()) + "] " + message);
	}
	
	public void debug(String message) {
		this.log(Level.DEBUG, message);
	}
	
	public void info(String message) {
		this.log(Level.INFO, message);
	}
	
	public void warning(String message) {
		this.log(Level.WARNING, message);
	}
	
	public void error(String message) {
		this.log(Level.ERROR, message);
	}
	
	public void error(String message, Throwable throwable) {
		this.log(Level.ERROR, message + "\n" + ExceptionUtils.getStackTrace(throwable));
	}
	
	private enum Level {
		DEBUG("[DEBUG]"), INFO("[INFO]"), WARNING("[WARNING]"), ERROR("[ERROR]");
		
		private final String value;
		Level(String value) { this.value = value; }
	    public String getValue() { return value; }
	}
}
