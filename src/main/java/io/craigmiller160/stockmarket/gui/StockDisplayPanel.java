package io.craigmiller160.stockmarket.gui;

import static io.craigmiller160.stockmarket.controller.StockMarketController.PORTFOLIO_STATE_PROPERTY;
import io.craigmiller160.mvp.listener.AbstractListenerView;
import io.craigmiller160.stockmarket.util.Fonts;
import io.craigmiller160.stockmarket.util.Language;

import java.awt.CardLayout;
import java.beans.PropertyChangeEvent;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.miginfocom.swing.MigLayout;

/**
 * GUI component that defines the display panel where stock information appears.
 * Like the <tt>Frame</tt>, this component has multiple sub components that it
 * displays, and those sub components are in separate classes. It uses a system
 * of placeholder components, combined with setter methods, to allow this class
 * to exist independently of other GUI classes while still incorporating them
 * into its design.
 * <p>
 * Like all GUI classes in this program, this class separates its 
 * instantiation code from its assembly code for maximum flexibility.
 * <p>
 * <b>THREAD SAFETY:</b> Swing is NOT thread safe.
 * 
 * @author craig
 * @version 2.0
 */
public class StockDisplayPanel extends AbstractListenerView {

	/**
	 * The panel created by this class.
	 */
	private final JPanel stockDisplayPanel;
	
	/**
	 * Blank placeholder component for the stock details panel.
	 */
	private JComponent stockDetailsPanel;
	
	/**
	 * Blank placeholder component for the stock history panel.
	 */
	private JComponent stockHistoryPanel;
	
	/**
	 * Blank placeholder component for the buy sell panel.
	 */
	private JComponent buySellPanel;
	
	/**
	 * Blank placeholder component for the search panel.
	 */
	private JComponent searchPanel;
	
	/**
	 * Tabbed pane that will hold the details and history panels.
	 */
	private JTabbedPane displayTabsPanel;
	
	/**
	 * Custom label for the tab of the tabbed pane for the details tab.
	 */
	private JLabel detailsTabLabel;
	
	/**
	 * Custom label for the tab of the tabbed pane for the history tab.
	 */
	private JLabel historyTabLabel;
	
	/**
	 * Panel that displays or hides the tabbed pane with stock information.
	 */
	private JPanel displaySwitchPanel;
	
	/**
	 * The layout for the display switch panel.
	 */
	private CardLayout displaySwitchLayout;
	
	/**
	 * The panel to be shown when the tabbed pane is hidden.
	 */
	private JPanel nothingSelectedPanel;
	
	/**
	 * Shared <tt>Language</tt> module for locale-specific text.
	 */
	private static final Language LANGUAGE = Language.getInstance();
	
	/**
	 * Stock details panel tab name for configuring the tabbed pane.
	 */
	private static final String STOCK_DETAILS_PANEL = LANGUAGE.getString("stock_details_tab");
	
	/**
	 * Stock history panel tab name for configuring the tabbed pane.
	 */
	private static final String STOCK_HISTORY_PANEL = LANGUAGE.getString("stock_history_tab");
	
	/**
	 * Nothing selected panel constant name, for when hiding the tabbed pane.
	 */
	private static final String NOTHING_SELECTED_PANEL = "NothingSelectedPanel";
	
	/**
	 * Tabbed pane panel constant name, for when showing the tabbed pane.
	 */
	private static final String DISPLAY_TABS_PANEL = "DetailTabsPanel";
	
	/**
	 * Create the panel.
	 */
	public StockDisplayPanel() {
		super();
		stockDisplayPanel = createStockDisplayPanel();
		initComponents();
		assembleStockDisplayPanel();
	}
	
	/**
	 * Create the <tt>JPanel</tt> that this class wraps around.
	 * 
	 * @return the created <tt>JPanel</tt>.
	 */
	private JPanel createStockDisplayPanel(){
		JPanel stockDisplayPanel = new JPanel();
		stockDisplayPanel.setLayout(new MigLayout(""));
		
		return stockDisplayPanel;
	}
	
	/**
	 * Return the panel created by this class.
	 * 
	 * @return the panel created by this class.
	 */
	public JPanel getPanel(){
		return stockDisplayPanel;
	}
	
	/**
	 * Initialize the components for this GUI class.
	 */
	private void initComponents(){
		stockDetailsPanel = new JPanel();
		stockHistoryPanel = new JPanel();
		buySellPanel = new JPanel();
		searchPanel = new JPanel();
		
		displayTabsPanel = new JTabbedPane();
		displayTabsPanel.setFont(Fonts.TAB_FONT);
		
		ImageIcon detailsIcon = new ImageIcon(
				this.getClass().getClassLoader().getResource("32p/details.png"));
		detailsTabLabel = createTabLabel(STOCK_DETAILS_PANEL, detailsIcon);
		
		ImageIcon historyIcon = new ImageIcon(
				this.getClass().getClassLoader().getResource("32p/history.png"));
		historyTabLabel = createTabLabel(STOCK_HISTORY_PANEL, historyIcon);
		
		displaySwitchLayout = new CardLayout();
		
		displaySwitchPanel = new JPanel();
		displaySwitchPanel.setLayout(displaySwitchLayout);
		
		nothingSelectedPanel = new JPanel();
	}
	
	/**
	 * Utility method to construct a label to be assigned as the tab
	 * on a <tt>JTabbedPane</tt>.
	 * 
	 * @param text the text for the tab.
	 * @param icon the icon for the tab.
	 * @return the label constructed for the tab.
	 */
	private JLabel createTabLabel(String text, ImageIcon icon){
		JLabel label = new JLabel(text);
		label.setFont(Fonts.TAB_FONT);
		label.setIcon(icon);
		label.setIconTextGap(5);
		label.setHorizontalTextPosition(JLabel.RIGHT);
		
		return label;
	}
	
	/**
	 * Assemble the components in this panel.
	 */
	private void assembleStockDisplayPanel(){
		stockDisplayPanel.removeAll();
		displayTabsPanel.removeAll();
		
		stockDisplayPanel.add(searchPanel, "dock north, center");
		
		
		displayTabsPanel.addTab(STOCK_DETAILS_PANEL, stockDetailsPanel);
		displayTabsPanel.addTab(STOCK_HISTORY_PANEL, stockHistoryPanel);
		displayTabsPanel.setTabComponentAt(0, detailsTabLabel);
		displayTabsPanel.setTabComponentAt(1, historyTabLabel);
		
		displaySwitchPanel.add(nothingSelectedPanel, NOTHING_SELECTED_PANEL);
		displaySwitchPanel.add(displayTabsPanel, DISPLAY_TABS_PANEL);
		
		stockDisplayPanel.add(displaySwitchPanel, "dock center");
		stockDisplayPanel.add(buySellPanel, "dock south");
		
		displayTabsPanel.revalidate();
		displayTabsPanel.repaint();
		stockDisplayPanel.revalidate();
		stockDisplayPanel.repaint();
	}
	
	/**
	 * Set the search panel from an external class, replacing the
	 * placeholder component.
	 * 
	 * @param searchPanel the search panel from an external class.
	 */
	public void setSearchPanel(JComponent searchPanel){
		this.searchPanel = searchPanel;
		assembleStockDisplayPanel();
	}
	
	/**
	 * Set the details panel from an external class, replacing the
	 * placeholder component.
	 * 
	 * @param stockDetailsPanel the details panel from an external class.
	 */
	public void setStockDetailsPanel(JComponent stockDetailsPanel){
		this.stockDetailsPanel = stockDetailsPanel;
		assembleStockDisplayPanel();
	}
	
	/**
	 * Set the history panel from an external class, replacing the
	 * placeholder component.
	 * 
	 * @param stockHistoryPanel the history panel from an external class.
	 */
	public void setStockHistoryPanel(JComponent stockHistoryPanel){
		this.stockHistoryPanel = stockHistoryPanel;
		assembleStockDisplayPanel();
	}
	
	/**
	 * Set the buy sell panel from an external class, replacing the
	 * placeholder component.
	 * 
	 * @param buySellPanel the buy sell panel from an external class.
	 */
	public void setBuySellPanel(JComponent buySellPanel){
		this.buySellPanel = buySellPanel;
		assembleStockDisplayPanel();
	}
	
	/**
	 * Set all components from external classes, replacing their placeholder
	 * components.
	 * 
	 * @param searchPanel the search panel.
	 * @param stockDetailsPanel the details panel.
	 * @param stockHistoryPanel the history panel.
	 * @param buySellPanel the buy sell panel.
	 */
	public void setAllComponents(JComponent searchPanel, 
			JComponent stockDetailsPanel, JComponent stockHistoryPanel,
			JComponent buySellPanel){
		this.searchPanel = searchPanel;
		this.stockDetailsPanel = stockDetailsPanel;
		this.stockHistoryPanel = stockHistoryPanel;
		this.buySellPanel = buySellPanel;
		assembleStockDisplayPanel();
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
	}
	
	/**
	 * Respond to a change in the portfolio state by changing which
	 * components are enabled/disabled, shown/hidden, etc.
	 * 
	 * @param portfolioState the state of the portfolio.
	 */
	public void portfolioStateChanged(PortfolioState portfolioState){
		if(portfolioState == PortfolioState.CLOSED){
			displaySwitchLayout.show(displaySwitchPanel, NOTHING_SELECTED_PANEL);
		}
		else if(portfolioState == PortfolioState.OPEN_NO_STOCK){
			displaySwitchLayout.show(displaySwitchPanel, NOTHING_SELECTED_PANEL);
		}
		else if(portfolioState == PortfolioState.OPEN_STOCK){
			displaySwitchLayout.show(displaySwitchPanel, DISPLAY_TABS_PANEL);
		}
		else if(portfolioState == PortfolioState.OPEN_OWNED_STOCK){
			displaySwitchLayout.show(displaySwitchPanel, DISPLAY_TABS_PANEL);
		}
	}

	@Override
	public Object getValueForAction(String actionCommand) {
		return null;
	}

}
