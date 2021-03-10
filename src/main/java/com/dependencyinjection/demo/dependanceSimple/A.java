package com.dependencyinjection.demo.dependanceSimple;

import com.dependencyinjection.demo.annotations.Injection;
import lombok.Data;

@Data
public class A {
    @Injection
    private B b;
}
