package com.sonic.website.app;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import com.sonic.website.core.common.manager.ManagerBase;
import com.sonic.website.core.common.support.LogCore;

@Service
public class SpringAwareManager extends ManagerBase implements ApplicationContextAware {
    private static ApplicationContext context = null;
    
    @Autowired
    ApplicationContext appContext;
    
    public static SpringAwareManager inst() {
        return ManagerBase.inst(SpringAwareManager.class);
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
        LogCore.BASE.info("SpringAwareManager inited!");
    }
    public ApplicationContext getApplicationContext() {
        return context;
    }
    public ApplicationContext getAppContext() {
        return appContext;
    }
    @Override
    public void init() {
    }
}
 