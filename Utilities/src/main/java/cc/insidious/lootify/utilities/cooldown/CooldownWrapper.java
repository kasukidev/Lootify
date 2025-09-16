package cc.insidious.lootify.utilities.cooldown;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class CooldownWrapper {

  private final CooldownOuterClass.Cooldown cooldown;

  public CooldownWrapper placeOnCooldown(UUID uniqueId, long duration) {
    CooldownOuterClass.Cooldown.Builder builder = this.cooldown.toBuilder();
    builder.putCooldowns(uniqueId.toString(), duration);
    return new CooldownWrapper(builder.build());
  }

  public CooldownWrapper removeOnCooldown(UUID uniqueId) {
    CooldownOuterClass.Cooldown.Builder builder = this.cooldown.toBuilder();
    builder.removeCooldowns(uniqueId.toString());
    return new CooldownWrapper(builder.build());
  }

  public boolean isOnCooldown(UUID uniqueId) {
    return this.cooldown.containsCooldowns(uniqueId.toString())
        && this.cooldown.getCooldownsMap().get(uniqueId.toString()) > System.currentTimeMillis();
  }

  public long getRemainingCooldown(UUID uniqueId) {
    return this.cooldown.getCooldownsMap().get(uniqueId.toString()) - System.currentTimeMillis();
  }
}
