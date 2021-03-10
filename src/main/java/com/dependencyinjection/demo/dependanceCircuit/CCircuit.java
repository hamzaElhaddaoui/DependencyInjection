package com.dependencyinjection.demo.dependanceCircuit;

import com.dependencyinjection.demo.annotations.Injection;
import lombok.Data;

@Data
public class CCircuit {
    @Injection
    private ACircuit aCircuit;
}
