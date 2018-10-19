import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Handles the populating of templates resolving with the provided map,
 * useful to generate code.
 */
public class TemplateWriter {

  private final String mFileDestination;
  private final String mTemplate;
  private final HashMap<String, Object> mBuildMap;

  public TemplateWriter(
    String fileDestination,
    String template
  )
  {
    mFileDestination = fileDestination;
    mTemplate = template;
    mBuildMap = new HashMap<String, Object>();
  }

  public void add(String key, Object value)
  {
    mBuildMap.put(key,value);
  }

  public void write() throws IOException {
    String template = mTemplate;

    for(Map.Entry<String,Object> entry : mBuildMap.entrySet() )
    {
      String value = entry.getValue().toString();
      template = template.replaceAll(Pattern.quote("${"+entry.getKey()+"}"), value);
    }

    new FileOutputStream(new File(mFileDestination)).write(
      template.getBytes(StandardCharsets.UTF_8)
    );
  }
}
