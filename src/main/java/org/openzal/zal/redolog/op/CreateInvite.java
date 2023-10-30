package org.openzal.zal.redolog.op;

public class CreateInvite {
    private final RedoableOp mOp;

    public CreateInvite(RedoableOp op)
    {
        mOp = op;
    }

    public int getCalendarItemId()
    {
        return ((com.zimbra.cs.redolog.op.CreateInvite) mOp.getProxiedObject()).getCalendarItemId();
    }
}
