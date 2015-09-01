package io.craigmiller160.stockmarket.gui;

import static io.craigmiller160.stockmarket.controller.StockMarketController.PORTFOLIO_STATE_PROPERTY;
import static io.craigmiller160.stockmarket.controller.StockMarketController.STOCK_SEARCH_ACTION;
import io.craigmiller160.mvp.listener.AbstractListenerView;
import io.craigmiller160.stockmarket.util.Fonts;
import io.craigmiller160.stockmarket.util.Language;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.jcip.annotations.NotThreadSafe;
import net.miginfocom.swing.MigLayout;

/**
 * GUI class that defines the panel containing the search bar
 * for looking up stocks.
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
public class SearchPanel extends AbstractListenerView {

	/**
	 * Shared logger for the program.
	 */
	private static final Logger LOGGER = Logger.getLogger("stockmarket.gui.SearchPanel");
	
	/**
	 * Button that displays the stock search field.
	 */
	private JButton lookupStockButton;
	
	/**
	 * Button to search for the inputted stock.
	 */
	private JButton searchButton;
	
	/**
	 * Button to cancel the search.
	 */
	private JButton cancelButton;
	
	/**
	 * Field to input the symbol of the stock to search for.
	 */
	private JTextField searchField;
	
	/**
	 * The panel containing just the button to display the search field.
	 */
	private JPanel buttonPanel;
	
	/**
	 * The panel containing the search field and search and cancel buttons.
	 */
	private JPanel fieldPanel;
	
	/**
	 * The search field's label.
	 */
	private JLabel searchFieldLabel;
	
	/**
	 * The panel created by this class.
	 */
	private final JPanel searchPanel;
	
	/**
	 * The <tt>CardLayout</tt> for switching between displaying the
	 * button panel and the field panel.
	 */
	private final CardLayout searchPanelLayout;
	
	/**
	 * Action Command for showing the field panel.
	 */
	public static final String SHOW_SEARCH_FIELD_ACTION = "ShowSearchField";
	
	/**
	 * Action Command for hiding the field panel.
	 */
	public static final String HIDE_SEARCH_FIELD_ACTION = "HideSearchField";
	
	/**
	 * <tt>CardLayout</tt> panel name for the panel with the lookup button.
	 */
	private static final String BUTTON_PANEL = "ButtonPanel";

	/**
	 * <tt>CardLayout</tt> panel name for the panel with the search field.
	 */
	private static final String FIELD_PANEL = "FieldPanel";
	
	/**
	 * Shared <tt>Language</tt> module for locale-specific text.
	 */
	private static final Language LANGUAGE = Language.getInstance();
	
	/**
	 * Create the panel.
	 */
	public SearchPanel() {
		super();
		searchPanelLayout = new CardLayout();
		searchPanel = createSearchPanel(searchPanelLayout);
		initComponents();
		assembleSearchPanel();
	}
	
	/**
	 * Create the <tt>JPanel</tt> that this class wraps around.
	 * 
	 * @return the created <tt>JPanel</tt>.
	 */
	private JPanel createSearchPanel(CardLayout layout){
		JPanel searchPanel = new JPanel();
		searchPanel.setLayout(searchPanelLayout);
		
		return searchPanel;
	}
	
	/**
	 * Initialize the components for this GUI class.
	 */
	private void initComponents(){
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new MigLayout());
		
		fieldPanel = new JPanel();
		fieldPanel.setLayout(new MigLayout("center"));
		
		lookupStockButton = createButton(LANGUAGE.getString("lookup_stock_label"), 
				"search", LANGUAGE.getString("lookup_stock_tooltip"), 
				SHOW_SEARCH_FIELD_ACTION);
		lookupStockButton.setEnabled(false);
		
		searchButton = createButton("", "search", LANGUAGE.getString("lookup_stock_tooltip"),
				STOCK_SEARCH_ACTION);
		searchButton.setEnabled(false);
		
		cancelButton = createButton("", "cancel", LANGUAGE.getString("cancel_search_tooltip"), 
				HIDE_SEARCH_FIELD_ACTION);
		
		searchField = createField("", 10, LANGUAGE.getString("lookup_stock_tooltip"), 
				STOCK_SEARCH_ACTION);
		searchField.getDocument().addDocumentListener(new SearchFieldListener());
		
		searchFieldLabel = createLabel(LANGUAGE.getString("search_field_label") + ": ");
	}
	
	/**
	 * Assemble the components in this panel.
	 */
	private void assembleSearchPanel(){
		buttonPanel.add(lookupStockButton, "center, pushx");
		
		fieldPanel.add(searchFieldLabel, "center");
		fieldPanel.add(searchField, "center");
		fieldPanel.add(searchButton, "");
		fieldPanel.add(cancelButton, "");
		
		searchPanel.add(buttonPanel, BUTTON_PANEL);
		searchPanel.add(fieldPanel, FIELD_PANEL);
	}
	
	/**
	 * Utility method for creating a <tt>JLabel</tt> based on the specified
	 * parameters.
	 * 
	 * @param text the label's text.
	 * @return the created label.
	 */
	private JLabel createLabel(String text){
		JLabel label = new JLabel(text);
		label.setFont(Fonts.LABEL_FONT);
		
		return label;
	}
	
	/**
	 * Utility method for creating a <tt>JTextField</tt> based on the specified
	 * parameters.
	 * 
	 * @param text the text field's text.
	 * @param length the text field's length.
	 * @param toolTipText the text field's tooltip.
	 * @param actionCommand the text field's action command.
	 * @return the created text field.
	 */
	private JTextField createField(String text, int length, String toolTipText, 
			String actionCommand){
		JTextField field = new JTextField(text, length);
		field.setToolTipText(toolTipText);
		field.setFont(Fonts.FIELD_FONT);
		field.setActionCommand(actionCommand);
		field.addActionListener(this);
		field.addActionListener(new CardSwitchListener());
		
		field.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "CancelAction");
		field.getActionMap().put("CancelAction", new CancelAction());
		
		return field;
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
		button.setActionCommand(actionCommand);
		button.setFont(Fonts.LABEL_FONT);
		button.setVerticalTextPosition(JButton.BOTTOM);
		button.setHorizontalTextPosition(JButton.CENTER);
		button.addActionListener(this);
		button.addActionListener(new CardSwitchListener());
		
		return button;
	}
	
	/**
	 * Return the panel created by this class.
	 * 
	 * @return the panel created by this class.
	 */
	public JPanel getPanel(){
		return searchPanel;
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
						"Not valid PortfolioState: " + event.getNewValue());
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
				new Object[] {"Enable State: " + portfolioState});
		
		if(portfolioState == PortfolioState.CLOSED){
			lookupStockButton.setEnabled(false);
		}
		else if(portfolioState == PortfolioState.OPEN_NO_STOCK){
			lookupStockButton.setEnabled(true);
		}
		else if(portfolioState == PortfolioState.OPEN_STOCK){
			lookupStockButton.setEnabled(true);
		}
		else if(portfolioState == PortfolioState.OPEN_OWNED_STOCK){
			lookupStockButton.setEnabled(true);
		}
	}
	
	/**
	 * Get the text from the stock search field.
	 * 
	 * @return the text from the stock search field.
	 */
	public String getSearchFieldText(){
		return searchField.getText();
	}
	
	
	/**
	 * Set the text in the stock search field.
	 * 
	 * @param text the text to set in the stock search field.
	 */
	public void setSearchFieldText(String text){
		searchField.setText(text);
	}
	
	/**
	 * Set the focus on the search field.
	 */
	private void setSearchFieldFocus(){
		searchField.grabFocus();
	}

	@Override
	public Object getValueForAction(String actionCommand) {
		Object result = null;
		if(actionCommand == STOCK_SEARCH_ACTION){
			LOGGER.logp(Level.FINEST, this.getClass().getName(), "getValue", 
					"Entering method", new Object[] {"Command: " + actionCommand});
			
			result = getSearchFieldText();
			setSearchFieldText("");
		}
		
		return result;
	}
	
	/**
	 * Private method called by the <tt>DocumentListener</tt> to enable/disable
	 * the search button depending on if there is text in the search field.
	 */
	private void enableSearchButton(){
		if(searchField.getText().equals("")){
			searchButton.setEnabled(false);
		}
		else{
			searchButton.setEnabled(true);
		}
	}
	
	/**
	 * Shared <tt>Action</tt> for keyboard shortcuts for hiding
	 * the search field.
	 * 
	 * @author craig
	 * @version 2.0
	 */
	private class CancelAction extends AbstractAction{

		/**
		 * SerialVersionUID for serialization support.
		 */
		private static final long serialVersionUID = -986102678823293178L;

		@Override
		public void actionPerformed(ActionEvent event) {
			searchPanelLayout.show(searchPanel, BUTTON_PANEL);
		}
		
	}
	
	/**
	 * A listener that watches for changes to the search field,
	 * and enables/disables the search button based on whether or not
	 * there is text in the search field.
	 * 
	 * @author craig
	 * @version 2.0
	 */
	private class SearchFieldListener implements DocumentListener{

		@Override
		public void changedUpdate(DocumentEvent event) {
			enableSearchButton();
		}

		@Override
		public void insertUpdate(DocumentEvent event) {
			enableSearchButton();
		}

		@Override
		public void removeUpdate(DocumentEvent event) {
			enableSearchButton();
		}
		
	}
	
	/**
	 * Internal listener for showing and hiding the search field.
	 * 
	 * @author craig
	 * @version 2.0
	 */
	private class CardSwitchListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent event) {
			if(event.getActionCommand() == SHOW_SEARCH_FIELD_ACTION){
				searchPanelLayout.show(searchPanel, FIELD_PANEL);
				setSearchFieldFocus();
			}
			else if(event.getActionCommand() == HIDE_SEARCH_FIELD_ACTION){
				searchPanelLayout.show(searchPanel, BUTTON_PANEL);
			}
			else if(event.getActionCommand() == STOCK_SEARCH_ACTION){
				searchPanelLayout.show(searchPanel, BUTTON_PANEL);
			}
		}
		
	}

}
