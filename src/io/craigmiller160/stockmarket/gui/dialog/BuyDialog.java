package io.craigmiller160.stockmarket.gui.dialog;

import static io.craigmiller160.stockmarket.controller.StockMarketController.BUY_STOCK_ACTION;
import io.craigmiller160.stockmarket.util.Fonts;
import io.craigmiller160.stockmarket.util.Language;

import java.awt.Frame;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import net.jcip.annotations.NotThreadSafe;

/**
 * A dialog for the buy stock transaction. The maximum limit on shares
 * that can be purchased is the cash balance available to be spent on
 * stocks. The amount of shares selected by this dialog will be 
 * "bought" and added to the user's portfolio.
 * <p>
 * <b>THREAD SAFETY:</b> Swing is NOT thread safe.
 * 
 * @author craig
 * @version 2.0
 */
@NotThreadSafe
public class BuyDialog extends TransactionDialog {

	/**
	 * Shares langauge module for locale-specific text.
	 */
	private static final Language LANGUAGE = Language.getInstance();
	
	/**
	 * The logger for this program.
	 */
	private static final Logger LOGGER = Logger.getLogger("stockmarket.gui.dialog.BuyDialog");

	/**
	 * Format for displaying money values.
	 */
	private NumberFormat moneyFormat = new DecimalFormat("$###,###,###,##0.00");
	
	/**
	 * The cash balance available to spend on shares of this stock.
	 */
	private BigDecimal cashBalance;
	
	/**
	 * The maximum limit on how many shares can be bought.
	 */
	private int shareLimit;
	
	/**
	 * Create a new non-modal buy dialog, with no owner.
	 */
	public BuyDialog() {
		super();
		init();
	}

	/**
	 * Create a new non-modal buy dialog with the specified container
	 * as its owner.
	 * 
	 * @param owner the owner of the dialog.
	 */
	public BuyDialog(Frame owner) {
		super(owner);
		init();
	}

	/**
	 * Create a new buy dialog with the specified container as
	 * its owner, and its modality specified.
	 * 
	 * @param owner the owner of the dialog.
	 * @param modal the modality of the dialog.
	 */
	public BuyDialog(Frame owner, boolean modal) {
		super(owner, modal);
		init();
	}
	
	/**
	 * Initialize values for this dialog.
	 */
	private void init(){
		cashBalance = new BigDecimal(50);
	}
	
	/**
	 * Set the cash balance available to spend on stocks.
	 * 
	 * @param cashBalance the cash balance.
	 */
	public void setCashBalance(BigDecimal cashBalance){
		this.cashBalance = cashBalance;
		shareLimit = cashBalance.divide(
				getCurrentPrice(), BigDecimal.ROUND_DOWN).intValue();
	}
	
	/**
	 * Get the cash balance available to spend on stocks.
	 * 
	 * @return the cash balance.
	 */
	public BigDecimal getCashBalance(){
		return cashBalance;
	}

	@Override
	protected ImageIcon createIcon() {
		return new ImageIcon(
				this.getClass().getClassLoader().getResource(
						"96p/buy.png"));
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>This implementation</b> defines the limit
	 * as the amount of cash available to purchase
	 * stocks.
	 */
	@Override
	protected String limitLabelText() {
		return LANGUAGE.getString("cash_balance_label");
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>This implementation</b> defines the limit 
	 * as the amount of cash available to purchase
	 * stocks.
	 */
	@Override
	protected String limitValueText() {
		return moneyFormat.format(cashBalance);
	}

	@Override
	public Object getValueForAction(String actionCommand) {
		Object result = null;
		if(actionCommand == BUY_STOCK_ACTION){
			LOGGER.logp(Level.FINEST, this.getClass().getName(), "getValue", 
					"Entering method", new Object[] {"Command: " + actionCommand});
			
			result = getQuantity();
		}
		
		return result;
	}

	@Override
	protected JButton createTransactionButton() {
		JButton button = new JButton(LANGUAGE.getString("buy_label"));
		button.setFont(Fonts.SMALL_LABEL_FONT);
		button.setActionCommand(BUY_STOCK_ACTION);
		button.setToolTipText(LANGUAGE.getString("buy_button_tooltip"));
		
		return button;
	}

	@Override
	protected String createTitleBarText() {
		return LANGUAGE.getString("buy_button_label");
	}

	@Override
	protected int shareLimit() {
		return shareLimit;
	}

}
