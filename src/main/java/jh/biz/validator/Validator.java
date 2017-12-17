package jh.biz.validator;

import java.util.Map;

public interface Validator {
    ValidateResult validate(Map<String,Object> map);
}
