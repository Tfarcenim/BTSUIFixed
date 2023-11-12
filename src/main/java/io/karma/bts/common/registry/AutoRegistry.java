package io.karma.bts.common.registry;

import com.google.common.collect.SetMultimap;
import io.karma.bts.common.BTSMod;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModClassLoader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.DataSerializerEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Backbone of the auto registry system.
 * This reads the annotation metadata for this mod
 * and registers all things marked with {@link Register} as suitable.
 *
 * @author KitsuneAlex
 * @since 23/06/2022
 */
public final class AutoRegistry {
    public static final AutoRegistry INSTANCE = new AutoRegistry();
    private static final Type ANNOTATION = Type.getType(Register.class);

    private final HashMap<Class<?>, IRegistryHandler<?>> handlers = new HashMap<>();

    private AutoRegistry() {
        addHandler(DataSerializer.class, new WrappingRegistryHandler<>(ForgeRegistries.DATA_SERIALIZERS, DataSerializerEntry::new, DataSerializerEntry::getSerializer));
        addHandler(Block.class, new SimpleRegistryHandler<>(ForgeRegistries.BLOCKS));
        addHandler(Item.class, new SimpleRegistryHandler<>(ForgeRegistries.ITEMS));
    }

    public <E> void addHandler(final @NotNull Class<E> type, final @NotNull IRegistryHandler<? extends E> handler) {
        if (handlers.containsKey(type)) {
            throw new IllegalStateException("Registry handler already exists for that type");
        }

        handlers.put(type, handler);
    }

    public void removeHandler(final @NotNull Class<?> type) {
        if (!handlers.containsKey(type)) {
            throw new IllegalStateException("Registry handler for that type doesn't exist");
        }

        handlers.remove(type);
    }

    @SuppressWarnings("unchecked")
    public <E> @Nullable IRegistryHandler<E> findHandler(final @NotNull Class<? extends E> type) {
        final Set<Entry<Class<?>, IRegistryHandler<?>>> entries = handlers.entrySet();

        for (final Entry<Class<?>, IRegistryHandler<?>> entry : entries) {
            if (!entry.getKey().isAssignableFrom(type)) {
                continue; // This is not the right thing..
            }

            return (IRegistryHandler<E>) entry.getValue();
        }

        return null;
    }

    public void preInit(final @NotNull ASMDataTable dataTable) {
        final Loader loader = Loader.instance();
        final ModClassLoader classLoader = loader.getModClassLoader();
        final ModContainer currentMod = loader.activeModContainer();
        final SetMultimap<String, ASMData> modData = dataTable.getAnnotationsFor(currentMod);
        final Set<ASMData> dataSet = modData.get(ANNOTATION.getClassName());

        for (final ASMData data : dataSet) {
            final String className = data.getClassName();
            Class<?> clazz;
            try {
                clazz = Class.forName(className, false, classLoader);
            } catch (ClassNotFoundException e) {
                clazz = null;
            }

            if (clazz == null) {
                continue; // Skip classes we can't resolve
            }

            final String objectName = data.getObjectName();
            Object instance;

            if (objectName.equals(className)) { // Register a new class instance
                BTSMod.LOGGER.info("Registering class {}", className);
                try {
                    instance =clazz.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            else { // Register an instance from a static field
                BTSMod.LOGGER.info("Registering field {}#{}", className, objectName);
                final Field field;
                try {
                    field = clazz.getDeclaredField(objectName);
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }

                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }

                try {
                    instance = field.get(null);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }

            if (instance == null) {
                throw new IllegalStateException("Could not retrieve entry instance");
            }

            final Class<?> entryType = instance.getClass();
            final IRegistryHandler<Object> handler = findHandler(entryType);

            if (handler == null) {
                BTSMod.LOGGER.warn("Ignoring registry entry {}, no handler for this type", className);
                continue;
            }

            final String entryName = (String) data.getAnnotationInfo().get("value");
            handler.register(entryName, instance);
        }

        BTSMod.LOGGER.info("Invoking early registry handlers");
        final Collection<IRegistryHandler<?>> handlers = this.handlers.values();

        for (final IRegistryHandler<?> handler : handlers) {
            handler.onPreInit();
        }
    }

    public void init() {
        BTSMod.LOGGER.info("Invoking late registry handlers");
        final Collection<IRegistryHandler<?>> handlers = this.handlers.values();

        for (final IRegistryHandler<?> handler : handlers) {
            handler.onInit();
        }
    }
}
