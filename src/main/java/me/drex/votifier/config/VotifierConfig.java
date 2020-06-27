package me.drex.votifier.config;

import com.google.common.reflect.TypeToken;
import joptsimple.internal.Messages;
import me.drex.votifier.Votifier;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.DefaultObjectMapperFactory;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.io.File;
import java.io.IOException;


public class VotifierConfig {
    private static Config config;
    private static Messages messages;
    private static ConfigurationNode mainNode;
    private static ConfigurationNode messagesNode;

    public static Config main() {
        return config;
    }

    public static Messages messages() {
        return messages;
    }

    public static ConfigurationNode getMainNode() {
        return mainNode;
    }

    public static ConfigurationNode getMessagesNode() {
        return messagesNode;
    }

    public static String getMessage(String key, Object... objects) {
        String msg = messagesNode.getNode((Object) key.split("\\.")).getString();
        return objects.length == 0 ? msg : msg != null ? String.format(msg, objects) : "Null<" + key + "?>";
    }

    public static void load() {
        try {
            File CONFIG_FILE = Votifier.getPath().resolve("config.hocon").toFile();
            ConfigurationLoader<CommentedConfigurationNode> mainLoader = HoconConfigurationLoader.builder()
                    .setFile(CONFIG_FILE).build();

            CONFIG_FILE.createNewFile();

            mainNode = mainLoader.load(configurationOptions());

            config = mainNode.getValue(TypeToken.of(Config.class), new Config());

            mainLoader.save(mainNode);
        } catch (IOException | ObjectMappingException e) {
            Votifier.getLogger().error("Exception handling a configuration file! " + VotifierConfig.class.getName());
            e.printStackTrace();
        }
    }

    public static ConfigurationOptions configurationOptions() {
        return ConfigurationOptions.defaults()
                .setHeader(Config.HEADER)
                .setObjectMapperFactory(DefaultObjectMapperFactory.getInstance())
                .setShouldCopyDefaults(true);
    }

}
