package com.atguigu.meet.config.aop;

import cn.hutool.core.util.ReflectUtil;
import com.atguigu.meet.annotation.ForbidOperateSelf;
import com.atguigu.meet.exception.BusinessException;
import com.atguigu.meet.utils.AdminContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ForbidOperateSelfAspect {

    @Pointcut("@annotation(forbidOperateSelf)")
    public void pointCut(ForbidOperateSelf forbidOperateSelf) {
    }

    @Around("pointCut(forbidOperateSelf)")
    public Object around(ProceedingJoinPoint joinPoint, ForbidOperateSelf forbidOperateSelf) throws Throwable {
        // 从你项目的 AdminContext 获取登录ID
        Long loginUserId = AdminContext.getLoginUserId();

        if (loginUserId == null) {
            throw new BusinessException(401, "未登录，请先进行身份验证");
        }

        Object[] args = joinPoint.getArgs();
        if (args.length == 0) {
            return joinPoint.proceed();
        }
        Object dtoParam = args[0];

        String fieldName = forbidOperateSelf.idField();
        Object value = ReflectUtil.getFieldValue(dtoParam, fieldName);

        // 兼容 Long 单个ID 和 Long[] 数组ID
        if (value instanceof Long[]) {
            Long[] idArr = (Long[]) value;
            for (Long targetId : idArr) {
                if (loginUserId.equals(targetId)) {
                    throw new BusinessException(500, forbidOperateSelf.message());
                }
            }
        } else if (value instanceof Long) {
            Long targetId = (Long) value;
            if (loginUserId.equals(targetId)) {
                throw new BusinessException(500, forbidOperateSelf.message());
            }
        }

        return joinPoint.proceed();
    }
}