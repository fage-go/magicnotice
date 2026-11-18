package com.zhangshuo.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.bind.CollectionTypeAdapterFactory;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GsonUtils {

    private static Gson gson;

    /**
     * 自定义TypeAdapter ,null对象将被解析成空字符串
     */
    public static final TypeAdapter<String> STRING = new TypeAdapter<String>() {
        @Override
        public String read(JsonReader reader) {
            try {
                if (reader.peek() == JsonToken.NULL) {
                    reader.nextNull();
                    return "";//原先是返回Null，这里改为返回空字符串
                }
                if (reader.peek() == JsonToken.BOOLEAN) {
                    //如果返回的是boolean，但是实体类定义的是String 的情况下
                    return String.valueOf(reader.nextBoolean());
                }
                return reader.nextString();
            } catch (Exception e) {
                LogUtils.e("json to String erro; -> " + e + reader.toString());
            }
            return "";
        }

        @Override
        public void write(JsonWriter writer, String value) {
            try {
                if (value == null) {
                    writer.nullValue();
                    return;
                }
                writer.value(value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    static {
        GsonBuilder gsonBulder = new GsonBuilder();
        gsonBulder.registerTypeAdapter(String.class, STRING);   //所有String类型null替换为字符串“”
        //通过反射获取instanceCreators属性
        try {
            Class builder = (Class) gsonBulder.getClass();
            Field f = builder.getDeclaredField("instanceCreators");
            f.setAccessible(true);
            Map<Type, InstanceCreator<?>> val = (Map<Type, InstanceCreator<?>>) f.get(gsonBulder);//得到此属性的值
            ConstructorConstructor constructorConstructor = new ConstructorConstructor(val);
            //注册数组的处理器
            gsonBulder.registerTypeAdapterFactory(new CollectionTypeAdapterFactory(constructorConstructor));

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        gson = gsonBulder.create();
    }

    /**
     * @author zhangshuo
     * @date 2020-04-19
     * @Desc gson 转换
     */
    public static String toString(Object object) {
        return gson.toJson(object);
    }

    /**
     * Json字符串 转为指定对象
     *
     * @param json json字符串
     * @param type 对象类型
     * @param <T>  对象类型
     * @return
     */
    public static <T> T toBean(String json, Type type) {
        return gson.fromJson(json, type);
    }

    public static <T> List<T> toBeanArray(String json, Class clazz) {
        Type type = new ParameterizedTypeImpl(clazz);
        List<T> dataList = gson.fromJson(json, type);
        if (dataList == null) return new ArrayList<>();
        return dataList;
    }


    public static <T> T convert(String data, Type type) {
        Type mType = $Gson$Types.canonicalize(type);  //将泛型转为type
        T t = null;
        try {
            t = GsonUtils.toBean(data, mType);
        } catch (JsonSyntaxException e) {
            LogUtils.e("errData :" + data + " type:" + type.toString());
        }
        return t;
    }

    private static class ParameterizedTypeImpl implements ParameterizedType {
        Class clazz;

        public ParameterizedTypeImpl(Class clz) {
            clazz = clz;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{clazz};
        }

        @Override
        public Type getRawType() {
            return List.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    }
}