package com.instantor.props;

import java.io.File;

public interface PropsResolver {
    public PropsResolver loadResolver(final String key);
    public PropsLoader resolve(final String key);

    public File toFile();
    public String toPath();
}
