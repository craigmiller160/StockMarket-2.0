package io.craigmiller160.stockmarket.gui;

import static io.craigmiller160.stockmarket.controller.StockMarketController.INITIAL_HISTORY_LENGTH_MONTHS;
import static io.craigmiller160.stockmarket.controller.StockMarketController.PORTFOLIO_STATE_PROPERTY;
import static io.craigmiller160.stockmarket.controller.StockMarketController.SELECTED_STOCK_HISTORY_PROPERTY;
import static io.craigmiller160.stockmarket.controller.StockMarketController.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import io.craigmiller160.mvp.listener.AbstractListenerView;
import io.craigmiller160.stockmarket.stock.HistoricalQuote;
import io.craigmiller160.stockmarket.stock.StockHistoryList;
import io.craigmiller160.stockmarket.util.Fonts;
import io.craigmiller160.stockmarket.util.Language;
import net.miginfocom.swing.MigLayout;

/**
 * Utilizes the <tt>JFreeChart</tt> library to display a chart of the 
 * history of a stock's price. Provides a button to change the length of time 
 * that the history is gathered for.
 * <p>
 * <b>THREAD SAFETY:</b> Swing is NOT thread safe.
 * 
 * @author craig
 * @version 2.0
 */
public class StockHistoryPanel extends AbstractListenerView {

	/**
	 * Shared <tt>Language</tt> module for locale-specific text.
	 */
	private static final Language LANGUAGE = Language.getInstance();
	
	/**
	 * The panel created by this class.
	 */
	private final JPanel stockHistoryPanel;
	
	/**
	 * The combo box to select the length of the stock history.
	 */
	private JComboBox<String> historyLengthCombo;
	
	/**
	 * The title label for this panel.
	 */
	private JLabel panelTitleLabel;
	
	/**
	 * Placeholder panel, replaced by <tt>JFreeChart ChartPanel</tt>
	 * whenever a stock history is passed to this panel.
	 */
	private JPanel chartPanel;
	
	/**
	 * Create a new stock history panel.
	 */
	public StockHistoryPanel() {
		super();
		stockHistoryPanel = createStockHistoryPanel();
		initComponents();
		assembleHistoryPanel();
	}
	
	/**
	 * Initialize the components of the panel.
	 */
	private void initComponents(){
		chartPanel = new JPanel();
		
		panelTitleLabel = createLabel(LANGUAGE.getString("stock_history_tab") + ": ", 
				Fonts.LABEL_FONT);
		
		String[] timeInterval = {
				"1 Month", "2 Months",
				"3 Months", "4 Months",
				"5 Months", "6 Months",
				"7 Months", "8 Months",
				"9 Months", "10 Months",
				"11 Months", "12 Months",
				"13 Months", "14 Months",
				"15 Months", "16 Months",
				"17 Months", "18 Months",
				"19 Months", "20 Months",
				"21 Months", "22 Months",
				"23 Months", "24 Months"
		};
		
		historyLengthCombo = createComboBox(timeInterval, Fonts.FIELD_FONT, 
				LANGUAGE.getString("history_combo_tooltip"));
		historyLengthCombo.setSelectedIndex(INITIAL_HISTORY_LENGTH_MONTHS - 1);
	}
	
	/**
	 * Utility method for creating a combo box.
	 * 
	 * @param comboText the text for the intervals of the combo box.
	 * @param font the font of the text of the combo box.
	 * @param toolTipText the text for this component's tooltip.
	 * @return
	 */
	private JComboBox<String> createComboBox(String[] comboText, Font font, 
			String toolTipText){
		JComboBox<String> comboBox = new JComboBox<>(comboText);
		comboBox.setFont(font);
		comboBox.setToolTipText(toolTipText);
		comboBox.addItemListener(new ComboItemListener());
		
		return comboBox;
	}
	
	/**
	 * Utility method for creating a label.
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
	
	/**
	 * Assemble the stock history panel.
	 */
	private void assembleHistoryPanel(){
		stockHistoryPanel.removeAll();
		
		stockHistoryPanel.add(chartPanel, "dock center, grow, push, wrap");
		
		stockHistoryPanel.add(panelTitleLabel, "split 2");
		stockHistoryPanel.add(historyLengthCombo, "wrap");
		
		stockHistoryPanel.revalidate();
		stockHistoryPanel.repaint();
	}
	
	/**
	 * Create the stock history panel.
	 * 
	 * @return the stock history panel.
	 */
	private JPanel createStockHistoryPanel(){
		JPanel stockHistoryPanel = new JPanel();
		stockHistoryPanel.setLayout(new MigLayout());
		stockHistoryPanel.setBackground(Color.WHITE);
		
		return stockHistoryPanel;
	}
	
	/**
	 * Get the panel created by this class.
	 * 
	 * @return the panel created by this class.
	 */
	public JPanel getPanel(){
		return stockHistoryPanel;
	}
	
	public void resetCombo(){
		historyLengthCombo.setSelectedIndex(INITIAL_HISTORY_LENGTH_MONTHS - 1);
	}
	
	/**
	 * Set the stock history to be displayed in the chart.
	 * 
	 * @param historyList the stock history to be displayed.
	 */
	public void setStockHistory(List<HistoricalQuote> historyList){
		String chartName = "";
		if(historyList instanceof StockHistoryList){
			chartName = ((StockHistoryList) historyList).getSymbol();
		}
		else{
			chartName = LANGUAGE.getString("stock_history_tab");
		}
		
		XYDataset dataset = createDataset(historyList);
		chartPanel = createChartPanel(dataset, chartName);
		assembleHistoryPanel();
	}
	
	/**
	 * Create the dataset for the history chart by converting 
	 * a list of stock history values.
	 * 
	 * @param historyList the stock history list to be converted into the dataset.
	 * @return the chart dataset.
	 */
	private XYDataset createDataset(List<HistoricalQuote> historyList){
		TimeSeries timeSeries = new TimeSeries(LANGUAGE.getString("time_series"));
		
		for(int i = (historyList.size() - 1); i >= 0; i--){
			Calendar cal = historyList.get(i).DATE;
			BigDecimal close = historyList.get(i).CLOSE_VALUE;
			timeSeries.add(new Day(
					cal.get(Calendar.DAY_OF_MONTH), 
					(cal.get(Calendar.MONTH) + 1),
					cal.get(Calendar.YEAR)), close);
		}
		
		TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
		timeSeriesCollection.addSeries(timeSeries);
		return timeSeriesCollection;
	}
	
	/**
	 * Create the chart panel, containing a chart based on the dataset.
	 * 
	 * @param dataset the data to use to create the chart.
	 * @param chartName the name to display on the chart.
	 * @return the constructed chart panel. 
	 */
	private JPanel createChartPanel(XYDataset dataset, String chartName){
		JFreeChart chart = createChart(dataset, chartName);
		return new ChartPanel(chart);
	}
	
	/**
	 * Create the chart, based on the dataset provided.
	 * 
	 * @param dataset the data to use to create the chart.
	 * @param chartName the name to display on the chart.
	 * @return the created chart.
	 */
	private JFreeChart createChart(XYDataset dataset, String chartName){
		JFreeChart chart = ChartFactory.createTimeSeriesChart(
				chartName, 
				LANGUAGE.getString("chart_date"), 
				LANGUAGE.getString("chart_price"), 
				dataset, false, true, false); //booleans: legend, tooltips, urls
		
		XYPlot xyplot = (XYPlot) chart.getPlot();
		xyplot.setDomainCrosshairVisible(true);
		xyplot.setRangeCrosshairVisible(true);
		
		return chart;
	}
	
	

	/**
	 * {@inheritDoc}
	 * @throws IllegalArgumentException if the new value from the event is not
	 * the expected type to perform the operation.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void changeProperty(PropertyChangeEvent event) {
		if(event.getPropertyName() == SELECTED_STOCK_PROPERTY){
			resetCombo();
		}
		else if(event.getPropertyName() == SELECTED_STOCK_HISTORY_PROPERTY){
			if(event.getNewValue() instanceof List<?>){
				setStockHistory((List<HistoricalQuote>) event.getNewValue());
			}
			else{
				throw new IllegalArgumentException(
						"Not valid stock history list: " + event.getNewValue());
			}
		}
		else if(event.getPropertyName() == PORTFOLIO_STATE_PROPERTY){
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
			
		}
		else if(portfolioState == PortfolioState.OPEN_NO_STOCK){
			
		}
		else if(portfolioState == PortfolioState.OPEN_STOCK){
			
		}
		else if(portfolioState == PortfolioState.OPEN_OWNED_STOCK){
			
		}
	}

	@Override
	public Object getValueForAction(String actionCommand) {
		Object result = null;
		if(actionCommand == STOCK_HISTORY_INTERVAL_ACTION){
			result = historyLengthCombo.getSelectedIndex() + 1;
		}
		
		return result;
	}
	
	/**
	 * Listener for selections on the history length combo box. It responds
	 * to an item selected state change by wrapping it in an <tt>ActionEvent</tt> and
	 * firing it to external listening controllers.
	 * 
	 * @author craig
	 * @version 2.0
	 */
	private class ComboItemListener implements ItemListener{

		@Override
		public void itemStateChanged(ItemEvent event) {
			if(event.getStateChange() == ItemEvent.SELECTED){
				ActionEvent aEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, 
						STOCK_HISTORY_INTERVAL_ACTION);
				
				StockHistoryPanel.this.actionPerformed(aEvent);
			}
		}
		
	}

}
