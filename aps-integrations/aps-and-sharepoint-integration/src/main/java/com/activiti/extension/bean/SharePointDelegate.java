package com.activiti.extension.bean;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.bouncycastle.util.encoders.Base64Encoder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.activiti.engine.delegate.Expression;
import org.springframework.util.StringUtils;
import com.activiti.domain.runtime.RelatedContent;
import com.activiti.service.runtime.RelatedContentService;
import com.activiti.service.runtime.RelatedContentStreamProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import org.apache.commons.codec.binary.Base64;

@Component("sharePointDelegate")
@Configuration
public class SharePointDelegate implements JavaDelegate {

	@Autowired
	private RelatedContentStreamProvider relatedContentStreamProvider;
	
	@Autowired
	private RelatedContentService relatedContentService;

	@Autowired
	private Environment env;

	private Expression folderName;
	private Expression result;
	private Expression contentField;
	private FileOutputStream fop;
	private static Logger logger = LoggerFactory.getLogger(SharePointDelegate.class);
	
	private static String CREATE_FOLDER_URL = "sharepoint.connector.createFolder.url";
	private static String UPLOAD_DOCUMENT_URL = "sharepoint.connector.uploadDocument.url";
	private static String SHARED_DOCUMENT_SITE_URL = "sharepoint.connector.sharedDocumentSite.url";
	private static String CLIENTID = "sharepoint.connector.clientId";
	private static String CLIENTSECRET = "sharepoint.connector.clientSecret";
	private static String TENANTID = "sharepoint.connector.tenantId";
	private static String RESOURCE = "sharepoint.connector.resource";
	
	
	public void execute(DelegateExecution execution) {

		try {
			
			String folderNameStr = (String) execution.getVariable(folderName.getExpressionText());
			logger.info("[Process=" + execution.getProcessInstanceId() + "][Spring Java Delegate=" + this + "]");
			logger.info("Folder Name :" + folderNameStr);
			
			logger.info("CREATE_FOLDER_URL :" + env.getProperty(CREATE_FOLDER_URL));
			logger.info("UPLOAD_DOCUMENT_URL :" + env.getProperty(UPLOAD_DOCUMENT_URL));
			logger.info("SHARED_DOCUMENT_SITE_URL :" + env.getProperty(SHARED_DOCUMENT_SITE_URL));
			logger.info("CLIENTID :" + env.getProperty(CLIENTID));
			logger.info("CLIENTSECRET :" + env.getProperty(CLIENTSECRET));
			logger.info("RESOURCE :" + env.getProperty(RESOURCE));
			
			
			//Removing Spaces in Folder Name
			if (StringUtils.hasText(folderNameStr) && StringUtils.containsWhitespace(folderNameStr)) {
				folderNameStr = folderNameStr.replaceAll("\\s", "%20");
			}
			// Create folder if not present
			String accessToken = createAccessToken(execution);
			createFolder(accessToken, folderNameStr, execution);

			// Upload file to that folder
			String newAccessToken = createAccessToken(execution);
			uploadDocumentByProcess(newAccessToken,folderNameStr, execution);
			execution.setVariable("result", "Success");
		} catch (Exception e) {
			execution.setVariable("result", "Failed" + e.getMessage() + e);
		}

	}

	private File prepareFileByDecodeBytes(String base64DecodedString, String fileName, DelegateExecution execution) {
		try {
			logger.info("Inside prepareFileByDecodeBytes method");
			byte[] decodedBytes = base64DecodedString.getBytes();
			File file = new File(fileName);
			logger.info("File:" + file);
			FileOutputStream fop1 = null;
			fop1 = new FileOutputStream(file);
			fop1.write(decodedBytes);
			fop1.close();
			return file;
		} catch (Exception e) {
			execution.setVariable("result", "Failed" + e.getMessage() + e);
			return null;
		}

	}

	private File prepareFile(String encodedString, String fileName, DelegateExecution execution) {
		try {
			logger.info("Inside prepareFile method");
			logger.info("Encoded bytes:" + encodedString);
			logger.info("File name:" + fileName);
			logger.info("Execution" + execution);
			BASE64Decoder decoder = new BASE64Decoder();
			byte[] decodedBytes;
			FileOutputStream fop;
			decodedBytes = new BASE64Decoder().decodeBuffer(encodedString);
			logger.info("Decoded bytes:" + decodedBytes);
			File file = new File(fileName);
			logger.info("File:" + file);
			FileOutputStream fop1 = null;
			fop1 = new FileOutputStream(file);
			fop1.write(decodedBytes);
			fop1.close();
			return file;
		} catch (Exception e) {
			execution.setVariable("result", "Failed" + e.getMessage() + e);
			return null;
		}

	}

	public String createFolder(String accessToken, String folderName, DelegateExecution execution) {
		try {
			logger.info("Inside upload folder method");
			CloseableHttpClient client = HttpClientBuilder.create().build();
			String resource = env.getProperty(CREATE_FOLDER_URL);
			HttpPost req = new HttpPost(resource);
			req.setHeader("Content-Type", "application/json;odata=verbose");
			req.setHeader("Accept", "application/json;odata=verbose");
			req.setHeader("Authorization", "Bearer " + accessToken);

			String body = "{ \"__metadata\"" + ":" + "{ \"type\"" + ":" + "\"SP.Folder\"" + "}" + ","
					+ " \"ServerRelativeUrl\"" + ":" + "\"LOE/" + folderName + "\"}";
			logger.info("Resource (createFolder) :" + resource);
			logger.info("Body :" + body);
			logger.info("Access token :" + accessToken);
			StringEntity params = new StringEntity(body);
			req.setEntity(params);
			CloseableHttpResponse response = client.execute(req);
			int status = response.getStatusLine().getStatusCode();
			logger.info("status: " + status);
			if (status == 201) {
				return folderName;
			} else {
				execution.setVariable("result", "Failed uploading folder");
				return null;
			}
		} catch (Exception e) {
			execution.setVariable("result", "Failed" + e.getMessage() + e);
			return null;
		}

	}

	// Create Document
	public void uploadDocument(Path source, File file, String accessToken, String fileName, String folderName,
			DelegateExecution execution) {
		try {
			logger.info("Inside upload document method");
			CloseableHttpClient client = HttpClientBuilder.create().build();
			String resource = env.getProperty(UPLOAD_DOCUMENT_URL) + "/_api/web/GetFolderByServerRelativeUrl('"
					+ env.getProperty(SHARED_DOCUMENT_SITE_URL) + folderName + "/')/Files/add" + "(url='" + fileName
					+ "',overwrite=true)";
			logger.info("Resource (uploadDocument) :" + resource);

			HttpPost req = new HttpPost(resource);
			req.setHeader("Accept", "application/json;odata=verbose");
			req.setHeader("Authorization", "Bearer " + accessToken);

			FileBody content = new FileBody(source.toFile());

			String boundary = UUID.nameUUIDFromBytes(source.toString().getBytes()).toString();

			HttpEntity entity = MultipartEntityBuilder.create().setBoundary(boundary)
					.setCharset(Charset.forName("UTF-8")).setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
					.addPart("content", content).build();

			req.setEntity(entity);
			CloseableHttpResponse response = client.execute(req);
			int status = response.getStatusLine().getStatusCode();
			logger.info("Status :" + status);
		} catch (Exception e) {
			execution.setVariable("result", "Failed" + e.getMessage() + e);
		}

	}
	
	//get all documents in the process
		public List<RelatedContent> getContents(String processInstanceId) throws BpmnError {
			logger.info("Inside getContents method with process instance id" + processInstanceId);
			List<RelatedContent> relatedContent = new ArrayList<RelatedContent>();
			Page<RelatedContent> page = null;
			int pageNumber = 0;
			try {			
				while ((page == null) || (page.hasNext())) {
					page = relatedContentService
							.getAllFieldContentForProcessInstance(
									processInstanceId, 50,
									pageNumber);
					relatedContent.addAll(page.getContent());
					pageNumber++;
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}	
			return relatedContent;
		}
		

	public void uploadDocumentByProcess(String accessToken,String folderName,DelegateExecution execution) throws Exception {
		try {
			logger.info("Inside upload document method");
			CloseableHttpClient client = HttpClientBuilder.create().build();
			List<RelatedContent> relatedContentList = null;
			logger.info("Document Content:" + contentField);
			if(contentField!=null){
				relatedContentList = getFieldContent(execution.getProcessInstanceId(), contentField.getExpressionText());
				
			} else {
				relatedContentList = getContents(execution.getProcessInstanceId());
			}
			if (relatedContentList != null) {
				for (RelatedContent relatedContent : relatedContentList) {

					if (StringUtils.hasText(relatedContent.getName()) && StringUtils.containsWhitespace(relatedContent.getName())) {
						relatedContent.setName(relatedContent.getName().replaceAll("\\s", "%20"));
					}
					String resource = env.getProperty(UPLOAD_DOCUMENT_URL) + "/_api/web/GetFolderByServerRelativeUrl('"
							+ env.getProperty(SHARED_DOCUMENT_SITE_URL) + folderName + "/')/Files/add" + "(url='" + relatedContent.getName()
							+ "',overwrite=true)";
					logger.info("Resource (uploadDocumentByProcess):" + resource);
					HttpPost req = new HttpPost(resource);
					logger.info("Request + related content stream provider" + req + relatedContentStreamProvider);
					req.setHeader("Accept", "application/json;odata=verbose");
					req.setHeader("Authorization", "Bearer " + accessToken);

					InputStream inputStream = relatedContentStreamProvider.getContentStream(relatedContent);
					byte[] streamByteArray = IOUtils.toByteArray(inputStream);
					String fileName = relatedContent.getName();
					File file = new File(fileName);
					fop = new FileOutputStream(file);
					fop.write(streamByteArray);
					logger.info("Binary data" + streamByteArray);
					

					//If you want to write file  in local directory ,just uncomment this code.
					
					/*FileOutputStream fop1 = new FileOutputStream("d:/newfolder/" + file);
					fop1.write(streamByteArray);
					fop1.close();*/
					
				req.setEntity(new FileEntity(file));

					CloseableHttpResponse response = client.execute(req);
					int status = response.getStatusLine().getStatusCode();
					logger.info("Status :" + status);
				}
			}

		} catch (Exception e) {
			execution.setVariable("result", "Failed" + e.getMessage() + e);
		}
		finally{
			if(fop !=null){
				fop.close();
			}
		}

	}
	
	
	public List<RelatedContent> getFieldContent(String processInstanceId, String field){
		logger.info("Inside getFieldContent method with process instance id and field" + processInstanceId + field);
		logger.info("Related content service :" + relatedContentService);
		List<RelatedContent> relatedContent = new ArrayList<RelatedContent>();
		Page<RelatedContent> page = null;
		int pageNumber = 0;
		try {			
			while ((page == null) || (page.hasNext())) {
				page = relatedContentService
						.getFieldContentForProcessInstance(
								processInstanceId,field, 50,
								pageNumber);
				relatedContent.addAll(page.getContent());
				pageNumber++;
			}
			return relatedContent;
		} catch (Exception e) {
			logger.error("Error in getFieldContent" + e.getMessage(), e);
			return null;
		}	
	}

	private String createAccessToken(DelegateExecution execution) {
		try {
			logger.info("Inside create Access Token");
			CloseableHttpClient client = HttpClientBuilder.create().build();

			String resource = "https://accounts.accesscontrol.windows.net/"+env.getProperty(TENANTID)+"/tokens/OAuth/2";
		
			HttpPost req = new HttpPost(resource);
			req.setHeader("content-type", "application/x-www-form-urlencoded");
			
			String body = "grant_type=client_credentials&client_id=" + URLEncoder.encode(env.getProperty(CLIENTID), "UTF-8") + "&client_secret=" + URLEncoder.encode(env.getProperty(CLIENTSECRET), "UTF-8")
			+ "&resource=" + URLEncoder.encode(env.getProperty(RESOURCE), "UTF-8");					
						
			logger.info("Body: " + body);
			logger.info("Resource (createAccessToken): "+ resource);
	
			StringEntity params = new StringEntity(body);
			req.setEntity(params);

			HttpResponse response = client.execute(req);
			String responseString = new BasicResponseHandler().handleResponse(response);
			
			logger.info("response:" + responseString);
			
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(responseString);
			logger.info((String) json.get("access_token"));
			return (String) json.get("access_token");
		} catch (Exception e) {
			execution.setVariable("result", "Failed" + e.getMessage() + e);
			return null;
		}
	}

	public Expression getFolderName() {
		return folderName;
	}

	public void setFolderName(Expression folderName) {
		this.folderName = folderName;
	}

}