package com.atdu.bio;
import com.sun.tools.javac.util.SharedNameTable;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BIOServer {
    public static void main(String[] args) throws Exception {
        //具体思路：
        //1.创建线程池
        //2.如果有客户端需要连接，服务端创建线程进行通信
        ExecutorService
                executorService = Executors.newCachedThreadPool();
        //3.创建Socket监听窗口
      ServerSocket serversocket = new ServerSocket(6666);
        System.out.println("服务器启动了");
        while(true){
             final Socket socket = serversocket.accept();
            System.out.println("连接到客户端：");
            executorService.execute(new Runnable() {
                @Override
                public void run() { //重写方法可以和客户端通讯
                    System.out.println(1);
                    Handler(socket);
                }
            });
        }
    }
    //编写handler与客户端通讯
    public  static void  Handler(Socket socket){
        //用tyr避免异常
        try{
            System.out.println("线程信息 id="+Thread.currentThread().getId());
            byte[] bytes = new byte[1024];
            //细节：注意Byte和byte：Byte是byte的包装类，同比于int和Integer的关系
            InputStream inputStream = socket.getInputStream();
            while(true){
                int read = inputStream.read();
                if(read !=-1){
                    String s = new String(bytes, 0, read);
                    System.out.println("内容为 +"+s);
                }else{
                    break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally{
            System.out.println("关闭于Client客户端的连接");
            try{
                socket.close();
            }catch (Exception e){
                e.printStackTrace();
            }

        }


    }
}
