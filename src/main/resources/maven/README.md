# Maven settings
## Create the following file if not exists and put the following content
File name : ~/.m2/settings.xml
Content

```
<settings>
        <profiles>
                <profile>
                        <id>ossrh</id>
                        <activation>
                                <activeByDefault>true</activeByDefault>
                        </activation>
                        <properties>
                                <gpg.keyname>839C4A82B068E031570C5452559FE948461241F8</gpg.keyname>
                                <gpg.passphrase>merusphere-OSS</gpg.passphrase>
                        </properties>
                </profile>
        </profiles>

        <servers>
                <server>
                        <id>ossrh</id>
                        <username>devops-merusphere</username>
                        <password>1_Qossrepo4plugin</password>
                </server>
        </servers>
</settings>
```