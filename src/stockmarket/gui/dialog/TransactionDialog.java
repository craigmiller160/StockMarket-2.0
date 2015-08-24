package stockmarket.gui.dialog;

import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
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

//TODO document how subclasses must use setShareLimit(int) method to ensure 
//that a limit is properly set.

//TODO also test what happens when that limit isn't set... just in case...

public abstract class TransactionDialog extends AbstractDefaultDialog {

	private static final Language LANGUAGE = Language.getInstance();
	
	private static final String DISMISS_ONLY_ACTION = "Dismiss";
	
	//TODO should this be here, or in the controller?
	private static final String TRANSACTION_ACTION = "TransactionAction";
	
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
	
	private int shareLimit;
	
	private int quantity;
	
	private NumberFormat moneyFormat = new DecimalFormat("$###,###,###,##0.00");
	
	private static final Logger LOGGER = Logger.getLogger("stockmarket.gui.dialog.TransactionDialog");
	
	//TODO document how this field is available for subclass access
	protected Stock stock;
	
	private DialogAction transactionAction;
	
	public TransactionDialog() {
		super();
		init();
	}

	public TransactionDialog(Frame owner) {
		super(owner);
		init();
	}

	public TransactionDialog(Frame owner, boolean modal) {
		super(owner, modal);
		init();
	}

	private void init() {
		transactionAction = new DialogAction();
		transactionAction.setActionCommand(TRANSACTION_ACTION);
		
		shareLimit = 0;
		
		dialogTitleLabel = createLabel("[TEMP TITLE]", Fonts.LABEL_FONT);
		
		limitLabel = createLabel("[TEMP LIMIT LABEL]" + ": ", Fonts.SMALL_LABEL_FONT);
		
		limitValue = createLabel("[TEMP LIMIT VALUE]", Fonts.SMALL_FIELD_FONT);
		
		sharePriceLabel = createLabel(LANGUAGE.getString("current_price_label") + ": ", 
				Fonts.SMALL_LABEL_FONT);
		
		sharePriceValue = createLabel("$0.00", 
				Fonts.SMALL_FIELD_FONT);
		
		sliderLabel = createLabel(LANGUAGE.getString("slider_label"), 
				Fonts.SMALL_LABEL_FONT);
		
		quantityFieldLabel = createLabel(LANGUAGE.getString("share_quantity_label") + ": ", 
				Fonts.SMALL_LABEL_FONT);
		
		valueFieldLabel = createLabel(LANGUAGE.getString("value_label") + ": ", 
				Fonts.SMALL_LABEL_FONT);
		
		valueField = createTextField("$0.00", 10, Fonts.SMALL_FIELD_FONT, 
				LANGUAGE.getString("transaction_value_tooltip"));
		valueField.setFocusable(false);
		
		quantityField = createTextField("0", 5, Fonts.SMALL_FIELD_FONT, 
				LANGUAGE.getString("transaction_quantity_tooltip"));
		
		PlainDocument quantityDocument = (PlainDocument) quantityField.getDocument();
		quantityDocument.setDocumentFilter(new QuantityFieldFilter());
		quantityDocument.addDocumentListener(new QuantityFieldListener());
		
		quantitySlider = createSlider(10, 
				LANGUAGE.getString("transaction_quantity_tooltip"));
		
		cancelButton = createButton(LANGUAGE.getString("cancel_button_label"),
				LANGUAGE.getString("cancel_button_tooltip"), 
				DISMISS_ONLY_ACTION, Fonts.SMALL_LABEL_FONT);
		
		configureInputActionMaps();		
	}
	
	private void configureInputActionMaps(){
		DialogAction dismissAction = new DialogAction();
		dismissAction.setActionCommand(DISMISS_ONLY_ACTION);
		
		JRootPane root = dialog.getRootPane();
		root.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), 
				DISMISS_ONLY_ACTION);
		root.getActionMap().put(DISMISS_ONLY_ACTION, dismissAction);
		
		quantitySlider.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), 
				DISMISS_ONLY_ACTION);
		quantitySlider.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), 
				TRANSACTION_ACTION);
		quantitySlider.getActionMap().put(DISMISS_ONLY_ACTION, dismissAction);
		quantitySlider.getActionMap().put(TRANSACTION_ACTION, transactionAction);
		
		quantityField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), 
				DISMISS_ONLY_ACTION);
		quantityField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), 
				TRANSACTION_ACTION);
		quantityField.getActionMap().put(DISMISS_ONLY_ACTION, dismissAction);
		quantityField.getActionMap().put(TRANSACTION_ACTION, transactionAction);
		
		transactionButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), 
				DISMISS_ONLY_ACTION);
		transactionButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), 
				TRANSACTION_ACTION);
		transactionButton.getActionMap().put(DISMISS_ONLY_ACTION, dismissAction);
		transactionButton.getActionMap().put(TRANSACTION_ACTION, transactionAction);
		
		cancelButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), 
				DISMISS_ONLY_ACTION);
		cancelButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), 
				DISMISS_ONLY_ACTION);
		cancelButton.getActionMap().put(DISMISS_ONLY_ACTION, dismissAction);
	}
	
	/**
	 * Set the stock that is being used in this transaction. <b>NOTE:</b>
	 * the stock needs to already have downloaded its values, or else 
	 * this operation will take place with outdated data, or possibly
	 * not even have the necessary data at all.
	 * 
	 * @param stock the stock that is being used in this transaction.
	 */
	public void setStock(Stock stock){
		this.stock = stock;
		dialogTitleLabel.setText(stock.getSymbol());
		sharePriceValue.setText(moneyFormat.format(stock.getCurrentPrice()));
	}
	
	protected void setShareLimit(int shareLimit){
		this.shareLimit = shareLimit;
		quantitySlider.setMaximum(shareLimit);
		int majorTick = shareLimit / 5;
		quantitySlider.setMajorTickSpacing(majorTick);
		quantitySlider.setMinorTickSpacing(majorTick / 5);
	}
	
	public int getShareLimit(){
		return shareLimit;
	}
	
	public BigDecimal getCurrentPrice(){
		return stock.getCurrentPrice();
	}
	
	public int getQuantity(){
		return quantity;
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
		JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, max, 0);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setSnapToTicks(true);
		slider.addChangeListener(new QuantitySliderListener());
		
		return slider;
	}
	
	private void setSliderPosition(int position){
		quantitySlider.setValue(position);
		quantity = position;
	}
	
	//TODO this could throw NumberFormatException, but won't because the filter prevents
	//non numeric text from being in this field
	private void setQuantityPositionToField(int position){
		quantityField.setText("" + position);
		quantity = position;
	}
	
	private void setValueField(int quantity){
		BigDecimal value = stock.getCurrentPrice().multiply(new BigDecimal(quantity));
		valueField.setText(moneyFormat.format(value));
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
		detailsPanel.add(limitValue, "align right, wrap");
		
		detailsPanel.add(sharePriceLabel, "gap 0 0 5 10");
		detailsPanel.add(sharePriceValue, "align right, wrap");
		
		detailsPanel.add(sliderLabel, "span 2, center, pushx, wrap");
		detailsPanel.add(quantitySlider, "span 2, growx, pushx, wrap");
		
		detailsPanel.add(quantityFieldLabel, "split 2");
		detailsPanel.add(quantityField, "");
		detailsPanel.add(valueFieldLabel, "split 2");
		detailsPanel.add(valueField, "");
		
		return detailsPanel;
	}

	@Override
	protected JButton[] addButtons() {
		JButton tempButton = createTransactionButton();
		transactionAction.setText(tempButton.getText());
		transactionAction.setToolTipText(tempButton.getToolTipText());
		transactionAction.setActionCommand(tempButton.getActionCommand());
		transactionAction.setIcon(tempButton.getIcon());
		
		transactionButton = new JButton(transactionAction);
		transactionButton.setFont(tempButton.getFont());
		
		return new JButton[] {transactionButton, cancelButton};
	}
	
	@Override
	protected void assembleDialog(){
		limitLabel.setText(limitLabelText());
		limitValue.setText(limitValueText());
		super.assembleDialog();
	}
	
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
	 * This method should return the button to execute the
	 * transaction. This is the equivalent of "ok" or "execute",
	 * but labeled based on the type of transaction.
	 * 
	 * @return the button to execute the transaction.
	 */
	protected abstract JButton createTransactionButton();
	
	
	private class QuantitySliderListener implements ChangeListener{

		@Override
		public void stateChanged(ChangeEvent event) {
			if(event.getSource() instanceof JSlider){
				JSlider slider = (JSlider) event.getSource();
				int position = slider.getValue();
				if(!quantityField.hasFocus()){
					setQuantityPositionToField(position);
					setValueField(position);
				}
			}
		}
		
	}
	
	private class QuantityFieldListener implements DocumentListener{

		@Override
		public void changedUpdate(DocumentEvent event) {
			updateSlider(event);
		}

		@Override
		public void insertUpdate(DocumentEvent event) {
			updateSlider(event);
		}

		@Override
		public void removeUpdate(DocumentEvent event){
			updateSlider(event);
		}
		
		//TODO this can throw NumberFormatException, but shouldn't because
		//The filter won't allow non-numerical text in the field.
		private void updateSlider(DocumentEvent event){
			try{
				Document doc = event.getDocument();
				String text = doc.getText(0, doc.getLength());
				int position;
				if(text.equals("") || text.contains(" ")){
					position = 0;
				}
				else{
					position = Integer.parseInt(text);
				}
				
				//TODO unsafe reference to quantityField
				if(quantityField.hasFocus()){
					setSliderPosition(position);
					setValueField(position);
				}
				
			}
			catch(BadLocationException ex){
				LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
						"updateSlider", 
						"Exception - THIS ONE SHOULDN'T OCCUR, CHECK THE CODE", ex);
			}
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
			String resultText = generateFinalInsertText(fb, offset, text);
			
			if(bypassInsertOrReplace(resultText)){
				super.insertString(fb, offset, text, attr);
			}
		}
		
		@Override
		public void replace(DocumentFilter.FilterBypass fb, int offset, int length, 
				String text, AttributeSet attr) throws BadLocationException{
			String resultText = generateFinalReplaceText(fb, offset, length, text);
			
			if(bypassInsertOrReplace(resultText)){
				super.replace(fb,  offset, length, text, attr);
			}
			
			Document doc = fb.getDocument();
			String fullText = doc.getText(0, doc.getLength());
			
			//If the first character is a 0, and the text is longer than 1 character,
			//remove the 0
			if(fullText.charAt(0) == '0' && fullText.length() > 1){
				remove(fb, 0, 1);
			}
		}
		
		@Override
		public void remove(DocumentFilter.FilterBypass fb, int offset, 
				int length) throws BadLocationException{
			Document doc = fb.getDocument();
			String fullText = doc.getText(0, doc.getLength());
			String textAfterRemove = fullText.substring(0, offset) 
					+ fullText.substring((offset + length - 1), (fullText.length() - 1));
			
			if(textAfterRemove.equals("")){
				insertString(fb, 0, "0", null);
				super.remove(fb, offset + 1, length);
			}
			else{
				super.remove(fb, offset, length);
			}
			
		}
		
		/**
		 * Generate what the final text will be if the insert operation
		 * is completed.
		 * 
		 * @param fb the <tt>FilterBypass</tt> object to work around the filter.
		 * @param offset the offset of the replacement text.
		 * @param newText the replacement text.
		 * @return the final text if the replace operation is completed.
		 * @throws BadLocationException if text retrieved from the <tt>Document</tt>
		 * doesn't exist at the specified location.
		 */
		private String generateFinalInsertText(DocumentFilter.FilterBypass fb, 
				int offset, String newText) throws BadLocationException{
			StringBuilder result = new StringBuilder();
			Document doc = fb.getDocument();
			String existingText = doc.getText(0, doc.getLength());
			
			result.append(existingText.substring(0, offset));
			result.append(newText);
			result.append(existingText.substring(offset));
			
			return result.toString();
		}
		
		/**
		 * Generate what the final text will be if the replace operation
		 * is completed.
		 * 
		 * @param fb the <tt>FilterBypass</tt> object to work around the filter.
		 * @param offset the offset of the replacement text.
		 * @param length the length of the replacement text.
		 * @param newText the replacement text.
		 * @return the final text if the replace operation is completed.
		 * @throws BadLocationException if text retrieved from the <tt>Document</tt>
		 * doesn't exist at the specified location.
		 */
		private String generateFinalReplaceText(DocumentFilter.FilterBypass fb, 
				int offset, int length, String newText) throws BadLocationException{
			StringBuilder result = new StringBuilder();
			Document doc = fb.getDocument();
			String existingText = doc.getText(0, doc.getLength());
			
			result.append(existingText.substring(0, offset));
			result.append(newText);
			if((offset + length) < existingText.length()){
				result.append(existingText.substring(offset + length));
			}
			
			return result.toString();
		}
		
		
		
		/**
		 * Checks the inserted or replaced text to determine if it should bypass 
		 * the filter or not.
		 * 
		 * @param fb the <tt>FilterBypass</tt> object.
		 * @param text the text to check.
		 * @return true if the text should bypass the filter.
		 * @throws BadLocationException if text retrieved from the <tt>Document</tt>
		 * doesn't exist at the specified location.
		 */
		private boolean bypassInsertOrReplace(String fullText) 
				throws BadLocationException{
			boolean result;
			
			if(! hasOnlyNumbers(fullText)){
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
				if(quantity > getShareLimit()){
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
		public void setIcon(Icon icon){
			putValue(AbstractAction.SMALL_ICON, icon);
		}
		
		@Override
		public void actionPerformed(ActionEvent event) {
			if(event.getActionCommand() != DISMISS_ONLY_ACTION){
				TransactionDialog.this.actionPerformed(event);
			}
			
			TransactionDialog.this.closeDialog();
		}
		
	}

}
