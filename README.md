ZAL - The Abstraction Layer for Zimbra.
===
<http://openzal.org/>

ZAL is distributed under the terms of GNU General Public License version 2 <http://www.gnu.org/licenses/gpl-2.0.html>.

API documentation is available on <https://zextras.github.io/OpenZAL/>

### Download ###

Latest ZAL branch is 1.11 downloadable from <http://openzal.org/1.11/>
You can download a precise ZAL version using this url:
        
        http://openzal.org/${branch}/zal-${version}-${zimbra-version}.jar

Where ${branch} is the ZAL branch, ${version} is ZAL full version (branch plus micro), ${zimbra-version} is the full zimbra version.  
For example with this url you will download zal version 1.9.1 for zimbra 8.6.0:
        
        http://openzal.org/1.11/zal-1.11.3-8.7.0.jar

### Runtime Dependencies ###

   Zimbra is all you need ;)

### Build Dependencies ###

PreBob Java preprocessor - <http://prebop.sourceforge.net/>  
Download and copy "preprocessor.jar" in "ant/"

Ant Contrib - <http://ant-contrib.sourceforge.net/>  
Download and copy "ant-contrib-1.0b3.jar" in "ant/"

Intellij Annotations - <http://mvnrepository.com/artifact/com.intellij/annotations/12.0>
Download and copy "annotations-12.jar" in "lib/"

Apache Commons Lang 3 - <http://search.maven.org/#artifactdetails%7Corg.apache.commons%7Ccommons-lang3%7C3.1%7Cjar>
Download and copy "commons-lang3-3.1.jar" in "lib/"

Google Guice 3.0 - <https://code.google.com/p/google-guice/downloads/detail?name=guice-3.0.zip>  
Download and extract all files in "lib/"

Jackson Annotations 2.7 - <http://search.maven.org/#artifactdetails%7Ccom.fasterxml.jackson.core%7Cjackson-annotations%7C2.7.4%7Cbundle>
Download and copy "jackson-annotations-2.7.4.jar" in "lib/"

Jackson Core 2.7 - <http://search.maven.org/#artifactdetails%7Ccom.fasterxml.jackson.core%7Cjackson-core%7C2.7.4%7Cbundle>
Download and copy "jackson-core-2.7.4.jar" in "lib/"

Jackson Databind 2.7 - <http://search.maven.org/#artifactdetails%7Ccom.fasterxml.jackson.core%7Cjackson-databind%7C2.7.4%7Cbundle>
Download and copy "jackson-databind-2.7.4.jar" in "lib/"

Zimbra Sources - <http://www.zimbra.com>  
For each version of zimbra copy "/opt/zimbra/lib/jars/" in "zimbra-jars/${VERSION}/"

### How to build ###

Resolve all ZAL dependencies then run:

> tools/generate-build > build.xml  
> ant zal-all

