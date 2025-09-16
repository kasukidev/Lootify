package cc.insidious.lootify.utilities.item;

import cc.insidious.lootify.utilities.CC;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemBuilder {

  private final ItemStack itemStack;

  private final ItemMeta itemMeta;

  public ItemBuilder(XMaterial material, int amount) {
    this.itemStack = material.parseItem();
    this.itemStack.setAmount(amount);
    this.itemMeta = this.itemStack.getItemMeta();
  }

  public ItemBuilder(XMaterial material) {
    this(material, 1);
  }

  public ItemBuilder setType(Material material) {
    this.itemStack.setType(material);
    return this;
  }

  public ItemBuilder setAmount(int amount) {
    this.itemStack.setAmount(amount);
    return this;
  }

  public ItemBuilder setName(String name) {
    this.itemMeta.setDisplayName(CC.chat(name));
    return this;
  }

  public ItemBuilder setLore(List<String> lore) {
    this.itemMeta.setLore(CC.chat(lore));
    return this;
  }

  public ItemBuilder setUnbreakable(boolean val) {
    this.itemMeta.spigot().setUnbreakable(val);
    return this;
  }

  public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
    this.itemMeta.addEnchant(enchantment, level, true);
    return this;
  }

  public ItemStack build() {
    this.itemStack.setItemMeta(this.itemMeta);
    return this.itemStack;
  }
}
