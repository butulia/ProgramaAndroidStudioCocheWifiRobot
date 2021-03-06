package com.hanry;
import java.io.BufferedInputStream;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hanry.Constant;
import com.hanry.R;
import com.hanry.WifiCarSettings;
import com.hanry.Constant.CommandArray;

public class Main extends Activity// implements SeekBar.OnSeekBarChangeListener
{
    private final int MSG_ID_ERR_CONN = 1001;
    //private final int MSG_ID_ERR_SEND = 1002;
    private final int MSG_ID_ERR_RECEIVE = 1003;
    private final int MSG_ID_CON_READ = 1004;
    private final int MSG_ID_CON_SUCCESS = 1005;    
    private final int MSG_ID_START_CHECK = 1006;
    private final int MSG_ID_ERR_INIT_READ = 1007;
    private final int MSG_ID_CLEAR_QUIT_FLAG = 1008;
    
    private final int MSG_ID_LOOP_START = 1010;
    private final int MSG_ID_HEART_BREAK_RECEIVE = 1011;
    private final int MSG_ID_HEART_BREAK_SEND = 1012;
    private final int MSG_ID_LOOP_END = 1013;
    
    private final int STATUS_INIT = 0x2001;
    //private final int STATUS_CONNECTING = 0x2002;
    private final int STATUS_CONNECTED = 0x2003;
    private final int WARNING_ICON_OFF_DURATION_MSEC = 600;
    private final int WARNING_ICON_ON_DURATION_MSEC = 800;    
    
    private final int WIFI_STATE_UNKNOW = 0x3000;
    private final int WIFI_STATE_DISABLED = 0x3001;
    private final int WIFI_STATE_NOT_CONNECTED = 0x3002;
    private final int WIFI_STATE_CONNECTED = 0x3003;
    
    private int MIN_GEAR_STEP = 2;
    private final int MAX_GEAR_VALUE = 180;
    private final int INIT_GEAR_VALUE = 90;
    private final int INIT_GEAR_HORIZONTAL_VALUE = 90;
    
    private final byte COMMAND_PERFIX = -1;
    private final int HEART_BREAK_CHECK_INTERVAL = 8000;//ms
    private final int QUIT_BUTTON_PRESS_INTERVAL = 2500;//ms
    private final int HEART_BREAK_SEND_INTERVAL = 2500;//ms
    
    private boolean m4test = false;

    private String CAMERA_VIDEO_URL = "http://192.168.2.1:8080/?action=stream";
    private String CAMERA_VIDEO_URL_TEST = "";
    private String ROUTER_CONTROL_URL = "192.168.2.1";
    private String ROUTER_CONTROL_URL_TEST = "192.168.1.1";
    private int ROUTER_CONTROL_PORT = 2001;
    private int ROUTER_CONTROL_PORT_TEST = 2001;
    private final String WIFI_SSID_PERFIX = "robot";

    private String EJEX_GRADOS_CENTRADO = "90";
    private String EJEY_GRADOS_CENTRADO = "90";

    private ImageButton ForWard;
    private ImageButton BackWard;
    private ImageButton TurnLeft;
    private ImageButton TurnRight;
    private ImageButton TakePicture;
    private ImageButton AlinearCamara;
    
    private ImageView mAnimIndicator;
    private boolean bAnimationEnabled = true;
    private Drawable mWarningIcon;
    private boolean bReaddyToSendCmd = false;
    private TextView mLogText;
  
    private Drawable ForWardon;
    private Drawable ForWardoff;
    private Drawable BackWardon;
    private Drawable BackWardoff;
    private Drawable TurnLefton;
    private Drawable TurnLeftoff;
    private Drawable TurnRighton;
    private Drawable TurnRightoff;
    private Drawable buttonLenon;
    private Drawable buttonLenoff;
    
    private SeekBarChin barraVertical;
    private SeekBar barraHorizontal;
    private int  lastBarralVerticalValue = -1;
    private int  lastBarraHorizontalValue = -1;

    private ImageButton buttonCus1;
    private ImageButton buttonLen;
    private boolean bLenon = false;
    private int mWifiStatus = STATUS_INIT;

    private Thread mThreadClient = null;
    private boolean mThreadFlag = false;

    private boolean mQuitFlag = false;
    private boolean bHeartBreakFlag = false;
    private int mHeartBreakCounter = 0;
    private int mLastCounter = 0;
    
    private Context mContext;
    SocketClient mtcpSocket;
    MjpegView backgroundView = null;
    /*
    private byte[] COMM_FORWARD = {(byte) 0xFF, (byte)0x00, (byte)0x01, (byte)0x00, (byte) 0xFF};
    private byte[] COMM_BACKWARD = {(byte) 0xFF, 0x00, 0x02, 0x00, (byte) 0xFF};
    private byte[] COMM_STOP = {(byte) 0xFF, 0x00, 0x00, 0x00, (byte) 0xFF};
    private byte[] COMM_LEFT = {(byte) 0xFF, 0x00, 0x03, 0x00, (byte) 0xFF};
    private byte[] COMM_RIGHT = {(byte) 0xFF, 0x00, 0x04, 0x00, (byte) 0xFF};
    
    private byte[] COMM_LEN_ON = {(byte) 0xFF, 0x04, 0x03, 0x00, (byte) 0xFF};
    private byte[] COMM_LEN_OFF = {(byte) 0xFF, 0x04, 0x02, 0x00, (byte) 0xFF};

    private byte[] COMM_GEAR_CONTROL = {(byte) 0xFF, 0x01, 0x01, 0x00, (byte) 0xFF};
    
    private byte[] COMM_SELF_CHECK = {(byte) 0xFF, (byte)0xEE, (byte)0xEE, 0x00, (byte) 0xFF};
    private byte[] COMM_SELF_CHECK_ALL = {(byte) 0xFF, (byte)0xEE, (byte)0xE0, 0x00, (byte) 0xFF};

    private byte[] COMM_HEART_BREAK = {(byte) 0xFF, (byte)0xEE, (byte)0xE1, 0x00, (byte) 0xFF};
*/
    // Comando cristianizados
    // Movimiento
    private String COMM_FORWARD = "w";
    private String COMM_BACKWARD = "s";
    private String COMM_STOP = "0";
    private String COMM_LEFT = "a";
    private String COMM_RIGHT = "d";
    // Luces
    private String COMM_LEN_ON = "n";
    private String COMM_LEN_OFF = "m";

    // Por averiguar
    // private String COMM_GEAR_CONTROL = "";  // Parece que es para establecer un angulo ¿?

    private String COMM_SELF_CHECK = ""; // llamado en una funcion selfCheck que no tiene usos
    private String COMM_SELF_CHECK_ALL = ""; // No tiene usos

    private String COMM_HEART_BREAK = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        
        initSettings();
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);

        ForWard= (ImageButton)findViewById(R.id.btnForward);
        TurnLeft= (ImageButton)findViewById(R.id.btnLeft);
        TurnRight=(ImageButton)findViewById(R.id.btnRight);
        BackWard= (ImageButton)findViewById(R.id.btnBack);
        AlinearCamara = (ImageButton)findViewById(R.id.btnAlinearCamara);

        buttonCus1= (ImageButton)findViewById(R.id.ButtonCus1);
        buttonCus1.setOnClickListener(buttonCus1ClickListener);
        buttonCus1.setOnLongClickListener(buttonCus1ClickListener2);
        
        buttonLen= (ImageButton)findViewById(R.id.btnLen);
        buttonLen.setOnClickListener(buttonLenClickListener);
        buttonLen.setLongClickable(true);
        
        TakePicture = (ImageButton)findViewById(R.id.ButtonTakePic);
        TakePicture.setOnClickListener(buttonTakePicClickListener);
        mAnimIndicator = (ImageView)findViewById(R.id.btnIndicator);
        mWarningIcon = getResources().getDrawable(R.drawable.sym_indicator1);
        
        ForWardon = getResources().getDrawable(R.drawable.sym_forward_1);
        ForWardoff = getResources().getDrawable(R.drawable.sym_forward);
        
        TurnLefton = getResources().getDrawable(R.drawable.sym_left_1);
        TurnLeftoff = getResources().getDrawable(R.drawable.sym_left);
        
        TurnRighton = getResources().getDrawable(R.drawable.sym_right_1);
        TurnRightoff = getResources().getDrawable(R.drawable.sym_right);
        
        BackWardon = getResources().getDrawable(R.drawable.sym_backward_1);
        BackWardoff = getResources().getDrawable(R.drawable.sym_backward);
        
        buttonLenon = getResources().getDrawable(R.drawable.sym_light);
        buttonLenoff = getResources().getDrawable(R.drawable.sym_light_off);
        
        
        backgroundView = (MjpegView)findViewById(R.id.mySurfaceView1); 
        
        mLogText = (TextView)findViewById(R.id.logTextView);
        if (null != mLogText) {
            mLogText.setBackgroundColor(Color.argb(0, 0, 255, 0));
            mLogText.setTextColor(Color.argb(90, 0, 255, 0));
        }

        barraVertical = (SeekBarChin)findViewById(R.id.barraVertical);
        barraVertical.setMax(MAX_GEAR_VALUE);
        barraVertical.setProgress(INIT_GEAR_VALUE);
        barraVertical.setOnSeekBarChangeListener(new SeekBarChin.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBarChin VerticalSeekBar, int progress, boolean fromUser) {
                if (Math.abs(progress - lastBarralVerticalValue) > MIN_GEAR_STEP) {
                    mLogText.setText("Angulo vertical: " + progress + "º");

                    lastBarralVerticalValue = progress;
                    sendCommand(getCommandServoVertical() + String.format("%03d", progress));
                }
            }

            public void onStartTrackingTouch(SeekBarChin VerticalSeekBar) {

            }

            public void onStopTrackingTouch(SeekBarChin VerticalSeekBar) {

            }
        });

        barraHorizontal = (SeekBar)findViewById(R.id.barraHorizontal);
        barraHorizontal.setMax(MAX_GEAR_VALUE);
        barraHorizontal.setProgress(INIT_GEAR_HORIZONTAL_VALUE);
        barraHorizontal.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar VerticalSeekBar, int progress, boolean fromUser) {
                if (Math.abs(progress - lastBarraHorizontalValue) > MIN_GEAR_STEP) {
                    mLogText.setText("Angulo horizontal: " + progress + "º");

                    lastBarraHorizontalValue = progress;
                    sendCommand(getCommandServoHorizontal() + String.format("%03d", progress));
                }
            }

            public void onStartTrackingTouch(SeekBar VerticalSeekBar) {

            }

            public void onStopTrackingTouch(SeekBar VerticalSeekBar) {

            }
        });

        buttonLen.setKeepScreenOn(true);
        
        //connect  
        connectToRouter(m4test);
        
        ForWard.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        sendCommand(COMM_FORWARD);
                        ForWard.setImageDrawable(ForWardon);
                        ForWard.invalidateDrawable(ForWardon);
                        break;
                    case MotionEvent.ACTION_UP:
                        sendCommand(COMM_STOP);
                        ForWard.setImageDrawable(ForWardoff);
                        ForWard.invalidateDrawable(ForWardoff);
                        break;
                }

                return false;
            }
        });
        
        BackWard.setOnTouchListener(new View.OnTouchListener() 
        {
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch(action)
                {
                case MotionEvent.ACTION_DOWN:
                    sendCommand(COMM_BACKWARD);
                    BackWard.setImageDrawable(BackWardon);
                    BackWard.invalidateDrawable(BackWardon);
                    break;                    
                case MotionEvent.ACTION_UP:
                    sendCommand(COMM_STOP);
                    BackWard.setImageDrawable(BackWardoff);
                    BackWard.invalidateDrawable(BackWardoff);
                    break;
                }
                return false;
            }
                    
        });
        
        TurnRight.setOnTouchListener(new View.OnTouchListener() 
        {
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch(action)
                {
                case MotionEvent.ACTION_DOWN:
                    sendCommand(COMM_RIGHT);
                    TurnRight.setImageDrawable(TurnRighton);
                    TurnRight.invalidateDrawable(TurnRighton);
                    break;
                case MotionEvent.ACTION_UP:
                    sendCommand(COMM_STOP);
                    TurnRight.setImageDrawable(TurnRightoff);
                    TurnRight.invalidateDrawable(TurnRightoff);
                    break;
                }
                return false;
            }
        });
        
        TurnLeft.setOnTouchListener(new View.OnTouchListener() 
        {
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch(action)
                {
                case MotionEvent.ACTION_DOWN:
                    sendCommand(COMM_LEFT);
                    TurnLeft.setImageDrawable(TurnLefton);
                    TurnLeft.invalidateDrawable(TurnLefton);
                    break;
                case MotionEvent.ACTION_UP:     
                    sendCommand(COMM_STOP);
                    TurnLeft.setImageDrawable(TurnLeftoff);
                    TurnLeft.invalidateDrawable(TurnLeftoff);
                    break;
                }
                return false;
            }
        });

        AlinearCamara.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                barraHorizontal.setProgress(Integer.parseInt(EJEX_GRADOS_CENTRADO));Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        barraVertical.setProgress(Integer.parseInt(EJEY_GRADOS_CENTRADO));
                    }
                }, 300);
            }
        });
    }

    private OnClickListener buttonLenClickListener = new OnClickListener() {
        public void onClick(View arg0) {            
              if (bLenon) {
                  bLenon = false;
                  sendCommand(COMM_LEN_OFF);
                  buttonLen.setImageDrawable(buttonLenoff);
                  buttonLen.invalidateDrawable(buttonLenon);
              } else  {
                  bLenon = true;
                  sendCommand(COMM_LEN_ON);
                  buttonLen.setImageDrawable(buttonLenon);
                  buttonLen.invalidateDrawable(buttonLenon);
              }
        }
    };
    
    private OnClickListener buttonTakePicClickListener = new OnClickListener() {
        public void onClick(View arg0) {
            if (null != backgroundView) {
                backgroundView.saveBitmap();
            }
        }
    };
    
    private OnClickListener buttonCus1ClickListener = new OnClickListener() {
        public void onClick(View arg0) {       
            Intent setIntent = new Intent();
            setIntent.setClass(mContext, WifiCarSettings.class);
            startActivity(setIntent);
        }
    };
    
    private OnLongClickListener buttonCus1ClickListener2 = new OnLongClickListener() {
        public boolean onLongClick(View arg0) {
            mThreadFlag = false;
            try {
                if (null != mThreadClient)
                    mThreadClient.join(); // wait for secondary to finish
            } catch (InterruptedException e) {
                mLogText.setText("buttonCus1ClickListener2 error:" +  e.getMessage());
            }
            
            connectToRouter(m4test);
            return false;
        }
    };
    
    private void selfcheck() {
        sendCommand(COMM_SELF_CHECK);
    }

    private void sendByteArrayCommand(byte[] data) {
        if ( mWifiStatus != STATUS_CONNECTED || null == mtcpSocket) {
            mLogText.setText("Wifi desconectada o fallo en el socket, intentelo de nuevo ...." +  data.toString());
            return;
        }

        if (!bReaddyToSendCmd) {
            mLogText.setText("please wait 1 second to send msg ....");
            return;
        }

        try {
            mtcpSocket.sendMsg(data);
        } catch (Exception e) {
            Log.i("Socket", e.getMessage() != null ? e.getMessage().toString() : "sendCommand error!");
        }

    }

    private void sendCommand(String data) {
        if ( mWifiStatus != STATUS_CONNECTED || null == mtcpSocket) {
            mLogText.setText("Wifi desconectada o fallo en el socket, intentelo de nuevo ...." +  data);
            return;
        }

        if (!bReaddyToSendCmd) {
            mLogText.setText("please wait 1 second to send msg ....");
            return;
        }

        try {
            mtcpSocket.sendMsg(data);
        } catch (Exception e) {
            Log.i("Socket", e.getMessage() != null ? e.getMessage().toString() : "sendCommand error!");
        }

    }
    
    private void handleCallback(byte[] command) {
        if (null == command || command.length != Constant.COMMAND_LENGTH) {
            return;
        }
        
        byte cmd1 = command[1];
        byte cmd2 = command[2];
        //byte cmd3 = command[3];
        
        if (command[0] != COMMAND_PERFIX || command[Constant.COMMAND_LENGTH-1] != COMMAND_PERFIX) {
        	return;	
        }
        
        if (cmd1 != 0x03) {
        	Log.i("Socket", "unknow command from router, ignor it! cmd1=" + cmd1);
        	return;
        }
        
        switch (cmd2) {
        case (byte)0x01:
            mLogText.setText("ni idea de que es esto");
        	handleHeartBreak();
        	break;
        case (byte)0x02:
            handleHeartBreak();
            break;
        default:
        	
            break;
        }
    }
    
    private int getWifiStatus () {
        int status = WIFI_STATE_UNKNOW;
        WifiManager mWifiMng = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        switch (mWifiMng.getWifiState()) {
        case WifiManager.WIFI_STATE_DISABLED:
        case WifiManager.WIFI_STATE_DISABLING:    
        case WifiManager.WIFI_STATE_ENABLING:
        case WifiManager.WIFI_STATE_UNKNOWN:
            status = WIFI_STATE_DISABLED;
            break;
        case WifiManager.WIFI_STATE_ENABLED:
            status = WIFI_STATE_NOT_CONNECTED;
            ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            State wifiState = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
            if (State.CONNECTED == wifiState) {
                WifiInfo info = mWifiMng.getConnectionInfo();
                if (null != info) {
                    String bSSID = info.getBSSID();
                    String SSID = info.getSSID();
                    Log.i("Socket", "getWifiStatus bssid=" + bSSID + " ssid=" + SSID);
                    if (null != SSID && SSID.length() > 0) {
                        if (SSID.toLowerCase().contains(WIFI_SSID_PERFIX)) {
                            status = WIFI_STATE_CONNECTED;
                        }
                    }
                }
            }
            break;
        default:
            break;
        }
        return status;
    }
    
    private void connectToRouter(boolean isTest) {
        int status = getWifiStatus();
        if (WIFI_STATE_CONNECTED == status || isTest) {
            mThreadFlag = true;
            mThreadClient = new Thread(mRunnable);
            mThreadClient.start();
            String cameraUrl = null;
            if (m4test) {
            	cameraUrl = CAMERA_VIDEO_URL_TEST;
            } else {
            	cameraUrl = CAMERA_VIDEO_URL;
            }
            if (null != cameraUrl && cameraUrl.length() > 4) {
            	backgroundView.setSource(cameraUrl);
            }
        } else if (WIFI_STATE_NOT_CONNECTED == status) {
            mLogText.setText("Wifi no conectada");
        } else {
            mLogText.setText("Wifi conectada!!!");
        }
    }
    private void initWifiConnection() {
        mWifiStatus = STATUS_INIT;
        Log.i("Socket", "initWifiConnection");
        try {
            if (mtcpSocket != null) {
                mtcpSocket.closeSocket();
            }
            String clientUrl = ROUTER_CONTROL_URL;
            int clientPort = ROUTER_CONTROL_PORT;
            if (m4test) {
            	clientUrl = ROUTER_CONTROL_URL_TEST;
                clientPort = ROUTER_CONTROL_PORT_TEST;
            }
            mtcpSocket = new SocketClient(clientUrl, clientPort);
            Log.i("Socket", "Wifi Connect created ip=" + clientUrl
            		+ " port=" + clientPort);
            mWifiStatus = STATUS_CONNECTED;
        } catch (Exception e) {
            Log.d("Socket", "initWifiConnection return exception! ");
        }
        
        Message msg = new Message();
        if (mWifiStatus != STATUS_CONNECTED || null == mtcpSocket) {          
            msg.what = MSG_ID_ERR_CONN;
        } else {
            msg.what = MSG_ID_CON_SUCCESS;
        }
        
        mHandler.sendMessage(msg);
    }
    
    private int appendBuffer (byte[] buffer, int len, byte[] dstBuffer, int dstLen) {
    	int j = 0;
    	int i = dstLen;
    	for (i = dstLen; i < Constant.COMMAND_LENGTH && j < len; i++) {
    		dstBuffer[i] = buffer[j];
    		j++;
    	}
    	return i;
    }
    
    private Runnable mRunnable = new Runnable() 
    {
        public void run()
        {   
            BufferedInputStream is = null;
            try {
                initWifiConnection();

                //mBufferedReaderClient = new BufferedReader(new InputStreamReader(mtcpSocket.getInputStream()));
                
                is = new BufferedInputStream(mtcpSocket.getInputStream());
            } catch (Exception e) {
                Message msg = new Message();
                msg.what = MSG_ID_ERR_INIT_READ;
                mHandler.sendMessage(msg);
                return;
            }            

            byte[] buffer = new byte[256];
            long lastTicket = System.currentTimeMillis();
            byte[] command = {0,0,0,0,0};
            int commandLength = 0;
            int i = 0;
            while (mThreadFlag)
            {
                try
                {
                    //if ( (recvMessageClient = mBufferedReaderClient.readLine()) != null )
                    //int ret = mBufferedReaderClient.read(buffer);
                    int ret = is.read(buffer);
                    if (ret > 0) {
                    	
	                    printRecBuffer ("receive buffer", buffer, ret);
	                    
	                    if(ret > 0 && ret <= Constant.COMMAND_LENGTH ) { 
	                    	long newTicket = System.currentTimeMillis();
	                    	long ticketInterval = newTicket - lastTicket;
	                    	Log.d("Socket", "time ticket interval =" + ticketInterval);
	                		
	                    	if (ticketInterval < Constant.MIN_COMMAND_REC_INTERVAL) {
	                    		if (commandLength > 0) {
	                    			commandLength = appendBuffer(buffer, ret, command, commandLength);
	                    		} else {
	                    			Log.d("Socket", "not recognized command-1");
	                    		}
	                    	} else {
	                    		if (buffer[0] == COMMAND_PERFIX ) {
	                    			for (i = 0; i < ret; i++) {
	                                    command[i] = buffer[i];
	                                }
	                    			commandLength = ret;
	                    		} else {
	                    			Log.d("Socket", "not recognized command-2");
	                    			commandLength = 0;
	                    		}
	                    	}
	                        
	                    	lastTicket = newTicket;
	                    	printRecBuffer ("print command", command, commandLength);
	                    	
	                    	if (commandLength >= Constant.COMMAND_LENGTH) {
	                    		Message msg = new Message();
	                            msg.what = MSG_ID_CON_READ;
	                            msg.obj = command;
	                            mHandler.sendMessage(msg);
	                            commandLength = 0;
	                    	} 
	                    }
                    }
                } catch (Exception e) {
                    Message msg = new Message();
                    msg.what = MSG_ID_ERR_RECEIVE;
                    mHandler.sendMessage(msg);
                }
            }
        }
    };
    
    void printRecBuffer(String tag, byte[] buffer, int len) {
    	StringBuffer sb = new StringBuffer();
    	sb.append(tag);
    	sb.append(" len = ");
    	sb.append(len);
    	sb.append(" :");
    	for (int i =0 ;i < len; i++) {
    		sb.append(buffer[i]);
    		sb.append(", ");
    	}
    	Log.i("Socket", sb.toString());
    }
    
    Handler mHandler = new Handler()
    {                                        
          public void handleMessage(Message msg)                                        
          {  
              Log.i("Main", "handle internal Message, id=" + msg.what);
              
              switch (msg.what) {
              case MSG_ID_ERR_RECEIVE:
                  break;
              case MSG_ID_CON_READ:
                  byte[] command = (byte[])msg.obj;
                  //mLogText.setText("handle response from router: " + command.toString() );
                  handleCallback(command);
                  break;
              case MSG_ID_ERR_INIT_READ:
                  mLogText.setText("MSG_ID_ERR_INIT_READ");
                  break;
              case MSG_ID_CON_SUCCESS:
                  mLogText.setText("MSG_ID_CON_SUCCESS");
    
                  Message msgStartCheck = new Message();
                  msgStartCheck.what = MSG_ID_START_CHECK;
                  mHandler.sendMessageDelayed(msgStartCheck, 3000);
                  
                  Message msgHB1 = new Message();
                  msgHB1.what = MSG_ID_HEART_BREAK_RECEIVE;
                  mHandler.sendMessage(msgHB1);
                  
                  Message msgHB2 = new Message();
                  msgHB2.what = MSG_ID_HEART_BREAK_SEND;
                  //mHandler.sendMessage(msgHB2);
                  
                  break;
              case MSG_ID_START_CHECK:
                  mLogText.setText("MSG_ID_START_CHECK");
                  bReaddyToSendCmd = true;
                  //selfcheck();
                  break;
              case MSG_ID_ERR_CONN:
                  mLogText.setText("MSG_ID_ERR_CONN");
                  break;
              case MSG_ID_CLEAR_QUIT_FLAG:
                  mQuitFlag = false;
                  break;
              case MSG_ID_HEART_BREAK_RECEIVE:
                  if (mHeartBreakCounter == 0) {
                      bHeartBreakFlag = false;
                      
                  } else if (mHeartBreakCounter > 0) {
                      bHeartBreakFlag = true;
                  } else {
                      mLogText.setText("mHeartBreakCounter < 0");
                  }
                  Log.i("main", "handle MSG_ID_HEART_BREAK_RECEIVE :flag=" + bHeartBreakFlag);
                  
                  if (mLastCounter == 0 && mHeartBreakCounter > 0) {
                      startIconAnimation();
                  }
                  mLastCounter = mHeartBreakCounter;
                  mHeartBreakCounter = 0;
                  Message msgHB = new Message();
                  msgHB.what = MSG_ID_HEART_BREAK_RECEIVE;
                  mHandler.sendMessageDelayed (msgHB, HEART_BREAK_CHECK_INTERVAL);
                  break;
              case MSG_ID_HEART_BREAK_SEND:
            	  Message msgSB = new Message();
                  msgSB.what = MSG_ID_HEART_BREAK_SEND;
                  Log.i("main", "handle MSG_ID_HEART_BREAK_SEND");
                  
                  sendCommand(COMM_HEART_BREAK);
                  mHandler.sendMessageDelayed (msgSB, HEART_BREAK_SEND_INTERVAL);
            	  break;
              default :
                  break;
              }
              super.handleMessage(msg);            

          }
     };
     
     private boolean isIconAnimationEnabled () {
         return bAnimationEnabled && bHeartBreakFlag;
     }
     private boolean mIconAnimationState = false;
     /** Icon animation handler for flashing warning alerts. */
     private final Handler mAnimationHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mIconAnimationState) {
                mAnimIndicator.setAlpha(255);
                if (isIconAnimationEnabled()) {
                    mAnimationHandler.sendEmptyMessageDelayed(0, WARNING_ICON_ON_DURATION_MSEC);
                }
            } else {
                mAnimIndicator.setAlpha(0);
                if (isIconAnimationEnabled()) {
                    mAnimationHandler.sendEmptyMessageDelayed(0, WARNING_ICON_OFF_DURATION_MSEC);
                }
            }
            mIconAnimationState = !mIconAnimationState;
            mAnimIndicator.invalidateDrawable(mWarningIcon);
        }
    };
    
    private void startIconAnimation() {
        Log.i("Animation", "startIconAnimation handler : " + mAnimationHandler);
        if (mAnimIndicator != null) {
            mAnimIndicator.setImageDrawable(mWarningIcon);
        }
        if (isIconAnimationEnabled())
            mAnimationHandler.sendEmptyMessageDelayed(0, WARNING_ICON_ON_DURATION_MSEC);
    }
    
    private void handleHeartBreak() {
        Log.i("Main", "handleHeartBreak");
        mHeartBreakCounter++;
        bHeartBreakFlag = true;
    }
    
    private void stopIconAnimation() {
        mAnimationHandler.removeMessages(0);
    }
    
    /*public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUserh) {
        
        if (Math.abs(progress - mSeekBarValue) > MIN_GEAR_STEP) {
            mLogText.setText("change angle: " + progress);

            //mSeekBarValue = progress;
            //COMM_GEAR_CONTROL = (byte)progress;
            //sendCommand(COMM_GEAR_CONTROL);
        }
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
      
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
   
    }*/
    public void onDestroy() {     
        if(null != mtcpSocket) {                
            try {
                mtcpSocket.closeSocket();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mThreadFlag = false;
            mThreadClient.interrupt();
        }
        
        if (null != mHandler) {
        	int i;
        	for (i = MSG_ID_LOOP_START + 1; i < MSG_ID_LOOP_END; i++ ) {
        		mHandler.removeMessages(i);
        	}
        }
        stopIconAnimation();
        super.onDestroy();
    }
    
    @Override
    protected void onResume() {
        initSettings ();
    	int status = getWifiStatus();
        if (WIFI_STATE_CONNECTED == status || m4test) {
            String cameraUrl = null;
            if (m4test) {
            	cameraUrl = CAMERA_VIDEO_URL_TEST;
            } else {
            	cameraUrl = CAMERA_VIDEO_URL;
            }
            if (null != cameraUrl && cameraUrl.length() > 4) {
                mLogText.setText("Reestableciendo url camara: " + cameraUrl);
            	backgroundView.setSource(cameraUrl);
                backgroundView.resumePlayback();
            }
        }
        establecerParametrosMotor();
        super.onResume();
    }
    
    @Override
    public void onBackPressed() {
        if (mQuitFlag) {
            finish();
        } else {
            mQuitFlag = true;
            Toast.makeText(mContext, "Presiona de nuevo para salir", Toast.LENGTH_LONG).show();
            Message msg = new Message();    
            msg.what = MSG_ID_CLEAR_QUIT_FLAG;
            mHandler.sendMessageDelayed(msg, QUIT_BUTTON_PRESS_INTERVAL);
        }
    }
    
    void initSettings () {
		 SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		 
		 String CameraUrl = settings.getString(Constant.PREF_KEY_CAMERA_URL, Constant.DEFAULT_VALUE_CAMERA_URL);
		 CAMERA_VIDEO_URL = CameraUrl;
		 CameraUrl = settings.getString(Constant.PREF_KEY_CAMERA_URL_TEST, Constant.DEFAULT_VALUE_CAMERA_URL_TEST);
		 CAMERA_VIDEO_URL_TEST = CameraUrl;		 
		 
		 String RouterUrl = settings.getString(Constant.PREF_KEY_ROUTER_URL, Constant.DEFAULT_VALUE_ROUTER_URL);
		 int index = RouterUrl.indexOf(":");
		 String routerIP = "";
		 String routerPort = "";
		 int port = 0;
		 if (index > 0) {
			 routerIP = RouterUrl.substring(0, index);
			 routerPort = RouterUrl.substring(index+1, RouterUrl.length() );
			 port = Integer.parseInt(routerPort);
		 }
		 
		 ROUTER_CONTROL_URL = routerIP;
		 ROUTER_CONTROL_PORT = port;

        RouterUrl = settings.getString(Constant.PREF_KEY_ROUTER_URL_TEST, Constant.DEFAULT_VALUE_ROUTER_URL_TEST);
		 index = RouterUrl.indexOf(":");
		 if (index > 0) {
			 routerIP = RouterUrl.substring(0, index);
			 routerPort = RouterUrl.substring(index+1, RouterUrl.length() );
			 port = Integer.parseInt(routerPort);
		 }
		 
		 ROUTER_CONTROL_URL_TEST = routerIP;
		 ROUTER_CONTROL_PORT_TEST = port;

        m4test = settings.getBoolean(Constant.PREF_KEY_TEST_MODE_ENABLED, false);

        COMM_LEN_ON = settings.getString(Constant.PREF_KEY_LEN_ON, Constant.DEFAULT_VALUE_LEN_ON);
        COMM_LEN_OFF = settings.getString(Constant.PREF_KEY_LEN_OFF, Constant.DEFAULT_VALUE_LEN_OFF);

        COMM_FORWARD = settings.getString(Constant.PREF_KEY_AVANZAR, Constant.DEFAULT_VALUE_AVANZAR);
        COMM_BACKWARD = settings.getString(Constant.PREF_KEY_RETROCEDER, Constant.DEFAULT_VALUE_RETROCEDER);
        COMM_LEFT = settings.getString(Constant.PREF_KEY_IZQUIERDA, Constant.DEFAULT_VALUE_IZQUIERDA);
        COMM_RIGHT = settings.getString(Constant.PREF_KEY_DERECHA, Constant.DEFAULT_VALUE_DERECHA);
        COMM_STOP = settings.getString(Constant.PREF_KEY_PARAR, Constant.DEFAULT_VALUE_PARAR);

        MIN_GEAR_STEP = Integer.parseInt(settings.getString(Constant.PREF_KEY_GEAR_STEP, Constant.DEFAULT_VALUE_GEAR_STEP));
    }
    
    void initLenControl (String prefKey, String defaultValue) {
   	 	/*SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

    	String comm = settings.getString(prefKey, defaultValue);
    	CommandArray cmd = new CommandArray(comm);
		if (cmd.isValid() ) {
			if (Constant.PREF_KEY_LEN_ON.equalsIgnoreCase(prefKey)) {
				COMM_LEN_ON[1] = cmd.mCmd1;
				COMM_LEN_ON[2] = cmd.mCmd2;
				COMM_LEN_ON[3] = cmd.mCmd3;
			} else if (Constant.PREF_KEY_LEN_OFF.equalsIgnoreCase(prefKey)) {
				COMM_LEN_OFF[1] = cmd.mCmd1;
				COMM_LEN_OFF[2] = cmd.mCmd2;
				COMM_LEN_OFF[3] = cmd.mCmd3;	
			} else {
				Log.i("Main", "unknow prefKey:" + prefKey); 
			}
		} else {
			Log.i("Main", "error format of command:" + comm); 
		}*/
    }

    void establecerParametrosMotor()
    {
        sendCommand(getCommandPotenciaMotor() + getValuePotenciaMotor());
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                sendCommand(getCommandPotenciaGiro() + getValuePotenciaGiro());
            }
        }, 300);
        handler.postDelayed(new Runnable() {
            public void run() {
                sendCommand(getCommandCorreccionDerecho() + getValueCorreccionDerecho());
            }
        }, 300);
        handler.postDelayed(new Runnable() {
            public void run() {
                sendCommand(getCommandCorreccionIzquierdo() + getValueCorreccionIzquierdo());
            }
        }, 300);
    }

    String getCommandStart()
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        return settings.getString(Constant.PREF_KEY_COMMAND_START, Constant.DEFAULT_VALUE_COMMAND_START);
    }

    String getCommandServoVertical()
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        return getCommandStart() + settings.getString(Constant.PREF_KEY_COMMAND_SERVO_VERTICAL, Constant.DEFAULT_VALUE_COMMAND_SERVO_VERTICAL);
    }

    String getCommandServoHorizontal()
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        return getCommandStart() + settings.getString(Constant.PREF_KEY_COMMAND_SERVO_HORIZONTAL, Constant.DEFAULT_VALUE_COMMAND_SERVO_HORIZONTAL);
    }

    String getCommandPotenciaMotor()
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        return getCommandStart() + settings.getString(Constant.PREF_KEY_COMMAND_POTENCIA, Constant.DEFAULT_VALUE_COMMAND_POTENCIA);
    }

    String getCommandPotenciaGiro()
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        return getCommandStart() + settings.getString(Constant.PREF_KEY_COMMAND_POTENCIA_GIRO, Constant.DEFAULT_VALUE_COMMAND_POTENCIA_GIRO);
    }

    String getCommandCorreccionDerecho()
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        return getCommandStart() + settings.getString(Constant.PREF_KEY_COMMAND_POTENCIA_DERECHO, Constant.DEFAULT_VALUE_COMMAND_POTENCIA_DERECHO);
    }

    String getCommandCorreccionIzquierdo()
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        return getCommandStart() + settings.getString(Constant.PREF_KEY_COMMAND_POTENCIA_IZQUIERDO, Constant.DEFAULT_VALUE_COMMAND_POTENCIA_IZQUIERDO);
    }

    String getValuePotenciaMotor()
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        return settings.getString(Constant.PREF_KEY_POTENCIA, Constant.DEFAULT_VALUE_POTENCIA);
    }

    String getValuePotenciaGiro()
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        return settings.getString(Constant.PREF_KEY_POTENCIA_GIRO, Constant.DEFAULT_VALUE_POTENCIA_GIRO);
    }

    String getValueCorreccionDerecho()
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        return settings.getString(Constant.PREF_KEY_POTENCIA_DERECHO, Constant.DEFAULT_VALUE_POTENCIA_DERECHO);
    }

    String getValueCorreccionIzquierdo()
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        return settings.getString(Constant.PREF_KEY_POTENCIA_IZQUIERDO, Constant.DEFAULT_VALUE_POTENCIA_IZQUIERDO);
    }
}