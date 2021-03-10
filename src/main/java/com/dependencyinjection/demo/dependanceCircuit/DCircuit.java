package com.dependencyinjection.demo.dependanceCircuit;

import com.dependencyinjection.demo.annotations.Injection;
import lombok.Data;

@Data
public class DCircuit {
    @Injection
    private ACircuit aCircuit;
}
