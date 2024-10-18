package tk.kanaostore.losteddev.skywars.menu.shop.perks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import tk.kanaostore.losteddev.skywars.cosmetics.Cosmetic;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticServer;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticType;
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.SkyWarsPerk;
import tk.kanaostore.losteddev.skywars.database.Database;
import tk.kanaostore.losteddev.skywars.menu.ConfigMenu;
import tk.kanaostore.losteddev.skywars.menu.ConfigMenu.ConfigAction;
import tk.kanaostore.losteddev.skywars.menu.ConfigMenu.ConfigItem;
import tk.kanaostore.losteddev.skywars.menu.api.PagedPlayerMenu;
import tk.kanaostore.losteddev.skywars.menu.shop.KitsAndPerksMenu;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;
import tk.kanaostore.losteddev.skywars.utils.StringUtils;

public class NormalPerksMenu extends PagedPlayerMenu {

  private static final ConfigMenu config = ConfigMenu.getByName("nperks");

  @EventHandler
  public void onInventoryClick(InventoryClickEvent evt) {
    if (evt.getInventory().equals(getCurrentInventory())) {
      evt.setCancelled(true);

      if (evt.getWhoClicked() instanceof Player && evt.getWhoClicked().equals(player)) {
        ItemStack item = evt.getCurrentItem();
        Account account = Database.getInstance().getAccount(player.getUniqueId());
        if (account == null) {
          player.closeInventory();
          return;
        }

        if (evt.getClickedInventory() != null && evt.getClickedInventory().equals(evt.getInventory()) && item != null && item.getType() != Material.AIR) {
          SkyWarsPerk perk = perks.get(item);
          if (evt.getSlot() == this.previousPage) {
            this.openPrevious();
          } else if (evt.getSlot() == this.nextPage) {
            this.openNext();
          } else if (perk != null) {
            if (perk.hasByPermission(player) && !perk.has(account)) {
              if (account.getInt("coins") < perk.getCoins()) {
                player.sendMessage(StringUtils.formatColors(config.getAsString("enough-coins")));
                return;
              }
              
              new ConfirmPerkMenu(player, perk, NormalPerksMenu.class);
            }
          } else {
            ConfigAction action = actions.get(item);
            if (action != null && !action.getType().equals("NOTHING")) {
              if (action.getType().equals("OPEN")) {
                String menu = action.getValue();
                if (menu.equalsIgnoreCase("shop")) {
                  new KitsAndPerksMenu(player);
                } else if (menu.equalsIgnoreCase("closeinv")) {
                  player.closeInventory();
                }
              } else {
                player.closeInventory();
                action.send(player);
              }
            }
          }
        }
      }
    }
  }

  private Map<ItemStack, SkyWarsPerk> perks;
  private Map<ItemStack, ConfigAction> actions;

  public NormalPerksMenu(Player player) {
    super(player, config.getTitle(), config.getRows());
    this.perks = new HashMap<>();
    this.actions = new HashMap<>();
    this.previousPage = 45;
    this.nextPage = 53;
    this.previousStack = config.getAsString("previous-page");
    this.nextStack = config.getAsString("next-page");
    this.onlySlots(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34);

    Account account = Database.getInstance().getAccount(player.getUniqueId());
    List<ItemStack> items = new ArrayList<>();
    for (Cosmetic c : CosmeticServer.SKYWARS.getByType(CosmeticType.SKYWARS_PERK)) {
      SkyWarsPerk perk = (SkyWarsPerk) c;
      if (perk.getMode() == 1) {
        String rarity = perk.getRarity().getName();
        boolean has = perk.has(account) && perk.hasByPermission(player);
        ItemStack icon = null;
        if (!has) {
          List<String> lore = new ArrayList<>();
          for (String string : config.getAsStringArray("description-locked")) {
            lore.add(StringUtils.formatColors(string).replace("{name}", perk.getRawName()).replace("{price}", StringUtils.formatNumber(perk.getCoins())).replace("{rarity}", rarity));
          }
          icon = perk.getIcon("§c", lore.toArray(new String[lore.size()]));
        } else {
          List<String> lore = new ArrayList<>();
          for (String string : config.getAsStringArray("description-unlocked")) {
            lore.add(StringUtils.formatColors(string).replace("{name}", perk.getRawName()).replace("{price}", StringUtils.formatNumber(perk.getCoins())).replace("{rarity}", rarity));
          }
          icon = perk.getIcon("§a", lore.toArray(new String[lore.size()]));
        }

        items.add(icon);
        this.perks.put(icon, perk);
      }
    }

    for (Map.Entry<Integer, ConfigItem> entry : config.getItems().entrySet()) {
      if (entry.getKey() >= 0 && entry.getKey() < (config.getRows() * 9)) {
        String stack = entry.getValue().getStack();

        // COINS
        stack = stack.replace("{coins}", StringUtils.formatNumber(account.getInt("coins")));

        ItemStack item = BukkitUtils.deserializeItemStack(stack);
        this.removeSlotsWith(item, entry.getKey());
        this.actions.put(item, entry.getValue().getAction());
      }
    }

    this.setItems(items);

    this.open();
    this.register();
  }

  public void cancel() {
    HandlerList.unregisterAll(this);
    this.perks.clear();
    this.perks = null;
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent evt) {
    if (evt.getPlayer().equals(player)) {
      this.cancel();
    }
  }

  @EventHandler
  public void onInventoryClose(InventoryCloseEvent evt) {
    if (evt.getPlayer().equals(player) && evt.getInventory().equals(getCurrentInventory())) {
      this.cancel();
    }
  }
}
