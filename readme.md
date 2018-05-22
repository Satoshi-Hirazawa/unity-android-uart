
# Unity で Androidのシリアル通信をするライブラリ

## 動作環境・テスト環境

### Serial Device
- Arduino Uno

### Android

- Android F-04H
- Android SO-02H
- Android SO-02F


## Usage

### Install

1. UnityPackageをインポート

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
    NativeUart.Connection();

### 文字列送信
    NativeUart.Send();

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
        nu.Connection ();
    }

    void Update () {
        nu.Send ("hoge");
    }




