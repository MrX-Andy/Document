Android接口定义语言  
Android Interface Definition Language  

### 定向#Tag  
传参时除了Java基本类型以及 String, CharSequence之外的类型;  
都需要在前面加上定向 tag, 具体加什么量需而定;  
AIDL 中的定向 tag 表示了在跨进程通信中数据的流向, 其中 in 表示数据只能由客户端流向服务端,  out 表示数据只能由服务端流向客户端,   
而 inout 则表示数据可在服务端与客户端之间双向流通, 其中, 数据流向是针对在客户端中的那个传入方法的对象而言的;  
in 为定向 tag 的话表现为服务端将会接收到一个那个对象的完整数据, 但是客户端的那个对象不会因为服务端对传参的修改而发生变动;  
out 的话表现为服务端将会接收到那个对象的的空对象, 但是在服务端对接收到的空对象有任何修改之后客户端将会同步变动;  
inout 为定向 tag 的情况下, 服务端将会接收到客户端传来对象的完整信息, 并且客户端将会同步服务端对该对象的任何变动;  

### DownloadMessageEntity  
readFromParcel  
一定要手动写一个 readFromParcel 方法;  
注意, 此处的读值顺序应当是和writeToParcel()方法中一致的;  
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
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString()) {
    }

    fun readFromParcel(inParcel: Parcel) {
        time = inParcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(time)
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
### SampleAidl.java  
```
/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\WorkSpace\\Android\\AndFun\\app_basic_service\\src\\main\\aidl\\com\\alex\\andfun\\service\\huggles\\model\\IDownloadEntityAidlInterface.aidl
 */
package com.alex.andfun.service.huggles.model;
public interface IDownloadEntityAidlInterface extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.alex.andfun.service.huggles.model.IDownloadEntityAidlInterface
{
private static final java.lang.String DESCRIPTOR = "com.alex.andfun.service.huggles.model.IDownloadEntityAidlInterface";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.alex.andfun.service.huggles.model.IDownloadEntityAidlInterface interface,
 * generating a proxy if needed.
 */
public static com.alex.andfun.service.huggles.model.IDownloadEntityAidlInterface asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.alex.andfun.service.huggles.model.IDownloadEntityAidlInterface))) {
return ((com.alex.andfun.service.huggles.model.IDownloadEntityAidlInterface)iin);
}
return new com.alex.andfun.service.huggles.model.IDownloadEntityAidlInterface.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
java.lang.String descriptor = DESCRIPTOR;
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(descriptor);
return true;
}
case TRANSACTION_getDownloadMessageEntity:
{
data.enforceInterface(descriptor);
com.alex.andfun.service.huggles.entity.DownloadMessageEntity _result = this.getDownloadMessageEntity();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_addMessage:
{
data.enforceInterface(descriptor);
com.alex.andfun.service.huggles.entity.DownloadMessageEntity _arg0;
if ((0!=data.readInt())) {
_arg0 = com.alex.andfun.service.huggles.entity.DownloadMessageEntity.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.addMessage(_arg0);
reply.writeNoException();
if ((_arg0!=null)) {
reply.writeInt(1);
_arg0.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
default:
{
return super.onTransact(code, data, reply, flags);
}
}
}
private static class Proxy implements com.alex.andfun.service.huggles.model.IDownloadEntityAidlInterface
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public com.alex.andfun.service.huggles.entity.DownloadMessageEntity getDownloadMessageEntity() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
com.alex.andfun.service.huggles.entity.DownloadMessageEntity _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getDownloadMessageEntity, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = com.alex.andfun.service.huggles.entity.DownloadMessageEntity.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void addMessage(com.alex.andfun.service.huggles.entity.DownloadMessageEntity entity) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((entity!=null)) {
_data.writeInt(1);
entity.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_addMessage, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
entity.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_getDownloadMessageEntity = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_addMessage = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
}
public com.alex.andfun.service.huggles.entity.DownloadMessageEntity getDownloadMessageEntity() throws android.os.RemoteException;
public void addMessage(com.alex.andfun.service.huggles.entity.DownloadMessageEntity entity) throws android.os.RemoteException;
}

```

### 参考 
https://www.cnblogs.com/rookiechen/p/5352053.html  
https://www.jianshu.com/p/375e3873b1f4  