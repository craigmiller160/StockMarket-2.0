package io.craigmiller160.stockmarket.model;

import static io.craigmiller160.stockmarket.controller.StockMarketController.DIALOG_DISPLAYED_PROPERTY;
import static io.craigmiller160.stockmarket.controller.StockMarketController.PORTFOLIO_STATE_PROPERTY;
import io.craigmiller160.stockmarket.gui.PortfolioState;
import io.craigmiller160.stockmarket.gui.dialog.Dialog;

import java.util.logging.Level;
import java.util.logging.Logger;

import mvp.core.AbstractPropertyModel;
import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

/**
 * A <tt>JavaBean</tt> bound property model that defines the current
 * state of the GUI of the <tt>StockMarket</tt> program.
 * <p>
 * <b>THREAD SAFETY:</b> This class is completely thread safe. The only
 * mutable field is an int, which cannot be affected by a reference leak.
 * 
 * @author craig
 * @version 2.0
 */
@ThreadSafe
public class GUIStateModel extends AbstractPropertyModel {

	/**
	 * SerialVersionUID for serialization support.
	 */
	private static final long serialVersionUID = 6574795567140849827L;
	
	/**
	 * The components enabled in the GUI.
	 */
	@GuardedBy("this")
	private PortfolioState portfolioState;
	
	/**
	 * The configuration info for the dialog that should be displayed at this time.
	 */
	@GuardedBy("this")
	private Object[] dialogConfig;
	
	/**
	 * Shared logger for the program.
	 */
	private static final Logger LOGGER = Logger.getLogger("stockmarket.model.GUIStateModel");

	/**
	 * Creates a new instance of this bound property model. Sets
	 * the portfolio state to CLOSED by default.
	 */
	public GUIStateModel() {
		super();
		portfolioState = PortfolioState.CLOSED;
	}
	
	/**
	 * Sets the current state of the portfolio, which determines which
	 * components should be enabled/disabled, shown/hidden, etc.
	 * 
	 * @param portfolioState the state of the portfolio.
	 */
	public void setPortfolioState(PortfolioState portfolioState){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"setPortfolioState", "Entering method", 
				new Object[] {"Portfolio State: " + portfolioState});
		
		//No old value here, needs to be null so that even if the setting isn't actually
		//being changed, it still registers so, in certain cases, the stock list gets cleared
		synchronized(this){
			this.portfolioState = portfolioState;
		}
		
		firePropertyChange(PORTFOLIO_STATE_PROPERTY, null, portfolioState);
	}
	
	/**
	 * Returns the program state affecting which components are enabled/disabled.
	 * 
	 * @return the state of which components are enabled/disabled.
	 */
	public synchronized PortfolioState getPortfolioState(){
		return portfolioState;
	}
	
	/**
	 * Set which dialog should be currently displayed. Note that this value
	 * does not need to be changed when it is no longer being displayed.
	 * Configuration information for the dialog is passed along as part of
	 * the parameter array.
	 * 
	 * @param dialog the constant name for the dialog to display.
	 * @param dialogConfig optional configuration information for displaying
	 * the dialog.
	 */
	public void setDialogDisplayed(Dialog dialog, Object... dialogConfig){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"setDialogDisplayed", "Entering method", 
				new Object[] {"Dialog: " + dialog, dialogConfig.toString()});
		
		Object[] fullConfig = new Object[dialogConfig.length + 1];
		fullConfig[0] = dialog;
		for(int i = 0; i < dialogConfig.length; i++){
			fullConfig[i + 1] = dialogConfig[i];
		}
		
		Object[] oldValue = null;
		synchronized(this){
			oldValue = this.dialogConfig;
			this.dialogConfig = fullConfig;
		}
		
		firePropertyChange(DIALOG_DISPLAYED_PROPERTY, oldValue, fullConfig);
	}
	
	/**
	 * Get the configuration information for the dialog that was most recently 
	 * displayed. Because this value
	 * does not necessarily get cleared when the dialog is closed, the value
	 * returned can only be guaranteed to be the last one displayed.
	 * 
	 * @return the configuration information for the dialog that was most recently displayed.
	 */
	public synchronized Object[] getDialogDisplayed(){
		return dialogConfig;
	}

}
