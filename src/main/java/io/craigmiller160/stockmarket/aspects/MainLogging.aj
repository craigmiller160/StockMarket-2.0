package io.craigmiller160.stockmarket.aspects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An aspect to handle all logging for the execution of
 * methods in the <tt>Main</tt> class.
 * 
 * @author craig
 * @version 2.3
 */
public aspect MainLogging {

	/**
	 * The logger for this aspect.
	 */
	private static final Logger LOGGER = 
			LoggerFactory.getLogger("io.craigmiller160.stockmarket.Main");
	
	/**
	 * A pointcut pointing to all the methods in the <tt>Main</tt>
	 * class.
	 */
	pointcut mainClassMethods() :
		execution(* io.craigmiller160.stockmarket.Main.*(..));
	
	/**
	 * A pointcut pointing to methods that should be excluded
	 * from aspect operations in the <tt>Main</tt> class.
	 */
	pointcut mainMethodsToExclude() :
		!execution(private static boolean verifyDirectory())
		&& !execution(private static void displayExceptionDialog(..));
		
	
	/**
	 * Advice to run before methods in the <tt>Main</tt> class.
	 * It logs entry into the methods and the beginning of their
	 * operations.
	 */
	before() : mainClassMethods() && mainMethodsToExclude(){
		String methodName = thisJoinPoint.getSignature().getName();
		LOGGER.debug("{}: Entering", methodName);
	}
	
	/**
	 * Advice to run after methods in the <tt>Main</tt> class
	 * execute. It logs the completion of the methods. 
	 * 
	 * @param o a return value, if any, from the methods.
	 */
	after() returning(Object o) : mainClassMethods() && mainMethodsToExclude(){
		String methodName = thisJoinPoint.getSignature().getName();
		LOGGER.info("{} Completed Successfully", methodName);
	}
	
}
