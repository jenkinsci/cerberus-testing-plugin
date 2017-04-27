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
