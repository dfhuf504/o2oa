package com.x.organization.assemble.express.jaxrs.group;

import java.util.ArrayList;
import java.util.List;

import com.x.organization.core.entity.Identity;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.express.Business;
import com.x.organization.core.entity.Person;

import net.sf.ehcache.Element;

class ActionListWithPerson extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			String cacheKey = ApplicationCache.concreteCacheKey(this.getClass(),
					StringUtils.join(wi.getPersonList(), ","),
					wi.getRecursiveGroupFlag(), wi.getReferenceFlag(), wi.getRecursiveOrgFlag());
			Element element = cache.get(cacheKey);
			if (null != element && (null != element.getObjectValue())) {
				result.setData((Wo) element.getObjectValue());
			} else {
				Wo wo = this.list(business, wi);
				cache.put(new Element(cacheKey, wo));
				result.setData(wo);
			}
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("个人")
		private List<String> personList = new ArrayList<>();

		@FieldDescribe("是否递归查询上级群组，默认true")
		private Boolean recursiveGroupFlag = true;

		@FieldDescribe("是否包含查找人员身份成员、人员归属组织成员的所属群组，默认false")
		private Boolean referenceFlag = false;

		@FieldDescribe("是否递归人员归属组织的上级组织所属群组，前提referenceFlag为true，默认false")
		private Boolean recursiveOrgFlag = false;

		public List<String> getPersonList() {
			return personList;
		}

		public void setPersonList(List<String> personList) {
			this.personList = personList;
		}

		public Boolean getReferenceFlag() {
			return referenceFlag;
		}

		public void setReferenceFlag(Boolean referenceFlag) {
			this.referenceFlag = referenceFlag;
		}

		public Boolean getRecursiveGroupFlag() {
			return recursiveGroupFlag;
		}

		public void setRecursiveGroupFlag(Boolean recursiveGroupFlag) {
			this.recursiveGroupFlag = recursiveGroupFlag;
		}

		public Boolean getRecursiveOrgFlag() {
			return recursiveOrgFlag;
		}

		public void setRecursiveOrgFlag(Boolean recursiveOrgFlag) {
			this.recursiveOrgFlag = recursiveOrgFlag;
		}
	}

	public static class Wo extends WoGroupAbstract {

	}

	private Wo list(Business business, Wi wi) throws Exception {
		List<Person> os = business.person().pick(wi.getPersonList());
		List<String> groupIds = new ArrayList<>();
		for (Person person : os) {
			groupIds.addAll(business.group().listSupDirectWithPerson(person.getId()));
			if(BooleanUtils.isTrue(wi.getReferenceFlag())){
				List<Identity> identityList = business.identity().listByPerson(person.getId());
				for(Identity identity : identityList){
					groupIds.addAll(business.group().listSupDirectWithIdentity(identity.getId()));
					groupIds.addAll(business.group().listSupDirectWithUnit(identity.getUnit()));
					if(BooleanUtils.isTrue(wi.getRecursiveOrgFlag())){
						List<String> orgIds = business.unit().listSupNested(identity.getUnit());
						for (String orgId : orgIds){
							groupIds.addAll(business.group().listSupDirectWithUnit(orgId));
						}
					}
				}
			}
		}
		groupIds = ListTools.trim(groupIds, true, true);
		List<String> groupIds2 = new ArrayList<>();
		groupIds2.addAll(groupIds);
		if(!BooleanUtils.isFalse(wi.getRecursiveGroupFlag())){
			for(String groupId : groupIds){
				groupIds2.addAll(business.group().listSupNested(groupId));
			}
			groupIds2 = ListTools.trim(groupIds2, true, true);
		}
		List<String> values = business.group().listGroupDistinguishedNameSorted(groupIds2);
		Wo wo = new Wo();
		wo.getGroupList().addAll(values);
		return wo;
	}

}