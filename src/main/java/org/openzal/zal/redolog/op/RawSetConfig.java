package org.openzal.zal.redolog.op;

import com.zimbra.cs.mailbox.MailboxOperation;
import com.zimbra.cs.redolog.RedoLogInput;
import com.zimbra.cs.redolog.RedoLogOutput;
import com.zimbra.cs.redolog.op.RedoableOp;
import java.io.IOException;

public class RawSetConfig extends RedoableOp {

  private String section;
  private String config;

  public RawSetConfig(Integer mailboxId, String section, String config) {
    super(MailboxOperation.SetConfig);
    setMailboxId(mailboxId);
    this.section = section;
    this.config = config;
  }

  protected RawSetConfig() {
    this(null, null, null);
  }

  public String getSection() {
    return section;
  }

  public String getConfig() {
    return config;
  }

  @Override
  public void redo() throws Exception {
    throw new UnsupportedOperationException();
  }

  @Override
  protected String getPrintableData() {
    StringBuffer sb = (new StringBuffer("section=")).append(this.section);
    sb.append(", config=").append(this.config.equals("") ? "null" : this.config);
    return sb.toString();
  }

  @Override
  protected void serializeData(RedoLogOutput out) throws IOException {
    out.writeUTF(this.section);
    out.writeUTF(config != null ? config : "");
  }

  @Override
  protected void deserializeData(RedoLogInput in) throws IOException {
    this.section = in.readUTF();
    this.config = in.readUTF();
  }
}
