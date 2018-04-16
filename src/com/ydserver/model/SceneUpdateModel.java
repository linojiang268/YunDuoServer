package com.ydserver.model;

import java.util.List;

import com.ydserver.db.SceneCommandMsg;
import com.ydserver.db.SceneInfo;

/**
 * 场景新增、修改
 * 
 * @author Administrator
 * 
 */
public class SceneUpdateModel {
	public SceneInfo sceneInfo;
	public List<SceneCommandMsg> commandMsgs;
}
