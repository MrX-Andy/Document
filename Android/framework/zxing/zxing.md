### zxing 
#### 识别 图片  
```
public static String decode(Bitmap bitmap) {
    MultiFormatReader multiFormatReader = new MultiFormatReader();
    // 获取bitmap的宽高，像素矩阵
    int width = bitmap.getWidth();
    int height = bitmap.getHeight();
    int[] bitmapPixelArray = new int[width * height];
    bitmap.getPixels(bitmapPixelArray, 0, width, 0, 0, width, height);
    RGBLuminanceSource source = new RGBLuminanceSource(width, height, bitmapPixelArray);
    BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
    try {
        Result result = multiFormatReader.decode(binaryBitmap);
        return result.getText();
    } catch (NotFoundException e) {
        e.printStackTrace();
    }
    return "";
}
```