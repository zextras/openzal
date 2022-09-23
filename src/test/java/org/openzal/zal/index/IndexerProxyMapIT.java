package org.openzal.zal.index;

import com.zimbra.cs.mime.MimeHandler;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class IndexerProxyMapIT {
    private IndexerProxyMap mIndexerProxyMap;
    private HashMap<String, Object> mMap;
    private MimeHandlerProvider mMimeHandlerProvider;
    private MimeHandler mMimeHandler1;
    private MimeHandler mMimeHandler2;

    @Before
    public void setup() {
        mMimeHandler1 = mock(MimeHandler.class);
        mMimeHandler2 = mock(MimeHandler.class);
        mMap = new HashMap<String, Object>();
        mMimeHandlerProvider = mock(MimeHandlerProvider.class);
        mIndexerProxyMap = new IndexerProxyMap(
                mMap,
                mMimeHandlerProvider
        );
    }

    @Test
    public void get_without_any_valid_indexer_use_the_map() {
        mMap.put("type,ext", mMimeHandler1);

        when(mMimeHandlerProvider.getMimeHandlerFor(anyString(), anyString())).thenReturn(null);

        assertSame(
                mMimeHandler1,
                mIndexerProxyMap.get("type,ext")
        );
    }

    @Test
    public void valid_indexer_map_doesnt_get_used() {
        mMap.put("type,ext", mMimeHandler1);

        when(mMimeHandlerProvider.getMimeHandlerFor(anyString(), anyString())).thenReturn(
                mMimeHandler2
        );

        assertSame(
                mMimeHandler2,
                mIndexerProxyMap.get("type,ext")
        );
    }

    @Test
    public void valid_indexer_invalid_key_doesnt_explode() {
        mMap.put("typeext", mMimeHandler1);

        when(mMimeHandlerProvider.getMimeHandlerFor(anyString(), anyString())).thenReturn(
                mMimeHandler2
        );

        assertSame(
                mMimeHandler2,
                mIndexerProxyMap.get("typeext")
        );
    }


    @Test
    public void valid_indexer_double_comma_doesnt_explode() {
        mMap.put("type,,ext", mMimeHandler1);

        when(mMimeHandlerProvider.getMimeHandlerFor(anyString(), anyString())).thenReturn(
                mMimeHandler2
        );

        assertSame(
                mMimeHandler2,
                mIndexerProxyMap.get("type,,eext")
        );
    }


    @Test
    public void valid_indexer_empty_key_doesnt_explode() {
        mMap.put("", mMimeHandler1);

        when(mMimeHandlerProvider.getMimeHandlerFor(anyString(), anyString())).thenReturn(
                mMimeHandler2
        );

        assertSame(
                mMimeHandler2,
                mIndexerProxyMap.get("")
        );
    }


}