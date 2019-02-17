#### Insert  

```
@SuppressWarnings("ConstantConditions")
@Nullable
@Override
public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
    int match = uriMatcher.match(uri);
    String tableName = C.Database.getMatchMap().get(match);
    LogTrack.w("tableName = " + tableName);
    sqLiteDatabase.insert(tableName, null, values);
    /*当该URI的ContentProvider数据发生变化时，通知外界（即访问该ContentProvider数据的访问者）*/
    ContentResolver contentResolver = getContext().getContentResolver();
    if (contentResolver != null) {
        contentResolver.notifyChange(uri, null);
    }
    return uri;
}
```
