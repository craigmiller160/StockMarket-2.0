package io.craigmiller160.stockmarket.aspects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An Aspect to handle all logging for GUI and GUI Dialog classes.
 * 
 * @author craig
 * @version 2.3
 *
 */
public aspect GuiLogging {

	/**
	 * The logger for this aspect.
	 */
	private static final Logger LOGGER = 
			LoggerFactory.getLogger("io.craigmiller160.stockmarket.gui");
	
	/**
	 * An advice that runs after the <tt>getValueForAction(String)</tt> method is called.
	 * If the method returns a value that's not null, the method and the value are
	 * logged.
	 * 
	 * @param s the command passed to the method.
	 * @param o the value returned by the method.
	 */
	after(String s) returning(Object o): execution(public Object getValueForAction(String)) && args(s){
		if(o != null){
			String methodName = thisJoinPoint.getSignature().getName();
			LOGGER.debug("{}: Command: {} Returns: {}", methodName, s, o);
		}
	}
	
}
