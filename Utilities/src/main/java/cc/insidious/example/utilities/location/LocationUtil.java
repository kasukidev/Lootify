package cc.insidious.example.utilities.location;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;

@UtilityClass
public class LocationUtil {

    public LocationOuterClass.Location fromBukkit(Location location) {
        return LocationOuterClass.Location.newBuilder()
                .setWorldId(location.getWorld().getName())
                .setX(location.getBlockX())
                .setY(location.getBlockY())
                .setZ(location.getBlockZ())
                .setYaw(location.getYaw())
                .setPitch(location.getPitch())
                .build();
    }

    public String toString(Location location) {
        return location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ", " + location.getWorld().getName();
    }
}