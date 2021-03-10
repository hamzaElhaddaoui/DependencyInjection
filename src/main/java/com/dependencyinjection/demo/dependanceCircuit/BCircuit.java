package com.dependencyinjection.demo.dependanceCircuit;

import com.dependencyinjection.demo.annotations.Injection;
import lombok.Data;

@Data
public class BCircuit {
    @Injection
    private CCircuit cCircuit;

    @Injection
    private DCircuit dCircuit;
}
