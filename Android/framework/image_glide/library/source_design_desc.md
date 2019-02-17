### 源码设计描述  

如果想做图片自适应, 又担心比较占据内存, 可以加上 maxWidth 和 maxHeight;   
否则可能会因为 wrap_content 导致图片过大, 引起内存飙升;  

内存缓存, 和磁盘缓存的转换;  

1.. 当使用 Glide加载图片时, Glide默认 根据 View视图对图片进行压缩 & 转换, 而不显示原始图, 这也是Glide加载速度高于Picasso的原因;   
2.. Glide的缓存功能设计成 二级缓存：内存缓存 & 硬盘缓存;   
3.. 缓存读取顺序：内存缓存 --> 磁盘缓存 --> 网络;  
4.. 内存缓存 默认开启, 内存缓存 & 磁盘缓存相互不影响，独立配置;  
5.. 内存缓存, 防止应用 重复将图片数据 读取到内存当中, 只缓存转换过后的图片;  
6.. 硬盘缓存, 防止应用 重复从网络或其他地方重复下载和读取数据;   
7.. 可缓存原始图片 & 缓存转换过后的图片，用户自行设置;  
8.. Glide的缓存机制使得 Glide具备非常好的图片缓存效果，从而使得具备较高的图片加载效率, 比如, 在 RecyclerView 上下滑动,   
    而RecyclerView中只要是Glide加载过的图片，都可以直接从内存中读取 & 展示，从而不需要重复从 网络或硬盘上读取，提高图片加载效率;  


### 缓存机制  
内存缓存主要是防止应用重复的把数据读到内存中，而硬盘缓存主要是防止应用重复的去网络上下载图片;  
这样才能让框架加载图片的速度更快，也能更省流量;  

默认情况下，Glide 会在开始一个新的图片请求之前检查以下多级的缓存:  
活动资源 (Active Resources) - 现在是否有另一个 View 正在展示这张图片;  
内存缓存 (Memory cache) - 该图片是否最近被加载过并仍存在于内存中;  
资源类型（Resource） - 该图片是否之前曾被解码、转换并写入过磁盘缓存;  
数据来源 (Data) - 构建这个图片的资源是否之前曾被写入过文件缓存;  

Glide 中内存缓存有两种, 
一级内存缓存，可以理解为 活跃的内存缓存, 和页面同生命周期的;  
二级内存缓存，可以理解为 LruCache, 和 app 同生命周期的;  
先用 活跃的缓存, 活跃的缓存中没有, 会到LruCache 中找, 如果找到, 会把这个缓存, 从LruCache 中, 搬到 活跃的缓存中;  
首次拿到的数据, 放在缓存中, 会先放到 活跃的缓存中;   
当页面结束, 再把这个缓存, 从活跃的缓存中, 搬到LruCache;  
  
loadFromActiveResources 使用的是 ActiveResources, 内部维护的是,   final Map<Key, ResourceWeakReference> activeEngineResources = new HashMap<>();  
暴露 get-取, activate-存, deactivate-删 等方法;  
做手动的计数管理, 当资源计数为0时, 则回收;  

loadFromCache 使用的是 LruCache, 内部维护的是   private final Map<T, Y> cache = new LinkedHashMap<>(100, 0.75f, true);  
暴露 get-取, put-存, remove-删 等方法;  

在 Engine#load 方法中, 
首先调用 EngineResource<?> active = loadFromActiveResources(key, isMemoryCacheable);  
如果存在 活跃的内存缓存, 则直接使用这个缓存对象;  

如果不存在 活跃的内存缓存, 与之对应, 则 调用 EngineResource<?> cached = getEngineResourceFromCache(key);  
在 getEngineResourceFromCache 方法中, 先 调用 LruCache.remove 方法, 讲对应的缓存, 取出来, 并移除,  
如果该缓存存在, 并加入 活跃的缓存中;  
那么什么时候会有 加入LruCache 的case 呢?  参考下面的 [页面退出, 内存缓存的转化]  
那么什么时候会有 加入活跃的缓存中呢?  参考下面的[首次缓存网络数据]  

#### 页面退出, 内存缓存的转化    
Activity#onDestroy  
FragmentManagerImpl#dispatchDestroy  
Fragment#performDestroy  
glide.ActivityFragmentLifecycle#onDestroy  
glide.RequestManager#onDestroy  
glide.RequestManager#clear  
glide.RequestManager#untrackOrDelegate  
RequestManager#untrack  
RequestTracker#clearRemoveAndRecycle  
RequestTracker#clearRemoveAndMaybeRecycle  
SingleRequest#clear  
SingleRequest#releaseResource  
Engine#release  
EngineResource#release  
Engine#onResourceReleased  
```
@Override
public void onResourceReleased(Key cacheKey, EngineResource<?> resource) {
    Util.assertMainThread();
    //  从 活跃的缓存中 移除  
    activeResources.deactivate(cacheKey);
    if (resource.isCacheable()) {
      //  加入 LruCache
      cache.put(cacheKey, resource);
    } else {
      resourceRecycler.recycle(resource);
    }
}
```
#### 首次缓存网络数据  
DecodeJob#decodeFromRetrievedData  
DecodeJob#notifyEncodeAndRelease  
DecodeJob#notifyComplete  
EngineJob#onResourceReady  
EngineJob.MainThreadCallback#handleMessage  
```
@Override
public boolean handleMessage(Message message) {
  EngineJob<?> job = (EngineJob<?>) message.obj;
  switch (message.what) {
    case MSG_COMPLETE:
      job.handleResultOnMainThread();
      break;
    case MSG_EXCEPTION:
      job.handleExceptionOnMainThread();
      break;
    case MSG_CANCELLED:
      job.handleCancelledOnMainThread();
      break;
    default:
      throw new IllegalStateException("Unrecognized message: " + message.what);
  }
  return true;
}
```
EngineJob#handleResultOnMainThread  
```
@Synthetic
void handleResultOnMainThread() {
    stateVerifier.throwIfRecycled();
    engineResource.acquire();
    listener.onEngineJobComplete(this, key, engineResource);
    //noinspection ForLoopReplaceableByForEach to improve perf
    for (int i = 0, size = cbs.size(); i < size; i++) {
      ResourceCallback cb = cbs.get(i);
      if (!isInIgnoredCallbacks(cb)) {
        engineResource.acquire();
        cb.onResourceReady(engineResource, dataSource);
      }
    }
    // Our request is complete, so we can release the resource.
    engineResource.release();
    release(false /*isRemovedFromQueue*/);
}
```
Engine#onEngineJobComplete  
```
@Override
public void onEngineJobComplete(EngineJob<?> engineJob, Key key, EngineResource<?> resource) {
    Util.assertMainThread();
    // A null resource indicates that the load failed, usually due to an exception.
    if (resource != null) {
      resource.setResourceListener(key, this);

      if (resource.isCacheable()) {
        //  存到 活跃的 缓存中
        activeResources.activate(key, resource);
      }
    }

    jobs.removeIfCurrent(key, engineJob);
}
```

loadFromCache  从内存缓存中获取资源，获取成功后会放入到activeResources中;  
loadFromActiveResources  从存活的资源中加载资源，资源加载完成后，再将这个缓存数据放到一个 value 为软引用的 activeResources map 中，  
并计数引用数，在图片加载完成后进行判断，如果引用计数为空则回收掉;  

Engine#loadFromCache  
Engine#onEngineJobComplete  
ActiveResources#activate   
```
void activate(Key key, EngineResource<?> resource) {
    ResourceWeakReference toPut = new ResourceWeakReference(key,resource,getReferenceQueue(),isActiveResourceRetentionAllowed);
    ResourceWeakReference removed = activeEngineResources.put(key, toPut);
    if (removed != null) {
      removed.reset();
   }
}
```
Engine#onResourceReleased  
```
@Override
public void onResourceReleased(Key cacheKey, EngineResource<?> resource) {
    Util.assertMainThread();
    activeResources.deactivate(cacheKey);
    if (resource.isCacheable()) {
      cache.put(cacheKey, resource);
    } else {
      resourceRecycler.recycle(resource);
    }
}
```


### 加载流程  
1.. 生成图片缓存 key;  
2.. 创建缓存对象 LruResourceCache;  
3.. 从内存中 获取图片缓存, 如果内存中有对应的图片缓存, 直接读取内存中缓存的图片, 这里的图片已经是转换后的图片了, 然后立即展示出来;                  
4.. 如果内存中无对应的图片缓存, 开启图片加载线程;  
5.. 从磁盘缓存中读取图片缓存,  如果存在磁盘缓存,  直接获取, 并处理, 并展示;  
6.. 如果磁盘缓存, 不存在, 从网络获取图片资源,  写入磁盘缓存, 写入内存缓存, 展示图片;  

MultiModelLoader.MultiFetcher#loadData  //  触发网络请求  

### setImageDrawable  
RequestBuilder#into  
RequestManager#track  
RequestTracker#runRequest   
SingleRequest#begin  
SingleRequest#onSizeReady  
Engine#load  

SingleRequest#onResourceReady  
ImageViewTarget#onResourceReady  
ImageViewTarget#setResourceInternal  
DrawableImageViewTarget#setResource  
ImageView#setImageDrawable  

### into 函数, 发生了什么?  
在哪里触发的请求   
RequestBuilder#into  
```
  private <Y extends Target<TranscodeType>> Y into(@NonNull Y target, @Nullable RequestListener<TranscodeType> targetListener, BaseRequestOptions<?> options) {
    Util.assertMainThread();
    Preconditions.checkNotNull(target);
    if (!isModelSet) {
      throw new IllegalArgumentException("You must call #load() before calling #into()");
    }

    Request request = buildRequest(target, targetListener, options);

    Request previous = target.getRequest();
    //  如果请求相同, 而且当前请求设置可以使用内存缓存, 则请求回收  
    if (request.isEquivalentTo(previous)
        && !isSkipMemoryCacheWithCompletePreviousRequest(options, previous)) {
      request.recycle();
      // If the request is completed, beginning again will ensure the result is re-delivered,
      // triggering RequestListeners and Targets. If the request is failed, beginning again will
      // restart the request, giving it another chance to complete. If the request is already
      // running, we can let it continue running without interruption.
      //  如果当前请求不在执行, 则会重新开始请求
      if (!Preconditions.checkNotNull(previous).isRunning()) {
        // Use the previous request rather than the new one to allow for optimizations like skipping
        // setting placeholders, tracking and un-tracking Targets, and obtaining View dimensions
        // that are done in the individual Request.
        previous.begin();
      }
      return target;
    }

    requestManager.clear(target);
    target.setRequest(request);
    // 开始请求,并追踪
    requestManager.track(target, request);

    return target;
  }

```
RequestManager#track  
RequestTracker#runRequest   
#### SingleRequest#begin  
```
@Override
public void begin() {
    assertNotCallingCallbacks();
    stateVerifier.throwIfRecycled();
    startTime = LogTime.getLogTime();
    if (model == null) {
      if (Util.isValidDimensions(overrideWidth, overrideHeight)) {
        width = overrideWidth;
        height = overrideHeight;
      }
      // Only log at more verbose log levels if the user has set a fallback drawable, because
      // fallback Drawables indicate the user expects null models occasionally.
      int logLevel = getFallbackDrawable() == null ? Log.WARN : Log.DEBUG;
      onLoadFailed(new GlideException("Received null model"), logLevel);
      return;
    }

    if (status == Status.RUNNING) {
      throw new IllegalArgumentException("Cannot restart a running request");
    }

    // If we're restarted after we're complete (usually via something like a notifyDataSetChanged
    // that starts an identical request into the same Target or View), we can simply use the
    // resource and size we retrieved the last time around and skip obtaining a new size, starting a
    // new load etc. This does mean that users who want to restart a load because they expect that
    // the view size has changed will need to explicitly clear the View or Target before starting
    // the new load.
    if (status == Status.COMPLETE) {
      onResourceReady(resource, DataSource.MEMORY_CACHE);
      return;
    }

    // Restarts for requests that are neither complete nor running can be treated as new requests
    // and can run again from the beginning.

    status = Status.WAITING_FOR_SIZE;
    if (Util.isValidDimensions(overrideWidth, overrideHeight)) {
      //  如果指定了 宽高, 直接调用 onSizeReady  
      onSizeReady(overrideWidth, overrideHeight);
    } else {
    //  如果没有指定 宽高, 则先动态计算, 再调用 onSizeReady
      target.getSize(this);
    }

    if ((status == Status.RUNNING || status == Status.WAITING_FOR_SIZE)
        && canNotifyStatusChanged()) {
      //  在图片请求开始前，会先使用Loading占位图代替最终的图片显示  
      target.onLoadStarted(getPlaceholderDrawable());
    }
    if (IS_VERBOSE_LOGGABLE) {
      logV("finished run method in " + LogTime.getElapsedMillis(startTime));
    }
}
```
SingleRequest#onSizeReady
或者  
```
ViewTarget#getSize  
ViewTarget.SizeDeterminer#getSize  
SingleRequest#onSizeReady  
Engine#load  
```  
Engine#load   
EngineJob#start  
```
public void start(DecodeJob<R> decodeJob) {
this.decodeJob = decodeJob;
    GlideExecutor executor = decodeJob.willDecodeFromCache()
        ? diskCacheExecutor
        : getActiveSourceExecutor();
    executor.execute(decodeJob);
}
```
DecodeJob#run  
#### DecodeJob#runWrapped  
1.. 初始状态 runReason = INITIALIZE, 调用 getNextStage; 
2.. stage 变成 RESOURCE_CACHE;  currentGenerator 变成 ResourceCacheGenerator, 执行 runGenerators();  
```
private void runGenerators() {
    currentThread = Thread.currentThread();
    startFetchTime = LogTime.getLogTime();
    boolean isStarted = false;
    // 执行 ResourceCacheGenerator#startNext  
    //  通过 DiskLruCacahe 
    //  cacheFile = helper.getDiskCache().get(currentKey);
    while (!isCancelled && currentGenerator != null
        && !(isStarted = currentGenerator.startNext())) {
        
      //  如果没有 取到;
      //  stage 变成 DATA_CACHE; currentGenerator 变成 DataCacheGenerator; 
      //  再一次进入 这个 while循环体;    参照 下面的 循环体B 
      stage = getNextStage(stage);
      currentGenerator = getNextGenerator();

      if (stage == Stage.SOURCE) {
        reschedule();
        return;
      }
    }
    // We've run out of stages and generators, give up.
    if ((stage == Stage.FINISHED || isCancelled) && !isStarted) {
      notifyFailed();
    }

    // Otherwise a generator started a new load and we expect to be called back in
    // onDataFetcherReady.
}
```
循环体B  
```
private void runGenerators() {
    currentThread = Thread.currentThread();
    startFetchTime = LogTime.getLogTime();
    boolean isStarted = false;
    // 执行 DataCacheGenerator#startNext  
    //  通过 DiskLruCacahe 
    //  cacheFile = helper.getDiskCache().get(currentKey);
    while (!isCancelled && currentGenerator != null
        && !(isStarted = currentGenerator.startNext())) {
        
      //  如果没有 取到;
      //  stage 变成 SOURCE; currentGenerator 变成 SourceGenerator; 
      //  再一次进入 这个 while循环体 C;  
      stage = getNextStage(stage);
      currentGenerator = getNextGenerator();

      if (stage == Stage.SOURCE) {
        reschedule();
        return;
      }
    }
    // We've run out of stages and generators, give up.
    if ((stage == Stage.FINISHED || isCancelled) && !isStarted) {
      notifyFailed();
    }

    // Otherwise a generator started a new load and we expect to be called back in
    // onDataFetcherReady.
}
```

循环体C  
```
private void runGenerators() {
    currentThread = Thread.currentThread();
    startFetchTime = LogTime.getLogTime();
    boolean isStarted = false;
    // 执行 SourceGenerator#startNext  
    //  在start 方法中,开始网络请求;  
    //  默认使用 HttpUrlFetcher#loadData 加载网络数据;    
    while (!isCancelled && currentGenerator != null
        && !(isStarted = currentGenerator.startNext())) {
      stage = getNextStage(stage);
      currentGenerator = getNextGenerator();

      if (stage == Stage.SOURCE) {
        reschedule();
        return;
      }
    }
    // We've run out of stages and generators, give up.
    if ((stage == Stage.FINISHED || isCancelled) && !isStarted) {
      notifyFailed();
    }

    // Otherwise a generator started a new load and we expect to be called back in
    // onDataFetcherReady.
}
```
3.. 网络数据加载成功  
在 HttpUrlFetcher#loadData 方法体内, 
```

//  HttpUrlFetcher#loadData
@Override
public void loadData(@NonNull Priority priority,
      @NonNull DataCallback<? super InputStream> callback) {
    long startTime = LogTime.getLogTime();
    try {
      InputStream result = loadDataWithRedirects(glideUrl.toURL(), 0, null, glideUrl.getHeaders());
      callback.onDataReady(result);
    } catch (IOException e) {
      if (Log.isLoggable(TAG, Log.DEBUG)) {
        Log.d(TAG, "Failed to load data for url", e);
      }
      callback.onLoadFailed(e);
    } finally {
      if (Log.isLoggable(TAG, Log.VERBOSE)) {
        Log.v(TAG, "Finished http url fetcher fetch in " + LogTime.getElapsedMillis(startTime));
      }
    }
}

//  SourceGenerator#onDataReady
@Override
public void onDataReady(Object data) {
    DiskCacheStrategy diskCacheStrategy = helper.getDiskCacheStrategy();
    if (data != null && diskCacheStrategy.isDataCacheable(loadData.fetcher.getDataSource())) {
      dataToCache = data;
      // We might be being called back on someone else's thread. Before doing anything, we should
      // reschedule to get back onto Glide's thread.
      //  DecodeJob#reschedule  
      cb.reschedule();
    } else {
      cb.onDataFetcherReady(loadData.sourceKey, data, loadData.fetcher,
          loadData.fetcher.getDataSource(), originalKey);
    }
}
``` 
4.. DecodeJob#reschedule  
```
@Override
public void reschedule() {
    //  改变 runReason 状态;  
    runReason = RunReason.SWITCH_TO_SOURCE_SERVICE;
    //  执行编解码
    callback.reschedule(this);
}

  @Override
public void reschedule(DecodeJob<?> job) {
    // Even if the job is cancelled here, it still needs to be scheduled so that it can clean itself
    // up.
    getActiveSourceExecutor().execute(job);
}
```    
DecodeJob#runGenerators  
```
private void runGenerators() {
    currentThread = Thread.currentThread();
    startFetchTime = LogTime.getLogTime();
    boolean isStarted = false;
    //  currentGenerator.startNext()  
    //  从当前策略对应的Generator获取数据，数据获取成功则回调DecodeJob的onDataFetcherReady对资源进行处理;  
    //  否则尝试从下一个策略的Generator获取数据
    while (!isCancelled && currentGenerator != null
        && !(isStarted = currentGenerator.startNext())) {  //  这里是触发的入口
      stage = getNextStage(stage);
      currentGenerator = getNextGenerator();
    
      if (stage == Stage.SOURCE) {
        reschedule();
        return;
      }
    }
    // We've run out of stages and generators, give up.
    if ((stage == Stage.FINISHED || isCancelled) && !isStarted) {
      notifyFailed();
    }
    
    // Otherwise a generator started a new load and we expect to be called back in
    // onDataFetcherReady.
}
```
SourceGenerator#startNext  
MultiModelLoader.MultiFetcher#loadData  
HttpUrlFetcher#loadData  
MultiModelLoader.MultiFetcher#onDataReady  
SourceGenerator#onDataReady  

### DecodeJob#runWrapped  
```
private void runWrapped() {
    switch (runReason) {
      case INITIALIZE:  //  第一次调度任务
        stage = getNextStage(Stage.INITIALIZE);
        currentGenerator = getNextGenerator();
        runGenerators();
        break;
      case SWITCH_TO_SOURCE_SERVICE:
        //  本地缓存策略失败，尝试重新获取数据，两种情况；
        //  当stage为Stage.SOURCE，或者获取数据失败并且执行和回调发生在了不同的线程
        runGenerators();
        break;
      case DECODE_DATA:
        //  获取数据成功，但执行和回调不在同一线程，希望回到自己的线程去处理数据
        decodeFromRetrievedData();
        break;
      default:
        throw new IllegalStateException("Unrecognized run reason: " + runReason);
    }
}
```

### 加载器  
DecodeHelper#getModelLoaders, 被两处调用;  
DataCacheGenerator#startNext  
```  
public boolean startNext() {
   modelLoaders = helper.getModelLoaders(cacheFile);
   modelLoaderIndex = 0;
}

```
ResourceCacheGenerator#startNext  
```
public boolean startNext() {
    if (loadData != null && helper.hasLoadPath(loadData.fetcher.getDataClass())) {
        started = true;
        loadData.fetcher.loadData(helper.getPriority(), this);
    }
}      
```
### 触发 placeholder  
placeholder 就是展位图, preview;   
RequestBuilder#into  
RequestManager#track  
RequestTracker#runRequest   
SingleRequest#begin  
ImageViewTarget#onLoadStarted  
```
@Override
public void onLoadStarted(@Nullable Drawable placeholder) {
    super.onLoadStarted(placeholder);
    setResourceInternal(null);
    setDrawable(placeholder);
}
```
### Engine#load  

```
/**
1.. 判断是否在主线程运行，说明到目前为止还是在主线程执行的，并没有真正的开启子线程。
2.. 通过keyFactory工厂来构建一个EngineKey对象，key关联着model，也就是url，它很根据model，view的宽高等等属性来构建一个EngineKey对象，   
    这个对象可以用来指定缓存地址，可以用来从缓存中查找资源等;  
3.. 根据创建的key对象分别调用loadFromCache和loadFromActiveResources方法来从内存中查找是否有缓存资源，如果有，则回调cb.onResourceReady来直接设置图片了;  
4.. 分别使用engineJobFactory和decodeJobFactory构建EngineJob和DecodeJob对象，这两个对象是真正的加载资源的两个重要类,  
    EngineJob对象负责开启线程去加载资源，并且加载得资源后转换到主线程并进行回调;  
    DecodeJob是真正的执行者，它就是去网络加载资源的地方，EngineJob开启线程，真正执行的是DecodeJob，DecodeJob之后完毕之后, EngineJob去分发回调;  
5.. EngineJob和DecodeJob的构建是基本一致的，我们看看比较复杂的DecodeJob的构建, 在build方法中，首先通过pool来创建一个DecodeJob对象,  
    然后调用DecodeJob对象的init方法进行初始化，在初始化中值得注意的是调用了decodeHelper对象的init方法.  
    decodeHelper方法是DecodeJob的重要辅助类，后面我们会详细的接触它。
6.. 上面也提到回调，这里先cb添加到engineJob.addCallback();中，然后调用EngineJob的start方法来开启线程。
 **/
public <R> LoadStatus load(GlideContext glideContext,Object model,Key signature,int width,int height,
      Class<?> resourceClass,Class<R> transcodeClass,Priority priority,DiskCacheStrategy diskCacheStrategy,
      Map<Class<?>, Transformation<?>> transformations,boolean isTransformationRequired,
      boolean isScaleOnlyOrNoTransform,Options options,boolean isMemoryCacheable,boolean useUnlimitedSourceExecutorPool,
      boolean useAnimationPool,boolean onlyRetrieveFromCache,ResourceCallback cb) {
    Util.assertMainThread();
    long startTime = VERBOSE_IS_LOGGABLE ? LogTime.getLogTime() : 0;

    EngineKey key = keyFactory.buildKey(model, signature, width, height, transformations,resourceClass, transcodeClass, options);

    EngineResource<?> active = loadFromActiveResources(key, isMemoryCacheable);
    if (active != null) {
      cb.onResourceReady(active, DataSource.MEMORY_CACHE);
      if (VERBOSE_IS_LOGGABLE) {
        logWithTimeAndKey("Loaded resource from active resources", startTime, key);
      }
      return null;
    }

    EngineResource<?> cached = loadFromCache(key, isMemoryCacheable);
    if (cached != null) {
      cb.onResourceReady(cached, DataSource.MEMORY_CACHE);
      if (VERBOSE_IS_LOGGABLE) {
        logWithTimeAndKey("Loaded resource from cache", startTime, key);
      }
      return null;
    }

    EngineJob<?> current = jobs.get(key, onlyRetrieveFromCache);
    if (current != null) {
      current.addCallback(cb);
      if (VERBOSE_IS_LOGGABLE) {
        logWithTimeAndKey("Added to existing load", startTime, key);
      }
      return new LoadStatus(cb, current);
    }

    //  异步加载 图片资源  
    EngineJob<R> engineJob =
        engineJobFactory.build(key,isMemoryCacheable,useUnlimitedSourceExecutorPool,useAnimationPool,onlyRetrieveFromCache);

    //  异步解码 图片资源  
    DecodeJob<R> decodeJob = decodeJobFactory.build(glideContext,model,key,signature,width,height,resourceClass,transcodeClass,
            priority,diskCacheStrategy,transformations,isTransformationRequired,isScaleOnlyOrNoTransform,onlyRetrieveFromCache,options,engineJob);

    jobs.put(key, engineJob);

    engineJob.addCallback(cb);
    engineJob.start(decodeJob);

    if (VERBOSE_IS_LOGGABLE) {
      logWithTimeAndKey("Started new load", startTime, key);
    }
    return new LoadStatus(cb, engineJob);
}
```
### 磁盘缓存  

com.bumptech.glide.load.data.HttpUrlFetcher#loadData  

DecodeJob#run  
DecodeJob#runWrapped    
DecodeJob#runGenerators  
SourceGenerator#startNext  
SourceGenerator#cacheData  
```
private void cacheData(Object dataToCache) {
    long startTime = LogTime.getLogTime();
    try {
      Encoder<Object> encoder = helper.getSourceEncoder(dataToCache);
      DataCacheWriter<Object> writer = new DataCacheWriter<>(encoder, dataToCache, helper.getOptions());
      originalKey = new DataCacheKey(loadData.sourceKey, helper.getSignature());
      helper.getDiskCache().put(originalKey, writer);memoryCache
      if (Log.isLoggable(TAG, Log.VERBOSE)) {
        Log.v(TAG, "Finished encoding source to cache"+ ", key: " + originalKey+ ", data: " + dataToCache+ ", encoder: " + encoder+ ", duration: " + LogTime.getElapsedMillis(startTime));
      }
    } finally {
      loadData.fetcher.cleanup();
    }
    sourceCacheGenerator =new DataCacheGenerator(Collections.singletonList(loadData.sourceKey), helper, this);
}
```
### 函数描述  
RequestBuilder#into  
```
/**
* 对当前的ImageView加载资源，取消该View已加载过的资源，并释放资源。
* @param view 此视图将取消先前加载并加载新资源的视图。
* @return 一个可以使Glide加载资源并通知相关生命周期事件的接口。（后面详细介绍Target）
*/
```

### 类描述  
RequestOptions  
使用request options可以实现 (包括但不限于):  
a) 占位符(Placeholders);  
b) 转换(Transformations);  
c) 缓存策略(Caching Strategies);  
d) 组件特有的设置项，例如编码质量，或Bitmap的解码配置等;  

Target  
介于请求和请求者之间的中介者的角色。Target 负责展示占位符，加载资源，并为每个请求决定合适的尺寸;  

Generator  
ResourceCacheGenerator  尝试从修改过的资源缓存中获取，如果缓存未命中，尝试从 DataCacheGenerator 中获取;  
DataCacheGenerator  尝试从未修改过的本地缓存中获取数据，如果缓存未命中则尝试从 SourceGenerator 中获取;  
SourceGenerator  从原始的资源中获取，可能是服务器，也可能是本地的一些原始资源;  

RunReason  
```
private enum RunReason {
    /** The first time we've been submitted. 
    * 第一次 提交
    */
    INITIALIZE,
    /**
     * We want to switch from the disk cache service to the source executor.
     * disk 缓存 没有找到, 现在要到 数据源处, 拉取数据, 数据源可能是 服务器;
     */
    SWITCH_TO_SOURCE_SERVICE,
    /**
     * We retrieved some data on a thread we don't own and want to switch back to our thread to
     * process the data.
     * 我们重新拉取到一些 数据, 
     */
    DECODE_DATA, 
}
```
Engine  负责任务创建，发起，回调，资源的管理;  

ModelLoader  各种资源的ModelLoader;  

ThumbnailRequestCoordinator  请求协调器，包含两个请求：缩略图请求＋完整图片请求;    
transCoder : 资源转换器，比如 BitmapBytesTransCoder(Bitmap转换为Bytes), GifDrawableBytesTransCoder;  
ResourceEncoder : 持久化数据的接口，注意，该类并不与decoder相对应，而是用于本地缓存的接口;  
ResourceDecoder : 数据解码器,比如ByteBufferGifDecoder (将ByteBuffer转换为Gif), StreamBitmapDecoder (Stream转换为Bitmap);  
ResourceTranscoder : 资源转换器，将给定的资源类型，转换为另一种资源类型，比如将Bitmap转换为Drawable，Bitmap转换为Bytes;  
Transformation : 比如对图片进行FitCenter，CircleCrop，CenterCrop的transformation，或者根据给定宽高对Bitmap进行处理的BitmapDrawableTransformation;  

EngineJob  开启线程, 异步加载图片;  
DecodeJob  对图片解码; 
DataSource  
```
public enum DataSource {
    //数据从本地硬盘获取，也有可能通过一个已经从远程获取到数据的Content Provider
    LOCAL,
    //数据从远程获取
    REMOTE,
    //数据来自未修改过的硬盘缓存
    DATA_DISK_CACHE,
    //数据来自已经修改过的硬盘缓存
    RESOURCE_DISK_CACHE,
    //数据来自内存
    MEMORY_CACHE,
}
```
Registry  
ModelLoaderRegistry  注册所有数据加载的loader  
ResourceDecoderRegistry  注册所有资源转换的decoder  
TransCoderRegistry  注册所有对decoder之后进行特殊处理的transCoder  
ResourceEncoderRegistry  注册所有持久化resource（处理过的资源）数据的encoder  
EncoderRegistry   注册所有的持久化原始数据的encoder  

ResourceDecoder  
ByteBufferBitmapDecoder  不管是加载网络图片还是加载本地资源，都是通过 ByteBufferBitmapDecoder 类进行解码  


