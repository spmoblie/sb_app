package com.songbao.sampo.utils.retrofit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 将一些重复的操作提出来，放到父类以免Loader里每个接口都有重复代码
 */

public class ObjectLoader {

    /**
     * @param observable
     * @param <T>
     * @return
     */
    protected <T> Observable<T> observe(Observable<T> observable){
        return observable
                .subscribeOn(Schedulers.io()) //切换至IO调度器去服务器获取数据
                .unsubscribeOn(Schedulers.io()) //获取数据后切回主线程去分发结果
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<T, Observable<T>>() {
                    @Override
                    public Observable<T> call(T t) {
                        return Observable.just(t);
                    }
                });
    }

}
