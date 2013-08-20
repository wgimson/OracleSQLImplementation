
public class Condition {
    private String attributeName, attributeValue, comparison;

    public Condition() {
        this.attributeName = null;
        this.comparison = null;
        this.attributeValue = null;
    }

    public Condition(String attrName, String comp, String attrVal) {
        this.attributeName = attrName;
        this.comparison = comp;
        this.attributeValue = attrVal;
    }

    public String getAttributeName() {
        return this.attributeName;
    }

    public String getAttributeValue() {
        return this.attributeValue;
    }

    public String getComparison() {
        return this.comparison;
    }

    public void setAttributeName(String attrName) {
        this.attributeName = attrName;
    }

    public void setAttributeValue(String attrVal) {
        this.attributeValue = attrVal;
    }

    public void setComparison(String comp) {
        this.comparison = comp;
    }
    
    @Override
    public String toString() {
        return "\'" + this.attributeName + " " + this.comparison + " " + 
                this.attributeValue + "\'";
    }
}
