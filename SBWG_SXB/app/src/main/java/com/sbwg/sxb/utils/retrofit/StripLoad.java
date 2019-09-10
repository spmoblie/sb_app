package com.sbwg.sxb.utils.retrofit;

import rx.functions.Func1;

/**
 * 剥离 最终数据
 */

public class StripLoad<T> implements Func1<BaseResponse<T>, T>{

    @Override
    public T call(BaseResponse<T> tBaseResponse) {
        return null;
    }

}
