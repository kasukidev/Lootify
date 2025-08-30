package cc.insidious.example.api.constant;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@UtilityClass
public class ExampleConstant {

  public final Charset CHARSET = StandardCharsets.UTF_8;
  public final String NMS_VERSION =
      Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
  public final String SQLITE_DATABASE_NAME = "example-database";
}
