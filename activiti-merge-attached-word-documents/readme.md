#### This project contains all the components required to merge the attached doc files (.doc/.docx) in a process

### Use-Case / Requirement
The workflow should create a cover page, with the names from the attached documents. 
Store the cover page in Alfresco Content Repo. 
Generate another document by combining the cover page and the merged documents.

### Prerequisites to run this demo end-2-end

* Alfresco Process Services (powered by Activiti) (Version 1.9 and above) - If you don't have it already, you can download a 30 day trial from [Alfresco Process Services (APS)](https://www.alfresco.com/products/business-process-management/alfresco-activiti).Instructions & help available at [Activiti Docs](http://docs.alfresco.com/activiti/docs/), [Alfresco BPM Community](https://community.alfresco.com/community/bpm)


## Configuration Steps

### Activiti Setup and Process Deployment
1. Setup Alfresco Activiti if you don't have one already. Instructions & help available at [Activiti Docs](http://docs.alfresco.com/activiti/docs/), [Alfresco BPM Community](https://community.alfresco.com/community/bpm)
2. Import the ![Merge-Documents.zip](Merge-Documents.zip) app available in this project into Activiti.
3. The process flow.  ![Process-Flow](Process-Flow.png)
4. The process configuration. ![Process-Configuration-1](Process-Configuration-1.png)
5. The javascript code to merge document. ![Javascript](Javascript.png)
```
java.lang.System.out.println("***  Started - Merge document *** ");
documentMergeBean.mergeDocuments('coverLetterT;file1;file2', 'coverLetterT', execution);
java.lang.System.out.println("***  Finished - Merge document *** ");
```

6. Publish to Alfresco configuration. ![Publish](Publish.png)
7. Publish/Deploy the App.



### Run the DEMO

### References
1. http://docs.alfresco.com/process-services1.6/topics/document_merge_bean_documentmergebean.html
2. https://docs.alfresco.com/activiti/docs/dev-guide/1.4.0/#_document_merge_bean_documentmergebean
3. http://docs.alfresco.com/process-services1.6/topics/document_templates.html