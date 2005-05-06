/*
 * Created on 03-May-2005
 */
package org.alfresco.web.ui.repo.renderer;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.alfresco.web.ui.repo.component.UINodeDescendants;
import org.springframework.web.jsf.FacesContextUtils;

import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.jsf.Utils;
import org.alfresco.web.jsf.renderer.BaseRenderer;

/**
 * @author Kevin Roast
 */
public class NodeDescendantsLinkRenderer extends BaseRenderer
{
   // ------------------------------------------------------------------------------
   // Renderer implementation
   
   /**
    * @see javax.faces.render.Renderer#decode(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
    */
   public void decode(FacesContext context, UIComponent component)
   {
      Map requestMap = context.getExternalContext().getRequestParameterMap();
      String fieldId = getHiddenFieldName(context, component);
      String value = (String)requestMap.get(fieldId);
      
      // we encoded the value to start with our Id
      if (value != null && value.startsWith(component.getClientId(context) + NamingContainer.SEPARATOR_CHAR))
      {
         value = value.substring(component.getClientId(context).length() + 1);
         
         // found a new selected value for this component
         // queue an event to represent the change
         int separatorIndex = value.indexOf(NamingContainer.SEPARATOR_CHAR);
         String selectedNodeId = value.substring(0, separatorIndex);
         boolean isParent = Boolean.parseBoolean(value.substring(separatorIndex + 1));
         NodeService service = getNodeService(context);
         NodeRef ref = new NodeRef(Repository.getStoreRef(), selectedNodeId);
         
         UINodeDescendants.NodeSelectedEvent event = new UINodeDescendants.NodeSelectedEvent(component, ref, isParent); 
         component.queueEvent(event);
      }
   }
   
   /**
    * @see javax.faces.render.Renderer#encodeEnd(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
    */
   public void encodeEnd(FacesContext context, UIComponent component) throws IOException
   {
      // always check for this flag - as per the spec
      if (component.isRendered() == true)
      {
         Writer out = context.getResponseWriter();
         
         UINodeDescendants control = (UINodeDescendants)component;
         
         // make sure we have a NodeRef from the 'value' property ValueBinding
         Object val = control.getValue();
         if (val instanceof NodeRef == false)
         {
            throw new IllegalArgumentException("UINodeDescendants component 'value' property must resolve to a NodeRef!");
         }
         NodeRef parentRef = (NodeRef)val;
         
         // TODO: check we have a "container" node here?!
         
         // use Spring JSF integration to get the node service bean
         NodeService service = getNodeService(context);
         List<ChildAssocRef> childRefs = service.getChildAssocs(parentRef);
         
         // TODO: need a comparator to sort node refs (based on childref qname)
         //       as currently the list is returned in a random order per request
         
         // walk each child ref and output a descendant link control for each item
         String separator = (String)component.getAttributes().get("separator");
         if (separator == null)
         {
            separator = DEFAULT_SEPARATOR;
         }
         int maximum = childRefs.size() > control.getMaxChildren() ? control.getMaxChildren() : childRefs.size();
         for (int index=0; index<maximum; index++)
         {
            ChildAssocRef ref = childRefs.get(index);
            out.write(renderDescendant(context, control, ref, false));
            
            // output separator if appropriate
            if (index < maximum - 1)
            {
               out.write( separator );
            }
         }
         
         // do we need to render ellipses to indicate more items than the maximum
         if (control.getShowEllipses() == true && childRefs.size() > control.getMaxChildren())
         {
            out.write( separator );
            // TODO: is this the correct way to get the information we need?
            //       e.g. primary parent may not be the correct path? how do we make sure we find
            //       the correct parent and more importantly the correct Display Name value!
            out.write( renderDescendant(context, control, service.getPrimaryParent(parentRef), true) );
         }
      }
   }
   
   /**
    * Render a descendant as a clickable link
    * 
    * @param context    FacesContext
    * @param control    UINodeDescendants to get attributes from
    * @param childRef   The ChildAssocRef of the child to render an HTML link for
    * @param ellipses   Whether to render the label of this descendant as a ellipses i.e. "..."
    *  
    * @return HTML for a descendant link
    */
   private String renderDescendant(FacesContext context, UINodeDescendants control, ChildAssocRef childRef, boolean ellipses)
   {
      StringBuilder buf = new StringBuilder(256);
      
      buf.append("<a href='#' onclick=\"");
      // build an HTML param that contains the client Id of this control, followed by the node Id
      // followed by whether this is the parent node not a decendant (ellipses clicked)
      String param = control.getClientId(context) + NamingContainer.SEPARATOR_CHAR +
                     childRef.getChildRef().getId() + NamingContainer.SEPARATOR_CHAR +
                     Boolean.toString(ellipses);
      buf.append(Utils.generateFormSubmit(context, control, getHiddenFieldName(context, control), param));
      buf.append('"');
      Map attrs = control.getAttributes();
      if (attrs.get("style") != null)
      {
         buf.append(" style=\"")
            .append(attrs.get("style"))
            .append('"');
      }
      if (attrs.get("styleClass") != null)
      {
         buf.append(" class=")
            .append(attrs.get("styleClass"));
      }
      buf.append('>');
      
      if (ellipses == false)
      {
         // label is the name of the child node
         buf.append(Utils.encode(childRef.getQName().getLocalName()));
      }
      else
      {
         // TODO: allow the ellipses string to be set as component property?
         buf.append("...");
      }
      
      buf.append("</a>");
      
      return buf.toString();
   }
   
   
   // ------------------------------------------------------------------------------
   // Private helpers

   /**
    * Get the hidden field name for this node descendant component.
    * Build a shared field name from the parent form name and the string "ndec".
    * 
    * @return hidden field name shared by all node descendant components within the Form.
    */
   private static String getHiddenFieldName(FacesContext context, UIComponent component)
   {
      return Utils.getParentForm(context, component).getClientId(context) + NamingContainer.SEPARATOR_CHAR + "ndec";
   }
   
   /**
    * Use Spring JSF integration to return the node service bean instance
    * 
    * @param context    FacesContext
    * 
    * @return node service bean instance or throws runtime exception if not found
    */
   private static NodeService getNodeService(FacesContext context)
   {
      NodeService service = (NodeService)FacesContextUtils.getRequiredWebApplicationContext(context).getBean(Repository.NODE_SERVICE);
      if (service == null)
      {
         throw new IllegalStateException("Unable to obtain NodeService bean reference.");
      }
      
      return service;
   }
   
   private static final String DEFAULT_SEPARATOR = " | ";
}
