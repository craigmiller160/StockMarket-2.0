package io.craigmiller160.stockmarket.stock;

import java.io.IOException;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.jcip.annotations.NotThreadSafe;

/**
 * An abstract implementation of the <tt>Stock</tt> interface. This class defines
 * several key elements of the implementation.
 * <p>
 * A sole constructor is provided, which sets the stock's symbol as a final value.
 * The symbol will function as the defining value of the stock. The methods <tt>hashCode()</tt> 
 * and <tt>equals()</tt> have been overriden to use this value to compare instances
 * of this class. In addition, the <tt>Comparable</tt> interface has been implemented
 * in this class, and it also utilizes the symbol to compare stock instances. Since in real life, 
 * two stocks of the same company will have the same symbol, and two stocks of different 
 * companies will not, this allows for better comparison of stock instances.
 * <p>
 * The <tt>getValueMap()</tt> method inherited from the <tt>Stock</tt> interface 
 * has been implemented and made final in this class. An additional protected method,
 * <tt>getModifiableValueMap()</tt>, has also been provided. The implementation of
 * <tt>getValueMap()</tt> now returns an unmodifiable <tt>Map</tt> for thread safety
 * reasons. However, subclasses will still need to be able to add various values
 * to the <tt>Map</tt>. The <tt>getModifiableValueMap()</tt> method has been
 * provided for the subclasses to use. This method is called by <tt>getValueMap()</tt>,
 * where the <tt>Map</tt> it received is wrapped in an unmodifiable <tt>Map</tt>
 * before being returned to the caller.
 * <p>
 * In addition, <tt>getModifiableValueMap()</tt> should never be made final.
 * It should always be there for additional subclasses in the tree to override
 * and add values to as the need arrises.
 * <p>
 * <b>THREAD SAFETY:</b> This abstract class is completely thread safe. However, most
 * of this class's state will be defined by its subclasses. This class cannot make
 * any guarantees about the thread safety of its subclasses, however subclasses CAN
 * count on all implemented methods of this abstract class as being thread safe.
 * <p>
 * For the purpose of simplicity, this class has a <tt>NotThreadSafe</tt> annotation,
 * because programs using it to abstractly manage its implementations should not
 * assume that the implementations are thread safe.
 * <p>
 * <b>NOTE ABOUT DOWNLOADERS:</b> <tt>StockDownloader</tt> implementations may not support every
 * field name listed as a constant here. When designing a subclass of <tt>AbstractStock</tt>, be
 * aware of which downloader is likely to be used and which fields are accepted by it. Fields that
 * are not supported by the downloader, but are passed to it as a parameter, will likely throw an 
 * exception.
 * 
 * @author Craig
 * @version 2.0
 * @see io.craigmiller160.stockmarket.stock.StockDownloader StockDownloader
 *
 */
@NotThreadSafe
public abstract class AbstractStock implements Stock, Comparable<AbstractStock>, Serializable{
	
	
	/**
	 * SerialVersionUID for serialization support.
	 */
	private static final long serialVersionUID = -1499524743557875615L;
	
	/**
	 * The symbol code for this stock.
	 */
	protected final String symbol;
	
	/**
	 * Constructs a stock based on the provided symbol.
	 * 
	 * @param symbol the symbol code for this stock.
	 */
	public AbstractStock(String symbol){
		this.symbol = symbol.toUpperCase();
	}
	
	@Override
	public String getSymbol(){
		return symbol;
	}
	
	@Override
	public abstract void setStockDetails(StockDownloader downloader, 
			boolean fullDetails) throws InvalidStockException, UnknownHostException, IOException;
	
	@Override
	public List<HistoricalQuote> getStockHistory(StockDownloader downloader, 
			int months) throws InvalidStockException, UnknownHostException, IOException{
		return downloader.downloadStockHistory(symbol, months);
	}
	
	/**
	 * Returns a modifiable <tt>Map</tt> of all the values contained in this stock, 
	 * as opposed to <tt>getValueMap()</tt> which returns an unmodifiable <tt>Map</tt>.
	 * This method should be the one overriden and configured with the necessary
	 * values, as it is invoked when <tt>getValueMap</tt> is called.
	 * <p>
	 * This method should always ONLY return a modifiable <tt>Map</tt>, to later
	 * be wrapped in an unmodifiable <tt>Map</tt>. This allows further subclasses
	 * to continue to override this method to add additional values to the <tt>Map</tt>
	 * without having to worry about <tt>Map</tt> immutability.
	 * 
	 * @param fullDetails whether or not the <tt>Map</tt> should contain all
	 * the values of this stock.
	 * @return a <tt>Map</tt>  of the values of this stock.
	 * @see io.craigmiller160.stockmarket.stock.AbstractStock#getValueMap(boolean) AbstractStock.getValueMap()
	 */
	protected abstract Map<String,Object> getModifiableValueMap(boolean fullDetails);
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * This final method calls on <tt>getModifiableValueMap()</tt> and wraps it in
	 * an unmodifiable <tt>Map</tt> before returning it. Subclasses wishing to add
	 * to the <tt>Map</tt> should override that protected method in order to do so,
	 * as it will publicly be invoked by this method.
	 * 
	 * @see io.craigmiller160.stockmarket.stock.AbstractStock#getModifiableValueMap(boolean) AbstractStock.getModifiableValueMap()
	 */
	@Override
	public final Map<String,Object> getValueMap(boolean fullDetails){
		return Collections.unmodifiableMap(getModifiableValueMap(fullDetails));
	}
	
	@Override
	public String toString(){
		return symbol;
	}
	
	@Override
	public int hashCode(){
		return symbol.hashCode();
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof AbstractStock){
			return this.hashCode() == obj.hashCode();
		}
		else{
			return false;
		}
	}
	
	@Override
	public int compareTo(AbstractStock s){
		return symbol.compareToIgnoreCase(s.getSymbol());
	}
	
}
