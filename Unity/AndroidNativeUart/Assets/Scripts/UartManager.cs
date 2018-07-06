using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;


public class UartManager : MonoBehaviour {
	
	NativeUart nu;
	bool setLED = false;

	void Awake(){
		nu = NativeUart.Instance;

		nu.OnUartState += SerialState;
		nu.OnUartDeviceList += SerialDeviceList;
		nu.OnUartMessageRead += SerialMessageReceived;
		nu.OnUartMessageReadLine += SerialMessageReceivedLine;

		nu.Init ();
	}

	void Start () {
		nu.Connection (9600);
	}

	void Update () {
	}
		
	public void SerialState(string msg){
		Debug.Log ("State : " + msg);
	}
	public void SerialDeviceList(string msg){
		Debug.Log ("Device : " + msg);
	}
	public void SerialMessageReceived(string msg){
	}
	public void SerialMessageReceivedLine(string msg){
		Debug.Log ("Message : " + msg);
		setLED = !setLED;
		if (setLED) {
			nu.SendLine ("ON");
		} 
		else {
			nu.SendLine ("OFF");
		}
	}
}
