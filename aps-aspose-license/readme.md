## Aspose: Evaluation Only : Red Banner

### Description
The banner at the top of the rendered PDF showing in red:  "Evaluation Only. Created with Aspose.Words. Copyright 2003-2015 Aspose Pty Ltd." states that either the license of the Aspose PDF render library has expired or couldn't be loaded because it hasn't been placed at the right location.


A document, which was attached to a process, may show a red banner consisting the text: "Evaluation Only. Created with Aspose.Words. Copyright 2003-2015 Aspose Pty Ltd."

### Resolution
We ship a full Aspose license with Activiti v1.4.1 either embedded in the binary installer, which drops the license at <Activiti_Install_Folder>/tomcat/lib/transform.lic or in the ZIP archive "activiti-app-1.4.1.zip" containing the Activiti app WAR files.

In order to deploy the license "transform.lic" properly you do have three options available.

1.) Drop the license "transform.lic" at Tomcat's classpath, e.g. <Activiti_Install_Folder>/tomcat/lib/
2.) Set the property aspose.license=<Path to folder containing the license>/transform.lic in "activiti-app.properties"
3.) Drop the license "transform.lic at user.home folder of the user who has started the Activiti instance and rename it to "Aspose.Total.Java.lic" => "<user.home>/.activiti/enterprise-license/Aspose.Total.Java.lic"

For all three options, you need to restart the instance.

### References
* https://community.hyland.com/tskb/ALF000005192-aspose-evaluation-only-red-banner
