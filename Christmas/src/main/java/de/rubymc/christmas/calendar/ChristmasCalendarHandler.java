package de.rubymc.christmas.calendar;

import de.rubymc.christmas.Main;
import de.rubymc.christmas.utils.ItemStackUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ChristmasCalendarHandler implements Listener {

    @EventHandler
    public void clickChristmasCalendarInventory(InventoryClickEvent e) {
        if (e.getCurrentItem() != null) {
            if (e.getClickedInventory() != null) {
                Player player = (Player)e.getWhoClicked();
                if (e.getView().getTitle().equals(Main.getPlugin().getConfiguration().getMessage("InventoryName"))) {
                    e.setCancelled(true);
                    if (e.getView().getTitle().equals(Main.getPlugin().getConfiguration().getMessage("InventoryName"))) {
                        int slotId = e.getSlot();
                        boolean clickableSlot = Main.getPlugin().getConfiguration().isClickable(slotId);
                        if (clickableSlot) {
                            boolean marked = Main.getPlugin().getConfiguration().markClicked(player, slotId);
                            if (marked) {
                                player.sendMessage(Main.getPlugin().getConfiguration().getMessage("Claimed").replace("%day%", ChatColor.stripColor(Main.getPlugin().getConfiguration().getInventory().getItem(slotId).getItemMeta().getDisplayName())));
                                Main.getPlugin().getConfiguration().playEffects(player, slotId);
                            } else {
                                player.sendMessage(Main.getPlugin().getConfiguration().getMessage("AlreadyClaimed").replace("%day%", ChatColor.stripColor(Main.getPlugin().getConfiguration().getInventory().getItem(slotId).getItemMeta().getDisplayName())));
                            }
                        } else {
                            if (!Main.getPlugin().getConfiguration().isDecember()) {
                                player.sendMessage(Main.getPlugin().getConfiguration().getMessage("NotDecember"));
                                return;
                            }

                            if (Main.getPlugin().getConfiguration().isCorrectSlot(slotId)) {
                                if (Main.getPlugin().getConfiguration().hasClicked(player, slotId)) {
                                    player.sendMessage(Main.getPlugin().getConfiguration().getMessage("AlreadyClaimed").replace("%day%", ChatColor.stripColor(Main.getPlugin().getConfiguration().getInventory().getItem(slotId).getItemMeta().getDisplayName())));
                                } else {
                                    player.sendMessage(Main.getPlugin().getConfiguration().getMessage("NotClaimable"));
                                }
                            }
                        }
                    }
                }

            }
        }
    }

    private boolean equals(ItemStack first, ItemStack second) {
        if (first != null && second != null && first.getType().equals(second.getType()) && first.hasItemMeta() && second.hasItemMeta() && first.getItemMeta().hasDisplayName() && second.getItemMeta().hasDisplayName() && first.getItemMeta().getDisplayName().equals(second.getItemMeta().getDisplayName())) {
            String firstTexture = ItemStackUtils.getTexture(first);
            String secondTexture = ItemStackUtils.getTexture(second);
            if (firstTexture != null && secondTexture != null && firstTexture.equals(secondTexture)) {
                return true;
            }
        }

        return false;
    }
}