```
package com.alex.andfun.service.huggles.dbhelper

/**
 * 作者：Alex
 * 时间：2017/12/22 23:58
 * 简述：
 */
object SqlConstant {
    @JvmStatic
    val createUserTable: String = """create table t_user(
        _id integer primary key,
        name      varchar(20) ,
        age       Integer DEFAULT 0
        );"""
}
```