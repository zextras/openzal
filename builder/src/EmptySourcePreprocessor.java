package src;

/**
 * To be used when no preprocessor is needed
 */
public class EmptySourcePreprocessor implements SourcePreprocessor {
  @Override
  public void apply(StringBuffer source) {
  }
}
