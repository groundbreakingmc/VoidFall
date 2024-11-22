package groundbreakingmc.voidfall.utils;

import lombok.Getter;
import lombok.experimental.Accessors;
import groundbreakingmc.voidfall.VoidFall;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public final class UpdatesChecker implements Listener {

    private final VoidFall plugin;

    @Getter
    @Accessors(fluent = true)
    private static boolean hasUpdate = false;
    private static String downloadLink = null;

    public UpdatesChecker(final VoidFall plugin) {
        this.plugin = plugin;
    }

    public void check(final boolean downloadUpdate, final boolean commandCall) {
        final HttpClient httpClient = HttpClient.newHttpClient();
        final HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://raw.githubusercontent.com/groundbreakingmc/VoidFall/main/version"))
                .build();

        final CompletableFuture<HttpResponse<String>> responseFuture = httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());

        responseFuture.thenAccept(response -> {
            if (response.statusCode() == 200) {

                final String[] body = response.body().split("\n", 2);
                final String[] versionInfo = body[0].split("->");

                if (hasUpdate = this.isHigher(versionInfo[0])) {
                    if (!commandCall) {
                        this.logUpdate(body[1].split("\n"), versionInfo[0], downloadUpdate);
                    }

                    if (downloadUpdate && (downloadLink = versionInfo[1]) != null) {
                        this.downloadJar(false);
                    }

                    return;
                }

                this.plugin.getMyLogger().info("\u001b[92mNo updates were found!\u001b[0m");
            } else {
                this.plugin.getMyLogger().warning("\u001b[31mCheck was canceled with response code: \u001b[91m" + response.statusCode() + "\u001b[31m.\u001b[0m");
                this.plugin.getMyLogger().warning("\u001b[31mPlease create an issue \u001b[94https://github.com/groundbreakingmc/VoidFall/issues \u001b[31mand report this error.\u001b[0m");
            }
        }).join();
    }

    private boolean isHigher(final String newVersion) {
        final String pluginVersion = this.plugin.getDescription().getVersion();
        final int currentVersionNum = Integer.parseInt(pluginVersion.replace(".", ""));
        final int newVersionNum = Integer.parseInt(newVersion.replace(".", ""));
        return currentVersionNum < newVersionNum;
    }

    public void downloadJar(final boolean commandCall) {
        if (downloadLink == null) {
            this.check(true, commandCall);
            return;
        } else if (downloadLink.isEmpty()) {
            this.plugin.getMyLogger().warning("\u001b[31mDownload link for new version of the plugin is empty!.\u001b[0m");
            this.plugin.getMyLogger().warning("\u001b[31mPlease create an issue at \u001b[94https://github.com/groundbreakingmc/VoidFall/issues \u001b[31mand report this error.\u001b[0m");
            return;
        }

        try {
            final File updateFolder = Bukkit.getUpdateFolderFile();
            final String jarFileName = new File(plugin.getClass().getProtectionDomain().getCodeSource().getLocation().getPath()).getName();
            final File outputFile = new File(updateFolder, jarFileName);

            final HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(30))
                    .followRedirects(HttpClient.Redirect.ALWAYS)
                    .build();

            final HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(downloadLink))
                    .timeout(Duration.ofMinutes(2))
                    .build();

            final HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() == 200) {
                final long totalSize = response.headers().firstValueAsLong("Content-Length").orElse(-1);
                try (final InputStream in = response.body();
                        final FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
                    final byte[] dataBuffer = new byte[1024];
                    int bytesRead;
                    int downloaded = 0;

                    while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                        fileOutputStream.write(dataBuffer, 0, bytesRead);
                        downloaded += bytesRead;
                        if (totalSize > 0) {
                            int progress = (int) ((downloaded / (double) totalSize) * 100);
                            if (progress % 10 == 0) {
                                this.plugin.getMyLogger().info("Downloaded: " + (downloaded / 1024) + " / " + (totalSize / 1024) + " KB (" + progress + "%)");
                            }
                        }
                    }

                    this.plugin.getMyLogger().info("Update downloaded successfully.");
                }
            } else {
                this.plugin.getMyLogger().warning("\u001b[31mJar downloading was canceled with response code: \u001b[91m" + response.statusCode() + "\u001b[31m.\u001b[0m");
                this.plugin.getMyLogger().warning("\u001b[31mPlease create an issue at \u001b[94https://github.com/groundbreakingmc/VoidFall/issues \u001b[31mand report this error.\u001b[0m");
            }
        } catch (final IOException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    private void logUpdate(final String[] body, final String newVersion, final boolean isAutoUpdateEnabled) {
        final StringBuilder stringBuilder = new StringBuilder("[UPDATE] ╓\n[UPDATE] ╠ New version found - v" + newVersion + "\n[UPDATE] ╚╗");

        for (int i = 0; i < body.length; i++) {
            stringBuilder.append(" ╏ ").append(body[i]).append('\n');
        }

        if (!isAutoUpdateEnabled) {
            stringBuilder.append("[UPDATE] ╔╝\n[UPDATE] ╠ Use 'voidfall update' to download update\n[UPDATE] ╙");
        } else {
            stringBuilder.append("[UPDATE] ─╜");
        }

        this.plugin.getMyLogger().info(stringBuilder.toString());
    }
}