package com.pg.dsm.upload.battery.util;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIUtil;
import matrix.db.Context;
import matrix.util.StringList;

public class FindObject {
    MapList resultList;
    private FindObject(Builder builder) {
        this.resultList = builder.resultList;
    }

    public MapList getResultList() {
        return resultList;
    }

    public static class Builder {

        String typePattern;
        String namePattern;
        String revisionPattern;
        String ownerPattern;
        String vaultPattern;
        String whereClause;
        boolean expandType;
        StringList objectSelectList;

        MapList resultList;


        Context context;
        public Builder(Context context) {
            this.context = context;
        }

        public String getTypePattern() {
            return typePattern;
        }

        public Builder setTypePattern(String typePattern) {
            this.typePattern = typePattern;
            return this;
        }

        public String getNamePattern() {
            return namePattern;
        }

        public Builder setNamePattern(String namePattern) {
            this.namePattern = namePattern;
            return this;
        }

        public String getRevisionPattern() {
            return revisionPattern;
        }

        public Builder setRevisionPattern(String revisionPattern) {
            this.revisionPattern = revisionPattern;
            return this;
        }

        public String getOwnerPattern() {
            return ownerPattern;
        }

        public Builder setOwnerPattern(String ownerPattern) {
            this.ownerPattern = ownerPattern;
            return this;
        }

        public String getVaultPattern() {
            return vaultPattern;
        }

        public Builder setVaultPattern(String vaultPattern) {
            this.vaultPattern = vaultPattern;
            return this;
        }

        public String getWhereClause() {
            return whereClause;
        }

        public Builder setWhereClause(String whereClause) {
            this.whereClause = whereClause;
            return this;
        }

        public boolean isExpandType() {
            return expandType;
        }

        public Builder setExpandType(boolean expandType) {
            this.expandType = expandType;
            return this;
        }

        public StringList getObjectSelectList() {
            return objectSelectList;
        }

        public Builder setObjectSelectList(StringList objectSelectList) {
            this.objectSelectList = objectSelectList;
            return this;
        }

        public FindObject build() throws FrameworkException {
            find();
            return new FindObject(this);
        }

        private void find() throws FrameworkException {
            this.resultList = DomainObject.findObjects(
                    context,
                    (UIUtil.isNotNullAndNotEmpty(typePattern)) ? typePattern: DomainConstants.QUERY_WILDCARD, // typePattern
                    (UIUtil.isNotNullAndNotEmpty(namePattern)) ? namePattern: DomainConstants.QUERY_WILDCARD, // name pattern
                    (UIUtil.isNotNullAndNotEmpty(revisionPattern)) ? revisionPattern: DomainConstants.QUERY_WILDCARD, // revision pattern
                    (UIUtil.isNotNullAndNotEmpty(ownerPattern)) ? ownerPattern: DomainConstants.QUERY_WILDCARD, // owner pattern
                    (UIUtil.isNotNullAndNotEmpty(vaultPattern)) ? vaultPattern: context.getVault().getName(), // vault pattern
                    (UIUtil.isNotNullAndNotEmpty(whereClause)) ? whereClause: DomainConstants.EMPTY_STRING, // where expression
                    expandType ? true : Boolean.FALSE, // expandType
                    (null != objectSelectList) ? objectSelectList: new StringList(DomainConstants.SELECT_ID));// objectSelects
        }
    }
}
