package jh.utils;

import hf.base.exceptions.BizFailException;
import jh.exceptions.BizException;
import org.apache.commons.collections.MapUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by tengfei on 2017/11/4.
 */
public class TypeConverter {

    private static Map<Class,Map<String,PropertyDescriptor>> beanCache = new ConcurrentHashMap<>();

    public static <T> T convert(Map<String,Object> request,Class<T> dataType) throws Exception {
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

            String fieldStr = String.valueOf(request.get(StringUtils.isEmpty(fieldValue.alias())?field.getName():fieldValue.alias()));
            String value = StringUtils.isEmpty(fieldStr)?fieldValue.defaults():fieldStr;

            if(org.apache.commons.lang3.StringUtils.isEmpty(value)) {
                if(fieldValue.required()) {
                    throw new BizFailException("field empty");
                }
                continue;
            }

            if(fieldValue.required() && org.apache.commons.lang3.StringUtils.isEmpty(value)) {
                throw new BizException(String.format("%s cannot be empty",field.getName()));
            } else if(Long.class.isAssignableFrom(field.getType())) {
                beanCache.get(dataType).get(field.getName()).getWriteMethod().invoke(data,new BigDecimal(value).longValue());
            }else if(Integer.class.isAssignableFrom(field.getType())) {
                beanCache.get(dataType).get(field.getName()).getWriteMethod().invoke(data,new BigDecimal(value).intValue());
            }else if(Date.class.isAssignableFrom(field.getType())) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = sdf.parse(value);
                beanCache.get(dataType).get(field.getName()).getWriteMethod().invoke(data,date);
            } else if(BigDecimal.class.isAssignableFrom(field.getType())){
                beanCache.get(dataType).get(field.getName()).getWriteMethod().invoke(data,new BigDecimal(value));
            } else {
                beanCache.get(dataType).get(field.getName()).getWriteMethod().invoke(data,value);
            }
        }

        return data;
    }
}
