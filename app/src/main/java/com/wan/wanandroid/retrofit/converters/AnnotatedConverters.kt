package com.wan.wanandroid.retrofit.converters

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type
import java.util.LinkedHashMap

final class AnnotatedConverters: Converter.Factory{
    companion object Builder {
        val factories: MutableMap<Class<out Annotation>, Converter.Factory> = LinkedHashMap()
        fun add(cls: Class<out Annotation> , factory: Converter.Factory):Builder{
            factories.put(cls, factory)
            return this
        }

        fun build(): AnnotatedConverters{
            return AnnotatedConverters(factories)
        }
    }

    val factories: MutableMap<Class<out Annotation>, Converter.Factory>

    private constructor(factories: MutableMap<Class<out Annotation>, Converter.Factory>){
        this.factories = factories
    }

    override fun responseBodyConverter(type: Type?, annotations: Array<out Annotation>?, retrofit: Retrofit?): Converter<ResponseBody, *>? {

        annotations?.let {
            for (ann in it){
                val factory = factories[ann.annotationClass.java]
                if (factory != null) {
                    return factory.responseBodyConverter(type, it, retrofit)
                }
            }
        }
        return retrofit!!.nextResponseBodyConverter<String>(null, type, annotations)
    }

    override fun requestBodyConverter(type: Type?, parameterAnnotations: Array<out Annotation>?, methodAnnotations: Array<out Annotation>?, retrofit: Retrofit?): Converter<*, RequestBody>? {

        parameterAnnotations?.let {
            for (pann in it){
                val factory = factories[pann.annotationClass.java]
                if (factory != null) {
                    return factory.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit)
                }
            }
        }
        return null
    }


}