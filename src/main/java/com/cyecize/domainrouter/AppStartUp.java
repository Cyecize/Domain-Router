package com.cyecize.domainrouter;

import com.cyecize.ioc.MagicInjector;
import com.cyecize.ioc.annotations.Service;
import com.cyecize.ioc.annotations.StartUp;

@Service
public class AppStartUp {
    public static void main(String[] args) throws Exception {
        MagicInjector.run(AppStartUp.class);
    }

    @StartUp
    public void startUp() {
        System.out.println("app ran fine");
    }
}
