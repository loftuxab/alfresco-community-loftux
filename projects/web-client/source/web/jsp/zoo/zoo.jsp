<%--
  Copyright (C) 2005 Alfresco, Inc.

  Licensed under the GNU Lesser General Public License as
  published by the Free Software Foundation; either version
  2.1 of the License, or (at your option) any later version.
  You may obtain a copy of the License at

    http://www.gnu.org/licenses/lgpl.txt

  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
  either express or implied. See the License for the specific
  language governing permissions and limitations under the
  License.
--%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/alfresco.tld" prefix="a" %>
<%@ taglib uri="/WEB-INF/repo.tld" prefix="r" %>

<%@ page buffer="32kb" %>
<%@ page isELIgnored="false" %>

<r:page>

<f:view>
   
   <h2>The Zoo</h2>
   
   <h:form id="zooForm">
      
      <h:commandButton id="show-components-zoo" value="Component Zoo" action="showRichListZoo" />
      <br/><br/>
      <%--
      <h:commandButton id="show-property-zoo" value="Property Zoo" action="showPropertyZoo" />
      <br/><br/>
      <h:commandButton id="show-auto-property-zoo" value="Auto Property Zoo" action="showAutoPropertyZoo" />
      <br/><br/>
      --%>
      <h:commandButton id="show-image-picker" value="Image Picker Zoo" action="showImagePickerZoo" />
      <br/><br/>
      <h:commandButton id="dyna-desc" value="Dynamic Description Zoo" action="showDynaDescZoo" />
      <br/><br/>
      <h:commandButton id="show-user-list" value="UserList Test Pages" action="showUserlist" />
      
      <p/><p/>
      <hr/><p/>
      <h:commandButton id="show-web-client" value="Back to the Web Client" action="showWebClient" />
      
   </h:form>
   
</f:view>

</r:page>