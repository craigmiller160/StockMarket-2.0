package stockmarket.gui;

import static stockmarket.controller.StockMarketController.BUY_STOCK_PREP_ACTION;
import static stockmarket.controller.StockMarketController.COMPONENTS_ENABLED_PROPERTY;
import static stockmarket.controller.StockMarketController.ENABLE_LOOKUP_STOCK_LOADED;
import static stockmarket.controller.StockMarketController.ENABLE_NO_PORTFOLIO_OPEN;
import static stockmarket.controller.StockMarketController.ENABLE_NO_STOCK_LOADED;
import static stockmarket.controller.StockMarketController.ENABLE_OWNED_STOCK_LOADED;
import static stockmarket.controller.StockMarketController.SELL_STOCK_PREP_ACTION;

import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import mvp.listener.AbstractListenerView;
import net.jcip.annotations.NotThreadSafe;
import net.miginfocom.swing.MigLayout;
import stockmarket.util.Fonts;
import stockmarket.util.Language;

/**
 * A GUI panel with the controls for buying and selling stocks on it.
 * The panel also contains a summary of the portfolio's stats.
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
public class BuySellPanel extends AbstractListenerView {

	/**
	 * Shared <tt>Language</tt> module for locale-specific text.
	 */
	private static final Language LANGUAGE = Language.getInstance();
	
	/**
	 * Shared logger for the program.
	 */
	private static final Logger LOGGER = Logger.getLogger("stockmarket.gui.BuySellPanel");
	
	/**
	 * The panel created by this class.
	 */
	private final JPanel buySellPanel;
	
	/**
	 * The button to buy stocks.
	 */
	private JButton buyButton;
	
	/**
	 * The button to sell stocks.
	 */
	private JButton sellButton;
	
	/**
	 * The program label, fills up space in the middle between the buttons.
	 */
	private JLabel programLabel;
	
	/**
	 * Create the panel.
	 */
	public BuySellPanel() {
		super();
		buySellPanel = createBuySellPanel();
		initComponents();
		assembleBuySellPanel();
	}
	
	/**
	 * Create the <tt>JPanel</tt> that this class wraps around.
	 * 
	 * @return the created <tt>JPanel</tt>.
	 */
	private JPanel createBuySellPanel(){
		JPanel buySellPanel = new JPanel();
		buySellPanel.setLayout(new MigLayout());
		
		return buySellPanel;
	}
	
	/**
	 * Initialize the components for this GUI class.
	 */
	private void initComponents(){
		//TODO remove this program label, it's taking up too much space.
		programLabel = createLabel(LANGUAGE.getString("program_title"), 
				Fonts.GIANT_LABEL_FONT, null);
		programLabel.setHorizontalAlignment(JLabel.CENTER);
		
		buyButton = createButton(LANGUAGE.getString("buy_button_label"), "buy",
				LANGUAGE.getString("buy_button_tooltip"), BUY_STOCK_PREP_ACTION);
		buyButton.setEnabled(false);
		
		sellButton = createButton(LANGUAGE.getString("sell_button_label"), "sell",
				LANGUAGE.getString("sell_button_tooltip"), SELL_STOCK_PREP_ACTION);
		sellButton.setEnabled(false);
	}
	
	/**
	 * Assemble the components in this panel.
	 */
	private void assembleBuySellPanel(){
		buySellPanel.add(buyButton, "");
		buySellPanel.add(programLabel, "center, grow, push");
		buySellPanel.add(sellButton, "");
	}
	
	/**
	 * Utility method for creating a <tt>JLabel</tt> based on the specified
	 * parameters.
	 * 
	 * @param text the label's text.
	 * @param font the label's font.
	 * @param toolTipText the label's tool tip text.
	 * @return the created label.
	 */
	private JLabel createLabel(String text, Font font, String toolTipText){
		JLabel label = new JLabel(text);
		label.setFont(font);
		label.setToolTipText(toolTipText);
		
		return label;
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
		button.setFont(Fonts.BIG_LABEL_FONT);
		button.setVerticalTextPosition(JButton.BOTTOM);
		button.setHorizontalTextPosition(JButton.CENTER);
		button.setActionCommand(actionCommand);
		button.addActionListener(this);
		
		return button;
	}
	
	/**
	 * Return the panel created by this class.
	 * 
	 * @return the panel created by this class.
	 */
	public JPanel getPanel(){
		return buySellPanel;
	}

	@Override
	public void changeProperty(PropertyChangeEvent event) {
		if(event.getPropertyName() == COMPONENTS_ENABLED_PROPERTY){
			LOGGER.logp(Level.FINEST, this.getClass().getName(), 
					"changeProperty", "Changing Property", 
					new Object[]{"Property: " + event.getPropertyName()});
			
			changeComponentsEnabled((Integer) event.getNewValue());
		}
	}
	
	/**
	 * Change which components are enabled in this class.
	 * 
	 * @param componentsEnabledState the state of the program to enable
	 * components for.
	 */
	public void changeComponentsEnabled(Integer componentsEnabledState){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"changeComponentsEnabled", "Entering method", 
				new Object[] {"Enable State: " + componentsEnabledState});
		
		if(componentsEnabledState == ENABLE_NO_PORTFOLIO_OPEN){
			buyButton.setEnabled(false);
			sellButton.setEnabled(false);
		}
		else if(componentsEnabledState == ENABLE_NO_STOCK_LOADED){
			buyButton.setEnabled(false);
			sellButton.setEnabled(false);
		}
		else if(componentsEnabledState == ENABLE_LOOKUP_STOCK_LOADED){
			buyButton.setEnabled(true);
			sellButton.setEnabled(false);
		}
		else if(componentsEnabledState == ENABLE_OWNED_STOCK_LOADED){
			buyButton.setEnabled(true);
			sellButton.setEnabled(true);
		}
	}

	@Override
	public Object getValueForAction(String valueToGet) {
		//LOGGER.logp(Level.FINEST, this.getClass().getName(), "getValue", 
				//"Entering method", new Object[] {"Command: " + valueToGet});
		
		// TODO Auto-generated method stub
		return null;
	}

}
