package org.openzal.zal.redolog;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RedoLogOutput extends com.zimbra.cs.redolog.RedoLogOutput {

  public interface Reader<T> {
    void read(T read);
  }

  private final List<Reader> readers;
  public static final Reader SKIP = new Reader() {
    @Override
    public void read(Object read) {}
  };

  int counter;

  public RedoLogOutput(Map<Integer, Reader> readers) {
    super((RandomAccessFile) null);
    this.readers = new ArrayList<>();

    Set<Integer> integers = readers.keySet();
    int i = 0;
    for( int j : integers ) {
      while( i < j ) {
        this.readers.add(SKIP);
        i++;
      }
      this.readers.add(readers.get(i));
      i++;
    }
    counter = 0;
  }

  public void addReader(int i, Reader reader) {
    int j = readers.size();
    if( j == i ) {
      readers.add(reader);
    } else {
      while( j < i ) {
        this.readers.add(SKIP);
        j++;
      }
      readers.add(i, reader);
    }
  }

  @Override
  public void write(byte[] b) throws IOException {
    throw new UnsupportedOperationException();
  }

  private void callReader(Object o) {
    if( counter < readers.size() ) {
      readers.get(counter++).read(o);
    }
  }

  @Override
  public void writeBoolean(boolean v) throws IOException {
    callReader(v);
  }

  @Override
  public void writeByte(byte v) throws IOException {
    callReader(v);
  }

  @Override
  public void writeShort(short v) throws IOException {
    callReader(v);
  }

  @Override
  public void writeInt(int v) throws IOException {
    callReader(v);
  }

  @Override
  public void writeLong(long v) throws IOException {
    callReader(v);
  }

  @Override
  public void writeDouble(double v) throws IOException {
    callReader(v);
  }

  @Override
  public void writeUTF(String v) throws IOException {
    callReader(v);
  }

  @Override
  public void writeUTFArray(String[] v) throws IOException {
    callReader(v);
  }
}
