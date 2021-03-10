package com.dependencyinjection.demo.dependanceAbstraite;

import com.dependencyinjection.demo.annotations.Injection;
import lombok.Data;

@Data
public abstract class BRelatedAbtract {
    @Injection
    private CRelatedAbstractService cRelatedAbstractService;
}
