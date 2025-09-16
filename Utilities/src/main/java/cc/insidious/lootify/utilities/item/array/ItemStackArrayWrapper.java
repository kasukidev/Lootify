package cc.insidious.lootify.utilities.item.array;

import cc.insidious.lootify.utilities.item.ItemStackSerializer;
import cc.insidious.lootify.utilities.item.Itemstack;
import com.cryptomorin.xseries.XMaterial;
import lombok.experimental.UtilityClass;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public class ItemStackArrayWrapper {

  public Itemstack.ItemStackArray fromBukkit(ItemStack[] itemStack) {
    if (itemStack == null || itemStack.length == 0) {
      return Itemstack.ItemStackArray.newBuilder().build();
    }

    Itemstack.ItemStackArray.Builder builder = Itemstack.ItemStackArray.newBuilder();

    for (int index = 0; index < itemStack.length; index++) {
      if (itemStack[index] == null) {
        builder.addItems(index, ItemStackSerializer.serialize(XMaterial.AIR.parseItem()));
        continue;
      }

      builder.addItems(index, ItemStackSerializer.serialize(itemStack[index]));
    }
    return builder.build();
  }

  public ItemStack[] toBukkit(Itemstack.ItemStackArray itemStackArray) {
    if (itemStackArray == null) {
      return null;
    }

    ItemStack[] itemStacks = new ItemStack[itemStackArray.getItemsCount()];

    for (int index = 0; index < itemStackArray.getItemsCount(); index++) {
      itemStacks[index] = ItemStackSerializer.deserialize(itemStackArray.getItems(index));
    }
    return itemStacks;
  }
}
