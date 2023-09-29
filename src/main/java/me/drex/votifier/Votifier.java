package me.drex.votifier;

import me.drex.votifier.config.YAMLConfig;
import me.drex.votifier.rsa.RSAIO;
import me.drex.votifier.rsa.RSAKeygen;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.security.KeyPair;

public class Votifier implements DedicatedServerModInitializer {
    private static final Logger LOGGER = LogManager.getLogger("Votifier");

    public static final String VERSION = FabricLoader.getInstance().getModContainer("votifier").get().getMetadata().getVersion().getFriendlyString();
    private static final Path path = new File(System.getProperty("user.dir")).toPath().resolve("votifier");
    private static Votifier instance;
    private final MinecraftServer server;
    private VoteReceiver voteReceiver;
    private KeyPair keyPair;
    private String address;


    public Votifier(MinecraftServer server) {
        Votifier.instance = this;
        this.server = server;
        this.address = server.getServerIp() == null ? "0.0.0.0" : server.getServerIp();
        loadRSA();
        YAMLConfig.load();
        if (YAMLConfig.enabled) start();
    }

    public static Votifier getInstance() {
        return instance;
    }

    public static Path getPath() {
        return path;
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    private void loadRSA() {
        File rsaDirectory = path.toFile();
        try {
            if (!rsaDirectory.exists()) {
                rsaDirectory.mkdir();
                keyPair = RSAKeygen.generate(2048);
                RSAIO.save(rsaDirectory, keyPair);
            } else {
                keyPair = RSAIO.load(rsaDirectory);
            }
        } catch (Exception ex) {
            LOGGER.error("Error reading RSA keys", ex);
        }
    }

    private void start() {
        // Initialize the VoteReceiver.
        int port = YAMLConfig.port;

        try {
            voteReceiver = new VoteReceiver(address, port);
            voteReceiver.start();

            LOGGER.info("Votifier started on {}.", port);
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    public void reload() {
        stop();
        YAMLConfig.load();
        start();
    }


    public void stop() {
        // Interrupt the vote receiver.
        if (voteReceiver != null) {
            voteReceiver.shutdown();
        }
        LOGGER.info("Votifier stopped.");
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    public MinecraftServer getServer() {
        return server;
    }

    @Override
    public void onInitializeServer() {
    }
}
