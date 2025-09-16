package cc.insidious.lootify.api.loottable.wrapper;

import cc.insidious.lootify.api.loottable.LootentryOuterClass;
import cc.insidious.lootify.utilities.item.Itemstack;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor @Getter
public class LootEntryWrapper {
    private final LootentryOuterClass.Lootentry lootentry;

    public UUID getUniqueId() {
        try {
            return UUID.fromString(this.lootentry.getUniqueID());
        } catch (IllegalArgumentException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public Itemstack.ItemStack getItemStack(){
        return lootentry.getItemStack();
    }

    public double getChance(){
        return lootentry.getChance();
    }

    public LootEntryWrapper setChance(double chance) {
        LootentryOuterClass.Lootentry.Builder builder = this.lootentry.toBuilder();
        builder.setChance(chance);
        return new LootEntryWrapper(builder.build());
    }

    public static LootEntryWrapper from(UUID uniqueId, Itemstack.ItemStack itemstack, double chance) {
        return new LootEntryWrapper(LootentryOuterClass.Lootentry.newBuilder()
                .setUniqueID(uniqueId.toString())
                .setItemStack(itemstack)
                .setChance(chance)
                .build());
    }
}
