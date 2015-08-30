package io.craigmiller160.stockmarket.stock;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import net.jcip.annotations.Immutable;

/**
 * An immutable object containing the closing price of a stock on a particular
 * date. This class contains only the date and the closing price, and so should 
 * only be used while wrapped by another object that knows what stock this quote
 * is for.
 * 
 * @author Craig
 * @version 2.0
 */
@Immutable
public class HistoricalQuote implements Comparable<HistoricalQuote>, Serializable{

	/**
	 * SerialVersionUID for serialization support.
	 */
	private static final long serialVersionUID = 6076096436057970640L;

	/**
	 * The date of the <tt>HistoricalQuote</tt>.
	 */
	public final Calendar DATE;
	
	/**
	 * The closing value of the stock on this date.
	 */
	public final BigDecimal CLOSE_VALUE;
	
	/**
	 * Constructs the <tt>HistoricalQuote</tt>.
	 * 
	 * @param date the date of this quote.
	 * @param closeValue the closing value of the stock on this date.
	 */
	public HistoricalQuote(Calendar date, BigDecimal closeValue) {
		this.DATE = date;
		this.CLOSE_VALUE = closeValue;
	}
	
	//TODO consider changing the hashCode/equals to be purely
	//based on the date, rather than the date & close.
	
	@Override
	public int hashCode(){
		int calCode = DATE.hashCode();
		int closeCode = CLOSE_VALUE.hashCode();
		return calCode + closeCode;
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof HistoricalQuote){
			return this.hashCode() == obj.hashCode();
		}
		else{
			return false;
		}
	}
	
	@Override
	public String toString(){
		DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy"); 
		NumberFormat moneyFormat = new DecimalFormat("$###,###,###,##0.00");
		return String.format("%s: %s", 
				dateFormat.format(DATE.getTime()), 
				moneyFormat.format(CLOSE_VALUE));
	}

	@Override
	public int compareTo(HistoricalQuote hq) {
		return this.DATE.compareTo(hq.DATE);
	}

}
