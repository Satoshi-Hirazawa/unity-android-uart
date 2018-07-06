package jp.co.satoshi.uart_plugin;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

import android.app.Activity;
import com.unity3d.player.UnityPlayer;
import java.util.HashMap;

/**
 * Created by LIFE_MAC34 on 2017/08/08.
 */

public class NativeUart extends Activity{

    static UsbManager mUsbManager;
    static UsbDevice mUsbDevice;

    static UsbDeviceConnection connection;

    static UsbEndpoint epIN = null;
    static UsbEndpoint epOUT = null;


    static String GAME_OBJECT = "NativeUart";
    static String CALLBACK_METHOD = "UartCallbackState";
    static  int boudrate = 9600;

    static final String ATMEGA_16U2  = "ATmega16U2";
    static final String FT232R_USB_UART = "FT232R_USB";
    static String deviceName;

    static  int receiveByteLen = 0;

    static boolean isPermission = false;
    static boolean isConnection = false;

    static Context mContext;

    static public void initialize(Context context) {

        // イニシャライズ
        UnityPlayer.UnitySendMessage(GAME_OBJECT, CALLBACK_METHOD, "Uart initialize...");
        mContext = context;

        mUsbManager = (UsbManager)mContext.getSystemService(Context.USB_SERVICE);

        // USBデバイスの検索
        updateDviceList();

        // USBパーミッションの取得
        Thread tPermission = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    while(!isPermission) {
                        isPermission = getPermission();
                        Thread.sleep(1000);
                    }
                }
                catch(Exception e){
                }
            }
        });
        tPermission.start();

        UnityPlayer.UnitySendMessage(GAME_OBJECT, CALLBACK_METHOD, "Uart initialized");
    }

    static public void connection(int baud){
        // connection

        boudrate = baud;

        // USBデバイスの接続
        Thread tConnection = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    while(!isConnection) {
                        if(isPermission) {
                            isConnection = connectDevice();
                        }
                        Thread.sleep(1000);
                    }
                }
                catch(Exception e){
                }
            }
        });
        tConnection.start();
    }


    static public void updateDviceList() {
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();

        if (deviceList == null || deviceList.isEmpty()) {
            UnityPlayer.UnitySendMessage(GAME_OBJECT, "UartCallbackDeviceList", "Error : no device found");
        }
        else {
            String string = "";

            for (String name : deviceList.keySet()) {
                string += name;


                if (deviceList.get(name).getVendorId() == 0x0403) {
                    deviceName = FT232R_USB_UART;
                    receiveByteLen = 2;
                    string += (" : " + deviceName + "\n");
                    mUsbDevice = deviceList.get(name);
                }
                else if (deviceList.get(name).getVendorId() == 0x2341) {
                    deviceName = ATMEGA_16U2;
                    receiveByteLen = 0;
                    string += (" : " + deviceName + "\n");
                    mUsbDevice = deviceList.get(name);
                }
                else {
                    string += "\n";
                }
            }
            UnityPlayer.UnitySendMessage(GAME_OBJECT, "UartCallbackDeviceList", string);
        }
    }

    static public boolean getPermission(){

        if (mUsbDevice == null) {
            UnityPlayer.UnitySendMessage(GAME_OBJECT, CALLBACK_METHOD, "Error : mUsbDevice null");
            return false;
        }
        if(mUsbManager.hasPermission(mUsbDevice)){
            UnityPlayer.UnitySendMessage(GAME_OBJECT, CALLBACK_METHOD, "hasPermission true");
            return true;
        }
        else {
            mUsbManager.requestPermission(mUsbDevice, PendingIntent.getBroadcast(mContext, 0, new Intent("hoge"), 0));
            UnityPlayer.UnitySendMessage(GAME_OBJECT, CALLBACK_METHOD, "Error : Uart hasPermission false");
            return false;
        }
    }

    static public boolean connectDevice() {

        if(!mUsbManager.hasPermission(mUsbDevice)) {
            return false;
        }

        connection = mUsbManager.openDevice(mUsbDevice);

        if (!connection.claimInterface(mUsbDevice.getInterface(mUsbDevice.getInterfaceCount() - 1), true)) {
            connection.close();
            return false;
        }

        epIN = null;
        epOUT = null;

        UnityPlayer.UnitySendMessage(GAME_OBJECT, CALLBACK_METHOD, "boudrate : " + String.valueOf(boudrate));

        if(deviceName == ATMEGA_16U2){

            byte [] lineRequest = set16U2Baud(boudrate);
            connection.controlTransfer(0x21, 34, 0, 0, null, 0, 0);
            connection.controlTransfer(0x21, 32, 0, 0, lineRequest, 7, 0);
            // connection.controlTransfer(0x21, 32, 0, 0, new byte[] {
            //         (byte)0x80, 0x25, 0x00, 0x00, 0x00, 0x00, 0x08
            // }, 7, 0);

        }
        else if(deviceName == FT232R_USB_UART){

            int baudRequest = setFTDIBaud(boudrate);

            UnityPlayer.UnitySendMessage(GAME_OBJECT, CALLBACK_METHOD, "FTDI boud : " + String.valueOf(baudRequest));

            connection.controlTransfer(0x40, 0, 0, 0, null, 0, 0);  //reset
            connection.controlTransfer(0x40, 0, 1, 0, null, 0, 0);  //clear Rx
            connection.controlTransfer(0x40, 0, 2, 0, null, 0, 0);  //clear Tx
            connection.controlTransfer(0x40, 2, 0x0000, 0, null, 0, 0);      //flow control none
            connection.controlTransfer(0x40, 3, baudRequest, 0, null, 0, 0); //baudrate
            connection.controlTransfer(0x40, 4, 0x0008, 0, null, 0, 0);      //data bit 8, parity none, stop bit 1, tx off
        }

        UsbInterface usbIf = mUsbDevice.getInterface(mUsbDevice.getInterfaceCount() - 1);
        for (int i = 0; i < usbIf.getEndpointCount(); i++) {
            if (usbIf.getEndpoint(i).getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                if (usbIf.getEndpoint(i).getDirection() == UsbConstants.USB_DIR_IN)
                    epIN = usbIf.getEndpoint(i);
                else
                    epOUT = usbIf.getEndpoint(i);
            }
        }

        if(connection.getFileDescriptor() != -1){
            UnityPlayer.UnitySendMessage(GAME_OBJECT, CALLBACK_METHOD, "connected");
            readThead();
            return true;
        }
        else {
            UnityPlayer.UnitySendMessage(GAME_OBJECT, CALLBACK_METHOD, "Error : Uart connection faild");
            return false;
        }
    }

    static public void readThead(){

        new Thread(new Runnable(){
            public void run(){
                try{
                    while(true){
                        final int size = 128;
                        final byte[] buffer = new byte[size];
                        final StringBuilder sb = new StringBuilder();

                        int length = connection.bulkTransfer(epIN, buffer, buffer.length, 100);

                        if (length > receiveByteLen) {

                            for(int i = receiveByteLen; i < length; i++){
                                sb.append((char) buffer[i]);
                            }
                            UnityPlayer.UnitySendMessage(GAME_OBJECT, "UartMessageReceived", String.valueOf(sb));
                        }
                        Thread.sleep(1);
                    }
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    static public void send(String str) {
        connection.bulkTransfer(epOUT, str.getBytes(), str.length(), 0);
    }
    static public void disconnect() {

        UsbInterface usbIf = mUsbDevice.getInterface(mUsbDevice.getInterfaceCount() - 1);

        if (connection.releaseInterface(usbIf)) {
            connection.close();
            UnityPlayer.UnitySendMessage(GAME_OBJECT, CALLBACK_METHOD, "Disconnected");
        }
    }

    static public byte[] set16U2Baud(int baudRate) {
        final byte[] lineEncodingRequest = { (byte) 0x80, 0x25, 0x00, 0x00, 0x00, 0x00, 0x08 };
        lineEncodingRequest[0] = (byte)(baudRate & 0xFF);
        lineEncodingRequest[1] = (byte)((baudRate >> 8) & 0xFF);
        lineEncodingRequest[2] = (byte)((baudRate >> 16) & 0xFF);

        return lineEncodingRequest;

    }

    static public int setFTDIBaud(int baudRate) {

        int b = 0x4138;

        switch (baudRate) {
            case 1200:   b = 0x09C4; break;
            case 2400:   b = 0x04E2; break;
            case 4800:   b = 0x0271; break;
            case 9600:   b = 0x4138; break;
            case 14400:  b = 0x80D0; break;
            case 19200:  b = 0x809C; break;
            case 38400:  b = 0xC04E; break;
            case 57600:  b = 0x0034; break;
            case 115200: b = 0x001A; break;
            default:     b = 0x4138; break;
        }
        return b;
    }
}
