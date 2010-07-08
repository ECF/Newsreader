/*******************************************************************************
 *  Copyright (c) 2010 Weltevree Beheer BV, Remain Software & Industrial-TSI
 *                                                                      
 * All rights reserved. This program and the accompanying materials     
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at             
 * http://www.eclipse.org/legal/epl-v10.html                            
 *                                                                      
 * Contributors:                                                        
 *    Wim Jongman - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.salvo.ui.internal.editor;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ecf.protocol.nntp.core.Debug;
import org.eclipse.ecf.protocol.nntp.core.ServerStoreFactory;
import org.eclipse.ecf.protocol.nntp.model.IArticle;
import org.eclipse.ecf.protocol.nntp.model.INewsgroup;
import org.eclipse.ecf.protocol.nntp.model.IServerStoreFacade;
import org.eclipse.ecf.protocol.nntp.model.NNTPException;
import org.eclipse.ecf.protocol.nntp.model.NNTPIOException;
import org.eclipse.ecf.protocol.nntp.model.SALVO;
import org.eclipse.ecf.protocol.nntp.model.StoreException;
import org.eclipse.ecf.protocol.nntp.model.UnexpectedResponseException;
import org.eclipse.ecf.salvo.ui.internal.resources.ISalvoResource;
import org.eclipse.ecf.salvo.ui.internal.resources.SalvoResourceFactory;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ILazyTreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;


public class ArticlePanelContentProvider implements ILazyTreeContentProvider {
	
	private INewsgroup newsgroup;

	private TreeViewer viewer;

	private int currentIndex;

	private IServerStoreFacade storeFacade;

	private int childrenCount;

	private boolean eof;

	private IArticle[] articles;

	private boolean checkBusy;

	private int lastArticle;

	private int articleOffset;

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput == null)
			return;

		checkBusy = false;

		this.viewer = (TreeViewer) viewer;
		newsgroup = (INewsgroup) newInput;
		storeFacade = ServerStoreFactory.instance().getServerStoreFacade();
		this.viewer.getTree().removeAll();
		currentIndex = 0;
		childrenCount = 0;
		articleOffset = 0;
		lastArticle = newsgroup.getHighWaterMark() + 1;
		eof = false;
		Debug.log(getClass(), "setinput : "
				+ newInput.getClass().getSimpleName() + ": "
				+ newsgroup.getArticleCount());

		int from = newsgroup.getLowWaterMark();
		int to = newsgroup.getHighWaterMark();

		this.viewer.setChildCount(newInput, from - to);
		try {
			getNextBatch();
		} catch (Exception e) {
			Debug.log(getClass(), e);
		}
	}

	public Object getParent(Object element) {
		if (element instanceof ISalvoResource) {
			return ((ISalvoResource) element).getParent();
		}
		return null;
	}

	public void updateChildCount(Object element, int currentChildCount) {
		int timer = Debug.timerStart(getClass());
		Debug.log(getClass(), "Update child count: "
				+ element.getClass().getSimpleName() + ". currentChildCount = "
				+ currentChildCount);

		if (element == newsgroup) {
			if (eof)
				viewer.setChildCount(element, childrenCount);
			else
				viewer
						.setChildCount(
								element,
								newsgroup.getArticleCount() > SALVO.BATCH_SIZE ? SALVO.BATCH_SIZE
										: newsgroup.getArticleCount());
		} else
			viewer.setChildCount(element, currentChildCount);

		Debug.timerStop(getClass(), timer);
	}

	public void updateElement(Object parent, int index) {

		if (checkBusy)
			return;

		checkBusy = true;
		Debug.log(getClass(), "#updateElement: " + index);

		if (parent == newsgroup) {

			while (index >= childrenCount) {
				if (currentIndex == articles.length) {

					// Are there more articles?
					if (lastArticle == newsgroup.getLowWaterMark()) {
						eof = true;
						viewer.setChildCount(parent, childrenCount);
						break;
					}

					// Yes there are more. Try to fetch them. If the user
					// canceled or another error occurred then assume end
					// of articles
					articleOffset += childrenCount;
					try {
						Debug.log(getClass(), "starting from getnextbatch");
						getNextBatch();
						Debug.log(getClass(), "Returned from getnextbatch");
					} catch (Exception e) {
						Debug.log(getClass(), "Exception from getnextbatch");
						Debug.log(getClass(), e.getMessage());
						eof = true;
						viewer.setChildCount(parent, childrenCount);
						break;
					}

					// Get ready for the next batch
					currentIndex = 0;
					viewer.setChildCount(newsgroup, articles.length
							+ articleOffset + SALVO.BATCH_SIZE);
				}

				// If this article is a first post (i.e. not a reply) then load
				// it in the table
				IArticle article = articles[currentIndex];

				lastArticle = article.getArticleNumber();
				if (article.getReferences().length == 0) {
					IArticle[] replies;
					try {
						replies = storeFacade.getAllFollowUps(article);
					} catch (NNTPIOException e) {
						// FIXME do some clever stuff
						Debug.log(getClass(), e);
						replies = new IArticle[0];
					} catch (UnexpectedResponseException e) {
						// FIXME do some clever stuff
						Debug.log(getClass(), e);
						replies = new IArticle[0];
					} catch (StoreException e) {
						// FIXME do some clever stuff
						Debug.log(getClass(), e);
						replies = new IArticle[0];
					}

					// update ui attributes of this article
					article.setThreadAttributes(replies);

					ISalvoResource resource = SalvoResourceFactory.getResource(
							article.getSubject(), article);
					viewer.replace(parent, childrenCount, resource);
					Debug.log(getClass(), "added child " + childrenCount);

					try {
						updateChildCount(resource, storeFacade.getFollowUps(
								article).length);
					} catch (NNTPException e) {
						updateChildCount(resource, 0);
					}

					childrenCount++;
				}
				currentIndex++;
			}
		}

		else {
			if (parent instanceof ISalvoResource
					&& ((ISalvoResource) parent).getObject() instanceof IArticle) {
				IArticle parentArticle = (IArticle) ((ISalvoResource) parent)
						.getObject();
				try {
					IArticle article = (IArticle) storeFacade.getFollowUps(
							(IArticle) parentArticle)[index];
					ISalvoResource resource = SalvoResourceFactory.getResource(
							article.getSubject(), article);
					viewer.replace(parent, index, resource);
					updateChildCount(resource, storeFacade
							.getFollowUps(article).length);
				} catch (NNTPException e) {
					Debug.log(getClass(), "This should not go wrong");
					Debug.log(getClass(), e);
				}
			}
		}

		checkBusy = false;
	}

	/**
	 * This method gets the next batch of articles with progress.
	 * 
	 * @throws InvocationTargetException
	 * @throws InterruptedException
	 */
	private void getNextBatch() throws InvocationTargetException,
			InterruptedException {

		IRunnableWithProgress runner = new IRunnableWithProgress() {

			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {

				monitor.beginTask("Fetching a batch of articles from "
						+ newsgroup.getNewsgroupName() + ".", 200); // IProgressMonitor.UNKNOWN);

				Runnable runner = new Runnable() {

					public void run() {
						try {
							articles = storeFacade.getArticles(newsgroup, newsgroup
									.getLowWaterMark(), lastArticle - 1);
						} catch (NNTPException e) {
						Debug.log(getClass(), e);
						}
					}
				};
				Thread thread = new Thread(runner);

				thread.start();

				int counter = 0;

				while (thread.isAlive()) {
					Thread.sleep(50);
					if (counter > 100)
						Display.getDefault().readAndDispatch();
					if (monitor.isCanceled())
						throw new InterruptedException(
								"User pressed cancel while Salvo was fetching articles.");
					monitor.worked(1);
					if (counter == 100) {
						monitor
								.subTask("Cancel button is now active, but the fetch is still running. Please be patient.");
					}
					counter++;
				}
			}
		};

		try {
			viewer.getTree().setRedraw(false);
			new ProgressMonitorDialog(viewer.getControl().getShell()).run(
					false, true, runner);
		} catch (InvocationTargetException e1) {
			Debug.log(getClass(), e1);
			throw e1;
		} catch (InterruptedException e1) {
			Debug.log(getClass(), e1);
			throw e1;
		} finally {
			viewer.getTree().setRedraw(true);
		}
	}

	public void dispose() {
	}

}