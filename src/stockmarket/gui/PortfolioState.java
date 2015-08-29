package stockmarket.gui;

/**
 * Constant values for the state of the portfolio during 
 * the lifecycle of this program. The state of the portfolio
 * determines which GUI components are enabled/disabled, 
 * shown/hidden, etc, at any point in the program. This state
 * changes regularly as the program progresses.
 * 
 * @author craig
 * @version 2.0
 */
public enum PortfolioState {

	/**
	 * No portfolio is currently loaded in the
	 * program. Most UI components should be disabled
	 * in this state.
	 */
	CLOSED,
	
	/**
	 * A portfolio is open, but no stock has been
	 * selected and loaded in the main panel. Most UI components
	 * should be enabled in this state, except for 
	 * anything that displays or interacts with  a
	 * selected stock.
	 */
	OPEN_NO_STOCK,
	
	/**
	 * A portfolio is open and a stock has been
	 * selected and loaded in the main panel. The
	 * selected stock is NOT one that is owned
	 * in the portfolio. The only items not enabled
	 * are for owned stock related functions. 
	 */
	OPEN_STOCK,
	
	/**
	 * A portfolio is opened and a stock has been
	 * selected and loaded in the main panel. The
	 * selected stock IS owned in the portfolio.
	 * All UI elements are now enabled.
	 */
	OPEN_OWNED_STOCK;
	
}
