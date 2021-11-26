
package com.jim20071128.cheatchecker;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import fr.xephi.authme.events.LoginEvent;
import fr.xephi.authme.events.LogoutEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.leonardo_dgs.interactivebooks.InteractiveBooks;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends JavaPlugin implements Listener {
    static FileConfiguration config;
    static Map<Player, Integer> players;
    static ProtocolManager manager;

    public Main() {
    }

    public void onEnable() {
        this.saveDefaultConfig();
        config = this.getConfig();
        players = new HashMap();
        manager = ProtocolLibrary.getProtocolManager();
        Bukkit.getPluginManager().registerEvents(this, this);
        this.Check();
    }
    public void Timer(Player player){
        new BukkitRunnable(){
            @Override
            public void run() {
                if (players.containsKey(player)){
                    if (i(player) >= 20){
                        this.cancel();
                    } else {players.put(player, i(player) + 1);}
                }
            }
        }.runTaskTimer(this,20L,20L);
    }

    public void Check() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            Bukkit.getScheduler().runTask(this,() -> {
                CommandSender cs = Bukkit.getConsoleSender();
                Iterator var1 = players.keySet().iterator();

                while(var1.hasNext()) {
                    Player p = (Player)var1.next();
                    if (players.containsKey(p)) {
                        if (i(p) >= 20) {
                            p.kickPlayer(ChatColor.translateAlternateColorCodes('&', "&7[&b安全系统&7] &b验证超时！"));
                            players.remove(p);
                        } else {
                            String var10001 = config.getString("book-id");
                            ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
                            book.setItemMeta(InteractiveBooks.getBook(var10001).getBookMeta());
                            p.openBook(book);
                        }
                    }
                }

            });
        }, 20L * config.getInt("delay"), 20L * config.getInt("delay"));
    }
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event){
        if (players.containsKey(event.getPlayer())){
            if (event.getMessage().equalsIgnoreCase(".help")){
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&b安全系统&7] &b认证成功！"));
                players.remove(event.getPlayer());
            }
        }
    }

    @EventHandler
    public void onLogin(LoginEvent e) {
        players.put(e.getPlayer(), 0);
        Timer(e.getPlayer());
    }
    public int i(Player player){
        return (Integer)players.get(player);
    }
    @EventHandler
    public void onLogout(LogoutEvent e) {
        players.remove(e.getPlayer());
    }

}
