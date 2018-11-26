package sdk;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 该类用于将下单后的支付成功或退款事件发送到nginx服务器中，进行日志的收集
 *
 */
public class AnalysticLogSDk {
    private static final String url ="http://192.168.91.7/index.html";
    private static final String platformName ="java";
    private static final String ver = "1";
    private static final String sdkName="java_sdk";

    /**
     * 发送支付成功事件
     * @param oid
     * @param u_mid
     * @return
     */
    public static boolean chargeSuccess(String oid,String u_mid) {
        boolean flag = false;
        if (isNotEmpty(oid) && isNotEmpty(u_mid)) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("pl", platformName);
            params.put("ver", ver);
            params.put("sdk", sdkName);
            params.put("en", "e_cs");
            params.put("c_time", System.currentTimeMillis() + "");
            params.put("oid", oid);
            params.put("u_mid", u_mid);
            //构建url
            String requestUrl = buildUrl(params, url);
            System.out.println(requestUrl);
            //将url添加进队列，并发送http请求
            SendMonitor.getInstance().addUrlQueue(requestUrl);
            flag = true;
        }
        return flag;
    }

    /**
     *发送退款成功事件
     * @param oid
     * @param u_mid
     * @return
     */
    public static boolean chargeRefund(String oid,String u_mid) {
        boolean flag = false;
        if (isNotEmpty(oid) && isNotEmpty(u_mid)) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("pl", platformName);
            params.put("ver", ver);
            params.put("sdk", sdkName);
            params.put("en", "e_cr");
            params.put("c_time", System.currentTimeMillis() + "");
            params.put("oid", oid);
            params.put("u_mid", u_mid);
            //构建url
            String requestUrl = buildUrl(params, url);
            SendMonitor.getInstance().addUrlQueue(requestUrl);
            System.out.println(requestUrl);
            flag = true;
        }
        return flag;
    }


    private static String buildUrl(Map<String, String> params, String url) {
        //构建stringbuilder对象拼接参数
        StringBuffer sb = new StringBuffer();
        //判断params是否为空
        if(!params.isEmpty()){
            sb.append(url+"?");
            for (Map.Entry<String,String>entry: params.entrySet()) {
                sb.append(entry.getKey());
                sb.append("=");
                try {
                    sb.append(URLEncoder.encode(entry.getValue(),"utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                sb.append("&");
            }
        }
        //返回拼接后的嗲参数的url
        return sb.toString().substring(0,sb.length()-1);
    }

    /**
     * 自定义方法判断字符串是否有效
     * @param str
     * @return
     */
    private static boolean isNotEmpty(String str) {
        boolean flag = false;
        if(str!=null&&!str.equals("")&&str.trim().length()!=0){
            flag=true;
        }
        return flag;
    }

}
