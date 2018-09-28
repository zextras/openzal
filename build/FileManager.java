import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import java.io.*;
import java.net.URI;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

class FileManager extends ForwardingJavaFileManager<JavaFileManager>
{
  private final String mDestinationJar;
  private ZipOutputStream mZip;

  FileManager(JavaFileManager javaFileManager, String destinationJar){
    super(javaFileManager);
    mDestinationJar = destinationJar;
    mZip = null;
  }

  @Override
  public void close() throws IOException {
    super.close();
    mZip.closeEntry();
    mZip.close();
    mZip = null;
  }

  @Override
  public JavaFileObject getJavaFileForInput(Location location, String className, JavaFileObject.Kind kind) throws IOException {
    System.out.println("getJavaFileForInput");
    return super.getJavaFileForInput(location, className, kind);
  }

  @Override
  public JavaFileObject getJavaFileForOutput(
    final Location location, final String className, final JavaFileObject.Kind kind, final FileObject fileObject
  ) throws IOException
  {
    if( mZip == null ) {
      File dstZip = new File(mDestinationJar);
      if( !dstZip.getParentFile().exists() ) {
        if( !dstZip.getParentFile().mkdirs() ) {
          throw new IOException("Unable to create directory:"+dstZip.getParentFile().getAbsolutePath());
        }
      }
      mZip = new ZipOutputStream(
        new FileOutputStream(dstZip)
      );
      mZip.setLevel(9);
    }
    return new JavaFileObject(){

      @Override
      public URI toUri() {
        return null;
      }

      @Override
      public String getName() {
        return className;
      }

      @Override
      public InputStream openInputStream() throws IOException {
        throw new UnsupportedOperationException();
      }

      @Override
      public OutputStream openOutputStream() throws IOException {
        ZipEntry zipEntry = new ZipEntry(className.replaceAll("[.]","/")+".class");
        mZip.putNextEntry(zipEntry);
        return new OutputStream() {
          @Override
          public void write(int i) throws IOException {
            mZip.write(i);
          }

          @Override
          public void write(byte[] bytes) throws IOException {
            mZip.write(bytes);
          }

          @Override
          public void write(byte[] bytes, int i, int i1) throws IOException {
            mZip.write(bytes, i, i1);
          }

          @Override
          public void close() throws IOException {
            mZip.closeEntry();
          }
        };
      }

      @Override
      public Reader openReader(boolean b) throws IOException {
        throw new UnsupportedOperationException();
      }

      @Override
      public CharSequence getCharContent(boolean b) throws IOException {
        throw new UnsupportedOperationException();
      }

      @Override
      public Writer openWriter() throws IOException {
        throw new UnsupportedOperationException();
      }

      @Override
      public long getLastModified() {
        throw new UnsupportedOperationException();
      }

      @Override
      public boolean delete() {
        throw new UnsupportedOperationException();
      }

      @Override
      public Kind getKind() {
        return kind;
      }

      @Override
      public boolean isNameCompatible(String s, Kind kind) {
        throw new UnsupportedOperationException();
      }

      @Override
      public NestingKind getNestingKind() {
        throw new UnsupportedOperationException();
      }

      @Override
      public Modifier getAccessLevel() {
        throw new UnsupportedOperationException();
      }
    };
  }

  @Override
  public FileObject getFileForInput(Location location, String s, String s1) throws IOException {
    System.out.println("getFileForInput");
    return super.getFileForInput(location, s, s1);
  }

  @Override
  public FileObject getFileForOutput(Location location, String s, String s1, FileObject fileObject) throws IOException {
    System.out.println("getFileForOutput");
    return super.getFileForOutput(location, s, s1, fileObject);
  }
}
