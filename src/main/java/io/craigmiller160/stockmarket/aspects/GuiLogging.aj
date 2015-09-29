package io.craigmiller160.stockmarket.aspects;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.craigmiller160.stockmarket.gui.PortfolioState;
import io.craigmiller160.stockmarket.stock.OwnedStock;
import io.craigmiller160.stockmarket.stock.Stock;

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
	after(String s) returning(Object o): execution(public Object getValueForAction(String)) 
		&& args(s){
		if(o != null){
			String methodName = thisJoinPoint.getSignature().getName();
			String className = thisJoinPoint.getSignature().getName();
			LOGGER.debug("{}.{}: Command: {} Returns: {}", className, methodName, s, o);
		}
	}
	
	/**
	 * An advice that runs before the <tt>portfolioStateChanged(PortfolioState)</tt> method
	 * is called. This advice logs the new portfolio state that's being reflected in the GUI.
	 * 
	 * @param ps the new <tt>PortfolioState</tt>.
	 */
	before(PortfolioState ps) : execution(public void portfolioStateChanged(PortfolioState)) 
		&& args(ps){
		String methodName = thisJoinPoint.getSignature().getName();
		String className = thisJoinPoint.getTarget().getClass().getName();
		LOGGER.trace("{}.{}: {}", className, methodName, ps);
	}
	
	/**
	 * An advice that runs before the <tt>displayDialog(Object[])</tt> method in the
	 * <tt>Frame</tt> class. This advice logs the configuration information for the
	 * dialog.
	 * 
	 * @param obArr the dialog configuration
	 */
	before(Object[] obArr) : 
		execution(public void io.craigmiller160.stockmarket.gui.Frame.displayDialog(Object[])) 
		&& args(obArr){
		String methodName = thisJoinPoint.getSignature().getName();
		String config = Arrays.deepToString(obArr);
		LOGGER.debug("{}: {}", methodName, config);
	}
	
	/**
	 * An advice that runs before setter methods in various GUI classes. 
	 * This advice logs the values that the panel's fields are being set to.
	 */
	before() :
		execution(public void io.craigmiller160.stockmarket.gui.*.set*(..))
		&& !within(io.craigmiller160.stockmarket.gui.Frame) 
		&& !within(io.craigmiller160.stockmarket.gui.SearchPanel){
		String methodName = thisJoinPoint.getSignature().getName();
		Object value = thisJoinPoint.getArgs()[0];
		LOGGER.trace("{}: {}", methodName, value);
	}
	
	/**
	 * An advice that runs before the <tt>displayStockDetails(Stock)</tt> method of
	 * the <tt>StockDetailsPanel</tt> class. This advice logs the stock being displayed.
	 * 
	 * @param s the <tt>Stock</tt> being displayed.
	 */
	before(Stock s) : 
		execution(public void io.craigmiller160.stockmarket.gui.StockDetailsPanel.displayStockDetails(Stock))
		&& args(s){
		String methodName = thisJoinPoint.getSignature().getName();
		boolean owned = s instanceof OwnedStock ? true : false;
		LOGGER.debug("{}: {}. Owned: {}", methodName, s, owned);
	}
}
