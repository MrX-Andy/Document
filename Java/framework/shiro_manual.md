```
基础概念
Authentication：身份认证/登录，验证用户是不是拥有相应的身份；
Authorization：授权，即权限验证，验证某个已认证的用户是否拥有某个权限；即判断用户是否能做事情，常见的如：验证某个用户是否拥有某个角色。或者细粒度的验证某个用户对某个资源是否具有某个权限；
Subject：主体，可以看到主体可以是任何可以与应用交互的“用户”；
SecurityManager：是Shiro的心脏；所有具体的交互都通过SecurityManager进行控制；它管理着所有Subject、且负责进行认证和授权、及会话、缓存的管理。
    SecurityManager是一个接口，继承了Authenticator, Authorizer, SessionManager这三个接口。
Authenticator：认证器，负责主体认证的，这是一个扩展点，如果用户觉得Shiro默认的不好，可以自定义实现；其需要认证策略（Authentication Strategy），即什么情况下算用户认证通过了；
Authrizer：授权器，或者访问控制器，用来决定主体是否有权限进行相应的操作；即控制着用户能访问应用中的哪些功能；
Realm：可以有1个或多个Realm，可以认为是安全实体数据源，即用于获取安全实体的；可以是JDBC实现，也可以是LDAP实现，或者内存实现等等；由用户提供；注意：Shiro不知道你的用户/权限存储在哪及以何种格式存储；所以我们一般在应用中都需要实现自己的Realm；
SessionManager：如果写过Servlet就应该知道Session的概念，Session呢需要有人去管理它的生命周期，这个组件就是SessionManager；而Shiro并不仅仅可以用在Web环境，也可以用在如普通的JavaSE环境、EJB等环境；所有呢，Shiro就抽象了一个自己的Session来管理主体与应用之间交互的数据；这样的话，比如我们在Web环境用，刚开始是一台Web服务器；接着又上了台EJB服务器；这时想把两台服务器的会话数据放到一个地方，这个时候就可以实现自己的分布式会话（如把数据放到Memcached服务器）；
SessionDAO：DAO大家都用过，数据访问对象，用于会话的CRUD，比如我们想把Session保存到数据库，那么可以实现自己的SessionDAO，通过如JDBC写到数据库；比如想把Session放到Memcached中，可以实现自己的Memcached SessionDAO；另外SessionDAO中可以使用Cache进行缓存，以提高性能；
CacheManager：缓存控制器，来管理如用户、角色、权限等的缓存的；因为这些数据基本上很少去改变，放到缓存中后可以提高访问的性能
Cryptography：密码模块，Shiro提高了一些常见的加密组件用于如密码加密/解密的。
Concurrency：shiro支持多线程应用的并发验证，即如在一个线程中开启另一个线程，能把权限自动传播过去；
Run As：允许一个用户假装为另一个用户（如果他们允许）的身份进行访问；
Remember Me：记住我，这个是非常常见的功能，即一次登录后，下次再来的话不用登录了。

身份验证
身份验证，即在应用中谁能证明他就是他本人。一般提供如他们的身份ID一些标识信息来表明他就是他本人，如提供身份证，用户名/密码来证明。
在shiro中，用户需要提供principals （身份）和credentials（证明）给shiro，从而应用能验证用户身份：
principals：身份信息，即主体的标识属性，可以是任何东西，如用户名、邮箱等，唯一即可。一个主体可以有多个principals，但只有一个Primary principals，一般是用户名/密码/手机号。
credentials：证明/凭证，即只有主体知道的安全值，如密码/数字证书等。



从如上代码可总结出身份验证的步骤：
1、收集用户身份/凭证，即如用户名/密码；
2、调用Subject.login进行登录，如果失败将得到相应的AuthenticationException异常，根据异常提示用户错误信息；否则登录成功；
3、最后调用Subject.logout进行退出操作。
如上测试的几个问题：
1、用户名/密码硬编码在ini配置文件，以后需要改成如数据库存储，且密码需要加密存储；
2、用户身份Token可能不仅仅是用户名/密码，也可能还有其他的，如登录时允许用户名/邮箱/手机号同时登录。
身份认证
流程如下：
1、首先调用Subject.login(token)进行登录，其会自动委托给Security Manager，调用之前必须通过SecurityUtils. setSecurityManager()设置；
2、SecurityManager负责真正的身份验证逻辑；它会委托给Authenticator进行身份验证；
3、Authenticator才是真正的身份验证者，Shiro API中核心的身份认证入口点，此处可以自定义插入自己的实现；
4、Authenticator可能会委托给相应的AuthenticationStrategy进行多Realm身份验证，默认ModularRealmAuthenticator会调用AuthenticationStrategy进行多Realm身份验证；
5、Authenticator会把相应的token传入Realm，从Realm获取身份验证信息，如果没有返回/抛出异常表示身份验证失败了。此处可以配置多个Realm，将按照相应的顺序及策略进行访问。

Realm
Realm：域，Shiro从从Realm获取安全数据（如用户、角色、权限），就是说SecurityManager要验证用户身份，那么它需要从Realm获取相应的用户进行比较以确定用户身份是否合法；也需要从Realm得到用户相应的角色/权限进行验证用户是否能进行操作；可以把Realm看成DataSource，即安全数据源。

Exception
DisabledAccountException（帐号被禁用） 
LockedAccountException（帐号被锁定） 
ExcessiveAttemptsException（登录失败次数过多） 
ExpiredCredentialsException（凭证过期）等




Filter
authc
认证过滤器  org.apache.shiro.web.filter.authc.FormAuthenticationFilter
基于表单的拦截器；如 “/**=authc” ，如果没有登录会跳到相应的登录页面登录；主要属性：
usernameParam：表单提交的用户名参数名（ username）；  
passwordParam：表单提交的密码参数名（password）； 
rememberMeParam：表单提交的密码参数名（rememberMe）；  
loginUrl：登录页面地址（/login.jsp）；
successUrl：登录成功后的默认重定向地址； 
failureKeyAttribute：登录失败后错误信息存储key（shiroLoginFailure）；
例如/admins/user/**=authc表示需要认证(登录)才能使用，没有参数 

authcBasic
认证过滤器  org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter
Basic HTTP身份验证拦截器，主要属性： 
applicationName：弹出登录框显示的信息（application）；
例如 /admins/user/**=authcBasic没有参数表示httpBasic认证


logout
org.apache.shiro.web.filter.authc.LogoutFilter
退出拦截器，主要属性：
redirectUrl：退出成功后重定向的地址（/）;示例 “/logout=logout”

user
认证过滤器  org.apache.shiro.web.filter.authc.UserFilter
用户拦截器，用户已经身份验证/记住我登录的都可；
示例 “/**=user”
例如 /admins/user/**=user没有参数表示必须存在用户，当登入操作时不做检查 

anon
认证过滤器  org.apache.shiro.web.filter.authc.AnonymousFilter
匿名拦截器，即不需要登录即可访问；一般用于静态资源过滤；
示例”/static/**=anon”
例子/admins/**=anon 没有参数，表示可以匿名使用。 

roles
授权过滤器   org.apache.shiro.web.filter.authz.RolesAuthorizationFilter
角色授权拦截器，验证用户是否拥有所有角色；主要属性： 
loginUrl：登录页面地址（/login.jsp）；
unauthorizedUrl：未授权后重定向的地址；
示例 “/admin/**=roles[admin]”
例子 /admins/user/**=roles[admin],
参数可以写多个，多个时必须加上引号，并且参数之间用逗号分割，当有多个参数时，
例如admins/user/**=roles["admin,guest"],每个参数通过才算通过，相当于hasAllRoles()方法。 

perms
授权过滤器   org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter
权限授权拦截器，验证用户是否拥有所有权限；属性和roles一样；
示例 “/user/**=perms["user:create"]”
例子/admins/user/**=perms[user:add:*]
参数可以写多个，多个时必须加上引号，并且参数之间用逗号分割，
例如 /admins/user/**=perms["user:add:*,user:modify:*"]，当有多个参数时必须每个参数都通过才通过，想当于isPermitedAll()方法。 

port
授权过滤器   org.apache.shiro.web.filter.authz.PortFilter
端口拦截器，主要属性：
port（80）：可以通过的端口；
示例 “/test= port[80]” ，如果用户访问该页面是非80，将自动将请求端口改为80并重定向到该80端口，其他路径/参数等都一样
例子/admins/user/**=port[8081],当请求的url的端口不是8081是跳转到schemal://serverName:8081?queryString,其中schmal是协议http或https等，
serverName是你访问的host,8081是url配置里port的端口，queryString 是你访问的url里的？后面的参数。 


rest
授权过滤器   org.apache.shiro.web.filter.authz.HttpMethodPermissionFilter
rest风格拦截器，自动根据请求方法构建权限字符串（GET=read, POST=create,PUT=update,DELETE=delete,HEAD=read,TRACE=read,OPTIONS=read, MKCOL=create）
构建权限字符串；
示例 “/users=rest[user]” ，会自动拼出 “user:read,user:create,user:update,user:delete” 权限字符串进行权限匹配（所有都得匹配，isPermittedAll）；
例子 /admins/user/**=rest[user],根据请求的方法，相当于/admins/user/**=perms[user:method] ,其中method为post，get，delete等。

ssl
授权过滤器   org.apache.shiro.web.filter.authz.SslFilter
SSL拦截器，只有请求协议是https才能通过；否则自动跳转会https端口（443）；其他和port拦截器一样；
例子 /admins/user/**=ssl没有参数，表示安全的url请求，协议为https 






Shiro 标签
标签名称	               	标签条件（均是显示标签内容）
<shiro:authenticated>							登录之后
<shiro:notAuthenticated>						不在登录状态时
<shiro:guest>									用户在没有RememberMe时
<shiro:user>									用户在RememberMe时
<shiro:hasAnyRoles name="abc,123" >		在有abc或者123角色时
<shiro:hasRole name="abc">					拥有角色abc
<shiro:lacksRole name="abc">				没有角色abc
<shiro:hasPermission name="abc">			拥有权限资源abc
<shiro:lacksPermission name="abc">			没有abc权限资源
<shiro:principal>								显示用户身份名称
<shiro:principal property="username"/>		显示用户身份中的属性值
App登录认证
1. APP第一次登录，使用用户名和密码，如果登录成功，将cookie保存在APP本地（比如sharepreference），后台将cookie值保存到user表里面
2.1 APP访问服务器，APP将cookie添加在heade里面，服务器session依然存在，可以正常访问
2.2 APP访问服务器，APP将cookie添加在heade里面，服务器session过期，访问失败，
由APP自动带着保存在本地的cookie去服务器登录，
这个时候， App一定要去Login页面的，不可能做到所有的接口，自动刷新toke，并继续访问之前断掉的接口；
如果每个接口先判断token失效问题， 再请求业务接口，代价太大；
服务器可以根据cookie和用户名进行登录，这样服务器又有session，会生成新的cookie返回给APP，APP更新本地cookie，又可以正常访问。
用户手动退出APP，删除APP本次存储的cookie，下次登录使用用户名和密码登录



单点登录
单点登录SSO（Single Sign On）说得简单点就是在一个多系统共存的环境下，用户在一处登录后，就不用在其他系统中登录，也就是用户的一次登录能得到其他所有系统的信任。
单点登录在大型网站里使用得非常频繁，例如像阿里巴巴这样的网站，在网站的背后是成百上千的子系统，用户一次操作或交易可能涉及到几十个子系统的协作，
如果每个子系统都需要用户认证，不仅用户会疯掉，各子系统也会为这种重复认证授权的逻辑搞疯掉。实现单点登录说到底就是要解决如何产生和存储那个信任，
再就是其他系统如何验证这个信任的有效性，因此要点也就以下几个：存储信任、验证信任。





以上方案就是要把信任关系存储在单独的SSO系统（暂且这么称呼它）里，说起来只是简单地从客户端移到了服务端，但其中几个问题需要重点解决：

如何高效存储大量临时性的信任数据
如何防止信息传递过程被篡改
如何让SSO系统信任登录系统和免登系统

用户角色权限管理
RBAC（Role-Based Access Control，基于角色的访问控制），就是用户通过角色与权限进行关联。简单地说，一个用户拥有若干角色，每一个角色拥有若干权限。
这样，就构造成“用户-角色-权限”的授权模型。在这种模型中，用户与角色之间，角色与权限之间，一般者是多对多的关系。
角色是什么？
可以理解为一定数量的权限的集合，权限的载体。例如：一个论坛系统，“超级管理员”、“版主”都是角色。版主可管理版内的帖子、可管理版内的用户等，这些是权限。
要给某个用户授予这些权限，不需要直接将权限授予用户，可将“版主”这个角色赋予该用户。

当用户的数量非常大时，要给系统每个用户逐一授权（授角色），是件非常烦琐的事情。这时，就需要给用户分组，每个用户组内有多个用户。
除了可给用户授权外，还可以给用户组授权。这样一来，用户拥有的所有权限，就是用户个人拥有的权限与该用户所在用户组拥有的权限之和。





权限表现成什么？
对功能模块的操作，对上传文件的删改，菜单的访问，甚至页面上某个按钮、某个图片的可见性控制，都可属于权限的范畴。
有些权限设计，会把功能操作作为一类，而把文件、菜单、页面元素等作为另一类，这样构成“用户-角色-权限-资源”的授权模型。
而在做数据表建模时，可把功能操作和资源统一管理，也就是都直接与权限表进行关联，这样可能更具便捷性和易扩展性。






请留意权限表中有一列“权限类型”，我们根据它的取值来区分是哪一类权限，
如“MENU”表示菜单的访问权限、“OPERATION”表示功能模块的操作权限、“FILE”表示文件的修改权限、“ELEMENT”表示页面元素的可见性控制等。

这样设计的好处有二。其一，不需要区分哪些是权限操作，哪些是资源，（实际上，有时候也不好区分，如菜单，把它理解为资源呢还是功能模块权限呢？）。
其二，方便扩展，当系统要对新的东西进行权限控制时，我只需要建立一个新的关联表“权限XX关联表”，并确定这类权限的权限类型字符串。

这里要注意的是，权限表与权限菜单关联表、权限菜单关联表与菜单表都是一对一的关系。（文件、页面权限点、功能操作等同理）。
也就是每添加一个菜单，就得同时往这三个表中各插入一条记录。这样，可以不需要权限菜单关联表，让权限表与菜单表直接关联，
此时，须在权限表中新增一列用来保存菜单的ID，权限表通过“权限类型”和这个ID来区分是种类型下的哪条记录。

角色组对角色进行分类管理





随着系统的日益庞大，为了方便管理，可引入角色组对角色进行分类管理，跟用户组不同，角色组不参与授权。
例如：某电网系统的权限管理模块中，角色就是挂在区局下，而区局在这里可当作角色组，它不参于权限分配。
另外，为方便上面各主表自身的管理与查找，可采用树型结构，如菜单树、功能树等，当然这些可不需要参于权限分配。

管理角色组
管理角色组可将管理角色与一组管理员或专家用户进行关联。管理员管理众多 Exchange 组织或收件人配置。专家用户管理 Exchange 的特定功能，例如遵从性。
或者，他们可能具有有限的管理能力（例如技术支持成员），但没有被授予广泛的管理权限。角色组通常会关联公用管理角色，这些角色使管理员和专家用户可以管理其组织以及收件人的配置。
例如，管理员能否管理收件人或使用邮箱发现功能是通过角色组控制的。

为管理员或专家用户分配权限最常见的操作是在角色组中添加或删除用户。有关详细信息，请参阅了解管理角色组。
角色组包含用于定义管理员和专家用户可执行的操作的下列组件：
管理角色组 管理角色组是一个特殊的通用安全组 (USG)，它包含属于角色组成员的邮箱、用户、USG 以及其他角色组。可以在此处添加和删除成员，并且它还是向其分配管理角色的对象。
角色组中所有角色的组合定义添加到角色组的用户可以在 Exchange 组织中管理的所有事情。
管理角色 管理角色是一组管理角色条目的容器。角色用于定义特定任务，这些任务可以由分配该角色的角色组成员执行。
管理角色条目是允许执行角色中的每个特定任务的 cmdlet、脚本或特殊权限。有关详细信息，请参阅了解管理角色。
管理角色分配 管理角色分配链接角色和角色组。将角色分配到角色组，将使角色组的成员能够使用此角色中定义的 cmdlet 和参数。角色分配可以使用管理作用域来控制何处能够使用此分配。
管理角色作用域 管理角色作用域是角色分配影响的作用域。将角色与作用域分配到角色组时，管理作用域会专门指定允许该分配进行管理的对象。
然后会将此分配及其作用域指定给角色组的成员，从而限制这些成员可以管理的内容。作用域可以由一系列服务器或数据库、组织单位 (OU)、服务器上的筛选器、数据库或收件人对象组成。
在向角色组添加用户时，该用户将被授予分配给该角色组的所有角色。如果对角色组和角色之间的任何角色分配应用作用域，这些作用域将控制该用户可管理哪些服务器配置或收件人。
如果要更改分配给角色组的角色，则需要更改将角色组链接到角色的角色分配。除非 Exchange 2013 中内置的分配不符合需要，否则不必更改这些分配。有关详细信息，请参阅了解管理角色分配。






无状态token登录
基本步骤
第一需要ShiroConfiguration：在这个类中主要是注入shiro的filterFactoryBean和securityManager等对象。
第二需要StatelessAccessControlFilter：这个类中实现访问控制过滤，当我们访问url的时候，这个类中的两个方法会进行拦截处理。
第三需要StatelessAuthorizingRealm：这个类中主要是身份认证，验证信息是否合理，是否有角色和权限信息。
第四需要StatelessAuthenticationToken：在shiro中有一个我们常用的UsernamePasswordToken，因为我们需要这里需要自定义一些属性值，比如：消息摘要，参数Map。
第五需要StatelessDefaultSubjectFactory：由于我们编写的是无状态的，每人情况是会创建session对象的，那么我们需要修改createSubject关闭session的创建。
第六需要HmacSHA256Utils：Java 加密解密之消息摘要算法，对我们的参数信息进行处理。
       以上就是基本这篇文章用到的几个核心的类，我们看看具体的步骤。
       
       
       
       
       





Api解释
AuthorizationInfo
Collection<String> getRoles(); //获取角色字符串信息
Collection<String> getStringPermissions(); //获取权限字符串信息
Collection<Permission> getObjectPermissions(); //获取Permission对象信息

SimpleAuthenticationInfo

SimpleAuthenticationInfo
/**  *构造函数接受一个“主”主帐户的主项及其相应的凭证，与指定的域相关联。  * 这是一个方便的构造函数，它将根据主体和realmName参数构造一个原理集。  * @params principal 身份，即主体的标识属性——与指定领域相关联的“主”主体。  *     可以是任何东西，如用户名、邮箱等，唯一即可。  *     一个主体可以有多个principals，但只有一个Primary principals，一般是用户名/密码/手机号。  * @params credentials 凭证——验证给定主体的凭证。  *     证明/凭证，即只有主体知道的安全值，如密码/数字证书等。  *     最常见的principals和credentials组合就是用户名/密码了。  * @params realmName-来自于主体和凭证被获取的领域。  */
public SimpleAuthenticationInfo(Object principal, Object credentials, String realmName);




Realm
一般情况下， 重写一个Realm需要重写以下方法
String getName(); //返回一个唯一的Realm名字  
boolean supports(AuthenticationToken token); //判断此Realm是否支持此Token  
AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)  throws AuthenticationException;  //根据Token获取认证信息 

getName

/**  * 返回分配给这个领域的(应用程序唯一的)名称。为单个应用程序配置的所有域必须具有唯一的名称。  * 返回:  * 分配给这个领域的(应用程序唯一的)名称。  */
String getName();


AuthenticatingRealm
doGetAuthenticationInfo






/**  * 认证信息.(身份验证) : Authentication 是用来验证用户身份  *  * @param authenticationToken  * @return  * @throws AuthenticationException  */
protected abstract AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException;

PrincipalCollection
Object getPrimaryPrincipal(); //得到主要的身份
<T> T oneByType(Class<T> type); //根据身份类型获取第一个
<T> Collection<T> byType(Class<T> type); //根据身份类型获取一组
List asList(); //转换为List
Set asSet(); //转换为Set
Collection fromRealm(String realmName); //根据Realm名字获取
Set<String> getRealmNames(); //获取所有身份验证通过的Realm名字
boolean isEmpty(); //判断是否为空

Subject






ehcache
name:缓存名称。
maxElementsInMemory:缓存最大数目
maxElementsOnDisk：硬盘最大缓存个数。 
eternal:对象是否永久有效，一但设置了，timeout将不起作用。 
overflowToDisk:是否保存到磁盘，当系统当机时
timeToIdleSeconds:设置对象在失效前的允许闲置时间（单位：秒）。仅当eternal=false对象不是永久有效时使用，可选属性，默认值是0，也就是可闲置时间无穷大。
timeToLiveSeconds:设置对象在失效前允许存活时间（单位：秒）。最大时间介于创建时间和失效时间之间。仅当eternal=false对象不是永久有效时使用，默认是0.，也就是对象存活时间无穷大。
diskPersistent：是否缓存虚拟机重启期数据 Whether the disk store persists between restarts of the Virtual Machine. The default value is false. 
diskSpoolBufferSizeMB：这个参数设置DiskStore（磁盘缓存）的缓存区大小。默认是30MB。每个Cache都应该有自己的一个缓冲区。 
diskExpiryThreadIntervalSeconds：磁盘失效线程运行时间间隔，默认是120秒。
memoryStoreEvictionPolicy：当达到maxElementsInMemory限制时，Ehcache将会根据指定的策略去清理内存。默认策略是LRU（最近最少使用）。你可以设置为FIFO（先进先出）或是LFU（较少使用）。 
clearOnFlush：内存数量最大时是否清除。
memoryStoreEvictionPolicy:
Ehcache的三种清空策略;
FIFO，first in first out，这个是大家最熟的，先进先出。
LFU， Less Frequently Used，就是上面例子中使用的策略，直白一点就是讲一直以来最少被使用的。如上面所讲，缓存的元素有一个hit属性，hit值最小的将会被清出缓存。
LRU，Least Recently Used，最近最少使用的，缓存的元素有一个时间戳，当缓存容量满了，而又需要腾出地方来缓存新的元素的时候，那么现有缓存元素中时间戳离当前时间最远的元素将被清出缓存。


遇到的问题
重定向问题
app请求的地址是
POST  http://192.168.0.7:8081/AppServer/orderList
contentType = applicationx-www-form-urlencoded
请求体: 
pwd=123456
phone=13146008029
server的拦截器，收到的请求地址是
LogTrack  WARN  [ (PrintParamsInterceptorController.java:44) #interceptor] 匹配到 2017-08-06 18:54:27  UserController  login    (com.alex.appserver.module.user.UserController)
   请求参数：
	拼接串：http://192.168.0.7:8081/AppServer/login;JSESSIONID=e8b00909-97c9-4aaa-bb32-436f69bd9de3
	请求行：
LogTrack  WARN  [ (UserController.java:69) #login]  进来了 http://192.168.0.7:8081/AppServer/login;JSESSIONID=e8b00909-97c9-4aaa-bb32-436f69bd9de3
很明显， url 被重定向了

解决携带 ;JSESSIONID 的问题
解决 http://192.168.0.7:8081/AppServer/login;JSESSIONID=e8b00909-97c9-4aaa-bb32-436f69bd9de3 携带 ;JSESSIONID 的问题
SimpleWebSessionManager
public class SimpleWebSessionManager extends DefaultWebSessionManager {     @Override     protected void onStart(Session session, SessionContext context) {         super.onStart(session, context);         ServletRequest request = WebUtils.getRequest(context);         request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, ShiroHttpServletRequest.COOKIE_SESSION_ID_SOURCE);     } }



       
```