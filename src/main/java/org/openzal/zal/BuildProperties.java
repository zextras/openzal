package org.openzal.zal;

import java.io.IOException;
import java.util.Properties;
import org.openzal.zal.log.ZimbraLog;

public class BuildProperties {

  private static final String PROJECT_VERSION = "build.version";
  private static final String BUILD_TIMESTAMP = "build.timestamp";
  private static final String BUILD_TIMESTAMP_FORMAT = "maven.build.timestamp.format";
  private static final String DEV_BUILD = "dev.build";
  private static final String COMMIT_DIRTY = "git.dirty";
  private static final String GIT_COMMIT_ABBREV = "git.commit.id.abbrev";
  private static final String GIT_COMMIT_FULL = "git.commit.id.full";
  private static final String MAILBOX_VERSION = "carbonio.version";

  private static volatile Properties projectProperties;
  private static final Object lock = new Object();

  private static Properties getProjectProperties() {
    if (projectProperties == null || projectProperties.isEmpty()) {
      synchronized (lock) {
        if (projectProperties == null || projectProperties.isEmpty()) {
          projectProperties = new Properties();
          try {
            projectProperties.load(BuildProperties.class.getClassLoader().getResourceAsStream("zalbuild.properties"));
          } catch (IOException exp) {
            ZimbraLog.extensions.warn("Cannot load properties " + Utils.exceptionToString(exp));
          }
        }
      }
    }
    return projectProperties;
  }

  public static String getProjectVersion() {
    return getProjectProperties().getProperty(PROJECT_VERSION);
  }

  public static String getCommitFull() {
    return getProjectProperties().getProperty(GIT_COMMIT_FULL);
  }

  public static boolean isDevBuild() {
    try {
      return Boolean.parseBoolean(getProjectProperties().getProperty(DEV_BUILD));
    } catch (Exception ignore) {}
    return false;
  }
}

