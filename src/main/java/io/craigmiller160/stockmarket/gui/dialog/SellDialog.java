package io.craigmiller160.stockmarket.gui.dialog;

import static io.craigmiller160.stockmarket.controller.StockMarketController.SELL_STOCK_ACTION;
import io.craigmiller160.stockmarket.stock.OwnedStock;
import io.craigmiller160.stockmarket.stock.Stock;
import io.craigmiller160.stockmarket.util.Fonts;
import io.craigmiller160.stockmarket.util.Language;

import java.awt.Frame;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import net.jcip.annotations.NotThreadSafe;

/**
 * A dialog for performing a sell shares transaction. The maximum limit
 * on the quantity of shares is the number of shares owned of the stock.
 * The amount of shares selected by this dialog will be "sold", and the
 * profits will be added to the cash balance available to buy more stocks.
 * <p>
 * <b>THREAD SAFETY:</b> Swing is NOT thread safe.
 * 
 * @author craig
 * @version 2.0
 */
@NotThreadSafe
public class SellDialog extends TransactionDialog {

	/**
	 * Shared <tt>Language</tt> module for locale-specific text.
	 */
	private static final Language LANGUAGE = Language.getInstance();
	
	/**
	 * The logger for the program.
	 */
	private static final Logger LOGGER = Logger.getLogger("stockmarket.gui.dialog.SellDialog");
	
	/**
	 * The maximum limit on how many shares can be sold.
	 */
	private int shareLimit;
	
	/**
	 * Create a new, non-modal dialog with no owner.
	 */
	public SellDialog() {
		super();
	}

	/**
	 * Create a new, non-modal dialog with the specified owner.
	 * 
	 * @param owner the owner of the dialog.
	 */
	public SellDialog(Frame owner) {
		super(owner);
	}

	/**
	 * Create a new dialog with the specified owner and modality.
	 * 
	 * @param owner the owner of the dialog.
	 * @param modal the modality of the dialog.
	 */
	public SellDialog(Frame owner, boolean modal) {
		super(owner, modal);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * This class will only accept an <tt>OwnedStock</tt> as a parameter,
	 * because only a stock that is owned and has a quantity value can 
	 * have shares be sold. If the parameter is not an <tt>OwnedStock</tt>,
	 * an exception is thrown.
	 * @throws IllegalArgumentException if the stock parameter is not 
	 * an instance of <tt>OwnedStock</tt>.
	 */
	@Override
	public void setStock(Stock stock){
		if(!(stock instanceof OwnedStock)){
			throw new IllegalArgumentException(
					"SellDialog requires the stock to be an OwnedStock");
		}
		else{
			OwnedStock ownedStock = (OwnedStock) stock;
			shareLimit = ownedStock.getQuantityOfShares();
			super.setStock(stock);
		}
	}

	@Override
	protected ImageIcon createIcon() {
		return new ImageIcon(
				this.getClass().getClassLoader().getResource(
						"96p/sell.png"));
	}

	@Override
	protected String limitLabelText() {
		return LANGUAGE.getString("shares_owned_label");
	}

	@Override
	protected String limitValueText() {
		String limitText = "0";
		if(!(stock instanceof OwnedStock)){
			throw new IllegalArgumentException(
					"SellDialog requires the stock to be an OwnedStock");
		}
		else{
			OwnedStock ownedStock = (OwnedStock) stock;
			limitText = "" + ownedStock.getQuantityOfShares();
		}
		
		return limitText;
	}

	@Override
	public Object getValueForAction(String actionCommand) {
		Object result = null;
		if(actionCommand == SELL_STOCK_ACTION){
			LOGGER.logp(Level.FINEST, this.getClass().getName(), "getValue", 
					"Entering method", new Object[] {"Command: " + actionCommand});
			
			result = getQuantity();
		}
		
		return result;
	}

	@Override
	protected JButton createTransactionButton() {
		JButton button = new JButton(LANGUAGE.getString("sell_label"));
		button.setFont(Fonts.SMALL_LABEL_FONT);
		button.setActionCommand(SELL_STOCK_ACTION);
		button.setToolTipText(LANGUAGE.getString("sell_button_tooltip"));
		
		return button;
	}

	@Override
	protected String createTitleBarText() {
		return LANGUAGE.getString("sell_button_text");
	}

	@Override
	protected int shareLimit() {
		return shareLimit;
	}

}
