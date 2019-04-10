package src;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class implements a preprocessor for java source files back-compatible with ant-prebop preprocessor.
 * Tests are directly inside in this class main().
 */
public class ZimbraVersionSourcePreprocessor implements SourcePreprocessor {
  private final boolean mDevMode;
  private final Pattern sIfPattern = Pattern.compile("[$]if([^$]*)[$]");
  private final Pattern sElseIfPattern = Pattern.compile("[$]elseif([^$]*)[$]");
  private final Pattern sExpressionPattern = Pattern.compile(" *([a-zA-Z]+) *([<>=!]+) *([0-9.]+) *([|]{2}|[&]{2}|[|][!])? *");
  private final Zimbra mZimbra;

  ZimbraVersionSourcePreprocessor(
    Zimbra zimbra,
    boolean devMode
  )
  {
    mZimbra = zimbra;
    mDevMode = devMode;
  }

  private class PreprocessorError extends RuntimeException
  {
    private final String mErrorMessage;
    private final StringBuffer mSource;
    private final int mPosition;

    PreprocessorError(
      String errorMessage,
      StringBuffer source,
      int position
    )
    {
      super(errorMessage);
      mErrorMessage = errorMessage;
      mSource = source;
      mPosition = position;
    }

    private int countNewlines()
    {
      int newlines = 0;
      for( int n=0; n < mPosition; ++n){
        if( mSource.charAt(n) == '\n') {
          newlines++;
        }
      }

      return newlines;
    }

    public String toString()
    {
      int endLineIdx = mSource.indexOf("\n",mPosition);

      String line;
      if( endLineIdx == -1 ) {
        line = mSource.substring(mPosition);
      } else {
        line = mSource.substring(mPosition, endLineIdx);
      }

      return
        countNewlines()+": "+mErrorMessage+"\n"+
          line+"\n"+
          "^";
    }
  }

  @Override
  public void apply(StringBuffer source)
  {
    int lastIndex = 0;
    while( true )
    {
      int index = source.indexOf("$if", lastIndex);
      if( index == -1 ) {
        break;
      }

      int endifIndex = source.indexOf("$endif", index);
      if( endifIndex == -1 ) {
        throw new PreprocessorError("Missing $endif", source, index);
      }

      StringBuffer conditionalPiece = new StringBuffer(
        source.substring(index, endifIndex+"$endif".length())
      );

      try {
        resolveExpression(conditionalPiece);
      }
      catch (PreprocessorError ex) {
        throw ex;
      }
      catch (Exception ex) {
        throw new PreprocessorError(ex.getMessage(), source, index);
      }

      source.replace(
        index,endifIndex+"$endif".length(),
        conditionalPiece.toString()
      );

      lastIndex = index+"$if".length();
    }
  }

  private void resolveExpression(StringBuffer code) {

    Matcher matcher = sIfPattern.matcher(code);
    if( !matcher.find() ) {
      throw new RuntimeException("Preprocessor: Bad $if syntax");
    }

    boolean result = parseAndEvaluate(
      matcher.group(1)
    );

    commentOrUncommentWhenNecessary(code, false, matcher, result);
    resolveSubexpression(code, result);
  }

  private void resolveSubexpression(StringBuffer code, boolean commentEverything) {

    Matcher elseIfMatcher = sElseIfPattern.matcher(code);
    if( elseIfMatcher.find() ) {
      boolean result = parseAndEvaluate(
        elseIfMatcher.group(1)
      );

      commentOrUncommentWhenNecessary(code, commentEverything, elseIfMatcher, result);

      StringBuffer subCode = new StringBuffer(
        code.substring(elseIfMatcher.end())
      );

      resolveSubexpression(subCode, commentEverything || result);
      code.replace(
        elseIfMatcher.end(),
        code.length(),
        subCode.toString()
      );
      return;
    }

    int elseIndex = code.indexOf("$else");
    if( elseIndex != -1 ) {
      int elseEndIndex = code.indexOf("$", elseIndex+1);
      if( elseEndIndex == -1 ) {
        throw new RuntimeException("Missing closing $ in $else");
      }

      int commentIndex = code.indexOf("*/", elseEndIndex);
      if( commentEverything ) {
        if (commentIndex != -1) {
          code.replace(commentIndex, commentIndex+2, "");
        }
      }
      else {
        if (commentIndex == -1) {
          code.insert(elseEndIndex + 1, " */");
        }
      }
    }
  }

  private void commentOrUncommentWhenNecessary(
    StringBuffer code,
    boolean commentEverything,
    Matcher matcher,
    boolean result
  )
  {
    int endCommentIndex = code.indexOf("*/", matcher.end() );
    int nextCommandIndex = code.indexOf("$", matcher.end());
    if(!commentEverything && result)
    {
      if( endCommentIndex == -1 || (nextCommandIndex != -1 && endCommentIndex > nextCommandIndex)) {
        code.insert(matcher.end(), " */");
      }
    }
    else
    {
      if( endCommentIndex != -1 && (nextCommandIndex == -1 || endCommentIndex < nextCommandIndex) ) {
        code.replace(endCommentIndex, endCommentIndex + 2, "");
      }
    }
  }

  private boolean parseAndEvaluate(String expression)
  {
    boolean result = true;
    boolean lastWasAnd = true;
    boolean shouldBeLast = false;
    int lastMatchIndex = 0;

    Matcher matcher = sExpressionPattern.matcher(expression);
    while( matcher.find() )
    {
      lastMatchIndex = matcher.end();

      if( shouldBeLast ) {
        throw new RuntimeException("Bad Expression syntax: missing && or ||");
      }
      String variableName = matcher.group(1);
      String condition = matcher.group(2);
      String version = matcher.group(3);
      String andOr = matcher.group(4);

      boolean currentResult = evaluate(
        variableName,
        condition,
        version
      );

      if( lastWasAnd )
      {
        result = result && currentResult;
      }
      else
      {
        result = result || currentResult;
      }

      lastWasAnd = "&&".equals(andOr);
      shouldBeLast = null == andOr;
    }

    if( expression.length() > lastMatchIndex ) {
      throw new RuntimeException("Bad Expression syntax: "+expression.substring(lastMatchIndex));
    }

    return result;
  }

  private boolean evaluate(String variableName, String condition, String version)
  {
    Version variableValue;

    switch (variableName)
    {
      case "ZimbraVersion":
        variableValue = mZimbra.getVersion();
        break;

      case "DevMode":
        variableValue = mDevMode ? new Version("1") : new Version("0");
        break;

      case "ZimbraX":
        variableValue = mZimbra.getType() == Zimbra.Type.x ? new Version("1") : new Version("0");
        break;

      default:
        throw new RuntimeException("Invalid variable "+variableName);
    }


    int compared = variableValue.compareTo(
      new Version(version)
    );

    switch (condition)
    {
      case "==": return compared == 0;
      case "!=": return compared != 0;
      case ">=": return compared >= 0;
      case ">":  return compared > 0;
      case "<=": return compared <= 0;
      case "<":  return compared < 0;

      default:
        throw new RuntimeException("Invalid condition "+condition);
    }
  }

  public static void main(String[] args)
  {
    {
      String sample =
        "/* $if ZimbraVersion >= 1.0 $ */\n" +
          "echo ciao\n" +
          "/* $endif */";

      assertSample(
        "1.0",
        sample,
        sample
      );

      assertSample(
        "0.0",
        "/* $if ZimbraVersion >= 1.0 $ \n" +
          "echo ciao\n" +
          "/* $endif */",
        sample
      );
    }

    {
      String sample =
        "/* $if ZimbraVersion >= 1.0 $ */\n" +
          "ciao_1\n" +
          "/* $else$ \n"+
          "ciao_2\n" +
          "/* $endif */";

      assertSample(
        "1.0",
        sample,
        sample
      );

      assertSample(
        "0.0",
        "/* $if ZimbraVersion >= 1.0 $ \n" +
          "ciao_1\n" +
          "/* $else$ */ \n" +
          "ciao_2\n" +
          "/* $endif */",
        sample
      );
    }

    {
      String sample =
        "/* $if ZimbraVersion >= 2.0 $ */\n" +
          "ciao_1\n" +
          "/* $elseif ZimbraVersion == 1.0 $\n"+
          "ciao_2\n" +
          "/* $else$\n"+
          "ciao_3\n" +
          "/* $endif */";

      assertSample(
        "2.0",
        sample,
        sample
      );

      assertSample(
        "1.0",
        "/* $if ZimbraVersion >= 2.0 $ \n" +
          "ciao_1\n" +
          "/* $elseif ZimbraVersion == 1.0 $ */\n"+
          "ciao_2\n" +
          "/* $else$\n"+
          "ciao_3\n" +
          "/* $endif */",
        sample
      );

      assertSample(
        "0.0",
        "/* $if ZimbraVersion >= 2.0 $ \n" +
          "ciao_1\n" +
          "/* $elseif ZimbraVersion == 1.0 $\n"+
          "ciao_2\n" +
          "/* $else$ */\n"+
          "ciao_3\n" +
          "/* $endif */",
        sample
      );
    }

    {
      String sample =
        "/* $if ZimbraVersion > 1.0 && ZimbraVersion > 2.0 $ */\n" +
          "echo ciao\n" +
          "/* $endif */";

      assertSample(
        "3.0",
        sample,
        sample
      );

      assertSample(
        "2.0",
        "/* $if ZimbraVersion > 1.0 && ZimbraVersion > 2.0 $ \n" +
          "echo ciao\n" +
          "/* $endif */",
        sample
      );
    }

    {
      String sample = "/* $if ZimbraVersion > 1.0 || ZimbraVersion > 2.0 $ */\n" +
        "echo ciao\n" +
        "/* $endif */";

      assertSample(
        "2.0",
        sample,
        sample
      );
    }

    {
      String sample = "/* $if ZimbraVersion >= 1.0 $\n" +
        "echo ciao\n" +
        "/* $endif */";

      assertSample(
        "0.0",
        sample,
        sample
      );

      assertSample(
        "1.0",
        "/* $if ZimbraVersion >= 1.0 $ */\n" +
          "echo ciao\n" +
          "/* $endif */",
        sample
      );
    }

    {
      String sample = "/* $if ZimbraVersion >= 1.0 $\n" +
        "ciao_1\n" +
        "/* $else $ */\n"+
        "ciao_2\n"+
        "/* $endif */";

      assertSample(
        "0.0",
        sample,
        sample
      );

      assertSample(
        "1.0",
        "/* $if ZimbraVersion >= 1.0 $ */\n" +
          "ciao_1\n" +
          "/* $else $ \n"+
          "ciao_2\n"+
          "/* $endif */",
        sample
      );
    }

    {
      String sample = "/* $if ZimbraVersion >= 1.0 $\n" +
        "ciao_1\n" +
        "/* $elseif ZimbraVersion == 0.0 $ */\n"+
        "ciao_2\n"+
        "/* $endif */";

      assertSample(
        "0.0",
        sample,
        sample
      );

      assertSample(
        "1.0",
        "/* $if ZimbraVersion >= 1.0 $ */\n" +
          "ciao_1\n" +
          "/* $elseif ZimbraVersion == 0.0 $ \n"+
          "ciao_2\n"+
          "/* $endif */",
        sample
      );
    }

    {
      String sample = "/* $if ZimbraVersion == 0.0 |! ZimbraVersion == 0.1 $ \n" +
        "primo\n" +
        "/* $elseif ZimbraVersion <= 1.0 $\n" +
        "secondo\n" +
        "/* $else $ */\n" +
        "terzo\n" +
        "/* $endif $ */";

      assertSample(
        "1.0",
        "/* $if ZimbraVersion == 0.0 |! ZimbraVersion == 0.1 $ \n" +
          "primo\n" +
          "/* $elseif ZimbraVersion <= 1.0 $ */\n" +
          "secondo\n" +
          "/* $else $ \n" +
          "terzo\n" +
          "/* $endif $ */",
        sample
      );

      assertSample(
        "0.1",
        "/* $if ZimbraVersion == 0.0 |! ZimbraVersion == 0.1 $ */ \n" +
          "primo\n" +
          "/* $elseif ZimbraVersion <= 1.0 $\n" +
          "secondo\n" +
          "/* $else $ \n" +
          "terzo\n" +
          "/* $endif $ */",
        sample
      );
    }

    {
      String sample =
        "/* $if ZimbraVersion != 8.0.1 && ZimbraVersion != 8.0.0 && ZimbraVersion < 8.5.0 $\n" +
          "ciao\n" +
          "/* $endif$ */";

      assertSample(
        "8.5",
        sample,
        sample
      );

      assertSample(
        "8.0.0",
        sample,
        sample
      );

      assertSample(
        "8.0.1",
        sample,
        sample
      );

      assertSample(
        "8.0.2",
        "/* $if ZimbraVersion != 8.0.1 && ZimbraVersion != 8.0.0 && ZimbraVersion < 8.5.0 $ */\n" +
          "ciao\n" +
          "/* $endif$ */",
        sample
      );
    }

    {
      String sample = "/* $if DevMode == 1 $\n" +
        "echo ciao\n" +
        "/* $endif */";

      assertSample(
        "0",
        sample,
        sample
      );

      assertSample(
        "1",
        "/* $if DevMode == 1 $ */\n" +
          "echo ciao\n" +
          "/* $endif */",
        sample
      );
    }

    {
      String sample = "/* $if !DevMode $*/\n" +
        "echo ciao\n" +
        "/* $endif */";

      boolean failed = false;
      try {
        assertSample(
          "0",
          sample,
          sample
        );

        failed = true;
      }
      catch (PreprocessorError ignore) {
      }

      if( failed ) {
        throw new RuntimeException("Expected exception");
      }
    }

    System.out.println("Test pass ;)");
  }

  private static void assertSample(String version, String expected, String code)
  {
    StringBuffer stringBuffer = new StringBuffer(code);
    new ZimbraVersionSourcePreprocessor(new Zimbra(Zimbra.Type.standard, new Version(version)), version.equals("1")).apply(stringBuffer);
    if( !stringBuffer.toString().equals(expected) )
    {
      throw new RuntimeException("\n=== Expected: ===\n"+expected+"\n=== But found: ===\n"+stringBuffer);
    }
  }
}
