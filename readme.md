
# 
Unity で AndroidとArduinoのシリアル通信をするライブラリ

## Support

### Serial Device
- Arduino Uno
- Arduino Duemilanove
- Arduino NANO
- SWITCH_SCIENCE Usb-Serial-FTDI

### Android

- Android F-04H
- Android SO-02H
- Android SO-02F
- Android Nexus5


## Usage

### Install

1. AndroidNativeUart.unitypackageをインポート
2. Unity/AndroidNativeUart/Assets/Plugins/Android/AndroidManifest.xml を Assets/Plugins下にコピー

### Test

1. testシーンを開く
2. Unityをビルド
3. ArduinoにTestSketchを書き込む
4. ArduinoとAndroidをつないでUSBを検出
5. アプリを開く
6. connectButtonを押す
7. logにArduinoからのメッセーゾが表示される
8. sendButtonでLEDのON/OFF

### Method

### Uartの初期化
    NativeUart.Init();

### Uartの接続
    NativeUart.Connection(int boud);

### 文字列送信
    NativeUart.Send(string msg);

### 文字列送信(終端に改行あり)
    NativeUart.SendLine();

### Uartの切断(未実装)
    NativeUart.Disconnect();

### Event

### Uartの状態を返すイベント
    NativeUart.OnUartState

#### 接続デバイスを返すイベント
    NativeUart.OnUartDeviceList

#### Uartのメッセージを返すイベント
    NativeUart.OnUartMessageRead

#### Uartのメッセージを改行ごとに返すイベント
    NativeUart.OnUartMessageReadLine


### Test Code

    private NativeUart nu;

    void Awake () {
        nu = NativeUart.Instance;
        nu.Init();
    }

    void Start () {
        nu.Connection (9600);
    }

    void Update () {
        nu.Send ("hoge");
    }




