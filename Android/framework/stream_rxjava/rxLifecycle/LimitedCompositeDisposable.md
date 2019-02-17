```
package org.alex.rxjava;

/**
 * 作者：Alex
 * 时间：2018/1/11 21:39
 * 简述：
 */

import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.internal.disposables.DisposableContainer;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.util.ExceptionHelper;
import io.reactivex.internal.util.OpenHashSet;

/**
 * A disposable container that can hold onto multiple other disposables and
 * offers O(1) add and removal complexity.
 */
@SuppressWarnings({"Convert2Diamond", "unused", "SameParameterValue", "WeakerAccess"})
public final class LimitedCompositeDisposable implements Disposable, DisposableContainer {

    private OpenHashSet<Disposable> resources;

    private volatile boolean disposed;

    /**
     * Creates an empty LimitedCompositeDisposable.
     */
    public LimitedCompositeDisposable() {
    }

    /**
     * Creates a CompositeDisposables with the given array of initial elements.
     *
     * @param resources the array of Disposables to start with
     */
    public LimitedCompositeDisposable(@NonNull Disposable... resources) {
        ObjectHelper.requireNonNull(resources, "resources is null");
        this.resources = new OpenHashSet<Disposable>(resources.length + 1);
        for (Disposable d : resources) {
            ObjectHelper.requireNonNull(d, "Disposable item is null");
            this.resources.add(d);
        }
    }

    /**
     * Creates a CompositeDisposables with the given Iterable sequence of initial elements.
     *
     * @param resources the Iterable sequence of Disposables to start with
     */
    public LimitedCompositeDisposable(@NonNull Iterable<? extends Disposable> resources) {
        ObjectHelper.requireNonNull(resources, "resources is null");
        this.resources = new OpenHashSet<Disposable>();
        for (Disposable d : resources) {
            ObjectHelper.requireNonNull(d, "Disposable item is null");
            this.resources.add(d);
        }
    }

    @Override
    public void dispose() {
        if (disposed) {
            return;
        }
        OpenHashSet<Disposable> set;
        synchronized (this) {
            if (disposed) {
                return;
            }
            disposed = true;
            set = resources;
            resources = null;
        }

        dispose(set);
    }

    @Override
    public boolean isDisposed() {
        return disposed;
    }

    @Override
    public boolean add(@NonNull Disposable d) {
        ObjectHelper.requireNonNull(d, "d is null");
        if (!disposed) {
            synchronized (this) {
                if (!disposed) {
                    OpenHashSet<Disposable> set = resources;
                    if (set == null) {
                        set = new OpenHashSet<Disposable>();
                        resources = set;
                    }
                    set.add(d);
                    return true;
                }
            }
        }
        d.dispose();
        return false;
    }

    /**
     * Atomically adds the given array of Disposables to the container or
     * disposes them all if the container has been disposed.
     *
     * @param ds the array of Disposables
     * @return true if the operation was successful, false if the container has been disposed
     */
    public boolean addAll(@NonNull Disposable... ds) {
        ObjectHelper.requireNonNull(ds, "ds is null");
        if (!disposed) {
            synchronized (this) {
                if (!disposed) {
                    OpenHashSet<Disposable> set = resources;
                    if (set == null) {
                        set = new OpenHashSet<Disposable>(ds.length + 1);
                        resources = set;
                    }
                    for (Disposable d : ds) {
                        ObjectHelper.requireNonNull(d, "d is null");
                        set.add(d);
                    }
                    return true;
                }
            }
        }
        for (Disposable d : ds) {
            d.dispose();
        }
        return false;
    }

    @Override
    public boolean remove(@NonNull Disposable d) {
        if (delete(d)) {
            d.dispose();
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(@NonNull Disposable d) {
        ObjectHelper.requireNonNull(d, "Disposable item is null");
        if (disposed) {
            return false;
        }
        synchronized (this) {
            if (disposed) {
                return false;
            }

            OpenHashSet<Disposable> set = resources;
            if (set == null || !set.remove(d)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Atomically clears the container, then disposes all the previously contained Disposables.
     */
    public void clear() {
        if (disposed) {
            return;
        }
        OpenHashSet<Disposable> set;
        synchronized (this) {
            if (disposed) {
                return;
            }

            set = resources;
            resources = null;
        }

        dispose(set);
    }

    public void removeDisposed() {
        removeDisposed(16);
    }

    public void removeDisposed(int size) {
        if (disposed) {
            return;
        }
        OpenHashSet<Disposable> set;
        synchronized (this) {
            if (disposed) {
                return;
            }
            set = resources;
            if (set.size() >= size) {
                Object[] disposableArray = set.keys();
                for (Object aDisposableArray : disposableArray) {
                    Disposable disposables = (Disposable) aDisposableArray;
                    if (disposables.isDisposed()) {
                        set.remove(disposables);
                    }
                }
            }
        }
    }

    /**
     * Returns the number of currently held Disposables.
     *
     * @return the number of currently held Disposables
     */
    public int size() {
        if (disposed) {
            return 0;
        }
        synchronized (this) {
            if (disposed) {
                return 0;
            }
            OpenHashSet<Disposable> set = resources;
            return set != null ? set.size() : 0;
        }
    }

    /**
     * Dispose the contents of the OpenHashSet by suppressing non-fatal
     * Throwables till the end.
     *
     * @param set the OpenHashSet to dispose elements of
     */
    private void dispose(OpenHashSet<Disposable> set) {
        if (set == null) {
            return;
        }
        List<Throwable> errors = null;
        Object[] array = set.keys();
        for (Object o : array) {
            if (o instanceof Disposable) {
                try {
                    ((Disposable) o).dispose();
                } catch (Throwable ex) {
                    Exceptions.throwIfFatal(ex);
                    if (errors == null) {
                        errors = new ArrayList<Throwable>();
                    }
                    errors.add(ex);
                }
            }
        }
        if (errors != null) {
            if (errors.size() == 1) {
                throw ExceptionHelper.wrapOrThrow(errors.get(0));
            }
            throw new CompositeException(errors);
        }
    }
}

```