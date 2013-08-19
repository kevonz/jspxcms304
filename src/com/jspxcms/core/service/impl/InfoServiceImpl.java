package com.jspxcms.core.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jspxcms.core.domain.Info;
import com.jspxcms.core.domain.InfoBuffer;
import com.jspxcms.core.domain.InfoDetail;
import com.jspxcms.core.domain.InfoFile;
import com.jspxcms.core.domain.InfoImage;
import com.jspxcms.core.domain.Node;
import com.jspxcms.core.domain.User;
import com.jspxcms.core.domain.Workflow;
import com.jspxcms.core.listener.InfoDeleteListener;
import com.jspxcms.core.listener.InfoListener;
import com.jspxcms.core.listener.NodeDeleteListener;
import com.jspxcms.core.listener.OrgDeleteListener;
import com.jspxcms.core.listener.SiteDeleteListener;
import com.jspxcms.core.listener.UserDeleteListener;
import com.jspxcms.core.repository.InfoDao;
import com.jspxcms.core.service.InfoAttributeService;
import com.jspxcms.core.service.InfoBufferService;
import com.jspxcms.core.service.InfoDetailService;
import com.jspxcms.core.service.InfoNodeService;
import com.jspxcms.core.service.InfoService;
import com.jspxcms.core.service.InfoSpecialService;
import com.jspxcms.core.service.InfoTagService;
import com.jspxcms.core.service.NodeService;
import com.jspxcms.core.service.SiteService;
import com.jspxcms.core.service.UserService;
import com.jspxcms.core.service.WorkflowService;
import com.jspxcms.core.support.DeleteException;

/**
 * 信息Service实现
 * 
 * @author liufang
 * 
 */
@Service
@Transactional
public class InfoServiceImpl implements InfoService, SiteDeleteListener,
		OrgDeleteListener, NodeDeleteListener, UserDeleteListener {
	public Info save(Info bean, InfoDetail detail, Integer[] nodeIds,
			Integer[] specialIds, Map<String, String> customs,
			Map<String, String> clobs, List<InfoImage> images,
			List<InfoFile> files, Integer[] attrIds,
			Map<String, String> attrImages, String[] tagNames, Integer nodeId,
			Integer creatorId, Boolean draft, Integer siteId) {
		bean.setSite(siteService.get(siteId));
		User creator = userService.get(creatorId);
		bean.setCreator(creator);
		bean.setOrg(creator.getOrg());
		Node node = nodeService.refer(nodeId);
		bean.setNode(node);
		bean.setCustoms(customs);
		bean.setClobs(clobs);
		bean.setImages(images);
		bean.setFiles(files);

		if (detail != null && StringUtils.isNotBlank(detail.getSmallImage())) {
			bean.setWithImage(true);
		}
		Workflow workflow = null;
		// 流程处理
		if (draft != null && draft) {
			// 草稿
			bean.setStatus(Info.DRAFT);
		} else {
			workflow = node.getWorkflow();
			if (workflow != null) {
				bean.setStatus(Info.AUDITING);
			} else {
				bean.setStatus(Info.NORMAL);
			}
		}

		bean.applyDefaultValue();
		bean = dao.save(bean);
		infoDetailService.save(detail, bean);
		// 将InfoBuffer对象一并保存，以免在网页浏览时再保存，导致并发保存报错
		infoBufferService.save(new InfoBuffer(), bean);
		infoAttrService.save(bean, attrIds, attrImages);
		infoNodeService.save(bean, nodeIds, nodeId);
		infoTagService.save(bean, tagNames);
		infoSpecialService.save(bean, specialIds);

		if (workflow != null) {
			int step = workflowService.auditPass(workflow, creator, creator,
					Info.WORKFLOW_TYPE, bean.getId(), null);
			if (step >= 0) {
				// 状态最大值只支持9
				if (step > 9) {
					step = 9;
				}
				bean.setStatus(Integer.toString(step));
			} else {
				bean.setStatus(Info.NORMAL);
			}
		}
		firePostSave(new Info[] { bean });
		return bean;
	}

	public Info update(Info bean, InfoDetail detail, Integer[] nodeIds,
			Integer[] specialIds, Map<String, String> customs,
			Map<String, String> clobs, List<InfoImage> images,
			List<InfoFile> files, Integer[] attrIds,
			Map<String, String> attrImages, String[] tagNames, Integer nodeId,
			Boolean draft) {
		if (detail != null && StringUtils.isNotBlank(detail.getSmallImage())) {
			bean.setWithImage(true);
		}

		if (draft != null) {
			if (draft) {
				bean.setStatus(Info.DRAFT);
			} else {
				bean.setStatus(Info.NORMAL);
			}
		}

		bean.applyDefaultValue();
		bean = dao.save(bean);
		if (nodeId != null) {
			nodeService.derefer(bean.getNode());
			bean.setNode(nodeService.refer(nodeId));
		}
		bean.getCustoms().clear();
		if (!CollectionUtils.isEmpty(customs)) {
			bean.getCustoms().putAll(customs);
		}
		bean.getClobs().clear();
		if (!CollectionUtils.isEmpty(clobs)) {
			bean.getClobs().putAll(clobs);
		}
		bean.getImages().clear();
		if (!CollectionUtils.isEmpty(images)) {
			bean.getImages().addAll(images);
		}
		bean.getFiles().clear();
		if (!CollectionUtils.isEmpty(files)) {
			bean.getFiles().addAll(files);
		}

		infoDetailService.update(detail, bean);
		infoAttrService.update(bean, attrIds, attrImages);
		infoNodeService.update(bean, nodeIds, nodeId);
		infoTagService.update(bean, tagNames);
		infoSpecialService.update(bean, specialIds);
		firePostUpdate(new Info[] { bean });
		return bean;
	}

	public Info[] auditPass(Integer[] ids, Integer userId, String opinion) {
		Info info;
		Workflow workflow;
		User operator = userService.get(userId);
		List<Info> infos = new ArrayList<Info>();
		for (Integer id : ids) {
			info = dao.findOne(id);
			String status = info.getStatus();
			// 审核中、退稿、草稿、投稿、采集可审核通过。
			if (info.isAuditing() || status.equals(Info.DRAFT)
					|| status.equals(Info.CONTRIBUTE)
					|| status.equals(Info.COLLECTED)
					|| status.equals(Info.REJECTION)) {
				workflow = info.getNode().getWorkflow();
				int step = workflowService.auditPass(workflow,
						info.getCreator(), operator, Info.WORKFLOW_TYPE,
						info.getId(), opinion);
				if (step == 0) {
					continue;
				}
				if (step == -1) {
					info.setStatus(Info.NORMAL);
				} else if (step > 0) {
					if (step > 9) {
						step = 9;
					}
					info.setStatus(String.valueOf(step));
				}
				infos.add(info);
			}
		}
		return infos.toArray(new Info[infos.size()]);
	}

	public Info[] auditReject(Integer[] ids, Integer userId, String opinion) {
		Info info;
		Workflow workflow;
		User operator = userService.get(userId);
		List<Info> infos = new ArrayList<Info>();
		for (Integer id : ids) {
			info = dao.findOne(id);
			String status = info.getStatus();
			// 审核中、已终审可审核退回。
			if (info.isAuditing() || status.equals(Info.NORMAL)) {
				workflow = info.getNode().getWorkflow();
				int step = workflowService.auditReject(workflow,
						info.getCreator(), operator, Info.WORKFLOW_TYPE,
						info.getId(), opinion);
				if (step == 0) {
					// 没有任何操作
					continue;
				}
				if (step == -1) {
					info.setStatus(Info.REJECTION);
				} else if (step > 0) {
					if (step > 9) {
						step = 9;
					}
					info.setStatus(String.valueOf(step));
				}
				infos.add(info);
			}
		}
		return infos.toArray(new Info[infos.size()]);
	}

	public Info[] submit(Integer[] ids, Integer userId) {
		Info info;
		Workflow workflow;
		User operator = userService.get(userId);
		List<Info> infos = new ArrayList<Info>();
		for (Integer id : ids) {
			info = dao.findOne(id);
			if (info.getStatus().equals(Info.DRAFT)
					|| info.getStatus().equals(Info.REJECTION)
					|| info.getStatus().equals(Info.COLLECTED)) {
				workflow = info.getNode().getWorkflow();
				int step = workflowService.auditPass(workflow,
						info.getCreator(), operator, Info.WORKFLOW_TYPE,
						info.getId(), null);
				if (step == 0) {
					continue;
				}
				if (step > 0) {
					// 状态最大值只支持9
					if (step > 9) {
						step = 9;
					}
					info.setStatus(Integer.toString(step));
				} else if (step == -1) {
					info.setStatus(Info.NORMAL);
				}
				infos.add(info);
			}
		}
		return infos.toArray(new Info[infos.size()]);
	}

	public Info[] antiSubmit(Integer[] ids) {
		Info info;
		List<Info> infos = new ArrayList<Info>();
		for (Integer id : ids) {
			info = dao.findOne(id);
			if (info.getStatus().equals(Info.REJECTION)) {
				info.setStatus(Info.DRAFT);
				infos.add(info);
			}
		}
		return infos.toArray(new Info[infos.size()]);
	}

	private Info doDelete(Integer id) {
		Info entity = dao.findOne(id);
		if (entity != null) {
			dao.delete(entity);
		}
		return entity;
	}

	public Info delete(Integer id) {
		firePreDelete(new Integer[] { id });
		Info bean = doDelete(id);
		if (bean != null) {
			firePostDelete(new Info[] { bean });
		}
		return bean;
	}

	public Info[] delete(Integer[] ids) {
		firePreDelete(ids);
		List<Info> list = new ArrayList<Info>(ids.length);
		Info bean;
		for (int i = 0; i < ids.length; i++) {
			bean = delete(ids[i]);
			if (bean != null) {
				list.add(bean);
			}
		}
		Info[] beans = list.toArray(new Info[list.size()]);
		firePostDelete(beans);
		return beans;
	}

	public void preUserDelete(Integer[] ids) {
		if (ArrayUtils.isNotEmpty(ids)) {
			if (dao.countByUserId(Arrays.asList(ids)) > 0) {
				throw new DeleteException("info.management");
			}
		}
	}

	public void preNodeDelete(Integer[] ids) {
		if (ArrayUtils.isNotEmpty(ids)) {
			if (dao.countByNodeId(Arrays.asList(ids)) > 0) {
				throw new DeleteException("info.management");
			}
		}
	}

	public void preOrgDelete(Integer[] ids) {
		if (ArrayUtils.isNotEmpty(ids)) {
			if (dao.countByOrgId(Arrays.asList(ids)) > 0) {
				throw new DeleteException("info.management");
			}
		}
	}

	public void preSiteDelete(Integer[] ids) {
		if (ArrayUtils.isNotEmpty(ids)) {
			if (dao.countBySiteId(Arrays.asList(ids)) > 0) {
				throw new DeleteException("info.management");
			}
		}
	}

	private void firePostSave(Info[] bean) {
		if (!CollectionUtils.isEmpty(listeners)) {
			for (InfoListener listener : listeners) {
				listener.postInfoSave(bean);
			}
		}
	}

	private void firePostUpdate(Info[] bean) {
		if (!CollectionUtils.isEmpty(listeners)) {
			for (InfoListener listener : listeners) {
				listener.postInfoUpdate(bean);
			}
		}
	}

	private void firePreDelete(Integer[] ids) {
		if (!CollectionUtils.isEmpty(deleteListeners)) {
			for (InfoDeleteListener listener : deleteListeners) {
				listener.preInfoDelete(ids);
			}
		}
	}

	private void firePostDelete(Info[] bean) {
		if (!CollectionUtils.isEmpty(listeners)) {
			for (InfoListener listener : listeners) {
				listener.postInfoDelete(bean);
			}
		}
	}

	private List<InfoListener> listeners;
	private List<InfoDeleteListener> deleteListeners;

	@Autowired(required = false)
	public void setListeners(List<InfoListener> listeners) {
		this.listeners = listeners;
	}

	@Autowired(required = false)
	public void setDeleteListeners(List<InfoDeleteListener> deleteListeners) {
		this.deleteListeners = deleteListeners;
	}

	private WorkflowService workflowService;
	private InfoAttributeService infoAttrService;
	private InfoTagService infoTagService;
	private InfoSpecialService infoSpecialService;
	private InfoNodeService infoNodeService;
	private InfoDetailService infoDetailService;
	private InfoBufferService infoBufferService;
	private NodeService nodeService;
	private UserService userService;
	private SiteService siteService;

	@Autowired
	public void setWorkflowService(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}

	@Autowired
	public void setInfoAttrService(InfoAttributeService infoAttrService) {
		this.infoAttrService = infoAttrService;
	}

	@Autowired
	public void setInfoTagService(InfoTagService infoTagService) {
		this.infoTagService = infoTagService;
	}

	@Autowired
	public void setInfoSpecialService(InfoSpecialService infoSpecialService) {
		this.infoSpecialService = infoSpecialService;
	}

	@Autowired
	public void setInfoNodeService(InfoNodeService infoNodeService) {
		this.infoNodeService = infoNodeService;
	}

	@Autowired
	public void setInfoDetailService(InfoDetailService infoDetailService) {
		this.infoDetailService = infoDetailService;
	}

	@Autowired
	public void setInfoBufferService(InfoBufferService infoBufferService) {
		this.infoBufferService = infoBufferService;
	}

	@Autowired
	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@Autowired
	public void setSiteService(SiteService siteService) {
		this.siteService = siteService;
	}

	private InfoDao dao;

	@Autowired
	public void setDao(InfoDao dao) {
		this.dao = dao;
	}
}
