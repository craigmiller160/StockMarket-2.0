import java.math.BigDecimal;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import io.craigmiller160.stockmarket.model.SQLPortfolioModel;
import io.craigmiller160.stockmarket.stock.DefaultOwnedStock;
import io.craigmiller160.stockmarket.stock.InvalidStockException;


public class Tester {
	public static void main(String[] args){
		SessionFactory factory = new Configuration().configure().buildSessionFactory();
		Session session = factory.openSession();
		session.beginTransaction();
		
		DefaultOwnedStock stock = new DefaultOwnedStock("AAPL");
		stock.setCompanyName("Apple");
		stock.setCurrentPrice(new BigDecimal(20));
		stock.setTotalValue(new BigDecimal(100));
		stock.setQuantityOfShares(5);
		stock.setNet(new BigDecimal(10));
		
		SQLPortfolioModel portfolio = new SQLPortfolioModel();
		portfolio.setStockInList(stock);
		session.save(portfolio);
		
		
		session.getTransaction().commit();
		session.close();
		factory.close();
		
		System.out.println(portfolio.getUserID());
	}
	
	public static void throwIt() throws InvalidStockException{
		throw new InvalidStockException("AAPL");
	}
}
