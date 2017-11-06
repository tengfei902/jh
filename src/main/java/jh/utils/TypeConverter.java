package jh.utils;

import jh.exceptions.BizException;
import org.apache.commons.collections.MapUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by tengfei on 2017/11/4.
 */
public class TypeConverter {

    private static Map<Class,Map<String,PropertyDescriptor>> beanCache = new ConcurrentHashMap<>();

    public static <T> T convert(HttpServletRequest request,Class<T> dataType) throws Exception {
        T data = dataType.newInstance();
        BeanInfo beanInfo = Introspector.getBeanInfo(dataType);

        if(MapUtils.isEmpty(beanCache.get(dataType))) {
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

            Map<String,PropertyDescriptor> map = new HashMap<>();
            for(PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                map.put(propertyDescriptor.getName(),propertyDescriptor);
            }
            beanCache.put(dataType,map);
        }

        Field[] fields = dataType.getDeclaredFields();

        for(Field field:fields) {
            if(!field.isAnnotationPresent(jh.model.annotations.Field.class)) {
                continue;
            }

            jh.model.annotations.Field fieldValue = field.getDeclaredAnnotation(jh.model.annotations.Field.class);

            if(fieldValue.required() && org.apache.commons.lang3.StringUtils.isEmpty(fieldValue.defaults())) {
                throw new BizException(String.format("%s cannot be empty",field.getName()));
            }
            String value = StringUtils.isEmpty(request.getParameter(field.getName()))?fieldValue.defaults():request.getParameter(field.getName());
            beanCache.get(dataType).get(field.getName()).getWriteMethod().invoke(data,value);
        }

        return data;
    }
}
