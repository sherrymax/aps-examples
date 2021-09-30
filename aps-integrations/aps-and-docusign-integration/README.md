# Upload to SharePoint - APS BPMN Stencil
### *Built as a Java extension project*

Developing an integration from APS to Sharepoint, leveraging as a java extension project. This helps developers to create APS-Sharepoint integrations to upload documents from APS. This article uses REST API from Sharepoint to build the integration.

Build, Deploy and Run
-------------

 1. [Setup a build environment](https://community.alfresco.com/community/bpm/blog/2016/11/14/activiti-enterprise-developer-series-setting-up-an-extension-project)
 2. Modify SharePointDelegate.java for the SharePoint Online target environment
 2. Run "mvn clean install"
 3. Deploy activiti-extension-jar-1.0-SNAPSHOT.jar, httpmime-4.3.5.jar and json-simple-1.1.1.jar (dependencies for the extension) to the activiti-app webapp's WEB-INF/lib folder
 4. Import the "SharePoint Online Upload Example.zip" into your APS instance via the APS UI
 5. Modify the process configuration to meet your needs and run the process end-end. If successful, the process will be uploaded to SharePoint Online

 ![Process Config 1](./process_config1.png?raw=true "Process Config 1")
 ![Process Config 2](./process_config2.png?raw=true "Process Config 2")

Configuration for SharePoint Online
-------------

A [SharePoint APP](https://mysite.sharepoint.com/sites/myapp/) has already been configured for internal Alfresco user.  
The credentials are populated in the [sharepoint-connector.properties](https://git.alfresco.com/solution-engineering/sharepoint_online_upload-aps-extension/blob/master/sharepoint-connector.properties) file.
Please ensure to remove these credentials before providing to external users.

Open the [sharepoint-connector.properties](https://git.alfresco.com/solution-engineering/sharepoint_online_upload-aps-extension/blob/master/sharepoint-connector.properties) file and modify the following strings based on YOUR SharePoint Online account.
```sh
sharepoint.connector.createFolder.url=https://mysite.sharepoint.com/sites/mysite/_api/web/Folders
sharepoint.connector.uploadDocument.url=https://mysite.sharepoint.com/sites/mysite
sharepoint.connector.sharedDocumentSite.url=/sites/mysite/LOE/
sharepoint.connector.clientId=123456-1234-1234-1234-1234567890@1234567-1234-1234-1234-1234567890
sharepoint.connector.tenantId=1234567-1234-1243-1234-1234567890
sharepoint.connector.clientSecret=abcdefghijklmn/ABCDEFGHIJKLMNOP/ABcDefghIJKLmnop=
sharepoint.connector.resource=00000003-0000-0ff1-ce00-000000000000/mysite.sharepoint.com@a92a627e-e9ce-48ba-8edb-6c64a4dbcf34
```
the sharepoint-connector.properties needs to be saved to webapps/activiti-app/WEB-INF/classes
This [link](https://dev.office.com/officestore/docs/create-or-update-client-ids-and-secrets) and has instructions for generating the Client ID and Client Secret.  [Access SharePoint Online using Postman](http://www.ktskumar.com/2017/01/access-sharepoint-online-using-postman/) provides additional information on setup and testing.


Future Modifications
-------------

 1. Expose configuration details as Stencil Properties
 2. Provide options for uploading a single file from a process variable, all process attachments, or base64 String
 3. Return SharePoint File URL


References
-------------
 * http://www.ktskumar.com/2017/01/access-sharepoint-online-using-postman/
 * https://dev.office.com/officestore/docs/create-or-update-client-ids-and-secrets
 * https://github.com/cijujoseph/activiti-examples/tree/master/aps-acs-integration-utils
