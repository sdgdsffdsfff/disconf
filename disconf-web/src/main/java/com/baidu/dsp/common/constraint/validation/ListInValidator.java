package com.baidu.dsp.common.constraint.validation;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.baidu.disconf.ub.common.utils.StringUtils;
import com.baidu.dsp.common.constraint.ListInConstraint;

public class ListInValidator implements
        ConstraintValidator<ListInConstraint, Integer> {

    private String allowIntegerListStr;
    private List<Integer> allowIntegerList;

    private static final String SEP = ",";

    @Override
    public void initialize(ListInConstraint constraintAnnotation) {

        this.allowIntegerListStr = constraintAnnotation.allowIntegerList();

        allowIntegerList = StringUtils.parseStringToIntegerList(
                allowIntegerListStr, SEP);
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {

        if (value == null) {
            return false;
        }

        return allowIntegerList.contains(value);
    }
}
