package nl.jqno.equalsverifier.internal.reflection.annotations;

import java.util.HashMap;
import java.util.Map;

public class AnnotationCache {
    private final Map<Class<?>, AnnotationClassCache> cache = new HashMap<>();
    private final AnnotationAccessor accessor;

    public AnnotationCache(AnnotationAccessor accessor) {
        this.accessor = accessor;
    }

    public boolean hasClassAnnotation(Class<?> type, Annotation annotation) {
        ensureEntry(type);
        return cache.get(type).hasClassAnnotation(annotation);
    }

    public boolean hasField(Class<?> type, String fieldName) {
        ensureEntry(type);
        return cache.get(type).hasField(fieldName);
    }

    public boolean hasFieldAnnotation(Class<?> type, String fieldName, Annotation annotation) {
        ensureEntry(type);
        return cache.get(type).hasFieldAnnotation(fieldName, annotation);
    }

    private void ensureEntry(Class<?> type) {
        if (!cache.containsKey(type)) {
            AnnotationClassCache c = accessor.analyse(type);
            cache.put(type, c);
        }
    }
}
