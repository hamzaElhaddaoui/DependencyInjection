package com.dependencyinjection.demo.dependanceSingletonWithCircuit;

import com.dependencyinjection.demo.annotations.Injection;
import lombok.Data;

@Data
public class BNormal {
    @Injection
    private CNormal cNormal;
}
