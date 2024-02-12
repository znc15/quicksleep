package com.littlesheep.quicksleep;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Main extends JavaPlugin implements Listener {
    private FileConfiguration messagesConfig;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        saveDefaultConfig();
        loadMessagesConfig();

        // 输出启动信息
        getLogger().info("==========================================");
        getLogger().info(getDescription().getName());
        getLogger().info("Version/版本: " + getDescription().getVersion());
        getLogger().info("Author/作者: " + String.join(", ", getDescription().getAuthors()));
        getLogger().info("QQ Group/QQ群: 690216634");
        getLogger().info("Github: https://github.com/znc15/quicksleep");
        getLogger().info(getDescription().getName() + " 已启用！");
        getLogger().info("❛‿˂̵✧");
        getLogger().info("==========================================");
    }

    private void loadMessagesConfig() {
        File messagesFile = new File(getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    private String getMessage(String key) {
        return messagesConfig.getString(key, "Message not found: " + key);
    }

    private int calculateRequiredSleepers() {
        FileConfiguration config = getConfig();
        int mode = config.getInt("mode", 1);
        if (mode == 2) {
            return config.getInt("fixed-amount", 1);
        } else {
            double percentage = config.getDouble("sleep-percentage", 0.5);
            return (int) Math.ceil(Bukkit.getServer().getOnlinePlayers().size() * percentage);
        }
    }

    @EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        if (event.getBedEnterResult() == PlayerBedEnterEvent.BedEnterResult.OK) {
            int requiredSleepers = calculateRequiredSleepers();
            long currentSleepers = Bukkit.getServer().getOnlinePlayers().stream()
                    .filter(player -> player.isSleeping()).count();
            if (currentSleepers + 1 >= requiredSleepers) {
                for (World world : Bukkit.getServer().getWorlds()) {
                    if (world.getEnvironment() == World.Environment.NORMAL) {
                        world.setTime(1000);
                        world.setStorm(false);
                        world.setThundering(false);
                        Bukkit.getServer().broadcastMessage(getMessage("night-skipped"));
                    }
                }
            }
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("==========================================");
        getLogger().info("Goodbye! 插件已关闭。");
        getLogger().info("==========================================");
    }
}
