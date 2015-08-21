package stockmarket.gui.dialog;

import java.awt.Font;
import java.awt.Frame;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;
import stockmarket.stock.Stock;
import stockmarket.util.Fonts;
import stockmarket.util.Language;

public abstract class TransactionDialog extends AbstractDefaultDialog {

	//TODO delete this method after testing is done
	public static void main(String[] args){
		SwingUtilities.invokeLater(new Runnable(){

			@Override
			public void run() {
				
			}
			
		});
	}
	
	private static final Language LANGUAGE = Language.getInstance();
	
	private JLabel dialogTitleLabel;
	private JLabel limitLabel;
	private JLabel limitValue;
	private JLabel sharePriceLabel;
	private JLabel sharePriceValue;
	
	private JLabel sliderLabel;
	private JSlider slider;
	
	private JTextField quantityField;
	private JTextField valueField;
	private JLabel quantityFieldLabel;
	private JLabel valueFieldLabel;
	
	private JButton transactionButton;
	private JButton cancelButton;
	
	private NumberFormat moneyFormat = new DecimalFormat("$###,###,###,##0.00");
	
	protected Stock stock;
	
	public TransactionDialog(Stock stock) {
		super();
		this.stock = stock;
	}

	public TransactionDialog(Frame owner, Stock stock) {
		super(owner);
		this.stock = stock;
	}

	public TransactionDialog(Frame owner, boolean modal, Stock stock) {
		super(owner, modal);
		this.stock = stock;
	}

	@Override
	protected void init() {
		dialogTitleLabel = createLabel(stock.getSymbol(), Fonts.LABEL_FONT);
		
		limitLabel = createLabel(limitLabelText(), Fonts.SMALL_LABEL_FONT);
		
		limitValue = createLabel(limitValueText(), Fonts.SMALL_FIELD_FONT);
		
		sharePriceLabel = createLabel(LANGUAGE.getString("current_price_label"), 
				Fonts.SMALL_LABEL_FONT);
		
		sharePriceValue = createLabel(moneyFormat.format(stock.getCurrentPrice()), 
				Fonts.SMALL_FIELD_FONT);
		
		sliderLabel = createLabel(LANGUAGE.getString("slider_label") + " " + transactionType(), 
				Fonts.SMALL_FIELD_FONT);
		
		quantityFieldLabel = createLabel(LANGUAGE.getString("share_quantity_label") + ": ", 
				Fonts.SMALL_LABEL_FONT);
		
		//TODO leaving off here, need to establish cost/profit for valueFieldLabel
		//Then do quantity & value fields, and the buttons & the slider
		
	}
	
	private JLabel createLabel(String text, Font font){
		JLabel label = new JLabel(text);
		label.setFont(font);
		
		return label;
	}

	@Override
	protected String createTitleBarText() {
		return transactionType() + " " + LANGUAGE.getString("shares_label");
	}

	@Override
	protected JPanel createTitlePanel() {
		JPanel titlePanel = new JPanel();
		titlePanel.setLayout(new MigLayout());
		
		titlePanel.add(dialogTitleLabel, "center, pushx");
		
		return titlePanel;
	}

	@Override
	protected JPanel createDetailsPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected JButton[] addButtons() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * This method should be return the type of 
	 * transaction this dialog is performing. "Buy" and "Sell"
	 * will be the most common implementations.
	 * 
	 * @return the type of transaction for this dialog.
	 */
	protected abstract String transactionType();
	
	/**
	 * This method should return the text for the label defining
	 * the maximum limit on the number of shares that can be a part
	 * of this transaction. This returns only a label naming what
	 * that limit is, not the value of that limit.
	 * 
	 * @return the text for the label naming the limit on this transaction
	 */
	protected abstract String limitLabelText();
	
	/**
	 * This method should return text representing the value
	 * of the maximum limit on the number of shares that can
	 * be a part of this transaction. This value does not
	 * have to be an actual number of shares, it can be any
	 * value that has the effect of restricting the number
	 * of shares.
	 * 
	 * @return the text of the value of the limit on this transaction.
	 */
	protected abstract String limitValueText();
	
	/**
	 * This method should return the maximum limit on
	 * how many shares can be involved in this transaction.
	 * Unlike <tt>limitValueText()</tt>, this
	 * method returns an <tt>int</tt> to be used in 
	 * calculations to ensure that this transaction doesn't
	 * go over that limit.
	 * 
	 * @return the limit on how many shares can be involved in this transaction.
	 */
	protected abstract int maxShareLimit();

}
