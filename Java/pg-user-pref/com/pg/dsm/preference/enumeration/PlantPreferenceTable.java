package com.pg.dsm.preference.enumeration;

import java.util.HashMap;
import java.util.Map;

import com.matrixone.apps.domain.util.MapList;
import com.pg.dsm.preference.util.PlantDataPreferenceUtil;

import matrix.db.Context;
import matrix.util.MatrixException;

public class PlantPreferenceTable {
    public enum Columns {
        PHYSICAL_ID {
            @Override
            public String getSelectColumn() {
                return "column[BusPhysicalId]";
            }
        },
        TYPE {
            @Override
            public String getSelectColumn() {
                return "column[BusType]";
            }
        },
        NAME {
            @Override
            public String getSelectColumn() {
                return "column[BusName]";
            }
        },
        REVISION {
            @Override
            public String getSelectColumn() {
                return "column[BusRevision]";
            }
        },
        CATEGORY {
            @Override
            public String getSelectColumn() {
                return "column[category]";
            }
        },
        CATEGORY_TYPE {
            @Override
            public String getSelectColumn() {
                return "column[categoryType]";
            }
        },
        PERSON_KEY {
            @Override
            public String getSelectColumn() {
                return "column[personKey]";
            }
        },
        AUTHORIZED {
            @Override
            public String getSelectColumn() {
                return "column[authorized]";
            }
        },
        ACTIVATED {
            @Override
            public String getSelectColumn() {
                return "column[activated]";
            }
        },
        AUTHORIZED_TO_USE {
            @Override
            public String getSelectColumn() {
                return "column[authorizedToUse]";
            }
        },
        AUTHORIZED_TO_PRODUCE {
            @Override
            public String getSelectColumn() {
                return "column[authorizedToProduce]";
            }
        },
        ORIGINATED {
            @Override
            public String getSelectColumn() {
                return "column[originated]";
            }
        };

        public abstract String getSelectColumn();
    }

    public enum WhereClause {
        CREATE {
            @Override
            public String getWhereClause(Context context, Map<String, String> pairs) {
                StringBuilder whereClauseBuilder = new StringBuilder();
                whereClauseBuilder.append(Columns.PERSON_KEY.getSelectColumn());
                whereClauseBuilder.append("==");
                whereClauseBuilder.append(context.getUser());
                whereClauseBuilder.append(" && ");
                whereClauseBuilder.append(Columns.CATEGORY.getSelectColumn());
                whereClauseBuilder.append("==");
                whereClauseBuilder.append(pairs.get(Columns.CATEGORY.getSelectColumn()));
                return whereClauseBuilder.toString();
            }
        },
        RETRIEVE_BY_CATEGORY {
            @Override
            public String getWhereClause(Context context, Map<String, String> pairs) {
                StringBuilder whereClauseBuilder = new StringBuilder();
                whereClauseBuilder.append(Columns.PERSON_KEY.getSelectColumn());
                whereClauseBuilder.append("==");
                whereClauseBuilder.append(context.getUser());
                whereClauseBuilder.append(" && ");
                whereClauseBuilder.append(Columns.CATEGORY.getSelectColumn());
                whereClauseBuilder.append("==");
                whereClauseBuilder.append(pairs.get(Columns.CATEGORY.getSelectColumn()));
                return whereClauseBuilder.toString();
            }
        },
        RETRIEVE_ALL {
            @Override
            public String getWhereClause(Context context, Map<String, String> pairs) {
                StringBuilder whereClauseBuilder = new StringBuilder();
                whereClauseBuilder.append(Columns.PERSON_KEY.getSelectColumn());
                whereClauseBuilder.append("==");
                whereClauseBuilder.append(context.getUser());
                return whereClauseBuilder.toString();
            }
        },
        UPDATE {
            @Override
            public String getWhereClause(Context context, Map<String, String> pairs) {
                StringBuilder whereClauseBuilder = new StringBuilder();
                whereClauseBuilder.append(Columns.PERSON_KEY.getSelectColumn());
                whereClauseBuilder.append("==");
                whereClauseBuilder.append(context.getUser());
                whereClauseBuilder.append(" && ");
                whereClauseBuilder.append(Columns.CATEGORY_TYPE.getSelectColumn());
                whereClauseBuilder.append("==");
                whereClauseBuilder.append(pairs.get(Columns.CATEGORY.getSelectColumn()));
                whereClauseBuilder.append(" && ");
                whereClauseBuilder.append(Columns.PHYSICAL_ID.getSelectColumn());
                whereClauseBuilder.append("==");
                whereClauseBuilder.append(pairs.get(Columns.PHYSICAL_ID.getSelectColumn()));
                whereClauseBuilder.append(" && ");
                whereClauseBuilder.append(Columns.CATEGORY.getSelectColumn());
                whereClauseBuilder.append("==");
                whereClauseBuilder.append(pairs.get(Columns.CATEGORY.getSelectColumn()));
                return whereClauseBuilder.toString();
            }
        },
        DELETE {
            @Override
            public String getWhereClause(Context context, Map<String, String> pairs) {
                StringBuilder whereClauseBuilder = new StringBuilder();
                whereClauseBuilder.append(Columns.PERSON_KEY.getSelectColumn());
                whereClauseBuilder.append("==");
                whereClauseBuilder.append(context.getUser());
                whereClauseBuilder.append(" && ");
                whereClauseBuilder.append(Columns.CATEGORY_TYPE.getSelectColumn());
                whereClauseBuilder.append("==");
                whereClauseBuilder.append(pairs.get(Columns.CATEGORY.getSelectColumn()));
                whereClauseBuilder.append(" && ");
                whereClauseBuilder.append(Columns.PHYSICAL_ID.getSelectColumn());
                whereClauseBuilder.append("==");
                whereClauseBuilder.append(pairs.get(Columns.PHYSICAL_ID.getSelectColumn()));
                return whereClauseBuilder.toString();
            }
        };

        public abstract String getWhereClause(Context context, Map<String, String> pairs);
    }

    public enum Retrieve {
        PRODUCT {
            @Override
            public MapList getPlantData(Context context) throws MatrixException {
                Map<String, String> pairs = new HashMap<>();
                pairs.put(Columns.CATEGORY.getSelectColumn(), PreferenceConstants.Basic.PRODUCT.get());
                PlantDataPreferenceUtil plantDataPreferenceUtil = new PlantDataPreferenceUtil();
                return plantDataPreferenceUtil.retrievePlantEntriesByCategory(context, WhereClause.RETRIEVE_BY_CATEGORY.getWhereClause(context, pairs));
            }
        },
        PACKAGING {
            @Override
            public MapList getPlantData(Context context) throws MatrixException {
                Map<String, String> pairs = new HashMap<>();
                pairs.put(Columns.CATEGORY.getSelectColumn(), PreferenceConstants.Basic.PACKAGING.get());
                PlantDataPreferenceUtil plantDataPreferenceUtil = new PlantDataPreferenceUtil();
                return plantDataPreferenceUtil.retrievePlantEntriesByCategory(context, WhereClause.RETRIEVE_BY_CATEGORY.getWhereClause(context, pairs));
            }
        },
        RAW_MATERIAL {
            @Override
            public MapList getPlantData(Context context) throws MatrixException {
                Map<String, String> pairs = new HashMap<>();
                pairs.put(Columns.CATEGORY.getSelectColumn(), PreferenceConstants.Basic.RAW_MATERIAL.get());
                PlantDataPreferenceUtil plantDataPreferenceUtil = new PlantDataPreferenceUtil();
                return plantDataPreferenceUtil.retrievePlantEntriesByCategory(context, WhereClause.RETRIEVE_BY_CATEGORY.getWhereClause(context, pairs));
            }
        },
        TECHNICAL_SPEC {
            @Override
            public MapList getPlantData(Context context) throws MatrixException {
                Map<String, String> pairs = new HashMap<>();
                pairs.put(Columns.CATEGORY.getSelectColumn(), PreferenceConstants.Basic.TECHNICAL_SPEC.get());
                PlantDataPreferenceUtil plantDataPreferenceUtil = new PlantDataPreferenceUtil();
                return plantDataPreferenceUtil.retrievePlantEntriesByCategory(context, WhereClause.RETRIEVE_BY_CATEGORY.getWhereClause(context, pairs));
            }
        },
        ALL {
            @Override
            public MapList getPlantData(Context context) throws MatrixException {
                PlantDataPreferenceUtil plantDataPreferenceUtil = new PlantDataPreferenceUtil();
                return plantDataPreferenceUtil.retrievePlantEntriesByCategory(context, WhereClause.RETRIEVE_ALL.getWhereClause(context, new HashMap<>()));
            }
        };

        public abstract MapList getPlantData(Context context) throws MatrixException;
    }
}
