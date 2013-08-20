
import java.util.*;

public class Tuple {

  public ArrayList<String> attributes;
  public ArrayList domains;
  public Comparable [] tuple;

  Tuple (Comparable [] tup, ArrayList attr, ArrayList dom) {
    attributes = new ArrayList();
    for (int i=0; i<attr.size(); i++) {
      String s = (String) attr.get(i);
      attributes.add(s);
    }
    domains = new ArrayList();
    for (int i=0; i<dom.size(); i++) {
      String s = (String) dom.get(i);
      domains.add(s);
    }
    int numAttr = attr.size();
    tuple = new Comparable [numAttr];
    for (int i=0; i<numAttr; i++) {
      tuple[i] = tup[i];
    }
  }

  public void set(int index, Comparable val) {
      this.tuple[index] = val;
  }
  
  public int indexOf(String attr) {
      for (String attribute : attributes) {
          if (attr.equals(attribute)) {
              return this.attributes.indexOf(attribute);
          }
      }
      return -1;
  }
  
  public boolean equals(Tuple t) {
    // assumes schemas are compatible
    for (int i=0; i<attributes.size(); i++) {
      String atype = (String) domains.get(i);
      if (atype.equals("VARCHAR")) {
        String t1val = (String) t.tuple[i];      
        String t2val = (String) tuple[i];      
        if (!t1val.equals(t2val))
          return false;
      }
      else if (atype.equals("INTEGER")) {
        Integer t1v = (Integer) t.tuple[i];      
        int t1val = t1v.intValue();
        Integer t2v = (Integer) tuple[i];      
        int t2val = t2v.intValue();
        if (t1val != t2val)
          return false;
      }
      else {
        Double t1v = (Double) t.tuple[i];      
        double t1val = t1v.intValue();
        Double t2v = (Double) tuple[i];      
        double t2val = t2v.intValue();
        if (t1val != t2val)
          return false;
      }
    }
    return true;
  }

  public Tuple project(ArrayList cn) {
    ArrayList attr = new ArrayList();
    ArrayList doms = new ArrayList();
    Comparable [] tup = new Comparable[cn.size()]; 
    for (int i=0; i<cn.size(); i++) {
      String cname = (String) cn.get(i);
      int index = attributes.indexOf(cname);
      String ctype = (String) domains.get(index);
      doms.add(ctype);
      tup[i] = tuple[index];
    }
    Tuple t = new Tuple(tup,cn,doms);
    return t; 
  }

  public Tuple extendedProject(ArrayList ct, ArrayList cn) {
    ArrayList attr = new ArrayList();
    ArrayList doms = new ArrayList();
    Comparable [] tup = new Comparable[cn.size()]; 
    for (int i=0; i<cn.size(); i++) {
      String colType = (String) ct.get(i);
      if (colType.equals("COLUMN")) {
        String cname = (String) cn.get(i);
        int index = attributes.indexOf(cname);
        String ctype = (String) domains.get(index);
        doms.add(ctype);
        tup[i] = tuple[index];
      }
      else if (colType.equals("VARCHAR")) {
        tup[i] = (String) cn.get(i);
        doms.add("VARCHAR");
      }
      else if (colType.equals("INTEGER")) {
        String sval = (String) cn.get(i);
        Integer ival = null;
        try {
          ival = new Integer(Integer.parseInt(sval));
        } catch (NumberFormatException e) {
            System.out.println("Invalid number");
          }
        tup[i] = ival;
        doms.add("INTEGER");
      } 
      else if (colType.equals("DECIMAL")) {
        String sval = (String) cn.get(i);
        Double dval = null;
        try {
          dval = new Double(Double.parseDouble(sval));
        } catch (NumberFormatException e) {
            System.out.println("Invalid number");
          }
        tup[i] = dval;
        doms.add("DECIMAL");
      } 
    }
    Tuple t = new Tuple(tup,cn,doms);
    return t; 
  }
  
  public boolean select(String lAttribute, String comparison, String rAttributeVal) {
      int i = 0;
      while (true){
          String attr = (String)this.attributes.get(i);
          if (lAttribute.equals(attr.trim())) {
              break;
          } else if (i == this.attributes.size()) {
              return false;
          } else {
              i++;
          }
      }
      String lDomain = (String)this.domains.get(i);
      if (lDomain.trim().equals("INTEGER")) {
          int lAttrInt = (Integer)this.tuple[i];
          int rAttrInt = Integer.parseInt(rAttributeVal);
          if (comparison.trim().equals("<")) {
              return (lAttrInt < rAttrInt);
          } else if (comparison.trim().equals("<=")) {
              return (lAttrInt <= rAttrInt);
          } else if (comparison.trim().equals("=")) {
              return (lAttrInt == rAttrInt);
          } else if (comparison.trim().equals("<>")) {
              return (lAttrInt != rAttrInt);
          } else if (comparison.trim().equals(">")) {
              return (lAttrInt > rAttrInt);
          } else if (comparison.trim().equals(">=")) {
              return (lAttrInt >= rAttrInt);
          } else {
              System.out.println("\nError: incorrect comparison.\n");
              return false;
          }
      } else if (lDomain.equals("DECIMAL")) {
          double lAttrInt = (Double)tuple[i];
          double rAttrInt = Double.parseDouble(rAttributeVal);
          if (comparison.trim().equals("<")) {
              return (lAttrInt < rAttrInt);
          } else if (comparison.trim().equals("<=")) {
              return (lAttrInt <= rAttrInt);
          } else if (comparison.trim().equals("=")) {
              return (lAttrInt == rAttrInt);
          } else if (comparison.trim().equals("<>")) {
              return (lAttrInt != rAttrInt);
          } else if (comparison.trim().equals(">")) {
              return (lAttrInt > rAttrInt);
          } else if (comparison.trim().equals(">=")) {
              return (lAttrInt >= rAttrInt);
          } else {
              System.out.println("\nError: incorrect comparison.\n");
              return false;
          }
      } else { // lDomain equals "VARCHAR"
          String lAttrStr = (String)tuple[i];
          if (comparison.equals("=")) {
            return lAttrStr.equals(rAttributeVal);
          } else if (comparison.equals("<>")){
            return (!lAttrStr.equals(rAttributeVal));
          } else if (comparison.equals("<")) {
            return (lAttrStr.compareTo(rAttributeVal) < 0);
          } else if (comparison.equals(">")) {
            return (lAttrStr.compareTo(rAttributeVal) > 0);
          } else if (comparison.equals("<=")) {
            return (lAttrStr.compareTo(rAttributeVal) <= 0);
          } else if (comparison.equals(">=")) {
            return (lAttrStr.compareTo(rAttributeVal) >= 0);
          } else {
              System.out.append("Error: Invalid comparison\n");
              return false;
          }
      }
  }

  String extractColumnValueAsString(String colName) {
    int index = attributes.indexOf(colName);
    String colType = (String) domains.get(index);
    String cval = null;
    if (colType.equals("VARCHAR"))
      cval = (String) tuple[index];
    else if (colType.equals("INTEGER")) {
      Integer ival = (Integer) tuple[index];
      cval = "" + ival.intValue();
    }
    else {
      Double dval = (Double) tuple[index];
      cval = "" + dval.doubleValue();
    }
    return cval;
  }

  void printTuple() {
    for (int i=0; i<attributes.size(); i++) {
      String aname = (String) attributes.get(i);
      String atype = (String) domains.get(i);
      if (atype.equals("VARCHAR")) {
        String aval = (String) tuple[i];
        System.out.print(aname+":"+atype+":"+aval+",");
      }
      else if (atype.equals("INTEGER")) {
        Integer avall = (Integer) tuple[i];
        int aval = avall.intValue();
        System.out.print(aname+":"+atype+":"+aval+",");
      }
      else {
        Double avall = (Double) tuple[i];
        double aval = avall.doubleValue();
        System.out.print(aname+":"+atype+":"+aval+",");
      }
    }
    System.out.println("");
  }

  boolean joins(Tuple t2, Vector leftJoinCols, Vector rightJoinCols, 
                          Vector lJoinDoms, Vector rJoinDoms) {
    for (int i=0; i<leftJoinCols.size(); i++) {
      Integer ljoinCol = (Integer) leftJoinCols.get(i);
      int ljcol = ljoinCol.intValue();
      Integer rjoinCol = (Integer) rightJoinCols.get(i);
      int rjcol = rjoinCol.intValue();
      String ldom = (String) lJoinDoms.get(i);
      String rdom = (String) rJoinDoms.get(i);
      if (ldom.equals("VARCHAR")) {
        String lval = (String) tuple[ljcol];
        String rval = (String) t2.tuple[rjcol];
        if (!lval.equals(rval))
          return false;
      } 
      else { // ldom and rdom are INTEGER or DECIMAL 
        double lval = 0.0;
        if (ldom.equals("INTEGER")) {
          Integer lvall = (Integer) tuple[ljcol];
          lval = lvall.doubleValue();
        } else {
            Double lvall = (Double) tuple[ljcol];
            lval = lvall.doubleValue();
          }
        double rval = 0.0;
        if (rdom.equals("INTEGER")) {
          Integer rvall = (Integer) t2.tuple[rjcol];
          rval = rvall.doubleValue();
        } else {
            Double rvall = (Double) t2.tuple[rjcol];
            rval = rvall.doubleValue();
          }
        if (lval != rval)
          return false;
      }
    } 
    return true;
  }
}
