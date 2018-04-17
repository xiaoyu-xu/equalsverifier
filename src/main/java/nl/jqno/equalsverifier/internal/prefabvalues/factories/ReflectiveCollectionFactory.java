package nl.jqno.equalsverifier.internal.prefabvalues.factories;

import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.ConditionalInstantiator;

import java.util.LinkedHashSet;
import java.util.function.Supplier;

import static nl.jqno.equalsverifier.internal.reflection.Util.*;

/**
 * Implementation of {@link PrefabValueFactory} that instantiates collections
 * using reflection, while taking generics into account.
 */
public class ReflectiveCollectionFactory<T> implements AbstractReflectiveGenericFactory<T> {
    private final String typeName;
    private final Supplier<Object> createEmpty;

    /* default */ ReflectiveCollectionFactory(String typeName, Supplier<Object> createEmpty) {
        this.typeName = typeName;
        this.createEmpty = createEmpty;
    }

    public static <T> ReflectiveCollectionFactory<T> callFactoryMethod(final String typeName, final String methodName) {
        return new ReflectiveCollectionFactory<>(
            typeName,
            () -> new ConditionalInstantiator(typeName).callFactory(methodName, classes(), objects()));
    }

    public static <T> ReflectiveCollectionFactory<T> callFactoryMethodWithParameter(
            final String typeName, final String methodName, final Class<?> parameterType, final Object parameterValue) {
        return new ReflectiveCollectionFactory<T>(
            typeName,
            () -> new ConditionalInstantiator(typeName).callFactory(methodName, classes(parameterType), objects(parameterValue)));
    }

    @Override
    public Tuple<T> createValues(TypeTag tag, PrefabValues prefabValues, LinkedHashSet<TypeTag> typeStack) {
        LinkedHashSet<TypeTag> clone = cloneWith(typeStack, tag);
        TypeTag entryTag = determineAndCacheActualTypeTag(0, tag, prefabValues, clone);

        Object red = createWith(prefabValues.giveRed(entryTag));
        Object black = createWith(prefabValues.giveBlack(entryTag));
        Object redCopy = createWith(prefabValues.giveRed(entryTag));

        return Tuple.of(red, black, redCopy);
    }

    private Object createWith(Object value) {
        Object result = createEmpty.get();
        invoke(classForName(typeName), result, "add", classes(Object.class), objects(value));
        return result;
    }
}
