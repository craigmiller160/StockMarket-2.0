package stockmarket.gui;

import static stockmarket.controller.StockMarketController.CLOSE_PORTFOLIO_ACTION;
import static stockmarket.controller.StockMarketController.COMPONENTS_ENABLED_PROPERTY;
import static stockmarket.controller.StockMarketController.EDIT_PORTFOLIO_NAME_ACTION;
import static stockmarket.controller.StockMarketController.ENABLE_LOOKUP_STOCK_LOADED;
import static stockmarket.controller.StockMarketController.ENABLE_NO_PORTFOLIO_OPEN;
import static stockmarket.controller.StockMarketController.ENABLE_NO_STOCK_LOADED;
import static stockmarket.controller.StockMarketController.ENABLE_OWNED_STOCK_LOADED;
import static stockmarket.controller.StockMarketController.MARKET_DATA_ACTION;
import static stockmarket.controller.StockMarketController.NEW_PORTFOLIO_ACTION;
import static stockmarket.controller.StockMarketController.OPEN_PORTFOLIO_ACTION;
import static stockmarket.controller.StockMarketController.PORTFOLIO_NAME_PROPERTY;
import static stockmarket.controller.StockMarketController.SAVE_PORTFOLIO_ACTION;

import java.beans.PropertyChangeEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import mvp.listener.AbstractListenerView;
import net.jcip.annotations.NotThreadSafe;
import stockmarket.util.Fonts;
import stockmarket.util.Language;

/**
 * GUI class that defines a toolbar for the application window.
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
public class ToolBar extends AbstractListenerView {

	/**
	 * Shared logger for the program.
	 */
	private static final Logger LOGGER = Logger.getLogger("stockmarket.gui.ToolBar");
	
	/**
	 * Shared <tt>Language</tt> module for locale-specific text.
	 */
	private static final Language LANGUAGE = Language.getInstance(); 
	
	/**
	 * The toolbar created by this class.
	 */
	private final JToolBar toolBar;
	
	/**
	 * The new portfolio button.
	 */
	private JButton newPortfolioButton;
	
	/**
	 * The open portfolio button.
	 */
	private JButton openPortfolioButton;
	
	/**
	 * The save portfolio button.
	 */
	private JButton savePortfolioButton;
	
	/**
	 * The close portfolio button.
	 */
	private JButton closePortfolioButton;
	
	/**
	 * The portfolio name field.
	 */
	private JTextField portfolioNameField;
	
	/**
	 * The edit portfolio name button.
	 */
	private JButton editNameButton;
	
	/**
	 * The portfolio name label.
	 */
	private JLabel portfolioNameLabel;
	
	/**
	 * The button to get market data.
	 */
	private JButton marketDataButton;
	
	/**
	 * Create the toolbar.
	 */
	public ToolBar() {
		super();
		toolBar = createToolBar();
		initComponents();
		assembleToolBar();
	}
	
	/**
	 * Create the <tt>JToolBar</tt> that this class wraps around.
	 * 
	 * @return the created <tt>JToolBar</tt>.
	 */
	private JToolBar createToolBar(){
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		
		return toolBar;
	}
	
	/**
	 * Initialize the components for this GUI class.
	 */
	private void initComponents(){
		newPortfolioButton = createButton("", "new", LANGUAGE.getString("new_portfolio_tooltip"), 
				NEW_PORTFOLIO_ACTION);
		
		openPortfolioButton = createButton("", "open", LANGUAGE.getString("open_portfolio_tooltip"),
				OPEN_PORTFOLIO_ACTION);
		
		savePortfolioButton = createButton("", "save", LANGUAGE.getString("save_portfolio_tooltip"),
				SAVE_PORTFOLIO_ACTION);
		savePortfolioButton.setEnabled(false);
		
		closePortfolioButton = createButton("", "close", LANGUAGE.getString("close_portfolio_tooltip"),
				CLOSE_PORTFOLIO_ACTION);
		closePortfolioButton.setEnabled(false);
		
		portfolioNameLabel = createLabel(LANGUAGE.getString("portfolio_name_label") + ": ");
		
		portfolioNameField = createField("", 15, LANGUAGE.getString("portfolio_name_tooltip"));
		portfolioNameField.setEnabled(false);
		
		editNameButton = createButton("", "edit", LANGUAGE.getString("edit_portfolio_name_tooltip"), 
				EDIT_PORTFOLIO_NAME_ACTION);
		editNameButton.setEnabled(false);
		
		//"yahoo" is referenced in the icon and the tooltip
		marketDataButton = createButton(LANGUAGE.getString("market_data_label"), "yahoo", 
				LANGUAGE.getString("market_data_tooltip"), MARKET_DATA_ACTION);
		marketDataButton.setEnabled(false);
	}
	
	/**
	 * Assemble the components in this toolbar.
	 */
	private void assembleToolBar(){
		toolBar.addSeparator();
		toolBar.add(newPortfolioButton);
		toolBar.add(openPortfolioButton);
		toolBar.add(savePortfolioButton);
		toolBar.add(closePortfolioButton);
		toolBar.addSeparator();
		toolBar.add(portfolioNameLabel);
		toolBar.add(portfolioNameField);
		toolBar.add(editNameButton);
		toolBar.addSeparator();
		toolBar.add(marketDataButton);
	}
	
	/**
	 * Get the toolbar created by this class.
	 * 
	 * @return the toolbar created by this class.
	 */
	public JToolBar getToolBar(){
		return toolBar;
	}
	
	/**
	 * Utility method for creating a <tt>JLabel</tt> based on the specified
	 * parameters.
	 * 
	 * @param text the label's text.
	 * @return the created label.
	 */
	private JLabel createLabel(String labelText){
		JLabel label = new JLabel(labelText);
		label.setFont(Fonts.LABEL_FONT);
		return label;
	}
	
	/**
	 * Utility method for creating a <tt>JTextField</tt> based on the specified
	 * parameters.
	 * 
	 * @param fieldText the field's text.
	 * @param length the field's length.
	 * @param toolTip the field's tooltip.
	 * @return the created field.
	 */
	private JTextField createField(String fieldText, int length, String toolTip){
		JTextField field = new JTextField(length);
		field.setToolTipText(toolTip);
		field.setText(fieldText);
		field.setEditable(false);
		field.setFocusable(false);
		field.setFont(Fonts.LABEL_FONT);
		
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
		JButton button = new JButton(text, 
				new ImageIcon(this.getClass().getClassLoader().getResource(
						"32p/" + icon + ".png")));
		button.setToolTipText(toolTipText);
		button.setFont(Fonts.LABEL_FONT);
		button.setActionCommand(actionCommand);
		button.addActionListener(this);
		
		return button;
	}

	/**
	 * {@inheritDoc}
	 * @throws IllegalArgumentException if the new value from the event is not
	 * the expected type to perform the operation.
	 */
	@Override
	public void changeProperty(PropertyChangeEvent event) {
		if(event.getPropertyName() == COMPONENTS_ENABLED_PROPERTY){
			LOGGER.logp(Level.FINEST, this.getClass().getName(), 
					"changeProperty", "Changing Property", 
					new Object[]{"Property: " + event.getPropertyName()});
			
			changeComponentsEnabled((Integer) event.getNewValue());
		}
		else if(event.getPropertyName() == PORTFOLIO_NAME_PROPERTY){
			setPortfolioNameFieldText((String) event.getNewValue());
		}
	}
	
	/**
	 * Change which components are enabled in this class.
	 * 
	 * @param componentsEnabledState the state of the program to enable
	 * components for.
	 */
	public void changeComponentsEnabled(int componentsEnabledState){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"changeComponentsEnabled", "Entering method", 
				new Object[] {"Enable State: " + componentsEnabledState});
		
		if(componentsEnabledState == ENABLE_NO_PORTFOLIO_OPEN){
			savePortfolioButton.setEnabled(false);
			closePortfolioButton.setEnabled(false);
			portfolioNameField.setEnabled(false);
			marketDataButton.setEnabled(false);
			editNameButton.setEnabled(false);
		}
		else if(componentsEnabledState == ENABLE_NO_STOCK_LOADED){
			savePortfolioButton.setEnabled(true);
			closePortfolioButton.setEnabled(true);
			portfolioNameField.setEnabled(true);
			marketDataButton.setEnabled(true);
			editNameButton.setEnabled(true);
		}
		else if(componentsEnabledState == ENABLE_LOOKUP_STOCK_LOADED){
			
		}
		else if(componentsEnabledState == ENABLE_OWNED_STOCK_LOADED){
			
		}
	}
	
	/**
	 * Set the text of the portfolio name field.
	 * 
	 * @param text the text to set to the field.
	 */
	public void setPortfolioNameFieldText(String text){
		portfolioNameField.setText(text);
	}

	@Override
	public Object getValueForAction(String actionCommand) {
		//LOGGER.logp(Level.FINEST, this.getClass().getName(), "getValue", 
				//"Entering method", new Object[] {"Command: " + valueToGet});
		
		
		// TODO Will be filled out if values are needed from this view
		return null;
	}

}
