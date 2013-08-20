
import java.io.*;
import java.util.*;

public class Relation {

    ////////////////////////////////////////////////////////////////////////
    // FIELDS
    ////////////////////////////////////////////////////////////////////////
    // Relation name
    private String relName;

    // Attribute names for the relation
    private ArrayList<String> attributes;

    // Types of the attributes
    private ArrayList<String> domains;

    // List of tuples for the relation
    private ArrayList table;

    // Counter for tuple iterator
    private int counter = 0;

    // Map associating relation names with relation memory images - static since
    // multiple instances of Relation will need to be mapped by, and refer to,
    // a single catalogMap
    private static Map catalogMap;

    ////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////////////////////////////////
    public Relation(String relName, ArrayList attributes, ArrayList domains) {
        this.counter = 0;
        this.relName = relName;
        this.attributes = attributes;
        this.domains = domains;
        this.table = new ArrayList();
    }

    ////////////////////////////////////////////////////////////////////////////
    // GETTERS
    ////////////////////////////////////////////////////////////////////////////
    public Map getCatalogMap() {
        return this.catalogMap;
    }

    public ArrayList<String> getAttributes() {
        return this.attributes;
    }
    
    public ArrayList<String> getDomains() {
        return this.domains;
    }
    
    public String getRelationName() {
        return this.relName;
    }
    
    public ArrayList getTable() {
        return this.table;
    }
    ////////////////////////////////////////////////////////////////////////////
    // SETTERS
    ////////////////////////////////////////////////////////////////////////////
    public void setAttributes(ArrayList<String> attrs) {
        this.attributes = attrs;
    }
    
    public void setDomains(ArrayList<String> doms) {
        this.domains = doms;
    }

    ////////////////////////////////////////////////////////////////////////////
    // DATABASE METHODS
    ////////////////////////////////////////////////////////////////////////////
    // Must be a static method since multiple Relations will be contained within
    // a single database
    public static Relation createDatabase(String dir) {
    catalogMap=new HashMap();
    FileInputStream fin, fin2 = null;
    BufferedReader infile, infile2 = null;

    try {
      fin = new FileInputStream(dir + "/catalog.dat.txt");
      infile = new BufferedReader(new InputStreamReader(fin));

      int numRelations = 0;
      String s = infile.readLine();
      try {
        numRelations = Integer.parseInt(s);
      } catch (NumberFormatException e) {
          System.out.println("Invalid number");
      }
      for (int i=0; i < numRelations; i++) {
        // Code to set Relation Scheme in Catalog
        String rname = infile.readLine();
        s = infile.readLine();
        int numAttributes = 0;
        try {
          numAttributes = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number");
        }
        ArrayList attrs = new ArrayList();
        ArrayList doms = new ArrayList();
        for (int j = 0; j < numAttributes; j++) {
          String aname = infile.readLine();
          String atype = infile.readLine();
          attrs.add(aname);
          doms.add(atype);
        }
        Relation r = new Relation(rname, attrs, doms);
        // Code to populate r with tuples
        String fname=dir + "/" + rname + ".dat.txt";
        fin2 = new FileInputStream(fname);
        infile2 = new BufferedReader(new InputStreamReader(fin2));
        s = infile2.readLine();
        int numTuples = 0;
        try {
          numTuples = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number 1");
        }
        for (int k = 0; k < numTuples; k++) {
          Comparable [] tuple = new Comparable[numAttributes]; 
          for (int j=0; j<numAttributes; j++) {
            s = infile2.readLine();
            if (r.domains.get(j).equals("VARCHAR")) // is varchar
              tuple[j] = s;
            else if (r.domains.get(j).equals("INTEGER")) { // is integer
              Integer ival = null;
              try {
                ival = new Integer(Integer.parseInt(s));
              } catch (NumberFormatException e) {
                  System.out.println("Invalid number 2");
              }
              tuple[j] = ival;
            } else { // is  decimal
                Double dval = null;
                try {
                  dval = new Double(Double.parseDouble(s));
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number 3");
                }
                tuple[j] = dval;
              }
          }
          r.table.add(tuple);
        } 
        catalogMap.put(rname,r);
        r.displayRelation();
      }

    } catch (IOException e) {
        System.out.println("\nError: Database: " + dir + "not found.\n");
    } 
        System.out.append("\nDatabase " + dir + " successfully created.\n");
        Relation nullRel = new Relation(null, null, null);
        return nullRel;
    }

    public static Relation createTable(String tableName, ArrayList<String> attributes) {
////////////////////////////////////////////////////////////////////////////////
        System.out.println(attributes);
        int numAttrs = attributes.size();
        ArrayList attrs = new ArrayList();
        ArrayList doms = new ArrayList();
        for (int i = 0; i < numAttrs; i++) {
          String[] attrNameAndType = attributes.get(i).trim().split(" ");
          if (attrNameAndType.length <= 1) {
              System.out.println("\nError: Malformed attribute specification.\n");
          } else {
              String aName = attrNameAndType[0];
              String aType = attrNameAndType[1];
              attrs.add(aName);
              doms.add(aType);
          }
        }
        Relation r = new Relation(tableName, attrs, doms);
        catalogMap.put(tableName, r);
////////////////////////////////////////////////////////////////////////////////
        r.displayRelation();
        System.out.println(catalogMap);
        return r;
    }

    public static void saveDatabase(String dir) {
    try {
      OutputStream f = new FileOutputStream(dir + "/catalog2.dat.txt");
      PrintStream outfile = new PrintStream(f);

      int numRelations=catalogMap.size();
      outfile.println(""+numRelations);
      Set s = catalogMap.entrySet();
      Iterator i=s.iterator();
      while (i.hasNext()) {
        Map.Entry e = (Map.Entry) i.next();
        String rname = (String) e.getKey();
        if (rname.startsWith("$"))
          continue;
        Relation r= (Relation) e.getValue();
        outfile.println(rname);
        outfile.println(""+r.attributes.size());
        for (int j=0; j<r.attributes.size(); j++) {
          String aname = (String) r.attributes.get(j);
          String atype = (String) r.domains.get(j);
          outfile.println(aname); 
          outfile.println(atype); 
        }
        String fname=dir + rname + ".dat.txt";
        OutputStream f2 = new FileOutputStream(fname);
        PrintStream outfile2 = new PrintStream(f2);
        int nTuples = r.table.size(); 
        outfile2.println(nTuples);
        for (int k = 0; k < nTuples; k++) {
          Comparable[] tup = (Comparable[])r.table.get(k);
          for (int m = 0; m < r.attributes.size(); m++) {
            String atype = (String)r.domains.get(m);
            if (atype.equals("VARCHAR")) {
              String sval = (String)tup[m];
              outfile2.println(sval);
            }
            else if (atype.equals("INTEGER")) {
              Integer ival = (Integer)tup[m];
              outfile2.println(ival.intValue());
            } else {
                Double dval = (Double)tup[m];
                outfile2.println(dval.doubleValue());
              }
          } // for m
        } // for k
      } // while
    } catch (IOException e) {
        System.out.println("Error");
    }
    }

    public Relation selection(String lAttr, String comparison, String rAttrVal) {
    ArrayList attr = new ArrayList();
    ArrayList dom = new ArrayList();
    for (int i = 0; i < this.attributes.size(); i++) {
        String aName = (String)attributes.get(i);
        String aType = (String)domains.get(i);
        attr.add(aName);
        dom.add(aType);
    }
    // Create a new Relation schema
    Relation r = new Relation(null, attr, dom);

    // Add tuples
    int numAttr = attr.size();
    int numTuples = this.table.size();
    for (int i = 0; i < numTuples; i++) {
        Comparable[] t = (Comparable[])this.table.get(i);
        Tuple tup = new Tuple(t, attr, dom);
        if (tup.select(lAttr, comparison, rAttrVal)) {
            r.table.add(t);
        }
    }
    return r;
    }
    
    public static Relation select(ArrayList<Condition> conds, String tblName) {
        Relation baseRel = (Relation)catalogMap.get(tblName);
        ArrayList<String> attrs = new ArrayList<String>();
        ArrayList<String> doms = new ArrayList<String>();
        for (int i = 0; i < baseRel.attributes.size(); i++) {
            String aName = baseRel.attributes.get(i);
            String aType = baseRel.domains.get(i);
            attrs.add(aName);
            doms.add(aType);
        }
        Relation r = new Relation(null, null, null);
        r.setAttributes(attrs);
        r.setDomains(doms);
        // Add Tuples
        int numAttr = attrs.size();
        int numTuples = baseRel.table.size(); // HERE IS WHERE BINARY SEARCH MUST 
        for (int i = 0; i < numTuples; i++) { // BE IMPLEMENTED!!!
            Comparable[] t = (Comparable[])baseRel.table.get(i);
            Tuple tup = new Tuple(t, attrs, doms);
            boolean addTuple = true;
            for (Condition cond : conds) {
                String lAttr = cond.getAttributeName();
                String comparison = cond.getComparison();
                String rAttrVal = cond.getAttributeValue();
                if (containsAttribute(lAttr, tblName)) { 
                    if (!tup.select(lAttr, comparison, rAttrVal)) { 
                        addTuple = false; 
                    }
                }
            }
            if (addTuple) {
                r.table.add(t);
            }
        }
        return r;
    }
    
    public static Relation deleteTuple(String tblName, ArrayList<Condition> 
            conds) {
        Relation r = (Relation)catalogMap.get(tblName);
        int numTuples = r.table.size();
        for (int i = 0; i < numTuples; i++) {
            Comparable[] t = (Comparable[])r.table.get(i);
            Tuple tup = new Tuple(t, r.attributes, r.domains);
            boolean deleteTuple = true;
            for (Condition cond : conds) {
                String lAttr = cond.getAttributeName();
                String comparison = cond.getComparison();
                String rAttrValue = cond.getAttributeValue();
                if (containsAttribute(lAttr, tblName)) {
                   if (!tup.select(lAttr, comparison, rAttrValue)) {
                       deleteTuple = false;
                   }
                }
            }
            if (deleteTuple) {
                r.table.remove(i);
            }
            numTuples = r.table.size();
        }
        return r;
    }
    
    public static Relation updateTuple(String tblName, ArrayList<Condition> 
            sets, ArrayList<Condition> conds) {
        Relation r = (Relation)catalogMap.get(tblName);
        int numTuples = r.table.size();
        for (int i = 0; i < numTuples; i++) {
            Comparable[] t = (Comparable[])r.table.get(i);
            Tuple tup = new Tuple(t, r.attributes, r.domains);
            boolean updateTuple = true;
            for (Condition cond : conds) {
                String lAttr = cond.getAttributeName();
                String comparison = cond.getComparison();
                String rAttrValue = cond.getAttributeValue();
                if (containsAttribute(lAttr, tblName)) {
                    if (!tup.select(lAttr, comparison, rAttrValue)) {
                        updateTuple = false;
                    }
                }
            }
            if (updateTuple) {
                for (Condition set : sets) {
                    String setVal = set.getAttributeValue();
                    String setAttr = set.getAttributeName();
                    int index = r.attributes.indexOf(setAttr);
                    Comparable[] updateTup  = (Comparable[])r.table.get(i);
                    setComparableIndexToType(updateTup, index, r, setVal);
                }
            }
        }
        return r;
    }
    
    private static void setComparableIndexToType(Comparable[] tup, 
            int index, Relation r, String val) {
        if (r.getDomains().get(index).equals("INTEGER")) {
            try {
                int updateVal = Integer.parseInt(val);
                tup[index] = updateVal;
            } catch (NumberFormatException nfe) {
                System.err.println("\nError: Expected integer but found string:"
                        + " " + val + ".\n");
            }
        }  else if (r.getDomains().get(index).equals("DECIMAL")) {
            try {
                double updateVal = Double.parseDouble(val);
                tup[index] = updateVal;
            } catch (NumberFormatException nfe) {
                System.err.println("\nError: Expected decimal but found string:"
                        + " " + val + ".\n");
            }
        } else {
            tup[index] = val;
        }
    }

    public static Relation getRelation (String relName) {
    if (catalogMap.containsKey(relName)) {
      Relation x = (Relation) catalogMap.get(relName);
      return x;
    } else
        return null;
    }; // getRelation

    public void displayRelation() {
        int numTuples = table.size();
        System.out.println("\nNumber of tuples = " + numTuples);
        for (int i = 0; i < numTuples; i++) {
          Comparable[] tup = (Comparable[])table.get(i);
          for (int j = 0; j < attributes.size(); j++) {
              this.printTupleValue(tup, j);
          } 
          System.out.println("");
        }
        System.out.println("");
    }
    
    public void printTupleValue(Comparable[] tuple, int index) {
        String aType = this.domains.get(index);
        if (aType.trim().contains("VARCHAR")) {
            String sVal = (String)tuple[index];
            System.out.print(sVal + ":");
        } else if (aType.trim().equals("INTEGER")) {
            Integer iVal = (Integer)tuple[index];
            System.out.print(iVal.intValue() + ":");
        } else {
            Double dVal = (Double)tuple[index];
            System.out.print(dVal.doubleValue() + ":");
        }
    }

    public static boolean tableExists(String tblName) {
        if (catalogMap.containsKey(tblName)) {
            return true;
        } else {
            System.err.println("\nError: Table " + tblName + " does not exist"
                    + " in the database.\n");
            return false;
        }
    }

    public static boolean attributeExists(String attrName, 
            ArrayList<String> tblList) {
       for (String tbl : tblList) {
           Relation rel = (Relation)catalogMap.get(tbl);
           for (String attr : rel.attributes) {
               if (attrName.trim().equals(attr.trim())) {
                   return true;
               }
           }
       }
       return false;
    }
    
    public static boolean containsAttribute(String attrName, String tblName) {
        Relation rel = (Relation)catalogMap.get(tblName);
        for (String attr : rel.attributes) {
            if (attrName.trim().equals(attr)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAppropriateAttributeNumber(String tblName, 
            ArrayList<String> attributes) {
        Relation r = (Relation)catalogMap.get(tblName);
        if (attributes.size() != r.attributes.size()) {
            return false;
        } else {
            return true;
        }
    }
    
    public static boolean paramNumberDoesNotExceedTableAttributes(String tblName, 
            ArrayList<String> attributes) {
        Relation r = (Relation)catalogMap.get(tblName);
        if (attributes.size() > r.attributes.size()) {
            return false;
        } else {
            return true;
        }
    }
    
    public static boolean isAppropriateValueNumber(ArrayList<String> attributes,
            ArrayList<String> values) {
        if (attributes.size() != values.size()) {
            return false;
        } else {
            return true;
        }
    }
    
    public static boolean areAppropriateValueTypes(String tblName, 
            ArrayList<String> attributes, ArrayList<String> values) {
        Relation r = (Relation)catalogMap.get(tblName);
        for (int i = 0; i < attributes.size(); i++) {
            int j = 0;
            while (!r.attributes.get(j).equalsIgnoreCase(attributes.get(i))) {
                j++;
            }
            String attrType = r.domains.get(j);
            
            if (attrType.equalsIgnoreCase("INTEGER")) {
                try {
                    int val = Integer.parseInt(values.get(i));
                } catch (NumberFormatException nfe) {
                    System.err.println("\nError: Type mismatch between " + 
                            "attribute: " + attributes.get(i) + " (type INTEGER) and value: "
                            + values.get(i) + ".\n");
                    return false;
                }
                 
            } else if (attrType.equalsIgnoreCase("DECIMAL")) {
                try {
                    double val = Double.parseDouble(values.get(i));
                } catch (NumberFormatException nfe) {
                    System.err.println("\nError: Type mismatch between " + 
                            "attribute: " + attributes.get(i) + " (type DECIMAL) and value: "
                            + values.get(i) + ".\n");
                    return false;
                }
            } else {            // attrType equals "VARCHAR"
                /* do nothing, anything can be a string */
            }
        }
        return true;
    }
    
    public void insertTuple(Tuple tup) {
        this.table.add(tup);
    }
    
    public static Relation projectAll(String table) {
        Relation fullyProjectedRel = (Relation)catalogMap.get(table);
        return fullyProjectedRel;
    }
    
    public static Relation project(ArrayList<String> attributes, 
             Relation unProjectedRel) {
        ArrayList<String> attrs = new ArrayList<String>();
        ArrayList<String> doms = new ArrayList<String>();
        for (String attrName : attributes) {
            int index = unProjectedRel.attributes.indexOf(attrName);
            if (index != -1) {
                String attrType = unProjectedRel.domains.get(index);
                attrs.add(attrName);
                doms.add(attrType);
            } else if ((attributes.get(0).trim().equals("*")) 
                     && (attributes.size() == 1)) {
                System.out.append("\nHere we must put code to print all " +
                        " attributes.\n");
            } else {
                System.err.append("\nError: Attribute " + attrName + " not "
                        + "found in table " + unProjectedRel.getRelationName());
            }
        }
        Relation r = new Relation(null, attrs, doms);
        int numAttrs = attrs.size();
        int numTuples = unProjectedRel.table.size();
        for (int i = 0; i < numTuples; i++) {
            Comparable[] t = (Comparable[])unProjectedRel.table.get(i);
            Comparable[] tup = new Comparable[numAttrs];
            for (int k = 0; k < numAttrs; k++) {
                String attrName = attrs.get(k);
                int index = unProjectedRel.attributes.indexOf(attrName);
                tup[k] = t[index];
            }
            r.table.add(tup);
        }
        //r.removeDuplicates();
        return r;
    }

    public static Relation project(ArrayList<String> attributes, String table) {
        ArrayList<String> attrs = new ArrayList<String>();
        ArrayList<String> doms = new ArrayList<String>();
        Relation unProjectedRel = (Relation)catalogMap.get(table);
        for (String attrName : attributes) {
            int index = unProjectedRel.attributes.indexOf(attrName);
            if (index != -1) {
                String attrType = unProjectedRel.domains.get(index);
                attrs.add(attrName);
                doms.add(attrType);
            } else if ((attributes.get(0).trim().equals("*")) 
                    && (attributes.size() == 1)) {
                System.out.println("\nHere we must write code to print out ALL"
                        + " attributes.\n");
            } else {
               //////////////////////////////////////////////////////////////// 
               // DEBUG
               ////////////////////////////////////////////////////////////////
               System.out.println("\nAttribute " + attrName + " not found in"
                       + " table " + table);
            }
        }
        Relation r = new Relation(null, attrs, doms);
        int numAttrs = attrs.size();
        int numTuples = unProjectedRel.table.size();
        for (int i = 0; i < numTuples; i++) {
            Comparable[] t = (Comparable[])unProjectedRel.table.get(i);
            Comparable[] tup = new Comparable[numAttrs];
            for (int k = 0; k < numAttrs; k++) {
                String attrName = attrs.get(k);
                int index = unProjectedRel.attributes.indexOf(attrName);
                tup[k] = t[index];
            }
            r.table.add(tup);
        }
        //r.removeDuplicates();
        return r;
    }
}
