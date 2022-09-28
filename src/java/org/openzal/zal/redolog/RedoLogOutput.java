package org.openzal.zal.redolog;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class RedoLogOutput extends com.zimbra.cs.redolog.RedoLogOutput {

  public interface Reader<T> {
    void read(int pos, T read);
  }

  private final List<Reader> readers;
  public static final Reader SKIP = new Reader() {
    @Override
    public void read(int pos, Object read) {}
  };

  int counter;

  public RedoLogOutput() {
    super((RandomAccessFile) null);
    this.readers = new ArrayList<>();
  }

  public RedoLogOutput addReader(Reader reader) {
    readers.add(reader);
    return this;
  }

  public RedoLogOutput addReader(int i, Reader reader) {
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
    return this;
  }

  public RedoLogOutput addSkip(int i) {
    return addReader(i, SKIP);
  }

  @Override
  public void write(byte[] b) throws IOException {}

  private void callReader(Object o) {
    if( counter < readers.size() ) {
      readers.get(counter).read(counter, o);
      counter++;
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
