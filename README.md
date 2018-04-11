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

Zimbra Sources - <http://www.zimbra.com>  
For each version of zimbra copy "/opt/zimbra/lib/jars/" in "zimbra-jars/${VERSION}/"

All others dependencies are downloaded automatically by ant and placed into lib directory 

### How to build ###

If you want to sign your jar place your key.pkcs8 into private directory to sign jar (go to <https://wiki.openssl.org/index.php/Command_Line_Utilities#pkcs8_.2F_pkcs5> to more details)

Resolve all ZAL dependencies then run:

> tools/generate-build > build.xml  
> ant zal-all

or to build only the current zimbra version: 

> ant zal-dev-current  

