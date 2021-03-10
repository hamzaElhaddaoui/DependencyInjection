package com.dependencyinjection.demo.dependanceAbstraite;

import com.dependencyinjection.demo.annotations.Injection;
import lombok.Data;

@Data
public class ARelating {
    @Injection
    private BRelatedAbtract bRelatedAbtract;
}
