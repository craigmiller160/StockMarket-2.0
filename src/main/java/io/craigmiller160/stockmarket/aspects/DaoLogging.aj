package io.craigmiller160.stockmarket.aspects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.craigmiller160.stockmarket.controller.HibernatePortfolioDao;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;

/**
 * An Aspect for all logging operations for the DAO
 * class.
 * <p>
 * As of Version 2.4, this aspect has been expanded
 * to also log all methods from the corresponding
 * service class as well.
 * 
 * @author craig
 * @version 2.4
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
		execution(public * io.craigmiller160.stockmarket.controller.HibernatePortfolioDao.*(..));
	
	/**
	 * A pointcut that points to all public methods in the <tt>PortfolioPersistServiceImpl</tt> class.
	 */
	pointcut persistService() :
		execution(public * io.craigmiller160.stockmarket.controller.PortfolioPersistServiceImpl.*(..));
	
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
	 * An Advice that runs before the execution of the DAO/Service methods
	 * in this program. It logs the entry into the methods and any arguments
	 * that are being passed.
	 */
	before() : (hibernateDao() && noCloseFactoryMethod()) || persistService(){
		String methodName = thisJoinPoint.getSignature().getName();
		String args = Arrays.deepToString(thisJoinPoint.getArgs());
		String source = null;
		if(thisJoinPoint.getTarget() instanceof HibernatePortfolioDao){
			source = "DAO";
		}
		else{
			source = "Service";
		}
		LOGGER.debug("{}.{} Beginning: {}", source, methodName, args);
	}
	
	/**
	 * An Advice that runs after the execution of the DAO/Service methods
	 * in this program. It logs that the methods completed and the value 
	 * they return, if any.
	 * 
	 * @param o the value the methods return.
	 */
	after() returning(Object o) : hibernateDao() && noPropertyChangeMethods() 
		&& noCloseFactoryMethod(){
		String methodName = thisJoinPoint.getSignature().getName();
		String source = null;
		if(thisJoinPoint.getTarget() instanceof HibernatePortfolioDao){
			source = "DAO";
		}
		else{
			source = "Service";
		}
		if(o != null){
			LOGGER.info("{}.{} Complete. Result: {}", source, methodName, o);
		}
		else{
			LOGGER.info("{}.{} Complete", source, methodName);
		}
	}
	
}
