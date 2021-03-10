package com.dependencyinjection.demo.dependanceSingletonInstance;

import com.dependencyinjection.demo.annotations.Injection;
import lombok.Data;

@Data
public class Y {
    @Injection
    private X x;
}
