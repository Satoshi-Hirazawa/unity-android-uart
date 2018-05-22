using System;
using System.Collections;
using UnityEngine;
using UnityEngine.UI;

public class ManageScroll : MonoBehaviour {

	// ScrollViewに表示するログ
	public static string Logs = "";
	// ログの差分を取得するための入れ物
	private string oldLogs = "";
	// ScrollViewのScrollRect
	private ScrollRect scrollRect;
	// ScrollViewのText
	private Text textLog;

	// Start時に各オブジェクトを取得
	void Start ()
	{
		scrollRect = this.gameObject.GetComponent<ScrollRect>();
		textLog = scrollRect.content.GetComponentInChildren<Text>();
	}

	void Update ()
	{
		// logsとoldLogsが異なるときにTextを更新
		if (scrollRect != null && Logs != oldLogs) {
			textLog.text = Logs;
			// Textが追加されたときに５フレーム後に自動でScrollViewのBottomに移動するようにする。
			StartCoroutine(DelayMethod(5, () =>
				{
					scrollRect.verticalNormalizedPosition = 0;
				}));
			oldLogs = Logs;
		}   
	}

	//ログを表示
	public void Log(string logText)
	{
		Logs += (logText);
		Debug.Log(logText);
	}

	// 指定したフレーム数後にActionが実行される
	private IEnumerator DelayMethod(int delayFrameCount, Action action)
	{
		for (var i = 0; i < delayFrameCount; i++)
		{
			yield return null;
		}
		action();
	}
}