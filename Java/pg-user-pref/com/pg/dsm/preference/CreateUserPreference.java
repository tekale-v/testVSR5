package com.pg.dsm.preference;

import java.util.Map;

import javax.json.JsonArray;

import com.pg.dsm.preference.enumeration.PreferenceConstants;
import com.pg.dsm.preference.util.CommonPickListUtil;
import com.pg.dsm.preference.util.ExplorationUtil;
import com.pg.dsm.preference.util.PackagingUtil;
import com.pg.dsm.preference.util.ProductUtil;
import com.pg.dsm.preference.util.RawMaterialUtil;
import com.pg.dsm.preference.util.TechSpecUtil;
//Added by DSM for 22x CW-06 for Requirement #50186,50187 - START
import com.pg.dsm.preference.util.SEPUtil;
import com.pg.dsm.preference.util.MEPUtil;
//Added by DSM for 22x CW-06 for Requirement #50186,50187 - END

import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class CreateUserPreference {
    public enum PackagingPickLists {
        PART_TYPE {
            transient PackagingUtil util = new PackagingUtil();

            @Override
            public Map<String, StringList> asRangeMap(Context context) throws MatrixException {
                return util.getPartTypesRange();
            }

            @Override
            public JsonArray asJsonArray(Context context) throws MatrixException {
                return util.getPartTypesJsonArray();
            }

            @Override
            public String asJsonString(Context context) throws MatrixException {
                return util.getPartTypesJsonString();
            }

            @Override
            public String getKey() {
                return PreferenceConstants.Preferences.PACKAGING_PART_TYPE.get();
            }
        },
        PACKAGING_MATERIAL_TYPE {
            transient PackagingUtil util = new PackagingUtil();

            @Override
            public Map<String, StringList> asRangeMap(Context context) throws MatrixException {
                return null;
            }

            /**
             * @format (json : choice : name, displayChoice : name)
             * @param context
             * @return
             * @throws MatrixException
             */
            @Override
            public JsonArray asJsonArray(Context context) throws MatrixException {
                return util.getMaterialTypeJsonArray(context);
            }

            @Override
            public String asJsonString(Context context) throws MatrixException {
                return util.getMaterialTypeJsonString(context);
            }

            @Override
            public String getKey() {
                return PreferenceConstants.Preferences.PACKAGING_MATERIAL_TYPE.get();
            }
        },
        PACKAGING_COMPONENT_TYPE {
            transient PackagingUtil util = new PackagingUtil();

            @Override
            public Map<String, StringList> asRangeMap(Context context) throws MatrixException {
                return util.getComponentTypeRange(context);
            }

            /**
             * @format (json : choice : name, displayChoice : name)
             * @param context
             * @return
             * @throws MatrixException
             */
            @Override
            public JsonArray asJsonArray(Context context) throws MatrixException {
                return util.getComponentTypeJsonArray(context);
            }

            @Override
            public String asJsonString(Context context) throws MatrixException {
                return util.getComponentTypeJsonString(context);
            }

            @Override
            public String getKey() {
                return PreferenceConstants.Preferences.PACKAGING_COMPONENT_TYPE.get();
            }
        },
        BASE_UNIT_OF_MEASURE {
            transient PackagingUtil util = new PackagingUtil();

            @Override
            public Map<String, StringList> asRangeMap(Context context) throws MatrixException {
                return util.getBaseUnitOfMeasureRange(context);
            }

            /**
             * @format (json : choice : name, displayChoice : name)
             * @param context
             * @return
             * @throws MatrixException
             */
            @Override
            public JsonArray asJsonArray(Context context) throws MatrixException {
                return util.getBaseUnitOfMeasureJsonArray(context);
            }

            @Override
            public String asJsonString(Context context) throws MatrixException {
                return util.getBaseUnitOfMeasureJsonString(context);
            }

            @Override
            public String getKey() {
                return PreferenceConstants.Preferences.PACKAGING_BASE_UOM.get();
            }
        },
        REPORTED_FUNCTION {
            transient PackagingUtil util = new PackagingUtil();

            @Override
            public Map<String, StringList> asRangeMap(Context context) throws MatrixException {
                return util.getReportedFunctionRange(context);
            }

            /**
             * @format (json : choice : name, displayChoice : name)
             * @param context
             * @return
             * @throws MatrixException
             */
            @Override
            public JsonArray asJsonArray(Context context) throws MatrixException {
                return util.getReportedFunctionJsonArray(context);
            }

            @Override
            public String asJsonString(Context context) throws MatrixException {
                return util.getReportedFunctionJsonString(context);
            }

            @Override
            public String getKey() {
                return PreferenceConstants.Preferences.PACKAGING_BASE_UOM.get();
            }
        };

        public abstract Map<String, StringList> asRangeMap(Context context) throws MatrixException;

        public abstract JsonArray asJsonArray(Context context) throws MatrixException;

        public abstract String asJsonString(Context context) throws MatrixException;

        public abstract String getKey();
    }

    public enum CommonPickLists {
        PART_CATEGORY {
            transient CommonPickListUtil util = new CommonPickListUtil();

            @Override
            public Map<String, StringList> getAsRange(Context context) throws MatrixException {
                return util.getPartCategoryRange();
            }

            @Override
            public JsonArray asJsonArray(Context context) throws MatrixException {
                return util.getPartCategoryJsonArray();
            }

            @Override
            public String asJsonString(Context context) throws MatrixException {
                return util.getPartCategoryJsonString();
            }

            @Override
            public String getKey() {
                return PreferenceConstants.Preferences.COMMON_CLASS.get();
            }
        },
        STRUCTURE_RELEASE_CRITERIA {
            transient CommonPickListUtil util = new CommonPickListUtil();

            @Override
            public Map<String, StringList> getAsRange(Context context) throws MatrixException {
                return util.getReleaseCriteriaRange(context);
            }

            /**
             * @format (json : choice : Yes, displayChoice : Yes)
             * @param context
             * @return
             * @throws MatrixException
             */
            @Override
            public JsonArray asJsonArray(Context context) throws MatrixException {
                return util.getReleaseCriteriaJsonArray(context);
            }

            @Override
            public String asJsonString(Context context) throws MatrixException {
                return util.getReleaseCriteriaJsonString(context);
            }

            @Override
            public String getKey() {
                return PreferenceConstants.Preferences.COMMON_RELEASE_CRITERIA.get();
            }
        },
        CLASS {
            transient CommonPickListUtil util = new CommonPickListUtil();

            @Override
            public Map<String, StringList> getAsRange(Context context) throws MatrixException {
                return util.getClassesRange(context);
            }

            @Override
            public JsonArray asJsonArray(Context context) throws MatrixException {
                return util.getClassesJsonArray(context);
            }

            @Override
            public String asJsonString(Context context) throws MatrixException {
                return util.getClassesJsonString(context);
            }

            @Override
            public String getKey() {
                return PreferenceConstants.Preferences.COMMON_CLASS.get();
            }
        },
        SEGMENT {
            transient CommonPickListUtil util = new CommonPickListUtil();

            @Override
            public Map<String, StringList> getAsRange(Context context) throws MatrixException {
                return util.getSegmentRange(context);
            }

            @Override
            public JsonArray asJsonArray(Context context) throws MatrixException {
                return util.getSegmentJsonArray(context);
            }

            @Override
            public String asJsonString(Context context) throws MatrixException {
                return util.getSegmentJsonString(context);
            }

            @Override
            public String getKey() {
                return PreferenceConstants.Preferences.COMMON_SEGMENT.get();
            }
        },
        BUSINESS_AREA {
            transient CommonPickListUtil util = new CommonPickListUtil();

            @Override
            public Map<String, StringList> getAsRange(Context context) throws MatrixException {
                return util.getBusinessAreaRange(context);
            }

            @Override
            public JsonArray asJsonArray(Context context) throws MatrixException {
                return util.getBusinessAreaJsonArray(context);
            }

            @Override
            public String asJsonString(Context context) throws MatrixException {
                return util.getBusinessAreaJsonString(context);
            }

            @Override
            public String getKey() {
                return PreferenceConstants.Preferences.COMMON_BUSINESS_AREA.get();
            }
        };

        public abstract Map<String, StringList> getAsRange(Context context) throws MatrixException;

        public abstract JsonArray asJsonArray(Context context) throws MatrixException;

        public abstract String asJsonString(Context context) throws MatrixException;

        public abstract String getKey();
    }

    public enum ProductPickLists {
        PART_TYPE {
            transient ProductUtil util = new ProductUtil();


            @Override
            public Map<String, StringList> asRangeMap(Context context) throws MatrixException {
                return util.getPartTypesRange();
            }

            @Override
            public JsonArray asJsonArray(Context context) throws MatrixException {
                return util.getPartTypeJsonArray();
            }

            @Override
            public String asJsonString(Context context) throws MatrixException {
                return util.getPartTypeJsonString();
            }


            @Override
            public String getKey() {
                return PreferenceConstants.Preferences.PRODUCT_PART_TYPE.get();
            }
        },
        REPORTED_FUNCTION {
            transient ProductUtil util = new ProductUtil();

            @Override
            public Map<String, StringList> asRangeMap(Context context) throws MatrixException {
                return util.getReportedFunctionRange(context);
            }

            @Override
            public JsonArray asJsonArray(Context context) throws MatrixException {
                return util.getReportedFunctionArray(context); // (confirmed: products including FOP type has same Reported Function)
            }

            @Override
            public String asJsonString(Context context) throws MatrixException {
                return util.getReportedFunctionString(context);
            }

            @Override
            public String getKey() {
                return PreferenceConstants.Preferences.PRODUCT_REPORTED_FUNCTION.get();
            }
        },
        PRODUCT_COMPLIANCE_REQUIRED {
            transient ProductUtil util = new ProductUtil();

            @Override
            public Map<String, StringList> asRangeMap(Context context) throws MatrixException {
                return util.getProductComplianceRange(context);
            }

            @Override
            public JsonArray asJsonArray(Context context) throws MatrixException {
                return util.getProductComplianceJsonArray(context);
            }

            @Override
            public String asJsonString(Context context) throws MatrixException {
                return util.getProductComplianceJsonString(context);
            }

            @Override
            public String getKey() {
                return PreferenceConstants.Preferences.PRODUCT_COMPLIANCE_REQUIRED.get();
            }
        };

        public abstract Map<String, StringList> asRangeMap(Context context) throws MatrixException;

        public abstract JsonArray asJsonArray(Context context) throws MatrixException;

        public abstract String asJsonString(Context context) throws MatrixException;

        public abstract String getKey();
    }

    public enum RawMaterialPickLists {
        PART_TYPE {
            transient RawMaterialUtil util = new RawMaterialUtil();

            @Override
            public Map<String, StringList> asRangeMap(Context context) throws MatrixException {
                return util.getPartTypesRange();
            }

            @Override
            public JsonArray asJsonArray(Context context) throws MatrixException {
                return util.getPartTypeJsonArray();
            }

            @Override
            public String asJsonString(Context context) throws MatrixException {
                return util.getPartTypeJsonString();
            }

            @Override
            public String getKey() {
                return PreferenceConstants.Preferences.RAW_MATERIAL_PART_TYPE.get();
            }
        },
        REPORTED_FUNCTION {
            transient RawMaterialUtil util = new RawMaterialUtil();

            @Override
            public Map<String, StringList> asRangeMap(Context context) throws MatrixException {
                return util.getReportedFunctionRange(context);
            }

            @Override
            public JsonArray asJsonArray(Context context) throws MatrixException {
                return util.getReportedFunctionJsonArray(context); // (confirmed: all raw material types has same Reported Function)
            }

            @Override
            public String asJsonString(Context context) throws MatrixException {
                return util.getReportedFunctionJsonString(context);
            }

            @Override
            public String getKey() {
                return PreferenceConstants.Preferences.RAW_MATERIAL_REPORTED_FUNCTION.get();
            }
        },
        MATERIAL_FUNCTION {
            transient RawMaterialUtil util = new RawMaterialUtil();

            @Override
            public Map<String, StringList> asRangeMap(Context context) throws MatrixException {
                return util.getMaterialFunctionRange(context);
            }

            @Override
            public JsonArray asJsonArray(Context context) throws MatrixException {
                return util.getMaterialFunctionJsonArray(context);
            }

            @Override
            public String asJsonString(Context context) throws MatrixException {
                return util.getMaterialFunctionJsonString(context);
            }

            @Override
            public String getKey() {
                return PreferenceConstants.Preferences.RAW_MATERIAL_FUNCTION.get();
            }
        };

        public abstract Map<String, StringList> asRangeMap(Context context) throws MatrixException;

        public abstract JsonArray asJsonArray(Context context) throws MatrixException;

        public abstract String asJsonString(Context context) throws MatrixException;

        public abstract String getKey();
    }

    public enum TechSpecPickLists {
        PART_TYPE {
            transient TechSpecUtil util = new TechSpecUtil();


            @Override
            public Map<String, StringList> asRangeMap(Context context) throws MatrixException {
                return util.getPartTypesRange();
            }

            @Override
            public JsonArray asJsonArray(Context context) throws MatrixException {
                return util.getPartTypeJsonArray();
            }

            @Override
            public String asJsonString(Context context) throws MatrixException {
                return util.getPartTypeJsonString();
            }


            @Override
            public String getKey() {
                return PreferenceConstants.Preferences.TECHNICAL_SPEC_PART_TYPE.get();
            }
        };

        public abstract Map<String, StringList> asRangeMap(Context context) throws MatrixException;

        public abstract JsonArray asJsonArray(Context context) throws MatrixException;

        public abstract String asJsonString(Context context) throws MatrixException;

        public abstract String getKey();
    }
	//Added by DSM for 22x CW-06 for Requirement #50186,50187 - START
    public enum MEPPickLists {
        PART_TYPE {
            transient MEPUtil util = new MEPUtil();

            @Override
            public Map<String, StringList> asRangeMap(Context context) throws MatrixException {
                return util.getPartTypesRange();
            }

            @Override
            public JsonArray asJsonArray(Context context) throws MatrixException {
                return util.getPartTypeJsonArray();
            }

            @Override
            public String asJsonString(Context context) throws MatrixException {
                return util.getPartTypeJsonString();
            }


            @Override
            public String getKey() {
                return PreferenceConstants.Preferences.MEP_PART_TYPE.get();
            }
        };

        public abstract Map<String, StringList> asRangeMap(Context context) throws MatrixException;

        public abstract JsonArray asJsonArray(Context context) throws MatrixException;

        public abstract String asJsonString(Context context) throws MatrixException;

        public abstract String getKey();
    }

    public enum SEPPickLists {
        PART_TYPE {
            transient SEPUtil util = new SEPUtil();


            @Override
            public Map<String, StringList> asRangeMap(Context context) throws MatrixException {
                return util.getPartTypesRange();
            }

            @Override
            public JsonArray asJsonArray(Context context) throws MatrixException {
                return util.getPartTypeJsonArray();
            }

            @Override
            public String asJsonString(Context context) throws MatrixException {
                return util.getPartTypeJsonString();
            }


            @Override
            public String getKey() {
                return PreferenceConstants.Preferences.SEP_PART_TYPE.get();
            }
        };

        public abstract Map<String, StringList> asRangeMap(Context context) throws MatrixException;

        public abstract JsonArray asJsonArray(Context context) throws MatrixException;

        public abstract String asJsonString(Context context) throws MatrixException;

        public abstract String getKey();
    }
//Added by DSM for 22x CW-06 for Requirement #50186,50187 - END

    public enum ExplorationPickLists {
        PART_TYPE {
            transient ExplorationUtil util = new ExplorationUtil();


            @Override
            public Map<String, StringList> asRangeMap(Context context) throws MatrixException {
                return util.getPartTypesRange();
            }

            @Override
            public JsonArray asJsonArray(Context context) throws MatrixException {
                return util.getPartTypeJsonArray();
            }

            @Override
            public String asJsonString(Context context) throws MatrixException {
                return util.getPartTypeJsonString();
            }


            @Override
            public String getKey() {
                return PreferenceConstants.Preferences.TECHNICAL_SPEC_PART_TYPE.get();
            }
        };

        public abstract Map<String, StringList> asRangeMap(Context context) throws MatrixException;

        public abstract JsonArray asJsonArray(Context context) throws MatrixException;

        public abstract String asJsonString(Context context) throws MatrixException;

        public abstract String getKey();
    }
}
