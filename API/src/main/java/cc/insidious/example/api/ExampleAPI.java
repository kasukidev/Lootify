package cc.insidious.example.api;

import cc.insidious.example.api.data.ILoadable;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class ExampleAPI {

    @Getter
    private static ExampleAPI instance;

    private Logger logger;

    private final Map<Class<?>, Object> moduleMap = new ConcurrentHashMap<>();

    public ExampleAPI() {
        instance = this;
        this.logger = Bukkit.getLogger();
    }

    public <T> void register(Class<T> type, T module) {
        Objects.requireNonNull(type, "type cannot be null");
        Objects.requireNonNull(module, "module cannot be null");
        if (!type.isInstance(module)) {
            throw new IllegalArgumentException(module.getClass().getName() + " does not implement " + type.getName());
        }

        // swap first (no side-effects under CHM locks)
        Object old = moduleMap.put(type, module);
        logger.info("Registered " + type.getSimpleName() + " -> " + module.getClass().getSimpleName());

        if (module instanceof ILoadable) {
            ILoadable loadableNew = (ILoadable) module;

            try {
                loadableNew.load();
            } catch (Throwable throwable) {
                if (old == null) {
                    moduleMap.remove(type, module);
                }
                else  {
                    moduleMap.replace(type, module, old);
                }
                logger.log(java.util.logging.Level.SEVERE, "load failed for " + type.getSimpleName(), throwable);
                throw throwable;
            }
        }

        // unload old after new is published
        if (old instanceof ILoadable) {
            ILoadable loadableOld = (ILoadable) old;
            try {
                loadableOld.unload();
            } catch (Throwable t) {
                logger.log(java.util.logging.Level.SEVERE, "unload failed for " + type.getSimpleName(), t);
            }
        }
    }

    public <T> T get(Class<T> type) {
        Object object = this.moduleMap.get(type);
        if (object == null) {
            throw new IllegalStateException(type.getSimpleName() + " not initialized.");
        }
        return type.cast(object);
    }

    public <T> java.util.Optional<T> maybe(Class<T> type) {
        return java.util.Optional.ofNullable(type.cast(this.moduleMap.get(type)));
    }

    public <T> void unregister(Class<T> type) {
        Object old = this.moduleMap.remove(type);
        if (old instanceof ILoadable) {
            ILoadable loadable = (ILoadable) old;
            safeUnload(loadable);
        }
    }

    public void shutdown() {
        this.moduleMap.values().forEach(object -> {
            if (object instanceof ILoadable) {
                ILoadable loadable = (ILoadable) object;
                safeUnload(loadable);
            }
        });
        this.moduleMap.clear();
    }

    private void safeLoad(ILoadable loadable) {
        try {
            loadable.load();
        } catch (Throwable throwable) {
            logger.severe("load failed: " + throwable);
        }
    }

    private void safeUnload(ILoadable loadable) {
        try {
            loadable.unload();
        } catch (Throwable throwable) {
            logger.severe("unload failed: " + throwable);
        }
    }
}
