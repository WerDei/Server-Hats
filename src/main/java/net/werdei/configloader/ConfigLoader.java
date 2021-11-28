package net.werdei.configloader;

import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public abstract class ConfigLoader
{
    private static final GsonBuilder gsonBuilder = new GsonBuilder()
                .excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT)
                .setPrettyPrinting();

    public static void load(Class<?> configClass, String fileName)
    {
        File file = new File(FabricLoader.getInstance().getConfigDir().toFile(), fileName);
        try
        {
            FileReader reader = new FileReader(file);
            gsonBuilder.create().fromJson(reader, configClass);
            reader.close();
        }
        catch (Exception ignored) {}

    }

    public static void save(Class<?> configClass, String fileName) throws ConfigException
    {
        File file = new File(FabricLoader.getInstance().getConfigDir().toFile(), fileName);
        try
        {
            FileWriter writer = new FileWriter(file);
            writer.write(gsonBuilder.create().toJson(configClass.getConstructor().newInstance()));
            writer.close();
        }
        catch (Exception e)
        {
            throw new ConfigException("Error creating a temporary instance of class " + configClass.getName(), e);
        }
    }
}
