package com.instantor.props;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public interface PropsLoader extends PropsResolver {
    public String get(final String key);
    public int getInt(final String key);

    public Map<String, String> toMap();
    public Properties toProps();

    public byte[] toByteArray();
    public InputStream toInputStream();

    @Override
    public String toString();
    public String toString(final String encoding);
}
