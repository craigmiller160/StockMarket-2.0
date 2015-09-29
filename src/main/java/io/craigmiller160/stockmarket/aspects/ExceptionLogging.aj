package io.craigmiller160.stockmarket.aspects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

/**
 * An Aspect for logging exceptions that occur during the 
 * program.
 * 
 * @author craig
 * @version 2.3
 */
public aspect ExceptionLogging {

	/**
	 * The logger for this aspect.
	 */
	private static final Logger LOGGER = 
			LoggerFactory.getLogger("io.craigmiller160.stockmarket.exception");
	
	/**
	 * A pointcut pointing to the <tt>UncaughtExceptionHandler</tt> classes.
	 */
	pointcut uncaughtExceptionHandler() :
		execution(* *UncaughtExceptionHandler.*(..));
	
	/**
	 * A pointcut pointing to the <tt>Main</tt> class of this program.
	 */
	pointcut mainClass() :
		within(io.craigmiller160.stockmarket.Main);
	
	/**
	 * A pointcut pointing to the <tt>StockMarketController</tt> class.
	 */
	pointcut controller() :
		within(io.craigmiller160.stockmarket.controller.StockMarketController);
	
	/**
	 * Advice that runs before an <tt>UncaughtExceptionHandler</tt> executes.
	 * It logs the uncaught exception.
	 * 
	 * @param thread the thread the exception came from.
	 * @param t the uncaught exception.
	 */
	before(Thread thread, Throwable t) : 
		uncaughtExceptionHandler() && args(thread,t){
		String stackTrace = getStackTraceString(t);
		LOGGER.error("\"UNCAUGHT EXCEPTION: {}\"", stackTrace);
	}
	
	/**
	 * Advice that runs before an exception is handled in the main class.
	 * It logs the exception.
	 * 
	 * @param ex the exception to be logged.
	 */
	before(Exception ex) : mainClass() && ((handler(Exception) && args(ex))
		|| (handler(IOException) && args(ex))){
		String stackTrace = getStackTraceString(ex);
		LOGGER.error("\"EXCEPTION: {}\"", stackTrace);
	}
	
	/**
	 * Advice that runs before an exception is handled in the controller
	 * class. It logs the exception.
	 * 
	 * @param ex the exception to be logged.
	 */
	before(Exception ex) : controller() && (
			(handler(Exception) && args(ex))
			|| (handler(IOException) && args(ex))
			|| (handler(InterruptedException) && args(ex))
			|| (handler(URISyntaxException) && args(ex))
			|| (handler(UnknownHostException) && args(ex))){
		String stackTrace = getStackTraceString(ex);
		LOGGER.error("\"EXCEPTION: {}\"", stackTrace);
	}
	
	/**
	 * Advice that runs before an <tt>ExecutionException</tt> is handled
	 * in the controller class. It logs the cause of that exception.
	 * 
	 * @param ex the <tt>ExecutionException</tt> to be logged.
	 */
	before(ExecutionException ex) : controller() && handler(ExecutionException) && args(ex){
		String stackTrace = getStackTraceString(ex.getCause());
		LOGGER.error("\"EXCEPTION: {}\"", stackTrace);
	}
	
	/**
	 * Parses the stack trace of an exception and returns a
	 * <tt>String</tt> so that the entire stack trace can be
	 * placed in a single cell in a CSV spreadsheet. 
	 * 
	 * @param t the exception to get the stack trace from.
	 * @return a <tt>String</tt> with the stack trace of the exception.
	 */
	private String getStackTraceString(Throwable t){
		StringBuffer buffer = new StringBuffer(t.getMessage());
		StackTraceElement[] steArr = t.getStackTrace();
		for(StackTraceElement ste : steArr){
			buffer.append("\n" + ste);
		}
		return buffer.toString();
	}
	
}
