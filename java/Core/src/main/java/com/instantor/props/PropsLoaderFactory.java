package com.instantor.props;

import java.io.File;
import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;

public class PropsLoaderFactory {
    private final Logger logger;
    private final String propsHome;

    private PropsLoaderFactory(final Logger logger) {
        this.logger = logger;
        propsHome = resolvePropsHome();
    }

    public static PropsLoaderFactory init(final Logger logger) {
        logger.trace("Initializing PropsLoaderFactory ...");
        return new PropsLoaderFactory(logger);
    }

    private String resolvePropsHome() {
        final String userHome = resolveProperty("user.home");
        return new File(userHome, ".props").getAbsolutePath();
    }

    private String resolveProperty(final String key) {
        final String value = System.getProperty(key);
        logger.trace("Resolving system property \"{}\": {}", key, value);
        if (value == null) throw new IllegalArgumentException("System property \"" + key + "\" was undefined!");
        return value;
    }

    public PropsResolver loadPure(final String projectName) {
        return loadBranch(projectName, null);
    }

    public PropsResolver loadBranch(final String projectName) {
        return loadBranch(projectName, projectName);
    }

    private final Map<Map.Entry<String, String>, PropsResolver> resolverCache = new LinkedHashMap<>();

    public PropsResolver loadBranch(final String projectName, final String branch) {
        final Map.Entry<String, String> projectBranch =
                new AbstractMap.SimpleEntry<>(projectName, branch);

        synchronized (resolverCache) {
            final PropsResolver cachedResolver = resolverCache.get(projectBranch);
            if (cachedResolver != null) return cachedResolver;

            final File file = new File(propsHome, branch != null
                    ? projectName + "_" + resolveProperty(branch + ".branch")
                    : projectName);

            logger.debug("Resolved path for _: {}", file);
            final PropsLoader propsResolver = new PropsLoaderImpl(logger, propsHome, new File(file, "_"));

            // Eagerly try to resolve all references, and fail early.
            for (final Map.Entry<String, String> entry : propsResolver) {
                final String key = entry.getKey();
                final String value = entry.getValue();

                try {
                    final boolean isResolver = PropsLoaderImpl.isResolver(value);
                    logger.trace("Is resolver = " + isResolver);

                    if (isResolver) {
                        final PropsResolver resolvedProps = propsResolver.loadResolver(key);
                        logger.info("  {} = {}", key, resolvedProps.toPath());
                    } else {
                        final PropsLoader loadedProps = propsResolver.resolve(key);
                        loadedProps.toByteArray();
                        logger.info("  {} = {}", key, loadedProps.toPath());
                    }
                } catch (final Exception e) {
                    throw new RuntimeException(String.format(
                            "Could not resolve key \"%s\" with value \"%s\" from config file %s",
                            key, value, propsResolver.toPath()), e);
                }
            }

            resolverCache.put(projectBranch, propsResolver);
            return propsResolver;
        }
    }
}
