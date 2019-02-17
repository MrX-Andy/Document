### CacheInterceptor  
okHttp中的缓存策略有强制缓存和对比缓存:  
强制缓存, 即缓存在有效期内就直接返回缓存, 不进行网络请求;  
对比缓存  
如果缓存超过有效期, 进行网络请求;  
如果数据未修改, 服务端返回不带body的304响应, 表示客户端缓存仍有效可用; 否则返回完整最新数据, 客户端取网络请求的最新数据;  
  
networkRequest 是否为空决定是否请求网络, cacheResponse是否为空决定是否使用缓存  
1.. networkRequest, cacheResponse 均为空, 构建返回一个状态码为504的response;  
2.. networkRequest 为空, cacheResponse 不为空, 执行强制缓存;  
3.. networkRequest, cacheResponse 均不为空, 执行对比缓存;  
4.. networkRequest 不为空, cacheResponse 为空, 网络请求获取到最新response后, 视情况缓存response;  

### intercept  
```
@Override 
public Response intercept(Chain chain) throws IOException {
    Response cacheCandidate = cache != null
        ? cache.get(chain.request())
        : null;

    long now = System.currentTimeMillis();

    //  用 request 的 url 作为 key, 查找缓存的 response 作为候选缓存
    CacheStrategy strategy = new CacheStrategy.Factory(now, chain.request(), cacheCandidate).get();
    //  根据生成的 networkRequest 和 cacheResponse 来决定执行什么缓存策略
    Request networkRequest = strategy.networkRequest;
    Response cacheResponse = strategy.cacheResponse;

    if (cache != null) {
      cache.trackResponse(strategy);
    }

    //  候选缓存存在, 但是缓存策略生成的 cache不存在, 关闭 cacheCandidate中的 BufferedSource 的输入输出流
    if (cacheCandidate != null && cacheResponse == null) {
      closeQuietly(cacheCandidate.body());   // The cache candidate wasn't applicable. Close it.
    }

    // If we're forbidden from using the network and the cache is insufficient, fail.
    //  即无网络请求又无合法缓存, 返回状态码 504 的 response
    if (networkRequest == null && cacheResponse == null) {
      return new Response.Builder()
          .request(chain.request())
          .protocol(Protocol.HTTP_1_1)
          .code(504)
          .message("Unsatisfiable Request (only-if-cached)")
          .body(Util.EMPTY_RESPONSE)
          .sentRequestAtMillis(-1L)
          .receivedResponseAtMillis(System.currentTimeMillis())
          .build();
    }

    // If we don't need the network, we're done.
    //  强制使用缓存
    if (networkRequest == null) {
      return cacheResponse.newBuilder()
          .cacheResponse(stripBody(cacheResponse))
          .build();
    }

    //  networkResponse表示当前网络请求的最新 response, cacheResponse表示由缓存策略获取的合法缓存 response
    Response networkResponse = null;
    try {
      networkResponse = chain.proceed(networkRequest);
    } finally {
      // If we're crashing on I/O or otherwise, don't leak the cache body.
      if (networkResponse == null && cacheCandidate != null) {
        closeQuietly(cacheCandidate.body());
      }
    }

    // If we have a cache response too, then we're doing a conditional get.
    //  对比缓存策略
    if (cacheResponse != null) {
      //   服务端返回 304 状态码, 表示本地缓存仍有效
      if (networkResponse.code() == HTTP_NOT_MODIFIED) {
        Response response = cacheResponse.newBuilder()
            .headers(combine(cacheResponse.headers(), networkResponse.headers()))
            .sentRequestAtMillis(networkResponse.sentRequestAtMillis())
            .receivedResponseAtMillis(networkResponse.receivedResponseAtMillis())
            .cacheResponse(stripBody(cacheResponse))
            .networkResponse(stripBody(networkResponse))
            .build();
        networkResponse.body().close();

        // Update the cache after combining headers but before stripping the
        // Content-Encoding header (as performed by initContentStream()).
        //  更新本地缓存信息
        cache.trackConditionalCacheHit();
        cache.update(cacheResponse, response);
        return response;
      } else {
        closeQuietly(cacheResponse.body());
      }
    }

    Response response = networkResponse.newBuilder()
        .cacheResponse(stripBody(cacheResponse))
        .networkResponse(stripBody(networkResponse))
        .build();

    if (cache != null) {
      if (HttpHeaders.hasBody(response) && CacheStrategy.isCacheable(response, networkRequest)) {
        // Offer this request to the cache.
        //  保存拥有 body 且符合缓存条件的响应
        CacheRequest cacheRequest = cache.put(response);
        //  通过该方法返回新建的 repsonse, 保存缓存 response 的 body 能够正确的写入和关闭流
        return cacheWritingResponse(cacheRequest, response);
      }

      //  判断是否时无效缓存, 若请求方式是 POST, PATCH, PUT, DELETE, MOVE, 则移除缓存
      if (HttpMethod.invalidatesCache(networkRequest.method())) {
        try {
          cache.remove(networkRequest);
        } catch (IOException ignored) {
          // The cache cannot be written.
        }
      }
    }

    return response;
}
```

### CacheStrategy.Factory#Factory  
```
public Factory(long nowMillis, Request request, Response cacheResponse) {
  this.nowMillis = nowMillis;
  this.request = request;
  this.cacheResponse = cacheResponse;

  if (cacheResponse != null) {
    this.sentRequestMillis = cacheResponse.sentRequestAtMillis();
    this.receivedResponseMillis = cacheResponse.receivedResponseAtMillis();
    Headers headers = cacheResponse.headers();
    for (int i = 0, size = headers.size(); i < size; i++) {
      String fieldName = headers.name(i);
      String value = headers.value(i);
      if ("Date".equalsIgnoreCase(fieldName)) {
        servedDate = HttpDate.parse(value);
        servedDateString = value;
      } else if ("Expires".equalsIgnoreCase(fieldName)) {
        expires = HttpDate.parse(value);
      } else if ("Last-Modified".equalsIgnoreCase(fieldName)) {
        lastModified = HttpDate.parse(value);
        lastModifiedString = value;
      } else if ("ETag".equalsIgnoreCase(fieldName)) {
        etag = value;
      } else if ("Age".equalsIgnoreCase(fieldName)) {
        ageSeconds = HttpHeaders.parseSeconds(value, -1);
      }
    }
  }
}
```

### CacheStrategy.Factory#getCandidate  
```
private CacheStrategy getCandidate() {
  // No cached response.
  if (cacheResponse == null) {
    return new CacheStrategy(request, null);
  }

  // Drop the cached response if it's missing a required handshake.
  if (request.isHttps() && cacheResponse.handshake() == null) {
    return new CacheStrategy(request, null);
  }

  // If this response shouldn't have been stored, it should never be used
  // as a response source. This check should be redundant as long as the
  // persistence store is well-behaved and the rules are constant.
  //  isCacheable方法通过 response 的状态码以及 response, request 的缓存控制字段来判断是否可缓存
  if (!isCacheable(cacheResponse, request)) {
    return new CacheStrategy(request, null);
  }

  //  判断 request 中的缓存控制字段和 header 是否设置 If-Modified-Since 或 If-None-Match
  CacheControl requestCaching = request.cacheControl();
  if (requestCaching.noCache() || hasConditions(request)) {
    return new CacheStrategy(request, null);
  }

  CacheControl responseCaching = cacheResponse.cacheControl();

  long ageMillis = cacheResponseAge();
  long freshMillis = computeFreshnessLifetime();

  if (requestCaching.maxAgeSeconds() != -1) {
    freshMillis = Math.min(freshMillis, SECONDS.toMillis(requestCaching.maxAgeSeconds()));
  }

  long minFreshMillis = 0;
  if (requestCaching.minFreshSeconds() != -1) {
    minFreshMillis = SECONDS.toMillis(requestCaching.minFreshSeconds());
  }

  long maxStaleMillis = 0;
  if (!responseCaching.mustRevalidate() && requestCaching.maxStaleSeconds() != -1) {
    maxStaleMillis = SECONDS.toMillis(requestCaching.maxStaleSeconds());
  }

  //  判断缓存是否在有效期内
  if (!responseCaching.noCache() && ageMillis + minFreshMillis < freshMillis + maxStaleMillis) {
    Response.Builder builder = cacheResponse.newBuilder();
    if (ageMillis + minFreshMillis >= freshMillis) {
      builder.addHeader("Warning", "110 HttpURLConnection \"Response is stale\"");
    }
    long oneDayMillis = 24 * 60 * 60 * 1000L;
    if (ageMillis > oneDayMillis && isFreshnessLifetimeHeuristic()) {
      builder.addHeader("Warning", "113 HttpURLConnection \"Heuristic expiration\"");
    }
    return new CacheStrategy(null, builder.build());
  }

  // Find a condition to add to the request. If the condition is satisfied, the response body
  // will not be transmitted.
  //  缓存超出有效期, 判断是否设置了 Etag 和 Last-Modified
  String conditionName;
  String conditionValue;
  if (etag != null) {
    conditionName = "If-None-Match";
    conditionValue = etag;
  } else if (lastModified != null) {
    conditionName = "If-Modified-Since";
    conditionValue = lastModifiedString;
  } else if (servedDate != null) {
    conditionName = "If-Modified-Since";
    conditionValue = servedDateString;
  } else {
    return new CacheStrategy(request, null); // No condition! Make a regular request.
  }

  //  添加请求头, Etag 对应 If-None-Match, Last-Modified 对应 If-Modified-Since
  Headers.Builder conditionalRequestHeaders = request.headers().newBuilder();
  Internal.instance.addLenient(conditionalRequestHeaders, conditionName, conditionValue);

  Request conditionalRequest = request.newBuilder()
      .headers(conditionalRequestHeaders.build())
      .build();
  return new CacheStrategy(conditionalRequest, cacheResponse);
}

```