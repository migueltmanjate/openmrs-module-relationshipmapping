/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.relationshipmapping.web.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.relationshipmapping.api.service.MasterCardRecordService;
import org.openmrs.module.relationshipmapping.model.MasterCardRecordMappingExecutionCycle;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * This class configured as controller using annotation and mapped with the URL of
 * 'module/relationshipmapping/relationshipmappingLink.form'.
 */
@Controller
public class MainController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	private Collection<MasterCardRecordMappingExecutionCycle> executionCycles;

	private int numOfPages = 0;
	private int currentPageNo = 1;


	@RequestMapping(value = "/module/relationshipmapping/mappinghistory.list", method = RequestMethod.GET)
	public void view() {
		// do nothing here, the rest will be handled by angular
	}

	@RequestMapping(method = RequestMethod.GET,value = "/module/relationshipmapping/executioncycles.json")
	public Map<String, Object> getMappingHistory(final @RequestParam(value = "search") String search,
									final @RequestParam(value = "pageNumber") Integer pageNumber,
									final @RequestParam(value = "pageSize") Integer pageSize) {

		Map<String, Object> response = new HashMap<String, Object>();
		if (Context.isAuthenticated()) {
			this.currentPageNo = pageNumber;

			MasterCardRecordService masterCardRecordService = Context.getService(MasterCardRecordService.class);
			List<MasterCardRecordMappingExecutionCycle> allExecutionCycles = masterCardRecordService.getAllExecutionCycles();

			if (executionCycles == null) {
				executionCycles = new ArrayList<MasterCardRecordMappingExecutionCycle>();
			} else {
				executionCycles.clear();
			}

			int startIndex = (currentPageNo - 1) * pageSize;
			int endIndex = allExecutionCycles.size() > (currentPageNo) * pageSize ? (currentPageNo) * pageSize : allExecutionCycles.size();
			for (int i = startIndex; i < endIndex; i++) {
				executionCycles.add(allExecutionCycles.get(i));
			}
			numOfPages = (int) Math.ceil(((double) allExecutionCycles.size()) / pageSize);

			List<Object> objects = new ArrayList<Object>();
			for(MasterCardRecordMappingExecutionCycle cycle:executionCycles){
				objects.add(convertToJsonMap(cycle));
			}

			response.put("pages", numOfPages);
			response.put("totalItems", allExecutionCycles.size());
			response.put("objects", objects);
		}
		return response;
	}

	@RequestMapping(method = RequestMethod.GET, value="/module/relationshipmapping/runtasknow.json")
	@ResponseBody
	public Map<String, Object> processUnmappedMastercardRecords(){
		Map<String, Object> response = new HashMap<String, Object>();
		if(Context.isAuthenticated()) {
			MasterCardRecordService service = Context.getService(MasterCardRecordService.class);
			if(!service.isTaskRunning()) {
				response.put("wasServiceRunningAnotherTask", false);

				service.processUnmappedMastercardRecords();
			} else {
				response.put("wasServiceRunningAnotherTask", true);
			}
		}
		return response;
	}



	Map<String, Object> convertToJsonMap (final MasterCardRecordMappingExecutionCycle cycle){
		Map<String, Object> map = new HashMap<String, Object>();
		if(cycle != null){
			map.put("indexCasesInvolved",cycle.getIndexCasesInvolved());
			map.put("queryEndTime",Context.getDateTimeFormat().format(cycle.getQueryEndTime()));
			map.put("personsCreated",cycle.getPersonsCreated());
			map.put("personsForObsCreated",cycle.getPersonsForObsCreated());
			map.put("relationshipsCreated",cycle.getRelationshipsCreated());
			map.put("obsCreated",cycle.getObsCreated());
		}
		return map;
	}
	@RequestMapping(value = "/module/relationshipmapping/getUserLocale.json", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getUserLocale(){
		return convertLocale(Context.getUserContext().getLocale().getLanguage().toString());
	}

	public static Map<String, Object>  convertLocale(String locale) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (locale != null) {
			map.put("locale", locale);
		}
		return map;
	}
}
