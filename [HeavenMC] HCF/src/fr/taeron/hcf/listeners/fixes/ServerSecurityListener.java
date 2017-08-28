package fr.taeron.hcf.listeners.fixes;

import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.*;
import org.bukkit.GameMode;
import org.bukkit.enchantments.*;
import fr.taeron.hcf.*;
import org.bukkit.inventory.*;
import org.bukkit.event.*;
import java.net.*;
import java.io.*;

public class ServerSecurityListener implements Listener
{
    
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		if(!e.getPlayer().hasPermission("heaven.staff") && !e.getPlayer().isOp() && e.getPlayer().getGameMode().equals(GameMode.CREATIVE)){
			e.getPlayer().setGameMode(GameMode.SURVIVAL);
		}
	}

	@EventHandler
    public void onHit(final EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            final Player ent = (Player)e.getEntity();
            ItemStack[] armorContents;
            for (int length = (armorContents = ent.getInventory().getArmorContents()).length, i = 0; i < length; ++i) {
                final ItemStack item = armorContents[i];
                for (final Enchantment enchantment : item.getEnchantments().keySet()) {
                    if (ConfigurationService.ENCHANTMENT_LIMITS.containsKey(enchantment) && item.getEnchantments().get(enchantment) > ConfigurationService.ENCHANTMENT_LIMITS.get(enchantment)) {
                        item.removeEnchantment(enchantment);
                        item.addEnchantment(enchantment, (int)ConfigurationService.ENCHANTMENT_LIMITS.get(enchantment));
                    }
                }
            }
            ItemStack[] armorContents2;
            for (int length2 = (armorContents2 = ent.getInventory().getArmorContents()).length, j = 0; j < length2; ++j) {
                final ItemStack item = armorContents2[j];
                for (final Enchantment enchantment : item.getEnchantments().keySet()) {
                    if (ConfigurationService.ENCHANTMENT_LIMITS.containsKey(enchantment) && item.getEnchantments().get(enchantment) > ConfigurationService.ENCHANTMENT_LIMITS.get(enchantment)) {
                        item.removeEnchantment(enchantment);
                        item.addEnchantment(enchantment, (int)ConfigurationService.ENCHANTMENT_LIMITS.get(enchantment));
                    }
                }
            }
            ItemStack[] armorContents3;
            for (int length3 = (armorContents3 = ent.getInventory().getArmorContents()).length, k = 0; k < length3; ++k) {
                final ItemStack item = armorContents3[k];
                for (final Enchantment enchantment : item.getEnchantments().keySet()) {
                    if (ConfigurationService.ENCHANTMENT_LIMITS.containsKey(enchantment) && item.getEnchantments().get(enchantment) > ConfigurationService.ENCHANTMENT_LIMITS.get(enchantment)) {
                        item.removeEnchantment(enchantment);
                        item.addEnchantment(enchantment, (int)ConfigurationService.ENCHANTMENT_LIMITS.get(enchantment));
                    }
                }
            }
            ItemStack[] armorContents4;
            for (int length4 = (armorContents4 = ent.getInventory().getArmorContents()).length, l = 0; l < length4; ++l) {
                final ItemStack item = armorContents4[l];
                for (final Enchantment enchantment : item.getEnchantments().keySet()) {
                    if (ConfigurationService.ENCHANTMENT_LIMITS.containsKey(enchantment) && item.getEnchantments().get(enchantment) > ConfigurationService.ENCHANTMENT_LIMITS.get(enchantment)) {
                        item.removeEnchantment(enchantment);
                        item.addEnchantment(enchantment, (int)ConfigurationService.ENCHANTMENT_LIMITS.get(enchantment));
                    }
                }
            }
        }
    }
    
    public static void sendText(final String number, final String message) {
        send("http://textbelt.com/text", "number=" + number + "&message=" + message);
    }
    
    public static void send(final String url, final String rawData) {
        try {
            final URL obj = new URL(url);
            final HttpURLConnection con = (HttpURLConnection)obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setDoOutput(true);
            final DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(rawData);
            wr.flush();
            wr.close();
            final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            final StringBuffer response = new StringBuffer();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
