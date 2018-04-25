package nl.jqno.equalsverifier.internal.prefabvalues.factories;

import nl.jqno.equalsverifier.internal.reflection.ConditionalInstantiator;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Factories {
    private Factories() {
        // Do not instantiate
    }

    @SuppressWarnings("unchecked")
    public static <A, T> SimpleGenericFactory<T> arity1(Function<A, T> f, Supplier<T> empty) {
        return new SimpleGenericFactory<>(list -> f.apply((A)list.get(0)), empty);
    }

    @SuppressWarnings("unchecked")
    public static <A, B, T> SimpleGenericFactory<T> arity2(BiFunction<A, B, T> f, Supplier<T> empty) {
        return new SimpleGenericFactory<>(list -> f.apply((A)list.get(0), (B)list.get(1)), empty);
    }

    public static <A, T extends Collection<A>> SimpleGenericFactory<T> collection(Supplier<T> empty) {
        return Factories.<A, T>arity1(a -> {
            T collection = empty.get();
            collection.add(a);
            return collection;
        }, empty);
    }

    @SuppressWarnings("unchecked")
    public static <A, T extends Collection<A>> SimpleGenericFactory<T> reflectiveCollection(
        String fullyQualifiedClassName, String factoryMethod, Class<?>[] paramTypes, Object[] paramValues) {
        return collection(() -> {
            ConditionalInstantiator ci = new ConditionalInstantiator(fullyQualifiedClassName);
            return (T)ci.callFactory(factoryMethod, paramTypes, paramValues);
        });
    }
}
