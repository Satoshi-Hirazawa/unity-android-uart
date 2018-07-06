using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class NativeUart : MonoBehaviour{

	static NativeUart instance;

	public delegate void UartDataReceivedEventHandler(string message);
	public event UartDataReceivedEventHandler OnUartState;
	public event UartDataReceivedEventHandler OnUartDeviceList;
	public event UartDataReceivedEventHandler OnUartMessageRead;
	public event UartDataReceivedEventHandler OnUartMessageReadLine;

	string readData = null;


	#if UNITY_ANDROID
	AndroidJavaClass nu;
	AndroidJavaObject context;
	AndroidJavaClass unityPlayer;
	#endif

	NativeUart(){
		Debug.Log("Create NativeUart instance.");

		#if UNITY_ANDROID
		nu = new AndroidJavaClass ("jp.co.satoshi.uart_plugin.NativeUart");
		unityPlayer = new AndroidJavaClass("com.unity3d.player.UnityPlayer"); 
		context  = unityPlayer.GetStatic<AndroidJavaObject>("currentActivity");
		#endif			
	}

	~NativeUart(){

		#if UNITY_ANDROID
		nu.CallStatic ("disconnect");
		#endif

		if (instance != null) {
			instance = null;
		}
	}
		
	public static NativeUart Instance {
		get {
			if (instance == null) {
				GameObject obj = new GameObject ("NativeUart");
				instance = obj.AddComponent<NativeUart>();
			}
			return instance;
		}
	}

	public void Init(){
		#if UNITY_ANDROID
		context.Call ("runOnUiThread", new AndroidJavaRunnable(() => {
			nu.CallStatic("initialize", context);
		}));
		#endif
	}


	public void Connection(int boud){
		#if UNITY_ANDROID
		context.Call ("runOnUiThread", new AndroidJavaRunnable(() => {
			nu.CallStatic ("connection", boud);
		}));
		#endif
	}

	public void Send(string msg){
		#if UNITY_ANDROID
		nu.CallStatic ("send", msg);
		#endif
	}

	public void SendLine(string msg){
		#if UNITY_ANDROID
		nu.CallStatic ("send", msg + "\r\n");
		#endif
	}

	public void Disconnect(){
		//		#if UNITY_ANDROID
		//		nativeSerial.CallStatic ("disconnect");
		//		#endif
	}

	public void UartCallbackState(string msg){
		OnUartState(msg);
	}
	public void UartCallbackDeviceList(string msg){
		OnUartDeviceList(msg);
	}
	public void UartMessageReceived(string msg){

		readData = readData + msg;
		if (msg.IndexOf ('\n') > -1) {
			OnUartMessageReadLine(readData);
			readData = null;
		} 
		OnUartMessageRead (msg);
	}
}
