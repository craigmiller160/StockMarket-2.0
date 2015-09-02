package io.craigmiller160.stockmarket.gui;

import static io.craigmiller160.stockmarket.controller.StockMarketController.BUY_STOCK_DIALOG_ACTION;
import static io.craigmiller160.stockmarket.controller.StockMarketController.PORTFOLIO_STATE_PROPERTY;
import static io.craigmiller160.stockmarket.controller.StockMarketController.SELL_STOCK_DIALOG_ACTION;
import io.craigmiller160.mvp.listener.AbstractListenerView;
import io.craigmiller160.stockmarket.util.Fonts;
import io.craigmiller160.stockmarket.util.Language;

import java.beans.PropertyChangeEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import net.jcip.annotations.NotThreadSafe;
import net.miginfocom.swing.MigLayout;

/**
 * A GUI panel with the controls for buying and selling stocks on it.
 * The panel also contains a summary of the portfolio's stats.
 * <p>
 * Like all GUI classes in this program, this class separates its 
 * instantiation code from its assembly code for maximum flexibility.
 * <p>
 * <b>THREAD SAFETY:</b> Swing is NOT thread safe.
 * 
 * @author craig
 * @version 2.0
 */
@NotThreadSafe
public class BuySellPanel extends AbstractListenerView {

	/**
	 * Shared <tt>Language</tt> module for locale-specific text.
	 */
	private static final Language LANGUAGE = Language.getInstance();
	
	/**
	 * Shared logger for the program.
	 */
	private static final Logger LOGGER = Logger.getLogger("stockmarket.gui.BuySellPanel");
	
	/**
	 * The panel created by this class.
	 */
	private final JPanel buySellPanel;
	
	/**
	 * The button to buy stocks.
	 */
	private JButton buyButton;
	
	/**
	 * The button to sell stocks.
	 */
	private JButton sellButton;
	
	/**
	 * Create the panel.
	 */
	public BuySellPanel() {
		super();
		buySellPanel = createBuySellPanel();
		initComponents();
		assembleBuySellPanel();
	}
	
	/**
	 * Create the <tt>JPanel</tt> that this class wraps around.
	 * 
	 * @return the created <tt>JPanel</tt>.
	 */
	private JPanel createBuySellPanel(){
		JPanel buySellPanel = new JPanel();
		buySellPanel.setLayout(new MigLayout());
		
		return buySellPanel;
	}
	
	/**
	 * Initialize the components for this GUI class.
	 */
	private void initComponents(){
		
		buyButton = createButton(LANGUAGE.getString("buy_button_label"), "buy",
				LANGUAGE.getString("buy_button_tooltip"), BUY_STOCK_DIALOG_ACTION);
		buyButton.setEnabled(false);
		
		sellButton = createButton(LANGUAGE.getString("sell_button_label"), "sell",
				LANGUAGE.getString("sell_button_tooltip"), SELL_STOCK_DIALOG_ACTION);
		sellButton.setEnabled(false);
	}
	
	/**
	 * Assemble the components in this panel.
	 */
	private void assembleBuySellPanel(){
		buySellPanel.add(buyButton, "align left, pushx");
		buySellPanel.add(sellButton, "align right, pushx");
	}
	
	/**
	 * Utility method for creating a <tt>JButton</tt> based on the specified
	 * parameters.
	 * 
	 * @param text the button's text.
	 * @param icon the button's icon.
	 * @param toolTipText the button's tooltip.
	 * @param actionCommand the button's action command.
	 * @return the created button.
	 */
	private JButton createButton(String text, String icon, String toolTipText, 
			String actionCommand){
		JButton button = new JButton(text, new ImageIcon(
				this.getClass().getClassLoader().getResource("32p/" + icon + ".png")));
		button.setToolTipText(toolTipText);
		button.setFont(Fonts.BIG_LABEL_FONT);
		button.setVerticalTextPosition(JButton.BOTTOM);
		button.setHorizontalTextPosition(JButton.CENTER);
		button.setActionCommand(actionCommand);
		button.addActionListener(this);
		
		return button;
	}
	
	/**
	 * Return the panel created by this class.
	 * 
	 * @return the panel created by this class.
	 */
	public JPanel getPanel(){
		return buySellPanel;
	}

	/**
	 * {@inheritDoc}
	 * @throws IllegalArgumentException if the new value from the event is not
	 * the expected type to perform the operation.
	 */
	@Override
	public void changeProperty(PropertyChangeEvent event) {
		if(event.getPropertyName() == PORTFOLIO_STATE_PROPERTY){
			LOGGER.logp(Level.FINEST, this.getClass().getName(), 
					"changeProperty", "Changing Property", 
					new Object[]{"Property: " + event.getPropertyName()});
			
			if(event.getNewValue() instanceof PortfolioState){
				portfolioStateChanged((PortfolioState) event.getNewValue());
			}
			else{
				throw new IllegalArgumentException(
						"Not instance of PortfolioState: " + event.getNewValue());
			}
		}
	}
	
	/**
	 * Respond to a change in the portfolio state by changing which
	 * components are enabled/disabled, shown/hidden, etc.
	 * 
	 * @param portfolioState the state of the portfolio.
	 */
	public void portfolioStateChanged(PortfolioState portfolioState){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"portfolioStateChanged", "Entering method", 
				new Object[] {"Portfolio State: " + portfolioState});
		
		if(portfolioState == PortfolioState.CLOSED){
			buyButton.setEnabled(false);
			sellButton.setEnabled(false);
		}
		else if(portfolioState == PortfolioState.OPEN_NO_STOCK){
			buyButton.setEnabled(false);
			sellButton.setEnabled(false);
		}
		else if(portfolioState == PortfolioState.OPEN_STOCK){
			buyButton.setEnabled(true);
			sellButton.setEnabled(false);
		}
		else if(portfolioState == PortfolioState.OPEN_OWNED_STOCK){
			buyButton.setEnabled(true);
			sellButton.setEnabled(true);
		}
	}

	@Override
	public Object getValueForAction(String actionCommand) {
		//LOGGER.logp(Level.FINEST, this.getClass().getName(), "getValue", 
				//"Entering method", new Object[] {"Command: " + valueToGet});
		
		// TODO Will be filled out if any values are ultimately needed
		//from this class.
		return null;
	}

}
