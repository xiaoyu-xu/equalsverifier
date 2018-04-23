package nl.jqno.equalsverifier.internal.prefabvalues.factories;

import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class GenericContainerFactory<T> implements AbstractReflectiveGenericFactory<T> {

    private final Function<List<Object>, T> factory;

    private GenericContainerFactory(Function<List<Object>, T> factory) {
        this.factory = factory;
    }

    @SuppressWarnings("unchecked")
    public static <A, T> GenericContainerFactory<T> one(Function<A, T> f) {
        return new GenericContainerFactory<>(list -> f.apply((A)list.get(0)));
    }

    @SuppressWarnings("unchecked")
    public static <A, B, T> GenericContainerFactory<T> two(BiFunction<A, B, T> f) {
        return new GenericContainerFactory<>(list -> f.apply((A)list.get(0), (B)list.get(1)));
    }

    @Override
    public Tuple<T> createValues(TypeTag tag, PrefabValues prefabValues, LinkedHashSet<TypeTag> typeStack) {
        LinkedHashSet<TypeTag> clone = cloneWith(typeStack, tag);

        List<Object> redValues = new ArrayList<>();
        List<Object> blackValues = new ArrayList<>();

        int n = tag.getType().getTypeParameters().length;
        for (int i = 0; i < n; i++) {
            TypeTag paramTag = determineAndCacheActualTypeTag(i, tag, prefabValues, clone);

            redValues.add(prefabValues.giveRed(paramTag));
            blackValues.add(prefabValues.giveBlack(paramTag));
        }

        Object red = factory.apply(redValues);
        Object black = factory.apply(blackValues);
        Object redCopy = factory.apply(redValues);

        return Tuple.of(red, black, redCopy);
    }
}
