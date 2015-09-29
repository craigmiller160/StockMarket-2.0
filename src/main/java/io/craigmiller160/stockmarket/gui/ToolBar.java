package io.craigmiller160.stockmarket.gui;

import static io.craigmiller160.stockmarket.controller.StockMarketController.CLOSE_PORTFOLIO_ACTION;
import static io.craigmiller160.stockmarket.controller.StockMarketController.EDIT_PORTFOLIO_NAME_ACTION;
import static io.craigmiller160.stockmarket.controller.StockMarketController.MARKET_DATA_ACTION;
import static io.craigmiller160.stockmarket.controller.StockMarketController.NEW_PORTFOLIO_ACTION;
import static io.craigmiller160.stockmarket.controller.StockMarketController.OPEN_PORTFOLIO_ACTION;
import static io.craigmiller160.stockmarket.controller.StockMarketController.PORTFOLIO_NAME_PROPERTY;
import static io.craigmiller160.stockmarket.controller.StockMarketController.PORTFOLIO_STATE_PROPERTY;
import static io.craigmiller160.stockmarket.controller.StockMarketController.SAVE_PORTFOLIO_ACTION;
import io.craigmiller160.mvp.listener.AbstractListenerView;
import io.craigmiller160.stockmarket.util.Fonts;
import io.craigmiller160.stockmarket.util.Language;

import java.beans.PropertyChangeEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import net.jcip.annotations.NotThreadSafe;

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
		if(event.getPropertyName() == PORTFOLIO_STATE_PROPERTY){
			if(event.getNewValue() instanceof PortfolioState){
				portfolioStateChanged((PortfolioState) event.getNewValue());
			}
			else{
				throw new IllegalArgumentException(
						"Not instance of PortfolioState: " + event.getNewValue());
			}
		}
		else if(event.getPropertyName() == PORTFOLIO_NAME_PROPERTY){
			if(event.getNewValue() instanceof String){
				setPortfolioNameFieldText((String) event.getNewValue());
			}
			else{
				throw new IllegalArgumentException(
						"Not valid String: " + event.getNewValue());
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
		if(portfolioState == PortfolioState.CLOSED){
			savePortfolioButton.setEnabled(false);
			closePortfolioButton.setEnabled(false);
			portfolioNameField.setEnabled(false);
			marketDataButton.setEnabled(false);
			editNameButton.setEnabled(false);
		}
		else if(portfolioState == PortfolioState.OPEN_NO_STOCK){
			savePortfolioButton.setEnabled(true);
			closePortfolioButton.setEnabled(true);
			portfolioNameField.setEnabled(true);
			marketDataButton.setEnabled(true);
			editNameButton.setEnabled(true);
		}
		else if(portfolioState == PortfolioState.OPEN_STOCK){
			
		}
		else if(portfolioState == PortfolioState.OPEN_OWNED_STOCK){
			
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
		return null;
	}

}
