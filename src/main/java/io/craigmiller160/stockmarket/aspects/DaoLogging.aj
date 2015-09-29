package io.craigmiller160.stockmarket.aspects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;

/**
 * An Aspect for all logging operations for the DAO
 * class.
 * 
 * @author craig
 * @version 2.3
 */
public aspect DaoLogging {

	/**
	 * The logger for this aspect.
	 */
	private static final Logger LOGGER = 
			LoggerFactory.getLogger("io.craigmiller160.stockmarket.controller.DAO");
	
	/**
	 * A pointcut that points to all public methods in the <tt>HibernatePortfolioDAO</tt> class.
	 */
	pointcut hibernateDao() : 
		execution(public * io.craigmiller160.stockmarket.controller.HibernatePortfolioDAO.*(..));
	
	/**
	 * A pointcut that excludes all methods that either accept one 
	 * <tt>PropertyChangeListener</tt> argument or one <tt>PropertyChangeEvent</tt>
	 * argument.
	 */
	pointcut noPropertyChangeMethods() : !args(PropertyChangeListener) && !args(PropertyChangeEvent);
	
	/**
	 * A pointcut that excludes the <tt>closeFactory()</tt> method.
	 */
	pointcut noCloseFactoryMethod() : !execution(public void closeFactory());
	
	/**
	 * An Advice that runs before the execution of the CRUD methods
	 * in the <tt>HibernatePortfolioDAO</tt> class. This advice logs
	 * that the methods have been accessed.
	 */
	before() : hibernateDao() && noPropertyChangeMethods() 
		&& noCloseFactoryMethod(){
		String methodName = thisJoinPoint.getSignature().getName();
		String args = Arrays.deepToString(thisJoinPoint.getArgs());
		LOGGER.debug("\"{} Beginning: {}\"", methodName, args);
	}
	
	/**
	 * An Advice that runs after the CRUD methods in the <tt>HibernatePortfolioDAO</tt>
	 * class return. It logs that the methods completed and the value they return.
	 * 
	 * @param o the value the methods return.
	 */
	after() returning(Object o) : hibernateDao() && noPropertyChangeMethods() 
		&& noCloseFactoryMethod(){
		String methodName = thisJoinPoint.getSignature().getName();
		if(o != null){
			LOGGER.info("\"{} Complete. Result: {}\"", methodName, o);
		}
		else{
			LOGGER.info("{} Complete", methodName);
		}
	}
	
}
