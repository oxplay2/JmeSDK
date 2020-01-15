package com.jayfella.sdk.registrar;

import com.jayfella.sdk.ext.registrar.filter.FilterRegistrar;
import com.jme3.app.SimpleApplication;
import com.jme3.post.Filter;
import com.jme3.post.filters.TranslucentBucketFilter;

public class TranslucentBucketFilterRegistrar extends FilterRegistrar {

    public TranslucentBucketFilterRegistrar() {
        setRegisteredClass(TranslucentBucketFilter.class);
    }

    @Override
    public Filter createInstance(SimpleApplication simpleApplication) {
        return new TranslucentBucketFilter(true);
    }
}
