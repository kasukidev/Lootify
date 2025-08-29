package cc.insidious.example.utilities.item;

import com.cryptomorin.xseries.XMaterial;
import de.tr7zw.changeme.nbtapi.*;
import lombok.experimental.UtilityClass;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Optional;
import java.util.stream.Collectors;

@UtilityClass
public class ItemStackSerializer {

  public Itemstack.ItemStack serialize(ItemStack item) {
    if (item == null) {
      return null;
    }

    XMaterial type = XMaterial.matchXMaterial(item.getType());

    if (type == null) {
      return null;
    }

    Itemstack.ItemStack.Builder builder =
        Itemstack.ItemStack.newBuilder()
            .setType(type.name())
            .setAmount(item.getAmount())
            .setDurability(item.getDurability());

    if (type != XMaterial.AIR && item.getAmount() > 0) {
      builder.setNbtData(new NBTItem(item).toString());
    }

    // Handle item meta
    if (item.hasItemMeta()) {
      processItemMetaSerialize(builder, item.getItemMeta());
    }

    return builder.build();
  }

  public ItemStack deserialize(Itemstack.ItemStack proto) {
    if (proto == null) {
      return null;
    }

    Optional<XMaterial> optional = XMaterial.matchXMaterial(proto.getType());

    if (!optional.isPresent()) {
      return null;
    }
    ItemStack item = optional.get().parseItem();
    item.setAmount(proto.getAmount());
    item.setDurability((short) proto.getDurability());

    String nbtData = proto.getNbtData();

    if (nbtData != null && !nbtData.isEmpty() && !nbtData.equalsIgnoreCase("NULL")) {
      processNBTDataDeserialize(item, nbtData);
    }

    ItemMeta meta = item.getItemMeta();

    processItemMetaDeserialize(meta, proto);
    item.setItemMeta(meta);

    return item;
  }

  private void processNBTDataDeserialize(ItemStack item, String nbtData) {

    try {
      NBT.modify(
          item,
          nbt -> {
            nbt.mergeCompound(NBT.parseNBT(nbtData));
          });

    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private void processItemMetaDeserialize(ItemMeta meta, Itemstack.ItemStack proto) {
    if (proto.hasDisplay()) {
      processDisplayMetaSetDeserialize(meta, proto);
    }

    switch (proto.getSpecialMetaCase()) {
      case BOOK:
        {
          processBookMetaDeserialize(meta, proto);
          break;
        }

      case LEATHER:
        {
          processLeatherMetaDeserialize(meta, proto);
          break;
        }

      case SKULL:
        {
          processSkullMetaDeserialize(meta, proto);
          break;
        }

      case POTION:
        {
          processPotionMetaDeserialize(meta, proto);
          break;
        }

      case MAP:
        {
          processMapMetaDeserialize(meta, proto);
          break;
        }

      case FIREWORK:
        {
          processFireworkMetaDeserialize(meta, proto);
          break;
        }

      case BANNER:
        {
          processBannerMetaDeserialize(meta, proto);
          break;
        }

      case SPECIALMETA_NOT_SET:
        {
          break;
        }
    }
  }

  private void processDisplayMetaSetDeserialize(ItemMeta meta, Itemstack.ItemStack proto) {
    Itemstack.DisplayMeta display = proto.getDisplay();

    if (!display.getDisplayName().isEmpty()) {
      meta.setDisplayName(display.getDisplayName());
    }
    if (display.getLoreCount() > 0) {
      meta.setLore(display.getLoreList());
    }
  }

  private void processBookMetaDeserialize(ItemMeta meta, Itemstack.ItemStack proto) {
    BookMeta bookMeta = (BookMeta) meta;
    Itemstack.BookMeta book = proto.getBook();

    if (!book.getTitle().isEmpty()) {
      bookMeta.setTitle(book.getTitle());
    }

    if (!book.getAuthor().isEmpty()) {
      bookMeta.setAuthor(book.getAuthor());
    }

    bookMeta.setPages(book.getPagesList());
  }

  private void processLeatherMetaDeserialize(ItemMeta meta, Itemstack.ItemStack proto) {
    LeatherArmorMeta leatherMeta = (LeatherArmorMeta) meta;
    Itemstack.LeatherArmorMeta leather = proto.getLeather();
    leatherMeta.setColor(Color.fromRGB(leather.getRed(), leather.getGreen(), leather.getBlue()));
  }

  private void processSkullMetaDeserialize(ItemMeta meta, Itemstack.ItemStack proto) {
    SkullMeta skullMeta = (SkullMeta) meta;
    skullMeta.setOwner(proto.getSkull().getOwner());
  }

  private void processPotionMetaDeserialize(ItemMeta meta, Itemstack.ItemStack proto) {
    PotionMeta potionMeta = (PotionMeta) meta;
    for (Itemstack.PotionEffect effect : proto.getPotion().getEffectsList()) {
      potionMeta.addCustomEffect(
          new PotionEffect(
              PotionEffectType.getById(effect.getType()),
              effect.getDuration(),
              effect.getAmplifier(),
              effect.getAmbient()),
          true);
    }
  }

  private void processMapMetaDeserialize(ItemMeta meta, Itemstack.ItemStack proto) {
    MapMeta mapMeta = (MapMeta) meta;
    mapMeta.setScaling(proto.getMap().getScaling());
  }

  private void processFireworkMetaDeserialize(ItemMeta meta, Itemstack.ItemStack proto) {
    FireworkMeta fireworkMeta = (FireworkMeta) meta;
    fireworkMeta.setPower(proto.getFirework().getPower());
    for (Itemstack.FireworkEffect effect : proto.getFirework().getEffectsList()) {
      FireworkEffect.Builder effectBuilder =
          FireworkEffect.builder()
              .with(convertFireworkType(effect.getType()))
              .flicker(effect.getFlicker())
              .trail(effect.getTrail());

      effectBuilder.withColor(
          effect.getColorsList().stream().map(Color::fromRGB).collect(Collectors.toList()));

      effectBuilder.withFade(
          effect.getFadeColorsList().stream().map(Color::fromRGB).collect(Collectors.toList()));

      fireworkMeta.addEffect(effectBuilder.build());
    }
  }

  private void processBannerMetaDeserialize(ItemMeta meta, Itemstack.ItemStack proto) {
    BannerMeta bannerMeta = (BannerMeta) meta;
    bannerMeta.setBaseColor(DyeColor.values()[proto.getBanner().getBaseColor()]);
    for (Itemstack.Pattern pattern : proto.getBanner().getPatternsList()) {
      bannerMeta.addPattern(
          new Pattern(
              DyeColor.values()[pattern.getColor()],
              PatternType.values()[pattern.getPatternType()]));
    }
  }

  private void processItemMetaSerialize(Itemstack.ItemStack.Builder builder, ItemMeta meta) {
    // Display meta (name and lore)
    if (meta.hasDisplayName() || meta.hasLore()) {
      processDisplayMetaSerialize(meta, builder);
    }

    // Handle specific meta types
    if (meta instanceof BookMeta) {
      processBookMetaSerialize(meta, builder);
      return;
    }

    if (meta instanceof LeatherArmorMeta) {
      processLeatherMetaSerialize(meta, builder);
      return;
    }

    if (meta instanceof SkullMeta) {
      processSkullMetaSerialize(meta, builder);
      return;
    }

    if (meta instanceof PotionMeta) {
      processPotionMetaSerialize(meta, builder);
      return;
    }

    if (meta instanceof MapMeta) {
      processMapMetaSerialize(meta, builder);
      return;
    }

    if (meta instanceof FireworkMeta) {
      processFireworkMetaSerialize(meta, builder);
      return;
    }

    if (meta instanceof BannerMeta) {
      processBannerMetaSerialize(meta, builder);
    }
  }

  private void processDisplayMetaSerialize(ItemMeta meta, Itemstack.ItemStack.Builder builder) {
    Itemstack.DisplayMeta.Builder displayBuilder = Itemstack.DisplayMeta.newBuilder();
    if (meta.hasDisplayName()) {
      displayBuilder.setDisplayName(meta.getDisplayName());
    }

    if (meta.hasLore()) {
      displayBuilder.addAllLore(meta.getLore());
    }
    builder.setDisplay(displayBuilder.build());
  }

  private void processBookMetaSerialize(ItemMeta meta, Itemstack.ItemStack.Builder builder) {
    BookMeta bookMeta = (BookMeta) meta;
    Itemstack.BookMeta.Builder bookBuilder = Itemstack.BookMeta.newBuilder();
    if (bookMeta.hasTitle()) {
      bookBuilder.setTitle(bookMeta.getTitle());
    }
    if (bookMeta.hasAuthor()) {
      bookBuilder.setAuthor(bookMeta.getAuthor());
    }
    if (bookMeta.hasPages()) {
      bookBuilder.addAllPages(bookMeta.getPages());
    }
    builder.setBook(bookBuilder.build());
  }

  private void processLeatherMetaSerialize(ItemMeta meta, Itemstack.ItemStack.Builder builder) {
    LeatherArmorMeta leatherMeta = (LeatherArmorMeta) meta;
    Color color = leatherMeta.getColor();
    builder.setLeather(
        Itemstack.LeatherArmorMeta.newBuilder()
            .setRed(color.getRed())
            .setGreen(color.getGreen())
            .setBlue(color.getBlue()));
  }

  private void processSkullMetaSerialize(ItemMeta meta, Itemstack.ItemStack.Builder builder) {
    SkullMeta skullMeta = (SkullMeta) meta;
    if (!skullMeta.hasOwner()) {
      return;
    }
    builder.setSkull(Itemstack.SkullMeta.newBuilder().setOwner(skullMeta.getOwner()));
  }

  private void processPotionMetaSerialize(ItemMeta meta, Itemstack.ItemStack.Builder builder) {
    PotionMeta potionMeta = (PotionMeta) meta;
    if (potionMeta.hasCustomEffects()) {
      Itemstack.PotionMeta.Builder potionBuilder = Itemstack.PotionMeta.newBuilder();
      for (PotionEffect effect : potionMeta.getCustomEffects()) {
        potionBuilder.addEffects(
            Itemstack.PotionEffect.newBuilder()
                .setType(effect.getType().getId())
                .setDuration(effect.getDuration())
                .setAmplifier(effect.getAmplifier())
                .setAmbient(effect.isAmbient()));
      }
      builder.setPotion(potionBuilder.build());
    }
  }

  private void processMapMetaSerialize(ItemMeta meta, Itemstack.ItemStack.Builder builder) {
    MapMeta mapMeta = (MapMeta) meta;
    builder.setMap(Itemstack.MapMeta.newBuilder().setScaling(mapMeta.isScaling()));
  }

  private void processFireworkMetaSerialize(ItemMeta meta, Itemstack.ItemStack.Builder builder) {
    FireworkMeta fireworkMeta = (FireworkMeta) meta;
    Itemstack.FireworkMeta.Builder fireworkBuilder =
        Itemstack.FireworkMeta.newBuilder().setPower(fireworkMeta.getPower());

    for (FireworkEffect effect : fireworkMeta.getEffects()) {
      Itemstack.FireworkEffect.Builder effectBuilder =
          Itemstack.FireworkEffect.newBuilder()
              .setType(convertFireworkType(effect.getType()))
              .setFlicker(effect.hasFlicker())
              .setTrail(effect.hasTrail());

      effect.getColors().forEach(color -> effectBuilder.addColors(color.asRGB()));

      effect.getFadeColors().forEach(color -> effectBuilder.addFadeColors(color.asRGB()));

      fireworkBuilder.addEffects(effectBuilder.build());
    }
    builder.setFirework(fireworkBuilder.build());
  }

  private void processBannerMetaSerialize(ItemMeta meta, Itemstack.ItemStack.Builder builder) {
    BannerMeta bannerMeta = (BannerMeta) meta;
    Itemstack.BannerMeta.Builder bannerBuilder =
        Itemstack.BannerMeta.newBuilder().setBaseColor(bannerMeta.getBaseColor().ordinal());

    for (Pattern pattern : bannerMeta.getPatterns()) {
      bannerBuilder.addPatterns(
          Itemstack.Pattern.newBuilder()
              .setColor(pattern.getColor().ordinal())
              .setPatternType(pattern.getPattern().ordinal()));
    }
    builder.setBanner(bannerBuilder.build());
  }

  private Itemstack.FireworkEffect.Type convertFireworkType(FireworkEffect.Type type) {
    switch (type) {
      case BALL_LARGE:
        return Itemstack.FireworkEffect.Type.BALL_LARGE;
      case STAR:
        return Itemstack.FireworkEffect.Type.STAR;
      case CREEPER:
        return Itemstack.FireworkEffect.Type.CREEPER;
      case BURST:
        return Itemstack.FireworkEffect.Type.BURST;
      default:
        return Itemstack.FireworkEffect.Type.BALL;
    }
  }

  private FireworkEffect.Type convertFireworkType(Itemstack.FireworkEffect.Type type) {
    switch (type) {
      case BALL:
        return FireworkEffect.Type.BALL;
      case BALL_LARGE:
        return FireworkEffect.Type.BALL_LARGE;
      case STAR:
        return FireworkEffect.Type.STAR;
      case CREEPER:
        return FireworkEffect.Type.CREEPER;
      case BURST:
        return FireworkEffect.Type.BURST;
      default:
        return FireworkEffect.Type.BALL;
    }
  }
}
