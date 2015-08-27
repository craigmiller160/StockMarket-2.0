package stockmarket.gui.dialog;

import static stockmarket.controller.StockMarketController.SELL_STOCK_ACTION;

import java.awt.Frame;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

import stockmarket.stock.OwnedStock;
import stockmarket.stock.Stock;
import stockmarket.util.Fonts;
import stockmarket.util.Language;

public class SellDialog extends TransactionDialog {

	//TODO delete this method after testing is done
	public static void main(String[] args){
		SwingUtilities.invokeLater(new Runnable(){

			@Override
			public void run() {
				
			}
			
		});
	}
	
	private static final Language LANGUAGE = Language.getInstance();
	
	private static final Logger LOGGER = Logger.getLogger("stockmarket.gui.dialog.SellDialog");
	
	public SellDialog(OwnedStock stock) {
		super();
	}

	public SellDialog(Frame owner, OwnedStock stock) {
		super(owner);
	}

	public SellDialog(Frame owner, boolean modal, OwnedStock stock) {
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
			setShareLimit(ownedStock.getQuantityOfShares());
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
	public Object getValueForAction(String valueToGet) {
		Object result = null;
		if(valueToGet == SELL_STOCK_ACTION){
			LOGGER.logp(Level.FINEST, this.getClass().getName(), "getValue", 
					"Entering method", new Object[] {"Command: " + valueToGet});
			
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

}
