package io.craigmiller160.stockmarket.util;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

/**
 * A Logger format that formats a log record for output to a CSV file. This allows
 * for easier parsing of log data using a spreadsheet program. For maximum effectiveness,
 * this <tt>Formatter</tt> is intended to be used alongside <tt>FileHandler</tt>s that
 * output to a ".csv" file.
 * <p>
 * In theory, the output this format produces will work with any spreadsheet program.
 * However, to date it has only been tested with Microsoft Excel.
 * <p>
 * <b>NOTE ABOUT EXCEPTIONS:</b> The full stack trace is outputted for any exceptions
 * this formatter receives via the <tt>LogRecord</tt>. However, the format places
 * each line of the stack trace on its own line within a single cell. Therefore, the
 * stack trace might not initially be visible when the .csv file is opened, until the
 * cell's height is changed.
 * <p>
 * This allows for quick, at a glance viewing of an exception, and the ability to 
 * expand and see the full stack trace if needed.
 * <p>
 * <b>THREAD SAFETY:</b> This formatter uses a counter to assign a unique index
 * number to each record it formats. This counter is properly synchronized, allowing
 * this formatter to be handled safely by as many threads as necessary.
 * 
 * @author craig
 * @version 2.0
 */
@ThreadSafe
public class LoggerCSVFormat extends Formatter{

	/**
	 * Incremented index value for all records formatted by
	 * this class.
	 */
	@GuardedBy("this")
	private int index;
	
	/**
	 * Constructs a new <tt>Formatter</tt> to format records
	 * for a CSV.
	 */
	public LoggerCSVFormat() {
		
	}

	@Override
	public String format(LogRecord record) {
		StringBuffer buffer = new StringBuffer();
		
		synchronized(this){
			if(index == 0){ //Create csv column headers
				buffer.append("Index,Level,Time,ThreadName.ID,"
						+ "Class,Method,Count: Parameters (Expand Cell),Message,Throwable (Expand Cell)");
				buffer.append(System.lineSeparator());
			}
			index++;
			buffer.append(index + ",");
		}
		
		buffer.append(record.getLevel() + ",");
		buffer.append(formatTime(record.getMillis()) + ",");
		
		ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
		String threadName = threadMXBean.getThreadInfo(record.getThreadID()).getThreadName();
		
		buffer.append(threadName + "." + record.getThreadID() + ",");
		buffer.append(record.getSourceClassName() + ",");
		buffer.append(record.getSourceMethodName() + ",");
		
		Object[] paramArr = record.getParameters();
		if(paramArr != null){
			buffer.append("\"" + paramArr.length + ": ");
			for(Object o : paramArr){
				buffer.append("(" + o + ") \n");
			}
			buffer.append("\",");
		}
		else{
			buffer.append(" ,");
		}
		
		buffer.append("\"" + record.getMessage() + "\" ,");
		
		Throwable thrown = record.getThrown();
		if(thrown != null){
			buffer.append("\"" + thrown);
			StackTraceElement[] steArr = thrown.getStackTrace();
			for(StackTraceElement ste : steArr){
				buffer.append("\nat " + ste);
			}
			buffer.append("\"");
		}
		
		buffer.append(System.lineSeparator());
		
		return buffer.toString();
	}
	
	/**
	 * Formats the raw millisecond time from the <tt>LogRecord</tt> into a 
	 * more readable format.
	 * 
	 * @param millisecs the raw millisecond time from the <tt>LogRecord</tt>.
	 * @return the formatted time value.
	 */
	private String formatTime(long millisecs){
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss:SSS");
		Date resultTime = new Date(millisecs);
		return timeFormat.format(resultTime);
	}

}
