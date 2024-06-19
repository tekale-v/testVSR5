package com.pg.dsm.preference.enumeration;

public class CopyDataTable {
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
                return "column[BusCategory]";
            }
        },
        TITLE {
            @Override
            public String getSelectColumn() {
                return "column[BusTitle]";
            }
        },
        USE_PREFERENCE {
            @Override
            public String getSelectColumn() {
                return "column[PreferenceKey]";
            }
        },
        PERSON_KEY {
            @Override
            public String getSelectColumn() {
                return "column[PersonKey]";
            }
        },
        ORIGINATED {
            @Override
            public String getSelectColumn() {
                return "column[originated]";
            }
        },
        UPTNAME {
            @Override
            public String getSelectColumn() {
                return "column[UPTName]";
            }
        },
        UPTPHYSICALID {
            @Override
            public String getSelectColumn() {
                return "column[UPTPhysicalID]";
            }
        };

        public abstract String getSelectColumn();
    }
}
