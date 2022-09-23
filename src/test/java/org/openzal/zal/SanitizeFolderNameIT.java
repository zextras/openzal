package org.openzal.zal;

import org.openzal.zal.exceptions.NoSuchItemException;
import org.openzal.zal.exceptions.UnableToSanitizeFolderNameException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class SanitizeFolderNameIT
{
  private Mailbox          mailbox;
  private OperationContext zcontext;
  private final String DEFAULT_FOLDER_NAME = "New Folder";

  @Before
  public void setUp()
    throws Exception
  {
    NoSuchItemException noShuchFolderException = mock(NoSuchItemException.class);
    mailbox = mock(Mailbox.class);
    when(mailbox.getFolderByName(any(OperationContext.class),
                                 anyString(),
                                 anyInt())).thenThrow(noShuchFolderException);
    zcontext = mock(OperationContext.class);
  }

  @Test
  public void callOriginalName_withExampleName_returnSameName() throws Exception
  {
    String name = "Example";
    SanitizeFolderName sfn = new SanitizeFolderName(mailbox, name, 0);
    assertEquals(sfn.getOriginalName(), name);
  }

  @Test
  public void sanitize_simpleName_returnTheSame() throws Exception
  {
    String name = "SimpleName";
    SanitizeFolderName sfn = new SanitizeFolderName(mailbox, name, 0);
    assertEquals(sfn.sanitizeName(zcontext), name);
  }

  @Test
  public void sanitize_simpleNameWithSpaces_returnTheSame() throws Exception
  {
    String name = "Simple Folder Name";
    SanitizeFolderName sfn = new SanitizeFolderName(mailbox, name, 0);
    assertEquals(sfn.sanitizeName(zcontext), name);
  }

  @Test
  public void sanitize_simpleNameWithTrailingSpaces_returnNameWithoutTrailingSpaces() throws Exception
  {
    String name = "  Simple Folder Name  ";
    String nameExpected = "Simple Folder Name";
    SanitizeFolderName sfn = new SanitizeFolderName(mailbox, name, 0);
    assertEquals(sfn.sanitizeName(zcontext), nameExpected);
  }

  @Test
  public void sanitize_nameWithCtrlChars_returnNameWithoutCtrlChars() throws Exception
  {
    String name = "Simple\tFolder Name\n";
    String nameExpected = "SimpleFolder Name";
    SanitizeFolderName sfn = new SanitizeFolderName(mailbox, name, 0);
    assertEquals(sfn.sanitizeName(zcontext), nameExpected);
  }

  @Test
  public void sanitize_nameWithCtrlCharsAndInvalidChars_returnNameWithoutCtrlCharsAndInvalidChars() throws Exception
  {
    String name = "Si:mple\tFo/lder N\"ame\n";
    String nameExpected = "SimpleFolder Name";
    SanitizeFolderName sfn = new SanitizeFolderName(mailbox, name, 0);
    assertEquals(sfn.sanitizeName(zcontext), nameExpected);
  }

  @Test
  public void sanitize_nameWithSpaceCtrlCharsAndDots_returnNewFolder() throws Exception
  {
    String name = "  \t..\n";
    SanitizeFolderName sfn = new SanitizeFolderName(mailbox, name, 0);
    assertEquals(sfn.sanitizeName(zcontext), DEFAULT_FOLDER_NAME);
  }

  @Test
  public void sanitize_nameOfAnExistingFolder_returnNextAvailableFolderName() throws Exception
  {
    String name = "Folder";
    Folder existingFolder = mock(Folder.class);
    Mailbox mailbox = mock(Mailbox.class);

    when(mailbox.getFolderByName(any(OperationContext.class),
                                         eq(name),
                                         anyInt())).thenReturn(existingFolder);

    when(mailbox.getFolderByName(any(OperationContext.class),
                                         eq("Folder 1"),
                                         anyInt())).thenThrow(NoSuchItemException.class);

    SanitizeFolderName sfn = new SanitizeFolderName(mailbox, name, 0);

    assertEquals(sfn.sanitizeName(zcontext), "Folder 1");

    verify(mailbox, times(1)).getFolderByName(any(OperationContext.class), eq("Folder"), anyInt());
    verify(mailbox, times(1)).getFolderByName(any(OperationContext.class), eq("Folder 1"), anyInt());
  }

  @Test
  public void sanitize_nameOfAnExistingFolder_throwsExceptionOnOverflow() throws Exception
  {
    Folder existingFolder = mock(Folder.class);
    Mailbox mailbox = mock(Mailbox.class);

    when(mailbox.getFolderByName(any(OperationContext.class),
                                 anyString(),
                                 anyInt())).thenReturn(existingFolder);


    SanitizeFolderName sfn = new SanitizeFolderName(mailbox, "Folder", 0);

    try {
      String name = sfn.sanitizeName(zcontext);
      fail();
    } catch (UnableToSanitizeFolderNameException ignored) {}

    verify(mailbox, times(1000)).getFolderByName(any(OperationContext.class), anyString(), anyInt());
  }

}
