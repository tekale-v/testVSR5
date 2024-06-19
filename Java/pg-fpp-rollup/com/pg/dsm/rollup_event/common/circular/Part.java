//Added by DSM (Sogeti) 2018x.6 Rollup Circular Circular Defect ID - 42216
package com.pg.dsm.rollup_event.common.circular;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.matrixone.apps.domain.DomainConstants;


public class Part {
    String id;
    String type;
    String name;
    String revision;
    String level;
    String relId;
    String current;
    boolean isSetProdcutPresent;
    private List<Part> children;
    private List<Part> substitutes;
    private Part parent;
    private boolean parentExist;
    private boolean childExist;
    private boolean substituteExist;
    private boolean isSubstitutePart;
    private String substituteForName;

    public Part(Map<?, ?> busMap) {
        this.id = (String) busMap.get(DomainConstants.SELECT_ID);
        this.type = (String) busMap.get(DomainConstants.SELECT_TYPE);
        this.name = (String) busMap.get(DomainConstants.SELECT_NAME);
        this.revision = (String) busMap.get(DomainConstants.SELECT_REVISION);
        this.level = (String) busMap.get(DomainConstants.SELECT_LEVEL);
        this.current = (String) busMap.get(DomainConstants.SELECT_CURRENT);
        this.relId = (String) busMap.get(DomainConstants.SELECT_RELATIONSHIP_ID);
        this.children = new ArrayList<>();
    }

    /**
     * @param part
     */
    public void addChild(Part part) {
        this.children.add(part);
    }

    /**
     * @param parts
     */
    public void addChildList(List<Part> parts) {
        this.children.addAll(parts);
    }

    /**
     * @return String
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return String
     */
    public String getType() {
        return type;
    }

    /**
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return String
     */
    public String getRevision() {
        return revision;
    }

    /**
     * @param revision
     */
    public void setRevision(String revision) {
        this.revision = revision;
    }

    /**
     * @return String
     */
    public String getLevel() {
        return level;
    }

    /**
     * @param level
     */
    public void setLevel(String level) {
        this.level = level;
    }

    /**
     * @return String
     */
    public String getCurrent() {
        return current;
    }

    /**
     * @param current
     */
    public void setCurrent(String current) {
        this.current = current;
    }

    /**
     * @return String
     */
    public String getRelId() {
        return relId;
    }

    /**
     * @param relId
     */
    public void setRelId(String relId) {
        this.relId = relId;
    }

    /**
     * @return List
     */
    public List<Part> getSubstitutes() {
        return substitutes;
    }

    /**
     * @param substitutes
     */
    public void setSubstitutes(List<Part> substitutes) {
        this.substitutes = substitutes;
    }

    /**
     * @return Part
     */
    public Part getParent() {
        return parent;
    }

    /**
     * @param parent
     */
    public void setParent(Part parent) {
        this.parent = parent;
    }

    /**
     * @return boolean
     */
    public boolean isParentExist() {
        return parentExist;
    }

    /**
     * @param parentExist
     */
    public void setParentExist(boolean parentExist) {
        this.parentExist = parentExist;
    }

    /**
     * @return boolean
     */
    public boolean isChildExist() {
        return childExist;
    }

    /**
     * @param childExist
     */
    public void setChildExist(boolean childExist) {
        this.childExist = childExist;
    }

    /**
     * @return boolean
     */
    public boolean isSubstituteExist() {
        return substituteExist;
    }

    /**
     * @param substituteExist
     */
    public void setSubstituteExist(boolean substituteExist) {
        this.substituteExist = substituteExist;
    }

    /**
     * @return List
     */
    public List<Part> getChildren() {
        return children;
    }

    /**
     * @param children the children to set
     */
    public void setChildren(List<Part> children) {
        this.children = children;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    /**
     * @return the isSetProdcutPresent
     */
    public boolean isSetProdcutPresent() {
        return isSetProdcutPresent;
    }

    /**
     * @param isSetProdcutPresent the isSetProdcutPresent to set
     */
    public void setSetProdcutPresent(boolean isSetProdcutPresent) {
        this.isSetProdcutPresent = isSetProdcutPresent;
    }

    /**
     * @return the isSubstitutePart
     */
    public boolean isSubstitutePart() {
        return isSubstitutePart;
    }

    /**
     * @param isSubstitutePart the isSubstitutePart to set
     */
    public void setSubstitutePart(boolean isSubstitutePart) {
        this.isSubstitutePart = isSubstitutePart;
    }

    /**
     * @return the substituteForName
     */
    public String getSubstituteForName() {
        return substituteForName;
    }

    /**
     * @param substituteForName the substituteForName to set
     */
    public void setSubstituteForName(String substituteForName) {
        this.substituteForName = substituteForName;
    }

}
