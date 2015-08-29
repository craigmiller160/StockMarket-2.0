package stockmarket.gui.dialog;

/**
 * Constant values for which dialog is currently being displayed.
 * Meant to be passed from the controller to the view to trigger
 * that dialog's creation.
 * 
 * @author craig
 * @version 2.0
 */
public enum Dialog {

	/**
	 * The dialog for setting the name of the portfolio.
	 */
	PORTFOLIO_NAME_DIALOG,
	
	/**
	 * The dialog for opening a saved portfolio.
	 */
	OPEN_PORTFOLIO_DIALOG,
	
	/**
	 * The dialog for buying shares of a stock.
	 */
	BUY_STOCK_DIALOG,
	
	/**
	 * The dialog for selling shares of a stock.
	 */
	SELL_STOCK_DIALOG,
	
	/**
	 * The About Program dialog.
	 */
	ABOUT_DIALOG,
	
	/**
	 * The dialog for changing the language setting.
	 */
	LANGUAGE_DIALOG,
	
	/**
	 * The dialog for displaying exceptions.
	 */
	EXCEPTION_DIALOG;
	
}
