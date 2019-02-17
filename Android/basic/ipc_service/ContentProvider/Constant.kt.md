```
package com.alex.andfun.service.baselibrary

import com.alex.andfun.service.huggles.entity.ProviderTableEntity

/**
 * 作者：Alex
 * 时间：2017/12/11 14:39
 * 简述：
 */
class C {
    object Database {
        /**
         * 用户表
         */
        @JvmStatic
        val userTableEntity: ProviderTableEntity = ProviderTableEntity(100, "t_user")
        /**
         * 订单表
         */
        @JvmStatic
        val orderTableEntity = ProviderTableEntity(101, "t_order")
        /**
         * 消息表
         */
        @JvmStatic
        val messageTableEntity = ProviderTableEntity(102, "t_message")

        @JvmStatic
        val matchMap = mapOf<Any, String>(
                userTableEntity.matchCode to userTableEntity.tableName,
                orderTableEntity.matchCode to orderTableEntity.tableName,
                messageTableEntity.matchCode to messageTableEntity.tableName
        )
    }

    object ProviderAction {
        /**与 android:authorities="com.alex.andfun.service.provider.DownLoadContentProvider"  相同*/
        const val DOWNLOAD_AUTHORITY = "com.alex.andfun.service.provider.DownLoadContentProvider"
    }

    object ServiceAction {
        const val LOCAL_DOWNLOAD = "com.alex.andfun.service.huggles.LocalDownloadService"
        const val REMOTE_DOWNLOAD = "com.alex.andfun.service.huggles.RemoteDownloadService"
        const val LOLLIPOP_DOWNLOAD = "com.alex.andfun.service.huggles.LollipopDownloadJobService"
    }

    object NotifyId {
        const val DOWNLOAD_FOREGROUND = 1
    }

    object JobId {
        const val DOWNLOAD_JOB = 1
    }

    object NotifyAction {
        const val REQUEST_START_ALL_DOWNLOAD_SERVICE = "REQUEST_START_ALL_DOWNLOAD_SERVICE"
        const val REQUEST_START_LOCAL_DOWNLOAD_SERVICE = "REQUEST_START_LOCAL_DOWNLOAD_SERVICE"
        const val REQUEST_START_REMOTE_DOWNLOAD_SERVICE = "REQUEST_START_REMOTE_DOWNLOAD_SERVICE"
    }
}
```