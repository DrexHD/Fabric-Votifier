package me.drex.votifier;

import me.drex.votifier.config.VotifierConfig;
import me.drex.votifier.rsa.RSAIO;
import me.drex.votifier.rsa.RSAKeygen;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.security.KeyPair;

public class Votifier implements DedicatedServerModInitializer {
    private static final Logger LOGGER = LogManager.getLogger("Votifier");

    private static final Path path = new File(System.getProperty("user.dir")).toPath().resolve("votifier");
    private static Votifier instance;
    private final MinecraftServer server;
    private String version;
    private VoteReceiver voteReceiver;
    private KeyPair keyPair;
    private String address;


    public Votifier(MinecraftServer server) {
        Votifier.instance = this;
        this.server = server;
        this.address = server.getServerIp() == null ? "0.0.0.0" : server.getServerIp();
        VotifierConfig.load();
        start();
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

    private void start() {
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
            return;
        }

        // Initialize the VoteReceiver.
        int port = VotifierConfig.main().port;

        try {
            voteReceiver = new VoteReceiver(this, address, port);
            voteReceiver.start();

            LOGGER.info("Votifier enabled.");
        } catch (Exception e) {
            LOGGER.error(e);
        }

    }


    public void stop() {
        // Interrupt the vote receiver.
        if (voteReceiver != null) {
            voteReceiver.shutdown();
        }
        LOGGER.info("Votifier disabled.");
    }

    public String getVersion() {
        return version;
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
