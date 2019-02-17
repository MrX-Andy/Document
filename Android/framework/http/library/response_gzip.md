### response gzip压缩  
```
int responseCode = httpURLConnection.getResponseCode();
if (responseCode == HttpURLConnection.HTTP_OK) {
    //un gzip
    in = httpURLConnection.getInputStream();
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    int len1;
    byte[] buffer1 = new byte[1024];
    while ((len1 = in.read(buffer1)) != -1) {
        byteArrayOutputStream.write(buffer1, 0, len1);
    }
    in.close();
    byteArrayOutputStream.close();
    final String str1 = new String(byteArrayOutputStream.toByteArray(), "utf-8");
    Debug.i_MrFu("未做解压的数据 = " + str1);

    //do gzip
    in = new GZIPInputStream(httpURLConnection.getInputStream());
    ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
    int len;
    byte[] buffer = new byte[1024];
    while ((len = in.read(buffer)) != -1) {
        arrayOutputStream.write(buffer, 0, len);
    }
    in.close();
    arrayOutputStream.close();
    final String str = new String(arrayOutputStream.toByteArray(), "utf-8");
    Debug.i_MrFu("做了解压的数据 = " + str);
}
```