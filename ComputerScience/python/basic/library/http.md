### 网络请求
```
import http.client
import json
import urllib.parse

queryMap = {'uid': 55, 'page': 1}
requestBody = urllib.parse.urlencode(queryMap)
baseUrl = "http://m.mp.oeeee.com/uncache.php?m=Doc&a=spaceInfo"
connection = http.client.HTTPConnection('m.mp.oeeee.com')
header = {"Content-type": "application/x-www-form-urlencoded", "Accept": "text/plain"}
connection.request(method="POST", url=baseUrl, headers=header, body=requestBody)
response = connection.getresponse()
res = response.read()
resp = json.loads(res)
print(resp)

```