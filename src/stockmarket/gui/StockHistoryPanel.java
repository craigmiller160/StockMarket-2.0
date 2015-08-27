package stockmarket.gui;

import static stockmarket.controller.StockMarketController.ENABLE_LOOKUP_STOCK_LOADED;
import static stockmarket.controller.StockMarketController.ENABLE_NO_PORTFOLIO_OPEN;
import static stockmarket.controller.StockMarketController.ENABLE_NO_STOCK_LOADED;
import static stockmarket.controller.StockMarketController.ENABLE_OWNED_STOCK_LOADED;
import static stockmarket.controller.StockMarketController.INITIAL_HISTORY_LENGTH_MONTHS;
import static stockmarket.controller.StockMarketController.SELECTED_STOCK_HISTORY_PROPERTY;
import static stockmarket.controller.StockMarketController.STOCK_HISTORY_INTERVAL_ACTION;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import stockmarket.stock.HistoricalQuote;
import stockmarket.stock.StockHistoryList;
import stockmarket.util.Fonts;
import stockmarket.util.Language;

public class StockHistoryPanel extends AbstractListenerView {

	private static final Language LANGUAGE = Language.getInstance();
	
	private static final Logger LOGGER = Logger.getLogger("stockmarket.gui.StockHistoryPanel");
	
	private final JPanel stockHistoryPanel;
	
	private JComboBox<String> historyLengthCombo;
	
	private JLabel panelTitleLabel;
	
	/**
	 * Placeholder panel, replaced by <tt>JFreeChart ChartPanel</tt>
	 * whenever a stock history is passed to this panel.
	 */
	private JPanel chartPanel;
	
	public StockHistoryPanel() {
		super();
		stockHistoryPanel = createStockHistoryPanel();
		initComponents();
		assembleHistoryPanel();
	}
	
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
		
		historyLengthCombo = createComboBox(timeInterval, Fonts.FIELD_FONT);
	}
	
	private JComboBox<String> createComboBox(String[] comboText, Font font){
		JComboBox<String> comboBox = new JComboBox<>(comboText);
		comboBox.setFont(font);
		comboBox.setSelectedIndex(INITIAL_HISTORY_LENGTH_MONTHS - 1);
		comboBox.addItemListener(new ComboItemListener());
		
		return comboBox;
	}
	
	private JLabel createLabel(String text, Font font){
		JLabel label = new JLabel(text);
		label.setFont(font);
		
		return label;
	}
	
	private void assembleHistoryPanel(){
		stockHistoryPanel.removeAll();
		
		stockHistoryPanel.add(chartPanel, "dock center, grow, push, wrap");
		
		stockHistoryPanel.add(panelTitleLabel, "split 2");
		stockHistoryPanel.add(historyLengthCombo, "wrap");
		
		stockHistoryPanel.revalidate();
		stockHistoryPanel.repaint();
	}
	
	private JPanel createStockHistoryPanel(){
		JPanel stockHistoryPanel = new JPanel();
		stockHistoryPanel.setLayout(new MigLayout());
		stockHistoryPanel.setBackground(Color.WHITE);
		
		return stockHistoryPanel;
	}
	
	public JPanel getPanel(){
		return stockHistoryPanel;
	}
	
	public void setStockHistory(List<HistoricalQuote> historyList){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"setStockHistory", "Entering", 
				new Object[]{"History List: " + historyList});
		
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
		
		LOGGER.logp(Level.INFO, this.getClass().getName(), 
				"setStockHistory", "Stock History Displayed");
	}
	
	private XYDataset createDataset(List<HistoricalQuote> historyList){
		TimeSeries timeSeries = new TimeSeries("Stock History"); //TODO add this name to locale text
		
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
	
	private JPanel createChartPanel(XYDataset dataset, String chartName){
		JFreeChart chart = createChart(dataset, chartName);
		return new ChartPanel(chart);
	}
	
	private JFreeChart createChart(XYDataset dataset, String chartName){
		//TODO explore the tooltips option, it's one of the boolean params. See JFreeChart documentation
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
	
	

	@SuppressWarnings("unchecked")
	@Override
	public void changeProperty(PropertyChangeEvent event) {
		if(event.getPropertyName() == SELECTED_STOCK_HISTORY_PROPERTY){
			LOGGER.logp(Level.FINEST, this.getClass().getName(), 
					"changeProperty", "Changing Property", 
					new Object[]{"Property: " + event.getPropertyName()});
			
			setStockHistory((List<HistoricalQuote>) event.getNewValue());
		}
	}
	
	public void guiStateChange(int newState){
		if(newState == ENABLE_NO_PORTFOLIO_OPEN){
			
		}
		else if(newState == ENABLE_NO_STOCK_LOADED){
			
		}
		else if(newState == ENABLE_LOOKUP_STOCK_LOADED){
			
		}
		else if(newState == ENABLE_OWNED_STOCK_LOADED){
			
		}
	}

	@Override
	public Object getValueForAction(String valueToGet) {
		LOGGER.logp(Level.FINEST, this.getClass().getName(), "getValue", 
				"Entering method", new Object[] {"Command: " + valueToGet});
		
		Object result = null;
		if(valueToGet == STOCK_HISTORY_INTERVAL_ACTION){
			result = historyLengthCombo.getSelectedIndex() + 1;
		}
		
		return result;
	}
	
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
