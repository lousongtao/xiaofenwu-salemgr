package com.shuishou.salemgr;

import java.awt.Font;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ConstantValue {
	public static final DateFormat DFYMDHM = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	public static final DateFormat DFYMDHMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final DateFormat DFHMS = new SimpleDateFormat("HH:mm:ss");
	public static final DateFormat DFYMD = new SimpleDateFormat("yyyy-MM-dd");
	public static final DateFormat DFWEEK = new SimpleDateFormat("EEE");
	public static final DateFormat DFYMDHMS_2 = new SimpleDateFormat("yyyyMMddHHmmss");
	
	public static final String DATE_PATTERN_YMD = "yyyy-MM-dd";
	public static final String DATE_PATTERN_YMDHMS = "yyyy-MM-dd HH:mm:ss";
	
	public static final String FORMAT_DOUBLE = "%.2f";
	
	public static final Font FONT_25BOLD = new Font(null, Font.BOLD, 25);
	public static final Font FONT_25PLAIN = new Font(null, Font.PLAIN, 25);	
	public static final Font FONT_20BOLD = new Font(null, Font.BOLD, 20);
	public static final Font FONT_20PLAIN = new Font(null, Font.PLAIN, 20);
	public static final Font FONT_15BOLD = new Font(null, Font.BOLD, 15);
	public static final Font FONT_15PLAIN = new Font(null, Font.PLAIN, 15);
	public static final Font FONT_10BOLD = new Font(null, Font.BOLD, 10);
	public static final Font FONT_10PLAIN = new Font(null, Font.PLAIN, 10);
	
	public static final String LANGUAGE_CHINESE = "cn";
	public static final String LANGUAGE_ENGLISH = "en";
	
	public static final String PERMISSION_QUERY_USER = "QUERY_USER";
	public static final String PERMISSION_CREATE_USER = "CREATE_USER";
	public static final String PERMISSION_EDIT_MENU = "EDIT_MENU";
	public static final String PERMISSION_QUERY_ORDER = "QUERY_ORDER";
	public static final String PERMISSION_UPDATE_ORDER = "UPDATE_ORDER";
	public static final String PERMISSION_CHANGE_CONFIRMCODE = "CHANGE_CONFIRMCODE";
	public static final String PERMISSION_QUERY_DESK = "QUERY_DESK";
	public static final String PERMISSION_EDIT_DESK = "EDIT_DESK";
	public static final String PERMISSION_EDIT_PRINTER = "EDIT_PRINTER";
	
	public final static String SPLITTAG_PERMISSION = ";";
	
	public static final String TYPE_CATEGORY1INFO = "C1";
	public static final String TYPE_CATEGORY2INFO = "C2";
	public static final String TYPE_DISHINFO = "DISH";
	
	public static final String CATEGORY_DISHIMAGE_ORIGINAL = "dishimage_original";
	public static final String CATEGORY_DISHIMAGE_MIDDLE = "dishimage_middle";
	public static final String CATEGORY_DISHIMAGE_SMALL = "dishimage_small";
	public static final String CATEGORY_PRINTTEMPLATE = "printtemplate";
	
	public static final int DISHIMAGE_WIDTH_SMALL = 50;
	public static final int DISHIMAGE_HEIGHT_SMALL = 50;
	public static final int DISHIMAGE_WIDTH_MIDDLE = 280;
	public static final int DISHIMAGE_HEIGHT_MIDDLE = 300;
	
	public static final String CSS_MENUTREENODE_ICON_SIZE = "menutreenode-icon-size";
	
	public static final byte INDENT_STATUS_OPEN = 1;
	public static final byte INDENT_STATUS_CLOSED = 2;
	public static final byte INDENT_STATUS_PAID = 3;
	public static final byte INDENT_STATUS_CANCELED = 4;
	
	public static final byte INDENT_OPERATIONTYPE_ADD = 1;
	public static final byte INDENT_OPERATIONTYPE_DELETE = 2;
	public static final byte INDENT_OPERATIONTYPE_CANCEL = 3;
	public static final byte INDENT_OPERATIONTYPE_PAY = 4;
	public static final byte INDENT_OPERATIONTYPE_REFUND = 5;
	
	//付款方式
	public static final String INDENT_PAYWAY_CASH = "cash";//现金
	public static final String INDENT_PAYWAY_BANKCARD = "bankcard";//刷卡
	public static final String INDENT_PAYWAY_MEMBER = "member";//会员
	
	public static final byte INDENTDETAIL_OPERATIONTYPE_ADD = 1;
	public static final byte INDENTDETAIL_OPERATIONTYPE_DELETE = 2;
	public static final byte INDENTDETAIL_OPERATIONTYPE_CHANGEAMOUNT = 5;
	
	public static final byte MENUCHANGE_TYPE_SOLDOUT = 0;
	
	public static final byte PRINT_TYPE_TOGETHER = 0;
	public static final byte PRINT_TYPE_SEPARATELY = 1;
	
	public static final byte SHIFTWORK_ONWORK = 0;
	public static final byte SHIFTWORK_OFFWORK = 1;
	
	public static final byte DISH_CHOOSEMODE_DEFAULT = 1;
	public static final byte DISH_CHOOSEMODE_SUBITEM = 2;
	public static final byte DISH_CHOOSEMODE_POPINFOCHOOSE = 3;
	public static final byte DISH_CHOOSEMODE_POPINFOQUIT = 4;
	
	public static final byte DISH_PURCHASETYPE_UNIT = 1;
	public static final byte DISH_PURCHASETYPE_WEIGHT = 2;
	

	public static final String CONFIGS_CONFIRMCODE = "CONFIRMCODE";
	public static final String CONFIGS_OPENCASHDRAWERCODE = "OPENCASHDRAWERCODE";
	public static final String CONFIGS_LANGUAGEAMOUNT = "LANGUAGEAMOUNT";
	public static final String CONFIGS_FIRSTLANGUAGENAME= "FIRSTLANGUAGENAME";
	public static final String CONFIGS_FIRSTLANGUAGEABBR = "FIRSTLANGUAGEABBR";
	public static final String CONFIGS_SECONDLANGUAGENAME= "SECONDLANGUAGENAME";
	public static final String CONFIGS_SECONDLANGUAGEABBR = "SECONDLANGUAGEABBR";
	
	public static final String TICKET_TEMPLATE_PURCHASE = "/purchase_ticket.json";
	public static final String TICKET_TEMPLATE_REFUND = "/refund_ticket.json";
	public static final String TICKET_TEMPLATE_PREBUY = "/preorder_ticket.json";
	public static final String TICKET_TEMPLATE_PREBUY_ALL = "/preorder_ticket_all.json";
	
	public static final byte INDENT_TYPE_ORDER = 1;//普通订单
	public static final byte INDENT_TYPE_REFUND = 2;//退货单
	public static final byte INDENT_TYPE_PREBUY_PAID = 3;//预购单已付款
	public static final byte INDENT_TYPE_PREBUY_UNPAID = 4;//预购单未付款
	
	public static final int INDENT_TYPE_PREBUY_FINISHED = 5;//预购单完结,已经转为订单
	public static final int INDENT_TYPE_ORDER_FROMPREBUY = 6;//预购单完结,已经转为订单
	
	public static final int PROMOTION_GOODS = 1;
	public static final int PROMOTION_CATEGORY2 = 2;
	public static final int PROMOTION_CATEGORY1 = 3;
	
	public static final int PROMOTION_REWARD_BUYNREDUCEPRICE = 1; //买n个, 直接折扣x元
	public static final int PROMOTION_REWARD_BUYNDISCOUNT = 2; //买n个, 给予百分比折扣
	public static final int PROMOTION_REWARD_BUYNNEXTREDUCEPRICE = 3; //买n个, 下一个直接折扣x元
	public static final int PROMOTION_REWARD_BUYNNEXTDISCOUNT = 4; //买n个, 下一个给予百分比折扣
	public static final int PROMOTION_REWARD_BUYNA_NEXTBREDUCEPRICE = 5; //买n个A, 下一个B给予直接折扣
	public static final int PROMOTION_REWARD_BUYNA_NEXTBDISCOUNT = 6; //买n个A, 下一个B给予百分比折扣
	public static final int PROMOTION_REWARD_BUYNA_MB_ABDISCOUNT = 7; //买n个A + m个B, AB同时给予直接折扣
	public static final int PROMOTION_REWARD_BUYNA_MB_ABREDUCEPRICE = 8; //买n个A + m个B, AB同时给予百分比折扣
	
	public static final int DISCOUNTTYPE_RATE= 1;     //折扣类型-按比例折扣
	public static final int DISCOUNTTYPE_QUANTITY = 2;//折扣类型-直接减数量
}
