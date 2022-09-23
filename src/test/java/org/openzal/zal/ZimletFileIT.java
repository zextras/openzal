package org.openzal.zal;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ZimletFileIT
{
  private com.zimbra.cs.zimlet.ZimletFile             mZimletFile;
  private com.zimbra.cs.zimlet.ZimletFile.ZimletEntry mZimletEntry;
  private ZimletFile                                  mZextrasZimletFile;
  private File                                        mZimletTmpDir;
  private File                                        mZimletTmpFile;

  @Before
  public void setup()
    throws Exception
  {
    String tmpName = RandomStringUtils.randomAlphanumeric(10);
    mZimletTmpDir = new File(System.getProperty("java.io.tmpdir"), tmpName);
    mZimletTmpDir.mkdirs();
    mZimletTmpFile = new File(mZimletTmpDir, "test.js");
    mZimletTmpFile.createNewFile();
    mZimletFile = mock(com.zimbra.cs.zimlet.ZimletFile.class);
    mZimletEntry = mock(com.zimbra.cs.zimlet.ZimletFile.ZimletEntry.class);

    when(mZimletEntry.getContents()).thenReturn("Test bytes".getBytes());
    when(mZimletEntry.getContentStream()).thenReturn(new ByteArrayInputStream("Test bytes".getBytes()));
    when(mZimletFile.getFile()).thenReturn(mZimletTmpDir);
    when(mZimletFile.getEntry("test.js")).thenReturn(mZimletEntry);

    mZextrasZimletFile = new ZimletFile(mZimletFile);
  }

  @After
  public void after()
    throws Exception
  {
    FileUtils.deleteDirectory(mZimletTmpDir);
  }

  @Test
  public void get_not_compressed_file()
    throws Exception
  {
    final InputStream contentStream = mZextrasZimletFile.getContentStream("test.js");
    assertArrayEquals("Test bytes".getBytes(), IOUtils.toByteArray(contentStream));
  }

  @Test
  public void get_gzip_compressed_file_and_check_is_saved()
    throws Exception
  {
    assertFalse(new File(mZimletTmpFile + ".gzip").exists());
    try (InputStream contentStream =
        new GzipCompressorInputStream(
            mZextrasZimletFile
                .getContentStream(
                    "test.js", Collections.singletonList(ZimletFile.CompressionLevel.GZIP))
                .getSecond())) {

        assertArrayEquals("Test bytes".getBytes(), IOUtils.toByteArray(contentStream));
        assertTrue(new File(mZimletTmpFile + ".gzip").exists());
      }
  }

  @Test
  public void get_fallback_codec_when_available() throws Exception {
    assertFalse(new File(mZimletTmpFile + ".gzip").exists());
    assertFalse(new File(mZimletTmpFile + ".br").exists());
    final Pair<String, InputStream> pairStream = mZextrasZimletFile.getContentStream(
        "test.js",
        Arrays.asList(ZimletFile.CompressionLevel.BROTLI, ZimletFile.CompressionLevel.GZIP));;
    try {
      assertEquals("gzip", pairStream.getFirst());
      InputStream contentStream = new GzipCompressorInputStream(pairStream.getSecond());

      // Should have fallen back to gzip
      assertArrayEquals("Test bytes".getBytes(), IOUtils.toByteArray(contentStream));
      assertTrue(new File(mZimletTmpFile + ".gzip").exists());
      assertFalse(new File(mZimletTmpFile + ".br").exists());
    } finally {
        if (pairStream != null) {
          pairStream.getSecond().close();
        }
    }
  }
}
