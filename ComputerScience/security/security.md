主要分为对称加密和非对称加密; 
 
密钥, 是改变密码行为的数字化参数; 
对称密钥加密, 编 / 解码使用相同密钥的算法;  
非对称密钥加密, 编 / 解码使用不同密钥的算法;  
公开密钥加密系统, 一种能够使数百万计算机, 便捷地发送机密报文的系统;  
数字签名, 用来验证报文未被伪造或篡改的校验和;  
数字证书, 由一个可信的组织验证和签发的识别信息;  

### SymmetricCipher 对称加密  
对称加密, 又称对称密钥加密, 私钥加密;  

对称加密的, 信息发送方和接收方, 使用同一个密钥去加密和解密数据;  
对称加密的特点是, 算法公开, 加密和解密速度快, 适合于对大数据量进行加密;  
其加密过程如下: 明文 + 加密算法 + 私钥 => 密文;  
解密过程如下: 密文 + 解密算法 + 私钥 => 明文;  

对称加密中用到的密钥叫做私钥, 私钥表示个人私有的密钥, 即该密钥不能被泄露;  
否则密文就很容易被破解, 所以对称加密的缺点是, 密钥安全管理困难;  

对称密钥常用算法
AES, DES, Triple-DES(3DES), RC2, RC4, RC5, TDEA, Blowfish, IDEA, SKIPJACK 等;  
DES 加密原理, 主要是对比特位进行操作, 交换位置, 异或等等;  

对称加密应用场景  
本地数据加密, 例如加密android 里 SharedPreferences 里面的某些敏感数据;  
网络传输, 登录接口 post 请求参数加密 {username=lisi,pwd=oJYa4i9VASRoxVLh75wPCg==};  
加密用户登录结果信息, 并序列化到本地磁盘, 将 user 对象序列化到本地磁盘, 下次登录时反序列化到内存里;  

### 散列函数  
散列函数, 是将任意长度的输入转化为, 定长输出的算法, 散列函数的结果经常被简称为散列;  
编程中普遍使用散列函数, 但并非所有散列函数都适用于密码学, 密码学散列函数有以下几个额外特性:  
1.. 抗原像性(单向性)  
给定一个散列, 计算上无法找到或者构造出生成它的数据源;  
2.. 抗第二原像性(弱抗碰撞性)  
给定一条消息和它的散列, 计算上无法找到不同的消息具有相同的散列;  
3.. 强抗碰撞性  
计算上无法找到两条散列相同的消息  

散列函数最常用的使用场合是, 以紧凑的方式表示, 并比较大量数据;  
比如, 为了避免直接比较两个文件 (可能很难, 比方说, 它们存放于世界上不同的位置),  你可以比较它们的散列;  
散列函数经常被称为指纹, 消息摘要, 或者简单称为摘要;    
现在使用最为广泛的散列函数是 SHA1, 它的输出是160位, 由于 SHA1 已经变弱, 所以建议升级为 SHA256;  
与密码不同, 散列函数的强度并不与散列长度对等;  


### 非对称加密   
非对称加密, 又称公钥加密;  
非对称加密与对称加密相比, 其安全性更好;  而非对称加密使用一对密钥, 即公钥和私钥, 且二者成对出现;  
私钥被自己保存, 不能对外泄露;  公钥指的是公共的密钥, 任何人都可以获得该密钥;  
用公钥或私钥中的任何一个进行加密, 用另一个进行解密;  
被公钥加密过的密文只能被私钥解密, 过程如下: 
明文 + 加密算法 + 公钥 => 密文,  密文 + 解密算法 + 私钥 => 明文;  
被私钥加密过的密文只能被公钥解密, 过程如下: 
明文 + 加密算法 + 私钥 => 密文,  密文 + 解密算法 + 公钥 => 明文;  
非对称加密的缺点是加密和解密花费时间长, 速度慢, 只适合对少量数据进行加密;  

在非对称加密中使用的主要算法有：RSA, Elgamal, Rabin, D-H, ECC(椭圆曲线加密算法)等;  

RSA 的原理, 两个大质数(p,q)乘积(n)难以逆向求解, 所以 pq 是对等的, 公钥和私钥也是对等的;  

❀ 使用方称为甲方, 提供方称为乙方;  
乙方通过相关技术, 生成两种密钥, 公钥和私钥, 乙方保管私钥, 向外公布公钥(任何人都可以使用);  
甲方得到乙方提供的公钥, 对明文进行加密操作;  
乙方得到加密的密文, 用私钥进行解密操作;  

❀ 互质关系  
如果两个正整数, 除了1以外, 没有其他公因子, 我们就称这两个数是互质关系;  
比如, 15和32没有公因子, 所以它们是互质关系, 这说明, 不是质数也可以构成互质关系;  

关于互质关系, 不难得到以下结论:  
两个不同的质数一定是互质数, 例如, 2与7, 13与19;  
相邻的两个自然数是互质数, 如 15与 16;  
相邻的两个奇数是互质数, 如 49与 51;  
较大数是质数的两个数是互质数, 如97与88;  
小数是质数, 大数不是小数的倍数的两个数是互质数; 例如 7和 16;  
2和任何奇数是互质数, 例如2和87;  
1和任意一个自然数是都是互质关系, 比如1和99;   
p 是大于1的奇数, 则 p 和 p-2 构成互质关系, 比如17和15;  

❀ 同余  
两个整数 a, b,  a mod n = b mod n, 则称 a, b 对于模 n 同余, 记作: a ≡ b (mod n);  
❀ 费马小定理  
如果p是质数, 且gcd(a, p) = 1, 那么 a ^ (p-1) mod p = 1 mod p, 记作  a ≡ 1 (mod p); 


[欧拉函数](library/euler_function.md)  
❀ 欧拉定理  
定理:   
(1)若n是素数p的k次幂, φ(n)=p^k-p^(k-1)=(p-1)p^(k-1), 因为除了p的倍数外, 其他数都跟n互质 ;    
(2)欧拉函数是积性函数——若m, n互质, φ(m*n)=φ(m)φ(n);    
特殊性质:   
1)当n为奇数时, φ(2n)=φ(n);    
2)p是素数, φ(p) = p - 1, φ(p)称为p的欧拉值;  

如果两个正整数a和n互质, 则n的欧拉函数 φ(n) 可以让下面的等式成立:   
![euler_100](ImageFiles/euler_100.png)    
也就是说, a的φ(n)次方被n除的余数为1;或者说, a的φ(n)次方减去1, 可以被n整除;  

欧拉定理有一个特殊情况:  
假设正整数a与质数p互质, 因为质数p的φ(p)等于p-1, 则欧拉定理可以写成  
![euler_103](ImageFiles/euler_103.png)     


❀ 比较一下  
对称加密:   
同一个密钥, 速度快, 效率高;  
在传输过程中, 密钥会联同报文一起;   

非对称加密:   
公钥, 私钥, 密钥对形式, 加密速度慢, 解密速度更慢;  
主要用在数字签名, 对密钥签名上;  


### 选定加解密方式  
```
常见的加密方式有两大类分为对称加密和非对称加密两种，其中对称加密采用的是AES的加密方式，非对称的加密采用的是RSA，
考虑到非对称加密在加密大量数据时会比较影响性能，
所以具体的方案是使用AES对数据源进行加密，之后使用RSA将AES加密使用的密钥进行加密，

HTTPS在传输的过程中会涉及到三个密钥:  
服务器端的公钥和私钥, 用来进行非对称加密;  
客户端生成的随机密钥, 用来进行对称加密;  
服务器端有一个密钥对, 即公钥和私钥, 是用来进行非对称加密使用的, 服务器端保存着私钥, 不能将其泄露, 公钥可以发送给任何人;  

第一次HTTP请求:  
1. 客户端向服务器发起HTTPS请求，请求得到RSA公钥。
2. 服务器将自己的RSA公钥返回给客户端。
3. 客户端收到服务器端的公钥之后，会对公钥进行检查，验证其合法性，严格的说，这里应该是验证服务器发送的数字证书的合法性，关于客户端如何验证数字证书的合法性，
如果公钥合格，那么客户端会生成一个随机值，这个随机值就是用于进行对称加密的AES密钥，
然后用服务器的RSA公钥对客户端AES密钥进行非对称加密，得到RSAED_AES密钥
之后的HTTP请求：
4. 客户端所有的HTTPS请求，都会携带客户端自己生成的RESED_AES密钥 这个参数，并使用AES密钥对数据加密，并将报文发送给服务端；
5. 服务器接收到客户端发来的报文之后，会用自己的RSA私钥对 客户端发来的RSAED_AES密钥进行非对称解密，得到客户端的AES密钥，
然后用客户端AES密钥对 客户端加密的数据进行对称解密；然后服务器 使用客户端的AES私钥，对数据加密，将加密后的密文返回给客户端。
6. 客户端收到服务器返回的密文，用AES密钥对其进行对称解密，得到原文。
```
Android端  
```
服务器端(server)分别生成自己的RSA密钥对,并提供接口给Android客户端获取RSA公钥(rsaPublicKey)
client生成AES密钥(aesKey)
client使用自己的AES密钥(aesKey)对转换为json格式的请求明文数据(data)进行加密，得到加密后的请求数据encryptData
client提供server提供的接口获取RSA公钥(rsaPublicKey)
client使用获取RSA公钥(rsaPublicKey)对AES密钥(aesKey)进行加密，得到encryptAesKey
client将encryptAesKey作为http请求头参数，将加密后的请求数据encryptData作为请求体一起传输给服务器端 

```

服务器端  
```
server 响应client的http请求，读取http请求头。获得client传过来的加密后的AES密钥(encryptAesKey)，
读取http请求体，获得client传过来的加密后的请求数据(encryptData)。
server使用自己的RSA私钥(rsaPrivateKey)对加密后的AES密钥(encryptAesKey)进行RSA解密，得到AES密钥(aesKey)
使用解密后的AES密钥(aesKey)对加密后的请求数据(encryptData),进行AES解密操作，得到解密后的请求数据(data)，该数据为json格式
对解密后的请求数据(data)进行json解析，然后做相关的响应操作。

```


### 一个小故事  
```
鲍勃有两种钥匙，一种是公钥，另一种是私钥。
鲍勃把公钥送给他的朋友们----帕蒂、道格、苏珊----每人一把。

苏珊要给鲍勃写一封保密的信，她写完后用鲍勃的公钥加密，就可以达到保密的效果。
鲍勃收信后，用私钥解密，就看到了信件内容。这里要强调的是，只要鲍勃的私钥不泄露，这封信就是安全的，即使落在别人手里，也无法解密。
这就是：公钥加密，私钥解密；

鲍勃给苏珊回信，决定采用"数字签名"。他写完后先用Hash函数，生成信件的摘要（digest）。
然后，鲍勃使用私钥，对这个摘要加密，生成"数字签名"（signature）。
鲍勃将这个签名，附在信件下面，一起发给苏珊。
苏珊收信后，取下数字签名，用鲍勃的公钥解密，得到信件的摘要。
苏珊再对信件本身使用Hash函数，将得到的结果，与上一步得到的摘要进行对比。如果两者一致，就证明这封信未被修改过。

复杂的情况出现了。道格想欺骗苏珊，他偷偷使用了苏珊的电脑，用自己的公钥换走了鲍勃的公钥。此时，苏珊实际拥有的是道格的公钥，但是还以为这是鲍勃的公钥。
因此，道格就可以冒充鲍勃，用自己的私钥做成"数字签名"，写信给苏珊，让苏珊用假的鲍勃公钥进行解密。
后来，苏珊感觉不对劲，发现自己无法确定公钥是否真的属于鲍勃。她想到了一个办法，要求鲍勃去找"证书中心"（certificate authority，简称CA），为公钥做认证。
证书中心用自己的私钥，对鲍勃的公钥和一些相关信息一起加密，生成"数字证书"（Digital Certificate）。
鲍勃拿到数字证书以后，就可以放心了。以后再给苏珊写信，只要在签名的同时，再附上数字证书就行了。
苏珊收信后，用CA的公钥解开数字证书，就可以拿到鲍勃真实的公钥了，然后就能证明"数字签名"是否真的是鲍勃签的。

使用hash函数生成摘要 digest；
使用私钥对摘要加密，生成数字签名 signature；

使用公钥对签名解密，得到摘要；
使用hash函数对信件操作，得到摘要，与上一步的摘要比对，是否一致；
```

数字签名  
```
发送报文时，发送方用一个哈希函数从报文文本中生成报文摘要,然后用自己的私人密钥对这个摘要进行加密，
这个加密后的摘要将作为报文的数字签名和报文一起发送给接收方，接收方首先用与发送方一样的哈希函数从接收到的原始报文中计算出报文摘要，
接着再用发送方的公用密钥来对报文附加的数字签名进行解密，如果这两个摘要相同、那么接收方就能确认该数字签名是发送方的。
数字签名有两种功效：一是能确定消息确实是由发送方签名并发出来的，因为别人假冒不了发送方的签名。二是数字签名能确定消息的完整性。
因为数字签名的特点是它代表了文件的特征，文件如果发生改变，数字摘要的值也将发生变化。不同的文件将得到不同的数字摘要。
一次数字签名涉及到一个哈希函数、发送者的公钥、发送者的私钥。
```

数字证书  
```
数字证书就是互联网通讯中标志通讯各方身份信息的一串数字，提供了一种在Internet 上验证通信实体身份的方式，数字证书不是数字身份证，
而是身份认证机构盖在数字身份证上的一个章或印（或者说加在数字身份证上的一个签名）。它是由权威机构——CA 机构，
又称为证书授权（Certificate Authority）中心发行的，人们可以在网上用它来识别对方的身份。
```
数字证书格式  
```
数字证书的格式普遍采用的是X.509V3 国际标准，一个标准的X.509 数字证书包含以下一些内容：
证书的版本信息
证书的序列号，每个证书都有一个唯一的证书序列号
证书所使用的签名算法
证书的发行机构名称，命名规则一般采用X.500 格式
证书的有效期，通用的证书一般采用UTC 时间格式，它的计时范围为1950-2049
证书所有人的名称，命名规则一般采用X.500 格式
证书所有人的公开密钥
证书发行者对证书的签名

数字证书是安全领域里的终极武器，SSL 通信协议里最核心的东西就是数字证书。他涉及到前面提到的所有知识：对称加密、非对称加密、消息摘要、数字签名等。
数字证书可以通过Java 自带的KeyTool 工具生成，生成后的数字证书一般保管在KeyStore 里。KeyStore可以叫做秘钥仓库。
秘钥仓库可以保管3 种类型的数据：
KeyStore.PrivateKeyEntry（非对称机密里的私钥）、KeyStore.SecretKeyEntry （对称加密里的秘钥）、KeyStore.TrustedCertificateEntry（受信任的证书）
```
消息摘要  
```
常用算法
MD5、SHA、CRC 等

应用场景
对用户密码进行md5 加密后保存到数据库里
软件下载站使用消息摘要计算文件指纹，防止被篡改
数字签名（后面知识点）
百度云，360网盘等云盘的妙传功能用的就是sha1值
Eclipse和Android Studio开发工具根据sha1值来判断v4，v7包是否冲突
据说银行的密码使用的就是MD5加密（因为MD5具有不可逆性）
病毒查杀，把每个病毒文件或apk进行MD5后得到一个特征码，拿着特征码去跟病毒数据库对比，特征码一致说明该文件是病毒
Git版本控制也使用到了sha1
```

SSL通信介绍  
```
客户端：→ 发送 请求， https:someserver/index.html
服务端：→ 发送 X.509证书（包含服务器的公用密钥）

客户端：用已知的CA列表，来认证证书，if(是浏览器) 让用户选择自承担风险的证书
客户端：→ 生成随机对称密钥，并用服务端的公钥加密，并发送密文

客户端 和 服务器都知道了对称密钥，并用它加密会话期间的最终用户数据
```

### OpenSSL命令  
```
参数详解
-passin arg
免输入密码 参数
指定私钥包含口令存放方式。比如用户将私钥的保护口令写入一个文件，采用此选项指定此文件，可以免去用户输入口令的操作。
比如用户将口令写入文件“pwd.txt”，输入的参数为：-passin file:pwd.txt。

配置openSSL环境变量
C:
CD  C:\OpenSSL-Win64\bin
set  OPENSSL_CONF=C:\OpenSSL-Win64\bin\openssl.cfg
查看私钥
D:
cd D:\WorkSpace\IntelliJ\AppServer2
C:\OpenSSL-Win64\bin\openssl rsa -in hack_server_private_rsa.pem -passin file:D:\WorkSpace\IntelliJ\AppServer2\config_pwd.java

查看公钥
openssl rsa -in rsa.key -pubout

转pkcs8编码
openssl pkcs8 -topk8 -inform PEM -in rsa_private_key.pem -outform PEM -nocrypt -out rsa_private_key_pkcs8.pem
```
OpenSSL证书制作  
https://www.openssl.org/  
http://slproweb.com/products/Win32OpenSSL.html  
http://blog.csdn.net/aking21alinjuju/article/details/7654097  
http://slproweb.com/products/Win32OpenSSL.html  

http://www.cnblogs.com/fehdd/p/6889619.html  
http://www.cnblogs.com/littleatp/p/5878763.html  

进入openSSL目录  
新建一个 test 空目录  
```
C:
CD  C:\OpenSSL-Win64\bin
set  OPENSSL_CONF=C:\OpenSSL-Win64\bin\openssl.cfg
CD  test

进入test目录  cd  test

生成RSA私钥
..\openssl genrsa -out rsa_private_key.pem 1024

生成RSA公钥
..\openssl rsa -in rsa_private_key.pem -pubout -out rsa_public_key.pem


将RSA私钥转换成PKCS8格式
..\openssl pkcs8 -topk8 -inform PEM -in rsa_private_key.pem -outform PEM -nocrypt


-----BEGIN PRIVATE KEY-----
MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAM5INjBwjEYmxulQ
rr8FjYdCAxZbJFplqFPWb7G74/3w8awnYVaQP+DQjFKd4uZ/4tBcu7Y0jvBswDM+
BDLRTEAnMPhV/a7xFwgkTLyVEUEYCzIoW0fqUkTTqdi1TE/GIOjSjYrDl3FIkDGf
Bcl8fBfHAbNMHn8Y1IRGULTcyDK3AgMBAAECgYAWDUYtu3pjbXXKL2Pe69sLYJUv
6KJkP4XVIJFkMK/Yz88mFjejp1wMzn6RvmspNPasS52NhuTY8udBExojzJgX+jtB
WkAv+hiHSujb8lEDNGHzpQjnj8awN3vsvjzzdcNqROMqWHERzynH7/s+JRavNnTj
8DDvhBS/zd9O3qk+YQJBAP07w96D12vMg1Z5SQRB00ew/090KVRRN4YK9CIRlysD
SbqE+6gquSOWDl97Q9R7N0ujAKbWk9hOCkBZ46F+ZA8CQQDQiSJvFqnog/tvCQ+f
Pr9kvGrdHxO+xhOU8RmNZOZBRKUmAy3aCg1GPlq5iryZuPR21c3dcpvG1kjRTkRo
On7ZAkEAqYM+RE+A/poyOMqLhBwEMHNdFHSvzc9oGYRvk/aueI6E5iRqyVGACUBD
csryxuOMSkddAlGVOjBTUGxbohkXJQJBAMr2zgblPhXKb2vQm93YSpwXLsNsveEO
FH0AdGhD7j9UOpDWw0fWRLk7cz3nJE1R0M3gpm+EXs2WPmVJEheH53ECQQCEP8yd
sbNdphhLBKQc6sPbXO/qt7PwMsEDPA1FTUz6bo7FGuvq9Qt+ErzygYIlHz8/85jX
BMKSEtsI8SEEsLb5
-----END PRIVATE KEY-----



生成加密的 RSA 私钥
..\openssl genrsa -out rsa_desprv_key.pem 1024 -des3 -passout pass:Alex




解密 RSA 私钥
..\openssl rsa -in rsa_desprv_key.pem -out rsa_nodesprv_key.pem -passin pass:Alex



生成 RSA 私钥和自签名证书
..\openssl req -x509 -nodes -days 365 -newkey rsa:1024 -keyout private_key.pem -out cert.crt



使用 RSA 私钥生成自签名证书
..\openssl req -new -x509 -days 3650 -key private_key.pem -out cert.crt



从私钥中导出公钥
openssl rsa -in server-key.pem -pubout -outform PEM -out server-key-pub.pem



查看 Cert 证书细节
..\openssl x509 -in cert.crt -noout -text



转换 Cert 证书格式
..\openssl x509 -in cert.cer -inform DER -outform PEM -out cert.pem


使用 RSA私钥生成 CSR 签名请求
..\openssl req -new -key private_key.pem -out cert.csr


查看CSR 的细节
..\openssl req -noout -text -in server.csr



使用 根CA 证书 对请求签发证书进行签发，生成 x509证书
..\openssl x509 -req -days 3650 -in server.csr -CA ca.crt -CAkey ca.key -CAcreateserial -out server.crt



```
### 证书文件格式(Certificate)  
```

格式					DER 
扩展名				.cer	.crt	.rsa
描述					ASN .1 DER用于存放证书 
特点					不含私钥、二进制

格式					PKCS7 
扩展名				.p7b	.p7r 
描述					PKCS #7 加密信息语法标准
特点          		1、p7b以树状展示证书链，不含私钥 2、p7r为CA对证书请求签名的回复，只能用于导入

格式          		CMS 
扩展名       		.p7c	.p7m	.p7s 
描述          	 	Cryptographic Message Syntax
特点          		 1、p7c只保存证  2、p7m：signature with enveloped data  3、p7s：时间戳签名文件

格式          		PEM 
扩展名       		.pem 
描述          		Printable Encoded Message
特点          		1、该编码格式在RFC1421中定义，其实PEM是【Privacy-Enhanced Mail】的简写，但他也同样广泛运用于密钥管理
特点          		2、ASCII文件 3、一般基于base 64编码

格式         		PKCS10 
扩展名      		.p10	.csr
描述         		PKCS #10 公钥加密标准【Certificate Signing Request】
特点         		1、证书签名请求文件 2、ASCII文件  3、CA签名后以p7r文件回复

格式         		SPC 
扩展名      		.pvk	.spc 
描述        		Software Publishing Certificate
特点         		1、微软公司特有的双证书文件格式，经常用于代码签名，pvk用于保存私钥  2、spc用于保存公钥

```

### 密钥库文件格式(Keystore)   
```
格式					JKS
扩展名				.jks		.ks
描述					Java Keystore 密钥库的Java实现版本，provider为SUN
特点					密钥库和私钥用不同的密码进行保护

格式					JCEKS
扩展名				.jce
描述					JCE Keystore 密钥库的JCE实现版本，provider为SUN JCE
特点					相对于JKS安全级别更高，保护Keystore私钥时采用TripleDES

格式					PKCS12
扩展名				.p12	.pfx
描述					PKCS #12 个人信息交换语法标准
特点					1、包含私钥、公钥及其证书 2、密钥库和私钥用相同密码进行保护

格式					BKS
扩展名				.bks
描述					Bouncycastle Keystore 密钥库的BC实现版本，provider为BC
特点					基于JCE实现

格式					UBER
扩展名				.ubr
描述					Bouncycastle UBER Keystore 密钥库的BC更安全实现版本，provider为BC

```

### 知识库
```
PEM格式证书的信息:
openssl x509 -in certificate.pem -text -noout
DER格式证书的信息:
openssl x509 -in certificate.der -inform der -text -noout
查看KEY证书的信息:
openssl rsa -in mykey.key -text -noout
查看KEY证书的信息，如果是DER格式的话:
openssl rsa -in mykey.key -text -noout -inform der
查看CSR证书的信息:
openssl req -noout -text -in my.csr
查看CSR证书的信息，如果是DER格式的话:
openssl req -noout -text -in my.csr -info der

只提取证书
openssl pkcs12 -nokeys -in cacert.p12 -out ca2cert.pem
只提取秘钥对
openssl pkcs12 -nocerts -in cacert.p12 -out ca2cert.pem

```

证书编码的转换  
```
PEM转为DER openssl x509 -in cert.crt -outform der -out cert.der
DER转为PEM openssl x509 -in cert.crt -inform der -outform pem -out cert.pem
(提示:要转换KEY文件也类似,只不过把x509换成rsa,要转CSR的话,把x509换成req...)
```

### KeyTool证书制作  
```
D:
CD  D:\WorkSpace\IntelliJ\AppServer2
keytool -genkey -alias tomcat -storetype PKCS12 -keyalg RSA -keysize 2048 -keystore keystore.p12 -validity 3650


密码 Alex123123
AlexCheung
Hack
Root
MC
AH
CN


方式二
keytool -genkey -alias tomcat -storetype PKCS12 -keyalg RSA -keysize 2048 -keystore keystore.p12 -validity 3650
D:
CD  D:\WorkSpace\IntelliJ\AppServer2
keytool -genkey -alias tomcat -keyalg RSA -keysize 1024 -keystore hack.jks -validity 3650

密码 Alex123123
AlexCheung
Hack
Root
MC
AH
CN
y



查看证书
keytool -list -v -keystore hack.jks


导出cer证书
keytool -alias tomcat -exportcert -keystore hack.jks -file hack.cer


提取RSA私钥

从JKS转换到PKCS12
一行书写，执行命令
keytool -importkeystore -srckeystore hack.jks -destkeystore hack.p12 -srcstoretype JKS -deststoretype PKCS12 -srcstorepass Alex123123 -deststorepass Alex123123 -srcalias tomcat -destalias tomcat -srckeypass Alex123123 -destkeypass Alex123123 -noprompt

多行书写，查看命令
keytool 
-importkeystore 
-srckeystore hack.jks 
-destkeystore 
hack.p12 
-srcstoretype JKS 
-deststoretype PKCS12 
-srcstorepass Alex123123 
-deststorepass Alex123123 
-srcalias tomcat  
-destalias tomcat 
-srckeypass Alex123123 
-destkeypass Alex123123 
-noprompt



PKCS12转换成PEM格式
C:
CD  C:\OpenSSL-Win64\bin
set  OPENSSL_CONF=C:\OpenSSL-Win64\bin\openssl.cfg
D:
CD  D:\WorkSpace\IntelliJ\AppServer2
C:\OpenSSL-Win64\bin\openssl pkcs12 -in hack.p12 -out hack_server_private_rsa.pem -passin pass:Alex123123 -passout pass:Alex123123



从私钥中导出公钥
C:
CD  C:\OpenSSL-Win64\bin
set  OPENSSL_CONF=C:\OpenSSL-Win64\bin\openssl.cfg
D:
CD  D:\WorkSpace\IntelliJ\AppServer2
C:\OpenSSL-Win64\bin\openssl rsa -in hack_server_private_rsa.pem -pubout -outform PEM -out hack_server_public_rsa.pem



一个RSA AES小案例
RSA的密钥对是文件形式存储的，
D:
cd D:\WorkSpace\IntelliJ\AppServer2
C:\OpenSSL-Win64\bin\openssl pkcs8 -topk8 -inform PEM -in hack_server_private_rsa.pem -outform PEM -nocrypt -out hack_server_private_rsa_pkcs8.pem -passin file:D:\WorkSpace\IntelliJ\AppServer2\config_pwd.java




```
### 参考  
http://blog.csdn.net/axi295309066/article/details/52494832  
http://lixuanbin.iteye.com/blog/2078020  
http://www.cnblogs.com/whoislcj/p/5470095.html  
http://blog.csdn.net/chenshufei2/article/details/48263823  
http://blog.csdn.net/qq_26685493/article/details/51179378  
http://blog.csdn.net/bbld_/article/details/38777491  
http://blog.csdn.net/fengltxx/article/details/53049466  
http://blog.csdn.net/aa464971/article/details/51035200  
http://blog.csdn.net/WHB20081815/article/details/73997645  
http://justjavac.iteye.com/blog/1144151  
http://www.youdzone.com/signature.html  
http://blog.csdn.net/ouyang_peng/article/details/50983574  
https://www.kancloud.cn/kancloud/rsa_algorithm/48493  
http://blog.csdn.net/jiange_zh/article/details/49339289  
http://blog.csdn.net/q376420785/article/details/8557266  
https://www.jianshu.com/p/53b9ffe8fea6  

欧拉函数  
http://blog.csdn.net/wangjian8006/article/details/7833319  

android Security  
https://tamicer.github.io/2017/01/15/retroft011/  

### 附录  
gcd                 greatest common divisor                 最大公约数  
对称加密     symmetric encryption;    
私钥加密     private-key cryptography;  
公钥加密     public key cryptography;  
散列函数     hash function;  

asymmetric			英 [ˌeɪsɪˈmetrɪk]  美 [ˌæsɪˈmɛtrɪk]    非对称的  
cipher					英 ['saɪfə(r)]  美 [ˈsaɪfɚ]     密码  
digest					英 [daɪ'dʒest; dɪ-]  美 [daɪ'dʒɛst		摘要  
encription				英 [ɪn'krɪpʃən]  美 [ɛn'krɪpʃən]		加密 密码  
layer					英 ['leɪə]  美 ['leɪr]		层  层次  
Secure					英 [sɪ'kjʊə; sɪ'kjɔː]  美 [sə'kjʊr]		安全 密码  
security				英 [sɪ'kjʊərətɪ]  美 [sə'kjʊrəti]		安全的 保密的  
symmetric				英 [sɪ'metrɪk]  美 [sɪ'metrɪk]    对称的  

AES					Advanced Encryption Standard		高级加密标准  
DES					Data Encryption Standard		数据加密标准  
SSL						Secure Sockets Layer			安全套接层  



