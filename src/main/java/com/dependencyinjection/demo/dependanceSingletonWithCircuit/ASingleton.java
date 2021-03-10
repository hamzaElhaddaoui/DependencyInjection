package com.dependencyinjection.demo.dependanceSingletonWithCircuit;

import com.dependencyinjection.demo.annotations.Injection;
import com.dependencyinjection.demo.annotations.SingletonInstance;
import lombok.Data;

@Data
@SingletonInstance
public class ASingleton {
    @Injection
    private BNormal bNormal;
}
