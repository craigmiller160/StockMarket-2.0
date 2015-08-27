package stockmarket.gui.dialog;

import static stockmarket.controller.StockMarketController.BUY_STOCK_ACTION;

import java.awt.Frame;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

import stockmarket.stock.DefaultStock;
import stockmarket.stock.YahooStockDownloader;
import stockmarket.util.Fonts;
import stockmarket.util.Language;

public class BuyDialog extends TransactionDialog {

	//TODO delete this method after testing is done
	public static void main(String[] args) throws Exception{
		YahooStockDownloader downloader = new YahooStockDownloader();
		
		final DefaultStock stock = new DefaultStock("AAPL");
		stock.setStockDetails(downloader, false);
		
		SwingUtilities.invokeLater(new Runnable(){

			@Override
			public void run() {
				
				
				BigDecimal cashBalance = new BigDecimal(5000);
				BuyDialog dialog = new BuyDialog();
				dialog.setStock(stock);
				dialog.setCashBalance(cashBalance);
				
				dialog.showDialog();
			}
			
		});
	}
	
	private static final Language LANGUAGE = Language.getInstance();
	
	private static final Logger LOGGER = Logger.getLogger("stockmarket.gui.dialog.BuyDialog");

	private NumberFormat moneyFormat = new DecimalFormat("$###,###,###,##0.00");
	
	private BigDecimal cashBalance;
	
	public BuyDialog() {
		super();
		init();
	}

	public BuyDialog(Frame owner) {
		super(owner);
		init();
	}

	public BuyDialog(Frame owner, boolean modal) {
		super(owner, modal);
		init();
	}
	
	private void init(){
		cashBalance = new BigDecimal(50);
	}
	
	public void setCashBalance(BigDecimal cashBalance){
		this.cashBalance = cashBalance;
		setShareLimit(cashBalance.divide(
				getCurrentPrice(), BigDecimal.ROUND_DOWN).intValue());
	}
	
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
	public Object getValueForAction(String valueToGet) {
		Object result = null;
		if(valueToGet == BUY_STOCK_ACTION){
			LOGGER.logp(Level.FINEST, this.getClass().getName(), "getValue", 
					"Entering method", new Object[] {"Command: " + valueToGet});
			
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

}
