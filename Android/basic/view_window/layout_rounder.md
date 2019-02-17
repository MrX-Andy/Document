### 布局圆角化  
```
//  api >= 21  
setOutlineProvider(new ViewOutlineProvider() {
    @Override
    public void getOutline(View view, Outline outline) {
        outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), 30);
    }
});
setClipToOutline(true); 
```

调用 paint.setXfermode, 传入一个 PorterDuffXfermode;  
  

### Bitmap#圆角化  
调用 paint.setShader, 传入一个 BitmapShader;  

```
BitmapShader shader = new BitmapShader(toTransform, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
Paint paint = new Paint();
paint.setAntiAlias(true);
paint.setShader(shader);
int width = result.getWidth();
int height = result.getHeight();
Canvas canvas = new Canvas(result);
canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
RectF rect = new RectF(0, 0, width, height);
Path path = new Path();
float radius[] = new float[]{roundingRadius[0], roundingRadius[0], roundingRadius[1], roundingRadius[1], roundingRadius[3], roundingRadius[3], roundingRadius[2], roundingRadius[2]};
path.addRoundRect(rect, radius, Path.Direction.CW);
canvas.drawPath(path, paint);
```