```
package org.alex.rxjava.lifecycle;

/**
 * 作者：Alex
 * 时间：2017/8/15 23:02
 * 简述：
 */
@SuppressWarnings("WeakerAccess")
public enum RxLifeCycleEvent implements LifeCycleEvent {
    ATTACH,
    CREATE,
    CREATE_VIEW,
    START,
    RESUME,
    PAUSE,
    STOP,
    DESTROY_VIEW,
    DESTROY,
    DETACH
}


```