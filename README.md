ZAL - The Abstraction Layer for Zimbra.
===
<http://openzal.org/>

ZAL is distributed under the terms of GNU General Public License version 2 <http://www.gnu.org/licenses/gpl-2.0.html>.

API documentation is available on <https://zextras.github.io/OpenZAL/>

### Download ###

Latest ZAL is downloadable from <git@github.com:ZeXtras/OpenZAL.git>
You can clone ZAL repository using:
        
        git clone https://zextras.github.io/OpenZAL/

### Runtime Dependencies ###

   Zimbra is all you need ;)

### Jar Signing ###

If you want to sign your jar place your key.pkcs8 into private directory otherwise skip this section (to sign jar go to <https://wiki.openssl.org/index.php/Command_Line_Utilities#pkcs8_.2F_pkcs5> for more details) 

### Build Targets ###

**zal-dev-current-binary**:

OpenZAL can be place anywhere, and zimbra must be installed into /opt/zimbra

Then run:

    ant zal-dev-current-binary  

**zal-dev-current-source**:

Simply place OpenZAL into same directory of zm-build and follow guide <https://github.com/Zimbra/zm-build/wiki/installer-build> to 
build Zimbra.

Then run:

    ant zal-dev-current-source  

**zal-all**:

For each zimbra version you want to build ZAL for:
 
* copy all file from "/opt/zimbra/lib/jars/" in "zimbra-jars/${VERSION}/"
* copy from "/opt/zimbra/common/jetty_home/lib/"  
jetty-continuation-x.x.x.y.jar
jetty-http-x.x.x.y.jar
jetty-io-x.x.x.y.jar
jetty-rewrite-x.x.x.y.jar
jetty-security-x.x.x.y.jar
jetty-server-x.x.x.y.jar
jetty-servlet-x.x.x.y.jar
jetty-servlets-x.x.x.y.jar
jetty-util-x.x.x.y.jar
in "zimbra-jars/${VERSION}/"
* copy jtnef-x.x.x.jar from "jetty_base/common/lib" in "zimbra-jars/${VERSION}/" 

Then run:

    ant zal-all  


