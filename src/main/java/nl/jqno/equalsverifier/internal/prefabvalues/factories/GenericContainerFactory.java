package nl.jqno.equalsverifier.internal.prefabvalues.factories;

import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public final class GenericContainerFactory<T> implements AbstractReflectiveGenericFactory<T> {

    private final Function<List<Object>, T> factory;
    private final Supplier<T> empty;

    private GenericContainerFactory(Function<List<Object>, T> factory, Supplier<T> empty) {
        this.factory = factory;
        this.empty = empty;
    }

    @SuppressWarnings("unchecked")
    public static <A, T> GenericContainerFactory<T> one(Function<A, T> f, Supplier<T> empty) {
        return new GenericContainerFactory<>(list -> f.apply((A)list.get(0)), empty);
    }

    @SuppressWarnings("unchecked")
    public static <A, B, T> GenericContainerFactory<T> two(BiFunction<A, B, T> f, Supplier<T> empty) {
        return new GenericContainerFactory<>(list -> f.apply((A)list.get(0), (B)list.get(1)), empty);
    }

    @Override
    public Tuple<T> createValues(TypeTag tag, PrefabValues prefabValues, LinkedHashSet<TypeTag> typeStack) {
        LinkedHashSet<TypeTag> clone = cloneWith(typeStack, tag);

        List<Object> redValues = new ArrayList<>();
        List<Object> blackValues = new ArrayList<>();

        boolean useEmpty = false;
        int n = tag.getType().getTypeParameters().length;
        for (int i = 0; i < n; i++) {
            TypeTag paramTag = determineAndCacheActualTypeTag(i, tag, prefabValues, clone);

            Object redValue = prefabValues.giveRed(paramTag);
            Object blackValue = prefabValues.giveBlack(paramTag);
            if (redValue.equals(blackValue)) { // This happens with single-element enums
                useEmpty = true;
            }
            redValues.add(redValue);
            blackValues.add(blackValue);
        }

        Object red = factory.apply(redValues);
        Object black = useEmpty && empty != null ? empty.get() : factory.apply(blackValues);
        Object redCopy = factory.apply(redValues);

        return Tuple.of(red, black, redCopy);
    }
}
