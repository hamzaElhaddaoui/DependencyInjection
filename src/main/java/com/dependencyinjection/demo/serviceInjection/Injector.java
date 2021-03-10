package com.dependencyinjection.demo.serviceInjection;


import com.dependencyinjection.demo.annotations.Injection;
import com.dependencyinjection.demo.annotations.SingletonInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.util.ArrayUtils;
import sun.security.util.ArrayUtil;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Injector {
    // Collection qui stock les services qui seront instancier une seul fois
    private Map<String,Object> instanciatedObject;

    // Collection pour stocker historique les dependences instancier afin d'eviter les boucles
    private Map<String, Object> dependenciesHistory;

    // Logger
    private static final Logger LOG = LoggerFactory.getLogger(Injector.class);

    public Injector(){
        this.instanciatedObject = new HashMap<>();
        this.dependenciesHistory = new HashMap<>();
    }


    /**
     * Cette methode permet d'enregistrer une dependance entre deux classe
     * @param classePere
     * @param classeFils
     */
    public void register(Class<?> classePere, Class<?> classeFils) throws NoCompatibleTypesClasses, CircuitDependenceException{
        // verification si la classeFils est vraiment class fils
        LOG.debug("Debut de la fonction register de la class injector");
        if(classePere.isAssignableFrom(classeFils)){
            try {
                // instanciation
                Object instance = classeFils.getDeclaredConstructor().newInstance();
                // l'ajoute des dependances
                loadDependencies(instance, classeFils);
                // enregistrement du reference
                this.instanciatedObject.put(classePere.getName(),instance);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }else
            throw new NoCompatibleTypesClasses("La class "+classePere.getName()+" n'est pas une super classe de "+classeFils.getName());
        LOG.debug("Fin de la fonction register de la class injector");
    }

    /**
     * Cette methode permet de cree une instance de la class passee en parametre
     * @param tClass
     * @return
     */
    public Object getInstance(Class<?> tClass) throws CircuitDependenceException {
        LOG.debug("Debut de la methode getInstance de la class injector");

        if (!this.instanciatedObject.containsKey(tClass.getName())) {
            try {
                // instanciation Objet
                Object obj = tClass.getDeclaredConstructor().newInstance();
                // voir si c'est un singleton
                if (tClass.isAnnotationPresent(SingletonInstance.class)) {
                    this.instanciatedObject.put(tClass.getName(), obj);
                }else{ // sinon
                    this.dependenciesHistory.put(tClass.getName(),obj);
                }
                this.loadDependencies(obj, tClass);
                this.dependenciesHistory = new HashMap<>();
                LOG.debug("Debut de la methode getInstance de la class injector");
                return obj;
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } else{
            LOG.debug("Debut de la methode getInstance de la class injector");
            return this.instanciatedObject.get(tClass.getName());
        }
        LOG.debug("Debut de la methode getInstance de la class injector");
        return null;
    }

    /**
     * Permet de charger les dependences d'une classe
     * @param instance
     * @param classeFils
     * @throws CircuitDependenceException
     */
    private void loadDependencies(Object instance, Class<?> classeFils) throws CircuitDependenceException {
        List<Field> fields = getAllFiels(classeFils);

        for (Field attribut : fields) {
            if (attribut.isAnnotationPresent(Injection.class)) {
                // Injection trouvé
                // Verifier si l'objet est deja instancier
                if (this.instanciatedObject.containsKey(attribut.getType().getName())) {
                    attribut.setAccessible(true);
                    try {
                        attribut.set(instance, this.instanciatedObject.get(attribut.getType().getName()));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                } else {
                    // si l'objet n'est pas encore instancier
                    // si la class conteint annotation SingletonInstance
                    if (attribut.getType().isAnnotationPresent(SingletonInstance.class)) {
                        // voir si la class est instanciable CAD class concrete
                        //TODO test class concrete
                        // si le cas on verifier si la class est deja instancier
                        try {
                            Object obj = attribut.getType().getDeclaredConstructor().newInstance();
                            // l'enregistrer
                            this.instanciatedObject.put(attribut.getType().getName(), obj);
                            // charger ces dependances
                            loadDependencies(obj, obj.getClass());
                            // referencer l'attribut
                            attribut.setAccessible(true);
                            attribut.set(instance, obj);
                        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    } else { // objet normal
                        // verifier si un objet de meme type est deja instancier pour eviter les boucles
                        Object declared = this.dependenciesHistory.get(attribut.getType().getName());
                        if (declared == null) {// un objet n'est pas encore instancier
                            try {
                                Object obj = attribut.getType().getDeclaredConstructor().newInstance();
                                attribut.setAccessible(true);
                                attribut.set(instance, obj);
                                this.dependenciesHistory.put(attribut.getType().getName(), obj);
                                this.loadDependencies(obj, obj.getClass());
                            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        } else {
                            throw new CircuitDependenceException("Un circuit de dépendence est detecte");
                        }
                    }

                }
            }
        }
    }

    private List<Field> getAllFiels(Class<?> tClass){
        List<Field> fields = new ArrayList<>();
        fields.addAll(Arrays.asList(tClass.getDeclaredFields()));
        while(tClass.getSuperclass()!=null){
            tClass = tClass.getSuperclass();
            fields.addAll(Arrays.asList(tClass.getDeclaredFields()));
        }
        return fields;
    }

    public class NoCompatibleTypesClasses extends Exception{
        NoCompatibleTypesClasses(String s){
            super(s);
        }
    }

    public class CircuitDependenceException extends Exception{
        CircuitDependenceException(String s){super(s);}
    }

}
