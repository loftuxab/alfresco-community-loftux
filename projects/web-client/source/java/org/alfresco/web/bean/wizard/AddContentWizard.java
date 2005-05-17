package org.alfresco.web.bean.wizard;

import java.io.File;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.transaction.UserTransaction;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.content.ContentService;
import org.alfresco.repo.content.ContentWriter;
import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.dictionary.bootstrap.DictionaryBootstrap;
import org.alfresco.repo.node.InvalidNodeRefException;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;
import org.alfresco.util.Conversion;
import org.alfresco.web.bean.FileUploadBean;
import org.alfresco.web.bean.NavigationBean;
import org.alfresco.web.bean.RepoUtils;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.ui.common.Utils;
import org.alfresco.web.ui.common.component.UIModeList;
import org.apache.log4j.Logger;
import org.springframework.web.jsf.FacesContextUtils;

/**
 * Handler class used by the Add Content Wizard 
 * 
 * @author gavinc
 */
public class AddContentWizard
{
   private static Logger logger = Logger.getLogger(AddContentWizard.class);
   private static final String ERROR_NODEREF = "Unable to find the repository node referenced by Id: {0} - the node has probably been deleted from the database.";

   // add content wizard specific properties
   private File file;
   private String fileName;
   private String owner;
   private boolean overwrite;
   private ContentService contentService;
      
   // common wizard properties
   private int currentStep = 1;
   private boolean finishDisabled = true;
   private String currentSpaceName;
   private NodeService nodeService;
   private NavigationBean navigator;

   /**
    * @return Returns the nodeService.
    */
   public NodeService getNodeService()
   {
      return this.nodeService;
   }

   /**
    * @param nodeService The nodeService to set.
    */
   public void setNodeService(NodeService nodeService)
   {
      this.nodeService = nodeService;
   }
   
   /**
    * @return Returns the navigation bean instance.
    */
   public NavigationBean getNavigator()
   {
      return navigator;
   }
   
   /**
    * @param navigator The NavigationBean to set.
    */
   public void setNavigator(NavigationBean navigator)
   {
      this.navigator = navigator;
   }
   
   /**
    * @return Returns the contentService.
    */
   public ContentService getContentService()
   {
      return contentService;
   }

   /**
    * @param contentService The contentService to set.
    */
   public void setContentService(ContentService contentService)
   {
      this.contentService = contentService;
   }

   /**
    * @return Returns the currentStep.
    */
   public int getCurrentStep()
   {
      return currentStep;
   }

   /**
    * @param currentStep The currentStep to set.
    */
   public void setCurrentStep(int currentStep)
   {
      this.currentStep = currentStep;
   }
   
   /**
    * @return Returns the currentSpaceName.
    */
   public String getCurrentSpaceName()
   {
      // NOTE: We do this ourself rather than using the browse bean as when the 
      //       wizard potentially gets restored from the shelf the current node
      //       may have changed from when the wizard was launched. That means we
      //       should also store the current id too!
      
      if (this.currentSpaceName == null)
      {
         String spaceName = "Alfresco Home";
         String currentNodeId = this.navigator.getCurrentNodeId();
         if (currentNodeId != null)
         {
            try
            {
               NodeRef ref = new NodeRef(Repository.getStoreRef(), currentNodeId);
               spaceName = RepoUtils.getNameForNode(this.nodeService, ref);
            }
            catch (InvalidNodeRefException refErr)
            {
               Utils.addErrorMessage(MessageFormat.format(ERROR_NODEREF, new Object[] {currentNodeId}));
            }
         }
         
         this.currentSpaceName = spaceName;
      } 
         
      return this.currentSpaceName;
   }

   /**
    * @param currentSpaceName The currentSpaceName to set.
    */
   public void setCurrentSpaceName(String currentSpaceName)
   {
      this.currentSpaceName = currentSpaceName;
   }

   /**
    * @return Returns the finishDisabled.
    */
   public boolean isFinishDisabled()
   {
      return finishDisabled;
   }

   /**
    * @param finishDisabled The finishDisabled to set.
    */
   public void setFinishDisabled(boolean finishDisabled)
   {
      this.finishDisabled = finishDisabled;
   }

   /**
    * @return Returns the name of the file
    */
   public String getFileName()
   {
      // try and retrieve the file and filename from the file upload bean
      // representing the file we previously uploaded.
      FacesContext ctx = FacesContext.getCurrentInstance();
      FileUploadBean fileBean = (FileUploadBean)ctx.getExternalContext().getSessionMap().
         get(FileUploadBean.FILE_UPLOAD_BEAN_NAME);
      if (fileBean != null)
      {
         this.file = fileBean.getFile();
         this.fileName = fileBean.getFileName();
      }
      
      return this.fileName;
   }

   /**
    * @param fileName The name of the file
    */
   public void setFileName(String fileName)
   {
      this.fileName = fileName;
      
      // we also need to keep the file upload bean in sync
      FacesContext ctx = FacesContext.getCurrentInstance();
      FileUploadBean fileBean = (FileUploadBean)ctx.getExternalContext().getSessionMap().
         get(FileUploadBean.FILE_UPLOAD_BEAN_NAME);
      if (fileBean != null)
      {
         fileBean.setFileName(this.fileName);
      }
   }

   /**
    * @return Returns the overwrite.
    */
   public boolean isOverwrite()
   {
      return overwrite;
   }

   /**
    * @param overwrite The overwrite to set.
    */
   public void setOverwrite(boolean overwrite)
   {
      this.overwrite = overwrite;
   }

   /**
    * @return Returns the owner.
    */
   public String getOwner()
   {
      return owner;
   }

   /**
    * @param owner The owner to set.
    */
   public void setOwner(String owner)
   {
      this.owner = owner;
   }

   /**
    * Deals with the next button being pressed
    * 
    * @return
    */
   public String next()
   {
      this.currentStep++;
      
      // determine whether the finish button should be enabled
      evaluateFinishButtonState();
      
      // determine which page to go to next
      String nextPage = determinePageForStep(this.currentStep);
      
      // navigate
      navigate(nextPage);
      
      if (logger.isDebugEnabled())
      {
         logger.debug("Navigating to next page: " + nextPage);
         logger.debug("currentStep: " + this.currentStep);
      }
      
      // return null to prevent the naviagtion handler looking up the next page
      return null;
   }
   
   /**
    * Deals with the back button being pressed
    * 
    * @return
    */
   public String back()
   {       
      this.currentStep--;
      
      // determine whether the finish button should be enabled
      evaluateFinishButtonState();
      
      // determine which page to go to next
      String previousPage = determinePageForStep(this.currentStep);
      
      // navigate
      navigate(previousPage);
      
      if (logger.isDebugEnabled())
      {
         logger.debug("Navigating to previous page: " + previousPage);
         logger.debug("currentStep: " + this.currentStep);
      }
      
      // return null to prevent the naviagtion handler looking up the next page
      return null;
   }
   
   /**
    * Deals with the finish button being pressed
    * 
    * @return outcome
    */
   public String finish()
   {
      if (logger.isDebugEnabled())
         logger.debug(getSummary());
      
      UserTransaction tx = null;
      
      try
      {
         tx = (UserTransaction)FacesContextUtils.getRequiredWebApplicationContext(
                 FacesContext.getCurrentInstance()).getBean(Repository.USER_TRANSACTION);
         tx.begin();
         
         // TODO: The current node id should be stored rather than retrieved so 
         //       restoration from the shelf behaves correctly.
         // get the node ref of the node that will contain the content
         NodeRef containerNodeRef;
         String nodeId = getNavigator().getCurrentNodeId();
         if (nodeId == null)
         {
            containerNodeRef = this.nodeService.getRootNode(Repository.getStoreRef());
         }
         else
         {
            containerNodeRef = new NodeRef(Repository.getStoreRef(), nodeId);
         }
         
         // TODO: deal with existing files and determine what to do from the
         //       this.overwrite member variable
         
         // create the node to represent the node
         String assocName = this.fileName.replace('.', '-');
         ChildAssocRef assocRef = this.nodeService.createNode(containerNodeRef,
                null,
                QName.createQName(NamespaceService.ALFRESCO_URI, assocName),
                DictionaryBootstrap.TYPE_QNAME_FILE);
         NodeRef fileNodeRef = assocRef.getChildRef();
         
         // add the name, created and modified date as properties for now
         Map<QName, Serializable> properties = new HashMap<QName, Serializable>(5);
         Date now = new Date( Calendar.getInstance().getTimeInMillis() );
         
         QName propName = QName.createQName(NamespaceService.ALFRESCO_URI, "name");
         properties.put(propName, this.fileName);
         
         QName propCreatedDate = QName.createQName(NamespaceService.ALFRESCO_URI, "createddate");
         properties.put(propCreatedDate, Conversion.dateToXmlDate(now));
        
         QName propModifiedDate = QName.createQName(NamespaceService.ALFRESCO_URI, "modifieddate");
         properties.put(propModifiedDate, Conversion.dateToXmlDate(now));
         
         // add the properties to the node
         nodeService.setProperties(fileNodeRef, properties);
         
         // get a writer for the content and put the file
         ContentWriter writer = contentService.getUpdatingWriter(fileNodeRef);
         writer.putContent(this.file);
         
         // commit the transaction
         tx.commit();
      }
      catch (Exception e)
      {
         // rollback the transaction
         try { if (tx != null) {tx.rollback();} } catch (Exception ex) {}
         throw new RuntimeException(e);
      }
      finally
      {
         clearUpload();
      }
      
      // reset the state
      reset();
      
      // navigate
      navigate("/jsp/browse/browse.jsp");
      
      return null;
   }
   
   /**
    * Deals with the cancel button being pressed
    * 
    * @return
    */
   public String cancel()
   {
      // reset the state
      clearUpload();
      reset();
      
      // navigate
      navigate("/jsp/browse/browse.jsp");
      
      return null;
   }
   
   /**
    * Deals with the minimise button being pressed
    * 
    * @return
    */
   public String minimise()
   {
      clearUpload();
      
      // navigate
      navigate("/jsp/browse/browse.jsp");
      
      return null;
   }
   
   /**
    * @return Returns the summary data for the wizard.
    */
   public String getSummary()
   {
      StringBuilder builder = new StringBuilder();
      builder.append("Into Space: ").append(this.currentSpaceName).append("<br/>");
      builder.append("File Name: ").append(this.fileName).append("<br/>");
      //builder.append("Overwrite: ").append(this.overwrite).append("<br/>");
      
      return builder.toString();
   }

   /**
    * Called when the step is changed by the left panel
    * 
    * @param event The event representing the step change
    */
   public void stepChanged(ActionEvent event)
   {
      UIModeList viewList = (UIModeList)event.getComponent();
      int step = Integer.parseInt(viewList.getValue().toString());
      
      String page = determinePageForStep(step);
      navigate(page);
      
      if (logger.isDebugEnabled())
         logger.debug("Navigating directly to: " + page);
   }
   
   /**
    * Sets the page to navigate to
    * 
    * @param page Page to navigate to
    */
   private void navigate(String page)
   {
      FacesContext ctx = FacesContext.getCurrentInstance();
      UIViewRoot newRoot = ctx.getApplication().getViewHandler().createView(ctx, page);
      ctx.setViewRoot(newRoot);
      ctx.renderResponse();
   }
   
   /**
    * Returns the page to be navigated to for the given step
    * 
    * @param step
    * @return
    */
   private String determinePageForStep(int step)
   {
      // TODO: in the wizard framework make this abstract and represent step
      //       0 as the page the wizard was launched from and step -1 as the
      //       cancel page
      
      String page = null;
      String dir = "/jsp/wizard/add-content/";
      
      switch (step)
      {
         case 1:
         {
            page = dir + "upload.jsp";
            break;
         }
         case 2:
         {
            page = dir + "summary.jsp";
            break;
         }
         default:
         {
            page = "/jsp/browse/browse.jsp";
         }
      }
      
      return page;
   }
   
   /**
    * Determines whether the finish button should be enabled and sets
    * the finishDisabled flag appropriately
    */
   private void evaluateFinishButtonState()
   {
      if (this.fileName == null)
      {
         this.finishDisabled = true;
      }
      else
      {
         this.finishDisabled = false;
      }
   }
   
   private void clearUpload()
   {
      // delete the temporary file we uploaded earlier
      if (this.file != null)
      {
         this.file.delete();
      }
      
      // remove the file upload bean from the session
      FacesContext ctx = FacesContext.getCurrentInstance();
      FileUploadBean fileBean = (FileUploadBean)ctx.getExternalContext().getSessionMap().
         remove(FileUploadBean.FILE_UPLOAD_BEAN_NAME);
   }
   
   /**
    * Resets the state of the wizard
    */
   private void reset()
   {  
      // reset all variables
      this.currentStep = 1;
      this.currentSpaceName = null;
      this.finishDisabled = false;
      this.fileName = null;
      this.owner = null;
      this.file = null;
      this.overwrite = false;
   }
}
