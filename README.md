# Cerberus Jenkins Plugin

This plugin allows : 
* To launch a test campaign on Cerberus
* To wait Cerberus executes it
* To display a report on console. Fail if a test is ko, else sucess.

## What is cerberus ?
Cerberus is an open source, user friendly, **automated testing sofware**. See [website](https://www.cerberus-testing.org) for more information

## Compatibility table
| cerberus jenkins plugin | cerberus                |
| ----------------------- | ----------------------- |
| 1.0.0 -> 1.0.1          | 1.11.0 -> 1.13.2        |
| 1.0.2 -> 1.0.4          | 1.14.0 -> 3.2           |
| 1.0.4 -> 1.0.5          | 3.3 -> today version    |


## Get started
* Install plugin on Jenkins
* Set parameters on "Manage Jenkins" / "Configure System" screen (under "Execute Cerberus Test" section) : 

![global_parameter](docs/global_parameter.png "Global parameter")

![global_parameter](docs/global_parameter_adv.png "Global parameter Advanced")

### On Jenkins
* To add a campaign Cerberus execution on a job, add a new build step "Execute Cerberus Campaign", and set parameters :

![build_parameter](docs/build_parameter.png "Build parameter")

![build_parameter](docs/build_parameter_adv.png "Build parameter Advanced")
 

### On Jenkinsfile
* Just call `executeCerberusCampaign` function with parameters :
	* `campaignName` : Campaign name to execute
	* `tag` : Tag to apply
	* `environment` :  Environment to execute (define into Cerberus)
	* `browser` : Browser executed by Cerberus to test application. If `browser` is empty, use global settings of Cerberus plugin.
	* `cerberusUrl` : Override default cerberus url
	* `manualHost` : Override host of the application to test
   
```
pipeline {	
   ...
    stages {
		...
		stage('cerberus Test') {
			steps {					
				executeCerberusCampaign campaignName : "Test_campaign" , tag : "Test_campaign_YYYYMMDDHHmmss", environment : "QA", browser : "firefox"
			}
		}		
		...
	}
}
```

## Run plugin on localhost
For more detail on how develop a plugin for Jenkins, see [here](https://wiki.jenkins-ci.org/display/JENKINS/Plugin+tutorial).

For Netbeans, you can use the preconfigured 'CerberusJenkinsPlugin' under 'Run Maven' menu entry.
It will trigger the command :
```
mvn -Djetty.port=8090 hpi:run
```

You can also run the following command to launch Jenkins with your plugin in debug mode:

```
mvnDebug -Djetty.port=8090 hpi:run
```


Unix:
```
$ export MAVEN_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=8000,suspend=n"
$ mvn package hpi:run
```

Windows:

```
> set MAVEN_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=8000,suspend=n
> mvn package hpi:run
```

If you open http://localhost:8090/jenkins in your browser, you should see the Jenkins page running in Jetty. The MAVEN_OPTS portion launches this whole thing with the debugger port 8000, so you should be able to start a debug session to this port from your IDE.

Once this starts running, keep it running. Jetty will pick up all the changes automatically.

When you make changes to view files in `src/main/resources` or resource files in `src/main/webapp`, just hit `F5` in your browser to see the changes.

When you change Java source files, compile them in your IDE and Jetty should automatically redeploy Jenkins to pick up those changes. There is no need to run mvn at all.

`MAVEN_OPTS` can be used to specify all sorts of other JVM parameters, like -Xmx


#### Import project into Eclispe

```
$ mvn -DdownloadSources=true -DdownloadJavadocs=true -DoutputDirectory=target/eclipse-classes -Declipse.workspace=<workspace_path> eclipse:eclipse eclipse:configure-workspace	
```


## Technical Architecture
### Principle
![principle](docs/principle.png "Principle")

### Code
`ExecuteCerberusCampaign` is the main class call by Jenkins. 
It uses :
* `ExecuteCampaign` that trigger a Cerberus campaign (Calling AddToExecutionQueueVxxx) to Cerberus.
```java
executeCampaign.execute();
```

* `CheckCampaignStatus` that checks every n seconds (5 as default) the status of campaign execution (Calling ResultCIVxxx). When all test executed, job is finished
```java
checkCampaignStatus.execute(resultDto -> {	  
	// display advancement
	logger.println("Advancement : " + resultDto.getPercentOfTestExecuted() + "%");
}, resultDto -> {
 	// display result and shutdown
	logger.println("Result : " + resultDto.getResult() + "); 
});
```

`ExecuteCampaign` and `CheckCampaignStatus` are independent and could be used by other process than Jenkins.

## Release Cerberus Jenkins Plugin

#### Prerequis
* Have an account on github, on right to commit on this repo
* Have an account on `jenkins officiel infra` to upload release (see https://wiki.jenkins.io/display/JENKINS/Hosting+Plugins#HostingPlugins-Requestuploadpermissions)

#### How ?
Modify you maven settings.xml
```
<settings>
...................

<!-- added this -->

  <pluginGroups>
    <pluginGroup>org.jenkins-ci.tools</pluginGroup>
  </pluginGroups>

  <servers>
    <server>
      <id>maven.jenkins-ci.org</id> 
      <username>jenkinsusername</username>
      <password>jenkinspassword</password>
    </server>
    <server>
      <id>github.com</id>
      <username>githubusername</username>
      <password>githubpassword</password>
    </server>
  </servers>
  
  <profiles>
    <profile>
      <id>jenkins</id>
      <activation>
        <activeByDefault>true</activeByDefault> 
      </activation>
      <repositories>
        <repository>
          <id>repo.jenkins-ci.org</id>
          <url>https://repo.jenkins-ci.org/public/</url>
        </repository>
      </repositories>
      <pluginRepositories>
        <pluginRepository>
          <id>repo.jenkins-ci.org</id>
          <url>https://repo.jenkins-ci.org/public/</url>
        </pluginRepository>
      </pluginRepositories>
    </profile>
  </profiles>
  
  <mirrors>
    <mirror>
      <id>repo.jenkins-ci.org</id>
      <url>https://repo.jenkins-ci.org/public/</url>
      <mirrorOf>m.g.o-public</mirrorOf>
    </mirror>
  </mirrors>
  
 <!-- end added this --> 
 
 ..............................
 
</settings>
```

Pull cerberus jenkins plugin and launch this cmd on project :
```
mvn release:prepare release:perform
```

Check if your new version is in https://updates.jenkins-ci.org/update-center.json. This file is updated every 4 hours (plus mirror propagation time), so there's some delay before your new version appears here. So, as a rule of thumbs, please wait at least 8 hours before starting to worry about any issue.


==> Don't forget to ask a pull request from jenkinsci to cerberustesting github after sucess operation



## License

Cerberus Copyright (C) 2013 - 2017 cerberustesting

This file is part of Cerberus.

Cerberus is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Cerberus is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Cerberus.  If not, see <http://www.gnu.org/licenses/>.
