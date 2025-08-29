package cc.insidious.example.utilities;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@UtilityClass
public class CC {
  private final Pattern unicodePattern = Pattern.compile("\\\\u\\+[a-fA-F0-9]{4}");
  private final Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");

  public String chat(String str) {
    Matcher match = unicodePattern.matcher(str);

    while (match.find()) {
      String code = str.substring(match.start(), match.end());
      str =
          str.replace(
              code, Character.toString((char) Integer.parseInt(code.replace("\\u+", ""), 16)));
      match = unicodePattern.matcher(str);
    }

    match = pattern.matcher(str);
    while (match.find()) {
      String hexCode = str.substring(match.start(), match.end());
      String replaceSharp = hexCode.replace('#', 'x');

      char[] ch = replaceSharp.toCharArray();
      StringBuilder builder = new StringBuilder();
      for (char c : ch) {
        builder.append("&").append(c);
      }

      str = str.replace(hexCode, builder.toString());
      match = pattern.matcher(str);
    }
    return ChatColor.translateAlternateColorCodes('&', str);
  }

  public List<String> chat(List<String> s) {
    return s.stream().map(CC::chat).collect(Collectors.toList());
  }
}
