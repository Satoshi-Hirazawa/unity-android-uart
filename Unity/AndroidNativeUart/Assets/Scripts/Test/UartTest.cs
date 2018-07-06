using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;


public class UartTest : MonoBehaviour {

	[SerializeField]
	Text serialState;
	[SerializeField]
	Text serialMessage;
	[SerializeField]
	Text serialDeviceList;
	[SerializeField]
 	InputField boudrateInput;

	[SerializeField]
	Button connectButton;
	[SerializeField]
	Button sendButton;

	[SerializeField]
	ManageScroll scrollManager;

	NativeUart nu;

	bool setLED = false;
	int boudrate = 9600;

	void Awake(){
		nu = NativeUart.Instance;

		sendButton.interactable = false;

		connectButton.onClick.AddListener  (() => {
			SerialConnection();
		});
		sendButton.onClick.AddListener  (() => {
			SerialSend();
		});

		boudrateInput.onEndEdit.AddListener((string boud) => {
			 boudrate = int.Parse(boud);
		});

		nu.OnUartState += SerialState;
		nu.OnUartDeviceList += SerialDeviceList;
		nu.OnUartMessageRead += SerialMessageReceived;
		nu.OnUartMessageReadLine += SerialMessageReceivedLine;

		nu.Init ();
	}

	void Start () {
	}

	void Update () {

		if (Application.platform == RuntimePlatform.Android) {
			if (Input.GetKeyDown(KeyCode.Escape)) {
				Application.Quit();

				return;
			}
		}
	}
		
	public void SerialConnection(){
		nu.Connection (boudrate);
	}

	public void SerialSend(){

		setLED = !setLED;

		if (setLED) {
			nu.SendLine ("ON");
		} 
		else {
			nu.SendLine ("OFF");
		}
	}

	public void SerialDisconnect(){
		nu.Disconnect ();
	}

	public void SerialState(string msg){
		scrollManager.Log (msg + "\n");
		serialState.text = "Callback State : \n " + msg;

		if (msg == "connected") {
			connectButton.interactable = false;
			sendButton.interactable = true;
		}
	}
	public void SerialDeviceList(string msg){
		serialDeviceList.text = "DeviceList : \n" + msg;
	}
	public void SerialMessageReceived(string msg){
		scrollManager.Log (msg);
	}
	public void SerialMessageReceivedLine(string msg){
		serialMessage.text = "Uart Msg : " + msg;
	}

}
