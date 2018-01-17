/*
 * Copyright (c) 2002-2014, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.blog.service;

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.blog.business.DocContent;
import fr.paris.lutece.plugins.blog.business.DocContentHome;
import fr.paris.lutece.plugins.blog.business.HtmlDoc;
import fr.paris.lutece.plugins.blog.business.HtmlDocHome;
import fr.paris.lutece.plugins.blog.business.IndexerAction;
import fr.paris.lutece.plugins.blog.business.Tag;
import fr.paris.lutece.plugins.blog.business.TagHome;
import fr.paris.lutece.plugins.blog.business.portlet.HtmlDocPublicationHome;
import fr.paris.lutece.plugins.blog.service.docsearch.HtmlDocSearchService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.util.sql.TransactionManager;

/**
 * This Service manages document actions (create, move, delete, validate ...) and notify listeners.
 */
public class HtmlDocService
{

    private static HtmlDocService _singleton = new HtmlDocService( );

    /**
     * Get the unique instance of the service
     *
     * @return The unique instance
     */
    public static HtmlDocService getInstance( )
    {
        return _singleton;
    }

    /**
     * Create an htmlDoc
     * 
     * @param htmlDoc
     *            The HtmlDoc
     * @param docContent
     *            The Doc content
     */
    public void createDocument( HtmlDoc htmlDoc, DocContent docContent )

    {

        TransactionManager.beginTransaction( HtmldocsPlugin.getPlugin( ) );

        try
        {
            HtmlDocHome.addInitialVersion( htmlDoc );
            for ( Tag tag : htmlDoc.getTag( ) )
            {

                TagHome.create( tag.getIdTag( ), htmlDoc.getId( ), tag.getPriority( ) );
            }
            if ( docContent != null )
            {

                docContent.setIdHtmlDocument( htmlDoc.getId( ) );
                DocContentHome.create( docContent );

            }
            TransactionManager.commitTransaction( HtmldocsPlugin.getPlugin( ) );
        }
        catch( Exception e )
        {
            TransactionManager.rollBack( HtmldocsPlugin.getPlugin( ) );
            throw new AppException( e.getMessage( ), e );
        }

        HtmlDocSearchService.getInstance( ).addIndexerAction( htmlDoc.getId( ), IndexerAction.TASK_CREATE, HtmldocsPlugin.getPlugin( ) );

    }
    /**
     * Create an htmlDoc
     * 
     * @param htmlDoc
     *            The HtmlDoc
     * @param docContent
     *            The Doc content
     */
    public void createDocument( HtmlDoc htmlDoc, List<DocContent> docContent )

    {

        TransactionManager.beginTransaction( HtmldocsPlugin.getPlugin( ) );

        try
        {
            HtmlDocHome.addInitialVersion( htmlDoc );
            for ( Tag tag : htmlDoc.getTag( ) )
            {

                TagHome.create( tag.getIdTag( ), htmlDoc.getId( ), tag.getPriority( ) );
            }
            if ( docContent != null )
            {
            	for(DocContent docCont:docContent){
            		
            		docCont.setIdHtmlDocument( htmlDoc.getId( ) );
                    DocContentHome.create( docCont );
            	}
                

            }
            TransactionManager.commitTransaction( HtmldocsPlugin.getPlugin( ) );
        }
        catch( Exception e )
        {
            TransactionManager.rollBack( HtmldocsPlugin.getPlugin( ) );
            throw new AppException( e.getMessage( ), e );
        }

        HtmlDocSearchService.getInstance( ).addIndexerAction( htmlDoc.getId( ), IndexerAction.TASK_CREATE, HtmldocsPlugin.getPlugin( ) );

    }

    /**
     * Remvove an HtmlDoc
     * 
     * @param nId
     *            The HtmlDoc id
     */
    public void deleteDocument( int nId )

    {
        TransactionManager.beginTransaction( HtmldocsPlugin.getPlugin( ) );

        try
        {
            TagHome.removeTagDoc( nId );
            DocContentHome.remove( nId );
            HtmlDocHome.remove( nId );
            HtmlDocHome.removeVersions( nId );
            TransactionManager.commitTransaction( HtmldocsPlugin.getPlugin( ) );
        }
        catch( Exception e )
        {
            TransactionManager.rollBack( HtmldocsPlugin.getPlugin( ) );
            throw new AppException( e.getMessage( ), e );
        }
        HtmlDocSearchService.getInstance( ).addIndexerAction( nId, IndexerAction.TASK_DELETE, HtmldocsPlugin.getPlugin( ) );

    }

    /**
     * Update an HtmlDoc
     * 
     * @param htmlDoc
     *            The Ht-mlDoc
     * @param docContent
     *            The Doc Content
     */
    public void updateDocument( HtmlDoc htmlDoc, DocContent docContent )

    {
        TransactionManager.beginTransaction( HtmldocsPlugin.getPlugin( ) );

        try
        {
            HtmlDocHome.addNewVersion( htmlDoc );
            if ( docContent != null && DocContentHome.getDocsContent( htmlDoc.getId( ) ) != null )
            {

                docContent.setIdHtmlDocument( htmlDoc.getId( ) );
                DocContentHome.update( docContent );

            }
            else
                if ( docContent != null )
                {

                    docContent.setIdHtmlDocument( htmlDoc.getId( ) );
                    DocContentHome.create( docContent );
                }
            TagHome.removeTagDoc( htmlDoc.getId( ) );
            for ( Tag tag : htmlDoc.getTag( ) )
            {

                TagHome.create( tag.getIdTag( ), htmlDoc.getId( ), tag.getPriority( ) );
            }
            TransactionManager.commitTransaction( HtmldocsPlugin.getPlugin( ) );
        }
        catch( Exception e )
        {
            TransactionManager.rollBack( HtmldocsPlugin.getPlugin( ) );
            throw new AppException( e.getMessage( ), e );
        }

        HtmlDocSearchService.getInstance( ).addIndexerAction( htmlDoc.getId( ), IndexerAction.TASK_MODIFY, HtmldocsPlugin.getPlugin( ) );

    }
    /**
     * Update an HtmlDoc
     * 
     * @param htmlDoc
     *            The Ht-mlDoc
     * @param docContent
     *            The Doc Content
     */
    public void updateDocument( HtmlDoc htmlDoc, List<DocContent> docContent )

    {
        TransactionManager.beginTransaction( HtmldocsPlugin.getPlugin( ) );

        try
        {
            HtmlDocHome.addNewVersion( htmlDoc );
            if ( docContent != null  )
            {
            	DocContentHome.remove(htmlDoc.getId( ));
            	for(DocContent docCont: docContent){
            		
            		docCont.setIdHtmlDocument( htmlDoc.getId( ) );
                    DocContentHome.create(docCont);

            	}
                
            }
          
            TagHome.removeTagDoc( htmlDoc.getId( ) );
            for ( Tag tag : htmlDoc.getTag( ) )
            {

                TagHome.create( tag.getIdTag( ), htmlDoc.getId( ), tag.getPriority( ) );
            }
            TransactionManager.commitTransaction( HtmldocsPlugin.getPlugin( ) );
        }
        catch( Exception e )
        {
            TransactionManager.rollBack( HtmldocsPlugin.getPlugin( ) );
            throw new AppException( e.getMessage( ), e );
        }

        HtmlDocSearchService.getInstance( ).addIndexerAction( htmlDoc.getId( ), IndexerAction.TASK_MODIFY, HtmldocsPlugin.getPlugin( ) );

    }

    /**
     * Returns an instance of a htmlDoc whose identifier is specified in parameter
     * 
     * @param nIdDocument
     *            The htmlDoc primary key
     * @return an instance of HtmlDoc
     */
    public HtmlDoc loadDocument( int nIdDocument )

    {
        HtmlDoc htmlDoc = HtmlDocHome.findByPrimaryKey( nIdDocument );
        List<DocContent> docContent = DocContentHome.getDocsContentByHtmlDoc( nIdDocument );
        htmlDoc.setDocContent( docContent );
        htmlDoc.setTag( TagHome.loadByDoc( nIdDocument ) );

        return htmlDoc;

    }

    /**
     * Returns an instance of a htmlDoc without binairie file whose identifier is specified in parameter
     * 
     * @param nIdDocument
     *            The htmlDoc primary key
     * @return an instance of HtmlDoc
     */
    public HtmlDoc findByPrimaryKeyWithoutBinaries( int nIdDocument )

    {
        HtmlDoc htmlDoc = HtmlDocHome.findByPrimaryKey( nIdDocument );
        htmlDoc.setTag( TagHome.loadByDoc( nIdDocument ) );
        htmlDoc.setHtmldocPubilcation( HtmlDocPublicationHome.getDocPublicationByIdDoc( nIdDocument ) );

        return htmlDoc;

    }

    /**
     * Load the data of all the htmlDoc objects and returns them as a list
     * 
     * @return the list which contains the data of all the htmlDoc objects
     */
    public List<HtmlDoc> getListDocWhithContent( )

    {
        List<HtmlDoc> listHtmlDocsWhithContent = new ArrayList<HtmlDoc>( );
        List<HtmlDoc> listHtmlDocs = HtmlDocHome.getHtmlDocsList( );

        for ( HtmlDoc doc : listHtmlDocs )
        {

            listHtmlDocsWhithContent.add( loadDocument( doc.getId( ) ) );
        }

        return listHtmlDocsWhithContent;

    }

    /**
     * Returns a list of a htmlDoc without binairie file whose identifier is specified in parameter
     * 
     * @return the list which contains the data of all the htmlDoc objects
     */
    public List<HtmlDoc> getListDocWithoutBinaries( )

    {
        List<HtmlDoc> listHtmlDocsWithoutBinaries = new ArrayList<HtmlDoc>( );
        List<HtmlDoc> listHtmlDocs = HtmlDocHome.getHtmlDocsList( );

        for ( HtmlDoc doc : listHtmlDocs )
        {

            listHtmlDocsWithoutBinaries.add( findByPrimaryKeyWithoutBinaries( doc.getId( ) ) );
        }

        return listHtmlDocsWithoutBinaries;

    }

    /**
     * Load the data of all the htmlDoc objects whose tag is specified in parameter
     * 
     * @param tag
     *            Tag param
     * @return the list which contains the data of all the htmlDoc objects
     */
    public List<HtmlDoc> searchListDocByTag( Tag tag )

    {
        List<HtmlDoc> listHtmlDocs = HtmlDocHome.getHtmlDocsList( );

        for ( HtmlDoc doc : listHtmlDocs )
        {

            doc.setTag( TagHome.getTagListByDoc( doc.getId( ) ) );
        }

        return listHtmlDocs;

    }
    
    /**
     * Load the data of nLimit last modified blog objects and returns them as a list
     * 
     * @param nLimit
     *            number of blog
     * @return The list which contains the data of of nLimit last modified blog objects
     */
    public List<HtmlDoc> getlastModifiedBlogsList ( int nLimit )

    {
        List<HtmlDoc> listBlogsWithoutBinaries = new ArrayList<HtmlDoc>( );
        List<HtmlDoc> listBlogs = HtmlDocHome.getLastModifiedHtmlDocsList( nLimit );

        for ( HtmlDoc doc : listBlogs )
        {
        	HtmlDoc blog = new HtmlDoc( );
        	List<DocContent> docContent = DocContentHome.getDocsContentByHtmlDoc( doc.getId( ) );
            blog.setDocContent( docContent );
            blog.setTag( TagHome.loadByDoc( doc.getId( ) ) );

            listBlogsWithoutBinaries.add( blog );
        }

        return listBlogsWithoutBinaries;

    }

}
