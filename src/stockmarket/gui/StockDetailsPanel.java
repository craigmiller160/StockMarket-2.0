package stockmarket.gui;

import static stockmarket.controller.StockMarketController.ENABLE_LOOKUP_STOCK_LOADED;
import static stockmarket.controller.StockMarketController.ENABLE_NO_PORTFOLIO_OPEN;
import static stockmarket.controller.StockMarketController.ENABLE_NO_STOCK_LOADED;
import static stockmarket.controller.StockMarketController.ENABLE_OWNED_STOCK_LOADED;
import static stockmarket.controller.StockMarketController.SELECTED_STOCK_PROPERTY;
import static stockmarket.stock.Stock.*;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import mvp.listener.AbstractListenerView;
import net.jcip.annotations.NotThreadSafe;
import net.miginfocom.swing.MigLayout;
import stockmarket.stock.OwnedStock;
import stockmarket.stock.Stock;
import stockmarket.util.Fonts;
import stockmarket.util.Language;

/**
 * GUI component that defines the panel that displays the detailed
 * information about a stock.
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
public class StockDetailsPanel extends AbstractListenerView {

	/**
	 * Shared logger for the program.
	 */
	private static final Logger LOGGER = Logger.getLogger("stockmarket.gui.StockDetailsPanel");
	
	/**
	 * Format for displaying amounts of money in the GUI.
	 */
	private NumberFormat moneyFormat = new DecimalFormat("$###,###,###,##0.00");
	
	/**
	 * Format for displaying percentages in the GUI.
	 */
	private NumberFormat percentFormat = new DecimalFormat("###,###,###,##0.00%");
	
	/**
	 * Format for displaying date values in the GUI.
	 */
	private DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	
	/**
	 * Format for displaying time values in the GUI.
	 */
	private DateFormat timeFormat = new SimpleDateFormat("hh:mmaa");
	
	/**
	 * The panel created by this class.
	 */
	private final JPanel stockDetailsPanel;
	
	/**
	 * The scroll pane for the panel created by this class.
	 */
	private final JScrollPane stockDetailsScrollPane;
	
	/**
	 * Shared <tt>Language</tt> module for locale-specific text.
	 */
	private static final Language LANGUAGE = Language.getInstance();
	
	/**
	 * The constant name of the not owned panel for the owned panel switcher.
	 */
	private static final String NOT_OWNED_PANEL = "NotOwnedPanel";
	
	/**
	 * The constant name of the owned panel for the owner panel switcher.
	 */
	private static final String OWNED_PANEL = "OwnedPanel";
	
	/**
	 * The stock symbol label.
	 */
	private JLabel symbolLabel;
	
	/**
	 * The company name label.
	 */
	private JLabel nameLabel;
	
	/**
	 * The current price label.
	 */
	private JLabel currentPriceLabel;
	
	/**
	 * The current price value.
	 */
	private JLabel currentPriceValue;
	
	/**
	 * The not owned panel. Displayed when the stock being shown
	 * is not owned in the portfolio.
	 */
	private JPanel notOwnedPanel;
	
	/**
	 * The owned panel. Displayed when the stock being shown
	 * is owned in the portfolio to show additional stats.
	 */
	private JPanel ownedPanel;
	
	/**
	 * The panel that switches between displaying the owned and not owned panels. 
	 */
	private JPanel ownershipSwitchPanel;
	
	/**
	 * The layout for the ownership switch panel.
	 */
	private CardLayout ownershipSwitchLayout;
	
	/**
	 * The label for if the stock isn't owned.
	 */
	private JLabel notOwnedLabel;
	
	/**
	 * The share quantity label.
	 */
	private JLabel shareQuantityLabel;
	
	/**
	 * The share quantity value.
	 */
	private JLabel shareQuantityValue;
	
	/**
	 * the total value label.
	 */
	private JLabel totalValueLabel;
	
	/**
	 * The total value... value.
	 */
	private JLabel totalValue;
	
	/**
	 * The net label.
	 */
	private JLabel netLabel;
	
	/**
	 * The net value.
	 */
	private JLabel netValue;
	
	/**
	 * The last trade date label.
	 */
	private JLabel lastTradeDateLabel;
	
	/**
	 * The last trade date value.
	 */
	private JLabel lastTradeDateValue;
	
	/**
	 * The last trade time label.
	 */
	private JLabel lastTradeTimeLabel;
	
	/**
	 * The last trade time value.
	 */
	private JLabel lastTradeTimeValue;
	
	/**
	 * The day's change label.
	 */
	private JLabel dayChangeLabel;
	
	/**
	 * The day's change value.
	 */
	private JLabel dayChangeValue;
	
	/**
	 * The year high label.
	 */
	private JLabel yearHighLabel;
	
	/**
	 * The year high value.
	 */
	private JLabel yearHighValue;
	
	/**
	 * The change in year high label.
	 */
	private JLabel changeYearHighLabel;
	
	/**
	 * The change in year high value.
	 */
	private JLabel changeYearHighValue;
	
	/**
	 * The year low label.
	 */
	private JLabel yearLowLabel;
	
	/**
	 * The year low value.
	 */
	private JLabel yearLowValue;
	
	/**
	 * The change in year low label.
	 */
	private JLabel changeYearLowLabel;
	
	/**
	 * The change in year low value.
	 */
	private JLabel changeYearLowValue;
	
	/**
	 * The fifty day average label.
	 */
	private JLabel fiftyDayAvgLabel;
	
	/**
	 * The fifty day value label.
	 */
	private JLabel fiftyDayAvgValue;
	
	/**
	 * The change in 50 day average label.
	 */
	private JLabel change50DayAvgLabel;
	
	/**
	 * The change in 50 day average value.
	 */
	private JLabel change50DayAvgValue;
	
	/**
	 * The two hundred day average label.
	 */
	private JLabel twoHundredDayAvgLabel;
	
	/**
	 * The two hundred day average value.
	 */
	private JLabel twoHundredDayAvgValue;
	
	/**
	 * The change in two hundred day average label.
	 */
	private JLabel change200DayAvgLabel;
	
	/**
	 * The change in two hundred day average value.
	 */
	private JLabel change200DayAvgValue;
	
	/**
	 * Create the panel.
	 */
	public StockDetailsPanel() {
		super();
		stockDetailsPanel = createStockDetailsPanel();
		stockDetailsScrollPane = createStockDetailsScrollPane(stockDetailsPanel);
		initComponents();
		assembleStockDetailsPanel();
	}
	
	/**
	 * Create the <tt>JPanel</tt> that this class wraps around.
	 * 
	 * @return the created <tt>JPanel</tt>.
	 */
	private JPanel createStockDetailsPanel(){
		JPanel stockDetailsPanel = new JPanel();
		stockDetailsPanel.setLayout(new MigLayout("center"));
		stockDetailsPanel.setBackground(Color.WHITE);
		
		return stockDetailsPanel;
	}
	
	/** 
	 * Create the <tt>JScrollPane</tt> for the panel created by this class
	 * to reside in.
	 * 
	 * @return the created <tt>JScrollPane</tt>.
	 */
	private JScrollPane createStockDetailsScrollPane(JPanel stockDetailsPanel){
		JScrollPane scrollPane = new JScrollPane(stockDetailsPanel);
		scrollPane.getVerticalScrollBar().setUnitIncrement(15);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(15);
		
		return scrollPane;
	}
	
	//TODO consider changing the change labels to "Change" instead of "Change From"
	//doesn't look right
	
	/** 
	 * Create the <tt>JScrollPane</tt> for the panel created by this class
	 * to reside in.
	 * 
	 * @return the created <tt>JScrollPane</tt>.
	 */
	private void initComponents(){
		//TODO remove all the text from the labels/fields here
		//after testing
		symbolLabel = createLabel("", Fonts.GIANT_LABEL_FONT, 
				LANGUAGE.getString("stock_symbol_tooltip")); //TODO remove text
		nameLabel = createLabel("", Fonts.GIANT_LABEL_FONT, 
				LANGUAGE.getString("stock_name_tooltip")); //TODO remove text
		currentPriceLabel = createLabel(LANGUAGE.getString("current_price_label") + ": ",
				Fonts.BIG_LABEL_FONT, LANGUAGE.getString("current_price_tooltip"));
		currentPriceValue = createLabel("$0.00", Fonts.BIG_FIELD_FONT, 
				LANGUAGE.getString("current_price_tooltip")); //TODO remove text
		
		notOwnedPanel = new JPanel();
		notOwnedPanel.setLayout(new MigLayout());
		notOwnedPanel.setBackground(Color.WHITE);
		
		ownedPanel = new JPanel();
		ownedPanel.setLayout(new MigLayout());
		ownedPanel.setBackground(Color.WHITE);
		
		ownershipSwitchLayout = new CardLayout();
		ownershipSwitchPanel = new JPanel();
		ownershipSwitchPanel.setLayout(ownershipSwitchLayout);
		
		notOwnedLabel = createLabel(LANGUAGE.getString("not_owned_label"), 
				Fonts.BIG_LABEL_FONT, null);
		
		shareQuantityLabel = createLabel(LANGUAGE.getString("share_quantity_label") + ": ", 
				Fonts.BIG_LABEL_FONT, LANGUAGE.getString("share_quantity_tooltip"));
		
		shareQuantityValue = createLabel("", Fonts.BIG_FIELD_FONT, 
				LANGUAGE.getString("share_quantity_tooltip")); //TODO remove text
		
		totalValueLabel = createLabel(LANGUAGE.getString("stock_value_label") + ": ", 
				Fonts.BIG_LABEL_FONT, LANGUAGE.getString("stock_value_tooltip"));
		
		totalValue = createLabel("", Fonts.BIG_FIELD_FONT, 
				LANGUAGE.getString("stock_value_tooltip")); //TODO remove text
		
		netLabel = createLabel(LANGUAGE.getString("stock_net_label") + ": ", 
				Fonts.BIG_LABEL_FONT, LANGUAGE.getString("stock_net_tooltip"));
		
		netValue = createLabel("", Fonts.BIG_FIELD_FONT, 
				LANGUAGE.getString("stock_net_tooltip")); //TODO remove text
		
		lastTradeDateLabel = createLabel(LANGUAGE.getString("last_trade_date_label") + ": ", 
				Fonts.BIG_LABEL_FONT, LANGUAGE.getString("last_trade_date_tooltip"));
		
		lastTradeDateValue = createLabel("", Fonts.BIG_FIELD_FONT, 
				LANGUAGE.getString("last_trade_date_tooltip")); //TODO remove text
		
		lastTradeTimeLabel = createLabel(LANGUAGE.getString("last_trade_time_label") + ": ", 
				Fonts.BIG_LABEL_FONT, LANGUAGE.getString("last_trade_time_tooltip"));
		
		lastTradeTimeValue = createLabel("", Fonts.BIG_FIELD_FONT, 
				LANGUAGE.getString("last_trade_time_tooltip")); //TODO remove text
		
		dayChangeLabel = createLabel(LANGUAGE.getString("day_change_label") + ": ", 
				Fonts.BIG_LABEL_FONT, LANGUAGE.getString("day_change_tooltip"));
		
		dayChangeValue = createLabel("", Fonts.BIG_FIELD_FONT, 
				LANGUAGE.getString("day_change_tooltip")); //TODO remove text
		
		yearHighLabel = createLabel(LANGUAGE.getString("year_high_label") + ": ", 
				Fonts.BIG_LABEL_FONT, LANGUAGE.getString("year_high_tooltip"));
		
		yearHighValue = createLabel("", Fonts.BIG_FIELD_FONT, 
				LANGUAGE.getString("year_high_tooltip")); //TODO remove text
		
		changeYearHighLabel = createLabel(LANGUAGE.getString("change_from_label") + ": ", 
				Fonts.BIG_LABEL_FONT, LANGUAGE.getString("change_year_high_tooltip"));
		
		changeYearHighValue = createLabel("", Fonts.BIG_FIELD_FONT, 
				LANGUAGE.getString("change_year_high_tooltip")); //TODO remove text
		
		yearLowLabel = createLabel(LANGUAGE.getString("year_low_label") + ": ", 
				Fonts.BIG_LABEL_FONT, LANGUAGE.getString("year_low_tooltip"));
		
		yearLowValue = createLabel("", Fonts.BIG_FIELD_FONT, 
				LANGUAGE.getString("year_low_tooltip")); //TODO remove text
		
		changeYearLowLabel = createLabel(LANGUAGE.getString("change_from_label") + ": ", 
				Fonts.BIG_LABEL_FONT, LANGUAGE.getString("change_year_low_tooltip"));
		
		changeYearLowValue = createLabel("", Fonts.BIG_FIELD_FONT, 
				LANGUAGE.getString("change_year_low_tooltip")); //TODO remove text
		
		fiftyDayAvgLabel = createLabel(LANGUAGE.getString("fifty_day_avg_label") + ": ", 
				Fonts.BIG_LABEL_FONT, LANGUAGE.getString("fifty_day_avg_tooltip"));
		
		fiftyDayAvgValue = createLabel("", Fonts.BIG_FIELD_FONT, 
				LANGUAGE.getString("fifty_day_avg_tooltip")); //TODO remove text
		
		change50DayAvgLabel = createLabel(LANGUAGE.getString("change_from_label") + ": ", 
				Fonts.BIG_LABEL_FONT, LANGUAGE.getString("change_50_avg_tooltip"));
		
		change50DayAvgValue = createLabel("", Fonts.BIG_FIELD_FONT,
				LANGUAGE.getString("change_50_avg_tooltip")); //TODO remove text
		
		twoHundredDayAvgLabel = createLabel(LANGUAGE.getString("two_hundred_day_avg_label") + ": ",
				Fonts.BIG_LABEL_FONT, LANGUAGE.getString("two_hundred_day_avg_tooltip"));
		
		twoHundredDayAvgValue = createLabel("", Fonts.BIG_FIELD_FONT, 
				LANGUAGE.getString("two_hundred_day_avg_tooltip")); //TODO remove text
		
		change200DayAvgLabel = createLabel(LANGUAGE.getString("change_from_label") + ": ",
				Fonts.BIG_LABEL_FONT, LANGUAGE.getString("change_200_avg_tooltip"));
		
		change200DayAvgValue = createLabel("", Fonts.BIG_FIELD_FONT, 
				LANGUAGE.getString("change_200_avg_tooltip")); //TODO remove text
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
	
	private void assembleStockDetailsPanel(){
		stockDetailsPanel.add(symbolLabel, "center, split 2, gap 0 0 20, pushx");
		stockDetailsPanel.add(nameLabel, "gap 0 0 20, pushx, wrap");
		stockDetailsPanel.add(currentPriceLabel, "center, split 2, gap 0 0 20, pushx");
		stockDetailsPanel.add(currentPriceValue, "pushx, wrap");
		
		notOwnedPanel.add(notOwnedLabel, "center, pushx");
		ownershipSwitchPanel.add(notOwnedPanel, NOT_OWNED_PANEL);
		
		ownedPanel.add(shareQuantityLabel, "gap 0");
		ownedPanel.add(shareQuantityValue, "");
		ownedPanel.add(totalValueLabel, "gap 20");
		ownedPanel.add(totalValue, "");
		ownedPanel.add(netLabel, "gap 20");
		ownedPanel.add(netValue, "");
		ownershipSwitchPanel.add(ownedPanel, OWNED_PANEL);
		
		stockDetailsPanel.add(ownershipSwitchPanel, "center, pushx, gap 0 0 20, span 2, wrap");
		
		stockDetailsPanel.add(lastTradeDateLabel, "center, split 4, pushx");
		stockDetailsPanel.add(lastTradeDateValue, "center, pushx");
		stockDetailsPanel.add(lastTradeTimeLabel, "center, pushx, gap 20 0 20");
		stockDetailsPanel.add(lastTradeTimeValue, "pushx, center, wrap");
		
		stockDetailsPanel.add(dayChangeLabel, "center, split 2, pushx, gap 20 0 20");
		stockDetailsPanel.add(dayChangeValue, "center, pushx, wrap");
		
		stockDetailsPanel.add(yearHighLabel, "center, split 4, pushx, gap 0 0 20");
		stockDetailsPanel.add(yearHighValue, "center, pushx");
		stockDetailsPanel.add(changeYearHighLabel, "center, pushx, gap 20 0 20");
		stockDetailsPanel.add(changeYearHighValue, "center, pushx, wrap");
		
		stockDetailsPanel.add(yearLowLabel, "center, split 4, pushx, gap 0 0 20");
		stockDetailsPanel.add(yearLowValue, "center, pushx");
		stockDetailsPanel.add(changeYearLowLabel, "center, pushx, gap 20 0 20");
		stockDetailsPanel.add(changeYearLowValue, "center, pushx, wrap");
		
		stockDetailsPanel.add(fiftyDayAvgLabel, "center, split 4, pushx, gap 0 0 20");
		stockDetailsPanel.add(fiftyDayAvgValue, "center, pushx");
		stockDetailsPanel.add(change50DayAvgLabel, "center, pushx, gap 20 0 20");
		stockDetailsPanel.add(change50DayAvgValue, "center, pushx, wrap");
		
		stockDetailsPanel.add(twoHundredDayAvgLabel, "center, split 4, pushx, gap 0 0 20");
		stockDetailsPanel.add(twoHundredDayAvgValue, "center, pushx");
		stockDetailsPanel.add(change200DayAvgLabel, "center, pushx, gap 20 0 20");
		stockDetailsPanel.add(change200DayAvgValue, "center, pushx, wrap");
	}
	
	/**
	 * Return the panel created by this class.
	 * 
	 * @return the panel created by this class.
	 */
	public JScrollPane getPanel(){
		return stockDetailsScrollPane;
	}

	/**
	 * {@inheritDoc}
	 * @throws IllegalArgumentException if the new value from the event is not
	 * the expected type to perform the operation.
	 */
	@Override
	public void changeProperty(PropertyChangeEvent event) {
		if(event.getPropertyName() == SELECTED_STOCK_PROPERTY){
			LOGGER.logp(Level.FINEST, this.getClass().getName(), 
					"changeProperty", "Changing Property",
					new Object[] {"Property: " + event.getPropertyName()});
			
			displayStockDetails((Stock) event.getNewValue());
		}
	}
	
	public void displayStockDetails(Stock stock){
		boolean owned = stock instanceof OwnedStock ? true : false;
		
		LOGGER.logp(Level.INFO, this.getClass().getName(), 
				"displayStockDetails", "Displaying Stock Details. Owned: " + owned,
				new Object[] {"Stock: " + stock});
		
		
		//TODO special response needed for OwnedStock
		
		Map<String,Object> valueMap = stock.getValueMap(true);
		
		String symbol = (String) valueMap.get(SYMBOL);
		setStockSymbol(symbol);
		
		String name = (String) valueMap.get(NAME);
		setStockName(name);
		
		BigDecimal currentPrice = (BigDecimal) valueMap.get(CURRENT_PRICE);
		setCurrentPrice(currentPrice);
		
		Calendar lastTradeDate = (Calendar) valueMap.get(LAST_TRADE_DATE);
		setLastTradeDate(lastTradeDate);
		
		Calendar lastTradeTime = (Calendar) valueMap.get(LAST_TRADE_TIME);
		setLastTradeTime(lastTradeTime);
		
		BigDecimal changeToday = (BigDecimal) valueMap.get(CHANGE_TODAY);
		BigDecimal changeTodayPercent = (BigDecimal) valueMap.get(CHANGE_TODAY_PERCENT);
		setChangeToday(changeToday, changeTodayPercent);
		
		BigDecimal yearHigh = (BigDecimal) valueMap.get(YEAR_HIGH);
		setYearHigh(yearHigh);
		
		BigDecimal changeYearHigh = (BigDecimal) valueMap.get(CHANGE_YEAR_HIGH);
		BigDecimal changeYearHighPercent = (BigDecimal) valueMap.get(CHANGE_YEAR_HIGH_PERCENT);
		setChangeYearHigh(changeYearHigh, changeYearHighPercent);
		
		BigDecimal yearLow = (BigDecimal) valueMap.get(YEAR_LOW);
		setYearLow(yearLow);
		
		BigDecimal changeYearLow = (BigDecimal) valueMap.get(CHANGE_YEAR_LOW);
		BigDecimal changeYearLowPercent = (BigDecimal) valueMap.get(CHANGE_YEAR_LOW_PERCENT);
		setChangeYearLow(changeYearLow, changeYearLowPercent);
		
		BigDecimal fiftyDayAvg = (BigDecimal) valueMap.get(FIFTY_DAY_AVG);
		set50DayAvg(fiftyDayAvg);
		
		BigDecimal change50DayAvg = (BigDecimal) valueMap.get(CHANGE_50_DAY_AVG);
		BigDecimal change50DayAvgPercent = (BigDecimal) valueMap.get(CHANGE_50_DAY_AVG_PERCENT);
		setChange50DayAvg(change50DayAvg, change50DayAvgPercent);
		
		BigDecimal twoHundredDayAvg = (BigDecimal) valueMap.get(TWO_HUNDRED_DAY_AVG);
		set200DayAvg(twoHundredDayAvg);
		
		BigDecimal change200DayAvg = (BigDecimal) valueMap.get(CHANGE_200_DAY_AVG);
		BigDecimal change200DayAvgPercent = (BigDecimal) valueMap.get(CHANGE_200_DAY_AVG_PERCENT);
		setChange200DayAvg(change200DayAvg, change200DayAvgPercent);
		
	}
	
	public void setStockSymbol(String symbol){
		symbolLabel.setText(symbol + ": ");
	}
	
	public void setStockName(String name){
		nameLabel.setText(name);
	}
	
	public void setCurrentPrice(BigDecimal currentPrice){
		currentPriceValue.setText(moneyFormat.format(currentPrice));
	}
	
	public void setLastTradeDate(Calendar lastTradeDate){
		lastTradeDateValue.setText(dateFormat.format(lastTradeDate.getTime()));
	}
	
	public void setLastTradeTime(Calendar lastTradeTime){
		lastTradeTimeValue.setText(timeFormat.format(lastTradeTime.getTime()));
	}
	
	public void setChangeToday(BigDecimal changeToday, BigDecimal changeTodayPercent){
		//TODO make sure that +/- or icon is displayed
		
		dayChangeValue.setText(
				moneyFormat.format(changeToday)
				+ " (" + percentFormat.format(changeTodayPercent.divide(new BigDecimal(100))) + ")");
	}
	
	public void setYearHigh(BigDecimal yearHigh){
		yearHighValue.setText(moneyFormat.format(yearHigh));
	}
	
	public void setChangeYearHigh(BigDecimal changeYearHigh, BigDecimal changeYearHighPercent){
		//TODO make sure that +/- or icon is displayed
		
		changeYearHighValue.setText(
				moneyFormat.format(changeYearHigh)
				+ " (" + percentFormat.format(changeYearHighPercent.divide(new BigDecimal(100))) + ")");
	}
	
	public void setYearLow(BigDecimal yearLow){
		yearLowValue.setText(moneyFormat.format(yearLow));
	}
	
	public void setChangeYearLow(BigDecimal changeYearLow, BigDecimal changeYearLowPercent){
		//TODO make sure that +/- or icon is displayed
		
		changeYearLowValue.setText(
				moneyFormat.format(changeYearLow)
				+ " (" + percentFormat.format(changeYearLowPercent.divide(new BigDecimal(100))) + ")");
	}
	
	public void set50DayAvg(BigDecimal fiftyDayAvg){
		fiftyDayAvgValue.setText(moneyFormat.format(fiftyDayAvg));
	}
	
	public void setChange50DayAvg(BigDecimal change50DayAvg, BigDecimal change50DayAvgPercent){
		//TODO make sure that +/- or icon is displayed
		
		change50DayAvgValue.setText(
				moneyFormat.format(change50DayAvg)
				+ " (" + percentFormat.format(change50DayAvgPercent.divide(new BigDecimal(100))) + ")");
	}
	
	public void set200DayAvg(BigDecimal twoHundredDayAvg){
		twoHundredDayAvgValue.setText(moneyFormat.format(twoHundredDayAvg));
	}
	
	public void setChange200DayAvg(BigDecimal change200DayAvg, BigDecimal change200DayAvgPercent){
		//TODO make sure that +/- or icon is displayed
		
		change200DayAvgValue.setText(
				moneyFormat.format(change200DayAvg)
				+ " (" + percentFormat.format(change200DayAvgPercent.divide(new BigDecimal(100))) + ")");
	}
	
	/**
	 * Change which components are enabled in this class.
	 * 
	 * @param componentsEnabledState the state of the program to enable
	 * components for.
	 */
	public void changeComponentsEnabled(int componentsEnabledState){
		if(componentsEnabledState == ENABLE_NO_PORTFOLIO_OPEN){
			
		}
		else if(componentsEnabledState == ENABLE_NO_STOCK_LOADED){
			
		}
		else if(componentsEnabledState == ENABLE_LOOKUP_STOCK_LOADED){
			
		}
		else if(componentsEnabledState == ENABLE_OWNED_STOCK_LOADED){
			
		}
	}

	@Override
	public Object getValueForAction(String actionCommand) {
		//LOGGER.logp(Level.FINEST, this.getClass().getName(), "getValue", 
				//"Entering method", new Object[] {"Command: " + valueToGet});
		
		
		// TODO Will be filled out if values are needed from this view
		return null;
	}

}
