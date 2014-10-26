/*******************************************************************************
 * Copyright (c) 2014 hangum.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     hangum - initial API and implementation
 ******************************************************************************/
package com.hangum.tadpole.rdb.core.extensionpoint.maineditor;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.hangum.tadpole.sql.dao.system.UserDBDAO;

/**
 * MainEditor extension
 * 
 * @author hangum
 *
 */
public abstract class IMainEditorExtension {
	
	/**
	 * 이 익스텐션을 동작가능 한지?
	 */
	private boolean enableExtension = false;
	
	/**
	 * 현재 보여지고 있는 데이터베이스.
	 */
	protected UserDBDAO editorUserDB = null;
	
	/**
	 * 메인 에디터에서 UI가 위치할 위치 지정.
	 * 
	 *  SWT.LEFT
	 *	SWT.RIGHT
	 *	SWT.TOP
	 *	SWT.BOTTOM
	 */
	protected int location = SWT.RIGHT;

	/**
	 * user create part control
	 * 
	 * @param parent
	 */
	public abstract void createPartControl(Composite parent); //{
//		initExtension();
//	}
	
	/**
	 * 화면을 초기화 합니다.
	 * 1. 초기 화면이 보여야 하는지 설정합니다. 
	 */
	public void initExtension(UserDBDAO userDB) {
		this.editorUserDB = userDB;
		enableExtension = true;
		
	}
	
	/**
	 * resultSetDoubleClick
	 * 
	 * @param selectIndex  select index
	 * @param mapColumns column <index, value>
	 */
	public abstract void resultSetDoubleClick(int selectIndex, Map<Integer, Object> mapColumns);

	/**
	 * 현재 에디터에서 사용하고 있는 extension
	 * @return
	 */
	public UserDBDAO getEditorUserDB() {
		return editorUserDB;
	}

	/**
	 * @return the enableExtension
	 */
	public boolean isEnableExtension() {
		return enableExtension;
	}

	/**
	 * @param enableExtension the enableExtension to set
	 */
	public void setEnableExtension(boolean enableExtension) {
		this.enableExtension = enableExtension;
	}

	/**
	 * @return the location
	 */
	public int getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(int location) {
		this.location = location;
	}
	
}