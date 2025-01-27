/*******************************************************************************
 * Copyright (c) 2013 hangum.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     hangum - initial API and implementation
 ******************************************************************************/
package com.hangum.tadpole.engine.sql.dialog.save;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.hangum.tadpold.commons.libs.core.define.PublicTadpoleDefine;
import com.hangum.tadpold.commons.libs.core.define.PublicTadpoleDefine.RESOURCE_TYPE;
import com.hangum.tadpole.commons.dialogs.message.TadpoleSimpleMessageDialog;
import com.hangum.tadpole.commons.google.analytics.AnalyticCaller;
import com.hangum.tadpole.commons.util.Utils;
import com.hangum.tadpole.engine.Messages;
import com.hangum.tadpole.engine.query.dao.system.UserDBDAO;
import com.hangum.tadpole.engine.query.dao.system.UserDBResourceDAO;
import com.hangum.tadpole.engine.query.sql.TadpoleSystem_UserDBResource;
import com.hangum.tadpole.session.manager.SessionManager;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

/**
 * Resource save dialog
 * 
 * @author hangum
 *
 */
public class ResourceSaveDialog extends Dialog {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(ResourceSaveDialog.class);
	
	private int BTN_SHOW_URL = IDialogConstants.CLIENT_ID + 1; 
	
	/** 화면에 초기 값을 뿌려 주어야 한다면 사용한다 */
	private UserDBResourceDAO initDBResource;
	
	private UserDBDAO userDB;
	private PublicTadpoleDefine.RESOURCE_TYPE resourceType;
	
	private Text textName;
	private Text textDescription;
	private Combo comboSharedType;
	
	private Combo comboUseAPI;
	private Text textAPIKey;
	
	/** return UserDBResourceDAO */
	private UserDBResourceDAO retResourceDao = new UserDBResourceDAO();

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 * @param userDB
	 * @param resourceType
	 */
	public ResourceSaveDialog(Shell parentShell, UserDBResourceDAO initDBResource, UserDBDAO userDB, PublicTadpoleDefine.RESOURCE_TYPE resourceType) {
		super(parentShell);
		
		if(initDBResource == null) this.initDBResource = new UserDBResourceDAO();
		else this.initDBResource = initDBResource;
		
		this.userDB = userDB;
		this.resourceType = resourceType;
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.ResourceSaveDialog_3);
	}
	
	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.verticalSpacing = 5;
		gridLayout.horizontalSpacing = 5;
		gridLayout.marginHeight = 5;
		gridLayout.marginWidth = 5;
		gridLayout.numColumns = 2;
		
		Label lblName = new Label(container, SWT.NONE);
		lblName.setText(Messages.ResourceSaveDialog_0);
		
		textName = new Text(container, SWT.BORDER);
		textName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textName.setText(initDBResource.getName());
		
		Label lblSharedType = new Label(container, SWT.NONE);
		lblSharedType.setText(Messages.ResourceSaveDialog_1);
		
		comboSharedType = new Combo(container, SWT.READ_ONLY);
		comboSharedType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		for(PublicTadpoleDefine.SHARED_TYPE type : PublicTadpoleDefine.SHARED_TYPE.values()) {
			comboSharedType.add(type.toString());
		}
		comboSharedType.select(0);
		
		Label lblDescription = new Label(container, SWT.NONE);
		lblDescription.setText(Messages.ResourceSaveDialog_2);
		
		textDescription = new Text(container, SWT.BORDER | SWT.WRAP | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		textDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		textDescription.setText(initDBResource.getDescription());
		
		Label lblUseApi = new Label(container, SWT.NONE);
		lblUseApi.setText(Messages.ResourceSaveDialog_lblUseApi_text);
		
		comboUseAPI = new Combo(container, SWT.READ_ONLY);
		comboUseAPI.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(PublicTadpoleDefine.YES_NO.YES.name().equals(comboUseAPI.getText())) {
					String strApiName = textAPIKey.getText();
					if("".equals(strApiName)) {
						textAPIKey.setText(Utils.getUniqueID());
					}
				}
			}
		});
		comboUseAPI.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboUseAPI.add(PublicTadpoleDefine.YES_NO.YES.name());
		comboUseAPI.add(PublicTadpoleDefine.YES_NO.NO.name());
		comboUseAPI.select(1);
		
		Label lblApiName = new Label(container, SWT.NONE);
		lblApiName.setText(Messages.ResourceSaveDialog_lblApiName_text);
		
		textAPIKey = new Text(container, SWT.BORDER);
		textAPIKey.setEditable(false);
		textAPIKey.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		initUI();

		return container;
	}
	
	/**
	 * initialize ui
	 */
	private void initUI() {
		textName.setFocus();
		
		// google analytic
		AnalyticCaller.track(this.getClass().getName());
				
	}
	
	@Override
	protected void okPressed() {
		String errMsg = isValid();
		if(null != errMsg) {
			MessageDialog.openError(null, "Confirm", errMsg); //$NON-NLS-1$
			textName.setFocus();
			return;
		}
		
		retResourceDao.setDb_seq(userDB.getSeq());
		retResourceDao.setResource_types(resourceType.toString());
		retResourceDao.setUser_seq(SessionManager.getUserSeq());
		
		retResourceDao.setName(textName.getText().trim());
		retResourceDao.setShared_type(comboSharedType.getText());
		retResourceDao.setDescription(textDescription.getText());
		
		retResourceDao.setRestapi_yesno(comboUseAPI.getText());
		retResourceDao.setRestapi_key(textAPIKey.getText());
		
		// Checking duplication name
		try {
			if(!TadpoleSystem_UserDBResource.userDBResourceDuplication(resourceType, userDB.getUser_seq(), userDB.getSeq(), textName.getText())) {
				MessageDialog.openError(null, "Duplication", Messages.ResourceSaveDialog_5); //$NON-NLS-1$
				return;
			}
		} catch (Exception e) {
			logger.error("SQL Editor File validator", e); //$NON-NLS-1$
			
			return;
		}
		
		super.okPressed();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
	 */
	@Override
	protected void buttonPressed(int buttonId) {
		if(buttonId == BTN_SHOW_URL) {
			String strApiKey = textAPIKey.getText();
			if(RESOURCE_TYPE.ERD == resourceType | strApiKey.equals("")) {
				MessageDialog.openError(getShell(), "Confirm", "Does not support API.");
				return;
			}
			String strServerURL = "http://{yoururl.com}";
//			String strQuery  = textQuery.getText();
			String strArguments = "1={FirstParameter}&2={SecondParameter}";
			
			// api server url
			String strURL = String.format("[API Server URL]\n%s?%s=%s&%s", 
					strServerURL + "/rest/base", 
					PublicTadpoleDefine.SERVICE_KEY_NAME, 
					textAPIKey.getText(),
					strArguments);
			
			// api dialog url
			strURL += String.format("\n\n[API Dialog URL]\n%s?%s=%s&%s", 
					strServerURL, 
					PublicTadpoleDefine.SERVICE_KEY_NAME, 
					textAPIKey.getText(),
					strArguments);

			TadpoleSimpleMessageDialog dialog = new TadpoleSimpleMessageDialog(getShell(), "API URL Information", strURL);
			dialog.open();
		} else {
			super.buttonPressed(buttonId);
		}
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, BTN_SHOW_URL, "Show URL", true); //$NON-NLS-1$
		createButton(parent, IDialogConstants.OK_ID, "Save", true); //$NON-NLS-1$
		createButton(parent, IDialogConstants.CANCEL_ID, "Cancel", false); //$NON-NLS-1$
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}
	
	/**
	 * data validation
	 * 
	 * @param name
	 * @return
	 */
	public String isValid() {
		int len = textName.getText().trim().length();
		if(len < 3) return "The name must enter at least 3 characters."; //$NON-NLS-1$
		
		return null;
	}
	
	/**
	 * return ResourceDAO
	 * 
	 * @return
	 */
	public UserDBResourceDAO getRetResourceDao() {
		return retResourceDao;
	}
}
