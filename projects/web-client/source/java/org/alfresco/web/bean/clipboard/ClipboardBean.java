/*
 * Created on 01-Jun-2005
 */
package org.alfresco.web.bean.clipboard;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.transaction.UserTransaction;

import org.alfresco.repo.dictionary.impl.DictionaryBootstrap;
import org.alfresco.repo.node.InvalidNodeRefException;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.node.operations.NodeOperationsService;
import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.web.app.context.UIContextService;
import org.alfresco.web.bean.NavigationBean;
import org.alfresco.web.bean.RepoUtils;
import org.alfresco.web.bean.repository.Node;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.ui.common.Utils;
import org.alfresco.web.ui.common.component.UIActionLink;
import org.alfresco.web.ui.repo.component.shelf.UIClipboardShelfItem;
import org.apache.log4j.Logger;
import org.springframework.web.jsf.FacesContextUtils;

/**
 * @author Kevin Roast
 */
public class ClipboardBean
{
   // ------------------------------------------------------------------------------
   // Bean property getters and setters 
   
   /**
    * @return Returns the NodeService.
    */
   public NodeService getNodeService()
   {
      return this.nodeService;
   }

   /**
    * @param nodeService The NodeService to set.
    */
   public void setNodeService(NodeService nodeService)
   {
      this.nodeService = nodeService;
   }
   
   /**
    * @return Returns the NodeOperationsService.
    */
   public NodeOperationsService getNodeOperationsService()
   {
      return this.nodeOperationsService;
   }

   /**
    * @param nodeOperationsService   The NodeOperationsService to set.
    */
   public void setNodeOperationsService(NodeOperationsService nodeOperationsService)
   {
      this.nodeOperationsService = nodeOperationsService;
   }
   
   /**
    * @return Returns the navigation bean instance.
    */
   public NavigationBean getNavigator()
   {
      return this.navigator;
   }
   
   /**
    * @param navigator The NavigationBean to set.
    */
   public void setNavigator(NavigationBean navigator)
   {
      this.navigator = navigator;
   }
   
   /**
    * @return Returns the clipboard items.
    */
   public List<ClipboardItem> getItems()
   {
      return this.items;
   }
   
   /**
    * @param items   The clipboard items to set.
    */
   public void setItems(List<ClipboardItem> items)
   {
      this.items = items;
   }
   
   
   // ------------------------------------------------------------------------------
   // Navigation action event handlers 
   
   /**
    * Action handler called to add a node to the clipboard for a Copy operation
    */
   public void copyNode(ActionEvent event)
   {
      UIActionLink link = (UIActionLink)event.getComponent();
      Map<String, String> params = link.getParameterMap();
      String id = params.get("id");
      if (id != null && id.length() != 0)
      {
         addClipboardNode(id, ClipboardStatus.COPY);
      }
   }
   
   /**
    * Action handler called to add a node to the clipboard for a Cut operation
    */
   public void cutNode(ActionEvent event)
   {
      UIActionLink link = (UIActionLink)event.getComponent();
      Map<String, String> params = link.getParameterMap();
      String id = params.get("id");
      if (id != null && id.length() != 0)
      {
         addClipboardNode(id, ClipboardStatus.CUT);
      }
   }
   
   /**
    * Action handler call from the browse screen to Paste All clipboard items into the current Space
    */
   public void pasteAll(ActionEvent event)
   {
      performPasteItems(-1);
   }
   
   /**
    * Action handler called to paste one or all items from the clipboard
    */
   public void pasteItem(ActionEvent event)
   {
      UIClipboardShelfItem.ClipboardEvent clipEvent = (UIClipboardShelfItem.ClipboardEvent)event;
      
      int index = clipEvent.Index;
      if (index >= this.items.size())
      {
         throw new IllegalStateException("Clipboard attempting paste a non existent item index: " + index);
      }
      
      performPasteItems(index);
   }
   
   /**
    * Perform a paste for the specified clipboard item(s)
    * 
    * @param index      of clipboard item to paste or -1 for all
    */
   private void performPasteItems(int index)
   {
      UserTransaction tx = null;
      try
      {
         tx = (UserTransaction)FacesContextUtils.getRequiredWebApplicationContext(
               FacesContext.getCurrentInstance()).getBean(Repository.USER_TRANSACTION);
         tx.begin();
         
         if (index == -1)
         {
            // paste all
            for (int i=0; i<this.items.size(); i++)
            {
               performClipboardOperation(this.items.get(i));
            }
            // remove the cut operation item from the clipboard
            List<ClipboardItem> newItems = new ArrayList<ClipboardItem>(this.items.size());
            for (int i=0; i<this.items.size(); i++)
            {
               ClipboardItem item = this.items.get(i);
               if (item.Mode != ClipboardStatus.CUT)
               {
                  newItems.add(item);
               }
            }
            setItems(newItems);
            // TODO: after a paste all - remove items from the clipboard...? or not. ask linton
         }
         else
         {
            // single paste operation
            ClipboardItem item = this.items.get(index);
            performClipboardOperation(item);
            if (item.Mode == ClipboardStatus.CUT)
            {
               this.items.remove(index);
            }
         }
         
         // commit the transaction
         tx.commit();
         
         // refresh UI on success
         UIContextService.getInstance(FacesContext.getCurrentInstance()).notifyBeans();
      }
      catch (Exception err)
      {
         // rollback the transaction
         try { if (tx != null) {tx.rollback();} } catch (Exception tex) {}
         Utils.addErrorMessage("Unable to paste item due to system error: " + err.getMessage(), err);
      }
   }

   /**
    * Perform the operation for the specified clipboard item
    * 
    * @param item
    */
   private void performClipboardOperation(ClipboardItem item)
   {
      NodeRef parentRef = new NodeRef(Repository.getStoreRef(), this.navigator.getCurrentNodeId());
      
      // TODO: should we use primary parent here?
      //       The problem is we can't pass round ChildAssocRefs as form params etc. in the UI!
      //       It's naff backend design if we need to pass childassocref around everywhere...!
      ChildAssocRef assocRef = this.nodeService.getPrimaryParent(item.Node.getNodeRef());
      
      if (item.Mode == ClipboardStatus.COPY)
      {
         if (logger.isDebugEnabled())
            logger.debug("Trying to copy node ID: " + item.Node.getId() + " into node ID: " + parentRef.getId());
         
         // call the node ops service to initiate the copy
         // TODO: what should the assoc QName be?
         boolean copyChildren = (item.Node.getType().equals(DictionaryBootstrap.TYPE_QNAME_FOLDER));
         NodeRef copyRef = this.nodeOperationsService.copy(item.Node.getNodeRef(), parentRef, null, assocRef.getQName(), copyChildren);
         
         // TODO: a temp fix for the fact that the NAME attribute is not copied as DD not here yet
         this.nodeService.setProperty(copyRef, RepoUtils.QNAME_NAME, item.Node.getName());
      }
      else
      {
         if (logger.isDebugEnabled())
            logger.debug("Trying to move node ID: " + item.Node.getId() + " into node ID: " + parentRef.getId());
         
         // move the node
         this.nodeService.moveNode(item.Node.getNodeRef(), parentRef, null, assocRef.getQName());
      }
   }
   
   /**
    * Add a clipboard node for an operation to the clipboard
    * 
    * @param id      ID of the node for the operation
    * @param mode    ClipboardStatus for the operation
    */
   private void addClipboardNode(String id, ClipboardStatus mode)
   {
      try
      {
         NodeRef ref = new NodeRef(Repository.getStoreRef(), id);
         
         // check for duplicates first
         ClipboardItem item = new ClipboardItem(new Node(ref, this.nodeService), mode);
         boolean foundDuplicate = false;
         for (int i=0; i<items.size(); i++)
         {
            if (items.get(i).equals(item))
            {
               // found a duplicate replace with new instance as copy mode may have changed
               items.set(i, item);
               foundDuplicate = true;
               break;
            }
         }
         // if duplicate not found, then append to list
         if (foundDuplicate == false)
         {
            items.add(item);
         }
      }
      catch (InvalidNodeRefException refErr)
      {
         Utils.addErrorMessage( MessageFormat.format(RepoUtils.ERROR_NODEREF, new Object[] {id}) );
      }
   }
   
   
   // ------------------------------------------------------------------------------
   // Private data
   
   private static Logger logger = Logger.getLogger(ClipboardBean.class);
   
   /** The NodeService to be used by the bean */
   private NodeService nodeService;
   
   /** The NodeOperationsService to be used by the bean */
   private NodeOperationsService nodeOperationsService;
   
   /** The NavigationBean reference */
   private NavigationBean navigator;
   
   /** Current state of the clipboard items */
   private List<ClipboardItem> items = new ArrayList<ClipboardItem>(4);
}
