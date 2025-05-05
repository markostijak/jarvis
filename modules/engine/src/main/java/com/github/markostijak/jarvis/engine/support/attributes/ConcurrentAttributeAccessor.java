package com.github.markostijak.jarvis.engine.support.attributes;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@SuppressWarnings("NullableProblems")
public class ConcurrentAttributeAccessor implements AttributeAccessor {

    protected final Map<String, Object> attributes = new ConcurrentHashMap<>(4);

    @Override
    public void setAttribute(String name, @Nullable Object value) {
        Assert.notNull(name, "Name must not be null");
        synchronized (this.attributes) {
            if (value != null) {
                this.attributes.put(name, value);
            } else {
                this.attributes.remove(name);
            }
        }
    }

    @Override
    @Nullable
    public Object getAttribute(String name) {
        Assert.notNull(name, "Name must not be null");
        return this.attributes.get(name);
    }

    @Override
    @Nullable
    public Object removeAttribute(String name) {
        Assert.notNull(name, "Name must not be null");
        return this.attributes.remove(name);
    }

    @Override
    public boolean hasAttribute(String name) {
        Assert.notNull(name, "Name must not be null");
        return this.attributes.containsKey(name);
    }

    @Override
    public String[] attributeNames() {
        synchronized (this.attributes) {
            return StringUtils.toStringArray(this.attributes.keySet());
        }
    }

}
