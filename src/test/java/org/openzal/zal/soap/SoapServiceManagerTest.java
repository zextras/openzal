package org.openzal.zal.soap;

import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SoapServiceManagerTest
{
    // ZX-5095
    @Test
    public void register_unregister_service()
    {
        SoapService soapService = mock(SoapService.class);
        when(soapService.getServiceName()).thenReturn("name");

        SoapServiceManager soapServiceManager = new SoapServiceManager();
        soapServiceManager.register(soapService);

        soapServiceManager.unregister(soapService);
    }
}