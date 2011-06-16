package org.eclipse.ecf.salvo.ui.internal.dialogs;

import java.io.ByteArrayInputStream;

import org.apache.james.mime4j.parser.MimeStreamParser;
import org.eclipse.ecf.protocol.nntp.core.Debug;
import org.eclipse.ecf.protocol.nntp.core.ServerStoreFactory;
import org.eclipse.ecf.protocol.nntp.model.IArticle;
import org.eclipse.ecf.protocol.nntp.model.INewsgroup;
import org.eclipse.ecf.protocol.nntp.model.SALVO;
import org.eclipse.ecf.salvo.ui.internal.MimeArticleContentHandler;
import org.eclipse.ecf.salvo.ui.internal.editor.ArticleWidgetBuilder;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

public class ThisUserArticlesComposite extends Composite {
	private List articlesList;
	private Composite messageComposite;
	private IArticle[] articles;

	public static void showArticles(Shell parentShell, INewsgroup newsgroup) {

		Shell shell = new Shell(parentShell);
		shell.setText("Articles by "
				+ newsgroup.getServer().getServerConnection().getUser()
				+ " in " + newsgroup.getNewsgroupName());

		ThisUserArticlesComposite instance = new ThisUserArticlesComposite(
				shell, newsgroup, SWT.NULL);

		if (shell.getChildren().length != 0) {
			
			Point size = instance.getSize();
			shell.setLayout(new FillLayout());
			shell.layout();

			if (size.x == 0 && size.y == 0) {
				instance.pack();
				shell.pack();
			} else {
				Rectangle shellBounds = shell.computeTrim(0, 0, size.x, size.y);
				shell.setSize(shellBounds.width, shellBounds.height);
			}

			shell.open();
		}

	}

	public ThisUserArticlesComposite(Composite parent, INewsgroup newsgroup,
			int style) {

		super(parent, style);
		initUIComponents();
		initArticleList(newsgroup);

	}

	private void initUIComponents() {
		try {

			FormLayout thisLayout = new FormLayout();
			this.setLayout(thisLayout);
			this.setSize(537, 378);

			{
				messageComposite = new Composite(this, SWT.BORDER);
				FormData messageCompositeLData = new FormData();
				messageCompositeLData.left = new FormAttachment(0, 1000, 5);
				messageCompositeLData.top = new FormAttachment(0, 1000, 173);
				messageCompositeLData.width = 520;
				messageCompositeLData.height = 199;
				messageComposite.setLayoutData(messageCompositeLData);
				messageComposite.setLayout(new GridLayout());
			}

			{
				FormData articlesListLData = new FormData();
				articlesListLData.width = 521;
				articlesListLData.height = 155;
				articlesListLData.left = new FormAttachment(10, 1000, 0);
				articlesListLData.right = new FormAttachment(980, 1000, 0);
				articlesListLData.top = new FormAttachment(33, 1000, 0);
				articlesListLData.bottom = new FormAttachment(443, 1000, 0);
				articlesList = new List(this, SWT.SINGLE | SWT.BORDER
						| SWT.V_SCROLL);
				articlesList.setLayoutData(articlesListLData);
				articlesList.addSelectionListener(new SelectionListener() {

					public void widgetSelected(SelectionEvent arg0) {
						showArticleBody();
					}

					public void widgetDefaultSelected(SelectionEvent arg0) {
					}
				});

			}
			this.layout();

		} catch (Exception e) {
			Debug.log(getClass(), e);
		}
	}

	private void initArticleList(INewsgroup newsgroup) {
		articles = ServerStoreFactory.instance().getServerStoreFacade()
				.getThisUserArticles(newsgroup);

		if (articles.length == 0) {
			MessageDialog.openError(new Shell(), "Salvo Newsreader",
					"You haven't posted any article in the selected Newsgroup");
			this.dispose();
		}

		for (IArticle article : articles) {
			articlesList.add(article.getSubject());
		}
	}

	private void showArticleBody() {
		IArticle article = articles[articlesList.getSelectionIndex()];

		StringBuffer buffer = new StringBuffer();

		try {

			String[] body = (String[]) ServerStoreFactory.instance()
					.getServerStoreFacade().getArticleBody(article);

			for (String line : body) {
				buffer.append(line + SALVO.CRLF);
			}

			MimeArticleContentHandler handler = new MimeArticleContentHandler(
					article);
			MimeStreamParser parser = new MimeStreamParser();
			parser.setContentHandler(handler);

			parser.parse(new ByteArrayInputStream(buffer.toString().getBytes()));

			if (!messageComposite.isDisposed()) {
				for (Control child : messageComposite.getChildren()) {
					child.dispose();
				}
			}

			ArticleWidgetBuilder.build(messageComposite, article, handler);
			messageComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
					true, true));
			messageComposite.layout(true);

		} catch (Exception e) {
			Debug.log(getClass(), e);
		}

	}

}
