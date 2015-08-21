package stockmarket.gui.dialog;

import java.awt.Frame;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import stockmarket.stock.DefaultStock;
import stockmarket.stock.Stock;
import stockmarket.util.Language;

public class BuyDialog extends TransactionDialog {

	//TODO delete this method after testing is done
	public static void main(String[] args){
		SwingUtilities.invokeLater(new Runnable(){

			@Override
			public void run() {
				DefaultStock stock = new DefaultStock("AAPL");
				
				BuyDialog dialog = new BuyDialog(stock);
				
				dialog.showDialog();
			}
			
		});
	}
	
	private static final Language LANGUAGE = Language.getInstance();
	
	public BuyDialog(Stock stock) {
		super(stock);
	}

	public BuyDialog(Frame owner, Stock stock) {
		super(owner, stock);
	}

	public BuyDialog(Frame owner, boolean modal, Stock stock) {
		super(owner, modal, stock);
	}

	@Override
	protected ImageIcon createIcon() {
		return new ImageIcon(
				this.getClass().getClassLoader().getResource(
						"96p/buy.png"));
	}

	@Override
	protected String transactionType() {
		return LANGUAGE.getString("buy_label");
	}

	@Override
	protected String limitLabelText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String limitValueText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected int maxShareLimit() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getValue(String valueToGet) {
		// TODO Auto-generated method stub
		return null;
	}

}
