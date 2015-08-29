package stockmarket.stock;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import net.jcip.annotations.NotThreadSafe;

/**
 * Special list class for storing a stock's history. It only accepts 
 * <tt>HistoricalQuote</tt> objects, and also requires a parameter
 * for the stock's symbol, a value this list can return.
 * <p>
 * This list extends <tt>AbstractList</tt> and so can be used 
 * interchangably with methods that accepts <tt>AbstractList</tt>
 * as a parameter.
 * <p>
 * <b>THREAD SAFETY:</b> This collection is NOT thread safe. It should
 * be confined to one thread at a time, or wrapped in some form of external 
 * synchronization for modification. 
 * 
 * @author craig
 * @version 2.0
 */
@NotThreadSafe
public class StockHistoryList extends AbstractList<HistoricalQuote> {
	
	/**
	 * The list this class wraps around.
	 */
	private final List<HistoricalQuote> historyList;
	
	/**
	 * The symbol of the stock this history is for.
	 */
	private final String symbol;
	
	/**
	 * Create the stock history list for a stock
	 * with the specified symbol.
	 * 
	 * @param symbol the symbol of the stock.
	 */
	public StockHistoryList(String symbol){
		this.historyList = new ArrayList<>();
		this.symbol = symbol;
	}
	
	/**
	 * Create the stock history list for a stock
	 * with the specified symbol, with the specified
	 * initial capacity.
	 * 
	 * @param symbol the symbol of the stock.
	 * @param capacity the initial capacity of the list.
	 */
	public StockHistoryList(String symbol, int capacity){
		this.historyList = new ArrayList<>(capacity);
		this.symbol = symbol;
	}
	
	/**
	 * Create the stock history list for a stock
	 * with the specified symbol, and fill it with
	 * the values in the collection provided.
	 * 
	 * @param symbol the symbol of the stock.
	 * @param collection the initial values to add to the list.
	 */
	public StockHistoryList(String symbol, 
			Collection<? extends HistoricalQuote> collection){
		this.historyList = new ArrayList<>(collection);
		this.symbol = symbol;
	}
	
	/**
	 * Get the symbol of the stock this history is for.
	 * 
	 * @return the stock's symbol.
	 */
	public String getSymbol(){
		return symbol;
	}
	
	@Override
	public boolean add(HistoricalQuote quote){
		return historyList.add(quote);
	}
	
	@Override
	public void add(int index, HistoricalQuote quote){
		historyList.add(index, quote);
	}
	
	@Override
	public boolean addAll(Collection<? extends HistoricalQuote> collection){
		return historyList.addAll(collection);
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends HistoricalQuote> collection){
		return historyList.addAll(index, collection);
	}
	
	@Override
	public void clear(){
		historyList.clear();
	}
	
	@Override
	public boolean contains(Object obj){
		return historyList.contains(obj);
	}
	
	@Override
	public void forEach(Consumer<? super HistoricalQuote> action){
		historyList.forEach(action);
	}
	
	@Override
	public HistoricalQuote get(int index){
		return historyList.get(index);
	}
	
	@Override
	public int indexOf(Object obj){
		return historyList.indexOf(obj);
	}
	
	@Override
	public boolean isEmpty(){
		return historyList.isEmpty();
	}
	
	@Override
	public Iterator<HistoricalQuote> iterator(){
		return historyList.iterator();
	}
	
	@Override
	public int lastIndexOf(Object obj){
		return historyList.lastIndexOf(obj);
	}
	
	@Override
	public ListIterator<HistoricalQuote> listIterator(){
		return historyList.listIterator();
	}
	
	@Override
	public ListIterator<HistoricalQuote> listIterator(int index){
		return historyList.listIterator(index);
	}
	
	@Override
	public HistoricalQuote remove(int index){
		return historyList.remove(index);
	}
	
	@Override
	public boolean remove(Object obj){
		return historyList.remove(obj);
	}
	
	@Override
	public boolean removeAll(Collection<?> collection){
		return historyList.removeAll(collection);
	}
	
	@Override
	public boolean removeIf(Predicate<? super HistoricalQuote> filter){
		return historyList.removeIf(filter);
	}
	
	@Override
	public void replaceAll(UnaryOperator<HistoricalQuote> operator){
		historyList.replaceAll(operator);
	}
	
	@Override
	public boolean retainAll(Collection<?> collection){
		return historyList.retainAll(collection);
	}
	
	@Override
	public HistoricalQuote set(int index, HistoricalQuote quote){
		return historyList.set(index, quote);
	}
	
	@Override
	public int size(){
		return historyList.size();
	}
	
	@Override
	public void sort(Comparator<? super HistoricalQuote> comparator){
		historyList.sort(comparator);
	}
	
	@Override
	public Spliterator<HistoricalQuote> spliterator(){
		return historyList.spliterator();
	}
	
	@Override
	public List<HistoricalQuote> subList(int fromIndex, int toIndex){
		StockHistoryList sublist = new StockHistoryList(symbol);
		sublist.addAll(historyList.subList(fromIndex, toIndex));
		return sublist;
	}
	
	@Override
	public Object[] toArray(){
		return historyList.toArray();
	}
	
	@Override
	public <T> T[] toArray(T[] array){
		return historyList.toArray(array);
	}
	
	//TODO work on the thread safety of this list, along with the other
		//list in the PortfolioModel

}
