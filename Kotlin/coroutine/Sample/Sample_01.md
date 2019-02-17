### 简单的小案例  
#### 预期需求描述  
先执行getUserId，之后根据userId获取emailMessage 和 smsMessage；其中三个操作都是耗时操作；  
且getUserId 第一执行，之后 getEmailMessage 和 getSmsMessage 并发执行，且同时在 getUserId 之后执行；  
如果用户点击取消，或者结束当前页面，耗时操作需要结束，或者不会继续得到结果并刷新UI，因为此时UI可能已经不在了；  

#### Activity  
```
package com.alex.andfun.coroutine
class New02Activity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.module_new_02)
        BindClickListener(this)
    }

    private val sample05 = Sample05()
    override fun onClick(v: View) {
    
        if (v.id == R.id.bt3) {
            sample05.getMessageList()
            return
        }
        if (v.id == R.id.bt4) {
            sample05.cancelGetMessageList()
            return
        }

    }

    override fun onDestroy() {
        sample05.cancelGetMessageList()
        super.onDestroy()
    }
}

```
#### 获取网络数据  
````
package com.alex.andfun.coroutine.sample

import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI
import org.alex.extension.logE
import org.alex.extension.logI
import org.alex.extension.logW
import org.alex.util.BaseUtil

/**
 * 作者：Alex
 * 时间：2018/7/2318:53
 * 简述：
 */
class Sample05 {

    private val job = Job()
    override fun getMessageList() {
        CoroutineScope(Main + job).launch {

        }

    }
    
    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    private suspend fun getUserId(time: Long): String {
        delay(time)
        return "0x999000"
    }

    private suspend fun getEmailMessage(time: Long, userId: String): String {
        delay(time)
        "getEmailMessage 返回结果".logW()
        return "给 $userId 的邮件消息"
    }

    private suspend fun getSmsMessage(time: Long, userId: String): String {
        delay(time)
        "getSmsMessage返回结果".logW()
        return "给 $userId 的短信消息"
    }

    private fun onGetMessageList(text: String) {
        "收到消息：$text ，主线程 ${BaseUtil.isUIThread()}".logI()
    }

}

````