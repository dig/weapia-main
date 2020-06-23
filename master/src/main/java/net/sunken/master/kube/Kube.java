package net.sunken.master.kube;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.NonNull;
import lombok.extern.java.Log;
import net.sunken.common.config.InjectConfig;
import net.sunken.common.server.Game;
import net.sunken.common.server.Server;
import net.sunken.common.server.ServerHelper;
import net.sunken.master.instance.config.InstanceConfiguration;
import net.sunken.master.instance.config.InstanceGameConfiguration;

import java.io.*;
import java.util.logging.Level;

@Log
@Singleton
public class Kube {

    @Inject
    private Gson gson;
    @Inject @InjectConfig
    private KubeConfiguration kubeConfiguration;
    @Inject @InjectConfig
    private InstanceConfiguration instanceConfiguration;

    private static String KUBERNETES_API_URL = "https://kubernetes.default.svc";
    private String serviceAccountBearer;

    @Inject
    public Kube(@InjectConfig KubeConfiguration kubeConfiguration) {
        if (kubeConfiguration.isKubernetes()) {
            try {
                BufferedReader bearerBuffer = new BufferedReader(new FileReader("/var/run/secrets/kubernetes.io/serviceaccount/token"));
                serviceAccountBearer = bearerBuffer.readLine();
            } catch (FileNotFoundException e) {
                log.log(Level.SEVERE, "Unable to find bearer file", e);
            } catch (IOException e) {
                log.log(Level.SEVERE, "Unable to load bearer file", e);
            }
        }
    }

    public boolean createPod(@NonNull Server server) {
        String imageUri = getImageUri(server.getType(), server.getGame());
        Preconditions.checkNotNull(imageUri, "Failed to create pod, null image URI.");

        try {
            JsonReader reader = new JsonReader(new FileReader("/home/minecraft/templates/base-pod.json"));
            JsonObject templateObject = gson.fromJson(reader, JsonObject.class);

            templateObject.getAsJsonObject("metadata").addProperty("name", server.getId());

            JsonArray containerArray = templateObject.getAsJsonObject("spec").getAsJsonArray("containers");
            for (int i = 0; i < containerArray.size(); i++) {
                JsonObject containerObject = containerArray.get(i).getAsJsonObject();

                containerObject.addProperty("name", "container" + i);
                containerObject.addProperty("image", imageUri);

                JsonArray envVariables = containerObject.getAsJsonArray("env");
                for (int i2 = 0; i2 < envVariables.size(); i2++) {
                    JsonObject currentObject = envVariables.get(i2).getAsJsonObject();

                    switch (currentObject.get("name").getAsString()) {
                        case "SERVER_ID":
                            currentObject.addProperty("value", server.getId());
                            break;
                        case "SERVER_TYPE":
                            currentObject.addProperty("value", server.getType().toString());
                            break;
                        case "SERVER_GAME":
                            currentObject.addProperty("value", server.getGame().toString());
                            break;
                        case "SERVER_WORLD":
                            currentObject.addProperty("value", server.getWorld().toString());
                            break;
                        case "MAXPLAYERS":
                            currentObject.addProperty("value", String.valueOf(server.getMaxPlayers()));
                            break;
                    }
                }

                for (String metadataKey : ServerHelper.SERVER_METADATA_KEYS) {
                    if (server.getMetadata().containsKey(metadataKey)) {
                        String kubeKey = metadataKey.toUpperCase().replace("-", "_");

                        JsonObject metadataObject = new JsonObject();
                        metadataObject.addProperty("name", kubeKey);
                        metadataObject.addProperty("value", server.getMetadata().get(metadataKey));
                        envVariables.add(metadataObject);
                    }
                }
            }

            HttpResponse<String> response = Unirest.post(String.format("%s/api/v1/namespaces/default/pods", KUBERNETES_API_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", String.format("Bearer %s", serviceAccountBearer))
                    .body(gson.toJson(templateObject))
                    .asString();

            if (response.getStatus() != 201) {
                log.severe(String.format("Unable to create new pod. (%s, %s, %s)", server.getId(), response.getStatus(), response.getBody()));
                return false;
            }

            log.info(String.format("Created new pod. (%s, %s)", server.getId(), response.getStatus()));
        } catch (UnirestException | FileNotFoundException ex) {
            log.log(Level.SEVERE, "Unable to create pod", ex);
            return false;
        }

        return true;
    }

    public boolean deletePod(@NonNull String podId) {
        try {
            HttpResponse<String> response = Unirest.delete(String.format("%s/api/v1/namespaces/default/pods/%s", KUBERNETES_API_URL, podId))
                    .header("Authorization", String.format("Bearer %s", serviceAccountBearer))
                    .asString();

            if (response.getStatus() != 200) {
                log.severe(String.format("Unable to delete pod. (%s, %s, %s)", podId, response.getStatus(), response.getBody()));
                return false;
            }

            log.info(String.format("Deleted pod. (%s, %s)", podId, response.getStatus()));
        } catch (UnirestException ex) {
            log.log(Level.SEVERE, "Unable to delete pod", ex);
            return false;
        }

        return true;
    }

    private String getImageUri(@NonNull Server.Type type, @NonNull Game game) {
        InstanceGameConfiguration config;
        if (instanceConfiguration.getTypes().containsKey(type)) {
            config = instanceConfiguration.getTypes().get(type);
        } else {
            config = instanceConfiguration.getGames().get(game);
        }

        return kubeConfiguration.getBranch().equals("develop") ? config.getInfrastructure().getDevelopment() : config.getInfrastructure().getProduction();
    }
}
