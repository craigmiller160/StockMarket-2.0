package io.craigmiller160.stockmarket.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * A format designed to display key information for all entries
 * into this program's log.
 * 
 * @author Craig
 * @version 2.0
 */
public class LoggerFormat extends Formatter{
	
	/**
	 * Instantiates this <tt>Formatter</tt> object.
	 */
	public LoggerFormat(){}
	
	/**
	 * Formats a log record to display its information in a clear
	 * and detailed fashion, prior to being published in the log
	 * itself.
	 * 
	 * @param record the log record to be formatted.
	 */
	@Override
	public String format(LogRecord record) {
		StringBuffer buf = new StringBuffer(1000);
		
		//Line one of output
		buf.append(record.getLevel() + "!");
		buf.append(" \"" + record.getMessage() + "\"");
		buf.append(" " + formatDate(record.getMillis()));
		buf.append(System.lineSeparator());
		
		//Line two of output
		buf.append("    Thread " + record.getThreadID());
		buf.append(" " + record.getSourceClassName());
		buf.append(" " + record.getSourceMethodName());
		Object[] obArr = record.getParameters();
		if(obArr != null){
			buf.append(" Parameters: ");
			for(Object o : obArr){
				buf.append(o + ", ");
			}
		}
		buf.append(System.lineSeparator());
		
		//Line three of output
		Throwable thrown = record.getThrown();
		if(thrown != null){
			buf.append("    " + thrown);
			//buf.append(System.lineSeparator() + "        " + thrown.getMessage());
			/*StackTraceElement[] steArr = thrown.getStackTrace();
			for(StackTraceElement ste : steArr){
				buf.append(System.lineSeparator() + "        " + ste.toString());
			}*/
			buf.append(System.lineSeparator());
		}
		buf.append(System.lineSeparator());
		
		return buf.toString();
	}
	
	/**
	 * Formats the raw millisecond amount of the date into a style more
	 * fitting for the log entry.
	 * 
	 * @param millisecs the date in milliseconds since 1970.
	 * @return the formatted date.
	 */
	private String formatDate(long millisecs){
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:SSS");
		Date resultDate = new Date(millisecs);
		return dateFormat.format(resultDate);
	}

	
	
}
