package sdk;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SendMonitor {
    //打印日志
    public static final Logger logger = Logger.getGlobal();
    //创建存放url的日志队列
    public static final BlockingQueue<String>blockingQueue =new LinkedBlockingDeque<String>();
    //创建单例对象
    private static SendMonitor sendMonitor =null;
    //私有的构造方法
    private SendMonitor() {

    }
    //获取单例对象
    public static SendMonitor getInstance(){
        if(sendMonitor==null){
            synchronized (SendMonitor.class){
                if(sendMonitor==null){
                    sendMonitor = new SendMonitor();
                    Thread th = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            requestUrl();
                        }
                    });

                    th.start();
                }

            }

        }
        return sendMonitor;
    }

    /**
     * 添加url到队列中
     */
    public  static void addUrlQueue(String url){
        try {
            System.out.println(url);
            blockingQueue.put(url);
        } catch (InterruptedException e) {
            logger.log(Level.WARNING,"添加url异常",e);
        }
    }
    /**
     * 消费队列中的url
     */
    public  static void requestUrl(){
        while (true){
            try {
                String url =blockingQueue.take();
                //发送http请求到nginx服务器
                HttpUrlUtil.sendUrl(url);
            } catch (InterruptedException e) {
                logger.log(Level.WARNING,"发送url异常",e);
            }
        }

    }
    public static class HttpUrlUtil{
        public static void sendUrl(String url){
            HttpURLConnection connection = null;
            InputStream is =null;
            try {
                URL u = new URL(url);
                connection = (HttpURLConnection) (u.openConnection());
                connection.setConnectTimeout(5000);
                connection.setRequestMethod("GET");
                connection.setReadTimeout(5000);
                is =connection.getInputStream();
            } catch (IOException e) {
                logger.log(Level.WARNING,"发送url失败",e);
            } finally {
                connection.disconnect();
                if(is!=null){
                    try {
                        is.close();
                    } catch (IOException e) {
                        logger.log(Level.WARNING,"关闭流异常",e);
                    }
                }
            }
        }
    }

}
