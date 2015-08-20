package stockmarket.gui;

import static stockmarket.controller.StockMarketController.CASH_BALANCE_PROPERTY;
import static stockmarket.controller.StockMarketController.CHANGE_IN_NET_WORTH_PROPERTY;
import static stockmarket.controller.StockMarketController.COMPONENTS_ENABLED_PROPERTY;
import static stockmarket.controller.StockMarketController.ENABLE_LOOKUP_STOCK_LOADED;
import static stockmarket.controller.StockMarketController.ENABLE_NO_PORTFOLIO_OPEN;
import static stockmarket.controller.StockMarketController.ENABLE_NO_STOCK_LOADED;
import static stockmarket.controller.StockMarketController.ENABLE_OWNED_STOCK_LOADED;
import static stockmarket.controller.StockMarketController.NET_WORTH_PROPERTY;
import static stockmarket.controller.StockMarketController.REFRESH_PORTFOLIO_ACTION;
import static stockmarket.controller.StockMarketController.STOCK_DETAILS_ACTION;
import static stockmarket.controller.StockMarketController.STOCK_LIST_PROPERTY;
import static stockmarket.controller.StockMarketController.TOTAL_STOCK_VALUE_PROPERTY;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

import net.jcip.annotations.NotThreadSafe;
import net.miginfocom.swing.MigLayout;
import stockmarket.stock.OwnedStock;
import stockmarket.stock.Stock;
import stockmarket.util.Fonts;
import stockmarket.util.Language;

/**
 * The panel containing the stock portfolio. The portfolio is
 * represented by a list of all the stocks it contains.
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
public class PortfolioPanel extends AbstractListenerView {
	
	//TODO think about the two scroll panes and how they'll interact with
	//each other
	
	//TODO add a mouse listener for a double click action on the portfolio list.
	
	//TODO sort stocks in list by symbol
	
	/**
	 * Shared <tt>Language</tt> module for locale-specific text.
	 */
	private static final Language LANGUAGE = Language.getInstance();
	
	/**
	 * Shared logger for the program.
	 */
	private static final Logger LOGGER = Logger.getLogger("stockmarket.gui.StockDetailsPanel");
	
	/**
	 * The panel created by this class.
	 */
	private final JPanel portfolioPanel;
	
	/**
	 * Scroll pane for this panel.
	 */
	private final JScrollPane portfolioPanelScrollPane;
	
	/**
	 * The header label of this panel.
	 */
	private JLabel panelHeaderLabel;
	
	/**
	 * The list of stocks owned in this portfolio.
	 */
	private JList<OwnedStock> ownedStockList;
	
	/**
	 * The button to refresh the portfolio.
	 */
	private JButton refreshButton;
	
	/**
	 * The button to lookup the details of an owned stock.
	 */
	private JButton detailsButton;
	
	/**
	 * The scroll pane for the stock list.
	 */
	private JScrollPane stockListScrollPane;
	
	/**
	 * The panel containing a summary of the portfolio's stats.
	 */
	private JPanel summaryPanel;
	
	/**
	 * The cash balance label.
	 */
	private JLabel cashBalanceLabel;
	
	/**
	 * The total stock label.
	 */
	private JLabel totalStockValueLabel;
	
	/**
	 * The net worth label.
	 */
	private JLabel netWorthLabel;
	
	/**
	 * The change in net worth label.
	 */
	private JLabel changeInNetWorthLabel;
	
	/**
	 * The portfolio summary title label.
	 */
	private JLabel portfolioSummaryLabel;
	
	/**
	 * The cash balance value.
	 */
	private JLabel cashBalanceValue;
	
	/**
	 * The total stock value.
	 */
	private JLabel totalStockValue;
	
	/**
	 * The net worth value.
	 */
	private JLabel netWorthValue;
	
	/**
	 * The change in net worth value.
	 */
	private JLabel changeInNetWorthValue;
	
	//TODO document this... or maybe remove it... might not be needed anymore
	private NumberFormat moneyFormat = new DecimalFormat("$###,###,###,##0.00");
	
	/**
	 * Create the panel.
	 */
	public PortfolioPanel() {
		super();
		portfolioPanel = createPortfolioPanel();
		portfolioPanelScrollPane = createPortfolioPanelScrollPane(portfolioPanel);
		initComponents();
		assemblePortfolioPanel();
	}
	
	/**
	 * Initialize the components for this GUI class.
	 */
	private void initComponents(){
		ownedStockList = new JList<>();
		ownedStockList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		ownedStockList.setToolTipText(LANGUAGE.getString("stock_list_tooltip"));
		ownedStockList.setCellRenderer(new PortfolioListCellRenderer());
		ownedStockList.setVisibleRowCount(4);
		
		stockListScrollPane = new JScrollPane(ownedStockList);
		stockListScrollPane.getVerticalScrollBar().setUnitIncrement(15);
		stockListScrollPane.getHorizontalScrollBar().setUnitIncrement(15);
		
		panelHeaderLabel = createLabel(LANGUAGE.getString("portfolio_panel_header"),
				Fonts.BIG_LABEL_FONT, null);
		panelHeaderLabel.setHorizontalAlignment(JLabel.CENTER);
		
		refreshButton = createButton(LANGUAGE.getString("refresh_button_label"), "refresh", 
				LANGUAGE.getString("refresh_button_tooltip"), REFRESH_PORTFOLIO_ACTION);
		refreshButton.setEnabled(false);
		
		detailsButton = createButton(LANGUAGE.getString("details_button_label"), "details",
				LANGUAGE.getString("details_button_tooltip"), STOCK_DETAILS_ACTION);
		detailsButton.setEnabled(false);
		
		
		//TODO summary panel stuff below
		
		portfolioSummaryLabel = createLabel(LANGUAGE.getString("portfolio_summary_label"), 
				Fonts.BIG_LABEL_FONT, null);
		portfolioSummaryLabel.setHorizontalAlignment(JLabel.CENTER);
		
		totalStockValueLabel = createLabel(LANGUAGE.getString("total_stock_value_label") + ": ",
				Fonts.LABEL_FONT, LANGUAGE.getString("total_stock_value_tooltip"));
		
		cashBalanceLabel = createLabel(LANGUAGE.getString("cash_balance_label") + ": ",
				Fonts.LABEL_FONT, LANGUAGE.getString("cash_balance_tooltip"));
		
		netWorthLabel = createLabel(LANGUAGE.getString("net_worth_label") + ": ", 
				Fonts.LABEL_FONT, LANGUAGE.getString("net_worth_tooltip"));
		
		changeInNetWorthLabel = createLabel(LANGUAGE.getString("net_worth_change_label") + ": ", 
				Fonts.LABEL_FONT, LANGUAGE.getString("net_worth_change_tooltip"));
		
		cashBalanceValue = createLabel("", Fonts.FIELD_FONT, //TODO remove text 
				LANGUAGE.getString("cash_balance_tooltip"));
		
		totalStockValue = createLabel("", Fonts.FIELD_FONT, //TODO remove text
				LANGUAGE.getString("total_stock_value_tooltip"));
		
		netWorthValue = createLabel("", Fonts.FIELD_FONT, //TODO remove text 
				LANGUAGE.getString("net_worth_tooltip"));
		
		changeInNetWorthValue = createLabel("", Fonts.FIELD_FONT, //TODO remove text
				LANGUAGE.getString("net_worth_change_tooltip"));
		
		summaryPanel = new JPanel();
		summaryPanel.setLayout(new MigLayout());
	}
	
	/**
	 * Assemble the components in this panel.
	 */
	private void assemblePortfolioPanel(){
		portfolioPanel.add(panelHeaderLabel, "dock north");
		portfolioPanel.add(stockListScrollPane, "dock center, wrap");
		
		portfolioPanel.add(detailsButton, "split 2, growx");
		portfolioPanel.add(refreshButton, "wrap, growx");
		
		//TODO this really doesn't need a parameter I don't think
		assembleSummaryPanel(summaryPanel);
		
		portfolioPanel.add(summaryPanel, "growx");
	}
	
	/**
	 * Assemble the summary panel.
	 * 
	 * @param summaryPanel the summary panel to be assembled.
	 */
	private void assembleSummaryPanel(JPanel summaryPanel){
		summaryPanel.setBackground(Color.WHITE);
		summaryPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		
		summaryPanel.add(portfolioSummaryLabel, "span 2, dock north");
		
		summaryPanel.add(cashBalanceLabel, "split 2, growx, gap 10 0 10");
		summaryPanel.add(cashBalanceValue, "align right, gap 0 10, pushx, wrap");
		
		summaryPanel.add(totalStockValueLabel, "split 2, growx, gap 10 0 10");
		summaryPanel.add(totalStockValue, "align right, gap 0 10, pushx, wrap");
		
		summaryPanel.add(netWorthLabel, "split 2, growx, gap 10 0 10");
		summaryPanel.add(netWorthValue, "align right, pushx, wrap, gap 0 10");
		
		summaryPanel.add(changeInNetWorthLabel, "split 2, growx, gap 10 0 10 10");
		summaryPanel.add(changeInNetWorthValue, "align right, wrap, pushx, gap 0 10");
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
		button.setActionCommand(actionCommand);
		button.setVerticalTextPosition(JButton.BOTTOM);
		button.setHorizontalTextPosition(JButton.CENTER);
		button.setFont(Fonts.LABEL_FONT);
		button.addActionListener(this);
		
		return button;
	}
	
	/** 
	 * Create the <tt>JPanel</tt> that this class wraps around.
	 * 
	 * @return the created <tt>JPanel</tt>.
	 */
	private JPanel createPortfolioPanel(){
		JPanel portfolioPanel = new JPanel();
		portfolioPanel.setLayout(new MigLayout());
		portfolioPanel.setBorder(BorderFactory.createEmptyBorder(
				5, 5, 5, 5));
		
		
		return portfolioPanel;
	}
	
	/** 
	 * Create the <tt>JScrollPane</tt> for the panel created by this class
	 * to reside in.
	 * 
	 * @return the created <tt>JScrollPane</tt>.
	 */
	private JScrollPane createPortfolioPanelScrollPane(JPanel portfolioPanel){
		JScrollPane scrollPane = new JScrollPane(portfolioPanel);
		scrollPane.getVerticalScrollBar().setUnitIncrement(15);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(15);
		
		return scrollPane;
	}
	
	/**
	 * Return the panel created by this class.
	 * 
	 * @return the panel created by this class.
	 */
	public JScrollPane getPanel(){
		return portfolioPanelScrollPane;
	}
	
	//TODO document this
	@SuppressWarnings("serial")
	public void setPortfolioContents(final List<OwnedStock> stockList){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"setPortfolioContents", "Entering method", 
				new Object[]{"Stock List: " + stockList});
		
		ownedStockList.setModel(new AbstractListModel<OwnedStock>(){
			@Override
			public OwnedStock getElementAt(int index) {
				return stockList.get(index);
			}
			
			@Override
			public int getSize() {
				return stockList.size();
			}
		});
	}

	//TODO document how an illegal argument exception is thrown if the param
	//is not valid for the cast operation
	@SuppressWarnings("unchecked")
	@Override
	public void changeProperty(PropertyChangeEvent event) {
		//Logger methods should stay in if/else statement, since this
		//method gets invoked frequently when the event is not one this
		//class needs to react to
		
		if(event.getPropertyName() == COMPONENTS_ENABLED_PROPERTY){
			LOGGER.logp(Level.FINEST, this.getClass().getName(), 
					"changeProperty", "Changing Property", 
					new Object[]{"Property: " + event.getPropertyName()});
			
			changeComponentsEnabled((Integer) event.getNewValue());
		}
		else if(event.getPropertyName() == STOCK_LIST_PROPERTY){
			LOGGER.logp(Level.FINEST, this.getClass().getName(), 
					"changeProperty", "Changing Property", 
					new Object[]{"Property: " + event.getPropertyName()});
			
			setPortfolioContents((List<OwnedStock>) event.getNewValue());
		}
		else if(event.getPropertyName() == COMPONENTS_ENABLED_PROPERTY){
			LOGGER.logp(Level.FINEST, this.getClass().getName(), 
					"changeProperty", "Changing Property", 
					new Object[]{"Property: " + event.getPropertyName()});
			
			changeComponentsEnabled((Integer) event.getNewValue());
		}
		else if(event.getPropertyName() == CASH_BALANCE_PROPERTY){
			LOGGER.logp(Level.FINEST, this.getClass().getName(), 
					"changeProperty", "Changing Property", 
					new Object[]{"Property: " + event.getPropertyName()});
			
			setCashBalanceValue((BigDecimal) event.getNewValue());
		}
		else if(event.getPropertyName() == TOTAL_STOCK_VALUE_PROPERTY){
			LOGGER.logp(Level.FINEST, this.getClass().getName(), 
					"changeProperty", "Changing Property", 
					new Object[]{"Property: " + event.getPropertyName()});
			
			setTotalStockValue((BigDecimal) event.getNewValue());
		}
		else if(event.getPropertyName() == NET_WORTH_PROPERTY){
			LOGGER.logp(Level.FINEST, this.getClass().getName(), 
					"changeProperty", "Changing Property", 
					new Object[]{"Property: " + event.getPropertyName()});
			
			setNetWorthValue((BigDecimal) event.getNewValue());
		}
		else if(event.getPropertyName() == CHANGE_IN_NET_WORTH_PROPERTY){
			LOGGER.logp(Level.FINEST, this.getClass().getName(), 
					"changeProperty", "Changing Property", 
					new Object[]{"Property: " + event.getPropertyName()});
			
			setChangeInNetWorthValue((BigDecimal) event.getNewValue());
		}
		
		//TODO add +/- and/or icons for increase/decrease in value
	}
	
	public void setChangeInNetWorthValue(BigDecimal value){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"setChangeInNetWorthValue", "Entering method", 
				new Object[] {"Net Worth Change: " + value});
		
		String text = moneyFormat.format(value);
		changeInNetWorthValue.setText(text);
	}
	
	public void setNetWorthValue(BigDecimal value){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"setChangeInNetWorthValue", "Entering method", 
				new Object[] {"Net Worth: " + value});
		
		String text = moneyFormat.format(value);
		netWorthValue.setText(text);
	}
	
	public void setTotalStockValue(BigDecimal value){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"setTotalStockValue", "Entering method", 
				new Object[] {"Total Stock Value: " + value});
		
		String text = moneyFormat.format(value);
		totalStockValue.setText(text);
	}
	
	public void setCashBalanceValue(BigDecimal value){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"setCashBalanceValue", "Entering method", 
				new Object[] {"Cash Balance: " + value});
		
		String text = moneyFormat.format(value);
		cashBalanceValue.setText(text);
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
			detailsButton.setEnabled(false);
			refreshButton.setEnabled(false);
			setPortfolioContents(new ArrayList<>());
		}
		else if(componentsEnabledState == ENABLE_NO_STOCK_LOADED){
			detailsButton.setEnabled(true);
			refreshButton.setEnabled(true);
			setPortfolioContents(new ArrayList<>());
		}
		else if(componentsEnabledState == ENABLE_LOOKUP_STOCK_LOADED){
			
		}
		else if(componentsEnabledState == ENABLE_OWNED_STOCK_LOADED){
			
		}
	}

	@Override
	public Object getValue(String valueToGet) {
		//LOGGER.logp(Level.FINEST, this.getClass().getName(), "getValue", 
				//"Entering method", new Object[] {"Command: " + valueToGet});
		
		
		// TODO Auto-generated method stub
		return null;
	}
	
	private class PortfolioListCellRenderer implements ListCellRenderer<OwnedStock>{

		@Override
		public Component getListCellRendererComponent(
				JList<? extends OwnedStock> list, OwnedStock stock, int index,
				boolean isSelected, boolean cellHasFocus) {
			
			NumberFormat moneyFormat = new DecimalFormat("$###,###,###,##0.00");
			
			//Create panel
			JPanel cellPanel = new JPanel();
			cellPanel.setLayout(new MigLayout());
			cellPanel.setBorder(BorderFactory.createEtchedBorder());
			
			//Get stock values
			Map<String,Object> stockValueMap = stock.getValueMap(false);
			
			//Create components for the cell
			JLabel symbolLabel = createLabel((String) stockValueMap.get(Stock.SYMBOL) + ": ", 
					Fonts.SMALL_LABEL_FONT);
			JLabel nameLabel = createLabel((String) stockValueMap.get(Stock.NAME), 
					Fonts.SMALL_FIELD_FONT);
			
			BigDecimal currentPrice = (BigDecimal) stockValueMap.get(Stock.CURRENT_PRICE);
			JLabel priceLabel = createLabel(LANGUAGE.getString("current_price_label") + ": ", 
					Fonts.SMALL_LABEL_FONT);
			JLabel priceValue = createLabel(moneyFormat.format(currentPrice), Fonts.SMALL_FIELD_FONT);
			
			Integer quantity = (Integer) stockValueMap.get(OwnedStock.QUANTITY_OF_SHARES);
			JLabel quantityLabel = createLabel(LANGUAGE.getString("share_quantity_label") + ": ", 
					Fonts.SMALL_LABEL_FONT);
			JLabel quantityValue = createLabel(quantity.toString(), 
					Fonts.SMALL_FIELD_FONT);
			
			BigDecimal total = (BigDecimal) stockValueMap.get(OwnedStock.TOTAL_VALUE);
			JLabel totalValueLabel = createLabel(LANGUAGE.getString("stock_value_label") + ": ", 
					Fonts.SMALL_LABEL_FONT);
			JLabel totalValue = createLabel(moneyFormat.format(total), 
					Fonts.SMALL_FIELD_FONT);
			
			BigDecimal net = (BigDecimal) stockValueMap.get(OwnedStock.NET);
			JLabel netLabel = createLabel(LANGUAGE.getString("stock_net_label") + ": ", 
					Fonts.SMALL_LABEL_FONT);
			JLabel netValue = createLabel(moneyFormat.format(net), 
					Fonts.SMALL_FIELD_FONT);
			//TODO add +/- and/or icon for increase/decrease in value.
			
			//Assemble the cell
			cellPanel.add(symbolLabel, "split 2");
			cellPanel.add(nameLabel, "gap 0 10");
			cellPanel.add(priceLabel, "split 2");
			cellPanel.add(priceValue, "wrap");
			
			cellPanel.add(quantityLabel, "split 2");
			cellPanel.add(quantityValue, "");
			cellPanel.add(totalValueLabel, "split 2");
			cellPanel.add(totalValue, "wrap");
			cellPanel.add(netLabel, "split 2");
			cellPanel.add(netValue, "");
			
			//Set the colors for selected/not selected
			if(isSelected){
				cellPanel.setBackground(Color.CYAN);
			}
			else{
				cellPanel.setBackground(Color.WHITE);
			}
			
			return cellPanel;
		}
		
		private JLabel createLabel(String text, Font font){
			JLabel label = new JLabel(text);
			label.setFont(font);
			
			return label;
		}
		
	}

}
