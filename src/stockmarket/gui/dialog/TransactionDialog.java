package stockmarket.gui.dialog;

import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

import net.miginfocom.swing.MigLayout;
import stockmarket.stock.Stock;
import stockmarket.util.Fonts;
import stockmarket.util.Language;

public abstract class TransactionDialog extends AbstractDefaultDialog {

	private static final Language LANGUAGE = Language.getInstance();
	
	private static final String CANCEL_ACTION = "Cancel";
	
	private JLabel dialogTitleLabel;
	private JLabel limitLabel;
	private JLabel limitValue;
	private JLabel sharePriceLabel;
	private JLabel sharePriceValue;
	
	private JLabel sliderLabel;
	private JSlider quantitySlider;
	
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

	private void init() {
		dialogTitleLabel = createLabel(stock.getSymbol(), Fonts.LABEL_FONT);
		
		limitLabel = createLabel(limitLabelText() + ": ", Fonts.SMALL_LABEL_FONT);
		
		limitValue = createLabel(limitValueText(), Fonts.SMALL_FIELD_FONT);
		
		sharePriceLabel = createLabel(LANGUAGE.getString("current_price_label") + ": ", 
				Fonts.SMALL_LABEL_FONT);
		
		sharePriceValue = createLabel(moneyFormat.format(stock.getCurrentPrice()), 
				Fonts.SMALL_FIELD_FONT);
		
		sliderLabel = createLabel(LANGUAGE.getString("slider_label") + " " + transactionType(), 
				Fonts.SMALL_FIELD_FONT);
		
		quantityFieldLabel = createLabel(LANGUAGE.getString("share_quantity_label") + ": ", 
				Fonts.SMALL_LABEL_FONT);
		
		valueFieldLabel = createLabel(LANGUAGE.getString("value_label") + ": ", 
				Fonts.SMALL_LABEL_FONT);
		
		valueField = createTextField("", 5, Fonts.SMALL_FIELD_FONT, 
				LANGUAGE.getString("transaction_value_tooltip"));
		//valueField.setEditable(false);
		valueField.setFocusable(false);
		
		quantityField = createTextField("", 5, Fonts.SMALL_FIELD_FONT, 
				LANGUAGE.getString("transaction_quantity_tooltip"));
		PlainDocument quantityDocument = (PlainDocument) quantityField.getDocument();
		quantityDocument.setDocumentFilter(new QuantityFieldFilter());
		quantityDocument.addDocumentListener(new QuantityFieldListener());
		
		quantitySlider = createSlider(maxShareLimit(), 
				LANGUAGE.getString("transaction_quantity_tooltip"));
		
		cancelButton = createButton(LANGUAGE.getString("cancel_button_label"),
				LANGUAGE.getString("cancel_button_tooltip"), 
				CANCEL_ACTION, Fonts.SMALL_LABEL_FONT);
		
		//TODO link slider and quantity field and value field together, so that a change
		//in either of the first two affects all three.
		
		configureInputActionMaps();		
	}
	
	private void configureInputActionMaps(){
		//TODO configure the input & action maps for keyboard shortcuts
	}
	
	private JButton createButton(String text, String toolTipText, 
			String actionCommand, Font font){
		DialogAction action = new DialogAction();
		action.setText(text);
		action.setToolTipText(toolTipText);
		action.setActionCommand(actionCommand);
		
		return createButton(action, font);
	}
	
	private JButton createButton(Action action, Font font){
		JButton button = new JButton(action);
		button.setFont(font);
		
		return button;
	}
	
	private JSlider createSlider(int max, String toolTipText){
		JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, max, 1);
		slider.setMajorTickSpacing(max / 5);
		slider.setMinorTickSpacing(max / 10); //TODO might need to change this and the one above when testing
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setSnapToTicks(true);
		slider.addChangeListener(new QuantitySliderListener());
		
		return slider;
	}
	
	private JTextField createTextField(String text, int length, Font font, String toolTipText){
		JTextField field = new JTextField(text, length);
		field.setFont(font);
		
		return field;
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
		JPanel detailsPanel = new JPanel();
		detailsPanel.setLayout(new MigLayout());
		
		detailsPanel.add(limitLabel, "");
		detailsPanel.add(limitValue, "wrap");
		
		detailsPanel.add(sharePriceLabel, "");
		detailsPanel.add(sharePriceValue, "wrap");
		
		detailsPanel.add(sliderLabel, "span 2, growx, pushx, wrap");
		detailsPanel.add(quantitySlider, "span 2, growx, pushx, wrap");
		
		detailsPanel.add(quantityFieldLabel, "split 2");
		detailsPanel.add(quantityField, "");
		detailsPanel.add(valueFieldLabel, "split 2");
		detailsPanel.add(valueField, "");
		
		return detailsPanel;
	}

	@Override
	protected JButton[] addButtons() {
		transactionButton = transactionButton();
		return new JButton[] {transactionButton, cancelButton};
	}
	
	@Override
	public void showDialog(){
		init();
		super.showDialog();
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
	
	/**
	 * This method should return the button to execute the
	 * transaction. This is the equivalent of "ok" or "execute",
	 * but labeled based on the type of transaction.
	 * 
	 * @return the button to execute the transaction.
	 */
	protected abstract JButton transactionButton();
	
	private class QuantitySliderListener implements ChangeListener{

		@Override
		public void stateChanged(ChangeEvent event) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private class QuantityFieldListener implements DocumentListener{

		@Override
		public void changedUpdate(DocumentEvent event) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void insertUpdate(DocumentEvent event) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void removeUpdate(DocumentEvent event) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	/**
	 * A <tt>DocumentFilter</tt> for the quantity field. It checks any
	 * input to ensure 1) Only numerical input is accepted and 2) The
	 * number of shares inputed stays below the maximum limit.
	 * 
	 * @author craig
	 * @version 2.0
	 */
	private class QuantityFieldFilter extends DocumentFilter{
		
		@Override
		public void insertString(DocumentFilter.FilterBypass fb, int offset, 
				String text, AttributeSet attr) throws BadLocationException{
			if(bypass(fb, text)){
				super.insertString(fb, offset, text, attr);
			}
		}
		
		@Override
		public void replace(DocumentFilter.FilterBypass fb, int offset, int length, 
				String text, AttributeSet attr) throws BadLocationException{
			if(bypass(fb, text)){
				super.replace(fb,  offset, length, text, attr);
			}
		}
		
		/**
		 * Checks the text to determine if it should bypass the filter
		 * or not.
		 * 
		 * @param fb the <tt>FilterBypass</tt> object.
		 * @param text the text to check.
		 * @return true if the text should bypass the filter.
		 * @throws BadLocationException if text retrieved from the <tt>Document</tt>
		 * doesn't exist at the specified location.
		 */
		private boolean bypass(DocumentFilter.FilterBypass fb, String text) 
				throws BadLocationException{
			boolean result;
			Document doc = fb.getDocument();
			String fullText = doc.getText(0, doc.getLength() - 1) + text;
			System.out.println("Text: " + text + " Full Text: " + fullText);
			
			if(! hasOnlyNumbers(text)){
				//First test if the string only has numbers, and if it doesn't it
				//doesn't get added
				
				//TODO work on some sort of visual response as well
				result = false;
				Toolkit.getDefaultToolkit().beep();
			}
			else{
				//If the string is just numbers, combine it with the existing text
				//and test to see if the full value is greater than the max
				int quantity = Integer.parseInt(fullText);
				
				//If it is greater than the max, the new text doesn't get added
				if(quantity > maxShareLimit()){
					//TODO work on some sort of visual response as well
					result = false;
					Toolkit.getDefaultToolkit().beep();
				}
				else{
					//If it passes both tests, it gets added
					result = true;
				}
			}
			
			return result;
		}
		
		/**
		 * Tests a <tt>String</tt> to see if it contains only valid
		 * number values. This is accomplished by deliberately running
		 * an unsafe <tt>Integer.parseInt(String)</tt>
		 * operation in a try-catch block. If no exception occurs, this
		 * method returns true that the <tt>String</tt> contains only
		 * numbers. If an exception occurs, it is caught and squashed,
		 * and this method returns false.
		 * 
		 * @param string the <tt>String</tt> to be tested.
		 * @return true if the <tt>String</tt> contains only numbers.
		 */
		private boolean hasOnlyNumbers(String string){
			try{
				Integer.parseInt(string);
				return true;
			}
			catch(NumberFormatException ex){
				return false;
			}
		}
		
	}
	
	/**
	 * Private implementation of the <tt>Action</tt> interface for this dialog.
	 * Allows for actions to be shared between components and keyboard shortcuts.
	 * 
	 * @author craig
	 * @version 2.0
	 */
	private class DialogAction extends AbstractAction{

		/**
		 * SerialVersionUID for serialization support.
		 */
		private static final long serialVersionUID = -3151599049989465129L;
		
		/**
		 * Create a new action.
		 */
		public DialogAction(){
			super();
		}
		
		/**
		 * Set the text for the action's visual component.
		 * 
		 * @param text the text for the action.
		 */
		public void setText(String text){
			putValue(AbstractAction.NAME, text);
		}
		
		/**
		 * Set the tooltip text for the action's visual component.
		 * 
		 * @param text the tool tip text for the action.
		 */
		public void setToolTipText(String text){
			putValue(AbstractAction.SHORT_DESCRIPTION, text);
		}
		
		/**
		 * Set the action command for the action.
		 * 
		 * @param command the action command for the action.
		 */
		public void setActionCommand(String command){
			putValue(AbstractAction.ACTION_COMMAND_KEY, command);
		}
		
		/**
		 * Set the icon for the action's visual component.
		 * 
		 * @param icon the icon for the action.
		 */
		public void setIcon(ImageIcon icon){
			putValue(AbstractAction.SMALL_ICON, icon);
		}
		
		@Override
		public void actionPerformed(ActionEvent event) {
			
		}
		
	}

}
