package io.craigmiller160.stockmarket.aspect;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Aspect to handle all logging for the <tt>Language</tt> module
 * for this program.
 * 
 * @author craig
 * @version 2.3
 */
public aspect LanguageLogging {

	/**
	 * The logger for this aspect.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger("io.craigmiller160.stockmarket.util.Language");
	
	/**
	 * A pointcut pointing to the <tt>setLocale(Locale)</tt> method of the
	 * <tt>Language</tt> class.
	 */
	pointcut localeSet() :
		execution(public void io.craigmiller160.stockmarket.util.Language.setLocale(*));
	
	/**
	 * Advice that runs before the execution of the target of the
	 * <tt>localeSet()</tt> pointcut. It logs the locale that the
	 * language module is being set to.
	 */
	before() : localeSet(){
		Locale locale = (Locale) thisJoinPoint.getArgs()[0];
		String methodName = thisJoinPoint.getSignature().getName();
		LOGGER.info("{}: {}", methodName, locale);
	}
	
}
