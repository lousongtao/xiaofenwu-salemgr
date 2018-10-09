package com.shuishou.salemgr.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableCellRenderer;

import org.apache.log4j.Logger;

import com.shuishou.salemgr.CommonTools;
import com.shuishou.salemgr.ConstantValue;
import com.shuishou.salemgr.Messages;
import com.shuishou.salemgr.beans.Category1;
import com.shuishou.salemgr.beans.Category2;
import com.shuishou.salemgr.beans.CurrentDutyInfo;
import com.shuishou.salemgr.beans.DiscountTemplate;
import com.shuishou.salemgr.beans.Goods;
import com.shuishou.salemgr.beans.Member;
import com.shuishou.salemgr.beans.PayWay;
import com.shuishou.salemgr.beans.Promotion;
import com.shuishou.salemgr.beans.UserData;
import com.shuishou.salemgr.http.HttpUtil;
import com.shuishou.salemgr.printertool.PrintThread;
import com.shuishou.salemgr.ui.components.DefaultTableCellHeaderRenderer;
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
	public static String portCashdrawer;
	public static String printerName;
	public static String printerIP;
	public static String FONT_PRINTTICKET;
	
	private JLabel lbStatusLogin = new JLabel();
	private JLabel lbIndentInfo = new JLabel();
	private JLabel lbCurrentTime = new JLabel();
	private JLabel lbMemberInfo = new JLabel();
	private JLabel lbGoodsAmount = new JLabel(Messages.getString("MainFrame.lbIndentInfo.itemamount"));
	private JLabel lbGoodsPrice = new JLabel(Messages.getString("MainFrame.lbIndentInfo.price"));
	private JLabel lbGoodsOriginPrice = new JLabel(Messages.getString("MainFrame.lbIndentInfo.OriginPrice"));
	private JLabel lbHotkeys = new JLabel("END=Checkout; F3=Choose Item; Ctrl+M=Lookfor Member; Ctrl+G=Lookfor Goods");
	private JTextField tfMember = new JTextField();
	private JTextField tfSearchGoods = new JTextField();
	private JButton btnQueryMember = new JButton(Messages.getString("Query"));
	private JButton btnAddMember = new JButton(Messages.getString("MainFrame.AddMember"));
	private JButton btnClearMember = new JButton(Messages.getString("MainFrame.ClearMember"));
	private IconButton btnCheckout = new IconButton(Messages.getString("MainFrame.Checkout")+"[SPACE]", "/resource/checkout.png"); //$NON-NLS-1$
	private JButton btnOpenCashdrawer = new JButton(Messages.getString("MainFrame.OpenCashdrawer")+"[F7]"); //$NON-NLS-1$
	private JButton btnShiftWork = new JButton(Messages.getString("MainFrame.ShiftWork")); //$NON-NLS-1$
	private JButton btnTodayIndent = new JButton(Messages.getString("MainFrame.TodayIndent")); //$NON-NLS-1$
	private IconButton btnPreOrder = new IconButton(Messages.getString("MainFrame.PreOrder"), "/resource/preorder.png"); //$NON-NLS-1$
	private JButton btnPreOrderMgr = new JButton(Messages.getString("MainFrame.PreOrderMgr")); //$NON-NLS-1$
	private JButton btnNoBarcodeItem = new JButton(Messages.getString("MainFrame.NoBarcodeItemMgr")+"[F5]"); //$NON-NLS-1$
	private JPopupMenu popupMenu = new JPopupMenu();
	private JMenuItem miDeleteItem = new JMenuItem(Messages.getString("MainFrame.DeleteThis"));
	private JMenuItem miDeleteAllItem = new JMenuItem(Messages.getString("MainFrame.DeleteAll"));
	private JMenuItem miChangeAmountPrice = new JMenuItem(Messages.getString("MainFrame.ChangeAmountPrice"));
	private JMenuItem miMarkRefund = new JMenuItem(Messages.getString("MainFrame.MarkRefund"));
	private JMenuItem miMarkAllRefund = new JMenuItem(Messages.getString("MainFrame.MarkAllRefund"));
	private JMenuItem miCancelMarkRefund = new JMenuItem(Messages.getString("MainFrame.CancelMarkRefund"));
//	private JMenuItem miChangeAmount = new JMenuItem(Messages.getString("MainFrame.ChangeAmount"));
	
	private ArrayList<DiscountTemplate> discountTemplateList = new ArrayList<>(); 
	private ArrayList<Promotion> promotionList = new ArrayList<>();
	private ArrayList<PayWay> paywayList = new ArrayList<>(); 
	private UserData onDutyUser = null;//在值班状态用户名称
	private HashMap<String, String> configsMap;
	private JTable tableGoods = new JTable();
	private GoodsTableModel modelGoods = new GoodsTableModel(this);
	private HashMap<String, Goods> mapGoods;//key = barcode
	private ArrayList<Goods> listNoBarcodeGoods = new ArrayList<>();
	private HashMap<String, Member> mapMember;
	private Member member;
	
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
		tableGoods.getColumnModel().getColumn(0).setPreferredWidth(200);//name
		tableGoods.getColumnModel().getColumn(1).setPreferredWidth(200);//barcode
		tableGoods.getColumnModel().getColumn(2).setPreferredWidth(50);//amount
		tableGoods.getColumnModel().getColumn(3).setPreferredWidth(50);//sell price
		tableGoods.getColumnModel().getColumn(4).setPreferredWidth(50);//member discount price
		tableGoods.getColumnModel().getColumn(5).setPreferredWidth(50);//modified price
		GoodsTableCellRenderer cellRender = new GoodsTableCellRenderer();
		cellRender.setHorizontalAlignment(JLabel.CENTER);
		tableGoods.setDefaultRenderer(Object.class,  cellRender);
		DefaultTableCellHeaderRenderer thr = new DefaultTableCellHeaderRenderer();
	    thr.setHorizontalAlignment(JLabel.CENTER);
	    tableGoods.getTableHeader().setDefaultRenderer(thr);
	    
		JScrollPane jspGoods = new JScrollPane(tableGoods, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		popupMenu.add(miDeleteItem);
		popupMenu.add(miDeleteAllItem);
		popupMenu.addSeparator();
		popupMenu.add(miChangeAmountPrice);
//		popupMenu.addSeparator();
//		popupMenu.add(miChangeAmount);
		popupMenu.addSeparator();
		popupMenu.add(miMarkRefund);
		popupMenu.add(miMarkAllRefund);
		popupMenu.add(miCancelMarkRefund);
//		tableGoods.setComponentPopupMenu(popupMenu);
		tableGoods.addMouseListener(new MouseAdapter(){
			public void mouseReleased(MouseEvent e){
                if (e.isPopupTrigger()){
                    JTable source = (JTable)e.getSource();
                    int row = source.rowAtPoint( e.getPoint() );
                    int column = source.columnAtPoint( e.getPoint() );

                    if (! source.isRowSelected(row)){
                        source.changeSelection(row, column, false, false);
                    }
                    //除了删除全部项, 其他的弹出式菜单不可应用于促销状况下的商品
                    ChoosedGoods cg = modelGoods.getData().get(row);
                    miDeleteItem.setEnabled(cg.promotion == null);
                    miChangeAmountPrice.setEnabled(cg.promotion == null);
                    miMarkRefund.setEnabled(cg.promotion == null);
                    miMarkAllRefund.setEnabled(cg.promotion == null);
                    miCancelMarkRefund.setEnabled(cg.promotion == null);
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					doChangePriceAmount();
				}
			}
		});
		
		tfSearchGoods.setPreferredSize(new Dimension(180, 35));
		tfSearchGoods.setMaximumSize(new Dimension(180, 35));
		tfSearchGoods.setMinimumSize(new Dimension(180, 35));
		JLabel lbSearch = new JLabel(Messages.getString("MainFrame.Search") + "[F1]");
		JPanel pSearch = new JPanel(new BorderLayout());
		pSearch.add(lbSearch, BorderLayout.WEST);
		pSearch.add(tfSearchGoods, BorderLayout.CENTER);
		Insets insets = new Insets(5,5,5,5);
		
		JPanel pMember = new JPanel(new GridBagLayout());
		pMember.setBorder(BorderFactory.createLineBorder(new Color(187, 79, 32)));
		JLabel lbMember = new JLabel(Messages.getString("MainFrame.Member")+"[F4]");
		tfMember.setPreferredSize(new Dimension(180, 35));
		tfMember.setMinimumSize(new Dimension(180, 35));
		tfMember.setMaximumSize(new Dimension(180, 35));
		pMember.add(lbMember, 			new GridBagConstraints(0, 0, 1, 1,0,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,0,0,0),0,0));
		pMember.add(tfMember, 			new GridBagConstraints(1, 0, 1, 1,0,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets,0,0));
		pMember.add(btnQueryMember,		new GridBagConstraints(2, 0, 1, 1,0,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets,0,0));
		pMember.add(btnAddMember, 		new GridBagConstraints(3, 0, 1, 1,0,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets,0,0));
		pMember.add(btnClearMember, 	new GridBagConstraints(4, 0, 1, 1,0,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets,0,0));
		pMember.add(lbMemberInfo, 		new GridBagConstraints(5, 0, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets,0,0));
		
		btnCheckout.setPreferredSize(new Dimension(200, 70));
		btnCheckout.setMaximumSize(new Dimension(200, 70));
		btnCheckout.setMinimumSize(new Dimension(200, 70));
		JPanel pCheckout = new JPanel(new GridBagLayout());
		pCheckout.setBorder(BorderFactory.createLineBorder(new Color(115, 164, 55)));
		pCheckout.add(lbGoodsAmount, new GridBagConstraints(0, 0, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.NONE, insets,0,0));
		pCheckout.add(lbGoodsOriginPrice, new GridBagConstraints(1, 0, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.NONE, insets,0,0));
		pCheckout.add(lbGoodsPrice,  new GridBagConstraints(0, 1, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.NONE, insets,0,0));
		pCheckout.add(btnCheckout,   new GridBagConstraints(2, 0, 1, 2,1,1, GridBagConstraints.EAST, GridBagConstraints.NONE, insets,0,0));
		JPanel pFunction = new JPanel(new GridBagLayout());
		
		int height = btnPreOrderMgr.getSize().height;
		int width = 140;
		btnPreOrderMgr.setPreferredSize(new Dimension(width, height));
		btnPreOrderMgr.setMaximumSize(new Dimension(width, height));
		btnPreOrderMgr.setMinimumSize(new Dimension(width, height));
//		btnOpenCashdrawer.setPreferredSize(new Dimension(width, height));
//		btnOpenCashdrawer.setMaximumSize(new Dimension(width, height));
//		btnOpenCashdrawer.setMinimumSize(new Dimension(width, height));
//		btnOpenCashdrawer.setBorder(BorderFactory.createLineBorder(Color.red));
		
		JPanel pFunction1 = new JPanel();
		pFunction1.add(btnNoBarcodeItem, new GridBagConstraints(0, 1, 1, 1,0,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets,0,0));
		pFunction1.add(btnShiftWork,	 new GridBagConstraints(1, 1, 1, 1,0,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets,0,0));
		pFunction1.add(btnOpenCashdrawer,new GridBagConstraints(2, 1, 1, 1,0,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets,0,0));
		pFunction1.add(btnTodayIndent,   new GridBagConstraints(3, 1, 1, 1,0,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets,0,0));
		
		pFunction.add(pSearch, 			new GridBagConstraints(0, 0, 1, 1,0,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets,0,0));
		pFunction.add(btnPreOrder,		new GridBagConstraints(1, 0, 1, 1,0,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets,0,0));
		pFunction.add(btnPreOrderMgr,	new GridBagConstraints(2, 0, 1, 1,0,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets,0,0));
		
		pFunction.add(pCheckout,		new GridBagConstraints(3, 0, 1, 2,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets,0,0));
		
		pFunction.add(pFunction1,		new GridBagConstraints(0, 1, 3, 1,0,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets,0,0));
		
		pFunction.add(pMember,			new GridBagConstraints(0, 2, 5, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets,0,0));
		pFunction.setPreferredSize(new Dimension(180, 150));
		
		
		
		lbStatusLogin.setBorder(BorderFactory.createLineBorder(Color.gray));
		lbStatusLogin.setPreferredSize(new Dimension(350, (int)lbStatusLogin.getPreferredSize().getHeight()));
		lbIndentInfo.setBorder(BorderFactory.createLineBorder(Color.gray));
		lbCurrentTime.setBorder(BorderFactory.createLineBorder(Color.gray));
		lbHotkeys.setBorder(BorderFactory.createLineBorder(Color.gray));
		JPanel pStatus = new JPanel(new GridBagLayout());
		pStatus.add(lbStatusLogin, new GridBagConstraints(0, 0, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,0,0,0),0,0));
		pStatus.add(lbIndentInfo,  new GridBagConstraints(1, 0, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,3,0,0),0,0));
		pStatus.add(lbCurrentTime, new GridBagConstraints(2, 0, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,3,0,0),0,0));
		
		this.getContentPane().setLayout(new GridBagLayout());
		this.getContentPane().add(jspGoods, 	new GridBagConstraints(0, 0, 1, 1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,0,0,0),0,0));
		this.getContentPane().add(pFunction, 	new GridBagConstraints(0, 1, 1, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,0,0,0),0,0));
//		this.getContentPane().add(lbHotkeys, 	new GridBagConstraints(0, 2, 1, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0),0,0));
		this.getContentPane().add(pStatus, 		new GridBagConstraints(0, 3, 1, 1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0),0,0));
		
		BarcodeScannerListener listener = new BarcodeScannerListener();
		
		btnCheckout.addActionListener(this);
		btnOpenCashdrawer.addActionListener(this);
		btnShiftWork.addActionListener(this);
//		btnChangeAmount.addActionListener(this);
//		btnDeleteItem.addActionListener(this);
		btnPreOrder.addActionListener(this);
//		btnRefund.addActionListener(this);
		btnPreOrderMgr.addActionListener(this);
		btnAddMember.addActionListener(this);
		btnQueryMember.addActionListener(this);
		btnClearMember.addActionListener(this);
		miDeleteAllItem.addActionListener(this);
		miMarkRefund.addActionListener(this);
		miMarkAllRefund.addActionListener(this);
		miCancelMarkRefund.addActionListener(this);
		miDeleteItem.addActionListener(this);
		miChangeAmountPrice.addActionListener(this);
		btnNoBarcodeItem.addActionListener(this);
//		miChangeAmount.addActionListener(this);
		btnTodayIndent.addActionListener(this);
		
        btnCheckout.addKeyListener(listener);
        btnOpenCashdrawer.addKeyListener(listener);
        btnShiftWork.addKeyListener(listener);
        btnTodayIndent.addKeyListener(listener);
//        btnDeleteItem.addKeyListener(listener);
//        btnRefund.addKeyListener(listener);
        btnPreOrder.addKeyListener(listener);
        btnPreOrderMgr.addKeyListener(listener);
        tableGoods.addKeyListener(listener);
        btnAddMember.addKeyListener(listener);
        btnQueryMember.addKeyListener(listener);
        btnClearMember.addKeyListener(listener);
        btnNoBarcodeItem.addKeyListener(listener);
        
        tfSearchGoods.addKeyListener(new KeyAdapter(){
        	public void keyTyped(KeyEvent e) {
        		if (e.getKeyChar() == KeyEvent.VK_ENTER){
        			lookforAnObjectByValue(tfSearchGoods.getText());
        			tfSearchGoods.setText("");
        		} else if (e.getKeyChar() == ' '){
        			//SPACE as the hotkey of checkout
        			//in keyTyped function, can get keychar only, cannot get keycode
        			e.consume();
        		}
        	}
        	
        	public void keyPressed(KeyEvent e) {
				if (e.getID() != KeyEvent.KEY_PRESSED)
					return;
				if (keyCodeIsHotkey(e)){
					doHotkeyResponse(e);
				}
			}
        });
        
        tfMember.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e) {
				if (e.getID() != KeyEvent.KEY_PRESSED)
					return;
				if (e.getKeyCode() == KeyEvent.VK_ENTER){
					doLookforMember();
					tfSearchGoods.requestFocusInWindow();
				} else if (keyCodeIsHotkey(e) && e.getKeyCode() != KeyEvent.VK_SPACE){//allow input space for member query, do not response checkout function
					doHotkeyResponse(e);
				}
			}
		});
        
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
		loadPromotion();
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
						c2.setCategory1(c1);
						if (c2.getGoods() != null) {
							for (Goods g : c2.getGoods()) {
								g.setCategory2(c2);
								if (g.getBarcode() != null && g.getBarcode().length() > 0)
									mapGoods.put(g.getBarcode(), g);
								else 
									listNoBarcodeGoods.add(g);
							}
						}
					}
				}
			}
		}
	}
	
	
	public ArrayList<Goods> getListNoBarcodeGoods() {
		return listNoBarcodeGoods;
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
	
	public Member getMemberByMemberCard(String memberCode){
		return mapMember.get(memberCode);
	}
	
	/**
	 * Hazily look for member, 
	 * @param key , can match name, membercard, phone number
	 * @return
	 */
	public ArrayList<Member> getMemberHazily(String key){
		ArrayList<Member> ms = new ArrayList<>();
		Iterator<Member> it = mapMember.values().iterator();
		while(it.hasNext()){
			Member m = it.next();
			if (m.getName().toLowerCase().indexOf(key.toLowerCase()) >= 0){
				ms.add(m);
			} else if (m.getMemberCard().indexOf(key.toLowerCase()) >= 0){
				ms.add(m);
			} else if (m.getTelephone() != null && m.getTelephone().indexOf(key) >= 0){
				ms.add(m);
			}
		}
		return ms;
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
	
	private void loadPromotion(){
		ArrayList<Promotion> ps = HttpUtil.loadPromotion(this);
		promotionList.clear();
		if (ps != null)
			promotionList.addAll(ps);
	}
	
	public ArrayList<Promotion> getPromotionList(){
		return promotionList;
	}
	private void loadPayWay(){
		ArrayList<PayWay> pws = HttpUtil.loadPayWay(this);
		paywayList.clear();
		if (pws != null){
			paywayList.addAll(pws);
			Collections.sort(paywayList, new Comparator<PayWay>(){

				@Override
				public int compare(PayWay o1, PayWay o2) {
					return o1.getSequence() - o2.getSequence();
				}});
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

	private void doChangePriceAmount(){
		int row = tableGoods.getSelectedRow();
		if (row < 0)
			return;
		ChoosedGoods cg = modelGoods.getObjectAt(row);
		ChangeGoodsPriceDialog dlg = new ChangeGoodsPriceDialog(this, cg.goods);
		double originPrice = cg.goods.getSellPrice();
		if (cg.modifiedPrice > 0)
			originPrice = cg.modifiedPrice;
		else if (member != null){
			originPrice = cg.goods.getSellPrice() * member.getDiscountRate();
			originPrice = Double.parseDouble(CommonTools.transferDouble2Scale(originPrice));//剔除过长的小数位
		}
		int originQuantity = cg.amount;
		double originDiscount = 100;
//		if (member != null)
//			originDiscount = member.getDiscountRate() * 100;
		dlg.setValue(originPrice, originQuantity, originDiscount);
		dlg.setVisible(true);
		if (!dlg.isCancel()){
			if (dlg.getModifiedPrice() != originPrice){
				cg.modifiedPrice = dlg.getModifiedPrice();
			}
			cg.amount = dlg.getQuantity();
			
			modelGoods.fireTableDataChanged();
			calculatePrice();
		}
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
	
	private void doDeleteAllItem(){
		modelGoods.deleteAllItem();
		modelGoods.fireTableDataChanged();
		calculatePrice();
	}

	private void doLookforMember(){
		lbMemberInfo.setText("");
		member = null;
		if (tfMember.getText() == null || tfMember.getText().length() == 0)
			return;
		ArrayList<Member> ms = getMemberHazily(tfMember.getText());
		if (ms == null || ms.isEmpty()){
			
			JOptionPane.showMessageDialog(MainFrame.this, Messages.getString("CheckoutDialog.NofindMember") + tfMember.getText());
			return;
		} else if (ms.size() == 1){
			member = ms.get(0);
		} else {
			MemberListDialog dlg = new MemberListDialog(this, ms, 1000, 600);
			dlg.setVisible(true);
			member = dlg.getChoosedMember();
		}
		if (member != null){
			//reload member data from server
			member = HttpUtil.doLoadMember(MainFrame.this, getOnDutyUser(), member.getMemberCard());
			//store into local memory
			mapMember.put(member.getMemberCard(), member);
			
			modelGoods.setMember(member);
			calculatePrice();
		}
		showMemberInfo(member);
	}
	
	private void showMemberInfo(Member m){
		if (m == null){
			tfMember.setText("");
			lbMemberInfo.setText("");
		} else {
			tfMember.setText(m.getName());
			lbMemberInfo.setText(Messages.getString("CheckoutDialog.MemberInfo.Name")+ m.getName() + ", " 
				+ Messages.getString("CheckoutDialog.MemberInfo.DiscountRate") + m.getDiscountRate() + ", "
				+ Messages.getString("CheckoutDialog.MemberInfo.Score") + CommonTools.transferDouble2Scale(m.getScore()) + ", "
				+ Messages.getString("CheckoutDialog.MemberInfo.Balance") + CommonTools.transferDouble2Scale(m.getBalanceMoney()) + ", "
				+ "Tel " + m.getTelephone());
		}
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
	 * firstly, look for by barcode; if not find, then look for by name; if both failed, then look for from server by barcode
	 * @param value
	 */
	private void lookforAnObjectByValue(String value){
		if (value == null || value.length() == 0)
			return;
		Goods goods = mapGoods.get(value);
		if (goods == null){
			goods = lookforAnObjectByName(value);
			if (goods == null){
				goods = HttpUtil.loadGoodsByBarcode(this, value);
				if (goods == null){
					JOptionPane.showMessageDialog(this, Messages.getString("MainFrame.NoFindByBarcode") + value);
					return;
				} else {
					//replace local object
					mapGoods.put(value, goods);
				}
			}
		}
		ChoosedGoods cg = new ChoosedGoods();
		cg.amount = 1;
		cg.goods = goods;
		modelGoods.addItem(cg);
		modelGoods.fireTableDataChanged();
		calculatePrice();
	}
	
	/**
	 * 商品加入购物列表
	 * @param goods
	 */
	private void addGoodsIntoCart(Goods goods){
		
	}
	
	private Goods lookforAnObjectByName(String name){
		ArrayList<Goods> resultlist = new ArrayList<Goods>();
		Iterator<Goods> it = mapGoods.values().iterator();
		while(it.hasNext()){
			Goods g = it.next();
			if (g.getName().toLowerCase().indexOf(name.toLowerCase()) >= 0){
				resultlist.add(g);
			}
		}
		if (resultlist.isEmpty()){
//			JOptionPane.showMessageDialog(this, Messages.getString("MainFrame.NoFindByBarcode") + name);
			return null;
		}else if (resultlist.size() == 1){
			return resultlist.get(0);
		} else {
			SearchObjectListDialog dlg = new SearchObjectListDialog(this, resultlist, WINDOW_WIDTH, WINDOW_HEIGHT);
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
			lbGoodsAmount.setText("");
			lbGoodsOriginPrice.setText("");
			lbGoodsPrice.setText("");
			return;
		}
		double price = CommonTools.getGoodsDiscountPrice(modelGoods.getData(), member);
		double originPrice = CommonTools.getGoodsOriginPrice(modelGoods.getData());
		int itemamount = 0;
		for(ChoosedGoods cg : modelGoods.getData()){
			itemamount += Math.abs(cg.amount); //sometimes the refund goods make the amount negative
		}
		lbGoodsAmount.setText(Messages.getString("MainFrame.lbIndentInfo.itemamount") + itemamount);
		lbGoodsPrice.setText(Messages.getString("MainFrame.lbIndentInfo.price") + CommonTools.transferNumberByPM(price, ""));
		lbGoodsOriginPrice.setText(Messages.getString("MainFrame.lbIndentInfo.OriginPrice") + CommonTools.transferNumberByPM(originPrice, ""));
	}
	
	public void putFocusOnTFBarcode(){
		tfSearchGoods.requestFocusInWindow();
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnCheckout){
			doCheckout();
			tfSearchGoods.requestFocusInWindow();
		} else if (e.getSource() == btnShiftWork){
			doSwiftWork();
			tfSearchGoods.requestFocusInWindow();
		} else if (e.getSource() == btnOpenCashdrawer){
			doOpenCashdrawer(true);
			tfSearchGoods.requestFocusInWindow();
		} 
//		else if (e.getSource() == miChangeAmount){
//			doChangeAmount();
//		} 
		else if (e.getSource() == btnPreOrder){ 
			doPreOrder();
			tfSearchGoods.requestFocusInWindow();
		} else if (e.getSource() == btnPreOrderMgr){
			doPreOrderMgr();
			tfSearchGoods.requestFocusInWindow();
		} else if (e.getSource() == btnQueryMember){
			doLookforMember();
			tfSearchGoods.requestFocusInWindow();
		} else if (e.getSource() == btnAddMember){
			MemberDialog dlg = new MemberDialog(this);
			dlg.setVisible(true);
			member = dlg.getMember();
			if (member != null){
				showMemberInfo(member);
				modelGoods.setMember(member);
				mapMember.put(member.getMemberCard(), member);
				calculatePrice();
			}
			tfSearchGoods.requestFocusInWindow();
		} else if (e.getSource() == btnClearMember){
			member = null;
			showMemberInfo(null);
			modelGoods.setMember(null);
			calculatePrice();
			tfSearchGoods.requestFocusInWindow();
		} else if (e.getSource() == miDeleteItem){
			doDeleteItem();
			tfSearchGoods.requestFocusInWindow();
		} else if (e.getSource() == miDeleteAllItem){
			doDeleteAllItem();
			tfSearchGoods.requestFocusInWindow();
		} else if (e.getSource() == miChangeAmountPrice){
			doChangePriceAmount();
			tfSearchGoods.requestFocusInWindow();
		} else if (e.getSource() == btnNoBarcodeItem){
			showNoBarcodeItemSelectionDialog();
			tfSearchGoods.requestFocusInWindow();
		} else if (e.getSource() == miMarkRefund){
			doMarkRefund();
			tfSearchGoods.requestFocusInWindow();
		} else if (e.getSource() == miMarkAllRefund){
			doMarkAllRefund();
			tfSearchGoods.requestFocusInWindow();
		} else if (e.getSource() == miCancelMarkRefund){
			doCancelMarkRefund();
			tfSearchGoods.requestFocusInWindow();
		} else if (e.getSource() == btnTodayIndent){
			doQueryTodayIndent();
			tfSearchGoods.requestFocusInWindow();
		}
	}
	
	private void doQueryTodayIndent(){
		QueryTodayIndentDialog dlg = new QueryTodayIndentDialog(this);
		dlg.setVisible(true);
	}
	
	private void doCancelMarkRefund(){
		ChoosedGoods cg = modelGoods.getObjectAt(tableGoods.getSelectedRow());
		cg.amount = Math.abs(cg.amount);//confirm this value keep positive
		modelGoods.fireTableRowsUpdated(tableGoods.getSelectedRow(), tableGoods.getSelectedRow());
		calculatePrice();
	}
	
	private void doMarkRefund(){
		ChoosedGoods cg = modelGoods.getObjectAt(tableGoods.getSelectedRow());
		cg.amount = Math.abs(cg.amount) * (-1);//confirm this value keep negative
		modelGoods.fireTableRowsUpdated(tableGoods.getSelectedRow(), tableGoods.getSelectedRow());
		calculatePrice();
	}
	
	private void doMarkAllRefund(){
		for (int i = 0; i < modelGoods.getRowCount(); i++) {
			ChoosedGoods cg = modelGoods.getObjectAt(i);
			cg.amount = Math.abs(cg.amount) * (-1);//confirm this value keep negative
		}
		
		modelGoods.fireTableRowsUpdated(0, modelGoods.getRowCount() - 1);
		calculatePrice();
	}
	
	private void showNoBarcodeItemSelectionDialog(){
		NoBarcodeGoodsDialog dlg = new NoBarcodeGoodsDialog(this, Messages.getString("MainFrame.NoBarcodeItemMgr"));
		dlg.setVisible(true);
		if (dlg.isCanceled) return;
		ArrayList<ChoosedGoods> listcg = dlg.getChoosedGoods();
		for (int i = 0; i < listcg.size(); i++) {
			modelGoods.addItem(listcg.get(i));
			modelGoods.fireTableDataChanged();
			calculatePrice();
		}
	}
	
	private void doPreOrderMgr(){
		PreorderMgmtDialog dlg = new PreorderMgmtDialog(this);
		dlg.setVisible(true);
	}
	
	private void doPreOrder(){
		if (modelGoods.getRowCount() == 0)
			return;
		//first check if there are refund goods in the list
		ArrayList<ChoosedGoods> items = modelGoods.getData();
		for(ChoosedGoods cg : items){
			if (cg.amount <= 0){
				JOptionPane.showMessageDialog(this, "There are some goods with negative amount, cannot do preorder for these.");
				return;
			}
		}
		PreOrderCheckoutDialog dlg = new PreOrderCheckoutDialog(this, Messages.getString("MainFrame.PreOrder"), true, modelGoods.getData(), member); //$NON-NLS-1$
		dlg.setVisible(true);
	}
	
	public void doOpenCashdrawer(boolean needpassword){
		if (needpassword){
			String code = JOptionPane.showInputDialog(this, Messages.getString("MainFrame.InputCodeOfOpenCashdrawer"));
			if (code == null)
				return;
			if (!configsMap.get(ConstantValue.CONFIGS_OPENCASHDRAWERCODE).equals(code)){
				JOptionPane.showMessageDialog(this, Messages.getString("MainFrame.ErrorCashdrawerCode"));
				return;
			}
		}
		Socket socket = null;
		OutputStream socketOut = null;
		OutputStreamWriter writer = null;
		try {
			socket = new Socket(printerIP, 9100);//打印机默认端口是9100, 如果某些型号打印机不是这个, 单独配置
			socket.setSoTimeout(1000);
			socketOut = socket.getOutputStream();
			writer = new OutputStreamWriter(socketOut, "GBK");
			char[] c = {27, 'p', 0, 60, 240};
			writer.write(c);
			writer.flush();
		} catch (IOException e) {
			logger.error("", e);
		} finally{
			try {
				if (socket != null)
					socket.close();
				if (socketOut != null)
					socketOut.close();
				if (writer != null)
					writer.close();
			} catch (IOException e) {}
		}
	}
	
	/**
	 * 清除上次留下的销售信息
	 */
	public void clearTable(){
		modelGoods.deleteAllItem();
		modelGoods.fireTableDataChanged();
		calculatePrice();
		member = null;
		modelGoods.setMember(null);
		showMemberInfo(null);
	}
	
	private void doCheckout(){
		if (modelGoods.getRowCount() == 0)
			return;
		
		CheckoutDialog dlg = new CheckoutDialog(this, Messages.getString("MainFrame.CheckoutTitle"), true, modelGoods.getData(), member); //$NON-NLS-1$
		dlg.setVisible(true);
		
	}
	
	private void doRefund(){
		if (modelGoods.getRowCount() == 0)
			return;
		
		RefundDialog dlg = new RefundDialog(this, Messages.getString("MainFrame.Refund"), true, modelGoods.getData(), member); //$NON-NLS-1$
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
//					Messages.getString("MainFrame.ShiftWorkPrint"), 
					Messages.getString("Cancel") };
			int n = JOptionPane.showOptionDialog(this, msg, Messages.getString("MainFrame.ShiftWorkTitle"),
					JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
			if (n == 0) {
				doOffDuty(onDutyUser, false);
			} 
//			else if (n == 1) {
//				doOffDuty(onDutyUser, true);
//			}
		}
	}
	
	public HashMap<String, String> getConfigsMap() {
		return configsMap;
	}

	public void setConfigsMap(HashMap<String, String> configsMap) {
		this.configsMap = configsMap;
	}
	
	private boolean keyCodeIsHotkey(KeyEvent e){
		if (e.getKeyCode() == KeyEvent.VK_END
				|| e.getKeyCode() == KeyEvent.VK_DELETE
				|| e.getKeyCode() == KeyEvent.VK_DOWN
				|| e.getKeyCode() == KeyEvent.VK_UP
				|| e.getKeyCode() == KeyEvent.VK_SPACE
				|| e.getKeyCode() == KeyEvent.VK_F1
				|| e.getKeyCode() == KeyEvent.VK_F2
				|| e.getKeyCode() == KeyEvent.VK_F3
				|| e.getKeyCode() == KeyEvent.VK_F4
				|| e.getKeyCode() == KeyEvent.VK_F5
				|| e.getKeyCode() == KeyEvent.VK_F6
				|| e.getKeyCode() == KeyEvent.VK_F7
				|| e.getKeyCode() == KeyEvent.VK_F8
				|| e.getKeyCode() == KeyEvent.VK_F9
				|| e.getKeyCode() == KeyEvent.VK_F10
				|| e.getKeyCode() == KeyEvent.VK_F11
				|| e.getKeyCode() == KeyEvent.VK_F12
				|| (e.getKeyCode() == KeyEvent.VK_M && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0))
				|| (e.getKeyCode() == KeyEvent.VK_G && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0))
				){
			return true;
		}
		return false;
	}
	
	private void doHotkeyResponse(KeyEvent e){
		if (e.getKeyCode() == KeyEvent.VK_SPACE){
			doCheckout();
		} else if (e.getKeyCode() == KeyEvent.VK_DELETE){
			
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN){
			
		} else if (e.getKeyCode() == KeyEvent.VK_F2){
			doChangePriceAmount();
		} else if (e.getKeyCode() == KeyEvent.VK_F3){
			if (tableGoods.getRowCount() > 0){
				int selectrow = tableGoods.getSelectedRow();
				selectrow++;
				if (selectrow < tableGoods.getRowCount())
					tableGoods.setRowSelectionInterval(selectrow, selectrow);
				else {
					selectrow = 0;
					tableGoods.setRowSelectionInterval(selectrow, selectrow);
				}
			}
		} else if (e.getKeyCode() == KeyEvent.VK_F4){
			tfMember.requestFocusInWindow();
		} else if (e.getKeyCode() == KeyEvent.VK_F1 || 
				(e.getKeyCode() == KeyEvent.VK_G && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0))){
			tfSearchGoods.requestFocusInWindow();
		} else if (e.getKeyCode() == KeyEvent.VK_F5){
			showNoBarcodeItemSelectionDialog();
		} else if (e.getKeyCode() == KeyEvent.VK_F7){
			doOpenCashdrawer(true);
		} else if (e.getKeyCode() == KeyEvent.VK_END){
			
		}          
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
		StartingWaitDialog waitDlg = new StartingWaitDialog();
		waitDlg.setVisible(true);
		
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
		MainFrame.printerIP = prop.getProperty("printerIP");
		MainFrame.FONT_PRINTTICKET = prop.getProperty("printFont");
		final MainFrame f = new MainFrame();
		f.setSize(MainFrame.WINDOW_WIDTH, MainFrame.WINDOW_HEIGHT);
//		f.setExtendedState(JFrame.MAXIMIZED_BOTH);
//		f.setUndecorated(true);
		f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		f.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				if (JOptionPane.showConfirmDialog(f, "Do you want to quit this system?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
					System.exit(0);
				}
			}
		});
		waitDlg.setVisible(false);
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
						lookforAnObjectByValue(barcode.toString());
					}
					e.consume();//must consume the ENTER, otherwise it will invoke the button's click event if this button is on focused
					barcode.delete(0, barcode.length());
				} else if (keyCodeIsHotkey(e)){
					doHotkeyResponse(e);
				} else {
					barcode.append(e.getKeyChar());
				}
			}
		}
	}
	
	class GoodsTableCellRenderer extends DefaultTableCellRenderer{
		private Color txtColor = new Color(151, 151, 151);
		private Color darkGreen = new Color(0, 247, 123);
		@Override
	    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	    {
	        final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	        if (isSelected){
	        	super.setForeground(table.getSelectionForeground());
	        	super.setBackground(table.getSelectionBackground());
	        } else {
		        c.setForeground(Color.black);
//	        	c.setForeground(txtColor);
		        ChoosedGoods cg = modelGoods.getObjectAt(row);
		        if (cg.amount < 0)
		        	c.setBackground(Color.lightGray);
		        else 
		        	c.setBackground(Color.white);
		        if (cg.promotion != null){
		        	c.setBackground(darkGreen);
		        }
	        }
	        return c;
	    }
	}
}
