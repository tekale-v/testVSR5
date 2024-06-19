package com.pg.cache;
/**
 * @author DS Platform Team
 * 2018x.6
 * enum defined to represent custom picklist fields with a constant. Each constant is mapped to following required values :-<br>
 * 1. Enovia Type name<br>
 * 2. Where Clause<br>
 * 3. Bus select for hidden value<br>
 * 4. Bus select for actual value<br> 
 * 5. Expiration Duration In Seconds<br>
 * 6. N.A.<br>
 */
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.framework.ui.UIUtil;

import matrix.db.Context;
import matrix.util.StringList;

public enum PGCachedTypes {
	pgPLICharacteristic(
			"type_pgPLICharacteristic",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			new String[] {"attribute[pgShortCode]"},
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME, "short_code"},
			null,
			"24"),
	pgPLICharacteristicSpecifics(
			"type_pgPLICharacteristicSpecifics",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			new String[] {"to[pgCharateristicToCharateristicSpecifics].from.name"},
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME, "charactristic_name"},
			null,
			"24"),
	pgPLICenterline(
			"type_pgPLICenterline",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME, "attribute[pgPLRelatedValue1]", "attribute[pgPLRelatedValue2]"},
			new String[] {},
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME, "unit_of_measure", "operational_line_name"},
			new String[] {"", "", "|", "|"},
			"24"),
	pgPLIOperationalLine(
			"type_pgPLIOperationalLine",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	pgPLIHazardClass(
			"type_pgPLIHazardClass",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	pgPLIPackingGroup(
			"type_pgPLIPackingGroup",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	pgPLIBusinessArea(
			"type_pgPLIBusinessArea",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	pgPLIProductCategoryPlatform(
			"type_pgPLIProductCategoryPlatform",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			new String[] {"from[pgPlatformToBusinessArea].to[pgPLIBusinessArea].name"},			
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME,"pgPLIBusinessArea_name"},
			null,
			"24"),
	pgPLIProductTechnologyPlatform(
			"type_pgPLIProductTechnologyPlatform",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			new String[] {"from[pgPlatformToPlatform].to[pgPLIProductCategoryPlatform].name"},			
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME,"pgPLIProductCategoryPlatform_name"},
			null,
			"24"),
	pgPLIProductTechnologyChassis(
			"type_pgPLIProductTechnologyChassis",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			new String[] {"to[pgPlatformToChassis].from[pgPLIProductTechnologyPlatform].name", "to[pgPLProductCategoryToChassis].fromrel.to"},			
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME,"pgPLIProductTechnologyPlatform_name", "pgPLIProductCategoryPlatform_name"},
			null,
			"24"),
	pgPLIChassisProductSize(
			"type_pgPLIChassisProductSize",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			new String[] {"from[pgPlatformToChassis].to[pgPLIProductTechnologyChassis].name"},
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME,"pgPLIProductTechnologyChassis_name"},
			null,
			"24"),
	pgPLILPDRegion(
			"type_pgPLILPDRegion",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			new String[] {"to[pgPLBusinessAreaToLPDRegion].from[pgPLIBusinessArea].name"},			
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME,"pgPLIBusinessArea_name"},
			null,
			"24"),
	pgPLILPDSubRegion(
			"type_pgPLILPDSubRegion",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME, "attribute["+DomainConstants.ATTRIBUTE_TITLE+"]"},
			new String[] {},			
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME, DomainConstants.ATTRIBUTE_TITLE},
			null,
			"24"),
	pgPLIFranchisePlatform(
			"type_pgPLIFranchisePlatform",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			new String[] {"from[pgPlatformToBusinessArea].to[pgPLIBusinessArea].name"},
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME,"pgPLIBusinessArea_name"},
			null,
			"24"),
	pgPLIPackageProcessPlatform(
			"type_pgPLIPackageProcessPlatform",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			new String[] {"from[pgPlatformToBusinessArea].to[pgPLIBusinessArea].name"},
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME,"pgPLIBusinessArea_name"},
			null,
			"24"),
	pgPLIMaterialPlatform(
			"type_pgPLIMaterialPlatform",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			new String[] {"from[pgPlatformToBusinessArea].to[pgPLIBusinessArea].name"},
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME,"pgPLIBusinessArea_name"},
			null,
			"24"),
	pgPLIPackagePlatform(
			"type_pgPLIPackagePlatform",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			new String[] {"from[pgPlatformToPlatform].to[pgPLIProductCategoryPlatform].name"},
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME,"pgPLIProductCategoryPlatform_name"},
			null,
			"24"),
	pgPLIProductEquipmentPlatform(
			"type_pgPLIProductEquipmentPlatform",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			new String[] {"from[pgPlatformToBusinessArea].to[pgPLIBusinessArea].name"},
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME,"pgPLIBusinessArea_name"},
			null,
			"24"),
	pgPLIPackageChassis(
			"type_pgPLIPackageChassis",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			new String[] {},//need to change
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME,"pgPLIPackageChassis_name"},
			null,
			"24"),
	pgPLIPackageEquipmentPlatform(
			"type_pgPLIPackageEquipmentPlatform",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			new String[] {"from[pgPlatformToChassis].to[pgPLIPackageEquipmentPlatform].name"},
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME,"pgPLIBusinessArea_name"},
			null,
			"24"),
	pgPLIProductProcessPlatform(
			"type_pgPLIProductProcessPlatform",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			new String[] {"from[pgPlatformToBusinessArea].to[pgPLIBusinessArea].name"},
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME,"pgPLIBusinessArea_name"},
			null,
			"24"),
	pgPLIStudyType(
			"type_pgPLIStudyType",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	Class(
			"type_Class",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	pgPLIAdditionalServices(
			"type_pgPLIAdditionalServices",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	pgPLIConsumerTestTechnique(
			"type_pgPLIConsumerTestTechnique",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	pgPLIConsumerTestFeedbackType(
			"type_pgPLIConsumerTestFeedbackType",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	pgPLIConsumerTestObjectiveType(
			"type_pgPLIConsumerTestObjectiveType",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	pgPLIStudyClass(
			"type_pgPLIStudyClass",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	pgPLIDrugClass(
			"type_pgPLIDrugClass",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	Plant(
			"type_Plant",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	pgPLIPanelistType(
			"type_pgPLIPanelistType",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	pgPLIEthnicityRace(
			"type_pgPLIEthnicityRace",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	pgPLIGender(
			"type_pgPLIGender",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	pgPLIPanelistSupervision(
			"type_pgPLIPanelistSupervision",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	pgPLIPanelistTask(
			"type_pgPLIPanelistTask",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	pgPLIDataCollectionMethods(
			"type_pgPLIDataCollectionMethods",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	pgPLIDataMerge(
			"type_pgPLIDataMerge",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	pgPLIAttachmentFormat(
			"type_pgPLIAttachmentFormat",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	pgPLIStudyLocations(
			"type_pgPLIStudyLocations",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	pgPLIStudySites(
			"type_pgPLIStudySites",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	pgPLIAssemblyState(
			"type_pgPLIAssemblyState",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	pgPLITechnicalBuildingBlocks(
			"type_pgPLITechnicalBuildingBlocks",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			new String[] {"from[pgPlatformToBusinessArea].to[pgPLIBusinessArea].name"},
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME,"pgPLIBusinessArea_name"},
			null,
			"24"),
	pgPLIDevelopmentArea(
			"type_pgPLIDevelopmentArea",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			new String[] {},
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	pgPLIDocumentTypePLM(
			"type_pgPLIDocumentTypePLM",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			new String[] {"to[pgPLSubsetItem].from[pgPLSubsetList].name"},
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME,"picklist_parent_name"},
			null,
			"24"),
	pgPLIDefectType(
			"type_pgPLIDefectType",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			new String[] {"attribute["+PropertyUtil.getSchemaProperty(null, "attribute_pgCEUpversionPickListCode")+"]"},
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME, "short_code"},
			null,
			"24"),
	pgPLIResponsibleFunction(
			"type_pgPLIResponsibleFunction",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			new String[] {"attribute["+PropertyUtil.getSchemaProperty(null, "attribute_pgCEUpversionPickListCode")+"]"},
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME,"short_code"},
			null,
			"24"),
	pgPLIReason(
			"type_pgPLIReason",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			new String[] {"attribute["+PropertyUtil.getSchemaProperty(null, "attribute_pgCEUpversionPickListCode")+"]"},
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME,"short_code"},
			null,
			"24"),
	pgBrand(
			"type_pgBrand",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	pgPLEGPDBCategory(
			"type_pgPLEGPDBCategory",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	pgPLIPlant(
			"type_pgPLIPlant",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	pgPLICountry(
			"type_pgPLICountry",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	pgPLIColor(
			"type_pgPLIColor",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	pgPLIColorIntensity(
			"type_pgPLIColorIntensity",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	pgPLISegment(
			"type_pgPLISegment",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	pgPLIMethodOrigin(
			"type_pgPLIMethodOrigin",
			"current != Inactive && current != New",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	pgPLIPlantTesting(
			"type_pgPLIPlantTesting",
			"current != Inactive && current != New",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	pgPLIRetestingUOM(
			"type_pgPLIRetestingUOM",
			"current != Inactive && current != New",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	pgPLIUnitofMeasureMasterList(
			"type_pgPLIUnitofMeasureMasterList",
			"current != Inactive && current != New",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	pgPLIReportType(
			"type_pgPLIReportType",
			"current != Inactive && current != New",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	pgPLIActionRequired(
			"type_pgPLIActionRequired",
			"current != Inactive && current != New",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	pgPLICriticalityFactor(
			"type_pgPLICriticalityFactor",
			"current != Inactive && current != New",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	pgPLITestGroup(
			"type_pgPLITestGroup",
			"current != Inactive && current != New",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	pgPLIApproverRole(
			"type_pgPLIApproverRole",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	pgPLILifeCycleStatus(
			"type_pgPLILifeCycleStatus",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	pgPLISamplingFrequency(
			"type_pgPLISamplingFrequency",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	pgPLISamplingTestingLocation(
			"type_pgPLISamplingTestingLocation",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	pgPLISamplingPurpose(
			"type_pgPLISamplingPurpose",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	pgPLIRegion(
			"type_pgPLIRegion",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	pgPLIOrganizationChangeManagement(
			"type_pgPLIOrganizationChangeManagement",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	pgPLIProductForm(
			"type_pgPLIProductForm",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	pgPLIGPSCopyElementCategory(
			"type_pgPLIGPSCopyElementCategory",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	pgPLIGPSCopyElementType(
			"type_pgPLIGPSCopyElementType",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	pgPLIPackComponentType(
			"type_pgPLIPackComponentType",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),	
	pgPLISamplingCapturePoint(
			"type_pgPLISamplingCapturePoint",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24"),
	pgPLIProductFormDetail(
			"type_pgPLIProductFormDetail",
			"current == 'Active'",
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			new String[] {DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME},
			null,
			"24");

	private final String strType;
	private final String strWhereClause;
	private final String[] strSingleSelects;
	private final String[] strMultiSelects;
	private final String[] strDisplaySelects;
	private final String[] strSelectValueSep;
	private final String strClientCacheExpirationDurationInHours;
	private static final String DEFAULT_CLIENT_CACHE_EXPIRATION_DURATION = "24";

	/**
	 * Constructor 1. Only Type - parameter is required. Where Clause, Expiration duration is set to their default values.<br>
	 * {@link #${CLASSNAME}(String, String, String}
	 * 
	 * @param strType
	 *            Symbolic name of Enovia Type to be mapped for each type-ahead field
	 */
	private PGCachedTypes(String strType) {
		this(strType, null, new String[] {DomainConstants.SELECT_ID}, null, new String[] {DomainConstants.SELECT_NAME}, null, DEFAULT_CLIENT_CACHE_EXPIRATION_DURATION);
	}

	/**
	 * Constructor 2. Expiration Duration is set to its default value.<br>
	 * {@link #${CLASSNAME}(String, String, String}
	 * 
	 * @param strType
	 *            Symbolic name of Enovia Type to be defined for each type-ahead field
	 * @param strWhereClause
	 *            Where clause defined for each type-ahead field
	 */
	private PGCachedTypes(String strType, String strWhereClause) {
		this(strType, strWhereClause, new String[] {DomainConstants.SELECT_ID}, null, new String[] {DomainConstants.SELECT_NAME}, null, DEFAULT_CLIENT_CACHE_EXPIRATION_DURATION);
	}
	
	/**
	 * Constructor 3. Expiration Duration is set to its default value.<br>
	 * {@link #${CLASSNAME}(String, String, String}
	 * 
	 * @param strType
	 *            Symbolic name of Enovia Type to be defined for each type-ahead field
	 * @param strWhereClause
	 *            Where clause defined for each type-ahead field
	 */
	private PGCachedTypes(String strType, String strWhereClause, String[] strActualName, String[] strDisplayName) {
		this(strType, strWhereClause, strActualName, null, strDisplayName, null, DEFAULT_CLIENT_CACHE_EXPIRATION_DURATION);
	}

	/**
	 * Constructor 3. Saves the following inputs
	 * 
	 * @param strType        Symbolic name of Enovia Type to be defined for each
	 *                       type-ahead field
	 * @param strWhereClause Where clause defined for each type-ahead field
	 * @param lExpDuration   String: Duration in seconds for which the browser cache
	 *                       will be retained.
	 */
	private PGCachedTypes(String strType, String strWhereClause, String[] strSingleSelects,
			String[] strMultiSelects, String[] strDisplayName, String[] strSelectSep, String strExpirationTime) {
		this.strType = strType;
		this.strWhereClause = strWhereClause;
		this.strSingleSelects = strSingleSelects;
		this.strMultiSelects = strMultiSelects;
		this.strDisplaySelects = strDisplayName;
		this.strSelectValueSep = strSelectSep;
		this.strClientCacheExpirationDurationInHours = strExpirationTime;
	}

	/**
	 * This method finds the Actual type name of the Symbolic name saved in {@link ${CLASSNAME}#_type}
	 * 
	 * @param paramContext
	 *            The enovia <code>Context</code> object
	 * @return Actual Type Name
	 */
	public String getType(Context paramContext) {
		return PropertyUtil.getSchemaProperty(paramContext, this.strType);
	}

	/**
	 * Getter for {@link ${CLASSNAME}#_where_clause}
	 * 
	 * @return Where Clause
	 */
	public String getWhereClause() {
		return this.strWhereClause;
	}
	
	/**
	 * Getter for all selectables
	 * 
	 * @return allSelects
	 */
	public StringList getActualSelectList() {
		StringList slSingleSelects = getSingleSelectList();
		StringList slMultiSelects = getMultiSelectList();

		if(slMultiSelects == null) {
			return slSingleSelects;
		} else if(slSingleSelects == null) {
			return slMultiSelects;
		} else {
			slSingleSelects.addAll(slMultiSelects);
		}

		return slSingleSelects;
	}
	
	/**
	 * Getter for {@link ${CLASSNAME}#strSingleSelects}
	 * 
	 * @return strSingleSelects
	 */
	public StringList getSingleSelectList() {
		StringList slSingleSelects = getStringListFromArray(this.strSingleSelects);
		return slSingleSelects;
	}
	
	/**
	 * Getter for {@link ${CLASSNAME}#strMultiSelects}
	 * 
	 * @return strMultiSelects
	 */
	public StringList getMultiSelectList() {
		StringList slMultiSelects = getStringListFromArray(this.strMultiSelects);
		return slMultiSelects;
	}
	
	/**
	 * Method to get StringList from String Array
	 * @param strStringArray : String[] to be converted
	 * @return : StringList
	 */
	private StringList getStringListFromArray(String[] strStringArray) {
		if(strStringArray == null) {
			return null;
		}
		StringList slReturnList = new StringList();
		for(int i=0; i<strStringArray.length; i++) {
			String strValue = strStringArray[i];
			if(UIUtil.isNullOrEmpty(strValue)) {
				strValue = "";
			}
			slReturnList.add(strValue);
		}
		return slReturnList;
	}

	/**
	 * Getter for {@link ${CLASSNAME}#strDisplaySelects}
	 * 
	 * @return strDisplaySelects
	 */
	public StringList getDisplaySelectList() {
		StringList slMultiSelects = getStringListFromArray(this.strDisplaySelects);
		return slMultiSelects;
	}
	
	/**
	 * Getter for {@link ${CLASSNAME}#strSelectValueSep}
	 * 
	 * @return strSelectValueSep
	 */
	public StringList getSelectableValueSeparatorList() {
		StringList slMultiSelects = getStringListFromArray(this.strSelectValueSep);
		return slMultiSelects;
	}
	
	/**
	 * Getter for {@link ${CLASSNAME}#_client_cache_expiration_duration_in_hours}
	 * 
	 * @return Expiration Duration In Seconds
	 */
	public String getClientCacheExpirationDurationInHours() {
		return this.strClientCacheExpirationDurationInHours;
	}
}
