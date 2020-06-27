package me.drex.votifier.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class Config {

    public static final String HEADER = "Votifier! Configuration File\n" +
            "Licensed Under the MIT License, Copyright (c) 2020\n" +
            "Votifier is using HOCON for its configuration files\n learn more about it here: " +
            "https://docs.spongepowered.org/stable/en/server/getting-started/configuration/hocon.html";

    @Setting(value = "enabled")
    public boolean enabled = true;

    @Setting(value = "port")
    public int port = 8192;

    @Setting(value = "commands", comment = "Commands, which get executed, when a vote is received, Variables: %PLAYER%, %SERVICE%, %TIMESTAMP and %ADDRESS%")
    public List<String> commands = new ArrayList<String>() {
        {
            this.add("tellraw @a [{\"text\":\"%PLAYER% voted on %SERVICE%\",\"color\":\"green\"}]");
        }
    };

}
