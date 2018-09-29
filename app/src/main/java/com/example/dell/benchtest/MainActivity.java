package com.example.dell.benchtest;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.SimpleTimeZone;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.CRC32;

public class MainActivity extends AppCompatActivity {
    /*
     主 变量
     */
// 主线程Handler
// 用于将从服务器获取的消息显示出来
    public static final int UPDATE_TEXT = 1;

    // Socket变量
    private Socket socket = null;
    private String jsonData;
    private String jsonDataRead;
    SendData senddata = new SendData();


    // 线程池
// 为了方便展示,此处直接采用线程池进行线程管理,而没有一个个开线程
    private ExecutorService mThreadPool;

    /*
     * 接收服务器消息 变量
     */
    // 输入流对象
    InputStream is;
    InputStreamReader isr;
    BufferedReader br;
    // 接收服务器发送过来的消息
    String response;
    /*
     * 发送消息到服务器 变量
     */
    // 输出流对象
    OutputStream outputStream;
    boolean isConnect = true;
    boolean SendDtatus = false;
    //final String IPAdress="120.78.144.43";
    //final String IPAdress = "192.168.173.1";
    //final int Port = 4567;
    int StartMark = 0, StopMark = 0;
/*
 * 按钮 变量
 */

    // 连接 断开连接 发送数据到服务器 的按钮变量
//private Button btnConnect, btnDisconnect, btnSend;
    // 显示接收服务器消息 按钮
    private List<ReadData.DataArrayBean> DataArrayBeanList=new ArrayList<>();
    private ReadData.DataArrayBean Header=new ReadData.DataArrayBean();
    private TextView receive_message;
    private EditText IPAdress,Port,DeviceIDSend;
    private TextView DevID, DevName, DevStatus, TestTime, TestName, TestDescription, FaultID, FaultDescription, zhycjgV, TestStatusV, FaultStatusV;
    private RecyclerView recyclerview;
    private DataArrayAdapter adapter;
    // 输入需要发送的消息 输入框


    @Override
    protected void onStop() {
        super.onStop();
         /*
         * 存取输入变量，防止活动隐藏被清空
         */
        SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
        editor.putString("IPAdress", IPAdress.getText().toString());
        editor.putString("DeviceID", DeviceIDSend.getText().toString());
        editor.putString("Port", Port.getText().toString());
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*
         * 读取输入变量，防止活动隐藏被清空
         */
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        IPAdress.setText(pref.getString("IPAdress", ""));
        DeviceIDSend.setText(pref.getString("DeviceID", ""));
        Port.setText(pref.getString("Port", ""));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




    /*
         * 初始化操作
         */

        DevID = (TextView) findViewById(R.id.DevID);
        DevName = (TextView) findViewById(R.id.DevName);
        DevStatus = (TextView) findViewById(R.id.DevStatus);
        TestTime = (TextView) findViewById(R.id.TestTime);
        TestName = (TextView) findViewById(R.id.TestName);
        TestDescription = (TextView) findViewById(R.id.TestDescription);
        FaultID = (TextView) findViewById(R.id.FaultID);
        FaultDescription = (TextView) findViewById(R.id.FaultDescription);
        zhycjgV = (TextView) findViewById(R.id.zhycjgV);
        receive_message = (TextView) findViewById(R.id.receive_message);
        TestStatusV = (TextView) findViewById(R.id.TestStatusV);
        FaultStatusV = (TextView) findViewById(R.id.FaultStatusV);
        recyclerview=(RecyclerView) findViewById(R.id.recycler_view);


        Header.setSensorName("传感器名称");
        Header .setSensorValue("传感器数值");
        Header.setUnit("单位");
        DataArrayBeanList.add(Header);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerview.setLayoutManager(layoutManager);
        adapter=new DataArrayAdapter(DataArrayBeanList);
        recyclerview.setAdapter(adapter);


        final Button btnStart = (Button) findViewById(R.id.Start);
        final Button btnStop = (Button) findViewById(R.id.Stop);
        final Button btnConnect = (Button) findViewById(R.id.connect);
        final Button btnDisconnect = (Button) findViewById(R.id.disconnect);

        Button Clear = (Button) findViewById(R.id.Clear);

        IPAdress = (EditText) findViewById(R.id.IPAddress);
        Port = (EditText) findViewById(R.id.Port);
        DeviceIDSend= (EditText) findViewById(R.id.DevIDSend);
        // 初始化线程池
        mThreadPool = Executors.newCachedThreadPool();
        // 初始化按钮状态
        btnDisconnect.setEnabled(false);
        btnStart.setEnabled(false);
        btnStop.setEnabled(false);

        // receive_message可以滑动显示
        receive_message.setMovementMethod(ScrollingMovementMethod.getInstance());
        /*
         * 创建客户端 & 服务器的连接
         */
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnect) {
                    //btnDisconnect.setEnabled(true);
                    //btnConnect.setEnabled(false);
                    mThreadPool.execute(new Runnable() {
                        //@Override
                        public void run() {
                            try {

                                // 创建Socket对象 & 指定服务端的IP 及 端口号
                                //socket = new Socket(IPAdress, Port);
                                runOnUiThread(new Runnable()//不允许其他线程直接操作组件，用提供的此方法可以
                                {
                                    public void run() {
                                        // TODO Auto-generated method stub
                                        receive_message.setText(new String("连接中，请等待!") + "\n");
                                    }
                                });
                                socket = new Socket();
                                SocketAddress endpoint = new InetSocketAddress(IPAdress.getText().toString(),Integer.parseInt(Port.getText().toString()));
                                socket.connect(endpoint, 5000);
                                //socket.setKeepAlive(true);
                                socket.setSoTimeout(10000);
                                // 判断客户端和服务器是否连接成功
                                System.out.println(socket.isConnected());
                                if (socket.isConnected()) {
                                    runOnUiThread(new Runnable()//不允许其他线程直接操作组件，用提供的此方法可以
                                    {
                                        public void run() {
                                            // TODO Auto-generated method stub
                                            receive_message.setText(new String("连接成功!") + "\n");
                                            isConnect = false;
                                            btnStart.setEnabled(true);
                                            btnStop.setEnabled(true);

                                            btnDisconnect.setEnabled(true);
                                            btnConnect.setEnabled(false);
                                            Receive_Thread receive_Thread = new Receive_Thread();
                                            receive_Thread.start();
                                        }
                                    });
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable()//不允许其他线程直接操作组件，用提供的此方法可以
                                {
                                    public void run() {
                                        // TODO Auto-generated method stub
                                        receive_message.setText(new String("连接失败!") + "\n");
                                    }
                                });
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();;
                                runOnUiThread(new Runnable()//不允许其他线程直接操作组件，用提供的此方法可以
                                {
                                    public void run() {
                                        // TODO Auto-generated method stub
                                        receive_message.setText(new String("连接失败!") + "\n");
                                    }
                                });
                            }
                        }
                    });
                } else {
                    // btnDisconnect.setEnabled(false);
                }
            }

        });
        /*
         * 接收 服务器消息
         */
        Clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                receive_message.setText("");
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (socket.isConnected()) {
                                SetSendData();
                                CRC crcsend=new CRC();
                                senddata.setCommand(1);

                                jsonData = new Gson().toJson(senddata);
                                int DendDataLength=jsonData.length();
                                CRC32 crc32Send=new CRC32();
                                crc32Send.update(jsonData.getBytes());
                                final long CRC32DataSendLong = crc32Send.getValue();
                                int CRC32DataSendInt=(int)CRC32DataSendLong;
                                String SendDataLengthString=new String(crcsend.intToBytes2(DendDataLength));
                                String SendDataCRCLengthString=new String(crcsend.intToBytes2(CRC32DataSendInt));
                                jsonData = "CAERI" + SendDataLengthString+jsonData+SendDataCRCLengthString;
                                outputStream = socket.getOutputStream();
                                // 步骤2：写入需要发送的数据到输出流对象中
                                outputStream.write(jsonData.getBytes("utf-8"));
                                // 特别注意：数据的结尾加上换行符才可让服务器端的readline()停止阻塞
                                // 步骤3：发送数据到服务端
                                outputStream.flush();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable()//不允许其他线程直接操作组件，用提供的此方法可以
                            {
                                public void run() {
                                    // TODO Auto-generated method stub
                                    receive_message.setText(new String("Send Failed !") + "\n");
                                }
                            });
                        }
                    }
                });
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (socket.isConnected()) {
                                SetSendData();
                                CRC crcsend=new CRC();
                                senddata.setCommand(0);

                                jsonData = new Gson().toJson(senddata);
                                int DendDataLength=jsonData.length();
                                CRC32 crc32Send=new CRC32();
                                crc32Send.update(jsonData.getBytes());
                                final long CRC32DataSendLong = crc32Send.getValue();
                                int CRC32DataSendInt=(int)CRC32DataSendLong;
                                String SendDataLengthString=new String(crcsend.intToBytes2(DendDataLength));
                                String SendDataCRCLengthString=new String(crcsend.intToBytes2(CRC32DataSendInt));
                                jsonData = "CAERI" + SendDataLengthString+jsonData+SendDataCRCLengthString;
                                outputStream = socket.getOutputStream();
                                // 步骤2：写入需要发送的数据到输出流对象中
                                outputStream.write(jsonData.getBytes("utf-8"));
                                // 特别注意：数据的结尾加上换行符才可让服务器端的readline()停止阻塞
                                // 步骤3：发送数据到服务端
                                outputStream.flush();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable()//不允许其他线程直接操作组件，用提供的此方法可以
                            {
                                public void run() {
                                    // TODO Auto-generated method stub
                                    receive_message.setText(new String("Send Failed !") + "\n");
                                }
                            });
                        }
                    }
                });
            }
        });
        /**
         * 发送消息 给 服务器
         */

        /**
         * 断开客户端 & 服务器的连接
         */

        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    // 最终关闭整个Socket连接
                    //System.out.println(socket.isConnected());
                    btnDisconnect.setEnabled(false);
                    btnConnect.setEnabled(true);
                    if (socket.isConnected()) {
                        // 断开 服务器发送到客户端 的连接，即关闭输入流读取器对象BufferedReader
                        // 断开 客户端发送到服务器 的连接，即关闭输出流对象OutputStream
                        //is.close();
                        if (SendDtatus) {
                            outputStream.close();
                            SendDtatus = false;
                        }
                        socket.close();
                        // 判断客户端和服务器是否已经断开连接
                        System.out.println(socket.isConnected());
                        isConnect = true;
                        btnStart.setEnabled(false);
                        btnStop.setEnabled(false);

                        receive_message.setText(new String("Connect Closed !") + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    receive_message.setText(new String("DisConnect Failed !") + "\n");
                }
            }
        });
    }

    class Receive_Thread extends Thread {
        public void run()//重写run方法
        {
            try {
                CRC crc=new CRC();
                while (true)
                {
                    CRC32 crc32=new CRC32();
                    if (socket.isConnected())
                    {
                        final byte[] buffer = new byte[1024];//创建接收缓冲区
                        is = socket.getInputStream();
                        final int len = is.read(buffer);//数据读出来，并且返回数据的长度
                        if (len == 0)
                        {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    receive_message.setText(new String("Receive Failed len=0 !") + "\n");
                                }
                            });
                        }
                        else
                        {
                            String ReadTotalDataString = new String(buffer).trim();
                            if(len>=9)
                            {
                                String headerstring = ReadTotalDataString.substring(0,5);
                                if (headerstring.equals("CAERI"))
                                {
                                    byte[] sublenbuffer = Arrays.copyOfRange(buffer, 5, 9);
                                    final long DataLength = crc.bytetolong(sublenbuffer);
                                    final int DataLen = (int) DataLength;
                                    if(len>=DataLen+13)
                                    {
                                        byte[] subbuffer = Arrays.copyOfRange(buffer, 9, DataLen+9);
                                        jsonDataRead = new String(subbuffer);
                                        final byte[] subcrcbuffer = Arrays.copyOfRange(buffer, DataLen+9, DataLen+14);
                                        crc32.update(jsonDataRead.getBytes());
                                        final long CRC32Data = crc32.getValue();
                                        final long CRC32CheckData = crc.bytetolong(subcrcbuffer);
                                        if (CRC32CheckData == CRC32Data)
                                        {
                                            ReadDataHandle();
                                        }
                                        else
                                            {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    receive_message.setText("CRC32校验失败!");
                                                }
                                            });
                                        }
                                    }
                                    else
                                    {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                receive_message.setText("长度错误!");
                                            }
                                        });
                                    }
                                }
                                /*else
                                {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            receive_message.setText("包头错误!");
                                        }
                                    });
                                }*/
                            }
                        }
                    }
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                runOnUiThread(new Runnable()//不允许其他线程直接操作组件，用提供的此方法可以
                {
                    public void run() {
                        // TODO Auto-generated method stub
                        receive_message.setText(new String("Receive Failed IO Err_请断开连接并重新连接电脑!") + "\n");
                    }
                });
            } catch (JsonSyntaxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                runOnUiThread(new Runnable()//不允许其他线程直接操作组件，用提供的此方法可以
                {
                    public void run() {
                        // TODO Auto-generated method stub
                        receive_message.setText(new String("Json Err_请断开连接并重新连接电脑 !") + "\n");
                    }
                });
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                runOnUiThread(new Runnable()//不允许其他线程直接操作组件，用提供的此方法可以
                {
                    public void run() {
                        // TODO Auto-generated method stub
                        receive_message.setText(new String("Receive Failed Sys_请断开连接并重新连接电脑 !") + "\n");
                    }
                });
            }
        }
    }

    class Send_Thread extends Thread {
        public void run()//重写run方法
        {
            try {
                outputStream = socket.getOutputStream();
                // 步骤2：写入需要发送的数据到输出流对象中
                outputStream.write(jsonData.getBytes("utf-8"));
                // 特别注意：数据的结尾加上换行符才可让服务器端的readline()停止阻塞
                // 步骤3：发送数据到服务端
                outputStream.flush();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                runOnUiThread(new Runnable()//不允许其他线程直接操作组件，用提供的此方法可以
                {
                    public void run() {
                        // TODO Auto-generated method stub
                        receive_message.setText(new String("Send Failed !") + "\n");
                    }
                });
            }
        }
    }

    private void SetSendData() {
        try {
            senddata.setCommand(0);
            senddata.setDevID(DeviceIDSend.getText().toString());
            senddata.setReserved1("11");
            senddata.setReserved2("22");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void ReadDataHandle() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Gson gs = new Gson();
                    ReadData readdata = gs.fromJson(jsonDataRead, ReadData.class);
                    DevID.setText(String.valueOf(readdata.getDevID()));
                    DevName.setText(String.valueOf(readdata.getDevName()));
                    switch (readdata.getDevStatus())
                    {
                        case 0:
                            DevStatus.setText("设备未开启");
                            TestStatusV.setBackgroundColor(Color.parseColor("#20B2AA"));
                            break;

                        case 1:
                            DevStatus.setText("开启未试验");
                            TestStatusV.setBackgroundColor(Color.parseColor("#FFFF00"));
                            break;
                        case 2:
                            DevStatus.setText("正常试验");
                            TestStatusV.setBackgroundColor(Color.parseColor("#00FF00"));
                            break;
                        case 3:
                            DevStatus.setText("试验中，有警告");
                            TestStatusV.setBackgroundColor(Color.parseColor("#DC143C"));
                            break;
                        case 4:
                            DevStatus.setText("错误，试验停止");
                            TestStatusV.setBackgroundColor(Color.parseColor("#DB7093"));
                            break;
                        default:
                            DevStatus.setText("");
                            TestStatusV.setBackgroundColor(Color.parseColor("#FFFF00"));
                            break;
                    }
                    TestName.setText(String.valueOf(readdata.getTestName()));
                    TestDescription.setText(String.valueOf(readdata.getTestDescription()));
                    FaultID.setText(String.valueOf(readdata.getFaultID()));
                    FaultDescription.setText(String.valueOf(readdata.getFaultDescription()));
                    FaultID.setText(String.valueOf(readdata.getFaultID()));
                    switch (readdata.getFaultID())
                    {
                        case 0:
                            FaultStatusV.setBackgroundColor(Color.parseColor("#00FF00"));
                            break;
                       default:
                           FaultStatusV.setBackgroundColor(Color.parseColor("#DC143C"));
                           break;
                    }
                    DataArrayBeanList.clear();
                    DataArrayBeanList.addAll(readdata.getDataArray());
                    DataArrayBeanList.add(0,Header);
                    adapter.notifyDataSetChanged();
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                    TestTime.setText(format.format(readdata.getTestTime() * 1000));
                    //receive_message始终显示最后一行数据
                    int offset = receive_message.getLineCount() * receive_message.getLineHeight();
                    if (offset > receive_message.getHeight()) {
                        receive_message.scrollTo(0, offset-receive_message.getHeight());
                    }
                } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable()//不允许其他线程直接操作组件，用提供的此方法可以
                {
                    public void run()
                    {
                        // TODO Auto-generated method stub
                        receive_message.setText(new String("转换失败 !")+"\n");
                    }
                });
            }
            }
        });
    }

}