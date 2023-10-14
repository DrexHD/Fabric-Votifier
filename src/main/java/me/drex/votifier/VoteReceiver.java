package me.drex.votifier;

import com.mojang.authlib.GameProfile;
import me.drex.votifier.config.YAMLConfig;
import me.drex.votifier.data.Vote;
import me.drex.votifier.rsa.RSA;
import net.minecraft.SharedConstants;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;

import javax.crypto.BadPaddingException;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Optional;


public class VoteReceiver extends Thread {

    private final String host;

    private final int port;

    private ServerSocket server;

    private boolean running = true;

    public VoteReceiver(String host, int port) throws Exception {
        this.host = host;
        this.port = port;
        initialize();
    }

    private void initialize() throws Exception {
        try {
            server = new ServerSocket();
            server.bind(new InetSocketAddress(host, port));
        } catch (Exception ex) {
            Votifier.getLogger().error("IP address and port are already in use", ex);
            throw new Exception(ex);
        }
    }

    /**
     * Shuts the vote receiver down cleanly.
     */
    public void shutdown() {
        running = false;
        if (server == null)
            return;
        try {
            server.close();
        } catch (Exception ex) {
            Votifier.getLogger().error("Unable to shut down vote receiver cleanly.");
        }
    }

    @Override
    public void run() {

        // Main loop.
        while (running) {
            try {
                Socket socket = server.accept();
                socket.setSoTimeout(5000); // Don't hang on slow connections.
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream()));
                InputStream in = socket.getInputStream();

                // Send them our version.
                writer.write("VOTIFIER " + Votifier.VERSION);
                writer.newLine();
                writer.flush();

                // Read the 256 byte block.
                byte[] block = new byte[256];
                in.read(block, 0, block.length);

                // Decrypt the block.
                block = RSA.decrypt(block, Votifier.getInstance().getKeyPair()
                        .getPrivate());
                int position = 0;

                // Perform the opcode check.
                String opcode = readString(block, position);
                position += opcode.length() + 1;
                if (!opcode.equals("VOTE")) {
                    // Something went wrong in RSA.
                    throw new Exception("Unable to decode RSA");
                }

                // Parse the block.
                String serviceName = readString(block, position);
                position += serviceName.length() + 1;
                String username = readString(block, position);
                position += username.length() + 1;
                String address = readString(block, position);
                position += address.length() + 1;
                String timeStamp = readString(block, position);
                position += timeStamp.length() + 1;

                // Create the vote.
                final Vote vote = new Vote(username, serviceName, timeStamp, address);

                if (YAMLConfig.debug)
                    Votifier.getLogger().info("Received vote record -> " + vote);

                MinecraftServer server = Votifier.getInstance().getServer();
                ServerCommandSource source = server.getCommandSource();
                for (String command : YAMLConfig.commands) {
                    Optional<GameProfile> optional = server.getUserCache().findByName(vote.getUsername());
                    String name = optional.isPresent() ? optional.get().getName() : vote.getUsername();
                    String result = command.replace("%PLAYER%", name)
                            .replace("%SERVICE%", vote.getServiceName())
                            .replace("%TIMESTAMP%", vote.getTimeStamp())
                            .replace("%ADDRESS%", vote.getAddress());
                    server.submitAndJoin(() -> server.getCommandManager().executeWithPrefix(source, result));
                }


                // Clean up.
                writer.close();
                in.close();
                socket.close();
            } catch (SocketException ex) {
                Votifier.getLogger().error("Protocol error. Ignoring packet - " + ex.getLocalizedMessage());
            } catch (BadPaddingException ex) {
                if (YAMLConfig.debug)
                    Votifier.getLogger().error(
                        "Unable to decrypt vote record. Make sure that that your public key matches the one you gave the server list.", ex);
            } catch (Exception ex) {
                Votifier.getLogger().error(
                        "Exception caught while receiving a vote notification",
                        ex);
            }
        }
    }

    /**
     * Reads a string from a block of data.
     *
     * @param data The data to read from
     * @return The string
     */
    private String readString(byte[] data, int offset) {
        StringBuilder builder = new StringBuilder();
        for (int i = offset; i < data.length; i++) {
            if (data[i] == '\n')
                break; // Delimiter reached.
            builder.append((char) data[i]);
        }
        return builder.toString();
    }
}

