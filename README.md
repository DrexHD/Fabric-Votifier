#Fabric Votifier
Fabric Votifier is a minecraft fabric mod that allows server owners to reward players for voting!
Original source: https://github.com/vexsoftware/votifier

##Setup
1. Download the jar from release tab or [build](#how-to-build) it.
2. Put the jar in your `/mods` folder
3. Start the server to generate config files
4. Change the config at `/votifier/config.yaml` to meet your needs (make sure the port is available and open)
5. `/reload` or restart your server for the changes to take place
6. Setup vote sites (public key can be found at `/votifier/public.key`)

##How to build
1. Edit build.gradle and mod.json to suit your needs.
2. Run the following command:
```
./gradlew build
```
3. Grab the generated Jar from `/build/libs/` 
