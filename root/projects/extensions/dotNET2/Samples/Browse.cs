using System;
using System.Collections.Generic;
using System.Collections;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using Alfresco;
using Alfresco.RepositoryWebService;
using Alfresco.ContentWebService;
using Microsoft.Web.Services3;
using Microsoft.Web.Services3.Security;
using Microsoft.Web.Services3.Security.Tokens;
using Microsoft.Web.Services3.Security.Utility;

namespace Samples
{
    public partial class Browse : Form
    {
        private Alfresco.RepositoryWebService.Store spacesStore;

        private RepositoryService repoService;
        private ContentService contentService;

        private Alfresco.RepositoryWebService.Reference currentReference;
        private ArrayList parentReferences = new ArrayList();

        /// <summary>
        /// Default constructor
        /// </summary>
        public Browse()
        {
            InitializeComponent();

            // Initialise the reference to the spaces store
            this.spacesStore = new Alfresco.RepositoryWebService.Store();
            this.spacesStore.scheme = Alfresco.RepositoryWebService.StoreEnum.workspace;
            this.spacesStore.address = "SpacesStore";
        }

        /// <summary>
        /// The form load event handler
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void Browse_Load(object sender, EventArgs e)
        {
            // Ensure the user has been authenticated
            if (AuthenticationUtils.IsSessionValid == false)
            {
                if (Login.doLogin() == false)
                {
                    Application.Exit();
                }
            }

            // Create the repo service and set the authentication credentials
            UsernameToken userToken = new UsernameToken(AuthenticationUtils.UserName, AuthenticationUtils.Ticket, (PasswordOption)2);
            this.repoService = WebServiceFactory.getRepositoryService();
            this.repoService.RequestSoapContext.Security.Timestamp.TtlInSeconds = (long)300;
            this.repoService.RequestSoapContext.Security.Tokens.Add(userToken);

            this.contentService = WebServiceFactory.getContentService();
            this.contentService.RequestSoapContext.Security.Timestamp.TtlInSeconds = (long)300;
            this.contentService.RequestSoapContext.Security.Tokens.Add(userToken);

            // Populate the list box 
            populateListBox();
        }

        /// <summary>
        /// Populate the list box with the children of the company home
        /// </summary>
        private void populateListBox()
        {
            Alfresco.RepositoryWebService.Reference reference = new Alfresco.RepositoryWebService.Reference();
            reference.store = this.spacesStore;
            reference.path = "/app:company_home";

            populateListBox(reference);
        }

        /// <summary>
        /// Populate the list with the children of the passed reference
        /// </summary>
        /// <param name="reference"></param>
        private void populateListBox(Alfresco.RepositoryWebService.Reference reference)
        {
            // Clear the list
            listViewBrowse.Clear();

            // Set the current reference
            this.currentReference = reference;

            // Query for the children of the reference
            QueryResult result = this.repoService.queryChildren(reference);

            if (result.resultSet.rows != null)
            {
                int index = 0;
                foreach (ResultSetRow row in result.resultSet.rows)
                {
                    // Get the name of the node
                    string name = null;
                    foreach (NamedValue namedValue in row.columns)
                    {
                        if (namedValue.name.Contains("name") == true)
                        {
                            name = namedValue.value;
                        }
                    }

                    // Create the list view item that will correspond to the child node
                    ListViewItem item = new ListViewItem();
                    item.Text = name;
                    if (row.node.type.Contains("folder") == true)
                    {
                        item.ImageIndex = 0;
                    }
                    else
                    {
                        item.ImageIndex = 1;
                    }
                    item.Tag = row.node;

                    // Add the item to the list
                    if (row.node.type.Contains("folder") == true)
                    {
                        listViewBrowse.Items.Insert(index, item);
                        index++;
                    }
                    else
                    {
                        listViewBrowse.Items.Add(item);
                    }
                }
            }
        }

        /// <summary>
        /// Double click event handler
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void listViewBrowse_DoubleClick(object sender, EventArgs e)
        {
            ListViewItem item = listViewBrowse.SelectedItems[0];
            if (item != null)
            {
                ResultSetRowNode node = item.Tag as ResultSetRowNode; 
                if (node != null)
                {
                    if (node.type.Contains("folder") == true)
                    {
                        // Create the reference for the node selected
                        Alfresco.RepositoryWebService.Reference reference = new Alfresco.RepositoryWebService.Reference();
                        reference.store = this.spacesStore;
                        reference.uuid = node.id;

                        // Parent references
                        this.parentReferences.Add(this.currentReference);

                        // Populate the list with the children of the selected node
                        populateListBox(reference);
                    }
                    else
                    {
                        // Create the reference for the node selected
                        Alfresco.ContentWebService.Store spacesStore2 = new Alfresco.ContentWebService.Store();
                        spacesStore2.scheme = Alfresco.ContentWebService.StoreEnum.workspace;
                        spacesStore2.address = "SpacesStore";

                        Alfresco.ContentWebService.Reference reference = new Alfresco.ContentWebService.Reference();
                        reference.store = spacesStore2;
                        reference.uuid = node.id;

                        // Lets try and get the content
                        Alfresco.ContentWebService.Predicate predicate = new Alfresco.ContentWebService.Predicate();
                        predicate.Items = new Object[] {reference};
                        Content[] contents = this.contentService.read(predicate, "{http://www.alfresco.org/model/content/1.0}content");
                        Content content = contents[0];
                        string url = content.url + "?ticket=" + AuthenticationUtils.Ticket;
                        webBrowser.Url = new Uri(url);
                    }
                }
            }
        }

        /// <summary>
        /// Click handler for the 'Go Up' button
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void toolStripButton1_Click(object sender, EventArgs e)
        {
            if (this.parentReferences.Count != 0)
            {
                Alfresco.RepositoryWebService.Reference reference = this.parentReferences[this.parentReferences.Count - 1] as Alfresco.RepositoryWebService.Reference;
                this.parentReferences.RemoveAt(this.parentReferences.Count - 1);
                populateListBox(reference);
            }
        }
    }
}