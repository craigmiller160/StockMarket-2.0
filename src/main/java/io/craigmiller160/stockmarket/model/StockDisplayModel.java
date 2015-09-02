package io.craigmiller160.stockmarket.model;

import static io.craigmiller160.stockmarket.controller.StockMarketController.SELECTED_STOCK_HISTORY_PROPERTY;
import static io.craigmiller160.stockmarket.controller.StockMarketController.SELECTED_STOCK_PROPERTY;
import io.craigmiller160.mvp.core.AbstractPropertyModel;
import io.craigmiller160.stockmarket.stock.AbstractStock;
import io.craigmiller160.stockmarket.stock.HistoricalQuote;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

/**
 * A <tt>JavaBean</tt> bound property model defining the value of the stock
 * currently being displayed in the program. Contains separate properties for
 * both the stock itself and the stock's history.
 * <p>
 * The stock history list parameter here is an <tt>AbstractList</tt>, rather
 * than a <tt>List</tt>, to facilitate the reflective setter 
 * <p>
 * <b>THREAD SAFETY:</b> This class is completely thread safe. The only potential
 * area it could be compromised is with the <tt>AbstractStock</tt> reference. Its
 * reference leaks out via the getter method and the <tt>PropertyChangeEvent</tt>
 * fired by its setter. If a thread-safe implementation of this abstract class
 * is used, there will be no risk. Consult the documentation for the stock class
 * implementation being used by this program to determine if additional locking is
 * needed.
 * 
 * @author craig
 * @version 2.0
 */
@ThreadSafe
public class StockDisplayModel extends AbstractPropertyModel {

	/**
	 * SerialUID for serialization support.
	 */
	private static final long serialVersionUID = -4589349711960887940L;
	
	/**
	 * The currently selected and displayed stock.
	 */
	@GuardedBy("this")
	private AbstractStock selectedStock;
	
	/**
	 * The history of the currently selected and displayed stock.
	 */
	@GuardedBy("this")
	private List<HistoricalQuote> selectedStockHistory;
	
	/**
	 * Shared logger for the program.
	 */
	private static final Logger LOGGER = Logger.getLogger("stockmarket.model.StockDisplayModel");
	
	/**
	 * Creates a new instance of this bound property model.
	 */
	public StockDisplayModel() {
		super();
	}
	
	/**
	 * Sets the selected and displayed stock. This is a bound 
	 * property, and a <tt>PropertyChangeEvent</tt> is fired when it is
	 * changed.
	 * 
	 * @param selectedStock the currently selected and displayed stock.
	 */
	public void setSelectedStock(AbstractStock selectedStock){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"setSelectedStock", "Entering method", 
				new Object[] {"Selected Stock: " + selectedStock});
		
		AbstractStock oldValue = null;
		synchronized(this){
			oldValue = this.selectedStock;
			this.selectedStock = selectedStock;
		}
		
		firePropertyChange(SELECTED_STOCK_PROPERTY, oldValue, selectedStock);
	}
	
	//TODO the event historyList reference needs to be made more thread-safe
	/**
	 * Sets the history of the selected and displayed stock. This is a bound 
	 * property, and a <tt>PropertyChangeEvent</tt> is fired when it is
	 * changed.
	 * 
	 * @param selectedStockHistory the history of the selected and displayed stock.
	 */
	public void setSelectedStockHistory(List<HistoricalQuote> selectedStockHistory){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"setSelectedStockHistory", "Entering method", 
				new Object[] {"Selected History: " + selectedStockHistory});
		
		List<HistoricalQuote> oldValue = null;
		synchronized(this){
			oldValue = this.selectedStockHistory;
			this.selectedStockHistory = selectedStockHistory;
		}
		
		firePropertyChange(SELECTED_STOCK_HISTORY_PROPERTY, oldValue, selectedStockHistory);
	}
	
	/**
	 * Returns the selected stock.
	 * 
	 * @return the selected stock.
	 */
	public synchronized AbstractStock getSelectedStock(){
		return selectedStock;
	}
	
	//TODO stockHistory return method needs to be made more thread safe
	/**
	 * Returns the history of the selected stock.
	 * 
	 * @return the history of the selected stock.
	 */
	public synchronized List<HistoricalQuote> getSelectedStockHistory(){
		return selectedStockHistory;
	}

}
