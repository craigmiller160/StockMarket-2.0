package io.craigmiller160.stockmarket.gui;

import static io.craigmiller160.stockmarket.controller.StockMarketController.CASH_BALANCE_PROPERTY;
import static io.craigmiller160.stockmarket.controller.StockMarketController.CHANGE_IN_NET_WORTH_PROPERTY;
import static io.craigmiller160.stockmarket.controller.StockMarketController.NET_WORTH_PROPERTY;
import static io.craigmiller160.stockmarket.controller.StockMarketController.PORTFOLIO_STATE_PROPERTY;
import static io.craigmiller160.stockmarket.controller.StockMarketController.REFRESH_PORTFOLIO_ACTION;
import static io.craigmiller160.stockmarket.controller.StockMarketController.STOCK_DETAILS_ACTION;
import static io.craigmiller160.stockmarket.controller.StockMarketController.STOCK_LIST_PROPERTY;
import static io.craigmiller160.stockmarket.controller.StockMarketController.*;
import io.craigmiller160.mvp.listener.AbstractListenerView;
import io.craigmiller160.stockmarket.stock.OwnedStock;
import io.craigmiller160.stockmarket.stock.Stock;
import io.craigmiller160.stockmarket.util.Fonts;
import io.craigmiller160.stockmarket.util.Language;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
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
	
	/**
	 * Format for amounts of money to be displayed in the GUI.
	 */
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
		ownedStockList.setVisibleRowCount(3);
		ownedStockList.addMouseListener(new PortfolioMouseListener());
		
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
		
		cashBalanceValue = createLabel("$0.00", Fonts.FIELD_FONT, 
				LANGUAGE.getString("cash_balance_tooltip"));
		
		totalStockValue = createLabel("$0.00", Fonts.FIELD_FONT,
				LANGUAGE.getString("total_stock_value_tooltip"));
		
		netWorthValue = createLabel("$0.00", Fonts.FIELD_FONT,
				LANGUAGE.getString("net_worth_tooltip"));
		
		changeInNetWorthValue = createLabel("$0.00", Fonts.FIELD_FONT,
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
		
		summaryPanel.add(portfolioSummaryLabel, "span 2, center, pushx, growx, dock north");
		
		summaryPanel.add(cashBalanceLabel, "gap 10 0 10");
		summaryPanel.add(cashBalanceValue, "align right, pushx, gap 0 10, wrap");
		
		summaryPanel.add(totalStockValueLabel, "gap 10 0 10");
		summaryPanel.add(totalStockValue, "align right, pushx, gap 0 10, wrap");
		
		summaryPanel.add(netWorthLabel, "gap 10 0 10");
		summaryPanel.add(netWorthValue, "align right, pushx, wrap, gap 0 10");
		
		summaryPanel.add(changeInNetWorthLabel, "gap 10 0 10 10");
		summaryPanel.add(changeInNetWorthValue, "align right, pushx, wrap, gap 0 10");
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
	
	/**
	 * Sets a list of stocks to be displayed in the portfolio <tt>JList</tt>
	 * object.
	 * 
	 * @param stockList the list of stocks to be displayed.
	 */
	@SuppressWarnings("serial") //This anonymous ListModel doesn't need a serialVersionUID.
	public void setPortfolioContents(final List<OwnedStock> stockList){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"setPortfolioContents", "Entering method", 
				new Object[]{"Stock List: " + stockList});
		
		if(stockList == null){
			ownedStockList.setModel(new DefaultListModel<OwnedStock>());
		}
		else{
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
	}

	/**
	 * {@inheritDoc}
	 * @throws IllegalArgumentException if the new value from the event is not
	 * the expected type to perform the operation.
	 */
	@SuppressWarnings("unchecked")
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
						"Not instance of PortfolioState: " + event.getNewValue());
			}
		}
		else if(event.getPropertyName() == STOCK_LIST_PROPERTY){
			LOGGER.logp(Level.FINEST, this.getClass().getName(), 
					"changeProperty", "Changing Property", 
					new Object[]{"Property: " + event.getPropertyName()});
			
			if(event.getNewValue() instanceof List<?> || event.getNewValue() == null){
				setPortfolioContents((List<OwnedStock>) event.getNewValue());
			}
			else{
				throw new IllegalArgumentException(
						"Not valid stock list value: " + event.getNewValue());
			}
		}
		else if(event.getPropertyName() == CASH_BALANCE_PROPERTY){
			LOGGER.logp(Level.FINEST, this.getClass().getName(), 
					"changeProperty", "Changing Property", 
					new Object[]{"Property: " + event.getPropertyName()});
			
			if(event.getNewValue() instanceof BigDecimal){
				setCashBalanceValue((BigDecimal) event.getNewValue());
			}
			else{
				throw new IllegalArgumentException(
						"Not instance of Big Decimal " + event.getNewValue());
			}
		}
		else if(event.getPropertyName() == TOTAL_STOCK_VALUE_PROPERTY){
			LOGGER.logp(Level.FINEST, this.getClass().getName(), 
					"changeProperty", "Changing Property", 
					new Object[]{"Property: " + event.getPropertyName()});
			
			if(event.getNewValue() instanceof BigDecimal){
				setTotalStockValue((BigDecimal) event.getNewValue());
			}
			else{
				throw new IllegalArgumentException(
						"Not instance of Big Decimal " + event.getNewValue());
			}
		}
		else if(event.getPropertyName() == NET_WORTH_PROPERTY){
			LOGGER.logp(Level.FINEST, this.getClass().getName(), 
					"changeProperty", "Changing Property", 
					new Object[]{"Property: " + event.getPropertyName()});
			
			if(event.getNewValue() instanceof BigDecimal){
				setNetWorthValue((BigDecimal) event.getNewValue());
			}
			else{
				throw new IllegalArgumentException(
						"Not instance of Big Decimal " + event.getNewValue());
			}
		}
		else if(event.getPropertyName() == CHANGE_IN_NET_WORTH_PROPERTY){
			LOGGER.logp(Level.FINEST, this.getClass().getName(), 
					"changeProperty", "Changing Property", 
					new Object[]{"Property: " + event.getPropertyName()});
			
			if(event.getNewValue() instanceof BigDecimal){
				setChangeInNetWorthValue((BigDecimal) event.getNewValue());
			}
			else{
				throw new IllegalArgumentException(
						"Not instance of Big Decimal " + event.getNewValue());
			}
		}
	}
	
	/**
	 * Sets the label for the change in net worth value.
	 * 
	 * @param value the change in net worth.
	 */
	public void setChangeInNetWorthValue(BigDecimal value){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"setChangeInNetWorthValue", "Entering method", 
				new Object[] {"Net Worth Change: " + value});
		
		String text = moneyFormat.format(value);
		changeInNetWorthValue.setText(text);
	}
	
	/**
	 * Sets the label for the net worth value.
	 * 
	 * @param value the net worth value.
	 */
	public void setNetWorthValue(BigDecimal value){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"setChangeInNetWorthValue", "Entering method", 
				new Object[] {"Net Worth: " + value});
		
		String text = moneyFormat.format(value);
		netWorthValue.setText(text);
	}
	
	/**
	 * Sets the label for the total stock value label.
	 * 
	 * @param value the total stock value label.
	 */
	public void setTotalStockValue(BigDecimal value){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"setTotalStockValue", "Entering method", 
				new Object[] {"Total Stock Value: " + value});
		
		String text = moneyFormat.format(value);
		totalStockValue.setText(text);
	}
	
	/**
	 * Sets the label for the cash balance value.
	 * 
	 * @param value the cash balance value.
	 */
	public void setCashBalanceValue(BigDecimal value){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"setCashBalanceValue", "Entering method", 
				new Object[] {"Cash Balance: " + value});
		
		String text = moneyFormat.format(value);
		cashBalanceValue.setText(text);
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
			detailsButton.setEnabled(false);
			refreshButton.setEnabled(false);
		}
		else if(portfolioState == PortfolioState.OPEN_NO_STOCK){
			detailsButton.setEnabled(true);
			refreshButton.setEnabled(true);
		}
		else if(portfolioState == PortfolioState.OPEN_STOCK){
			detailsButton.setEnabled(true);
			refreshButton.setEnabled(true);
		}
		else if(portfolioState == PortfolioState.OPEN_OWNED_STOCK){
			detailsButton.setEnabled(true);
			refreshButton.setEnabled(true);
		}
	}

	@Override
	public Object getValueForAction(String actionCommand) {
		Object result = null;
		if(actionCommand == LOOKUP_PORTFOLIO_STOCK_ACTION){
			LOGGER.logp(Level.FINEST, this.getClass().getName(), "getValue", 
					"Entering method", new Object[] {"Command: " + actionCommand});
			
			result = ownedStockList.getSelectedIndex();
		}
		
		return result;
	}
	
	/**
	 * The mouse listener for the portfolio list, to register
	 * double-click selections.
	 * 
	 * @author craig
	 * @version 2.0
	 */
	private class PortfolioMouseListener extends MouseAdapter{
		
		@Override
		public void mouseClicked(MouseEvent event){
			if(event.getClickCount() == 2){
				ActionEvent newEvent = new ActionEvent(
						PortfolioPanel.this,
						ActionEvent.ACTION_PERFORMED, 
						LOOKUP_PORTFOLIO_STOCK_ACTION);
				
				PortfolioPanel.this.actionPerformed(newEvent);
			}
		}
		
	}
	
	/**
	 * The cell renderer for the list displaying the stocks owned
	 * in the user's portfolio.
	 * 
	 * @author craig
	 * @version 2.0
	 */
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
			JLabel priceLabel = createLabel(LANGUAGE.getString("share_price") + ": ", 
					Fonts.SMALL_LABEL_FONT);
			JLabel priceValue = createLabel(moneyFormat.format(currentPrice), Fonts.SMALL_FIELD_FONT);
			
			Integer quantity = (Integer) stockValueMap.get(OwnedStock.QUANTITY_OF_SHARES);
			JLabel quantityLabel = createLabel(LANGUAGE.getString("share_quantity_label") + ": ", 
					Fonts.SMALL_LABEL_FONT);
			JLabel quantityValue = createLabel(quantity.toString(), 
					Fonts.SMALL_FIELD_FONT);
			
			BigDecimal total = (BigDecimal) stockValueMap.get(OwnedStock.TOTAL_VALUE);
			JLabel totalValueLabel = createLabel(LANGUAGE.getString("value_label") + ": ", 
					Fonts.SMALL_LABEL_FONT);
			JLabel totalValue = createLabel(moneyFormat.format(total), 
					Fonts.SMALL_FIELD_FONT);
			
			BigDecimal net = (BigDecimal) stockValueMap.get(OwnedStock.NET);
			JLabel netLabel = createLabel(LANGUAGE.getString("stock_net_label") + ": ", 
					Fonts.SMALL_LABEL_FONT);
			JLabel netValue = createLabel(moneyFormat.format(net), 
					Fonts.SMALL_FIELD_FONT);
			
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
		
		/**
		 * Utility class for creating a <tt>JLabel</tt>.
		 * 
		 * @param text the label's text.
		 * @param font the label's font.
		 * @return the created label.
		 */
		private JLabel createLabel(String text, Font font){
			JLabel label = new JLabel(text);
			label.setFont(font);
			
			return label;
		}
		
	}

}
