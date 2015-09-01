/**
 * 
 */
/**
 * Provides the classes necessary to create <tt>Stock</tt> objects for this program.
 * <tt>Stock</tt> objects represent real-life stocks traded on the New York Stock 
 * Exchange. The stock API in this package has three main components: a basic stock, 
 * an owned stock, and the downloader.
 * <p>
 * A basic stock is simply a stock that exists on a stock exchange. It may or may not 
 * be owned in the user's portfolio, and contains no ownership attributes. It is
 * primarily used for defining the fields this program is tracking for all stocks,
 * and serving as a vehicle for looking up information about various stocks on the 
 * market.
 * <p>
 * An owned stock is a stock that is currently owned in the user's portfolio. In
 * addition to inheriting all the basic stock attributes, an owned stock tracks how
 * many shares are owned, their value, and provides operation for increasing and
 * decreasing the amount of shares owned.
 * <p>
 * Finally, the downloader is the tool for updating the stock's values with information
 * from the internet. It exists as a separate module and is passed to the stock 
 * classes as-needed to provide refreshed information.
 * 
 * @author Craig
 * @since Version 2.0
 */
package io.craigmiller160.stockmarket.stock;