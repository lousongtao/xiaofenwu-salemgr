package com.shuishou.salemgr.http;

import java.awt.Window;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.shuishou.salemgr.ConstantValue;
import com.shuishou.salemgr.Messages;
import com.shuishou.salemgr.beans.Category1;
import com.shuishou.salemgr.beans.Category2;
import com.shuishou.salemgr.beans.CurrentDutyInfo;
import com.shuishou.salemgr.beans.DiscountTemplate;
import com.shuishou.salemgr.beans.Goods;
import com.shuishou.salemgr.beans.HttpResult;
import com.shuishou.salemgr.beans.Indent;
import com.shuishou.salemgr.beans.Member;
import com.shuishou.salemgr.beans.PayWay;
import com.shuishou.salemgr.beans.Promotion;
import com.shuishou.salemgr.beans.UserData;
import com.shuishou.salemgr.ui.MainFrame;

public class HttpUtil {

	private final static Logger logger = Logger.getLogger("HttpUtil");
	
	public static HttpClient getHttpClient(){
		HttpParams mHttpParams=new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(mHttpParams, 20*1000);
        HttpConnectionParams.setSoTimeout(mHttpParams, 20*1000);
        HttpConnectionParams.setSocketBufferSize(mHttpParams, 8*1024);
        HttpClientParams.setRedirecting(mHttpParams, true);
          
        HttpClient httpClient=new DefaultHttpClient(mHttpParams);
        return httpClient;
	}
	
    public static String getJSONObjectByGet(String uriString){
//        JSONObject resultJsonObject=null;
    	String result = null;
        if ("".equals(uriString)||uriString==null) {
            return null;
        }
        HttpClient httpClient=getHttpClient();
        StringBuilder urlStringBuilder=new StringBuilder(uriString);
        StringBuilder entityStringBuilder=new StringBuilder();
        HttpGet httpGet=new HttpGet(urlStringBuilder.toString());
        BufferedReader bufferedReader=null;
        HttpResponse httpResponse=null;
        try {
            httpResponse=httpClient.execute(httpGet); 
        } catch (Exception e) {
        	logger.error("", e);
        }
        if (httpResponse == null)
        	return null;
        int statusCode=httpResponse.getStatusLine().getStatusCode();
        HttpEntity httpEntity=httpResponse.getEntity();
        if (httpEntity!=null) {
            try {
                bufferedReader=new BufferedReader(new InputStreamReader(httpEntity.getContent(), "UTF-8"), 8*1024);
                String line=null;
                while ((line=bufferedReader.readLine())!=null) {
                    entityStringBuilder.append(line+"\n");
                }
                if (statusCode==HttpStatus.SC_OK) {
                	return entityStringBuilder.toString();
//                	resultJsonObject=new JSONObject(entityStringBuilder.toString());
                } else {
                	logger.error("Http Error: URl : "+ uriString 
                			+ "\nhttpcode : "+ httpResponse.getStatusLine().getStatusCode()
                			+ "\nresponse message : " + entityStringBuilder.toString());
                }
                
            } catch (Exception e) {
            	logger.error("", e);
            }
        }
        
        return null;
    }
    
    public static String getJSONObjectByPost(String path,Map<String, String> paramsHashMap, String encoding) {
    	String result = null;
//        JSONObject resultJsonObject = null;
        List<NameValuePair> nameValuePairArrayList = new ArrayList<NameValuePair>();
        if (paramsHashMap != null && !paramsHashMap.isEmpty()) {
            for (Map.Entry<String, String> entry : paramsHashMap.entrySet()) {
                nameValuePairArrayList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
          
        UrlEncodedFormEntity entity = null;
        try {
            entity = new UrlEncodedFormEntity(nameValuePairArrayList, encoding);
            HttpPost httpPost = new HttpPost(path);
            httpPost.setEntity(entity);
            HttpClient httpClient = getHttpClient();
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity != null) {
                try {
                    BufferedReader bufferedReader = new BufferedReader(  
                    new InputStreamReader(httpEntity.getContent(),"UTF-8"), 8 * 1024);
                    StringBuilder entityStringBuilder = new StringBuilder();
                    String line = null;
                    while ((line = bufferedReader.readLine()) != null) {
                        entityStringBuilder.append(line + "\n");
                    }
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
//                    	resultJsonObject = new JSONObject(entityStringBuilder.toString());
                    	return entityStringBuilder.toString();
                    } else {
                    	logger.error("Http Error: URl : "+ path + "\nparam : "+ paramsHashMap 
                    			+ "\nhttpcode : "+ httpResponse.getStatusLine().getStatusCode()
                    			+ "\nresponse message : " + entityStringBuilder.toString());
                    }
                    
                } catch (Exception e) {
                    logger.error("", e);
                }
            }
            
        } catch (Exception e) {
            logger.error("", e);
        }
        return null;
    } 
    
    public static ArrayList<Category1> loadGoods(MainFrame parent){
		String url = "goods/querygoods";
		String response = HttpUtil.getJSONObjectByGet(MainFrame.SERVER_URL + url);
		if (response == null){
			logger.error("get null from server for loading goods. URL = " + url);
			JOptionPane.showMessageDialog(parent, "get null from server for loading goods. URL = " + url);
			return null;
		}
		HttpResult<ArrayList<Category1>> result = new Gson().fromJson(response, new TypeToken<HttpResult<ArrayList<Category1>>>(){}.getType());
		if (!result.success){
			logger.error("return false while loading goods. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(parent, result.result);
			return null;
		}
		return result.data;
	}
    
    public static ArrayList<Member> loadMembers(MainFrame parent){
		String url = "member/queryallmember";
		String response = HttpUtil.getJSONObjectByGet(MainFrame.SERVER_URL + url);
		if (response == null){
			logger.error("get null from server for loading member. URL = " + url);
			JOptionPane.showMessageDialog(parent, "get null from server for loading member. URL = " + url);
			return null;
		}
		Gson gson = new GsonBuilder().setDateFormat(ConstantValue.DATE_PATTERN_YMDHMS).create();
		HttpResult<ArrayList<Member>> result = gson.fromJson(response, new TypeToken<HttpResult<ArrayList<Member>>>(){}.getType());
		if (!result.success){
			logger.error("return false while loading member. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(parent, result.result);
			return null;
		}
		return result.data;
	}
    
    public static Goods loadGoodsByBarcode(MainFrame parent, String barcode){
		String url = "goods/querygoodsbybarcode";
		Map<String, String> params = new HashMap<String, String>();
		params.put("barCode", barcode);
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params, "utf-8");
		if (response == null){
			logger.error("get null from server for loading goods by barcode " + barcode + ". URL = " + url);
			JOptionPane.showMessageDialog(parent, "get null from server for loading goods by barcode " + barcode + ". URL = " + url);
			return null;
		}
		Gson gson = new GsonBuilder().setDateFormat(ConstantValue.DATE_PATTERN_YMDHMS).create();
		HttpResult<Goods> result = gson.fromJson(response, new TypeToken<HttpResult<Goods>>(){}.getType());
		if (!result.success){
			logger.error("return false while loading goods by barcode " + barcode + ". URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(parent, result.result);
			return null;
		}
		return result.data;
	}
	
    public static HashMap<String, String> loadConfigsMap(MainFrame parent){
		String url = "common/queryconfigmap";
		String response = HttpUtil.getJSONObjectByGet(MainFrame.SERVER_URL + url);
		if (response == null){
			logger.error("get null from server for loading configs. URL = " + url);
			JOptionPane.showMessageDialog(parent, "get null from server for loading configs. URL = " + url);
			return null;
		}
		HttpResult<HashMap<String, String>> result = new Gson().fromJson(response, new TypeToken<HttpResult<HashMap<String, String>>>(){}.getType());
		if (!result.success){
			logger.error("return false while loading configs. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(parent, result.result);
			return null;
		}
		return result.data;	
	}
	
    public static CurrentDutyInfo loadShiftDutyInfo(MainFrame parent){
		String url = "management/getcurrentduty";
		String response = HttpUtil.getJSONObjectByGet(MainFrame.SERVER_URL + url);
		if (response == null){
			logger.error("get null from server for duty employee info. URL = " + url);
			JOptionPane.showMessageDialog(parent, "get null from server for duty employee info. URL = " + url);
			return null;
		}
		HttpResult<CurrentDutyInfo> result = new Gson().fromJson(response, new TypeToken<HttpResult<CurrentDutyInfo>>(){}.getType());
		if (!result.success){
			logger.error("return false while get duty employee info. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(parent, result.result);
			return null;
		}
		return result.data;
	}
	
    public static ArrayList<DiscountTemplate> loadDiscountTemplates(MainFrame parent){
		String url = "common/getdiscounttemplates";
		String response = HttpUtil.getJSONObjectByGet(MainFrame.SERVER_URL + url);
		if (response == null){
			logger.error("get null from server for discount templates. URL = " + url);
			JOptionPane.showMessageDialog(parent, "get null from server for discount templates. URL = " + url);
			return null;
		}
		HttpResult<ArrayList<DiscountTemplate>> result = new Gson().fromJson(response, new TypeToken<HttpResult<ArrayList<DiscountTemplate>>>(){}.getType());
		if (!result.success){
			logger.error("return false while get discount templates. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(parent, result.result);
			return null;
		}
		return result.data;
	}
    
    public static ArrayList<Promotion> loadPromotion(MainFrame parent){
		String url = "promotion/queryallpromotion";
		String response = HttpUtil.getJSONObjectByGet(MainFrame.SERVER_URL + url);
		if (response == null){
			logger.error("get null from server for promotion. URL = " + url);
			JOptionPane.showMessageDialog(parent, "get null from server for promotion. URL = " + url);
			return null;
		}
		HttpResult<ArrayList<Promotion>> result = new Gson().fromJson(response, new TypeToken<HttpResult<ArrayList<Promotion>>>(){}.getType());
		if (!result.success){
			logger.error("return false while get promotion. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(parent, result.result);
			return null;
		}
		return result.data;
	}
	
    public static ArrayList<PayWay> loadPayWay(MainFrame parent){
		String url = "common/getpayways";
		String response = HttpUtil.getJSONObjectByGet(MainFrame.SERVER_URL + url);
		if (response == null){
			logger.error("get null from server for pay way. URL = " + url);
			JOptionPane.showMessageDialog(parent, "get null from server for pay way. URL = " + url);
			return null;
		}
		HttpResult<ArrayList<PayWay>> result = new Gson().fromJson(response, new TypeToken<HttpResult<ArrayList<PayWay>>>(){}.getType());
		if (!result.success){
			logger.error("return false while get pay way. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(parent, result.result);
			return null;
		}
		return result.data;
	}
	
    public static CurrentDutyInfo doOnDuty(MainFrame parent, int userId, boolean printLastDutyTicket){
		String url = "management/startshiftwork";
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", String.valueOf(userId));
		params.put("printLastDutyTicket", String.valueOf(printLastDutyTicket));
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params, "UTF-8");
		if (response == null || response.length() == 0){
			logger.error("get null from server for starting shiftwork. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(parent, "get null from server for starting shiftwork. URL = " + url + ", param = "+ params);
			return null;
		}
		HttpResult<CurrentDutyInfo> result = new Gson().fromJson(response, new TypeToken<HttpResult<CurrentDutyInfo>>(){}.getType());
		if (!result.success){
			logger.error("return false while starting shiftwork. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(parent, result.result);
			return null;
		}
		return result.data;
	}
	
    public static CurrentDutyInfo doOffDuty(MainFrame parent, UserData user, boolean print){
		String url = "management/endshiftwork";
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", String.valueOf(user.getId()));
		params.put("printShiftTicket", Boolean.valueOf(print).toString());
		params.put("startTime", ConstantValue.DFYMDHMS.format(user.getStartTime()));
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params, "UTF-8");
		if (response == null || response.length() == 0){
			logger.error("get null from server while off duty. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(parent, "get null from server while off duty. URL = " + url + ", param = "+ params);
			return null;
		}
		HttpResult<CurrentDutyInfo> result = new Gson().fromJson(response, new TypeToken<HttpResult<CurrentDutyInfo>>(){}.getType());
		if (!result.success){
			logger.error("return false while end shiftwork. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(parent, result.result);
			return null;
		}
		return result.data;
	}
    
    public static ArrayList<Indent> doQueryPreOrder(MainFrame parent, UserData user, String memberCard, Date startTime, Date endTime){
		String url = "indent/queryprebuyindent";
		Map<String, String> params = new HashMap<>();
		params.put("userId", user.getId() + "");
		if (memberCard != null && memberCard.length() > 0)
			params.put("member",memberCard);
		if (startTime != null){
			params.put("starttime", ConstantValue.DFYMDHMS.format(startTime));
		}
		if (endTime != null){
			params.put("endtime", ConstantValue.DFYMDHMS.format(endTime));
		}
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params, "UTF-8");
		if (response == null){
			logger.error("get null from server for query preorder. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(parent, "get null from server for query preorder. URL = " + url);
			return null;
		}
		Gson gson = new GsonBuilder().setDateFormat(ConstantValue.DATE_PATTERN_YMDHMS).create();
		HttpResult<ArrayList<Indent>> result = gson.fromJson(response, new TypeToken<HttpResult<ArrayList<Indent>>>(){}.getType());
		if (!result.success){
			logger.error("return false while query preorder. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(parent, result.result);
			return null;
		}
		return result.data;
	}
    
    public static Indent doChangePreOrderToOrder(JDialog parent, UserData user, String indentId){
		String url = "indent/changepreordertoorder";
		Map<String, String> params = new HashMap<>();
		params.put("userId", user.getId() + "");
		params.put("indentId", indentId);
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params, "UTF-8");
		if (response == null){
			logger.error("get null from server for change preorder to order. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(parent, "get null from server for change preorder to order. URL = " + url);
			return null;
		}
		Gson gson = new GsonBuilder().setDateFormat(ConstantValue.DATE_PATTERN_YMDHMS).create();
		HttpResult<Indent> result = gson.fromJson(response, new TypeToken<HttpResult<Indent>>(){}.getType());
		if (!result.success){
			logger.error("return false while change preorder to order. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(parent, result.result);
			return null;
		}
		return result.data;
	}
    
    public static boolean doDeletePreOrder(JDialog parent, UserData user, String indentId){
		String url = "indent/deletepreorder";
		Map<String, String> params = new HashMap<>();
		params.put("userId", user.getId() + "");
		params.put("indentId", indentId);
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params, "UTF-8");
		if (response == null){
			logger.error("get null from server for delete preorder. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(parent, "get null from server for delete preorder. URL = " + url);
			return false;
		}
		Gson gson = new GsonBuilder().setDateFormat(ConstantValue.DATE_PATTERN_YMDHMS).create();
		HttpResult<Indent> result = gson.fromJson(response, new TypeToken<HttpResult<Indent>>(){}.getType());
		if (!result.success){
			logger.error("return false while delete preorder. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(parent, result.result);
			return false;
		}
		return true;
	}
    
    public static Member doLoadMember(Window parent, UserData user, String memberCard){
		String url = "member/querymember";
		Map<String, String> params = new HashMap<>();
		params.put("userId", user.getId() + "");
		params.put("memberCard", memberCard);
		String response = HttpUtil.getJSONObjectByPost(MainFrame.SERVER_URL + url, params, "UTF-8");
		if (response == null){
			logger.error("get null from server for query member by membercard. URL = " + url + ", param = "+ params);
			JOptionPane.showMessageDialog(parent, "get null from server for query member by membercard. URL = " + url);
			return null;
		}
		Gson gson = new GsonBuilder().setDateFormat(ConstantValue.DATE_PATTERN_YMDHMS).create();
		HttpResult<ArrayList<Member>> result = gson.fromJson(response, new TypeToken<HttpResult<ArrayList<Member>>>(){}.getType());
		if (!result.success){
			logger.error("return false while query member by membercard. URL = " + url + ", response = "+response);
			JOptionPane.showMessageDialog(parent, result.result);
			return null;
		}
		if (result.data != null && !result.data.isEmpty())
			return result.data.get(0);
		else return null;
	}
}
