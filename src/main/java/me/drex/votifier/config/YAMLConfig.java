package me.drex.votifier.config;

import me.drex.votifier.Votifier;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class YAMLConfig {

    public static List<String> commands = new ArrayList<>() {{
        this.add("tellraw @a [{\"text\":\"%PLAYER% voted on %SERVICE%\",\"color\":\"green\"}]");
        this.add("scoreboard players add %PLAYER% voted 1");
    }};
    public static boolean enabled = true;
    public static int port = 8192;
    public static boolean debug = false;
    private static final Yaml yaml = new Yaml();
    private static final HashMap<String, Object> data = new HashMap<>();
    private static final File CONFIG_FILE = Votifier.getPath().resolve("config.yaml").toFile();

    public static void load() {
        if (CONFIG_FILE.exists()) {
            try {
                HashMap<String, Object> config = yaml.load(new FileInputStream(CONFIG_FILE));
                enabled = (boolean) config.getOrDefault("enabled", enabled);
                port = (int) config.getOrDefault("port", port);
                commands = (List<String>) config.getOrDefault("commands", commands);
                debug = (boolean) config.getOrDefault("debug", debug);
            } catch (Exception ex) {
                Votifier.getLogger().error("Couldn't load config", ex);
            }
        } else {
            createFile();
        }

    }

    public static void createFile() {
        try {
            CONFIG_FILE.createNewFile();
            StringWriter writer = new StringWriter();
            data.put("enabled", enabled);
            data.put("port", 8192);
            data.put("commands", commands);
            data.put("debug", debug);
            yaml.dump(data, writer);
            FileOutputStream fileOutputStream = new FileOutputStream(CONFIG_FILE);
            fileOutputStream.write(writer.toString().getBytes());
        } catch (IOException ex) {
            Votifier.getLogger().error("Couldn't create config file", ex);
        }
    }

}
