Android接口定义语言  
Android Interface Definition Language  

### 定向#Tag  
传参时除了Java基本类型以及String，CharSequence之外的类型；  
都需要在前面加上定向tag，具体加什么量需而定；  
AIDL中的定向 tag 表示了在跨进程通信中数据的流向，其中 in 表示数据只能由客户端流向服务端， out 表示数据只能由服务端流向客户端，  
而 inout 则表示数据可在服务端与客户端之间双向流通。其中，数据流向是针对在客户端中的那个传入方法的对象而言的。  
in 为定向 tag 的话表现为服务端将会接收到一个那个对象的完整数据，但是客户端的那个对象不会因为服务端对传参的修改而发生变动；  
out 的话表现为服务端将会接收到那个对象的的空对象，但是在服务端对接收到的空对象有任何修改之后客户端将会同步变动；  
inout 为定向 tag 的情况下，服务端将会接收到客户端传来对象的完整信息，并且客户端将会同步服务端对该对象的任何变动。  

### DownloadMessageEntity  
readFromParcel  
一定要手动写一个 readFromParcel 方法；  
注意，此处的读值顺序应当是和writeToParcel()方法中一致的；  
```
fun readFromParcel(inParcel: Parcel) {
    time = inParcel.readString()
    title = inParcel.readString()
    content = inParcel.readString()
}
```
剩下的, 用插件自动生成  
```
@SuppressLint("ParcelCreator")
class DownloadMessageEntity(
        var time: String = "2017-12-10 20:00",
        var title: String = "标题",
        var content: String = "具体内容"
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    fun readFromParcel(inParcel: Parcel) {
        time = inParcel.readString()
        title = inParcel.readString()
        content = inParcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(time)
        parcel.writeString(title)
        parcel.writeString(content)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DownloadMessageEntity> {
        override fun createFromParcel(parcel: Parcel): DownloadMessageEntity {
            return DownloadMessageEntity(parcel)
        }

        override fun newArray(size: Int): Array<DownloadMessageEntity?> {
            return arrayOfNulls(size)
        }
    }
}
```
### Entity.aidl  
```
package com.alex.andfun.service.back.entity;  
parcelable  DownloadMessageEntity;  
```
### EntityAidlInterface  
```
package com.alex.andfun.service.back.model;
import com.alex.andfun.service.back.entity.DownloadMessageEntity;

interface IDownloadEntityAidlInterface {
        DownloadMessageEntity getDownloadMessageEntity();
        void addMessage(inout DownloadMessageEntity entity);
}
```  

### 参考 
https://www.cnblogs.com/rookiechen/p/5352053.html  
https://www.jianshu.com/p/375e3873b1f4  