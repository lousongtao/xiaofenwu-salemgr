package com.shuishou.salemgr.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Timer;

import javax.print.PrintService;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.Logger;

import com.shuishou.salemgr.ConstantValue;
import com.shuishou.salemgr.Messages;
import com.shuishou.salemgr.beans.Category1;
import com.shuishou.salemgr.beans.Category2;
import com.shuishou.salemgr.beans.CurrentDutyInfo;
import com.shuishou.salemgr.beans.DiscountTemplate;
import com.shuishou.salemgr.beans.Goods;
import com.shuishou.salemgr.beans.Member;
import com.shuishou.salemgr.beans.PayWay;
import com.shuishou.salemgr.beans.UserData;
import com.shuishou.salemgr.http.HttpUtil;
import com.shuishou.salemgr.printertool.PrintThread;
import com.shuishou.salemgr.ui.components.IconButton;
import com.shuishou.salemgr.ui.components.JBlockedButton;
import com.shuishou.salemgr.ui.components.NumberTextField;
import com.shuishou.salemgr.ui.uibean.ChoosedGoods;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

public class MainFrame extends JFrame implements ActionListener{
	public final static Logger logger = Logger.getLogger(MainFrame.class.getName());
	public static int WINDOW_WIDTH;
	public static int WINDOW_HEIGHT;
	public static int WINDOW_LOCATIONX;
	public static int WINDOW_LOCATIONY;
	public static String language;
	public static String SERVER_URL;
	public static int GOODS_AMOUNT;
	public static int MEMBER_AMOUNT;
	private OutputStream outputStreamCashdrawer;
	public static String portCashdrawer;
	public static String printerName;
	
	private JLabel lbStatusLogin = new JLabel();
	private JLabel lbIndentInfo = new JLabel();
	private JLabel lbCurrentTime = new JLabel();
	private IconButton btnCheckout = new IconButton(Messages.getString("MainFrame.Checkout"), "/resource/checkout.png"); //$NON-NLS-1$
	private IconButton btnDeleteItem = new IconButton(Messages.getString("MainFrame.DeleteItem"), "/resource/deleteitem.png"); //$NON-NLS-1$
	private IconButton btnChangeAmount = new IconButton(Messages.getString("MainFrame.ChangeAmount"), "/resource/changeamount.png"); //$NON-NLS-1$
//	private IconButton btnMember = new IconButton(Messages.getString("MainFrame.Member"), "/resource/member.png"); //$NON-NLS-1$
	private JBlockedButton btnOpenCashdrawer = new JBlockedButton(Messages.getString("MainFrame.OpenCashdrawer"), "/resource/cashdrawer.png"); //$NON-NLS-1$
	private IconButton btnShiftWork = new IconButton(Messages.getString("MainFrame.ShiftWork"), "/resource/swiftwork.png"); //$NON-NLS-1$
	private IconButton btnRefund = new IconButton(Messages.getString("MainFrame.Refund"), "/resource/refund.png"); //$NON-NLS-1$
	private IconButton btnPreOrder = new IconButton(Messages.getString("MainFrame.PreOrder"), "/resource/preorder.png"); //$NON-NLS-1$
	private JButton btnPreOrderMgr = new JButton(Messages.getString("MainFrame.PreOrderMgr")); //$NON-NLS-1$
	private JTextField tfSearch = new JTextField();
	
	private ArrayList<DiscountTemplate> discountTemplateList = new ArrayList<>(); 
	private ArrayList<PayWay> paywayList = new ArrayList<>(); 
//	private ArrayList<Category1> category1List = new ArrayList<>();
	private UserData onDutyUser = null;//在值班状态用户名称
	private HashMap<String, String> configsMap;
	private JTable tableGoods = new JTable();
	private GoodsTableModel modelGoods = new GoodsTableModel();
	private HashMap<String, Goods> mapGoods;
	private HashMap<String, Member> mapMember;
	
	public MainFrame(){
		initUI();
		initData();
		setLocation(WINDOW_LOCATIONX, WINDOW_LOCATIONY);
		setTitle(Messages.getString("MainFrame.FrameTitle")); //$NON-NLS-1$
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void initUI(){
		tableGoods.setModel(modelGoods);
		tableGoods.setRowHeight(40);
		tableGoods.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableGoods.getColumnModel().getColumn(0).setPreferredWidth(200);
		tableGoods.getColumnModel().getColumn(1).setPreferredWidth(200);
		tableGoods.getColumnModel().getColumn(2).setPreferredWidth(200);
		tableGoods.getColumnModel().getColumn(3).setPreferredWidth(200);
		JScrollPane jspGoods = new JScrollPane(tableGoods, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		JLabel lbSearch = new JLabel(Messages.getString("MainFrame.Search"));
		JPanel pFunction = new JPanel(new GridBagLayout());
		Insets insets = new Insets(5,5,5,0);
		int col = 0;
		pFunction.add(lbSearch, 		new GridBagConstraints(col++, 0, 1, 1,0,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets,0,0));
		pFunction.add(tfSearch, 		new GridBagConstraints(col++, 0, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets,0,0));
		pFunction.add(btnCheckout, 		new GridBagConstraints(col++, 0, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets,0,0));
		pFunction.add(btnChangeAmount, 	new GridBagConstraints(col++, 0, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets,0,0));
		pFunction.add(btnDeleteItem, 	new GridBagConstraints(col++, 0, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets,0,0));
		pFunction.add(btnRefund, 		new GridBagConstraints(col++, 0, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets,0,0));
		pFunction.add(btnPreOrder,		new GridBagConstraints(col++, 0, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets,0,0));
		col = 2;
		pFunction.add(btnPreOrderMgr,	new GridBagConstraints(col++, 1, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets,0,0));
		pFunction.add(btnShiftWork,		new GridBagConstraints(col++, 1, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets,0,0));
		pFunction.add(btnOpenCashdrawer,new GridBagConstraints(col++, 1, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets,0,0));
		pFunction.setPreferredSize(new Dimension(180, 100));
		
		btnCheckout.addActionListener(this);
		btnOpenCashdrawer.addActionListener(this);
		btnShiftWork.addActionListener(this);
		btnChangeAmount.addActionListener(this);
		btnDeleteItem.addActionListener(this);
		btnPreOrder.addActionListener(this);
		btnRefund.addActionListener(this);
		btnPreOrderMgr.addActionListener(this);
		
		lbStatusLogin.setBorder(BorderFactory.createLineBorder(Color.gray));
		lbIndentInfo.setBorder(BorderFactory.createLineBorder(Color.gray));
		lbCurrentTime.setBorder(BorderFactory.createLineBorder(Color.gray));
		JPanel pStatus = new JPanel(new GridBagLayout());
		pStatus.add(lbStatusLogin, new GridBagConstraints(0, 0, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,0,0,0),0,0));
		pStatus.add(lbIndentInfo,  new GridBagConstraints(1, 0, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,3,0,0),0,0));
		pStatus.add(lbCurrentTime, new GridBagConstraints(2, 0, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,3,0,0),0,0));
		
		this.getContentPane().setLayout(new GridBagLayout());
		this.getContentPane().add(jspGoods, 	new GridBagConstraints(0, 0, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,0,0,0),0,0));
		this.getContentPane().add(pFunction, 	new GridBagConstraints(0, 1, 1, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,0,0,0),0,0));
		this.getContentPane().add(pStatus, 		new GridBagConstraints(0, 2, 1, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0),0,0));
		
		BarcodeScannerListener listener = new BarcodeScannerListener();
		tfSearch.addKeyListener(new KeyAdapter(){
        	public void keyTyped(KeyEvent e) {
        		if (e.getKeyChar() == KeyEvent.VK_ENTER){
        			searchAnObjectByValue(tfSearch.getText());
        		}
        	}
        });
        btnCheckout.addKeyListener(listener);
        btnOpenCashdrawer.addKeyListener(listener);
        btnShiftWork.addKeyListener(listener);
        btnChangeAmount.addKeyListener(listener);
        btnDeleteItem.addKeyListener(listener);
        btnRefund.addKeyListener(listener);
        btnPreOrder.addKeyListener(listener);
        btnPreOrderMgr.addKeyListener(listener);
        tableGoods.addKeyListener(listener);
        
        //start printer thread
        new PrintThread().startThread();
	}
	
	private void initData(){
		loadShiftDutyInfo();
		loadDiscountTemplates();
		loadGoods();
		loadMembers();
		loadConfigsMap();
		initRefreshTimer();
		loadPayWay();
	}
	
	private void initRefreshTimer(){
		Timer timer = new Timer();
		timer.schedule(new java.util.TimerTask(){

			@Override
			public void run() {
				//refresh time
				lbCurrentTime.setText(ConstantValue.DFYMDHM.format(new Date()));
			}}, 0, 60*1000);
	}
	
	public List<DiscountTemplate> getDiscountTemplateList() {
		return discountTemplateList;
	}

	public void setDiscountTemplateList(ArrayList<DiscountTemplate> discountTemplateList) {
		this.discountTemplateList = discountTemplateList;
	}

	public ArrayList<PayWay> getPaywayList() {
		return paywayList;
	}

	public void setPaywayList(ArrayList<PayWay> paywayList) {
		this.paywayList = paywayList;
	}

	public void startLogin(String userName, String password){
		LoginDialog dlg = new LoginDialog(this);
		dlg.setValue(userName, password);
		dlg.setVisible(true);
	}
	
	/**
	 * this class just hold Category1 objects. if need goods object, please loop into the category1 objects
	 */
	private void loadGoods(){
		ArrayList<Category1> category1List = HttpUtil.loadGoods(this);
		//store goods into a hashmap
		if (GOODS_AMOUNT > 0)
			mapGoods = new HashMap<>(2 * GOODS_AMOUNT);
		if (category1List != null) {
			for (Category1 c1 : category1List) {
				if (c1.getCategory2s() != null) {
					for (Category2 c2 : c1.getCategory2s()) {
						if (c2.getGoods() != null) {
							for (Goods g : c2.getGoods()) {
								mapGoods.put(g.getBarcode(), g);
							}
						}
					}
				}
			}
		}
	}
	
	private void loadMembers(){
		ArrayList<Member> members = HttpUtil.loadMembers(this);
		if (MEMBER_AMOUNT > 0){
			mapMember = new HashMap<>(2 * MEMBER_AMOUNT);
		}
		if (members != null){
			for(Member m : members){
				mapMember.put(m.getMemberCard(), m);
			}
		}
	}
	
	public void addMember(Member member){
		mapMember.put(member.getMemberCard(), member);
	}
	
	public HashMap<String, Member> getMapMember() {
		return mapMember;
	}
	
	public Member getMember(String memberCode){
		return mapMember.get(memberCode);
	}

	private void loadConfigsMap(){
		configsMap = HttpUtil.loadConfigsMap(this);	
	}
	
	private void loadShiftDutyInfo(){
		CurrentDutyInfo cd = HttpUtil.loadShiftDutyInfo(this);
		if (cd != null && cd.currentDutyId > 0){
			Date startTime = null;
			try {
				startTime = ConstantValue.DFYMDHMS.parse(cd.startTime);
			} catch (Exception e) {
				logger.error("get wrong format of on duty date");
			}
			onDutyUser = new UserData(cd.currentDutyId, cd.currentDutyName, startTime);
			lbStatusLogin.setText(Messages.getString("MainFrame.currentDutyWorker") + onDutyUser.getName() //$NON-NLS-1$
					+ Messages.getString("MainFrame.DutyStartTime") + cd.startTime); //$NON-NLS-1$
		}
		
	}
	
	private void loadDiscountTemplates(){
		ArrayList<DiscountTemplate> dts = HttpUtil.loadDiscountTemplates(this);
		discountTemplateList.clear();
		if (dts != null)
			discountTemplateList.addAll(dts);
	}
	
	private void loadPayWay(){
		ArrayList<PayWay> pws = HttpUtil.loadPayWay(this);
		paywayList.clear();
		if (pws != null){
			paywayList.addAll(pws);
		}
	}
	
	public void doOnDuty(int userId, boolean printLastDutyTicket){
		CurrentDutyInfo cd = HttpUtil.doOnDuty(this, userId, printLastDutyTicket);
		if (cd == null)
			return;
		Date startTime = null;
		try {
			startTime = ConstantValue.DFYMDHMS.parse(cd.startTime);
		} catch (ParseException e) {
			logger.error("get wrong format of on duty date ");
		}
		onDutyUser = new UserData(cd.currentDutyId, cd.currentDutyName, startTime);
		lbStatusLogin.setText(Messages.getString("MainFrame.currentDutyWorker") + cd.currentDutyName  //$NON-NLS-1$
			+ Messages.getString("MainFrame.DutyStartTime") + cd.startTime); //$NON-NLS-1$
	}
	
	public void doOffDuty(UserData user, boolean print){
		CurrentDutyInfo cd = HttpUtil.doOffDuty(this, user, print);
		if (cd == null)
			return;
		onDutyUser = null;
		lbStatusLogin.setText("");

		startLogin(null, null);
	}
	
	public UserData getOnDutyUser() {
		return onDutyUser;
	}

	private void doDeleteItem(){
		int row = tableGoods.getSelectedRow();
		if (row < 0){
			return;
		}
		modelGoods.deleteItem(row);
		modelGoods.fireTableDataChanged();
		calculatePrice();
	}
	
	private void doChangeAmount(){
		int row = tableGoods.getSelectedRow();
		if (row < 0){
			return;
		}
		int oldAmount = modelGoods.getObjectAt(row).amount;
		ChangeAmountDialog dlg = new ChangeAmountDialog(this, "Change Amount", oldAmount);
		dlg.setVisible(true);
		if (!dlg.isConfirm)
			return;
		int newAmount = dlg.inputInteger;
		modelGoods.getObjectAt(row).amount = newAmount;
		modelGoods.fireTableDataChanged();
		calculatePrice();
	}
	
	/**
	 * firstly, look for by barcode; if not find, then look for by name
	 * @param value
	 */
	private void searchAnObjectByValue(String value){
		Goods goods = mapGoods.get(value);
		if (goods == null){
			goods = searchAnObjectByName(value);
			if (goods == null){
				return;
			}
		}
		tfSearch.setText("");
		ChoosedGoods cg = new ChoosedGoods();
		cg.amount = 1;
		cg.goods = goods;
		modelGoods.addItem(cg);
		modelGoods.fireTableDataChanged();
		calculatePrice();
	}
	
	private Goods searchAnObjectByName(String name){
		ArrayList<Goods> resultlist = new ArrayList<Goods>();
		Iterator<Goods> it = mapGoods.values().iterator();
		while(it.hasNext()){
			Goods g = it.next();
			if (g.getName().indexOf(name) >= 0){
				resultlist.add(g);
			}
		}
		if (resultlist.isEmpty()){
			JOptionPane.showMessageDialog(this, Messages.getString("MainFrame.NoFindByBarcode") + name);
			return null;
		}else if (resultlist.size() == 1){
			return resultlist.get(0);
		} else {
			SearchObjectListDialog dlg = new SearchObjectListDialog(this, resultlist, 600, 300);
			dlg.setVisible(true);
			return dlg.getChoosedGoods();
		}
	}
	
	public Goods getGoodsByBarcode(String barcode){
		Goods goods = mapGoods.get(barcode);
		return goods;
	}
	
	private void calculatePrice(){
		if (modelGoods.getData() == null || modelGoods.getData().isEmpty()){
			lbIndentInfo.setText("");
			return;
		}
		double price = 0;
		int itemamount = 0;
		for(ChoosedGoods cg : modelGoods.getData()){
			itemamount += cg.amount;
			price += cg.amount * cg.goods.getSellPrice();
		}
		lbIndentInfo.setText(Messages.getString("MainFrame.lbIndentInfo.typeamount") + modelGoods.getRowCount()+ "  "
				+ Messages.getString("MainFrame.lbIndentInfo.itemamount") + itemamount + "  "
				+ Messages.getString("MainFrame.lbIndentInfo.price") + " $" + String.format("%.2f",price));
	}
	
	public void putFocusOnTFBarcode(){
		tfSearch.requestFocusInWindow();
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnCheckout){
			doCheckout();
		} else if (e.getSource() == btnShiftWork){
			doSwiftWork();
		} else if (e.getSource() == btnOpenCashdrawer){
			doOpenCashdrawer(true);
		} else if (e.getSource() == btnDeleteItem){
			doDeleteItem();
		} else if (e.getSource() == btnChangeAmount){
			doChangeAmount();
		} else if (e.getSource() == btnRefund){
			doRefund();
		} else if (e.getSource() == btnPreOrder){
			doPreOrder();
		} else if (e.getSource() == btnPreOrderMgr){
			doPreOrderMgr();
		}
	}
	
	private void doPreOrderMgr(){
		PreorderQueryDialog dlg = new PreorderQueryDialog(this);
		dlg.setVisible(true);
	}
	
	private void doPreOrder(){
		if (modelGoods.getRowCount() == 0)
			return;
		
		PreOrderCheckoutDialog dlg = new PreOrderCheckoutDialog(this, Messages.getString("MainFrame.PreOrder"), true, modelGoods.getData()); //$NON-NLS-1$
		dlg.setVisible(true);
	}
	
	private void doOpenCashdrawer(boolean needpassword){
		if (needpassword){
			String code = JOptionPane.showInputDialog(this, Messages.getString("MainFrame.InputCodeOfOpenCashdrawer"));
			if (code == null)
				return;
			if (!configsMap.get(ConstantValue.CONFIGS_OPENCASHDRAWERCODE).equals(code)){
				JOptionPane.showMessageDialog(this, Messages.getString("MainFrame.ErrorCashdrawerCode"));
				return;
			}
		}
		
		PrintService ps = null;
		for(PrintService service : PrinterJob.lookupPrintServices()){
			if (service.getName().equals(this.printerName)){
				ps = service;
				break;
			}
		}
		if (ps == null){
			JOptionPane.showMessageDialog(this, "Cannot drive printer by name : " + printerName);
			return;
		}
		
		PrinterJob pj = PrinterJob.getPrinterJob();
		
		pj.setPrintable(new Printable(){

			@Override
			public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
				if (pageIndex > 0) {
					return NO_SUCH_PAGE;
				}
				Graphics2D g2d = (Graphics2D) graphics;
				g2d.drawString("", 0, 0);
				
				return PAGE_EXISTS;
			}});
		try {
			pj.setPrintService(ps);
			pj.print();
		} catch (PrinterException e) {
			JOptionPane.showMessageDialog(this, "Cannot drive printer");
			logger.error("", e);
		}
	}
	
	public void clearTable(){
		modelGoods.deleteAllItem();
		modelGoods.fireTableDataChanged();
		calculatePrice();
	}
	
	private void doCheckout(){
		if (modelGoods.getRowCount() == 0)
			return;
		
		CheckoutDialog dlg = new CheckoutDialog(this, Messages.getString("MainFrame.CheckoutTitle"), true, modelGoods.getData()); //$NON-NLS-1$
		dlg.setVisible(true);
	}
	
	private void doRefund(){
		if (modelGoods.getRowCount() == 0)
			return;
		
		RefundDialog dlg = new RefundDialog(this, Messages.getString("MainFrame.Refund"), true, modelGoods.getData()); //$NON-NLS-1$
		dlg.setVisible(true);
	}
	

	/**
	 * if there is no duty user currently, do nothing
	 * if there is a duty user, as whether print the shift ticket.
	 */
	public void doSwiftWork() {
		if (onDutyUser == null) {
			
		} else {
			String msg = Messages.getString("MainFrame.OffDutyMsg") + onDutyUser.getName(); //$NON-NLS-1$
			Object[] options = { Messages.getString("MainFrame.ShiftWork"),
					Messages.getString("MainFrame.ShiftWorkPrint"), Messages.getString("Cancel") };
			int n = JOptionPane.showOptionDialog(this, msg, Messages.getString("MainFrame.ShiftWorkTitle"),
					JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
			if (n == 0) {
				doOffDuty(onDutyUser, false);
			} else if (n == 1) {
				doOffDuty(onDutyUser, true);
			}
		}
	}
	
	public HashMap<String, String> getConfigsMap() {
		return configsMap;
	}

	public void setConfigsMap(HashMap<String, String> configsMap) {
		this.configsMap = configsMap;
	}

	public static void main(String[] args){
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				MainFrame.logger.error(ConstantValue.DFYMDHMS.format(new Date()));
				MainFrame.logger.error("", e);
				e.printStackTrace();
			}
		});
		
		//load properties
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = MainFrame.class.getClassLoader().getResourceAsStream("config.properties");
			// load a properties file
			prop.load(input);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		Messages.initResourceBundle(prop.getProperty("language"));
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());//windows 格式
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		int fontsize = Integer.parseInt(prop.getProperty("fontsize"));
		Font font = new Font(null, Font.PLAIN, fontsize);
		Enumeration enums = UIManager.getDefaults().keys();
		while(enums.hasMoreElements()){
			Object key = enums.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof Font){
				UIManager.put(key, font);
			}
		}
		MainFrame.SERVER_URL = prop.getProperty("SERVER_URL");
		MainFrame.WINDOW_WIDTH = Integer.parseInt(prop.getProperty("mainframe.width"));
		MainFrame.WINDOW_HEIGHT = Integer.parseInt(prop.getProperty("mainframe.height"));
		MainFrame.WINDOW_LOCATIONX = Integer.parseInt(prop.getProperty("mainframe.locationx"));
		MainFrame.WINDOW_LOCATIONY = Integer.parseInt(prop.getProperty("mainframe.locationy"));
		MainFrame.language = prop.getProperty("language");
		MainFrame.portCashdrawer=prop.getProperty("portCashdrawer");
		MainFrame.GOODS_AMOUNT=Integer.parseInt(prop.getProperty("goods.amount"));
		MainFrame.MEMBER_AMOUNT = Integer.parseInt(prop.getProperty("member.amount"));
		MainFrame.printerName = prop.getProperty("printerName");
		final MainFrame f = new MainFrame();
		f.setExtendedState(JFrame.MAXIMIZED_BOTH);
//		f.setUndecorated(true);
		f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		f.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				if (JOptionPane.showConfirmDialog(f, "Do you want to quit this system?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
					System.exit(0);
				}
			}
		});
		f.setVisible(true);
		f.startLogin(prop.getProperty("defaultuser.name"), prop.getProperty("defaultuser.password"));
	}
	
	class BarcodeScannerListener extends KeyAdapter{
		private long lastEventTimeStamp = 0L;
		private static final long THRESHOLD = 100;
		private static final int MIN_BARCODE_LENGTH = 8;
		private final StringBuffer barcode = new StringBuffer();
		@Override
		public void keyPressed(KeyEvent e) {
			if (KeyEvent.KEY_PRESSED == e.getID()) {
				if (e.getWhen() - lastEventTimeStamp > THRESHOLD) {
					barcode.delete(0, barcode.length());
				}

				lastEventTimeStamp = e.getWhen();

				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					if (barcode.length() >= MIN_BARCODE_LENGTH) {
						searchAnObjectByValue(barcode.toString());
					}
					e.consume();//must consume the ENTER, otherwise it will invoke the button's click event if it is on focused
					barcode.delete(0, barcode.length());
				} else {
					barcode.append(e.getKeyChar());
				}
			}
		}
	}
}
