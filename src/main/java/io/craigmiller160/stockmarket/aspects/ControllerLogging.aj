package io.craigmiller160.stockmarket.aspects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.FutureTask;

import io.craigmiller160.stockmarket.stock.Stock;

/**
 * An aspect to handle all logging for the execution of
 * methods in the <tt>StockMarketController</tt> class.
 * 
 * @author craig
 * @version 2.3
 */
public aspect ControllerLogging {

	/**
	 * The logger for this aspect.
	 */
	private static final Logger LOGGER = 
			LoggerFactory.getLogger(
					"io.craigmiller160.stockmarket.controller.StockMarketController");
	
	/**
	 * A pointcut pointing to all the methods in the <tt>StockMarketController</tt>
	 * class.
	 */
	pointcut controllerClassMethods() :
		execution(* io.craigmiller160.stockmarket.controller.StockMarketController.*(..));
	
	/**
	 * A pointcut pointing to methods to exclude from the operations of this
	 * aspect.
	 */
	pointcut controllerMethodsToExclude() :
		!execution(protected void processEvent(..))
		&& !execution(private void parseEvent(String,Object))
		&& !execution(private FutureTask<Stock> getDownload*(..))
		&& !execution(private void launderStockExecutionException(..))
		&& !execution(private void displayExceptionDialog(..))
		&& !execution(public * *PersistService(..))
		&& !execution(private void firePortfolioPropertyChanges(..));
	
	/**
	 * Advice to run before the execution of methods in the <tt>StockMarketController</tt>
	 * class. It logs information about the beginning of the method execution.
	 */
	before() : controllerClassMethods() && controllerMethodsToExclude(){
		String methodName = thisJoinPoint.getSignature().getName();
		Object valueFromView = null;
		if(thisJoinPoint.getArgs().length > 0){
			valueFromView = thisJoinPoint.getArgs()[0];
		}
		if(valueFromView == null){
			LOGGER.debug("{}: Entering", methodName);
		}
		else if(valueFromView.getClass().isArray()){
			LOGGER.debug("{} Entering. ValueFromView: {}", 
					methodName, Arrays.deepToString((Object[]) valueFromView));
		}
		else{
			LOGGER.debug("{} Entering. ValueFromView: {}", 
					methodName, valueFromView);
		}
	}
	
	/**
	 * Advice to run after the execution of methods in the <tt>StockMarketController</tt>
	 * class. It logs information about the method completion.
	 * 
	 * @param o the return value, if there is one, from the method.
	 */
	after() returning(Object o) : controllerClassMethods() && controllerMethodsToExclude(){
		String methodName = thisJoinPoint.getSignature().getName();
		Object valueFromView = null;
		if(thisJoinPoint.getArgs().length > 0){
			valueFromView = thisJoinPoint.getArgs()[0];
		}
		if(valueFromView == null){
			LOGGER.info("{} Completed", methodName);
		}
		else if(valueFromView.getClass().isArray()){
			LOGGER.info("{} Completed. ValueFromView: {}", 
					methodName, Arrays.deepToString((Object[]) valueFromView));
		}
		else{
			LOGGER.info("{} Completed. ValueFromView: {}", 
					methodName, valueFromView);
		}
	}
	
}
