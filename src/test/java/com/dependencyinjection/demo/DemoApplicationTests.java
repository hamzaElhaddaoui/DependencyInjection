package com.dependencyinjection.demo;

import com.dependencyinjection.demo.dependanceAbstraite.*;
import com.dependencyinjection.demo.dependanceCircuit.*;
import com.dependencyinjection.demo.dependanceSimple.*;
import com.dependencyinjection.demo.dependanceSingletonInstance.Y;
import com.dependencyinjection.demo.dependanceSingletonWithCircuit.*;
import com.dependencyinjection.demo.serviceInjection.Injector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoApplicationTests {

    public static Injector injector;

    @BeforeAll
    public static void initialisationInjector(){
        injector = new Injector();
    }

    @Test
    void testSimpleInjection() {
        try {
            A a = (A) injector.getInstance(A.class);
            Assertions.assertNotNull(a);
            B b = a.getB();
            Assertions.assertNotNull(b);
            C c = b.getC();
            Assertions.assertNotNull(c);
            Assertions.assertEquals("Cet objet est instancier correctement de la class CCircuit",c.instantiationConfess());
        } catch (Injector.CircuitDependenceException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testCircuitInjection(){
        Assertions.assertThrows(Injector.CircuitDependenceException.class,()-> injector.getInstance(ACircuit.class));
    }

    @Test
    void testSingletonInstanceInjection(){
        try {
            Y y1 = (Y) injector.getInstance(Y.class);
            Y y2 = (Y) injector.getInstance(Y.class);
            Assertions.assertNotNull(y1);
            Assertions.assertNotNull(y2);
            Assertions.assertEquals(y1.getX(),y2.getX());
        } catch (Injector.CircuitDependenceException e) {
            e.printStackTrace();
        }

    }

    @Test
    void testSingletonAvecCircuitInjection(){
        try {

            ASingleton a = (ASingleton) injector.getInstance(ASingleton.class);
            Assertions.assertNotNull(a);
            BNormal b = a.getBNormal();
            Assertions.assertNotNull(b);
            CNormal c = b.getCNormal();
            Assertions.assertNotNull(c);
            ASingleton a2 = c.getASingleton();
            Assertions.assertNotNull(a2);
            Assertions.assertEquals(a,a2);

            ASingleton a3 = (ASingleton) injector.getInstance(ASingleton.class);
            Assertions.assertEquals(a,a3);

        } catch (Injector.CircuitDependenceException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testDependanceAbstraite(){
        try {

            injector.register(CRelatedAbstractService.class, CRelatedConcreteServiceOne.class);
            injector.register(BRelatedAbtract.class, BRelatedConcreteTwo.class);
            ARelating aRelating = (ARelating) injector.getInstance(ARelating.class);
            Assertions.assertNotNull(aRelating);
            Assertions.assertNotNull(aRelating.getBRelatedAbtract());
            BRelatedAbtract bRelatedAbtract = aRelating.getBRelatedAbtract();
            Assertions.assertTrue(bRelatedAbtract instanceof BRelatedConcreteTwo);
            CRelatedAbstractService cRelatedAbstractService = bRelatedAbtract.getCRelatedAbstractService();
            Assertions.assertNotNull(cRelatedAbstractService);
            Assertions.assertTrue(cRelatedAbstractService instanceof CRelatedConcreteServiceOne);

            ARelating aRelating2 = (ARelating) injector.getInstance(ARelating.class);
            Assertions.assertEquals(aRelating.getBRelatedAbtract(),aRelating2.getBRelatedAbtract());

        } catch (Injector.NoCompatibleTypesClasses noCompatibleTypesClasses) {
            noCompatibleTypesClasses.printStackTrace();
        } catch (Injector.CircuitDependenceException e) {
            e.printStackTrace();
        }

    }
}
