import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

class DatedOutputStream extends OutputStream
{
  private final PrintStream   mStdout;
  private final long          mStartTime;
  private final AtomicBoolean mSharedMustWriteDate;
  DateFormat mDateFormat;

  public DatedOutputStream(PrintStream stdout, long startTime, AtomicBoolean sharedMustWriteDate)
  {
    mStdout = stdout;
    mStartTime = startTime;
    mSharedMustWriteDate = sharedMustWriteDate;
    mDateFormat = new SimpleDateFormat("HH:mm:ss");
  }

  private void writDateTime()
  {
    double delta = (System.currentTimeMillis() - mStartTime) / 1000.0;
    try
    {
      mStdout.write('[');
      mStdout.write(mDateFormat.format(new Date()).getBytes(StandardCharsets.UTF_8));
      mStdout.write(String.format(" +%.1f", delta).getBytes(StandardCharsets.UTF_8));
      mStdout.write(']');
      mStdout.write(' ');
    }
    catch (IOException e)
    {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void write(int i) throws IOException
  {
    if( mSharedMustWriteDate.get() ) {
      writDateTime();
      mSharedMustWriteDate.set( false );
    }

    if (i == '\n')
    {
      mStdout.write('\n');
      mSharedMustWriteDate.set( true );
    }
    else
    {
      mStdout.write(i);
    }
  }

  @Override
  public void flush() throws IOException
  {
    mStdout.flush();
  }

  @Override
  public void close() throws IOException
  {
  }
}
