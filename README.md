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

### Build Dependencies ###

PreBob Java preprocessor - <http://prebop.sourceforge.net/>  
Download and copy "preprocessor.jar" in "lib/"

Ant Contrib - <http://ant-contrib.sourceforge.net/>  
Download and copy "ant-contrib-1.0b3.jar" in "lib/"

Intellij Annotations - <http://mvnrepository.com/artifact/com.intellij/annotations/12.0>
Download and copy "annotations-12.jar" in "lib/"

Google Guice 4.0 - <https://code.google.com/p/google-guice/downloads/detail?name=guice-4.0.zip>  
Download and extract all files in "lib/"
    
Jackson Annotations 2.7 - <http://search.maven.org/#artifactdetails%7Ccom.fasterxml.jackson.core%7Cjackson-annotations%7C2.7.4%7Cbundle>
Download and copy "jackson-annotations-2.7.4.jar" in "lib/"

Jackson Databind 2.7 - <http://search.maven.org/#artifactdetails%7Ccom.fasterxml.jackson.core%7Cjackson-databind%7C2.7.4%7Cbundle>
Download and copy "jackson-databind-2.7.4.jar" in "lib/"

Apache Commons DbUtils - <http://commons.apache.org/proper/commons-dbutils/download_dbutils.cgi>
Download and copy "commons-dbutils-1.7.jar" in "lib/"

Zimbra Sources - <http://www.zimbra.com>  
For each version of zimbra copy "/opt/zimbra/lib/jars/" in "zimbra-jars/${VERSION}/"    

### How to build ###

Generate a pkcs8 key (see here for more details : <https://wiki.openssl.org/index.php/Manual:Pkcs8(1)>)
and place into "private" directory the "key.pkcs8" file to sign final jar (or comment out ChecksumWriter from build.xml if you don't need it)
 
Resolve all ZAL dependencies then run:

> tools/generate-build > build.xml  
> ant zal-all

