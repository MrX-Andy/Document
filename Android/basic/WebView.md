### WebView  

◆ Android WebView 收到 H5 的数据    
```
webView.addJavascriptInterface(jsCallFunction, "androidWebView");  

public class JsCallFunction {
    @JavascriptInterface
    public void callShortToast(String text) {
        ToastUtil.shortCenter(text);
        LogTrack.e(text);
    }
}
```
◆ Android WebView 传给 H5 数据  
webView.loadUrl("javascript:paramsFromMobile('"+info+"')");  
◆ 清除 音视频等缓存  
webView.loadUrl("about:blank");

◆ 参考  
https://www.jianshu.com/p/345f4d8a5cfa  